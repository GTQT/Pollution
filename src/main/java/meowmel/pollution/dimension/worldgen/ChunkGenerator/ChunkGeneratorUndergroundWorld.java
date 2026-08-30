package meowmel.pollution.dimension.worldgen.ChunkGenerator;

import gregtech.api.fluids.store.FluidStorageKeys;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.dimension.biome.UndergroundBiomes;
import meowmel.pollution.dimension.biome.UndergroundTerrainMode;
import meowmel.pollution.dimension.biome.biomes.POBiomeUndergroundStyle;
import meowmel.pollution.dimension.worldgen.WorldEngineNoise;
import meowmel.pollution.dimension.worldgen.mapGen.MapGenCavesUnderground;
import meowmel.pollution.dimension.worldgen.mapGen.MapGenUndergroundBridge;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenFluidPool;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenGarden;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenMushroom;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenOreStone1;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenOreStone2;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenSingle;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenStalactite;
import meowmel.pollution.dimension.worldgen.mapGen.WorldGenUndergroundWater;
import meowmel.gtqtcore.common.blocks.GTQTMetaBlocks;
import meowmel.gtqtcore.common.blocks.StoneVariantBlock;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockStone;
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
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
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

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static meowmel.pollution.dimension.worldgen.terraingen.InitMapGenEvent.EventType.UNDERGROUND;
import static meowmel.pollution.dimension.worldgen.terraingen.InitMapGenEvent.EventType.UNDERGROUND_CAVE;
import static meowmel.pollution.dimension.worldgen.terraingen.TerrainGen.getModdedMapGen;

/**
 * 地下世界区块生成器（群系驱动，对照 docs/underground-biome-driven.md）：
 * - 群系分布由 {@link meowmel.pollution.dimension.biome.BiomeProviderUnderground} 决定
 * - 地形按 chunk 内多数群系的地形模式分支（标准/微丘/柱廊/低地/河谷）
 * - 洞窟骨架约束（所有模式）：y ≥ 84 实心洞顶，y ≤ 8 基岩底座
 */
public class ChunkGeneratorUndergroundWorld implements IChunkGenerator {

    // ===== 洞窟骨架 =====
    private static final int CAVE_CEILING = 84;   // 洞顶岩层下界（y >= 84 一律实心）
    private static final int CAVE_FLOOR = 8;      // 基岩底座上界（buildSurfaces 处理）

    // ===== 基础方块 =====
    private static final IBlockState STONE = Blocks.STONE.getDefaultState();
    private static final IBlockState SWAMP_WATER = Blocks.WATER.getDefaultState();
    private static final IBlockState LAVA = Blocks.LAVA.getDefaultState();

    // ===== GT 石材变种 =====
    private static final IBlockState LIMESTONE = gtStoneState(StoneVariantBlock.StoneType.LIMESTONE);
    private static final IBlockState KOMATIITE = gtStoneState(StoneVariantBlock.StoneType.KOMATIITE);
    private static final IBlockState GREEN_SCHIST = gtStoneState(StoneVariantBlock.StoneType.GREEN_SCHIST);
    private static final IBlockState BLUE_SCHIST = gtStoneState(StoneVariantBlock.StoneType.BLUE_SCHIST);
    private static final IBlockState KIMBERLITE = gtStoneState(StoneVariantBlock.StoneType.KIMBERLITE);
    private static final IBlockState QUARTZITE = gtStoneState(StoneVariantBlock.StoneType.QUARTZITE);
    private static final IBlockState SLATE = gtStoneState(StoneVariantBlock.StoneType.SLATE);
    private static final IBlockState SHALE = gtStoneState(StoneVariantBlock.StoneType.SHALE);

