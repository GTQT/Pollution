package keqing.pollution.loaders.recipes;

import gregtech.api.items.metaitem.MetaItem;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.api.recipes.PORecipeMaps;

import static gregtech.api.GTValues.VA;
import static gregtech.api.unification.ore.OrePrefix.rotor;
import static gregtech.api.unification.ore.OrePrefix.screw;
import static gregtech.common.metatileentities.MetaTileEntities.MUFFLER_HATCH;
import static keqing.pollution.api.unification.PollutionMaterials.ElvenElementium;
import static keqing.pollution.api.unification.PollutionMaterials.Terrasteel;
import static keqing.pollution.common.items.PollutionMetaItems.*;
import static keqing.pollution.common.metatileentity.PollutionMetaTileEntities.FLUX_MUFFLERS;

public class MufflerHatchRecipes {
    public static void init() {

        for(int i = 0; i < 9; ++i) {
            PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(FLUX_MUFFLERS[i])
                    .input(screw, Terrasteel, 1)
                    .input(rotor, ElvenElementium, 1)
                    .input(getFilterByTier(i), 4)
                    .fluidInputs(GTQTMaterials.Magic.getFluid(1000))
                    .output(MUFFLER_HATCH[i+1])
                    .duration(400)
                    .EUt(VA[i])
                    .buildAndRegister();
        }
    }

    public static MetaItem<?>.MetaValueItem getFilterByTier(int tier) {
        return switch (tier) {
            case 3, 4 -> FILTER_MKII;
            case 5, 6 -> FILTER_MKIII;
            case 7, 8 -> FILTER_MKIV;
            case 9 -> FILTER_MKV;
            default -> FILTER_MKI;
        };
    }
}
