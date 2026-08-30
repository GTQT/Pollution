package meowmel.pollution.common.warpevent.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
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
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) {
                    return;
                }
                for (int i = 0; i < 32; i++) {
                    double px = message.x + world.rand.nextDouble() * 1.5 - 0.75;
                    double py = message.y + world.rand.nextDouble() * 2.0 - 1.0;
                    double pz = message.z + world.rand.nextDouble() * 1.5 - 0.75;
                    world.spawnParticle(EnumParticleTypes.PORTAL, px, py, pz, 0, 0, 0);
                }
                world.playSound(message.x, message.y, message.z,
                        SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
            });
            return null;
        }
    }
}
