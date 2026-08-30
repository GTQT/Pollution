package meowmel.pollution.dimension.biome;

import gregtech.api.fluids.store.FluidStorageKeys;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.common.entity.moster.EntitySlimeAer;
import meowmel.pollution.common.entity.moster.EntitySlimeAqua;
import meowmel.pollution.common.entity.moster.EntitySlimeignis;
import meowmel.pollution.common.entity.moster.EntitySlimeTerra;
import meowmel.pollution.dimension.biome.biomes.POBiomeUnderground;
import meowmel.pollution.dimension.biome.biomes.POBiomeUndergroundStyle;
import meowmel.pollution.dimension.worldgen.feature.WorldGenBigVines;
import meowmel.pollution.dimension.worldgen.feature.WorldGenFluidPool;
import meowmel.pollution.dimension.worldgen.feature.WorldGenGlowstoneCeiling;
import meowmel.pollution.dimension.worldgen.feature.WorldGenMushroomBlockCluster;
import meowmel.pollution.dimension.worldgen.feature.WorldGenOnCaveFloor;
import meowmel.pollution.dimension.worldgen.feature.WorldGenScatteredBlock;
import meowmel.pollution.dimension.worldgen.feature.WorldGenSlantedPillar;
import meowmel.pollution.dimension.worldgen.feature.WorldGenStalactite;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Arrays;

/**
 * 地下世界群系集合（对照 docs/underground-biome-driven.md §4 设计）：
 * 深窟基础（兜底，现状）+ 7 个风格群系，全部 = 表面方块 + 装饰器组，
 * 地形骨架维度级统一（不破坏景观）。
 */
public final class UndergroundBiomes {

    private UndergroundBiomes() {
    }

    // ===== 共享装饰器（无状态生成器，可安全复用） =====

    private static final WorldGenerator STALACTITE = new WorldGenStalactite();
    private static final WorldGenerator SLANTED_PILLAR = new WorldGenSlantedPillar();
    private static final WorldGenerator GLOWSTONE_CEILING = new WorldGenGlowstoneCeiling();
    private static final WorldGenerator BIG_RED_MUSHROOM = new WorldGenOnCaveFloor(new WorldGenBigMushroom(Blocks.RED_MUSHROOM_BLOCK));
    private static final WorldGenerator BIG_BROWN_MUSHROOM = new WorldGenOnCaveFloor(new WorldGenBigMushroom(Blocks.BROWN_MUSHROOM_BLOCK));
    private static final WorldGenerator RED_MUSHROOM = new WorldGenScatteredBlock(Blocks.RED_MUSHROOM, 64);
    private static final WorldGenerator BROWN_MUSHROOM = new WorldGenScatteredBlock(Blocks.BROWN_MUSHROOM, 64);
    private static final WorldGenerator BROWN_CLUSTER = new WorldGenMushroomBlockCluster(Blocks.BROWN_MUSHROOM_BLOCK);
    private static final WorldGenerator RED_CLUSTER = new WorldGenMushroomBlockCluster(Blocks.RED_MUSHROOM_BLOCK);
    private static final WorldGenerator VINES = new WorldGenVines();
    private static final WorldGenerator BIG_VINES = new WorldGenBigVines();
    private static final WorldGenerator CACTUS = new WorldGenCactus();
    private static final WorldGenerator JUNGLE_TREE = new WorldGenOnCaveFloor(new WorldGenTrees(false));
    private static final WorldGenerator LEAVES_DENSE = new WorldGenScatteredBlock(Blocks.LEAVES, 128);
    private static final WorldGenerator WATER_POOL = new WorldGenFluidPool(Blocks.WATER);
    private static final WorldGenerator LAVA_POOL = new WorldGenFluidPool(Blocks.LAVA);
    private static final WorldGenerator TAR_POOL = new WorldGenFluidPool(
            PollutionMaterials.PureTar.getFluid(FluidStorageKeys.LIQUID).getBlock());
    private static final WorldGenerator GRAVEL = new WorldGenMinable(Blocks.GRAVEL.getDefaultState(), 33, BlockMatcher.forBlock(Blocks.STONE));
    private static final WorldGenerator SOUL_SAND = new WorldGenMinable(Blocks.SOUL_SAND.getDefaultState(), 24, BlockMatcher.forBlock(Blocks.STONE));
    private static final WorldGenerator QUARTZ_ORE = new WorldGenMinable(Blocks.QUARTZ_ORE.getDefaultState(), 8, BlockMatcher.forBlock(Blocks.STONE));

