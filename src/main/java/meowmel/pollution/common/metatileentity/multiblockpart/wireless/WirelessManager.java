package meowmel.pollution.common.metatileentity.multiblockpart.wireless;

import meowmel.gtqtcore.GTQTCore;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WirelessManager {
    private static WirelessManager INSTANCE;

    private final Map<Integer, Long> cacheByDim = new ConcurrentHashMap<>();

    private WirelessManager() {

    }

    public static WirelessManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WirelessManager();
        }
        return INSTANCE;
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        GTQTCore.LOGGER.info("Magic Wireless Manager Initialized");
    }

    public long getEnergy(int dim) {
        return cacheByDim.getOrDefault(dim, 0L);
    }

    /**
     * 向无线网添加能量
     * @param dim    维度
     * @param amount 请求添加的量（负数视为无效）
     * @return 实际添加的量（amount>0时返回amount，否则返回0）
     */
    public long addEnergy(int dim, long amount) {
        if (amount <= 0) return 0L;
        long current = cacheByDim.getOrDefault(dim, 0L);
        cacheByDim.put(dim, current + amount);
        return amount;
    }

    /**
     * 从无线网移除能量
     * @param dim    维度
     * @param amount 请求移除的量（负数视为无效）
     * @return 实际移除的量（不超过现有能量）
     */
    public long removeEnergy(int dim, long amount) {
        if (amount <= 0) return 0L;
        long current = cacheByDim.getOrDefault(dim, 0L);
        long removed = Math.min(current, amount);
        long newValue = current - removed;
        if (newValue == 0) {
            cacheByDim.remove(dim);
        } else {
            cacheByDim.put(dim, newValue);
        }
        return removed;
    }

    /**
     * 设备请求能量（从无线网中取出）
     * @param dim     维度
     * @param request 请求量（负数视为无效）
     * @return 实际给予的量（不超过请求量且不超过当前缓存）
     */
    public long requestEnergy(int dim, long request) {
        if (request <= 0) return 0L;
        long current = cacheByDim.getOrDefault(dim, 0L);
        long give = Math.min(current, request);
        if (give == 0) return 0L;
        long newValue = current - give;
        if (newValue == 0) {
            cacheByDim.remove(dim);
        } else {
            cacheByDim.put(dim, newValue);
        }
        return give;
    }

    /**
     * 从世界数据中加载缓存（通常在服务器加载世界时调用一次）
     * @param world 服务端世界对象（建议使用世界的 overworld）
     */
    public void loadFromWorld(World world) {
        if (world.isRemote) return; // 仅服务端保存
        WirelessWorldData data = WirelessWorldData.get(world);
        cacheByDim.clear();
        cacheByDim.putAll(data.getData());
        GTQTCore.LOGGER.info("Loaded wireless energy data for {} dimensions", cacheByDim.size());
    }

    /**
     * 将当前缓存保存到世界数据中（通常在服务器保存世界时自动调用）
     * @param world 服务端世界对象
     */
    public void saveToWorld(World world) {
        if (world.isRemote) return;
        WirelessWorldData data = WirelessWorldData.get(world);
        Map<Integer, Long> storage = data.getData();
        storage.clear();
        storage.putAll(cacheByDim);
        data.markDirty();
        GTQTCore.LOGGER.debug("Saved wireless energy data for {} dimensions", storage.size());
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        World world = event.getWorld();
        if (world != null && !world.isRemote && world.provider.getDimension() == 0) {
            saveToWorld(world);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world != null && !world.isRemote && world.provider.getDimension() == 0) {
            loadFromWorld(world);
        }
    }
}