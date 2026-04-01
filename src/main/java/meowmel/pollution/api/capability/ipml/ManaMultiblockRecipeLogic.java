package meowmel.pollution.api.capability.ipml;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import meowmel.pollution.api.metatileentity.ManaMultiblockController;

public class ManaMultiblockRecipeLogic extends MultiblockRecipeLogic {

    ManaMultiblockController metaTileEntity;

    public ManaMultiblockRecipeLogic(ManaMultiblockController tileEntity) {
        super(tileEntity);
        metaTileEntity = tileEntity;
    }

    @Override
    protected long getEnergyInputPerSecond() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected long getEnergyStored() {
        return metaTileEntity.getManaStore();
    }

    @Override
    protected long getEnergyCapacity() {
        return metaTileEntity.getManaCapacity();
    }

    protected boolean drawMana(long recipeMana, boolean simulate) {
        return metaTileEntity.consumeMana(recipeMana, simulate);
    }

    @Override
    public long getMaxVoltage() {
        return GTValues.V[metaTileEntity.getMaxManaHatchTier()];
    }

    @Override
    public long getMaximumOverclockVoltage() {
        return metaTileEntity.getMaximumOverclockMana();
    }
}