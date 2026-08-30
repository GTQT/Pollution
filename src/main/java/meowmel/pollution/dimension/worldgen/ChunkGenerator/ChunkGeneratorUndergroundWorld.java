package meowmel.pollution.dimension.worldgen.ChunkGenerator;

import gregtech.api.fluids.store.FluidStorageKeys;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.dimension.worldgen.feature.WorldGenFluidPool;
import meowmel.pollution.dimension.worldgen.feature.WorldGenGarden;
import meowmel.pollution.dimension.worldgen.feature.WorldGenMushroomBlockCluster;
import meowmel.pollution.dimension.worldgen.feature.WorldGenScatteredBlock;
import meowmel.pollution.dimension.worldgen.feature.WorldGenStalactite;
import meowmel.pollution.dimension.worldgen.feature.WorldGenUndergroundWater;
import meowmel.pollution.dimension.worldgen.mapgen.MapGenCavesUnderground;
import meowmel.pollution.dimension.worldgen.mapgen.MapGenUndergroundBridge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
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
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.InitNoiseGensEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static meowmel.pollution.dimension.worldgen.terraingen.InitMapGenEvent.EventType.UNDERGROUND;
import static meowmel.pollution.dimension.worldgen.terraingen.InitMapGenEvent.EventType.UNDERGROUND_CAVE;
import static meowmel.pollution.dimension.worldgen.terraingen.TerrainGen.getModdedMapGen;

/**
 * 地下世界区块生成器：噪声地形 + 洞穴 + 地下堡垒/废弃矿井。
 */
public class ChunkGeneratorUndergroundWorld implements IChunkGenerator {

    // ===== 基础方块 =====
    private static final IBlockState STONE = Blocks.STONE.getDefaultState();
    private static final IBlockState SWAMP_WATER = Blocks.WATER.getDefaultState();
    private static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();

    // ===== 噪声生成器（构造器中可能被 mod 事件替换，故非 final） =====
    private final World world;
    private final boolean generateStructures;
    private final Random rand;
    private NoiseGeneratorOctaves lowFreqNoise1;
    private NoiseGeneratorOctaves lowFreqNoise2;
    private NoiseGeneratorOctaves perlinNoise1;
    private NoiseGeneratorOctaves slowsandGravelNoiseGen;
    private NoiseGeneratorOctaves stoneExclusivityNoiseGen;
    private NoiseGeneratorOctaves scaleNoise;
    private NoiseGeneratorOctaves depthNoise;

    // 噪声输出 buffer（跨 chunk 复用，避免反复分配）
    private double[] scaleNoiseData;
    private double[] depthNoiseData;
    private double[] mainNoiseData;
    private double[] lowFreqNoise1Data;
    private double[] lowFreqNoise2Data;
    private double[] heightNoiseBuffer;
    private double[] gravelNoiseBuffer;

    // ===== 结构生成器 =====
    private final MapGenUndergroundBridge undergroundBridgeGen = new MapGenUndergroundBridge();
    private final MapGenMineshaft mineshaftGen = new MapGenMineshaft();
    private final MapGenBase caveGen;

    // ===== 装饰生成器 =====
    private final WorldGenStalactite stalactiteGen = new WorldGenStalactite();
    private final WorldGenMushroomBlockCluster brownMushroomCluster = new WorldGenMushroomBlockCluster(Blocks.BROWN_MUSHROOM_BLOCK);
    private final WorldGenMushroomBlockCluster redMushroomCluster = new WorldGenMushroomBlockCluster(Blocks.RED_MUSHROOM_BLOCK);
    private final WorldGenerator gravelGen = new WorldGenMinable(Blocks.GRAVEL.getDefaultState(), 33, BlockMatcher.forBlock(Blocks.STONE));
    private final WorldGenUndergroundWater stagnantWaterGen = new WorldGenUndergroundWater(Blocks.WATER, true);
    private final WorldGenUndergroundWater swampWaterGen = new WorldGenUndergroundWater(Blocks.WATER, false);
    private final WorldGenScatteredBlock redMushroomFeature = new WorldGenScatteredBlock(Blocks.RED_MUSHROOM, 64);
    private final WorldGenScatteredBlock brownMushroomFeature = new WorldGenScatteredBlock(Blocks.BROWN_MUSHROOM, 64);
    private final WorldGenGarden gardenGen = new WorldGenGarden();
    private final WorldGenScatteredBlock caveGrassGen = new WorldGenScatteredBlock(Blocks.TALLGRASS, 48);
    private final WorldGenScatteredBlock caveLeavesGen = new WorldGenScatteredBlock(Blocks.LEAVES, 48);
    private final WorldGenFluidPool waterPoolGen = new WorldGenFluidPool(Blocks.WATER);
    private final WorldGenFluidPool lavaPoolGen = new WorldGenFluidPool(Blocks.LAVA);
    private final WorldGenFluidPool tarPoolGen = new WorldGenFluidPool(PollutionMaterials.PureTar.getFluid(FluidStorageKeys.LIQUID).getBlock());

