package meowmel.pollution.common.warpevent.net;

import io.netty.buffer.ByteBuf;
import meowmel.pollution.client.warpevent.ClientWarpEffects;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** 闪现/传送事件：客户端在指定位置播放传送粒子与音效 */
public class PacketBlinkParticles implements IMessage {
    private double x;
    private double y;
    private double z;

    public PacketBlinkParticles() {
    }

    public PacketBlinkParticles(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
    }

    public static class Handler implements IMessageHandler<PacketBlinkParticles, IMessage> {
        @Override
        public IMessage onMessage(PacketBlinkParticles message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                ClientWarpEffects.scheduleBlink(message.x, message.y, message.z);
            }
            return null;
        }
    }
}
