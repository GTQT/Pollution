package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.tile.TileEntityStarstreamObeliskCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/** Procedural giant obelisk model, halo system and sixteen-channel star links. */
public class TesrStarstreamObeliskCore extends TileEntitySpecialRenderer<TileEntityStarstreamObeliskCore> {

    private static final int SIDES = 8;
    private static final int RING_SEGMENTS = 96;
    private static final ResourceLocation OBELISK_TEXTURE = new ResourceLocation(
            "pollution", "textures/blocks/starstream/obelisk_core.png");
    private static final int[][] ANCHOR_OFFSETS = {
            {0, -11}, {4, -10}, {8, -8}, {10, -4},
            {11, 0}, {10, 4}, {8, 8}, {4, 10},
            {0, 11}, {-4, 10}, {-8, 8}, {-10, 4},
            {-11, 0}, {-10, -4}, {-8, -8}, {-4, -10}
    };

    @Override
    public boolean isGlobalRenderer(TileEntityStarstreamObeliskCore tile) {
        // The procedural body, inclined halos and anchor links extend far
        // beyond the core block. Rendering it as a global TESR prevents Forge
        // from dropping the entire effect when only that block leaves the
        // camera frustum. Distance culling is still controlled by the TE.
        return true;
    }

    @Override
    public void render(TileEntityStarstreamObeliskCore tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (!tile.hasWorld()) return;
        double time = tile.getWorld().getTotalWorldTime() + partialTicks;

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(true);

        renderObeliskBody(time);
        renderEnergyEffects(tile.isLinkedAndFormed(), time);
        renderWirelessBeacon(tile, time);

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

    private static void renderObeliskBody(double time) {
        // Textured stepped foundation and a short, broad lower monument. The
        // old single 19-block spike looked like a stretched primitive.
        Minecraft.getMinecraft().getTextureManager().bindTexture(OBELISK_TEXTURE);
        GlStateManager.enableTexture2D();
        drawTexturedFrustum(3.05D, 2.68D, 0.00D, 0.45D, 0xAAA6D9, 255);
        drawTexturedFrustum(2.68D, 2.18D, 0.45D, 1.10D, 0x8C86C8, 255);
        drawTexturedFrustum(2.18D, 1.74D, 1.10D, 2.05D, 0x8178C3, 255);
        drawTexturedFrustum(1.74D, 1.48D, 2.05D, 6.35D, 0x7770B8, 255);
        drawTexturedFrustum(1.48D, 0.42D, 6.35D, 7.65D, 0x8E82D6, 255);

        // Four cardinal fins break up the shaft silhouette and visually lock
        // the rendered model into the four-sided multiblock platform.
        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 1.0F, 0.0F);
            drawTexturedBlade(1.20D, 2.75D, 1.35D, 5.75D, 0.34D,
                    i % 2 == 0 ? 0x7ADDF5 : 0xC488F2, 235);
            GlStateManager.popMatrix();
        }

        // A separate crown floats above the central star core. Its inverted
        // lower tip and faceted upper point retain the obelisk identity.
        drawTexturedFrustum(0.18D, 1.82D, 9.65D, 10.75D, 0x9C8CE5, 255);
        drawTexturedFrustum(1.82D, 1.36D, 10.75D, 13.45D, 0x8275CC, 255);
        drawTexturedFrustum(1.36D, 0.05D, 13.45D, 16.55D, 0xB09BFF, 255);
        GlStateManager.disableTexture2D();

