package meowmel.pollution.common.warpevent;

import meowmel.pollution.POConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 污染→扭曲联动：玩家身处高咒波(flux)区块时持续累积临时扭曲。
 * 只依赖 Thaumcraft 公共 API（thaumcraft.api.*），不触碰内部类。
 */
public final class FluxWarpManager {
    /** 每个玩家未满 1 点的扭曲累积缓冲（避免每 tick 浮点损失） */
    private static final Map<UUID, Float> FLUX_BUFFER = new HashMap<>();

    private FluxWarpManager() {
    }

    /**
     * 按玩家所在区块咒波累积临时扭曲（由调度器每 20 tick 调用一次）。
     * 咒波超过阈值时：每秒获得 (flux - threshold) * rate 点临时扭曲。
     */
    public static void accumulateWarpFromFlux(EntityPlayer player) {
        float threshold = POConfig.WarpEventSwitch.fluxThreshold;
        float flux = AuraHelper.getFlux(player.world, new BlockPos(player));
        if (flux <= threshold) {
            FLUX_BUFFER.remove(player.getUniqueID());
            return;
        }
        float gain = (float) ((flux - threshold) * POConfig.WarpEventSwitch.fluxWarpRate * 20);
        float buffered = FLUX_BUFFER.getOrDefault(player.getUniqueID(), 0F) + gain;
        int whole = (int) buffered;
        if (whole > 0) {
            addTempWarp(player, whole);
            FLUX_BUFFER.put(player.getUniqueID(), buffered - whole);
        } else {
            FLUX_BUFFER.put(player.getUniqueID(), buffered);
        }
    }

    /** 总扭曲 = 永久 + 普通 + 临时（TC 的 IPlayerWarp 不提供总和方法） */
    public static int getTotalWarp(EntityPlayer player) {
        IPlayerWarp warp = getWarp(player);
        return warp.get(EnumWarpType.PERMANENT)
                + warp.get(EnumWarpType.NORMAL)
                + warp.get(EnumWarpType.TEMPORARY);
    }

    public static int getTempWarp(EntityPlayer player) {
        return getWarp(player).get(EnumWarpType.TEMPORARY);
    }

    public static void addTempWarp(EntityPlayer player, int amount) {
        getWarp(player).add(EnumWarpType.TEMPORARY, amount);
    }

    /** 扣除临时扭曲，返回实际扣除量（不会动 NORMAL/PERMANENT 扭曲） */
    public static int consumeTempWarp(EntityPlayer player, int amount) {
        int temp = getTempWarp(player);
        if (temp <= 0) {
            return 0;
        }
        int consumed = Math.min(temp, amount);
        getWarp(player).reduce(EnumWarpType.TEMPORARY, consumed);
        return consumed;
    }

    private static IPlayerWarp getWarp(EntityPlayer player) {
        return ThaumcraftCapabilities.getWarp(player);
    }
}
