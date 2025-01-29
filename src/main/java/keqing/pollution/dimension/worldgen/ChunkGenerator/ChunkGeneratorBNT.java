package keqing.pollution.dimension.worldgen.ChunkGenerator;

import keqing.pollution.dimension.worldgen.mapGen.*;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.WorldGenFluidPool;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static keqing.pollution.dimension.worldgen.terraingen.InitMapGenEvent.EventType.BTN;
import static keqing.pollution.dimension.worldgen.terraingen.InitMapGenEvent.EventType.BTN_CAVE;
import static keqing.pollution.dimension.worldgen.terraingen.TerrainGen.getModdedMapGen;

public class ChunkGeneratorBNT implements IChunkGenerator {
    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
    protected static final IBlockState BTLStone = BlockRegistry.BETWEENSTONE.getDefaultState();
    protected static final IBlockState BTLPitstone = BlockRegistry.PITSTONE.getDefaultState();
    protected static final IBlockState BTLCragRock = BlockRegistry.CRAGROCK.getDefaultState();
    protected static final IBlockState BTLLimestone = BlockRegistry.LIMESTONE.getDefaultState();
    protected static final IBlockState StagnantWater = BlockRegistry.STAGNANT_WATER.getDefaultState();
    protected static final IBlockState SwampWater = BlockRegistry.SWAMP_WATER.getDefaultState();
    protected static final IBlockState BTLBedRock = BlockRegistry.BETWEENLANDS_BEDROCK.getDefaultState();

