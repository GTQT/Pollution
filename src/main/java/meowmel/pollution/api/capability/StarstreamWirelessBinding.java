package meowmel.pollution.api.capability;

import meowmel.pollution.common.metatileentity.multiblock.astral.ConstellationTowerDefinition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Persistent consumer-side binding for the Starstream wireless network.
 *
 * <p>A future machine stores one instance, serializes it with its own NBT and
 * calls {@link #requestEnergy(World, BlockPos, String, long, boolean)} on the
 * server. The binding keeps a stable terminal identity, remembers the network
 * selected with a linker and automatically changes to the nearest loaded core
 * or relay belonging to that network.</p>
 */
public final class StarstreamWirelessBinding {

    private static final String NBT_TERMINAL_ID_MOST = "TerminalIdMost";
    private static final String NBT_TERMINAL_ID_LEAST = "TerminalIdLeast";
    private static final String NBT_NETWORK_ID_MOST = "NetworkIdMost";
    private static final String NBT_NETWORK_ID_LEAST = "NetworkIdLeast";
    private static final String NBT_PROVIDER_DIMENSION = "ProviderDimension";
    private static final String NBT_PROVIDER_POS = "ProviderPos";
    private static final long NO_PROVIDER_POS = Long.MIN_VALUE;
    private static final int DISCOVERY_INTERVAL = 20;

    private UUID terminalId = UUID.randomUUID();
    private UUID networkId;
    private int providerDimension = Integer.MIN_VALUE;
    private long providerPos = NO_PROVIDER_POS;
    private long nextDiscoveryTick;
    private long transferredThisTick;
    private long lastTransferTick = Long.MIN_VALUE;
    private StarstreamWirelessTerminalStatus status =
            StarstreamWirelessTerminalStatus.UNBOUND;

    public UUID getTerminalId() {
        return terminalId;
    }

    @Nullable
    public UUID getNetworkId() {
        return networkId;
    }

    public boolean isBound() {
        return networkId != null;
    }

    public StarstreamWirelessTerminalStatus getStatus() {
        return status;
    }

    public String getStatusTranslationKey() {
        return status.getTranslationKey();
    }

    public long getTransferredThisTick(World world) {
        refreshTransferWindow(world);
        return transferredThisTick;
    }

    public int getProviderDimension() {
        return providerDimension;
    }

    @Nullable
    public BlockPos getProviderPos() {
        return providerPos == NO_PROVIDER_POS ? null : BlockPos.fromLong(providerPos);
    }

    /** Selects a network and discards the old cached provider. */
    public void bind(UUID selectedNetworkId) {
        if (selectedNetworkId == null) {
            clear();
            return;
        }
        networkId = selectedNetworkId;
        clearProvider();
        nextDiscoveryTick = 0L;
        status = StarstreamWirelessTerminalStatus.DISCOVERING;
    }

    public void clear() {
        networkId = null;
        clearProvider();
        nextDiscoveryTick = 0L;
        status = StarstreamWirelessTerminalStatus.UNBOUND;
    }

    /**
     * Requests energy through the nearest usable loaded node. This method must
     * be called on the logical server; it never force-loads chunks.
     */
    public long requestEnergy(World world, BlockPos consumerPos, String constellationId,
                              long amount, boolean simulate) {
        if (world == null || world.isRemote) {
            status = StarstreamWirelessTerminalStatus.WRONG_SIDE;
            return 0L;
        }
        refreshTransferWindow(world);
        if (networkId == null) {
            status = StarstreamWirelessTerminalStatus.UNBOUND;
            return 0L;
        }
        if (amount <= 0L) return 0L;
        if (consumerPos == null) {
            status = StarstreamWirelessTerminalStatus.INVALID_REQUEST;
            return 0L;
        }
        if (ConstellationTowerDefinition.fromId(constellationId) == null) {
            status = StarstreamWirelessTerminalStatus.INVALID_CHANNEL;
            return 0L;
        }

        IStarstreamWirelessProvider provider = resolveOrDiscoverProvider(world, consumerPos);
        if (provider == null) return 0L;

        long received = provider.requestWirelessEnergy(consumerPos, networkId, terminalId,
                constellationId, amount, simulate);
        if (received > 0L) {
            status = StarstreamWirelessTerminalStatus.ONLINE;
            if (!simulate) {
                transferredThisTick += received;
                lastTransferTick = world.getTotalWorldTime();
            }
        } else if (!provider.isWirelessNetworkOnline()) {
            status = StarstreamWirelessTerminalStatus.NETWORK_OFFLINE;
            clearProvider();
            nextDiscoveryTick = world.getTotalWorldTime() + DISCOVERY_INTERVAL;
        } else {
            status = StarstreamWirelessTerminalStatus.WAITING_FOR_ENERGY;
        }
        return received;
    }

    /**
     * Atomically consumes all channels needed by one machine operation. No
     * channel is deducted when any requirement or bandwidth check fails.
     */
    public boolean consumeEnergy(World world, BlockPos consumerPos,
                                 Map<String, Long> requirements, boolean simulate) {
        if (world == null || world.isRemote) {
            status = StarstreamWirelessTerminalStatus.WRONG_SIDE;
            return false;
        }
        refreshTransferWindow(world);
        if (networkId == null) {
            status = StarstreamWirelessTerminalStatus.UNBOUND;
            return false;
        }
        if (requirements != null) {
            for (String constellationId : requirements.keySet()) {
                if (ConstellationTowerDefinition.fromId(constellationId) == null) {
                    status = StarstreamWirelessTerminalStatus.INVALID_CHANNEL;
                    return false;
                }
            }
        }
        long total = totalRequest(requirements);
        if (consumerPos == null || total <= 0L) {
            status = StarstreamWirelessTerminalStatus.INVALID_REQUEST;
            return false;
        }

        IStarstreamWirelessProvider provider = resolveOrDiscoverProvider(world, consumerPos);
        if (provider == null) return false;
        boolean consumed = provider.consumeWirelessEnergy(consumerPos, networkId, terminalId,
                requirements, simulate);
        if (consumed) {
            status = StarstreamWirelessTerminalStatus.ONLINE;
            if (!simulate) {
                transferredThisTick += total;
                lastTransferTick = world.getTotalWorldTime();
            }
        } else if (!provider.isWirelessNetworkOnline()) {
            status = StarstreamWirelessTerminalStatus.NETWORK_OFFLINE;
            clearProvider();
            nextDiscoveryTick = world.getTotalWorldTime() + DISCOVERY_INTERVAL;
        } else {
            status = StarstreamWirelessTerminalStatus.WAITING_FOR_ENERGY;
        }
        return consumed;
    }

    @Nullable
    private IStarstreamWirelessProvider resolveOrDiscoverProvider(World world,
                                                                  BlockPos consumerPos) {
        IStarstreamWirelessProvider provider = resolveCachedProvider(world, consumerPos);
        if (provider != null) return provider;
        long tick = world.getTotalWorldTime();
        status = StarstreamWirelessTerminalStatus.DISCOVERING;
        if (tick < nextDiscoveryTick) return null;
        ProviderSearchResult search = findNearestProvider(world, consumerPos);
        nextDiscoveryTick = tick + DISCOVERY_INTERVAL;
        if (search.provider == null) {
            status = search.networkFound
                    ? StarstreamWirelessTerminalStatus.NETWORK_OFFLINE
                    : StarstreamWirelessTerminalStatus.PROVIDER_UNAVAILABLE;
            clearProvider();
            return null;
        }
        cacheProvider(world, search.tile);
        return search.provider;
    }

    @Nullable
    private IStarstreamWirelessProvider resolveCachedProvider(World world, BlockPos consumerPos) {
        if (providerPos == NO_PROVIDER_POS
                || providerDimension != world.provider.getDimension()) return null;
        BlockPos cachedPos = BlockPos.fromLong(providerPos);
        if (!world.isBlockLoaded(cachedPos)) return null;
        TileEntity tile = world.getTileEntity(cachedPos);
        if (!(tile instanceof IStarstreamWirelessProvider)) return null;
        IStarstreamWirelessProvider provider = (IStarstreamWirelessProvider) tile;
        UUID providerNetwork = provider.getWirelessNetworkId();
        if (!networkId.equals(providerNetwork)
                || cachedPos.distanceSq(consumerPos) > square(provider.getWirelessRange())
                || !provider.isWirelessNetworkOnline()) return null;
        return provider;
    }

    private ProviderSearchResult findNearestProvider(World world, BlockPos consumerPos) {
        TileEntity nearestTile = null;
        IStarstreamWirelessProvider nearestProvider = null;
        double nearestDistance = Double.MAX_VALUE;
        boolean networkFound = false;
        for (TileEntity tile : world.loadedTileEntityList) {
            if (!(tile instanceof IStarstreamWirelessProvider)) continue;
            IStarstreamWirelessProvider provider = (IStarstreamWirelessProvider) tile;
            double distance = tile.getPos().distanceSq(consumerPos);
            if (distance > square(provider.getWirelessRange())) continue;
            if (!networkId.equals(provider.getWirelessNetworkId())) continue;
            networkFound = true;
            if (!provider.isWirelessNetworkOnline() || distance >= nearestDistance) continue;
            nearestDistance = distance;
            nearestTile = tile;
            nearestProvider = provider;
        }
        return new ProviderSearchResult(nearestTile, nearestProvider, networkFound);
    }

    private void cacheProvider(World world, TileEntity tile) {
        providerDimension = world.provider.getDimension();
        providerPos = tile.getPos().toLong();
    }

    private void clearProvider() {
        providerDimension = Integer.MIN_VALUE;
        providerPos = NO_PROVIDER_POS;
    }

    private void refreshTransferWindow(World world) {
        if (world == null) return;
        long tick = world.getTotalWorldTime();
        if (lastTransferTick != tick) transferredThisTick = 0L;
    }

    private static long square(int value) {
        return (long) value * value;
    }

    private static long totalRequest(Map<String, Long> requirements) {
        if (requirements == null || requirements.isEmpty()) return -1L;
        long total = 0L;
        for (Map.Entry<String, Long> entry : requirements.entrySet()) {
            Long amount = entry.getValue();
            if (ConstellationTowerDefinition.fromId(entry.getKey()) == null
                    || amount == null || amount < 0L
                    || Long.MAX_VALUE - total < amount) return -1L;
            total += amount;
        }
        return total;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong(NBT_TERMINAL_ID_MOST, terminalId.getMostSignificantBits());
        tag.setLong(NBT_TERMINAL_ID_LEAST, terminalId.getLeastSignificantBits());
        if (networkId != null) {
            tag.setLong(NBT_NETWORK_ID_MOST, networkId.getMostSignificantBits());
            tag.setLong(NBT_NETWORK_ID_LEAST, networkId.getLeastSignificantBits());
        }
        if (providerPos != NO_PROVIDER_POS) {
            tag.setInteger(NBT_PROVIDER_DIMENSION, providerDimension);
            tag.setLong(NBT_PROVIDER_POS, providerPos);
        }
        return tag;
    }

    public void deserializeNBT(NBTTagCompound tag) {
        if (tag == null) {
            clear();
            return;
        }
        if (tag.hasKey(NBT_TERMINAL_ID_MOST) && tag.hasKey(NBT_TERMINAL_ID_LEAST)) {
            UUID storedTerminal = new UUID(tag.getLong(NBT_TERMINAL_ID_MOST),
                    tag.getLong(NBT_TERMINAL_ID_LEAST));
            if (!isZero(storedTerminal)) terminalId = storedTerminal;
        }
        if (tag.hasKey(NBT_NETWORK_ID_MOST) && tag.hasKey(NBT_NETWORK_ID_LEAST)) {
            UUID storedNetwork = new UUID(tag.getLong(NBT_NETWORK_ID_MOST),
                    tag.getLong(NBT_NETWORK_ID_LEAST));
            networkId = isZero(storedNetwork) ? null : storedNetwork;
        } else {
            networkId = null;
        }
        if (networkId != null && tag.hasKey(NBT_PROVIDER_POS)) {
            providerDimension = tag.getInteger(NBT_PROVIDER_DIMENSION);
            providerPos = tag.getLong(NBT_PROVIDER_POS);
        } else {
            clearProvider();
        }
        nextDiscoveryTick = 0L;
        transferredThisTick = 0L;
        lastTransferTick = Long.MIN_VALUE;
        status = networkId == null ? StarstreamWirelessTerminalStatus.UNBOUND
                : StarstreamWirelessTerminalStatus.DISCOVERING;
    }

    private static boolean isZero(UUID id) {
        return id.getMostSignificantBits() == 0L && id.getLeastSignificantBits() == 0L;
    }

    private static final class ProviderSearchResult {

        private final TileEntity tile;
        private final IStarstreamWirelessProvider provider;
        private final boolean networkFound;

        private ProviderSearchResult(TileEntity tile,
                                     IStarstreamWirelessProvider provider,
                                     boolean networkFound) {
            this.tile = tile;
            this.provider = provider;
            this.networkFound = networkFound;
        }
    }
}
