package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/** 扭曲事件：咒波遮蔽视线，失明 */
public class WarpBlind extends IEventWarp {
    public WarpBlind(int minWarp) {
        super("blind", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 160, 0));
        return true;
    }
}
