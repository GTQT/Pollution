package keqing.pollution.api.metatileentity;

import gregicality.multiblocks.api.capability.IParallelMultiblock;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import keqing.gtqtcore.common.block.blocks.BlockCoolingCoil;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.capability.IManaMultiblock;
import keqing.pollution.api.capability.ipml.POManaMultiblockWithElectricRecipeLogic;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class POManaMultiblockWithElectric extends RecipeMapMultiblockController implements IManaMultiblock {

	public POManaMultiblockWithElectric(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
		super(metaTileEntityId, recipeMap);
		this.recipeMapWorkable = new POManaMultiblockWithElectricRecipeLogic(this);
	}
	public IManaHatch ManaHatch;
	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		List<IEnergyContainer> energyContainer = new ArrayList(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
		energyContainer.addAll(this.getAbilities(MultiblockAbility.INPUT_LASER));
		this.energyContainer=new EnergyContainerList(energyContainer);
		this.ManaHatch=this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0);
	}
	public IManaHatch getIManaHatch()
	{
		return ManaHatch;
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

}
