package meowmel.pollution.loaders.recipes;

import static gregtech.api.GTValues.EV;
import static gregtech.api.GTValues.VA;
import static gregtech.api.recipes.RecipeMaps.MIXER_RECIPES;
import static gregtech.api.unification.ore.OrePrefix.dust;

import static meowmel.pollution.api.unification.PollutionMaterials.*;

public class MaterialsLine {
    public static void init()
    {
        MIXER_RECIPES.recipeBuilder()
                .input(dust, InfusedAir, 1)
                .input(dust, InfusedFire, 1)
                .input(dust, InfusedWater, 1)
                .input(dust, InfusedEarth, 1)
                .output(dust, EnergyCrystal, 4)
                .circuitMeta(4)
                .duration(800).EUt(VA[EV]).buildAndRegister();

        MIXER_RECIPES.recipeBuilder()
                .input(dust, EnergyCrystal, 4)
                .input(dust, InfusedEntropy, 1)
                .input(dust, InfusedOrder, 1)
                .output(dust, StarmetalAlloy, 6)
                .circuitMeta(6)
                .duration(800).EUt(VA[EV]).buildAndRegister();

    }
}