    private static final int WATER_LEVEL = 63;

    public ChunkGeneratorUndergroundWorld(World worldIn, boolean generateStructures, long seed) {
        this.world = worldIn;
        this.generateStructures = generateStructures;
        this.rand = new Random(seed);

        this.lowFreqNoise1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.lowFreqNoise2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.perlinNoise1 = new NoiseGeneratorOctaves(this.rand, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.rand, 4);
        this.stoneExclusivityNoiseGen = new NoiseGeneratorOctaves(this.rand, 4);
        this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);

        worldIn.setSeaLevel(WATER_LEVEL);

        // 允许其他模组通过事件替换噪声生成器与地图生成器
        InitNoiseGensEvent.ContextHell ctx = new InitNoiseGensEvent.ContextHell(
                lowFreqNoise1, lowFreqNoise2, perlinNoise1, slowsandGravelNoiseGen,
                stoneExclusivityNoiseGen, scaleNoise, depthNoise);
        ctx = TerrainGen.getModdedNoiseGenerators(worldIn, this.rand, ctx);
        this.lowFreqNoise1 = ctx.getLPerlin1();
        this.lowFreqNoise2 = ctx.getLPerlin2();
        this.perlinNoise1 = ctx.getPerlin();
        this.slowsandGravelNoiseGen = ctx.getPerlin2();
        this.stoneExclusivityNoiseGen = ctx.getPerlin3();
        this.scaleNoise = ctx.getScale();
        this.depthNoise = ctx.getDepth();

