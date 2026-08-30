package meowmel.pollution.common.warpevent;

import meowmel.pollution.POConfig;
import meowmel.pollution.Pollution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 扭曲事件调度器。
 *
 * 触发链路（污染→扭曲→事件）：
 * 1. 每 20 tick：按玩家所在区块咒波累积临时扭曲（FluxWarpManager）
 * 2. 每 checkInterval tick（默认 2000=100秒）：概率 = sqrt(总扭曲)/100，命中则入队并扣除事件代价
 * 3. 每 dequeueInterval tick（默认 20=1秒）：随机出队一个事件执行
 * 4. 每世界 tick：驱动多段/计时/常驻事件的持续效果
 */
@Mod.EventBusSubscriber(modid = Pollution.MODID)
public class WarpEventHandler {

    /** 需要世界 tick 驱动的持续事件实例（初始化时按枚举缓存） */
    private static final List<IEventWarp> ACTIVE_EVENTS = new ArrayList<>();

    private WarpEventHandler() {
    }

    /** 缓存持续事件实例，主类 preInit 时调用 */
    public static void init() {
        ACTIVE_EVENTS.clear();
        for (WarpEvents entry : WarpEvents.values()) {
            IEventWarp event = entry.instantiate();
            if (event instanceof IActionWarpEvent
                    || event instanceof ITimerWarpEvent
                    || event instanceof IWorldTickWarpEvent) {
                ACTIVE_EVENTS.add(event);
            }
        }
    }

    private static boolean isEligible(EntityPlayer player) {
        if (!POConfig.WarpEventSwitch.enableWarpEvents) {
            return false;
        }
        if (POConfig.WarpEventSwitch.wussMode) {
            return false;
        }
        return !player.capabilities.isCreativeMode && !player.isSpectator();
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        World world = player.world;
        if (world.isRemote || !isEligible(player)) {
            return;
        }

        // 1. 污染→扭曲累积（每 20 tick 一次，降低 API 调用开销）
        if (player.ticksExisted % 20 == 0) {
            FluxWarpManager.accumulateWarpFromFlux(player);
        }

        // 2. 触发判定入队
        int totalWarp = FluxWarpManager.getTotalWarp(player);
        if (player.ticksExisted % POConfig.WarpEventSwitch.checkInterval == 0 && totalWarp > 0) {
            int chance = (int) Math.sqrt(totalWarp);
            if (world.rand.nextInt(100) <= chance) {
                IEventWarp picked = pickEvent(world, totalWarp);
                if (picked != null) {
                    WarpQueue.queue(player, picked.getName());
                    FluxWarpManager.consumeTempWarp(player, picked.getCost());
                }
            }
        }

        // 3. 出队执行
        if (player.ticksExisted % POConfig.WarpEventSwitch.dequeueInterval == 0 && world.rand.nextBoolean()) {
            String name = WarpQueue.dequeueRandom(player);
            if (name != null) {
                IEventWarp warpEvent = WarpEvents.byName(name);
                if (warpEvent != null && warpEvent.canDo(world, player)) {
                    warpEvent.doEvent(world, player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }
        if (!POConfig.WarpEventSwitch.enableWarpEvents || ACTIVE_EVENTS.isEmpty()) {
            return;
        }
        for (EntityPlayer player : event.world.playerEntities) {
            if (player.isDead) {
                continue;
            }
            for (IEventWarp activeEvent : ACTIVE_EVENTS) {
                activeEvent.onWorldTick(event.world, player);
            }
        }
    }

    /** 从启用且扭曲达标的事件中随机选一个 */
    private static IEventWarp pickEvent(World world, int totalWarp) {
        List<WarpEvents> eligible = new ArrayList<>();
        for (WarpEvents entry : WarpEvents.values()) {
            if (entry.isEnabled() && entry.getMinWarp() <= totalWarp) {
                eligible.add(entry);
            }
        }
        if (eligible.isEmpty()) {
            return null;
        }
        return eligible.get(world.rand.nextInt(eligible.size())).instantiate();
    }
}
