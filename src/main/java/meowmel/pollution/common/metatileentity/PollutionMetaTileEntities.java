package meowmel.pollution.common.metatileentity;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.WorkableTieredMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.multi.electric.generator.MetaTileEntityLargeTurbine;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import meowmel.pollution.Pollution;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.pollution.common.block.metablocks.POManaPlate;
import meowmel.pollution.common.block.metablocks.POTurbine;
import meowmel.pollution.common.metatileentity.multiblock.*;
import meowmel.pollution.common.metatileentity.multiblock.MetaTileEntityFluxClear;
import meowmel.pollution.common.metatileentity.multiblock.bloodMagic.MetaTileEntityBMHPCA;
import meowmel.pollution.common.metatileentity.multiblock.bot.*;
import meowmel.pollution.common.metatileentity.multiblock.generator.MetaTileEntityMagicLargeTurbine;
import meowmel.pollution.common.metatileentity.multiblock.generator.MetaTileEntityMagicMegaTurbine;
import meowmel.pollution.common.metatileentity.multiblock.generator.MetaTileEntityMultiDanDeLifeOn;
import meowmel.pollution.common.metatileentity.multiblock.magic.*;
import meowmel.pollution.common.metatileentity.multiblockpart.*;
import meowmel.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCABridge;
import meowmel.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCAComputation;
import meowmel.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCACooler;
import meowmel.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCAEmpty;
import meowmel.pollution.common.metatileentity.single.*;
import meowmel.pollution.common.metatileentity.storage.MetaTileEntityAspectTank;
import meowmel.gtqtcore.client.textures.GTQTTextures;
import meowmel.gtqtcore.common.metatileentities.multi.electric.generator.MetaTileEntityMegaTurbine;
import net.minecraft.util.ResourceLocation;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static meowmel.pollution.api.recipes.PORecipeMaps.MAGIC_TURBINE_FUELS;
import static meowmel.pollution.client.textures.POTextures.*;

public class PollutionMetaTileEntities {
   // public static final MetaTileEntityMultiblockPart[] FLUX_MUFFLERS = new MetaTileEntityMultiblockPart[9];
    public static final TieredMetaTileEntity[] AURA_GENERATORS = new TieredMetaTileEntity[6];
    public static final MetaTileEntityVisProvider[] VIS_PROVIDERS = new MetaTileEntityVisProvider[9];
    public static final MetaTileEntityFluxClear[] VIS_CLEAR = new MetaTileEntityFluxClear[4];
    public static final MetaTileEntityMultiblockPart[] FLUX_MUFFLERS = new MetaTileEntityMultiblockPart[9];
    public static final MetaTileEntityFluxClear[] FLUX_CLEARS = new MetaTileEntityFluxClear[3];
    public static final MetaTileEntity[] ASPECT_TANK = new MetaTileEntity[10];
    public static final SimpleGeneratorMetaTileEntity[] MAGIC_TURBINE = new SimpleGeneratorMetaTileEntity[3];
    public static final WorkableTieredMetaTileEntity[] FLUX_PROMOTED_FUEL_CELL = new WorkableTieredMetaTileEntity[5];
    public static final TieredMetaTileEntity[] MAGIC_ENERGY_ABSORBER = new TieredMetaTileEntity[5];
    public static final TieredMetaTileEntity[] SMALL_NODE_GENERATOR = new TieredMetaTileEntity[4];
    public static MetaTileEntityInfusedExchange INFUSED_EXCHANGE;
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

    public static MetaTileEntityManaHatch[] MANA_INPUT_HATCH_1A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_INPUT_HATCH_4A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_INPUT_HATCH_16A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_INPUT_HATCH_64A = new MetaTileEntityManaHatch[14];

    public static MetaTileEntityManaHatch[] MANA_OUTPUT_HATCH_1A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_OUTPUT_HATCH_4A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_OUTPUT_HATCH_16A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_OUTPUT_HATCH_64A = new MetaTileEntityManaHatch[14];

