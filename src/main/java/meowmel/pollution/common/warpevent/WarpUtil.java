package meowmel.pollution.common.warpevent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 扭曲事件常用世界操作工具。
 */
public final class WarpUtil {
    private WarpUtil() {
    }

    /**
     * 从世界顶部向下找到第一个"脚下有实心方块、上方是空气"的站立点，
     * 返回站立点的 BlockPos；找不到返回 null。
     */
    public static BlockPos findStandablePosition(World world, BlockPos start) {
        int x = start.getX();
        int z = start.getZ();
        for (int y = 255; y > 0; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState below = world.getBlockState(pos);
            if (below.getBlock().isAir(below, world, pos)) {
                continue;
            }
            BlockPos stand = pos.up();
            IBlockState standState = world.getBlockState(stand);
            IBlockState headState = world.getBlockState(stand.up());
            if (standState.getBlock().isAir(standState, world, stand)
                    && headState.getBlock().isAir(headState, world, stand.up())) {
                return stand;
            }
        }
        return null;
    }
}
