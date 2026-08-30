package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import meowmel.pollution.common.warpevent.WarpUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 扭曲事件：咒波固化，玩家被黑曜石环墙困住 */
public class WarpObsidian extends IEventWarp {
    public WarpObsidian(int minWarp) {
        super("obsidian", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        BlockPos center = new BlockPos(player);
        int placed = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) < 2 && Math.abs(dz) < 2) {
                    continue; // 只围外圈
                }
                BlockPos stand = WarpUtil.findStandablePosition(world,
                        new BlockPos(center.getX() + dx, center.getY(), center.getZ() + dz));
                if (stand != null && world.isAirBlock(stand)) {
                    world.setBlockState(stand, Blocks.OBSIDIAN.getDefaultState());
                    placed++;
                }
            }
        }
        if (placed <= 0) {
            return false;
        }
        sendChat(player);
        return true;
    }
}
