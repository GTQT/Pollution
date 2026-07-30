package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.tile.TileEntityMineralExtractor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/**
 * Render-only adaptation of ChromatiCraft's mineral extractor presentation.
 *
 * <p>No original textures or models are required: the frame, moving flares,
 * central crystal, and orbit rings are generated directly as colored geometry.</p>
 */
public class TesrMineralExtractor extends TileEntitySpecialRenderer<TileEntityMineralExtractor> {

    private static final int CUBE_EDGE_COUNT = 12;
    private static final int RING_SEGMENTS = 64;

    @Override
    public void render(TileEntityMineralExtractor tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        double time = tile.hasWorld()
                ? tile.getWorld().getTotalWorldTime() + partialTicks
                : System.currentTimeMillis() / 50.0D;

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        renderEnergyFrame(time);
        renderEdgeFlares(time);
        renderOrbitRings(time);
        renderCrystal(time);

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

    private static void renderEnergyFrame(double time) {
        GL11.glLineWidth(5.0F);
        drawCubeEdges(0.485D, time, 34);
        GL11.glLineWidth(1.5F);
        drawCubeEdges(0.487D, time, 210);
    }

    private static void drawCubeEdges(double radius, double time, int alpha) {
        double[][] corners = {
                {-radius, -radius, -radius}, {radius, -radius, -radius},
                {radius, radius, -radius}, {-radius, radius, -radius},
                {-radius, -radius, radius}, {radius, -radius, radius},
                {radius, radius, radius}, {-radius, radius, radius}
        };
        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < CUBE_EDGE_COUNT; i++) {
            int colorA = rainbow(time * 2.0D + i * 23.0D);
            int colorB = rainbow(time * 2.0D + i * 23.0D + 42.0D);
            addLine(buffer, corners[edges[i][0]], corners[edges[i][1]], colorA, colorB, alpha);
        }
        tessellator.draw();
    }

    private static void renderEdgeFlares(double time) {
        double[] cursor = squareCursor(time * 0.035D);
        double px = -0.5D + cursor[0];
        double py = -0.5D + cursor[1];
        int color = rainbow(time * 4.0D);

        drawGlowPoint(px, py, -0.505D, 0.045D, color, 190);
        drawGlowPoint(-px, -py, -0.505D, 0.045D, color, 190);
        drawGlowPoint(-px, py, 0.505D, 0.045D, color, 190);
        drawGlowPoint(px, -py, 0.505D, 0.045D, color, 190);

        drawGlowPoint(-0.505D, py, px, 0.045D, color, 190);
        drawGlowPoint(-0.505D, -py, -px, 0.045D, color, 190);
        drawGlowPoint(0.505D, py, -px, 0.045D, color, 190);
        drawGlowPoint(0.505D, -py, px, 0.045D, color, 190);
    }

    private static double[] squareCursor(double time) {
        double phase = time % 4.0D;
        if (phase < 1.0D) {
            return new double[]{phase, 0.0D};
        }
        if (phase < 2.0D) {
            return new double[]{1.0D, phase - 1.0D};
        }
        if (phase < 3.0D) {
            return new double[]{3.0D - phase, 1.0D};
        }
        return new double[]{0.0D, 4.0D - phase};
    }

