package meowmel.pollution.api.capability.ipml;

import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;

import static gregtech.api.recipes.logic.OverclockingLogic.PERFECT_DURATION_FACTOR;
import static gregtech.api.recipes.logic.OverclockingLogic.STD_DURATION_FACTOR;

public class MagicMultiblockRecipeLogic extends ManaMultiblockRecipeLogic {

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
    @Override
    protected void updateRecipeProgress() {
        if (canRecipeProgress && drawMana(recipeEUt, true) && metaTileEntity.drainInfusedFluid(1, true)) {
            drawMana(recipeEUt, false);
            metaTileEntity.drainInfusedFluid(1, false);
            // as recipe starts with progress on 1 this has to be > only not => to compensate for it
            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
        } else if (recipeEUt > 0) {
            decreaseProgress();
        }
    }

    @Override
    protected double getOverclockingDurationFactor() {
        return metaTileEntity.consumeVis(1, true) ? PERFECT_DURATION_FACTOR : STD_DURATION_FACTOR;
    }

    @Override
    protected void completeRecipe() {
        metaTileEntity.consumeVis(1, false);
    }
}
