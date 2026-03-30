package meowmel.pollution.loaders.recipes;

import gregtech.api.items.metaitem.MetaItem;

import static meowmel.pollution.common.items.PollutionMetaItems.*;

public class MufflerHatchRecipes {
    public static void init() {

        /*
        for(int i = 0; i < 9; ++i) {
            PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(FLUX_MUFFLERS[i])
                    .input(screw, Terrasteel, 1)
                    .input(rotor, ElvenElementium, 1)
                    .input(getFilterByTier(i), 4)
                    .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                    .output(MUFFLER_HATCH[i+1])
                    .duration(400)
                    .EUt(VA[i])
                    .buildAndRegister();
        }

         */
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
