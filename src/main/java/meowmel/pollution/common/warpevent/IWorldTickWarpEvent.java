package meowmel.pollution.common.warpevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 常驻世界 tick 扭曲事件：触发后在持续时间内每 tick 执行一次 triggerEvent。
 */
public abstract class IWorldTickWarpEvent extends IEventWarp {
    protected IWorldTickWarpEvent(String name, int minWarp) {
        super(name, minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        modEventInt(player, name, getDuration(world));
        return true;
    }

    /** 事件持续 tick 数 */
    protected int getDuration(World world) {
        return 10 + world.rand.nextInt(20);
    }

    /** 事件持续期间的逐 tick 效果 */
    protected abstract void triggerEvent(World world, EntityPlayer player);

    @Override
    public void onWorldTick(World world, EntityPlayer player) {
        int amount = getEventInt(player, name);
        if (amount > 0) {
            setEventInt(player, name, amount - 1);
            triggerEvent(world, player);
        }
    }
}
