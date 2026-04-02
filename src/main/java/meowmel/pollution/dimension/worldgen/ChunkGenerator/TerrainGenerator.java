package meowmel.pollution.dimension.worldgen.ChunkGenerator;

import java.util.Random;

public class TerrainGenerator {
    private static final int OCTAVES = 4;
    private static final double PERSISTENCE = 0.5;
    private static final double LACUNARITY = 2.0;
    private static final double SCALE = 0.008;
    private static final double AMPLITUDE_SCALE = 60.0;

    private final long seed;

    public TerrainGenerator(Random random) {
        this.seed = random.nextLong();
    }

    public double[] getHeights(int xOffset, int zOffset) {
        double[] heights = new double[256];
        double baseWorldX = xOffset * 16.0 * SCALE;
        double baseWorldZ = zOffset * 16.0 * SCALE;
        int index = 0;
        for (int localX = 0; localX < 16; localX++) {
            double worldX = baseWorldX + localX * SCALE;
            for (int localZ = 0; localZ < 16; localZ++) {
                double worldZ = baseWorldZ + localZ * SCALE;
                double value = 0.0;
                double amplitude = 1.0;
                double frequency = 1.0;
                for (int o = 0; o < OCTAVES; o++) {
                    value += amplitude * perlinNoise(worldX * frequency, worldZ * frequency);
                    amplitude *= PERSISTENCE;
                    frequency *= LACUNARITY;
                }

                value = Math.min(1.0, Math.max(-1.0, value)) * AMPLITUDE_SCALE;
                heights[index++] = value;
            }
        }
        return heights;
    }

    private double perlinNoise(double x, double y) {
        int ix = (int) Math.floor(x);
        int iy = (int) Math.floor(y);
        double fx = x - ix;
        double fy = y - iy;

        double u = fade(fx);
        double v = fade(fy);

        double g00 = dotGridGradient(ix, iy, fx, fy);
        double g10 = dotGridGradient(ix + 1, iy, fx - 1, fy);
        double g01 = dotGridGradient(ix, iy + 1, fx, fy - 1);
        double g11 = dotGridGradient(ix + 1, iy + 1, fx - 1, fy - 1);

        double nx0 = lerp(g00, g10, u);
        double nx1 = lerp(g01, g11, u);
        double value = lerp(nx0, nx1, v);

        // 标准Perlin输出范围[-0.707,0.707] → 乘1.5后约[-1,1]
        value = value * 1.5;
        return Math.min(1.0, Math.max(-1.0, value));
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private double dotGridGradient(int ix, int iy, double dx, double dy) {
        double[] grad = getGradient(ix, iy);
        return grad[0] * dx + grad[1] * dy;
    }

    private double[] getGradient(int ix, int iy) {
        long h = (ix * 0x9e3779b97f4a7c15L) ^ (iy * 0xbf58476d1ce4e5b9L) ^ seed;
        h = (h ^ (h >>> 33)) * 0x85ebca6bL;
        h = (h ^ (h >>> 33)) * 0xc2b2ae35L;
        h = h ^ (h >>> 33);
        double angle = (h & 0x7FFFFFFFFFFFFFFFL) / (double) 0x8000000000000000L * Math.PI * 2;
        return new double[]{Math.cos(angle), Math.sin(angle)};
    }
}