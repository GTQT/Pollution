package keqing.pollution.common.metatileentity;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.WorkableTieredMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.multi.electric.generator.MetaTileEntityLargeTurbine;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps;
import keqing.gtqtcore.client.textures.GTQTTextures;
import keqing.pollution.Pollution;
import keqing.pollution.api.metatileentity.POMetaTileEntitySingleTurbine;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.block.metablocks.POManaPlate;
import keqing.pollution.common.block.metablocks.POTurbine;
import keqing.pollution.common.metatileentity.multiblock.*;
import keqing.pollution.common.metatileentity.multiblock.bloodMagic.MetaTileEntityBMHPCA;
import keqing.pollution.common.metatileentity.multiblock.bot.*;
import keqing.pollution.common.metatileentity.multiblock.generator.MetaTileEntityMultiDanDeLifeOn;
import keqing.pollution.common.metatileentity.multiblock.magic.*;
import keqing.pollution.common.metatileentity.multiblock.primitive.MetaTileEntityPrimitiveMudPump;
import keqing.pollution.common.metatileentity.multiblock.primitive.MetaTileEntityStove;
import keqing.pollution.common.metatileentity.multiblockpart.*;
import keqing.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCABridge;
import keqing.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCAComputation;
import keqing.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCACooler;
import keqing.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCAEmpty;
import keqing.pollution.common.metatileentity.single.*;
import net.minecraft.util.ResourceLocation;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static keqing.pollution.client.textures.POTextures.*;

