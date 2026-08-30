package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import meowmel.pollution.common.warpevent.WarpUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 扭曲事件：咒波催生，周围冒出蘑菇 */
public class WarpMushrooms extends IEventWarp {
    public WarpMushrooms(int minWarp) {
        super("mushrooms", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        int spawned = 0;
        for (int i = 0; i < 8 && spawned < 5; i++) {
            BlockPos start = new BlockPos(
                    player.posX + (world.rand.nextDouble() - 0.5) * 10,
                    player.posY,
                    player.posZ + (world.rand.nextDouble() - 0.5) * 10);
            BlockPos stand = WarpUtil.findStandablePosition(world, start);
            if (stand == null) {
                continue;
            }
            Block mushroom = world.rand.nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
            if (mushroom.canPlaceBlockAt(world, stand)
                    && mushroom instanceof BlockMushroom
                    && ((BlockMushroom) mushroom).canGrow(world, stand, world.getBlockState(stand), false)) {
                world.setBlockState(stand, mushroom.getDefaultState());
                spawned++;
            }
        }
        return spawned > 0;
    }
}
