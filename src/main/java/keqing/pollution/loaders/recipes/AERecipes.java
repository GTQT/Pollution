package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.util.GTUtility;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.Pollution;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static keqing.pollution.api.utils.ae2Index.*;
import static keqing.pollution.api.utils.ae2Index.fluidCell4k;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

public class AERecipes {
	public static void init() {
		common();  //注册控制器啊 驱动器啊 合成存储器之类的方块
		iohatch();//注册接口啊 样板总线 面板之类的非实体方块
		disk();//注册硬盘
		magic_assembler();

	}

	private static ItemStack item(String name){
		return GameRegistry.makeItemStack(name,0, 1,null);
	}
	private static ItemStack item(String name, int meta){
		return GameRegistry.makeItemStack(name,meta, 1,null);
	}

	/*
	提前学习 关于物品
	神秘需要的物品可以使用 "screwStainlessSteel" 的矿辞形式
	或者 new ItemStack(ItemsTC.mirroredGlass))) 的物品形式

	需要注意的是gt的物品需要进行格式转换 写法如下 你需要去GT的MetaItems类里寻找物品
	 new ItemStack(MetaItems.ELECTRIC_MOTOR_LV.getMetaItem()),
	 MetaItems.ELECTRIC_MOTOR_LV表示的是物品  .getMetaItem()后缀为格式转换

	AE的物品写法
	 AEApi.instance().definitions().blocks().iface().maybeStack(1).orElse(ItemStack.EMPTY),
	 这是一个AE接口
	   AE的物品写法极其神奇，
	  AEApi.instance().definitions().blocks()这里是前缀 .maybeStack(1).orElse(ItemStack.EMPTY),这里是前缀这里是后缀
	  打开package appeng.api.definitions.IBlocks你就能看到AE的物品拉
	  例如 quartzOre()表示赛特斯水晶矿，写成物品形式就是
	   AEApi.instance().definitions().blocks().quartzOre().maybeStack(1).orElse(ItemStack.EMPTY),

	  神秘物品写法
	   new ItemStack(ItemsTC.mirroredGlass)));太简单了 去ItemsTC找物品就行

	案例 奥数工作台
	ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Pollution.MODID, "test"), new ShapedArcaneRecipe(
	这一行的 “test” 部分是配方ID，需要每个之间都不一样，最好使用物品的id
				new ResourceLocation(""),
				"",
				250,
				需要的灵气=250

				new AspectList().add(Aspect.AIR, 2).add(Aspect.ORDER, 2).add(Aspect.FIRE, 2),
				需要的元素

				new ItemStack(MetaItems.ELECTRIC_MOTOR_LV.getMetaItem()),
				输出物品

				以下是你熟悉的合成表
				"SBS",
				"BMB",
				"SBS",
				'S', "screwStainlessSteel",
				'B', "plateSteel",
				'M', new ItemStack(ItemsTC.mirroredGlass)));


	 案例 坩埚
	ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(Refstrings.MODID, "generator_air_upgrade"), new CrucibleRecipe(
				// "MACHINE_UPGRADE",
				"",
				new ItemStack(CommonProxy.Upgrades, 1, 1),
				new ItemStack(CommonProxy.Upgrades, 1, 0),
				输入输出物品

				new AspectList().add(Aspect.AIR, 50)));
				生成的元素






		案例 铸模
	 ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Refstrings.MODID, "essentis_cell_basic"), new InfusionRecipe(
				// "LARGE_ESSENTIA_GENERATOR",
				"",
				new ItemStack(EGMetaBlocks.EG_ESSENTIA_CELL),
				输出的物品

				4,
				new AspectList().add(Aspect.WATER, 50).add(Aspect.VOID, 50).add(Aspect.EXCHANGE, 25),


				以下为需要的物品
				"gearAluminium",
				MetaItems.ELECTRIC_PUMP_HV.getStackForm(),
				"plateStainlessSteel",
				"plateStainlessSteel",
				"pipeSmallFluidStainlessSteel",
				new ItemStack(BlocksTC.tubeBuffer),
				new ItemStack(MetaBlocks.TRANSPARENT_CASING)));
	 */
	private static void iohatch() {
	}

