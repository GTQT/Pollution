package meowmel.pollution.dimension.worldgen;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.worldgen.config.BedrockFluidDepositBuilder;
import gregtech.api.worldgen.config.OreDepositBuilder;
import gregtech.api.worldgen.config.WorldGenRegistry;
import gregtech.common.blocks.StoneVariantBlock;
import gregtech.common.items.OrbItems;
import meowmel.gtqtcore.api.unification.material.GTQTMaterials;
import meowmel.gtqtcore.common.blocks.GTQTMetaBlocks;
import meowmel.pollution.POConfig;
import meowmel.pollution.api.unification.PollutionMaterials;
import net.minecraft.block.state.IBlockState;
import vazkii.botania.common.block.ModBlocks;

/** Registers Pollution's ore veins via the GTCEu pure-code registration API. */
public final class PollutionOreVeins {

    private PollutionOreVeins() {}

    static int undergroundId = POConfig.WorldSettingSwitch.UndergroundDimensionID;
    static int alfheimId = POConfig.WorldSettingSwitch.AlfheimDimensionID;

    public static void registerOrbs() {
        // Orb → dimension mapping (JEI 星球图标反查)
        OrbItems.setDisplayItem(undergroundId, OrbItems.DISPLAY_UNDERGROUND);
        OrbItems.setDisplayItem(alfheimId, OrbItems.DISPLAY_ALFHEIM);

        // Dimension display names (JEI 矿脉/流体页维度名)
        WorldGenRegistry.INSTANCE.addNamedDimension(undergroundId, "Underground");
        WorldGenRegistry.INSTANCE.addNamedDimension(alfheimId, "Alfheim");
    }

