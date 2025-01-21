package keqing.pollution.common.block;

import gregtech.common.blocks.MetaBlocks;
import keqing.pollution.common.block.metablocks.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PollutionMetaBlocks {
	public static POMagicBlock MAGIC_BLOCK;
	public static POTurbine TURBINE;
	public static POMBeamCore BEAM_CORE;
	public static POCoilBlock WIRE_COIL;
	public static POFusionReactor FUSION_REACTOR;
	public static POGlass GLASS;
	public static POBotBlock BOT_BLOCK;
	public static POManaPlate MANA_PLATE;
	public static POHyper HYPER;
	private PollutionMetaBlocks() {
	}

	public static void init() {
		MAGIC_BLOCK = new POMagicBlock();
		MAGIC_BLOCK.setRegistryName("magic_block");

		TURBINE = new POTurbine();
		TURBINE.setRegistryName("turbine");


		BEAM_CORE = new POMBeamCore();
		BEAM_CORE.setRegistryName("beam_core");

		WIRE_COIL = new POCoilBlock();
		WIRE_COIL.setRegistryName("wire_coil");
		FUSION_REACTOR = new POFusionReactor();
		FUSION_REACTOR.setRegistryName("fusion_reactor");
		GLASS = new POGlass();
		GLASS.setRegistryName("glass");

		BOT_BLOCK = new POBotBlock();
		BOT_BLOCK.setRegistryName("bot_block");

		MANA_PLATE = new POManaPlate();
		MANA_PLATE.setRegistryName("mana_plate");

        HYPER = new POHyper();
		HYPER.setRegistryName("hyper");
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModels() {
		registerItemModel(MAGIC_BLOCK);
		registerItemModel(TURBINE);
		registerItemModel(BEAM_CORE);
		registerItemModel(WIRE_COIL);
		registerItemModel(FUSION_REACTOR);
		registerItemModel(GLASS);
		registerItemModel(BOT_BLOCK);
		registerItemModel(MANA_PLATE);
		registerItemModel(HYPER);
	}

	@SideOnly(Side.CLIENT)
	private static void registerItemModel(Block block) {
		for (IBlockState state : block.getBlockState().getValidStates()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
					block.getMetaFromState(state),
					new ModelResourceLocation(block.getRegistryName(),
							MetaBlocks.statePropertiesToString(state.getProperties())));
		}
	}
}