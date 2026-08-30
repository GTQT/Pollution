package meowmel.pollution.loaders.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.nbtmatch.NBTCondition;
import gregtech.api.recipes.ingredients.nbtmatch.NBTMatcher;
import gregtech.api.recipes.ingredients.nbtmatch.NBTTagType;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import meowmel.pollution.api.astral.AstralNbtHelper;
import meowmel.pollution.Pollution;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POConstellationCrystal;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import meowmel.pollution.common.metatileentity.multiblock.astral.ConstellationTowerDefinition;
import meowmel.pollution.api.unification.PollutionMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

/** Recipes for the permanently attuned constellation-tower controllers. */
public final class ConstellationTowerRecipes {

    private ConstellationTowerRecipes() {}

    public static void init() {
        registerRitualCrystals();
        ConstellationTowerDefinition[] definitions = ConstellationTowerDefinition.values();
        for (int i = 0; i < definitions.length; i++) {
            ConstellationTowerDefinition definition = definitions[i];
            RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                    .input(MetaTileEntities.HULL[GTValues.UHV])
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV, 4)
                    .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED, 8)
                    .input(PollutionMetaItems.CELESTIAL_CALIBRATION_CORE, 4)
                    .input(PollutionMetaItems.HARMONIZING_RUNE_CORE, 2)
                    .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL, 16)
                    .input(MetaItems.FIELD_GENERATOR_UHV, 4)
                    .input(MetaItems.EMITTER_UHV, 8)
                    .input(MetaItems.SENSOR_UHV, 8)
                    .input(ItemsAS.skyResonator)
                    .inputNBT(PollutionMetaItems.CONSTELLATION_DATA_WAFER, NBTMatcher.EQUAL_TO,
                            NBTCondition.create(NBTTagType.STRING,
                                    AstralNbtHelper.POLLUTION_CONSTELLATION, definition.getId()))
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 32000))
                    .fluidInputs(PollutionMaterials.Starrymansus.getFluid(8000))
                    .fluidInputs(PollutionMaterials.DimensionalTransformingAgent.getFluid(2000))
                    .output(PollutionMetaTileEntities.CONSTELLATION_TOWERS[i])
                    .duration(2400)
                    .EUt(GTValues.VA[GTValues.UHV])
                    .buildAndRegister();
        }
    }

    private static void registerRitualCrystals() {
        ItemStack ritualCrystal = PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                .getItemVariant(POConstellationCrystal.CrystalType.RITUAL_CRYSTAL);

        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Pollution.MODID, "constellation_ritual_crystal"),
                new InfusionRecipe(
                        "INFUSION@2",
                        ritualCrystal.copy(),
                        10,
                        new AspectList()
                                .add(Aspect.CRYSTAL, 250)
                                .add(Aspect.MAGIC, 250)
                                .add(Aspect.AURA, 128)
                                .add(Aspect.LIGHT, 128)
                                .add(Aspect.ENERGY, 128),
                        PollutionMetaItems.CELESTIAL_CALIBRATION_CORE.getStackForm(),
                        PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UV.getStackForm(),
                        MetaItems.FIELD_GENERATOR_UV.getStackForm(),
                        PollutionMetaItems.ASTRAL_LENS_ADVANCED.getStackForm(),
                        new ItemStack(ItemsAS.celestialCrystal),
                        ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UV.getStackForm(),
                        MetaItems.FIELD_GENERATOR_UV.getStackForm(),
                        PollutionMetaItems.ASTRAL_LENS_ADVANCED.getStackForm(),
                        new ItemStack(ItemsAS.celestialCrystal),
                        ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        ItemCraftingComponent.MetaType.STARDUST.asStack(),
                        ItemCraftingComponent.MetaType.STARDUST.asStack()));

        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Pollution.MODID, "constellation_tower_core"),
                new InfusionRecipe(
                        "INFUSION@2",
                        PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                                .getItemVariant(POConstellationCrystal.CrystalType.TOWER_CORE),
                        15,
                        new AspectList()
                                .add(Aspect.CRYSTAL, 250)
                                .add(Aspect.MAGIC, 250)
                                .add(Aspect.AURA, 250)
                                .add(Aspect.ENERGY, 250)
                                .add(Aspect.ORDER, 128)
                                .add(Aspect.VOID, 128),
                        ritualCrystal.copy(),
                        ritualCrystal.copy(),
                        PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV.getStackForm(),
                        ritualCrystal.copy(),
                        MetaItems.FIELD_GENERATOR_UHV.getStackForm(),
                        ritualCrystal.copy(),
                        PollutionMetaItems.ASTRAL_LENS_ADVANCED.getStackForm(),
                        ritualCrystal.copy(),
                        PollutionMetaItems.CELESTIAL_CALIBRATION_CORE.getStackForm(),
                        ritualCrystal.copy(),
                        PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL.getStackForm(),
                        ritualCrystal.copy(),
                        MetaItems.FIELD_GENERATOR_UHV.getStackForm(),
                        ritualCrystal.copy(),
                        PollutionMetaItems.ASTRAL_LENS_ADVANCED.getStackForm(),
                        PollutionMetaItems.STARRY_RUNE.getStackForm(),
                        new ItemStack(ItemsAS.skyResonator)));
    }
}
