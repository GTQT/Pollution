package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.ITimerWarpEvent;
import meowmel.pollution.common.warpevent.net.MeowmelNetwork;
import meowmel.pollution.common.warpevent.net.PacketBlood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 扭曲事件（倒计时）：四周渗出"血"，滴滴答答地流淌 */
public class WarpBlood extends ITimerWarpEvent {
    public WarpBlood(int minWarp) {
        super("blood", minWarp);
    }

    @Override
    protected int getDuration(World world) {
        return 60 + world.rand.nextInt(40);
    }

    @Override
    protected void timerTick(World world, EntityPlayer player, int ticksLeft) {
        // 每 5 tick 在玩家周围随机位置添加一个滴落点
        if (ticksLeft % 5 != 0) {
            return;
        }
        double x = player.posX + (world.rand.nextDouble() - 0.5) * 4;
        double y = player.posY + world.rand.nextDouble() * 2 - 0.5;
        double z = player.posZ + (world.rand.nextDouble() - 0.5) * 4;
        BlockPos pos = new BlockPos(x, y, z);
        if (world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos)) {
            MeowmelNetwork.sendToAllAround(x, y, z, 48, player.dimension, new PacketBlood(x, y, z, false));
        }
    }

    @Override
    protected void onTimerEnd(World world, EntityPlayer player) {
        MeowmelNetwork.sendToAllAround(player.posX, player.posY, player.posZ, 48, player.dimension,
                new PacketBlood(0, 0, 0, true));
    }
}
