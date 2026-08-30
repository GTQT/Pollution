package meowmel.pollution.common.warpevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * 扭曲事件基类（参考 WarpTheory 的事件抽象，代码全新实现）。
 * 事件本身是无状态的：所有持续状态都通过 NBT 存在玩家实体数据上，
 * 事件对象只负责一次性执行（doEvent）或被调度器按 tick 驱动（onWorldTick）。
 */
public abstract class IEventWarp {
    /** 事件唯一名（也用作配置与 NBT 键） */
    protected final String name;
    /** 触发所需最低扭曲值 */
    protected final int minWarp;

    protected IEventWarp(String name, int minWarp) {
        this.name = name;
        this.minWarp = minWarp;
    }

    public final String getName() {
        return name;
    }

    public final int getSeverity() {
        return minWarp;
    }

    /** 入队时从玩家临时扭曲中扣除的代价 */
    public final int getCost() {
        return Math.max(1, (int) Math.ceil(minWarp / 10.0));
    }

    /** 执行前检查（默认恒可执行，子类可覆写） */
    public boolean canDo(World world, EntityPlayer player) {
        return true;
    }

    /** 事件主体：出队时调用一次 */
    public abstract boolean doEvent(World world, EntityPlayer player);

    /** 世界 tick 驱动钩子：多段/计时/常驻事件的子类覆写，普通事件为空实现 */
    public void onWorldTick(World world, EntityPlayer player) {
    }

    protected void sendChat(EntityPlayer player) {
        sendChat(player, "chat.pollution.warp." + name);
    }

    protected void sendChat(EntityPlayer player, String key) {
        player.sendMessage(new TextComponentTranslation(key));
    }

    // ===== NBT 事件计数器读写 =====

    protected NBTTagCompound getEventsTag(EntityPlayer player) {
        NBTTagCompound root = player.getEntityData().getCompoundTag(WarpTag.ROOT);
        if (!player.getEntityData().hasKey(WarpTag.ROOT)) {
            player.getEntityData().setTag(WarpTag.ROOT, root);
        }
        NBTTagCompound events = root.getCompoundTag(WarpTag.EVENTS);
        if (!root.hasKey(WarpTag.EVENTS)) {
            root.setTag(WarpTag.EVENTS, events);
        }
        return events;
    }

    protected int getEventInt(EntityPlayer player, String key) {
        return getEventsTag(player).getInteger(key);
    }

    protected void setEventInt(EntityPlayer player, String key, int value) {
        getEventsTag(player).setInteger(key, value);
    }

    /** 按增量修改计数器，下限为 0 */
    protected void modEventInt(EntityPlayer player, String key, int delta) {
        setEventInt(player, key, Math.max(0, getEventInt(player, key) + delta));
    }
}
