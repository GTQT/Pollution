package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import meowmel.pollution.common.warpevent.net.MeowmelNetwork;
import meowmel.pollution.common.warpevent.net.PacketFakeRain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** 扭曲事件：蓝色假雨（客户端特效，视觉欺骗） */
public class WarpFakeRain extends IEventWarp {
    public WarpFakeRain(int minWarp) {
        super("fakerain", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        MeowmelNetwork.sendToAllAround(player.posX, player.posY, player.posZ, 64, player.dimension,
                new PacketFakeRain(1, 100 + world.rand.nextInt(60)));
        return true;
    }
}
