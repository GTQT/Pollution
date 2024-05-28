package keqing.pollution.loaders.recipes;

public class AERecipes {
	public static void init() {
		common();  //注册控制器啊 驱动器啊 合成存储器之类的方块
		iohatch();//注册接口啊 样板总线 面板之类的非实体方块
		disk();//注册硬盘

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

	}
}
