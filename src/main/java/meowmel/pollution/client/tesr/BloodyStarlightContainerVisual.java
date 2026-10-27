package meowmel.pollution.client.tesr;

import meowmel.pollution.client.entity.dragon.model.BuiltinFactory;
import meowmel.pollution.client.entity.dragon.model.DragonModel;
import meowmel.pollution.common.entity.dragon.DragonLifeStage;
import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

/** Shared geometry for placed, held, inventory and dropped containers. Client-only. */
public final class BloodyStarlightContainerVisual {
    private static final DragonModel MODEL = new DragonModel(BuiltinFactory.COMPAT);
    private static final String[] APPEARANCES = {"female", "male", "rare"};
    private static final ResourceLocation[] BODY = textures("body");
    private static final ResourceLocation[] GLOW = textures("glow");
    private static final Map<String, EntityPollutionDragon> PREVIEWS = new LinkedHashMap<String, EntityPollutionDragon>(32, 0.75F, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, EntityPollutionDragon> entry) { return size() > 32; }
    };
    private static World previewWorld;
    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);
    // LWJGL 2 glGetFloat requires room for 16 floats even for GL_CURRENT_COLOR.
    private static final FloatBuffer COLOR = BufferUtils.createFloatBuffer(16);
    private static final float[] VIEW = new float[16];

    private BloodyStarlightContainerVisual() {}

    private static ResourceLocation[] textures(String layer) {
        ResourceLocation[] result = new ResourceLocation[3];
        for (int i = 0; i < result.length; i++) result[i] = new ResourceLocation("pollution",
                "textures/entity/dragon/ender/" + APPEARANCES[i] + "/" + layer + ".png");
        return result;
    }

    public static void render(NBTTagCompound payload, double time, float partialTicks, boolean detailed) {
        render(payload, time, partialTicks, detailed, false);
    }

    public static void render(NBTTagCompound payload, double time, float partialTicks, boolean detailed, boolean hideBase) {
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        int cullMode = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE);
        int src = GL11.glGetInteger(GL11.GL_BLEND_SRC), dst = GL11.glGetInteger(GL11.GL_BLEND_DST);
        int srcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA), dstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        COLOR.clear();
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, COLOR);
        float oldR = COLOR.get(0), oldG = COLOR.get(1), oldB = COLOR.get(2), oldA = COLOR.get(3);
        int alphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float alphaRef = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        int boundTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(0.5, 0, 0.5);
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            if (!hideBase) pedestal();
            GlStateManager.translate(0, 0.56, 0);
            if (!payload.isEmpty()) renderDragon(payload, time, partialTicks);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            GlStateManager.enableBlend();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003F);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);
            MATRIX.clear();
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
            for (int i = 0; i < 16; i++) VIEW[i] = MATRIX.get(i);
            // Far shell before near shell. Depth testing still occludes the sphere behind solid blocks.
            GlStateManager.enableCull();
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
            sphere(detailed ? 32 : 16, 0.38, 0.13F);
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            sphere(detailed ? 32 : 16, 0.38, 0.16F);
            GlStateManager.disableCull();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            float pulse = payload.isEmpty() ? 0.45F : (float) (0.75 + Math.sin(time * Math.PI / 30) * 0.15);
            for (int i = 0; i < 2; i++) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate((float) (time * (i == 0 ? 0.35 : -0.24)), 0, 1, 0);
                GlStateManager.rotate(i == 0 ? 42 : -55, 1, 0, 0);
                ring(0.425, 0.018, pulse * 0.12F, detailed ? 64 : 32);
                ring(0.425, 0.0035, pulse, detailed ? 64 : 32);
                GlStateManager.popMatrix();
            }
            stars(time, detailed ? 18 : 7, pulse);
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            GlStateManager.bindTexture(boundTexture);
            GlStateManager.alphaFunc(alphaFunc, alphaRef);
            GlStateManager.tryBlendFuncSeparate(src, dst, srcAlpha, dstAlpha);
            GlStateManager.depthMask(depthMask);
            GlStateManager.cullFace(cullMode == GL11.GL_FRONT ? GlStateManager.CullFace.FRONT : GlStateManager.CullFace.BACK);
            if (cull) GlStateManager.enableCull(); else GlStateManager.disableCull();
            if (blend) GlStateManager.enableBlend(); else GlStateManager.disableBlend();
            if (texture) GlStateManager.enableTexture2D(); else GlStateManager.disableTexture2D();
            if (lighting) GlStateManager.enableLighting(); else GlStateManager.disableLighting();
            GlStateManager.color(oldR, oldG, oldB, oldA);
            GlStateManager.popMatrix();
        }
    }

    private static void renderDragon(NBTTagCompound payload, double time, float partialTicks) {
        World world = Minecraft.getMinecraft().world;
        if (previewWorld != world) { PREVIEWS.clear(); previewWorld = world; }
        if (world == null) return;
        NBTTagCompound display = payload.getCompoundTag("Display");
        String key = payload.getUniqueId("CaptureId").toString() + ':' + display.hashCode();
        EntityPollutionDragon dragon = PREVIEWS.get(key);
        if (dragon == null) {
            DragonLifeStage stage;
            try { stage = DragonLifeStage.valueOf(display.getString("Stage")); }
            catch (IllegalArgumentException invalid) { return; }
            if (stage == DragonLifeStage.EGG) return;
            dragon = new EntityPollutionDragon(world);
            dragon.setStage(stage);
            dragon.setAppearance(display.getInteger("Appearance"));
            dragon.setBaseScale(display.getFloat("BaseScale"));
            PREVIEWS.put(key, dragon);
        }
        // Visual clock only. This object is never spawned or ticked.
        dragon.ticksExisted = (int) time;
        MODEL.setLivingAnimations(dragon, 0, 0, partialTicks);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(0, Math.sin(time * 0.035) * 0.008, 0);
            GlStateManager.rotate((float) (time * 0.3 + 35), 0, 1, 0);
            // Entire wing/neck/tail animation envelope, not the much smaller entity hitbox.
            GlStateManager.scale(0.029, -0.029, 0.029);
            GlStateManager.translate(0, 0.5, -1.4);
            GlStateManager.enableTexture2D();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            Minecraft.getMinecraft().getTextureManager().bindTexture(BODY[dragon.getAppearance()]);
            GlStateManager.color(1, 1, 1, 1);
            MODEL.render(dragon, 0, 0, dragon.ticksExisted, 0, 0, 0.0625F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GlStateManager.depthMask(false);
            Minecraft.getMinecraft().getTextureManager().bindTexture(GLOW[dragon.getAppearance()]);
            MODEL.render(dragon, 0, 0, dragon.ticksExisted, 0, 0, 0.0625F);
            GlStateManager.depthMask(true);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static void pedestal() {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 8; i++) {
            double a = i * Math.PI / 4, c = (i + 1) * Math.PI / 4;
            vertex(b, Math.cos(a) * 0.23, 0.01, Math.sin(a) * 0.23, 0.12F, 0.04F, 0.09F, 1);
            vertex(b, Math.cos(c) * 0.23, 0.01, Math.sin(c) * 0.23, 0.12F, 0.04F, 0.09F, 1);
            vertex(b, Math.cos(c) * 0.19, 0.10, Math.sin(c) * 0.19, 0.30F, 0.15F, 0.23F, 1);
            vertex(b, Math.cos(a) * 0.19, 0.10, Math.sin(a) * 0.19, 0.30F, 0.15F, 0.23F, 1);
            vertex(b, 0, 0.10, 0, 0.20F, 0.08F, 0.15F, 1);
            vertex(b, Math.cos(a) * 0.19, 0.10, Math.sin(a) * 0.19, 0.28F, 0.12F, 0.20F, 1);
            vertex(b, Math.cos(c) * 0.19, 0.10, Math.sin(c) * 0.19, 0.28F, 0.12F, 0.20F, 1);
            vertex(b, 0, 0.10, 0, 0.20F, 0.08F, 0.15F, 1);
        }
        Tessellator.getInstance().draw();
    }

    private static void sphere(int segments, double radius, float alpha) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int row = 0; row < 12; row++) {
            double p = Math.PI * row / 12, q = Math.PI * (row + 1) / 12;
            for (int i = 0; i < segments; i++) {
                double a = Math.PI * 2 * i / segments, c = Math.PI * 2 * (i + 1) / segments;
                sphereVertex(b, radius, p, a, alpha);
                sphereVertex(b, radius, p, c, alpha);
                sphereVertex(b, radius, q, c, alpha);
                sphereVertex(b, radius, q, a, alpha);
            }
        }
        Tessellator.getInstance().draw();
    }

    private static void sphereVertex(BufferBuilder b, double r, double p, double a, float alpha) {
        double nx = Math.sin(p) * Math.cos(a), ny = Math.cos(p), nz = Math.sin(p) * Math.sin(a);
        double vx = VIEW[0] * nx + VIEW[4] * ny + VIEW[8] * nz;
        double vy = VIEW[1] * nx + VIEW[5] * ny + VIEW[9] * nz;
        double vz = VIEW[2] * nx + VIEW[6] * ny + VIEW[10] * nz;
        double facing = Math.abs(vz) / Math.max(0.0001, Math.sqrt(vx * vx + vy * vy + vz * vz));
        float rimAlpha = 0.025F + alpha * (float) Math.pow(1 - facing, 2);
        vertex(b, r * nx, r * ny, r * nz, 0.82F, 0.025F, 0.12F, rimAlpha);
    }

    private static void ring(double r, double width, float alpha, int segments) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < segments; i++) {
            double a = Math.PI * 2 * i / segments, c = Math.PI * 2 * (i + 1) / segments;
            vertex(b, Math.cos(a) * (r - width), 0, Math.sin(a) * (r - width), 1, 0.04F, 0.12F, alpha);
            vertex(b, Math.cos(c) * (r - width), 0, Math.sin(c) * (r - width), 1, 0.04F, 0.12F, alpha);
            vertex(b, Math.cos(c) * (r + width), 0, Math.sin(c) * (r + width), 1, 0.08F, 0.20F, alpha);
            vertex(b, Math.cos(a) * (r + width), 0, Math.sin(a) * (r + width), 1, 0.08F, 0.20F, alpha);
        }
        Tessellator.getInstance().draw();
    }

    private static void stars(double time, int count, float alpha) {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < count; i++) {
            double a = time * 0.008 + i * 2.39996;
            double y = Math.sin(i * 1.73 + time * 0.012) * 0.28;
            double r = Math.sqrt(0.32 * 0.32 - y * y);
            double x = Math.cos(a) * r, z = Math.sin(a) * r, size = 0.004;
            vertex(b, x - size, y, z, 1, 0.6F, 0.7F, alpha);
            vertex(b, x, y + size * 2, z, 1, 0.6F, 0.7F, alpha);
            vertex(b, x + size, y, z, 1, 0.6F, 0.7F, alpha);
            vertex(b, x, y - size * 2, z, 1, 0.6F, 0.7F, alpha);
        }
        Tessellator.getInstance().draw();
    }

    private static void vertex(BufferBuilder b, double x, double y, double z, float r, float g, float blue, float a) {
        b.pos(x, y, z).color(r, g, blue, a).endVertex();
    }
}
