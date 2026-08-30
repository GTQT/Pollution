package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/** 扭曲事件：咒波翻涌，反胃 */
public class WarpNausea extends IEventWarp {
    public WarpNausea(int minWarp) {
        super("nausea", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 160, 0));
        return true;
    }
}
