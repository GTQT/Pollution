package meowmel.pollution.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMapBuilder;
import gregtech.api.recipes.builders.*;
import gregtech.core.sound.GTSoundEvents;
import meowmel.pollution.api.recipes.ui.IndustrialInfusionUI;
import meowmel.pollution.api.recipes.ui.MagicGuideUI;
import meowmel.pollution.api.recipes.ui.MagicPropertyRecipeUI;
import meowmel.pollution.client.POSoundEvent;
import net.minecraft.init.SoundEvents;

public class PORecipeMaps {
    public static final RecipeMap<FuelRecipeBuilder> DAN_DE_LIFE_ON = new RecipeMapBuilder<>("dan_de_life_on", new FuelRecipeBuilder())
            .itemInputs(2)
            .itemOutputs(2)
            .fluidInputs(2)
            .fluidOutputs(2)
            .allowEmptyOutputs()
            .sound(POSoundEvent.MANA_PLUSE)
            .build();

    // 八级魔力线，每一级魔力是一个电压
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
            .itemInputs(5)
            .itemOutputs(4)
            .fluidInputs(5)
            .fluidOutputs(4)
            .ui(MagicPropertyRecipeUI::new)
            .sound(GTSoundEvents.CHEMICAL_REACTOR)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MAGIC_ASSEMBLER_RECIPES = new RecipeMapBuilder<>("magic_assembler", new SimpleRecipeBuilder())
            .itemInputs(9)
            .itemOutputs(1)
            .fluidInputs(3)
            .fluidOutputs(0)
            .ui(MagicPropertyRecipeUI::new)
            .sound(GTSoundEvents.ASSEMBLER)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MAGIC_GREENHOUSE_RECIPES = new RecipeMapBuilder<>("magic_greenhouse", new SimpleRecipeBuilder())
            .itemInputs(4)
            .itemOutputs(4)
            .fluidInputs(1)
            .fluidOutputs(1)
            .ui(MagicPropertyRecipeUI::new)
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
    public static final RecipeMap<SimpleRecipeBuilder> MANA_INFUSION_RECIPES = new RecipeMapBuilder<>("mana_infusion_recipes", new SimpleRecipeBuilder())
            .itemInputs(2)
            .itemOutputs(1)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MANA_RUNE_ALTAR_RECIPES = new RecipeMapBuilder<>("mana_rune_altar_recipes", new SimpleRecipeBuilder())
            .itemInputs(10)
            .itemOutputs(1)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MANA_PETAL_RECIPES = new RecipeMapBuilder<>("mana_petal_recipes", new SimpleRecipeBuilder())
            .itemInputs(16)
            .itemOutputs(1)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> PURE_DAISY_RECIPES = new RecipeMapBuilder<>("pure_daisy_recipes", new SimpleRecipeBuilder())
            .itemInputs(1)
            .itemOutputs(1)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> INDUSTRIAL_INFUSION_RECIPES = new RecipeMapBuilder<>("industrial_infusion_recipes", new SimpleRecipeBuilder())
            .itemInputs(25)
            .itemOutputs(1)
            .fluidInputs(8)
            .fluidOutputs(0)
            .ui(IndustrialInfusionUI::new)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> MAGIC_METEORS_RECIPES = new RecipeMapBuilder<>("magic_meteors_recipes", new SimpleRecipeBuilder())
            .itemInputs(1)
            .itemOutputs(25)
            .fluidInputs(1)
            .sound(SoundEvents.ENTITY_GENERIC_EXPLODE)
            .build();

    public static final RecipeMap<SimpleRecipeBuilder> INDUSTRIAL_STARLIGHT_INFUSER_RECIPES =
            new RecipeMapBuilder<>("industrial_starlight_infuser_recipes", new SimpleRecipeBuilder())
                    .itemInputs(1)
                    .itemOutputs(1)
                    .fluidInputs(2)
                    .fluidOutputs(0)
                    .ui(MagicPropertyRecipeUI::new)
                    .progressBar(GuiTextures.PROGRESS_BAR_ARROW)
                    .sound(GTSoundEvents.ARC)
                    .build();