        this.caveGen = getModdedMapGen(new MapGenCavesUnderground(), UNDERGROUND_CAVE);
        getModdedMapGen(this.undergroundBridgeGen, UNDERGROUND);
    }

    // ===== 地形高度 =====

    private void prepareHeights(int chunkX, int chunkZ, ChunkPrimer primer) {
        int waterLevel = this.world.getSeaLevel();
        this.heightNoiseBuffer = this.getHeights(this.heightNoiseBuffer, chunkX * 4, 0, chunkZ * 4, 5, 17, 5);

        final double interpolationFactorY = 0.0625D; // 1/16，垂直插值步长

        for (int xIndex = 0; xIndex < 4; ++xIndex) {
            for (int zIndex = 0; zIndex < 4; ++zIndex) {
                for (int yIndex = 0; yIndex < 16; ++yIndex) {
                    double cornerNoise1 = this.heightNoiseBuffer[((xIndex) * 5 + zIndex) * 17 + yIndex];
                    double cornerNoise2 = this.heightNoiseBuffer[((xIndex) * 5 + zIndex + 1) * 17 + yIndex];
                    double cornerNoise3 = this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex) * 17 + yIndex];
                    double cornerNoise4 = this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex + 1) * 17 + yIndex];

                    double deltaY1 = (this.heightNoiseBuffer[((xIndex) * 5 + zIndex) * 17 + yIndex + 1] - cornerNoise1) * interpolationFactorY;
                    double deltaY2 = (this.heightNoiseBuffer[((xIndex) * 5 + zIndex + 1) * 17 + yIndex + 1] - cornerNoise2) * interpolationFactorY;
                    double deltaY3 = (this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex) * 17 + yIndex + 1] - cornerNoise3) * interpolationFactorY;
                    double deltaY4 = (this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex + 1) * 17 + yIndex + 1] - cornerNoise4) * interpolationFactorY;

                    for (int subYIndex = 0; subYIndex < 16; ++subYIndex) {
                        double xInterpolated1 = cornerNoise1;
                        double xInterpolated2 = cornerNoise2;
                        double xDelta1 = (cornerNoise3 - cornerNoise1) * 0.25D;
                        double xDelta2 = (cornerNoise4 - cornerNoise2) * 0.25D;

                        for (int subXIndex = 0; subXIndex < 4; ++subXIndex) {
                            double zInterpolated = xInterpolated1;
                            double zDelta = (xInterpolated2 - xInterpolated1) * 0.25D;

                            for (int subZIndex = 0; subZIndex < 4; ++subZIndex) {
                                IBlockState blockState = null;
                                int currentHeight = subYIndex + yIndex * 16;

                                // 噪声达到阈值则生成石头，否则保持空洞（低于海平面时填水）
                                if (zInterpolated > -0.2D) {
                                    blockState = STONE;
                                }

                                // 噪声空洞且低于海平面：填充水
                                if (blockState == null && currentHeight < waterLevel) {
                                    blockState = SWAMP_WATER;
                                }

                                if (blockState != null) {
                                    primer.setBlockState(subXIndex + xIndex * 4, currentHeight, subZIndex + zIndex * 4, blockState);
                                }

                                zInterpolated += zDelta;
                            }

                            xInterpolated1 += xDelta1;
                            xInterpolated2 += xDelta2;
                        }

                        cornerNoise1 += deltaY1;
                        cornerNoise2 += deltaY2;
                        cornerNoise3 += deltaY3;
                        cornerNoise4 += deltaY4;
                    }
                }
            }
        }
    }

    private void buildSurfaces(int chunkX, int chunkZ, ChunkPrimer primer) {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, this.world)) {
            return;
        }

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                // 基岩层：底部 2 层 + 顶部 2 层
                primer.setBlockState(localZ, 0, localX, Blocks.BEDROCK.getDefaultState());
                primer.setBlockState(localZ, 1, localX, Blocks.BEDROCK.getDefaultState());
                primer.setBlockState(localZ, 254, localX, Blocks.BEDROCK.getDefaultState());
                primer.setBlockState(localZ, 255, localX, Blocks.BEDROCK.getDefaultState());
            }
        }
    }

    /**
     * 水体附近的石头替换为沙砾：湖床覆盖 2-4 层沙砾，水面附近的湖岸石壁单层沙砾，
     * 模拟真实湖泊的沉积效果。
     */
    private void replaceStoneNearWater(int chunkX, int chunkZ, ChunkPrimer primer) {
        int waterLevel = this.world.getSeaLevel();

        // 每列一个噪声值，决定湖床沙砾层厚度（2-4 层）
        this.gravelNoiseBuffer = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoiseBuffer,
                chunkX * 16, 0, chunkZ * 16, 16, 1, 16, 0.125D, 1.0D, 0.125D);

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int gravelDepth = 2 + (int) (Math.abs(this.gravelNoiseBuffer[localX * 16 + localZ]) / 6.0D);
                gravelDepth = Math.min(gravelDepth, 4);

                // 湖床：自上而下穿过水体后遇到的第一层石头开始，向下替换为沙砾
                boolean inWater = false;
                for (int y = waterLevel - 1; y > 2; --y) {
                    IBlockState state = primer.getBlockState(localZ, y, localX);
                    if (state.getBlock() == Blocks.WATER) {
                        inWater = true;
                    } else if (state.getBlock() == Blocks.STONE) {
                        if (inWater) {
                            for (int d = 0; d < gravelDepth; ++d) {
                                if (primer.getBlockState(localZ, y - d, localX).getBlock() != Blocks.STONE) {
                                    break;
                                }
                                primer.setBlockState(localZ, y - d, localX, GRAVEL);
                            }
                        }
                        break;
                    }
                }

                // 湖岸：水面附近紧邻水体或洞穴空气的石头替换为沙砾（边缘列无法做邻接检测，跳过）
                if (localX == 0 || localX == 15 || localZ == 0 || localZ == 15) {
                    continue;
                }
                for (int y = waterLevel - 4; y <= waterLevel + 3; ++y) {
                    if (primer.getBlockState(localZ, y, localX).getBlock() != Blocks.STONE) {
                        continue;
                    }
                    if (isAdjacentToWaterOrAir(primer, localZ, y, localX)) {
                        primer.setBlockState(localZ, y, localX, GRAVEL);
                    }
                }
            }
        }
    }

    private static boolean isAdjacentToWaterOrAir(ChunkPrimer primer, int primerX, int y, int primerZ) {
        return isWaterOrAir(primer.getBlockState(primerX - 1, y, primerZ))
                || isWaterOrAir(primer.getBlockState(primerX + 1, y, primerZ))
                || isWaterOrAir(primer.getBlockState(primerX, y, primerZ - 1))
                || isWaterOrAir(primer.getBlockState(primerX, y, primerZ + 1));
    }

    private static boolean isWaterOrAir(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.WATER || block == Blocks.AIR;
    }

    // ===== 主生成入口 =====

    @Override
    public @NotNull Chunk generateChunk(int x, int z) {
        this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        this.prepareHeights(x, z, chunkPrimer);
        this.buildSurfaces(x, z, chunkPrimer);
        this.replaceStoneNearWater(x, z, chunkPrimer);
        this.caveGen.generate(this.world, x, z, chunkPrimer);

        if (this.generateStructures) {
            // 每个区块随机决定生成地下堡垒或废弃矿井（与 populate 阶段的结构生成配合）
            if (this.rand.nextBoolean()) {
                this.undergroundBridgeGen.generate(this.world, x, z, chunkPrimer);
            } else {
                this.mineshaftGen.generate(this.world, x, z, chunkPrimer);
            }
        }

        Chunk chunk = new Chunk(this.world, chunkPrimer, x, z);
        Biome[] biomes = this.world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte) Biome.getIdForBiome(biomes[i]);
        }

        chunk.resetRelightChecks();
        return chunk;
    }

    private double[] getHeights(double[] noiseData, int startX, int startY, int startZ, int width, int height, int depth) {
        if (noiseData == null) {
            noiseData = new double[width * height * depth];
        }

        ChunkGeneratorEvent.InitNoiseField event = new ChunkGeneratorEvent.InitNoiseField(this, noiseData, startX, startY, startZ, width, height, depth);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.DENY) {
            double[] customNoise = event.getNoisefield();
            if (customNoise == null || customNoise.length != noiseData.length) {
                throw new IllegalStateException("Invalid noise field returned from event");
            }
            return customNoise;
        }

        double amplitude1 = 684.412D;
        double amplitude2 = 2053.236D;

        this.scaleNoiseData = this.scaleNoise.generateNoiseOctaves(this.scaleNoiseData, startX, startY, startZ, width, 1, depth, 1.0D, 0.0D, 1.0D);
        this.depthNoiseData = this.depthNoise.generateNoiseOctaves(this.depthNoiseData, startX, startY, startZ, width, 1, depth, 100.0D, 0.0D, 100.0D);
        this.mainNoiseData = this.perlinNoise1.generateNoiseOctaves(this.mainNoiseData, startX, startY, startZ, width, height, depth, 8.555150000000001D, 34.2206D, 8.555150000000001D);
        this.lowFreqNoise1Data = this.lowFreqNoise1.generateNoiseOctaves(this.lowFreqNoise1Data, startX, startY, startZ, width, height, depth, amplitude1, amplitude2, amplitude1);
        this.lowFreqNoise2Data = this.lowFreqNoise2.generateNoiseOctaves(this.lowFreqNoise2Data, startX, startY, startZ, width, height, depth, amplitude1, amplitude2, amplitude1);

        // 垂直权重：中间平滑、顶部收窄，保证 256 格高度连续
        double[] cosineWeights = new double[height];
        for (int yIndex = 0; yIndex < height; ++yIndex) {
            cosineWeights[yIndex] = Math.cos(yIndex * Math.PI * 6.0D / (height - 1)) * 2.0D;
            double distanceFromCenter = yIndex;
            if (yIndex > (height - 1) / 2) {
                distanceFromCenter = (height - 1) - yIndex;
            }
            if (distanceFromCenter < 4.0D) {
                distanceFromCenter = 4.0D - distanceFromCenter;
                cosineWeights[yIndex] -= distanceFromCenter * distanceFromCenter * distanceFromCenter * 10.0D;
            }
        }

        int currentIndex = 0;
        for (int xIndex = 0; xIndex < width; ++xIndex) {
            for (int zIndex = 0; zIndex < depth; ++zIndex) {
                for (int yIndex = 0; yIndex < height; ++yIndex) {
                    double weight = cosineWeights[yIndex];
                    double lowFreq1 = this.lowFreqNoise1Data[currentIndex] / 512.0D;
                    double lowFreq2 = this.lowFreqNoise2Data[currentIndex] / 512.0D;
                    double blendFactor = (this.mainNoiseData[currentIndex] / 10.0D + 1.0D) / 2.0D;

                    double heightValue;
                    if (blendFactor < 0.0D) {
                        heightValue = lowFreq1;
                    } else if (blendFactor > 1.0D) {
                        heightValue = lowFreq2;
                    } else {
                        heightValue = lowFreq1 + (lowFreq2 - lowFreq1) * blendFactor;
                    }
                    heightValue -= weight;

                    // 顶部 4 个采样点平滑收窄
                    if (yIndex > height - 4) {
                        double edgeBlendFactor = (yIndex - (height - 4)) / 3.0F;
                        heightValue = heightValue * (1.0D - edgeBlendFactor) - 10.0D * edgeBlendFactor;
                    }

                    noiseData[currentIndex] = heightValue;
                    ++currentIndex;
                }
            }
        }
        return noiseData;
    }

    // ===== 装饰阶段 =====

    @Override
    public void populate(int chunkX, int chunkZ) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BlockFalling.fallInstantly = true;
        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, random, chunkX, chunkZ, false);

        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;
        BlockPos chunkOrigin = new BlockPos(blockX, 0, blockZ);
        Biome currentBiome = this.world.getBiome(chunkOrigin.add(16, 0, 16));
        ChunkPos chunkPosition = new ChunkPos(chunkX, chunkZ);

        this.undergroundBridgeGen.generateStructure(this.world, random, chunkPosition);
        this.mineshaftGen.generateStructure(this.world, random, chunkPosition);

        // 地下湖泊（水体集群）
        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.NETHER_LAVA)) {
            for (int i = 0; i < 8; ++i) {
                generateFeature(this.swampWaterGen, chunkOrigin, random);
            }
        }

        // 洞穴装饰（钟乳石/树叶/水池/岩浆池/焦油池/花园等随机一种）
        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.FIRE)) {
            int featureCount = random.nextInt(random.nextInt(10) + 1) + 1;
            for (int i = 0; i < featureCount; ++i) {
                switch (random.nextInt(7)) {
                    case 0 -> generateFeature(this.caveGrassGen, chunkOrigin, random);
                    case 1 -> generateFeature(this.stalactiteGen, chunkOrigin, random);
                    case 2 -> generateFeature(this.caveLeavesGen, chunkOrigin, random);
                    case 3 -> generateFeature(this.waterPoolGen, chunkOrigin, random);
                    case 4 -> generateFeature(this.lavaPoolGen, chunkOrigin, random);
                    case 5 -> generateFeature(this.tarPoolGen, chunkOrigin, random);
                    default -> generateFeature(this.gardenGen, chunkOrigin, random);
                }
            }
        }

        // 蘑菇灯集群
        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.GLOWSTONE)) {
            int clusterCount = random.nextInt(random.nextInt(10) + 1);
            for (int i = 0; i < clusterCount; ++i) {
                generateFeature(this.brownMushroomCluster, chunkOrigin, random);
            }
            for (int i = 0; i < 10; ++i) {
                generateFeature(this.redMushroomCluster, chunkOrigin, random);
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, random, chunkX, chunkZ, false);
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(this.world, random, chunkPosition));

        // 蘑菇特征
        if (TerrainGen.decorate(this.world, random, chunkPosition, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            generateMushroomFeatures(chunkOrigin, random);
        }

        // 沙砾集群
        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.NETHER_MAGMA)) {
            for (int i = 0; i < 4; ++i) {
                generateFeature(this.gravelGen, chunkOrigin, random, 120 + random.nextInt(100));
            }
        }

        // 死水集群
        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.NETHER_LAVA2)) {
            for (int i = 0; i < 16; ++i) {
                int yOffset = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 8 : 0;
                generateFeature(this.stagnantWaterGen, chunkOrigin, random, yOffset);
            }
        }

        currentBiome.decorate(this.world, random, new BlockPos(blockX, 0, blockZ));
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(this.world, random, chunkOrigin));

        BlockFalling.fallInstantly = false;
    }

    private void generateFeature(WorldGenerator generator, BlockPos basePos, Random random) {
        generator.generate(this.world, random, basePos.add(random.nextInt(16) + 8, random.nextInt(240) + 4, random.nextInt(16) + 8));
    }

    private void generateFeature(WorldGenerator generator, BlockPos basePos, Random random, int yOffset) {
        generator.generate(this.world, random, basePos.add(random.nextInt(16), yOffset, random.nextInt(16)));
    }

    private void generateMushroomFeatures(BlockPos basePos, Random random) {
        if (random.nextBoolean()) {
            generateFeature(this.redMushroomFeature, basePos, random);
        }
        if (random.nextBoolean()) {
            generateFeature(this.brownMushroomFeature, basePos, random);
        }
    }

    // ===== 结构查询 =====

    @Override
    public boolean generateStructures(@NotNull Chunk chunkIn, int x, int z) {
        boolean generated = false;

        // 废弃矿井（原版逻辑）
        if (this.rand.nextInt(80) == 0) {
            this.mineshaftGen.generateStructure(this.world, this.rand, new ChunkPos(x, z));
            generated = true;
        }

        if (this.undergroundBridgeGen.generateStructure(this.world, this.rand, new ChunkPos(x, z))) {
            generated = true;
        }
        return generated;
    }

    @Override
    public @NotNull List<Biome.SpawnListEntry> getPossibleCreatures(@NotNull EnumCreatureType creatureType, @NotNull BlockPos pos) {
        if (creatureType == EnumCreatureType.MONSTER) {
            if (this.undergroundBridgeGen.isInsideStructure(pos)
                    || (this.undergroundBridgeGen.isPositionInStructure(this.world, pos)
                    && this.world.getBlockState(pos.down()).getBlock() == Blocks.STONEBRICK)) {
                return this.undergroundBridgeGen.getSpawnList();
            }
        }
        return this.world.getBiome(pos).getSpawnableList(creatureType);
    }

    @Override
    @Nullable
    public BlockPos getNearestStructurePos(@NotNull World worldIn, @NotNull String structureName, @NotNull BlockPos position, boolean findUnexplored) {
        if ("UndergroundFortress".equals(structureName)) {
            return this.undergroundBridgeGen.getNearestStructurePos(worldIn, position, findUnexplored);
        } else if ("Mineshaft".equals(structureName)) {
            return this.mineshaftGen.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        return null;
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        if ("UndergroundFortress".equals(structureName)) {
            return this.undergroundBridgeGen.isInsideStructure(pos);
        } else if ("Mineshaft".equals(structureName)) {
            return this.mineshaftGen.isInsideStructure(pos);
        }
        return false;
    }

    @Override
    public void recreateStructures(@NotNull Chunk chunkIn, int x, int z) {
        this.undergroundBridgeGen.generate(this.world, x, z, null);
        this.mineshaftGen.generate(this.world, x, z, null);
    }
}
