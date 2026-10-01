package meowmel.pollution.dimension.biome.gen;

import meowmel.pollution.dimension.biome.UndergroundBiomes;
import meowmel.pollution.dimension.worldgen.UndergroundBiomeLayout;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

/**
 * 地下世界群系分布层（{@link GenLayer} 标准机制）。
 *
 * <p>本类现在是**薄适配器**：只做「槽位 → {@link Biome}」的翻译，
 * 分布本身（抖动网格 / Voronoi 胞）在 {@link UndergroundBiomeLayout}，那里与 Minecraft 无关，
 * 可被 {@code tools/biome-probe} 离线复现与标定。
 *
 * <p>🔴 <b>旧实现为什么是一片片长条</b>（已修，详见 {@link UndergroundBiomeLayout} 的类注释）：
 * 它按**单个二维噪声的数值**是否落在某个窄区间来选群系，而"数值落在窄区间"解出来的是该场的
 * **等值线** —— 沿等高线蜿蜒的细长条带（实测平均宽 9–12 格、单斑块形状因子 71–155）。
 * 团块必须用**二维**选择，现在是抖动网格胞（实测平均宽 40–42 格、形状因子 6.0–8.6）。
 *
 * <p>⚠ 另外记一笔：改成团块后一度出现"整个世界只有一个群系"，那不是本类的问题，
 * 而是胞太大（256 格）+ 兜底群系渗流（占比 &gt; 0.593）导致玩家活动范围常常整个落在同一个胞里。
 * 现在 {@link UndergroundBiomeLayout} 用 CELL=128、兜底 55–61%（低于渗流阈值）。
 */
public class GenLayerUndergroundBiomes extends GenLayer {

    private final long seed;

    public GenLayerUndergroundBiomes(long seed) {
        super(0);
        this.seed = seed;
    }

    @Override
    public int[] getInts(int areaX, int areaZ, int areaWidth, int areaHeight) {
        int[] result = IntCache.getIntCache(areaWidth * areaHeight);
        for (int z = 0; z < areaHeight; ++z) {
            for (int x = 0; x < areaWidth; ++x) {
                int slot = UndergroundBiomeLayout.slotAt(seed, areaX + x, areaZ + z);
                result[x + z * areaWidth] = Biome.getIdForBiome(UndergroundBiomes.ALL[slot]);
            }
        }
        return result;
    }
}
