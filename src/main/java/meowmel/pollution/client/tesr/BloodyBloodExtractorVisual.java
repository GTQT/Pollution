package meowmel.pollution.client.tesr;

import meowmel.pollution.common.metatileentity.multiblock.bloodMagic.MetaTileEntityBloodyBloodExtractor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/** Additional optics only. The ten container TESRs render their own spheres and dragons. */
public final class BloodyBloodExtractorVisual {
    private static final FloatBuffer BASIS = BufferUtils.createFloatBuffer(16);
    private BloodyBloodExtractorVisual() {}

    public static void render(MetaTileEntityBloodyBloodExtractor machine, double x, double y, double z,
                              int progress, int buffered, int mask, boolean active, float partialTicks) {
        BlockPos origin = machine.shapePosition(0, 0, 0), right = machine.shapePosition(1, 0, 0).subtract(origin);
        BlockPos back = machine.shapePosition(0, 0, 1).subtract(origin);
        BASIS.clear();
        BASIS.put(new float[]{right.getX(), 0, right.getZ(), 0, 0, 1, 0, 0,
                back.getX(), 0, back.getZ(), 0, 0, 0, 0, 1}).flip();
        GL11.glPushMatrix();
        GL11.glTranslated(x + origin.getX() - machine.getPos().getX() + 0.5,
                y + origin.getY() - machine.getPos().getY(), z + origin.getZ() - machine.getPos().getZ() + 0.5);
        GL11.glMultMatrix(BASIS);
        renderOptics(progress, buffered, mask, active, machine.getWorld().getTotalWorldTime() + partialTicks);
        GL11.glPopMatrix();
    }

    /** Also used by the offscreen rendering check. Raw GL inside attrib scope leaves Minecraft's state cache intact. */
    public static void renderOptics(int progress, int buffered, int mask, boolean active, double time) {
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_LIGHTING_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDepthMask(false);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (buffered > 0) fluid(4.02, 4.02 + Math.min(1, buffered / 16000.0) * 4.85);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            ring(4.1 + progress / 100.0 * 4.7, active ? 0.85F : 0.25F);
            if (active) {
                BufferBuilder b = Tessellator.getInstance().getBuffer();
                b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                for (int i = 0; i < 10; i++) {
                    if ((mask & 1 << i) == 0) continue;
                    double sign = i < 5 ? -1 : 1, y = 4.56 + i % 5;
                    beam(b, sign * 2.9, sign * 1.55, y, 0.012, 0.6F);
                    double pulse = (time * 0.07 + i * 0.17) % 1;
                    double px = sign * (2.9 - pulse * 1.35);
                    beam(b, px, px + sign * 0.1, y, 0.036, 0.95F);
                }
                Tessellator.getInstance().draw();
            }
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            GL11.glPopAttrib();
        }
    }

    private static void ring(double y, float alpha) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 48; i++) {
            double a = i * Math.PI / 24, c = (i + 1) * Math.PI / 24;
            point(b, 1.30, a, y, alpha); point(b, 1.36, a, y, alpha);
            point(b, 1.36, c, y, alpha); point(b, 1.30, c, y, alpha);
        }
        Tessellator.getInstance().draw();
    }
    private static void fluid(double bottom, double top) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 32; i++) {
            double a = i * Math.PI / 16, c = (i + 1) * Math.PI / 16;
            point(b, 1.36, a, bottom, 0.38F); point(b, 1.36, c, bottom, 0.38F);
            point(b, 1.36, c, top, 0.38F); point(b, 1.36, a, top, 0.38F);
            point(b, 0.74, a, top, 0.48F); point(b, 1.36, a, top, 0.48F);
            point(b, 1.36, c, top, 0.48F); point(b, 0.74, c, top, 0.48F);
        }
        Tessellator.getInstance().draw();
    }
    private static void point(BufferBuilder b, double r, double a, double y, float alpha) {
        b.pos(Math.cos(a) * r, y, Math.sin(a) * r).color(0.82F, 0.02F, 0.12F, alpha).endVertex();
    }
    private static void beam(BufferBuilder b, double start, double end, double y, double width, float alpha) {
        b.pos(start, y - width, 0).color(1F, 0.03F, 0.12F, alpha).endVertex();
        b.pos(end, y - width, 0).color(1F, 0.03F, 0.12F, alpha).endVertex();
        b.pos(end, y + width, 0).color(1F, 0.03F, 0.12F, alpha).endVertex();
        b.pos(start, y + width, 0).color(1F, 0.03F, 0.12F, alpha).endVertex();
        b.pos(start, y, -width).color(1F, 0.03F, 0.12F, alpha).endVertex();
        b.pos(end, y, -width).color(1F, 0.03F, 0.12F, alpha).endVertex();
        b.pos(end, y, width).color(1F, 0.03F, 0.12F, alpha).endVertex();
        b.pos(start, y, width).color(1F, 0.03F, 0.12F, alpha).endVertex();
    }
}
