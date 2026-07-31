package meowmel.pollution.dimension.worldgen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java 1.12 port of ASJCore's {@code WE_PerlinNoise}.
 *
 * <p>The arithmetic and octave numbering intentionally match the original
 * implementation because both Alfheim's biome map and terrain height depend
 * on its exact output.</p>
 */
public final class WorldEngineNoise {

    private static final Map<ProfileKey, NoiseProfile> PROFILES = new ConcurrentHashMap<>();

    private WorldEngineNoise() {
    }

    public static double perlinNoise2D(long seed, double x, double z,
                                       double persistence, int octaves) {
        return perlinNoise2D(seed, x, z, profile(persistence, octaves));
    }

    public static double perlinNoise2D(long seed, double x, double z,
                                       NoiseProfile profile) {
        double total = 0.0D;
        for (int octave = 0; octave < profile.frequencies.length; octave++) {
            total += cosineInterpolatedNoise2D(
                    seed,
                    x * profile.frequencies[octave],
                    z * profile.frequencies[octave]) * profile.amplitudes[octave];
        }
        return total;
    }

    /**
     * Builds each distinct WorldEngine noise profile once. Values are still
     * calculated with the source's Math.pow calls, then reused by every column.
     */
    public static NoiseProfile profile(double persistence, int octaves) {
        ProfileKey key = new ProfileKey(persistence, octaves);
        NoiseProfile existing = PROFILES.get(key);
        if (existing != null) {
            return existing;
        }
        NoiseProfile created = new NoiseProfile(persistence, octaves);
        NoiseProfile raced = PROFILES.putIfAbsent(key, created);
        return raced == null ? created : raced;
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
        return 1.0D - ((n * (n * n * 15731L + 789221L) + 1376312589L)
                & 0x7FFFFFFFL) / 1073741824.0D;
    }

    private static double cosineInterpolate(double a, double b, double amount) {
        double factor = (1.0D - Math.cos(amount * Math.PI)) * 0.5D;
        return a * (1.0D - factor) + b * factor;
    }

    public static final class NoiseProfile {
        private final double[] frequencies;
        private final double[] amplitudes;

        private NoiseProfile(double persistence, int octaves) {
            this.frequencies = new double[octaves];
            this.amplitudes = new double[octaves];
            for (int octave = 1; octave <= octaves; octave++) {
                frequencies[octave - 1] = Math.pow(2.0D, octave);
                amplitudes[octave - 1] = Math.pow(persistence, octave);
            }
        }
    }

    private static final class ProfileKey {
        private final long persistenceBits;
        private final int octaves;

        private ProfileKey(double persistence, int octaves) {
            this.persistenceBits = Double.doubleToLongBits(persistence);
            this.octaves = octaves;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof ProfileKey)) {
                return false;
            }
            ProfileKey other = (ProfileKey) object;
            return persistenceBits == other.persistenceBits && octaves == other.octaves;
        }

        @Override
        public int hashCode() {
            return 31 * Long.hashCode(persistenceBits) + octaves;
        }
    }
}