	private static void disk() {
	}

	private static void common() {
        //石英纤维
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS_PANE, 6)
				.input(OrePrefix.dust, NetherQuartz, 3)
				.outputs(GTUtility.copy(4, siliconFiber))
				.duration(100)
				.EUt(16)
				.buildAndRegister();

		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS_PANE, 6)
				.input(OrePrefix.dust, CertusQuartz, 3)
				.outputs(GTUtility.copy(8, siliconFiber))
				.duration(100)
				.EUt(16)
				.buildAndRegister();

		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS_PANE, 6)
				.input(OrePrefix.dust, NetherQuartz, 3)
				.input(OrePrefix.wireFine, BorosilicateGlass, 8)
				.circuitMeta(1)
				.outputs(GTUtility.copy(8, siliconFiber))
				.duration(100)
				.EUt(16)
				.buildAndRegister();

		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
				.input(Blocks.GLASS_PANE, 6)
				.input(OrePrefix.dust, CertusQuartz, 3)
				.input(OrePrefix.wireFine, BorosilicateGlass, 8)
				.circuitMeta(1)
				.outputs(GTUtility.copy(16, siliconFiber))
				.duration(100)
				.EUt(16)
				.buildAndRegister();

		//石英玻璃
        RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(Blocks.GLASS, 4)
				.input(OrePrefix.dust, CertusQuartz, 1)
				.outputs(GTUtility.copy(8, quartzGlass))
				.duration(100)
				.blastFurnaceTemp(1800)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(Blocks.GLASS, 4)
				.input(OrePrefix.dust, NetherQuartz, 1)
				.outputs(GTUtility.copy(8, quartzGlass))
				.duration(100)
				.blastFurnaceTemp(1800)
				.EUt(120)
				.buildAndRegister();

        //聚能石英玻璃
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.inputs(GTUtility.copy(1, quartzGlass))
				.input(OrePrefix.dust, GTQTMaterials.Fluix, 2)
				.outputs(GTUtility.copy(1, vibrantGlass))
				.duration(100)
				.blastFurnaceTemp(1800)
				.EUt(120)
				.buildAndRegister();

		//奥术工作台破坏核心
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "breaking"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.FIRE, 1),
				GTUtility.copy(1, breaking),
				"DAD",
				"BCB",
				"DAD",
				'A', "plateThaumium",
				'B', "dustFluix",
				'C', "circuitMv",
				'D', "plateNetherQuartz"));
		//奥术工作台成型核心
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "forming"),  new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.WATER, 1),
				GTUtility.copy(1, forming),
				"DAD",
				"BCB",
				"DAD",
				'A', "plateThaumium",
				'B', "dustFluix",
				'C', "circuitMv",
				'D', "plateCertusQuartz"));

		//注魔me输入输出总线
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "me_input"), new InfusionRecipe(
				"INFUSION@2",
				GTUtility.copy(1, inputBus),
				4,
				new AspectList().add(Aspect.WATER, 16).add(Aspect.ENTROPY, 16),
				GTUtility.copy(1, breaking),
				"frameGtMansussteel",
				Blocks.STICKY_PISTON,
				"plateThaumium",
				MetaItems.CONVEYOR_MODULE_MV.getStackForm(),
				MetaItems.ITEM_FILTER.getStackForm()));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "me_output"), new InfusionRecipe(
				"INFUSION@2",
				GTUtility.copy(1, inputBus),
				4,
				new AspectList().add(Aspect.FIRE, 16).add(Aspect.ORDER, 16),
				GTUtility.copy(1, forming),
				"frameGtMansussteel",
				Blocks.PISTON,
				"plateThaumium",
				MetaItems.CONVEYOR_MODULE_MV.getStackForm(),
				MetaItems.ITEM_FILTER.getStackForm()));

        //注魔me流体输入输出总线
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "me_fluid_input"), new InfusionRecipe(
				"INFUSION@2",
				GTUtility.copy(1, fluidInputBus),
				4,
				new AspectList().add(Aspect.WATER, 16).add(Aspect.ENTROPY, 16),
				GTUtility.copy(1, breaking),
				"frameGtMansussteel",
				Blocks.STICKY_PISTON,
				"plateLapis",
				MetaItems.ELECTRIC_PUMP_MV.getStackForm(),
				MetaItems.FLUID_FILTER.getStackForm()));
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "me_fluid_output"), new InfusionRecipe(
				"INFUSION@2",
				GTUtility.copy(1, fluidOutputBus),
				4,
				new AspectList().add(Aspect.FIRE, 16).add(Aspect.ORDER, 16),
				GTUtility.copy(1, forming),
				"frameGtMansussteel",
				Blocks.PISTON,
				"plateLapis",
				MetaItems.ELECTRIC_PUMP_MV.getStackForm(),
				MetaItems.FLUID_FILTER.getStackForm()));

		//注魔me能源接收器
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "energy_acceptor"), new InfusionRecipe(
				"INFUSION@2",
				GTUtility.copy(1, energyAcceptor),
				6,
				new AspectList().add(Aspect.ENERGY, 32).add(Aspect.MAGIC, 16).add(Aspect.ORDER, 16).add(Aspect.CRAFT, 16),
				MetaTileEntities.ENERGY_INPUT_HATCH[MV].getStackForm(),
				"frameGtAluminium",
				"plateThaumium",
				"plateThaumium",
				GTUtility.copy(1, quartzGlass),
				GTUtility.copy(1, quartzGlass)));

        //注魔me控制器
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Pollution.MODID, "me_controller"), new InfusionRecipe(
				"INFUSION@2",
				GTUtility.copy(1, controller),
				6,
				new AspectList().add(Aspect.ENERGY, 32).add(Aspect.MAGIC, 16).add(Aspect.ORDER, 16).add(Aspect.CRAFT, 16),
				GTUtility.copy(1, energyAcceptor),
				"frameGtStainlessSteel",
				ItemsTC.morphicResonator,
				ItemsTC.visResonator,
				"plateMansussteel",
				"plateMansussteel",
				GTUtility.copy(1, vibrantGlass),
				GTUtility.copy(1, vibrantGlass)));

		//照明面板
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "light_panel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				10,
				new AspectList().add(Aspect.FIRE, 1),
				GTUtility.copy(1, lightPanel),
				"DAD",
				"BCB",
				"DAD",
				'A', "dustGlowstone",
				'B', "plateThaumium",
				'C', "dustRedstone",
				'D', GTUtility.copy(1, quartzGlass)));

        //me终端
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "me_panel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				20,
				new AspectList().add(Aspect.ORDER, 1),
				GTUtility.copy(1, mePanel),
				"AB ",
				"CD ",
				"   ",
				'A', GTUtility.copy(1, forming),
				'B', GTUtility.copy(1, lightPanel),
				'C', "circuitMv",
				'D', GTUtility.copy(1, breaking)));

        //me合成终端
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "me_crafting_panel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				20,
				new AspectList().add(Aspect.ORDER, 1),
				GTUtility.copy(1, mePatternTerminalEx),
				"AB ",
				"CD ",
				"   ",
				'A', GTUtility.copy(1, forming),
				'B', GTUtility.copy(1, mePanel),
				'C', Blocks.CRAFTING_TABLE,
				'D', GTUtility.copy(1, breaking)));

        //me流体终端
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "me_crafting_panel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				20,
				new AspectList().add(Aspect.ORDER, 1).add(Aspect.WATER, 1),
				GTUtility.copy(1, meFluidTerminal),
				"AB ",
				"CD ",
				"   ",
				'A', GTUtility.copy(1, forming),
				'B', GTUtility.copy(1, mePanel),
				'C', "plateLapis",
				'D', GTUtility.copy(1, breaking)));

		//me样板终端
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "me_pattern_panel"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				20,
				new AspectList().add(Aspect.ORDER, 1),
				GTUtility.copy(1, mePatternTerminal),
				"AB ",
				"CD ",
				"   ",
				'A', GTUtility.copy(1, engineeringProcessor),
				'B', GTUtility.copy(1, mePanel),
				'C', "plateMansussteel",
				'D', GTUtility.copy(1, blankPattern)));

		//me箱子
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "me_chest"), new ShapedArcaneRecipe(
				new ResourceLocation(""),
				"FIRSTSTEPS@2",
				25,
				new AspectList().add(Aspect.ORDER, 2).add(Aspect.EARTH, 2),
				GTUtility.copy(1, chest),
				"DAD",
				"ECE",
				"DBD",
				'A', GTUtility.copy(1, mePanel),
				'B', "gemValonite",
				'C', Blocks.CHEST,
				'D', "plateThaumium",
		        'E', GTUtility.copy(1, quartzGlass)));

		// 注魔me存储总线
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:part.220"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:part", 220),
						4,
						new AspectList()
								.add(Aspect.MOTION, 10)
								.add(Aspect.ORDER, 10),
						item("appliedenergistics2:chest"),
						item("thaumcraft:morphic_resonator"),
						item("appliedenergistics2:part", 240),
						item("appliedenergistics2:part", 260)
				)
		);

		// 注魔me流体存储总线
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:part.221"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:part", 221),
						4,
						new AspectList()
								.add(Aspect.MOTION, 10)
								.add(Aspect.ORDER, 10),
						item("appliedenergistics2:chest"),
						item("thaumcraft:morphic_resonator"),
						item("appliedenergistics2:part", 241),
						item("appliedenergistics2:part", 261)
				)
		);

		// 注魔me接口
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:interface"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:interface"),
						4,
						new AspectList()
								.add(Aspect.MOTION, 10)
								.add(Aspect.ORDER, 10),
						"frameGtMansussteel",
						"plateThaumium",
						"plateThaumium",
						item("appliedenergistics2:part", 240),
						item("appliedenergistics2:part", 260),
						item("appliedenergistics2:quartz_glass"),
						item("appliedenergistics2:quartz_glass")
				)
		);

		// 注魔me流体接口
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:fluid_interface"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:fluid_interface"),
						4,
						new AspectList()
								.add(Aspect.MOTION, 10)
								.add(Aspect.ORDER, 10)
								.add(Aspect.WATER, 10),
						"frameGtMansussteel",
						"plateLapis",
						"plateLapis",
						item("appliedenergistics2:part", 241),
						item("appliedenergistics2:part", 261),
						item("appliedenergistics2:quartz_glass"),
						item("appliedenergistics2:quartz_glass")
				)
		);

		// 注魔me驱动器
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:drive"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:drive"),
						6,
						new AspectList()
								.add(Aspect.VOID, 64)
								.add(Aspect.ORDER, 20),
						item("appliedenergistics2:chest"),
						"plateMansussteel",
						"plateMansussteel",
						"circuitHv",
						item("gregtech:machine", 1561)
				)
		);

		// 注魔分子装配室
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:molecular_assembler"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:molecular_assembler"),
						8,
						new AspectList()
								.add(Aspect.MIND, 64)
								.add(Aspect.MAN, 8)
								.add(Aspect.CRAFT, 32),
						"workbench",
						item("appliedenergistics2:quartz_vibrant_glass"),
						item("appliedenergistics2:quartz_vibrant_glass"),
						item("gregtech:machine", 111),
						"circuitHv",
						item("appliedenergistics2:material", 43),
						item("appliedenergistics2:material", 44)
				)
		);

		// 1k元件
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:material.35"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:material", 35),
						2,
						new AspectList()
								.add(Aspect.VOID, 8)
								.add(Aspect.ORDER, 8),
						item("thaumcraft:vis_resonator"),
						"frameGtSteel",
						"crystalCertusQuartz",
						"crystalCertusQuartz",
						"plateThaumium",
						"plateThaumium",
						"circuitLv"
				)
		);

		// 4k元件
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:material.36"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:material", 36),
						4,
						new AspectList()
								.add(Aspect.VOID, 16)
								.add(Aspect.ORDER, 16),
						item("thaumcraft:vis_resonator"),
						"frameGtAluminium",
						"crystalCertusQuartz",
						"crystalCertusQuartz",
						"plateThaumium",
						"plateThaumium",
						"circuitMv"
				)
		);

		// 16k元件
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:material.37"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:material", 37),
						6,
						new AspectList()
								.add(Aspect.VOID, 32)
								.add(Aspect.ORDER, 32),
						item("thaumcraft:morphic_resonator"),
						"frameGtStainlessSteel",
						"crystalCertusQuartz",
						"crystalCertusQuartz",
						"plateMansussteel",
						"plateMansussteel",
						"circuitHv"
				)
		);

		// 64k元件
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:material.38"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:material", 38),
						8,
						new AspectList()
								.add(Aspect.VOID, 64)
								.add(Aspect.ORDER, 64),
						item("thaumcraft:morphic_resonator"),
						"frameGtTitanium",
						"crystalCertusQuartz",
						"crystalCertusQuartz",
						"plateMansussteel",
						"plateMansussteel",
						"circuitEv"
				)
		);

		// 致密能量单元
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:dense_energy_cell"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:dense_energy_cell"),
						6,
						new AspectList().add(Aspect.ENERGY, 256),
						item("thaumcraft:vis_battery"),
						item("appliedenergistics2:energy_cell"),
						item("appliedenergistics2:energy_cell"),
						item("appliedenergistics2:energy_cell"),
						item("appliedenergistics2:energy_cell"),
						item("appliedenergistics2:energy_cell"),
						item("appliedenergistics2:energy_cell"),
						item("appliedenergistics2:energy_cell"),
						item("appliedenergistics2:energy_cell")
				)
		);

		// fluix珍珠
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:material.9"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:material", 9),
						4,
						new AspectList()
								.add(Aspect.VOID, 16)
								.add(Aspect.ENERGY, 16),
						"enderpearl",
						"dustFluix",
						"dustFluix",
						"dustFluix",
						"dustFluix",
						"dustFluix",
						"dustFluix",
						"dustFluix",
						"dustFluix"
				)
		);

		// 无线接收器
		ThaumcraftApi.addInfusionCraftingRecipe(
				new ResourceLocation(Pollution.MODID, "autoGrS.appliedenergistics2:material.41"),
				new InfusionRecipe(
						"INFUSION@2",
						item("appliedenergistics2:material", 41),
						4,
						new AspectList()
								.add(Aspect.VOID, 16)
								.add(Aspect.MAGIC, 16)
								.add(Aspect.ELDRITCH, 32),
						item("appliedenergistics2:material", 9),
						item("appliedenergistics2:part", 140),
						item("appliedenergistics2:part", 140),
						item("appliedenergistics2:part", 140),
						item("appliedenergistics2:part", 140),
						"plateThaumium",
						"plateThaumium",
						item("gregtech:meta_item_1", 218)
				)
		);

		// 合成单元
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation(Thaumcraft.MODID, "autoGrS.appliedenergistics2:crafting_unit"),
				new ShapedArcaneRecipe(
						new ResourceLocation(""),
						"FIRSTSTEPS@2",
						25,
						new AspectList().add(Aspect.AIR, 1)
								.add(Aspect.FIRE, 1)
								.add(Aspect.WATER, 1)
								.add(Aspect.EARTH, 1)
								.add(Aspect.ORDER, 1)
								.add(Aspect.ENTROPY, 1),
						item("appliedenergistics2:crafting_unit"),
						"DAD", "BCB", "DAD",
						'A', item("appliedenergistics2:quartz_vibrant_glass"),
						'B', "circuitLv",
						'C', "workbench",
						'D', "plateThaumium"
				)
		);

		// 能量单元
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation(Thaumcraft.MODID, "autoGrS.appliedenergistics2:energy_cell"),
				new ShapedArcaneRecipe(
						new ResourceLocation(""),
						"FIRSTSTEPS@2",
						50,
						new AspectList().add(Aspect.FIRE, 3)
								.add(Aspect.ORDER, 3),
						item("appliedenergistics2:energy_cell"),
						"DAD", "BCB", "DAD",
						'A', "crystalCertusQuartz",
						'B', "circuitLv",
						'C', item("thaumcraft:vis_battery"),
						'D', "plateThaumium"
				)
		);

		// me安全终端
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation(Thaumcraft.MODID, "autoGrS.appliedenergistics2:security_station"),
				new ShapedArcaneRecipe(
						new ResourceLocation(""),
						"FIRSTSTEPS@2",
						100,
						new AspectList().add(Aspect.AIR, 4)
								.add(Aspect.FIRE, 4)
								.add(Aspect.WATER, 4)
								.add(Aspect.EARTH, 4)
								.add(Aspect.ORDER, 4)
								.add(Aspect.ENTROPY, 4),
						item("appliedenergistics2:security_station"),
						"DAD", "BCB", "DAD",
						'A', item("appliedenergistics2:chest"),
						'B', "circuitHv",
						'C', item("appliedenergistics2:material", 37),
						'D', "plateThaumium"
				)
		);
		// 无线终端
		ThaumcraftApi.addArcaneCraftingRecipe(
				new ResourceLocation(Thaumcraft.MODID, "autoGrS.appliedenergistics2:wireless_terminal"),
				new ShapedArcaneRecipe(
						new ResourceLocation(""),
						"FIRSTSTEPS@2",
						150,
						new AspectList().add(Aspect.AIR, 6)
								.add(Aspect.FIRE, 6)
								.add(Aspect.WATER, 6)
								.add(Aspect.EARTH, 6)
								.add(Aspect.ORDER, 6)
								.add(Aspect.ENTROPY, 6),
						item("appliedenergistics2:wireless_terminal"),
						"DAD", "BCB", "DED",
						'A', "circuitHv",
						'B', item("appliedenergistics2:material", 41),
						'C', item("appliedenergistics2:part", 380),
						'D', "plateThaumium",
						'E', item("appliedenergistics2:dense_energy_cell")
				)
		);
	    }

	private static void magic_assembler() {
	//me接口 1刻金框架+两核心+6钛板=16个
    //me流体接口 1秘银框架+两核心+6钛板=16个
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, Titanium, 6)
				.input(OrePrefix.frameGt, PollutionMaterials.keqinggold)
				.inputs(GTUtility.copy(1, forming))
				.inputs(GTUtility.copy(1, breaking))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(16, meInterface))
				.duration(160)
				.circuitMeta(20)
				.EUt(1920)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, Titanium, 6)
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver)
				.inputs(GTUtility.copy(1, forming))
				.inputs(GTUtility.copy(1, breaking))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(16, meFluidInterface))
				.duration(160)
				.circuitMeta(20)
				.EUt(1920)
				.buildAndRegister();

