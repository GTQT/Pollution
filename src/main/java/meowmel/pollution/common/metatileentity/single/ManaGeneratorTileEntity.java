package meowmel.pollution.common.metatileentity.single;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.recipes.PORecipeMaps;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ManaGeneratorTileEntity extends SimpleGeneratorMetaTileEntity implements IManaHatch {

    public ManaGeneratorTileEntity(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, PORecipeMaps.MANA_GEN_RECIPES, Textures.COMBUSTION_GENERATOR_OVERLAY, tier, (n) -> n + 1,1.0);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new ManaGeneratorTileEntity(metaTileEntityId, getTier());
    }

    @Override
    protected void reinitializeEnergyContainer() {
        super.reinitializeEnergyContainer();
        ((EnergyContainerHandler) energyContainer).setSideOutputCondition((facing) -> facing == frontFacing);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player,  List<String> tooltip, boolean advanced) {
        tooltip.add(1, I18n.format("花花草草的力量！"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", this.energyContainer.getEnergyCapacity()));
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        super.addToolUsages(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        tooltip.add(I18n.format("gregtech.tool_action.soft_mallet.reset"));
    }

    @Override
    public long getMaxMana() {
        return energyContainer.getEnergyCapacity();
    }

    @Override
    public long getMana() {
        return energyContainer.getEnergyStored();
    }

    @Override
    public boolean isFull() {
        return getMana() >= getMaxMana();
    }

    public void receiveMana(long mana) {
        if (!isFull()) {
            energyContainer.addEnergy(mana);
        }
    }

    @Override
    public boolean consumeMana(long amount, boolean simulate) {
        // 发电机拒绝消耗魔力
        return false;
    }
}