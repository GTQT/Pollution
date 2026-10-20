package meowmel.pollution.common.entity.dragon.net;

import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketDragonWobble implements IMessage {
    private int entityId;
    private int duration;
    private int axis;
    private int crackLevel;

    public PacketDragonWobble() {
    }

    public PacketDragonWobble(int entityId, int duration, int axis, int crackLevel) {
        this.entityId = entityId;
        this.duration = duration;
        this.axis = axis;
        this.crackLevel = crackLevel;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeByte(duration);
        buffer.writeByte(axis);
        buffer.writeByte(crackLevel);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        entityId = buffer.readInt();
        duration = buffer.readUnsignedByte();
        axis = buffer.readUnsignedByte();
        crackLevel = buffer.readUnsignedByte();
    }

    public static class Handler implements IMessageHandler<PacketDragonWobble, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final PacketDragonWobble message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    World world = Minecraft.getMinecraft().world;
                    if (world == null) {
                        return;
                    }
                    Entity entity = world.getEntityByID(message.entityId);
                    if (entity instanceof EntityPollutionDragon) {
                        ((EntityPollutionDragon) entity).applyClientWobble(
                                message.duration, message.axis, message.crackLevel);
                    }
                }
            });
            return null;
        }
    }
}
