package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import meowmel.pollution.common.warpevent.WarpUtil;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 扭曲事件：僵尸围城——咒波引来一群僵尸（原版实体，命名后不消失） */
public class WarpZombieSiege extends IEventWarp {
    public WarpZombieSiege(int minWarp) {
        super("siege", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        int count = 4 + world.rand.nextInt(4);
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            BlockPos stand = WarpUtil.findStandablePosition(world, new BlockPos(
                    player.posX + (world.rand.nextDouble() - 0.5) * 12,
                    player.posY,
                    player.posZ + (world.rand.nextDouble() - 0.5) * 12));
            if (stand == null) {
                continue;
            }
            EntityZombie zombie = new EntityZombie(world);
            zombie.setLocationAndAngles(stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5,
                    world.rand.nextFloat() * 360.0F, 0.0F);
            zombie.setCustomNameTag("被污染的僵尸");
            if (world.spawnEntity(zombie)) {
                spawned++;
            }
        }
        if (spawned <= 0) {
            return false;
        }
        sendChat(player);
        return true;
    }
}
