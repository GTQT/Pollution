package meowmel.pollution.api.capability.ipml;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.recipes.Recipe;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;

import static gregtech.api.recipes.logic.OverclockingLogic.PERFECT_DURATION_FACTOR;
import static gregtech.api.recipes.logic.OverclockingLogic.STD_DURATION_FACTOR;

public class MagicMultiblockRecipeLogic extends MultiblockRecipeLogic {

    MagicRecipeMapMultiblockController metaTileEntity;
    private boolean explicitVisPaid;

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
        int infusedFluidPerTick = getInfusedFluidPerTick();
        long manaPerTick = getManaPerTick();
        int lifeEssencePerTick = getLifeEssencePerTick();
        int visPerCraft = getVisPerCraft();
        boolean hasMagicRequirements = previousRecipe == null || metaTileEntity.checkMagicRequirements(previousRecipe);
        boolean canPayExplicitVis = !hasExplicitVisCost() || explicitVisPaid
                || metaTileEntity.consumeVis(visPerCraft, true);

        if (canRecipeProgress && hasMagicRequirements && drawEnergy(recipeEUt, true)
                && metaTileEntity.drainInfusedFluid(infusedFluidPerTick, true)
                && metaTileEntity.consumeMana(manaPerTick, true)
                && metaTileEntity.consumeLifeEssence(lifeEssencePerTick, true)
                && canPayExplicitVis) {
            drawEnergy(recipeEUt, false);
            metaTileEntity.drainInfusedFluid(infusedFluidPerTick, false);
            metaTileEntity.consumeMana(manaPerTick, false);
            metaTileEntity.consumeLifeEssence(lifeEssencePerTick, false);
            if (hasExplicitVisCost() && !explicitVisPaid) {
                metaTileEntity.consumeVis(visPerCraft, false);
                explicitVisPaid = true;
            }
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
        return metaTileEntity.consumeVis(getVisPerCraft(), true) ? PERFECT_DURATION_FACTOR : STD_DURATION_FACTOR;
    }

    @Override
    protected void completeRecipe() {
        int visPerCraft = getVisPerCraft();
        boolean shouldChargeLegacyVis = !hasExplicitVisCost();
        super.completeRecipe();
        if (shouldChargeLegacyVis) {
            metaTileEntity.consumeVis(visPerCraft, false);
        }
        explicitVisPaid = false;
    }

    @Override
    public boolean checkRecipe(Recipe recipe) {
        return super.checkRecipe(recipe) && metaTileEntity.checkMagicRequirements(recipe);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        explicitVisPaid = false;
    }

    /**
     * Legacy magic-machine recipes did not contain property data. Preserve their
     * single 1 mB/t element cost, while new properties scale with the number of
     * parallel operations actually being performed.
     */
    private int getInfusedFluidPerTick() {
        Recipe recipe = previousRecipe;
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.INFUSED_FLUID_PER_TICK)) return 1;
        return scaleInt(recipe.getProperty(MagicRecipeProperties.INFUSED_FLUID_PER_TICK, 0));
    }

    private long getManaPerTick() {
        Recipe recipe = previousRecipe;
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.MANA_PER_TICK)) return 0L;
        return scaleLong(recipe.getProperty(MagicRecipeProperties.MANA_PER_TICK, 0L));
    }

    private int getLifeEssencePerTick() {
        Recipe recipe = previousRecipe;
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.LIFE_ESSENCE_PER_TICK)) return 0;
        return scaleInt(recipe.getProperty(MagicRecipeProperties.LIFE_ESSENCE_PER_TICK, 0));
    }

    private int getVisPerCraft() {
        Recipe recipe = previousRecipe;
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.VIS_PER_CRAFT)) return 1;
        return scaleInt(recipe.getProperty(MagicRecipeProperties.VIS_PER_CRAFT, 0));
    }

    private boolean hasExplicitVisCost() {
        return previousRecipe != null && previousRecipe.hasProperty(MagicRecipeProperties.VIS_PER_CRAFT)
                && previousRecipe.getProperty(MagicRecipeProperties.VIS_PER_CRAFT, 0) > 0;
    }

    private int getParallelCostMultiplier() {
        return Math.max(1, parallelRecipesPerformed);
    }

    private int scaleInt(int amount) {
        if (amount <= 0) return 0;
        long result = (long) amount * getParallelCostMultiplier();
        return result > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result;
    }

    private long scaleLong(long amount) {
        if (amount <= 0) return 0L;
        int multiplier = getParallelCostMultiplier();
        return amount > Long.MAX_VALUE / multiplier ? Long.MAX_VALUE : amount * multiplier;
    }
}
