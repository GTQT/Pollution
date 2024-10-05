package keqing.pollution.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.BlastRecipeBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.PrimitiveRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;
import keqing.pollution.api.recipes.builder.ManaRecipesBuilder;

public class PORecipeMaps {
	public static final RecipeMap<BlastRecipeBuilder> MAGIC_ALLOY_BLAST_RECIPES = new RecipeMap<>("magic_blast_smelter", 9, 0,
			3, 1, new BlastRecipeBuilder(), false)
			.setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
			.setSlotOverlay(false, false, true, GuiTextures.FURNACE_OVERLAY_1)
			.setSlotOverlay(false, true, false, GuiTextures.FURNACE_OVERLAY_2)
			.setSlotOverlay(false, true, true, GuiTextures.FURNACE_OVERLAY_2)
			.setSlotOverlay(true, true, false, GuiTextures.FURNACE_OVERLAY_2)
			.setSlotOverlay(true, true, true, GuiTextures.FURNACE_OVERLAY_2)
			.setSound(GTSoundEvents.FURNACE);

	public static final RecipeMap<PrimitiveRecipeBuilder> MAGIC_FUSION_REACTOR = new RecipeMap<>("magic_fusion_reactor", 0, 0,
			1, 2, new PrimitiveRecipeBuilder(), false)
			.setSound(GTSoundEvents.CHEMICAL_REACTOR);

	public static final RecipeMap<SimpleRecipeBuilder> MAGIC_CHEMICAL_REACTOR_RECIPES = new RecipeMap<>("magic_chemical_reactor", 3, 3, 5, 4, new SimpleRecipeBuilder(), false)
			.setSound(GTSoundEvents.CHEMICAL_REACTOR);

	public static final RecipeMap<SimpleRecipeBuilder> MAGIC_ASSEMBLER_RECIPES = new RecipeMap<>("magic_assembler", 9, 1, 1, 0, new SimpleRecipeBuilder(), false)
			.setSound(GTSoundEvents.ASSEMBLER);

	public static final RecipeMap<SimpleRecipeBuilder> MAGIC_GREENHOUSE_RECIPES = new RecipeMap<>("magic_greenhouse", 4, 4, 1, 1, new SimpleRecipeBuilder(), false)
			.setSound(GTSoundEvents.REPLICATOR);

	public static final RecipeMap<FuelRecipeBuilder> MAGIC_TURBINE_FUELS = new RecipeMap<>("magic_turbine", 0, 0, 1, 1, new FuelRecipeBuilder(), false)
			.allowEmptyOutput()
			.setSound(GTSoundEvents.TURBINE);

	public static final RecipeMap<BlastRecipeBuilder> FORGE_ALCHEMY_RECIPES = new RecipeMap<>("forge_alchemy", 9, 3,
			6, 3, new BlastRecipeBuilder(), false)
			.setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
			.setSlotOverlay(false, false, true, GuiTextures.FURNACE_OVERLAY_1)
			.setSlotOverlay(false, true, false, GuiTextures.FURNACE_OVERLAY_2)
			.setSlotOverlay(false, true, true, GuiTextures.FURNACE_OVERLAY_2)
			.setSlotOverlay(true, true, false, GuiTextures.FURNACE_OVERLAY_2)
			.setSlotOverlay(true, true, true, GuiTextures.FURNACE_OVERLAY_2)
			.setSound(GTSoundEvents.FURNACE);

	//////////////////////////////////////////
	public static final RecipeMap<ManaRecipesBuilder> MANA_INFUSION_RECIPES = new RecipeMap<>("mana_infusion_recipes", 2, 1, 0, 0, new ManaRecipesBuilder(), false)
			.setSound(GTSoundEvents.REPLICATOR);

}
