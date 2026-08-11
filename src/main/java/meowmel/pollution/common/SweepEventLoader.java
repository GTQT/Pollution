package meowmel.pollution.common;

import baubles.api.BaublesApi;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;

/**
 * 魔法扫帚飞行机制（移植自 GregTech-Lite-Core PR #139）
 * 持有/放在背包或饰品栏时获得飞行与免伤，物品离身后收回
 */
@Mod.EventBusSubscriber(modid = "pollution")
public class SweepEventLoader {

    private static final String ACTIVE_KEY = "PollutionMagicSweepActive";
    private static final String PREVIOUS_FLIGHT_KEY = "PollutionMagicSweepPreviousFlight";
    private static final String MIGRATION_KEY = "PollutionMagicSweepStateMigrated";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side == Side.CLIENT) return;
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        NBTTagCompound sweepData = getPersistentData(player);
        migrateLegacyCapabilities(player, sweepData);
        boolean active = sweepData.getBoolean(ACTIVE_KEY);

        // 创造和旁观模式由原版管理能力，扫帚不接管其飞行状态。
        if (player.isCreative() || player.isSpectator()) {
            if (active) clearSweepState(sweepData);
            return;
        }

        if (hasMagicSweep(player)) {
            if (!active) {
                sweepData.setBoolean(ACTIVE_KEY, true);
                sweepData.setBoolean(PREVIOUS_FLIGHT_KEY, player.capabilities.allowFlying);
            }
            grantFlyAbility(player);
        } else if (active) {
            restoreFlyAbility(player, sweepData.getBoolean(PREVIOUS_FLIGHT_KEY));
            clearSweepState(sweepData);
        }
    }

    /** 免伤不写入 PlayerCapabilities，避免退出或异常中断后留下永久无敌。 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (!player.world.isRemote && hasMagicSweep(player)) {
            event.setCanceled(true);
        }
    }

    private static boolean hasMagicSweep(EntityPlayer player) {
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

    private static void grantFlyAbility(EntityPlayerMP player) {
        if (!player.capabilities.allowFlying) {
            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
        }
    }

    private static void restoreFlyAbility(EntityPlayerMP player, boolean previousAllowFlying) {
        boolean changed = player.capabilities.allowFlying != previousAllowFlying;
        player.capabilities.allowFlying = previousAllowFlying;
        if (!previousAllowFlying && player.capabilities.isFlying) {
            player.capabilities.isFlying = false;
            changed = true;
        }
        if (changed) player.sendPlayerAbilities();
    }

    private static NBTTagCompound getPersistentData(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static void clearSweepState(NBTTagCompound sweepData) {
        sweepData.removeTag(ACTIVE_KEY);
        sweepData.removeTag(PREVIOUS_FLIGHT_KEY);
    }

    /** 清理旧实现可能在玩家存档中遗留的创造飞行和免伤标志。 */
    private static void migrateLegacyCapabilities(EntityPlayerMP player, NBTTagCompound sweepData) {
        if (sweepData.getBoolean(MIGRATION_KEY)) return;
        sweepData.setBoolean(MIGRATION_KEY, true);

        if (player.isCreative() || player.isSpectator()) return;
        if (!player.capabilities.allowFlying || !player.capabilities.disableDamage) return;

        player.capabilities.allowFlying = false;
        player.capabilities.isFlying = false;
        player.capabilities.disableDamage = false;
        player.sendPlayerAbilities();
    }
}
