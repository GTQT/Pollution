package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POConstellationCrystal;
import meowmel.pollution.common.block.tile.TileEntityConstellationCrystal;
import meowmel.pollution.common.block.tile.TileEntityStarstreamRelay;
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
    private static final int NEXUS_BEAM_SEGMENTS = 96;
    private static final int NEXUS_BEAM_SIDES = 10;
    private static final int[][] RITUAL_RING_OFFSETS = {
            {0, -3}, {2, -2}, {3, 0}, {2, 2},
            {0, 3}, {-2, 2}, {-3, 0}, {-2, -2}
    };

    @Override
    public boolean isGlobalRenderer(TileEntityConstellationCrystal tile) {
        if (!tile.hasWorld()) return false;
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        return state.getBlock() == PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                && PollutionMetaBlocks.CONSTELLATION_CRYSTAL.getState(state)
                == POConstellationCrystal.CrystalType.TOWER_CORE;
    }

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
            // The baked core and its local rings rotate, but the network beam
            // must stay locked to the target world position.
            GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);
            renderNexusLink(tile, time, bob);
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

    private static void renderNexusLink(TileEntityConstellationCrystal tile, double time, double sourceBob) {
        BlockPos target = tile.getLinkedNexusPos();
        if (target == null) return;
        double dx = target.getX() - tile.getPos().getX();
        double dz = target.getZ() - tile.getPos().getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double endpointHeight = tile.getLinkedEndpointType()
                == TileEntityStarstreamRelay.EndpointType.RELAY ? 0.95D : 8.15D;
        double targetY = target.getY() - tile.getPos().getY() + endpointHeight - sourceBob;
        double arcHeight = Math.min(14.0D, 2.5D + horizontalDistance * 0.08D);
        int hueSeed = tile.getConstellationId() == null ? 0 : tile.getConstellationId().hashCode();
        float hue = (hueSeed & 0xFFFF) / 65535.0F;
        int color = Color.HSBtoRGB(hue, 0.62F, 1.0F);

        renderStarstreamBeam(dx, targetY, dz, arcHeight, color, time);
    }

    /** Shared thick beam used by tower and relay route segments. */
    public static void renderStarstreamBeam(double dx, double targetY, double dz,
                                            double arcHeight, int color, double time) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        // Render real world-space geometry rather than relying only on OpenGL
        // line width. This keeps the link visibly thick at a distance and
        // gives it the layered core/glow appearance of a ChromatiCraft beam.
        drawNexusBeamTube(dx, targetY, dz, arcHeight, color, 0.28D, 18, time, 0.20D);
        drawNexusBeamTube(dx, targetY, dz, arcHeight, color, 0.17D, 48, time, 0.45D);
        drawNexusBeamTube(dx, targetY, dz, arcHeight, color, 0.095D, 205, time, 1.0D);

        // A very bright, thin spine prevents the translucent layers from
        // looking hollow and makes the transfer direction easy to read.
        int coreColor = mixWithWhite(color, 0.72F);
        drawNexusBeamTube(dx, targetY, dz, arcHeight, coreColor, 0.035D, 235, time, 1.0D);

        GL11.glPointSize(7.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 10; i++) {
            double progress = (time * 0.018D + i / 10.0D) % 1.0D;
            double y = targetY * progress + Math.sin(Math.PI * progress) * arcHeight;
            buffer.pos(dx * progress, y, dz * progress)
                    .color((color >> 16) & 255, (color >> 8) & 255, color & 255, 230)
                    .endVertex();
        }
        tessellator.draw();
        GL11.glPointSize(1.0F);
    }

    private static void drawNexusBeamTube(double dx, double dy, double dz,
                                           double arcHeight, int color, double radius,
                                           int alpha, double time, double pulseStrength) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (int side = 0; side < NEXUS_BEAM_SIDES; side++) {
            double sideAngleA = Math.PI * 2.0D * side / NEXUS_BEAM_SIDES;
            double sideAngleB = Math.PI * 2.0D * (side + 1) / NEXUS_BEAM_SIDES;
            buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i <= NEXUS_BEAM_SEGMENTS; i++) {
                double progress = (double) i / NEXUS_BEAM_SEGMENTS;
                double centerX = dx * progress;
                double centerY = dy * progress + Math.sin(Math.PI * progress) * arcHeight;
                double centerZ = dz * progress;

                // Tangent of the curved beam. The two perpendicular vectors
                // below form a stable circular cross-section along its path.
                double tangentX = dx;
                double tangentY = dy + Math.PI * arcHeight * Math.cos(Math.PI * progress);
                double tangentZ = dz;
                double tangentLength = Math.sqrt(tangentX * tangentX
                        + tangentY * tangentY + tangentZ * tangentZ);
                if (tangentLength < 1.0E-6D) tangentLength = 1.0D;
                tangentX /= tangentLength;
                tangentY /= tangentLength;
                tangentZ /= tangentLength;

                double basisAX = -tangentZ;
                double basisAY = 0.0D;
                double basisAZ = tangentX;
                double basisALength = Math.sqrt(basisAX * basisAX + basisAZ * basisAZ);
                if (basisALength < 1.0E-6D) {
                    basisAX = 1.0D;
                    basisAZ = 0.0D;
                } else {
                    basisAX /= basisALength;
                    basisAZ /= basisALength;
                }
                double basisBX = tangentY * basisAZ - tangentZ * basisAY;
                double basisBY = tangentZ * basisAX - tangentX * basisAZ;
                double basisBZ = tangentX * basisAY - tangentY * basisAX;

                // Bright bands travel from tower to nexus and produce the
                // slightly ribbed, energetic surface visible in the reference.
                double wave = 0.5D + 0.5D * Math.sin(progress * Math.PI * 30.0D - time * 0.62D);
                double radiusPulse = radius * (1.0D + wave * 0.10D * pulseStrength);
                int vertexAlpha = (int) Math.min(255.0D,
                        alpha * (0.78D + wave * 0.22D * pulseStrength));

                addBeamVertex(buffer, centerX, centerY, centerZ,
                        basisAX, basisAY, basisAZ, basisBX, basisBY, basisBZ,
                        sideAngleA, radiusPulse, color, vertexAlpha);
                addBeamVertex(buffer, centerX, centerY, centerZ,
                        basisAX, basisAY, basisAZ, basisBX, basisBY, basisBZ,
                        sideAngleB, radiusPulse, color, vertexAlpha);
            }
            tessellator.draw();
        }
    }

    private static void addBeamVertex(BufferBuilder buffer,
                                      double centerX, double centerY, double centerZ,
                                      double basisAX, double basisAY, double basisAZ,
                                      double basisBX, double basisBY, double basisBZ,
                                      double angle, double radius, int color, int alpha) {
        double cos = Math.cos(angle) * radius;
        double sin = Math.sin(angle) * radius;
        buffer.pos(centerX + basisAX * cos + basisBX * sin,
                        centerY + basisAY * cos + basisBY * sin,
                        centerZ + basisAZ * cos + basisBZ * sin)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                .endVertex();
    }

    private static int mixWithWhite(int color, float amount) {
        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;
        red += Math.round((255 - red) * amount);
        green += Math.round((255 - green) * amount);
        blue += Math.round((255 - blue) * amount);
        return red << 16 | green << 8 | blue;
    }

    private static void drawNexusCurve(double dx, double dy, double dz,
                                       double arcHeight, int color, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        int segments = 96;
        for (int i = 0; i <= segments; i++) {
            double progress = (double) i / segments;
            double y = dy * progress + Math.sin(Math.PI * progress) * arcHeight;
            buffer.pos(dx * progress, y, dz * progress)
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
