package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;

import static gregtech.api.recipes.RecipeMaps.EXTRACTOR_RECIPES;
import static gregtech.api.unification.ore.OrePrefix.*;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class InfusedManager {
	public static void init() {
		//这个文件写的没用喵
		RemoveExtractor(infused_air);
		RemoveExtractor(infused_fire);
		RemoveExtractor(infused_water);
		RemoveExtractor(infused_earth);
		RemoveExtractor(infused_entropy);
		RemoveExtractor(infused_order);
	}

	private static void RemoveExtractor(Material material) {
		GTRecipeHandler.removeRecipesByInputs(EXTRACTOR_RECIPES, OreDictUnifier.get(block, material, 1));
		GTRecipeHandler.removeRecipesByInputs(EXTRACTOR_RECIPES, OreDictUnifier.get(gem, material, 1));
		GTRecipeHandler.removeRecipesByInputs(EXTRACTOR_RECIPES, OreDictUnifier.get(gemFlawless, material, 1));
		GTRecipeHandler.removeRecipesByInputs(EXTRACTOR_RECIPES, OreDictUnifier.get(dust, material, 1));
		GTRecipeHandler.removeRecipesByInputs(EXTRACTOR_RECIPES, OreDictUnifier.get(gemExquisite, material, 1));
	}
}
