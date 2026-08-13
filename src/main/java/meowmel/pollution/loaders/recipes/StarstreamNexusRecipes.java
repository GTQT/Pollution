package meowmel.pollution.loaders.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import meowmel.pollution.Pollution;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POConstellationCrystal;
import meowmel.pollution.common.block.metablocks.POStarstreamObelisk;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

/** Endgame acquisition path for the Starstream Nexus and every new structural block. */
public final class StarstreamNexusRecipes {

    private StarstreamNexusRecipes() {}

    public static void init() {
        registerCasings();
        registerAnchorInfusion();
        registerCoreInfusion();
        registerController();
        registerLinker();
        registerRelay();
        registerInterdimensionalRelay();
        registerChunkAnchor();
        registerOperationCore();
    }

    private static void registerCasings() {
        ItemStack casing = stack(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_CASING);
        ItemStack runed = stack(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_RUNED_CASING);

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GTUtility.copy(8, BlockMarble.MarbleBlockType.BRICKS.asStack()))
                .input(PollutionMetaItems.STARRY_RUNE, 2)
                .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL, 4)
                .fluidInputs(PollutionMaterials.starrymansus.getFluid(2000))
                .outputs(GTUtility.copy(8, casing))
                .duration(600)
                .EUt(GTValues.VA[GTValues.UV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GTUtility.copy(4, casing))
                .input(PollutionMetaItems.STARRY_RUNE, 4)
                .input(PollutionMetaItems.HARMONIZING_RUNE_CORE, 2)
                .fluidInputs(PollutionMaterials.starrymansus.getFluid(4000))
                .outputs(GTUtility.copy(4, runed))
                .duration(900)
                .EUt(GTValues.VA[GTValues.UHV])
                .buildAndRegister();
    }

    private static void registerAnchorInfusion() {
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Pollution.MODID, "constellation_anchor"),
                new InfusionRecipe(
                        "INFUSION@2",
                        stack(POStarstreamObelisk.ObeliskBlockType.CONSTELLATION_ANCHOR),
                        16,
                        new AspectList()
                                .add(Aspect.MAGIC, 512)
                                .add(Aspect.AURA, 512)
                                .add(Aspect.ORDER, 256)
                                .add(Aspect.ENERGY, 256)
                                .add(Aspect.LIGHT, 256),
                        stack(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_RUNED_CASING),
                        PollutionMetaItems.STARRY_RUNE.getStackForm(),
                        ritualCrystal(),
                        PollutionMetaItems.ASTRAL_RESONANCE_COIL.getStackForm(),
                        PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV.getStackForm(),
                        PollutionMetaItems.CELESTIAL_CALIBRATION_CORE.getStackForm(),
                        ritualCrystal(),
                        MetaItems.FIELD_GENERATOR_UHV.getStackForm(),
                        PollutionMetaItems.STARRY_RUNE.getStackForm(),
                        ritualCrystal(),
                        PollutionMetaItems.ASTRAL_RESONANCE_COIL.getStackForm(),
                        PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV.getStackForm(),
                        PollutionMetaItems.CELESTIAL_CALIBRATION_CORE.getStackForm(),
                        ritualCrystal(),
                        MetaItems.FIELD_GENERATOR_UHV.getStackForm()));
    }

    private static void registerCoreInfusion() {
        ItemStack towerCore = PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                .getItemVariant(POConstellationCrystal.CrystalType.TOWER_CORE);
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Pollution.MODID, "starstream_obelisk_core"),
                new InfusionRecipe(
                        "INFUSION@2",
                        stack(POStarstreamObelisk.ObeliskBlockType.OBELISK_CORE),
                        25,
                        new AspectList()
                                .add(Aspect.MAGIC, 2048)
                                .add(Aspect.AURA, 2048)
                                .add(Aspect.ENERGY, 2048)
                                .add(Aspect.ORDER, 1024)
                                .add(Aspect.VOID, 1024)
                                .add(Aspect.LIGHT, 1024),
                        PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL.getStackForm(),
                        towerCore.copy(), ritualCrystal(),
                        PollutionMetaItems.HARMONIZING_RUNE_CORE.getStackForm(),
                        towerCore.copy(), ritualCrystal(),
                        MetaItems.FIELD_GENERATOR_UHV.getStackForm(),
                        towerCore.copy(), ritualCrystal(),
                        PollutionMetaItems.ASTRAL_RESONANCE_COIL.getStackForm(),
                        towerCore.copy(), ritualCrystal(),
                        PollutionMetaItems.HARMONIZING_RUNE_CORE.getStackForm(),
                        towerCore.copy(), ritualCrystal(),
                        MetaItems.FIELD_GENERATOR_UHV.getStackForm(),
                        towerCore.copy(), ritualCrystal(),
                        PollutionMetaItems.ASTRAL_RESONANCE_COIL.getStackForm(),
                        towerCore.copy(), ritualCrystal(),
                        PollutionMetaItems.CAUSALITY_CATALYST.getStackForm(),
                        towerCore.copy(), ritualCrystal(),
                        PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL.getStackForm()));
    }

    private static void registerController() {
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.HULL[GTValues.UHV])
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV, 16)
                .input(PollutionMetaItems.HARMONIZING_RUNE_CORE, 16)
                .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL, 64)
                .input(MetaItems.FIELD_GENERATOR_UHV, 16)
                .input(MetaItems.EMITTER_UHV, 16)
                .input(MetaItems.SENSOR_UHV, 16)
                .inputs(stack(POStarstreamObelisk.ObeliskBlockType.OBELISK_CORE))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 128000))
                .fluidInputs(PollutionMaterials.starrymansus.getFluid(32000))
                .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(8000))
                .output(PollutionMetaTileEntities.STARSTREAM_NEXUS_OBELISK)
                .duration(6000)
                .EUt(GTValues.VA[GTValues.UHV])
                .buildAndRegister();
    }

    private static void registerLinker() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.ASTRAL_LENS_BASIC)
                .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL, 2)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_IV, 2)
                .input(MetaItems.EMITTER_IV, 2)
                .input(MetaItems.SENSOR_IV, 2)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 2000))
                .output(PollutionMetaItems.STARSTREAM_LINKER)
                .duration(600)
                .EUt(GTValues.VA[GTValues.IV])
                .buildAndRegister();
    }

    private static void registerRelay() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(stack(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_RUNED_CASING))
                .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED)
                .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL, 8)
                .input(PollutionMetaItems.HARMONIZING_RUNE_CORE, 4)
                .input(MetaItems.EMITTER_UHV, 4)
                .input(MetaItems.SENSOR_UHV, 4)
                .fluidInputs(PollutionMaterials.starrymansus.getFluid(4000))
                .outputs(new ItemStack(PollutionMetaBlocks.STARSTREAM_RELAY))
                .duration(1800)
                .EUt(GTValues.VA[GTValues.UHV])
                .buildAndRegister();
    }

    private static void registerInterdimensionalRelay() {
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(new ItemStack(PollutionMetaBlocks.STARSTREAM_RELAY, 2))
                .input(PollutionMetaItems.HARMONIZING_RUNE_CORE, 8)
                .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL, 16)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV, 4)
                .input(MetaItems.FIELD_GENERATOR_UHV, 4)
                .input(MetaItems.EMITTER_UHV, 8)
                .input(MetaItems.SENSOR_UHV, 4)
                .fluidInputs(PollutionMaterials.starrymansus.getFluid(16000))
                .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(8000))
                .outputs(new ItemStack(PollutionMetaBlocks.STARSTREAM_INTERDIMENSIONAL_RELAY))
                .duration(3600)
                .EUt(GTValues.VA[GTValues.UHV])
                .buildAndRegister();
    }

    private static void registerChunkAnchor() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(GTUtility.copy(4, BlockMarble.MarbleBlockType.BRICKS.asStack()))
                .input(PollutionMetaItems.ASTRAL_LENS_BASIC)
                .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL, 4)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_IV, 2)
                .input(MetaItems.FIELD_GENERATOR_IV, 2)
                .input(MetaItems.EMITTER_IV, 2)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 8000))
                .outputs(new ItemStack(PollutionMetaBlocks.STARSTREAM_CHUNK_ANCHOR))
                .duration(900)
                .EUt(GTValues.VA[GTValues.IV])
                .buildAndRegister();
    }

    private static void registerOperationCore() {
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .inputs(stack(POStarstreamObelisk.ObeliskBlockType.CONSTELLATION_ANCHOR))
                .inputs(GTUtility.copy(4,
                        stack(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_RUNED_CASING)))
                .inputs(GTUtility.copy(8, ritualCrystal()))
                .input(PollutionMetaItems.HARMONIZING_RUNE_CORE, 4)
                .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL, 16)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV, 4)
                .input(MetaItems.FIELD_GENERATOR_UHV, 4)
                .input(MetaItems.EMITTER_UHV, 4)
                .input(MetaItems.SENSOR_UHV, 4)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 32000))
                .fluidInputs(PollutionMaterials.starrymansus.getFluid(8000))
                .outputs(new ItemStack(PollutionMetaBlocks.STARSTREAM_OPERATION_CORE))
                .duration(2400)
                .EUt(GTValues.VA[GTValues.UHV])
                .buildAndRegister();
    }

    private static ItemStack ritualCrystal() {
        return PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                .getItemVariant(POConstellationCrystal.CrystalType.RITUAL_CRYSTAL);
    }

    private static ItemStack stack(POStarstreamObelisk.ObeliskBlockType type) {
        return PollutionMetaBlocks.STARSTREAM_OBELISK.getItemVariant(type);
    }
}