        // Four luminous rune bands are embedded into the lower monument.
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);
        for (int i = 0; i < 3; i++) {
            double pulse = 0.72D + Math.sin(time * 0.09D + i) * 0.12D;
            drawFrustum(1.53D - i * 0.08D, 1.53D - i * 0.08D,
                    2.75D + i * 1.45D, 2.89D + i * 1.45D,
                    i % 2 == 0 ? 0x48E8FF : 0xD45CFF,
                    i % 2 == 0 ? 0x48E8FF : 0xD45CFF,
                    (int) (180.0D * pulse));
        }

        // The exposed stellar heart replaces the featureless middle section.
        drawSphere(1.24D + Math.sin(time * 0.08D) * 0.06D,
                8.65D, 0x69EAFF, 165);
        drawSphere(0.72D + Math.sin(time * 0.11D) * 0.04D,
                8.65D, 0xF2C0FF, 220);
        drawFrustum(0.18D, 0.10D, 1.25D, 7.75D, 0x71F5FF, 0xD86BFF, 155);
        drawFrustum(0.10D, 0.05D, 9.55D, 16.10D, 0xD86BFF, 0xF5E0FF, 145);

        renderOrbitingShards(time);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    private static void renderEnergyEffects(boolean linked, double time) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        double bob = Math.sin(time * 0.075D) * 0.22D;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, bob, 0.0D);

        GL11.glLineWidth(6.0F);
        drawRotatingRing(3.55D, 2.1D, time * 0.55D, 58, 0.0F, 0x51E8FF);
        drawRotatingRing(4.65D, 8.65D, -time * 0.38D, 66, 58.0F, 0xBB6CFF);
        drawRotatingRing(5.35D, 8.65D, time * 0.72D, 62, -47.0F, 0xF1D878);

        GL11.glLineWidth(1.8F);
        drawRotatingRing(3.55D, 2.1D, time * 0.55D, 205, 0.0F, 0x7CF4FF);
        drawRotatingRing(4.65D, 8.65D, -time * 0.38D, 220, 58.0F, 0xD294FF);
        drawRotatingRing(5.35D, 8.65D, time * 0.72D, 212, -47.0F, 0xFFF2A3);

        if (linked) {
            GL11.glLineWidth(4.5F);
            drawAnchorLinks(time, 38);
            GL11.glLineWidth(1.25F);
            drawAnchorLinks(time, 185);
        }

        GlStateManager.popMatrix();
    }

    private static void renderWirelessBeacon(TileEntityStarstreamObeliskCore tile, double time) {
        float activity = tile.getWirelessRenderActivity(0.0F);
        if (activity <= 0.002F) return;

        double height = Math.max(12.0D, 256.0D - tile.getPos().getY() - 8.65D);
        double radius = 0.55D + activity * 0.95D;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 8.65D, 0.0D);

        drawRainbowBeaconShell(radius, height, time, activity);
        drawBeaconCore(radius * 0.30D, height, (int) (175.0F * activity));

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2.0F + activity * 3.5F);
        drawBeaconHelix(radius * 1.30D, height, time, 0.0D,
                (int) (205.0F * activity));
        drawBeaconHelix(radius * 1.30D, height, -time, Math.PI,
                (int) (165.0F * activity));

        for (int i = 0; i < 3; i++) {
            double phase = (time * 0.035D + i / 3.0D) % 1.0D;
            double ringRadius = 1.5D + phase * (4.0D + activity * 2.0D);
            int alpha = (int) ((1.0D - phase) * 180.0D * activity);
            drawRotatingRing(ringRadius, 0.08D + i * 0.035D,
                    time * (0.5D + i * 0.15D), alpha, 0.0F,
                    Color.HSBtoRGB((float) ((time * 0.004D + i / 3.0D) % 1.0D),
                            0.70F, 1.0F));
        }
        GlStateManager.popMatrix();
    }

    private static void drawRainbowBeaconShell(double radius, double height,
                                                double time, float activity) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int verticalSegments = Math.max(12, Math.min(48, (int) (height / 4.0D)));
        int alpha = (int) (105.0F * activity);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int y = 0; y < verticalSegments; y++) {
            double y0 = height * y / verticalSegments;
            double y1 = height * (y + 1) / verticalSegments;
            int c0 = Color.HSBtoRGB((float) ((y / (double) verticalSegments
                    - time * 0.008D + 1.0D) % 1.0D), 0.78F, 1.0F);
            int c1 = Color.HSBtoRGB((float) (((y + 1.0D) / verticalSegments
                    - time * 0.008D + 1.0D) % 1.0D), 0.78F, 1.0F);
            for (int side = 0; side < 8; side++) {
                double a0 = Math.PI * 2.0D * side / 8.0D + time * 0.002D;
                double a1 = Math.PI * 2.0D * (side + 1) / 8.0D + time * 0.002D;
                vertex(buffer, Math.cos(a0) * radius, y0,
                        Math.sin(a0) * radius, c0, alpha);
                vertex(buffer, Math.cos(a1) * radius, y0,
                        Math.sin(a1) * radius, c0, alpha);
                vertex(buffer, Math.cos(a1) * radius, y1,
                        Math.sin(a1) * radius, c1, alpha);
                vertex(buffer, Math.cos(a0) * radius, y1,
                        Math.sin(a0) * radius, c1, alpha);
            }
        }
        tessellator.draw();
    }

    private static void drawBeaconCore(double radius, double height, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 2; i++) {
            double angle = Math.PI * i / 2.0D + Math.PI / 4.0D;
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;
            vertex(buffer, -dx, 0.0D, -dz, 0xF8FDFF, alpha);
            vertex(buffer, dx, 0.0D, dz, 0xF8FDFF, alpha);
            vertex(buffer, dx, height, dz, 0xF8FDFF, alpha);
            vertex(buffer, -dx, height, -dz, 0xF8FDFF, alpha);
        }
        tessellator.draw();
    }

    private static void drawBeaconHelix(double radius, double height, double time,
                                        double phase, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int segments = 160;
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= segments; i++) {
            double progress = i / (double) segments;
            double angle = progress * Math.PI * 16.0D + time * 0.045D + phase;
            int color = Color.HSBtoRGB((float) ((progress + time * 0.004D) % 1.0D),
                    0.62F, 1.0F);
            vertex(buffer, Math.cos(angle) * radius, progress * height,
                    Math.sin(angle) * radius, color, alpha);
        }
        tessellator.draw();
    }

    private static void renderOrbitingShards(double time) {
        for (int i = 0; i < 8; i++) {
            double orbit = time * (0.34D + (i & 1) * 0.08D) + i * 45.0D;
            double radius = 2.65D + (i % 3) * 0.42D;
            double height = 8.65D + Math.sin(time * 0.08D + i * 0.9D) * 1.15D;
            GlStateManager.pushMatrix();
            GlStateManager.rotate((float) (orbit % 360.0D), 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(radius, height, 0.0D);
            GlStateManager.rotate((float) (-orbit * 2.1D % 360.0D), 0.3F, 1.0F, 0.4F);
            int color = i % 2 == 0 ? 0x63E8FF : 0xD579FF;
            drawFrustum(0.30D, 0.05D, -0.52D, 0.52D, color, 0xF4E9FF, 175);
            GlStateManager.popMatrix();
        }
    }

    private static void drawTexturedFrustum(double bottomRadius, double topRadius,
                                            double bottomY, double topY,
                                            int tint, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (int i = 0; i < SIDES; i++) {
            double a0 = Math.PI * 2.0D * i / SIDES + Math.PI / 8.0D;
            double a1 = Math.PI * 2.0D * (i + 1) / SIDES + Math.PI / 8.0D;
            texturedVertex(buffer, Math.cos(a0) * bottomRadius, bottomY,
                    Math.sin(a0) * bottomRadius, 0.0D, 1.0D, tint, alpha);
            texturedVertex(buffer, Math.cos(a1) * bottomRadius, bottomY,
                    Math.sin(a1) * bottomRadius, 1.0D, 1.0D, tint, alpha);
            texturedVertex(buffer, Math.cos(a1) * topRadius, topY,
                    Math.sin(a1) * topRadius, 1.0D, 0.0D, tint, alpha);
            texturedVertex(buffer, Math.cos(a0) * topRadius, topY,
                    Math.sin(a0) * topRadius, 0.0D, 0.0D, tint, alpha);
        }
        tessellator.draw();

        if (topRadius > 0.1D) {
            buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_COLOR);
            texturedVertex(buffer, 0.0D, topY, 0.0D, 0.5D, 0.5D, tint, alpha);
            for (int i = 0; i <= SIDES; i++) {
                double angle = Math.PI * 2.0D * i / SIDES + Math.PI / 8.0D;
                texturedVertex(buffer, Math.cos(angle) * topRadius, topY,
                        Math.sin(angle) * topRadius,
                        0.5D + Math.cos(angle) * 0.5D,
                        0.5D + Math.sin(angle) * 0.5D, tint, alpha);
            }
            tessellator.draw();
        }
    }

    /** A thick tapered rune blade pointing along local +X. */
    private static void drawTexturedBlade(double innerRadius, double outerRadius,
                                          double bottomY, double topY, double halfWidth,
                                          int tint, int alpha) {
        double outerBottomY = bottomY + 0.85D;
        double outerTopY = topY - 0.85D;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        // Two broad decorated faces.
        texturedVertex(buffer, innerRadius, bottomY, -halfWidth, 0, 1, tint, alpha);
        texturedVertex(buffer, outerRadius, outerBottomY, -halfWidth, 1, 1, tint, alpha);
        texturedVertex(buffer, outerRadius, outerTopY, -halfWidth, 1, 0, tint, alpha);
        texturedVertex(buffer, innerRadius, topY, -halfWidth, 0, 0, tint, alpha);
        texturedVertex(buffer, innerRadius, topY, halfWidth, 0, 0, tint, alpha);
        texturedVertex(buffer, outerRadius, outerTopY, halfWidth, 1, 0, tint, alpha);
        texturedVertex(buffer, outerRadius, outerBottomY, halfWidth, 1, 1, tint, alpha);
        texturedVertex(buffer, innerRadius, bottomY, halfWidth, 0, 1, tint, alpha);

        // Outer edge and the two sloped caps give the blade real thickness.
        texturedVertex(buffer, outerRadius, outerBottomY, -halfWidth, 0, 1, tint, alpha);
        texturedVertex(buffer, outerRadius, outerBottomY, halfWidth, 1, 1, tint, alpha);
        texturedVertex(buffer, outerRadius, outerTopY, halfWidth, 1, 0, tint, alpha);
        texturedVertex(buffer, outerRadius, outerTopY, -halfWidth, 0, 0, tint, alpha);
        texturedVertex(buffer, innerRadius, bottomY, -halfWidth, 0, 1, tint, alpha);
        texturedVertex(buffer, innerRadius, bottomY, halfWidth, 1, 1, tint, alpha);
        texturedVertex(buffer, outerRadius, outerBottomY, halfWidth, 1, 0, tint, alpha);
        texturedVertex(buffer, outerRadius, outerBottomY, -halfWidth, 0, 0, tint, alpha);
        texturedVertex(buffer, innerRadius, topY, -halfWidth, 0, 0, tint, alpha);
        texturedVertex(buffer, outerRadius, outerTopY, -halfWidth, 1, 0, tint, alpha);
        texturedVertex(buffer, outerRadius, outerTopY, halfWidth, 1, 1, tint, alpha);
        texturedVertex(buffer, innerRadius, topY, halfWidth, 0, 1, tint, alpha);
        tessellator.draw();
    }

    private static void drawSphere(double radius, double centerY, int color, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int latitudeSegments = 12;
        int longitudeSegments = 32;
        for (int latitude = 0; latitude < latitudeSegments; latitude++) {
            double lat0 = -Math.PI / 2.0D + Math.PI * latitude / latitudeSegments;
            double lat1 = -Math.PI / 2.0D + Math.PI * (latitude + 1) / latitudeSegments;
            double y0 = centerY + Math.sin(lat0) * radius;
            double y1 = centerY + Math.sin(lat1) * radius;
            double r0 = Math.cos(lat0) * radius;
            double r1 = Math.cos(lat1) * radius;
            buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (int longitude = 0; longitude <= longitudeSegments; longitude++) {
                double angle = Math.PI * 2.0D * longitude / longitudeSegments;
                vertex(buffer, Math.cos(angle) * r0, y0, Math.sin(angle) * r0, color, alpha);
                vertex(buffer, Math.cos(angle) * r1, y1, Math.sin(angle) * r1, color, alpha);
            }
            tessellator.draw();
        }
    }

    private static void drawRotatingRing(double radius, double height, double rotation,
                                         int alpha, float tilt, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, height, 0.0D);
        GlStateManager.rotate((float) (rotation % 360.0D), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(tilt, 1.0F, 0.0F, 1.0F);
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
        GlStateManager.popMatrix();
    }

    private static void drawAnchorLinks(double time, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < ANCHOR_OFFSETS.length; i++) {
            int color = Color.HSBtoRGB((float) ((i / 16.0D + time * 0.0025D) % 1.0D), 0.7F, 1.0F);
            double angle = Math.atan2(ANCHOR_OFFSETS[i][1], ANCHOR_OFFSETS[i][0]);
            double innerX = Math.cos(angle) * 1.5D;
            double innerZ = Math.sin(angle) * 1.5D;
            double wave = Math.sin(time * 0.11D + i * 0.72D) * 0.18D;
            buffer.pos(innerX, 1.0D + wave, innerZ)
                    .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                    .endVertex();
            buffer.pos(ANCHOR_OFFSETS[i][0], 0.72D - wave, ANCHOR_OFFSETS[i][1])
                    .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                    .endVertex();
        }
        tessellator.draw();
    }

    private static void drawFrustum(double bottomRadius, double topRadius,
                                    double bottomY, double topY,
                                    int bottomColor, int topColor, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < SIDES; i++) {
            double a0 = Math.PI * 2.0D * i / SIDES + Math.PI / 8.0D;
            double a1 = Math.PI * 2.0D * (i + 1) / SIDES + Math.PI / 8.0D;
            double bx0 = Math.cos(a0) * bottomRadius;
            double bz0 = Math.sin(a0) * bottomRadius;
            double bx1 = Math.cos(a1) * bottomRadius;
            double bz1 = Math.sin(a1) * bottomRadius;
            double tx0 = Math.cos(a0) * topRadius;
            double tz0 = Math.sin(a0) * topRadius;
            double tx1 = Math.cos(a1) * topRadius;
            double tz1 = Math.sin(a1) * topRadius;
            vertex(buffer, bx0, bottomY, bz0, bottomColor, alpha);
            vertex(buffer, bx1, bottomY, bz1, bottomColor, alpha);
            vertex(buffer, tx1, topY, tz1, topColor, alpha);
            vertex(buffer, tx0, topY, tz0, topColor, alpha);
        }
        tessellator.draw();

        if (topRadius > 0.1D) {
            buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            vertex(buffer, 0.0D, topY, 0.0D, topColor, alpha);
            for (int i = 0; i <= SIDES; i++) {
                double angle = Math.PI * 2.0D * i / SIDES + Math.PI / 8.0D;
                vertex(buffer, Math.cos(angle) * topRadius, topY,
                        Math.sin(angle) * topRadius, topColor, alpha);
            }
            tessellator.draw();
        }
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z, int color, int alpha) {
        buffer.pos(x, y, z)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                .endVertex();
    }

    private static void texturedVertex(BufferBuilder buffer, double x, double y, double z,
                                       double u, double v, int color, int alpha) {
        buffer.pos(x, y, z).tex(u, v)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                .endVertex();
    }
}
