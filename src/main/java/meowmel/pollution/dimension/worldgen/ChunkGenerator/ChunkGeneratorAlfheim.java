package meowmel.pollution.dimension.worldgen.ChunkGenerator;

import meowmel.pollution.dimension.biome.AlfheimBiomes;
import meowmel.pollution.dimension.biome.biomes.AlfheimBiome;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenMinable;
import vazkii.botania.common.block.ModBlocks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Base-only Alfheim terrain port.
 * Story structures, anomalies, seasonal logic, portals and dynamic islands are
 * intentionally absent; see the migration boundary in the task description.
 */
public final class ChunkGeneratorAlfheim implements IChunkGenerator {

    // WorldProviderAlfheim.kt applies BiomeAlfheim.offset (-7) to
    // WE_TerrainGenerator.worldSeaGenMaxY.  The source generator's normal
    // water plane is Y=64, so Alfheim's actual water plane is Y=57.
    private static final int SEA_LEVEL = 57;
    private static final IBlockState WATER = Blocks.WATER.getDefaultState();
    private static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    private static final IBlockState CARVABLE_STONE = Blocks.STONE.getDefaultState();
    private static final IBlockState LIVINGROCK = ModBlocks.livingrock.getDefaultState();

    private final World world;
    private final Random random;
    private final NoiseGeneratorOctaves terrainNoise;
    private final NoiseGeneratorOctaves detailNoise;
    private final MapGenCaves caves = new MapGenCaves();
    private final MapGenRavine ravines = new MapGenRavine();

