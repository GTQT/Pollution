package keqing.pollution.dimension.worldgen.mapGen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenGarden extends WorldGenerator {
    // 配置常量
    private static final int GARDEN_SIZE = 16;  // 花园区域边长
    private static final int FLOWER_ATTEMPTS = 50; // 花朵生成尝试次数
    private static final int TREE_ATTEMPTS = 8;   // 树木生成尝试次数
    private static final int MIN_TREES = 3;       // 最小生成树木数量

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos origin) {
        // Step 1: 将地表转换为草方块
        convertToGrass(worldIn, rand, origin);

        // Step 2: 生成花朵
        generateFlowers(worldIn, rand, origin);

        // Step 3: 生成树木
        generateTrees(worldIn, rand, origin);

        return true;
    }

    // 将指定区域地表转换为草方块
    private void convertToGrass(World world, Random rand, BlockPos center) {
        // 使用Perlin噪声或其他噪声函数生成不规则边界
        int irregularity = 5; // 不规则程度，可以根据需要调整

        for (int x = -GARDEN_SIZE / 2; x < GARDEN_SIZE / 2; x++) {
            for (int z = -GARDEN_SIZE / 2; z < GARDEN_SIZE / 2; z++) {
                // 使用随机偏移来创建不规则边界
                int offsetX = x + (int) (rand.nextGaussian() * irregularity);
                int offsetZ = z + (int) (rand.nextGaussian() * irregularity);

                BlockPos pos = center.add(offsetX, 0, offsetZ);

                // 增加检查：确保区块已加载
                if (!world.isBlockLoaded(pos)) {
                    continue;
                }

                // 检查当前位置是否适合转换为草方块
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() == Blocks.STONE) {

                    world.setBlockState(pos.up(), Blocks.GRASS.getDefaultState(), 2);

                    // 随机添加草丛
                    if (rand.nextFloat() < 0.3F && world.isAirBlock(pos.up(2))) {
                        world.setBlockState(pos.up(2), Blocks.TALLGRASS.getStateFromMeta(1), 2);
                    }
                }
            }
        }
    }


    // 生成随机花朵
    private void generateFlowers(World world, Random rand, BlockPos center) {
        BlockPos startPos = center.add(-GARDEN_SIZE / 2, 0, -GARDEN_SIZE / 2);

        for (int i = 0; i < FLOWER_ATTEMPTS; i++) {
            // 随机花园内的位置
            int offsetX = rand.nextInt(GARDEN_SIZE);
            int offsetZ = rand.nextInt(GARDEN_SIZE);
            BlockPos pos = startPos.add(offsetX, 0, offsetZ);

            // 获取地表位置
            BlockPos surfacePos = world.getTopSolidOrLiquidBlock(pos);

            // 检查是否适合放置花朵
            if (world.isAirBlock(surfacePos)) {
                IBlockState flowerState = getRandomFlower(rand);

                // 确保花朵可以放置在该方块上
                Block flowerBlock = flowerState.getBlock();
                if (flowerBlock.canPlaceBlockAt(world, surfacePos)) {
                    world.setBlockState(surfacePos, flowerState, 2);
                }
            }
        }
    }

    // 获取随机花朵类型
    private IBlockState getRandomFlower(Random rand) {
        if (rand.nextFloat() < 0.6F) {
            // 红色系花朵（罂粟、郁金香等）
            return Blocks.RED_FLOWER.getDefaultState();
        } else {
            // 其他花朵（蒲公英、滨菊等）
            return Blocks.YELLOW_FLOWER.getDefaultState();
        }
    }

    // 生成树木
    // 生成树木 (修复原木属性错误)
    private void generateTrees(World world, Random rand, BlockPos center) {
        int treesGenerated = 0;
        BlockPos startPos = center.add(-GARDEN_SIZE / 2, 0, -GARDEN_SIZE / 2);

        for (int i = 0; i < TREE_ATTEMPTS && treesGenerated < MIN_TREES; i++) {
            // 随机花园内的位置
            int offsetX = rand.nextInt(GARDEN_SIZE);
            int offsetZ = rand.nextInt(GARDEN_SIZE);
            BlockPos pos = startPos.add(offsetX, 0, offsetZ);

            // 获取地表位置并向上偏移1格（树需要空间）
            BlockPos surfacePos = world.getTopSolidOrLiquidBlock(pos).up();

            // 使用正确的原木和树叶类型
            WorldGenAbstractTree treeGen;
            if (rand.nextBoolean()) {
                // 橡树 (使用原版生成器避免属性问题)
                treeGen = new WorldGenTrees(true, 4 + rand.nextInt(2),
                        Blocks.LOG.getDefaultState(),
                        Blocks.LEAVES.getDefaultState(),
                        false);
            } else {
                // 白桦树 (使用原版生成器)
                treeGen = new WorldGenTrees(true, 5 + rand.nextInt(2),
                        Blocks.LOG.getDefaultState(),
                        Blocks.LEAVES.getDefaultState(),
                        false);
            }

            // 尝试生成树木
            if (treeGen.generate(world, rand, surfacePos)) {
                treesGenerated++;
            }
        }
    }
}