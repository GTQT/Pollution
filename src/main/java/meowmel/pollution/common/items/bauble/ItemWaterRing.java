package meowmel.pollution.common.items.bauble;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import gregtech.api.unification.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.items.IItemHandler;
import vazkii.botania.api.mana.ManaItemHandler;

public class ItemWaterRing extends ItemBaubleBehavior {

    /** 本模组给予的夜视标记等级，用于和普通夜视区分 */
    private static final int NIGHT_VISION_MARKER = -42;
    /** 憋气时每次请求的魔力值 */
    private static final int MANA_PER_BREATH = 300;

    private final int sourcePerTick;

    public ItemWaterRing(int sourcePerTick, int maxSource, Material material, BaubleType type) {
        super(maxSource, material, type);
        this.sourcePerTick = sourcePerTick;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        super.onWornTick(stack, player);

        // 不在水中时收回效果
        if (!player.isInsideOfMaterial(net.minecraft.block.material.Material.WATER)) {
            onUnequipped(stack, player);
            return;
        }

        // 同时装备植物魔法的水之戒时让位，避免效果叠加（不消耗源质）
        if (player instanceof EntityPlayer entityPlayer && hasBotaniaWaterRing(entityPlayer, stack)) {
            onUnequipped(stack, player);
            return;
        }

        // 先确认源质足够再实际消耗
        if (!consumeSource(sourcePerTick, false, stack)) {
            onUnequipped(stack, player);
            return;
        }
        consumeSource(sourcePerTick, true, stack);

        applyWaterEffects(stack, player);
    }

    private void applyWaterEffects(ItemStack stack, EntityLivingBase player) {
        boolean flying = player instanceof EntityPlayer entityPlayer && entityPlayer.capabilities.isFlying;
        if (!flying) {
            double motionX = player.motionX * 1.2;
            double motionY = player.motionY * 1.2;
            double motionZ = player.motionZ * 1.2;
            if (Math.abs(motionX) < 1.3) {
                player.motionX = motionX;
            }
            if (Math.abs(motionY) < 1.3) {
                player.motionY = motionY;
            }
            if (Math.abs(motionZ) < 1.3) {
                player.motionZ = motionZ;
            }
        }

        if (player.getActivePotionEffect(MobEffects.NIGHT_VISION) == null) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, Integer.MAX_VALUE,
                    NIGHT_VISION_MARKER, true, true));
        }

        if (player.getAir() <= 1 && player instanceof EntityPlayer entityPlayer) {
            int mana = ManaItemHandler.requestMana(stack, entityPlayer, MANA_PER_BREATH, true);
            if (mana > 0) {
                player.setAir(mana);
            }
        }
    }

    /**
     * @return 玩家身上是否装备了植物魔法的水之戒（不含本物品本身）
     */
    private static boolean hasBotaniaWaterRing(EntityPlayer player, ItemStack self) {
        IItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            ItemStack equipped = baubles.getStackInSlot(slot);
            if (equipped.isEmpty() || equipped == self) continue;
            if (equipped.getItem() instanceof vazkii.botania.common.item.equipment.bauble.ItemWaterRing) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
        if (effect != null && effect.getAmplifier() == NIGHT_VISION_MARKER) {
            player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }
}
