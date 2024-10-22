package keqing.pollution.common.metatileentity;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.electric.MetaTileEntitySingleTurbine;
import gregtech.common.metatileentities.multi.electric.generator.MetaTileEntityLargeTurbine;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.Pollution;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.block.metablocks.POTurbine;
import keqing.pollution.common.metatileentity.multiblock.*;
import keqing.pollution.common.metatileentity.multiblockpart.*;
import keqing.pollution.common.metatileentity.single.*;
import net.minecraft.util.ResourceLocation;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static keqing.pollution.client.textures.POTextures.*;

public class PollutionMetaTileEntities {
	public static ResourceLocation gtqtcoreId(String id) {
		return new ResourceLocation(Pollution.MODID, id);
	}

	public static final MetaTileEntityMultiblockPart[] FLUX_MUFFLERS = new MetaTileEntityMultiblockPart[9];
	public static final TieredMetaTileEntity[] AURA_GENERATORS = new TieredMetaTileEntity[6];
	public static final MetaTileEntityVisProvider[] VIS_PROVIDERS = new MetaTileEntityVisProvider[9];
	public static final MetaTileEntityVisClear[] VIS_CLEAR = new MetaTileEntityVisClear[4];
	public static final MetaTileEntityFluxClear[] FLUX_CLEARS = new MetaTileEntityFluxClear[3];

	public static MetaTileEntityInfusedExchange TANK;

	public static MetaTileEntityInfusedExchange TANK_TEST;

	public static MetaTileEntityMagicBender MAGIC_BENDER;
	public static MetaTileEntityMagicCentrifuge MAGIC_CENTRIFUGE;
	public static MetaTileEntityMagicElectricBlastFurnace MAGIC_ELECTRIC_BLAST_FURNACE;
	public static MetaTileEntityMagicElectrolyzer MAGIC_ELECTROLYZER;
	public static MetaTileEntityMagicMixer MAGIC_MIXER;
	public static MetaTileEntityMagicMacerator MAGIC_MACERATOR;
	public static MetaTileEntityMagicChemicalBath MAGIC_CHEMICAL_BATH;
	public static MetaTileEntityMagicSifter MAGIC_SIFTER;
	public static MetaTileEntityMagicCutter MAGIC_CUTTER;
	public static MetaTileEntityMagicWireMill MAGIC_WIREMILL;
	public static MetaTileEntityMagicSolidifier MAGIC_SOLIDIFIER;
	public static MetaTileEntityMagicBrewery MAGIC_BREWERY;
	public static MetaTileEntityIndustrialInfusion INDUSTRIAL_INFUSION;
	public static MetaTileEntityMagicBattery MAGIC_BATTERY;
	public static MetaTileEntityMagicChemicalReactor MAGIC_CHEMICAL_REACTOR;
	public static MetaTileEntityMagicAutoclave MAGIC_AUTOCLAVE;
	public static MetaTileEntityMagicExtruder MAGIC_EXTRUDER;
	public static MetaTileEntityMagicGreenHouse MAGIC_GREEN_HOUSE;
	public static MetaTileEntityMagicDistillery MAGIC_DISTILLERY;
	public static MetaTileEntityMagicAlloyBlastSmelter MAGIC_ALLOY_BLAST;
	public static MetaTileEntityEssenceCollector ESSENCE_COLLECTOR;
	public static MetaTileEntityMagicFusionReactor MAGIC_FUSION_REACTOR;
	public static TESTMetaTileEntityLargeAssembler TEST_MANA;
	public static MetaTileEntityNodeProducer NODE_PRODUCER;
	public static MetaTileEntityLargeNodeGenerator LARGE_NODE_GENERATOR;
	public static MetaTileEntityNodeWasher NODE_WASHER;
	public static MetaTileEntityVisHatch[] VIS_HATCH = new MetaTileEntityVisHatch[14];
	public static MetaTileEntityTankHatch[] TANK_HATCH = new MetaTileEntityTankHatch[1];
	public static MetaTileEntityManaHatch[] MANA_HATCH = new MetaTileEntityManaHatch[14];
	public static MetaTileEntityManaPoolHatch[] MANA_POOL_HATCH = new MetaTileEntityManaPoolHatch[14];
	public static MetaTileEntityLargeTurbine LARGE_MAGIC_TURBINE;
	public static MetaTileEntitySolarPlate[] SOLAR_PLATE = new MetaTileEntitySolarPlate[18];
	public static final SimpleGeneratorMetaTileEntity[] MAGIC_TURBINE = new SimpleGeneratorMetaTileEntity[3];
	public static SimpleGeneratorMetaTileEntity[] MANA_GENERATOR =new SimpleGeneratorMetaTileEntity[6];
	public static MetaTileEntityEndoflameArray ENDOFLAME_ARRAY;
//植魔系列
	public static MetaTileEntityBotDistillery BOT_DISTILLERY;
	public static MetaTileEntityManaPlate Mana_PLATE;
	public static MetaTileEntityMagicAssembler MAGIC_ASSEMBLER;
	public static MetaTileEntityNodeBlastFurnace NODE_BLAST_FURNACE;
	public static MetaTileEntitySmallChemicalPlant SMALL_CHEMICAL_PLANT;
	public static MetaTileEntityEssenceSmelter ESSENCE_SMELTER;