    public static final RecipeMap<SimpleRecipeBuilder> INDUSTRIAL_LIGHTWELL_RECIPES =
            new RecipeMapBuilder<>("industrial_lightwell_recipes", new SimpleRecipeBuilder())
                    .itemInputs(1)
                    .itemOutputs(0)
                    .fluidInputs(0)
                    .fluidOutputs(1)
                    .ui(MagicPropertyRecipeUI::new)
                    .progressBar(GuiTextures.PROGRESS_BAR_ARROW)
                    .sound(GTSoundEvents.ARC)
                    .build();

    public static final RecipeMap<SimpleRecipeBuilder> CELESTIAL_OBSERVATION_RECIPES =
            new RecipeMapBuilder<>("celestial_observation", new SimpleRecipeBuilder())
                    .itemInputs(3)
                    .itemOutputs(1)
                    .fluidInputs(2)
                    .fluidOutputs(0)
                    .ui(MagicPropertyRecipeUI::new)
                    .progressBar(GuiTextures.PROGRESS_BAR_ARROW)
                    .sound(GTSoundEvents.ARC)
                    .build();

    public static final RecipeMap<SimpleRecipeBuilder> CELESTIAL_CALIBRATION_RECIPES =
            new RecipeMapBuilder<>("celestial_calibration", new SimpleRecipeBuilder())
                    .itemInputs(6)
                    .itemOutputs(1)
                    .fluidInputs(2)
                    .fluidOutputs(0)
                    .ui(MagicPropertyRecipeUI::new)
                    .progressBar(GuiTextures.PROGRESS_BAR_ARROW)
                    .sound(GTSoundEvents.ARC)
                    .build();

    /** One-way NBT-preserving cultivation chain; only the magic autoclave exposes this map. */
    public static final RecipeMap<SimpleRecipeBuilder> CRYSTAL_CULTIVATION_RECIPES =
            new RecipeMapBuilder<>("crystal_cultivation", new SimpleRecipeBuilder())
                    .itemInputs(6)
                    .itemOutputs(1)
                    .fluidInputs(3)
                    .fluidOutputs(0)
                    .ui(MagicPropertyRecipeUI::new)
                    .progressBar(GuiTextures.PROGRESS_BAR_ARROW)
                    .sound(GTSoundEvents.CHEMICAL_REACTOR)
                    .build();

    /** The open-sky celestial growth array is the only controller for this map. */
    public static final RecipeMap<SimpleRecipeBuilder> CELESTIAL_CRYSTAL_GROWTH_RECIPES =
            new RecipeMapBuilder<>("celestial_crystal_growth", new SimpleRecipeBuilder())
                    .itemInputs(4)
                    .itemOutputs(1)
                    .fluidInputs(3)
                    .fluidOutputs(0)
                    .ui(MagicPropertyRecipeUI::new)
                    .progressBar(GuiTextures.PROGRESS_BAR_ARROW)
                    .sound(GTSoundEvents.ARC)
                    .build();

    /** Static JEI documentation; these maps deliberately have no executable controller. */
    public static final RecipeMap<SimpleRecipeBuilder> CONSTELLATION_WAFER_GUIDE_RECIPES =
            new RecipeMapBuilder<>("constellation_wafer_guide", new SimpleRecipeBuilder())
                    .itemInputs(1)
                    .itemOutputs(0)
                    .allowEmptyOutputs()
                    .ui(MagicGuideUI::new)
                    .build();

    /** Static JEI documentation; these maps deliberately have no executable controller. */
    public static final RecipeMap<SimpleRecipeBuilder> TAROT_GUIDE_RECIPES =
            new RecipeMapBuilder<>("tarot_guide", new SimpleRecipeBuilder())
                    .itemInputs(1)
                    .itemOutputs(0)
                    .allowEmptyOutputs()
                    .ui(MagicGuideUI::new)
                    .build();

    /** Static JEI documentation for the non-repeatable crystal seed and growth chain. */
    public static final RecipeMap<SimpleRecipeBuilder> CRYSTAL_CULTIVATION_GUIDE_RECIPES =
            new RecipeMapBuilder<>("crystal_cultivation_guide", new SimpleRecipeBuilder())
                    .itemInputs(1)
                    .itemOutputs(0)
                    .allowEmptyOutputs()
                    .ui(MagicGuideUI::new)
                    .build();

}
