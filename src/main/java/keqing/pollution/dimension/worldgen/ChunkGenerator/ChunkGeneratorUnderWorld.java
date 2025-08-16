package keqing.pollution.dimension.worldgen.ChunkGenerator;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.Materials;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.StoneVariantBlock;
import keqing.gtqtcore.common.block.GTQTMetaBlocks;
import keqing.gtqtcore.common.block.blocks.GTQTStoneVariantBlock;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.dimension.worldgen.mapGen.*;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockStone;
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
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static keqing.pollution.dimension.worldgen.terraingen.InitMapGenEvent.EventType.BTN;
import static keqing.pollution.dimension.worldgen.terraingen.InitMapGenEvent.EventType.BTN_CAVE;
import static keqing.pollution.dimension.worldgen.terraingen.TerrainGen.getModdedMapGen;

public class ChunkGeneratorUnderWorld implements IChunkGenerator {
    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
    protected static final IBlockState Stone = Blocks.STONE.getDefaultState();
    protected static final IBlockState SwampWater = Blocks.WATER.getDefaultState();
    private static IBlockState gtStoneState(GTQTStoneVariantBlock.StoneType stoneType) {
        return GTQTMetaBlocks.GTQT_STONE_BLOCKS.get(GTQTStoneVariantBlock.StoneVariant.SMOOTH).getState(stoneType);
    }
    private static final IBlockState Kimberlite = gtStoneState(GTQTStoneVariantBlock.StoneType.KIMBERLITE);

    private static final IBlockState Gabbro = gtStoneState(GTQTStoneVariantBlock.StoneType.GABBRO);
    private static final IBlockState Limestone = gtStoneState(GTQTStoneVariantBlock.StoneType.LIMESTONE);
    private static final IBlockState Phyllite = gtStoneState(GTQTStoneVariantBlock.StoneType.PHYLLITE);
    private static final IBlockState Quartzite = gtStoneState(GTQTStoneVariantBlock.StoneType.QUARTZITE);
    private static final IBlockState Soapstone = gtStoneState(GTQTStoneVariantBlock.StoneType.SOAPSTONE);
    private static final IBlockState Gneiss = gtStoneState(GTQTStoneVariantBlock.StoneType.GNEISS);
    private static final IBlockState Shale = gtStoneState(GTQTStoneVariantBlock.StoneType.SHALE);
    private static final IBlockState Slate = gtStoneState(GTQTStoneVariantBlock.StoneType.SLATE);

