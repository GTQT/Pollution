package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/** 扭曲事件：污染腐化血肉，凋零 */
public class WarpWither extends IEventWarp {
    public WarpWither(int minWarp) {
        super("wither", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 400, 2));
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_WITHER_AMBIENT, SoundCategory.AMBIENT, 0.6F, 1.0F);
        return true;
    }
}