    private static void renderOrbitRings(double time) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) (time * 1.8D % 360.0D), 0.0F, 1.0F, 0.0F);
        GL11.glLineWidth(2.0F);
        drawRing(0.37D, time, 135);
        GlStateManager.rotate(67.5F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) (-time * 1.2D % 360.0D), 0.0F, 0.0F, 1.0F);
        drawRing(0.31D, time + 90.0D, 110);
        GlStateManager.popMatrix();
    }

    private static void drawRing(double radius, double time, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < RING_SEGMENTS; i++) {
            double angle = Math.PI * 2.0D * i / RING_SEGMENTS;
            int color = rainbow(time * 2.0D + i * 360.0D / RING_SEGMENTS);
            addVertex(buffer,
                    Math.cos(angle) * radius,
                    0.0D,
                    Math.sin(angle) * radius,
                    color,
                    alpha);
        }
        tessellator.draw();
    }

    private static void renderCrystal(double time) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(
                Math.sin(time / 32.0D) * 0.035D,
                Math.sin(time / 18.0D) * 0.055D,
                Math.sin(time / 41.0D) * 0.035D);
        GlStateManager.rotate((float) (time * 2.25D % 360.0D), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (18.0D * Math.cos(time / 45.0D)), 1.0F, 0.0F, 1.0F);

        drawCrystalGeometry(0.18D, 0.42D, time, 155);
        GlStateManager.scale(0.72D, 0.72D, 0.72D);
        drawCrystalGeometry(0.18D, 0.42D, time + 70.0D, 235);
        GlStateManager.popMatrix();
    }

    private static void drawCrystalGeometry(double radius, double halfHeight, double time, int alpha) {
        double middle = 0.13D;
        double[][] top = {
                {0.0D, halfHeight, 0.0D},
                {-radius, middle, -radius}, {radius, middle, -radius},
                {radius, middle, radius}, {-radius, middle, radius}
        };
        double[][] bottom = {
                {0.0D, -halfHeight, 0.0D},
                {-radius, -middle, -radius}, {radius, -middle, -radius},
                {radius, -middle, radius}, {-radius, -middle, radius}
        };

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 4; i++) {
            int next = 1 + (i + 1) % 4;
            int color = rainbow(time * 3.0D + i * 52.0D);
            addTriangle(buffer, top[0], top[i + 1], top[next], color, alpha);
            addTriangle(buffer, bottom[0], bottom[next], bottom[i + 1], color, alpha);

            int nextBottom = 1 + (i + 1) % 4;
            addTriangle(buffer, top[i + 1], bottom[i + 1], bottom[nextBottom], color, alpha);
            addTriangle(buffer, top[i + 1], bottom[nextBottom], top[next], color, alpha);
        }
        tessellator.draw();
    }

    private static void drawGlowPoint(double x, double y, double z, double radius, int color, int alpha) {
        double[][] points = {
                {x, y + radius, z}, {x, y - radius, z},
                {x - radius, y, z}, {x + radius, y, z},
                {x, y, z - radius}, {x, y, z + radius}
        };
        int[][] faces = {
                {0, 2, 4}, {0, 4, 3}, {0, 3, 5}, {0, 5, 2},
                {1, 4, 2}, {1, 3, 4}, {1, 5, 3}, {1, 2, 5}
        };

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int[] face : faces) {
            addTriangle(buffer, points[face[0]], points[face[1]], points[face[2]], color, alpha);
        }
        tessellator.draw();
    }

    private static void addLine(BufferBuilder buffer, double[] start, double[] end,
                                int startColor, int endColor, int alpha) {
        addVertex(buffer, start[0], start[1], start[2], startColor, alpha);
        addVertex(buffer, end[0], end[1], end[2], endColor, alpha);
    }

    private static void addTriangle(BufferBuilder buffer, double[] first, double[] second,
                                    double[] third, int color, int alpha) {
        addVertex(buffer, first[0], first[1], first[2], color, alpha);
        addVertex(buffer, second[0], second[1], second[2], color, alpha);
        addVertex(buffer, third[0], third[1], third[2], color, alpha);
    }

    private static void addVertex(BufferBuilder buffer, double x, double y, double z,
                                  int color, int alpha) {
        buffer.pos(x, y, z)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                .endVertex();
    }

    private static int rainbow(double phase) {
        float hue = (float) ((phase % 360.0D + 360.0D) % 360.0D / 360.0D);
        return Color.HSBtoRGB(hue, 0.78F, 1.0F) & 0xFFFFFF;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityMineralExtractor tile) {
        return false;
    }
}
