package meowmel.pollution.client.entity.dragon;

import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerPollutionDragonGlow implements LayerRenderer<EntityPollutionDragon> {
    private static final ResourceLocation[] GLOW_TEXTURES = {
            new ResourceLocation("pollution:textures/entity/dragon/ender/female/glow.png"),
            new ResourceLocation("pollution:textures/entity/dragon/ender/male/glow.png"),
            new ResourceLocation("pollution:textures/entity/dragon/ender/rare/glow.png")
    };
    private final RenderPollutionDragon renderer;

    public LayerPollutionDragonGlow(RenderPollutionDragon renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityPollutionDragon dragon, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (dragon.isEgg() || dragon.isInvisible()) {
            return;
        }
        int brightness = dragon.getBrightnessForRender();
        int low = brightness % 65536;
        int high = brightness / 65536;
        renderer.bindTexture(GLOW_TEXTURES[Math.max(0, Math.min(GLOW_TEXTURES.length - 1, dragon.getAppearance()))]);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        renderer.getMainModel().render(dragon, limbSwing, limbSwingAmount, ageInTicks,
                netHeadYaw, headPitch, scale);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, low, high);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
