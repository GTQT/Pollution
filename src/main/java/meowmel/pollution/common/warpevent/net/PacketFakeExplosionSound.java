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
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) {
                    return;
                }
                world.playSound(message.x, message.y, message.z,
                        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 2.0F,
                        (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
                for (int i = 0; i < 24; i++) {
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                            message.x + world.rand.nextDouble() * 2.0 - 1.0,
                            message.y + world.rand.nextDouble() * 1.5,
                            message.z + world.rand.nextDouble() * 2.0 - 1.0,
                            world.rand.nextDouble() * 0.1, world.rand.nextDouble() * 0.1, world.rand.nextDouble() * 0.1);
                }
            });
            return null;
        }
    }
}
