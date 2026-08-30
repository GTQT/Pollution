package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import meowmel.pollution.common.warpevent.WarpUtil;
import meowmel.pollution.common.warpevent.net.MeowmelNetwork;
import meowmel.pollution.common.warpevent.net.PacketBlinkParticles;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 扭曲事件：咒波闪现，玩家被随机传送（带传送粒子特效） */
public class WarpBlink extends IEventWarp {
    public WarpBlink(int minWarp) {
        super("blink", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        for (int attempt = 0; attempt < 16; attempt++) {
            BlockPos start = new BlockPos(
                    player.posX + (world.rand.nextDouble() - 0.5) * 24,
                    player.posY,
                    player.posZ + (world.rand.nextDouble() - 0.5) * 24);
            BlockPos stand = WarpUtil.findStandablePosition(world, start);
            if (stand == null) {
                continue;
            }
            double oldX = player.posX;
            double oldY = player.posY;
            double oldZ = player.posZ;
            sendChat(player);
            player.setPositionAndUpdate(stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5);
            MeowmelNetwork.sendToAllAround(oldX, oldY, oldZ, 48, player.dimension,
                    new PacketBlinkParticles(oldX, oldY, oldZ));
            MeowmelNetwork.sendToAllAround(player.posX, player.posY, player.posZ, 48, player.dimension,
                    new PacketBlinkParticles(player.posX, player.posY, player.posZ));
            return true;
        }
        return false;
    }
}
