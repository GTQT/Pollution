package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.tile.TileEntityMagicCircle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;

public class TesrMagicCircle extends TileEntitySpecialRenderer<TileEntityMagicCircle> {
	public static final ResourceLocation MAGIC_CIRCLE = new ResourceLocation("pollution", "models/magic_circle.png");
	public static final IModelCustom Magic_Model = AdvancedModelLoader.loadModel(new ResourceLocation("pollution", "models/magic_circle.obj"));

	@Override
	public void render(TileEntityMagicCircle te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		if (te.getRl() == null)
			FMLClientHandler.instance().getClient().getTextureManager().bindTexture(MAGIC_CIRCLE);
		else
			FMLClientHandler.instance().getClient().getTextureManager().bindTexture(te.getRl());
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(0.5, 0.5, 0.5);
		if (te.isCanRotation())
			GlStateManager.rotate(((float) System.currentTimeMillis() / ((float) 64 / te.getSpeed())) % 360, 0F, 1F, 0F);

		final float scale = te.getScala();
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.color(1, 1, 1, 1);
		Magic_Model.renderAll();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.enableCull();
	}

	@Override
	public boolean isGlobalRenderer(TileEntityMagicCircle te) {
		return super.isGlobalRenderer(te);
	}
}
