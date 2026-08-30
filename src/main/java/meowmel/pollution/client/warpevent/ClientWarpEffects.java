package meowmel.pollution.client.warpevent;

import meowmel.pollution.Pollution;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 客户端扭曲事件特效状态：血腥滴落粒子 + 假雨。
 * 由数据包写入状态，本类在客户端每 tick 渲染并自然衰减。
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Pollution.MODID, value = Side.CLIENT)
public class ClientWarpEffects {
    /** 血腥滴落位置 {x,y,z} */
    private static final List<double[]> BLOOD_SPOTS = new ArrayList<>();
    /** 假雨等级（1=蓝雨，2=红雨，0=无） */
    private static int fakeRainLevel = 0;
    private static int fakeRainTicks = 0;

    private ClientWarpEffects() {
    }

    public static void addBloodSpot(double x, double y, double z) {
        BLOOD_SPOTS.add(new double[]{x, y, z});
    }

    public static void clearBlood() {
        BLOOD_SPOTS.clear();
    }

    public static void setFakeRain(int level, int ticks) {
        fakeRainLevel = Math.max(0, level);
        fakeRainTicks = Math.max(0, ticks);
        if (fakeRainLevel == 0) {
            fakeRainTicks = 0;
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        World world = Minecraft.getMinecraft().world;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (world == null || player == null) {
            return;
        }

        // 血腥滴落：红色粉尘沿重力下落，随机消失
        for (Iterator<double[]> it = BLOOD_SPOTS.iterator(); it.hasNext(); ) {
            double[] spot = it.next();
            world.spawnParticle(EnumParticleTypes.REDSTONE, spot[0], spot[1], spot[2], 0, -0.05, 0);
            if (world.rand.nextInt(40) == 0) {
                it.remove();
            }
        }

        // 假雨：玩家周围随机落下雨滴
        if (fakeRainTicks > 0 && fakeRainLevel > 0) {
            fakeRainTicks--;
            EnumParticleTypes type = fakeRainLevel >= 2 ? EnumParticleTypes.DRIP_LAVA : EnumParticleTypes.WATER_DROP;
            double px = player.posX + (world.rand.nextDouble() - 0.5) * 12;
            double pz = player.posZ + (world.rand.nextDouble() - 0.5) * 12;
            double py = player.posY + world.rand.nextDouble() * 6 + 1;
            world.spawnParticle(type, px, py, pz, 0, 0, 0);
            if (fakeRainTicks <= 0) {
                fakeRainLevel = 0;
            }
        }
    }
}
