package keqing.pollution.loaders.recipes;

import static gregtech.api.GTValues.EV;
import static gregtech.api.GTValues.VA;
import static gregtech.api.recipes.RecipeMaps.MIXER_RECIPES;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static keqing.gtqtcore.api.unification.GTQTMaterials.Okin;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.api.unification.PollutionMaterials.infused_order;

public class MaterialsLine {
    public static void init()
    {
        MIXER_RECIPES.recipeBuilder()
                .input(dust, infused_air, 1)
                .input(dust, infused_fire, 1)
                .input(dust, infused_water, 1)
                .input(dust, infused_earth, 1)
                .output(dust, energy_crystal, 4)
                .circuitMeta(4)
                .duration(800).EUt(VA[EV]).buildAndRegister();

        MIXER_RECIPES.recipeBuilder()
                .input(dust, energy_crystal, 4)
                .input(dust, infused_entropy, 1)
                .input(dust, infused_order, 1)
                .output(dust, Okin, 6)
                .circuitMeta(6)
                .duration(800).EUt(VA[EV]).buildAndRegister();

    }
}