//核心
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, CertusQuartz, 6)
				.input(OrePrefix.dust, GTQTMaterials.Fluix)
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(16, breaking))
				.duration(160)
				.circuitMeta(1)
				.EUt(480)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, NetherQuartz, 6)
				.input(OrePrefix.dust, GTQTMaterials.Fluix)
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(16, forming))
				.duration(160)
				.circuitMeta(1)
				.EUt(480)
				.buildAndRegister();

//三种电路板
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(GTUtility.copy(4, logicBase))
				.input(OrePrefix.plate, RedAlloy)
				.fluidInputs(PollutionMaterials.mansussteel.getFluid(144))
				.outputs(GTUtility.copy(16, logicProcessor))
				.duration(160)
				.EUt(480)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(GTUtility.copy(4, engineeringBase))
				.input(OrePrefix.plate, RedAlloy)
				.fluidInputs(PollutionMaterials.mansussteel.getFluid(144))
				.outputs(GTUtility.copy(16, engineeringProcessor))
				.duration(160)
				.EUt(480)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(GTUtility.copy(4, calculationBase))
				.input(OrePrefix.plate, RedAlloy)
				.fluidInputs(PollutionMaterials.mansussteel.getFluid(144))
				.outputs(GTUtility.copy(16, calculationProcessor))
				.duration(160)
				.EUt(480)
				.buildAndRegister();