public class PollutionMetaTileEntities {
	public static ResourceLocation PollutionID(String id) {
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
	public static MetaTileEntityNodeProducer NODE_PRODUCER;
	public static MetaTileEntityLargeNodeGenerator LARGE_NODE_GENERATOR;
	public static MetaTileEntityNodeWasher NODE_WASHER;
	public static MetaTileEntityVisHatch[] VIS_HATCH = new MetaTileEntityVisHatch[14];
	public static MetaTileEntityManaHatch[] MANA_HATCH = new MetaTileEntityManaHatch[14];
	public static MetaTileEntityManaPoolHatch[] MANA_POOL_HATCH = new MetaTileEntityManaPoolHatch[14];
	public static MetaTileEntityLargeTurbine LARGE_MAGIC_TURBINE;
	public static MetaTileEntitySolarPlate[] SOLAR_PLATE = new MetaTileEntitySolarPlate[18];
	public static final SimpleGeneratorMetaTileEntity[] MAGIC_TURBINE = new SimpleGeneratorMetaTileEntity[3];
	public static SimpleGeneratorMetaTileEntity[] MANA_GENERATOR = new SimpleGeneratorMetaTileEntity[6];
	//高阶机器，植魔系列
	public static MetaTileEntityEndoflameArray ENDOFLAME_ARRAY;
	public static MetaTileEntityBotDistillery BOT_DISTILLERY;
	public static MetaTileEntityManaPlate Mana_PLATE;
	public static MetaTileEntityMagicAssembler MAGIC_ASSEMBLER;
	public static MetaTileEntityNodeBlastFurnace NODE_BLAST_FURNACE;
	public static MetaTileEntitySmallChemicalPlant SMALL_CHEMICAL_PLANT;
	public static MetaTileEntityEssenceSmelter ESSENCE_SMELTER;
	public static MetaTileEntityBotGasCollector BOT_GAS_COLLECTOR;
	public static MetaTileEntityGtEssenceSmelter GT_ESSENCE_SMELTER;
	public static MetaTileEntityBotVacuumFreezer BOT_VACUUM_FREEZER;
	//原始设备
	public static MetaTileEntityPrimitiveMudPump PRIMITIVE_MUD_PUMP;
	public static MetaTileEntityStove PRIMITIVE_STOVE;

	public static final WorkableTieredMetaTileEntity[] FLUX_PROMOTED_FUEL_CELL = new WorkableTieredMetaTileEntity[5];
	public static final TieredMetaTileEntity[] MAGIC_ENERGY_ABSORBER = new TieredMetaTileEntity[5];
	public static final TieredMetaTileEntity[] SMALL_NODE_GENERATOR =new TieredMetaTileEntity[4];
	public static MetaTileEntityMegaManaTurbine MEGA_MANA_TURBINE;
	public static MetaTileEntityLargeTurbine LARGE_MANA_TURBINE;
	public static MetaTileEntityMultiDanDeLifeOn Muti_Dan_De_Life_On;
	public static MetaTileEntityCentralVisTower CENTRAL_VIS_TOWER;
	public static MetaTileEntityManaInfusionReactor MANA_INFUSION_REACTOR;
	public static MetaTileEntityBotCircuitAssembler BOT_CIRCUIT_ASSEMBLER;
	public static MetaTileEntityNodeFusionReactor[] NODE_FUSION_REACTOR = new MetaTileEntityNodeFusionReactor[3];

	//
	public static MetaTileEntitySourceCharge SOURCE_CHARGE;

	//BMHPCA
	public static MetaTileEntityBMHPCAEmpty BMHPCA_EMPTY_COMPONENT;
	public static MetaTileEntityBMHPCAComputation BMHPCA_COMPUTATION_COMPONENT;
	public static MetaTileEntityBMHPCAComputation BMHPCA_ADVANCED_COMPUTATION_COMPONENT;
	public static MetaTileEntityBMHPCACooler BMHPCA_ADVANCED_COOLER_COMPONENT;
	public static MetaTileEntityBMHPCACooler BMHPCA_SUPER_COOLER_COMPONENT;
	public static MetaTileEntityBMHPCACooler BMHPCA_ULTIMATE_COOLER_COMPONENT;
	public static MetaTileEntityBMHPCABridge BMHPCA_BRIDGE_COMPONENT;
	public static MetaTileEntityBMHPCA BMHPCA;
	public static void initialization() {

		for (int i = 0; i <= 4; i++) {
			String tierName = GTValues.VN[i + 1].toLowerCase();
			AURA_GENERATORS[i] = registerMetaTileEntity(15900 + i - 1, new MetaTileEntityVisGenerator(PollutionID("vis." + tierName), i + 1));
		}

		for (int i = 0; i <= 7; i++) {
			String tierName = GTValues.VN[i + 1].toLowerCase();
			VIS_PROVIDERS[i] = registerMetaTileEntity(15920 + i - 1, new MetaTileEntityVisProvider(PollutionID("vis_provider." + tierName), i + 1));
		}

		for (int i = 0; i <= 2; i++) {
			String tierName = GTValues.VN[i + 1].toLowerCase();
			VIS_CLEAR[i] = registerMetaTileEntity(15930 + i - 1, new MetaTileEntityVisClear(PollutionID("flux_clear." + tierName), i + 1));
		}

		FLUX_CLEARS[1] = registerMetaTileEntity(15933, new MetaTileEntityFluxClear(PollutionID("flux_clear.ev"), GTValues.EV));
		FLUX_CLEARS[2] = registerMetaTileEntity(15934, new MetaTileEntityFluxClear(PollutionID("flux_clear.iv"), GTValues.IV));
		TANK = registerMetaTileEntity(15935, new MetaTileEntityInfusedExchange(PollutionID("infused_exchange")));

		MAGIC_BENDER = registerMetaTileEntity(15936, new MetaTileEntityMagicBender(PollutionID("magic_bender")));
		MAGIC_ELECTRIC_BLAST_FURNACE = registerMetaTileEntity(15937, new MetaTileEntityMagicElectricBlastFurnace(PollutionID("magic_electric_blast_furnace")));
		MAGIC_CENTRIFUGE = registerMetaTileEntity(15938, new MetaTileEntityMagicCentrifuge(PollutionID("magic_centrifuge")));
		MAGIC_ELECTROLYZER = registerMetaTileEntity(15939, new MetaTileEntityMagicElectrolyzer(PollutionID("magic_electrolyzer")));
		MAGIC_MIXER = registerMetaTileEntity(15940, new MetaTileEntityMagicMixer(PollutionID("magic_mixer")));
		MAGIC_MACERATOR = registerMetaTileEntity(15941, new MetaTileEntityMagicMacerator(PollutionID("magic_macerator")));
		MAGIC_CHEMICAL_BATH = registerMetaTileEntity(15942, new MetaTileEntityMagicChemicalBath(PollutionID("magic_chemical_bath")));
		MAGIC_SIFTER = registerMetaTileEntity(15943, new MetaTileEntityMagicSifter(PollutionID("magic_sifter")));
		MAGIC_CUTTER = registerMetaTileEntity(15944, new MetaTileEntityMagicCutter(PollutionID("magic_cutter")));
		MAGIC_WIREMILL = registerMetaTileEntity(15945, new MetaTileEntityMagicWireMill(PollutionID("magic_wiremill")));
		MAGIC_SOLIDIFIER = registerMetaTileEntity(15946, new MetaTileEntityMagicSolidifier(PollutionID("magic_solidifier")));
		MAGIC_BREWERY = registerMetaTileEntity(15947, new MetaTileEntityMagicBrewery(PollutionID("magic_brewery")));
		MAGIC_DISTILLERY = registerMetaTileEntity(15948, new MetaTileEntityMagicDistillery(PollutionID("magic_distillery")));
		INDUSTRIAL_INFUSION = registerMetaTileEntity(15949, new MetaTileEntityIndustrialInfusion(PollutionID("industrial_infusion")));
		MAGIC_BATTERY = registerMetaTileEntity(15950, new MetaTileEntityMagicBattery(PollutionID("magic_battery")));
		MAGIC_ALLOY_BLAST = registerMetaTileEntity(15951, new MetaTileEntityMagicAlloyBlastSmelter(PollutionID("magic_alloy_blast")));
		MAGIC_CHEMICAL_REACTOR = registerMetaTileEntity(15952, new MetaTileEntityMagicChemicalReactor(PollutionID("magic_chemical_reactor")));
		MAGIC_AUTOCLAVE = registerMetaTileEntity(15953, new MetaTileEntityMagicAutoclave(PollutionID("magic_autoclave")));
		MAGIC_EXTRUDER = registerMetaTileEntity(15954, new MetaTileEntityMagicExtruder(PollutionID("magic_extruder")));
		MAGIC_GREEN_HOUSE = registerMetaTileEntity(15955, new MetaTileEntityMagicGreenHouse(PollutionID("magic_green_house")));
		ESSENCE_COLLECTOR = registerMetaTileEntity(15956, new MetaTileEntityEssenceCollector(PollutionID("essence_collector")));
		MAGIC_FUSION_REACTOR = registerMetaTileEntity(15957, new MetaTileEntityMagicFusionReactor(PollutionID("magic_fusion_reactor")));

		MAGIC_TURBINE[0] = registerMetaTileEntity(15958,
				new POMetaTileEntitySingleTurbine(PollutionID("magic_turbine.lv"), PORecipeMaps.MAGIC_TURBINE_FUELS,
						GTQTTextures.ROCKET_ENGINE_OVERLAY, 1, GTUtility.genericGeneratorTankSizeFunction));
		MAGIC_TURBINE[1] = registerMetaTileEntity(15959,
				new POMetaTileEntitySingleTurbine(PollutionID("magic_turbine.mv"), PORecipeMaps.MAGIC_TURBINE_FUELS,
						GTQTTextures.ROCKET_ENGINE_OVERLAY, 2, GTUtility.genericGeneratorTankSizeFunction));
		MAGIC_TURBINE[2] = registerMetaTileEntity(15960,
				new POMetaTileEntitySingleTurbine(PollutionID("magic_turbine.hv"), PORecipeMaps.MAGIC_TURBINE_FUELS,
						GTQTTextures.ROCKET_ENGINE_OVERLAY, 3, GTUtility.genericGeneratorTankSizeFunction));

		LARGE_MAGIC_TURBINE = registerMetaTileEntity(15961, new MetaTileEntityLargeTurbine(PollutionID("large_turbine.magic"),
				PORecipeMaps.MAGIC_TURBINE_FUELS, 4,
				PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT),
				PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.STAINLESS_STEEL_GEARBOX),
				POTextures.SPELL_PRISM_HOT, true, Textures.HPCA_OVERLAY));

