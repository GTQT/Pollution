package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/** 扭曲事件：咒波侵蚀，中毒（已有中毒则叠加时长） */
public class WarpPoison extends IEventWarp {
    public WarpPoison(int minWarp) {
        super("poison", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        int duration = 160;
        PotionEffect current = player.getActivePotionEffect(MobEffects.POISON);
        if (current != null) {
            duration += current.getDuration();
        }
        player.addPotionEffect(new PotionEffect(MobEffects.POISON, duration, 0));
        return true;
    }
}