    /**
     * Registers all Pollution ore veins. Call after GTCEu's preInit (MetaBlocks ready);
     * may run before or after GTCEu's own worldgen registry initialization, as
     * {@link WorldGenRegistry#addVeinDefinitions} appends without locking.
     */
    public static void registerVeins() {
        registerStoneSpheres(undergroundId,alfheimId);
        registerGTQTStoneSpheres( 0,undergroundId,alfheimId);

        // UndergroundWorlds ore veins
        registerVein("cryolite_vein", "pollution.veins.ore.cryolite",
                40, 0.50f, 80, 160, GTQTMaterials.Cryolite,
                GTQTMaterials.Cryolite, GTQTMaterials.Cryolite, GTQTMaterials.Cryolite, Materials.Bauxite,
                8, 20, undergroundId);
        registerVein("flame_coal_vein", "pollution.veins.ore.flame_coal",
                60, 0.60f, 60, 180, PollutionMaterials.FlameCoal,
                PollutionMaterials.FlameCoal, PollutionMaterials.FlameCoal, PollutionMaterials.FlameCoal,
                Materials.Diamond, 18, 32, undergroundId);
        registerVein("galena_vein", "pollution.veins.ore.galena",
                40, 0.25f, 60, 180, Materials.Galena,
                Materials.Galena, Materials.Galena, Materials.Silver, Materials.Lead, 14, 16, undergroundId);
        registerVein("nickel_vein", "pollution.veins.ore.nickel",
                40, 0.25f, 120, 240, Materials.Nickel,
                Materials.Garnierite, Materials.Nickel, Materials.Cobaltite, Materials.Pentlandite,
                14, 16, undergroundId);
        registerVein("octine_vein", "pollution.veins.ore.octine",
                60, 0.40f, 40, 120, PollutionMaterials.Octine,
                PollutionMaterials.Octine, PollutionMaterials.Octine, PollutionMaterials.Octine,
                PollutionMaterials.MeltGold, 12, 16, undergroundId);
        registerVein("pyrargyrite_vein", "pollution.veins.ore.pyrargyrite",
                20, 0.25f, 40, 120, PollutionMaterials.Pyrargyrite,
                PollutionMaterials.AuthorityLead, PollutionMaterials.AuthorityLead,
                PollutionMaterials.Pyrargyrite, PollutionMaterials.Pyrargyrite, 12, 16, undergroundId);
        registerVein("scabyst_vein", "pollution.veins.ore.scabyst",
                20, 0.25f, 40, 120, PollutionMaterials.Scabyst,
                PollutionMaterials.AuthorityLead, PollutionMaterials.AuthorityLead,
                PollutionMaterials.Scabyst, PollutionMaterials.Scabyst, 12, 16, undergroundId);
        registerVein("syrmorite_vein", "pollution.veins.ore.syrmorite",
                60, 0.40f, 40, 120, PollutionMaterials.Syrmorite,
                PollutionMaterials.Syrmorite, PollutionMaterials.Syrmorite, PollutionMaterials.Syrmorite,
                PollutionMaterials.MeltGold, 12, 16, undergroundId);
        registerVein("valonite_vein", "pollution.veins.ore.valonite",
                20, 0.25f, 40, 160, PollutionMaterials.Valonite,
                PollutionMaterials.DumbTin, PollutionMaterials.DumbTin,
                PollutionMaterials.Valonite, PollutionMaterials.Valonite, 14, 18, undergroundId);
        registerVein("zinc_vein", "pollution.veins.ore.pluto_zinc",
                20, 0.25f, 120, 240, PollutionMaterials.PlutoZinc,
                Materials.Sulfur, Materials.Sulfur,
                PollutionMaterials.PlutoZinc, PollutionMaterials.PlutoZinc, 12, 16, undergroundId);

        // Alfheim: Botania livingrock-hosted ore veins
        registerAlfheimVein("dragonstone_vein", "pollution.vein.dragonstone",
                12, 0.2f, 20, 70, PollutionMaterials.Dragonstone,
                PollutionMaterials.Dragonstone, PollutionMaterials.Dragonstone,
                PollutionMaterials.Dragonstone, PollutionMaterials.Dragonstone, 16, 24, alfheimId);
        registerAlfheimVein("elementium_vein", "pollution.vein.elementium",
                8, 0.15f, 8, 40, PollutionMaterials.ElvenElementium,
                PollutionMaterials.ElvenElementium, PollutionMaterials.ElvenElementium,
                PollutionMaterials.ElvenElementium, PollutionMaterials.ElvenElementium, 16, 24, alfheimId);
        registerAlfheimVein("pixie_quartz_vein", "pollution.vein.pixie_quartz",
                18, 0.25f, 30, 100, PollutionMaterials.ElvenQuartz,
                PollutionMaterials.PixieDust, PollutionMaterials.ElvenQuartz,
                PollutionMaterials.ElvenQuartz, PollutionMaterials.PixieDust, 18, 26, alfheimId);

        // 神秘矿脉
        registerVein("thaumastic_air_vein", "pollution.veins.ore.thaumastic.air",
                40, 0.50f, 20, 80, PollutionMaterials.InfusedAir,
                PollutionMaterials.InfusedAir, PollutionMaterials.InfusedAir,
                PollutionMaterials.Amber, Materials.Cinnabar, 12, 20, undergroundId,alfheimId);
        registerVein("thaumastic_earth_vein", "pollution.veins.ore.thaumastic.earth",
                40, 0.50f, 20, 80, PollutionMaterials.InfusedEarth,
                PollutionMaterials.InfusedEarth, PollutionMaterials.InfusedEarth,
                PollutionMaterials.Amber, Materials.Cinnabar, 12, 20, undergroundId,alfheimId);
        registerVein("thaumastic_entropy_vein", "pollution.veins.ore.thaumastic.entropy",
                40, 0.50f, 20, 80, PollutionMaterials.InfusedEntropy,
                PollutionMaterials.InfusedEntropy, PollutionMaterials.InfusedEntropy,
                PollutionMaterials.Amber, Materials.Cinnabar, 12, 20, undergroundId,alfheimId);
        registerVein("thaumastic_fire_vein", "pollution.veins.ore.thaumastic.fire",
                40, 0.50f, 20, 80, PollutionMaterials.InfusedFire,
                PollutionMaterials.InfusedFire, PollutionMaterials.InfusedFire,
                PollutionMaterials.Amber, Materials.Cinnabar, 12, 20, undergroundId,alfheimId);
        registerVein("thaumastic_order_vein", "pollution.veins.ore.thaumastic.order",
                40, 0.50f, 20, 80, PollutionMaterials.InfusedOrder,
                PollutionMaterials.InfusedOrder, PollutionMaterials.InfusedOrder,
                PollutionMaterials.Amber, Materials.Cinnabar, 12, 20, undergroundId,alfheimId);
        registerVein("thaumastic_water_vein", "pollution.veins.ore.thaumastic.water",
                40, 0.50f, 20, 80, PollutionMaterials.InfusedWater,
                PollutionMaterials.InfusedWater, PollutionMaterials.InfusedWater,
                PollutionMaterials.Amber, Materials.Cinnabar, 12, 20, undergroundId,alfheimId);

        // 流体矿脉
        registerFluidDeposit("lava_deposit", "pollution.veins.fluid.lava",
                65, 125, 250, 1, 100, 30, Materials.Lava.getFluid(), undergroundId);
        registerFluidDeposit("pure_tar_deposit", "pollution.veins.fluid.pure_tar",
                20, 100, 200, 1, 100, 20, PollutionMaterials.PureTar.getFluid(),undergroundId);
        registerFluidDeposit("mana_deposit", "pollution.veins.fluid.mana",
                10, 5, 25, 1, 100, 40, GTQTMaterials.Mana.getFluid(), alfheimId);
        registerFluidDeposit("water_deposit", "pollution.veins.fluid.water",
                20, 50, 100, 1, 100, 60, Materials.Water.getFluid(), undergroundId, alfheimId);
    }

