package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 从洞穴地面岩石上向上生成石柱（石笋景观）。
 */
public class WorldGenStalactite extends WorldGenerator {

    private static final int COLUMN_ATTEMPTS = 64;
    private static final int MIN_HEIGHT = 3;
    private static final int MAX_HEIGHT = 10;

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < COLUMN_ATTEMPTS; ++i) {
            // 在中心点附近随机取一个位置
            BlockPos blockpos = position.add(
                    rand.nextInt(8) - rand.nextInt(8),
                    rand.nextInt(4) - rand.nextInt(4),
                    rand.nextInt(8) - rand.nextInt(8));

            // 起始位置必须是空气且下方为石头（石柱从地面长起）
            if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.STONE) {
                // 生成随机高度（3-10 格）的石柱
                int height = rand.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1) + MIN_HEIGHT;

                for (int j = 0; j < height; ++j) {
                    BlockPos currentPos = blockpos.up(j);
                    if (worldIn.isAirBlock(currentPos)) {
                        worldIn.setBlockState(currentPos, Blocks.STONE.getDefaultState(), 2);
                    } else {
                        // 遇到非空气方块时停止向上延伸
                        break;
                    }
                }
            }
        }

        return true;
    }
}