    public static MetaTileEntityManaPoolHatch[] MANA_POOL_INPUT_HATCH = new MetaTileEntityManaPoolHatch[14];
    public static MetaTileEntityManaPoolHatch[] MANA_POOL_OUTPUT_HATCH = new MetaTileEntityManaPoolHatch[14];

    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_INPUT_HATCH_1A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_INPUT_HATCH_4A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_INPUT_HATCH_16A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_INPUT_HATCH_64A = new MetaTileEntityWirelessManaHatch[14];

    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_OUTPUT_HATCH_1A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_OUTPUT_HATCH_4A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_OUTPUT_HATCH_16A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_OUTPUT_HATCH_64A = new MetaTileEntityWirelessManaHatch[14];

    public static MetaTileEntityWirelessManaPoolHatch[] WIRELESS_MANA_POOL_INPUT_HATCH = new MetaTileEntityWirelessManaPoolHatch[14];
    public static MetaTileEntityWirelessManaPoolHatch[] WIRELESS_MANA_POOL_OUTPUT_HATCH = new MetaTileEntityWirelessManaPoolHatch[14];

    public static MetaTileEntityMagicLargeTurbine LARGE_MAGIC_TURBINE;
    public static MetaTileEntityMagicLargeTurbine LARGE_MANA_TURBINE;
    public static MetaTileEntityMagicMegaTurbine MEGA_MAGIC_TURBINE;
    public static MetaTileEntityMagicMegaTurbine MEGA_MANA_TURBINE;


    public static MetaTileEntitySolarPlate[] SOLAR_PLATE = new MetaTileEntitySolarPlate[18];
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

    public static MetaTileEntityManaPetalApothecary MANA_PETAL_APOTHECARY;
    public static MetaTileEntityManaRuneAltar MANA_RUNE_ALTAR;
    public static MetaTileEntityIndustrialPureDaisy INDUSTRIAL_PURE_DAISY;

    public static ResourceLocation PollutionID(String id) {
        return new ResourceLocation(Pollution.MODID, id);
    }

