package meowmel.pollution.loaders.recipes;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.item.ItemSlate;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.recipes.ingredients.nbtmatch.NBTCondition;
import gregtech.api.recipes.ingredients.nbtmatch.NBTMatcher;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import meowmel.gtqtcore.api.unification.material.GTQTMaterials;
import meowmel.gtqtcore.common.items.GTQTMetaItems;
import meowmel.pollution.Pollution;
import meowmel.pollution.api.astral.AstralNbtHelper;
import meowmel.pollution.api.amplification.MagicProcessTag;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.recipes.properties.AstralCondition;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ModItems;

import java.util.ArrayList;
import java.util.List;

import static gregtech.api.GTValues.EV;
import static gregtech.api.GTValues.HV;
import static gregtech.api.GTValues.IV;
import static gregtech.api.GTValues.LV;
import static gregtech.api.GTValues.LuV;
import static gregtech.api.GTValues.MV;
import static gregtech.api.GTValues.ULV;
import static gregtech.api.GTValues.UHV;
import static gregtech.api.GTValues.UEV;
import static gregtech.api.GTValues.UIV;
import static gregtech.api.GTValues.UXV;
import static gregtech.api.GTValues.OpV;
import static gregtech.api.GTValues.UV;
import static gregtech.api.GTValues.VA;
import static gregtech.api.GTValues.ZPM;
import static gregtech.api.unification.material.Materials.*;
import static meowmel.gtqtcore.api.unification.material.GTQTMaterials.Sunnarium;

/**
 * Recipe owner for the cross-mod integration layer.  Keep these recipes out of
 * the older chemistry and machine files so every new bridge item has one
 * traceable source and one intentional first consumer.
 */
public final class MagicIntegrationRecipes {

    private MagicIntegrationRecipes() {}

    public static void init() {
        registerFoundationalMaterials();
        registerStarmetalAlchemy();
        registerRockCrystalCatalysis();
        registerOptics();
        registerBloodAndBotaniaIntermediates();
        registerAttunedWafersAndBioengineering();
        registerTarotStock();
        registerCircuitBoards();
        registerAdvancedAstralComponents();
        registerCelestialMachines();
        registerMagicFunctionalComponents();
        registerThaumcraftInfusionComponents();
        registerTransformCore();
    }

