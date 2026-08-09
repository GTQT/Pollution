package meowmel.pollution.loaders.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.nbtmatch.NBTCondition;
import gregtech.api.recipes.ingredients.nbtmatch.NBTMatcher;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.item.ItemStack;

/** Crafting routes for the three optional authorization hatches. */
public final class MagicHatchRecipes {

    private MagicHatchRecipes() {
    }

    public static void init() {
        registerBloodMagicHatch(PollutionMetaItems.MAGIC_CIRCUIT_MV.getStackForm());
        registerBloodMagicHatch(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_MV.getStackForm());
        registerAstralLensHatch(PollutionMetaItems.MAGIC_CIRCUIT_MV.getStackForm());
        registerAstralLensHatch(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_MV.getStackForm());
        registerAdvancedAstralLensHatch(PollutionMetaItems.MAGIC_CIRCUIT_LuV.getStackForm());
        registerAdvancedAstralLensHatch(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LuV.getStackForm());
        registerTarotHatch(PollutionMetaItems.MAGIC_CIRCUIT_LV.getStackForm());
        registerTarotHatch(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LV.getStackForm());
    }

    private static void registerBloodMagicHatch(ItemStack circuit) {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaItems.BATTERY_HULL_MV)
                .input(MetaItems.ELECTRIC_PUMP_MV)
                .input(MetaItems.SENSOR_MV)
                .input(PollutionMetaItems.BLOOD_PORT)
                .inputs(circuit)
                .output(PollutionMetaTileEntities.BLOOD_MAGIC_HATCH)
                .duration(300)
                .EUt(GTValues.VA[GTValues.MV])
                .buildAndRegister();
    }

    private static void registerAstralLensHatch(ItemStack circuit) {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaItems.BATTERY_HULL_MV)
                .input(MetaItems.FIELD_GENERATOR_LV)
                .input(MetaItems.SENSOR_MV)
                .input(PollutionMetaItems.ASTRAL_LENS_BASIC)
                .input(ItemsAS.skyResonator)
                .input(ItemsAS.constellationPaper)
                .inputs(circuit)
                .output(PollutionMetaTileEntities.ASTRAL_LENS_HATCH)
                .duration(300)
                .EUt(GTValues.VA[GTValues.MV])
                .buildAndRegister();
    }

    private static void registerTarotHatch(ItemStack circuit) {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaItems.BATTERY_HULL_LV)
                .input(MetaItems.SENSOR_LV)
                .input(MetaItems.FIELD_GENERATOR_LV)
                .input(PollutionMetaItems.BLANK_TAROT_CARD)
                .input(PollutionMetaItems.ARCANE_INK_CAPSULE)
                .input(thaumcraft.api.items.ItemsTC.salisMundus)
                .inputs(circuit)
                .output(PollutionMetaTileEntities.TAROT_HATCH)
                .duration(240)
                .EUt(GTValues.VA[GTValues.MV])
                .buildAndRegister();
    }

    private static void registerAdvancedAstralLensHatch(ItemStack circuit) {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.HULL[GTValues.LuV])
                .input(MetaItems.FIELD_GENERATOR_LuV)
                .input(MetaItems.SENSOR_LuV)
                .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED)
                .inputNBT(PollutionMetaItems.CONSTELLATION_DATA_WAFER, NBTMatcher.ANY, NBTCondition.ANY)
                .input(PollutionMetaItems.CELESTIAL_CALIBRATION_CORE)
                .inputs(circuit)
                .output(PollutionMetaTileEntities.ASTRAL_LENS_HATCH_ADVANCED)
                .duration(600)
                .EUt(GTValues.VA[GTValues.LuV])
                .buildAndRegister();
    }
}
