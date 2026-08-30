package meowmel.pollution.dimension.biome;

import meowmel.pollution.dimension.worldgen.WorldEngineNoise;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 地下世界多群系提供器（对照 docs/underground-biome-driven.md §2.3 规格）：
 * - 一维噪声 → 区间 Profile：熔岩盆地 (-0.45,-0.15] / 蘑菇森林 (-0.15,0.30] /
 *   钟乳石石林 (0.30,0.55] / 水晶森林 (0.55,0.80] / 深窟基础兜底
 * - 独立河流噪声叠加：|riverNoise| < 0.06 时覆盖为地下暗河（跨群系的线状特征）
 */
public final class BiomeProviderUnderground extends BiomeProvider {

    private static final double PERSISTENCE = 1.2D;
    private static final int OCTAVES = 6;
    private static final double SCALE_X = 2000.0D;
    private static final double SCALE_Y = 0.4D;
    private static final WorldEngineNoise.NoiseProfile BIOME_NOISE =
            WorldEngineNoise.profile(PERSISTENCE, OCTAVES);

    /** 河流噪声参数（低频，周期约 700 格） */
    private static final double RIVER_SCALE = 666.0D;
    private static final double RIVER_THRESHOLD = 0.06D;

    private static final Profile[] PROFILES = {
            new Profile(UndergroundBiomes.LAVA_BASIN, -0.45D, -0.15D),
            new Profile(UndergroundBiomes.MYCELIUM_FOREST, -0.15D, 0.30D),
            new Profile(UndergroundBiomes.STALACTITE_PILLAR, 0.30D, 0.55D),
            new Profile(UndergroundBiomes.CRYSTAL_CAVERN, 0.55D, 0.80D),
    };

    private static final List<Biome> SPAWN_BIOMES = Collections.unmodifiableList(Arrays.asList(
            UndergroundBiomes.DEEP_CAVE
    ));

    private final long noiseSeed;
    private final long riverSeed;

    public BiomeProviderUnderground(long seed) {
        this.noiseSeed = (long) Math.pow((double) (seed * 84L), 6.0D);
        this.riverSeed = seed * 31L + 7L;
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return SPAWN_BIOMES;
    }

    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
        return fill(biomes, x, z, width, height);
    }

    @Override
    public Biome[] getBiomes(@Nullable Biome[] biomes, int x, int z, int width, int height) {
        return fill(biomes, x, z, width, height);
    }

    @Override
    public Biome[] getBiomes(@Nullable Biome[] biomes, int x, int z,
                             int width, int height, boolean cacheFlag) {
        return fill(biomes, x, z, width, height);
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
        for (int sampleX = x - radius; sampleX <= x + radius; sampleX += 4) {
            for (int sampleZ = z - radius; sampleZ <= z + radius; sampleZ += 4) {
                if (!allowed.contains(getBiomeAt(sampleX, sampleZ))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @Nullable
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
        BlockPos result = null;
        int matches = 0;
        for (int sampleX = x - range; sampleX <= x + range; sampleX += 4) {
            for (int sampleZ = z - range; sampleZ <= z + range; sampleZ += 4) {
                if (biomes.contains(getBiomeAt(sampleX, sampleZ))) {
                    if (result == null || random.nextInt(matches + 1) == 0) {
                        result = new BlockPos(sampleX, 0, sampleZ);
                    }
                    matches++;
                }
            }
        }
        return result;
    }

    private Biome[] fill(Biome[] result, int x, int z, int width, int height) {
        if (result == null || result.length < width * height) {
            result = new Biome[width * height];
        }
        for (int dz = 0; dz < height; dz++) {
            for (int dx = 0; dx < width; dx++) {
                result[dx + dz * width] = getBiomeAt(x + dx, z + dz);
            }
        }
        return result;
    }

    private Biome getBiomeAt(int x, int z) {
        // 河流噪声优先：河道覆盖任何群系
        double river = WorldEngineNoise.perlinNoise2D(riverSeed, x / RIVER_SCALE, z / RIVER_SCALE, BIOME_NOISE);
        if (Math.abs(river) < RIVER_THRESHOLD) {
            return UndergroundBiomes.UNDERGROUND_RIVER;
        }

        double mapValue = WorldEngineNoise.perlinNoise2D(
                noiseSeed, x / SCALE_X, z / SCALE_X, BIOME_NOISE) * SCALE_Y;

        Profile selected = null;
        for (Profile profile : PROFILES) {
            if (mapValue >= profile.min && mapValue <= profile.max
                    && (selected == null || profile.width() < selected.width())) {
                selected = profile;
            }
        }
        return selected == null ? UndergroundBiomes.DEEP_CAVE : selected.biome;
    }

    private static final class Profile {
        private final Biome biome;
        private final double min;
        private final double max;

        private Profile(Biome biome, double min, double max) {
            this.biome = biome;
            this.min = min;
            this.max = max;
        }

        private double width() {
            return max - min;
        }
    }
}