    // ===== 各群系装饰器组（数组元素 = 一次生成调用） =====

    /** 钟乳石群：石头柱子密集 + 沙砾 + 水池 */
    private static final WorldGenerator[] STALACTITE_GROUP = concat(
            repeat(STALACTITE, 24), repeat(GRAVEL, 4), repeat(WATER_POOL, 2));

    /** 水晶簇落：倾斜粗石英柱 + 石英矿 + 水池 */
    private static final WorldGenerator[] CRYSTAL_GROUP = concat(
            repeat(SLANTED_PILLAR, 8), repeat(QUARTZ_ORE, 12), repeat(WATER_POOL, 2));

    /** 蘑菇林：原版大蘑菇 + 单蘑菇 + 蘑菇灯 */
    private static final WorldGenerator[] MUSHROOM_GROUP = concat(
            repeat(BIG_RED_MUSHROOM, 8), repeat(BIG_BROWN_MUSHROOM, 8),
            repeat(RED_MUSHROOM, 8), repeat(BROWN_MUSHROOM, 8), repeat(BROWN_CLUSTER, 6), repeat(RED_CLUSTER, 6));

    /** 繁茂洞穴：超级大藤蔓 + 原版藤蔓 + 高密度树叶 + 水池 */
    private static final WorldGenerator[] LUSH_GROUP = concat(
            repeat(BIG_VINES, 8), repeat(VINES, 8), repeat(LEAVES_DENSE, 32), repeat(WATER_POOL, 4));

    /** 原始洞穴：丛林树 + 藤蔓 + 洞顶萤石 */
    private static final WorldGenerator[] PRIMORDIAL_GROUP = concat(
            repeat(JUNGLE_TREE, 8), repeat(VINES, 8), repeat(GLOWSTONE_CEILING, 12));

    /** 沙漠洞穴：仙人掌 + 沙砾 + 水池 */
    private static final WorldGenerator[] DESERT_GROUP = concat(
            repeat(CACTUS, 12), repeat(GRAVEL, 6), repeat(WATER_POOL, 2));

    /** 岩浆洞穴：岩浆池密集 + 灵魂沙矿脉 + 焦油池 */
    private static final WorldGenerator[] MAGMA_GROUP = concat(
            repeat(LAVA_POOL, 12), repeat(SOUL_SAND, 8), repeat(TAR_POOL, 4));

    // ===== 元素史莱姆生成条目 =====

    private static final Biome.SpawnListEntry[] ALL_SLIMES = {
            new Biome.SpawnListEntry(EntitySlimeAer.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeignis.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeAqua.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 2, 1, 2),
    };

    // ===== 8 群系 =====

    /** ① 深窟基础（兜底）：现状群系，不实现 IUndergroundBiome → generator 走原版逻辑，景观零变化 */
    public static final Biome DEEP_CAVE = new POBiomeUnderground();

