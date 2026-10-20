package meowmel.pollution.client.entity.dragon;

import meowmel.pollution.client.entity.dragon.model.BuiltinFactory;
import meowmel.pollution.client.entity.dragon.model.DragonModel;
import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderPollutionDragon extends RenderLiving<EntityPollutionDragon> {
    private static final ResourceLocation[] BODY_TEXTURES = {
            new ResourceLocation("pollution:textures/entity/dragon/ender/female/body.png"),
            new ResourceLocation("pollution:textures/entity/dragon/ender/male/body.png"),
            new ResourceLocation("pollution:textures/entity/dragon/ender/rare/body.png")
    };

    public RenderPollutionDragon(RenderManager manager) {
        super(manager, new DragonModel(BuiltinFactory.COMPAT), 0.65F);
        addLayer(new LayerPollutionDragonGlow(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPollutionDragon dragon) {
        return BODY_TEXTURES[Math.max(0, Math.min(BODY_TEXTURES.length - 1, dragon.getAppearance()))];
    }

    @Override
    public void doRender(EntityPollutionDragon dragon, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        if (dragon.isEgg()) {
            renderEgg(dragon, x, y, z, partialTicks);
            return;
        }
        super.doRender(dragon, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntityPollutionDragon dragon, float partialTickTime) {
        float scale = dragon.getRenderScale(partialTickTime);
        shadowSize = 0.65F * scale;
        GlStateManager.scale(scale, scale, scale);
    }

    private void renderEgg(EntityPollutionDragon dragon, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        float remaining = dragon.getWobbleTicks();
        if (remaining > 0) {
            float intensity = Math.min(1.0F, remaining / 10.0F);
            float angle = (float) Math.sin((dragon.ticksExisted + partialTicks) * 0.75F) * 12.0F * intensity;
            if (dragon.getWobbleAxis() == 0) {
                GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
            } else if (dragon.getWobbleAxis() == 1) {
                GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
            } else {
                GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
            }
        }
        GlStateManager.scale(0.72F, 0.72F, 0.72F);
        IBlockState egg = Blocks.DRAGON_EGG.getDefaultState();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        net.minecraft.util.math.BlockPos pos = dragon.getPosition();
        buffer.setTranslation(-pos.getX() - 0.5D, -pos.getY(), -pos.getZ() - 0.5D);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
                dragon.world, Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(egg), egg, pos, buffer, false);
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        if (dragon.getCrackLevel() > 0) {
            // The block model buffer is centered around the entity, while crack vertices use 0..1 block coordinates.
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, 0.0F, -0.5F);
            renderCracks(dragon.getCrackLevel());
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private void renderCracks(int level) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1.35F);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        float alpha = Math.min(0.9F, 0.45F + level * 0.15F);
        addCrack(buffer, 0.48D, 0.22D, 0.42D, 0.36D, 0.54D, 0.49D, alpha);
        if (level > 1) {
            addCrack(buffer, 0.53D, 0.48D, 0.47D, 0.44D, 0.69D, 0.43D, alpha);
            addCrack(buffer, 0.44D, 0.43D, 0.53D, 0.36D, 0.57D, 0.65D, alpha);
        }
        if (level > 2) {
            addCrack(buffer, 0.58D, 0.32D, 0.5D, 0.63D, 0.53D, 0.62D, alpha);
            addCrack(buffer, 0.41D, 0.6D, 0.48D, 0.49D, 0.51D, 0.52D, alpha);
        }
        Tessellator.getInstance().draw();
        GL11.glLineWidth(1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    private static void addCrack(BufferBuilder buffer, double x1, double y1, double z1,
                                 double x2, double y2, double z2, float alpha) {
        addLine(buffer, x1, y1, 0.04D, x2, y2, 0.04D, alpha);
        addLine(buffer, x1, y1, 0.96D, x2, y2, 0.96D, alpha);
        addLine(buffer, 0.04D, y1, z1, 0.04D, y2, z2, alpha);
        addLine(buffer, 0.96D, y1, z1, 0.96D, y2, z2, alpha);
        addLine(buffer, x1, 0.96D, z1, x2, 0.96D, z2, alpha);
    }

    private static void addLine(BufferBuilder buffer, double x1, double y1, double z1,
                                double x2, double y2, double z2, float alpha) {
        buffer.pos(x1, y1, z1).color(15, 10, 25, (int) (255.0F * alpha)).endVertex();
        buffer.pos(x2, y2, z2).color(15, 10, 25, (int) (255.0F * alpha)).endVertex();
    }
}
