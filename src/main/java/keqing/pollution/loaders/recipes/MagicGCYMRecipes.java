package keqing.pollution.loaders.recipes;

import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.*;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtechfoodoption.machines.GTFOTileEntities;
import keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.metatileentities.GTQTMetaTileEntities;
import keqing.pollution.Pollution;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.*;
import keqing.pollution.common.items.PollutionMetaItems;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.init.Blocks;
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
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import static gregtech.api.GTValues.*;
import static gregtech.api.recipes.RecipeMaps.ASSEMBLY_LINE_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.api.unification.ore.OrePrefix.ingotHot;
import static keqing.gtqtcore.api.unification.GTQTMaterials.Ichorium;
import static keqing.gtqtcore.api.unification.GTQTMaterials.Orichalcum;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_GREENHOUSE_RECIPES;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.common.items.PollutionMetaItems.*;

public class MagicGCYMRecipes {
	public static void init() {
		materials();
	}

	private static void materials() {
		//六要素的流体提取机配方
		GTQTcoreRecipeMaps.FLUID_EXTRACTOR_RECIPES.recipeBuilder()
				.input(dust, infused_air, 1)
				.fluidOutputs(infused_air.getFluid(144))
				.duration(200)
				.EUt(30)
				.buildAndRegister();
		GTQTcoreRecipeMaps.FLUID_EXTRACTOR_RECIPES.recipeBuilder()
				.input(dust, infused_fire, 1)
				.fluidOutputs(infused_fire.getFluid(144))
				.duration(200)
				.EUt(30)
				.buildAndRegister();
		GTQTcoreRecipeMaps.FLUID_EXTRACTOR_RECIPES.recipeBuilder()
				.input(dust, infused_water, 1)
				.fluidOutputs(infused_water.getFluid(144))
				.duration(200)
				.EUt(30)
				.buildAndRegister();
		GTQTcoreRecipeMaps.FLUID_EXTRACTOR_RECIPES.recipeBuilder()
				.input(dust, infused_earth, 1)
				.fluidOutputs(infused_earth.getFluid(144))
				.duration(200)
				.EUt(30)
				.buildAndRegister();
		GTQTcoreRecipeMaps.FLUID_EXTRACTOR_RECIPES.recipeBuilder()
				.input(dust, infused_order, 1)
				.fluidOutputs(infused_order.getFluid(144))
				.duration(200)
				.EUt(30)
				.buildAndRegister();
		GTQTcoreRecipeMaps.FLUID_EXTRACTOR_RECIPES.recipeBuilder()
				.input(dust, infused_entropy, 1)
				.fluidOutputs(infused_entropy.getFluid(144))
				.duration(200)
				.EUt(30)
				.buildAndRegister();

		//要素粉
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(BlocksTC.crystalAir)
				.output(dust, infused_air)
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(BlocksTC.crystalFire)
				.output(dust, infused_fire)
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(BlocksTC.crystalWater)
				.output(dust, infused_water)
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(BlocksTC.crystalEarth)
				.output(dust, infused_earth)
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(BlocksTC.crystalOrder)
				.output(dust, infused_order)
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(BlocksTC.crystalEntropy)
				.output(dust, infused_entropy)
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
				.fluidInputs(infused_air.getFluid(144))
				.output(BlocksTC.crystalAir)
				.duration(200)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
				.fluidInputs(infused_fire.getFluid(144))
				.output(BlocksTC.crystalFire)
				.duration(200)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
				.fluidInputs(infused_water.getFluid(144))
				.output(BlocksTC.crystalWater)
				.duration(200)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
				.fluidInputs(infused_earth.getFluid(144))
				.output(BlocksTC.crystalEarth)
				.duration(200)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
				.fluidInputs(infused_order.getFluid(144))
				.output(BlocksTC.crystalOrder)
				.duration(200)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
				.fluidInputs(infused_entropy.getFluid(144))
				.output(BlocksTC.crystalEntropy)
				.duration(200)
				.EUt(120)
				.buildAndRegister();

		//这里是六个基础外壳材料的搅拌机配方
		//风要素-律动钛
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, Bauxite, 2)
				.input(dust, Aluminium, 1)
				.input(dust, Manganese, 1)
				.input(dust, PollutionMaterials.infused_air, 5)
				.output(dust, PollutionMaterials.aertitanium, 9)
				.circuitMeta(20)
				.duration(900)
				.EUt(120)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(dust, Bauxite, 2)
				.input(dust, Aluminium, 1)
				.input(dust, Manganese, 1)
				.input(dust, PollutionMaterials.infused_air, 5)
				.fluidOutputs(PollutionMaterials.aertitanium.getFluid(9 * 144))
				.circuitMeta(20)
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//火要素-残日钢
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, Steel, 2)
				.input(dust, Magnesium, 1)
				.input(dust, Lithium, 1)
				.input(dust, PollutionMaterials.infused_fire, 5)
				.output(dust, PollutionMaterials.ignissteel, 9)
				.circuitMeta(20)
				.duration(900)
				.EUt(120)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(dust, Steel, 2)
				.input(dust, Magnesium, 1)
				.input(dust, Lithium, 1)
				.input(dust, PollutionMaterials.infused_fire, 5)
				.fluidOutputs(PollutionMaterials.ignissteel.getFluid(9 * 144))
				.circuitMeta(20)
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//水要素-捩花银
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, Silver, 2)
				.input(dust, Tin, 1)
				.fluidInputs(Mercury.getFluid(1000))
				.input(dust, PollutionMaterials.infused_water, 5)
				.output(dust, PollutionMaterials.aquasilver, 9)
				.circuitMeta(20)
				.duration(900)
				.EUt(120)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(dust, Silver, 2)
				.input(dust, Tin, 1)
				.fluidInputs(Mercury.getFluid(1000))
				.input(dust, PollutionMaterials.infused_water, 5)
				.fluidOutputs(PollutionMaterials.aquasilver.getFluid(9 * 144))
				.circuitMeta(20)
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//地要素-定坤铜
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, Copper, 2)
				.input(dust, Boron, 1)
				.input(dust, Carbon, 1)
				.input(dust, PollutionMaterials.infused_earth, 5)
				.output(dust, PollutionMaterials.terracopper, 9)
				.circuitMeta(20)
				.duration(900)
				.EUt(120)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(dust, Copper, 2)
				.input(dust, Boron, 1)
				.input(dust, Carbon, 1)
				.input(dust, PollutionMaterials.infused_earth, 5)
				.fluidOutputs(PollutionMaterials.terracopper.getFluid(9 * 144))
				.circuitMeta(20)
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//秩序要素-司辰铅
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, Lead, 2)
				.input(dust, Silicon, 1)
				.input(dust, Gold, 1)
				.input(dust, PollutionMaterials.infused_order, 5)
				.output(dust, PollutionMaterials.ordolead, 9)
				.circuitMeta(20)
				.duration(900)
				.EUt(120)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(dust, Lead, 2)
				.input(dust, Silicon, 1)
				.input(dust, Gold, 1)
				.input(dust, PollutionMaterials.infused_order, 5)
				.fluidOutputs(PollutionMaterials.ordolead.getFluid(9 * 144))
				.circuitMeta(20)
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//混沌要素-无极铝
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, Aluminium, 2)
				.fluidInputs(Fluorine.getFluid(1000))
				.input(dust, Thorium, 1)
				.input(dust, PollutionMaterials.infused_entropy, 5)
				.output(dust, perditioaluminium, 9)
				.duration(900)
				.EUt(120)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(dust, Aluminium, 2)
				.fluidInputs(Fluorine.getFluid(1000))
				.input(dust, Thorium, 1)
				.input(dust, PollutionMaterials.infused_entropy, 5)
				.fluidOutputs(PollutionMaterials.perditioaluminium.getFluid(9 * 144))
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//高炉2700°烧六种合金捏
		//高速保护液配方
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, aertitanium, 1)
				.fluidInputs(infused_air.getFluid(144))
				.output(ingotHot, aertitanium, 1)
				.circuitMeta(11)
				.duration(500)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, ignissteel, 1)
				.fluidInputs(infused_fire.getFluid(144))
				.output(ingotHot, ignissteel, 1)
				.circuitMeta(11)
				.duration(500)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, aquasilver, 1)
				.fluidInputs(infused_water.getFluid(144))
				.output(ingotHot, aquasilver, 1)
				.circuitMeta(11)
				.duration(500)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, terracopper, 1)
				.fluidInputs(infused_earth.getFluid(144))
				.output(ingotHot, terracopper, 1)
				.circuitMeta(11)
				.duration(500)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, ordolead, 1)
				.fluidInputs(infused_order.getFluid(144))
				.output(ingotHot, ordolead, 1)
				.circuitMeta(11)
				.duration(500)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, perditioaluminium, 1)
				.fluidInputs(infused_entropy.getFluid(144))
				.output(ingotHot, perditioaluminium, 1)
				.circuitMeta(11)
				.duration(500)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//魔力钢 神秘锭简化配方
		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(dust, infused_air, 1)
				.input(dust, infused_fire, 1)
				.input(dust, infused_water, 1)
				.input(dust, infused_earth, 1)
				.input(dust, infused_order, 1)
				.input(dust, infused_entropy, 1)
				.input(dust, Iron, 4)
				.fluidOutputs(manasteel.getFluid(576))
				.circuitMeta(1)
				.duration(400)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(dust, Iron, 1)
				.input(dust, infused_earth, 10)
				.input(dust, infused_fire, 5)
				.input(dust, infused_air, 5)
				.fluidOutputs(thaumium.getFluid(3024))
				.circuitMeta(2)
				.duration(1200)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();

		//这里是六个外壳的制作配方
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, PollutionMaterials.aertitanium, 6)
				.input(OrePrefix.frameGt, PollutionMaterials.aertitanium, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_AIR))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, PollutionMaterials.ignissteel, 6)
				.input(OrePrefix.frameGt, PollutionMaterials.ignissteel, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, PollutionMaterials.aquasilver, 6)
				.input(OrePrefix.frameGt, PollutionMaterials.aquasilver, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, PollutionMaterials.terracopper, 6)
				.input(OrePrefix.frameGt, PollutionMaterials.terracopper, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_EARTH))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, PollutionMaterials.ordolead, 6)
				.input(OrePrefix.frameGt, PollutionMaterials.ordolead, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_ORDER))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, perditioaluminium, 6)
				.input(OrePrefix.frameGt, mansussteel, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.VOID_PRISM))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		//加一个空白的配方
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, ordolead, 6)
				.input(OrePrefix.frameGt, mansussteel, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		//另一个虚空的配方
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, perditioaluminium, 6)
				.input(OrePrefix.frameGt, perditioaluminium, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		//冷冻的配方
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, aquasilver, 6)
				.input(OrePrefix.frameGt, mansussteel, 1)
				.outputs(PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_COLD))
				.circuitMeta(6)
				.duration(300)
				.EUt(120)
				.buildAndRegister();
		//转换矩阵
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "infused_exchange"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.INFUSED_EXCHANGE.getStackForm(),
				2,
				new AspectList().add(Aspect.EXCHANGE, 16).add(Aspect.MOTION, 16).add(Aspect.WATER, 16),
				GTQTMetaTileEntities.FLUID_EXTRACTOR[MV].getStackForm(),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"oreCrystalAir",
				"oreCrystalWater",
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.VOID_PRISM)));
		//三个小机器
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "turbine_1"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				50,
				new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1),
				PollutionMetaTileEntities.MAGIC_TURBINE[0].getStackForm(),
				"CEC",
				"DBD",
				"AFA",
				'A', "gearMansussteel",
				'B', MetaTileEntities.HULL[1].getStackForm(),
				'C', new ItemStack(MetaItems.ELECTRIC_PISTON_LV.getMetaItem(), 1, 172),
				'D', new ItemStack(MetaItems.ELECTRIC_MOTOR_LV.getMetaItem(), 1, 127),
				'E', "circuitLv",
				'F', "cableGtSingleTin"));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "turbine_2"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				100,
				new AspectList().add(Aspect.FIRE, 2).add(Aspect.ORDER, 2),
				PollutionMetaTileEntities.MAGIC_TURBINE[1].getStackForm(),
				"CEC",
				"DBD",
				"AFA",
				'A', "gearMansussteel",
				'B', MetaTileEntities.HULL[2].getStackForm(),
				'C', new ItemStack(MetaItems.ELECTRIC_PISTON_MV.getMetaItem(), 1, 173),
				'D', new ItemStack(MetaItems.ELECTRIC_MOTOR_MV.getMetaItem(), 1, 128),
				'E', "circuitMv",
				'F', "cableGtSingleCopper"));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "turbine_3"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				150,
				new AspectList().add(Aspect.FIRE, 4).add(Aspect.ORDER, 4),
				PollutionMetaTileEntities.MAGIC_TURBINE[2].getStackForm(),
				"CEC",
				"DBD",
				"AFA",
				'A', "gearMansussteel",
				'B', MetaTileEntities.HULL[3].getStackForm(),
				'C', new ItemStack(MetaItems.ELECTRIC_PISTON_HV.getMetaItem(), 1, 174),
				'D', new ItemStack(MetaItems.ELECTRIC_MOTOR_HV.getMetaItem(), 1, 129),
				'E', "circuitHv",
				'F', "cableGtSingleGold"));
		//魔法燃气
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "large_magic_turbine"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.LARGE_MAGIC_TURBINE.getStackForm(),
				4,
				new AspectList().add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128).add(Aspect.ENERGY, 64).add(Aspect.MOTION, 16),
				new ItemStack(SEGREGATECORE.getMetaItem(), 1, 6),
				"gemValonite",
				"circuitHv",
				"circuitHv",
				"circuitHv",
				"circuitHv",
				"frameGtMansussteel",
				"oreCrystalAir",
				"oreCrystalOrder",
				"oreCrystalFire",
				PollutionMetaTileEntities.MAGIC_TURBINE[2].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT)));

		//魔法炖屎
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_alloy_blast"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_ALLOY_BLAST.getStackForm(),
				4,
				new AspectList().add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128).add(Aspect.FIRE, 64),
				new ItemStack(HOTCORE.getMetaItem(), 1, 3),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalFire",
				"oreCrystalOrder",
				"oreCrystalEarth",
				MetaTileEntities.ALLOY_SMELTER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT)));

		//工业注魔主方块
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "industrial_infusion"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.INDUSTRIAL_INFUSION.getStackForm(),
				12,
				new AspectList().add(Aspect.MAGIC, 250).add(Aspect.AURA, 250).add(Aspect.VOID, 64).add(Aspect.MIND, 128).add(Aspect.CRAFT, 128),
				new ItemStack(BlocksTC.infusionMatrix),
				"blockValonite",
				new ItemStack(BlocksTC.matrixCost),
				new ItemStack(BlocksTC.matrixSpeed),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				new ItemStack(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 1, 204),
				new ItemStack(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 1, 204),
				new ItemStack(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 1, 204),
				new ItemStack(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 1, 204),
		        new ItemStack(ItemsTC.causalityCollapser),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_V),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.COMPOSE_IV)));

		//节点机、聚变主方块
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "node_producer"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.NODE_PRODUCER.getStackForm(),
				8,
				new AspectList().add(Aspect.MAGIC, 128).add(Aspect.MECHANISM, 250).add(Aspect.AURA, 250).add(Aspect.VOID, 250),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_I),
				"gemValonite",
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				new ItemStack(BlocksTC.logSilverwood),
				new ItemStack(BlocksTC.crystalAir),
				new ItemStack(BlocksTC.crystalFire),
				new ItemStack(BlocksTC.crystalEarth),
				new ItemStack(BlocksTC.crystalWater),
				new ItemStack(BlocksTC.crystalOrder),
				new ItemStack(BlocksTC.crystalEntropy),
				"frameGtMansussteel",
				new ItemStack(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 1, 204),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.VOID_PRISM)));

		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_fusion_reactor"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_FUSION_REACTOR.getStackForm(),
				8,
				new AspectList().add(Aspect.ENERGY, 250).add(Aspect.ENTROPY, 250).add(Aspect.MAGIC, 128).add(Aspect.VOID, 64),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_I),
				"gemValonite",
				new ItemStack(BlocksTC.crystalAir),
				new ItemStack(BlocksTC.crystalEntropy),
				new ItemStack(ItemsTC.causalityCollapser),
				"blockVoid",
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"frameGtMansussteel",
				"blockUranium235",
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.VOID_PRISM)));
		//节点发电机
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "large_node_generator"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.LARGE_NODE_GENERATOR.getStackForm(),
				8,
				new AspectList().add(Aspect.MAGIC, 128).add(Aspect.MECHANISM, 250).add(Aspect.AURA, 250).add(Aspect.MOTION, 64),
				PollutionMetaTileEntities.AURA_GENERATORS[4].getStackForm(),
				"gemValonite",
				"gemScabyst",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_I),
				new ItemStack(BlocksTC.arcaneWorkbenchCharger),
				new ItemStack(BlocksTC.rechargePedestal),
				new ItemStack(BlocksTC.visBattery),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"frameGtMansussteel",
				"blockKeqinggold",
				"blockHyperdimensionalSilver",
				new ItemStack(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.VOID_PRISM)));
		//聚灵阵
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "essence_collector"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.ESSENCE_COLLECTOR.getStackForm(),
				7,
				new AspectList().add(Aspect.MAGIC, 250).add(Aspect.MECHANISM, 250).add(Aspect.AURA, 250),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				"gemValonite",
				new ItemStack(BlocksTC.crystalAir),
				new ItemStack(BlocksTC.crystalFire),
				new ItemStack(BlocksTC.crystalEarth),
				new ItemStack(BlocksTC.crystalWater),
				new ItemStack(BlocksTC.crystalOrder),
				new ItemStack(BlocksTC.crystalEntropy),
				"frameGtMansussteel",
				new ItemStack(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 1, 204),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_4)));
		//温室
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_greenhouse"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_GREEN_HOUSE.getStackForm(),
				5,
				new AspectList().add(Aspect.PLANT, 250).add(Aspect.MAGIC, 128).add(Aspect.MECHANISM, 128),
				GTFOTileEntities.GREENHOUSE.getStackForm(),
				"gemValonite",
				"circuitHv",
				"circuitHv",
				"circuitHv",
				"circuitHv",
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_3)));
		//温室刻晴要的几个配方
		MAGIC_GREENHOUSE_RECIPES.recipeBuilder()
				.input(BlocksTC.saplingGreatwood)
				.fluidInputs(infused_earth.getFluid(144))
				.output(BlocksTC.logGreatwood, 8)
				.output(BlocksTC.saplingGreatwood, 2)
				.duration(200)
				.circuitMeta(1)
				.EUt(120)
				.buildAndRegister();
		MAGIC_GREENHOUSE_RECIPES.recipeBuilder()
				.input(BlocksTC.saplingGreatwood)
				.input(MetaItems.FERTILIZER.getMetaItem(), 1, 1001)
				.fluidInputs(infused_earth.getFluid(144))
				.output(BlocksTC.logGreatwood, 16)
				.output(BlocksTC.saplingGreatwood, 4)
				.output(BlocksTC.leafGreatwood, 16)
				.duration(200)
				.circuitMeta(2)
				.EUt(120)
				.buildAndRegister();
		MAGIC_GREENHOUSE_RECIPES.recipeBuilder()
				.input(BlocksTC.saplingSilverwood)
				.fluidInputs(infused_earth.getFluid(288))
				.output(BlocksTC.logSilverwood, 4)
				.output(BlocksTC.saplingSilverwood, 1)
				.duration(400)
				.circuitMeta(1)
				.EUt(480)
				.buildAndRegister();
		MAGIC_GREENHOUSE_RECIPES.recipeBuilder()
				.input(BlocksTC.saplingSilverwood)
				.input(MetaItems.FERTILIZER.getMetaItem(), 1, 1001)
				.fluidInputs(infused_earth.getFluid(288))
				.output(BlocksTC.logSilverwood, 8)
				.output(BlocksTC.saplingSilverwood, 2)
				.output(BlocksTC.leafSilverwood, 8)
				.duration(400)
				.circuitMeta(2)
				.EUt(480)
				.buildAndRegister();


		//魔导电池配方（试作）
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_battery"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_BATTERY.getStackForm(),
				7,
				new AspectList().add(Aspect.ENERGY, 250).add(Aspect.MAGIC, 128).add(Aspect.MECHANISM, 128),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_1),
				"gemValonite",
				"batteryMv",
				"batteryMv",
				"batteryMv",
				new ItemStack(MetaItems.FIELD_GENERATOR_MV.getMetaItem(), 1, 203),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_4)));
		//这里是主方块的注魔配方
		//配方暂定造价：主注魔材料催化剂，次要材料mv电路板*4+对应要素的水晶+mv级别的对应小机器一台+一个对应外壳+法罗钠晶体一个
		//蒸馏二合一
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_distillery"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_DISTILLERY.getStackForm(),
				5,
				new AspectList().add(Aspect.AIR, 125).add(Aspect.WATER, 125).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalAir",
				"oreCrystalWater",
				GTQTMetaTileEntities.DISTILLATION_TOWER.getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_COLD)));
		//酿造三合一
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_brewery"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_BREWERY.getStackForm(),
				5,
				new AspectList().add(Aspect.AIR, 125).add(Aspect.WATER, 125).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalAir",
				"oreCrystalWater",
				MetaTileEntities.BREWERY[MV].getStackForm(),
				MetaTileEntities.FLUID_HEATER[MV].getStackForm(),
				MetaTileEntities.FERMENTER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_COLD)));
		//化反
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_chemical_reactor"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_CHEMICAL_REACTOR.getStackForm(),
				5,
				new AspectList().add(Aspect.ALCHEMY, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				MetaTileEntities.LARGE_CHEMICAL_REACTOR.getStackForm(),
				"gemValonite",
				new ItemStack(HOTCORE.getMetaItem(), 1, 3),
				new ItemStack(PollutionMetaItems.COLDCORE.getMetaItem(), 1, 4),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"oreCrystalWater",
				MetaBlocks.BOILER_CASING.getItemVariant(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER)));
		//高压
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_autoclave"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_AUTOCLAVE.getStackForm(),
				5,
				new AspectList().add(Aspect.AIR, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalAir",
				"oreCrystalWater",
				MetaTileEntities.AUTOCLAVE[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_AIR)));
		//压模
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_extruder"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_EXTRUDER.getStackForm(),
				5,
				new AspectList().add(Aspect.TOOL, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalOrder",
				MetaTileEntities.EXTRUDER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_ORDER)));
		//卷板
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_bender"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_BENDER.getStackForm(),
				5,
				new AspectList().add(Aspect.METAL, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalOrder",
				"oreCrystalEarth",
				MetaTileEntities.BENDER[MV].getStackForm(),
				MetaTileEntities.COMPRESSOR[MV].getStackForm(),
				MetaTileEntities.FORMING_PRESS[MV].getStackForm(),
				MetaTileEntities.FORGE_HAMMER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_ORDER)));
		//固化三合一
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_solidifier"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_SOLIDIFIER.getStackForm(),
				5,
				new AspectList().add(Aspect.EXCHANGE, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.COLDCORE.getMetaItem(), 1, 4),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalOrder",
				"oreCrystalEntropy",
				MetaTileEntities.FLUID_SOLIDIFIER[MV].getStackForm(),
				MetaTileEntities.CANNER[MV].getStackForm(),
				MetaTileEntities.EXTRACTOR[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_ORDER)));
		//轧线
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wiremill"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_WIREMILL.getStackForm(),
				5,
				new AspectList().add(Aspect.TOOL, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalOrder",
				"oreCrystalEarth",
				"oreCrystalFire",
				MetaTileEntities.WIREMILL[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_ORDER)));
		//筛选
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_sifter"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_SIFTER.getStackForm(),
				5,
				new AspectList().add(Aspect.CRYSTAL, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalEarth",
				"oreCrystalAir",
				MetaTileEntities.SIFTER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_EARTH),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_AIR)));
		//切割
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_cutter"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_CUTTER.getStackForm(),
				5,
				new AspectList().add(Aspect.SOUL, 125).add(Aspect.ENTROPY, 125).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(HOTCORE.getMetaItem(), 1, 3),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalEntropy",
				"oreCrystalEarth",
				"oreCrystalWater",
				MetaTileEntities.CUTTER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT)));
		//离心
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_centrifuge"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_CENTRIFUGE.getStackForm(),
				5,
				new AspectList().add(Aspect.AIR, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalAir",
				MetaTileEntities.CENTRIFUGE[MV].getStackForm(),
				MetaTileEntities.THERMAL_CENTRIFUGE[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_AIR)));
		//高炉
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_blast"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_ELECTRIC_BLAST_FURNACE.getStackForm(),
				5,
				new AspectList().add(Aspect.FIRE, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(HOTCORE.getMetaItem(), 1, 3),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalFire",
				MetaTileEntities.ELECTRIC_FURNACE[MV].getStackForm(),
				MetaTileEntities.ALLOY_SMELTER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT)));
		//洗矿机
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_chemical_bath"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_CHEMICAL_BATH.getStackForm(),
				5,
				new AspectList().add(Aspect.WATER, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.COLDCORE.getMetaItem(), 1, 4),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalWater",
				MetaTileEntities.CHEMICAL_BATH[MV].getStackForm(),
				MetaTileEntities.ORE_WASHER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER)));
		//研磨机
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_macerator"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_MACERATOR.getStackForm(),
				5,
				new AspectList().add(Aspect.EARTH, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalEarth",
				MetaTileEntities.MACERATOR[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_EARTH)));
		//电解机
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_electrolyzer"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_ELECTROLYZER.getStackForm(),
				5,
				new AspectList().add(Aspect.ORDER, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalOrder",
				MetaTileEntities.ELECTROLYZER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_ORDER)));
		//搅拌机
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_mixer"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_MIXER.getStackForm(),
				5,
				new AspectList().add(Aspect.ENTROPY, 250).add(Aspect.MAGIC, 64).add(Aspect.MECHANISM, 128),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				"gemValonite",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"circuitMv",
				"oreCrystalEntropy",
				MetaTileEntities.MIXER[MV].getStackForm(),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.VOID_PRISM)));
		//这里是魔力钢、漫宿钢等的配方
		//不纯魔力搅拌
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, PollutionMaterials.infused_order)
				.input(dust, PollutionMaterials.infused_entropy)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(48))
				.circuitMeta(1)
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, PollutionMaterials.infused_air)
				.input(dust, PollutionMaterials.infused_earth)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(48))
				.circuitMeta(1)
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, PollutionMaterials.infused_fire)
				.input(dust, PollutionMaterials.infused_water)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(48))
				.circuitMeta(1)
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		//不纯魔力+铁粉 高炉烧制魔力钢锭
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, Iron)
				.fluidInputs(PollutionMaterials.impuremana.getFluid(144))
				.output(OrePrefix.ingot, PollutionMaterials.manasteel)
				.blastFurnaceTemp(1800)
				.duration(400)
				.EUt(120)
				.buildAndRegister();
		//世界盐 搅拌机配方
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, PollutionMaterials.infused_air)
				.input(dust, PollutionMaterials.infused_fire)
				.input(dust, PollutionMaterials.infused_water)
				.input(dust, PollutionMaterials.infused_earth)
				.input(dust, PollutionMaterials.infused_order)
				.input(dust, PollutionMaterials.infused_entropy)
				.fluidInputs(Redstone.getFluid(288))
				.output(dust, PollutionMaterials.salismundus, 6)
				.duration(600)
				.EUt(120)
				.buildAndRegister();
		//3魔力钢粉+2神秘粉+1世界盐粉=6漫宿钢粉
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, PollutionMaterials.manasteel, 3)
				.input(dust, PollutionMaterials.thaumium, 2)
				.input(dust, PollutionMaterials.salismundus)
				.output(dust, PollutionMaterials.mansussteel, 6)
				.duration(600)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(dust, PollutionMaterials.manasteel, 3)
				.input(dust, PollutionMaterials.thaumium, 2)
				.input(ItemsTC.salisMundus)
				.output(dust, PollutionMaterials.mansussteel, 6)
				.duration(600)
				.EUt(120)
				.buildAndRegister();
		//这里是五个核心的配方
		//魔导强磁阵法核心
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "beam_core_0"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_0),
				6,
				new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC, 8).add(Aspect.METAL, 16),
				"blockSteelMagnetic",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"frameGtMansussteel",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		//魔导内爆阵法核心
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "beam_core_1"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_1),
				6,
				new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC, 8).add(Aspect.ENTROPY, 16),
				new ItemStack(Blocks.TNT),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"frameGtMansussteel",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		//魔导导压阵法核心
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "beam_core_2"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_2),
				6,
				new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC, 8).add(Aspect.SENSES, 16),
				new ItemStack(Blocks.SOUL_SAND),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"frameGtMansussteel",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		//魔导束流阵法核心
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "beam_core_3"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_3),
				6,
				new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC, 8).add(Aspect.WATER, 16),
				new ItemStack(BlocksTC.crystalWater),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"frameGtMansussteel",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		//魔导聚能阵法核心
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "beam_core_4"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_4),
				6,
				new AspectList().add(Aspect.MECHANISM, 16).add(Aspect.MAGIC, 8).add(Aspect.ENERGY, 16),
				new ItemStack(BlocksTC.visBattery),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"frameGtMansussteel",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		//线圈（八个）
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wirecoil-1"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.WIRE_COIL.getItemVariant(POCoilBlock.WireCoilType.COIL_LEVEL_1),
				4,
				new AspectList().add(Aspect.FIRE, 4).add(Aspect.MAGIC, 4),
				MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.CUPRONICKEL),
				"plateMansussteel",
				"plateMansussteel",
				"plateIgnissteel",
				"plateIgnissteel",
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wirecoil-2"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.WIRE_COIL.getItemVariant(POCoilBlock.WireCoilType.COIL_LEVEL_2),
				4,
				new AspectList().add(Aspect.FIRE, 4).add(Aspect.MAGIC, 4),
				MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.KANTHAL),
				"plateMansussteel",
				"plateMansussteel",
				"plateIgnissteel",
				"plateIgnissteel",
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wirecoil-3"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.WIRE_COIL.getItemVariant(POCoilBlock.WireCoilType.COIL_LEVEL_3),
				4,
				new AspectList().add(Aspect.FIRE, 8).add(Aspect.MAGIC, 8),
				MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.NICHROME),
				"plateMansussteel",
				"plateMansussteel",
				"plateIgnissteel",
				"plateIgnissteel",
				new ItemStack(ItemsTC.morphicResonator),
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wirecoil-4"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.WIRE_COIL.getItemVariant(POCoilBlock.WireCoilType.COIL_LEVEL_4),
				4,
				new AspectList().add(Aspect.FIRE, 8).add(Aspect.MAGIC, 8),
				MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.RTM_ALLOY),
				"plateMansussteel",
				"plateMansussteel",
				"plateIgnissteel",
				"plateIgnissteel",
				new ItemStack(ItemsTC.morphicResonator),
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wirecoil-5"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.WIRE_COIL.getItemVariant(POCoilBlock.WireCoilType.COIL_LEVEL_5),
				4,
				new AspectList().add(Aspect.FIRE, 16).add(Aspect.MAGIC, 16),
				MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.HSS_G),
				"plateMansussteel",
				"plateMansussteel",
				"plateIgnissteel",
				"plateIgnissteel",
				"blockSubstrate",
				new ItemStack(ItemsTC.morphicResonator),
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wirecoil-6"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.WIRE_COIL.getItemVariant(POCoilBlock.WireCoilType.COIL_LEVEL_6),
				4,
				new AspectList().add(Aspect.FIRE, 16).add(Aspect.MAGIC, 16),
				MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.NAQUADAH),
				"plateMansussteel",
				"plateMansussteel",
				"plateIgnissteel",
				"plateIgnissteel",
				"blockSubstrate",
				new ItemStack(ItemsTC.morphicResonator),
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wirecoil-7"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.WIRE_COIL.getItemVariant(POCoilBlock.WireCoilType.COIL_LEVEL_7),
				4,
				new AspectList().add(Aspect.FIRE, 32).add(Aspect.MAGIC, 32),
				MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.TRINIUM),
				"plateMansussteel",
				"plateMansussteel",
				"plateIgnissteel",
				"plateIgnissteel",
				"blockSubstrate",
				new ItemStack(BlocksTC.visBattery),
				new ItemStack(ItemsTC.morphicResonator),
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_wirecoil-8"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.WIRE_COIL.getItemVariant(POCoilBlock.WireCoilType.COIL_LEVEL_8),
				4,
				new AspectList().add(Aspect.FIRE, 32).add(Aspect.MAGIC, 32),
				MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.TRITANIUM),
				"plateMansussteel",
				"plateMansussteel",
				"plateIgnissteel",
				"plateIgnissteel",
				"blockSubstrate",
				new ItemStack(BlocksTC.visBattery),
				new ItemStack(ItemsTC.morphicResonator),
				new ItemStack(ItemsTC.visResonator)));

		//玻璃
		//a型玻璃
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "glass-a"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.FIRE, 1),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS, 4),
				"ADA",
				"BCB",
				"ADA",
				'A', "blockGlass",
				'B', new ItemStack(ItemsTC.visResonator),
				'C', new ItemStack(ItemsTC.morphicResonator),
				'D', "plateMansussteel"));
		//b型玻璃
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "glass-b"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.AIR, 1),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.BAMINATED_GLASS, 4),
				"ADA",
				"BCB",
				"ADA",
				'A', "blockGlass",
				'B', new ItemStack(ItemsTC.visResonator),
				'C', new ItemStack(ItemsTC.morphicResonator),
				'D', "plateMansussteel"));
		//c型玻璃
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "glass-c"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.WATER, 1),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.CAMINATED_GLASS, 4),
				"ADA",
				"BCB",
				"ADA",
				'A', "blockGlass",
				'B', new ItemStack(ItemsTC.visResonator),
				'C', new ItemStack(ItemsTC.morphicResonator),
				'D', "plateMansussteel"));
		//d型玻璃
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "glass-d"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.ENTROPY, 1),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.DAMINATED_GLASS, 4),
				"ADA",
				"BCB",
				"ADA",
				'A', "blockGlass",
				'B', new ItemStack(ItemsTC.visResonator),
				'C', new ItemStack(ItemsTC.morphicResonator),
				'D', "plateMansussteel"));
		//l型玻璃
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "glass-l"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.ORDER, 1),
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS, 4),
				"ADA",
				"BCB",
				"ADA",
				'A', "blockGlass",
				'B', new ItemStack(ItemsTC.visResonator),
				'C', new ItemStack(ItemsTC.morphicResonator),
				'D', "plateMansussteel"));
		//灵气仓（只到iv）
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "vis_hatch-1"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.VIS_HATCH[ULV].getStackForm(),
				6,
				new AspectList().add(Aspect.AURA, 128).add(Aspect.MAGIC, 32).add(Aspect.DESIRE, 64),
				MetaTileEntities.FLUID_IMPORT_HATCH[LV].getStackForm(),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				new ItemStack(MetaItems.FIELD_GENERATOR_LV.getMetaItem(), 1, 202), //lv从202开始
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "vis_hatch-2"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.VIS_HATCH[LV].getStackForm(),
				6,
				new AspectList().add(Aspect.AURA, 128).add(Aspect.MAGIC, 32).add(Aspect.DESIRE, 64),
				MetaTileEntities.FLUID_IMPORT_HATCH[MV].getStackForm(),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				new ItemStack(MetaItems.FIELD_GENERATOR_MV.getMetaItem(), 1, 203),
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "vis_hatch-3"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.VIS_HATCH[MV].getStackForm(),
				6,
				new AspectList().add(Aspect.AURA, 128).add(Aspect.MAGIC, 32).add(Aspect.DESIRE, 64),
				MetaTileEntities.FLUID_IMPORT_HATCH[HV].getStackForm(),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				new ItemStack(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 1, 204),
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "vis_hatch-4"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.VIS_HATCH[HV].getStackForm(),
				6,
				new AspectList().add(Aspect.AURA, 128).add(Aspect.MAGIC, 32).add(Aspect.DESIRE, 64),
				MetaTileEntities.FLUID_IMPORT_HATCH[EV].getStackForm(),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "vis_hatch-5"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.VIS_HATCH[EV].getStackForm(),
				6,
				new AspectList().add(Aspect.AURA, 128).add(Aspect.MAGIC, 32).add(Aspect.DESIRE, 64),
				MetaTileEntities.FLUID_IMPORT_HATCH[IV].getStackForm(),
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				"plateMansussteel",
				new ItemStack(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206),
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		//管道
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "pipe-bronze"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.BRONZE_PIPE),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateBronze",
				'C', MetaBlocks.BOILER_CASING.getItemVariant(BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "pipe-steel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.STEEL_PIPE),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateSteel",
				'C', MetaBlocks.BOILER_CASING.getItemVariant(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "pipe-polytetrafluoroethylene"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.POLYTETRAFLUOROETHYLENE_PIPE),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "platePolytetrafluoroethylene",
				'C', MetaBlocks.BOILER_CASING.getItemVariant(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "pipe-titanium"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.TITANIUM_PIPE),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateTitanium",
				'C', MetaBlocks.BOILER_CASING.getItemVariant(BlockBoilerCasing.BoilerCasingType.TITANIUM_PIPE)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "pipe-tungstensteel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.TUNGSTENSTEEL_PIPE),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateTungstenSteel",
				'C', MetaBlocks.BOILER_CASING.getItemVariant(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE)));
		//齿轮
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "gearbox_bronze"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.BRONZE_GEARBOX),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateBronze",
				'C', MetaBlocks.TURBINE_CASING.getItemVariant(BlockTurbineCasing.TurbineCasingType.BRONZE_GEARBOX)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "gearbox_steel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.STEEL_GEARBOX),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateSteel",
				'C', MetaBlocks.TURBINE_CASING.getItemVariant(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "gearbox_bronze"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.BRONZE_GEARBOX),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateBronze",
				'C', MetaBlocks.TURBINE_CASING.getItemVariant(BlockTurbineCasing.TurbineCasingType.BRONZE_GEARBOX)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "gearbox_stainless"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.STAINLESS_STEEL_GEARBOX),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateStainlessSteel",
				'C', MetaBlocks.TURBINE_CASING.getItemVariant(BlockTurbineCasing.TurbineCasingType.STEEL_GEARBOX)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "gearbox_titanium"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.TITANIUM_GEARBOX),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateTitanium",
				'C', MetaBlocks.TURBINE_CASING.getItemVariant(BlockTurbineCasing.TurbineCasingType.TITANIUM_GEARBOX)));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "gearbox_tungstensteel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.TURBINE.getItemVariant(POTurbine.MagicBlockType.TUNGSTENSTEEL_GEARBOX),
				"BBB",
				"ACA",
				"BBB",
				'A', "plateMansussteel",
				'B', "plateTungstenSteel",
				'C', MetaBlocks.TURBINE_CASING.getItemVariant(BlockTurbineCasing.TurbineCasingType.TUNGSTENSTEEL_GEARBOX)));
		//蕴魔引导外壳，电池外壳
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "battery_casing"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				75,
				new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.MAGIC_BATTERY, 16),
				"BAB",
				"DCD",
				"BAB",
				'A', "plateMansussteel",
				'B', "plateThaumium",
				'C', "frameGtMansussteel",
				'D', new ItemStack(MetaItems.FIELD_GENERATOR_MV.getMetaItem(), 1, 203)));
		//filter配方
		//一级
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "filter_1"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				50,
				new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_1, 4),
				"AAA",
				"DCD",
				"AAA",
				'A', "plateMansussteel",
				'C', "frameGtMansussteel",
				'D', new ItemStack(BlocksTC.visBattery)));
		//二级
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "filter_2"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				75,
				new AspectList().add(Aspect.FIRE, 2).add(Aspect.ORDER, 2),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_2, 8),
				"AAA",
				"ABA",
				"AAA",
				'A', PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_1),
				'B', new ItemStack(MetaItems.FIELD_GENERATOR_LV.getMetaItem(), 1, 202)));
		//三级
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "filter_3"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				100,
				new AspectList().add(Aspect.FIRE, 3).add(Aspect.ORDER, 3),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_3, 8),
				"AAA",
				"ABA",
				"AAA",
				'A', PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_2),
				'B', new ItemStack(MetaItems.FIELD_GENERATOR_MV.getMetaItem(), 1, 203)));
		//四级
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "filter_4"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				125,
				new AspectList().add(Aspect.FIRE, 4).add(Aspect.ORDER, 4),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_4, 8),
				"AAA",
				"ABA",
				"AAA",
				'A', PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_3),
				'B', new ItemStack(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 1, 204)));
		//五级
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "filter_5"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				150,
				new AspectList().add(Aspect.FIRE, 5).add(Aspect.ORDER, 5),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_5, 8),
				"AAA",
				"ABA",
				"AAA",
				'A', PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_4),
				'B', new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205)));

		//约束器配方
		//粗制
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "reactor-frame-i"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.ENTROPY, 1).add(Aspect.ORDER, 1),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_I, 2),
				"AAA",
				"CBD",
				"AAA",
				'A', "plateMansussteel",
				'B', "frameGtMansussteel",
				'C', PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.VOID_PRISM),
				'D', PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM)));
		//后面几种
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "reactor-frame-ii"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_II),
				2,
				new AspectList().add(Aspect.ORDER, 8).add(Aspect.PROTECT, 8),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_I),
				"gemScabyst",
				"circuitLv",
				"dustThorium",
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "reactor-frame-iii"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_III),
				4,
				new AspectList().add(Aspect.ORDER, 16).add(Aspect.PROTECT, 16),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_II),
				"gemScabyst",
				"circuitMv",
				"dustThorium",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "reactor-frame-iv"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_IV),
				6,
				new AspectList().add(Aspect.ORDER, 32).add(Aspect.PROTECT, 32),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_III),
				"gemScabyst",
				"gemValonite",
				"circuitHv",
				"dustUranium",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "reactor-frame-v"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_V),
				8,
				new AspectList().add(Aspect.ORDER, 64).add(Aspect.PROTECT, 64),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_IV),
				"gemScabyst",
				"gemValonite",
				"circuitEv",
				"dustUranium",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator),
				new ItemStack(BlocksTC.visBattery)));
		//蕴魔炼炉扩散要素凝聚外壳
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "alloy_blast_casing"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.ALLOY_BLAST_CASING),
				4,
				new AspectList().add(Aspect.FIRE, 16).add(Aspect.METAL, 64),
				PollutionMetaBlocks.MAGIC_BLOCK.getItemVariant(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT),
				"plateOctine",
				"plateOctine",
				"plateMansussteel",
				"plateMansussteel",
				new ItemStack(PollutionMetaItems.HOTCORE.getMetaItem(), 1, 3),
				new ItemStack(SEGREGATECORE.getMetaItem(), 1, 6)));
		//裂变核心
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "reactor-compose-i"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.COMPOSE_I),
				2,
				new AspectList().add(Aspect.ENTROPY, 8).add(Aspect.ENERGY, 8).add(Aspect.VOID, 8),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.FRAME_I),
				"gemOpal",
				new ItemStack(BlocksTC.crystalEntropy),
				"circuitLv",
				new ItemStack(ItemsTC.visResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "reactor-compose-ii"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.COMPOSE_II),
				4,
				new AspectList().add(Aspect.ENTROPY, 16).add(Aspect.ENERGY, 16).add(Aspect.VOID, 16),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.COMPOSE_I),
				"gemOpal",
				new ItemStack(BlocksTC.crystalEntropy),
				"circuitMv",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "reactor-compose-iii"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.COMPOSE_III),
				6,
				new AspectList().add(Aspect.ENTROPY, 32).add(Aspect.ENERGY, 32).add(Aspect.VOID, 32),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.COMPOSE_II),
				"gemOpal",
				"gemAmethyst",
				new ItemStack(BlocksTC.crystalEntropy),
				"circuitHv",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "reactor-compose-iv"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.COMPOSE_IV),
				8,
				new AspectList().add(Aspect.ENTROPY, 64).add(Aspect.ENERGY, 64).add(Aspect.VOID, 64),
				PollutionMetaBlocks.FUSION_REACTOR.getItemVariant(POFusionReactor.FusionBlockType.COMPOSE_III),
				"gemOpal",
				"gemAmethyst",
				new ItemStack(BlocksTC.visBattery),
				new ItemStack(BlocksTC.crystalEntropy),
				"circuitEv",
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		//泰拉方块 高阶漫宿 大组装
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, manasteel, 5)
				.input(plate, Terrasteel, 1)
				.input(frameGt, mansussteel ,1)
				.circuitMeta(16)
				.outputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING))
				.duration(300)
				.EUt(1920)
				.buildAndRegister();
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, thaumium, 2)
				.input(plate, hyperdimensional_silver, 4)
				.input(frameGt, mansussteel,1)
				.circuitMeta(16)
				.outputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_BASIC))
				.duration(300)
				.EUt(1920)
				.buildAndRegister();
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "magic_assembler"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MAGIC_ASSEMBLER.getStackForm(),
				10,
				new AspectList().add(Aspect.CRAFT, 250).add(Aspect.ORDER, 128).add(Aspect.MAGIC, 64).add(Aspect.MOTION, 64),
				PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.FILTER_5),
				"blockValonite",
				"frameGtKeqinggold",
				"frameGtHyperdimensionalSilver",
				"frameGtTerrasteel",
				new ItemStack(BlocksTC.arcaneWorkbench),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				new ItemStack(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206),
				new ItemStack(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206),
				new ItemStack(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206),
				new ItemStack(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206),
				MetaTileEntities.ASSEMBLER[IV].getStackForm()));
		//泰拉系六方块 强制魔法组装 六种合金板+符文+泰拉钢+魔力
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, aquasilver, 3)
				.inputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING))
				.input(plate, Terrasteel, 3)
				.input(frameGt, keqinggold)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_1_CASING, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, terracopper, 3)
				.inputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING))
				.input(plate, Terrasteel, 3)
				.input(frameGt, keqinggold)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_2_CASING, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, ordolead, 3)
				.inputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING))
				.input(plate, Terrasteel, 3)
				.input(frameGt, keqinggold)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_3_CASING, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, ignissteel, 3)
				.inputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING))
				.input(plate, Terrasteel, 3)
				.input(frameGt, keqinggold)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_4_CASING, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, aertitanium, 3)
				.inputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING))
				.input(plate, Terrasteel, 3)
				.input(frameGt, keqinggold)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_5_CASING, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, perditioaluminium, 3)
				.inputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING))
				.input(plate, Terrasteel, 3)
				.input(frameGt, keqinggold)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_6_CASING, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		//漫宿系五方块
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, ignissteel, 3)
				.inputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_BASIC))
				.input(plate, ElvenElementium, 3)
				.input(ModItems.rune, 1, 1)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_1, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, aertitanium, 3)
				.inputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_BASIC))
				.input(plate, ElvenElementium, 3)
				.input(ModItems.rune, 1, 3)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_2, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, Terrasteel, 3)
				.inputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_BASIC))
				.input(plate, ElvenElementium, 3)
				.input(ModItems.rune, 1, 2)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_3, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, aquasilver, 3)
				.inputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_BASIC))
				.input(plate, ElvenElementium, 3)
				.input(ModItems.rune, 1, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_4, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, terracopper, 3)
				.inputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_BASIC))
				.input(plate, ElvenElementium, 3)
				.input(ModItems.rune, 1, 6)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_5, 2))
				.circuitMeta(6)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();
		//锻炉
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaTileEntities.ELECTRIC_BLAST_FURNACE, 64)
				.input(BlocksTC.smelterThaumium, 16)
				.input(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 16, 206)
				.input(block, valonite, 1)
				.input(gear, hyperdimensional_silver, 4)
				.input(frameGt, Terrasteel, 4)
				.input(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150)
				.fluidInputs(dimensional_transforming_agent.getFluid(1000))
				.outputs(PollutionMetaTileEntities.NODE_BLAST_FURNACE.getStackForm())
				.duration(10000)
				.EUt(7680)
				.buildAndRegister();
		//小化工厂
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaTileEntities.CHEMICAL_REACTOR[IV], 4)
				.input(GTQTMetaTileEntities.CHEMICAL_PLANT, 1)
				.input(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 4, 206)
				.input(block, valonite, 1)
				.input(gear, hyperdimensional_silver, 4)
				.input(frameGt, keqinggold, 4)
				.input(PollutionMetaItems.EVOLUTIONCORE.getMetaItem(), 1, 8)
				.fluidInputs(dimensional_transforming_agent.getFluid(1000))
				.outputs(PollutionMetaTileEntities.SMALL_CHEMICAL_PLANT.getStackForm())
				.duration(10000)
				.EUt(7680)
				.buildAndRegister();
		//并行控制仓（四种）
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaTileEntities.HULL[IV], 4)
				.input(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206)
				.input(frameGt, mansussteel, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(GCYMMetaTileEntities.PARALLEL_HATCH[0].getStackForm(4))
				.duration(800)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaTileEntities.HULL[LuV], 4)
				.input(MetaItems.FIELD_GENERATOR_LuV.getMetaItem(), 1, 207)
				.input(frameGt, mansussteel, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(GCYMMetaTileEntities.PARALLEL_HATCH[1].getStackForm(4))
				.duration(800)
				.EUt(30720)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaTileEntities.HULL[ZPM], 4)
				.input(MetaItems.FIELD_GENERATOR_ZPM.getMetaItem(), 1, 208)
				.input(frameGt, mansussteel, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(GCYMMetaTileEntities.PARALLEL_HATCH[2].getStackForm(4))
				.duration(800)
				.EUt(122880)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaTileEntities.HULL[UV], 4)
				.input(MetaItems.FIELD_GENERATOR_UV.getMetaItem(), 1, 209)
				.input(frameGt, mansussteel, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(GCYMMetaTileEntities.PARALLEL_HATCH[3].getStackForm(4))
				.duration(800)
				.EUt(491520)
				.buildAndRegister();
		//炼金枢纽
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "essence_smelter"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.ESSENCE_SMELTER.getStackForm(),
				4,
				new AspectList().add(Aspect.ALCHEMY, 128).add(Aspect.FIRE, 64).add(Aspect.MAGIC, 32).add(Aspect.MECHANISM, 16),
				BlocksTC.smelterThaumium,
				"gemValonite",
				"frameGtThaumium",
				"frameGtMansussteel",
				new ItemStack(BlocksTC.metalAlchemical),
				new ItemStack(BlocksTC.metalAlchemical),
				new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5),
				new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6),
				new ItemStack(MetaItems.FIELD_GENERATOR_MV.getMetaItem(), 1, 203),
				new ItemStack(MetaItems.FIELD_GENERATOR_MV.getMetaItem(), 1, 203),
				new ItemStack(MetaItems.FIELD_GENERATOR_MV.getMetaItem(), 1, 203),
				new ItemStack(MetaItems.FIELD_GENERATOR_MV.getMetaItem(), 1, 203),
				MetaTileEntities.CHEMICAL_REACTOR[MV].getStackForm()));
		//gt版炼金枢纽
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(PollutionMetaTileEntities.ESSENCE_SMELTER.getStackForm())
				.inputs(PollutionMetaTileEntities.MAGIC_CHEMICAL_REACTOR.getStackForm())
				.input(MetaItems.FIELD_GENERATOR_HV.getMetaItem(), 8, 204)
				.input(BlocksTC.smelterThaumium, 8)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.outputs(PollutionMetaTileEntities.GT_ESSENCE_SMELTER.getStackForm())
				.circuitMeta(1)
				.EUt(1920)
				.duration(1000)
				.buildAndRegister();
		//bot集气
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(GTQTMetaTileEntities.GAS_COLLECTOR.getStackForm(16))
				.input(circuit, MarkerMaterials.Tier.IV, 16)
				.input(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 4, 206)
				.input(MetaItems.ELECTRIC_PUMP_IV, 16)
				.input(plate, ElvenElementium, 32)
				.input(frameGt, keqinggold, 4)
				.input(gear, hyperdimensional_silver, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(10000))
				.outputs(PollutionMetaTileEntities.BOT_GAS_COLLECTOR.getStackForm())
				.circuitMeta(16)
				.EUt(30720)
				.duration(1000)
				.buildAndRegister();
		//bot冰箱
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(GTQTMetaTileEntities.VACUUM_FREEZER.getStackForm(16))
				.input(circuit, MarkerMaterials.Tier.IV, 16)
				.input(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 4, 206)
				.input(MetaItems.ELECTRIC_PUMP_IV, 16)
				.input(plate, Terrasteel, 32)
				.input(frameGt, hyperdimensional_silver, 4)
				.input(gear, keqinggold, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(10000))
				.outputs(PollutionMetaTileEntities.BOT_VACUUM_FREEZER.getStackForm())
				.circuitMeta(16)
				.EUt(30720)
				.duration(1000)
				.buildAndRegister();
		//大型魔力轮机
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(PollutionMetaTileEntities.MAGIC_ENERGY_ABSORBER[4].getStackForm(8))
				.input(circuit, MarkerMaterials.Tier.IV, 8)
				.input(MetaItems.ELECTRIC_MOTOR_IV, 16)
				.input(MetaItems.ELECTRIC_PUMP_IV, 16)
				.input(plate, Terrasteel, 32)
				.input(gear, keqinggold, 4)
				.fluidInputs(GTQTMaterials.Magic.getFluid(16000))
				.fluidInputs(Lubricant.getFluid(16000))
				.outputs(PollutionMetaTileEntities.LARGE_MANA_TURBINE.getStackForm())
				.circuitMeta(16)
				.EUt(30720)
				.duration(1000)
				.buildAndRegister();

		//巨型魔力轮机
		ASSEMBLY_LINE_RECIPES.recipeBuilder()
				.inputs(PollutionMetaTileEntities.LARGE_MANA_TURBINE.getStackForm(64))
				.input(frameGt,ElvenElementium,64)
				.input(circuit, MarkerMaterials.Tier.ZPM, 4)
				.input(circuit, MarkerMaterials.Tier.LuV, 16)
				.input(circuit, MarkerMaterials.Tier.IV, 64)
				.input(MetaItems.ELECTRIC_PUMP_LuV, 64)
				.input(MetaItems.FIELD_GENERATOR_LuV, 16)
				.input(plateDense, Terrasteel, 32)
				.input(cableGtHex, keqinggold, 16)
				.fluidInputs(GTQTMaterials.Magic.getFluid(64000))
				.fluidInputs(Lubricant.getFluid(64000))
				.outputs(PollutionMetaTileEntities.MEGA_MANA_TURBINE.getStackForm())
				.EUt(VA[ZPM])
				.duration(1600)
				.scannerResearch(b -> b
						.researchStack(PollutionMetaTileEntities.LARGE_MANA_TURBINE.getStackForm())
						.EUt(VA[LuV]))
				.buildAndRegister();
		//启命花园
		ASSEMBLY_LINE_RECIPES.recipeBuilder()
				.inputs(ItemBlockSpecialFlower.ofType("dandelifeon"))
				.input(ModBlocks.enchantedSoil,64)
				.input(ModBlocks.enchantedSoil,64)
				.input(ModBlocks.enchantedSoil,64)
				.input(ModBlocks.enchantedSoil,64)
				.input(frameGt,Terrasteel,64)
				.input(circuit, MarkerMaterials.Tier.UHV, 16)
				.input(circuit, MarkerMaterials.Tier.UV, 32)
				.input(circuit, MarkerMaterials.Tier.ZPM, 64)
				.input(MetaItems.FIELD_GENERATOR_UV, 64)
				.input(MetaItems.EMITTER_UV,64)
				.input(MetaItems.SENSOR_UV,64)
				.input(plateDense, ElvenElementium, 32)
				.input(plateDense, Terrasteel, 32)
				.input(cableGtHex, keqinggold, 16)
				.fluidInputs(GTQTMaterials.Richmagic.getFluid(64000))
				.fluidInputs(UUMatter.getFluid(128000))
				.fluidInputs(Orichalcum.getFluid(64000))
				.fluidInputs(Silver.getPlasma(64000))
				.outputs(PollutionMetaTileEntities.Muti_Dan_De_Life_On.getStackForm())
				.EUt(VA[UHV])

				.duration(1600)
				.buildAndRegister();
		//蕴魔草地
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(ModItems.overgrowthSeed,64)
				.inputs(new ItemStack(ModBlocks.altGrass, 128, 4))
				.input(dust, Terrasteel, 64)
				.fluidInputs(blackmansus.getFluid(6400))
				.fluidInputs(whitemansus.getFluid(6400))
				.outputs(new ItemStack(ModBlocks.enchantedSoil, 128))
				.EUt(30720)
				.duration(500)
				.buildAndRegister();
		//注魔草地
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(new ItemStack(ModItems.grassSeeds, 64, 7))
				.input(Blocks.GRASS,128)
				.input(dust, salismundus, 64)
				.fluidInputs(blackmansus.getFluid(3200))
				.fluidInputs(whitemansus.getFluid(3200))
				.outputs(new ItemStack(ModBlocks.altGrass, 128, 4))
				.EUt(8192)
				.duration(500)
				.buildAndRegister();
		//增生之种
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(new ItemStack(ModItems.grassSeeds, 4, 7))
				.input(ModItems.manaResource,4,8)
				.input(dust, Ichorium, 1)
				.fluidInputs(blackmansus.getFluid(3200))
				.fluidInputs(whitemansus.getFluid(3200))
				.fluidInputs(UUMatter.getFluid(12800))
				.outputs(new ItemStack(ModItems.overgrowthSeed,4))
				.EUt(32768)
				.duration(5000)
				.buildAndRegister();
		//火红莲
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "endoflame_array"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.ENDOFLAME_ARRAY.getStackForm(),
				6,
				new AspectList().add(Aspect.PLANT, 250).add(Aspect.FIRE, 128).add(Aspect.MAGIC, 32).add(Aspect.MECHANISM, 16),
				new ItemStack(HOTCORE.getMetaItem(), 1, 3),
				"frameGtKeqinggold",
				new ItemStack(ModBlocks.pylon, 1, 1),
				ItemBlockSpecialFlower.ofType("endoflame"),
				PollutionMetaBlocks.BOT_BLOCK.getItemVariant(POBotBlock.BotBlockType.TERRA_4_CASING),
				"gemValonite",
				"circuitIv",
				"circuitIv",
				new ItemStack(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206),
				new ItemStack(MetaItems.FIELD_GENERATOR_IV.getMetaItem(), 1, 206),
				MetaTileEntities.LARGE_GAS_TURBINE.getStackForm()));

		//部分TC物品 玻璃 组装机配方
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, Steel, 48)
				.input(OrePrefix.gem, Quartzite, 12)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.output(ItemsTC.visResonator, 12)
				.EUt(1920)
				.duration(100)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(gem, valonite, 8)
				.input(plate, thaumium, 48)
				.fluidInputs(GTQTMaterials.Magic.getFluid(1000))
				.output(ItemsTC.morphicResonator, 8)
				.EUt(1920)
				.duration(100)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS, 2)
				.input(ItemsTC.morphicResonator)
				.fluidInputs(GTQTMaterials.Magic.getFluid(500))
				.outputs(PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS))
				.circuitMeta(1)
				.EUt(1920)
				.duration(100)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS, 2)
				.input(ItemsTC.morphicResonator)
				.fluidInputs(GTQTMaterials.Magic.getFluid(500))
				.outputs(PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS))
				.circuitMeta(2)
				.EUt(1920)
				.duration(100)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS, 2)
				.input(ItemsTC.morphicResonator)
				.fluidInputs(GTQTMaterials.Magic.getFluid(500))
				.outputs(PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.BAMINATED_GLASS))
				.circuitMeta(3)
				.EUt(1920)
				.duration(100)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS, 2)
				.input(ItemsTC.morphicResonator)
				.fluidInputs(GTQTMaterials.Magic.getFluid(500))
				.outputs(PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.CAMINATED_GLASS))
				.circuitMeta(4)
				.EUt(1920)
				.duration(100)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS, 2)
				.input(ItemsTC.morphicResonator)
				.fluidInputs(GTQTMaterials.Magic.getFluid(500))
				.outputs(PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.DAMINATED_GLASS))
				.circuitMeta(5)
				.EUt(1920)
				.duration(100)
				.buildAndRegister();
		//高级魔导组件
		//理式核心
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(stickLong, aetheric_dark_steel, 2)
				.input(frameGt, aetheric_dark_steel, 1)
				.input(plate, iizunamaru_electrum, 8)
				.input(gear, iizunamaru_electrum, 4)
				.input(ItemsTC.morphicResonator, 16)
				.input(ItemsTC.visResonator, 16)
				.input(MetaItems.FIELD_GENERATOR_LuV)
				.fluidInputs(starrymansus.getFluid(1000))
				.output(CORE_OF_IDEA)
				.EUt(30720)
				.duration(400)
				.buildAndRegister();
		//自动反诘装置
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(gear, blood_of_avernus, 6)
				.input(gearSmall, blood_of_avernus, 4)
				.input(stickLong, iizunamaru_electrum, 4)
				.input(stick, aetheric_dark_steel, 4)
				.input(plate, GTQTMaterials.VoidMetal, 8)
				.input(CORE_OF_IDEA, 2)
				.input(MetaItems.ELECTRIC_PISTON_LUV, 2)
				.input(MetaItems.ROBOT_ARM_LuV)
				.fluidInputs(starrymansus.getFluid(2000))
				.fluidInputs(blackmansus.getFluid(8000))
				.fluidInputs(whitemansus.getFluid(8000))
				.output(AUTO_ELENCHUS_DEVICE)
				.EUt(30720)
				.duration(400)
				.buildAndRegister();
		//太一燃素瓶
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(rotor, iizunamaru_electrum, 2)
				.input(gearSmall, blood_of_avernus, 4)
				.input(ring, iizunamaru_electrum, 32)
				.input(screw, aetheric_dark_steel, 12)
				.input(plate, GTQTMaterials.VoidMetal, 8)
				.input(CORE_OF_IDEA, 2)
				.input(ItemsTC.causalityCollapser, 4)
				.input(MetaItems.ELECTRIC_PUMP_LuV, 2)
				.fluidInputs(infused_fire.getFluid(64000))
				.fluidInputs(blackmansus.getFluid(8000))
				.fluidInputs(whitemansus.getFluid(8000))
				.output(BOTTLE_OF_PHLOGISTONIC_ONENESS)
				.EUt(30720)
				.duration(400)
				.buildAndRegister();
		//四因阐释器
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plate, iizunamaru_electrum, 16)
				.input(plate, blood_of_avernus, 16)
				.input(plate, aetheric_dark_steel, 16)
				.input(plate, GTQTMaterials.VoidMetal, 16)
				.input(ItemsTC.causalityCollapser, 4)
				.input(MetaItems.ROBOT_ARM_LuV, 2)
				.input(CORE_OF_IDEA, 2)
				.input(AUTO_ELENCHUS_DEVICE)
				.fluidInputs(dimensional_transforming_agent.getFluid(8000))
				.fluidInputs(blackmansus.getFluid(8000))
				.fluidInputs(whitemansus.getFluid(8000))
				.output(ELUCIDATOR_OF_FOUR_CAUSES)
				.EUt(30720)
				.duration(400)
				.buildAndRegister();
		//意志数据链
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(plateDouble, iizunamaru_electrum, 16)
				.input(plateDouble, GTQTMaterials.VoidMetal, 16)
				.input(stickLong, aetheric_dark_steel, 8)
				.input(MetaItems.SENSOR_ZPM, 1)
				.input(MetaItems.EMITTER_ZPM, 1)
				.input(CORE_OF_IDEA, 2)
				.input(BOTTLE_OF_PHLOGISTONIC_ONENESS, 1)
				.fluidInputs(dimensional_transforming_agent.getFluid(8000))
				.fluidInputs(sentient_metal, 1440)
				.fluidInputs(binding_metal, 1440)
				.output(SYMPTOMATIC_VIS_DATA_LINK)
				.EUt(122880)
				.duration(400)
				.buildAndRegister();
		//中控塔
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(PollutionMetaTileEntities.BOT_GAS_COLLECTOR.getStackForm(1))
				.inputs(PollutionMetaTileEntities.FLUX_CLEARS[2].getStackForm(1))
				.inputs(MetaTileEntities.CENTRAL_MONITOR.getStackForm(1))
				.inputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_BASIC, 4))
				.input(ItemsTC.morphicResonator, 64)
				.input(frameGt, hyperdimensional_silver, 16)
				.input(MetaItems.EMITTER_LuV, 8)
				.input(MetaItems.SENSOR_LuV, 8)
				.input(MetaItems.FIELD_GENERATOR_LuV, 4)
				.fluidInputs(GTQTMaterials.Richmagic.getFluid(16000))
				.fluidInputs(blackmansus.getFluid(16000))
				.fluidInputs(whitemansus.getFluid(16000))
				.outputs(PollutionMetaTileEntities.CENTRAL_VIS_TOWER.getStackForm(1))
				.EUt(30720)
				.duration(4000)
				.buildAndRegister();
		//电路组装
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(MetaTileEntities.CIRCUIT_ASSEMBLER[LuV].getStackForm(4))
				.inputs(PollutionMetaBlocks.MANA_PLATE.getItemVariant(POManaPlate.ManaBlockType.MANA_5, 4))
				.input(MetaItems.FIELD_GENERATOR_LuV, 4)
				.input(AUTO_ELENCHUS_DEVICE, 4)
				.input(ELUCIDATOR_OF_FOUR_CAUSES, 2)
				.input(frameGt, blood_of_avernus, 16)
				.input(plate, ElvenElementium, 16)
				.fluidInputs(GTQTMaterials.Richmagic.getFluid(16000))
				.fluidInputs(blackmansus.getFluid(16000))
				.fluidInputs(whitemansus.getFluid(16000))
				.outputs(PollutionMetaTileEntities.BOT_CIRCUIT_ASSEMBLER.getStackForm())
				.EUt(30720)
				.duration(4000)
				.buildAndRegister();

		//bot 四个机器
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "pure_daisy"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.INDUSTRIAL_PURE_DAISY.getStackForm(),
				6,
				new AspectList().add(Aspect.PLANT, 128).add(Aspect.MAGIC, 32).add(Aspect.MECHANISM, 32),
				ItemBlockSpecialFlower.ofType("puredaisy"),
				"plateManasteel",
				"plateManasteel",
				"plateMansussteel",
				"plateMansussteel",
				"circuitEv",
				"circuitEv",
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "mana_infusion"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MANA_INFUSION_REACTOR.getStackForm(),
				6,
				new AspectList().add(Aspect.PLANT, 128).add(Aspect.MAGIC, 32).add(Aspect.ALCHEMY, 128),
				ModBlocks.alchemyCatalyst,
				"plateManasteel",
				"plateManasteel",
				"plateMansussteel",
				"plateMansussteel",
				"circuitEv",
				"circuitEv",
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "mana_rune_altar"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MANA_RUNE_ALTAR.getStackForm(),
				6,
				new AspectList().add(Aspect.PLANT, 128).add(Aspect.MAGIC, 64).add(Aspect.CRAFT, 32),
				ModBlocks.runeAltar,
				"plateManasteel",
				"plateManasteel",
				"plateMansussteel",
				"plateMansussteel",
				"circuitEv",
				"circuitEv",
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "mana_petal"), new InfusionRecipe(
				"INFUSION@2",
				PollutionMetaTileEntities.MANA_PETAL_APOTHECARY.getStackForm(),
				6,
				new AspectList().add(Aspect.PLANT, 128).add(Aspect.MAGIC, 32).add(Aspect.AURA, 32),
				ModBlocks.altar,
				"plateManasteel",
				"plateManasteel",
				"plateMansussteel",
				"plateMansussteel",
				"circuitEv",
				"circuitEv",
				new ItemStack(MetaItems.FIELD_GENERATOR_EV.getMetaItem(), 1, 205),
				new ItemStack(ItemsTC.visResonator),
				new ItemStack(ItemsTC.morphicResonator)));
	}
}
