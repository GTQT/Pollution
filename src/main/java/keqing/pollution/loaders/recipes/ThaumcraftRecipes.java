package keqing.pollution.loaders.recipes;

import com.cleanroommc.groovyscript.compat.mods.botania.Botania;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.Pollution;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.items.PollutionMetaItems;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.material.ItemManaResource;

import static gregtech.api.unification.material.Materials.*;


public class ThaumcraftRecipes {
	public static void init() {
		catalyst();
		misc();
		solar();

	}

	private static void solar() {
		//阳光化合物制作
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.infused_light.getFluid(2304))
				.input(OrePrefix.dust, PollutionMaterials.salismundus, 4)
				.notConsumable(PollutionMetaItems.HOTCORE.getStackForm())
				.output(OrePrefix.dust, PollutionMaterials.sunnarium, 1)
				.duration(200)
				.EUt(480)
				.buildAndRegister();
		//太阳能试写，一级（风-混沌-地-火-秩序-水）
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "air_solar_1"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[0].getStackForm(),
				2,
				new AspectList().add(Aspect.ENERGY, 16).add(Aspect.AIR, 16),
				"frameGtMansussteel",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitLv",
				new ItemStack(BlocksTC.crystalAir),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleTin"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "entropy_solar_1"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[3].getStackForm(),
				2,
				new AspectList().add(Aspect.ENERGY, 16).add(Aspect.ENTROPY, 16),
				"frameGtMansussteel",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitLv",
				new ItemStack(BlocksTC.crystalEntropy),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleTin"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "earth_solar_1"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[6].getStackForm(),
				2,
				new AspectList().add(Aspect.ENERGY, 16).add(Aspect.EARTH, 16),
				"frameGtMansussteel",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitLv",
				new ItemStack(BlocksTC.crystalEarth),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleTin"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "fire_solar_1"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[9].getStackForm(),
				2,
				new AspectList().add(Aspect.ENERGY, 16).add(Aspect.FIRE, 16),
				"frameGtMansussteel",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitLv",
				new ItemStack(BlocksTC.crystalFire),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleTin"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "order_solar_1"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[12].getStackForm(),
				2,
				new AspectList().add(Aspect.ENERGY, 16).add(Aspect.ORDER, 16),
				"frameGtMansussteel",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitLv",
				new ItemStack(BlocksTC.crystalOrder),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleTin"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "water_solar_1"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[15].getStackForm(),
				2,
				new AspectList().add(Aspect.ENERGY, 16).add(Aspect.WATER, 16),
				"frameGtMansussteel",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitLv",
				new ItemStack(BlocksTC.crystalWater),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				new ItemStack(MetaItems.SENSOR_LV.getMetaItem(), 1, 232),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleTin"));
		//二级
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "air_solar_2"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[1].getStackForm(),
				4,
				new AspectList().add(Aspect.ENERGY, 32).add(Aspect.AIR, 64),
				PollutionMetaTileEntities.SOLAR_PLATE[0].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitMv",
				new ItemStack(BlocksTC.crystalAir),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleCopper"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "entropy_solar_2"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[4].getStackForm(),
				4,
				new AspectList().add(Aspect.ENERGY, 32).add(Aspect.ENTROPY, 64),
				PollutionMetaTileEntities.SOLAR_PLATE[3].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitMv",
				new ItemStack(BlocksTC.crystalEntropy),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleCopper"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "earth_solar_2"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[7].getStackForm(),
				4,
				new AspectList().add(Aspect.ENERGY, 32).add(Aspect.EARTH, 64),
				PollutionMetaTileEntities.SOLAR_PLATE[6].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitMv",
				new ItemStack(BlocksTC.crystalEarth),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleCopper"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "fire_solar_2"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[10].getStackForm(),
				4,
				new AspectList().add(Aspect.ENERGY, 32).add(Aspect.FIRE, 64),
				PollutionMetaTileEntities.SOLAR_PLATE[9].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitMv",
				new ItemStack(BlocksTC.crystalFire),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleCopper"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "order_solar_2"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[13].getStackForm(),
				4,
				new AspectList().add(Aspect.ENERGY, 32).add(Aspect.ORDER, 64),
				PollutionMetaTileEntities.SOLAR_PLATE[12].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitMv",
				new ItemStack(BlocksTC.crystalOrder),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleCopper"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "water_solar_2"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[16].getStackForm(),
				4,
				new AspectList().add(Aspect.ENERGY, 32).add(Aspect.WATER, 64),
				PollutionMetaTileEntities.SOLAR_PLATE[15].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitMv",
				new ItemStack(BlocksTC.crystalWater),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				new ItemStack(MetaItems.SENSOR_MV.getMetaItem(), 1, 233),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleCopper"));
		//三级
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "air_solar_3"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[2].getStackForm(),
				6,
				new AspectList().add(Aspect.ENERGY, 64).add(Aspect.AIR, 250),
				PollutionMetaTileEntities.SOLAR_PLATE[1].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitHv",
				new ItemStack(BlocksTC.crystalAir),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleGold"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "entropy_solar_3"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[5].getStackForm(),
				6,
				new AspectList().add(Aspect.ENERGY, 64).add(Aspect.ENTROPY, 250),
				PollutionMetaTileEntities.SOLAR_PLATE[4].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitHv",
				new ItemStack(BlocksTC.crystalEntropy),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleGold"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "earth_solar_3"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[8].getStackForm(),
				6,
				new AspectList().add(Aspect.ENERGY, 64).add(Aspect.EARTH, 250),
				PollutionMetaTileEntities.SOLAR_PLATE[7].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitHv",
				new ItemStack(BlocksTC.crystalEarth),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleGold"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "fire_solar_3"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[11].getStackForm(),
				6,
				new AspectList().add(Aspect.ENERGY, 64).add(Aspect.FIRE, 250),
				PollutionMetaTileEntities.SOLAR_PLATE[10].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitHv",
				new ItemStack(BlocksTC.crystalFire),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleGold"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "order_solar_3"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[14].getStackForm(),
				6,
				new AspectList().add(Aspect.ENERGY, 64).add(Aspect.ORDER, 250),
				PollutionMetaTileEntities.SOLAR_PLATE[13].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitHv",
				new ItemStack(BlocksTC.crystalOrder),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleGold"));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "water_solar_3"), new InfusionRecipe(
				"",
				PollutionMetaTileEntities.SOLAR_PLATE[17].getStackForm(),
				6,
				new AspectList().add(Aspect.ENERGY, 64).add(Aspect.WATER, 250),
				PollutionMetaTileEntities.SOLAR_PLATE[16].getStackForm(),
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				"dustSunnarium",
				new ItemStack(GTQTMetaItems.SOLAR_PLATE_MKI.getMetaItem(), 1, 90),
				"circuitHv",
				new ItemStack(BlocksTC.crystalWater),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				new ItemStack(MetaItems.SENSOR_HV.getMetaItem(), 1, 234),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS),
				"cableGtSingleGold"));
	}

	private static void catalyst() {
		//活性催化粗胚，搅拌机
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(Mercury.getFluid(1000))
				.input(OrePrefix.dust, Materials.Amethyst)
				.input(OrePrefix.dust, Materials.CertusQuartz)
				.input(OrePrefix.dust, Materials.Opal)
				.input(OrePrefix.dust, Materials.Salt)
				.input(OrePrefix.dust, Materials.Sulfur)
				.output(OrePrefix.dust, PollutionMaterials.roughdraft, 6)
				.duration(600)
				.EUt(120)
				.buildAndRegister();

		//魔力催化剂基底，搅拌机
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.roughdraft, 6)
				.input(OrePrefix.dust, PollutionMaterials.infused_air)
				.input(OrePrefix.dust, PollutionMaterials.infused_fire)
				.input(OrePrefix.dust, PollutionMaterials.infused_entropy)
				.output(OrePrefix.dust, PollutionMaterials.substrate, 9)
				.duration(600)
				.EUt(120)
				.buildAndRegister();

		//空白催化核心，注魔
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "blank_catalyst_core"), new InfusionRecipe(
				"",
				new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2),
				6,
				new AspectList().add(Aspect.ALCHEMY, 64).add(Aspect.EXCHANGE, 32).add(Aspect.MAGIC, 32).add(Aspect.ENERGY, 16),
				"gemValonite",
				"blockSubstrate",
				"blockSubstrate",
				"gemExquisiteAmethyst",
				"gemExquisiteOpal",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator),
				new ItemStack(ItemsTC.causalityCollapser),
				new ItemStack(BlocksTC.visBattery)));

		//冷热催化核心，注魔
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "hot_catalyst_core"), new InfusionRecipe(
				"",
				new ItemStack(PollutionMetaItems.HOTCORE.getMetaItem(), 1, 3),
				3,
				new AspectList().add(Aspect.FIRE, 128).add(Aspect.AURA, 32),
				new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2),
				"gemExquisiteRuby",
				"gemExquisiteRuby",
				"oreCrystalFire",
				"oreCrystalFire",
				new ItemStack(Items.BLAZE_POWDER),
				new ItemStack(Items.BLAZE_POWDER),
				"plateOctine",
				"plateOctine"));

		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "cold_catalyst_core"), new InfusionRecipe(
				"",
				new ItemStack(PollutionMetaItems.COLDCORE.getMetaItem(), 1, 4),
				3,
				new AspectList().add(Aspect.COLD, 128).add(Aspect.AURA, 32),
				new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2),
				"gemExquisiteSapphire",
				"gemExquisiteSapphire",
				"oreCrystalWater",
				"oreCrystalWater",
				new ItemStack(Blocks.SNOW),
				new ItemStack(Blocks.SNOW),
				new ItemStack(Blocks.ICE),
				new ItemStack(Blocks.ICE)));

		//凝聚催化核心，注魔
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "integration_catalyst_core"), new InfusionRecipe(
				"",
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				5,
				new AspectList().add(Aspect.ORDER, 128).add(Aspect.AURA, 64),
				new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2),
				"gemExquisiteDiamond",
				"gemExquisiteDiamond",
				"oreCrystalOrder",
				"oreCrystalOrder",
				new ItemStack(BlocksTC.logSilverwood),
				new ItemStack(BlocksTC.logSilverwood),
				"plateLead",
				"plateLead"));

		//分离催化核心，注魔
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "segregation_catalyst_core"), new InfusionRecipe(
				"",
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				5,
				new AspectList().add(Aspect.ENTROPY, 128).add(Aspect.AURA, 64),
				new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2),
				new ItemStack(Items.ENDER_EYE),
				new ItemStack(Items.ENDER_EYE),
				"oreCrystalEntropy",
				"oreCrystalEntropy",
				new ItemStack(ItemsTC.voidSeed),
				new ItemStack(ItemsTC.voidSeed),
				"dustGunpowder",
				"dustGunpowder"));

		//一个配方糖，在拥有一个空白核心以后就可以方便地制作更多了！
		//强制魔导催化
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.infused_water.getFluid(9216))
				.fluidInputs(PollutionMaterials.infused_fire.getFluid(9216))
				.fluidInputs(PollutionMaterials.infused_order.getFluid(9216))
				.fluidInputs(PollutionMaterials.infused_air.getFluid(9216))
				.input(OrePrefix.gem, PollutionMaterials.valonite)
				.notConsumable(new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2))
				.outputs(new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2))
				.duration(3600)
				.EUt(480)
				.buildAndRegister();

		//奇术基底
		PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Sulfur, 3)
				.input(OrePrefix.dust, Salt, 2)
				.input(OrePrefix.dust, Bismuth)
				.fluidInputs(Mercury.getFluid(4000))
				.fluidOutputs(PollutionMaterials.basic_substrate.getFluid(1440))
				.blastFurnaceTemp(2700)
				.circuitMeta(20)
				.duration(600)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Sulfur, 3)
				.input(OrePrefix.dust, PollutionMaterials.salismundus, 2)
				.input(OrePrefix.dust, Bismuth)
				.fluidInputs(Mercury.getFluid(4000))
				.fluidOutputs(PollutionMaterials.basic_substrate.getFluid(5760))
				.blastFurnaceTemp(2700)
				.circuitMeta(20)
				.duration(600)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.sulfo_plumbic_salt, 3)
				.input(OrePrefix.dust, PollutionMaterials.salismundus, 2)
				.input(OrePrefix.dust, Bismuth, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.fluidOutputs(PollutionMaterials.advanced_substrate.getFluid(1440))
				.blastFurnaceTemp(3600)
				.circuitMeta(20)
				.duration(1200)
				.EUt(1920)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.sulfo_plumbic_salt, 3)
				.input(OrePrefix.dust, PollutionMaterials.salismundus, 2)
				.input(OrePrefix.dust, PollutionMaterials.syrmorite, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.fluidOutputs(PollutionMaterials.advanced_substrate.getFluid(5760))
				.blastFurnaceTemp(3600)
				.circuitMeta(20)
				.duration(1200)
				.EUt(1920)
				.buildAndRegister();
	}

	private static void misc() {
		//杂项，包括奇珍的基本利用方法等
		//法罗钠相关的两个resonator的高效配方
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "vis_resonator_efficient"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				20,
				new AspectList().add(Aspect.ORDER, 1),
				new ItemStack(ItemsTC.visResonator, 8),
				" B ",
				"BMB",
				" B ",
				'B', "plateSteel",
				'M', "gemValonite"));

		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "morphic_resonator_efficient"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				20,
				new AspectList().add(Aspect.ORDER, 1),
				new ItemStack(ItemsTC.morphicResonator, 4),
				"ABA",
				"CMD",
				"AEA",
				'A', "paneGlass",
				'B', "plateBrass",
				'C', "toolHammer",
				'D', "toolWrench",
				'E', "plateGold",
				'M', "gemValonite"));

		//注魔 人工制作法罗钠
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "artificial_valonite"), new InfusionRecipe(
				"INFUSION@2",
				OreDictUnifier.get(OrePrefix.gem, PollutionMaterials.valonite),
				4,
				new AspectList().add(Aspect.FLUX, 16).add(Aspect.AURA, 16).add(Aspect.MAN, 16),
				"gemExquisiteDiamond",
				new ItemStack(BlocksTC.logSilverwood),
				new ItemStack(Items.DIAMOND),
				new ItemStack(BlocksTC.crystalTaint)));

		//1赛摩铜粉+1炽炎铁粉=2交错铜铁混合物
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.syrmorite)
				.input(OrePrefix.dust, PollutionMaterials.octine)
				.output(OrePrefix.dust, PollutionMaterials.thaummix, 2)
				.duration(100)
				.EUt(120)
				.buildAndRegister();

		//2交错铜铁混合物=1神秘粉+1红石粉+1铜粉
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.thaummix, 2)
				.circuitMeta(1)
				.output(OrePrefix.dust, PollutionMaterials.thaumium, 1)
				.output(OrePrefix.dust, Redstone, 1)
				.output(OrePrefix.dust, Copper, 1)
				.duration(400)
				.blastFurnaceTemp(1800)
				.EUt(120)
				.buildAndRegister();

		//人工制造痂壳晶
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "artificial_scabyst"), new InfusionRecipe(
				"INFUSION@2",
				OreDictUnifier.get(OrePrefix.gem, PollutionMaterials.scabyst),
				2,
				new AspectList().add(Aspect.TOOL, 25),
				"gemExquisiteAmethyst",
				new ItemStack(BlocksTC.logGreatwood),
				"gemAmethyst",
				new ItemStack(BlocksTC.crystalEarth)));

		//痂壳晶高效制作vis电池
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "vis_battery_efficient"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"VISBATTERY@2",
				50,
				new AspectList().add(Aspect.ORDER, 1).add(Aspect.FIRE, 1),
				new ItemStack(BlocksTC.visBattery, 4),
				"AAA",
				"AMA",
				"AAA",
				'A', "plateScabyst",
				'M', new ItemStack(ItemsTC.visResonator)));

		//人工下界星
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "artificial_star"), new InfusionRecipe(
				"INFUSION@2",
				new ItemStack(Items.NETHER_STAR),
				6,
				new AspectList().add(Aspect.DEATH, 128).add(Aspect.UNDEAD, 128).add(Aspect.MAGIC, 32).add(Aspect.AURA, 16),
				"gemvalonite",
				new ItemStack(Blocks.DIAMOND_BLOCK),
				new ItemStack(Blocks.EMERALD_BLOCK),
				new ItemStack(Blocks.NETHERRACK),
				new ItemStack(Blocks.SOUL_SAND),
				"gemExquisiteAmethyst",
				"gemExquisiteOpal",
				new ItemStack(ItemsTC.voidSeed),
				new ItemStack(BlocksTC.crystalEntropy)));

		//打粉 希望有用？
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(ModItems.manaResource)
				.output(OrePrefix.dust, PollutionMaterials.manasteel)
				.duration(10)
				.EUt(2)
				.buildAndRegister();
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(ItemsTC.ingots)
				.output(OrePrefix.dust, PollutionMaterials.thaumium)
				.duration(10)
				.EUt(2)
				.buildAndRegister();

		//黑土贤者之石合成
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "stone-1"), new InfusionRecipe(
				"INFUSION@2",
				new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150),
				12,
				new AspectList().add(Aspect.ALCHEMY, 250).add(Aspect.CRAFT, 250).add(Aspect.MAGIC, 250).add(Aspect.AURA, 64),
				new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2),
				new ItemStack(MetaItems.QUANTUM_STAR.getMetaItem(), 1, 282),
				new ItemStack(MetaItems.QUANTUM_EYE.getMetaItem(), 1, 281),
				new ItemStack(ItemsTC.voidSeed),
				new ItemStack(ItemsTC.causalityCollapser),
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				"gemValonite",
				"blockSubstrate",
				"blockTerrasteel",
				PollutionMetaTileEntities.ESSENCE_COLLECTOR.getStackForm()));
	}
}
