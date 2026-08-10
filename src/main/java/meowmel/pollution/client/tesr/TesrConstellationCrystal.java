package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POConstellationCrystal;
import meowmel.pollution.common.block.tile.TileEntityConstellationCrystal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/** Animates the normal baked crystal model and adds rings around the tower core. */
public class TesrConstellationCrystal extends TileEntitySpecialRenderer<TileEntityConstellationCrystal> {

    private static final int RING_SEGMENTS = 64;
    private static final int RAINBOW_SEGMENTS = 128;
    private static final int[][] RITUAL_RING_OFFSETS = {
            {0, -3}, {2, -2}, {3, 0}, {2, 2},
            {0, 3}, {-2, 2}, {-3, 0}, {-2, -2}
    };

    @Override
    public void render(TileEntityConstellationCrystal tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (!tile.hasWorld()) return;
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state.getBlock() != PollutionMetaBlocks.CONSTELLATION_CRYSTAL) return;

        POConstellationCrystal block = PollutionMetaBlocks.CONSTELLATION_CRYSTAL;
        POConstellationCrystal.CrystalType type = block.getState(state);
        boolean core = type == POConstellationCrystal.CrystalType.TOWER_CORE;
        double time = tile.getWorld().getTotalWorldTime() + partialTicks;
        // Keep a ritual ring on one horizontal plane while it bobs. The lone
        // tower core can retain a position-derived phase of its own.
        double phase = core ? (tile.getPos().toLong() & 1023L) * 0.03125D : 0.0D;
        double bob = Math.sin(time * (core ? 0.065D : 0.095D) + phase)
                * (core ? 0.065D : 0.105D);
        float rotation = (float) ((time * (core ? 1.35D : 2.6D) + phase * 57.2958D) % 360.0D);
        ItemStack modelStack = new ItemStack(block, 1, block.getMetaFromState(state));

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D + bob, z + 0.5D);
        GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
        if (!core) {
            GlStateManager.rotate((float) (5.0D * Math.sin(time / 22.0D)), 1.0F, 0.0F, 1.0F);
        }

        // RenderItem itself shifts baked 0..1 model coordinates by -0.5 on
        // every axis. The current matrix origin is already the block center,
        // so adding another manual shift here would move the rotation pivot.
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getRenderItem().renderItem(
                modelStack, ItemCameraTransforms.TransformType.NONE);

        if (core) {
            renderCoreRings(time);
            if (hasCompleteRitualRing(tile)) {
                renderRitualRainbowRing(time);
            }
        }

        GL11.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private static void renderCoreRings(double time) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2.0F);

        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) (time * 0.9D % 360.0D), 0.0F, 1.0F, 0.0F);
        drawRing(0.70D, 0x74E8FF, 155);
        GlStateManager.rotate((float) (62.0D + Math.sin(time / 30.0D) * 8.0D), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) (-time * 1.7D % 360.0D), 0.0F, 0.0F, 1.0F);
        drawRing(0.62D, 0xBB7CFF, 135);
        GlStateManager.popMatrix();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    private static boolean hasCompleteRitualRing(TileEntityConstellationCrystal tile) {
        IBlockState ritualState = PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                .getState(POConstellationCrystal.CrystalType.RITUAL_CRYSTAL);
        BlockPos corePos = tile.getPos();
        for (int[] offset : RITUAL_RING_OFFSETS) {
            BlockPos crystalPos = corePos.add(offset[0], -6, offset[1]);
            if (!tile.getWorld().getBlockState(crystalPos).equals(ritualState)) return false;
        }
        return true;
    }

    /**
     * Five musical-stave-like rainbow curves orbit the completed ritual ring.
     * The whole stave bounces while each band carries a travelling vertical wave.
     */
    private static void renderRitualRainbowRing(double time) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        double bounce = Math.sin(time * 0.14D) * 0.16D;

        // Broad translucent pass creates a soft glow around every curve.
        GL11.glLineWidth(5.0F);
        for (int band = 0; band < 5; band++) {
            drawRainbowBand(time, band, bounce, 42);
        }

        // Thin bright pass keeps the rainbow readable from a distance.
        GL11.glLineWidth(1.8F);
        for (int band = 0; band < 5; band++) {
            drawRainbowBand(time, band, bounce, 215);
        }

        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    private static void drawRainbowBand(double time, int band, double bounce, int alpha) {
        double bandOffset = band - 2.0D;
        double radius = 2.92D + bandOffset * 0.075D;
        double baseY = -6.0D + bounce + bandOffset * 0.075D;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= RAINBOW_SEGMENTS; i++) {
            double progress = (double) i / RAINBOW_SEGMENTS;
            double angle = Math.PI * 2.0D * progress;
            double wave = Math.sin(angle * 3.0D - time * 0.17D + band * 0.42D) * 0.19D;
            int color = Color.HSBtoRGB(
                    (float) ((progress + time * 0.006D + band * 0.055D) % 1.0D),
                    0.72F,
                    1.0F);
            buffer.pos(Math.cos(angle) * radius, baseY + wave, Math.sin(angle) * radius)
                    .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                    .endVertex();
        }
        tessellator.draw();
    }

    private static void drawRing(double radius, int color, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < RING_SEGMENTS; i++) {
            double angle = Math.PI * 2.0D * i / RING_SEGMENTS;
            buffer.pos(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius)
                    .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                    .endVertex();
        }
        tessellator.draw();
    }
}
