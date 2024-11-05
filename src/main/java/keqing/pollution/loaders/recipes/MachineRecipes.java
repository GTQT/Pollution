package keqing.pollution.loaders.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.wood.BlockGregPlanks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS;
import static gregtech.loaders.recipe.CraftingComponent.*;
import static gregtech.loaders.recipe.MetaTileEntityLoader.registerMachineRecipe;
import static keqing.pollution.api.unification.PollutionMaterials.octine;
import static keqing.pollution.api.unification.PollutionMaterials.syrmorite;
import static keqing.pollution.common.items.PollutionMetaItems.BLANKCORE;
import static keqing.pollution.common.metatileentity.PollutionMetaTileEntities.PRIMITIVE_MUD_PUMP;
import static keqing.pollution.common.metatileentity.PollutionMetaTileEntities.PRIMITIVE_STOVE;

public class MachineRecipes {
	public static void init() {
		machine();
		muffler();
		primitiveMulti();
	}

	private static void primitiveMulti() {
		ModHandler.addShapedRecipe(true, "primitive_mud_pump", PRIMITIVE_MUD_PUMP.getStackForm(),
				"RGS", "OWd", "CLC",
				'R', new UnificationEntry(OrePrefix.ring,syrmorite),
				'G', new UnificationEntry(OrePrefix.pipeNormalFluid, Materials.Wood),
				'S', new UnificationEntry(OrePrefix.screw, octine),
				'O', new UnificationEntry(OrePrefix.rotor, octine),
				'W', new ItemStack(BlockRegistry.WEEDWOOD_PLANKS),
				'C', new ItemStack(Blocks.STONE_SLAB, 1, 3),
				'L', new UnificationEntry(OrePrefix.pipeLargeFluid, Materials.Wood));

		ModHandler.addShapedRecipe(true, "primitive_stove", PRIMITIVE_STOVE.getStackForm(),
				"BGB", "XCX", "BGB",
				'G', new UnificationEntry(OrePrefix.screw, syrmorite),
				'X', new UnificationEntry(OrePrefix.plate, Potin),
				'B', MetaBlocks.METAL_CASING.getItemVariant(PRIMITIVE_BRICKS),
				'C', new UnificationEntry(OrePrefix.stick, octine));
	}

	private static void muffler() {
		ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.lv");
		ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.mv");
		ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.hv");
		ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.ev");
		ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.iv");
		ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.luv");
		ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.zpm");
		ModHandler.removeRecipeByName("gregtech:gregtech.machine.muffler_hatch.uv");

		registerMachineRecipe(PollutionMetaTileEntities.FLUX_MUFFLERS, "HM", "PR",
				'H', HULL,
				'M', MOTOR,
				'P', PIPE_NORMAL,
				'R', ROTOR);

		registerMachineRecipe(PollutionMetaTileEntities.AURA_GENERATORS,
				"ABA", "CHC", "ABA",
				'H', HULL,
				'A', MOTOR,
				'B', PISTON,
				'C', ROTOR);

		registerMachineRecipe(PollutionMetaTileEntities.VIS_PROVIDERS,
				"ABA", "CHC", "ABA",
				'H', HULL,
				'A', MOTOR,
				'B', EMITTER,
				'C', ROTOR);

		registerMachineRecipe(PollutionMetaTileEntities.VIS_CLEAR,
				"ABA", "CHC", "ABA",
				'H', HULL,
				'A', MOTOR,
				'B', SENSOR,
				'C', ROTOR);

		registerMachineRecipe(PollutionMetaTileEntities.VIS_HATCH,
				"ABA", "CHC", "ABA",
				'H', HULL,
				'A', CONVEYOR,
				'B', BLANKCORE,
				'C', EMITTER);
	}

	private static void machine() {
		ModHandler.addShapedRecipe(true, "flux_clear1", PollutionMetaTileEntities.FLUX_CLEARS[1].getStackForm(),
				"CBC", "FMF", "CBC", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(),
				'B', new UnificationEntry(OrePrefix.rotor, Titanium),
				'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.EV),
				'F', MetaItems.ELECTRIC_PUMP_EV);

		ModHandler.addShapedRecipe(true, "flux_clear2", PollutionMetaTileEntities.FLUX_CLEARS[2].getStackForm(),
				"CBC", "FMF", "CBC", 'M', MetaTileEntities.HULL[GTValues.IV].getStackForm(),
				'B', new UnificationEntry(OrePrefix.rotor, TungstenSteel),
				'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.IV),
				'F', MetaItems.ELECTRIC_PUMP_IV);


	}
}
