package meowmel.pollution.dimension.biome.gen;

import meowmel.pollution.dimension.biome.UndergroundBiomes;
import meowmel.pollution.dimension.worldgen.WorldEngineNoise;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

/**
 * 地下世界群系分布层（GenLayer 标准机制）。
 *
 * 分布结构：7 个特殊群系是噪声值域上的"窗口岛屿"（每个窗口宽 WINDOW），
 * 窗口之间全部是深窟基础群系——特殊群系彼此远离，中间用基础洞穴过渡。
 *
 * 控制参数：
 * - SCALE：噪声尺度，越大群系越大（世界距离 = 噪声窗口 × SCALE 比例）
 * - WINDOW：特殊群系窗口宽度（噪声值域单位），决定群系直径
 * - SLOT_SPACING：7 个槽位均分 [-AMPLITUDE, +AMPLITUDE]，决定群系间距
 */
public class GenLayerUndergroundBiomes extends GenLayer {

    private static final WorldEngineNoise.NoiseProfile BIOME_NOISE = WorldEngineNoise.profile(1.2D, 6);
    private static final double SCALE = 4000.0D;
    private static final double NOISE_AMPLITUDE = 0.4D;
    /** 特殊群系窗口宽度（全宽）——约 240~300 格直径 */
    private static final double WINDOW = 0.06D;
    /** 7 个特殊群系槽位均分噪声值域 */
    private static final double SLOT_SPACING = NOISE_AMPLITUDE * 2.0D / 7.0D;

    private final long noiseSeed;

    public GenLayerUndergroundBiomes(long seed) {
        super(0);
        this.noiseSeed = (long) Math.pow((double) (seed * 84L), 6.0D);
    }

    @Override
    public int[] getInts(int areaX, int areaZ, int areaWidth, int areaHeight) {
        int[] result = IntCache.getIntCache(areaWidth * areaHeight);
        for (int z = 0; z < areaHeight; ++z) {
            for (int x = 0; x < areaWidth; ++x) {
                double value = WorldEngineNoise.perlinNoise2D(
                        noiseSeed, (areaX + x) / SCALE, (areaZ + z) / SCALE, BIOME_NOISE) * NOISE_AMPLITUDE;

                int biomeId;
                int slot = (int) Math.floor((value + NOISE_AMPLITUDE) / SLOT_SPACING);
                if (slot < 0 || slot >= 7) {
                    biomeId = Biome.getIdForBiome(UndergroundBiomes.DEEP_CAVE);
                } else {
                    // 槽位中心（窗口中心）
                    double center = -NOISE_AMPLITUDE + SLOT_SPACING * (slot + 0.5);
                    if (Math.abs(value - center) <= WINDOW / 2.0D) {
                        biomeId = Biome.getIdForBiome(UndergroundBiomes.ALL[slot]);
                    } else {
                        biomeId = Biome.getIdForBiome(UndergroundBiomes.DEEP_CAVE);
                    }
                }
                result[x + z * areaWidth] = biomeId;
            }
        }
        return result;
    }
}