    private static void registerStoneSpheres(int... dimensionId) {
        StoneVariantBlock.StoneType[] stoneTypes = {
                StoneVariantBlock.StoneType.BLACK_GRANITE,
                StoneVariantBlock.StoneType.RED_GRANITE,
                StoneVariantBlock.StoneType.MARBLE,
                StoneVariantBlock.StoneType.BASALT,
        };
        for (StoneVariantBlock.StoneType stoneType : stoneTypes) {
            OreDepositBuilder.definitionBuilder("pollution/" + stoneType.getName() + "_sphere")
                    .translationKey("gregtech.vein.sphere." + stoneType.getName())
                    .weight(120)
                    .priority(100)
                    .density(1.0f)
                    .minHeight(10)
                    .countAsVein(false)
                    .dimensionId(dimensionId)
                    .sphereGeneration(10, 20)
                    .stoneSmoothSphereFill(stoneType)
                    .buildAndRegister(WorldGenRegistry.INSTANCE);
        }
    }

    private static void registerGTQTStoneSpheres(int... dimensionId) {
        meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType[] stoneTypes = {
                meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType.LIMESTONE,
                meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType.KOMATIITE,
                meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType.GREEN_SCHIST,
                meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType.BLUE_SCHIST,
                meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType.QUARTZITE,
                meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType.SLATE,
                meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType.SHALE,
        };
        for (meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneType stoneType : stoneTypes) {
            IBlockState state = GTQTMetaBlocks.STONE_BLOCKS
                    .get(meowmel.gtqtcore.common.blocks.StoneVariantBlock.StoneVariant.SMOOTH)
                    .getState(stoneType);
            OreDepositBuilder.definitionBuilder("pollution/" + stoneType.getName() + "_sphere")
                    .translationKey("gregtech.vein.sphere." + stoneType.getName())
                    .weight(120)
                    .priority(100)
                    .density(1.0f)
                    .minHeight(10)
                    .countAsVein(false)
                    .dimensionId(dimensionId)
                    .sphereGeneration(10, 20)
                    .ignoreBedrockFill(gregtech.api.worldgen.filler.FillerEntry.createSimpleFiller(state))
                    .buildAndRegister(WorldGenRegistry.INSTANCE);
        }
    }

    /** 基岩流体矿脉 */
    private static void registerFluidDeposit(String name, String translationKey, int weight,
                                             int minYield, int maxYield, int depletionAmount, int depletionChance,
                                             int depletedYield, net.minecraftforge.fluids.Fluid fluid,
                                             int... dimensionIds) {
        BedrockFluidDepositBuilder.definitionBuilder("pollution/" + name)
                .translationKey(translationKey)
                .weight(weight)
                .yields(minYield, maxYield)
                .depletion(depletionAmount, depletionChance, depletedYield)
                .dimensionId(dimensionIds)
                .fluid(fluid)
                .buildAndRegister(WorldGenRegistry.INSTANCE);
    }

    /** 矿脉 */
    private static void registerVein(String name, String translationKey, int weight, float density,
                                     int minHeight, int maxHeight, Material surfaceRock,
                                     Material primary, Material secondary, Material between,
                                     Material sporadic, int radiusMin, int radiusMax, int... dimensionId) {
        OreDepositBuilder.definitionBuilder("pollution/" + name)
                .translationKey(translationKey)
                .weight(weight)
                .density(density)
                .minHeight(minHeight)
                .maxHeight(maxHeight)
                .dimensionId(dimensionId)
                .surfaceRock(surfaceRock)
                .layeredGeneration(radiusMin, radiusMax)
                .layeredFill(primary, secondary, between, sporadic)
                .buildAndRegister(WorldGenRegistry.INSTANCE);
    }

    /** Alfheim 矿脉 */
    private static void registerAlfheimVein(String name, String translationKey, int weight, float density,
                                            int minHeight, int maxHeight, Material surfaceRock,
                                            Material primary, Material secondary, Material between,
                                            Material sporadic, int radiusMin, int radiusMax, int dimensionId) {
        OreDepositBuilder.definitionBuilder("pollution/" + name)
                .translationKey(translationKey)
                .weight(weight)
                .density(density)
                .minHeight(minHeight)
                .maxHeight(maxHeight)
                .dimensionId(dimensionId)
                .generationPredicate((state, world, pos) -> state.getBlock() == ModBlocks.livingrock)
                .surfaceRock(surfaceRock)
                .layeredGeneration(radiusMin, radiusMax)
                .layeredFill(primary, secondary, between, sporadic)
                .buildAndRegister(WorldGenRegistry.INSTANCE);
    }
}
