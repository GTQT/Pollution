package keqing.pollution.dimension.worldgen.mapGen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenStalactite extends WorldGenerator {
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            // 生成随机位置
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            // 检查初始位置是否为空气方块且下方为 Betweenstone
            if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.STONE) {
                // 生成随机高度，范围在3到10之间
                int height = rand.nextInt(8) + 3;

                // 生成钟乳石柱
                for (int j = 0; j < height; ++j) {
                    BlockPos currentPos = blockpos.up(j);
                    if (worldIn.isAirBlock(currentPos)) {
                        worldIn.setBlockState(currentPos, Blocks.STONE.getDefaultState(), 2);
                    } else {
                        // 如果遇到非空气方块，停止生成钟乳石柱
                        break;
                    }
                }
            }
        }

        return true;
    }

}