    // ===== 原版岩石变种 =====
    private static final IBlockState ANDESITE = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);
    private static final IBlockState DIORITE = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
    private static final IBlockState GRANITE = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE);

    /** 可用于 surface 层的全部岩石变种（含 GT 石材与原版石材） */
    private static final IBlockState[] ROCK_VARIANTS = {
            ANDESITE, DIORITE, GRANITE, KOMATIITE, LIMESTONE, QUARTZITE, GREEN_SCHIST, SLATE, BLUE_SCHIST, SHALE
    };

    // ===== 群系噪声 =====
    private static final WorldEngineNoise.NoiseProfile HILL_NOISE = WorldEngineNoise.profile(0.5D, 2);
    private static final WorldEngineNoise.NoiseProfile PILLAR_NOISE = WorldEngineNoise.profile(0.7D, 3);
    private static final WorldEngineNoise.NoiseProfile RIVER_NOISE = WorldEngineNoise.profile(1.2D, 6);
    /** 与 BiomeProviderUnderground 的河流种子一致，保证河道位置对齐 */
    private final long riverSeed;
    private final long hillSeed;

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

    // ===== 结构生成器 =====
    private final MapGenUndergroundBridge undergroundBridgeGen = new MapGenUndergroundBridge();
    private final MapGenMineshaft mineshaftGen = new MapGenMineshaft();
    private final MapGenBase caveGen;

    // ===== 装饰生成器 =====
    private final WorldGenStalactite stalactiteGen = new WorldGenStalactite();
    private final WorldGenOreStone1 brownMushroomCluster = new WorldGenOreStone1();
    private final WorldGenOreStone2 redMushroomCluster = new WorldGenOreStone2();
    private final WorldGenerator gravelGen = new WorldGenMinable(Blocks.GRAVEL.getDefaultState(), 33, BlockMatcher.forBlock(Blocks.GRAVEL));
    private final WorldGenUndergroundWater stagnantWaterGen = new WorldGenUndergroundWater(Blocks.WATER, true);
    private final WorldGenUndergroundWater swampWaterGen = new WorldGenUndergroundWater(Blocks.WATER, false);
    private final WorldGenMushroom redMushroomFeature = new WorldGenMushroom(Blocks.RED_MUSHROOM);
    private final WorldGenMushroom brownMushroomFeature = new WorldGenMushroom(Blocks.BROWN_MUSHROOM);
    private final WorldGenBigMushroom bigBrownMushroomFeature = new WorldGenBigMushroom(Blocks.BROWN_MUSHROOM);
    private final WorldGenGarden gardenGen = new WorldGenGarden();
    private final WorldGenSingle caveGrassGen = new WorldGenSingle(Blocks.TALLGRASS);
    private final WorldGenSingle caveLeavesGen = new WorldGenSingle(Blocks.LEAVES);
    private final WorldGenFluidPool waterPoolGen = new WorldGenFluidPool(Blocks.WATER);
    private final WorldGenFluidPool lavaPoolGen = new WorldGenFluidPool(Blocks.LAVA);
    private final WorldGenFluidPool tarPoolGen = new WorldGenFluidPool(PollutionMaterials.PureTar.getFluid(FluidStorageKeys.LIQUID).getBlock());
    private final WorldGenMinable quartzOreGen = new WorldGenMinable(Blocks.QUARTZ_ORE.getDefaultState(), 8, BlockMatcher.forBlock(Blocks.STONE));
    private final WorldGenMinable diamondOreGen = new WorldGenMinable(Blocks.DIAMOND_ORE.getDefaultState(), 4, BlockMatcher.forBlock(Blocks.STONE));
    private final WorldGenMinable lapisOreGen = new WorldGenMinable(Blocks.LAPIS_ORE.getDefaultState(), 6, BlockMatcher.forBlock(Blocks.STONE));
    private final WorldGenMinable clayGen = new WorldGenMinable(Blocks.CLAY.getDefaultState(), 16, BlockMatcher.forBlock(Blocks.STONE));

    private static final int WATER_LEVEL = 63;

    public ChunkGeneratorUndergroundWorld(World worldIn, boolean generateStructures, long seed) {
        this.world = worldIn;
        this.generateStructures = generateStructures;
        this.rand = new Random(seed);
        this.riverSeed = seed * 31L + 7L;   // 与 BiomeProviderUnderground 对齐
        this.hillSeed = seed * 13L + 5L;

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

    private static IBlockState gtStoneState(StoneVariantBlock.StoneType stoneType) {
        return GTQTMetaBlocks.STONE_BLOCKS.get(StoneVariantBlock.StoneVariant.SMOOTH).getState(stoneType);
    }

    // ===== 地形模式判定 =====

    /** 按 chunk 内 biome 数组多数决地形模式（非地下世界群系一律标准） */
    private UndergroundTerrainMode resolveTerrainMode(Biome[] biomes) {
        EnumMap<UndergroundTerrainMode, Integer> counts = new EnumMap<>(UndergroundTerrainMode.class);
        for (Biome biome : biomes) {
            UndergroundTerrainMode mode = biome instanceof POBiomeUndergroundStyle
                    ? ((POBiomeUndergroundStyle) biome).getTerrainMode()
                    : UndergroundTerrainMode.STANDARD;
            counts.merge(mode, 1, Integer::sum);
        }
        UndergroundTerrainMode result = UndergroundTerrainMode.STANDARD;
        int max = 0;
        for (Map.Entry<UndergroundTerrainMode, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;
    }

    // ===== 地形高度 =====

    private void prepareHeights(int chunkX, int chunkZ, ChunkPrimer primer, UndergroundTerrainMode mode) {
        // 柱状柱廊模式走独立逻辑（不参与噪声插值）
        if (mode == UndergroundTerrainMode.PILLAR) {
            preparePillarTerrain(chunkX, chunkZ, primer);
            return;
        }

        int waterLevel = this.world.getSeaLevel();
        this.heightNoiseBuffer = this.getHeights(this.heightNoiseBuffer, chunkX * 4, 0, chunkZ * 4, 5, 17, 5);

        // 群系噪声采样（5×5 采样点，与插值块组一一对应）
        double[][] hillOffsets = mode == UndergroundTerrainMode.HILLY ? sampleHillNoise(chunkX, chunkZ) : null;
        double[][] riverValues = mode == UndergroundTerrainMode.RIVER ? sampleRiverNoise(chunkX, chunkZ) : null;

        final double interpolationFactorY = 0.0625D; // 1/16，垂直插值步长

        for (int xIndex = 0; xIndex < 4; ++xIndex) {
            for (int zIndex = 0; zIndex < 4; ++zIndex) {
                double cornerNoise1 = this.heightNoiseBuffer[((xIndex) * 5 + zIndex) * 17 + 0];
                double cornerNoise2 = this.heightNoiseBuffer[((xIndex) * 5 + zIndex + 1) * 17 + 0];
                double cornerNoise3 = this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex) * 17 + 0];
                double cornerNoise4 = this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex + 1) * 17 + 0];
                double deltaY1 = 0, deltaY2 = 0, deltaY3 = 0, deltaY4 = 0;

                // 微丘偏移与河道标记（按采样块）
                double hillOffset = hillOffsets != null ? hillOffsets[xIndex][zIndex] : 0.0D;
                boolean riverBlock = riverValues != null && Math.abs(riverValues[xIndex][zIndex]) < 0.06D;

                for (int yIndex = 0; yIndex < 16; ++yIndex) {
                    cornerNoise1 = this.heightNoiseBuffer[((xIndex) * 5 + zIndex) * 17 + yIndex];
                    cornerNoise2 = this.heightNoiseBuffer[((xIndex) * 5 + zIndex + 1) * 17 + yIndex];
                    cornerNoise3 = this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex) * 17 + yIndex];
                    cornerNoise4 = this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex + 1) * 17 + yIndex];
                    deltaY1 = (this.heightNoiseBuffer[((xIndex) * 5 + zIndex) * 17 + yIndex + 1] - cornerNoise1) * interpolationFactorY;
                    deltaY2 = (this.heightNoiseBuffer[((xIndex) * 5 + zIndex + 1) * 17 + yIndex + 1] - cornerNoise2) * interpolationFactorY;
                    deltaY3 = (this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex) * 17 + yIndex + 1] - cornerNoise3) * interpolationFactorY;
                    deltaY4 = (this.heightNoiseBuffer[((xIndex + 1) * 5 + zIndex + 1) * 17 + yIndex + 1] - cornerNoise4) * interpolationFactorY;

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

                                // 河谷模式：河道列优先（挖至 y=10，水填至 y=8）
                                if (riverBlock) {
                                    if (currentHeight <= 8) {
                                        blockState = SWAMP_WATER;
                                    } else if (currentHeight < 10) {
                                        blockState = null; // 河道空气层
                                    }
                                } else if (zInterpolated + hillOffset > -0.2D) {
                                    if (currentHeight < waterLevel - 1) {
                                        blockState = KIMBERLITE;
                                    } else if (currentHeight > waterLevel + 5) {
                                        blockState = STONE;
                                    } else {
                                        blockState = (zInterpolated > 0.6D) ? STONE : KIMBERLITE;
                                    }
                                }

                                // 噪声空洞且低于海平面：填充水
                                if (blockState == null && currentHeight < waterLevel && !riverBlock) {
                                    blockState = SWAMP_WATER;
                                }

                                // 熔岩盆地：洞窟底部的水替换为岩浆
                                if (blockState == SWAMP_WATER && mode == UndergroundTerrainMode.LOWLAND && currentHeight < 25) {
                                    blockState = LAVA;
                                }

                                // 洞窟骨架：洞顶一律实心（任何模式）
                                if (currentHeight >= CAVE_CEILING) {
                                    blockState = STONE;
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

    /** 柱状柱廊地形：钟乳石从洞顶（y=84）悬挂，1/3 概率连地成石柱，无柱区为大厅空洞 */
    private void preparePillarTerrain(int chunkX, int chunkZ, ChunkPrimer primer) {
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int worldX = chunkX * 16 + localX;
                int worldZ = chunkZ * 16 + localZ;
                double pillar = WorldEngineNoise.perlinNoise2D(riverSeed, worldX / 64.0D, worldZ / 64.0D, PILLAR_NOISE);
                boolean isPillarColumn = pillar > 0.35D;

                if (isPillarColumn) {
                    double pillarHeight = 20.0D + WorldEngineNoise.perlinNoise2D(hillSeed, worldX / 32.0D, worldZ / 32.0D, HILL_NOISE) * 25.0D;
                    int topY = CAVE_CEILING;
                    int bottomY = CAVE_CEILING - (int) pillarHeight;
                    boolean connectsFloor = WorldEngineNoise.perlinNoise2D(hillSeed, worldX / 16.0D, worldZ / 16.0D, HILL_NOISE) > 0.5D;

                    for (int y = 0; y < CAVE_CEILING; ++y) {
                        if (y <= CAVE_FLOOR) {
                            continue; // 基岩留给 buildSurfaces
                        }
                        if (y >= bottomY || (connectsFloor && y >= CAVE_FLOOR)) {
                            primer.setBlockState(localX, y, localZ, STONE);
                        }
                    }
                    // 柱顶 1 格石灰岩点缀
                    if (topY - 1 > bottomY) {
                        primer.setBlockState(localX, topY - 1, localZ, LIMESTONE);
                    }
                }
                // 无柱列保持空洞（顶部由洞顶规则覆盖）
            }
        }
    }

    private double[][] sampleHillNoise(int chunkX, int chunkZ) {
        double[][] result = new double[5][5];
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                result[x][z] = WorldEngineNoise.perlinNoise2D(hillSeed, (chunkX * 4 + x) / 24.0D, (chunkZ * 4 + z) / 24.0D, HILL_NOISE) * 8.0D;
            }
        }
        return result;
    }

    private double[][] sampleRiverNoise(int chunkX, int chunkZ) {
        double[][] result = new double[5][5];
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                result[x][z] = WorldEngineNoise.perlinNoise2D(riverSeed, (chunkX * 4 + x) / 666.0D, (chunkZ * 4 + z) / 666.0D, RIVER_NOISE);
            }
        }
        return result;
    }

    private void buildSurfaces(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomes) {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, this.world)) {
            return;
        }

        // 生成 10 个随机岩石变种层（高度 60-250，层厚 3-5）
        int[] variantHeights = new int[10];
        int[] variantLayers = new int[10];
        IBlockState[] variantBlocks = new IBlockState[10];
        for (int i = 0; i < 10; i++) {
            variantHeights[i] = this.rand.nextInt(190) + 60;
            variantLayers[i] = 3 + this.rand.nextInt(2);
            variantBlocks[i] = ROCK_VARIANTS[this.rand.nextInt(ROCK_VARIANTS.length)];
        }

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                // 基岩层：底部 2 层 + 顶部 2 层
                primer.setBlockState(localZ, 0, localX, Blocks.BEDROCK.getDefaultState());
                primer.setBlockState(localZ, 1, localX, Blocks.BEDROCK.getDefaultState());
                primer.setBlockState(localZ, 254, localX, Blocks.BEDROCK.getDefaultState());
                primer.setBlockState(localZ, 255, localX, Blocks.BEDROCK.getDefaultState());

                // 应用岩石变种层（仅替换石头方块）
                for (int i = 0; i < 10; i++) {
                    int startY = variantHeights[i];
                    int layerCount = variantLayers[i];
                    IBlockState blockType = variantBlocks[i];
                    int endY = Math.min(250, startY + layerCount - 1);
                    for (int yPos = startY; yPos <= endY; yPos++) {
                        if (primer.getBlockState(localZ, yPos, localX).getBlock() == Blocks.STONE) {
                            primer.setBlockState(localZ, yPos, localX, blockType);
                        }
                    }
                }
            }
        }

        // 群系表层替换：蘑菇森林菌丝、水晶森林石英岩、熔岩盆地黑曜石、暗河河床沙砾/黏土
        // 替换目标 = 从洞底向上找到的第一个实心方块（排除水/岩浆等流体）
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                Biome biome = biomes[localX + localZ * 16];
                if (biome != UndergroundBiomes.MYCELIUM_FOREST
                        && biome != UndergroundBiomes.CRYSTAL_CAVERN
                        && biome != UndergroundBiomes.LAVA_BASIN
                        && biome != UndergroundBiomes.UNDERGROUND_RIVER) {
                    continue;
                }
                int groundY = -1;
                for (int y = CAVE_FLOOR + 1; y < CAVE_CEILING; ++y) {
                    IBlockState state = primer.getBlockState(localZ, y, localX);
                    if (state.getMaterial().isSolid()) {
                        groundY = y;
                        break;
                    }
                }
                if (groundY <= 0) {
                    continue;
                }
                if (biome == UndergroundBiomes.MYCELIUM_FOREST) {
                    primer.setBlockState(localZ, groundY, localX, Blocks.MYCELIUM.getDefaultState());
                    for (int d = 1; d <= 3; d++) {
                        if (primer.getBlockState(localZ, groundY - d, localX).getBlock() == Blocks.STONE) {
                            primer.setBlockState(localZ, groundY - d, localX, Blocks.DIRT.getDefaultState());
                        }
                    }
                } else if (biome == UndergroundBiomes.CRYSTAL_CAVERN) {
                    primer.setBlockState(localZ, groundY, localX, QUARTZITE);
                } else if (biome == UndergroundBiomes.LAVA_BASIN) {
                    primer.setBlockState(localZ, groundY, localX, Blocks.OBSIDIAN.getDefaultState());
                } else if (biome == UndergroundBiomes.UNDERGROUND_RIVER) {
                    primer.setBlockState(localZ, groundY, localX,
                            this.rand.nextBoolean() ? Blocks.GRAVEL.getDefaultState() : Blocks.CLAY.getDefaultState());
                }
            }
        }
    }

    // ===== 主生成入口 =====

    @Override
    public Chunk generateChunk(int x, int z) {
        this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);

        // 先取群系数组（决定地形模式与表层），与后续 biomeArray 一致
        Biome[] biomes = this.world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
        UndergroundTerrainMode mode = resolveTerrainMode(biomes);

        ChunkPrimer chunkPrimer = new ChunkPrimer();
        this.prepareHeights(x, z, chunkPrimer, mode);
        this.buildSurfaces(x, z, chunkPrimer, biomes);
        this.caveGen.generate(this.world, x, z, chunkPrimer);

        if (this.generateStructures) {
            // 河谷/柱廊群系跳过地下堡垒（悬空风险），只生成废弃矿井
            boolean allowFortress = mode != UndergroundTerrainMode.RIVER && mode != UndergroundTerrainMode.PILLAR;
            if (allowFortress && this.rand.nextBoolean()) {
                this.undergroundBridgeGen.generate(this.world, x, z, chunkPrimer);
            } else {
                this.mineshaftGen.generate(this.world, x, z, chunkPrimer);
            }
        }

        Chunk chunk = new Chunk(this.world, chunkPrimer, x, z);
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

    // ===== 装饰阶段（按群系分发） =====

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

        // ===== 群系装饰分发 =====
        if (currentBiome == UndergroundBiomes.STALACTITE_PILLAR) {
            // 钟乳石石林：钟乳石密集 + 沙砾 + 水池
            for (int i = 0; i < 16; ++i) generateFeature(this.stalactiteGen, chunkOrigin, random);
            for (int i = 0; i < 6; ++i) generateFeature(this.gravelGen, chunkOrigin, random);
            for (int i = 0; i < 4; ++i) generateFeature(this.waterPoolGen, chunkOrigin, random);
        } else if (currentBiome == UndergroundBiomes.MYCELIUM_FOREST) {
            // 蘑菇森林：蘑菇灯柱 + 单蘑菇 + 巨蘑菇 + 水池
            for (int i = 0; i < 6; ++i) generateFeature(this.brownMushroomCluster, chunkOrigin, random);
            for (int i = 0; i < 6; ++i) generateFeature(this.redMushroomCluster, chunkOrigin, random);
            for (int i = 0; i < 12; ++i) generateFeature(this.redMushroomFeature, chunkOrigin, random);
            for (int i = 0; i < 12; ++i) generateFeature(this.brownMushroomFeature, chunkOrigin, random);
            for (int i = 0; i < 8; ++i) generateFeature(this.bigBrownMushroomFeature, chunkOrigin, random);
            for (int i = 0; i < 3; ++i) generateFeature(this.waterPoolGen, chunkOrigin, random);
        } else if (currentBiome == UndergroundBiomes.CRYSTAL_CAVERN) {
            // 水晶森林：晶簇占位（石英/钻石/青金石矿石）+ 水池
            for (int i = 0; i < 20; ++i) generateFeature(this.quartzOreGen, chunkOrigin, random);
            for (int i = 0; i < 4; ++i) generateFeature(this.diamondOreGen, chunkOrigin, random);
            for (int i = 0; i < 8; ++i) generateFeature(this.lapisOreGen, chunkOrigin, random);
            for (int i = 0; i < 4; ++i) generateFeature(this.waterPoolGen, chunkOrigin, random);
        } else if (currentBiome == UndergroundBiomes.LAVA_BASIN) {
            // 熔岩盆地：岩浆池密集 + 焦油池 + 沙砾
            for (int i = 0; i < 12; ++i) generateFeature(this.lavaPoolGen, chunkOrigin, random);
            for (int i = 0; i < 6; ++i) generateFeature(this.tarPoolGen, chunkOrigin, random);
            for (int i = 0; i < 2; ++i) generateFeature(this.gravelGen, chunkOrigin, random);
        } else if (currentBiome == UndergroundBiomes.UNDERGROUND_RIVER) {
            // 地下暗河：水潭 + 黏土河床补充
            for (int i = 0; i < 8; ++i) generateFeature(this.stagnantWaterGen, chunkOrigin, random);
            for (int i = 0; i < 6; ++i) generateFeature(this.clayGen, chunkOrigin, random);
        } else {
            // 深窟基础（兜底）：原有混合装饰
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
            for (int i = 0; i < random.nextInt(random.nextInt(10) + 1); ++i) {
                generateFeature(this.brownMushroomCluster, chunkOrigin, random);
            }
            for (int i = 0; i < 10; ++i) {
                generateFeature(this.redMushroomCluster, chunkOrigin, random);
            }
            for (int i = 0; i < 4; ++i) {
                generateFeature(this.gravelGen, chunkOrigin, random, 120 + random.nextInt(100));
            }
            for (int i = 0; i < 16; ++i) {
                int yOffset = net.minecraftforge.common.ForgeModContainer.fixVanillaCascading ? 8 : 0;
                generateFeature(this.stagnantWaterGen, chunkOrigin, random, yOffset);
            }
            if (random.nextBoolean()) {
                generateFeature(this.redMushroomFeature, chunkOrigin, random);
            }
            if (random.nextBoolean()) {
                generateFeature(this.brownMushroomFeature, chunkOrigin, random);
            }
        }

        // 事件与群系装饰（保留原版事件钩子，便于其他模组干预）
        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.NETHER_LAVA)) {
            for (int i = 0; i < 8; ++i) {
                generateFeature(this.swampWaterGen, chunkOrigin, random);
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, random, chunkX, chunkZ, false);
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(this.world, random, chunkPosition));

        if (TerrainGen.decorate(this.world, random, chunkPosition, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            if (random.nextBoolean()) {
                generateFeature(this.redMushroomFeature, chunkOrigin, random);
            }
            if (random.nextBoolean()) {
                generateFeature(this.brownMushroomFeature, chunkOrigin, random);
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

    // ===== 结构查询 =====

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
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
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
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
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
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
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        this.undergroundBridgeGen.generate(this.world, x, z, null);
        this.mineshaftGen.generate(this.world, x, z, null);
    }
}
