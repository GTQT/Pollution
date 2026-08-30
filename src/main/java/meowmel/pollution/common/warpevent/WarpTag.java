package meowmel.pollution.common.warpevent;

/**
 * 扭曲事件系统在玩家 NBT 中使用的标签 key 集中定义。
 * 根标签挂在玩家实体数据下，与其它 mod 的标签隔离。
 */
public final class WarpTag {
    /** 玩家 NBT 根标签 */
    public static final String ROOT = "pollution_warp";
    /** 事件队列（NBTTagList<String>） */
    public static final String QUEUE = "queue";
    /** 活动事件计数器（NBTTagCompound, key=事件名） */
    public static final String EVENTS = "events";

    private WarpTag() {
    }
}
