package keqing.pollution.client.entity;

import keqing.pollution.common.entity.moster.EntityTcSlime;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderTcSlimeIgnis extends RenderTcSlime{

    private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("textures/entity/slime/slime_ignis.png");

    public RenderTcSlimeIgnis(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTcSlime entity) {
        return SLIME_TEXTURES;
    }
}
