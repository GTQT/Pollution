package meowmel.pollution.dimension.worldgen.ChunkGenerator;

import meowmel.pollution.dimension.biome.AlfheimBiomes;
import meowmel.pollution.dimension.biome.biomes.AlfheimBiome;
import meowmel.pollution.dimension.worldgen.WorldEngineNoise;
import meowmel.pollution.dimension.worldgen.feature.WorldGenAlfheimLake;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import vazkii.botania.common.block.ModBlocks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Base-world-generation-only Alfheim port.
 *
 * <p>The terrain, biome interpolation, biome layers, ores and lakes are based
 * on the actual WorldEngine and Alfheim sources. Story structures, seasonal
 * systems, disasters, portals and other-world travel are intentionally absent.</p>
 */
public final class ChunkGeneratorAlfheim implements IChunkGenerator {

    private static final int SEA_LEVEL = 57;
    private static final IBlockState WATER = Blocks.WATER.getDefaultState();
    private static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    private static final IBlockState CARVABLE_STONE = Blocks.STONE.getDefaultState();
    private static final IBlockState LIVINGROCK = ModBlocks.livingrock.getDefaultState();
    private static final WorldGenAlfheimLake WATER_LAKE = new WorldGenAlfheimLake(Blocks.WATER);
    private static final int MAX_INTERPOLATE_QUALITY = findMaxInterpolationQuality();
    private static final InterpolationStencil[] INTERPOLATION_STENCILS =
            buildInterpolationStencils(MAX_INTERPOLATE_QUALITY);

    private final World world;
    private final long seed;
    private final Random random;
    private final MapGenBase caves;
    private final MapGenBase ravines;

