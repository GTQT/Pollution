package meowmel.pollution.dimension.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/** Exact Java port of WorldEngine's Alfheim biome-map selection. */
public final class BiomeProviderAlfheim extends BiomeProvider {

    private static final double PERSISTENCE = 1.2D;
    private static final int OCTAVES = 6;
    private static final double SCALE_X = 8000.0D;
    private static final double SCALE_Y = 0.4D;

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

    private final long seed;

    public BiomeProviderAlfheim(long seed) {
        this.seed = seed;
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return getBiomeAt(pos.getX(), pos.getZ());
    }

    @Override
    public Biome getBiome(BlockPos pos, Biome defaultBiome) {
        return getBiomeAt(pos.getX(), pos.getZ());
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
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
        for (int sampleX = x - radius; sampleX <= x + radius; sampleX += 16) {
            for (int sampleZ = z - radius; sampleZ <= z + radius; sampleZ += 16) {
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
                if (biomes.contains(getBiomeAt(sampleX, sampleZ)) && (result == null || random.nextInt(++matches) == 0)) {
                    result = new BlockPos(sampleX, 0, sampleZ);
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
        long noiseSeed = (long) Math.pow((double) (seed * 84L), 6.0D);
        double mapValue = perlinNoise2D(noiseSeed, x / SCALE_X, z / SCALE_X, PERSISTENCE, OCTAVES) * SCALE_Y;

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

    private static double perlinNoise2D(long seed, double x, double z, double persistence, int octaves) {
        double total = 0.0D;
        for (int octave = 1; octave <= octaves; octave++) {
            double frequency = Math.pow(2.0D, octave);
            double amplitude = Math.pow(persistence, octave);
            total += cosineInterpolatedNoise2D(seed, x * frequency, z * frequency) * amplitude;
        }
        return total;
    }

    private static double cosineInterpolatedNoise2D(long seed, double x, double z) {
        long integerX = (long) x;
        long integerZ = (long) z;
        double fractionalX = Math.abs(x) - Math.abs(integerX);
        double fractionalZ = Math.abs(z) - Math.abs(integerZ);
        long neighborX = Math.abs(x) == x ? integerX + 1L : integerX - 1L;
        long neighborZ = Math.abs(z) == z ? integerZ + 1L : integerZ - 1L;

        double v1 = smoothNoise2D(seed, integerX, integerZ);
        double v2 = smoothNoise2D(seed, neighborX, integerZ);
        double v3 = smoothNoise2D(seed, integerX, neighborZ);
        double v4 = smoothNoise2D(seed, neighborX, neighborZ);
        return cosineInterpolate(cosineInterpolate(v1, v2, fractionalX),
                cosineInterpolate(v3, v4, fractionalX), fractionalZ);
    }

    private static double smoothNoise2D(long seed, long x, long z) {
        double corners = (numberNoise2D(seed, x - 1L, z - 1L)
                + numberNoise2D(seed, x + 1L, z - 1L)
                + numberNoise2D(seed, x - 1L, z + 1L)
                + numberNoise2D(seed, x + 1L, z + 1L)) / 16.0D;
        double sides = (numberNoise2D(seed, x - 1L, z)
                + numberNoise2D(seed, x + 1L, z)
                + numberNoise2D(seed, x, z - 1L)
                + numberNoise2D(seed, x, z + 1L)) / 8.0D;
        return corners + sides + numberNoise2D(seed, x, z) / 4.0D;
    }

    private static double numberNoise2D(long seed, long x, long z) {
        long n = x + z * 31L + seed * 11L;
        n = (n << 13) ^ n;
        long value = ((n * n * 15731L + 789221L) * n + 1376312589L) & 2147483647L;
        return 1.0D - value / 1073741824.0D;
    }

    private static double cosineInterpolate(double a, double b, double amount) {
        double factor = (1.0D - Math.cos(amount * Math.PI)) * 0.5D;
        return a * (1.0D - factor) + b * factor;
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