		NODE_PRODUCER = registerMetaTileEntity(15962, new MetaTileEntityNodeProducer(PollutionID("node_producer")));
		LARGE_NODE_GENERATOR = registerMetaTileEntity(15963, new MetaTileEntityLargeNodeGenerator(PollutionID("large_node_generator")));
		NODE_WASHER = registerMetaTileEntity(15964, new MetaTileEntityNodeWasher(PollutionID("node_washer")));

		MANA_GENERATOR[1] = registerMetaTileEntity(15965, new ManaGeneratorTileEntity(PollutionID("mana_gen_lv"), 1));
		MANA_GENERATOR[2] = registerMetaTileEntity(15966, new ManaGeneratorTileEntity(PollutionID("mana_gen_mv"), 2));
		MANA_GENERATOR[3] = registerMetaTileEntity(15967, new ManaGeneratorTileEntity(PollutionID("mana_gen_hv"), 3));
		MANA_GENERATOR[4] = registerMetaTileEntity(15968, new ManaGeneratorTileEntity(PollutionID("mana_gen_ev"), 4));
		MANA_GENERATOR[5] = registerMetaTileEntity(15969, new ManaGeneratorTileEntity(PollutionID("mana_gen_iv"), 5));

		BMHPCA_EMPTY_COMPONENT = registerMetaTileEntity(15980,
				new MetaTileEntityBMHPCAEmpty(PollutionID("bm_hpca.empty_component")));
		BMHPCA_COMPUTATION_COMPONENT = registerMetaTileEntity(15981,
				new MetaTileEntityBMHPCAComputation(PollutionID("bm_hpca.super_computation_component"), false));
		BMHPCA_ADVANCED_COMPUTATION_COMPONENT = registerMetaTileEntity(15982,
				new MetaTileEntityBMHPCAComputation(PollutionID("bm_hpca.ultimate_computation_component"), true));
		BMHPCA_ADVANCED_COOLER_COMPONENT = registerMetaTileEntity(15983,
				new MetaTileEntityBMHPCACooler(PollutionID("bm_hpca.advance_heat_sink_component"), false,false));
		BMHPCA_SUPER_COOLER_COMPONENT = registerMetaTileEntity(15984,
				new MetaTileEntityBMHPCACooler(PollutionID("bm_hpca.super_cooler_component"), true,false));
		BMHPCA_ULTIMATE_COOLER_COMPONENT = registerMetaTileEntity(15985,
				new MetaTileEntityBMHPCACooler(PollutionID("bm_hpca.ultimate_cooler_component"), false,true));
		BMHPCA_BRIDGE_COMPONENT = registerMetaTileEntity(15986,
				new MetaTileEntityBMHPCABridge(PollutionID("bm_hpca.bridge_component")));

