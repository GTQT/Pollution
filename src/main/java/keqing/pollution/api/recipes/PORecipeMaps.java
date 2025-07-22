package keqing.pollution.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMapBuilder;
import gregtech.api.recipes.builders.*;
import gregtech.core.sound.GTSoundEvents;
import keqing.pollution.api.recipes.builder.ManaRecipesBuilder;
import keqing.pollution.client.POSoundEvent;

public class PORecipeMaps {
    public static final RecipeMap<FuelRecipeBuilder> DAN_DE_LIFE_ON = new RecipeMapBuilder<>("dan_de_life_on", new FuelRecipeBuilder())
            .itemInputs(2)
            .itemOutputs(2)
            .fluidInputs(2)
            .fluidOutputs(2)
            .allowEmptyOutputs()
            .sound(POSoundEvent.MANA_PLUSE)
            .build();

    public static final RecipeMap<FuelRecipeBuilder> MANA_TO_EU = new RecipeMapBuilder<>("mana_to_eu", new FuelRecipeBuilder())
            .itemInputs(2)
            .itemOutputs(2)
            .fluidInputs(2)
            .fluidOutputs(2)
            .allowEmptyOutputs()
            .sound(POSoundEvent.MANA_PLUSE)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MANA_GEN_RECIPES = new RecipeMapBuilder<>("mana_gen_recipes", new SimpleRecipeBuilder())
            .itemInputs(0)
            .itemOutputs(0)
            .fluidInputs(0)
            .fluidOutputs(0)
            .allowEmptyOutputs()
            .sound(POSoundEvent.MANA_PLUSE)
            .build();

    public static final RecipeMap<BlastRecipeBuilder> MAGIC_ALLOY_BLAST_RECIPES = new RecipeMapBuilder<>("magic_blast_smelter", new BlastRecipeBuilder())
            .itemInputs(9)
            .itemOutputs(0)
            .fluidInputs(3)
            .fluidOutputs(1)
            .itemSlotOverlay(GuiTextures.FURNACE_OVERLAY_1, false, false)
            .itemSlotOverlay(GuiTextures.FURNACE_OVERLAY_1, false, true)
            .itemSlotOverlay(GuiTextures.FURNACE_OVERLAY_2, true, false)
            .fluidSlotOverlay(GuiTextures.FURNACE_OVERLAY_2, true, true)
            .sound(GTSoundEvents.FURNACE)
            .build();

    public static final RecipeMap<PrimitiveRecipeBuilder> STOVE_RECIPES = new RecipeMapBuilder<>("stove", new PrimitiveRecipeBuilder())
            .itemInputs(2)
            .itemOutputs(1)
            .sound(GTSoundEvents.FURNACE)
            .build();

    public static final RecipeMap<PrimitiveRecipeBuilder> MAGIC_FUSION_REACTOR = new RecipeMapBuilder<>("magic_fusion_reactor", new PrimitiveRecipeBuilder())
            .fluidInputs(1)
            .fluidOutputs(2)
            .sound(GTSoundEvents.CHEMICAL_REACTOR)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MAGIC_CHEMICAL_REACTOR_RECIPES = new RecipeMapBuilder<>("magic_chemical_reactor", new SimpleRecipeBuilder())
            .itemInputs(3)
            .itemOutputs(4)
            .fluidInputs(5)
            .fluidOutputs(4)
            .sound(GTSoundEvents.CHEMICAL_REACTOR)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MAGIC_ASSEMBLER_RECIPES = new RecipeMapBuilder<>("magic_assembler", new SimpleRecipeBuilder())
            .itemInputs(9)
            .itemOutputs(1)
            .fluidInputs(3)
            .fluidOutputs(0)
            .sound(GTSoundEvents.ASSEMBLER)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MAGIC_GREENHOUSE_RECIPES = new RecipeMapBuilder<>("magic_greenhouse", new SimpleRecipeBuilder())
            .itemInputs(4)
            .itemOutputs(1)
            .fluidInputs(1)
            .fluidOutputs(1)
            .sound(GTSoundEvents.REPLICATOR)
            .build();

    public static final RecipeMap<FuelRecipeBuilder> MAGIC_TURBINE_FUELS = new RecipeMapBuilder<>("magic_turbine", new FuelRecipeBuilder())
            .fluidInputs(1)
            .fluidOutputs(1)
            .allowEmptyOutputs()
            .sound(GTSoundEvents.TURBINE)
            .build();


    public static final RecipeMap<BlastRecipeBuilder> FORGE_ALCHEMY_RECIPES = new RecipeMapBuilder<>("forge_alchemy", new BlastRecipeBuilder())
            .itemInputs(9)
            .itemOutputs(3)
            .fluidInputs(6)
            .fluidOutputs(3)
            .itemSlotOverlay(GuiTextures.FURNACE_OVERLAY_1, false, false)
            .itemSlotOverlay(GuiTextures.FURNACE_OVERLAY_1, false, true)
            .itemSlotOverlay(GuiTextures.FURNACE_OVERLAY_2, true, false)
            .fluidSlotOverlay(GuiTextures.FURNACE_OVERLAY_2, true, true)
            .sound(GTSoundEvents.FURNACE)
            .build();

    public static final RecipeMap<FusionRecipeBuilder> NODE_MAGIC_FUSION_RECIPES = new RecipeMapBuilder<>("node_magic_fusion",
            new FusionRecipeBuilder())
            .fluidInputs(3)
            .fluidOutputs(3)
            .progressBar(GuiTextures.PROGRESS_BAR_FUSION)
            .sound(GTSoundEvents.ARC)
            .build();

    //////////////////////////////////////////
    public static final RecipeMap<ManaRecipesBuilder> MANA_INFUSION_RECIPES = new RecipeMapBuilder<>("mana_infusion_recipes", new ManaRecipesBuilder())
            .itemInputs(2)
            .itemOutputs(1)
            .build();

    public static final RecipeMap<ManaRecipesBuilder> MANA_RUNE_ALTAR_RECIPES = new RecipeMapBuilder<>("mana_rune_altar_recipes", new ManaRecipesBuilder())
            .itemInputs(10)
            .itemOutputs(1)
            .build();

    public static final RecipeMap<ManaRecipesBuilder> MANA_PETAL_RECIPES = new RecipeMapBuilder<>("mana_petal_recipes", new ManaRecipesBuilder())
            .itemInputs(16)
            .itemOutputs(1)
            .build();

    public static final RecipeMap<ManaRecipesBuilder> PURE_DAISY_RECIPES = new RecipeMapBuilder<>("pure_daisy_recipes", new ManaRecipesBuilder())
            .itemInputs(1)
            .itemOutputs(1)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> INDUSTRIAL_INFUSION_RECIPES = new RecipeMapBuilder<>("industrial_infusion_recipes", new SimpleRecipeBuilder())
            .itemInputs(48)
            .itemOutputs(1)
            .fluidInputs(16)
            .fluidOutputs(0)
            .build();

}
