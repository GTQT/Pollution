package meowmel.pollution.client.entity;


import meowmel.pollution.common.entity.moster.EntityBlitz;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly (Side.CLIENT)
public class RenderEntityBlitz extends RenderLiving<EntityBlitz> {

	private static ResourceLocation texture= new ResourceLocation("pollution:textures/entity/" + "blitz.png");

	public RenderEntityBlitz(RenderManager renderManager) {

		super(renderManager, ModelElemental.INSTANCE, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBlitz entity) {

		return texture;
	}

	@Override
	public void doRender(EntityBlitz entity, double x, double y, double z, float entityYaw, float partialTicks) {

		doRenderBlitz(entity, x, y, z, entityYaw, partialTicks);
	}

	private void doRenderBlitz(EntityBlitz entity, double x, double y, double z, float entityYaw, float partialTicks) {

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

}
