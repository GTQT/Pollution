package keqing.pollution.loaders.recipes;

import WayofTime.bloodmagic.block.BlockLifeEssence;
import WayofTime.bloodmagic.client.render.entity.SentientSpecterRenderFactory;
import WayofTime.bloodmagic.util.Constants;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.blocks.BlockFusionCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POHyper;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.items.PollutionMetaItems;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.items.ItemsTC;

import static gregtech.api.GTValues.*;
import static gregtech.api.GTValues.VA;
import static gregtech.api.recipes.RecipeMaps.MIXER_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static keqing.gtqtcore.api.unification.GTQTMaterials.*;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class NodeFusionRecipes {

	public static void init() {
		fusion();
		blockRecipes();
		fuels();
	}
	private static void fuels(){
		RecipeMaps.PLASMA_GENERATOR_FUELS.recipeBuilder()
				.fluidInputs(new FluidStack[]{PollutionMaterials.hyperdimensional_silver.getPlasma(1)})
				.fluidOutputs(new FluidStack[]{PollutionMaterials.hyperdimensional_silver.getFluid(1)})
				.duration(400)
				.EUt(VA[EV])
				.buildAndRegister();
		RecipeMaps.PLASMA_GENERATOR_FUELS.recipeBuilder()
				.fluidInputs(new FluidStack[]{PollutionMaterials.keqinggold.getPlasma(1)})
				.fluidOutputs(new FluidStack[]{PollutionMaterials.keqinggold.getFluid(1)})
				.duration(400)
				.EUt(VA[EV])
				.buildAndRegister();
	}

	private static void blockRecipes(){
		//方块 MK1
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_1))
				.input(PollutionMetaItems.CORE_OF_IDEA, 2)
				.input(ItemsTC.morphicResonator, 32)
				.input(MetaItems.ELECTRIC_MOTOR_LuV, 1)
				.input(OrePrefix.screw, PollutionMaterials.blood_of_avernus, 8)
				.input(OrePrefix.frameGt, PollutionMaterials.aetheric_dark_steel, 4)
				.input(OrePrefix.frameGt, PollutionMaterials.iizunamaru_electrum, 4)
				.fluidInputs(PollutionMaterials.keqinggold.getFluid(576))
				.fluidInputs(PollutionMaterials.whitemansus.getFluid(8000))
				.fluidInputs(PollutionMaterials.blackmansus.getFluid(8000))
				.outputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_1_CASING, 4))
				.EUt(VA[LuV])
				.duration(400)
				.buildAndRegister();
		//方块 mk2 mk3
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_1_CASING, 1))
				.input(PollutionMetaItems.ELUCIDATOR_OF_FOUR_CAUSES, 1)
				.input(PollutionMetaItems.STARRY_RUNE, 2)
				.input(MetaItems.ELECTRIC_MOTOR_ZPM, 1)
				.input(OrePrefix.screw, Duranium, 4)
				.input(OrePrefix.frameGt, Europium, 2)
				.fluidInputs(binding_metal.getFluid(576))
				.fluidInputs(sentient_metal.getFluid(576))
				.fluidInputs(PollutionMaterials.starrymansus.getFluid(2000))
				.outputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_2_CASING, 1))
				.EUt(VA[ZPM])
				.duration(400)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_2_CASING, 1))
				.input(PollutionMetaItems.SYMPTOMATIC_VIS_DATA_LINK, 1)
				.input(PollutionMetaItems.STARRY_RUNE, 4)
				.input(MetaItems.ELECTRIC_MOTOR_UV, 1)
				.input(OrePrefix.screw, Americium, 4)
				.input(OrePrefix.frameGt, Darmstadtium, 2)
				.fluidInputs(existing_nexus.getFluid(576))
				.fluidInputs(fading_nexus.getFluid(576))
				.fluidInputs(PollutionMaterials.starrymansus.getFluid(4000))
				.outputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_3_CASING, 1))
				.EUt(VA[UV])
				.duration(400)
				.buildAndRegister();
		//魔法聚变 MK1-MK3
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaTileEntities.FUSION_REACTOR[0], 16)
				.inputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_1_CASING, 8))
				.inputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_1, 16))
				.input(PollutionMetaItems.BOTTLE_OF_PHLOGISTONIC_ONENESS, 8)
				.input(PollutionMetaItems.ELUCIDATOR_OF_FOUR_CAUSES, 4)
				.input(PollutionMetaItems.AUTO_ELENCHUS_DEVICE, 4)
				.inputs(MetaBlocks.FUSION_CASING.getItemVariant(BlockFusionCasing.CasingType.SUPERCONDUCTOR_COIL, 4))
				.input(OrePrefix.plate, Materials.Osmiridium, 32)
				.input(GTQTMetaItems.NEUTRON, 16)
				.fluidInputs(PollutionMaterials.aetheric_dark_steel.getFluid(36000))
				.fluidInputs(PollutionMaterials.keqinggold.getFluid(36000))
				.fluidInputs(PollutionMaterials.starrymansus.getFluid(10000))
				.outputs(PollutionMetaTileEntities.NODE_FUSION_REACTOR[0].getStackForm())
				.EUt(VA[ZPM])
				.duration(40000)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(PollutionMetaTileEntities.NODE_FUSION_REACTOR[0].getStackForm())
				.inputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_2_CASING, 8))
				.inputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_1, 32))
				.input(PollutionMetaItems.SYMPTOMATIC_VIS_DATA_LINK,4)
				.inputs(MetaBlocks.FUSION_CASING.getItemVariant(BlockFusionCasing.CasingType.SUPERCONDUCTOR_COIL, 8))
				.input(OrePrefix.plate, Duranium, 32)
				.inputs(PollutionMetaItems.STONE_OF_PHILOSOPHER_3.getStackForm())
				.fluidInputs(binding_metal.getFluid(36000))
				.fluidInputs(sentient_metal.getFluid(36000))
				.fluidInputs(PollutionMaterials.starrymansus.getFluid(10000))
				.outputs(PollutionMetaTileEntities.NODE_FUSION_REACTOR[1].getStackForm())
				.EUt(VA[UV])
				.duration(40000)
				.buildAndRegister();
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.inputs(PollutionMetaTileEntities.NODE_FUSION_REACTOR[1].getStackForm())
				.inputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_3_CASING, 8))
				.inputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_1, 64))
				.input(PollutionMetaItems.SYMPTOMATIC_VIS_DATA_LINK,8)
				.inputs(MetaBlocks.FUSION_CASING.getItemVariant(BlockFusionCasing.CasingType.SUPERCONDUCTOR_COIL, 16))
				.input(OrePrefix.plate, Darmstadtium, 32)
				.inputs(PollutionMetaItems.STONE_OF_PHILOSOPHER_4.getStackForm())
				.fluidInputs(existing_nexus.getFluid(36000))
				.fluidInputs(fading_nexus.getFluid(36000))
				.fluidInputs(PollutionMaterials.starrymansus.getFluid(10000))
				.outputs(PollutionMetaTileEntities.NODE_FUSION_REACTOR[2].getStackForm())
				.EUt(VA[UHV])
				.duration(40000)
				.buildAndRegister();
	}
	private static void fusion() {
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.infused_sense.getFluid(1000))
				.fluidInputs(PollutionMaterials.keqinggold.getFluid(288))
				.fluidOutputs(PollutionMaterials.sentient_metal.getFluid(144))
				.EUToStart(120000000L)
				.EUt(VA[LuV])
				.duration(400)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.infused_soul.getFluid(1000))
				.fluidInputs(PollutionMaterials.hyperdimensional_silver.getFluid(288))
				.fluidOutputs(PollutionMaterials.binding_metal.getFluid(144))
				.EUToStart(120000000L)
				.EUt(VA[LuV])
				.duration(400)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(GTQTMaterials.Richmagic.getFluid(1000))
				.fluidInputs(Materials.Silver.getFluid(144))
				.fluidOutputs(PollutionMaterials.hyperdimensional_silver.getPlasma(144))
				.EUToStart(80000000L)
				.EUt(VA[LuV])
				.duration(90)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(GTQTMaterials.Richmagic.getFluid(1000))
				.fluidInputs(Materials.Gold.getFluid(144))
				.fluidOutputs(PollutionMaterials.keqinggold.getPlasma(144))
				.EUToStart(80000000L)
				.EUt(VA[LuV])
				.duration(90)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(GTQTMaterials.Richmagic.getFluid(1000))
				.fluidInputs(Materials.Water.getFluid(1000))
				.fluidOutputs(PollutionMaterials.dimensional_transforming_agent.getFluid(1000))
				.EUToStart(60000000L)
				.EUt(VA[IV])
				.duration(200)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.infused_magic.getFluid(144))
				.fluidInputs(Materials.Water.getFluid(1000))
				.fluidOutputs(GTQTMaterials.Richmagic.getFluid(1000))
				.EUToStart(60000000L)
				.EUt(VA[EV])
				.duration(200)
				.buildAndRegister();
        //生命源质聚变
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(infused_life.getFluid(1000))
				.fluidInputs(Richmagic.getFluid(1000))
				.fluidOutputs(new FluidStack(BlockLifeEssence.getLifeEssence(), 1000))
				.EUToStart(140000000L)
				.EUt(VA[LuV])
				.duration(64)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(new FluidStack(BlockLifeEssence.getLifeEssence(), 1000))
				.fluidInputs(Terrasteel.getFluid(288))
				.fluidOutputs(existing_nexus.getFluid(144))
				.EUToStart(200000000L)
				.EUt(VA[ZPM])
				.duration(200)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(new FluidStack(BlockLifeEssence.getLifeEssence(), 1000))
				.fluidInputs(VoidMetal.getFluid(288))
				.fluidOutputs(fading_nexus.getFluid(144))
				.EUToStart(200000000L)
				.EUt(VA[ZPM])
				.duration(200)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(infused_instrument.getFluid(144))
				.fluidInputs(Titanium.getFluid(144))
				.fluidOutputs(Tritanium.getFluid(288))
				.EUToStart(400000000L)
				.EUt(VA[ZPM])
				.duration(40)
				.buildAndRegister();
		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(Naquadria.getFluid(144))
				.fluidInputs(infused_energy.getFluid(1000))
				.fluidOutputs(Neutronium.getFluid(144))
				.EUToStart(400000000L)
				.EUt(VA[ZPM])
				.duration(60)
				.buildAndRegister();

		PORecipeMaps.NODE_MAGIC_FUSION_RECIPES.recipeBuilder()
				.fluidInputs(PollutionMaterials.infused_entropy.getFluid(144))
				.fluidInputs(PollutionMaterials.infused_order.getFluid(144))
				.fluidOutputs(GTQTMaterials.Okin.getFluid(576))
				.EUToStart(420000000L)
				.EUt(VA[UHV])
				.duration(200)
				.buildAndRegister();
	}
}
