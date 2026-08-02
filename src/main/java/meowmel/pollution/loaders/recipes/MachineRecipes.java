package meowmel.pollution.loaders.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import meowmel.gtqtcore.api.unification.material.GTQTMaterials;
import net.minecraft.init.Items;

import static gregtech.api.GTValues.VA;
import static gregtech.api.unification.material.Materials.Titanium;
import static gregtech.api.unification.material.Materials.TungstenSteel;
import static gregtech.loaders.recipe.CraftingComponent.*;
import static gregtech.loaders.recipe.MetaTileEntityLoader.registerMachineRecipe;
import static meowmel.pollution.api.unification.PollutionMaterials.*;
import static meowmel.pollution.common.items.PollutionMetaItems.*;
import static meowmel.pollution.common.metatileentity.PollutionMetaTileEntities.*;

public class MachineRecipes {
    public static void init() {
        machine();
        muffler();
        filterRecipes();
    }

    private static void filterRecipes() {
        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(InfusedEarth.getFluid(1000))
                .output(FILTER_MKI)
                .duration(100)
                .EUt(VA[1])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(InfusedWater.getFluid(1000))
                .output(FILTER_MKII)
                .duration(100)
                .EUt(VA[2])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(Syrmorite.getFluid(1000))
                .output(FILTER_MKIII)
                .duration(100)
                .EUt(VA[3])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(GTQTMaterials.Thaumium.getFluid(1000))
                .output(FILTER_MKIV)
                .duration(100)
                .EUt(VA[4])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(Items.PAPER)
                .fluidInputs(Octine.getFluid(1000))
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

        for (int i = 0; i < PollutionMetaTileEntities.VIS_CLEAR.length; i++) {
            int tier = i + GTValues.LV;
            ModHandler.addShapedRecipe(true, "vis_clear_" + GTValues.VN[tier].toLowerCase(),
                    PollutionMetaTileEntities.VIS_CLEAR[i].getStackForm(),
                    "ABA", "CHC", "ABA",
                    'H', HULL.getIngredient(tier),
                    'A', MOTOR.getIngredient(tier),
                    'B', SENSOR.getIngredient(tier),
                    'C', ROTOR.getIngredient(tier));
        }



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

        registerMachineRecipe(PollutionMetaTileEntities.INFUSED_FLUID_HATCH,
                "PBP", "CHC", "PBP",
                'H', HULL,
                'P', PUMP,
                'B', PIPE_NORMAL,
                'C', CIRCUIT);

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
        for (int i = 0; i < PollutionMetaTileEntities.MAGIC_ENERGY_ABSORBER.length; i++) {
            int tier = GTValues.LV + i;
            ModHandler.addShapedRecipe(true,
                    "magic_energy_absorber_node_" + GTValues.VN[tier].toLowerCase(),
                    PollutionMetaTileEntities.MAGIC_ENERGY_ABSORBER[i].getStackForm(),
                    "CVC", "FLF", "EHE",
                    'H', HULL.getIngredient(tier),
                    'L', LARGE_NODE_GENERATOR.getStackForm(),
                    'E', MetaItems.EMITTER_LuV,
                    'C', CIRCUIT.getIngredient(tier),
                    'V', VIS_HATCH[i].getStackForm(),
                    'F', FIELD_GENERATOR.getIngredient(tier));
        }

    }

    private static void machine() {
        ModHandler.addShapedRecipe(true, "flux_clear1", FLUX_CLEARS[0].getStackForm(),
                "CBC", "FMF", "CBC", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(),
                'B', new UnificationEntry(OrePrefix.rotor, Titanium),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.EV),
                'F', MetaItems.ELECTRIC_PUMP_EV);

        ModHandler.addShapedRecipe(true, "flux_clear2", FLUX_CLEARS[1].getStackForm(),
                "CBC", "FMF", "CBC", 'M', MetaTileEntities.HULL[GTValues.IV].getStackForm(),
                'B', new UnificationEntry(OrePrefix.rotor, TungstenSteel),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.IV),
                'F', MetaItems.ELECTRIC_PUMP_IV);

        ModHandler.addShapedRecipe(true, "industrial_starlight_infuser",
                INDUSTRIAL_STARLIGHT_INFUSER.getStackForm(),
                "RCR", "PIP", "FHF",
                'R', BlockMarble.MarbleBlockType.RUNED.asStack(),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.IV),
                'P', MetaItems.ELECTRIC_PUMP_IV,
                'I', BlocksAS.starlightInfuser,
                'F', MetaItems.FIELD_GENERATOR_IV,
                'H', MetaTileEntities.HULL[GTValues.IV].getStackForm());

        ModHandler.addShapedRecipe(true, "industrial_lightwell",
                INDUSTRIAL_LIGHTWELL.getStackForm(),
                "RCR", "PWP", "FHF",
                'R', BlockMarble.MarbleBlockType.RUNED.asStack(),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.IV),
                'P', MetaItems.ELECTRIC_PUMP_IV,
                'W', BlocksAS.blockWell,
                'F', MetaItems.FIELD_GENERATOR_IV,
                'H', MetaTileEntities.HULL[GTValues.IV].getStackForm());

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
