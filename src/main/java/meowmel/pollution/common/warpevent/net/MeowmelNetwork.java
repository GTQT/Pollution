package meowmel.pollution.common.warpevent.net;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 扭曲事件特效专用网络通道（服务端→客户端）。
 * 注册顺序即包 ID，改动需保持两侧一致（均在本类内，安全）。
 */
public final class MeowmelNetwork {
    public static final SimpleNetworkWrapper CHANNEL =
            NetworkRegistry.INSTANCE.newSimpleChannel("pollution:warp");

    private static int nextId = 0;

    private MeowmelNetwork() {
    }

    public static void init() {
        CHANNEL.registerMessage(PacketBlinkParticles.Handler.class, PacketBlinkParticles.class, nextId++, Side.CLIENT);
        CHANNEL.registerMessage(PacketBlood.Handler.class, PacketBlood.class, nextId++, Side.CLIENT);
        CHANNEL.registerMessage(PacketFakeRain.Handler.class, PacketFakeRain.class, nextId++, Side.CLIENT);
        CHANNEL.registerMessage(PacketFakeExplosionSound.Handler.class, PacketFakeExplosionSound.class, nextId++, Side.CLIENT);
    }

    /** 向以 (x,y,z) 为中心 range 半径内所有玩家发送 */
    public static void sendToAllAround(double x, double y, double z, double range, int dimension, IMessage message) {
        CHANNEL.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }
}