//分子装配室 1刻金框架+4玻璃+2核心各2=4
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(GTUtility.copy(4, quartzGlass))
				.input(OrePrefix.frameGt, PollutionMaterials.keqinggold)
				.inputs(GTUtility.copy(2, forming))
				.inputs(GTUtility.copy(2, breaking))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(4, molecularAssembler))
				.circuitMeta(21)
				.duration(400)
				.EUt(7680)
				.buildAndRegister();

//p2p
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(GTUtility.copy(2, meGlassCable))
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver)
				.inputs(GTUtility.copy(engineeringProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(16, p2p))
				.circuitMeta(21)
				.duration(100)
				.EUt(1920)
				.buildAndRegister();

//玻璃线缆
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(GTUtility.copy(16, siliconFiber))
				.input(OrePrefix.gem, GTQTMaterials.Fluix)
				.fluidInputs(PollutionMaterials.mansussteel.getFluid(144))
				.outputs(GTUtility.copy(64, meGlassCable))
				.duration(100)
				.EUt(480)
				.buildAndRegister();

//me驱动器
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, Titanium, 4)
				.input(OrePrefix.frameGt, PollutionMaterials.keqinggold)
				.inputs(GTUtility.copy(2, meGlassCable))
				.inputs(GTUtility.copy(2, logicProcessor))
				.inputs(GTUtility.copy(2, engineeringProcessor))
				.fluidInputs(PollutionMaterials.mansussteel.getFluid(144))
				.outputs(GTUtility.copy(8, drive))
				.duration(400)
				.circuitMeta(20)
				.EUt(1920)
				.buildAndRegister();

