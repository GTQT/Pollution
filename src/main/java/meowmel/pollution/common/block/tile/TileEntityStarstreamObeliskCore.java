package meowmel.pollution.common.block.tile;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import meowmel.pollution.api.capability.IConstellationEnergyBank;
import meowmel.pollution.api.capability.IStarstreamWirelessProvider;
import meowmel.pollution.api.capability.StarstreamNetworkConstants;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POStarstreamObelisk;
import meowmel.pollution.common.metatileentity.multiblock.astral.ConstellationTowerDefinition;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityStarstreamNexusObelisk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Persistent 16-channel energy bank carried by the physical obelisk core. */
public class TileEntityStarstreamObeliskCore extends TileEntity
        implements IConstellationEnergyBank, IStarstreamWirelessProvider, ITickable {

    public static final long CAPACITY_PER_CONSTELLATION = 1_677_721_600L;
    public static final long TOTAL_CAPACITY = CAPACITY_PER_CONSTELLATION
            * ConstellationTowerDefinition.values().length;
    private static final long NO_CONTROLLER_POS = Long.MIN_VALUE;
    private static final String NBT_ENERGIES = "ConstellationEnergies";
    private static final String NBT_CONTROLLER_POS = "ControllerPos";
    private static final String NBT_NETWORK_ID_MOST = "NetworkIdMost";
    private static final String NBT_NETWORK_ID_LEAST = "NetworkIdLeast";
    private static final String NBT_INBOUND_LINKS = "InboundTowerLinks";
    private static final String NBT_LINK_POS = "SourcePos";
    private static final String NBT_LINK_CONSTELLATION = "Constellation";
    private static final String NBT_INBOUND_RELAYS = "InboundRelays";
    private static final String NBT_RELAY_POS = "RelayPos";
    private static final String NBT_WIRELESS_ENABLED = "WirelessOutputEnabled";
    private static final String NBT_WIRELESS_LIMIT = "WirelessOutputLimit";
    private static final String NBT_WIRELESS_ACTIVE = "WirelessOutputActive";
    private static final String NBT_WIRELESS_RATE = "WirelessOutputRate";
    private static final String NBT_WIRELESS_RELAYS = "WirelessRelays";
    private static final String NBT_NODE_POS = "NodePos";
    private static final String NBT_NODE_DIMENSION = "NodeDimension";
    private static final String NBT_NODE_ID_MOST = "NodeIdMost";
    private static final String NBT_NODE_ID_LEAST = "NodeIdLeast";
    private static final String NBT_NODE_PARENT_POS = "ParentPos";
    private static final String NBT_NODE_PARENT_DIMENSION = "ParentDimension";
    private static final String NBT_NODE_DEPTH = "Depth";
    private static final String NBT_NODE_LAST_SEEN = "LastSeen";
    private static final String NBT_WIRELESS_TERMINALS = "WirelessTerminals";
    private static final String NBT_TERMINAL_ID_MOST = "TerminalIdMost";
    private static final String NBT_TERMINAL_ID_LEAST = "TerminalIdLeast";
    private static final String NBT_TERMINAL_DIMENSION = "TerminalDimension";
    private static final String NBT_TERMINAL_POS = "TerminalPos";
    private static final String NBT_TERMINAL_CHANNEL = "TerminalChannel";
    private static final String NBT_TERMINAL_LAST_SEEN = "TerminalLastSeen";
    private static final String NBT_TERMINAL_TRANSFERRED = "TerminalTransferred";

    private final long[] energies = new long[ConstellationTowerDefinition.values().length];
    private final long[] receivedThisTick = new long[ConstellationTowerDefinition.values().length];
    private final long[] wirelessOutputByChannel =
            new long[ConstellationTowerDefinition.values().length];
    private final Map<Long, String> inboundTowerLinks = new LinkedHashMap<>();
    private final Set<Long> inboundRelayLinks = new LinkedHashSet<>();
    private final Map<String, WirelessRelayRecord> wirelessRelays = new LinkedHashMap<>();
    private final Map<UUID, WirelessTerminalRecord> wirelessTerminals = new LinkedHashMap<>();
    private final Set<UUID> activeWirelessConsumers = new HashSet<>();
    private final Map<UUID, Long> wirelessOutputByConsumer = new LinkedHashMap<>();
    private long controllerPos = NO_CONTROLLER_POS;
    private UUID networkId = UUID.randomUUID();
    private long lastInputTick = Long.MIN_VALUE;
    private long lastWirelessOutputTick = Long.MIN_VALUE;
    private long lastWirelessTransferTick = Long.MIN_VALUE;
    private long lastWirelessSyncTick = Long.MIN_VALUE;
    private long wirelessOutputThisTick;
    private long lastCompletedWirelessOutput;
    private long wirelessOutputLimit = StarstreamNetworkConstants.NEXUS_WIRELESS_OUTPUT_PER_TICK;
    private long syncedWirelessOutput;
    private boolean wirelessOutputEnabled = true;
    private boolean syncedWirelessActive;
    private boolean lastSyncedWirelessActive;
    private float clientWirelessActivity;

    public void bindController(BlockPos position) {
        long packed = position.toLong();
        if (controllerPos == packed) return;
        controllerPos = packed;
        markDirty();
    }

    public UUID getNetworkId() {
        return networkId;
    }

    @Override
    public UUID getWirelessNetworkId() {
        return networkId;
    }

    @Override
    public int getWirelessRange() {
        return StarstreamNetworkConstants.NEXUS_WIRELESS_RANGE;
    }

    public boolean registerInboundTower(BlockPos sourcePos, String constellationId) {
        if (ConstellationTowerDefinition.fromId(constellationId) == null) return false;
        long packed = sourcePos.toLong();
        if (!inboundTowerLinks.containsKey(packed)
                && inboundTowerLinks.size() + inboundRelayLinks.size()
                >= StarstreamNetworkConstants.MAX_DIRECT_INPUT_LINKS) {
            return false;
        }
        inboundTowerLinks.put(packed, constellationId);
        markDirty();
        return true;
    }

    public void unregisterInboundTower(BlockPos sourcePos) {
        if (inboundTowerLinks.remove(sourcePos.toLong()) != null) markDirty();
    }

    public boolean registerInboundRelay(BlockPos sourcePos) {
        long packed = sourcePos.toLong();
        int total = inboundTowerLinks.size() + inboundRelayLinks.size();
        if (!inboundRelayLinks.contains(packed)
                && total >= StarstreamNetworkConstants.MAX_DIRECT_INPUT_LINKS) return false;
        inboundRelayLinks.add(packed);
        markDirty();
        return true;
    }

    public void unregisterInboundRelay(BlockPos sourcePos) {
        if (inboundRelayLinks.remove(sourcePos.toLong())) markDirty();
    }

    public int getRootRelayCount() {
        return inboundRelayLinks.size();
    }

    public int getOnlineRootRelayCount() {
        if (world == null) return 0;
        int online = 0;
        for (Long packed : inboundRelayLinks) {
            BlockPos relayPos = BlockPos.fromLong(packed);
            if (!world.isBlockLoaded(relayPos)) continue;
            TileEntity tile = world.getTileEntity(relayPos);
            if (tile instanceof TileEntityStarstreamRelay
                    && networkId.equals(((TileEntityStarstreamRelay) tile).resolveNetworkId())) {
                online++;
            }
        }
        return online;
    }

    public void heartbeatWirelessRelay(BlockPos relayPos, UUID relayId,
                                       BlockPos parentPos, int depth) {
        int dimension = world == null ? Integer.MIN_VALUE : world.provider.getDimension();
        heartbeatWirelessRelay(dimension, relayPos, relayId,
                dimension, parentPos, depth);
    }

    public void heartbeatWirelessRelay(int relayDimension, BlockPos relayPos, UUID relayId,
                                       int parentDimension, BlockPos parentPos, int depth) {
        if (world == null || world.isRemote || relayPos == null || relayId == null
                || parentPos == null || depth < 1
                || depth > StarstreamNetworkConstants.MAX_RELAY_HOPS) return;
        String key = relayKey(relayDimension, relayPos.toLong());
        WirelessRelayRecord existing = wirelessRelays.get(key);
        if (existing == null
                && wirelessRelays.size() >= StarstreamNetworkConstants.MAX_REGISTERED_RELAYS) return;
        long tick = world.getTotalWorldTime();
        boolean changed = existing == null || !relayId.equals(existing.relayId)
                || existing.dimension != relayDimension
                || existing.position != relayPos.toLong()
                || existing.parentDimension != parentDimension
                || existing.parentPos != parentPos.toLong() || existing.depth != depth;
        wirelessRelays.put(key, new WirelessRelayRecord(
                relayId, relayDimension, relayPos.toLong(), parentDimension,
                parentPos.toLong(), depth, tick));
        if (changed) markDirty();
    }

    private static String relayKey(int dimension, long packedPos) {
        return dimension + ":" + packedPos;
    }

    public int getRegisteredRelayCount() {
        return wirelessRelays.size();
    }

    public int getOnlineRelayCount() {
        if (world == null) return 0;
        long tick = world.getTotalWorldTime();
        int online = 0;
        for (WirelessRelayRecord record : wirelessRelays.values()) {
            if (tick - record.lastSeen <= StarstreamNetworkConstants.RELAY_OFFLINE_TIMEOUT) online++;
        }
        return online;
    }

    public int getMaximumRelayDepth() {
        int depth = 0;
        for (WirelessRelayRecord record : wirelessRelays.values()) {
            depth = Math.max(depth, record.depth);
        }
        return depth;
    }

    public int getRegisteredTerminalCount() {
        return wirelessTerminals.size();
    }

    public int getOnlineTerminalCount() {
        if (world == null) return 0;
        long tick = world.getTotalWorldTime();
        int online = 0;
        for (WirelessTerminalRecord record : wirelessTerminals.values()) {
            if (tick - record.lastSeen <= StarstreamNetworkConstants.TERMINAL_OFFLINE_TIMEOUT) online++;
        }
        return online;
    }

    public int cleanupStaleNetworkRecords() {
        if (world == null || world.isRemote) return 0;
        long cutoff = world.getTotalWorldTime() - StarstreamNetworkConstants.STALE_RECORD_TIMEOUT;
        int before = wirelessRelays.size() + wirelessTerminals.size();
        wirelessRelays.values().removeIf(record -> record.lastSeen < cutoff);
        wirelessTerminals.values().removeIf(record -> record.lastSeen < cutoff);
        int removed = before - wirelessRelays.size() - wirelessTerminals.size();
        if (removed > 0) {
            markDirty();
            syncToClient();
        }
        return removed;
    }

    public int getInboundLinkCount() {
        return inboundTowerLinks.size() + inboundRelayLinks.size();
    }

    public int getActiveInboundLinkCount() {
        if (world == null) return 0;
        int active = 0;
        for (Long packed : inboundTowerLinks.keySet()) {
            BlockPos sourcePos = BlockPos.fromLong(packed);
            if (!world.isBlockLoaded(sourcePos)
                    || !(world.getTileEntity(sourcePos) instanceof TileEntityConstellationCrystal)) continue;
            TileEntityConstellationCrystal source =
                    (TileEntityConstellationCrystal) world.getTileEntity(sourcePos);
            if (source.isOperationalTowerCore() && source.isLinkedTo(networkId, pos)) active++;
        }
        for (Long packed : inboundRelayLinks) {
            BlockPos sourcePos = BlockPos.fromLong(packed);
            if (!world.isBlockLoaded(sourcePos)
                    || !(world.getTileEntity(sourcePos) instanceof TileEntityStarstreamRelay)) continue;
            TileEntityStarstreamRelay relay = (TileEntityStarstreamRelay) world.getTileEntity(sourcePos);
            if (relay.isLinkedTo(networkId, pos, TileEntityStarstreamRelay.EndpointType.NEXUS)) active++;
        }
        return active;
    }

    @Override
    public void update() {
        if (world == null) return;
        if (world.isRemote) {
            float load = wirelessOutputLimit <= 0L ? 0.0F
                    : Math.min(1.0F, syncedWirelessOutput / (float) wirelessOutputLimit);
            float target = syncedWirelessActive
                    ? 0.25F + 0.75F * (float) Math.sqrt(load) : 0.0F;
            clientWirelessActivity += (target - clientWirelessActivity)
                    * (target > clientWirelessActivity ? 0.18F : 0.08F);
            if (clientWirelessActivity < 0.002F) clientWirelessActivity = 0.0F;
            return;
        }
        refreshWirelessOutputWindow();
        long tick = world.getTotalWorldTime();
        if (tick % 100L == 0L) pruneLoadedBrokenLinks();
        boolean active = lastWirelessTransferTick != Long.MIN_VALUE
                && tick - lastWirelessTransferTick <= 10L;
        if (active != lastSyncedWirelessActive
                || (active && tick - lastWirelessSyncTick >= 5L)) {
            lastSyncedWirelessActive = active;
            syncedWirelessActive = active;
            syncedWirelessOutput = Math.max(wirelessOutputThisTick,
                    lastCompletedWirelessOutput);
            lastWirelessSyncTick = tick;
            syncToClient();
        }
    }

    private void syncToClient() {
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private void pruneLoadedBrokenLinks() {
        boolean changed = false;
        Iterator<Map.Entry<Long, String>> towers = inboundTowerLinks.entrySet().iterator();
        while (towers.hasNext()) {
            Map.Entry<Long, String> entry = towers.next();
            BlockPos sourcePos = BlockPos.fromLong(entry.getKey());
            if (!world.isBlockLoaded(sourcePos)) continue;
            TileEntity tile = world.getTileEntity(sourcePos);
            if (!(tile instanceof TileEntityConstellationCrystal)
                    || !((TileEntityConstellationCrystal) tile)
                    .isLinkedTo(networkId, pos, TileEntityStarstreamRelay.EndpointType.NEXUS)) {
                towers.remove();
                changed = true;
            }
        }
        Iterator<Long> relays = inboundRelayLinks.iterator();
        while (relays.hasNext()) {
            BlockPos relayPos = BlockPos.fromLong(relays.next());
            if (!world.isBlockLoaded(relayPos)) continue;
            TileEntity tile = world.getTileEntity(relayPos);
            if (!(tile instanceof TileEntityStarstreamRelay)
                    || !((TileEntityStarstreamRelay) tile)
                    .isLinkedTo(networkId, pos, TileEntityStarstreamRelay.EndpointType.NEXUS)) {
                relays.remove();
                changed = true;
            }
        }
        Iterator<Map.Entry<String, WirelessRelayRecord>> registered =
                wirelessRelays.entrySet().iterator();
        while (registered.hasNext()) {
            Map.Entry<String, WirelessRelayRecord> entry = registered.next();
            WirelessRelayRecord record = entry.getValue();
            World relayWorld = DimensionManager.getWorld(record.dimension);
            if (relayWorld == null) continue;
            BlockPos relayPos = BlockPos.fromLong(record.position);
            if (!relayWorld.isBlockLoaded(relayPos)) continue;
            TileEntity tile = relayWorld.getTileEntity(relayPos);
            if (!(tile instanceof TileEntityStarstreamRelay)
                    || !record.relayId
                    .equals(((TileEntityStarstreamRelay) tile).getRelayId())) {
                registered.remove();
                changed = true;
            }
        }
        if (changed) {
            markDirty();
            syncToClient();
        }
    }

    public long getInputThisTick() {
        refreshInputWindow();
        long total = 0L;
        for (long value : receivedThisTick) total += value;
        return total;
    }

    public long getInputThisTick(String constellationId) {
        refreshInputWindow();
        int index = indexOf(constellationId);
        return index < 0 ? 0L : receivedThisTick[index];
    }

    public boolean isWirelessOutputEnabled() {
        return wirelessOutputEnabled;
    }

    @Override
    public boolean isWirelessNetworkOnline() {
        return wirelessOutputEnabled && isLinkedAndFormed();
    }

    public void setWirelessOutputEnabled(boolean enabled) {
        if (wirelessOutputEnabled == enabled) return;
        wirelessOutputEnabled = enabled;
        markDirty();
        syncToClient();
    }

    public long getWirelessOutputLimit() {
        return wirelessOutputLimit;
    }

    public void setWirelessOutputLimit(long limit) {
        long clamped = Math.max(0L, Math.min(
                StarstreamNetworkConstants.NEXUS_WIRELESS_OUTPUT_PER_TICK, limit));
        if (wirelessOutputLimit == clamped) return;
        wirelessOutputLimit = clamped;
        markDirty();
        syncToClient();
    }

    public long getWirelessOutputThisTick() {
        if (world != null && world.isRemote) return syncedWirelessOutput;
        refreshWirelessOutputWindow();
        return wirelessOutputThisTick;
    }

    public long getWirelessOutputThisTick(String constellationId) {
        refreshWirelessOutputWindow();
        int index = indexOf(constellationId);
        return index < 0 ? 0L : wirelessOutputByChannel[index];
    }

    public int getActiveWirelessConsumerCount() {
        refreshWirelessOutputWindow();
        return activeWirelessConsumers.size();
    }

    public long getWirelessOutputThisTick(UUID consumerId) {
        if (consumerId == null) return 0L;
        refreshWirelessOutputWindow();
        Long transferred = wirelessOutputByConsumer.get(consumerId);
        return transferred == null ? 0L : transferred;
    }

    public float getWirelessRenderActivity(float partialTicks) {
        return Math.max(0.0F, Math.min(1.0F, clientWirelessActivity));
    }

    /** Direct wireless access for a terminal inside the core's 128-block field. */
    @Override
    public long requestWirelessEnergy(BlockPos consumerPos, UUID requestedNetworkId,
                                      UUID consumerId, String constellationId,
                                      long amount, boolean simulate) {
        if (consumerPos == null || pos.distanceSq(consumerPos)
                > StarstreamNetworkConstants.NEXUS_WIRELESS_RANGE_SQUARED) return 0L;
        return provideWirelessEnergy(requestedNetworkId, consumerId,
                world.provider.getDimension(), consumerPos,
                constellationId, amount, simulate);
    }

    @Override
    public boolean consumeWirelessEnergy(BlockPos consumerPos, UUID requestedNetworkId,
                                         UUID consumerId, Map<String, Long> requirements,
                                         boolean simulate) {
        if (consumerPos == null || pos.distanceSq(consumerPos)
                > StarstreamNetworkConstants.NEXUS_WIRELESS_RANGE_SQUARED) return false;
        return provideWirelessEnergyBatch(requestedNetworkId, consumerId,
                world.provider.getDimension(), consumerPos, requirements, simulate);
    }

    /**
     * Authenticated central withdrawal used by relays. Relays validate their
     * own distance, identity and per-tick bandwidth before reaching this call.
     */
    public long provideWirelessEnergy(UUID requestedNetworkId, UUID consumerId,
                                      String constellationId, long amount,
                                      boolean simulate) {
        return provideWirelessEnergy(requestedNetworkId, consumerId,
                world == null ? Integer.MIN_VALUE : world.provider.getDimension(), null,
                constellationId, amount, simulate);
    }

    public long provideWirelessEnergy(UUID requestedNetworkId, UUID consumerId,
                                      int consumerDimension, BlockPos consumerPos,
                                      String constellationId, long amount,
                                      boolean simulate) {
        if (requestedNetworkId == null || !networkId.equals(requestedNetworkId)
                || consumerId == null || !wirelessOutputEnabled
                || !isLinkedAndFormed() || amount <= 0L) return 0L;
        int index = indexOf(constellationId);
        if (index < 0) return 0L;
        heartbeatWirelessTerminal(consumerId, consumerDimension, consumerPos,
                constellationId, 0L);
        refreshWirelessOutputWindow();
        long remaining = Math.max(0L, wirelessOutputLimit - wirelessOutputThisTick);
        long consumerUsed = wirelessOutputByConsumer.getOrDefault(consumerId, 0L);
        long consumerRemaining = Math.max(0L,
                StarstreamNetworkConstants.DEFAULT_CONSUMER_OUTPUT_PER_TICK - consumerUsed);
        long offered = Math.min(amount, Math.min(remaining, consumerRemaining));
        long extracted = extractConstellationEnergy(constellationId, offered, simulate);
        if (!simulate && extracted > 0L) {
            wirelessOutputThisTick += extracted;
            wirelessOutputByChannel[index] += extracted;
            wirelessOutputByConsumer.put(consumerId, consumerUsed + extracted);
            activeWirelessConsumers.add(consumerId);
            lastWirelessTransferTick = world.getTotalWorldTime();
            heartbeatWirelessTerminal(consumerId, consumerDimension, consumerPos,
                    constellationId, extracted);
        }
        return extracted;
    }

    /** Atomic multi-channel withdrawal used by recipe consumers and relays. */
    public boolean provideWirelessEnergyBatch(UUID requestedNetworkId, UUID consumerId,
                                              Map<String, Long> requirements,
                                              boolean simulate) {
        return provideWirelessEnergyBatch(requestedNetworkId, consumerId,
                world == null ? Integer.MIN_VALUE : world.provider.getDimension(), null,
                requirements, simulate);
    }

    public boolean provideWirelessEnergyBatch(UUID requestedNetworkId, UUID consumerId,
                                              int consumerDimension, BlockPos consumerPos,
                                              Map<String, Long> requirements,
                                              boolean simulate) {
        if (requestedNetworkId == null || !networkId.equals(requestedNetworkId)
                || consumerId == null || !wirelessOutputEnabled
                || !isLinkedAndFormed() || requirements == null
                || requirements.isEmpty()) return false;

        long[] requested = new long[energies.length];
        long total = 0L;
        for (Map.Entry<String, Long> entry : requirements.entrySet()) {
            int index = indexOf(entry.getKey());
            Long boxedAmount = entry.getValue();
            if (index < 0 || boxedAmount == null || boxedAmount < 0L) return false;
            long amount = boxedAmount;
            if (amount == 0L) continue;
            if (Long.MAX_VALUE - total < amount
                    || Long.MAX_VALUE - requested[index] < amount) return false;
            total += amount;
            requested[index] += amount;
        }
        if (total <= 0L) return false;

        heartbeatWirelessTerminal(consumerId, consumerDimension, consumerPos,
                requirements.size() == 1 ? requirements.keySet().iterator().next() : "multi", 0L);

        refreshWirelessOutputWindow();
        long consumerUsed = wirelessOutputByConsumer.getOrDefault(consumerId, 0L);
        if (total > wirelessOutputLimit - wirelessOutputThisTick
                || total > StarstreamNetworkConstants.DEFAULT_CONSUMER_OUTPUT_PER_TICK
                - consumerUsed) return false;
        for (int i = 0; i < requested.length; i++) {
            if (requested[i] > energies[i]) return false;
        }
        if (simulate) return true;

        for (int i = 0; i < requested.length; i++) {
            long amount = requested[i];
            if (amount <= 0L) continue;
            energies[i] -= amount;
            wirelessOutputByChannel[i] += amount;
        }
        wirelessOutputThisTick += total;
        wirelessOutputByConsumer.put(consumerId, consumerUsed + total);
        activeWirelessConsumers.add(consumerId);
        lastWirelessTransferTick = world.getTotalWorldTime();
        heartbeatWirelessTerminal(consumerId, consumerDimension, consumerPos,
                requirements.size() == 1 ? requirements.keySet().iterator().next() : "multi", total);
        markDirty();
        return true;
    }

    private void heartbeatWirelessTerminal(UUID consumerId, int dimension, BlockPos consumerPos,
                                           String channel, long transferred) {
        if (world == null || world.isRemote || consumerId == null) return;
        WirelessTerminalRecord old = wirelessTerminals.get(consumerId);
        if (old == null && wirelessTerminals.size()
                >= StarstreamNetworkConstants.MAX_REGISTERED_TERMINALS) return;
        long totalTransferred = old == null ? 0L : old.totalTransferred;
        totalTransferred = transferred > Long.MAX_VALUE - totalTransferred
                ? Long.MAX_VALUE : totalTransferred + transferred;
        wirelessTerminals.put(consumerId, new WirelessTerminalRecord(
                dimension, consumerPos == null ? Long.MIN_VALUE : consumerPos.toLong(),
                channel == null ? "" : channel, world.getTotalWorldTime(), totalTransferred));
        if (old == null || transferred > 0L) markDirty();
    }

    private void refreshWirelessOutputWindow() {
        if (world == null || world.isRemote) return;
        long tick = world.getTotalWorldTime();
        if (lastWirelessOutputTick != tick) {
            if (lastWirelessOutputTick != Long.MIN_VALUE) {
                lastCompletedWirelessOutput = wirelessOutputThisTick;
            }
            lastWirelessOutputTick = tick;
            wirelessOutputThisTick = 0L;
            activeWirelessConsumers.clear();
            wirelessOutputByConsumer.clear();
            for (int i = 0; i < wirelessOutputByChannel.length; i++) {
                wirelessOutputByChannel[i] = 0L;
            }
        }
    }

    public boolean onCoreRightClick(EntityPlayer player) {
        MetaTileEntityStarstreamNexusObelisk controller = findController();
        if (controller == null) {
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.machine.starstream_nexus.core.unlinked"), true);
            return true;
        }
        return controller.onCoreRightClick(player);
    }

    public boolean isLinkedAndFormed() {
        MetaTileEntityStarstreamNexusObelisk controller = findController();
        return controller != null && controller.isStructureFormed();
    }

    private MetaTileEntityStarstreamNexusObelisk findController() {
        if (world == null || !isObeliskCore()) return null;
        if (controllerPos != NO_CONTROLLER_POS) {
            MetaTileEntityStarstreamNexusObelisk controller = findControllerAt(BlockPos.fromLong(controllerPos));
            if (controller != null && controller.isCoreAt(pos)) return controller;
        }
        BlockPos controllerLevel = pos.down(2);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            MetaTileEntityStarstreamNexusObelisk controller = findControllerAt(controllerLevel.offset(facing, 14));
            if (controller != null && controller.isCoreAt(pos)) {
                if (!world.isRemote) bindController(controller.getPos());
                return controller;
            }
        }
        return null;
    }

    private MetaTileEntityStarstreamNexusObelisk findControllerAt(BlockPos candidate) {
        TileEntity tile = world.getTileEntity(candidate);
        if (!(tile instanceof IGregTechTileEntity)) return null;
        MetaTileEntity metaTileEntity = ((IGregTechTileEntity) tile).getMetaTileEntity();
        return metaTileEntity instanceof MetaTileEntityStarstreamNexusObelisk
                ? (MetaTileEntityStarstreamNexusObelisk) metaTileEntity : null;
    }

    private boolean isObeliskCore() {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == PollutionMetaBlocks.STARSTREAM_OBELISK
                && PollutionMetaBlocks.STARSTREAM_OBELISK.getState(state)
                == POStarstreamObelisk.ObeliskBlockType.OBELISK_CORE;
    }

    private static int indexOf(String constellationId) {
        ConstellationTowerDefinition definition = ConstellationTowerDefinition.fromId(constellationId);
        return definition == null ? -1 : definition.ordinal();
    }

    @Override
    public long getConstellationEnergyStored(String constellationId) {
        int index = indexOf(constellationId);
        return index < 0 ? 0L : energies[index];
    }

    @Override
    public long getConstellationEnergyCapacity(String constellationId) {
        return indexOf(constellationId) < 0 ? 0L : CAPACITY_PER_CONSTELLATION;
    }

    @Override
    public long getTotalConstellationEnergyStored() {
        long total = 0L;
        for (long energy : energies) total += energy;
        return total;
    }

    @Override
    public long getTotalConstellationEnergyCapacity() {
        return TOTAL_CAPACITY;
    }

    @Override
    public long receiveConstellationEnergy(String constellationId, long amount, boolean simulate) {
        int index = indexOf(constellationId);
        if (index < 0 || amount <= 0L) return 0L;
        refreshInputWindow();
        long accepted = Math.min(amount, CAPACITY_PER_CONSTELLATION - energies[index]);
        if (!simulate && accepted > 0L) {
            energies[index] += accepted;
            receivedThisTick[index] += accepted;
            markDirty();
        }
        return accepted;
    }

    private void refreshInputWindow() {
        if (world == null) return;
        long tick = world.getTotalWorldTime();
        if (lastInputTick != tick) {
            lastInputTick = tick;
            for (int i = 0; i < receivedThisTick.length; i++) receivedThisTick[i] = 0L;
        }
    }

    @Override
    public long extractConstellationEnergy(String constellationId, long amount, boolean simulate) {
        int index = indexOf(constellationId);
        if (index < 0 || amount <= 0L) return 0L;
        long extracted = Math.min(amount, energies[index]);
        if (!simulate && extracted > 0L) {
            energies[index] -= extracted;
            markDirty();
        }
        return extracted;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagCompound stored = new NBTTagCompound();
        for (ConstellationTowerDefinition definition : ConstellationTowerDefinition.values()) {
            long energy = energies[definition.ordinal()];
            if (energy > 0L) stored.setLong(definition.getId(), energy);
        }
        compound.setTag(NBT_ENERGIES, stored);
        if (controllerPos != NO_CONTROLLER_POS) compound.setLong(NBT_CONTROLLER_POS, controllerPos);
        compound.setLong(NBT_NETWORK_ID_MOST, networkId.getMostSignificantBits());
        compound.setLong(NBT_NETWORK_ID_LEAST, networkId.getLeastSignificantBits());
        compound.setBoolean(NBT_WIRELESS_ENABLED, wirelessOutputEnabled);
        compound.setLong(NBT_WIRELESS_LIMIT, wirelessOutputLimit);
        compound.setBoolean(NBT_WIRELESS_ACTIVE, syncedWirelessActive);
        compound.setLong(NBT_WIRELESS_RATE, world != null && world.isRemote
                ? syncedWirelessOutput
                : Math.max(wirelessOutputThisTick, lastCompletedWirelessOutput));
        NBTTagList links = new NBTTagList();
        for (Map.Entry<Long, String> entry : inboundTowerLinks.entrySet()) {
            NBTTagCompound link = new NBTTagCompound();
            link.setLong(NBT_LINK_POS, entry.getKey());
            link.setString(NBT_LINK_CONSTELLATION, entry.getValue());
            links.appendTag(link);
        }
        compound.setTag(NBT_INBOUND_LINKS, links);
        NBTTagList relays = new NBTTagList();
        for (Long packed : inboundRelayLinks) {
            NBTTagCompound relay = new NBTTagCompound();
            relay.setLong(NBT_RELAY_POS, packed);
            relays.appendTag(relay);
        }
        compound.setTag(NBT_INBOUND_RELAYS, relays);
        NBTTagList registeredRelays = new NBTTagList();
        for (Map.Entry<String, WirelessRelayRecord> entry : wirelessRelays.entrySet()) {
            WirelessRelayRecord record = entry.getValue();
            NBTTagCompound node = new NBTTagCompound();
            node.setInteger(NBT_NODE_DIMENSION, record.dimension);
            node.setLong(NBT_NODE_POS, record.position);
            node.setLong(NBT_NODE_ID_MOST, record.relayId.getMostSignificantBits());
            node.setLong(NBT_NODE_ID_LEAST, record.relayId.getLeastSignificantBits());
            node.setInteger(NBT_NODE_PARENT_DIMENSION, record.parentDimension);
            node.setLong(NBT_NODE_PARENT_POS, record.parentPos);
            node.setInteger(NBT_NODE_DEPTH, record.depth);
            node.setLong(NBT_NODE_LAST_SEEN, record.lastSeen);
            registeredRelays.appendTag(node);
        }
        compound.setTag(NBT_WIRELESS_RELAYS, registeredRelays);
        NBTTagList registeredTerminals = new NBTTagList();
        for (Map.Entry<UUID, WirelessTerminalRecord> entry : wirelessTerminals.entrySet()) {
            WirelessTerminalRecord record = entry.getValue();
            NBTTagCompound terminal = new NBTTagCompound();
            terminal.setLong(NBT_TERMINAL_ID_MOST, entry.getKey().getMostSignificantBits());
            terminal.setLong(NBT_TERMINAL_ID_LEAST, entry.getKey().getLeastSignificantBits());
            terminal.setInteger(NBT_TERMINAL_DIMENSION, record.dimension);
            terminal.setLong(NBT_TERMINAL_POS, record.position);
            terminal.setString(NBT_TERMINAL_CHANNEL, record.channel);
            terminal.setLong(NBT_TERMINAL_LAST_SEEN, record.lastSeen);
            terminal.setLong(NBT_TERMINAL_TRANSFERRED, record.totalTransferred);
            registeredTerminals.appendTag(terminal);
        }
        compound.setTag(NBT_WIRELESS_TERMINALS, registeredTerminals);
        // Deliberately do not write the legacy OutputRoutes list. Wireless
        // consumers pull from the central bank and are never stored as beams.
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagCompound stored = compound.getCompoundTag(NBT_ENERGIES);
        for (ConstellationTowerDefinition definition : ConstellationTowerDefinition.values()) {
            energies[definition.ordinal()] = Math.max(0L,
                    Math.min(CAPACITY_PER_CONSTELLATION, stored.getLong(definition.getId())));
        }
        controllerPos = compound.hasKey(NBT_CONTROLLER_POS)
                ? compound.getLong(NBT_CONTROLLER_POS) : NO_CONTROLLER_POS;
        if (compound.hasKey(NBT_NETWORK_ID_MOST) && compound.hasKey(NBT_NETWORK_ID_LEAST)) {
            networkId = new UUID(compound.getLong(NBT_NETWORK_ID_MOST),
                    compound.getLong(NBT_NETWORK_ID_LEAST));
        } else {
            networkId = UUID.randomUUID();
        }
        wirelessOutputEnabled = !compound.hasKey(NBT_WIRELESS_ENABLED)
                || compound.getBoolean(NBT_WIRELESS_ENABLED);
        wirelessOutputLimit = compound.hasKey(NBT_WIRELESS_LIMIT)
                ? Math.max(0L, Math.min(StarstreamNetworkConstants.NEXUS_WIRELESS_OUTPUT_PER_TICK,
                compound.getLong(NBT_WIRELESS_LIMIT)))
                : StarstreamNetworkConstants.NEXUS_WIRELESS_OUTPUT_PER_TICK;
        syncedWirelessActive = compound.getBoolean(NBT_WIRELESS_ACTIVE);
        syncedWirelessOutput = Math.max(0L, compound.getLong(NBT_WIRELESS_RATE));
        inboundTowerLinks.clear();
        NBTTagList links = compound.getTagList(NBT_INBOUND_LINKS, 10);
        for (int i = 0; i < links.tagCount()
                && inboundTowerLinks.size() < StarstreamNetworkConstants.MAX_DIRECT_INPUT_LINKS; i++) {
            NBTTagCompound link = links.getCompoundTagAt(i);
            String constellation = link.getString(NBT_LINK_CONSTELLATION);
            if (ConstellationTowerDefinition.fromId(constellation) != null) {
                inboundTowerLinks.put(link.getLong(NBT_LINK_POS), constellation);
            }
        }
        inboundRelayLinks.clear();
        NBTTagList relays = compound.getTagList(NBT_INBOUND_RELAYS, 10);
        for (int i = 0; i < relays.tagCount()
                && inboundTowerLinks.size() + inboundRelayLinks.size()
                < StarstreamNetworkConstants.MAX_DIRECT_INPUT_LINKS; i++) {
            inboundRelayLinks.add(relays.getCompoundTagAt(i).getLong(NBT_RELAY_POS));
        }
        wirelessRelays.clear();
        NBTTagList registeredRelays = compound.getTagList(NBT_WIRELESS_RELAYS, 10);
        for (int i = 0; i < registeredRelays.tagCount()
                && wirelessRelays.size() < StarstreamNetworkConstants.MAX_REGISTERED_RELAYS; i++) {
            NBTTagCompound node = registeredRelays.getCompoundTagAt(i);
            int depth = node.getInteger(NBT_NODE_DEPTH);
            if (depth < 1 || depth > StarstreamNetworkConstants.MAX_RELAY_HOPS) continue;
            UUID relayId = new UUID(node.getLong(NBT_NODE_ID_MOST),
                    node.getLong(NBT_NODE_ID_LEAST));
            int nodeDimension = node.hasKey(NBT_NODE_DIMENSION)
                    ? node.getInteger(NBT_NODE_DIMENSION) : world.provider.getDimension();
            int parentDimension = node.hasKey(NBT_NODE_PARENT_DIMENSION)
                    ? node.getInteger(NBT_NODE_PARENT_DIMENSION) : nodeDimension;
            long nodePos = node.getLong(NBT_NODE_POS);
            wirelessRelays.put(relayKey(nodeDimension, nodePos), new WirelessRelayRecord(
                    relayId, nodeDimension, nodePos, parentDimension,
                    node.getLong(NBT_NODE_PARENT_POS), depth,
                    node.getLong(NBT_NODE_LAST_SEEN)));
        }
        wirelessTerminals.clear();
        NBTTagList registeredTerminals = compound.getTagList(NBT_WIRELESS_TERMINALS, 10);
        for (int i = 0; i < registeredTerminals.tagCount()
                && wirelessTerminals.size()
                < StarstreamNetworkConstants.MAX_REGISTERED_TERMINALS; i++) {
            NBTTagCompound terminal = registeredTerminals.getCompoundTagAt(i);
            UUID terminalId = new UUID(terminal.getLong(NBT_TERMINAL_ID_MOST),
                    terminal.getLong(NBT_TERMINAL_ID_LEAST));
            if (terminalId.getMostSignificantBits() == 0L
                    && terminalId.getLeastSignificantBits() == 0L) continue;
            wirelessTerminals.put(terminalId, new WirelessTerminalRecord(
                    terminal.getInteger(NBT_TERMINAL_DIMENSION),
                    terminal.getLong(NBT_TERMINAL_POS),
                    terminal.getString(NBT_TERMINAL_CHANNEL),
                    terminal.getLong(NBT_TERMINAL_LAST_SEEN),
                    Math.max(0L, terminal.getLong(NBT_TERMINAL_TRANSFERRED))));
        }
        // The legacy OutputRoutes list is intentionally ignored. It belonged
        // to the removed nexus -> constellation tower output implementation.
        lastInputTick = Long.MIN_VALUE;
        lastWirelessOutputTick = Long.MIN_VALUE;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos).grow(16.0D, 3.0D, 16.0D).expand(0.0D, 28.0D, 0.0D);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = writeToNBT(new NBTTagCompound());
        tag.removeTag(NBT_WIRELESS_RELAYS);
        tag.removeTag(NBT_WIRELESS_TERMINALS);
        return tag;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager network, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    private static final class WirelessRelayRecord {

        private final UUID relayId;
        private final int dimension;
        private final long position;
        private final int parentDimension;
        private final long parentPos;
        private final int depth;
        private final long lastSeen;

        private WirelessRelayRecord(UUID relayId, int dimension, long position,
                                    int parentDimension, long parentPos,
                                    int depth, long lastSeen) {
            this.relayId = relayId;
            this.dimension = dimension;
            this.position = position;
            this.parentDimension = parentDimension;
            this.parentPos = parentPos;
            this.depth = depth;
            this.lastSeen = lastSeen;
        }
    }

    private static final class WirelessTerminalRecord {

        private final int dimension;
        private final long position;
        private final String channel;
        private final long lastSeen;
        private final long totalTransferred;

        private WirelessTerminalRecord(int dimension, long position, String channel,
                                       long lastSeen, long totalTransferred) {
            this.dimension = dimension;
            this.position = position;
            this.channel = channel;
            this.lastSeen = lastSeen;
            this.totalTransferred = totalTransferred;
        }
    }
}
