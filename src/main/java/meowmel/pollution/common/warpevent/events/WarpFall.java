package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IWorldTickWarpEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 扭曲事件（常驻）：世界破洞——脚下地面被抽走，持续坠落。
 * 只破坏非基岩、非流体的普通方块，且 y 不低于 1。
 */
public class WarpFall extends IWorldTickWarpEvent {
    public WarpFall(int minWarp) {
        super("fall", minWarp);
    }

    @Override
    protected int getDuration(World world) {
        return 10 + world.rand.nextInt(15);
    }

    @Override
    protected void triggerEvent(World world, EntityPlayer player) {
        if (world.rand.nextInt(2) != 0) {
            return;
        }
        BlockPos below = new BlockPos(player.posX, Math.floor(player.posY) - 1, player.posZ);
        if (below.getY() < 1) {
            return;
        }
        IBlockState state = world.getBlockState(below);
        Block block = state.getBlock();
        if (block == Blocks.BEDROCK || block == Blocks.AIR || block instanceof BlockLiquid) {
            return;
        }
        world.setBlockToAir(below);
        world.playEvent(2001, below, Block.getStateId(state));
    }
}
