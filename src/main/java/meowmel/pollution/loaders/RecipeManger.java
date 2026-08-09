package meowmel.pollution.loaders;

import meowmel.pollution.loaders.recipes.*;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.loaders.recipes.*;

public class RecipeManger {
    public static void init() {
        // Must run before any integration recipe applies a magic-only property.
        MagicRecipeProperties.init();
        BotaniaRecipes.init();
        MachineRecipes.init();
        AERecipes.init();
        BloodAltar.init();
        BloodCircuit.init();
        MaterialsLine.init();
        MagicIntegrationRecipes.init();
        MagicGuideRecipes.init();
        ForgeAlchemyRecipes.init();
        CircuitManager.init();
        ThaumcraftRecipes.init();
        CrystalLine.init();
        MagicFuelRecipes.init();
        MagicGCYMRecipes.init();
        MagicHatchRecipes.init();
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