    public ChunkGeneratorAlfheim(World world, long seed) {
        this.world = world;
        this.world.setSeaLevel(SEA_LEVEL);
        this.random = new Random(seed);
        this.terrainNoise = new NoiseGeneratorOctaves(new Random(seed), 6);
        this.detailNoise = new NoiseGeneratorOctaves(new Random(seed ^ 0x5DEECE66DL), 4);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        ChunkPrimer primer = new ChunkPrimer();
        Biome[] biomes = world.getBiomeProvider().getBiomesForGeneration(null, chunkX * 16, chunkZ * 16, 16, 16);
        double[] large = terrainNoise.generateNoiseOctaves(null, chunkX * 16, chunkZ * 16, 16, 16, 0.00125D, 0.00125D, 1.0D);
        double[] detail = detailNoise.generateNoiseOctaves(null, chunkX * 16, chunkZ * 16, 16, 16, 0.0125D, 0.0125D, 1.0D);

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int index = localX + localZ * 16;
                Biome biome = biomes[index];
                int terrainHeight = terrainHeight(biome, large[index], detail[index]);
                generateColumn(primer, localX, localZ, terrainHeight, biome);
            }
        }

        // Vanilla 1.12 carvers only recognize vanilla stone. Generate a
        // temporary stone body, carve it, then restore every surviving stone
        // cell to livingrock. This reproduces WorldEngine's livingrock
        // replacement list without importing its 1.7 carvers.
        caves.generate(world, chunkX, chunkZ, primer);
        ravines.generate(world, chunkX, chunkZ, primer);
        restoreLivingrock(primer);

        Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte) Biome.getIdForBiome(biomes[i]);
        }
        chunk.resetRelightChecks();
        return chunk;
    }

    private static int terrainHeight(Biome biome, double largeNoise, double detailNoise) {
        int sourceHeight = biome instanceof AlfheimBiome
                ? ((AlfheimBiome) biome).getSurfaceHeight()
                : SEA_LEVEL;
        // NoiseGeneratorOctaves is not normalized.  Constrain it before
        // applying variation so an unusually large octave result cannot turn
        // every land biome into seabed.
        double large = normalizeNoise(largeNoise);
        double detail = normalizeNoise(detailNoise);
        double variation = Math.max(4.0D, biome.getHeightVariation() * 8.0D);
        int height = (int) Math.round(sourceHeight + large * variation + detail * 3.0D);

        // Every source biome with a grass surface (field, forests and all
        // plateaus) is terrestrial.  Keep its top block above the source's
        // shifted water plane so its vanilla grass, trees and decorations
        // have valid generation ground.
        if (biome.topBlock.getBlock() == Blocks.GRASS) {
            height = Math.max(SEA_LEVEL + 1, height);
        }
        return Math.max(5, Math.min(245, height));
    }

    private static double normalizeNoise(double value) {
        return value / (1.0D + Math.abs(value));
    }

    private static void generateColumn(ChunkPrimer primer, int x, int z, int terrainHeight, Biome biome) {
        for (int y = 0; y <= terrainHeight; y++) {
            primer.setBlockState(x, y, z, y == 0 ? BEDROCK : CARVABLE_STONE);
        }
        for (int y = terrainHeight + 1; y <= SEA_LEVEL; y++) {
            primer.setBlockState(x, y, z, WATER);
        }

        IBlockState top = biome.topBlock;
        IBlockState filler = biome.fillerBlock;
        primer.setBlockState(x, terrainHeight, z, top);
        if (terrainHeight >= SEA_LEVEL - 1) {
            for (int y = Math.max(1, terrainHeight - 3); y < terrainHeight; y++) {
                primer.setBlockState(x, y, z, filler);
            }
        }
    }

    private static void restoreLivingrock(ChunkPrimer primer) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y < 256; y++) {
                    if (primer.getBlockState(x, y, z).getBlock() == Blocks.STONE) {
                        primer.setBlockState(x, y, z, LIVINGROCK);
                    }
                }
            }
        }
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        random.setSeed(world.getSeed());
        long xSeed = random.nextLong() / 2L * 2L + 1L;
        long zSeed = random.nextLong() / 2L * 2L + 1L;
        random.setSeed(chunkX * xSeed + chunkZ * zSeed ^ world.getSeed());

        BlockPos origin = new BlockPos(chunkX * 16, 0, chunkZ * 16);
        Biome biome = world.getBiome(origin.add(8, 0, 8));
        generateOres(origin);
        generateLakes(origin, biome);
        biome.decorate(world, random, origin);
    }

    private void generateOres(BlockPos origin) {
        // Source material -> replacement: Dragonstone/Elementium/Iffesal use
        // vanilla diamond/gold/emerald. Quartz, gold and lapis retain their
        // corresponding vanilla forms. All veins replace Botania livingrock.
        generateOre(Blocks.DIAMOND_ORE.getDefaultState(), 4, 6, 1, 16, origin);
        generateOre(Blocks.GOLD_ORE.getDefaultState(), 6, 18, 1, 59, origin);
        generateOre(Blocks.GOLD_ORE.getDefaultState(), 4, 24, 60, 140, origin);
        generateOre(Blocks.QUARTZ_ORE.getDefaultState(), 6, 18, 1, 59, origin);
        generateOre(Blocks.QUARTZ_ORE.getDefaultState(), 4, 24, 60, 140, origin);
        generateOre(Blocks.GOLD_ORE.getDefaultState(), 4, 3, 1, 34, origin);
        generateOre(Blocks.EMERALD_ORE.getDefaultState(), 3, 4, 16, 48, origin);
        generateOre(Blocks.LAPIS_ORE.getDefaultState(), 10, 2, 1, 26, origin);
    }

    private void generateOre(IBlockState state, int size, int count, int minY, int maxY, BlockPos origin) {
        WorldGenMinable generator = new WorldGenMinable(state, size, BlockMatcher.forBlock(ModBlocks.livingrock));
        for (int i = 0; i < count; i++) {
            int y = minY + random.nextInt(maxY - minY + 1);
            generator.generate(world, random, origin.add(random.nextInt(16), y, random.nextInt(16)));
        }
    }

    private void generateLakes(BlockPos origin, Biome biome) {
        int chance = 12;
        int minY = 0;
        if (biome == AlfheimBiomes.LOW_PLATEAU || biome == AlfheimBiomes.MID_PLATEAU || biome == AlfheimBiomes.HIGH_PLATEAU_FOREST) {
            chance = 2;
            minY = biome == AlfheimBiomes.LOW_PLATEAU ? 76 : biome == AlfheimBiomes.MID_PLATEAU ? 100 : 124;
        } else if (biome == AlfheimBiomes.HIGH_PLATEAU) {
            chance = 1;
            minY = 124;
        }
        if (random.nextInt(chance) == 0) {
            // AlfheimLakeGen chooses a random Y in [minY, 255], then descends
            // through air before carving its lake.  WorldGenLakes performs the
            // same descent/carve pattern on the 1.12 target.
            BlockPos lakePos = origin.add(random.nextInt(16), minY + random.nextInt(256 - minY), random.nextInt(16));
            new WorldGenLakes(Blocks.WATER).generate(world, random, lakePos);
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
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        // No static structures are part of this migration.
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
