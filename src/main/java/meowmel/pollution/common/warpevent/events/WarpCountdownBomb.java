package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.ITimerWarpEvent;
import meowmel.pollution.common.warpevent.net.MeowmelNetwork;
import meowmel.pollution.common.warpevent.net.PacketFakeExplosionSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * 扭曲事件（倒计时）：倒计时炸弹——10 秒倒计时后假爆炸。
 * 只有音效与烟雾，不会造成伤害，纯粹制造恐惧。
 */
public class WarpCountdownBomb extends ITimerWarpEvent {
    public WarpCountdownBomb(int minWarp) {
        super("countdownbomb", minWarp);
    }

    @Override
    protected int getDuration(World world) {
        return 200; // 10 秒
    }

    @Override
    protected void timerTick(World world, EntityPlayer player, int ticksLeft) {
        // 每秒播报剩余秒数
        if (ticksLeft > 0 && ticksLeft % 20 == 0) {
            int seconds = ticksLeft / 20;
            player.sendMessage(new TextComponentTranslation("chat.pollution.warp.countdownbomb.tick", seconds));
        }
    }

    @Override
    protected void onTimerEnd(World world, EntityPlayer player) {
        player.sendMessage(new TextComponentTranslation("chat.pollution.warp.countdownbomb.end"));
        MeowmelNetwork.sendToAllAround(player.posX, player.posY, player.posZ, 64, player.dimension,
                new PacketFakeExplosionSound(player.posX, player.posY, player.posZ));
    }
}
