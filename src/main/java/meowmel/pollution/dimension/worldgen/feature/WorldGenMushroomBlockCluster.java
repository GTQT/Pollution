package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 在洞穴顶部岩石下生成蘑菇方块簇（向下蔓延的团块），作为地下洞穴的光源装饰。
 */
public class WorldGenMushroomBlockCluster extends WorldGenerator {

    // 簇块蔓延的随机尝试次数
    private static final int SPREAD_ATTEMPTS = 1500;

    private final Block mushroomBlock;

    public WorldGenMushroomBlockCluster(Block mushroomBlockIn) {
        this.mushroomBlock = mushroomBlockIn;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        // 生成点必须是空气，且上方紧邻实心方块（悬挂在任何洞顶下）
        if (!worldIn.isAirBlock(position)) {
            return false;
        }
        if (!worldIn.getBlockState(position.up()).getMaterial().isSolid()) {
            return false;
        }

        worldIn.setBlockState(position, this.mushroomBlock.getDefaultState(), 2);

        // 从种子点向下/四周随机蔓延：仅当候选位置恰好与一个已有簇块相邻时才继续蔓延
        for (int i = 0; i < SPREAD_ATTEMPTS; ++i) {
            BlockPos candidate = position.add(
                    rand.nextInt(8) - rand.nextInt(8),
                    -rand.nextInt(12),
                    rand.nextInt(8) - rand.nextInt(8));

            if (!worldIn.isAirBlock(candidate)) {
                continue;
            }

            int adjacentCount = 0;
            for (EnumFacing facing : EnumFacing.values()) {
                if (worldIn.getBlockState(candidate.offset(facing)).getBlock() == this.mushroomBlock) {
                    ++adjacentCount;
                }
                if (adjacentCount > 1) {
                    break;
                }
            }

            if (adjacentCount == 1) {
                worldIn.setBlockState(candidate, this.mushroomBlock.getDefaultState(), 2);
            }
        }

        return true;
    }
}
