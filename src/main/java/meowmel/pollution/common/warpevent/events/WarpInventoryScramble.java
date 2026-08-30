package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 扭曲事件：咒波扰乱秩序，背包物品被随机打乱（不丢失） */
public class WarpInventoryScramble extends IEventWarp {
    public WarpInventoryScramble(int minWarp) {
        super("inventoryscramble", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        List<ItemStack> slots = new ArrayList<>();
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            slots.add(player.inventory.mainInventory.get(i));
        }
        Collections.shuffle(slots, world.rand);
        for (int i = 0; i < slots.size(); i++) {
            player.inventory.mainInventory.set(i, slots.get(i));
        }
        return true;
    }
}
