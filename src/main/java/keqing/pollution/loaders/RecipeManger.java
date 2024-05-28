package keqing.pollution.loaders;

import keqing.pollution.loaders.recipes.*;

public class RecipeManger {
	public static void init() {
		MachineRecipes.init();
		AERecipes.init();
		ThaumcraftRecipes.init();
		MagicGCYMRecipes.init();
		InfusedManager.init();
		CompoundAspectRecipes.init();
		MagicChemicalRecipes.init();
	}


}