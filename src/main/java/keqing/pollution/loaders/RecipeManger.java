package keqing.pollution.loaders;

import keqing.pollution.loaders.recipes.*;

import static WayofTime.bloodmagic.meteor.MeteorRegistry.meteorMap;

public class RecipeManger {
	public static void init() {
		BotaniaRecipes.init();
		MachineRecipes.init();
		AERecipes.init();
		BloodAltar.init();
		//MeteorsHelper.init();
		ForgeAlchemyRecipes.init();
		CircuitManager.init();
		ThaumcraftRecipes.init();
		MagicGCYMRecipes.init();
		InfusedManager.init();
		CompoundAspectRecipes.init();
		MagicChemicalRecipes.init();
	}


}