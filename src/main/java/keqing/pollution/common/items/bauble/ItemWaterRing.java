package keqing.pollution.common.items.bauble;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import gregtech.api.unification.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import vazkii.botania.api.mana.ManaItemHandler;

import static net.minecraft.block.material.Material.WATER;

public class ItemWaterRing extends ItemBaubleBehavior {

    int consumeSource;

    public ItemWaterRing(int consumeSource, int MaxSource, Material material, BaubleType type) {
        super(MaxSource, material, type);
        this.consumeSource = consumeSource;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        super.onWornTick(stack, player);
        if (player.isInsideOfMaterial(WATER)&&consumeSource(consumeSource,false,stack)) {
            consumeSource(consumeSource,true,stack);

            if (player instanceof EntityPlayer) {
                ItemStack firstRing = BaublesApi.getBaublesHandler((EntityPlayer)player).getStackInSlot(1);
                if (!firstRing.isEmpty() && firstRing.getItem() instanceof vazkii.botania.common.item.equipment.bauble.ItemWaterRing && firstRing != stack) {
                    return;
                }
            }
            double motionX = player.motionX * 1.2;
            double motionY = player.motionY * 1.2;
            double motionZ = player.motionZ * 1.2;
            boolean flying = player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isFlying;
            if (Math.abs(motionX) < 1.3 && !flying) {
                player.motionX = motionX;
            }

            if (Math.abs(motionY) < 1.3 && !flying) {
                player.motionY = motionY;
            }

            if (Math.abs(motionZ) < 1.3 && !flying) {
                player.motionZ = motionZ;
            }

            PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
            if (effect == null) {
                PotionEffect neweffect = new PotionEffect(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, -42, true, true);
                player.addPotionEffect(neweffect);
            }

            if (player.getAir() <= 1 && player instanceof EntityPlayer) {
                int mana = ManaItemHandler.requestMana(stack, (EntityPlayer)player, 300, true);
                if (mana > 0) {
                    player.setAir(mana);
                }
            }
        } else {
            this.onUnequipped(stack, player);
        }



    }
    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {

        PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
        if (effect != null && effect.getAmplifier() == -42) {
            player.removePotionEffect(MobEffects.NIGHT_VISION);
        }


    }
}
