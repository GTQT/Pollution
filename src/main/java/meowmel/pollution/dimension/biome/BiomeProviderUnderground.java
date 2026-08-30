package meowmel.pollution.dimension.biome;

import meowmel.pollution.dimension.biome.gen.GenLayerUndergroundBiomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 地下世界群系提供器（参考 Nether-API 的 BiomeProviderNetherAPI）：
 * GenLayer 标准机制 + BiomeCache，支持三个 getBiomes 重载。
 * 出生点仅允许深窟基础群系。
 */
public class BiomeProviderUnderground extends BiomeProvider {

    private final GenLayer genBiomes;
    private final GenLayer biomeIndexLayer;
    private final BiomeCache biomeCache;
    private final List<Biome> biomesToSpawnIn;

    public BiomeProviderUnderground(long seed, WorldType worldType) {
        super();
        GenLayerUndergroundBiomes layer = new GenLayerUndergroundBiomes(seed);
        this.genBiomes = layer;
        this.biomeIndexLayer = layer;
        this.biomeCache = new BiomeCache(this);
        this.biomesToSpawnIn = Collections.singletonList(UndergroundBiomes.DEEP_CAVE);
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return biomesToSpawnIn;
    }

    @Override
    public Biome[] getBiomesForGeneration(@Nullable Biome[] listToReuse, int x, int z, int width, int height) {
        return fillFromLayer(listToReuse, genBiomes, x, z, width, height);
    }

    @Override
    public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int height) {
        return fillFromLayer(listToReuse, biomeIndexLayer, x, z, width, height);
    }

    @Override
    public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z,
                             int width, int height, boolean cacheFlag) {
        IntCache.resetIntCache();
        int size = width * height;
        if (listToReuse == null || listToReuse.length < size) {
            listToReuse = new Biome[size];
        }
        // 标准 BiomeCache 路径（整区块查询走缓存）
        if (cacheFlag && width == 16 && height == 16 && (x & 15) == 0 && (z & 15) == 0) {
            Biome[] cached = biomeCache.getCachedBiomes(x, z);
            System.arraycopy(cached, 0, listToReuse, 0, size);
            return listToReuse;
        }
        return fillFromLayer(listToReuse, biomeIndexLayer, x, z, width, height);
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
        return !allowed.isEmpty() && super.areBiomesViable(x, z, radius << 2, allowed);
    }

    @Override
    @Nullable
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
        return super.findBiomePosition(x, z, range, biomes, random);
    }

    @Override
    public void cleanupCache() {
        biomeCache.cleanupCache();
    }

    private static Biome[] fillFromLayer(@Nullable Biome[] listToReuse, GenLayer layer,
                                         int x, int z, int width, int height) {
        IntCache.resetIntCache();
        int size = width * height;
        if (listToReuse == null || listToReuse.length < size) {
            listToReuse = new Biome[size];
        }
        int[] biomeIds = layer.getInts(x, z, width, height);
        for (int i = 0; i < size; i++) {
            Biome biome = Biome.getBiome(biomeIds[i]);
            if (biome == null) {
                throw new IllegalStateException("Unmapped biome id: " + biomeIds[i]);
            }
            listToReuse[i] = biome;
        }
        return listToReuse;
    }
}
