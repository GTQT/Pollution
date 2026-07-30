package meowmel.pollution.loaders.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;

/** Crafting routes for the three optional authorization hatches. */
public final class MagicHatchRecipes {

    private MagicHatchRecipes() {
    }

    public static void init() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaItems.BATTERY_HULL_MV)
                .input(MetaItems.ELECTRIC_PUMP_MV)
                .input(MetaItems.SENSOR_MV)
                .input(PollutionMetaItems.BLOOD_PORT)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.MV)
                .output(PollutionMetaTileEntities.BLOOD_MAGIC_HATCH)
                .duration(300)
                .EUt(GTValues.VA[GTValues.MV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaItems.BATTERY_HULL_MV)
                .input(MetaItems.FIELD_GENERATOR_LV)
                .input(MetaItems.SENSOR_MV)
                .input(ItemsAS.skyResonator)
                .input(ItemsAS.constellationPaper)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.MV)
                .output(PollutionMetaTileEntities.ASTRAL_LENS_HATCH)
                .duration(300)
                .EUt(GTValues.VA[GTValues.MV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaItems.BATTERY_HULL_LV)
                .input(MetaItems.SENSOR_LV)
                .input(PollutionMetaItems.TAROT_THE_FOOL)
                .input(PollutionMetaItems.TAROT_THE_WHEEL_OF_FORTUNE)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.LV)
                .output(PollutionMetaTileEntities.TAROT_HATCH)
                .duration(200)
                .EUt(GTValues.VA[GTValues.LV])
                .buildAndRegister();
    }
}
