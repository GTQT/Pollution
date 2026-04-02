package meowmel.pollution.dimension.worldgen.ChunkGenerator;

import gregtech.api.fluids.store.FluidStorageKeys;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenBloodBound;
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
import net.minecraft.world.gen.NoiseGeneratorSimplex;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static meowmel.pollution.common.block.blocks.PollutionBlocksInit.FLESH_BLOCK;

public class ChunkGeneratorBlood implements IChunkGenerator {

    private static final int SEA_LEVEL = 90;
    private static final int GEN_LAND_LEVEL = 80;

    private static final IBlockState FLESH_STATE = FLESH_BLOCK.getDefaultState();
    private static final IBlockState BEDROCK_STATE = Blocks.BEDROCK.getDefaultState();
    private static final IBlockState PLASMA_STATE = PollutionMaterials.BloodPlasma.getFluid(FluidStorageKeys.LIQUID).getBlock().getDefaultState();

    private final Random rand;
    private final World world;
    private final WorldGenBloodBound fleshFeatureGen;

    private TerrainGenerator heightNoise;
    private NoiseGeneratorSimplex islandNoise;

    private Biome[] biomesForGeneration;

    public ChunkGeneratorBlood(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.fleshFeatureGen = new WorldGenBloodBound();
        initializeNoiseGenerators(rand);
    }

    private void initializeNoiseGenerators(Random seed) {
        this.heightNoise = new TerrainGenerator(seed);
        this.islandNoise = new NoiseGeneratorSimplex(seed);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer primer = new ChunkPrimer();

        this.biomesForGeneration = this.world.getBiomeProvider()
                .getBiomes(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);

        generateTerrain(primer, chunkX, chunkZ);
        buildSurfaces(primer, chunkX, chunkZ);

        Chunk chunk = new Chunk(this.world, primer, chunkX, chunkZ);
        setBiomeArray(chunk);
        chunk.generateSkylightMap();

        return chunk;
    }

    private void generateTerrain(ChunkPrimer primer, int chunkX, int chunkZ) {
        double[] noises = getHeights(chunkX, chunkZ);

        int index = 0;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                double noiseVal = noises[index++];

                double terrainHeightDouble = GEN_LAND_LEVEL + noiseVal;
                int terrainHeight = (int) MathHelper.clamp(terrainHeightDouble, 1, 250);

                primer.setBlockState(x, 0, z, BEDROCK_STATE);

                for (int y = 1 ; y < SEA_LEVEL; ++y) {
                    primer.setBlockState(x, y, z, PLASMA_STATE);
                }

                for (int y = 1; y < terrainHeight; ++y) {
                    primer.setBlockState(x, y, z, FLESH_STATE);
                }
            }
        }
    }


    private double[] getHeights(int xOffset, int zOffset) {
        return heightNoise.getHeights(xOffset, zOffset);
    }

    private void buildSurfaces(ChunkPrimer primer, int chunkX, int chunkZ) {
        if (net.minecraftforge.event.ForgeEventFactory
                .onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, this.world)) {
            return;
        }
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(
                true, this, this.world, this.rand, chunkX, chunkZ, false);

        BlockPos chunkPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);

        this.world.getBiome(chunkPos.add(8, 0, 8))
                .decorate(this.world, this.rand, chunkPos);

        double featureNoise = islandNoise.getValue(chunkX, chunkZ);

        if (featureNoise > 0.3D) {
            int attempts = this.rand.nextInt(2) + 1;
            for (int i = 0; i < attempts; i++) {
                int x = this.rand.nextInt(16);
                int z = this.rand.nextInt(16);
                int y = this.world.getHeight(chunkPos.add(x, 0, z)).getY();

                if (y > SEA_LEVEL) {
                    BlockPos spawnPos = chunkPos.add(x, y, z);
                    this.fleshFeatureGen.generate(this.world, this.rand, spawnPos);
                }
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(
                false, this, this.world, this.rand, chunkX, chunkZ, false);
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunk, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, BlockPos pos) {
        return this.world.getBiome(pos).getSpawnableList(type);
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World world, String structure, BlockPos pos, boolean findUnexplored) {
        return null;
    }

    @Override
    public boolean isInsideStructure(World world, String structure, BlockPos pos) {
        return false;
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {
    }

    private void setBiomeArray(Chunk chunk) {
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);
        }
    }
}