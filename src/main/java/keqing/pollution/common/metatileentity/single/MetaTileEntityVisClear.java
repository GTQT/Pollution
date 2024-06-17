package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import keqing.pollution.POConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;

public class MetaTileEntityVisClear extends TieredMetaTileEntity {
	private final double VisTicks;
	int tier;
	int time;
	private final long energyAmountPer;
	private boolean isActive;

	@Override
	public MetaTileEntityVisClear createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return new MetaTileEntityVisClear(metaTileEntityId, getTier());
	}

	public MetaTileEntityVisClear(ResourceLocation metaTileEntityId, int tier) {
		super(metaTileEntityId, tier);
		this.tier = tier;
		this.VisTicks =  tier* POConfig.fluxScrubberMultiplier;
		this.energyAmountPer = GTValues.VA[tier];
		initializeInventory();
	}

	@Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
		ModularUI.Builder builder = ModularUI.defaultBuilder();
		builder.dynamicLabel(7, 30, () -> "Vis Clear", 0x232323);
		builder.dynamicLabel(7, 50, () -> "Tier: " + this.getTier(), 0x232323);
		builder.dynamicLabel(7, 70, () -> "Vis: " + AuraHelper.getFlux(getWorld(), getPos()), 0x232323);
		return builder.build(getHolder(), entityPlayer);
	}

	@Override
	public void update() {
		super.update();

		time++;
		if (time == 20)
				if (AuraHelper.drainFlux(getWorld(), getPos(), (float) VisTicks, true) > 0) {
					if (!getWorld().isRemote && energyContainer.getEnergyStored() >= energyAmountPer) {
						energyContainer.removeEnergy(energyAmountPer);
						isActive = true;
						{
							AuraHelper.drainFlux(getWorld(), getPos(), (float) VisTicks * 20, false);
							time = 1;
						}

					} else isActive = false;
				}
	}

	public boolean isActive() {
		return super.isActive() && this.isActive;
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		super.renderMetaTileEntity(renderState, translation, pipeline);
		Textures.POWER_SUBSTATION_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
	}

	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		tooltip.add(I18n.format("pollution.flux_clear.tire", tier, VisTicks));
		tooltip.add(I18n.format("pollution.flux_clear.tooltip"));
		tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", this.energyContainer.getInputVoltage(), GTValues.VNF[this.getTier()]));
		tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
	}

}
