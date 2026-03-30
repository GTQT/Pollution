package meowmel.pollution.loaders;

import meowmel.pollution.loaders.recipes.*;
import meowmel.pollution.loaders.recipes.*;

public class RecipeManger {
    public static void init() {
        BotaniaRecipes.init();
        MachineRecipes.init();
        AERecipes.init();
        BloodAltar.init();
        BloodCircuit.init();
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
        TarChain.init();
        MufflerHatchRecipes.init();
        ManaToEuRecipes.init();
        DandelifeonRecipe.init();
        NodeFusionRecipes.init();
    }


}