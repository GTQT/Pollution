package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtechfoodoption.machines.GTFOTileEntities;
import keqing.gtqtcore.common.metatileentities.GTQTMetaTileEntities;
import keqing.pollution.Pollution;
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

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static gregtech.api.unification.ore.OrePrefix.ingotHot;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_GREENHOUSE_RECIPES;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.common.items.PollutionMetaItems.HOTCORE;
import static keqing.pollution.common.items.PollutionMetaItems.SEGREGATECORE;

public class MagicGCYMRecipes {
	public static void init() {
		materials();
	}

	private static void materials() {
		//加一个直接炼矿的
        /*
        MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
                .input(dust,infused_air,  16)
                .fluidOutputs(infused_air.getFluid(32*144))
                .notConsumable(HOTCORE)
                .notConsumable(COLDCORE)
                .notConsumable(INTEGRATECORE)
                .notConsumable(SEGREGATECORE)
                .duration(2400)
                .blastFurnaceTemp(2700)
                .EUt(480)
                .buildAndRegister();

        MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
                .input(dust,infused_fire,  16)
                .fluidOutputs(infused_fire.getFluid(32*144))
                .notConsumable(HOTCORE)
                .notConsumable(COLDCORE)
                .notConsumable(INTEGRATECORE)
                .notConsumable(SEGREGATECORE)
                .duration(2400)
                .blastFurnaceTemp(2700)
                .EUt(480)
                .buildAndRegister();

        MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
                .input(dust,infused_water,  16)
                .fluidOutputs(infused_water.getFluid(32*144))
                .notConsumable(HOTCORE)
                .notConsumable(COLDCORE)
                .notConsumable(INTEGRATECORE)
                .notConsumable(SEGREGATECORE)
                .duration(2400)
                .blastFurnaceTemp(2700)
                .EUt(480)
                .buildAndRegister();

        MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
                .input(dust,infused_earth,  16)
                .fluidOutputs(infused_earth.getFluid(32*144))
                .notConsumable(HOTCORE)
                .notConsumable(COLDCORE)
                .notConsumable(INTEGRATECORE)
                .notConsumable(SEGREGATECORE)
                .duration(2400)
                .blastFurnaceTemp(2700)
                .EUt(480)
                .buildAndRegister();

        MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
                .input(dust,infused_entropy,  16)
                .fluidOutputs(infused_entropy.getFluid(32*144))
                .notConsumable(HOTCORE)
                .notConsumable(COLDCORE)
                .notConsumable(INTEGRATECORE)
                .notConsumable(SEGREGATECORE)
                .duration(2400)
                .blastFurnaceTemp(2700)
                .EUt(480)
                .buildAndRegister();

        MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
                .input(dust,infused_order,  16)
                .fluidOutputs(infused_order.getFluid(32*144))
                .notConsumable(HOTCORE)
                .notConsumable(COLDCORE)
                .notConsumable(INTEGRATECORE)
                .notConsumable(SEGREGATECORE)
                .duration(2400)
                .blastFurnaceTemp(2700)
                .EUt(480)
                .buildAndRegister();

         */
		//这里是六个基础外壳材料的搅拌机配方
		//风要素-律动钛
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Bauxite, 2)
				.input(OrePrefix.dust, Aluminium, 1)
				.input(OrePrefix.dust, Manganese, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_air, 5)
				.output(OrePrefix.dust, PollutionMaterials.aertitanium, 9)
				.duration(900)
				.EUt(480)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Bauxite, 2)
				.input(OrePrefix.dust, Aluminium, 1)
				.input(OrePrefix.dust, Manganese, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_air, 5)
				.fluidOutputs(PollutionMaterials.aertitanium.getFluid(9 * 144))
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//火要素-残日钢
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Steel, 2)
				.input(OrePrefix.dust, Magnesium, 1)
				.input(OrePrefix.dust, Lithium, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_fire, 5)
				.output(OrePrefix.dust, PollutionMaterials.ignissteel, 9)
				.duration(900)
				.EUt(480)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Steel, 2)
				.input(OrePrefix.dust, Magnesium, 1)
				.input(OrePrefix.dust, Lithium, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_fire, 5)
				.fluidOutputs(PollutionMaterials.ignissteel.getFluid(9 * 144))
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//水要素-捩花银
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Silver, 2)
				.input(OrePrefix.dust, Tin, 1)
				.fluidInputs(Mercury.getFluid(1000))
				.input(OrePrefix.dust, PollutionMaterials.infused_water, 5)
				.output(OrePrefix.dust, PollutionMaterials.aquasilver, 9)
				.duration(900)
				.EUt(480)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Silver, 2)
				.input(OrePrefix.dust, Tin, 1)
				.fluidInputs(Mercury.getFluid(1000))
				.input(OrePrefix.dust, PollutionMaterials.infused_water, 5)
				.fluidOutputs(PollutionMaterials.aquasilver.getFluid(9 * 144))
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//地要素-定坤铜
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Copper, 2)
				.input(OrePrefix.dust, Boron, 1)
				.input(OrePrefix.dust, Carbon, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_earth, 5)
				.output(OrePrefix.dust, PollutionMaterials.terracopper, 9)
				.duration(900)
				.EUt(480)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Copper, 2)
				.input(OrePrefix.dust, Boron, 1)
				.input(OrePrefix.dust, Carbon, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_earth, 5)
				.fluidOutputs(PollutionMaterials.terracopper.getFluid(9 * 144))
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//秩序要素-司辰铅
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Lead, 2)
				.input(OrePrefix.dust, Silicon, 1)
				.input(OrePrefix.dust, Gold, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_order, 5)
				.output(OrePrefix.dust, PollutionMaterials.ordolead, 9)
				.duration(900)
				.EUt(480)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Lead, 2)
				.input(OrePrefix.dust, Silicon, 1)
				.input(OrePrefix.dust, Gold, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_order, 5)
				.fluidOutputs(PollutionMaterials.ordolead.getFluid(9 * 144))
				.duration(300)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//混沌要素-无极铝
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Aluminium, 2)
				.fluidInputs(Fluorine.getFluid(1000))
				.input(OrePrefix.dust, Thorium, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_entropy, 5)
				.output(OrePrefix.dust, perditioaluminium, 9)
				.duration(900)
				.EUt(480)
				.buildAndRegister();

		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Aluminium, 2)
				.fluidInputs(Fluorine.getFluid(1000))
				.input(OrePrefix.dust, Thorium, 1)
				.input(OrePrefix.dust, PollutionMaterials.infused_entropy, 5)
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
				.duration(600)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, ignissteel, 1)
				.fluidInputs(infused_fire.getFluid(144))
				.output(ingotHot, ignissteel, 1)
				.circuitMeta(11)
				.duration(600)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, aquasilver, 1)
				.fluidInputs(infused_water.getFluid(144))
				.output(ingotHot, aquasilver, 1)
				.circuitMeta(11)
				.duration(600)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, terracopper, 1)
				.fluidInputs(infused_earth.getFluid(144))
				.output(ingotHot, terracopper, 1)
				.circuitMeta(11)
				.duration(600)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, ordolead, 1)
				.fluidInputs(infused_order.getFluid(144))
				.output(ingotHot, ordolead, 1)
				.circuitMeta(11)
				.duration(600)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(dust, perditioaluminium, 1)
				.fluidInputs(infused_entropy.getFluid(144))
				.output(ingotHot, perditioaluminium, 1)
				.circuitMeta(11)
				.duration(600)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		//魔力钢 神秘锭简化配方
		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, infused_air, 1)
				.input(OrePrefix.dust, infused_fire, 1)
				.input(OrePrefix.dust, infused_water, 1)
				.input(OrePrefix.dust, infused_earth, 1)
				.input(OrePrefix.dust, infused_order, 1)
				.input(OrePrefix.dust, infused_entropy, 1)
				.input(OrePrefix.dust, Iron, 4)
				.fluidOutputs(manasteel.getFluid(576))
				.circuitMeta(1)
				.duration(400)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();
		MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Iron, 1)
				.input(OrePrefix.dust, infused_earth, 10)
				.input(OrePrefix.dust, infused_fire, 5)
				.input(OrePrefix.dust, infused_air, 5)
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
				.input(OrePrefix.dust, PollutionMaterials.infused_order)
				.input(OrePrefix.dust, PollutionMaterials.infused_entropy)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(18))
				.circuitMeta(1)
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.infused_air)
				.input(OrePrefix.dust, PollutionMaterials.infused_earth)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(18))
				.circuitMeta(1)
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.infused_fire)
				.input(OrePrefix.dust, PollutionMaterials.infused_water)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(18))
				.circuitMeta(1)
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		//不纯魔力+铁粉 高炉烧制魔力钢锭
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Iron)
				.fluidInputs(PollutionMaterials.impuremana.getFluid(144))
				.output(OrePrefix.ingot, PollutionMaterials.manasteel)
				.blastFurnaceTemp(1800)
				.duration(400)
				.EUt(120)
				.buildAndRegister();
		//世界盐 搅拌机配方
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.infused_air)
				.input(OrePrefix.dust, PollutionMaterials.infused_fire)
				.input(OrePrefix.dust, PollutionMaterials.infused_water)
				.input(OrePrefix.dust, PollutionMaterials.infused_earth)
				.input(OrePrefix.dust, PollutionMaterials.infused_order)
				.input(OrePrefix.dust, PollutionMaterials.infused_entropy)
				.fluidInputs(Redstone.getFluid(288))
				.output(OrePrefix.dust, PollutionMaterials.salismundus, 6)
				.duration(600)
				.EUt(120)
				.buildAndRegister();
		//3魔力钢粉+2神秘粉+1世界盐粉=6漫宿钢粉
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.manasteel, 3)
				.input(OrePrefix.dust, PollutionMaterials.thaumium, 2)
				.input(OrePrefix.dust, PollutionMaterials.salismundus)
				.output(OrePrefix.dust, PollutionMaterials.mansussteel, 6)
				.duration(600)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.manasteel, 3)
				.input(OrePrefix.dust, PollutionMaterials.thaumium, 2)
				.input(ItemsTC.salisMundus)
				.output(OrePrefix.dust, PollutionMaterials.mansussteel, 6)
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
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.AAMINATED_GLASS, 2),
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
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.BAMINATED_GLASS, 2),
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
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.CAMINATED_GLASS, 2),
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
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.DAMINATED_GLASS, 2),
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
				PollutionMetaBlocks.GLASS.getItemVariant(POGlass.MagicBlockType.LAMINATED_GLASS, 2),
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
	}
}