	public static void initialization() {

		for (int i = 0; i <= 4; i++) {
			String tierName = GTValues.VN[i + 1].toLowerCase();
			AURA_GENERATORS[i] = registerMetaTileEntity(15900 + i - 1, new MetaTileEntityVisGenerator(gtqtcoreId("vis." + tierName), i + 1));
		}

		for (int i = 0; i <= 7; i++) {
			String tierName = GTValues.VN[i + 1].toLowerCase();
			VIS_PROVIDERS[i] = registerMetaTileEntity(15920 + i - 1, new MetaTileEntityVisProvider(gtqtcoreId("vis_provider." + tierName), i + 1));
		}

		for (int i = 0; i <= 2; i++) {
			String tierName = GTValues.VN[i + 1].toLowerCase();
			VIS_CLEAR[i] = registerMetaTileEntity(15930 + i - 1, new MetaTileEntityVisClear(gtqtcoreId("flux_clear." + tierName), i + 1));
		}

		FLUX_CLEARS[1] = registerMetaTileEntity(15933, new MetaTileEntityFluxClear(gtqtcoreId("flux_clear.ev"), GTValues.EV));
		FLUX_CLEARS[2] = registerMetaTileEntity(15934, new MetaTileEntityFluxClear(gtqtcoreId("flux_clear.iv"), GTValues.IV));
		TANK = registerMetaTileEntity(15935, new MetaTileEntityInfusedExchange(gtqtcoreId("infused_exchange")));

		MAGIC_BENDER = registerMetaTileEntity(15936, new MetaTileEntityMagicBender(gtqtcoreId("magic_bender")));
		MAGIC_ELECTRIC_BLAST_FURNACE = registerMetaTileEntity(15937, new MetaTileEntityMagicElectricBlastFurnace(gtqtcoreId("magic_electric_blast_furnace")));
		MAGIC_CENTRIFUGE = registerMetaTileEntity(15938, new MetaTileEntityMagicCentrifuge(gtqtcoreId("magic_centrifuge")));
		MAGIC_ELECTROLYZER = registerMetaTileEntity(15939, new MetaTileEntityMagicElectrolyzer(gtqtcoreId("magic_electrolyzer")));
		MAGIC_MIXER = registerMetaTileEntity(15940, new MetaTileEntityMagicMixer(gtqtcoreId("magic_mixer")));
		MAGIC_MACERATOR = registerMetaTileEntity(15941, new MetaTileEntityMagicMacerator(gtqtcoreId("magic_macerator")));
		MAGIC_CHEMICAL_BATH = registerMetaTileEntity(15942, new MetaTileEntityMagicChemicalBath(gtqtcoreId("magic_chemical_bath")));
		MAGIC_SIFTER = registerMetaTileEntity(15943, new MetaTileEntityMagicSifter(gtqtcoreId("magic_sifter")));
		MAGIC_CUTTER = registerMetaTileEntity(15944, new MetaTileEntityMagicCutter(gtqtcoreId("magic_cutter")));
		MAGIC_WIREMILL = registerMetaTileEntity(15945, new MetaTileEntityMagicWireMill(gtqtcoreId("magic_wiremill")));
		MAGIC_SOLIDIFIER = registerMetaTileEntity(15946, new MetaTileEntityMagicSolidifier(gtqtcoreId("magic_solidifier")));
		MAGIC_BREWERY = registerMetaTileEntity(15947, new MetaTileEntityMagicBrewery(gtqtcoreId("magic_brewery")));
		MAGIC_DISTILLERY = registerMetaTileEntity(15948, new MetaTileEntityMagicDistillery(gtqtcoreId("magic_distillery")));
		INDUSTRIAL_INFUSION = registerMetaTileEntity(15949, new MetaTileEntityIndustrialInfusion(gtqtcoreId("industrial_infusion")));
		MAGIC_BATTERY = registerMetaTileEntity(15950, new MetaTileEntityMagicBattery(gtqtcoreId("magic_battery")));
		MAGIC_ALLOY_BLAST = registerMetaTileEntity(15951, new MetaTileEntityMagicAlloyBlastSmelter(gtqtcoreId("magic_alloy_blast")));
		MAGIC_CHEMICAL_REACTOR = registerMetaTileEntity(15952, new MetaTileEntityMagicChemicalReactor(gtqtcoreId("magic_chemical_reactor")));
		MAGIC_AUTOCLAVE = registerMetaTileEntity(15953, new MetaTileEntityMagicAutoclave(gtqtcoreId("magic_autoclave")));
		MAGIC_EXTRUDER = registerMetaTileEntity(15954, new MetaTileEntityMagicExtruder(gtqtcoreId("magic_extruder")));
		MAGIC_GREEN_HOUSE = registerMetaTileEntity(15955, new MetaTileEntityMagicGreenHouse(gtqtcoreId("magic_green_house")));
		ESSENCE_COLLECTOR = registerMetaTileEntity(15956, new MetaTileEntityEssenceCollector(gtqtcoreId("essence_collector")));
		MAGIC_FUSION_REACTOR = registerMetaTileEntity(15957, new MetaTileEntityMagicFusionReactor(gtqtcoreId("magic_fusion_reactor")));

		MAGIC_TURBINE[0] = registerMetaTileEntity(15958,
				new MetaTileEntitySingleTurbine(gtqtcoreId("magic_turbine.lv"), PORecipeMaps.MAGIC_TURBINE_FUELS,
						Textures.POWER_SUBSTATION_OVERLAY, 1, GTUtility.genericGeneratorTankSizeFunction));
		MAGIC_TURBINE[1] = registerMetaTileEntity(15959,
				new MetaTileEntitySingleTurbine(gtqtcoreId("magic_turbine.mv"), PORecipeMaps.MAGIC_TURBINE_FUELS,
						Textures.POWER_SUBSTATION_OVERLAY, 2, GTUtility.genericGeneratorTankSizeFunction));
		MAGIC_TURBINE[2] = registerMetaTileEntity(15960,
				new MetaTileEntitySingleTurbine(gtqtcoreId("magic_turbine.hv"), PORecipeMaps.MAGIC_TURBINE_FUELS,
						Textures.POWER_SUBSTATION_OVERLAY, 3, GTUtility.genericGeneratorTankSizeFunction));

		LARGE_MAGIC_TURBINE = registerMetaTileEntity(15961, new MetaTileEntityLargeTurbine(gtqtcoreId("large_turbine.magic"),
				PORecipeMaps.MAGIC_TURBINE_FUELS, 4,
				PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT),
				PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.STAINLESS_STEEL_GEARBOX),
				POTextures.SPELL_PRISM_HOT, true, Textures.HPCA_OVERLAY));

		NODE_PRODUCER = registerMetaTileEntity(15962, new MetaTileEntityNodeProducer(gtqtcoreId("node_producer")));
		LARGE_NODE_GENERATOR = registerMetaTileEntity(15963, new MetaTileEntityLargeNodeGenerator(gtqtcoreId("large_node_generator")));
		NODE_WASHER = registerMetaTileEntity(15964, new MetaTileEntityNodeWasher(gtqtcoreId("node_washer")));

		MANA_GENERATOR[1] = registerMetaTileEntity(15965, new ManaGeneratorTileEntity(gtqtcoreId("mana_gen_lv"), 1));
		MANA_GENERATOR[2] = registerMetaTileEntity(15966, new ManaGeneratorTileEntity(gtqtcoreId("mana_gen_mv"), 2));
		MANA_GENERATOR[3] = registerMetaTileEntity(15967, new ManaGeneratorTileEntity(gtqtcoreId("mana_gen_hv"), 3));
		MANA_GENERATOR[4] = registerMetaTileEntity(15968, new ManaGeneratorTileEntity(gtqtcoreId("mana_gen_ev"), 4));
		MANA_GENERATOR[5] = registerMetaTileEntity(15969, new ManaGeneratorTileEntity(gtqtcoreId("mana_gen_iv"), 5));

		for (int i = 1; i <= 8; i++) {
			String tierName = GTValues.VN[i].toLowerCase();
			FLUX_MUFFLERS[i] = registerMetaTileEntity(16000 + i - 1, new MetaTileEntityFluxMuffler(gtqtcoreId("pollution_muffler_hatch." + tierName), i));
		}

		for (int i = 0; i < VIS_HATCH.length; i++) {
			int tier = GTValues.LV + i;
			VIS_HATCH[i] = registerMetaTileEntity(16020 + i, new MetaTileEntityVisHatch(
					gtqtcoreId(String.format("vis_hatch.%s", GTValues.VN[tier])), tier));
		}

		for (int i = 0; i < TANK_HATCH.length; i++) {
			int tier = GTValues.LV + i;
			TANK_HATCH[i] = registerMetaTileEntity(16040 + i, new MetaTileEntityTankHatch(
					gtqtcoreId(String.format("tank_hatch.%s", GTValues.VN[tier])), tier));
		}

		int kind;
		for (kind = 1; kind <= 6; kind++) {
			SOLAR_PLATE[kind * 3 - 3] = registerMetaTileEntity(16060 + kind * 3 - 3, new MetaTileEntitySolarPlate(
					gtqtcoreId(String.format("solar_plate_%s.%s", 1, kind)), 1, kind, SOLAR_PLATE_I));
			SOLAR_PLATE[kind * 3 - 2] = registerMetaTileEntity(16060 + kind * 3 - 2, new MetaTileEntitySolarPlate(
					gtqtcoreId(String.format("solar_plate_%s.%s", 2, kind)), 2, kind, SOLAR_PLATE_II));
			SOLAR_PLATE[kind * 3 - 1] = registerMetaTileEntity(16060 + kind * 3 - 1, new MetaTileEntitySolarPlate(
					gtqtcoreId(String.format("solar_plate_%s.%s", 3, kind)), 3, kind, SOLAR_PLATE_III));

		}

		for (int i = 0; i < MANA_HATCH.length; i++) {
			int tier = GTValues.LV + i;
			MANA_HATCH[i] = registerMetaTileEntity(15800 + i, new MetaTileEntityManaHatch(gtqtcoreId(String.format("mana_hatch.%s", GTValues.VN[tier])), tier));
		}
		for (int i = 0; i < MANA_POOL_HATCH.length; i++) {
			int tier = GTValues.LV + i;
			MANA_POOL_HATCH[i] = registerMetaTileEntity(15815 + i, new MetaTileEntityManaPoolHatch(gtqtcoreId(String.format("mana_pool_hatch.%s", GTValues.VN[tier])), tier));
		}

		TEST_MANA = registerMetaTileEntity(15850, new TESTMetaTileEntityLargeAssembler(gtqtcoreId("test")));
		ENDOFLAME_ARRAY = registerMetaTileEntity(15851,new MetaTileEntityEndoflameArray(gtqtcoreId("endoflame_array")));

		BOT_DISTILLERY = registerMetaTileEntity(15852, new MetaTileEntityBotDistillery(gtqtcoreId("bot_distillery")));
		Mana_PLATE = registerMetaTileEntity(15853, new MetaTileEntityManaPlate(gtqtcoreId("mana_plate")));
		MAGIC_ASSEMBLER = registerMetaTileEntity(15854, new MetaTileEntityMagicAssembler(gtqtcoreId("magic_assembler")));
	    NODE_BLAST_FURNACE = registerMetaTileEntity(15855, new MetaTileEntityNodeBlastFurnace(gtqtcoreId("node_blast_furnace")));
	    SMALL_CHEMICAL_PLANT = registerMetaTileEntity(15856, new MetaTileEntitySmallChemicalPlant(gtqtcoreId("small_chemical_plant")));
	    ESSENCE_SMELTER = registerMetaTileEntity(15857, new MetaTileEntityEssenceSmelter(gtqtcoreId("essence_smelter")));
	}
}