    public ChunkGeneratorAlfheim(World world, long seed) {
        this.world = world;
        this.seed = seed;
        this.world.setSeaLevel(SEA_LEVEL);
        this.random = new Random(seed);
        this.caves = TerrainGen.getModdedMapGen(new MapGenCaves(), InitMapGenEvent.EventType.CAVE);
        this.ravines = TerrainGen.getModdedMapGen(new MapGenRavine(), InitMapGenEvent.EventType.RAVINE);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        int chunkBlockX = chunkX * 16;
        int chunkBlockZ = chunkZ * 16;
        ChunkPrimer primer = new ChunkPrimer();

        int border = MAX_INTERPOLATE_QUALITY;
        int borderedSize = 16 + border * 2;
        Biome[] borderedBiomes = world.getBiomeProvider().getBiomesForGeneration(
                null, chunkBlockX - border, chunkBlockZ - border, borderedSize, borderedSize);
        Biome[] biomes = new Biome[256];
        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                biomes[localX + localZ * 16] =
                        borderedBiomes[index(localX + border, localZ + border, borderedSize)];
            }
        }
        boolean interpolate = containsMultipleBiomes(borderedBiomes);
        int[] terrainHeights = new int[256];
        int maxTerrainHeight = 0;

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int index = localX + localZ * 16;
                int terrainHeight = MathHelper.floor(interpolatedHeight(
                        chunkBlockX, chunkBlockZ, localX, localZ,
                        borderedBiomes, borderedSize, interpolate, border));
                terrainHeights[index] = terrainHeight;
                maxTerrainHeight = Math.max(maxTerrainHeight, Math.min(255, terrainHeight));
                generateBaseColumn(primer, localX, localZ, terrainHeight);
            }
        }

        // Vanilla carvers recognize vanilla stone. After carving, only scan
        // through the highest generated terrain cell instead of all 256 Y
        // levels, then apply WorldEngine's biome layers in source order.
        caves.generate(world, chunkX, chunkZ, primer);
        ravines.generate(world, chunkX, chunkZ, primer);
        restoreLivingrock(primer, maxTerrainHeight);

        random.setSeed(seed * (long) Math.pow(chunkX, 3)
                + (long) Math.pow(chunkZ, 2) * 9874L + 7684053L);
        applyBiomeLayers(primer, biomes, terrainHeights);

        Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte) Biome.getIdForBiome(biomes[i]);
        }
        chunk.resetRelightChecks();
        return chunk;
    }

    /**
     * Direct port of WE_TerrainGenerator.interpolatedBlock.
     */
    private double interpolatedHeight(int chunkBlockX, int chunkBlockZ, int localX, int localZ,
                                      Biome[] bordered, int borderedSize,
                                      boolean interpolate, int border) {
        int centerX = border + localX;
        int centerZ = border + localZ;
        AlfheimBiome center = alfheimBiome(bordered[index(centerX, centerZ, borderedSize)]);

        if (!interpolate) {
            return center.getSurfaceHeight() + WorldEngineNoise.perlinNoise2D(
                    seed,
                    (chunkBlockX + (long) localX) / center.getTerrainScaleX(),
                    (chunkBlockZ + (long) localZ) / center.getTerrainScaleX(),
                    center.getTerrainNoiseProfile()) * center.getTerrainScaleY();
        }

        double persistence = 0.0D;
        double scaleX = 0.0D;
        double scaleY = 0.0D;
        int octaves = 0;
        int surfaceHeight = 0;
        int samples = 0;
        WorldEngineNoise.NoiseProfile commonNoiseProfile = center.getTerrainNoiseProfile();
        boolean sameNoiseProfile = true;

        InterpolationStencil stencil = INTERPOLATION_STENCILS[center.getInterpolateQuality()];
        int[] offsetX = stencil.offsetXByCenter[centerX];
        int[] offsetZ = stencil.offsetZByCenter[centerZ];
        for (int i = 0; i < offsetX.length; i++) {
            int sampleX = centerX + offsetX[i];
            int sampleZ = centerZ + offsetZ[i];
            AlfheimBiome sample = alfheimBiome(bordered[index(sampleX, sampleZ, borderedSize)]);
            if (sample.getTerrainNoiseProfile() != commonNoiseProfile) {
                sameNoiseProfile = false;
            }
            samples++;
            persistence += sample.getTerrainPersistence();
            octaves += sample.getTerrainOctaves();
            scaleX += sample.getTerrainScaleX();
            scaleY += sample.getTerrainScaleY();
            surfaceHeight += sample.getSurfaceHeight();
        }

        persistence /= samples;
        scaleX /= samples;
        scaleY /= samples;
        octaves /= samples;
        surfaceHeight /= samples;
        double noiseX = (chunkBlockX + (long) localX) / scaleX;
        double noiseZ = (chunkBlockZ + (long) localZ) / scaleX;
        double noise = sameNoiseProfile
                ? WorldEngineNoise.perlinNoise2D(seed, noiseX, noiseZ, commonNoiseProfile)
                : WorldEngineNoise.perlinNoise2D(seed, noiseX, noiseZ, persistence, octaves);
        return surfaceHeight + noise * scaleY;
    }

    private static void generateBaseColumn(ChunkPrimer primer, int x, int z, int terrainHeight) {
        int cappedHeight = Math.min(255, terrainHeight);
        for (int y = 0; y <= cappedHeight; y++) {
            primer.setBlockState(x, y, z, CARVABLE_STONE);
        }
        for (int y = Math.max(0, terrainHeight + 1); y <= SEA_LEVEL; y++) {
            primer.setBlockState(x, y, z, WATER);
        }
    }

    private static void restoreLivingrock(ChunkPrimer primer, int maxTerrainHeight) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y <= maxTerrainHeight; y++) {
                    if (primer.getBlockState(x, y, z).getBlock() == Blocks.STONE) {
                        primer.setBlockState(x, y, z, LIVINGROCK);
                    }
                }
            }
        }
    }

    /**
     * Ports the per-biome WE_BiomeLayer passes after caves and ravines.
     */
    private void applyBiomeLayers(ChunkPrimer primer, Biome[] biomes, int[] terrainHeights) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int columnIndex = x + z * 16;
                AlfheimBiome biome = alfheimBiome(biomes[columnIndex]);
                int topY = findTopSolid(primer, x, z, Math.min(255, terrainHeights[columnIndex]));

                if (topY >= 0) {
                    int fillerDepth = biome.getFillerDepth(random);
                    for (int y = topY; y >= Math.max(0, topY - fillerDepth); y--) {
                        if (primer.getBlockState(x, y, z).getBlock() == ModBlocks.livingrock) {
                            primer.setBlockState(x, y, z, biome.fillerBlock);
                        }
                    }

                    boolean liquidAbove = topY < 255
                            && primer.getBlockState(x, topY + 1, z).getMaterial().isLiquid();
                    if (biome.canGenerateTopUnderwater() || !liquidAbove) {
                        if (primer.getBlockState(x, topY, z).equals(biome.fillerBlock)) {
                            primer.setBlockState(x, topY, z, biome.getGeneratedTopBlock(random));
                        }
                    }
                }
                primer.setBlockState(x, 0, z, BEDROCK);
            }
        }
    }

    private static int findTopSolid(ChunkPrimer primer, int x, int z, int startY) {
        for (int y = startY; y >= 0; y--) {
            IBlockState state = primer.getBlockState(x, y, z);
            if (state.getBlock() != Blocks.AIR && !state.getMaterial().isLiquid()) {
                return y;
            }
        }
        return -1;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        random.setSeed(world.getSeed());
        long xSeed = random.nextLong() / 2L * 2L + 1L;
        long zSeed = random.nextLong() / 2L * 2L + 1L;
        random.setSeed(chunkX * xSeed + chunkZ * zSeed ^ world.getSeed());

        BlockPos origin = new BlockPos(chunkX * 16, 0, chunkZ * 16);
        boolean populateStarted = false;
        try {
            ForgeEventFactory.onChunkPopulate(true, this, world, random, chunkX, chunkZ, false);
            populateStarted = true;
            generateLake(chunkX, chunkZ, origin, 12, 0);

            // Decoration features use the vanilla +8 offset and may extend
            // into the neighboring chunk. Select the biome at the center of
            // that decoration area instead of consuming two random values and
            // sampling an unrelated point in the raw 16x16 chunk.
            Biome biome = world.getBiome(origin.add(16, 0, 16));
            if (biome == AlfheimBiomes.LOW_PLATEAU) {
                generateLake(chunkX, chunkZ, origin, 2, 76);
            } else if (biome == AlfheimBiomes.MID_PLATEAU) {
                generateLake(chunkX, chunkZ, origin, 2, 100);
            } else if (biome == AlfheimBiomes.HIGH_PLATEAU) {
                generateLake(chunkX, chunkZ, origin, 1, 124);
            } else if (biome == AlfheimBiomes.HIGH_PLATEAU_FOREST) {
                generateLake(chunkX, chunkZ, origin, 2, 124);
            }
            biome.decorate(world, random, origin);
        } finally {
            try {
                if (populateStarted) {
                    ForgeEventFactory.onChunkPopulate(
                            false, this, world, random, chunkX, chunkZ, false);
                }
            } finally {
                BlockFalling.fallInstantly = false;
            }
        }
    }

    private void generateLake(int chunkX, int chunkZ, BlockPos origin, int chance, int minY) {
        if (random.nextInt(chance) != 0
                || !TerrainGen.populate(this, world, random, chunkX, chunkZ, false,
                PopulateChunkEvent.Populate.EventType.LAKE)) {
            return;
        }
        BlockPos lakePos = origin.add(
                random.nextInt(16),
                minY + random.nextInt(256 - minY),
                random.nextInt(16));
        WATER_LAKE.generate(world, random, lakePos);
    }

    private static boolean containsMultipleBiomes(Biome[] biomes) {
        Biome first = biomes[0];
        for (int i = 1; i < biomes.length; i++) {
            if (biomes[i] != first) {
                return true;
            }
        }
        return false;
    }

    private static AlfheimBiome alfheimBiome(Biome biome) {
        return biome instanceof AlfheimBiome
                ? (AlfheimBiome) biome
                : (AlfheimBiome) AlfheimBiomes.FIELD;
    }

    private static int findMaxInterpolationQuality() {
        int result = 0;
        for (Biome biome : AlfheimBiomes.ALL) {
            result = Math.max(result, alfheimBiome(biome).getInterpolateQuality());
        }
        return result;
    }

    /**
     * Precomputes the exact angle/radius sampling order used by WorldEngine.
     * Keeping duplicate offsets is intentional: they contribute repeatedly to
     * the source average, but their trigonometry does not need to be redone for
     * every terrain column.
     */
    private static InterpolationStencil[] buildInterpolationStencils(int maxQuality) {
        InterpolationStencil[] stencils = new InterpolationStencil[maxQuality + 1];
        int maxCenterCoordinate = 15 + maxQuality * 2;
        for (int quality = 0; quality <= maxQuality; quality++) {
            int sampleCount = 361 * (quality + 1);
            int[][] offsetXByCenter = new int[maxCenterCoordinate + 1][sampleCount];
            int[][] offsetZByCenter = new int[maxCenterCoordinate + 1][sampleCount];
            int sample = 0;
            for (int angle = 0; angle <= 360; angle++) {
                float radians = angle * (float) Math.PI / 180.0F;
                for (int radius = 0; radius <= quality; radius++) {
                    float xOffset = MathHelper.cos(radians) * radius;
                    float zOffset = MathHelper.sin(radians) * radius;
                    for (int center = 0; center <= maxCenterCoordinate; center++) {
                        offsetXByCenter[center][sample] =
                                MathHelper.floor(center + xOffset) - center;
                        offsetZByCenter[center][sample] =
                                MathHelper.floor(center + zOffset) - center;
                    }
                    sample++;
                }
            }
            stencils[quality] = new InterpolationStencil(offsetXByCenter, offsetZByCenter);
        }
        return stencils;
    }

    private static int index(int x, int z, int width) {
        return x + z * width;
    }

    private static final class InterpolationStencil {
        private final int[][] offsetXByCenter;
        private final int[][] offsetZByCenter;

        private InterpolationStencil(int[][] offsetXByCenter, int[][] offsetZByCenter) {
            this.offsetXByCenter = offsetXByCenter;
            this.offsetZByCenter = offsetZByCenter;
        }
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return world.getBiome(pos).getSpawnableList(creatureType);
    }

    @Override
    @Nullable
    public BlockPos getNearestStructurePos(World worldIn, String structureName,
                                           BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        // No structures are part of the requested migration boundary.
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
