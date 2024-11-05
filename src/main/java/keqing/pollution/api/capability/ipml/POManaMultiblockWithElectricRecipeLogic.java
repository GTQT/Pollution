package keqing.pollution.api.capability.ipml;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.pollution.api.metatileentity.POManaMultiblockWithElectric;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.recipes.PORecipeMaps;

public class POManaMultiblockWithElectricRecipeLogic extends MultiblockRecipeLogic {
	POManaMultiblockWithElectric tileEntity;

	public POManaMultiblockWithElectricRecipeLogic(POManaMultiblockWithElectric tileEntity) {
		super(tileEntity);
		this.tileEntity=tileEntity;
	}

	@Override
	public void setMaxProgress(int maxProgress) {
		this.maxProgressTime = maxProgress;
	}

	public void updateRecipeProgress(){
		if (this.canRecipeProgress && this.drawEnergy(this.recipeEUt, true)) {
			//此处修改了耗电
			this.drawEnergy((int)(this.recipeEUt * (1 - 0.05 * tileEntity.getTier())), false);
			if (++this.progressTime > this.maxProgressTime) {
				this.completeRecipe();
			}

			if (this.hasNotEnoughEnergy && this.getEnergyInputPerSecond() > 19L * (long)this.recipeEUt) {
				this.hasNotEnoughEnergy = false;
			}
		} else if (this.recipeEUt > 0) {
			this.hasNotEnoughEnergy = true;
			this.decreaseProgress();
		}
	}

}
