package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import meowmel.pollution.common.warpevent.net.MeowmelNetwork;
import meowmel.pollution.common.warpevent.net.PacketFakeRain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** 扭曲事件：腐蚀性红色咒波雨（客户端特效） */
public class WarpRain extends IEventWarp {
    public WarpRain(int minWarp) {
        super("rain", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        MeowmelNetwork.sendToAllAround(player.posX, player.posY, player.posZ, 64, player.dimension,
                new PacketFakeRain(2, 120 + world.rand.nextInt(80)));
        return true;
    }
}