		BMHPCA= registerMetaTileEntity(15990, new MetaTileEntityBMHPCA(PollutionID("bm_hpca")));

		for (int i = 0; i < 9; i++) {
			String tierName = GTValues.VN[i + 1].toLowerCase();
			FLUX_MUFFLERS[i] = registerMetaTileEntity(16000 + i, new MetaTileEntityFluxMuffler(PollutionID("pollution_muffler_hatch." + tierName), i + 1));
		}

		for (int i = 0; i < VIS_HATCH.length; i++) {
			int tier = GTValues.LV + i;
			VIS_HATCH[i] = registerMetaTileEntity(16020 + i, new MetaTileEntityVisHatch(
					PollutionID(String.format("vis_hatch.%s", GTValues.VN[tier])), tier));
		}

		int kind;
		for (kind = 1; kind <= 6; kind++) {
			SOLAR_PLATE[kind * 3 - 3] = registerMetaTileEntity(16060 + kind * 3 - 3, new MetaTileEntitySolarPlate(
					PollutionID(String.format("solar_plate_%s.%s", 1, kind)), 1, kind, SOLAR_PLATE_I));
			SOLAR_PLATE[kind * 3 - 2] = registerMetaTileEntity(16060 + kind * 3 - 2, new MetaTileEntitySolarPlate(
					PollutionID(String.format("solar_plate_%s.%s", 2, kind)), 2, kind, SOLAR_PLATE_II));
			SOLAR_PLATE[kind * 3 - 1] = registerMetaTileEntity(16060 + kind * 3 - 1, new MetaTileEntitySolarPlate(
					PollutionID(String.format("solar_plate_%s.%s", 3, kind)), 3, kind, SOLAR_PLATE_III));

		}