//me控制器
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, TungstenSteel, 4)
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver)
				.inputs(GTUtility.copy(16, siliconFiber))
				.inputs(GTUtility.copy(4, calculationProcessor))
				.fluidInputs(PollutionMaterials.mansussteel.getFluid(144))
				.outputs(GTUtility.copy(8, controller))
				.duration(400)
				.circuitMeta(20)
				.EUt(1920)
				.buildAndRegister();

//存储组件
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.CENTRAL_PROCESSING_UNIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.LV, 4)
				.inputs(GTUtility.copy(1, logicProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(4, cell1k))
				.duration(320)
				.circuitMeta(22)
				.EUt(30)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.RANDOM_ACCESS_MEMORY)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.MV, 4)
				.inputs(GTUtility.copy(2, logicProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(200))
				.outputs(GTUtility.copy(4, cell4k))
				.duration(320)
				.circuitMeta(22)
				.EUt(120)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.ULTRA_LOW_POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.HV, 4)
				.inputs(GTUtility.copy(4, logicProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(400))
				.outputs(GTUtility.copy(4, cell16k))
				.duration(320)
				.circuitMeta(22)
				.EUt(480)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.LOW_POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.EV, 4)
				.inputs(GTUtility.copy(8, logicProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(800))
				.outputs(GTUtility.copy(4, cell64k))
				.duration(320)
				.circuitMeta(22)
				.EUt(1920)
				.buildAndRegister();

//256k+组件
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.IV, 4)
				.inputs(GTUtility.copy(16, logicProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(1600))
				.outputs(GameRegistry.makeItemStack("nae2:material", 19, 4, null))
				.duration(320)
				.circuitMeta(22)
				.EUt(VA[IV])
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.LuV, 4)
				.inputs(GTUtility.copy(32, logicProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(3200))
				.outputs(GameRegistry.makeItemStack("nae2:material", 20, 4, null))
				.duration(320)
				.circuitMeta(22)
				.EUt(VA[LuV])
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.ZPM, 4)
				.inputs(GTUtility.copy(64, logicProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(6400))
				.outputs(GameRegistry.makeItemStack("nae2:material", 21, 4, null))
				.duration(320)
				.circuitMeta(22)
				.EUt(VA[ZPM])
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(GTQTMetaItems.NANO_POWER_IC)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.UV, 4)
				.inputs(GTUtility.copy(64, logicProcessor))
				.inputs(GTUtility.copy(64, logicProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(12800))
				.outputs(GameRegistry.makeItemStack("nae2:material", 22, 4, null))
				.duration(320)
				.circuitMeta(22)
				.EUt(VA[UV])
				.buildAndRegister();


//流体
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.CENTRAL_PROCESSING_UNIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.LV, 4)
				.inputs(GTUtility.copy(1, calculationProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(4, fluidCell1k))
				.duration(320)
				.circuitMeta(23)
				.EUt(30)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.RANDOM_ACCESS_MEMORY)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.MV, 4)
				.inputs(GTUtility.copy(2, calculationProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(200))
				.outputs(GTUtility.copy(4, fluidCell4k))
				.duration(320)
				.circuitMeta(23)
				.EUt(120)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.ULTRA_LOW_POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.HV, 4)
				.inputs(GTUtility.copy(4, calculationProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(400))
				.outputs(GTUtility.copy(4, fluidCell16k))
				.duration(320)
				.circuitMeta(23)
				.EUt(480)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.LOW_POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.EV, 4)
				.inputs(GTUtility.copy(8, calculationProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(800))
				.outputs(GTUtility.copy(4, fluidCell64k))
				.duration(320)
				.circuitMeta(23)
				.EUt(1920)
				.buildAndRegister();

//256k+流体组件
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.IV, 4)
				.inputs(GTUtility.copy(16, calculationProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(1600))
				.outputs(GameRegistry.makeItemStack("nae2:material", 24, 4, null))
				.duration(320)
				.circuitMeta(23)
				.EUt(VA[IV])
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.LuV, 4)
				.inputs(GTUtility.copy(32, calculationProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(3200))
				.outputs(GameRegistry.makeItemStack("nae2:material", 25, 4, null))
				.duration(320)
				.circuitMeta(23)
				.EUt(VA[LuV])
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.ZPM, 4)
				.inputs(GTUtility.copy(64, calculationProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(6400))
				.outputs(GameRegistry.makeItemStack("nae2:material", 26, 4, null))
				.duration(320)
				.circuitMeta(23)
				.EUt(VA[ZPM])
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(GTQTMetaItems.NANO_POWER_IC)
				.input(OrePrefix.circuit, MarkerMaterials.Tier.UV, 4)
				.inputs(GTUtility.copy(64, calculationProcessor))
				.inputs(GTUtility.copy(64, calculationProcessor))
				.fluidInputs(GTQTMaterials.Magic.getFluid(12800))
				.outputs(GameRegistry.makeItemStack("nae2:material", 27, 4, null))
				.duration(320)
				.circuitMeta(23)
				.EUt(VA[UV])
				.buildAndRegister();

//输入输出总线 4种
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, Titanium, 4)
				.input(OrePrefix.frameGt, PollutionMaterials.keqinggold)
				.inputs(GTUtility.copy(1, meInterface))
				.inputs(GTUtility.copy(1, breaking))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(8, inputBus))
				.duration(160)
				.circuitMeta(18)
				.EUt(480)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, Titanium, 4)
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver)
				.inputs(GTUtility.copy(1, meInterface))
				.inputs(GTUtility.copy(1, forming))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(8, outputBus))
				.duration(160)
				.circuitMeta(19)
				.EUt(480)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, Titanium, 4)
				.input(OrePrefix.frameGt, PollutionMaterials.keqinggold)
				.inputs(GTUtility.copy(1, meFluidInterface))
				.inputs(GTUtility.copy(1, breaking))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(8, fluidInputBus))
				.duration(160)
				.circuitMeta(18)
				.EUt(480)
				.buildAndRegister();

		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(OrePrefix.plate, Titanium, 4)
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver)
				.inputs(GTUtility.copy(1, meFluidInterface))
				.inputs(GTUtility.copy(1, forming))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.outputs(GTUtility.copy(8, fluidOutputBus))
				.duration(160)
				.circuitMeta(19)
				.EUt(480)
				.buildAndRegister();
	}
}
