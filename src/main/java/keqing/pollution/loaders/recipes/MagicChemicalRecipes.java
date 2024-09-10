package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtechfoodoption.item.GTFOMetaItem;
import keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps;
import keqing.gtqtcore.api.unification.GCYSMaterials;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.api.unification.TJMaterials;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;

public class MagicChemicalRecipes {
	public static void init() {
		chemical();
		kqt_chain();
	}

	private static void chemical() {

		//四种催化剂的更简单的配方
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2))
				.fluidInputs(PollutionMaterials.infused_fire.getFluid(2304))
				.notConsumable(new ItemStack(PollutionMetaItems.HOTCORE.getMetaItem(), 1, 3))
				.outputs(new ItemStack(PollutionMetaItems.HOTCORE.getMetaItem(), 1, 3))
				.duration(3600)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2))
				.fluidInputs(PollutionMaterials.infused_cold.getFluid(2304))
				.notConsumable(new ItemStack(PollutionMetaItems.COLDCORE.getMetaItem(), 1, 4))
				.outputs(new ItemStack(PollutionMetaItems.COLDCORE.getMetaItem(), 1, 4))
				.duration(3600)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2))
				.fluidInputs(PollutionMaterials.infused_alchemy.getFluid(2304))
				.notConsumable(new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5))
				.outputs(new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5))
				.duration(3600)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.inputs(new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2))
				.fluidInputs(PollutionMaterials.infused_alchemy.getFluid(2304))
				.notConsumable(new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6))
				.outputs(new ItemStack(PollutionMetaItems.SEGREGATECORE.getMetaItem(), 1, 6))
				.duration(3600)
				.EUt(480)
				.buildAndRegister();
		//可燃冰→石墨烯+氢
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, GTQTMaterials.Gashydrate, 4)
				.output(OrePrefix.dust, Materials.Graphene, 1)
				.fluidOutputs(Materials.Methane.getFluid(9600))
				.fluidOutputs(Materials.Water.getFluid(4800))
				.duration(200)
				.EUt(30720)
				.buildAndRegister();
		//10H2O+N2+S=H2SO4+2HNO3+8H2, EV
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Materials.Sulfur, 1)
				.fluidInputs(Materials.Water.getFluid(10000))
				.fluidInputs(Materials.Nitrogen.getFluid(1000))
				.fluidOutputs(Materials.SulfuricAcid.getFluid(1000))
				.fluidOutputs(Materials.NitricAcid.getFluid(2000))
				.fluidOutputs(Materials.Hydrogen.getFluid(8000))
				.duration(200)
				.EUt(1920)
				.buildAndRegister();
		//6NaCl+2C+3N2+8H20+5H2=2NaOH+2Na2CO3+6NH4Cl, EV
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Materials.Carbon, 2)
				.input(OrePrefix.dust, Materials.Salt, 6)
				.fluidInputs(Materials.Water.getFluid(8000))
				.fluidInputs(Materials.Nitrogen.getFluid(3000))
				.fluidInputs(Materials.Hydrogen.getFluid(5000))
				.output(OrePrefix.dust, Materials.SodiumHydroxide, 2)
				.output(OrePrefix.dust, Materials.SodaAsh, 2)
				.output(OrePrefix.dust, Materials.AmmoniumChloride, 6)
				.duration(200)
				.EUt(1920)
				.buildAndRegister();

		//SiCl4+4C2H5OH=(C2H5O)4Si+4HCl
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(GCYSMaterials.SiliconTetrachloride.getFluid(1000))
				.fluidInputs(Materials.Ethanol.getFluid(4000))
				.fluidOutputs(PollutionMaterials.ethyl_silicate.getFluid(1000))
				.fluidOutputs(Materials.HydrochloricAcid.getFluid(4000))
				.circuitMeta(1)
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		//荷叶粉
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
				.input(Blocks.WATERLILY, 1)
				.output(OrePrefix.dust, PollutionMaterials.lotus_dust)
				.duration(200)
				.EUt(8)
				.buildAndRegister();
		//LLP粗胚
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.ethyl_silicate.getFluid(4000))
				.input(OrePrefix.dust, PollutionMaterials.lotus_dust, 4)
				.output(OrePrefix.dust, PollutionMaterials.rough_llp)
				.duration(400)
				.EUt(120)
				.buildAndRegister();
		//LLP@SiO2
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.rough_llp, 4)
				.fluidInputs(PollutionMaterials.infused_water.getFluid(2304))
				.output(OrePrefix.dust, PollutionMaterials.llp, 1)
				.duration(2000)
				.EUt(480)
				.buildAndRegister();
		//LLP原油悬浊液
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(Materials.RawOil.getFluid(1000))
				.input(OrePrefix.dust, PollutionMaterials.llp, 1)
				.fluidOutputs(PollutionMaterials.oil_with_llp.getFluid(1000))
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(Materials.OilLight.getFluid(1000))
				.input(OrePrefix.dust, PollutionMaterials.llp, 1)
				.fluidOutputs(PollutionMaterials.oil_with_llp.getFluid(1000))
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(Materials.OilHeavy.getFluid(1000))
				.input(OrePrefix.dust, PollutionMaterials.llp, 1)
				.fluidOutputs(PollutionMaterials.oil_with_llp.getFluid(1000))
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Oil.getFluid(1000))
				.input(OrePrefix.dust, PollutionMaterials.llp, 1)
				.fluidOutputs(PollutionMaterials.oil_with_llp.getFluid(1000))
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		//离心循环LLP
		RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.oil_with_llp.getFluid(1000))
				.output(OrePrefix.dust, PollutionMaterials.llp, 1)
				.fluidOutputs(GTQTMaterials.PreTreatedCrudeOil.getFluid(1500))
				.fluidOutputs(Materials.SaltWater.getFluid(200))
				.duration(20)
				.EUt(120)
				.buildAndRegister();
		//悖论物质
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.infused_entropy.getFluid(2304))
				.fluidInputs(PollutionMaterials.infused_energy.getFluid(2304))
				.input(ItemsTC.alumentum)
				.output(ItemsTC.causalityCollapser)
				.duration(200)
				.EUt(120)
				.buildAndRegister();
		//史莱姆产出：焦油、糖、甘油、胶水、橡胶、燃油（特殊）
		GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Biomass.getFluid(200))
				.notConsumable(new ItemStack(PollutionMetaItems.TARSLIME.getMetaItem(), 1, 20))
				.fluidOutputs(Materials.OilHeavy.getFluid(1000))
				.rate(10)
				.duration(60)
				.EUt(120)
				.buildAndRegister();
		GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Biomass.getFluid(200))
				.notConsumable(new ItemStack(PollutionMetaItems.SUGARSLIME.getMetaItem(), 1, 21))
				.output(Items.SUGAR, 16)
				.rate(10)
				.duration(60)
				.EUt(120)
				.buildAndRegister();
		GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Biomass.getFluid(200))
				.notConsumable(new ItemStack(PollutionMetaItems.GLYCEROLSLIME.getMetaItem(), 1, 23))
				.fluidOutputs(Materials.Glycerol.getFluid(1000))
				.rate(10)
				.duration(60)
				.EUt(120)
				.buildAndRegister();
		GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Biomass.getFluid(200))
				.notConsumable(new ItemStack(PollutionMetaItems.GLUESLIME.getMetaItem(), 1, 22))
				.fluidOutputs(Materials.Glue.getFluid(1000))
				.rate(10)
				.duration(60)
				.EUt(120)
				.buildAndRegister();
		GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Biomass.getFluid(200))
				.notConsumable(new ItemStack(PollutionMetaItems.RUBBERSLIME.getMetaItem(), 1, 24))
				.fluidOutputs(Materials.Rubber.getFluid(1000))
				.rate(10)
				.duration(60)
				.EUt(120)
				.buildAndRegister();
		GTQTcoreRecipeMaps.BIOLOGICAL_REACTION_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Biomass.getFluid(1000))
				.notConsumable(new ItemStack(PollutionMetaItems.TARSLIME.getMetaItem(), 1, 20))
				.fluidInputs(PollutionMaterials.infused_energy.getFluid(144))
				.fluidOutputs(Materials.Diesel.getFluid(1000))
				.rate(20)
				.duration(120)
				.EUt(120)
				.buildAndRegister();
		//纯化焦油+粘液球 搅拌
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(Items.SLIME_BALL, 4)
				.fluidInputs(PollutionMaterials.pure_tar.getFluid(1000))
				.fluidOutputs(PollutionMaterials.super_sticky_tar.getFluid(1000))
				.duration(200)
				.EUt(120)
				.buildAndRegister();
		//超粘稠焦油 魔导催化反应 焦油史莱姆
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.super_sticky_tar.getFluid(4000))
				.fluidInputs(PollutionMaterials.infused_life.getFluid(9216))
				.fluidInputs(PollutionMaterials.infused_soul.getFluid(576))
				.outputs(new ItemStack(PollutionMetaItems.TARSLIME.getMetaItem(), 1, 20))
				.duration(2000)
				.EUt(120)
				.buildAndRegister();
		//其他的史莱姆
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.inputs(new ItemStack(PollutionMetaItems.TARSLIME.getMetaItem(), 1, 20))
				.input(Items.SUGAR, 4)
				.outputs(new ItemStack(PollutionMetaItems.SUGARSLIME.getMetaItem(), 1, 21))
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.inputs(new ItemStack(PollutionMetaItems.TARSLIME.getMetaItem(), 1, 20))
				.fluidInputs(Materials.Glue.getFluid(1000))
				.outputs(new ItemStack(PollutionMetaItems.GLUESLIME.getMetaItem(), 1, 22))
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.inputs(new ItemStack(PollutionMetaItems.TARSLIME.getMetaItem(), 1, 20))
				.fluidInputs(Materials.Glycerol.getFluid(1000))
				.outputs(new ItemStack(PollutionMetaItems.GLYCEROLSLIME.getMetaItem(), 1, 23))
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.inputs(new ItemStack(PollutionMetaItems.TARSLIME.getMetaItem(), 1, 20))
				.fluidInputs(Materials.Rubber.getFluid(1000))
				.outputs(new ItemStack(PollutionMetaItems.RUBBERSLIME.getMetaItem(), 1, 24))
				.duration(100)
				.EUt(120)
				.buildAndRegister();
		//焦化催化剂核心
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(Thaumcraft.MODID, "coking_core"), new InfusionRecipe(
				"INFUSION@2",
				new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7),
				3,
				new AspectList().add(Aspect.ENERGY, 128).add(Aspect.AURA, 32),
				new ItemStack(PollutionMetaItems.BLANKCORE.getMetaItem(), 1, 2),
				"gemCoke",
				"gemCoke",
				"gemCoal",
				"gemCoal",
				new ItemStack(ItemsTC.alumentum),
				new ItemStack(ItemsTC.alumentum),
				new ItemStack(BlocksTC.crystalEntropy),
				new ItemStack(BlocksTC.crystalEntropy)));
		//糖——HMF
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.input(Items.SUGAR, 8)
				.notConsumable(Materials.DilutedSulfuricAcid.getFluid(1000))
				.notConsumable(TJMaterials.ZirconiumTetrachloride.getFluid(1000))
				.fluidOutputs(GTQTMaterials.Hydroxymethylfurfural.getFluid(1000))
				.duration(400)
				.EUt(30)
				.buildAndRegister();
		//HMF——2-甲基呋喃
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.notConsumable(OrePrefix.dust, Materials.Palladium)
				.notConsumable(OrePrefix.dust, Materials.SodiumBicarbonate, 8)
				.fluidInputs(GTQTMaterials.Hydroxymethylfurfural.getFluid(1000))
				.fluidInputs(Materials.Hydrogen.getFluid(10000))
				.fluidOutputs(GTQTMaterials.Methylfuran.getFluid(500))
				.duration(400)
				.EUt(120)
				.buildAndRegister();
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.notConsumable(OrePrefix.dust, PollutionMaterials.thaummix)
				.notConsumable(OrePrefix.dust, Materials.SodiumBicarbonate, 8)
				.fluidInputs(GTQTMaterials.Hydroxymethylfurfural.getFluid(1000))
				.fluidInputs(Materials.Hydrogen.getFluid(10000))
				.fluidOutputs(GTQTMaterials.Methylfuran.getFluid(500))
				.duration(400)
				.EUt(120)
				.buildAndRegister();
		//魔力抗爆焦化硝基苯
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.fluidInputs(GTQTMaterials.Methylfuran.getFluid(1000))
				.fluidInputs(Materials.Ethanol.getFluid(1000))
				.fluidInputs(Materials.Nitrobenzene.getFluid(10000))
				.fluidInputs(PollutionMaterials.infused_energy.getFluid(1152))
				.notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
				.fluidOutputs(PollutionMaterials.magic_nitrobenzene.getFluid(16000))
				.duration(200)
				.EUt(480)
				.buildAndRegister();
        //木头焦化不纯魔力
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
				.input(BlocksTC.logGreatwood, 16)
				.notConsumable(new ItemStack(PollutionMetaItems.HOTCORE.getMetaItem(),1, 3))
				.output(OrePrefix.dust, Materials.Ash, 4)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(576))
				.duration(400)
				.EUt(120)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
				.input(BlocksTC.logSilverwood, 8)
				.notConsumable(new ItemStack(PollutionMetaItems.HOTCORE.getMetaItem(),1, 3))
				.output(OrePrefix.dust, Materials.Ash, 4)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(576))
				.duration(400)
				.EUt(120)
				.buildAndRegister();
		//双氧水
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.INTEGRATECORE.getMetaItem(), 1, 5))
				.fluidInputs(Materials.Water.getFluid(2000))
				.fluidInputs(Materials.Oxygen.getFluid(1000))
				.fluidOutputs(GCYSMaterials.HydrogenPeroxide.getFluid(2000))
				.duration(200)
				.EUt(1920)
				.buildAndRegister();

		//世界盐（贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.dust, Materials.Salt, 10)
				.output(OrePrefix.dust, PollutionMaterials.salismundus, 1)
				.duration(1000)
				.EUt(30)
				.buildAndRegister();

		//红蓝石转换（贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.dust, Materials.Electrotine, 64)
				.output(OrePrefix.dust, Materials.Redstone, 64)
				.duration(100)
				.EUt(30)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.dust, Materials.Redstone, 64)
				.output(OrePrefix.dust, Materials.Electrotine, 64)
				.duration(1000)
				.EUt(30)
				.buildAndRegister();
		//僵尸脑（贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(Items.ROTTEN_FLESH, 1)
				.fluidInputs(PollutionMaterials.infused_death.getFluid(576))
				.fluidInputs(PollutionMaterials.infused_soul.getFluid(144))
				.output(ItemsTC.brain, 1)
				.duration(120)
				.EUt(120)
				.buildAndRegister();
		//海水提溴（贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.fluidInputs(GTQTMaterials.SeaWater.getFluid(16000))
				.fluidOutputs(Materials.Bromine.getFluid(100))
				.fluidOutputs(Materials.Iodine.getFluid(10))
				.output(OrePrefix.dust, Materials.Salt, 16)
				.duration(120)
				.EUt(1920)
				.buildAndRegister();
		//丙酮（贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.fluidInputs(Materials.Methane.getFluid(3000))
				.fluidInputs(Materials.Water.getFluid(1000))
				.fluidOutputs(Materials.Acetone.getFluid(1000))
				.fluidOutputs(Materials.Hydrogen.getFluid(4000))
				.circuitMeta(20)
				.duration(120)
				.EUt(480)
				.buildAndRegister();
		//工业制香蕉~（彩蛋，贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.dust, Materials.Barium)
				.input(OrePrefix.dust, Materials.Sodium, 2)
				.output(GTFOMetaItem.BANANA.getMetaItem(), 1, 122)
				.duration(120)
				.EUt(344)
				.buildAndRegister();
		//炽焰铁、赛摩铜、法罗钠（贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.gem, Materials.Diamond, 9)
				.fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(42))
				.fluidInputs(PollutionMaterials.infused_crystal.getFluid(1440))
				.output(OrePrefix.gem, PollutionMaterials.valonite, 1)
				.duration(500)
				.EUt(7680)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.dust, Materials.Iron, 4)
				.fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(6))
				.fluidInputs(PollutionMaterials.infused_fire.getFluid(576))
				.output(OrePrefix.dust, PollutionMaterials.octine, 1)
				.duration(240)
				.EUt(1920)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.gem, Materials.Copper, 4)
				.fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(6))
				.fluidInputs(PollutionMaterials.infused_instrument.getFluid(576))
				.output(OrePrefix.dust, PollutionMaterials.syrmorite, 1)
				.duration(240)
				.EUt(1920)
				.buildAndRegister();
		//贤者石复制（贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.block, PollutionMaterials.keqinggold, 1)
				.fluidInputs(PollutionMaterials.infused_alchemy.getFluid(14400))
				.outputs(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.duration(10000)
				.EUt(480)
				.buildAndRegister();
		//核心制作（贤者1）
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver, 1)
				.fluidInputs(PollutionMaterials.infused_energy.getFluid(576))
				.outputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_0))
				.circuitMeta(1)
				.duration(1000)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver, 1)
				.fluidInputs(PollutionMaterials.infused_energy.getFluid(576))
				.outputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_1))
				.circuitMeta(2)
				.duration(1000)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver, 1)
				.fluidInputs(PollutionMaterials.infused_energy.getFluid(576))
				.outputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_2))
				.circuitMeta(3)
				.duration(1000)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver, 1)
				.fluidInputs(PollutionMaterials.infused_energy.getFluid(576))
				.outputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_3))
				.circuitMeta(4)
				.duration(1000)
				.EUt(480)
				.buildAndRegister();
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.input(OrePrefix.frameGt, PollutionMaterials.hyperdimensional_silver, 1)
				.fluidInputs(PollutionMaterials.infused_energy.getFluid(576))
				.outputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_4))
				.circuitMeta(5)
				.duration(1000)
				.EUt(480)
				.buildAndRegister();
	}
	private static void kqt_chain(){

		//方铅矿矿粉+世界盐 搅拌 硫铅盐
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.salismundus, 1)
				.input(OrePrefix.dust, Materials.Galena, 2)
				.output(OrePrefix.dust, PollutionMaterials.sulfo_plumbic_salt, 3)
				.duration(300)
				.EUt(480)
				.buildAndRegister();

		//硫铅盐+不纯魔力 流体固化 蕴魔硫铅盐
		RecipeMaps.FLUID_SOLIDFICATION_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.sulfo_plumbic_salt, 1)
				.fluidInputs(PollutionMaterials.impuremana.getFluid(48))
				.output(OrePrefix.dust, PollutionMaterials.magical_sulfo_plumbic_salt, 1)
				.duration(100)
				.EUt(120)
				.buildAndRegister();

		//蕴魔硫铅盐+液态序 工业高炉 一次炼金残渣+一次升华蒸汽
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.magical_sulfo_plumbic_salt, 3)
				.fluidInputs(PollutionMaterials.infused_order.getFluid(432))
				.output(OrePrefix.dust, PollutionMaterials.alchemical_residue_1, 3)
				.fluidOutputs(PollutionMaterials.alchemical_vapor_1.getFluid(1000))
				.duration(1200)
				.blastFurnaceTemp(1600)
				.EUt(120)
				.buildAndRegister();

		//一次炼金残渣 离心 硫+铅+概率银
		RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.alchemical_residue_1, 3)
				.output(OrePrefix.dust, Materials.Sulfur,1)
				.output(OrePrefix.dust, Materials.Lead, 1)
				.chancedOutput(OrePrefix.dust, Materials.Silver, 1, 7000, 200)
				.output(OrePrefix.dust, PollutionMaterials.alchemical_residue_1, 3)
				.duration(100)
				.EUt(30)
				.buildAndRegister();

		//一次升华蒸汽+锡粉+四氯化碳 化反 神秘锡溶液
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.alchemical_vapor_1.getFluid(1000))
				.input(OrePrefix.dust, Materials.Tin, 3)
				.fluidInputs(GCYSMaterials.CarbonTetrachloride.getFluid(3000))
				.fluidOutputs(PollutionMaterials.magical_tin_solution.getFluid(3000))
				.duration(300)
				.EUt(480)
				.buildAndRegister();

		//神秘锡溶液+硫酸 化反 硫酸亚锡神秘溶液
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.magical_tin_solution.getFluid(1000))
				.fluidInputs(Materials.SulfuricAcid.getFluid(1000))
				.fluidOutputs(PollutionMaterials.magical_stannous_sulfate_solution.getFluid(1000))
				.duration(100)
				.EUt(480)
				.buildAndRegister();

		//硫酸亚锡神秘溶液 高压釜 高魔素硫酸亚锡+四氯化碳
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.magical_stannous_sulfate_solution.getFluid(1000))
				.output(OrePrefix.dust, PollutionMaterials.highmana_stannous_sulfate, 1)
				.fluidOutputs(GCYSMaterials.CarbonTetrachloride.getFluid(1000))
				.duration(100)
				.EUt(480)
				.buildAndRegister();

		//高魔素硫酸亚锡+液态水 工业高炉 二次炼金残渣+二次升华蒸汽
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.highmana_stannous_sulfate, 3)
				.fluidInputs(PollutionMaterials.infused_water.getFluid(432))
				.output(OrePrefix.dust, PollutionMaterials.alchemical_residue_2, 3)
				.fluidOutputs(PollutionMaterials.alchemical_vapor_2.getFluid(1000))
				.duration(1800)
				.blastFurnaceTemp(1600)
				.EUt(120)
				.buildAndRegister();

		//二次炼金残渣 离心 锡+概率硫+不纯魔力
		RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.alchemical_residue_2, 1)
				.output(OrePrefix.dust, Materials.Tin,1)
				.chancedOutput(OrePrefix.dust, Materials.Sulfur, 1, 2000, 200)
				.fluidOutputs(PollutionMaterials.impuremana.getFluid(18))
				.duration(200)
				.EUt(30)
				.buildAndRegister();

		//二次升华蒸汽+水银+世界盐 搅拌 含杂汞盐溶液
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.alchemical_vapor_2.getFluid(1000))
				.fluidInputs(Materials.Mercury.getFluid(9000))
				.input(OrePrefix.dust, PollutionMaterials.salismundus, 10)
				.fluidOutputs(PollutionMaterials.impure_mercuric_salt_solution.getFluid(10000))
				.duration(300)
				.EUt(480)
				.buildAndRegister();

		//含杂汞盐溶液 蒸馏塔 神秘汞盐溶液+世界盐+水
		RecipeMaps.DISTILLATION_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.impure_mercuric_salt_solution.getFluid(1000))
				.output(OrePrefix.dust, PollutionMaterials.salismundus, 1)
				.fluidOutputs(PollutionMaterials.mercuric_salt_solution.getFluid(100))
				.fluidOutputs(Materials.Water.getFluid(900))
				.duration(30)
				.EUt(480)
				.buildAndRegister();

		//神秘汞盐溶液+火粉 工业高炉 三次炼金残渣+三次升华蒸汽
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.infused_fire, 3)
				.fluidInputs(PollutionMaterials.mercuric_salt_solution.getFluid(1000))
				.output(OrePrefix.dust, PollutionMaterials.alchemical_residue_3, 10)
				.fluidOutputs(PollutionMaterials.alchemical_vapor_3.getFluid(1000))
				.duration(600)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();

		//三次炼金残渣 离心 汞+概率盐
		RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.alchemical_residue_3, 10)
				.chancedOutput(OrePrefix.dust, Materials.Salt, 1, 1500, 200)
				.fluidOutputs(Materials.Mercury.getFluid(9000))
				.duration(400)
				.EUt(30)
				.buildAndRegister();

		//三次升华蒸汽+氯化铁+水 化反 魔力激活氯化铁溶液
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.alchemical_vapor_3.getFluid(1000))
				.fluidInputs(Materials.Iron3Chloride.getFluid(1000))
				.fluidInputs(Materials.Water.getFluid(1000))
				.fluidOutputs(PollutionMaterials.magic_activated_iron_chloride_solution.getFluid(3000))
				.duration(600)
				.EUt(480)
				.buildAndRegister();

		//魔力激活氯化铁溶液+小撮炽焰铁粉+铁粉+甲醇 化反 魔力激活氯化亚铁甲醇溶液
		RecipeMaps.LARGE_CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.magic_activated_iron_chloride_solution.getFluid(3000))
				.input(OrePrefix.dustTiny, PollutionMaterials.octine, 1)
				.input(OrePrefix.dust, Materials.Iron, 3)
				.fluidInputs(Materials.Methanol.getFluid(1000))
				.fluidOutputs(PollutionMaterials.magic_activated_ferrous_chloride_ethanol_solution.getFluid(6000))
				.duration(600)
				.EUt(480)
				.buildAndRegister();

		//魔力激活氯化亚铁甲醇溶液+磁化钕杆 电解机 除杂激活氯化亚铁甲醇溶液+四次炼金残渣
		RecipeMaps.ELECTROLYZER_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.magic_activated_ferrous_chloride_ethanol_solution.getFluid(1000))
				.notConsumable(OrePrefix.stick, Materials.NeodymiumMagnetic, 1)
				.fluidOutputs(PollutionMaterials.purified_activated_ferrous_chloride_ethanol_solution.getFluid(1000))
				.output(OrePrefix.dust, PollutionMaterials.alchemical_residue_4, 1)
				.duration(100)
				.EUt(480)
				.buildAndRegister();

		//四次炼金残渣 离心 概率火+概率魔力钢粉+概率世界盐
		RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.alchemical_residue_4, 2)
				.chancedOutput(OrePrefix.dust, PollutionMaterials.infused_fire, 1, 1500, 100)
				.chancedOutput(OrePrefix.dust, PollutionMaterials.manasteel, 1, 2500, 100)
				.chancedOutput(OrePrefix.dust, PollutionMaterials.salismundus, 1, 500, 100)
				.duration(300)
				.EUt(30)
				.buildAndRegister();

		//除杂激活氯化亚铁甲醇溶液 电弧炉 除杂激活氯化亚铁
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.purified_activated_ferrous_chloride_ethanol_solution.getFluid(1000))
				.output(OrePrefix.dust, PollutionMaterials.purified_activated_ferrous_chloride, 1)
				.duration(100)
				.EUt(480)
				.buildAndRegister();

		//除杂激活氯化亚铁+地粉 工业高炉 四次升华蒸汽+氯化亚铁
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.purified_activated_ferrous_chloride, 6)
				.input(OrePrefix.dust, PollutionMaterials.infused_earth, 3)
				.output(OrePrefix.dust, PollutionMaterials.ferrous_chloride, 6)
				.fluidOutputs(PollutionMaterials.alchemical_vapor_4.getFluid(1000))
				.duration(900)
				.blastFurnaceTemp(2700)
				.EUt(480)
				.buildAndRegister();

		//四次升华蒸汽+小撮赛摩铜粉+蒸馏水 搅拌 赛摩铜掺杂魔水溶液
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.alchemical_vapor_4.getFluid(1000))
				.input(OrePrefix.dustTiny, PollutionMaterials.syrmorite, 1)
				.fluidInputs(Materials.DistilledWater.getFluid(520))
				.fluidOutputs(PollutionMaterials.syrmorite_doped_magic_water_solution.getFluid(1520))
				.duration(300)
				.EUt(1920)
				.buildAndRegister();

		//赛摩铜掺杂魔水溶液+世界盐 化反 铜粉+未成形胚胎魔水+水
		RecipeMaps.LARGE_CHEMICAL_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.syrmorite_doped_magic_water_solution.getFluid(15200))
				.input(OrePrefix.dust, PollutionMaterials.salismundus, 1)
				.output(OrePrefix.dust, Materials.Copper)
				.fluidOutputs(PollutionMaterials.unformed_embryo_magic_water.getFluid(1000))
				.fluidOutputs(Materials.Water.getFluid(14200))
				.duration(3000)
				.EUt(1920)
				.buildAndRegister();

		//未成形胚胎魔水+悖论物质nc+黑土贤者之石nc 魔导催化 胚胎魔水
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.unformed_embryo_magic_water.getFluid(1000))
				.notConsumable(new ItemStack(ItemsTC.causalityCollapser))
				.notConsumable(new ItemStack(PollutionMetaItems.STONE_OF_PHILOSOPHER_1.getMetaItem(), 1, 150))
				.fluidOutputs(PollutionMaterials.embryo_magic_water.getFluid(1000))
				.duration(120)
				.EUt(1920)
				.buildAndRegister();

		//胚胎魔水+风粉 工业高炉 五次升华蒸汽+五次炼金残渣
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.embryo_magic_water.getFluid(100))
				.input(OrePrefix.dust, PollutionMaterials.infused_air, 3)
				.output(OrePrefix.dust, PollutionMaterials.alchemical_residue_5, 1)
				.fluidOutputs(PollutionMaterials.alchemical_vapor_5.getFluid(1000))
				.duration(600)
				.blastFurnaceTemp(3600)
				.EUt(1920)
				.buildAndRegister();

		//五次炼金残渣 离心 白漫宿+概率地
		RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.alchemical_residue_5, 1)
				.chancedOutput(OrePrefix.dust, PollutionMaterials.infused_earth, 1, 1000, 100)
				.fluidOutputs(PollutionMaterials.whitemansus.getFluid(10))
				.duration(1600)
				.EUt(30)
				.buildAndRegister();

		//五次升华蒸汽+银粉+蓝石粉 搅拌 不稳次元银
		RecipeMaps.MIXER_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.alchemical_vapor_5.getFluid(1000))
				.input(OrePrefix.dust, Materials.Silver, 1)
				.input(OrePrefix.dust, Materials.Electrotine, 4)
				.output(OrePrefix.dust, PollutionMaterials.unstable_dimensional_silver, 6)
				.duration(300)
				.EUt(1920)
				.buildAndRegister();

		//不稳次元银+末影珍珠粉+氡 魔法炖屎 超次元含杂秘银流
		PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.unstable_dimensional_silver, 6)
				.input(OrePrefix.dust, Materials.EnderPearl, 3)
				.fluidInputs(Materials.Radon.getFluid(1000))
				.fluidOutputs(PollutionMaterials.impure_hyperdimensional_silver.getFluid(1440))
				.duration(1200)
				.blastFurnaceTemp(3600)
				.EUt(1920)
				.buildAndRegister();

		//超次元含杂秘银流+世界盐 化学浸洗 超次元秘银（粉）+六次炼金残渣
		RecipeMaps.CHEMICAL_BATH_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.salismundus, 1)
				.fluidInputs(PollutionMaterials.impure_hyperdimensional_silver.getFluid(1440))
				.output(OrePrefix.dust, PollutionMaterials.hyperdimensional_silver, 1)
				.output(OrePrefix.dust, PollutionMaterials.alchemical_residue_6, 10)
				.duration(300)
				.EUt(1920)
				.buildAndRegister();

		//六次炼金残渣 离心 黑漫宿+红石+末影之眼粉+概率虚空种子
		RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.alchemical_residue_6, 10)
				.output(OrePrefix.dust, Materials.Redstone, 4)
				.output(OrePrefix.dust, Materials.EnderEye, 3)
				.chancedOutput(new ItemStack(ItemsTC.voidSeed), 100, 10)
				.fluidOutputs(PollutionMaterials.blackmansus.getFluid(10))
				.duration(6400)
				.EUt(30)
				.buildAndRegister();

		//超次元秘银+液态熵 工业高炉 六次升华蒸汽
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, PollutionMaterials.hyperdimensional_silver, 1)
				.fluidInputs(PollutionMaterials.infused_entropy.getFluid(432))
				.fluidOutputs(PollutionMaterials.alchemical_vapor_6.getFluid(890))
				.duration(600)
				.blastFurnaceTemp(4500)
				.EUt(1920)
				.buildAndRegister();

		//六次升华蒸汽+魔力+黑漫宿+白漫宿 大化反 次元改造剂
		PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.alchemical_vapor_6.getFluid(890))
				.fluidInputs(GTQTMaterials.Magic.getFluid(100))
				.fluidInputs(PollutionMaterials.blackmansus.getFluid(5))
				.fluidInputs(PollutionMaterials.whitemansus.getFluid(5))
				.fluidOutputs(PollutionMaterials.dimensional_transforming_agent.getFluid(1000))
				.duration(100)
				.EUt(7680)
				.buildAndRegister();

		//次元改造剂+金粉 高炉 刻金
		RecipeMaps.BLAST_RECIPES.recipeBuilder()
				.input(OrePrefix.dust, Materials.Gold, 1)
				.fluidInputs(PollutionMaterials.dimensional_transforming_agent.getFluid(42))
				.output(OrePrefix.ingotHot, PollutionMaterials.keqinggold, 1)
				.duration(2000)
				.blastFurnaceTemp(5400)
				.EUt(7680)
				.buildAndRegister();
	}
}
