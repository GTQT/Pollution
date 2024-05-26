package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps;
import keqing.gtqtcore.api.unification.GCYSMaterials;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.api.unification.TJMaterials;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.items.PollutionMetaItems;
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
import thaumcraft.api.items.ItemsTC;

public class MagicChemicalRecipes {
	public static void init() {
		chemical();
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
				.fluidOutputs(GTQTMaterials.PreTreatedCrudeOil.getFluid(1000))
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

	}
}
