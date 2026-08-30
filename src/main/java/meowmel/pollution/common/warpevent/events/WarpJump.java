package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/** 扭曲事件：咒波灌体，身体失控地高高跃起 */
public class WarpJump extends IEventWarp {
    public WarpJump(int minWarp) {
        super("jump", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 160, 2));
        player.motionY = 0.5;
        player.velocityChanged = true;
        return true;
    }
}
