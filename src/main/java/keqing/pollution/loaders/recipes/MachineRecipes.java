package keqing.pollution.loaders.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.init.Items;

import static gregtech.api.GTValues.VA;
import static gregtech.api.unification.material.Materials.Titanium;
import static gregtech.api.unification.material.Materials.TungstenSteel;
import static gregtech.loaders.recipe.CraftingComponent.*;
import static gregtech.loaders.recipe.MetaTileEntityLoader.registerMachineRecipe;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.common.items.PollutionMetaItems.*;
import static keqing.pollution.common.metatileentity.PollutionMetaTileEntities.*;

public class MachineRecipes {
    public static void init() {
        machine();
        muffler();
        filterRecipes();
    }

    private static void filterRecipes() {
        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(infused_earth.getFluid(1000))
                .output(FILTER_MKI)
                .duration(100)
                .EUt(VA[1])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(infused_water.getFluid(1000))
                .output(FILTER_MKII)
                .duration(100)
                .EUt(VA[2])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(syrmorite.getFluid(1000))
                .output(FILTER_MKIII)
                .duration(100)
                .EUt(VA[3])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(thaumium.getFluid(1000))
                .output(FILTER_MKIV)
                .duration(100)
                .EUt(VA[4])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(octine.getFluid(1000))
                .output(FILTER_MKV)
                .duration(100)
                .EUt(VA[5])
                .buildAndRegister();
    }

    private static void muffler() {
        registerMachineRecipe(PollutionMetaTileEntities.AURA_GENERATORS,
                "ABA", "CHC", "ABA",
                'H', HULL,
                'A', MOTOR,
                'B', PISTON,
                'C', ROTOR);

        registerMachineRecipe(PollutionMetaTileEntities.VIS_PROVIDERS,
                "ABA", "CHC", "ABA",
                'H', HULL,
                'A', MOTOR,
                'B', EMITTER,
                'C', ROTOR);

        registerMachineRecipe(PollutionMetaTileEntities.VIS_CLEAR,
                "ABA", "CHC", "ABA",
                'H', HULL,
                'A', MOTOR,
                'B', SENSOR,
                'C', ROTOR);


        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.lv");
        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.mv");
        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.hv");
        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.ev");
        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.iv");
        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.luv");
        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.zpm");
        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.uv");
        ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.uhv");
        registerMachineRecipe(PollutionMetaTileEntities.FLUX_MUFFLERS,
                "HM", "PR",
                'H', HULL,
                'M', MOTOR,
                'P', PIPE_NORMAL,
                'R', ROTOR);




        registerMachineRecipe(PollutionMetaTileEntities.VIS_HATCH,
                "ABA", "CHC", "ABA",
                'H', HULL,
                'A', CONVEYOR,
                'B', BLANKCORE,
                'C', EMITTER);

        registerMachineRecipe(PollutionMetaTileEntities.FLUX_PROMOTED_FUEL_CELL,
                "PBP", "EHE", "MCM",
                'H', HULL,
                'P', PISTON,
                'B', BLANKCORE,
                'C', CIRCUIT,
                'M', MOTOR,
                'E', EMITTER);
        registerMachineRecipe(PollutionMetaTileEntities.MAGIC_ENERGY_ABSORBER,
                "CSC", "HBH", "MEM",
                'H', HULL,
                'S', SENSOR,
                'B', BLANKCORE,
                'C', CIRCUIT,
                'M', MOTOR,
                'E', EMITTER
        );
        registerMachineRecipe(PollutionMetaTileEntities.MAGIC_ENERGY_ABSORBER,
                "CVC", "FLF", "EHE",
                'H', HULL,
                'L', LARGE_NODE_GENERATOR.getStackForm(),
                'E', MetaItems.EMITTER_LuV,
                'C', CIRCUIT,
                'V', VIS_HATCH,
                'F', FIELD_GENERATOR
        );
    }

    private static void machine() {
        ModHandler.addShapedRecipe(true, "flux_clear1", FLUX_CLEARS[1].getStackForm(),
                "CBC", "FMF", "CBC", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(),
                'B', new UnificationEntry(OrePrefix.rotor, Titanium),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.EV),
                'F', MetaItems.ELECTRIC_PUMP_EV);

        ModHandler.addShapedRecipe(true, "flux_clear2", FLUX_CLEARS[2].getStackForm(),
                "CBC", "FMF", "CBC", 'M', MetaTileEntities.HULL[GTValues.IV].getStackForm(),
                'B', new UnificationEntry(OrePrefix.rotor, TungstenSteel),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.IV),
                'F', MetaItems.ELECTRIC_PUMP_IV);
        ModHandler.addShapedRecipe(true, "SMALL_NODE_GENERATOR_LuV", SMALL_NODE_GENERATOR[0].getStackForm(),
                "CVC", "FLF", "EHE",
                'H', MetaTileEntities.HULL[6].getStackForm(),
                'L', LARGE_NODE_GENERATOR.getStackForm(),
                'E', MetaItems.EMITTER_LuV,
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.LuV),
                'V', VIS_HATCH[5].getStackForm(),
                'F', MetaItems.FIELD_GENERATOR_LuV
        );
        ModHandler.addShapedRecipe(true, "SMALL_NODE_GENERATOR_ZPM", SMALL_NODE_GENERATOR[1].getStackForm(),
                "CVC", "FLF", "EHE",
                'H', MetaTileEntities.HULL[7].getStackForm(),
                'L', LARGE_NODE_GENERATOR.getStackForm(),
                'E', MetaItems.EMITTER_ZPM,
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ZPM),
                'V', VIS_HATCH[6].getStackForm(),
                'F', MetaItems.FIELD_GENERATOR_ZPM
        );
        ModHandler.addShapedRecipe(true, "SMALL_NODE_GENERATOR_UV", SMALL_NODE_GENERATOR[2].getStackForm(),
                "CVC", "FLF", "EHE",
                'H', MetaTileEntities.HULL[8].getStackForm(),
                'L', LARGE_NODE_GENERATOR.getStackForm(),
                'E', MetaItems.EMITTER_UV,
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.UV),
                'V', VIS_HATCH[7].getStackForm(),
                'F', MetaItems.FIELD_GENERATOR_UV
        );
        ModHandler.addShapedRecipe(true, "SMALL_NODE_GENERATOR_UHV", SMALL_NODE_GENERATOR[3].getStackForm(),
                "CVC", "FLF", "EHE",
                'H', MetaTileEntities.HULL[9].getStackForm(),
                'L', LARGE_NODE_GENERATOR.getStackForm(),
                'E', MetaItems.EMITTER_UHV,
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.UHV),
                'V', VIS_HATCH[8].getStackForm(),
                'F', MetaItems.FIELD_GENERATOR_UHV
        );
    }
}
