package meowmel.pollution.client.entity;


import meowmel.pollution.common.entity.moster.EntityBlizz;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly (Side.CLIENT)
public class RenderEntityBlizz extends RenderLiving<EntityBlizz> {

	private static ResourceLocation texture= new ResourceLocation("pollution:textures/entity/" + "blizz.png");

	public RenderEntityBlizz(RenderManager renderManager) {

		super(renderManager, ModelElemental.INSTANCE, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBlizz entity) {

		return texture;
	}

	@Override
	public void doRender(EntityBlizz entity, double x, double y, double z, float entityYaw, float partialTicks) {

		doRenderBlizz(entity, x, y, z, entityYaw, partialTicks);
	}

	private void doRenderBlizz(EntityBlizz entity, double x, double y, double z, float entityYaw, float partialTicks) {

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

}