    private static final IBlockState Andesite = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);
    private static final IBlockState Diorite = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
    private static final IBlockState Granite = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE);
    private final World world;
    private final boolean generateStructures;
    private final Random rand;
    //钟乳石集群
    private final WorldGenStalactite stalactite = new WorldGenStalactite();
    //蘑菇灯集群
    private final WorldGenOreStone1 worldGenOreStone1 = new WorldGenOreStone1();
    private final WorldGenOreStone2 worldGenOreStone2 = new WorldGenOreStone2();
    //矿物集群
    private final WorldGenerator mudGen = new WorldGenMinable(Blocks.DIRT.getDefaultState(), 14, BlockMatcher.forBlock(Blocks.DIRT));
    private final WorldGenerator siltGen = new WorldGenMinable(Blocks.GRAVEL.getDefaultState(), 33, BlockMatcher.forBlock(Blocks.GRAVEL));
    //流体集群
    private final WorldGenBNTWater StagnantWaterGen = new WorldGenBNTWater(Blocks.WATER, true);
    private final WorldGenBNTWater SwampWaterGen = new WorldGenBNTWater(Blocks.WATER, false);
    //小蘑菇集群
    private final WorldGenMushroom MushroomFeature1 = new WorldGenMushroom(Blocks.RED_MUSHROOM);
    private final WorldGenMushroom MushroomFeature2 = new WorldGenMushroom(Blocks.BROWN_MUSHROOM);
    private final WorldGenGarden garden = new WorldGenGarden();
    //洞穴草集群
    private final WorldGenSingle worldGenCaveGrass = new WorldGenSingle(Blocks.TALLGRASS);
    //洞穴落叶集群
    private final WorldGenSingle worldGenNesting = new WorldGenSingle(Blocks.LEAVES);
    //洞穴水集群
    private final WorldGenFluidPool worldGenFluidPool = new WorldGenFluidPool(Blocks.WATER);
    private final WorldGenFluidPool worldGenLavaPool = new WorldGenFluidPool(Blocks.LAVA);
    private final WorldGenFluidPool worldGenTarPool = new WorldGenFluidPool(PollutionMaterials.pure_tar.getFluid(FluidStorageKeys.LIQUID).getBlock());

    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    double[] pnr;
    double[] ar;
    double[] br;
    double[] noiseData4;
    double[] dr;
    // 新增字段
    private final MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
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

    public ChunkGeneratorUnderWorld(World worldIn, boolean p_i45637_2_, long seed) {
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

    public void prepareHeights(int chunkX, int chunkZ, ChunkPrimer primer) {
        int xSections = 4;
        // 使用完整海平面高度（通常为63）
        int waterLevel = this.world.getSeaLevel();
        int noiseResolutionX = 5;
        // 调整为17个采样点（覆盖256格高度）
        int noiseResolutionY = 17;
        int noiseResolutionZ = 5;

        // 获取高度数据（Y轴采样点增加到17）
        this.buffer = this.getHeights(this.buffer, chunkX * 4, 0, chunkZ * 4, 5, 17, 5);

        for (int xIndex = 0; xIndex < 4; ++xIndex) {
            for (int zIndex = 0; zIndex < 4; ++zIndex) {
                // 扩展Y循环范围：0-15（覆盖256格高度）
                for (int yIndex = 0; yIndex < 16; ++yIndex) {
                    double interpolationFactorY = 0.125D;
                    // 注意：Y轴采样点现在是17个（索引0-16）
                    double cornerNoiseValue1 = this.buffer[((xIndex) * 5 + zIndex) * 17 + yIndex];
                    double cornerNoiseValue2 = this.buffer[((xIndex) * 5 + zIndex + 1) * 17 + yIndex];
                    double cornerNoiseValue3 = this.buffer[((xIndex + 1) * 5 + zIndex) * 17 + yIndex];
                    double cornerNoiseValue4 = this.buffer[((xIndex + 1) * 5 + zIndex + 1) * 17 + yIndex];

                    double deltaY1 = (this.buffer[((xIndex) * 5 + zIndex) * 17 + yIndex + 1] - cornerNoiseValue1) * 0.125D;
                    double deltaY2 = (this.buffer[((xIndex) * 5 + zIndex + 1) * 17 + yIndex + 1] - cornerNoiseValue2) * 0.125D;
                    double deltaY3 = (this.buffer[((xIndex + 1) * 5 + zIndex) * 17 + yIndex + 1] - cornerNoiseValue3) * 0.125D;
                    double deltaY4 = (this.buffer[((xIndex + 1) * 5 + zIndex + 1) * 17 + yIndex + 1] - cornerNoiseValue4) * 0.125D;

                    // 扩展子高度循环：0-15（每个yIndex处理16格高度）
                    for (int subYIndex = 0; subYIndex < 16; ++subYIndex) {
                        double interpolationFactorX = 0.25D;
                        double xInterpolatedValue1 = cornerNoiseValue1;
                        double xInterpolatedValue2 = cornerNoiseValue2;
                        double xDeltaValue1 = (cornerNoiseValue3 - cornerNoiseValue1) * 0.25D;
                        double xDeltaValue2 = (cornerNoiseValue4 - cornerNoiseValue2) * 0.25D;

                        for (int subXIndex = 0; subXIndex < 4; ++subXIndex) {
                            double interpolationFactorZ = 0.25D;
                            double zInterpolatedValue = xInterpolatedValue1;
                            double zDeltaValue = (xInterpolatedValue2 - xInterpolatedValue1) * 0.25D;

                            for (int subZIndex = 0; subZIndex < 4; ++subZIndex) {
                                IBlockState iblockstate = null;

                                // 计算当前实际高度（0-255范围）
                                int currentHeight = subYIndex + yIndex * 16;

                                // 水面以下生成水（使用完整海平面）
                                if (currentHeight < waterLevel) {
                                    iblockstate = SwampWater;
                                }

                                // 噪声值大于0生成石头（覆盖整个高度范围）
                                if (zInterpolatedValue > 0.0D) {
                                    //如何在水平面以下使用黑色变种石头
                                    if(currentHeight < waterLevel - 1) iblockstate = Kimberlite;
                                    else if(currentHeight > waterLevel+5) iblockstate = Stone;
                                    else {
                                        if (zInterpolatedValue > 0.7D)iblockstate = Stone;
                                        else iblockstate = Kimberlite;
                                    }
                                }

                                int finalX = subXIndex + xIndex * 4;
                                int finalY = currentHeight; // 直接使用计算出的高度
                                int finalZ = subZIndex + zIndex * 4;
                                primer.setBlockState(finalX, finalY, finalZ, iblockstate);
                                zInterpolatedValue += zDeltaValue;
                            }

                            xInterpolatedValue1 += xDeltaValue1;
                            xInterpolatedValue2 += xDeltaValue2;
                        }

                        cornerNoiseValue1 += deltaY1;
                        cornerNoiseValue2 += deltaY2;
                        cornerNoiseValue3 += deltaY3;
                        cornerNoiseValue4 += deltaY4;
                    }
                }
            }
        }
    }


    public void buildSurfaces(int chunkX, int chunkZ, ChunkPrimer primer) {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, this.world))
            return;

        final int seaLevel = this.world.getSeaLevel();
        final double noiseScale = 0.03125D;

        // 预计算噪声数据（保持不变）
        this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(
                this.slowsandNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, noiseScale, noiseScale, 1.0D
        );
        this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(
                this.gravelNoise, chunkX * 16, 109, chunkZ * 16, 16, 1, 16, noiseScale, 1.0D, noiseScale
        );
        this.depthBuffer = this.BTLStoneExculsivityNoiseGen.generateNoiseOctaves(
                this.depthBuffer, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, 0.0625D, 0.0625D, 0.0625D
        );

        // 为每个位置预计算随机值（保持不变）
        double[] randSlowSand = new double[256];
        double[] randGravel = new double[256];
        double[] randDepth = new double[256];
        for (int i = 0; i < 256; i++) {
            randSlowSand[i] = this.rand.nextDouble() * 0.2D;
            randGravel[i] = this.rand.nextDouble() * 0.2D;
            randDepth[i] = this.rand.nextDouble() * 0.25D;
        }

        // 生成10个随机高度位置（0-255）及其配置
        int[] variantHeights = new int[10];
        int[] variantLayers = new int[10];
        IBlockState[] variantBlocks = new IBlockState[10];

        for (int i = 0; i < 10; i++) {
            // 随机高度（0-255）
            variantHeights[i] = this.rand.nextInt(256);
            // 随机层数（2-3层）
            variantLayers[i] = 2 + this.rand.nextInt(2);
            // 随机选择石头变种
            int variant = this.rand.nextInt(11);
            variantBlocks[i] = switch (variant) {
                case 0 -> Andesite;
                case 1 -> Diorite;
                case 2 -> Granite;
                case 3 -> Gabbro;
                case 4 -> Limestone;
                case 5 -> Quartzite;
                case 6 -> Phyllite;
                case 7 -> Gneiss;
                case 8 -> Slate;
                case 9 -> Soapstone;
                case 10 -> Shale;
                default -> Blocks.STONE.getDefaultState();
            };
        }

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                final int index = localX + localZ * 16;

                boolean hasSlowSand = this.slowsandNoise[index] + randSlowSand[index] > 0.0D;
                boolean hasGravel = this.gravelNoise[index] + randGravel[index] > 0.0D;
                int stoneLayerDepth = (int) (this.depthBuffer[index] / 3.0D + 3.0D + randDepth[index]);
                int currentStoneLayer = -1;

                IBlockState topBlock = Blocks.STONE.getDefaultState();
                IBlockState fillerBlock = Blocks.STONE.getDefaultState();

                for (int yPos = 255; yPos >= 0; --yPos) {
                    // 基岩层处理（保持不变）
                    if (yPos <= 4 || yPos >= 248) {
                        primer.setBlockState(localZ, yPos, localX, Blocks.BEDROCK.getDefaultState());
                        continue;
                    }

                    IBlockState currentBlock = primer.getBlockState(localZ, yPos, localX);

                    if (currentBlock.getMaterial() != Material.AIR) {
                        if (currentBlock.getBlock() == Blocks.STONE) {
                            if (currentStoneLayer == -1) {
                                currentStoneLayer = stoneLayerDepth;

                                // 地表特殊覆盖层（保持不变）
                                if (yPos >= seaLevel - 4 && yPos <= seaLevel + 1) {
                                    if (hasGravel) {
                                        topBlock = Blocks.STONE.getDefaultState();
                                        fillerBlock = Blocks.CLAY.getDefaultState();
                                    }
                                    if (hasSlowSand) {
                                        topBlock = Blocks.SAND.getDefaultState();
                                        fillerBlock = Blocks.GRAVEL.getDefaultState();
                                    }
                                }

                                // 水下处理（保持不变）
                                if (yPos < seaLevel && (topBlock.getMaterial() == Material.AIR || topBlock == Blocks.STONE.getDefaultState())) {
                                    topBlock = Blocks.WATER.getDefaultState();
                                }

                                // 设置顶层方块
                                primer.setBlockState(localZ, yPos, localX,
                                        (yPos >= seaLevel - 1) ? topBlock : fillerBlock);
                            } else if (currentStoneLayer > 0) {
                                --currentStoneLayer;
                                primer.setBlockState(localZ, yPos, localX, fillerBlock);
                            }
                        }
                    } else {
                        currentStoneLayer = -1;
                    }
                }

                // 应用岩石变种层（新逻辑）
                for (int i = 0; i < 10; i++) {
                    int startY = variantHeights[i];
                    int layers = variantLayers[i];
                    IBlockState blockType = variantBlocks[i];

                    for (int layer = 0; layer < layers; layer++) {
                        int yPos = startY + layer;
                        if (yPos > 250) continue; // 确保不超出上限

                        // 仅替换石头方块（不影响基岩/沙子/沙砾等）
                        if (primer.getBlockState(localZ, yPos, localX).getBlock() == Blocks.STONE) {
                            primer.setBlockState(localZ, yPos, localX, blockType);
                        }
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
            Random rand = new Random();
            if (rand.nextBoolean()) {
                this.mapGenBTNBridge.generate(this.world, x, z, chunkprimer);
            } else {
                this.mineshaftGenerator.generate(this.world, x, z, chunkprimer);
            }
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

    private double[] getHeights(double[] noiseData, int startX, int startY, int startZ, int width, int height, int depth) {
        if (noiseData == null) {
            noiseData = new double[width * height * depth];
        }

        // 事件处理（保持不变）
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

        this.noiseData4 = this.scaleNoise.generateNoiseOctaves(this.noiseData4, startX, startY, startZ, width, 1, depth, 1.0D, 0.0D, 1.0D);
        this.dr = this.depthNoise.generateNoiseOctaves(this.dr, startX, startY, startZ, width, 1, depth, 100.0D, 0.0D, 100.0D);
        this.pnr = this.perlinNoise1.generateNoiseOctaves(this.pnr, startX, startY, startZ, width, height, depth, 8.555150000000001D, 34.2206D, 8.555150000000001D);
        this.ar = this.lperlinNoise1.generateNoiseOctaves(this.ar, startX, startY, startZ, width, height, depth, amplitude1, amplitude2, amplitude1);
        this.br = this.lperlinNoise2.generateNoiseOctaves(this.br, startX, startY, startZ, width, height, depth, amplitude1, amplitude2, amplitude1);

        int currentIndex = 0;
        double[] cosineWeights = new double[height];

        // 调整权重计算适应256格高度
        for (int yIndex = 0; yIndex < height; ++yIndex) {
            // 使用(height-1)作为分母（16个区间对应17个采样点）
            cosineWeights[yIndex] = Math.cos(yIndex * Math.PI * 6.0D / (height - 1)) * 2.0D;
            double distanceFromCenter = yIndex;

            // 对称处理（中点索引 = (height-1)/2 = 8）
            if (yIndex > (height - 1) / 2) {
                distanceFromCenter = (height - 1) - yIndex;
            }

            // 边界平滑（保持4格缓冲）
            if (distanceFromCenter < 4.0D) {
                distanceFromCenter = 4.0D - distanceFromCenter;
                cosineWeights[yIndex] -= distanceFromCenter * distanceFromCenter * distanceFromCenter * 10.0D;
            }
        }

        for (int xIndex = 0; xIndex < width; ++xIndex) {
            for (int zIndex = 0; zIndex < depth; ++zIndex) {
                for (int yIndex = 0; yIndex < height; ++yIndex) {
                    double weight = cosineWeights[yIndex];
                    double arValue = this.ar[currentIndex] / 512.0D;
                    double brValue = this.br[currentIndex] / 512.0D;
                    double blendFactor = (this.pnr[currentIndex] / 10.0D + 1.0D) / 2.0D;
                    double finalHeightValue;

                    if (blendFactor < 0.0D) {
                        finalHeightValue = arValue;
                    } else if (blendFactor > 1.0D) {
                        finalHeightValue = brValue;
                    } else {
                        finalHeightValue = arValue + (brValue - arValue) * blendFactor;
                    }

                    finalHeightValue -= weight;

                    // 顶部平滑（保持最后4个采样点）
                    if (yIndex > height - 4) {
                        double edgeBlendFactor = (yIndex - (height - 4)) / 3.0F;
                        finalHeightValue = finalHeightValue * (1.0D - edgeBlendFactor) - 10.0D * edgeBlendFactor;
                    }

                    noiseData[currentIndex] = finalHeightValue;
                    ++currentIndex;
                }
            }
        }

        return noiseData;
    }


    /**
     * Generate initial structures in this chunk, e.g. mineshafts, temples, lakes, and dungeons
     *
     * @param chunkX Chunk x coordinate
     * @param chunkZ Chunk z coordinate
     */
    public void populate(int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, ThreadLocalRandom.current(), chunkX, chunkZ, false);

        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;
        BlockPos chunkOrigin = new BlockPos(blockX, 0, blockZ);
        Biome currentBiome = this.world.getBiome(chunkOrigin.add(16, 0, 16));
        ChunkPos chunkPosition = new ChunkPos(chunkX, chunkZ);

        this.mapGenBTNBridge.generateStructure(this.world, ThreadLocalRandom.current(), chunkPosition);
        this.mineshaftGenerator.generateStructure(this.world, ThreadLocalRandom.current(), chunkPosition);

        // Nether Lava Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), chunkX, chunkZ, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_LAVA)) {
            for (int featureIndex = 0; featureIndex < 8; ++featureIndex) {
                generateFeature(this.SwampWaterGen, chunkOrigin, ThreadLocalRandom.current());
            }
        }

        // Fire Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), chunkX, chunkZ, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.FIRE)) {
            int featureCount = ThreadLocalRandom.current().nextInt(ThreadLocalRandom.current().nextInt(10) + 1) + 1;
            for (int loopIndex = 0; loopIndex < featureCount; ++loopIndex) {
                switch (ThreadLocalRandom.current().nextInt(7)) {
                    case 0:
                        generateFeature(this.worldGenCaveGrass, chunkOrigin, ThreadLocalRandom.current());
                        break;
                    case 1:
                        generateFeature(this.stalactite, chunkOrigin, ThreadLocalRandom.current());
                        break;
                    case 2:
                        generateFeature(this.worldGenNesting, chunkOrigin, ThreadLocalRandom.current());
                        break;
                    case 3:
                        generateFeature(this.worldGenFluidPool, chunkOrigin, ThreadLocalRandom.current());
                        break;
                    case 4:
                        generateFeature(this.worldGenLavaPool, chunkOrigin, ThreadLocalRandom.current());
                        break;
                    case 5:
                        generateFeature(this.worldGenTarPool, chunkOrigin, ThreadLocalRandom.current());
                        break;
                    default:
                        generateFeature(this.garden, chunkOrigin, ThreadLocalRandom.current());
                        break;
                }
            }
        }

        // Glowstone Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), chunkX, chunkZ, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.GLOWSTONE)) {
            int oreCount = ThreadLocalRandom.current().nextInt(ThreadLocalRandom.current().nextInt(10) + 1);
            for (int loopIndex = 0; loopIndex < oreCount; ++loopIndex) {
                generateFeature(this.worldGenOreStone1, chunkOrigin, ThreadLocalRandom.current());
            }

            for (int fixedOreCount = 0; fixedOreCount < 10; ++fixedOreCount) {
                generateFeature(this.worldGenOreStone2, chunkOrigin, ThreadLocalRandom.current());
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, ThreadLocalRandom.current(), chunkX, chunkZ, false);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Pre(this.world, ThreadLocalRandom.current(), chunkPosition));

        // Shroom Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(this.world, ThreadLocalRandom.current(), chunkPosition, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            generateMushroomFeatures(chunkOrigin, ThreadLocalRandom.current());
        }

        // Nether Magma Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), chunkX, chunkZ, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_MAGMA)) {
            for (int magmaIndex = 0; magmaIndex < 4; ++magmaIndex) {
                generateFeature(this.siltGen, chunkOrigin, ThreadLocalRandom.current(), 120 + ThreadLocalRandom.current().nextInt(100));
            }
        }

        // Nether Lava2 Generation
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, ThreadLocalRandom.current(), chunkX, chunkZ, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.NETHER_LAVA2)) {
            for (int lavaIndex = 0; lavaIndex < 16; ++lavaIndex) {
                int yOffset = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 8 : 0;
                generateFeature(this.StagnantWaterGen, chunkOrigin, ThreadLocalRandom.current(), yOffset);
            }
        }

        currentBiome.decorate(this.world, ThreadLocalRandom.current(), new BlockPos(blockX, 0, blockZ));

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Post(this.world, ThreadLocalRandom.current(), chunkOrigin));

        BlockFalling.fallInstantly = false;
    }


    private void generateFeature(WorldGenerator generator, BlockPos basePos, ThreadLocalRandom random) {
        generator.generate(this.world, random, basePos.add(random.nextInt(16) + 8, random.nextInt(240) + 4, random.nextInt(16) + 8));
    }

    private void generateFeature(WorldGenerator generator, BlockPos basePos, ThreadLocalRandom random, int yOffset) {
        generator.generate(this.world, random, basePos.add(random.nextInt(16), yOffset, random.nextInt(16)));
    }

    private void generateMushroomFeatures(BlockPos basePos, ThreadLocalRandom random) {
        for (int i = 0; i < 2; ++i) {
            if (random.nextBoolean()) {
                WorldGenerator mushroomGenerator = switch (i) {
                    case 0 -> this.MushroomFeature1;
                    case 1 -> this.MushroomFeature2;
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
        boolean generated = false;

        // 生成废弃矿井（原版逻辑）
        if (this.rand.nextInt(80) == 0) {
            this.mineshaftGenerator.generateStructure(this.world, this.rand, new ChunkPos(x, z));
            generated = true;
        }

        // 原有结构生成
        if (this.mapGenBTNBridge != null && this.mapGenBTNBridge.generateStructure(this.world, this.rand, new ChunkPos(x, z))) {
            generated = true;
        }

        return generated;
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        if (creatureType == EnumCreatureType.MONSTER) {
            if (this.mapGenBTNBridge.isInsideStructure(pos)) {
                return this.mapGenBTNBridge.getSpawnList();
            }

            if (this.mapGenBTNBridge.isPositionInStructure(this.world, pos) && this.world.getBlockState(pos.down()).getBlock() == Blocks.STONEBRICK) {
                return this.mapGenBTNBridge.getSpawnList();
            }
        }

        Biome biome = this.world.getBiome(pos);
        return biome.getSpawnableList(creatureType);
    }

    @Nullable
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        if ("BTNFortress".equals(structureName) && this.mapGenBTNBridge != null) {
            return this.mapGenBTNBridge.getNearestStructurePos(worldIn, position, findUnexplored);
        } else if ("Mineshaft".equals(structureName)) {
            return this.mineshaftGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        return null;
    }

    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        if ("BTNFortress".equals(structureName) && this.mapGenBTNBridge != null) {
            return this.mapGenBTNBridge.isInsideStructure(pos);
        }
        // 新增：矿井判断
        else if ("Mineshaft".equals(structureName)) {
            return this.mineshaftGenerator.isInsideStructure(pos);
        }
        return false;
    }

    /**
     * Recreates data about structures intersecting given chunk (used for example by getPossibleCreatures), without
     * placing any blocks. When called for the first time before any chunk is generated - also initializes the internal
     * state needed by getPossibleCreatures.
     */
    // 在recreateStructures方法中重建结构数据
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        this.mapGenBTNBridge.generate(this.world, x, z, null);
        this.mineshaftGenerator.generate(this.world, x, z, null);
    }
}
