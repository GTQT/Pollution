package meowmel.pollution.client.entity;

import meowmel.pollution.common.entity.moster.EntityTcSlime;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import static meowmel.pollution.Pollution.MODID;

public class RenderTcSlimeAer extends RenderTcSlime{

    private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation(MODID,"textures/entity/slime/slime_aer.png");

    public RenderTcSlimeAer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTcSlime entity) {
        return SLIME_TEXTURES;
    }
}