    /** ② 钟乳石群：石头表面，石柱群密集 */
    public static final Biome STALACTITE_CLUSTER = new POBiomeUndergroundStyle(
            "Stalactite Cluster", 0x3A6EA5, 0.2F, 0.4F,
            Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState(), STALACTITE_GROUP,
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 3, 1, 3),
            new Biome.SpawnListEntry(EntitySlimeignis.class, 3, 1, 3),
            new Biome.SpawnListEntry(EntityBat.class, 10, 2, 4));

    /** ③ 水晶簇落：石英岩表面，倾斜粗石英柱 */
    public static final Biome CRYSTAL_CLUSTER = new POBiomeUndergroundStyle(
            "Crystal Cluster", 0x00FFCC, 0.3F, 0.2F,
            Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState(), CRYSTAL_GROUP,
            new Biome.SpawnListEntry(EntitySlimeAer.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeignis.class, 2, 1, 2));

    /** ④ 蘑菇林：菌丝表面 + 泥土填充，原版大蘑菇 */
    public static final Biome MUSHROOM_FOREST = new POBiomeUndergroundStyle(
            "Mushroom Forest", 0x6A5ACD, 0.6F, 0.9F,
            Blocks.MYCELIUM.getDefaultState(), Blocks.DIRT.getDefaultState(), MUSHROOM_GROUP,
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 4, 1, 3),
            new Biome.SpawnListEntry(EntitySlimeAqua.class, 4, 1, 3),
            new Biome.SpawnListEntry(EntityBat.class, 8, 2, 4));

    /** ⑤ 繁茂洞穴：草方块表面 + 泥土填充，藤蔓 + 高密度树叶 */
    public static final Biome LUSH_CAVE = new POBiomeUndergroundStyle(
            "Lush Cave", 0x33CC33, 0.6F, 0.8F,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), LUSH_GROUP,
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 4, 1, 3),
            new Biome.SpawnListEntry(EntitySlimeAqua.class, 3, 1, 3),
            new Biome.SpawnListEntry(EntityBat.class, 10, 2, 4));

    /** ⑥ 原始洞穴：草方块表面 + 泥土填充，丛林树 + 藤蔓 + 洞顶萤石 */
    public static final Biome PRIMORDIAL_CAVE = new POBiomeUndergroundStyle(
            "Primordial Cave", 0x66CC00, 0.7F, 0.9F,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), PRIMORDIAL_GROUP,
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 4, 1, 3),
            new Biome.SpawnListEntry(EntitySlimeAqua.class, 3, 1, 3));

    /** ⑦ 沙漠洞穴：沙子表面 + 砂岩填充，仙人掌 */
    public static final Biome DESERT_CAVE = new POBiomeUndergroundStyle(
            "Desert Cave", 0xE0C060, 0.9F, 0.0F,
            Blocks.SAND.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), DESERT_GROUP,
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 3, 1, 3),
            new Biome.SpawnListEntry(EntitySlimeAer.class, 3, 1, 3));

    /** ⑧ 岩浆洞穴：地狱岩表面 + 灵魂沙，岩浆池密集 */
    public static final Biome MAGMA_CAVE = new POBiomeUndergroundStyle(
            "Magma Cave", 0xFF4500, 2.0F, 0.0F,
            Blocks.NETHERRACK.getDefaultState(), Blocks.NETHERRACK.getDefaultState(), MAGMA_GROUP,
            new Biome.SpawnListEntry(EntitySlimeignis.class, 5, 2, 4));

    /**
     * 全部地下世界群系（GenLayer 区间分布用，顺序即分布索引）。
     * 排列原则：相邻区间避免"装饰组与表面方块群系错配"——蘑菇林(菌丝)夹在水晶(石英岩)
     * 和钟乳石(石头)之间，不与草方块群系（繁茂/原始）相邻；
     * 草方块群系（繁茂/原始）也互相隔开。
     */
    public static final Biome[] ALL = {
            MAGMA_CAVE, DESERT_CAVE, PRIMORDIAL_CAVE, CRYSTAL_CLUSTER,
            MUSHROOM_FOREST, LUSH_CAVE, STALACTITE_CLUSTER, DEEP_CAVE
    };

    // ===== 工具 =====
    private static WorldGenerator[] repeat(WorldGenerator generator, int count) {
        WorldGenerator[] result = new WorldGenerator[count];
        Arrays.fill(result, generator);
        return result;
    }

    private static WorldGenerator[] concat(WorldGenerator[]... arrays) {
        int total = 0;
        for (WorldGenerator[] array : arrays) {
            total += array.length;
        }
        WorldGenerator[] result = new WorldGenerator[total];
        int index = 0;
        for (WorldGenerator[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }
        return result;
    }
}
