package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IActionWarpEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 扭曲事件（多段）：咒波腐化大地。
 * 1 级：草→泥土；2 级：草→灰化土、泥土→菌丝。
 */
public class WarpSwamp extends IActionWarpEvent {
    public WarpSwamp(int minWarp) {
        super("swamp", minWarp, 2);
    }

    @Override
    protected int getDuration(int level, World world) {
        return 40 + level * 30;
    }

    @Override
    protected void triggerEvent(int eventLevel, World world, EntityPlayer player) {
        BlockPos pos = new BlockPos(
                player.posX + (world.rand.nextDouble() - 0.5) * 16,
                player.posY,
                player.posZ + (world.rand.nextDouble() - 0.5) * 16);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (eventLevel >= 2) {
            if (block == Blocks.GRASS) {
                world.setBlockState(pos, Blocks.SAND.getDefaultState());
            } else if (block == Blocks.DIRT) {
                world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState());
            }
        } else {
            if (block == Blocks.GRASS) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            }
        }
    }
}
