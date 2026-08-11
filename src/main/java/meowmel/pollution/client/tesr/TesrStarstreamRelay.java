package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.tile.TileEntityStarstreamRelay;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/** Floating relay crystal and local wireless activity rings. */
public class TesrStarstreamRelay extends TileEntitySpecialRenderer<TileEntityStarstreamRelay> {

    private static final int RING_SEGMENTS = 64;

    @Override
    public boolean isGlobalRenderer(TileEntityStarstreamRelay tile) {
        return true;
    }

    @Override
    public void render(TileEntityStarstreamRelay tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (!tile.hasWorld()) return;
        double time = tile.getWorld().getTotalWorldTime() + partialTicks;
        double bob = Math.sin(time * 0.09D) * 0.10D;
        int color = Color.HSBtoRGB((float) ((time * 0.0025D) % 1.0D), 0.58F, 1.0F);

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.45D + bob, z + 0.5D);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) (time * 1.8D % 360.0D), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(18.0F, 1.0F, 0.0F, 1.0F);
        drawOctahedron(0.58D, color, 34);
        drawOctahedron(0.40D, color, 210);
        drawOctahedron(0.20D, 0xEEFAFF, 245);
        GlStateManager.popMatrix();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(5.0F);
        drawRing(0.72D, color, 42, time * 1.2D, 25.0F);
        drawRing(0.62D, 0xE9C4FF, 38, -time * 1.65D, -58.0F);
        GL11.glLineWidth(1.6F);
        drawRing(0.72D, color, 210, time * 1.2D, 25.0F);
        drawRing(0.62D, 0xE9C4FF, 195, -time * 1.65D, -58.0F);

        float wirelessActivity = tile.getWirelessRenderActivity(partialTicks);
        if (wirelessActivity > 0.002F) {
            for (int i = 0; i < 3; i++) {
                double phase = (time * 0.055D + i / 3.0D) % 1.0D;
                double radius = 0.85D + phase * 2.75D;
                int ringColor = Color.HSBtoRGB((float) ((time * 0.006D
                        + phase + i / 3.0D) % 1.0D), 0.66F, 1.0F);
                int ringAlpha = (int) ((1.0D - phase) * 210.0D * wirelessActivity);
                GL11.glLineWidth(2.0F + wirelessActivity * 2.5F);
                drawRing(radius, ringColor, ringAlpha, 0.0D, 0.0F);
            }
        }

        // Relay backbone beams represent real tower energy flowing inward.
        // Wireless output never creates a target beam.
        if (tile.getInputRenderActivity(partialTicks) > 0.02F) {
            BlockPos target = tile.getOutputPos();
            if (target != null
                    && tile.getOutputDimension() == tile.getWorld().provider.getDimension()) {
                double dx = target.getX() - tile.getPos().getX();
                double dz = target.getZ() - tile.getPos().getZ();
                double endpointHeight = tile.getOutputType()
                        == TileEntityStarstreamRelay.EndpointType.NEXUS ? 8.65D : 1.45D;
                double dy = target.getY() + endpointHeight
                        - (tile.getPos().getY() + 1.45D + bob);
                double distance = Math.sqrt(dx * dx + dz * dz);
                double arcHeight = Math.min(12.0D, 1.8D + distance * 0.055D);
                TesrConstellationCrystal.renderStarstreamBeam(
                        dx, dy, dz, arcHeight, color, time);
            }
        }

        GL11.glPointSize(1.0F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private static void drawOctahedron(double radius, int color, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 4; i++) {
            double a0 = Math.PI * 2.0D * i / 4.0D;
            double a1 = Math.PI * 2.0D * (i + 1) / 4.0D;
            vertex(buffer, 0.0D, radius, 0.0D, color, alpha);
            vertex(buffer, Math.cos(a0) * radius, 0.0D, Math.sin(a0) * radius, color, alpha);
            vertex(buffer, Math.cos(a1) * radius, 0.0D, Math.sin(a1) * radius, color, alpha);
            vertex(buffer, 0.0D, -radius, 0.0D, color, alpha);
            vertex(buffer, Math.cos(a1) * radius, 0.0D, Math.sin(a1) * radius, color, alpha);
            vertex(buffer, Math.cos(a0) * radius, 0.0D, Math.sin(a0) * radius, color, alpha);
        }
        tessellator.draw();
    }

    private static void drawRing(double radius, int color, int alpha,
                                 double rotation, float tilt) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) (rotation % 360.0D), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(tilt, 1.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < RING_SEGMENTS; i++) {
            double angle = Math.PI * 2.0D * i / RING_SEGMENTS;
            vertex(buffer, Math.cos(angle) * radius, 0.0D,
                    Math.sin(angle) * radius, color, alpha);
        }
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z,
                               int color, int alpha) {
        buffer.pos(x, y, z)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                .endVertex();
    }
}