    public static void initialization() {
        //单方块发电
        for (int i = 0; i < AURA_GENERATORS.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            AURA_GENERATORS[i] = registerMetaTileEntity(i, new MetaTileEntityVisGenerator(PollutionID("vis." + tierName), i + 1));
        }

        MAGIC_TURBINE[0] = registerMetaTileEntity(10,
                new SimpleGeneratorMetaTileEntity(PollutionID("magic_turbine.lv"), MAGIC_TURBINE_FUELS,
                        GTQTTextures.ROCKET_ENGINE_OVERLAY, 1, GTUtility.genericGeneratorTankSizeFunction,1));
        MAGIC_TURBINE[1] = registerMetaTileEntity(11,
                new SimpleGeneratorMetaTileEntity(PollutionID("magic_turbine.mv"), MAGIC_TURBINE_FUELS,
                        GTQTTextures.ROCKET_ENGINE_OVERLAY, 2, GTUtility.genericGeneratorTankSizeFunction,1));
        MAGIC_TURBINE[2] = registerMetaTileEntity(12,
                new SimpleGeneratorMetaTileEntity(PollutionID("magic_turbine.hv"), MAGIC_TURBINE_FUELS,
                        GTQTTextures.ROCKET_ENGINE_OVERLAY, 3, GTUtility.genericGeneratorTankSizeFunction,1));

        MANA_GENERATOR[1] = registerMetaTileEntity(15, new ManaGeneratorTileEntity(PollutionID("mana_gen_lv"), 1));
        MANA_GENERATOR[2] = registerMetaTileEntity(16, new ManaGeneratorTileEntity(PollutionID("mana_gen_mv"), 2));
        MANA_GENERATOR[3] = registerMetaTileEntity(17, new ManaGeneratorTileEntity(PollutionID("mana_gen_hv"), 3));
        MANA_GENERATOR[4] = registerMetaTileEntity(18, new ManaGeneratorTileEntity(PollutionID("mana_gen_ev"), 4));
        MANA_GENERATOR[5] = registerMetaTileEntity(19, new ManaGeneratorTileEntity(PollutionID("mana_gen_iv"), 5));

        //FluxPromotedGenerator
        /*
        FLUX_PROMOTED_FUEL_CELL[0] = registerMetaTileEntity(20, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.lv"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY, 1, GTUtility.genericGeneratorTankSizeFunction));
        FLUX_PROMOTED_FUEL_CELL[1] = registerMetaTileEntity(21, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.mv"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY, 2, GTUtility.genericGeneratorTankSizeFunction));
        FLUX_PROMOTED_FUEL_CELL[2] = registerMetaTileEntity(22, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.hv"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY, 3, GTUtility.genericGeneratorTankSizeFunction));
        FLUX_PROMOTED_FUEL_CELL[3] = registerMetaTileEntity(23, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.ev"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY, 4, GTUtility.genericGeneratorTankSizeFunction));
        FLUX_PROMOTED_FUEL_CELL[4] = registerMetaTileEntity(24, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell.iv"), GTQTcoreRecipeMaps.FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY, 5, GTUtility.genericGeneratorTankSizeFunction));
*/

        //微缩节点反应堆
        SMALL_NODE_GENERATOR[0] = registerMetaTileEntity(20, new MetaTileEntitySmallNodeGenerator(PollutionID("pollution_small_node_generator.luv"), 6));
        SMALL_NODE_GENERATOR[1] = registerMetaTileEntity(21, new MetaTileEntitySmallNodeGenerator(PollutionID("pollution_small_node_generator.zpm"), 7));
        SMALL_NODE_GENERATOR[2] = registerMetaTileEntity(22, new MetaTileEntitySmallNodeGenerator(PollutionID("pollution_small_node_generator.uv"), 8));
        SMALL_NODE_GENERATOR[3] = registerMetaTileEntity(23, new MetaTileEntitySmallNodeGenerator(PollutionID("pollution_small_node_generator.uhv"), 9));


        //蛋鸡
        MAGIC_ENERGY_ABSORBER[0] = registerMetaTileEntity(25, new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.lv"), 1));
        MAGIC_ENERGY_ABSORBER[1] = registerMetaTileEntity(26, new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.mv"), 2));
        MAGIC_ENERGY_ABSORBER[2] = registerMetaTileEntity(27, new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.hv"), 3));
        MAGIC_ENERGY_ABSORBER[3] = registerMetaTileEntity(28, new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.ev"), 4));
        MAGIC_ENERGY_ABSORBER[4] = registerMetaTileEntity(29, new MetaTileEntityMagicEnergyAbsorber(PollutionID("pollution_magic_energy_absorber.iv"), 5));


        // 太阳能
        int kind;
        for (kind = 1; kind <= 6; kind++) {
            SOLAR_PLATE[kind * 3 - 3] = registerMetaTileEntity(30 + kind * 3 - 3, new MetaTileEntitySolarPlate(
                    PollutionID(String.format("solar_plate_%s.%s", 1, kind)), 1, kind, SOLAR_PLATE_I));
            SOLAR_PLATE[kind * 3 - 2] = registerMetaTileEntity(30 + kind * 3 - 2, new MetaTileEntitySolarPlate(
                    PollutionID(String.format("solar_plate_%s.%s", 2, kind)), 2, kind, SOLAR_PLATE_II));
            SOLAR_PLATE[kind * 3 - 1] = registerMetaTileEntity(30 + kind * 3 - 1, new MetaTileEntitySolarPlate(
                    PollutionID(String.format("solar_plate_%s.%s", 3, kind)), 3, kind, SOLAR_PLATE_III));
        }

        //多方块发电
        LARGE_MAGIC_TURBINE = registerMetaTileEntity(100, new MetaTileEntityMagicLargeTurbine(PollutionID("large_turbine.magic"),
                MAGIC_TURBINE_FUELS, GTValues.EV,
                PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT),
                PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.STAINLESS_STEEL_GEARBOX),
                POTextures.SPELL_PRISM_HOT, true, Textures.HPCA_OVERLAY));

        MEGA_MAGIC_TURBINE = registerMetaTileEntity(101, new MetaTileEntityMagicMegaTurbine(PollutionID("mega_turbine.magic"),
                MAGIC_TURBINE_FUELS, GTValues.IV,
                PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT),
                PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.STAINLESS_STEEL_GEARBOX),
                POTextures.SPELL_PRISM_HOT, true, Textures.HPCA_OVERLAY));

        //大型魔力轮机
        LARGE_MANA_TURBINE = registerMetaTileEntity(102, new MetaTileEntityMagicLargeTurbine(PollutionID("pollution_large_mana_turbine"),
                PORecipeMaps.MANA_TO_EU, GTValues.LuV,
                PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3),
                PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_PIPE),
                POTextures.MANA_3, false, Textures.HPCA_OVERLAY));

        //巨型魔力轮机
        MEGA_MANA_TURBINE = registerMetaTileEntity(103, new MetaTileEntityMagicMegaTurbine(PollutionID("pollution_large_mana_turbine"),
                PORecipeMaps.MANA_TO_EU, GTValues.ZPM,
                PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3),
                PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_PIPE),
                POTextures.MANA_3, false, Textures.HPCA_OVERLAY));

        //启命英机
        Muti_Dan_De_Life_On = registerMetaTileEntity(130, new MetaTileEntityMultiDanDeLifeOn(PollutionID("pollution_multi_dan_de_life_on")));

        //单方块
        for (int i = 0; i < VIS_PROVIDERS.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            VIS_PROVIDERS[i] = registerMetaTileEntity(200 + i - 1, new MetaTileEntityVisProvider(PollutionID("vis_provider." + tierName), i + 1));
        }

        for (int i = 0; i < VIS_CLEAR.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            VIS_CLEAR[i] = registerMetaTileEntity(210 + i - 1, new MetaTileEntityFluxClear(PollutionID("flux_clear." + tierName), i + 1));
        }

        for (int i = 0; i < FLUX_MUFFLERS.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            FLUX_MUFFLERS[i] = registerMetaTileEntity(220 + i, new MetaTileEntityFluxMuffler(PollutionID("pollution_muffler_hatch." + tierName), i + 1));
        }

        //多方块
        FLUX_CLEARS[0] = registerMetaTileEntity(300, new MetaTileEntityFluxClear(PollutionID("flux_clear.ev"), GTValues.EV));
        FLUX_CLEARS[1] = registerMetaTileEntity(301, new MetaTileEntityFluxClear(PollutionID("flux_clear.iv"), GTValues.IV));

        INFUSED_EXCHANGE = registerMetaTileEntity(302, new MetaTileEntityInfusedExchange(PollutionID("infused_exchange")));
        MAGIC_BENDER = registerMetaTileEntity(303, new MetaTileEntityMagicBender(PollutionID("magic_bender")));
        MAGIC_ELECTRIC_BLAST_FURNACE = registerMetaTileEntity(304, new MetaTileEntityMagicElectricBlastFurnace(PollutionID("magic_electric_blast_furnace")));
        MAGIC_CENTRIFUGE = registerMetaTileEntity(305, new MetaTileEntityMagicCentrifuge(PollutionID("magic_centrifuge")));
        MAGIC_ELECTROLYZER = registerMetaTileEntity(306, new MetaTileEntityMagicElectrolyzer(PollutionID("magic_electrolyzer")));
        MAGIC_MIXER = registerMetaTileEntity(307, new MetaTileEntityMagicMixer(PollutionID("magic_mixer")));
        MAGIC_MACERATOR = registerMetaTileEntity(308, new MetaTileEntityMagicMacerator(PollutionID("magic_macerator")));
        MAGIC_CHEMICAL_BATH = registerMetaTileEntity(309, new MetaTileEntityMagicChemicalBath(PollutionID("magic_chemical_bath")));
        MAGIC_SIFTER = registerMetaTileEntity(310, new MetaTileEntityMagicSifter(PollutionID("magic_sifter")));
        MAGIC_CUTTER = registerMetaTileEntity(311, new MetaTileEntityMagicCutter(PollutionID("magic_cutter")));
        MAGIC_WIREMILL = registerMetaTileEntity(312, new MetaTileEntityMagicWireMill(PollutionID("magic_wiremill")));
        MAGIC_SOLIDIFIER = registerMetaTileEntity(313, new MetaTileEntityMagicSolidifier(PollutionID("magic_solidifier")));
        MAGIC_BREWERY = registerMetaTileEntity(314, new MetaTileEntityMagicBrewery(PollutionID("magic_brewery")));
        MAGIC_DISTILLERY = registerMetaTileEntity(315, new MetaTileEntityMagicDistillery(PollutionID("magic_distillery")));
        INDUSTRIAL_INFUSION = registerMetaTileEntity(316, new MetaTileEntityIndustrialInfusion(PollutionID("industrial_infusion")));
        MAGIC_BATTERY = registerMetaTileEntity(317, new MetaTileEntityMagicBattery(PollutionID("magic_battery")));
        MAGIC_ALLOY_BLAST = registerMetaTileEntity(318, new MetaTileEntityMagicAlloyBlastSmelter(PollutionID("magic_alloy_blast")));
        MAGIC_CHEMICAL_REACTOR = registerMetaTileEntity(319, new MetaTileEntityMagicChemicalReactor(PollutionID("magic_chemical_reactor")));
        MAGIC_AUTOCLAVE = registerMetaTileEntity(320, new MetaTileEntityMagicAutoclave(PollutionID("magic_autoclave")));
        MAGIC_EXTRUDER = registerMetaTileEntity(321, new MetaTileEntityMagicExtruder(PollutionID("magic_extruder")));
        MAGIC_GREEN_HOUSE = registerMetaTileEntity(322, new MetaTileEntityMagicGreenHouse(PollutionID("magic_green_house")));
        ESSENCE_COLLECTOR = registerMetaTileEntity(323, new MetaTileEntityEssenceCollector(PollutionID("essence_collector")));
        MAGIC_FUSION_REACTOR = registerMetaTileEntity(324, new MetaTileEntityMagicFusionReactor(PollutionID("magic_fusion_reactor")));
        SOURCE_CHARGE = registerMetaTileEntity(325, new MetaTileEntitySourceCharge(PollutionID("source_charge")));
        ENDOFLAME_ARRAY = registerMetaTileEntity(326, new MetaTileEntityEndoflameArray(PollutionID("endoflame_array")));
        BOT_DISTILLERY = registerMetaTileEntity(327, new MetaTileEntityBotDistillery(PollutionID("bot_distillery")));
        Mana_PLATE = registerMetaTileEntity(328, new MetaTileEntityManaPlate(PollutionID("mana_plate")));
        MAGIC_ASSEMBLER = registerMetaTileEntity(329, new MetaTileEntityMagicAssembler(PollutionID("magic_assembler")));
        NODE_BLAST_FURNACE = registerMetaTileEntity(330, new MetaTileEntityNodeBlastFurnace(PollutionID("node_blast_furnace")));
        SMALL_CHEMICAL_PLANT = registerMetaTileEntity(331, new MetaTileEntitySmallChemicalPlant(PollutionID("small_chemical_plant")));
        ESSENCE_SMELTER = registerMetaTileEntity(332, new MetaTileEntityEssenceSmelter(PollutionID("essence_smelter")));
        BOT_GAS_COLLECTOR = registerMetaTileEntity(333, new MetaTileEntityBotGasCollector(PollutionID("bot_gas_collector")));
        GT_ESSENCE_SMELTER = registerMetaTileEntity(334, new MetaTileEntityGtEssenceSmelter(PollutionID("gt_essence_smelter")));
        BOT_VACUUM_FREEZER = registerMetaTileEntity(335, new MetaTileEntityBotVacuumFreezer(PollutionID("bot_vacuum_freezer")));
        CENTRAL_VIS_TOWER = registerMetaTileEntity(336, new MetaTileEntityCentralVisTower(PollutionID("central_vis_tower")));
        BOT_CIRCUIT_ASSEMBLER = registerMetaTileEntity(337, new MetaTileEntityBotCircuitAssembler(PollutionID("bot_circuit_assembler")));
        NODE_FUSION_REACTOR[0] = registerMetaTileEntity(338, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.luv"), 6));
        NODE_FUSION_REACTOR[1] = registerMetaTileEntity(339, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.zpm"), 7));
        NODE_FUSION_REACTOR[2] = registerMetaTileEntity(340, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.uv"), 8));
        MANA_INFUSION_REACTOR = registerMetaTileEntity(341, new MetaTileEntityManaInfusionReactor(PollutionID("mana_infusion_reactor")));
        NODE_PRODUCER = registerMetaTileEntity(342, new MetaTileEntityNodeProducer(PollutionID("node_producer")));
        LARGE_NODE_GENERATOR = registerMetaTileEntity(343, new MetaTileEntityLargeNodeGenerator(PollutionID("large_node_generator")));
        NODE_WASHER = registerMetaTileEntity(344, new MetaTileEntityNodeWasher(PollutionID("node_washer")));
        INDUSTRIAL_PURE_DAISY = registerMetaTileEntity(345, new MetaTileEntityIndustrialPureDaisy(PollutionID("industial_pure_daisy")));
        MANA_PETAL_APOTHECARY = registerMetaTileEntity(346, new MetaTileEntityManaPetalApothecary(PollutionID("mana_petal_apothecary")));
        MANA_RUNE_ALTAR = registerMetaTileEntity(347, new MetaTileEntityManaRuneAltar(PollutionID("mana_rune_altar")));

        //仓口
        for (int i = 0; i < VIS_HATCH.length; i++) {
            int tier = GTValues.LV + i;
            VIS_HATCH[i] = registerMetaTileEntity(400 + i, new MetaTileEntityVisHatch(
                    PollutionID(String.format("vis_hatch.%s", GTValues.VN[tier])), tier));
        }


        // 普通
        for (int i = 0; i < MANA_INPUT_HATCH_1A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_INPUT_HATCH_1A[i] = registerMetaTileEntity(415 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_input_hatch_1a.%s", GTValues.VN[tier])), tier,1,false));
            MANA_INPUT_HATCH_4A[i] = registerMetaTileEntity(430 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_input_hatch_4a.%s", GTValues.VN[tier])), tier,4,false));
            MANA_INPUT_HATCH_16A[i] = registerMetaTileEntity(445 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_input_hatch_16a.%s", GTValues.VN[tier])), tier,16,false));
            MANA_INPUT_HATCH_64A[i] = registerMetaTileEntity(460 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_input_hatch_64a.%s", GTValues.VN[tier])), tier,64,false));

            MANA_OUTPUT_HATCH_1A[i] = registerMetaTileEntity(475 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_output_hatch_1a.%s", GTValues.VN[tier])), tier,1,true));
            MANA_OUTPUT_HATCH_4A[i] = registerMetaTileEntity(490 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_output_hatch_4a.%s", GTValues.VN[tier])), tier,4,true));
            MANA_OUTPUT_HATCH_16A[i] = registerMetaTileEntity(505 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_output_hatch_16a.%s", GTValues.VN[tier])), tier,16,true));
            MANA_OUTPUT_HATCH_64A[i] = registerMetaTileEntity(520 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_output_hatch_64a.%s", GTValues.VN[tier])), tier,64,true));

            MANA_POOL_INPUT_HATCH[i] = registerMetaTileEntity(535 + i, new MetaTileEntityManaPoolHatch(PollutionID(String.format("mana_pool_input_hatch.%s", GTValues.VN[tier])), tier,false));
            MANA_POOL_OUTPUT_HATCH[i] = registerMetaTileEntity(550 + i, new MetaTileEntityManaPoolHatch(PollutionID(String.format("mana_pool_output_hatch.%s", GTValues.VN[tier])), tier,true));
        }

        // 无线
        for (int i = 0; i < WIRELESS_MANA_INPUT_HATCH_1A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_INPUT_HATCH_1A[i] = registerMetaTileEntity(600 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_input_hatch_1a.%s", GTValues.VN[tier])), tier,1,false));
            WIRELESS_MANA_INPUT_HATCH_16A[i] = registerMetaTileEntity(630 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_input_hatch_16a.%s", GTValues.VN[tier])), tier,16,false));
            WIRELESS_MANA_INPUT_HATCH_64A[i] = registerMetaTileEntity(645 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_input_hatch_64a.%s", GTValues.VN[tier])), tier,64,false));

            WIRELESS_MANA_OUTPUT_HATCH_1A[i] = registerMetaTileEntity(660 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_output_hatch_1a.%s", GTValues.VN[tier])), tier,1,true));
            WIRELESS_MANA_OUTPUT_HATCH_4A[i] = registerMetaTileEntity(675 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_output_hatch_4a.%s", GTValues.VN[tier])), tier,4,true));
            WIRELESS_MANA_OUTPUT_HATCH_16A[i] = registerMetaTileEntity(690 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_output_hatch_16a.%s", GTValues.VN[tier])), tier,16,true));
            WIRELESS_MANA_OUTPUT_HATCH_64A[i] = registerMetaTileEntity(705 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_output_hatch_64a.%s", GTValues.VN[tier])), tier,64,true));

            WIRELESS_MANA_POOL_INPUT_HATCH[i] = registerMetaTileEntity(720 + i, new MetaTileEntityWirelessManaPoolHatch(PollutionID(String.format("wireless.mana_pool_input_hatch.%s", GTValues.VN[tier])), tier,false));
            WIRELESS_MANA_POOL_OUTPUT_HATCH[i] = registerMetaTileEntity(735 + i, new MetaTileEntityWirelessManaPoolHatch(PollutionID(String.format("wireless.mana_pool_output_hatch.%s", GTValues.VN[tier])), tier,true));
        }

        // HPCA
        BMHPCA_EMPTY_COMPONENT = registerMetaTileEntity(900,
                new MetaTileEntityBMHPCAEmpty(PollutionID("bm_hpca.empty_component")));
        BMHPCA_COMPUTATION_COMPONENT = registerMetaTileEntity(901,
                new MetaTileEntityBMHPCAComputation(PollutionID("bm_hpca.super_computation_component"), false));
        BMHPCA_ADVANCED_COMPUTATION_COMPONENT = registerMetaTileEntity(902,
                new MetaTileEntityBMHPCAComputation(PollutionID("bm_hpca.ultimate_computation_component"), true));
        BMHPCA_ADVANCED_COOLER_COMPONENT = registerMetaTileEntity(903,
                new MetaTileEntityBMHPCACooler(PollutionID("bm_hpca.advance_heat_sink_component"), false, false));
        BMHPCA_SUPER_COOLER_COMPONENT = registerMetaTileEntity(904,
                new MetaTileEntityBMHPCACooler(PollutionID("bm_hpca.super_cooler_component"), true, false));
        BMHPCA_ULTIMATE_COOLER_COMPONENT = registerMetaTileEntity(905,
                new MetaTileEntityBMHPCACooler(PollutionID("bm_hpca.ultimate_cooler_component"), false, true));
        BMHPCA_BRIDGE_COMPONENT = registerMetaTileEntity(906,
                new MetaTileEntityBMHPCABridge(PollutionID("bm_hpca.bridge_component")));
        BMHPCA = registerMetaTileEntity(907, new MetaTileEntityBMHPCA(PollutionID("bm_hpca")));

        ASPECT_TANK[0] = registerMetaTileEntity(1000, new MetaTileEntityAspectTank(PollutionID("aspect_tank.lv"), 1, 10000));
        ASPECT_TANK[1] = registerMetaTileEntity(1001, new MetaTileEntityAspectTank(PollutionID("aspect_tank.mv"), 2, 20000));
        ASPECT_TANK[2] = registerMetaTileEntity(1002, new MetaTileEntityAspectTank(PollutionID("aspect_tank.hv"), 3, 40000));
        ASPECT_TANK[3] = registerMetaTileEntity(1003, new MetaTileEntityAspectTank(PollutionID("aspect_tank.ev"), 4, 80000));
        ASPECT_TANK[4] = registerMetaTileEntity(1004, new MetaTileEntityAspectTank(PollutionID("aspect_tank.iv"), 5, 160000));
        ASPECT_TANK[5] = registerMetaTileEntity(1005, new MetaTileEntityAspectTank(PollutionID("aspect_tank.luv"), 6, 320000));
        ASPECT_TANK[6] = registerMetaTileEntity(1006, new MetaTileEntityAspectTank(PollutionID("aspect_tank.zpm"), 7, 640000));
        ASPECT_TANK[7] = registerMetaTileEntity(1007, new MetaTileEntityAspectTank(PollutionID("aspect_tank.uv"), 8, 1280000));
        ASPECT_TANK[8] = registerMetaTileEntity(1008, new MetaTileEntityAspectTank(PollutionID("aspect_tank.uhv"), 9, 2560000));
    }
}
