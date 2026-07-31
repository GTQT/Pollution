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

/** Exact Java port of WorldEngine's Alfheim biome-map selection. */
public final class BiomeProviderAlfheim extends BiomeProvider {

    private static final double PERSISTENCE = 1.2D;
    private static final int OCTAVES = 6;
    private static final double SCALE_X = 8000.0D;
    private static final double SCALE_Y = 0.4D;
    private static final WorldEngineNoise.NoiseProfile BIOME_NOISE =
            WorldEngineNoise.profile(PERSISTENCE, OCTAVES);

    /** Registration order from WorldProviderAlfheim.genSettings. */
    private static final Profile[] PROFILES = {
            new Profile(AlfheimBiomes.FIELD, -0.55D, 0.82D),
            new Profile(AlfheimBiomes.GIANT_FLOWER_FIELD, 1.0D, 10.0D),
            new Profile(AlfheimBiomes.BEACH, -0.5D, -0.35D),
            new Profile(AlfheimBiomes.SANDBANK, -0.41D, -0.38D),
            new Profile(AlfheimBiomes.RIVER, -0.48D, -0.38D),
            new Profile(AlfheimBiomes.LOW_PLATEAU, 0.2D, 0.78D),
            new Profile(AlfheimBiomes.MID_PLATEAU, 0.3D, 0.75D),
            new Profile(AlfheimBiomes.HIGH_PLATEAU, 0.4D, 0.7D),
            new Profile(AlfheimBiomes.HIGH_PLATEAU_FOREST, 0.49D, 0.58D),
            new Profile(AlfheimBiomes.HIGH_PLATEAU_FIELD, 0.43D, 0.65D),
            new Profile(AlfheimBiomes.ISLAND_FOREST, -10.0D, 0.82D),
            new Profile(AlfheimBiomes.PIT_FOREST, 0.82D, 1.0D)
    };

    private static final List<Biome> SPAWN_BIOMES = Collections.unmodifiableList(Arrays.asList(
            AlfheimBiomes.FIELD,
            AlfheimBiomes.ISLAND_FOREST
    ));

    private final long noiseSeed;

    public BiomeProviderAlfheim(long seed) {
        // WE_Biome.getBiomeAt: (long) Math.pow(worldSeed * 84, 6)
        this.noiseSeed = (long) Math.pow((double) (seed * 84L), 6.0D);
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

    /**
     * BiomeCache calls this overload directly. The base implementation cannot
     * be used because this provider deliberately has no GenLayer instances.
     */
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
        double mapValue = WorldEngineNoise.perlinNoise2D(
                noiseSeed, x / SCALE_X, z / SCALE_X, BIOME_NOISE) * SCALE_Y;

        Profile selected = null;
        for (Profile profile : PROFILES) {
            if (mapValue >= profile.min && mapValue <= profile.max
                    && (selected == null || profile.width() < selected.width())) {
                selected = profile;
            }
        }
        // WorldEngine uses the first registered biome as its fallback.
        return selected == null ? AlfheimBiomes.FIELD : selected.biome;
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
