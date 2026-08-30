package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 在洞穴石壁上生成渗水点（单格水源），用于死水/沼泽水体集群。
 */
public class WorldGenUndergroundWater extends WorldGenerator {

    private final Block block;
    private final boolean insideRock;

    public WorldGenUndergroundWater(Block blockIn, boolean insideRockIn) {
        this.block = blockIn;
        this.insideRock = insideRockIn;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        // 上方必须是石头（渗水点挂在石壁/洞顶下方）
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.STONE) {
            return false;
        }
        // 当前位置必须是空气或石头
        if (!worldIn.isAirBlock(position) && worldIn.getBlockState(position).getBlock() != Blocks.STONE) {
            return false;
        }

        // 统计四个水平方向与下方的石头/空气邻居数量（上方已确认为石头）
        int stoneNeighbors = 0;
        int airNeighbors = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP) {
                continue;
            }
            BlockPos neighbor = position.offset(facing);
            if (worldIn.getBlockState(neighbor).getBlock() == Blocks.STONE) {
                ++stoneNeighbors;
            } else if (worldIn.isAirBlock(neighbor)) {
                ++airNeighbors;
            }
        }

        // 完全被石头包裹（5 面石头），或 4 面石头 + 1 面空气开口（渗水向洞口流）时生成水源
        if ((!this.insideRock && stoneNeighbors == 4 && airNeighbors == 1) || stoneNeighbors == 5) {
            IBlockState state = this.block.getDefaultState();
            worldIn.setBlockState(position, state, 2);
            worldIn.immediateBlockTick(position, state, rand);
        }

        return true;
    }
}