    private final World world;
    private final boolean generateStructures;
    private final Random rand;
    //钟乳石集群
    private final WorldGenStalactite stalactite = new WorldGenStalactite();
    //蘑菇灯集群
    private final WorldGenOreStone1 worldGenOreStone1 = new WorldGenOreStone1();
    private final WorldGenOreStone2 worldGenOreStone2 = new WorldGenOreStone2();
    //矿物集群
    private final WorldGenerator mudGen = new WorldGenMinable(BlockRegistry.MUD.getDefaultState(), 14, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));
    private final WorldGenerator siltGen = new WorldGenMinable(BlockRegistry.SILT.getDefaultState(), 33, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));
    //流体集群
    private final WorldGenBNTWater StagnantWaterGen = new WorldGenBNTWater(BlockRegistry.STAGNANT_WATER, true);
    private final WorldGenBNTWater SwampWaterGen = new WorldGenBNTWater(BlockRegistry.SWAMP_WATER, false);
    //小蘑菇集群
    private final WorldGenMushroom MushroomFeature1 = new WorldGenMushroom(BlockRegistry.BULB_CAPPED_MUSHROOM);
    private final WorldGenMushroom MushroomFeature2 = new WorldGenMushroom(BlockRegistry.BLACK_HAT_MUSHROOM);
    private final WorldGenMushroom MushroomFeature3 = new WorldGenMushroom(BlockRegistry.FLAT_HEAD_MUSHROOM);
    //洞穴草集群
    private final WorldGenSingle worldGenCaveGrass = new WorldGenSingle(BlockRegistry.CAVE_GRASS);
    //洞穴落叶集群
    private final WorldGenSingle worldGenNesting = new WorldGenSingle(BlockRegistry.NESTING_BLOCK_STICKS);
    //洞穴水集群
    private final WorldGenFluidPool worldGenFluidPool = new WorldGenFluidPool(BlockRegistry.STAGNANT_WATER);
    private final WorldGenFluidPool worldGenLavaPool = new WorldGenFluidPool(Blocks.LAVA);
    //灵气集群
    private final WorldGenPhaseLiq worldGenPhaseLiq = new WorldGenPhaseLiq();

    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    double[] pnr;
    double[] ar;
    double[] br;
    double[] noiseData4;
    double[] dr;
    /**
     * Holds the noise used to determine whether slowsand can be generated at a location
     */
    private double[] slowsandNoise = new double[256];
    private double[] gravelNoise = new double[256];
    private double[] depthBuffer = new double[256];
    private double[] buffer;
    private NoiseGeneratorOctaves lperlinNoise1;
    private NoiseGeneratorOctaves lperlinNoise2;
    private NoiseGeneratorOctaves perlinNoise1;
    /**
     * Determines whether slowsand or gravel can be generated at a location
     */
    private NoiseGeneratorOctaves slowsandGravelNoiseGen;
    /**
     * Determines whether something other than nettherack can be generated at a location
     */
    private NoiseGeneratorOctaves BTLStoneExculsivityNoiseGen;
    //生物集群
    private MapGenBTNBridge mapGenBTNBridge = new MapGenBTNBridge();
    //洞穴集群
    private MapGenBase mapGenCavesBTN = new MapGenCavesBTN();

    public ChunkGeneratorBNT(World worldIn, boolean p_i45637_2_, long seed) {
        this.world = worldIn;
        this.generateStructures = p_i45637_2_;
        this.rand = new Random(seed);
        this.lperlinNoise1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.lperlinNoise2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.perlinNoise1 = new NoiseGeneratorOctaves(this.rand, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.rand, 4);
        this.BTLStoneExculsivityNoiseGen = new NoiseGeneratorOctaves(this.rand, 4);
        this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);
        worldIn.setSeaLevel(63);

        net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell ctx =
                new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell(lperlinNoise1, lperlinNoise2, perlinNoise1, slowsandGravelNoiseGen, BTLStoneExculsivityNoiseGen, scaleNoise, depthNoise);
        ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(worldIn, this.rand, ctx);
        this.lperlinNoise1 = ctx.getLPerlin1();
        this.lperlinNoise2 = ctx.getLPerlin2();
        this.perlinNoise1 = ctx.getPerlin();
        this.slowsandGravelNoiseGen = ctx.getPerlin2();
        this.BTLStoneExculsivityNoiseGen = ctx.getPerlin3();
        this.scaleNoise = ctx.getScale();
        this.depthNoise = ctx.getDepth();
        this.mapGenBTNBridge = (MapGenBTNBridge) getModdedMapGen(mapGenBTNBridge, BTN);
        this.mapGenCavesBTN = getModdedMapGen(mapGenCavesBTN, BTN_CAVE);
    }

    public void prepareHeights(int p_185936_1_, int p_185936_2_, ChunkPrimer primer) {
        int i = 4;
        int j = this.world.getSeaLevel() / 2 + 1;
        int k = 5;
        int l = 17;
        int i1 = 5;
        this.buffer = this.getHeights(this.buffer, p_185936_1_ * 4, 0, p_185936_2_ * 4, 5, 17, 5);

        for (int j1 = 0; j1 < 4; ++j1) {
            for (int k1 = 0; k1 < 4; ++k1) {
                for (int l1 = 0; l1 < 16; ++l1) {
                    double d0 = 0.125D;
                    double d1 = this.buffer[((j1) * 5 + k1) * 17 + l1];
                    double d2 = this.buffer[((j1) * 5 + k1 + 1) * 17 + l1];
                    double d3 = this.buffer[((j1 + 1) * 5 + k1) * 17 + l1];
                    double d4 = this.buffer[((j1 + 1) * 5 + k1 + 1) * 17 + l1];
                    double d5 = (this.buffer[((j1) * 5 + k1) * 17 + l1 + 1] - d1) * 0.125D;
                    double d6 = (this.buffer[((j1) * 5 + k1 + 1) * 17 + l1 + 1] - d2) * 0.125D;
                    double d7 = (this.buffer[((j1 + 1) * 5 + k1) * 17 + l1 + 1] - d3) * 0.125D;
                    double d8 = (this.buffer[((j1 + 1) * 5 + k1 + 1) * 17 + l1 + 1] - d4) * 0.125D;

                    for (int i2 = 0; i2 < 8; ++i2) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int j2 = 0; j2 < 4; ++j2) {
                            double d14 = 0.25D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * 0.25D;

                            for (int k2 = 0; k2 < 4; ++k2) {
                                IBlockState iblockstate = null;

                                if (l1 * 8 + i2 < j) {
                                    iblockstate = SwampWater;
                                }

                                if (d15 > 0.0D) {
                                    iblockstate = BTLStone;
                                }

                                int l2 = j2 + j1 * 4;
                                int i3 = i2 + l1 * 8;
                                int j3 = k2 + k1 * 4;
                                primer.setBlockState(l2, i3, j3, iblockstate);
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void buildSurfaces(int p_185937_1_, int p_185937_2_, ChunkPrimer primer) {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, p_185937_1_, p_185937_2_, primer, this.world))
            return;
        int i = this.world.getSeaLevel() + 1;
        double d0 = 0.03125D;
        this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, p_185937_1_ * 16, p_185937_2_ * 16, 0, 16, 16, 1, 0.03125D, 0.03125D, 1.0D);
        this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, p_185937_1_ * 16, 109, p_185937_2_ * 16, 16, 1, 16, 0.03125D, 1.0D, 0.03125D);
        this.depthBuffer = this.BTLStoneExculsivityNoiseGen.generateNoiseOctaves(this.depthBuffer, p_185937_1_ * 16, p_185937_2_ * 16, 0, 16, 16, 1, 0.0625D, 0.0625D, 0.0625D);

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {

                boolean flag = this.slowsandNoise[j + k * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                boolean flag1 = this.gravelNoise[j + k * 16] + this.rand.nextDouble() * 0.2D > 0.0D;

                int l = (int) (this.depthBuffer[j + k * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int i1 = -1;
                IBlockState iblockstate = BTLStone;
                IBlockState iblockstate1 = BTLStone;

                for (int j1 = 127; j1 >= 0; --j1) {
                    if (j1 < 127 - this.rand.nextInt(5) && j1 > this.rand.nextInt(5)) {
                        IBlockState iblockstate2 = primer.getBlockState(k, j1, j);

                        iblockstate2.getBlock();
                        if (iblockstate2.getMaterial() != Material.AIR) {
                            if (iblockstate2.getBlock() == BlockRegistry.BETWEENSTONE) {
                                if (i1 == -1) {
                                    if (l <= 0) {
                                        iblockstate = AIR;
                                        iblockstate1 = BTLStone;
                                    } else if (j1 >= i - 4 && j1 <= i + 1) {
                                        iblockstate = BTLStone;
                                        iblockstate1 = BTLStone;

                                        if (flag1) {
                                            iblockstate = BTLLimestone;
                                            iblockstate1 = BTLStone;
                                        }

                                        if (flag) {
                                            iblockstate = BTLCragRock;
                                            iblockstate1 = BTLPitstone;
                                        }
                                    }

                                    if (j1 < i && iblockstate.getMaterial() == Material.AIR) {
                                        iblockstate = StagnantWater;
                                    }

                                    i1 = l;

                                    if (j1 >= i - 1) {
                                        primer.setBlockState(k, j1, j, iblockstate);
                                    } else {
                                        primer.setBlockState(k, j1, j, iblockstate1);
                                    }
                                } else if (i1 > 0) {
                                    --i1;
                                    primer.setBlockState(k, j1, j, iblockstate1);
                                }
                            }
                        } else {
                            i1 = -1;
                        }
                    } else {
                        primer.setBlockState(k, j1, j, BTLBedRock);
                    }
                }
            }
        }
    }

    /**
     * Generates the chunk at the specified position, from scratch
     */
    public Chunk generateChunk(int x, int z) {
        this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.prepareHeights(x, z, chunkprimer);
        this.buildSurfaces(x, z, chunkprimer);
        this.mapGenCavesBTN.generate(this.world, x, z, chunkprimer);

        if (this.generateStructures) {
            this.mapGenBTNBridge.generate(this.world, x, z, chunkprimer);
        }

        Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
        Biome[] abiome = this.world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i) {
            abyte[i] = (byte) Biome.getIdForBiome(abiome[i]);
        }

        chunk.resetRelightChecks();
        return chunk;
    }

    private double[] getHeights(double[] heights, int xStart, int yStart, int zStart, int xSize, int ySize, int zSize) {
        if (heights == null) {
            heights = new double[xSize * ySize * zSize];
        }

        // Event handling
        ChunkGeneratorEvent.InitNoiseField event = new ChunkGeneratorEvent.InitNoiseField(this, heights, xStart, yStart, zStart, xSize, ySize, zSize);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.DENY) {
            double[] noiseField = event.getNoisefield();
            if (noiseField == null || noiseField.length != heights.length) {
                throw new IllegalStateException("Invalid noise field returned from event");
            }
            return noiseField;
        }

        double d0 = 684.412D;
        double d1 = 2053.236D;

        this.noiseData4 = this.scaleNoise.generateNoiseOctaves(this.noiseData4, xStart, yStart, zStart, xSize, 1, zSize, 1.0D, 0.0D, 1.0D);
        this.dr = this.depthNoise.generateNoiseOctaves(this.dr, xStart, yStart, zStart, xSize, 1, zSize, 100.0D, 0.0D, 100.0D);
        this.pnr = this.perlinNoise1.generateNoiseOctaves(this.pnr, xStart, yStart, zStart, xSize, ySize, zSize, 8.555150000000001D, 34.2206D, 8.555150000000001D);
        this.ar = this.lperlinNoise1.generateNoiseOctaves(this.ar, xStart, yStart, zStart, xSize, ySize, zSize, d0, d1, d0);
        this.br = this.lperlinNoise2.generateNoiseOctaves(this.br, xStart, yStart, zStart, xSize, ySize, zSize, d0, d1, d0);

        int index = 0;
        double[] adouble = new double[ySize];

        for (int j = 0; j < ySize; ++j) {
            adouble[j] = Math.cos((double) j * Math.PI * 6.0D / (double) ySize) * 2.0D;
            double d2 = j;

            if (j > ySize / 2) {
                d2 = ySize - 1 - j;
            }

            if (d2 < 4.0D) {
                d2 = 4.0D - d2;
                adouble[j] -= d2 * d2 * d2 * 10.0D;
            }
        }

        for (int x = 0; x < xSize; ++x) {
            for (int z = 0; z < zSize; ++z) {
                double sum = 0.0D;

                for (int y = 0; y < ySize; ++y) {
                    double d4 = adouble[y];
                    double d5 = this.ar[index] / 512.0D;
                    double d6 = this.br[index] / 512.0D;
                    double d7 = (this.pnr[index] / 10.0D + 1.0D) / 2.0D;
                    double d8;

                    if (d7 < 0.0D) {
                        d8 = d5;
                    } else if (d7 > 1.0D) {
                        d8 = d6;
                    } else {
                        d8 = d5 + (d6 - d5) * d7;
                    }

                    d8 = d8 - d4;

                    if (y > ySize - 4) {
                        double d9 = (float) (y - (ySize - 4)) / 3.0F;
                        d8 = d8 * (1.0D - d9) + -10.0D * d9;
                    }

                    heights[index] = d8;
                    ++index;
                }
            }
        }

        return heights;
    }

    /**
     * Generate initial structures in this chunk, e.g. mineshafts, temples, lakes, and dungeons
     *
     * @param x Chunk x coordinate
     * @param z Chunk z coordinate
     */
    public void populate(int x, int z) {
        BlockFalling.fallInstantly = true;
        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, ThreadLocalRandom.current(), x, z, false);

        int i = x * 16;
        int j = z * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
        ChunkPos chunkpos = new ChunkPos(x, z);

        this.mapGenBTNBridge.generateStructure(this.world, ThreadLocalRandom.current(), chunkpos);

        // Nether Lava Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), x, z, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_LAVA)) {
            for (int k = 0; k < 8; ++k) {
                generateFeature(this.SwampWaterGen, blockpos, ThreadLocalRandom.current());
            }
        }

        // Fire Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), x, z, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.FIRE)) {
            int count = ThreadLocalRandom.current().nextInt(ThreadLocalRandom.current().nextInt(10) + 1) + 1;
            for (int i1 = 0; i1 < count; ++i1) {
                switch (ThreadLocalRandom.current().nextInt(5)) {
                    case 0:
                        generateFeature(this.worldGenCaveGrass, blockpos, ThreadLocalRandom.current());
                        break;
                    case 1:
                        generateFeature(this.stalactite, blockpos, ThreadLocalRandom.current());
                        break;
                    case 2:
                        generateFeature(this.worldGenNesting, blockpos, ThreadLocalRandom.current());
                        break;
                    case 3:
                        generateFeature(this.worldGenFluidPool, blockpos, ThreadLocalRandom.current());
                        break;
                    case 4:
                        generateFeature(this.worldGenLavaPool, blockpos, ThreadLocalRandom.current());
                        break;
                }
            }
        }

        // Glowstone Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), x, z, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.GLOWSTONE)) {
            int count = ThreadLocalRandom.current().nextInt(ThreadLocalRandom.current().nextInt(10) + 1);
            for (int j1 = 0; j1 < count; ++j1) {
                generateFeature(this.worldGenOreStone1, blockpos, ThreadLocalRandom.current());
            }

            for (int k1 = 0; k1 < 10; ++k1) {
                generateFeature(this.worldGenOreStone2, blockpos, ThreadLocalRandom.current());
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, ThreadLocalRandom.current(), x, z, false);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Pre(this.world, ThreadLocalRandom.current(), chunkpos));

        // Shroom Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(this.world, ThreadLocalRandom.current(), chunkpos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            generateMushroomFeatures(blockpos, ThreadLocalRandom.current());
        }

        // Quartz Ore Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(this.world, ThreadLocalRandom.current(), mudGen, blockpos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.QUARTZ)) {
            for (int l1 = 0; l1 < 16; ++l1) {
                generateFeature(mudGen, blockpos, ThreadLocalRandom.current());
            }
        }

        int i2 = this.world.getSeaLevel() / 2 + 1;

        // Nether Magma Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), x, z, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_MAGMA)) {
            for (int l = 0; l < 4; ++l) {
                generateFeature(this.siltGen, blockpos, ThreadLocalRandom.current(), i2 - 5 + ThreadLocalRandom.current().nextInt(10));
            }
        }

        // Nether Lava2 Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), x, z, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_LAVA2)) {
            for (int j2 = 0; j2 < 16; ++j2) {
                int offset = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 8 : 0;
                generateFeature(this.StagnantWaterGen, blockpos, ThreadLocalRandom.current(), offset);
            }
        }

        biome.decorate(this.world, ThreadLocalRandom.current(), new BlockPos(i, 0, j));

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Post(this.world, ThreadLocalRandom.current(), blockpos));

        BlockFalling.fallInstantly = false;
    }

    private void generateFeature(WorldGenerator generator, BlockPos basePos, ThreadLocalRandom random) {
        generator.generate(this.world, random, basePos.add(random.nextInt(16) + 8, random.nextInt(120) + 4, random.nextInt(16) + 8));
    }

    private void generateFeature(WorldGenerator generator, BlockPos basePos, ThreadLocalRandom random, int yOffset) {
        generator.generate(this.world, random, basePos.add(random.nextInt(16), yOffset, random.nextInt(16)));
    }

    private void generateMushroomFeatures(BlockPos basePos, ThreadLocalRandom random) {
        for (int i = 0; i < 3; ++i) {
            if (random.nextBoolean()) {
                WorldGenerator mushroomGenerator = switch (i) {
                    case 0 -> this.MushroomFeature1;
                    case 1 -> this.MushroomFeature2;
                    case 2 -> this.MushroomFeature3;
                    default -> null;
                };
                generateFeature(mushroomGenerator, basePos, random);
            }
        }
    }

    /**
     * Called to generate additional structures after initial worldgen, used by ocean monuments
     */
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        if (creatureType == EnumCreatureType.MONSTER) {
            if (this.mapGenBTNBridge.isInsideStructure(pos)) {
                return this.mapGenBTNBridge.getSpawnList();
            }

            if (this.mapGenBTNBridge.isPositionInStructure(this.world, pos) && this.world.getBlockState(pos.down()).getBlock() == BlockRegistry.CRAGROCK_BRICKS) {
                return this.mapGenBTNBridge.getSpawnList();
            }
        }

        Biome biome = this.world.getBiome(pos);
        return biome.getSpawnableList(creatureType);
    }

    @Nullable
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return "BTNFortress".equals(structureName) && this.mapGenBTNBridge != null ? this.mapGenBTNBridge.getNearestStructurePos(worldIn, position, findUnexplored) : null;
    }

    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return "BTNFortress".equals(structureName) && this.mapGenBTNBridge != null && this.mapGenBTNBridge.isInsideStructure(pos);
    }

    /**
     * Recreates data about structures intersecting given chunk (used for example by getPossibleCreatures), without
     * placing any blocks. When called for the first time before any chunk is generated - also initializes the internal
     * state needed by getPossibleCreatures.
     */
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        this.mapGenBTNBridge.generate(this.world, x, z, null);
    }
}
