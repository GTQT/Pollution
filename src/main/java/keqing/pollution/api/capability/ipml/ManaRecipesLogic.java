package keqing.pollution.api.capability.ipml;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import keqing.pollution.api.recipes.properties.ManaProperty;
import net.minecraft.nbt.NBTTagCompound;

public class ManaRecipesLogic extends MultiblockRecipeLogic {

    int TotalMana;
    int CurrentMana;

    public ManaRecipesLogic(RecipeMapMultiblockController metaTileEntity) {
        super(metaTileEntity);
    }

    protected void setupRecipe(Recipe recipe) {
        super.setupRecipe(recipe);
        this.TotalMana = recipe.getProperty(ManaProperty.getInstance(), 0);
        this.CurrentMana = TotalMana / progressTime;
    }

    protected void completeRecipe() {
        super.completeRecipe();
        this.TotalMana = 0;
        this.CurrentMana = 0;
    }

    public IManaProvider getManaProvider() {
        IManaProvider controller = (IManaProvider) this.metaTileEntity;
        return controller.getManaProvider();
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        if (this.progressTime > 0) {
            compound.setInteger("TotalMana", this.TotalMana);
            compound.setInteger("CurrentMana", this.CurrentMana);
        }

        return compound;
    }

    public void deserializeNBT(NBTTagCompound compound) {
        super.deserializeNBT(compound);
        if (this.progressTime > 0) {
            this.TotalMana = compound.getInteger("TotalMana");
            this.CurrentMana = compound.getInteger("CurrentMana");
        }
    }

    protected void updateRecipeProgress() {
        if (this.canRecipeProgress && this.drawEnergy(this.recipeEUt, true) && drainMana(this.CurrentMana, true)) {
            this.drawEnergy(this.recipeEUt, false);
            this.drainMana(this.CurrentMana, false);
            if (++this.progressTime > this.maxProgressTime) {
                this.completeRecipe();
            }

            if (this.hasNotEnoughEnergy && this.getEnergyInputPerSecond() > 19L * (long) this.recipeEUt) {
                this.hasNotEnoughEnergy = false;
            }
        } else if (this.recipeEUt > 0) {
            this.hasNotEnoughEnergy = true;
            this.decreaseProgress();
        }

    }

    boolean drainMana(int amount, boolean simulate) {
        return getManaProvider().drainMana(amount, simulate);
    }
}
