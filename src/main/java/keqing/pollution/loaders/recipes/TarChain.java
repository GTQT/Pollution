package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.recipes.RecipeMaps.CRACKING_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.Materials.Redstone;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static keqing.gtqtcore.api.unification.GTQTMaterials.*;
import static keqing.gtqtcore.api.unification.GTQTMaterials.SeverelySteamCrackedCoalOil;

public class TarChain {
    static void lightlyCrack(Material raw, Material hydroCracked, Material steamCracked) {
        CRACKING_RECIPES.recipeBuilder()
                .circuitMeta(1)
                .fluidInputs(raw.getFluid(1000))
                .fluidInputs(Hydrogen.getFluid(2000))
                .fluidOutputs(hydroCracked.getFluid(1000))
                .duration(80).EUt(120).buildAndRegister();


        CRACKING_RECIPES.recipeBuilder()
                .circuitMeta(1)
                .fluidInputs(raw.getFluid(1000))
                .fluidInputs(Steam.getFluid(1000))
                .fluidOutputs(steamCracked.getFluid(1000))
                .duration(80).EUt(240).buildAndRegister();

    }
    static void severelyCrack(Material raw, Material hydroCracked, Material steamCracked) {
        CRACKING_RECIPES.recipeBuilder()
                .circuitMeta(2)
                .fluidInputs(raw.getFluid(1000))
                .fluidInputs(Hydrogen.getFluid(6000))
                .fluidOutputs(hydroCracked.getFluid(1000))
                .duration(160).EUt(240).buildAndRegister();


        CRACKING_RECIPES.recipeBuilder()
                .circuitMeta(3)
                .fluidInputs(raw.getFluid(1000))
                .fluidInputs(Steam.getFluid(1000))
                .fluidOutputs(steamCracked.getFluid(1000))
                .duration(160).EUt(480).buildAndRegister();

    }
    public static void init() {
        //煤焦油 发酵 纯化焦油
        RecipeMaps.FERMENTING_RECIPES.recipeBuilder()
                .fluidInputs(CoalTar.getFluid(1000))
                .input(dust,Redstone)
                .fluidOutputs(PollutionMaterials.pure_tar.getFluid(1000))
                .duration(200)
                .EUt(120)
                .buildAndRegister();

        lightlyCrack(PollutionMaterials.pure_tar, LightlyHydroCrackedCoalOil, LightlySteamCrackedCoalOil);
        severelyCrack(PollutionMaterials.pure_tar, SeverelyHydroCrackedCoalOil, SeverelySteamCrackedCoalOil);


    }
}