		for (int i = 0; i < MANA_HATCH.length; i++) {
			int tier = GTValues.LV + i;
			MANA_HATCH[i] = registerMetaTileEntity(15800 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_hatch.%s", GTValues.VN[tier])), tier));
		}
		for (int i = 0; i < MANA_POOL_HATCH.length; i++) {
			int tier = GTValues.LV + i;
			MANA_POOL_HATCH[i] = registerMetaTileEntity(15815 + i, new MetaTileEntityManaPoolHatch(PollutionID(String.format("mana_pool_hatch.%s", GTValues.VN[tier])), tier));
		}

		ENDOFLAME_ARRAY = registerMetaTileEntity(15851, new MetaTileEntityEndoflameArray(PollutionID("endoflame_array")));

		BOT_DISTILLERY = registerMetaTileEntity(15852, new MetaTileEntityBotDistillery(PollutionID("bot_distillery")));
		Mana_PLATE = registerMetaTileEntity(15853, new MetaTileEntityManaPlate(PollutionID("mana_plate")));
		MAGIC_ASSEMBLER = registerMetaTileEntity(15854, new MetaTileEntityMagicAssembler(PollutionID("magic_assembler")));
		NODE_BLAST_FURNACE = registerMetaTileEntity(15855, new MetaTileEntityNodeBlastFurnace(PollutionID("node_blast_furnace")));
		SMALL_CHEMICAL_PLANT = registerMetaTileEntity(15856, new MetaTileEntitySmallChemicalPlant(PollutionID("small_chemical_plant")));
		ESSENCE_SMELTER = registerMetaTileEntity(15857, new MetaTileEntityEssenceSmelter(PollutionID("essence_smelter")));
		BOT_GAS_COLLECTOR = registerMetaTileEntity(15858, new MetaTileEntityBotGasCollector(PollutionID("bot_gas_collector")));
		GT_ESSENCE_SMELTER = registerMetaTileEntity(15859, new MetaTileEntityGtEssenceSmelter(PollutionID("gt_essence_smelter")));
		BOT_VACUUM_FREEZER = registerMetaTileEntity(15860, new MetaTileEntityBotVacuumFreezer(PollutionID("bot_vacuum_freezer")));
		CENTRAL_VIS_TOWER = registerMetaTileEntity(15861, new MetaTileEntityCentralVisTower(PollutionID("central_vis_tower")));
		BOT_CIRCUIT_ASSEMBLER = registerMetaTileEntity(15862, new MetaTileEntityBotCircuitAssembler(PollutionID("bot_circuit_assembler")));
		NODE_FUSION_REACTOR[0] = registerMetaTileEntity(15863, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.luv"), 6));
		NODE_FUSION_REACTOR[1] = registerMetaTileEntity(15864, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.zpm"), 7));
		NODE_FUSION_REACTOR[2] = registerMetaTileEntity(15865, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.uv"), 8));
		MANA_INFUSION_REACTOR = registerMetaTileEntity(15866, new MetaTileEntityManaInfusionReactor(PollutionID("mana_infusion_reactor")));
		//Primitive
		PRIMITIVE_MUD_PUMP = registerMetaTileEntity(16100, new MetaTileEntityPrimitiveMudPump(PollutionID("primitive_mud_pump")));
		PRIMITIVE_STOVE = registerMetaTileEntity(16101, new MetaTileEntityStove(PollutionID("primitive_stove")));
		//FluxPromotedGenerator

		FLUX_PROMOTED_FUEL_CELL[0] = registerMetaTileEntity(16102, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.lv"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY,1, GTUtility.genericGeneratorTankSizeFunction));
		FLUX_PROMOTED_FUEL_CELL[1] = registerMetaTileEntity(16103, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.mv"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY,2, GTUtility.genericGeneratorTankSizeFunction));
		FLUX_PROMOTED_FUEL_CELL[2] = registerMetaTileEntity(16104, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.hv"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY,3, GTUtility.genericGeneratorTankSizeFunction));
		FLUX_PROMOTED_FUEL_CELL[3] = registerMetaTileEntity(16105, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.ev"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY,4, GTUtility.genericGeneratorTankSizeFunction));
		FLUX_PROMOTED_FUEL_CELL[4] = registerMetaTileEntity(16106, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.iv"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY,5, GTUtility.genericGeneratorTankSizeFunction));

		//蛋鸡
		MAGIC_ENERGY_ABSORBER[0] = registerMetaTileEntity(16107,new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.lv"),1));
		MAGIC_ENERGY_ABSORBER[1] = registerMetaTileEntity(16108,new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.mv"),2));
		MAGIC_ENERGY_ABSORBER[2] = registerMetaTileEntity(16109,new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.hv"),3));
		MAGIC_ENERGY_ABSORBER[3] = registerMetaTileEntity(16110,new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.ev"),4));
		MAGIC_ENERGY_ABSORBER[4] = registerMetaTileEntity(16111,new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.iv"),5));

		//微缩节点反应堆
		SMALL_NODE_GENERATOR[0] = registerMetaTileEntity(16112,new MetaTileEntitySmallNodeGenerator(PollutionID("pollution_small_node_generator.luv"),6));
		SMALL_NODE_GENERATOR[1] = registerMetaTileEntity(16113,new MetaTileEntitySmallNodeGenerator(PollutionID("pollution_small_node_generator.zpm"),7));
		SMALL_NODE_GENERATOR[2] = registerMetaTileEntity(16114,new MetaTileEntitySmallNodeGenerator(PollutionID("pollution_small_node_generator.uv"),8));
		SMALL_NODE_GENERATOR[3] = registerMetaTileEntity(16115,new MetaTileEntitySmallNodeGenerator(PollutionID("pollution_small_node_generator.uhv"),9));
		//启命英机
		Muti_Dan_De_Life_On = registerMetaTileEntity(16118, new MetaTileEntityMultiDanDeLifeOn(PollutionID("pollution_multi_dan_de_life_on")));
		//巨型魔力轮机
		MEGA_MANA_TURBINE = registerMetaTileEntity(16119, new MetaTileEntityMegaManaTurbine(PollutionID("pollution_mega_mana_turbine"),PORecipeMaps.MANA_TO_EU,10,
				POTextures.MANA_5, false, Textures.HPCA_OVERLAY));
		//大型魔力轮机
		LARGE_MANA_TURBINE = registerMetaTileEntity(16120, new MetaTileEntityLargeTurbine(PollutionID("pollution_large_mana_turbine"),
				PORecipeMaps.MANA_TO_EU, 6,
				PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3),
				PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_PIPE),
				POTextures.MANA_3, false, Textures.HPCA_OVERLAY));

		SOURCE_CHARGE=registerMetaTileEntity(16150,new MetaTileEntitySourceCharge(PollutionID("source_charge")));


	}
}
