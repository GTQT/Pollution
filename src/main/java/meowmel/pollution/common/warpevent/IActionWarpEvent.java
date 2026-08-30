package meowmel.pollution.common.warpevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 多段扭曲事件：按玩家扭曲值决定强度等级（1~numLevels），
 * 触发后在持续时间内由世界 tick 逐 tick 驱动 triggerEvent。
 */
public abstract class IActionWarpEvent extends IEventWarp {
    protected final int numLevels;

    protected IActionWarpEvent(String name, int minWarp, int numLevels) {
        super(name, minWarp);
        this.numLevels = Math.max(1, numLevels);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        int level = calculateLevel(player);
        int duration = getDuration(level, world);
        sendChat(player);
        modEventInt(player, getCounterKey(level), duration);
        return true;
    }

    /** 按总扭曲值计算事件等级：每 60 点扭曲升一级 */
    protected int calculateLevel(EntityPlayer player) {
        int totalWarp = FluxWarpManager.getTotalWarp(player);
        int level = 1 + totalWarp / 60;
        return Math.max(1, Math.min(numLevels, level));
    }

    /** 事件持续 tick 数 */
    protected int getDuration(int level, World world) {
        return 15 + world.rand.nextInt(30);
    }

    protected String getCounterKey(int level) {
        return name + "_" + level;
    }

    /** 事件持续期间的逐 tick 效果 */
    protected abstract void triggerEvent(int eventLevel, World world, EntityPlayer player);

    @Override
    public void onWorldTick(World world, EntityPlayer player) {
        for (int level = numLevels; level >= 1; level--) {
            int amount = getEventInt(player, getCounterKey(level));
            if (amount > 0) {
                setEventInt(player, getCounterKey(level), amount - 1);
                triggerEvent(level, world, player);
            }
        }
    }
}
