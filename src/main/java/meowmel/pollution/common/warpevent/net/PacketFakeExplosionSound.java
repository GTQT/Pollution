package meowmel.pollution.common.warpevent.net;

import io.netty.buffer.ByteBuf;
import meowmel.pollution.Pollution;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** 假爆炸事件：客户端播放爆炸音效与烟雾粒子（无实际伤害/破坏） */
public class PacketFakeExplosionSound implements IMessage {
    private double x;
    private double y;
    private double z;

    public PacketFakeExplosionSound() {
    }

    public PacketFakeExplosionSound(double x, double y, double z) {
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

    public static class Handler implements IMessageHandler<PacketFakeExplosionSound, IMessage> {
        @Override
        public IMessage onMessage(PacketFakeExplosionSound message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                Pollution.proxy.scheduleWarpFakeExplosion(message.x, message.y, message.z);
            }
            return null;
        }
    }
}
