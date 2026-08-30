package meowmel.pollution.dimension.biome;

import meowmel.pollution.common.entity.moster.EntitySlimeAer;
import meowmel.pollution.common.entity.moster.EntitySlimeAqua;
import meowmel.pollution.common.entity.moster.EntitySlimeignis;
import meowmel.pollution.common.entity.moster.EntitySlimeOrdo;
import meowmel.pollution.common.entity.moster.EntitySlimePerditio;
import meowmel.pollution.common.entity.moster.EntitySlimeTerra;
import meowmel.pollution.dimension.biome.biomes.POBiomeUndergroundStyle;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

/**
 * 地下世界 6 群系集合（对照 docs/underground-biome-driven.md §2.1.x 规格）。
 * 深窟基础为兜底群系；熔岩/蘑菇/石林/水晶由一维噪声区间分区，暗河为河流噪声叠加。
 */
public final class UndergroundBiomes {

    private UndergroundBiomes() {
    }

    // ===== 元素史莱姆生成条目（权重/数量按群系差异化） =====

    private static final Biome.SpawnListEntry[] ALL_SLIMES = {
            new Biome.SpawnListEntry(EntitySlimeAer.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeignis.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeAqua.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeOrdo.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimePerditio.class, 2, 1, 2),
    };

    // ===== 6 群系 =====

    /** ① 深窟基础（兜底）：完全保留现状洞窟形态 */
    public static final Biome DEEP_CAVE = new POBiomeUndergroundStyle(
            "Deep Cave Basic", 0xADD8E6, 0.5F, 0.5F,
            Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState(),
            UndergroundTerrainMode.STANDARD, ALL_SLIMES);

    /** ② 钟乳石石林：石灰岩填充，钟乳石从洞顶悬挂 */
    public static final Biome STALACTITE_PILLAR = new POBiomeUndergroundStyle(
            "Stalactite Pillar", 0x3A6EA5, 0.2F, 0.4F,
            Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState(),
            UndergroundTerrainMode.PILLAR,
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 3, 1, 3),
            new Biome.SpawnListEntry(EntitySlimeignis.class, 3, 1, 3),
            new Biome.SpawnListEntry(EntityBat.class, 10, 2, 4));

    /** ③ 蘑菇森林：菌丝地表 + 泥土填充，微丘地形 */
    public static final Biome MYCELIUM_FOREST = new POBiomeUndergroundStyle(
            "Mycelium Forest", 0x6A5ACD, 0.6F, 0.9F,
            Blocks.MYCELIUM.getDefaultState(), Blocks.DIRT.getDefaultState(),
            UndergroundTerrainMode.HILLY,
            new Biome.SpawnListEntry(EntitySlimeTerra.class, 4, 1, 3),
            new Biome.SpawnListEntry(EntitySlimeAqua.class, 4, 1, 3),
            new Biome.SpawnListEntry(EntityBat.class, 8, 2, 4));

    /** ④ 水晶森林：石英岩填充，晶簇点缀 */
    public static final Biome CRYSTAL_CAVERN = new POBiomeUndergroundStyle(
            "Crystal Cavern", 0x00FFCC, 0.3F, 0.2F,
            Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState(),
            UndergroundTerrainMode.STANDARD,
            new Biome.SpawnListEntry(EntitySlimeAer.class, 2, 1, 2),
            new Biome.SpawnListEntry(EntitySlimeignis.class, 2, 1, 2));

    /** ⑤ 熔岩盆地：洞窟底部岩浆填充 */
    public static final Biome LAVA_BASIN = new POBiomeUndergroundStyle(
            "Lava Basin", 0xFF4500, 2.0F, 0.0F,
            Blocks.OBSIDIAN.getDefaultState(), Blocks.STONE.getDefaultState(),
            UndergroundTerrainMode.LOWLAND,
            new Biome.SpawnListEntry(EntitySlimeignis.class, 5, 2, 4));

    /** ⑥ 地下暗河：河流噪声叠加，河谷挖槽填水 */
    public static final Biome UNDERGROUND_RIVER = new POBiomeUndergroundStyle(
            "Underground River", 0x0033FF, 0.5F, 0.9F,
            Blocks.GRAVEL.getDefaultState(), Blocks.CLAY.getDefaultState(),
            UndergroundTerrainMode.RIVER,
            new Biome.SpawnListEntry(EntitySlimeAqua.class, 5, 2, 4));

    /** 全部地下世界群系（用于 biome 判定循环） */
    public static final Biome[] ALL = {
            DEEP_CAVE, STALACTITE_PILLAR, MYCELIUM_FOREST, CRYSTAL_CAVERN, LAVA_BASIN, UNDERGROUND_RIVER
    };
}
