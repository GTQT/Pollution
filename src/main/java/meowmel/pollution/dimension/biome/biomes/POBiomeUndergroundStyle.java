package meowmel.pollution.dimension.biome.biomes;

import meowmel.pollution.dimension.biome.IUndergroundBiome;
import meowmel.pollution.dimension.worldgen.ChunkGenerator.ChunkGeneratorUndergroundWorld;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHellDecorator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 地下世界风格化群系（参数化）：表面方块 + 装饰器组 + 刷怪表。
 * 实现 {@link IUndergroundBiome}，被 chunk generator 自动识别。
 * 装饰器组为空时 populate 走维度级原版装饰（populateWithVanilla）。
 */
public class POBiomeUndergroundStyle extends Biome implements IUndergroundBiome {

    /** 群系装饰器组（数组元素 = 一次生成调用，顺序执行） */
    private final WorldGenerator[] decorators;

    public POBiomeUndergroundStyle(String name, int waterColor, float temperature, float rainfall,
                                   IBlockState topBlock, IBlockState fillerBlock,
                                   WorldGenerator[] decorators,
                                   Biome.SpawnListEntry... monsterEntries) {
        super(new Biome.BiomeProperties(name)
                .setWaterColor(waterColor)
                .setTemperature(temperature)
                .setRainfall(rainfall));
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        for (Biome.SpawnListEntry entry : monsterEntries) {
            this.spawnableMonsterList.add(entry);
        }
        // 关键：必须显式赋值给父类字段（Biome 默认 topBlock 是草方块，不赋值会导致所有群系地表都是草方块）
        this.topBlock = topBlock;
        this.fillerBlock = fillerBlock;
        this.decorators = decorators;
        this.decorator = new BiomeHellDecorator();
    }

    @Override
    public void buildSurface(ChunkGeneratorUndergroundWorld chunkGenerator, int chunkX, int chunkZ,
                             ChunkPrimer primer, int x, int z, double terrainNoise) {
        // 从水面以上开始扫描：只替换水面以上（干燥洞窟）的地表，
        // 水下保持石头/湖床沙砾（水下不长蘑菇/草）。
        int waterLevel = chunkGenerator.getWorld().getSeaLevel();
        for (int y = 250; y >= waterLevel; --y) {
            if (primer.getBlockState(x, y, z).getBlock() != Blocks.STONE) {
                continue;
            }
            // 上方是实心方块 → 不是地表，继续向下
            if (primer.getBlockState(x, y + 1, z).getMaterial().isSolid()) {
                continue;
            }
            // 地表：铺 topBlock，同一实心层内向下 3 格铺 fillerBlock（遇空/流体停止）
            primer.setBlockState(x, y, z, this.topBlock);
            for (int depth = 1; depth <= 3; depth++) {
                IBlockState below = primer.getBlockState(x, y - depth, z);
                if (below.getBlock() == Blocks.STONE) {
                    primer.setBlockState(x, y - depth, z, this.fillerBlock);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void populate(ChunkGeneratorUndergroundWorld chunkGenerator, int chunkX, int chunkZ) {
        if (decorators == null || decorators.length == 0) {
            chunkGenerator.populateWithVanilla(chunkX, chunkZ);
            return;
        }
        World world = chunkGenerator.getWorld();
        Random rand = chunkGenerator.getRand();
        BlockPos origin = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        for (WorldGenerator generator : decorators) {
            generator.generate(world, rand,
                    origin.add(rand.nextInt(16) + 8, rand.nextInt(240) + 4, rand.nextInt(16) + 8));
        }
    }
}
