package meowmel.pollution.common.warpevent.net;

import io.netty.buffer.ByteBuf;
import meowmel.pollution.client.warpevent.ClientWarpEffects;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** 血腥事件：客户端添加/清空滴落粒子位置 */
public class PacketBlood implements IMessage {
    private double x;
    private double y;
    private double z;
    private boolean clear;

    public PacketBlood() {
    }

    public PacketBlood(double x, double y, double z, boolean clear) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.clear = clear;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeBoolean(clear);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        clear = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketBlood, IMessage> {
        @Override
        public IMessage onMessage(PacketBlood message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                if (message.clear) {
                    ClientWarpEffects.clearBlood();
                } else {
                    ClientWarpEffects.addBloodSpot(message.x, message.y, message.z);
                }
            });
            return null;
        }
    }
}
