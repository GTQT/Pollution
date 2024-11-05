package keqing.pollution.api.metatileentity;

import gregicality.multiblocks.api.capability.IParallelMultiblock;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.RecipeMap;
import keqing.pollution.api.capability.IManaMultiblock;
import keqing.pollution.api.capability.ipml.POManaMultiblockWithElectricRecipeLogic;
import net.minecraft.util.ResourceLocation;

public abstract class POManaMultiblockWithElectric extends RecipeMapMultiblockController implements IManaMultiblock, IParallelMultiblock {

	public POManaMultiblockWithElectric(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
		super(metaTileEntityId, recipeMap);
		this.recipeMapWorkable = new POManaMultiblockWithElectricRecipeLogic(this);
	}

	@Override
	public boolean isMana() {
		return true;
	}
	public int getMana() {
		return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMana();
	}
	public int getMaxMana() {
		return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMaxMana();
	}
	@Override
	public int getTier() {
		return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTier();
	}
	@Override
	public boolean work() {
		return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).consumeMana((int) Math.pow(2,getTier()));
	}
	@Override
	public boolean isParallel() {
		return true;
	}

}
