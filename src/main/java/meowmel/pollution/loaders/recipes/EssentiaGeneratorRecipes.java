package meowmel.pollution.loaders.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMaps;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.items.ItemEssentiaUpgrade;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.items.PollutionItemsInit;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import meowmel.pollution.common.metatileentity.multiblockpart.MetaTileEntityEssentiaHatch;
import net.minecraft.item.ItemStack;

/**
 * Crafting routes for the Large Essentia Generator family.
 *
 * <p>The controller and its diffusion cells sit at EV/IV so the machine stays behind the pack's
 * Thaumcraft progression, while the input hatch spans LV..MAX like the other magic hatches.</p>
 */
public final class EssentiaGeneratorRecipes {

    private EssentiaGeneratorRecipes() {}

    public static void init() {
        registerController();
        registerDiffusionCells();
        registerInputHatches();
        registerUpgrades();
    }

    private static void registerController() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.HULL[GTValues.IV])
                .input(MetaItems.EMITTER_IV, 4)
                .input(MetaItems.SENSOR_IV, 4)
                .input(MetaItems.FIELD_GENERATOR_IV, 2)
                .inputs(PollutionMetaItems.MAGIC_CIRCUIT_IV.getStackForm(4))
                .inputs(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_IV.getStackForm())
                .fluidInputs(meowmel.pollution.api.unification.PollutionMaterials.InfusedMagic.getFluid(4000))
                .outputs(PollutionMetaTileEntities.LARGE_ESSENTIA_GENERATOR.getStackForm())
                .duration(1200)
                .EUt(GTValues.VA[GTValues.IV])
                .buildAndRegister();
    }

    private static void registerDiffusionCells() {
        // Tier 1: the entry cell, cheap enough to be the first thing a player builds.
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaItems.ELECTRIC_PUMP_EV, 2)
                .input(MetaItems.FIELD_GENERATOR_EV, 1)
                .input(thaumcraft.api.blocks.BlocksTC.stoneArcaneBrick, 4)
                .inputs(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_EV.getStackForm())
                .fluidInputs(meowmel.pollution.api.unification.PollutionMaterials.InfusedMagic.getFluid(1152))
                .outputs(PollutionMetaBlocks.ESSENTIA_CELL.getItemVariant(
                        meowmel.pollution.common.block.metablocks.POEssentiaCell.CellType.ESSENTIA_CELL_T1, 4))
                .duration(400)
                .EUt(GTValues.VA[GTValues.EV])
                .buildAndRegister();

        // Tiers 2..4 reuse the previous tier as a component, so the shell upgrade path is explicit.
        for (int tier = 2; tier <= 4; tier++) {
            meowmel.pollution.common.block.metablocks.POEssentiaCell.CellType previous =
                    meowmel.pollution.common.block.metablocks.POEssentiaCell.CellType.values()[tier - 2];
            meowmel.pollution.common.block.metablocks.POEssentiaCell.CellType current =
                    meowmel.pollution.common.block.metablocks.POEssentiaCell.CellType.values()[tier - 1];
            int gtTier = GTValues.EV + (tier - 2);
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .inputs(PollutionMetaBlocks.ESSENTIA_CELL.getItemVariant(previous, 4))
                    .input(MetaItems.FIELD_GENERATOR_EV, tier)
                    .input(MetaItems.SENSOR_EV, tier)
                    .inputs(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_EV.getStackForm(tier))
                    .fluidInputs(meowmel.pollution.api.unification.PollutionMaterials.InfusedMagic
                            .getFluid(1152 * tier))
                    .outputs(PollutionMetaBlocks.ESSENTIA_CELL.getItemVariant(current, 4))
                    .duration(400 * tier)
                    .EUt(GTValues.VA[Math.min(gtTier, GTValues.IV)])
                    .buildAndRegister();
        }
    }

    private static void registerInputHatches() {
        for (int i = 0; i < PollutionMetaTileEntities.ESSENTIA_HATCH.length; i++) {
            int tier = GTValues.LV + i;
            if (tier > GTValues.MAX) break;
            MetaTileEntityEssentiaHatch hatch = PollutionMetaTileEntities.ESSENTIA_HATCH[i];
            if (hatch == null) continue;
            int hullTier = Math.max(GTValues.LV, tier - 1);
            ItemStack circuit = magicCircuitFor(tier);
            if (circuit.isEmpty()) continue;
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(MetaTileEntities.HULL[hullTier])
                    .input(MetaItems.ELECTRIC_PUMP_EV, 2)
                    .input(MetaItems.SENSOR_EV, 2)
                    .input(thaumcraft.api.blocks.BlocksTC.stoneArcaneBrick, 4)
                    .inputs(circuit)
                    .outputs(hatch.getStackForm())
                    .duration(300)
                    .EUt(GTValues.VA[Math.min(tier, GTValues.IV)])
                    .buildAndRegister();
        }
    }

    /** Reuses the pack's magic circuit ladder; the cell hatch itself is not voltage-locked. */
    private static ItemStack magicCircuitFor(int tier) {
        return switch (tier) {
            case GTValues.LV -> PollutionMetaItems.MAGIC_CIRCUIT_LV.getStackForm();
            case GTValues.MV -> PollutionMetaItems.MAGIC_CIRCUIT_MV.getStackForm();
            case GTValues.HV -> PollutionMetaItems.MAGIC_CIRCUIT_HV.getStackForm();
            case GTValues.EV -> PollutionMetaItems.MAGIC_CIRCUIT_EV.getStackForm();
            case GTValues.IV -> PollutionMetaItems.MAGIC_CIRCUIT_IV.getStackForm();
            case GTValues.LuV -> PollutionMetaItems.MAGIC_CIRCUIT_LuV.getStackForm();
            case GTValues.ZPM -> PollutionMetaItems.MAGIC_CIRCUIT_ZPM.getStackForm();
            case GTValues.UV -> PollutionMetaItems.MAGIC_CIRCUIT_UV.getStackForm();
            case GTValues.UHV -> PollutionMetaItems.MAGIC_CIRCUIT_UHV.getStackForm();
            case GTValues.UEV -> PollutionMetaItems.MAGIC_CIRCUIT_UEV.getStackForm();
            case GTValues.UIV -> PollutionMetaItems.MAGIC_CIRCUIT_UIV.getStackForm();
            case GTValues.UXV -> PollutionMetaItems.MAGIC_CIRCUIT_UXV.getStackForm();
            case GTValues.OpV -> PollutionMetaItems.MAGIC_CIRCUIT_OpV.getStackForm();
            case GTValues.MAX -> PollutionMetaItems.MAGIC_CIRCUIT_MAX.getStackForm();
            default -> ItemStack.EMPTY;
        };
    }

    private static void registerUpgrades() {
        // One "empty" module crafts the blank, then each category is stamped separately.
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.BLANK_TAROT_CARD, 4)
                .input(PollutionMetaItems.ARCANE_INK_CAPSULE, 2)
                .input(MetaItems.SENSOR_HV, 2)
                .outputs(new ItemStack(PollutionItemsInit.ESSENTIA_UPGRADE, 4, 0))
                .duration(200)
                .EUt(GTValues.VA[GTValues.HV])
                .buildAndRegister();

        for (int damage = 1; damage < ItemEssentiaUpgrade.ListUpgrade.length; damage++) {
            int gtTier = switch (damage) {
                case 1, 2 -> GTValues.HV;
                case 3, 4, 5 -> GTValues.EV;
                case 6, 7 -> GTValues.IV;
                default -> GTValues.LuV;
            };
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .inputs(new ItemStack(PollutionItemsInit.ESSENTIA_UPGRADE, 1, 0))
                    .input(MetaItems.SENSOR_LV, 4)
                    .input(MetaItems.EMITTER_LV, 2)
                    .input(thaumcraft.api.items.ItemsTC.salisMundus, 1)
                    .fluidInputs(meowmel.pollution.api.unification.PollutionMaterials.InfusedMagic
                            .getFluid(288 * damage))
                    .outputs(new ItemStack(PollutionItemsInit.ESSENTIA_UPGRADE, 1, damage))
                    .duration(200 + 40 * damage)
                    .EUt(GTValues.VA[gtTier])
                    .circuitMeta(damage)
                    .buildAndRegister();
        }
    }
}
