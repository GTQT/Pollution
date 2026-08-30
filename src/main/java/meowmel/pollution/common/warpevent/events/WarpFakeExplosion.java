package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import meowmel.pollution.common.warpevent.net.MeowmelNetwork;
import meowmel.pollution.common.warpevent.net.PacketFakeExplosionSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** 扭曲事件：假爆炸——只有音效和烟雾，没有伤害与破坏 */
public class WarpFakeExplosion extends IEventWarp {
    public WarpFakeExplosion(int minWarp) {
        super("fakeexplosion", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        MeowmelNetwork.sendToAllAround(player.posX, player.posY, player.posZ, 64, player.dimension,
                new PacketFakeExplosionSound(player.posX, player.posY, player.posZ));
        return true;
    }
}
