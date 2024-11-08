package keqing.pollution.api.capability.ipml;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.metatileentity.POManaMultiblockWithElectric;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.recipes.PORecipeMaps;

public class POManaMultiblockWithElectricRecipeLogic extends MultiblockRecipeLogic {

	POManaMultiblockWithElectric tileEntity;

	public POManaMultiblockWithElectricRecipeLogic(POManaMultiblockWithElectric tileEntity) {
		super(tileEntity);
		this.tileEntity=tileEntity;
	}
	public IManaHatch getManaHatch() {
		POManaMultiblockWithElectric controller = (POManaMultiblockWithElectric)this.metaTileEntity;
		return controller.getIManaHatch();
	}
	@Override
	public int getParallelLimit() {
		if (this.getManaHatch().getTier() < 4) return 64;
		else if (this.getManaHatch().getTier() < 8) return 64 + (this.getManaHatch().getTier() - 4) * 64;
		else return 512;
	}

	@Override
	public void updateRecipeProgress() {
		if ((getManaHatch().getMana() >= 20 * Math.pow(2, getManaHatch().getTier() - 1))) {
			if (this.canRecipeProgress && this.drawEnergy(this.recipeEUt / getManaHatch().getTier(), true)) {
				this.drawEnergy(this.recipeEUt / getManaHatch().getTier(), false);
				getManaHatch().consumeMana((int) (20 * Math.pow(2, getManaHatch().getTier() - 1)));

				if (++this.progressTime > this.maxProgressTime) {
					this.completeRecipe();
				}
			}
		}

	}

}
