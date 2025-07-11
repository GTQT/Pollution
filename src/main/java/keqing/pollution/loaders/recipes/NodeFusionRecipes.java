package keqing.pollution.loaders.recipes;

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
import static keqing.gtqtcore.api.unification.GTQTMaterials.GadoliniumSiliconGermanium;
import static keqing.gtqtcore.api.unification.GTQTMaterials.Okin;
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
				.input(PollutionMetaItems.CORE_OF_IDEA, 4)
				.input(ItemsTC.morphicResonator, 32)
				.input(MetaItems.ELECTRIC_MOTOR_LuV, 2)
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
		//魔法聚变 MK1
		PORecipeMaps.MAGIC_ASSEMBLER_RECIPES.recipeBuilder()
				.input(MetaTileEntities.FUSION_REACTOR[0], 16)
				.inputs(PollutionMetaBlocks.HYPER.getItemVariant(POHyper.HyperType.HYPER_1_CASING, 32))
				.inputs(PollutionMetaBlocks.BEAM_CORE.getItemVariant(POMBeamCore.MagicBlockType.BEAM_CORE_1, 16))
				.input(PollutionMetaItems.BOTTLE_OF_PHLOGISTONIC_ONENESS, 16)
				.input(PollutionMetaItems.ELUCIDATOR_OF_FOUR_CAUSES, 8)
				.input(PollutionMetaItems.AUTO_ELENCHUS_DEVICE, 8)
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
