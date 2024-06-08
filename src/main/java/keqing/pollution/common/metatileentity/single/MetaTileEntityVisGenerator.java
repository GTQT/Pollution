package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import keqing.pollution.POConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;

import java.util.List;

import static gregtech.api.GTValues.VA;

public class MetaTileEntityVisGenerator extends TieredMetaTileEntity {
	static final float euPerVis = 250.0f;
	long euRest;
	long euDesired;
	float desiredVis;
	float drainedVis;
	long euGeneration;

	public MetaTileEntityVisGenerator(ResourceLocation metaTileEntityId, int tier) {
		super(metaTileEntityId, tier);
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
		return new MetaTileEntityVisGenerator(metaTileEntityId, getTier());
	}

	@Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
		return null;
	}

	@Override
	public void update() {
		super.update();
		if (!getWorld().isRemote) {
			euRest=energyContainer.getEnergyCapacity()-energyContainer.getEnergyStored();
			euDesired=Math.min(euRest,energyContainer.getOutputVoltage()*energyContainer.getOutputAmperage());
			desiredVis=(float) euDesired/ POConfig.visGeneratorEuPerVis;
			drainedVis= AuraHandler.drainVis(getWorld(),getPos(),desiredVis,false);
			euGeneration=(long)Math.floor(drainedVis*POConfig.visGeneratorEuPerVis);
			energyContainer.changeEnergy(euGeneration);
			if(euGeneration >0)
			{
				AuraHelper.polluteAura(getWorld(),getPos(),
						MathHelper.clamp(drainedVis*POConfig.visGeneratorPollutionMultiplier,
								POConfig.visGeneratorMinPollution,POConfig.visGeneratorMaxPollution),
						POConfig.visGeneratorPollutionShowEffects);
			}
		}
	}

	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
		String key = this.metaTileEntityId.getPath().split("\\.")[0];
		String mainKey = String.format("gregtech.machine.%s.tooltip", key);
		if (I18n.hasKey(mainKey)) {
			tooltip.add(1, I18n.format(mainKey));
		}
		tooltip.add(I18n.format("gregtech.universal.tooltip.consume",
				VA[getTier()]/POConfig.visGeneratorEuPerVis,POConfig.visGeneratorEuPerVis));
		tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()]));
		tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", this.energyContainer.getEnergyCapacity()));
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		super.renderMetaTileEntity(renderState, translation, pipeline);
		Textures.HPCA_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
	}

	@Override
	protected boolean isEnergyEmitter() {
		return true;
	}

}