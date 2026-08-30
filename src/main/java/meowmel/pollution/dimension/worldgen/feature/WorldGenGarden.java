package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 在地下洞穴中生成一片小花园：石面转草地、随机花朵与树木。
 */
public class WorldGenGarden extends WorldGenerator {

    // 配置常量
    private static final int GARDEN_SIZE = 16;      // 花园区域边长
    private static final int FLOWER_ATTEMPTS = 50;  // 花朵生成尝试次数
    private static final int TREE_ATTEMPTS = 8;     // 树木生成尝试次数
    private static final int MIN_TREES = 3;         // 最大生成树木数量
    private static final int EDGE_IRREGULARITY = 5; // 花园边缘不规则程度

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos origin) {
        convertToGrass(worldIn, rand, origin);
        generateFlowers(worldIn, rand, origin);
        generateTrees(worldIn, rand, origin);
        return true;
    }

    // 将指定区域地表的石头顶面转换为草方块
    private void convertToGrass(World world, Random rand, BlockPos center) {
        for (int x = -GARDEN_SIZE / 2; x < GARDEN_SIZE / 2; x++) {
            for (int z = -GARDEN_SIZE / 2; z < GARDEN_SIZE / 2; z++) {
                // 使用随机偏移来创建不规则边界
                int offsetX = x + (int) (rand.nextGaussian() * EDGE_IRREGULARITY);
                int offsetZ = z + (int) (rand.nextGaussian() * EDGE_IRREGULARITY);

                BlockPos pos = center.add(offsetX, 0, offsetZ);

                // 确保区块已加载
                if (!world.isBlockLoaded(pos)) {
                    continue;
                }

                // 石头顶面且上方为空气时才铺草地
                if (world.getBlockState(pos).getBlock() == Blocks.STONE && world.isAirBlock(pos.up())) {
                    world.setBlockState(pos.up(), Blocks.DIRT.getDefaultState(), 2);

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
            BlockPos pos = startPos.add(rand.nextInt(GARDEN_SIZE), 0, rand.nextInt(GARDEN_SIZE));
            BlockPos surfacePos = world.getTopSolidOrLiquidBlock(pos);

            if (world.isAirBlock(surfacePos)) {
                IBlockState flowerState = getRandomFlower(rand);
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
            // 黄色系花朵（蒲公英等）
            return Blocks.YELLOW_FLOWER.getDefaultState();
        }
    }

    // 生成树木：随机橡木或白桦树
    private void generateTrees(World world, Random rand, BlockPos center) {
        int treesGenerated = 0;
        BlockPos startPos = center.add(-GARDEN_SIZE / 2, 0, -GARDEN_SIZE / 2);

        for (int i = 0; i < TREE_ATTEMPTS && treesGenerated < MIN_TREES; i++) {
            BlockPos pos = startPos.add(rand.nextInt(GARDEN_SIZE), 0, rand.nextInt(GARDEN_SIZE));

            // 获取地表位置并向上偏移 1 格（树需要空间）
            BlockPos surfacePos = world.getTopSolidOrLiquidBlock(pos).up();

            IBlockState logState;
            IBlockState leafState;
            int treeHeight;
            if (rand.nextBoolean()) {
                // 橡树
                logState = Blocks.LOG.getDefaultState();
                leafState = Blocks.LEAVES.getDefaultState();
                treeHeight = 4 + rand.nextInt(2);
            } else {
                // 白桦树（LOG/LEAVES 各有独立的 variant 属性实例，不能用 BlockPlanks.VARIANT）
                logState = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
                leafState = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH);
                treeHeight = 5 + rand.nextInt(2);
            }

            WorldGenAbstractTree treeGen = new WorldGenTrees(true, treeHeight, logState, leafState, false);
            if (treeGen.generate(world, rand, surfacePos)) {
                treesGenerated++;
            }
        }
    }
}
