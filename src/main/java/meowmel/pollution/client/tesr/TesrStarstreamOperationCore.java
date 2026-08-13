package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.tile.TileEntityStarstreamOperationCore;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/** Permanently moving spherical heart used by endgame machines and rituals. */
public class TesrStarstreamOperationCore
        extends TileEntitySpecialRenderer<TileEntityStarstreamOperationCore> {

    private static final int RING_SEGMENTS = 72;

    @Override
    public boolean isGlobalRenderer(TileEntityStarstreamOperationCore tile) {
        return true;
    }

    @Override
    public void render(TileEntityStarstreamOperationCore tile,
                       double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        if (!tile.hasWorld()) return;
        double time = tile.getWorld().getTotalWorldTime() + partialTicks;
        double bob = Math.sin(time * 0.085D) * 0.12D;
        boolean bound = tile.isNetworkBound();
        int rainbow = Color.HSBtoRGB((float) ((time * 0.0035D) % 1.0D),
                0.58F, 1.0F);
        int bodyColor = bound ? rainbow : 0x756C8D;

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.18D + bob, z + 0.5D);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) (time * 1.65D % 360.0D),
                0.25F, 1.0F, 0.18F);
        drawSphere(0.48D, bodyColor, 65);
        drawSphere(0.34D, bound ? 0x9BEFFF : 0x655F78, 205);
        drawSphere(0.18D, bound ? 0xFFF4D7 : 0xB4AFC1, 245);
        drawSphereGrid(0.505D, bound ? rainbow : 0x948BAA,
                bound ? 205 : 120);
        GlStateManager.popMatrix();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(4.6F);
        drawRing(0.68D, bound ? rainbow : 0x777080, 42,
                time * 1.35D, 28.0F);
        drawRing(0.76D, bound ? 0xD990FF : 0x615A70, 36,
                -time * 1.05D, -55.0F);
        GL11.glLineWidth(1.45F);
        drawRing(0.68D, bound ? rainbow : 0x938B9E, 215,
                time * 1.35D, 28.0F);
        drawRing(0.76D, bound ? 0xE8B9FF : 0x8C8498, 190,
                -time * 1.05D, -55.0F);

        renderOrbitingNodes(time, bound);

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

    private static void renderOrbitingNodes(double time, boolean bound) {
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(time * (1.1D + (i & 1) * 0.18D)
                    + i * 45.0D);
            double radius = 0.86D + (i % 3) * 0.08D;
            double nodeY = Math.sin(time * 0.09D + i * 0.8D) * 0.18D;
            int color = bound
                    ? Color.HSBtoRGB((float) ((i / 8.0D + time * 0.003D) % 1.0D),
                    0.60F, 1.0F)
                    : 0x817A90;
            GlStateManager.pushMatrix();
            GlStateManager.translate(Math.cos(angle) * radius, nodeY,
                    Math.sin(angle) * radius);
            drawSphere(0.065D, color, bound ? 235 : 130);
            GlStateManager.popMatrix();
        }
    }

    private static void drawSphere(double radius, int color, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int latitudes = 12;
        int longitudes = 28;
        for (int latitude = 0; latitude < latitudes; latitude++) {
            double lat0 = -Math.PI / 2.0D + Math.PI * latitude / latitudes;
            double lat1 = -Math.PI / 2.0D + Math.PI * (latitude + 1) / latitudes;
            double y0 = Math.sin(lat0) * radius;
            double y1 = Math.sin(lat1) * radius;
            double r0 = Math.cos(lat0) * radius;
            double r1 = Math.cos(lat1) * radius;
            buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (int longitude = 0; longitude <= longitudes; longitude++) {
                double angle = Math.PI * 2.0D * longitude / longitudes;
                vertex(buffer, Math.cos(angle) * r0, y0,
                        Math.sin(angle) * r0, color, alpha);
                vertex(buffer, Math.cos(angle) * r1, y1,
                        Math.sin(angle) * r1, color, alpha);
            }
            tessellator.draw();
        }
    }

    private static void drawSphereGrid(double radius, int color, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        for (int latitude = -2; latitude <= 2; latitude++) {
            double pitch = latitude * Math.PI / 10.0D;
            double y = Math.sin(pitch) * radius;
            double ringRadius = Math.cos(pitch) * radius;
            buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i < RING_SEGMENTS; i++) {
                double angle = Math.PI * 2.0D * i / RING_SEGMENTS;
                vertex(buffer, Math.cos(angle) * ringRadius, y,
                        Math.sin(angle) * ringRadius, color, alpha);
            }
            tessellator.draw();
        }
        for (int longitude = 0; longitude < 8; longitude++) {
            double yaw = longitude * Math.PI / 4.0D;
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i <= 36; i++) {
                double pitch = -Math.PI / 2.0D + Math.PI * i / 36.0D;
                double horizontal = Math.cos(pitch) * radius;
                vertex(buffer, Math.cos(yaw) * horizontal,
                        Math.sin(pitch) * radius,
                        Math.sin(yaw) * horizontal, color, alpha);
            }
            tessellator.draw();
        }
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
                .color((color >> 16) & 255, (color >> 8) & 255,
                        color & 255, alpha)
                .endVertex();
    }
}
