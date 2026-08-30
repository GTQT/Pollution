package meowmel.pollution.common.warpevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

/**
 * 扭曲事件队列：事件名按触发顺序存进玩家 NBT，
 * 出队时随机取一个执行（同 WarpTheory 的随机出队机制）。
 */
public final class WarpQueue {
    private WarpQueue() {
    }

    private static NBTTagCompound getRootTag(EntityPlayer player) {
        NBTTagCompound root = player.getEntityData().getCompoundTag(WarpTag.ROOT);
        if (!player.getEntityData().hasKey(WarpTag.ROOT)) {
            player.getEntityData().setTag(WarpTag.ROOT, root);
        }
        return root;
    }

    public static void queue(EntityPlayer player, String eventName) {
        NBTTagCompound root = getRootTag(player);
        NBTTagList list = root.getTagList(WarpTag.QUEUE, 8);
        list.appendTag(new NBTTagString(eventName));
        root.setTag(WarpTag.QUEUE, list);
    }

    /** 随机取一个事件出队，队列为空返回 null */
    public static String dequeueRandom(EntityPlayer player) {
        NBTTagCompound root = getRootTag(player);
        NBTTagList list = root.getTagList(WarpTag.QUEUE, 8);
        if (list.tagCount() == 0) {
            return null;
        }
        int index = player.world.rand.nextInt(list.tagCount());
        String name = list.getStringTagAt(index);
        list.removeTag(index);
        if (list.tagCount() == 0) {
            root.removeTag(WarpTag.QUEUE);
        } else {
            root.setTag(WarpTag.QUEUE, list);
        }
        return name;
    }

    public static int size(EntityPlayer player) {
        return getRootTag(player).getTagList(WarpTag.QUEUE, 8).tagCount();
    }
}
