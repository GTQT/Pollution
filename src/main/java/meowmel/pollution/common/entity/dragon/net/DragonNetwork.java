package meowmel.pollution.common.entity.dragon.net;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class DragonNetwork {
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("pollution:dragon");
    private static int nextId;

    private DragonNetwork() {
    }

    public static void init() {
        CHANNEL.registerMessage(PacketDragonWobble.Handler.class, PacketDragonWobble.class, nextId++, Side.CLIENT);
    }

    public static void sendToAllAround(Entity entity, IMessage message) {
        CHANNEL.sendToAllAround(message, new NetworkRegistry.TargetPoint(
                entity.dimension, entity.posX, entity.posY, entity.posZ, 64.0D));
    }
}
