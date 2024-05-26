package keqing.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.pollution.api.capability.IVisHatch;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public class MetaTileEntityVisHatch extends MetaTileEntityMultiblockPart
		implements IMultiblockAbilityPart<IVisHatch>, IVisHatch {

	int tier;

	public MetaTileEntityVisHatch(ResourceLocation metaTileEntityId, int tier) {
		super(metaTileEntityId, tier);
		this.tier = tier;
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityVisHatch(this.metaTileEntityId, this.getTier());
	}

	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format("等级 %s  容积上限 %s", tier, 1000 * tier));
	}

	@Override
	public int getTier() {
		return tier;
	}

	@Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
		ModularUI.Builder builder = ModularUI.defaultBuilder();
		builder.dynamicLabel(7, 30, () -> "Vis Hatch", 0x232323);
		builder.dynamicLabel(7, 50, () -> "Tier: " + this.getTier(), 0x232323);
		return builder.build(getHolder(), entityPlayer);
	}

	@Override
	public MultiblockAbility<IVisHatch> getAbility() {
		return POMultiblockAbility.VIS_HATCH;
	}

	@Override
	public void registerAbilities(List<IVisHatch> list) {
		list.add(this);
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		super.renderMetaTileEntity(renderState, translation, pipeline);
		if (shouldRenderOverlay()) {
			OrientedOverlayRenderer overlayRenderer;
			if (getTier() <= GTValues.HV)
				overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK1_OVERLAY;
			else if (getTier() <= GTValues.ZPM)
				overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK2_OVERLAY;
			else if (getTier() <= GTValues.UEV)
				overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK3_OVERLAY;
			else
				overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK4_OVERLAY;

			if (getController() != null && getController() instanceof RecipeMapMultiblockController) {
				overlayRenderer.renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
						getController().isActive(),
						getController().getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, null)
								.isWorkingEnabled());
			} else {
				overlayRenderer.renderOrientedState(renderState, translation, pipeline, getFrontFacing(), false, false);
			}
		}
	}

	@Override
	public boolean canPartShare() {
		return false;
	}

}
