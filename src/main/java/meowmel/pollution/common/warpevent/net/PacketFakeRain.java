package meowmel.pollution.common.warpevent.net;

import io.netty.buffer.ByteBuf;
import meowmel.pollution.client.warpevent.ClientWarpEffects;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * 假雨事件：客户端开始一段持续 ticks 的假雨。
 * level 1 = 蓝色雨，level 2 = 腐蚀性红色雨；level 0 表示停止。
 */
public class PacketFakeRain implements IMessage {
    private int level;
    private int ticks;

    public PacketFakeRain() {
    }

    public PacketFakeRain(int level, int ticks) {
        this.level = level;
        this.ticks = ticks;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(level);
        buf.writeInt(ticks);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        level = buf.readInt();
        ticks = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketFakeRain, IMessage> {
        @Override
        public IMessage onMessage(PacketFakeRain message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(
                    () -> ClientWarpEffects.setFakeRain(message.level, message.ticks));
            return null;
        }
    }
}
