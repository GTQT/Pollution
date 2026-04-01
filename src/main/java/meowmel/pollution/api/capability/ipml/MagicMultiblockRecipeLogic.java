package meowmel.pollution.api.capability.ipml;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;

import static gregtech.api.recipes.logic.OverclockingLogic.PERFECT_DURATION_FACTOR;
import static gregtech.api.recipes.logic.OverclockingLogic.STD_DURATION_FACTOR;

public class MagicMultiblockRecipeLogic extends MultiblockRecipeLogic {

    MagicRecipeMapMultiblockController metaTileEntity;

    public MagicMultiblockRecipeLogic(MagicRecipeMapMultiblockController tileEntity) {
        super(tileEntity);
        metaTileEntity = tileEntity;
    }

    /**
     * Update the current running recipe's progress
     * <p>
     * Also handles consuming running heat by default
     * </p>
     */
    protected void updateRecipeProgress() {
        if (canRecipeProgress && drawEnergy(recipeEUt, true) && metaTileEntity.drainInfusedFluid(1, true)) {
            drawEnergy(recipeEUt, false);
            metaTileEntity.drainInfusedFluid(1, false);
            // as recipe starts with progress on 1 this has to be > only not => to compensate for it
            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
            if (this.hasNotEnoughEnergy && getEnergyInputPerSecond() > 19L * recipeEUt) {
                this.hasNotEnoughEnergy = false;
            }
        } else if (recipeEUt > 0) {
            // only set hasNotEnoughEnergy if this recipe is consuming recipe
            // generators always have enough energy
            this.hasNotEnoughEnergy = true;
            decreaseProgress();
        }
    }


    @Override
    protected double getOverclockingDurationFactor() {
        return metaTileEntity.consumeVis(1, true) ? PERFECT_DURATION_FACTOR : STD_DURATION_FACTOR;
    }

    @Override
    protected void completeRecipe() {
        super.completeRecipe();
        metaTileEntity.consumeVis(1, false);
    }
}
