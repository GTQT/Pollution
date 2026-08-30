package meowmel.pollution.common.warpevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 倒计时扭曲事件：触发后倒计时 getDuration() tick，
 * 期间逐 tick 调 timerTick，归零时调 onTimerEnd。
 */
public abstract class ITimerWarpEvent extends IEventWarp {
    protected ITimerWarpEvent(String name, int minWarp) {
        super(name, minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        modEventInt(player, name, getDuration(world));
        return true;
    }

    /** 倒计时总 tick 数 */
    protected int getDuration(World world) {
        return 200;
    }

    /** 倒计时期间逐 tick 效果，ticksLeft 为剩余 tick 数（0 表示最后一刻） */
    protected abstract void timerTick(World world, EntityPlayer player, int ticksLeft);

    /** 倒计时结束回调 */
    protected void onTimerEnd(World world, EntityPlayer player) {
    }

    @Override
    public void onWorldTick(World world, EntityPlayer player) {
        int amount = getEventInt(player, name);
        if (amount > 0) {
            int left = amount - 1;
            setEventInt(player, name, left);
            timerTick(world, player, left);
            if (left <= 0) {
                onTimerEnd(world, player);
            }
        }
    }
}
