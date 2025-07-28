package keqing.pollution.client.entity;

import keqing.pollution.common.entity.moster.EntityTcSlime;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import static keqing.pollution.Pollution.MODID;

public class RenderTcSlimeOrdo extends RenderTcSlime{

    private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation(MODID,"textures/entity/slime/slime_ordo.png");

    public RenderTcSlimeOrdo(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTcSlime entity) {
        return SLIME_TEXTURES;
    }
}
