package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IWorldTickWarpEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** 扭曲事件（常驻）：咒波狂风，持续吹飞玩家 */
public class WarpWind extends IWorldTickWarpEvent {
    public WarpWind(int minWarp) {
        super("wind", minWarp);
    }

    @Override
    protected int getDuration(World world) {
        return 20 + world.rand.nextInt(30);
    }

    @Override
    protected void triggerEvent(World world, EntityPlayer player) {
        if (world.rand.nextBoolean()) {
            player.addVelocity(world.rand.nextGaussian() * 0.3, world.rand.nextDouble() * 0.5, world.rand.nextGaussian() * 0.3);
            player.velocityChanged = true;
        }
    }
}