    private static void registerFoundationalMaterials() {
        // Four raw aquamarines are wet-cleaned and order-sorted into optical powder.
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .inputs(withCount(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(), 4))
                .fluidInputs(DistilledWater.getFluid(1000))
                .fluidInputs(PollutionMaterials.InfusedOrder.getFluid(144))
                .output(OrePrefix.dust, PollutionMaterials.OpticalGradeAquamarine, 4)
                .duration(300)
                .EUt(VA[HV])
                .buildAndRegister();

        // The sifter deliberately loses one quarter of the batch before bonus recovery.
        RecipeMaps.SIFTER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, PollutionMaterials.OpticalGradeAquamarine, 4)
                .output(OrePrefix.gem, PollutionMaterials.OpticalGradeAquamarine, 3)
                .chancedOutput(OrePrefix.gem, PollutionMaterials.OpticalGradeAquamarine, 1, 5000, 0)
                .duration(240)
                .EUt(VA[HV])
                .buildAndRegister();

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                .inputs(new ItemStack(BlocksAS.customFlower))
                .output(OrePrefix.dust, PollutionMaterials.StarlightPollen, 2)
                .duration(160)
                .EUt(VA[MV])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .input(BlocksTC.logSilverwood, 4)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 250))
                .fluidInputs(Water.getFluid(1000))
                .fluidOutputs(PollutionMaterials.MoonlightResin.getFluid(500))
                .duration(400)
                .EUt(VA[HV])
                .buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, PollutionMaterials.StarlightPollen, 2)
                .input(OrePrefix.dust, PollutionMaterials.Salismundus)
                .input(Items.DYE, 2, 0)
                .fluidInputs(PollutionMaterials.MoonlightResin.getFluid(250))
                .fluidInputs(PollutionMaterials.InfusedMagic.getFluid(144))
                .fluidOutputs(PollutionMaterials.ArcaneInk.getFluid(500))
                .duration(300)
                .EUt(VA[HV])
                .buildAndRegister();
    }

    private static void registerOptics() {
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .inputs(ItemCraftingComponent.MetaType.GLASS_LENS.asStack())
                .fluidInputs(Silver.getFluid(144))
                .fluidInputs(PollutionMaterials.MoonlightResin.getFluid(50))
                .output(PollutionMetaItems.SILVERED_GLASS_LENS)
                .duration(240)
                .EUt(VA[HV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.wireFine, Silver, 8)
                .inputs(ItemCraftingComponent.MetaType.RESO_GEM.asStack())
                .input(PollutionMetaItems.RUBBERSLIME)
                .fluidInputs(PollutionMaterials.MoonlightResin.getFluid(250))
                .output(PollutionMetaItems.ASTRAL_RESONANCE_COIL)
                .duration(300)
                .EUt(VA[HV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.SILVERED_GLASS_LENS, 2)
                .input(PollutionMetaItems.ASTRAL_RESONANCE_COIL)
                .input(OrePrefix.gem, PollutionMaterials.OpticalGradeAquamarine, 2)
                .input(BlocksAS.translucentBlock)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .output(PollutionMetaItems.ASTRAL_LENS_BASIC)
                .duration(400)
                .EUt(VA[HV])
                .buildAndRegister();
    }

    private static void registerStarmetalAlchemy() {
        SimpleRecipeBuilder stardust = PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
                .input(OrePrefix.dust, Iron)
                .input(OrePrefix.dust, PollutionMaterials.OpticalGradeAquamarine)
                .fluidInputs(PollutionMaterials.InfusedMagic.getFluid(144))
                .fluidInputs(PollutionMaterials.InfusedAlchemy.getFluid(144))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .outputs(ItemCraftingComponent.MetaType.STARDUST.asStack())
                .duration(200)
                .EUt(VA[IV]);
        MagicRecipeProperties.processTags(stardust, MagicProcessTag.ORE, MagicProcessTag.MINERAL_ENRICHMENT,
                MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.MULTI_MAGIC, MagicProcessTag.CATALYTIC);
        stardust.buildAndRegister();

        SimpleRecipeBuilder starmetal = PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
                .input(OrePrefix.ingot, Iron)
                .input(OrePrefix.dust, PollutionMaterials.OpticalGradeAquamarine)
                .fluidInputs(PollutionMaterials.InfusedMagic.getFluid(144))
                .fluidInputs(PollutionMaterials.InfusedAlchemy.getFluid(144))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .outputs(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack())
                .duration(200)
                .EUt(VA[IV]);
        MagicRecipeProperties.processTags(starmetal, MagicProcessTag.ORE, MagicProcessTag.MINERAL_ENRICHMENT,
                MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.MULTI_MAGIC, MagicProcessTag.CATALYTIC);
        starmetal.buildAndRegister();
    }

    private static void registerRockCrystalCatalysis() {
        SimpleRecipeBuilder seedSelection = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(ItemsAS.rockCrystal))
                .input(PollutionMetaItems.SILVERED_GLASS_LENS)
                .input(OrePrefix.dust, PollutionMaterials.OpticalGradeAquamarine, 2)
                .fluidInputs(PollutionMaterials.MoonlightResin.getFluid(250))
                .output(PollutionMetaItems.ROCK_CRYSTAL_SEED)
                .duration(400)
                .EUt(VA[HV]);
        MagicRecipeProperties.crystalSeedSelection(seedSelection);
        MagicRecipeProperties.magic(seedSelection, 2, 20L, 0, 32, AstralCondition.NONE, "");
        MagicRecipeProperties.processTags(seedSelection, MagicProcessTag.PRECISION, MagicProcessTag.CATALYTIC,
                MagicProcessTag.RESONANCE, MagicProcessTag.INFUSION);
        seedSelection.buildAndRegister();

        SimpleRecipeBuilder cultivation = PORecipeMaps.CRYSTAL_CULTIVATION_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.ROCK_CRYSTAL_SEED)
                .input(ModBlocks.livingrock)
                .inputs(new ItemStack(ItemsAS.shiftingStar))
                .notConsumable(PollutionMetaItems.INTEGRATECORE.getStackForm())
                .input(OrePrefix.dust, PollutionMaterials.OpticalGradeAquamarine, 8)
                .notConsumable(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getStackForm())
                .fluidInputs(PollutionMaterials.MoonlightResin.getFluid(250))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .output(PollutionMetaItems.CELESTIAL_CRYSTAL_EMBRYO)
                .duration(600)
                .EUt(VA[LuV]);
        MagicRecipeProperties.crystalEmbryoCultivation(cultivation);
        MagicRecipeProperties.magic(cultivation, 4, 50L, 0, 64, AstralCondition.NONE, "");
        MagicRecipeProperties.processTags(cultivation, MagicProcessTag.ORE, MagicProcessTag.MINERAL_ENRICHMENT,
                MagicProcessTag.CATALYTIC, MagicProcessTag.HIDDEN_RITUAL, MagicProcessTag.STRUCTURAL_CONTROL);
        cultivation.buildAndRegister();

        int growthRecipes = 0;
        for (IConstellation constellation : ConstellationRegistry.getAllConstellations()) {
            if (constellation == null) continue;

            SimpleRecipeBuilder growth = PORecipeMaps.CELESTIAL_CRYSTAL_GROWTH_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.CELESTIAL_CRYSTAL_EMBRYO)
                    .input(PollutionMetaItems.SILVERED_GLASS_LENS)
                    .inputs(ItemCraftingComponent.MetaType.STARDUST.asStack())
                    .input(OrePrefix.dust, PollutionMaterials.OpticalGradeAquamarine, 2)
                    .fluidInputs(PollutionMaterials.MoonlightResin.getFluid(500))
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 1000))
                    .fluidInputs(PollutionMaterials.InfusedCrystal.getFluid(500))
                    // Static JEI output; MagicMultiblockRecipeLogic replaces it with the embryo's
                    // quality-bearing independent cultivated-crystal NBT before reserving output space.
                    .output(PollutionMetaItems.CULTIVATED_CRYSTAL)
                    .duration(1200)
                    .EUt(VA[LuV]);
            MagicRecipeProperties.crystalCelestialGrowth(growth);
            MagicRecipeProperties.celestialTarget(growth, constellation.getSimpleName());
            MagicRecipeProperties.magic(growth, 6, 100L, 0, 128,
                    AstralCondition.night(constellation.getSimpleName(), 0.15F), "");
            MagicRecipeProperties.processTags(growth, MagicProcessTag.PRECISION, MagicProcessTag.TIMED,
                    MagicProcessTag.RESONANCE, MagicProcessTag.INFUSION, MagicProcessTag.STRUCTURAL_CONTROL);
            growth.buildAndRegister();
            growthRecipes++;
        }
        Pollution.LOGGER.info("Registered {} constellation-specific celestial crystal growth recipes", growthRecipes);
    }

    private static void registerBloodAndBotaniaIntermediates() {
        RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Blocks.STONE_SLAB, 4, 0))
                .fluidInputs(Chlorine.getFluid(250))
                .output(PollutionMetaItems.STERILE_SLATE_BLANK)
                .duration(200)
                .EUt(VA[MV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, PollutionMaterials.Manasteel, 2)
                .input(OrePrefix.foil, Copper, 8)
                .input(BlocksAS.translucentBlock)
                .input(PollutionMetaItems.SILVERED_GLASS_LENS)
                .fluidInputs(GTQTMaterials.Mana.getFluid(500))
                .output(PollutionMetaItems.PRECISION_RUNE_BLANK)
                .duration(300)
                .EUt(VA[HV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.MANA_RESONANCE_COIL)
                .input(OrePrefix.dust, PollutionMaterials.InfusedPlant, 4)
                .input(PollutionMetaItems.RUBBERSLIME)
                .input(ModItems.rune, 1, 3)
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .output(PollutionMetaItems.NATURAL_INFUSED_COIL)
                .duration(400)
                .EUt(VA[HV])
                .buildAndRegister();

        // GT only prepares the sterile body; Blood Magic still supplies the life identity and LP cost.
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addBloodAltar(
                Ingredient.fromStacks(PollutionMetaItems.STERILE_SLATE_BLANK.getStackForm()),
                ItemSlate.SlateType.BLANK.getStack(1),
                1, 1000, 5, 10);
    }

    private static void registerAttunedWafersAndBioengineering() {
        // The cutter's diamond-coated head performs the cutting, grinding and
        // polishing passes. CUTTER_RECIPES accepts only one item input.
        RecipeMaps.CUTTER_RECIPES.recipeBuilder()
                .input(ItemsAS.tunedRockCrystal)
                .fluidInputs(Lubricant.getFluid(250))
                .output(PollutionMetaItems.ATTUNED_CRYSTAL_WAFER, 4)
                .duration(500)
                .EUt(VA[IV])
                .buildAndRegister();

        // Constellation Data Wafers are deliberately not assembled here. Their
        // only production path is the Celestial Observation Array, so every
        // wafer represents an actual observation under its live constellation.

        // The four legacy flesh boards retain their item IDs for save compatibility,
        // but now form a biological-template progression rather than an ore:circuit family.
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.BLOOD_PRIMITIVE_MEAT, 2)
                .input(MetaItems.STEM_CELLS, 2)
                .input(OrePrefix.plate, Polytetrafluoroethylene)
                .input(PollutionMetaItems.BLOOD_PORT)
                .fluidInputs(PollutionMaterials.BloodPlasma.getFluid(1000))
                .fluidInputs(SterileGrowthMedium.getFluid(500))
                .output(PollutionMetaItems.BLOOD_CIRCUIT, 2)
                .duration(400)
                .EUt(VA[IV])
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.BLOOD_CIRCUIT)
                .input(PollutionMetaItems.BLOOD_RATS_BRAIN)
                .input(PollutionMetaItems.BLOOD_MITOCHONDRION_POWER)
                .input(PollutionMetaItems.BLOOD_ENDORPHINS_STABILIZER)
                .fluidInputs(SterileGrowthMedium.getFluid(1000))
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .output(PollutionMetaItems.BLOOD_CIRCUIT_ADVANCED)
                .duration(500)
                .EUt(VA[IV])
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.BLOOD_CIRCUIT_ADVANCED)
                .input(PollutionMetaItems.BLOOD_IPS_HUMAN_BRAIN)
                .input(PollutionMetaItems.LIVING_MAGIC_BIOFILM, 2)
                .fluidInputs(PollutionMaterials.CelestialBiologicalMedium.getFluid(500))
                .fluidInputs(PollutionMaterials.InfusedThought.getFluid(288))
                .output(PollutionMetaItems.BLOOD_CIRCUIT_ULTIMATE)
                .duration(600)
                .EUt(VA[LuV])
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.BLOOD_CIRCUIT_ULTIMATE)
                .inputs(ItemCraftingComponent.MetaType.STARDUST.asStack())
                .inputNBT(PollutionMetaItems.CONSTELLATION_DATA_WAFER, NBTMatcher.ANY, NBTCondition.ANY)
                .fluidInputs(PollutionMaterials.infused_purified_blood.getFluid(1000))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 1000))
                .output(PollutionMetaItems.BLOOD_CIRCUIT_SUPREME)
                .duration(800)
                .EUt(VA[ZPM])
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.BLOOD_CIRCUIT)
                .input(MetaItems.STEM_CELLS, 4)
                .fluidInputs(SterileGrowthMedium.getFluid(1000))
                .fluidInputs(GTQTMaterials.Mana.getFluid(1000))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .output(PollutionMetaItems.LIVING_MAGIC_BIOFILM)
                .duration(600)
                .EUt(VA[LuV])
                .buildAndRegister();
    }

    private static void registerTarotStock() {
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder()
                .input(Items.PAPER, 4)
                .input(Items.STRING, 2)
                .output(PollutionMetaItems.BLANK_TAROT_CARD)
                .duration(120)
                .EUt(VA[ZPM])
                .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                .input(Items.GLASS_BOTTLE)
                .fluidInputs(PollutionMaterials.ArcaneInk.getFluid(250))
                .output(PollutionMetaItems.ARCANE_INK_CAPSULE)
                .duration(100)
                .EUt(VA[MV])
                .buildAndRegister();

        // The card image is not a cosmetic crafting result: each Major Arcana
        // is a written magic-program used by the Tarot Hatch. The circuit
        // setting identifies the arcana while the emblem prevents all cards
        // from collapsing into a single interchangeable blank-card recipe.
        registerTarotCards();
    }

    private static void registerTarotCards() {
        registerTarotCard(1, PollutionMetaItems.TAROT_THE_FOOL, new ItemStack(Items.ENDER_PEARL), EnderPearl);
        registerTarotCard(2, PollutionMetaItems.TAROT_THE_MAGICIAN, new ItemStack(ItemsTC.salisMundus), Electrum);
        registerTarotCard(3, PollutionMetaItems.TAROT_THE_HIGH_PRIESTESS, new ItemStack(Items.ENDER_EYE), CertusQuartz);
        registerTarotCard(4, PollutionMetaItems.TAROT_THE_EMPRESS, new ItemStack(Items.GOLDEN_APPLE), Titanium);
        registerTarotCard(5, PollutionMetaItems.TAROT_THE_EMPEROR, new ItemStack(Items.IRON_INGOT), TungstenSteel);
        registerTarotCard(6, PollutionMetaItems.TAROT_THE_HIGHOPHANT, new ItemStack(Items.BOOK), Palladium);
        registerTarotCard(7, PollutionMetaItems.TAROT_THE_LOVERS, new ItemStack(Items.DYE, 1, 1), RoseGold);
        registerTarotCard(8, PollutionMetaItems.TAROT_THE_CHARIOT, new ItemStack(Items.MINECART), Aluminium);
        registerTarotCard(9, PollutionMetaItems.TAROT_THE_STRENGTH, new ItemStack(Items.BLAZE_ROD), Steel);
        registerTarotCard(10, PollutionMetaItems.TAROT_THE_HERMIT, new ItemStack(Items.GOLD_NUGGET), Platinum);
        registerTarotCard(11, PollutionMetaItems.TAROT_THE_WHEEL_OF_FORTUNE, new ItemStack(Items.CLOCK), Osmium);
        registerTarotCard(12, PollutionMetaItems.TAROT_JUSTICE, new ItemStack(Items.IRON_SWORD), SolderingAlloy);
        registerTarotCard(13, PollutionMetaItems.TAROT_THE_HANGED_MAN, new ItemStack(Items.LEAD), Lead);
        registerTarotCard(14, PollutionMetaItems.TAROT_DEATH, new ItemStack(Items.BONE), NetherStar);
        registerTarotCard(15, PollutionMetaItems.TAROT_TEMPERANCE, new ItemStack(Items.GLASS_BOTTLE), StainlessSteel);
        registerTarotCard(16, PollutionMetaItems.TAROT_THE_DEVIL, new ItemStack(Items.MAGMA_CREAM), Naquadah);
        registerTarotCard(17, PollutionMetaItems.TAROT_THE_TOWER, new ItemStack(Blocks.TNT), NaquadahAlloy);
        registerTarotCard(18, PollutionMetaItems.TAROT_THE_STAR, new ItemStack(Items.NETHER_STAR), PollutionMaterials.InfusedLight);
        registerTarotCard(19, PollutionMetaItems.TAROT_THE_MOON, new ItemStack(Items.GHAST_TEAR), PollutionMaterials.InfusedDark);
        registerTarotCard(20, PollutionMetaItems.TAROT_THE_SUN, new ItemStack(Items.GLOWSTONE_DUST), Sunnarium);
        registerTarotCard(21, PollutionMetaItems.TAROT_JUDGEMENT, new ItemStack(Items.TOTEM_OF_UNDYING), Iridium);
        registerTarotCard(22, PollutionMetaItems.TAROT_THE_WORLD, new ItemStack(Items.DRAGON_BREATH), NaquadahEnriched);
    }

    private static void registerTarotCard(int circuit, gregtech.api.items.metaitem.MetaItem<?>.MetaValueItem card,
                                          ItemStack emblem, Material industrialAnchor) {
        SimpleRecipeBuilder builder = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .circuitMeta(circuit)
                .input(PollutionMetaItems.BLANK_TAROT_CARD)
                .input(PollutionMetaItems.ARCANE_INK_CAPSULE)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_ZPM)
                .input(OrePrefix.dust, PollutionMaterials.Salismundus)
                .input(OrePrefix.dust, industrialAnchor, 2)
                .inputs(emblem)
                .fluidInputs(PollutionMaterials.InfusedMagic.getFluid(144))
                .output(card)
                .duration(200)
                .EUt(VA[ZPM]);
        MagicRecipeProperties.processTags(builder, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.INFUSION);
        builder.buildAndRegister();
    }

    private static void registerCircuitBoards() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(Items.PAPER, 4)
                .input(OrePrefix.foil, Copper, 4)
                .input(OrePrefix.dust, PollutionMaterials.Salismundus)
                .fluidInputs(Glue.getFluid(250))
                .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_ULV)
                .duration(120)
                .EUt(VA[ULV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_ULV)
                .input(OrePrefix.ingot, PollutionMaterials.BasicSubstrate)
                .input(OrePrefix.plate, PollutionMaterials.Manasteel, 2)
                .input(OrePrefix.dust, PollutionMaterials.Salismundus, 2)
                .fluidInputs(GTQTMaterials.Mana.getFluid(500))
                .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LV)
                .duration(160)
                .EUt(VA[LV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LV)
                .input(PollutionMetaItems.STERILE_SLATE_BLANK, 2)
                .input(OrePrefix.wireFine, Silver, 8)
                .input(MetaItems.RESISTOR, 2)
                .fluidInputs(PollutionMaterials.InfusedLife.getFluid(288))
                .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_MV)
                .duration(220)
                .EUt(VA[MV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_MV)
                .input(OrePrefix.gem, PollutionMaterials.OpticalGradeAquamarine, 2)
                .input(PollutionMetaItems.SILVERED_GLASS_LENS)
                .input(PollutionMetaItems.MANA_RESONANCE_COIL)
                .notConsumable(new ItemStack(ItemsTC.visResonator))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_HV)
                .duration(300)
                .EUt(VA[HV])
                .buildAndRegister();

        registerMagicCircuitBoardEV();
        registerMagicCircuitBoardIV();
        registerMagicCircuitBoardLuV();
        registerMagicCircuitBoardZPM();
        registerMagicCircuitBoardUV();
        registerMagicCircuitBoardsHighTier();
    }

    private static void registerMagicCircuitBoardEV() {
        List<Object> components = new ArrayList<>();
        addCopies(components, PollutionMetaItems.PRECISION_RUNE_BLANK.getStackForm(), 2);
        components.add(PollutionMetaItems.NATURAL_INFUSED_COIL.getStackForm());
        addCopies(components, MetaItems.SMD_CAPACITOR.getStackForm(), 4);
        addCopies(components, MetaItems.SMD_TRANSISTOR.getStackForm(), 4);
        addCopies(components, MetaItems.SMD_DIODE.getStackForm(), 4);

        addInfusionRecipe(
                "magic_circuit_board_ev",
                PollutionMetaItems.MAGIC_CIRCUIT_BOARD_EV.getStackForm(),
                4,
                new AspectList()
                        .add(Aspect.CRAFT, 32)
                        .add(Aspect.MECHANISM, 32)
                        .add(Aspect.MAGIC, 24),
                PollutionMetaItems.MAGIC_CIRCUIT_BOARD_HV.getStackForm(),
                components.toArray());
    }

    private static void registerMagicCircuitBoardIV() {
        List<Object> components = new ArrayList<>();
        components.add(PollutionMetaItems.WHITE_RUNE.getStackForm());
        components.add(PollutionMetaItems.BLACK_RUNE.getStackForm());
        components.add(PollutionMetaItems.STARRY_RUNE.getStackForm());
        components.add(PollutionMetaItems.NODE_STABILIZATION_FRAME.getStackForm());
        addCopies(components, MetaItems.SMD_CAPACITOR.getStackForm(), 8);
        addCopies(components, MetaItems.SMD_TRANSISTOR.getStackForm(), 8);
        addCopies(components, MetaItems.SMD_DIODE.getStackForm(), 8);

        addInfusionRecipe(
                "magic_circuit_board_iv",
                PollutionMetaItems.MAGIC_CIRCUIT_BOARD_IV.getStackForm(),
                6,
                new AspectList()
                        .add(Aspect.MAGIC, 64)
                        .add(Aspect.MECHANISM, 64)
                        .add(Aspect.ORDER, 32)
                        .add(Aspect.AURA, 32),
                PollutionMetaItems.MAGIC_CIRCUIT_BOARD_EV.getStackForm(),
                components.toArray());
    }

    private static void registerMagicCircuitBoardLuV() {
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_IV)
                .input(PollutionMetaItems.LIVING_MAGIC_BIOFILM, 2)
                .input(PollutionMetaItems.BLOOD_CIRCUIT_ADVANCED)
                .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED)
                .input(MetaItems.ADVANCED_SMD_CAPACITOR, 8)
                .input(MetaItems.ADVANCED_SMD_TRANSISTOR, 8)
                .fluidInputs(PollutionMaterials.CelestialBiologicalMedium.getFluid(500))
                .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LuV)
                .duration(600)
                .EUt(VA[IV])
                .buildAndRegister();
    }

    private static void registerMagicCircuitBoardZPM() {
        for (IConstellation constellation : ConstellationRegistry.getAllConstellations()) {
            if (constellation == null) continue;
            SimpleRecipeBuilder builder = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LuV)
                    .input(PollutionMetaItems.ATTUNED_CRYSTAL_WAFER)
                    .inputs(AstralNbtHelper.createDataWafer(constellation))
                    .inputs(AstralNbtHelper.createCalibratedCore(constellation))
                    .notConsumable(PollutionMetaItems.CULTIVATED_CRYSTAL)
                    .input(MetaItems.ADVANCED_SMD_DIODE, 8)
                    .input(MetaItems.ADVANCED_SMD_TRANSISTOR, 8)
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                    .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_ZPM)
                    .duration(800)
                    .EUt(VA[LuV]);
            MagicRecipeProperties.astralCondition(builder,
                    AstralCondition.night(constellation.getSimpleName(), 0.10F));
            MagicRecipeProperties.cultivatedCrystalQuality(builder, 85);
            MagicRecipeProperties.processTags(builder, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC);
            builder.buildAndRegister();
        }
    }

    private static void registerMagicCircuitBoardUV() {
        for (IConstellation constellation : ConstellationRegistry.getAllConstellations()) {
            if (constellation == null) continue;
            SimpleRecipeBuilder builder = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_ZPM)
                    .inputs(AstralNbtHelper.createDataWafer(constellation))
                    .inputs(AstralNbtHelper.createCalibratedCore(constellation))
                    .input(PollutionMetaItems.BLOOD_CIRCUIT_SUPREME)
                    .input(PollutionMetaItems.ASTRAL_NEURAL_BUNDLE)
                    .input(MetaItems.ADVANCED_SMD_RESISTOR, 8)
                    .input(MetaItems.ADVANCED_SMD_CAPACITOR, 8)
                    .fluidInputs(PollutionMaterials.CelestialBiologicalMedium.getFluid(500))
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 1000))
                    .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UV)
                    .duration(1000)
                    .EUt(VA[ZPM]);
            MagicRecipeProperties.astralCondition(builder,
                    AstralCondition.night(constellation.getSimpleName(), 0.15F));
            MagicRecipeProperties.processTags(builder, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC);
            builder.buildAndRegister();
        }
    }

    private static void registerMagicCircuitBoardsHighTier() {
        for (IConstellation constellation : ConstellationRegistry.getAllConstellations()) {
            if (constellation == null) continue;
            ItemStack wafer = AstralNbtHelper.createDataWafer(constellation);
            ItemStack core = AstralNbtHelper.createCalibratedCore(constellation);
            AstralCondition condition = AstralCondition.night(constellation.getSimpleName(), 0.20F);

            SimpleRecipeBuilder uhv = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UV)
                    .inputs(wafer.copy())
                    .inputs(core.copy())
                    .input(PollutionMetaItems.BLOOD_CIRCUIT_SUPREME)
                    .input(PollutionMetaItems.ASTRAL_NEURAL_BUNDLE, 2)
                    .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED)
                    .input(GTQTMetaItems.GOOWARE_SMD_TRANSISTOR, 8)
                    .input(GTQTMetaItems.GOOWARE_SMD_CAPACITOR, 8)
                    .input(GTQTMetaItems.GOOWARE_SMD_DIODE, 8)
                    .fluidInputs(PollutionMaterials.CelestialBiologicalMedium.getFluid(1000))
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 2000))
                    .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV)
                    .duration(1200).EUt(VA[UV]);
            MagicRecipeProperties.astralCondition(uhv, condition);
            MagicRecipeProperties.processTags(uhv, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.THREE_MAGIC_SYSTEMS);
            uhv.buildAndRegister();

            SimpleRecipeBuilder uev = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV)
                    .inputs(wafer.copy())
                    .inputs(core.copy())
                    .input(PollutionMetaItems.ASTRAL_BLOOD_CATALYST)
                    .input(PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL)
                    .input(GTQTMetaItems.OPTICAL_SMD_TRANSISTOR, 8)
                    .input(GTQTMetaItems.OPTICAL_SMD_RESISTOR, 8)
                    .input(GTQTMetaItems.OPTICAL_SMD_DIODE, 8)
                    .input(GTQTMetaItems.OPTICAL_SMD_CAPACITOR, 8)
                    .fluidInputs(PollutionMaterials.starrymansus.getFluid(2000))
                    .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(500))
                    .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UEV)
                    .duration(1400).EUt(VA[UHV]);
            MagicRecipeProperties.astralCondition(uev, condition);
            MagicRecipeProperties.processTags(uev, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.THREE_MAGIC_SYSTEMS,
                    MagicProcessTag.CONSUMABLE_CATALYST);
            MagicRecipeProperties.consumableCatalystInputs(uev, 3);
            uev.buildAndRegister();

            SimpleRecipeBuilder uiv = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UEV)
                    .inputs(wafer.copy())
                    .inputs(withCount(core, 2))
                    .input(PollutionMetaItems.HARMONIZING_RUNE_CORE)
                    .input(PollutionMetaItems.ASTRAL_NEURAL_BUNDLE, 4)
                    .input(GTQTMetaItems.SPINTRONIC_SMD_TRANSISTOR, 8)
                    .input(GTQTMetaItems.SPINTRONIC_SMD_RESISTOR, 8)
                    .input(GTQTMetaItems.SPINTRONIC_SMD_DIODE, 8)
                    .input(GTQTMetaItems.SPINTRONIC_SMD_CAPACITOR, 8)
                    .fluidInputs(PollutionMaterials.starrymansus.getFluid(4000))
                    .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(1000))
                    .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UIV)
                    .duration(1600).EUt(VA[UEV]);
            MagicRecipeProperties.astralCondition(uiv, condition);
            MagicRecipeProperties.processTags(uiv, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.THREE_MAGIC_SYSTEMS);
            uiv.buildAndRegister();

            SimpleRecipeBuilder uxv = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UIV)
                    .inputs(wafer.copy())
                    .inputs(withCount(core, 2))
                    .input(PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL)
                    .input(PollutionMetaItems.HARMONIZING_RUNE_CORE, 2)
                    .input(GTQTMetaItems.COSMIC_SMD_TRANSISTOR, 8)
                    .input(GTQTMetaItems.COSMIC_SMD_RESISTOR, 8)
                    .input(GTQTMetaItems.COSMIC_SMD_DIODE, 8)
                    .input(GTQTMetaItems.COSMIC_SMD_CAPACITOR, 8)
                    .fluidInputs(PollutionMaterials.starrymansus.getFluid(8000))
                    .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(2000))
                    .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UXV)
                    .duration(1800).EUt(VA[UIV]);
            MagicRecipeProperties.astralCondition(uxv, condition);
            MagicRecipeProperties.processTags(uxv, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.THREE_MAGIC_SYSTEMS);
            uxv.buildAndRegister();

            SimpleRecipeBuilder opv = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UXV)
                    .inputs(wafer.copy())
                    .inputs(withCount(core, 4))
                    .input(PollutionMetaItems.CAUSALITY_CATALYST)
                    .input(PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL)
                    .input(GTQTMetaItems.SUPRACAUSAL_SMD_TRANSISTOR, 16)
                    .input(GTQTMetaItems.SUPRACAUSAL_SMD_RESISTOR, 16)
                    .input(GTQTMetaItems.SUPRACAUSAL_SMD_DIODE, 16)
                    .input(GTQTMetaItems.SUPRACAUSAL_SMD_CAPACITOR, 16)
                    .fluidInputs(PollutionMaterials.starrymansus.getFluid(16000))
                    .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(4000))
                    .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_OpV)
                    .duration(2000).EUt(VA[UXV]);
            MagicRecipeProperties.astralCondition(opv, condition);
            MagicRecipeProperties.processTags(opv, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.THREE_MAGIC_SYSTEMS,
                    MagicProcessTag.CONSUMABLE_CATALYST);
            MagicRecipeProperties.consumableCatalystInputs(opv, 3);
            opv.buildAndRegister();

            SimpleRecipeBuilder max = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_OpV)
                    .inputs(withCount(wafer, 4))
                    .inputs(withCount(core, 8))
                    .input(PollutionMetaItems.CAUSALITY_CATALYST, 2)
                    .input(PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL, 2)
                    .input(PollutionMetaItems.BLOOD_CIRCUIT_SUPREME, 8)
                    .input(GTQTMetaItems.SUPRACAUSAL_SMD_TRANSISTOR, 32)
                    .input(GTQTMetaItems.SUPRACAUSAL_SMD_DIODE, 32)
                    .input(GTQTMetaItems.SUPRACAUSAL_SMD_CAPACITOR, 32)
                    .fluidInputs(PollutionMaterials.starrymansus.getFluid(32000))
                    .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(8000))
                    .output(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_MAX)
                    .duration(2400).EUt(VA[OpV]);
            MagicRecipeProperties.astralCondition(max, condition);
            MagicRecipeProperties.processTags(max, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.THREE_MAGIC_SYSTEMS,
                    MagicProcessTag.CONSUMABLE_CATALYST);
            MagicRecipeProperties.consumableCatalystInputs(max, 3);
            max.buildAndRegister();
        }
    }

    private static void registerAdvancedAstralComponents() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.ASTRAL_LENS_BASIC)
                .input(PollutionMetaItems.ATTUNED_CRYSTAL_WAFER)
                .input(ItemsAS.celestialCrystal)
                .input(MetaItems.ROBOT_ARM_LuV)
                .input(MetaItems.SENSOR_LuV)
                .input(OrePrefix.gearSmall, Titanium, 4)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 1000))
                .output(PollutionMetaItems.ASTRAL_LENS_ADVANCED)
                .duration(600)
                .EUt(VA[LuV])
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED)
                .input(PollutionMetaItems.ATTUNED_CRYSTAL_WAFER, 2)
                .input(ItemsAS.skyResonator)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LuV)
                .input(MetaItems.FIELD_GENERATOR_LuV)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 1000))
                .fluidInputs(PollutionMaterials.InfusedOrder.getFluid(576))
                .output(PollutionMetaItems.CELESTIAL_CALIBRATION_CORE)
                .duration(800)
                .EUt(VA[LuV])
                .buildAndRegister();

        SimpleRecipeBuilder neuralBundle = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaItems.LIVING_MAGIC_BIOFILM, 2)
                .input(PollutionMetaItems.BLOOD_CIRCUIT_ADVANCED)
                .input(PollutionMetaItems.ATTUNED_CRYSTAL_WAFER)
                .inputNBT(PollutionMetaItems.CONSTELLATION_DATA_WAFER, NBTMatcher.ANY, NBTCondition.ANY)
                .notConsumable(PollutionMetaItems.CULTIVATED_CRYSTAL)
                .input(MetaItems.ADVANCED_SMD_TRANSISTOR, 8)
                .fluidInputs(PollutionMaterials.CelestialBiologicalMedium.getFluid(500))
                .fluidInputs(PollutionMaterials.synthetic_computational_blood.getFluid(500))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .output(PollutionMetaItems.ASTRAL_NEURAL_BUNDLE, 2)
                .duration(800)
                .EUt(VA[ZPM]);
        MagicRecipeProperties.cultivatedCrystalQuality(neuralBundle, 85);
        neuralBundle.buildAndRegister();

        // High-tier bridge items are constellation-specific at production time.
        // Their output remains a common item, while the exact wafer/core pair and
        // live sky authorization prevent cross-constellation substitution.
        for (IConstellation constellation : ConstellationRegistry.getAllConstellations()) {
            if (constellation == null) continue;
            ItemStack wafer = AstralNbtHelper.createDataWafer(constellation);
            ItemStack core = AstralNbtHelper.createCalibratedCore(constellation);
            AstralCondition condition = AstralCondition.night(constellation.getSimpleName(), 0.20F);

            SimpleRecipeBuilder astralBlood = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV)
                    .inputs(wafer.copy())
                    .inputs(core.copy())
                    .input(PollutionMetaItems.BLOOD_CIRCUIT_SUPREME)
                    .input(PollutionMetaItems.ASTRAL_NEURAL_BUNDLE, 2)
                    .inputs(withCount(ItemCraftingComponent.MetaType.STARDUST.asStack(), 4))
                    .fluidInputs(PollutionMaterials.infused_purified_blood.getFluid(2000))
                    .fluidInputs(PollutionMaterials.CelestialBiologicalMedium.getFluid(1000))
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 2000))
                    .output(PollutionMetaItems.ASTRAL_BLOOD_CATALYST, 2)
                    .duration(1400).EUt(VA[UHV]);
            MagicRecipeProperties.astralCondition(astralBlood, condition);
            astralBlood.buildAndRegister();

            SimpleRecipeBuilder harmonizing = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV)
                    .inputs(wafer.copy())
                    .inputs(core.copy())
                    .input(PollutionMetaItems.WHITE_RUNE)
                    .input(PollutionMetaItems.BLACK_RUNE)
                    .input(PollutionMetaItems.STARRY_RUNE)
                    .input(MetaItems.FIELD_GENERATOR_UV)
                    .fluidInputs(PollutionMaterials.starrymansus.getFluid(2000))
                    .fluidInputs(GTQTMaterials.Mana.getFluid(4000))
                    .output(PollutionMetaItems.HARMONIZING_RUNE_CORE)
                    .duration(1400).EUt(VA[UHV]);
            MagicRecipeProperties.astralCondition(harmonizing, condition);
            harmonizing.buildAndRegister();

            SimpleRecipeBuilder primordial = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UHV)
                    .inputs(core.copy())
                    .input(PollutionMetaItems.ASTRAL_BLOOD_CATALYST)
                    .input(PollutionMetaItems.HARMONIZING_RUNE_CORE)
                    .input(PollutionMetaItems.BALL_IN_ITSELF)
                    .input(ItemsAS.celestialCrystal)
                    .input(MetaItems.FIELD_GENERATOR_UHV)
                    .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(2000))
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 4000))
                    .output(PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL)
                    .duration(1800).EUt(VA[UHV]);
            MagicRecipeProperties.astralCondition(primordial, condition);
            MagicRecipeProperties.processTags(primordial, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.THREE_MAGIC_SYSTEMS,
                    MagicProcessTag.CONSUMABLE_CATALYST);
            MagicRecipeProperties.consumableCatalystInputs(primordial, 2);
            primordial.buildAndRegister();

            SimpleRecipeBuilder causality = PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_UXV)
                    .inputs(wafer.copy())
                    .inputs(core.copy())
                    .input(PollutionMetaItems.PRIMORDIAL_STAR_BLOOD_CRYSTAL)
                    .input(PollutionMetaItems.BALL_IN_ITSELF)
                    .input(PollutionMetaItems.SYMPTOMATIC_VIS_DATA_LINK)
                    .input(MetaItems.FIELD_GENERATOR_UIV)
                    .fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(8000))
                    .fluidInputs(PollutionMaterials.starrymansus.getFluid(16000))
                    .output(PollutionMetaItems.CAUSALITY_CATALYST)
                    .duration(2200).EUt(VA[UIV]);
            MagicRecipeProperties.astralCondition(causality, condition);
            MagicRecipeProperties.processTags(causality, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.HIDDEN_RITUAL, MagicProcessTag.STRUCTURAL_CONTROL,
                    MagicProcessTag.THREE_MAGIC_SYSTEMS);
            causality.buildAndRegister();
        }
    }

    private static void registerCelestialMachines() {
        for (IConstellation constellation : ConstellationRegistry.getAllConstellations()) {
            if (constellation == null) continue;
            ItemStack constellationPaper = new ItemStack(ItemsAS.constellationPaper);
            hellfirepvp.astralsorcery.common.item.ItemConstellationPaper
                    .setConstellation(constellationPaper, constellation);

            SimpleRecipeBuilder observation = PORecipeMaps.CELESTIAL_OBSERVATION_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.ATTUNED_CRYSTAL_WAFER)
                    .input(MetaItems.TOOL_DATA_STICK)
                    .notConsumable(constellationPaper)
                    .fluidInputs(PollutionMaterials.ArcaneInk.getFluid(100))
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 250))
                    .outputs(AstralNbtHelper.createDataWafer(constellation))
                    .duration(300).EUt(VA[LuV]);
            MagicRecipeProperties.infusedFluidPerTick(observation, 0);
            MagicRecipeProperties.astralCondition(observation,
                    AstralCondition.night(constellation.getSimpleName(), 0.10F));
            MagicRecipeProperties.celestialTarget(observation, constellation.getSimpleName());
            MagicRecipeProperties.processTags(observation, MagicProcessTag.PRECISION, MagicProcessTag.TIMED,
                    MagicProcessTag.INFUSION, MagicProcessTag.EXPERIMENTAL, MagicProcessTag.HIDDEN_RITUAL);
            observation.buildAndRegister();

            SimpleRecipeBuilder calibration = PORecipeMaps.CELESTIAL_CALIBRATION_RECIPES.recipeBuilder()
                    .input(PollutionMetaItems.CELESTIAL_CALIBRATION_CORE)
                    .inputs(AstralNbtHelper.createDataWafer(constellation))
                    .notConsumable(PollutionMetaItems.ASTRAL_LENS_ADVANCED)
                    .notConsumable(PollutionMetaItems.CULTIVATED_CRYSTAL)
                    .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 1000))
                    .fluidInputs(PollutionMaterials.InfusedOrder.getFluid(576))
                    .outputs(AstralNbtHelper.createCalibratedCore(constellation))
                    .duration(600).EUt(VA[LuV]);
            MagicRecipeProperties.infusedFluidPerTick(calibration, 0);
            MagicRecipeProperties.astralCondition(calibration,
                    AstralCondition.night(constellation.getSimpleName(), 0.15F));
            MagicRecipeProperties.cultivatedCrystalQuality(calibration, 70);
            MagicRecipeProperties.celestialTarget(calibration, constellation.getSimpleName());
            MagicRecipeProperties.processTags(calibration, MagicProcessTag.PRECISION, MagicProcessTag.TIMED,
                    MagicProcessTag.CATALYTIC, MagicProcessTag.INFUSION, MagicProcessTag.MULTI_MAGIC,
                    MagicProcessTag.HIDDEN_RITUAL, MagicProcessTag.STRUCTURAL_CONTROL);
            calibration.buildAndRegister();
        }

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.HULL[LuV])
                .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED, 2)
                .input(MetaItems.SENSOR_LuV, 2)
                .input(MetaItems.EMITTER_LuV)
                .input(MetaItems.TOOL_DATA_STICK, 4)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LuV, 4)
                .input(ItemsAS.skyResonator)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 2000))
                .output(PollutionMetaTileEntities.CELESTIAL_OBSERVATION_ARRAY)
                .duration(800).EUt(VA[LuV])
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.HULL[LuV])
                .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED, 4)
                .input(MetaItems.FIELD_GENERATOR_LuV, 2)
                .input(MetaItems.ROBOT_ARM_LuV, 2)
                .input(PollutionMetaItems.CELESTIAL_CALIBRATION_CORE)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LuV, 4)
                .inputNBT(PollutionMetaItems.CONSTELLATION_DATA_WAFER, NBTMatcher.ANY, NBTCondition.ANY)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 4000))
                .output(PollutionMetaTileEntities.CELESTIAL_CALIBRATION_MATRIX)
                .duration(1200).EUt(VA[LuV])
                .buildAndRegister();

        // The growth array is intentionally downstream of celestial calibration:
        // it is the first industrial machine that turns crystal seed quality
        // into a persistent lens-hatch benefit.
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.HULL[LuV])
                .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED, 4)
                .input(PollutionMetaItems.CELESTIAL_CALIBRATION_CORE)
                .input(PollutionMetaItems.ATTUNED_CRYSTAL_WAFER, 2)
                .input(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_LuV, 4)
                .input(MetaItems.FIELD_GENERATOR_LuV, 2)
                .input(ItemsAS.skyResonator)
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 4000))
                .output(PollutionMetaTileEntities.CELESTIAL_CRYSTAL_GROWTH_ARRAY)
                .duration(1200).EUt(VA[LuV])
                .buildAndRegister();
    }

    private static void registerMagicFunctionalComponents() {
        // The first five carriers retain their established high-tier recipes in
        // MagicGCYMRecipes. Only the two previously missing biological/astral
        // carriers are introduced here.
        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaTileEntities.BMHPCA_COMPUTATION_COMPONENT)
                .input(MetaItems.EMITTER_LuV)
                .input(PollutionMetaItems.LIVING_MAGIC_BIOFILM)
                .input(PollutionMetaItems.ASTRAL_NEURAL_BUNDLE)
                .inputNBT(PollutionMetaItems.CONSTELLATION_DATA_WAFER, NBTMatcher.ANY, NBTCondition.ANY)
                .fluidInputs(PollutionMaterials.synthetic_computational_blood.getFluid(1000))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .output(PollutionMetaItems.NEEDLE_OF_MYSTIC_INTERPELLATION)
                .duration(800)
                .EUt(VA[ZPM])
                .buildAndRegister();

        PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
                .input(PollutionMetaTileEntities.BMHPCA_COMPUTATION_COMPONENT)
                .input(MetaItems.SENSOR_LuV)
                .input(PollutionMetaItems.LIVING_MAGIC_BIOFILM)
                .input(PollutionMetaItems.ASTRAL_NEURAL_BUNDLE)
                .input(PollutionMetaItems.BLOOD_CIRCUIT_ULTIMATE)
                .input(PollutionMetaItems.ASTRAL_LENS_ADVANCED)
                .fluidInputs(PollutionMaterials.synthetic_computational_blood.getFluid(1000))
                .fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, 500))
                .output(PollutionMetaItems.COGITO_AED)
                .duration(800)
                .EUt(VA[ZPM])
                .buildAndRegister();
        // BALL_IN_ITSELF already has its native Thaumcraft infusion route below,
        // completing all eight functional carriers without duplicating that recipe.
    }

    private static void registerThaumcraftInfusionComponents() {
        registerBallInItself(PollutionMetaItems.MAGIC_CIRCUIT_EV.getStackForm());
        registerBallInItself(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_EV.getStackForm());

        List<Object> nodeComponents = new ArrayList<>();
        nodeComponents.add(PollutionMetaItems.WHITE_RUNE.getStackForm());
        nodeComponents.add(PollutionMetaItems.BALL_IN_ITSELF.getStackForm());
        nodeComponents.add(PollutionMetaItems.FILTER_MKIII.getStackForm());
        addCopies(nodeComponents,
                OreDictUnifier.get(OrePrefix.frameGt, PollutionMaterials.Mansussteel), 4);
        addInfusionRecipe(
                "node_stabilization_frame",
                PollutionMetaItems.NODE_STABILIZATION_FRAME.getStackForm(),
                8,
                new AspectList()
                        .add(Aspect.AURA, 64)
                        .add(Aspect.MAGIC, 32)
                        .add(Aspect.ORDER, 32),
                PollutionMetaItems.PACKAGED_AURA_NODE.getStackForm(),
                nodeComponents.toArray());

        registerMagicControlAssembly(PollutionMetaItems.MAGIC_CIRCUIT_MV.getStackForm());
        registerMagicControlAssembly(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_MV.getStackForm());
    }

    private static void registerBallInItself(ItemStack circuit) {
        List<Object> components = new ArrayList<>();
        components.add(circuit);
        addCopies(components, new ItemStack(ItemsTC.visResonator), 2);
        addCopies(components, new ItemStack(ItemsTC.morphicResonator), 2);
        addCopies(components, MetaItems.FIELD_GENERATOR_EV.getStackForm(), 2);

        addInfusionRecipe(
                "ball_in_itself_" + circuitRoute(circuit),
                PollutionMetaItems.BALL_IN_ITSELF.getStackForm(),
                6,
                new AspectList()
                        .add(Aspect.MIND, 32)
                        .add(Aspect.ORDER, 24)
                        .add(Aspect.MAGIC, 24),
                PollutionMetaItems.CORE_OF_IDEA.getStackForm(),
                components.toArray());
    }

    private static void registerMagicControlAssembly(ItemStack circuit) {
        addInfusionRecipe(
                "magic_control_assembly_" + circuitRoute(circuit),
                PollutionMetaItems.MAGIC_CONTROL_ASSEMBLY.getStackForm(),
                4,
                new AspectList()
                        .add(Aspect.MECHANISM, 32)
                        .add(Aspect.MAGIC, 24)
                        .add(Aspect.AURA, 16),
                circuit,
                PollutionMetaItems.MANA_RESONANCE_COIL.getStackForm(),
                PollutionMetaItems.BLOOD_PORT.getStackForm(),
                new ItemStack(ItemsTC.visResonator),
                new ItemStack(ItemsAS.skyResonator),
                MetaItems.ROBOT_ARM_MV.getStackForm(),
                MetaItems.SENSOR_MV.getStackForm());
    }

    private static void registerTransformCore() {
        registerTransformCore(PollutionMetaItems.MAGIC_CIRCUIT_EV.getStackForm());
        registerTransformCore(PollutionMetaItems.MAGIC_CIRCUIT_BOARD_EV.getStackForm());
    }

    private static void registerTransformCore(ItemStack circuit) {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.frameGt, PollutionMaterials.Mansussteel)
                .inputs(PollutionMetaBlocks.BEAM_CORE
                        .getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_4))
                .inputs(circuit)
                .input(PollutionMetaItems.NATURAL_INFUSED_COIL)
                .input(ItemsTC.morphicResonator, 4)
                .fluidInputs(PollutionMaterials.InfusedExchange.getFluid(576))
                .output(PollutionMetaItems.TRANSFORM_ENHANCE)
                .duration(500)
                .EUt(VA[EV])
                .buildAndRegister();
    }

    private static void addInfusionRecipe(String id, ItemStack output, int instability,
                                          AspectList aspects, ItemStack central, Object... components) {
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Pollution.MODID, id),
                new InfusionRecipe(
                        "INFUSION@2",
                        output,
                        instability,
                        aspects,
                        central,
                        components));
    }

    private static void addCopies(List<Object> components, ItemStack stack, int count) {
        for (int i = 0; i < count; i++) {
            components.add(stack.copy());
        }
    }

    private static String circuitRoute(ItemStack circuit) {
        return ItemStack.areItemsEqual(circuit, PollutionMetaItems.MAGIC_CIRCUIT_MV.getStackForm())
                || ItemStack.areItemsEqual(circuit, PollutionMetaItems.MAGIC_CIRCUIT_EV.getStackForm())
                ? "enchanted_circuit"
                : "magic_circuit";
    }

    private static ItemStack withCount(ItemStack stack, int count) {
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }
}
