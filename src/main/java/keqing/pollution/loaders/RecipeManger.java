package keqing.pollution.loaders;

import keqing.pollution.loaders.recipes.*;
import keqing.pollution.loaders.recipes.mods.Botania;
import keqing.pollution.loaders.recipes.mods.TheBetweendLand.BiologyLine;
import keqing.pollution.loaders.recipes.mods.TheBetweendLand.StoneLine;
import keqing.pollution.loaders.recipes.mods.TheBetweendLand.VanillaRecipes;

public class RecipeManger {
    public static void init() {
        BotaniaRecipes.init();
        MachineRecipes.init();
        AERecipes.init();
        BloodAltar.init();
        //MeteorsHelper.init();
        ForgeAlchemyRecipes.init();
        CircuitManager.init();
        ThaumcraftRecipes.init();
        MagicGCYMRecipes.init();
        InfusedManager.init();
        CompoundAspectRecipes.init();
        MagicChemicalRecipes.init();
        Botania.init();
        VanillaRecipes.init();
        StoneLine.init();
        BiologyLine.init();
        ManaToEuRecipes.init();
        DandelifeonRecipe.init();
        NodeFusionRecipes.init();
    }


}