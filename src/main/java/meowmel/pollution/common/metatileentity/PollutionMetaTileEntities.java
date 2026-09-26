package meowmel.pollution.common.metatileentity;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.WorkableTieredMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;
import meowmel.pollution.Pollution;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.pollution.common.block.metablocks.POManaPlate;

import meowmel.pollution.common.metatileentity.multiblock.*;
import meowmel.pollution.common.metatileentity.multiblock.bloodMagic.MetaTileEntityBMHPCA;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityIndustrialStarlightInfuser;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityIndustrialLightwell;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityCelestialObservationArray;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityCelestialCalibrationMatrix;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityCelestialCrystalGrowthArray;
import meowmel.pollution.common.metatileentity.multiblock.astral.ConstellationTowerDefinition;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityConstellationTower;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityStarstreamNexusObelisk;
import meowmel.pollution.common.metatileentity.multiblock.bot.*;
import meowmel.pollution.common.metatileentity.multiblock.generator.MetaTileEntityMagicLargeTurbine;
import meowmel.pollution.common.metatileentity.multiblock.generator.MetaTileEntityLargeEssentiaGenerator;
import meowmel.pollution.common.metatileentity.multiblock.generator.MetaTileEntityMagicMegaTurbine;
import meowmel.pollution.common.metatileentity.multiblock.generator.MetaTileEntityMultiDanDeLifeOn;
import meowmel.pollution.common.metatileentity.multiblock.magic.*;
import meowmel.pollution.common.metatileentity.multiblockpart.*;
import meowmel.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCABridge;
import meowmel.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCAComputation;
import meowmel.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCACooler;
import meowmel.pollution.common.metatileentity.multiblockpart.BMHPCA.MetaTileEntityBMHPCAEmpty;
import meowmel.pollution.common.metatileentity.single.*;
import meowmel.pollution.common.metatileentity.storage.MetaTileEntityQuantumAspectTank;
import meowmel.pollution.common.metatileentity.storage.MetaTileEntityQuantumManaTank;
import meowmel.gtqtcore.client.textures.GTQTTextures;
import net.minecraft.util.ResourceLocation;

import static gregtech.api.util.GTUtility.gregtechId;
import static gregtech.common.metatileentities.MetaTileEntities.MUFFLER_HATCH;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static meowmel.pollution.api.recipes.PORecipeMaps.MAGIC_TURBINE_FUELS;
import static meowmel.pollution.client.textures.POTextures.*;

public class PollutionMetaTileEntities {

    // 单方块
    // 单方块发电机
    public static final SimpleGeneratorMetaTileEntity[] MAGIC_TURBINE = new SimpleGeneratorMetaTileEntity[5];
    public static final MetaTileEntityAuraGenerator[] AURA_GENERATORS = new MetaTileEntityAuraGenerator[5];
    public static final MetaTileEntitySolarPlate[] SOLAR_PLATE = new MetaTileEntitySolarPlate[18];
    public static final SimpleGeneratorMetaTileEntity[] MANA_GENERATOR = new SimpleGeneratorMetaTileEntity[5];
    public static final WorkableTieredMetaTileEntity[] FLUX_PROMOTED_FUEL_CELL = new WorkableTieredMetaTileEntity[5];
    public static final MetaTileEntityMagicEnergyAbsorber[] MAGIC_ENERGY_ABSORBER = new MetaTileEntityMagicEnergyAbsorber[5];
    public static final MetaTileEntitySmallNodeGenerator[] SMALL_NODE_GENERATOR = new MetaTileEntitySmallNodeGenerator[5];

    // 单方块机器
    public static final MetaTileEntityFluxClear[] VIS_CLEAR = new MetaTileEntityFluxClear[3];
    public static final MetaTileEntityVisProvider[] VIS_PROVIDERS = new MetaTileEntityVisProvider[3];

    // 多方块
    // 多方块发电机
    public static MetaTileEntityMagicLargeTurbine LARGE_MAGIC_TURBINE;
    public static MetaTileEntityLargeEssentiaGenerator LARGE_ESSENTIA_GENERATOR;

    public static MetaTileEntityMagicLargeTurbine LARGE_MANA_TURBINE;
    public static MetaTileEntityMagicMegaTurbine MEGA_MAGIC_TURBINE;
    public static MetaTileEntityMagicMegaTurbine MEGA_MANA_TURBINE;

    // 多方块机器
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

