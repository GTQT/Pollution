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

    private final Map<Integer, Long> energyManaCacheByDim = new ConcurrentHashMap<>();
    private final Map<Integer, Long> manaPoolCacheByDim = new ConcurrentHashMap<>();

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

    /** Energy-type mana used by wireless/infinite mana hatches. */
    public long getEnergy(int dim) {
        return energyManaCacheByDim.getOrDefault(dim, 0L);
    }

    public long addEnergy(int dim, long amount) {
        return addToCache(energyManaCacheByDim, dim, amount);
    }

    public long removeEnergy(int dim, long amount) {
        return removeFromCache(energyManaCacheByDim, dim, amount);
    }

    public long requestEnergy(int dim, long request) {
        return removeFromCache(energyManaCacheByDim, dim, request);
    }

    /** Pure Botania mana used only by wireless mana-pool hatches. */
    public long getManaPool(int dim) {
        return manaPoolCacheByDim.getOrDefault(dim, 0L);
    }

    public long addManaPool(int dim, long amount) {
        return addToCache(manaPoolCacheByDim, dim, amount);
    }

    public long removeManaPool(int dim, long amount) {
        return removeFromCache(manaPoolCacheByDim, dim, amount);
    }

    public long requestManaPool(int dim, long request) {
        return removeFromCache(manaPoolCacheByDim, dim, request);
    }

    private long addToCache(Map<Integer, Long> cache, int dim, long amount) {
        if (amount <= 0L) return 0L;
        final long[] accepted = {amount};
        cache.merge(dim, amount, (current, added) -> {
            long free = Long.MAX_VALUE - current;
            accepted[0] = Math.min(added, free);
            return current + accepted[0];
        });
        return accepted[0];
    }

    private long removeFromCache(Map<Integer, Long> cache, int dim, long amount) {
        if (amount <= 0L) return 0L;
        long current = cache.getOrDefault(dim, 0L);
        long removed = Math.min(current, amount);
        if (removed == 0L) return 0L;
        long remaining = current - removed;
        if (remaining == 0L) {
            cache.remove(dim);
        } else {
            cache.put(dim, remaining);
        }
        return removed;
    }

    public void loadFromWorld(World world) {
        if (world.isRemote) return;
        WirelessWorldData data = WirelessWorldData.get(world);
        energyManaCacheByDim.clear();
        manaPoolCacheByDim.clear();
        energyManaCacheByDim.putAll(data.getEnergyManaData());
        manaPoolCacheByDim.putAll(data.getManaPoolData());
        GTQTCore.LOGGER.info("Loaded wireless mana data: {} energy dimensions, {} pool dimensions",
                energyManaCacheByDim.size(), manaPoolCacheByDim.size());
    }

    public void saveToWorld(World world) {
        if (world.isRemote) return;
        WirelessWorldData data = WirelessWorldData.get(world);
        Map<Integer, Long> energyStorage = data.getEnergyManaData();
        Map<Integer, Long> manaPoolStorage = data.getManaPoolData();
        energyStorage.clear();
        manaPoolStorage.clear();
        energyStorage.putAll(energyManaCacheByDim);
        manaPoolStorage.putAll(manaPoolCacheByDim);
        data.markDirty();
        GTQTCore.LOGGER.debug("Saved wireless mana data: {} energy dimensions, {} pool dimensions",
                energyStorage.size(), manaPoolStorage.size());
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
