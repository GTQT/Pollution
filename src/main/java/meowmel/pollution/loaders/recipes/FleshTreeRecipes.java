package meowmel.pollution.loaders.recipes;

import WayofTime.bloodmagic.core.RegistrarBloodMagic;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.core.registry.OrbRegistry;
import WayofTime.bloodmagic.item.ItemActivationCrystal;
import WayofTime.bloodmagic.item.ItemSlate;
import WayofTime.bloodmagic.item.types.ShardType;
import meowmel.pollution.Pollution;
import meowmel.pollution.common.block.blocks.PollutionBlocksInit;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POBloodExtractorCasing;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;

/** 血肉之树终局入口配方。 */
public final class FleshTreeRecipes {

    private FleshTreeRecipes() {}

    public static void init() {
        registerBloodExtractor();
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Pollution.MODID, "bloody_starlight_container"),
                new InfusionRecipe("INFUSION@2",
                        new ItemStack(PollutionBlocksInit.BLOODY_STARLIGHT_CONTAINER), 6,
                        new AspectList().add(Aspect.LIFE, 64).add(Aspect.SOUL, 64)
                                .add(Aspect.MAGIC, 64).add(Aspect.ORDER, 32),
                        new ItemStack(Blocks.GLASS),
                        PollutionMetaItems.ASTRAL_BLOOD_CATALYST.getStackForm(),
                        PollutionMetaItems.STARRY_RUNE.getStackForm(),
                        new ItemStack(PollutionBlocksInit.HEARTFRUIT),
                        new ItemStack(ItemsTC.salisMundus),
                        PollutionMetaItems.ASTRAL_BLOOD_CATALYST.getStackForm(),
                        PollutionMetaItems.STARRY_RUNE.getStackForm(),
                        new ItemStack(PollutionBlocksInit.HEARTFRUIT),
                        new ItemStack(ItemsTC.salisMundus)));
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Pollution.MODID, "flesh_tree_sapling"),
                new InfusionRecipe(
                        "INFUSION@2",
                        new ItemStack(PollutionBlocksInit.FLESH_SAPLING),
                        12,
                        new AspectList()
                                .add(Aspect.LIFE, 250)
                                .add(Aspect.DEATH, 250)
                                .add(Aspect.SOUL, 250)
                                .add(Aspect.PLANT, 128)
                                .add(Aspect.MAGIC, 128)
                                .add(Aspect.FLUX, 64),
                        PollutionMetaItems.ASTRAL_NEURAL_BUNDLE.getStackForm(),
                        ItemSlate.SlateType.ETHEREAL.getStack(1),
                        ItemSlate.SlateType.ETHEREAL.getStack(1),
                        new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL),
                        new ItemStack(RegistrarBloodMagicItems.ITEM_DEMON_CRYSTAL),
                        ShardType.DEMONIC.getStack(1),
                        ShardType.DEMONIC.getStack(1),
                        PollutionMetaItems.INTEGRATECORE.getStackForm(),
                        PollutionMetaItems.INTEGRATECORE.getStackForm(),
                        PollutionMetaItems.STONE_OF_PHILOSOPHER_3.getStackForm(),
                        OrbRegistry.getOrbStack(RegistrarBloodMagic.ORB_ARCHMAGE),
                        ItemActivationCrystal.CrystalType.AWAKENED.getStack(1),
                        new ItemStack(ItemsTC.primordialPearl)));
    }

    private static void registerBloodExtractor() {
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.HULL[GTValues.UHV])
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV, 4)
                .input(MetaItems.ELECTRIC_PUMP_UHV, 4)
                .input(MetaItems.ELECTRIC_PISTON_UHV, 2)
                .input(MetaItems.FIELD_GENERATOR_UHV, 2)
                .input(PollutionMetaItems.ASTRAL_BLOOD_CATALYST, 8)
                .input(PollutionMetaItems.STARRY_RUNE, 8)
                .inputs(PollutionMetaBlocks.BLOOD_EXTRACTOR_CASING.getItemVariant(POBloodExtractorCasing.Type.SEALED_CASING, 8))
                .fluidInputs(PollutionMaterials.AstralBloodPlasma.getFluid(8000))
                .fluidInputs(PollutionMaterials.StarryArcaneAlloy.getFluid(2304))
                .output(PollutionMetaTileEntities.BLOODY_BLOOD_EXTRACTOR)
                .duration(2400).EUt(GTValues.VA[GTValues.UHV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, Materials.StainlessSteel)
                .input(OrePrefix.plate, PollutionMaterials.StarryArcaneAlloy, 4)
                .input(PollutionMetaItems.STARRY_RUNE)
                .fluidInputs(PollutionMaterials.AstralBloodPlasma.getFluid(250))
                .outputs(PollutionMetaBlocks.BLOOD_EXTRACTOR_CASING.getItemVariant(POBloodExtractorCasing.Type.SEALED_CASING, 2))
                .duration(200).EUt(GTValues.VA[GTValues.UV]).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(Blocks.GLASS, 4)
                .input(OrePrefix.foil, PollutionMaterials.StarryArcaneAlloy, 4)
                .input(PollutionMetaItems.STARRY_RUNE)
                .fluidInputs(PollutionMaterials.AstralBloodPlasma.getFluid(250))
                .outputs(PollutionMetaBlocks.BLOOD_EXTRACTOR_CASING.getItemVariant(POBloodExtractorCasing.Type.CYLINDER_GLASS, 4))
                .duration(200).EUt(GTValues.VA[GTValues.UV]).buildAndRegister();
    }
}