    public static MetaTileEntityManaPetalApothecary MANA_PETAL_APOTHECARY;
    public static MetaTileEntityManaRuneAltar MANA_RUNE_ALTAR;
    public static MetaTileEntityIndustrialPureDaisy INDUSTRIAL_PURE_DAISY;
    public static MetaTileEntityIndustrialStarlightInfuser INDUSTRIAL_STARLIGHT_INFUSER;
    public static MetaTileEntityIndustrialLightwell INDUSTRIAL_LIGHTWELL;
    public static MetaTileEntityCelestialObservationArray CELESTIAL_OBSERVATION_ARRAY;
    public static MetaTileEntityCelestialCalibrationMatrix CELESTIAL_CALIBRATION_MATRIX;
    public static MetaTileEntityCelestialCrystalGrowthArray CELESTIAL_CRYSTAL_GROWTH_ARRAY;
    public static final MetaTileEntityConstellationTower[] CONSTELLATION_TOWERS =
            new MetaTileEntityConstellationTower[ConstellationTowerDefinition.values().length];
    public static MetaTileEntityStarstreamNexusObelisk STARSTREAM_NEXUS_OBELISK;

    // 杂项
    public static final MetaTileEntityLargeFluxClear[] FLUX_CLEARS = new MetaTileEntityLargeFluxClear[3];

    // 血魔法HPCA
    public static MetaTileEntityBMHPCAEmpty BMHPCA_EMPTY_COMPONENT;
    public static MetaTileEntityBMHPCAComputation BMHPCA_COMPUTATION_COMPONENT;
    public static MetaTileEntityBMHPCAComputation BMHPCA_ADVANCED_COMPUTATION_COMPONENT;
    public static MetaTileEntityBMHPCACooler BMHPCA_ADVANCED_COOLER_COMPONENT;
    public static MetaTileEntityBMHPCACooler BMHPCA_SUPER_COOLER_COMPONENT;
    public static MetaTileEntityBMHPCACooler BMHPCA_ULTIMATE_COOLER_COMPONENT;
    public static MetaTileEntityBMHPCABridge BMHPCA_BRIDGE_COMPONENT;
    public static MetaTileEntityBMHPCA BMHPCA;


    // 杂项
    public static MetaTileEntitySourceCharge SOURCE_CHARGE;

    // 仓室
    public static final MetaTileEntityFluxMuffler[] FLUX_MUFFLERS = new MetaTileEntityFluxMuffler[9];
    public static MetaTileEntityVisHatch[] VIS_HATCH = new MetaTileEntityVisHatch[14];
    public static MetaTileEntityInfusedFluidHatch[] INFUSED_FLUID_HATCH = new MetaTileEntityInfusedFluidHatch[14];

    public static MetaTileEntityManaHatch[] MANA_INPUT_HATCH_1A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_INPUT_HATCH_4A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_INPUT_HATCH_16A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_INPUT_HATCH_64A = new MetaTileEntityManaHatch[14];

    public static MetaTileEntityManaHatch[] MANA_OUTPUT_HATCH_1A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_OUTPUT_HATCH_4A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_OUTPUT_HATCH_16A = new MetaTileEntityManaHatch[14];
    public static MetaTileEntityManaHatch[] MANA_OUTPUT_HATCH_64A = new MetaTileEntityManaHatch[14];

    public static MetaTileEntityEssentiaHatch[] ESSENTIA_HATCH = new MetaTileEntityEssentiaHatch[14];

    public static MetaTileEntityManaPoolHatch[] MANA_POOL_INPUT_HATCH = new MetaTileEntityManaPoolHatch[3];
    public static MetaTileEntityManaPoolHatch[] MANA_POOL_OUTPUT_HATCH = new MetaTileEntityManaPoolHatch[3];

    public static final MetaTileEntityAdvancedMufflerHatch[] ADVANCED_MUFFLER_HATCH = new MetaTileEntityAdvancedMufflerHatch[GTValues.UHV +1]; // LV-UHV

    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_INPUT_HATCH_1A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_INPUT_HATCH_4A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_INPUT_HATCH_16A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_INPUT_HATCH_64A = new MetaTileEntityWirelessManaHatch[14];

    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_OUTPUT_HATCH_1A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_OUTPUT_HATCH_4A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_OUTPUT_HATCH_16A = new MetaTileEntityWirelessManaHatch[14];
    public static MetaTileEntityWirelessManaHatch[] WIRELESS_MANA_OUTPUT_HATCH_64A = new MetaTileEntityWirelessManaHatch[14];

    public static MetaTileEntityWirelessManaPoolHatch[] WIRELESS_MANA_POOL_INPUT_HATCH = new MetaTileEntityWirelessManaPoolHatch[3];
    public static MetaTileEntityWirelessManaPoolHatch[] WIRELESS_MANA_POOL_OUTPUT_HATCH = new MetaTileEntityWirelessManaPoolHatch[3];

