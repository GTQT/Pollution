package meowmel.pollution.common;

import baubles.api.BaublesApi;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 魔法扫帚飞行机制（移植自 GregTech-Lite-Core PR #139）
 * 持有/放在背包或饰品栏时获得飞行与免伤，物品离身后收回
 */
@Mod.EventBusSubscriber(modid = "pollution")
public class SweepEventLoader {

    /** 正在骑乘扫帚的玩家，用于在物品离身后收回能力 */
    private static final Set<UUID> SWEEP_RIDERS = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side == Side.CLIENT) return;
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        if (canGrantFlyAbilities(player)) {
            grantFlyAbilities(player);
            SWEEP_RIDERS.add(player.getUniqueID());
        } else if (SWEEP_RIDERS.remove(player.getUniqueID())) {
            revokeFlyAbilities(player);
        }
    }

    private static boolean canGrantFlyAbilities(EntityPlayerMP player) {
        ItemStack sweep = PollutionMetaItems.MAGIC_SWEEP.getStackForm();
        // 主手/副手
        if (player.getHeldItemMainhand().isItemEqual(sweep)) return true;
        if (player.getHeldItemOffhand().isItemEqual(sweep)) return true;

        // 背包（主背包+护甲+副手）
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i).isItemEqual(sweep)) return true;
        }

        // 饰品栏（Baubles）
        IItemHandler baubleInventory = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubleInventory.getSlots(); i++) {
            if (baubleInventory.getStackInSlot(i).isItemEqual(sweep)) return true;
        }

        return false;
    }

    private static void grantFlyAbilities(EntityPlayerMP player) {
        player.capabilities.allowFlying = true;
        player.capabilities.disableDamage = true;
        player.sendPlayerAbilities();
    }

    private static void revokeFlyAbilities(EntityPlayerMP player) {
        if (player.isCreative()) return;
        player.capabilities.allowFlying = false;
        player.capabilities.isFlying = false;
        player.capabilities.disableDamage = false;
        player.sendPlayerAbilities();
    }
}
