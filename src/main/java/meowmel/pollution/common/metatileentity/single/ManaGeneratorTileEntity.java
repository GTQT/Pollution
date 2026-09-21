package meowmel.pollution.common.metatileentity.single;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.texture.Textures;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.recipes.PORecipeMaps;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

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
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("花花草草的力量", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out",
                energyContainer.getOutputVoltage(), GTValues.VNF[getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity",
                energyContainer.getEnergyCapacity()));
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