package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import meowmel.pollution.common.warpevent.WarpUtil;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 扭曲事件：咒波引雷，玩家附近落雷 */
public class WarpLightning extends IEventWarp {
    public WarpLightning(int minWarp) {
        super("lightning", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        BlockPos ground = WarpUtil.findStandablePosition(world, new BlockPos(
                player.posX + world.rand.nextGaussian() * 4.0,
                player.posY,
                player.posZ + world.rand.nextGaussian() * 4.0));
        if (ground == null) {
            return false;
        }
        sendChat(player);
        world.addWeatherEffect(new EntityLightningBolt(world,
                ground.getX() + 0.5, ground.getY(), ground.getZ() + 0.5, false));
        return true;
    }
}
