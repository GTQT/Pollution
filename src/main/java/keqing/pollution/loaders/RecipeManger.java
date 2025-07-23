package keqing.pollution.loaders;

import keqing.pollution.loaders.recipes.*;
import keqing.pollution.loaders.recipes.mods.Botania;

public class RecipeManger {
    public static void init() {
        BotaniaRecipes.init();
        MachineRecipes.init();
        AERecipes.init();
        BloodAltar.init();
        BloodCircuit.init();
        //MeteorsHelper.init();
        MaterialsLine.init();
        ForgeAlchemyRecipes.init();
        CircuitManager.init();
        ThaumcraftRecipes.init();
        CrystalLine.init();
        MagicFuelRecipes.init();
        MagicGCYMRecipes.init();
        InfusedManager.init();
        CompoundAspectRecipes.init();
        MagicChemicalRecipes.init();
        MufflerHatchRecipes.init();
        ManaToEuRecipes.init();
        DandelifeonRecipe.init();
        NodeFusionRecipes.init();
    }


}