    public static MetaTileEntityFilterHatch FILTER_HATCH;
    public static MetaTileEntityTarotHatch TAROT_HATCH;
    public static MetaTileEntityBloodMagicHatch BLOOD_MAGIC_HATCH;
    public static MetaTileEntityAstralLensHatch ASTRAL_LENS_HATCH;
    public static MetaTileEntityAstralLensHatch ASTRAL_LENS_HATCH_ADVANCED;

    // 存储
    public static final MetaTileEntityQuantumAspectTank[] QUANTUM_ASPECT_TANKS = new MetaTileEntityQuantumAspectTank[9];
    public static final MetaTileEntityQuantumManaTank[] QUANTUM_MANA_TANKS = new MetaTileEntityQuantumManaTank[9];

    public static ResourceLocation PollutionID(String id) {
        return new ResourceLocation(Pollution.MODID, id);
    }

    public static void initialization() {
        // ===== 单方块发电机 =====
        // MAGIC_TURBINE 0-10
        for (int i = 0; i < MAGIC_TURBINE.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            MAGIC_TURBINE[i] = registerMetaTileEntity(i, new SimpleGeneratorMetaTileEntity(PollutionID("magic_turbine." + tierName), MAGIC_TURBINE_FUELS,
                            GTQTTextures.ROCKET_ENGINE_OVERLAY, i + 1, GTUtility.genericGeneratorTankSizeFunction,1));
        }

        // AURA_GENERATORS 10-19
        for (int i = 0; i < AURA_GENERATORS.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            AURA_GENERATORS[i] = registerMetaTileEntity(10 + i, new MetaTileEntityAuraGenerator(PollutionID("aura_generator." + tierName), i + 1));
        }

        // SOLAR_PLATE 20-38
        for (SolarPlateType type : SolarPlateType.values()) {
            int kind = type.getKind();
            SOLAR_PLATE[kind * 3 - 3] = registerMetaTileEntity(20 + kind * 3 - 3, new MetaTileEntitySolarPlate(
                    PollutionID(String.format("solar_plate_%s.%s", 1, kind)), 1, type, SOLAR_PLATE_I));
            SOLAR_PLATE[kind * 3 - 2] = registerMetaTileEntity(20 + kind * 3 - 2, new MetaTileEntitySolarPlate(
                    PollutionID(String.format("solar_plate_%s.%s", 2, kind)), 2, type, SOLAR_PLATE_II));
            SOLAR_PLATE[kind * 3 - 1] = registerMetaTileEntity(20 + kind * 3 - 1, new MetaTileEntitySolarPlate(
                    PollutionID(String.format("solar_plate_%s.%s", 3, kind)), 3, type, SOLAR_PLATE_III));
        }

        // MANA_GENERATOR 40-50
        for (int i = 0; i < MANA_GENERATOR.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            MANA_GENERATOR[i] = registerMetaTileEntity(40 + i, new ManaGeneratorTileEntity(PollutionID("mana_generator." + tierName), i + 1));
        }

        // FLUX_PROMOTED_FUEL_CELL 50-60
        /*
        for (int i = 0; i < FLUX_PROMOTED_FUEL_CELL.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            FLUX_PROMOTED_FUEL_CELL[i] = registerMetaTileEntity(40 + i, new MetaTileEntityFluxPromotedFuelCell(PollutionID("flux_promoted_fuel_cell." + tierName), FUEL_CELL, Textures.POWER_SUBSTATION_OVERLAY, i+1, GTUtility.genericGeneratorTankSizeFunction));
        }
         */

        // MAGIC_ENERGY_ABSORBER 60-70
        for (int i = 0; i < MAGIC_ENERGY_ABSORBER.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            MAGIC_ENERGY_ABSORBER[i] = registerMetaTileEntity(60 + i, new MetaTileEntityMagicEnergyAbsorber(PollutionID("magic_energy_absorber." + tierName), i + 1));
        }

        // SMALL_NODE_GENERATOR 70-80
        for (int i = 0; i < SMALL_NODE_GENERATOR.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            SMALL_NODE_GENERATOR[i] = registerMetaTileEntity(70 + i, new MetaTileEntitySmallNodeGenerator(PollutionID("small_node_generator." + tierName), i + 5));
        }

        // ===== 单方块机器 =====
        // VIS_CLEAR 100
        for (int i = 0; i < VIS_CLEAR.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            VIS_CLEAR[i] = registerMetaTileEntity(100 + i, new MetaTileEntityFluxClear(PollutionID("flux_clear." + tierName), i + 1));
        }

        // VIS_PROVIDERS
        for (int i = 0; i < VIS_PROVIDERS.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            VIS_PROVIDERS[i] = registerMetaTileEntity(110 + i, new MetaTileEntityVisProvider(PollutionID("vis_provider." + tierName), i + 1));
        }

        // ===== 多方块发电机 =====
        // LARGE_MAGIC_TURBINE
        LARGE_MAGIC_TURBINE = registerMetaTileEntity(500, new MetaTileEntityMagicLargeTurbine(PollutionID("large_turbine.magic"),
                MAGIC_TURBINE_FUELS, GTValues.EV,
                PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT),
                MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STAINLESS_STEEL_GEARBOX),
                POTextures.SPELL_PRISM_HOT, true, Textures.HPCA_OVERLAY));

        // LARGE_MANA_TURBINE
        LARGE_MANA_TURBINE = registerMetaTileEntity(501, new MetaTileEntityMagicLargeTurbine(PollutionID("large_turbine.mana"),
                PORecipeMaps.MANA_TO_EU, GTValues.LuV,
                PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3),
                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE),
                POTextures.MANA_3, false, Textures.HPCA_OVERLAY));

        // MEGA_MAGIC_TURBINE
        MEGA_MAGIC_TURBINE = registerMetaTileEntity(502, new MetaTileEntityMagicMegaTurbine(PollutionID("mega_turbine.magic"),
                MAGIC_TURBINE_FUELS, GTValues.IV,
                PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT),
                MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.STAINLESS_STEEL_GEARBOX),
                POTextures.SPELL_PRISM_HOT, true, Textures.HPCA_OVERLAY));

        // MEGA_MANA_TURBINE
        MEGA_MANA_TURBINE = registerMetaTileEntity(503, new MetaTileEntityMagicMegaTurbine(PollutionID("mega_turbine.mana"),
                PORecipeMaps.MANA_TO_EU, GTValues.ZPM,
                PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_3),
                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE),
                POTextures.MANA_3, false, Textures.HPCA_OVERLAY));

        // LARGE_ESSENTIA_GENERATOR
        LARGE_ESSENTIA_GENERATOR = registerMetaTileEntity(510,
                new MetaTileEntityLargeEssentiaGenerator(PollutionID("large_essentia_generator")));

        // ===== 多方块机器 =====
        INFUSED_EXCHANGE = registerMetaTileEntity(520, new MetaTileEntityInfusedExchange(PollutionID("infused_exchange")));
        MAGIC_BENDER = registerMetaTileEntity(521, new MetaTileEntityMagicBender(PollutionID("magic_bender")));
        MAGIC_CENTRIFUGE = registerMetaTileEntity(522, new MetaTileEntityMagicCentrifuge(PollutionID("magic_centrifuge")));
        MAGIC_ELECTRIC_BLAST_FURNACE = registerMetaTileEntity(523, new MetaTileEntityMagicElectricBlastFurnace(PollutionID("magic_electric_blast_furnace")));
        MAGIC_ELECTROLYZER = registerMetaTileEntity(524, new MetaTileEntityMagicElectrolyzer(PollutionID("magic_electrolyzer")));
        MAGIC_MIXER = registerMetaTileEntity(525, new MetaTileEntityMagicMixer(PollutionID("magic_mixer")));
        MAGIC_MACERATOR = registerMetaTileEntity(526, new MetaTileEntityMagicMacerator(PollutionID("magic_macerator")));
        MAGIC_CHEMICAL_BATH = registerMetaTileEntity(527, new MetaTileEntityMagicChemicalBath(PollutionID("magic_chemical_bath")));
        MAGIC_SIFTER = registerMetaTileEntity(528, new MetaTileEntityMagicSifter(PollutionID("magic_sifter")));
        MAGIC_CUTTER = registerMetaTileEntity(529, new MetaTileEntityMagicCutter(PollutionID("magic_cutter")));
        MAGIC_WIREMILL = registerMetaTileEntity(530, new MetaTileEntityMagicWireMill(PollutionID("magic_wiremill")));
        MAGIC_SOLIDIFIER = registerMetaTileEntity(531, new MetaTileEntityMagicSolidifier(PollutionID("magic_solidifier")));
        MAGIC_BREWERY = registerMetaTileEntity(532, new MetaTileEntityMagicBrewery(PollutionID("magic_brewery")));
        INDUSTRIAL_INFUSION = registerMetaTileEntity(533, new MetaTileEntityIndustrialInfusion(PollutionID("industrial_infusion")));
        MAGIC_BATTERY = registerMetaTileEntity(534, new MetaTileEntityMagicBattery(PollutionID("magic_battery")));
        MAGIC_CHEMICAL_REACTOR = registerMetaTileEntity(535, new MetaTileEntityMagicChemicalReactor(PollutionID("magic_chemical_reactor")));
        MAGIC_AUTOCLAVE = registerMetaTileEntity(536, new MetaTileEntityMagicAutoclave(PollutionID("magic_autoclave")));
        MAGIC_EXTRUDER = registerMetaTileEntity(537, new MetaTileEntityMagicExtruder(PollutionID("magic_extruder")));
        MAGIC_GREEN_HOUSE = registerMetaTileEntity(538, new MetaTileEntityMagicGreenHouse(PollutionID("magic_green_house")));
        MAGIC_DISTILLERY = registerMetaTileEntity(539, new MetaTileEntityMagicDistillery(PollutionID("magic_distillery")));
        MAGIC_ALLOY_BLAST = registerMetaTileEntity(540, new MetaTileEntityMagicAlloyBlastSmelter(PollutionID("magic_alloy_blast")));
        ESSENCE_COLLECTOR = registerMetaTileEntity(541, new MetaTileEntityEssenceCollector(PollutionID("essence_collector")));
        MAGIC_FUSION_REACTOR = registerMetaTileEntity(542, new MetaTileEntityMagicFusionReactor(PollutionID("magic_fusion_reactor")));
        NODE_PRODUCER = registerMetaTileEntity(543, new MetaTileEntityNodeProducer(PollutionID("node_producer")));
        LARGE_NODE_GENERATOR = registerMetaTileEntity(544, new MetaTileEntityLargeNodeGenerator(PollutionID("large_node_generator")));
        NODE_WASHER = registerMetaTileEntity(545, new MetaTileEntityNodeWasher(PollutionID("node_washer")));
        ENDOFLAME_ARRAY = registerMetaTileEntity(546, new MetaTileEntityEndoflameArray(PollutionID("endoflame_array")));
        BOT_DISTILLERY = registerMetaTileEntity(547, new MetaTileEntityBotDistillery(PollutionID("bot_distillery")));
        Mana_PLATE = registerMetaTileEntity(548, new MetaTileEntityManaPlate(PollutionID("mana_plate")));
        MAGIC_ASSEMBLER = registerMetaTileEntity(549, new MetaTileEntityMagicAssembler(PollutionID("magic_assembler")));
        NODE_BLAST_FURNACE = registerMetaTileEntity(550, new MetaTileEntityNodeBlastFurnace(PollutionID("node_blast_furnace")));
        SMALL_CHEMICAL_PLANT = registerMetaTileEntity(551, new MetaTileEntitySmallChemicalPlant(PollutionID("small_chemical_plant")));
        ESSENCE_SMELTER = registerMetaTileEntity(552, new MetaTileEntityEssenceSmelter(PollutionID("essence_smelter")));
        BOT_GAS_COLLECTOR = registerMetaTileEntity(553, new MetaTileEntityBotGasCollector(PollutionID("bot_gas_collector")));
        GT_ESSENCE_SMELTER = registerMetaTileEntity(554, new MetaTileEntityGtEssenceSmelter(PollutionID("gt_essence_smelter")));
        BOT_VACUUM_FREEZER = registerMetaTileEntity(555, new MetaTileEntityBotVacuumFreezer(PollutionID("bot_vacuum_freezer")));

        Muti_Dan_De_Life_On = registerMetaTileEntity(570, new MetaTileEntityMultiDanDeLifeOn(PollutionID("pollution_multi_dan_de_life_on")));
        CENTRAL_VIS_TOWER = registerMetaTileEntity(571, new MetaTileEntityCentralVisTower(PollutionID("central_vis_tower")));
        MANA_INFUSION_REACTOR = registerMetaTileEntity(572, new MetaTileEntityManaInfusionReactor(PollutionID("mana_infusion_reactor")));
        BOT_CIRCUIT_ASSEMBLER = registerMetaTileEntity(573, new MetaTileEntityBotCircuitAssembler(PollutionID("bot_circuit_assembler")));
        NODE_FUSION_REACTOR[0] = registerMetaTileEntity(574, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.luv"), 6));
        NODE_FUSION_REACTOR[1] = registerMetaTileEntity(575, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.zpm"), 7));
        NODE_FUSION_REACTOR[2] = registerMetaTileEntity(576, new MetaTileEntityNodeFusionReactor(PollutionID("node_fusion_reactor.uv"), 8));

        MANA_PETAL_APOTHECARY = registerMetaTileEntity(600, new MetaTileEntityManaPetalApothecary(PollutionID("mana_petal_apothecary")));
        MANA_RUNE_ALTAR = registerMetaTileEntity(601, new MetaTileEntityManaRuneAltar(PollutionID("mana_rune_altar")));
        INDUSTRIAL_PURE_DAISY = registerMetaTileEntity(602, new MetaTileEntityIndustrialPureDaisy(PollutionID("industial_pure_daisy")));
        INDUSTRIAL_STARLIGHT_INFUSER = registerMetaTileEntity(603,
                new MetaTileEntityIndustrialStarlightInfuser(PollutionID("industrial_starlight_infuser")));
        INDUSTRIAL_LIGHTWELL = registerMetaTileEntity(604,
                new MetaTileEntityIndustrialLightwell(PollutionID("industrial_lightwell")));
        CELESTIAL_OBSERVATION_ARRAY = registerMetaTileEntity(605,
                new MetaTileEntityCelestialObservationArray(PollutionID("celestial_observation_array")));
        CELESTIAL_CALIBRATION_MATRIX = registerMetaTileEntity(606,
                new MetaTileEntityCelestialCalibrationMatrix(PollutionID("celestial_calibration_matrix")));
        CELESTIAL_CRYSTAL_GROWTH_ARRAY = registerMetaTileEntity(607,
                new MetaTileEntityCelestialCrystalGrowthArray(PollutionID("celestial_crystal_growth_array")));

        ConstellationTowerDefinition[] constellationTowers = ConstellationTowerDefinition.values();
        for (int i = 0; i < constellationTowers.length; i++) {
            ConstellationTowerDefinition definition = constellationTowers[i];
            CONSTELLATION_TOWERS[i] = registerMetaTileEntity(610 + i,
                    new MetaTileEntityConstellationTower(PollutionID(definition.getControllerPath()), definition));
        }

        STARSTREAM_NEXUS_OBELISK = registerMetaTileEntity(630,
                new MetaTileEntityStarstreamNexusObelisk(PollutionID("starstream_nexus_obelisk")));

        // ===== 杂项 =====
        // FLUX_CLEARS
        FLUX_CLEARS[0] = registerMetaTileEntity(800, new MetaTileEntityLargeFluxClear(PollutionID("large_flux_clear.ev"), FluxClearType.EV));
        FLUX_CLEARS[1] = registerMetaTileEntity(801, new MetaTileEntityLargeFluxClear(PollutionID("large_flux_clear.iv"), FluxClearType.IV));
        FLUX_CLEARS[2] = registerMetaTileEntity(802, new MetaTileEntityLargeFluxClear(PollutionID("large_flux_clear.luv"), FluxClearType.LuV));

        // ===== 血魔法HPCA =====
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

        // ===== 杂项 =====
        // SOURCE_CHARGE
        SOURCE_CHARGE = registerMetaTileEntity(1000, new MetaTileEntitySourceCharge(PollutionID("source_charge")));

        // ===== 仓室 =====
        // FLUX_MUFFLERS
        for (int i = 0; i < FLUX_MUFFLERS.length; i++) {
            String tierName = GTValues.VN[i + 1].toLowerCase();
            FLUX_MUFFLERS[i] = registerMetaTileEntity(1100 + i, new MetaTileEntityFluxMuffler(PollutionID("flux_muffler_hatch." + tierName), i + 1));
        }

        // VIS_HATCH
        for (int i = 0; i < VIS_HATCH.length; i++) {
            int tier = GTValues.LV + i;
            VIS_HATCH[i] = registerMetaTileEntity(1115 + i, new MetaTileEntityVisHatch(
                    PollutionID(String.format("vis_hatch.%s", GTValues.VN[tier])), tier));
        }

        // INFUSED_FLUID_HATCH
        for (int i = 0; i < INFUSED_FLUID_HATCH.length; i++) {
            int tier = GTValues.LV + i;
            INFUSED_FLUID_HATCH[i] = registerMetaTileEntity(1130 + i,
                    new MetaTileEntityInfusedFluidHatch(
                            PollutionID("infused_fluid_hatch." + GTValues.VN[tier].toLowerCase()), tier));
        }

        // MANA_INPUT_HATCH_1A
        for (int i = 0; i < MANA_INPUT_HATCH_1A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_INPUT_HATCH_1A[i] = registerMetaTileEntity(1145 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_input_hatch_1a.%s", GTValues.VN[tier])), tier,1,false));
        }

        // MANA_INPUT_HATCH_4A
        for (int i = 0; i < MANA_INPUT_HATCH_4A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_INPUT_HATCH_4A[i] = registerMetaTileEntity(1160 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_input_hatch_4a.%s", GTValues.VN[tier])), tier,4,false));
        }

        // MANA_INPUT_HATCH_16A
        for (int i = 0; i < MANA_INPUT_HATCH_16A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_INPUT_HATCH_16A[i] = registerMetaTileEntity(1175 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_input_hatch_16a.%s", GTValues.VN[tier])), tier,16,false));
        }

        // MANA_INPUT_HATCH_64A
        for (int i = 0; i < MANA_INPUT_HATCH_64A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_INPUT_HATCH_64A[i] = registerMetaTileEntity(1190 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_input_hatch_64a.%s", GTValues.VN[tier])), tier,64,false));
        }

        // MANA_OUTPUT_HATCH_1A
        for (int i = 0; i < MANA_OUTPUT_HATCH_1A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_OUTPUT_HATCH_1A[i] = registerMetaTileEntity(1205 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_output_hatch_1a.%s", GTValues.VN[tier])), tier,1,true));
        }

        // MANA_OUTPUT_HATCH_4A
        for (int i = 0; i < MANA_OUTPUT_HATCH_4A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_OUTPUT_HATCH_4A[i] = registerMetaTileEntity(1220 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_output_hatch_4a.%s", GTValues.VN[tier])), tier,4,true));
        }

        // MANA_OUTPUT_HATCH_16A
        for (int i = 0; i < MANA_OUTPUT_HATCH_16A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_OUTPUT_HATCH_16A[i] = registerMetaTileEntity(1235 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_output_hatch_16a.%s", GTValues.VN[tier])), tier,16,true));
        }

        // MANA_OUTPUT_HATCH_64A
        for (int i = 0; i < MANA_OUTPUT_HATCH_64A.length; i++) {
            int tier = GTValues.LV + i;
            MANA_OUTPUT_HATCH_64A[i] = registerMetaTileEntity(1250 + i, new MetaTileEntityManaHatch(PollutionID(String.format("mana_output_hatch_64a.%s", GTValues.VN[tier])), tier,64,true));
        }

        // ESSENTIA_HATCH
        for (int i = 0; i < ESSENTIA_HATCH.length; i++) {
            int tier = GTValues.LV + i;
            ESSENTIA_HATCH[i] = registerMetaTileEntity(1265 + i,
                    new MetaTileEntityEssentiaHatch(
                            PollutionID("essentia_hatch." + GTValues.VN[tier].toLowerCase()), tier));
        }

        // MANA_POOL_INPUT_HATCH / MANA_POOL_OUTPUT_HATCH
        MetaTileEntityManaPoolHatch.PoolType[] poolTypes = MetaTileEntityManaPoolHatch.PoolType.values();
        for (int i = 0; i < poolTypes.length; i++) {
            MetaTileEntityManaPoolHatch.PoolType poolType = poolTypes[i];
            MANA_POOL_INPUT_HATCH[i] = registerMetaTileEntity(1300 + i, new MetaTileEntityManaPoolHatch(
                    PollutionID("mana_pool_input_hatch." + poolType.getName()), poolType, false));
        }
        for (int i = 0; i < poolTypes.length; i++) {
            MetaTileEntityManaPoolHatch.PoolType poolType = poolTypes[i];
            MANA_POOL_OUTPUT_HATCH[i] = registerMetaTileEntity(1315 + i, new MetaTileEntityManaPoolHatch(
                    PollutionID("mana_pool_output_hatch." + poolType.getName()), poolType, true));
        }

        // ADVANCED_MUFFLER_HATCH
        for (int i = 0; i < ADVANCED_MUFFLER_HATCH.length - 1; i++) {
            int tier = i + 1;
            String voltageName = GTValues.VN[tier].toLowerCase();
            ADVANCED_MUFFLER_HATCH[i] = registerMetaTileEntity(1350 + i,
                    new MetaTileEntityAdvancedMufflerHatch(gregtechId("advanced_muffler_hatch." + voltageName), tier));
        }

        // WIRELESS_MANA_INPUT_HATCH_1A
        for (int i = 0; i < WIRELESS_MANA_INPUT_HATCH_1A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_INPUT_HATCH_1A[i] = registerMetaTileEntity(1400 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_input_hatch_1a.%s", GTValues.VN[tier])), tier,1,false));
        }

        // WIRELESS_MANA_INPUT_HATCH_4A
        for (int i = 0; i < WIRELESS_MANA_INPUT_HATCH_4A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_INPUT_HATCH_4A[i] = registerMetaTileEntity(1415 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_input_hatch_4a.%s", GTValues.VN[tier])), tier,4,false));
        }

        // WIRELESS_MANA_INPUT_HATCH_16A
        for (int i = 0; i < WIRELESS_MANA_INPUT_HATCH_16A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_INPUT_HATCH_16A[i] = registerMetaTileEntity(1430 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_input_hatch_16a.%s", GTValues.VN[tier])), tier,16,false));
        }

        // WIRELESS_MANA_INPUT_HATCH_64A
        for (int i = 0; i < WIRELESS_MANA_INPUT_HATCH_64A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_INPUT_HATCH_64A[i] = registerMetaTileEntity(1445 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_input_hatch_64a.%s", GTValues.VN[tier])), tier,64,false));
        }

        // WIRELESS_MANA_OUTPUT_HATCH_1A
        for (int i = 0; i < WIRELESS_MANA_OUTPUT_HATCH_1A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_OUTPUT_HATCH_1A[i] = registerMetaTileEntity(1460 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_output_hatch_1a.%s", GTValues.VN[tier])), tier,1,true));
        }

        // WIRELESS_MANA_OUTPUT_HATCH_4A
        for (int i = 0; i < WIRELESS_MANA_OUTPUT_HATCH_4A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_OUTPUT_HATCH_4A[i] = registerMetaTileEntity(1475 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_output_hatch_4a.%s", GTValues.VN[tier])), tier,4,true));
        }

        // WIRELESS_MANA_OUTPUT_HATCH_16A
        for (int i = 0; i < WIRELESS_MANA_OUTPUT_HATCH_16A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_OUTPUT_HATCH_16A[i] = registerMetaTileEntity(1490 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_output_hatch_16a.%s", GTValues.VN[tier])), tier,16,true));
        }

        // WIRELESS_MANA_OUTPUT_HATCH_64A
        for (int i = 0; i < WIRELESS_MANA_OUTPUT_HATCH_64A.length; i++) {
            int tier = GTValues.LV + i;
            WIRELESS_MANA_OUTPUT_HATCH_64A[i] = registerMetaTileEntity(1505 + i, new MetaTileEntityWirelessManaHatch(PollutionID(String.format("wireless.mana_output_hatch_64a.%s", GTValues.VN[tier])), tier,64,true));
        }

        // WIRELESS_MANA_POOL_INPUT_HATCH / WIRELESS_MANA_POOL_OUTPUT_HATCH
        for (int i = 0; i < poolTypes.length; i++) {
            MetaTileEntityManaPoolHatch.PoolType poolType = poolTypes[i];
            WIRELESS_MANA_POOL_INPUT_HATCH[i] = registerMetaTileEntity(1520 + i, new MetaTileEntityWirelessManaPoolHatch(
                    PollutionID("wireless.mana_pool_input_hatch." + poolType.getName()), poolType, false));
        }
        for (int i = 0; i < poolTypes.length; i++) {
            MetaTileEntityManaPoolHatch.PoolType poolType = poolTypes[i];
            WIRELESS_MANA_POOL_OUTPUT_HATCH[i] = registerMetaTileEntity(1535 + i, new MetaTileEntityWirelessManaPoolHatch(
                    PollutionID("wireless.mana_pool_output_hatch." + poolType.getName()), poolType, true));
        }

        // FILTER_HATCH
        FILTER_HATCH = registerMetaTileEntity(1600,
                new MetaTileEntityFilterHatch(PollutionID("filter_hatch"), GTValues.EV));

        // TAROT_HATCH
        TAROT_HATCH = registerMetaTileEntity(1601,
                new MetaTileEntityTarotHatch(PollutionID("tarot_hatch"), GTValues.LV));

        // BLOOD_MAGIC_HATCH
        BLOOD_MAGIC_HATCH = registerMetaTileEntity(1602,
                new MetaTileEntityBloodMagicHatch(PollutionID("blood_magic_hatch"), GTValues.MV));

        // ASTRAL_LENS_HATCH
        ASTRAL_LENS_HATCH = registerMetaTileEntity(1603,
                new MetaTileEntityAstralLensHatch(PollutionID("astral_lens_hatch"), GTValues.MV));

        // ASTRAL_LENS_HATCH_ADVANCED
        ASTRAL_LENS_HATCH_ADVANCED = registerMetaTileEntity(1604,
                new MetaTileEntityAstralLensHatch(PollutionID("astral_lens_hatch_advanced"), GTValues.LuV));

        // ===== 存储 =====
        // QUANTUM_ASPECT_TANKS
        for (int i = 0; i < QUANTUM_ASPECT_TANKS.length; i++) {
            String name = GTValues.VN[GTValues.LV + i].toLowerCase();
            QUANTUM_ASPECT_TANKS[i] = registerMetaTileEntity(1700 + i, new MetaTileEntityQuantumAspectTank(
                    PollutionID("quantum_aspect_tank." + name), 1 + i, 10000 * (1 << i)));
        }

        // QUANTUM_MANA_TANKS
        for (int i = 0; i < QUANTUM_MANA_TANKS.length; i++) {
            String name = GTValues.VN[GTValues.LV + i].toLowerCase();
            QUANTUM_MANA_TANKS[i] = registerMetaTileEntity(1710 + i, new MetaTileEntityQuantumManaTank(
                    PollutionID("quantum_mana_tank." + name), 1 + i, 10000 * (1 << i)));
        }
    }
}
