package meowmel.pollution.common.block.tile;

import meowmel.pollution.api.capability.StarstreamNetworkConstants;
import meowmel.pollution.api.capability.IStarstreamWirelessProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Stateless constellation-energy repeater. It stores route metadata but no
 * energy; accepted energy is committed directly to the next loaded endpoint.
 */
public class TileEntityStarstreamRelay extends TileEntity
        implements IStarstreamWirelessProvider, ITickable {

    private static final long NO_OUTPUT_POS = Long.MIN_VALUE;
    private static final String NBT_ID_MOST = "RelayIdMost";
    private static final String NBT_ID_LEAST = "RelayIdLeast";
    private static final String NBT_OUTPUT_DIMENSION = "OutputDimension";
    private static final String NBT_OUTPUT_POS = "OutputPos";
    private static final String NBT_OUTPUT_ID_MOST = "OutputIdMost";
    private static final String NBT_OUTPUT_ID_LEAST = "OutputIdLeast";
    private static final String NBT_OUTPUT_TYPE = "OutputType";
    private static final String NBT_INPUTS = "Inputs";
    private static final String NBT_INPUT_POS = "Position";
    private static final String NBT_INPUT_TYPE = "Type";
    private static final String NBT_WIRELESS_ACTIVE = "WirelessActive";
    private static final String NBT_WIRELESS_RATE = "WirelessRate";
    private static final String NBT_INPUT_ACTIVE = "InputActive";
    private static final String NBT_INPUT_RATE = "InputRate";

    private UUID relayId = UUID.randomUUID();
    private int outputDimension = Integer.MIN_VALUE;
    private long outputPos = NO_OUTPUT_POS;
    private long outputIdMost;
    private long outputIdLeast;
    private EndpointType outputType = EndpointType.NONE;
    private final Map<Long, EndpointType> inboundEndpoints = new LinkedHashMap<>();
    private long lastTransferTick = Long.MIN_VALUE;
    private long lastInputTransferTick = Long.MIN_VALUE;
    private long transferredThisTick;
    private long lastCompletedInputTransfer;
    private long syncedInputTransfer;
    private boolean syncedInputActive;
    private boolean lastSyncedInputActive;
    private float clientInputActivity;
    private long lastWirelessOutputTick = Long.MIN_VALUE;
    private long lastWirelessTransferTick = Long.MIN_VALUE;
    private long lastWirelessSyncTick = Long.MIN_VALUE;
    private long wirelessOutputThisTick;
    private long lastCompletedWirelessOutput;
    private long syncedWirelessOutput;
    private boolean syncedWirelessActive;
    private boolean lastSyncedWirelessActive;
    private float clientWirelessActivity;
    private RouteStatus routeStatus = RouteStatus.UNLINKED;

    public UUID getRelayId() {
        return relayId;
    }

    public boolean hasOutput() {
        return outputType != EndpointType.NONE && outputPos != NO_OUTPUT_POS;
    }

    public BlockPos getOutputPos() {
        return hasOutput() ? BlockPos.fromLong(outputPos) : null;
    }

    public EndpointType getOutputType() {
        return outputType;
    }

    public int getOutputDimension() {
        return outputDimension;
    }

    public int getInboundCount() {
        return inboundEndpoints.size();
    }

    public long getTransferredThisTick() {
        if (world != null && world.isRemote) return syncedInputTransfer;
        refreshTransferWindow();
        return transferredThisTick;
    }

    public float getInputRenderActivity(float partialTicks) {
        return Math.max(0.0F, Math.min(1.0F, clientInputActivity));
    }

    public long getWirelessOutputThisTick() {
        if (world != null && world.isRemote) return syncedWirelessOutput;
        refreshWirelessOutputWindow();
        return wirelessOutputThisTick;
    }

    public float getWirelessRenderActivity(float partialTicks) {
        return Math.max(0.0F, Math.min(1.0F, clientWirelessActivity));
    }

    protected void recordWirelessOutput(long amount) {
        if (amount <= 0L || world == null || world.isRemote) return;
        refreshWirelessOutputWindow();
        wirelessOutputThisTick += amount;
        lastWirelessTransferTick = world.getTotalWorldTime();
    }

    public String getRouteStatusTranslationKey() {
        return "pollution.starstream_relay.status." + routeStatus.key;
    }

    public boolean registerInbound(BlockPos sourcePos, EndpointType sourceType) {
        if (sourceType == EndpointType.NONE) return false;
        long packed = sourcePos.toLong();
        if (!inboundEndpoints.containsKey(packed)
                && inboundEndpoints.size() >= StarstreamNetworkConstants.MAX_RELAY_INPUTS) {
            return false;
        }
        inboundEndpoints.put(packed, sourceType);
        markDirty();
        return true;
    }

    public void unregisterInbound(BlockPos sourcePos) {
        if (inboundEndpoints.remove(sourcePos.toLong()) != null) markDirty();
    }

    public boolean bindOutput(int dimension, BlockPos targetPos, UUID targetId, EndpointType type) {
        if (type != EndpointType.RELAY && type != EndpointType.NEXUS) return false;
        if (hasOutput() && isLinkedTo(targetId, targetPos, type)) return true;
        unregisterFromOldOutput();
        outputDimension = dimension;
        outputPos = targetPos.toLong();
        outputIdMost = targetId.getMostSignificantBits();
        outputIdLeast = targetId.getLeastSignificantBits();
        outputType = type;
        routeStatus = RouteStatus.IDLE;
        resetTransferWindow();
        markDirty();
        syncToClient();
        return true;
    }

    public void clearOutput() {
        unregisterFromOldOutput();
        outputDimension = Integer.MIN_VALUE;
        outputPos = NO_OUTPUT_POS;
        outputIdMost = 0L;
        outputIdLeast = 0L;
        outputType = EndpointType.NONE;
        routeStatus = RouteStatus.UNLINKED;
        resetTransferWindow();
        markDirty();
        syncToClient();
    }

    public boolean isLinkedTo(UUID targetId, BlockPos targetPos, EndpointType type) {
        return hasOutput() && outputType == type && outputPos == targetPos.toLong()
                && outputIdMost == targetId.getMostSignificantBits()
                && outputIdLeast == targetId.getLeastSignificantBits();
    }

    private void unregisterFromOldOutput() {
        if (world == null || !hasOutput() || world.provider.getDimension() != outputDimension) return;
        BlockPos oldPos = BlockPos.fromLong(outputPos);
        if (!world.isBlockLoaded(oldPos)) return;
        TileEntity target = world.getTileEntity(oldPos);
        if (outputType == EndpointType.RELAY && target instanceof TileEntityStarstreamRelay) {
            ((TileEntityStarstreamRelay) target).unregisterInbound(pos);
        } else if (outputType == EndpointType.NEXUS && target instanceof TileEntityStarstreamObeliskCore) {
            ((TileEntityStarstreamObeliskCore) target).unregisterInboundRelay(pos);
        }
    }

    public boolean routeContains(UUID endpointId) {
        TileEntityStarstreamRelay current = this;
        Set<UUID> visited = new HashSet<>();
        for (int hop = 0; hop <= StarstreamNetworkConstants.MAX_RELAY_HOPS; hop++) {
            if (!visited.add(current.relayId)) return false;
            if (current.relayId.equals(endpointId)) return true;
            if (current.world == null || current.outputType != EndpointType.RELAY
                    || current.world.provider.getDimension() != current.outputDimension) return false;
            BlockPos nextPos = BlockPos.fromLong(current.outputPos);
            if (!current.world.isBlockLoaded(nextPos)) return false;
            TileEntity next = current.world.getTileEntity(nextPos);
            if (!(next instanceof TileEntityStarstreamRelay)) return false;
            TileEntityStarstreamRelay nextRelay = (TileEntityStarstreamRelay) next;
            if (!new UUID(current.outputIdMost, current.outputIdLeast).equals(nextRelay.relayId)) return false;
            current = nextRelay;
        }
        return false;
    }

    public UUID resolveNetworkId() {
        TileEntityStarstreamObeliskCore nexus = findNetworkCore();
        return nexus != null && nexus.isWirelessNetworkOnline() ? nexus.getNetworkId() : null;
    }

    public TileEntityStarstreamObeliskCore findNetworkCore() {
        TileEntityStarstreamRelay current = this;
        Set<UUID> visited = new HashSet<>();
        for (int hop = 0; hop < StarstreamNetworkConstants.MAX_RELAY_HOPS; hop++) {
            if (!visited.add(current.relayId) || !current.hasOutput()
                    || current.world == null
                    || current.world.provider.getDimension() != current.outputDimension) return null;
            BlockPos targetPos = BlockPos.fromLong(current.outputPos);
            if (current.pos.distanceSq(targetPos)
                    > StarstreamNetworkConstants.DIRECT_LINK_RANGE_SQUARED
                    || !current.world.isBlockLoaded(targetPos)) return null;
            TileEntity target = current.world.getTileEntity(targetPos);
            UUID expectedId = new UUID(current.outputIdMost, current.outputIdLeast);
            if (current.outputType == EndpointType.NEXUS
                    && target instanceof TileEntityStarstreamObeliskCore) {
                TileEntityStarstreamObeliskCore nexus = (TileEntityStarstreamObeliskCore) target;
                return expectedId.equals(nexus.getNetworkId()) && nexus.isLinkedAndFormed()
                        ? nexus : null;
            }
            if (current.outputType != EndpointType.RELAY
                    || !(target instanceof TileEntityStarstreamRelay)) return null;
            TileEntityStarstreamRelay next = (TileEntityStarstreamRelay) target;
            if (!expectedId.equals(next.relayId)) return null;
            current = next;
        }
        return null;
    }

    @Override
    public UUID getWirelessNetworkId() {
        TileEntityStarstreamObeliskCore nexus = findNetworkCore();
        return nexus == null ? null : nexus.getNetworkId();
    }

    @Override
    public int getWirelessRange() {
        return StarstreamNetworkConstants.RELAY_WIRELESS_RANGE;
    }

    @Override
    public boolean isWirelessNetworkOnline() {
        return resolveNetworkId() != null;
    }

    /** Number of relay nodes from this node to the nexus, or -1 if offline. */
    public int getNetworkDepth() {
        TileEntityStarstreamRelay current = this;
        Set<UUID> visited = new HashSet<>();
        for (int depth = 1; depth <= StarstreamNetworkConstants.MAX_RELAY_HOPS; depth++) {
            if (!visited.add(current.relayId) || !current.hasOutput()
                    || current.world == null
                    || current.world.provider.getDimension() != current.outputDimension) return -1;
            BlockPos targetPos = BlockPos.fromLong(current.outputPos);
            if (current.pos.distanceSq(targetPos)
                    > StarstreamNetworkConstants.DIRECT_LINK_RANGE_SQUARED
                    || !current.world.isBlockLoaded(targetPos)) return -1;
            TileEntity target = current.world.getTileEntity(targetPos);
            UUID expectedId = new UUID(current.outputIdMost, current.outputIdLeast);
            if (current.outputType == EndpointType.NEXUS
                    && target instanceof TileEntityStarstreamObeliskCore) {
                TileEntityStarstreamObeliskCore nexus = (TileEntityStarstreamObeliskCore) target;
                return expectedId.equals(nexus.getNetworkId()) && nexus.isLinkedAndFormed()
                        ? depth : -1;
            }
            if (current.outputType != EndpointType.RELAY
                    || !(target instanceof TileEntityStarstreamRelay)) return -1;
            TileEntityStarstreamRelay next = (TileEntityStarstreamRelay) target;
            if (!expectedId.equals(next.relayId)) return -1;
            current = next;
        }
        return -1;
    }

    /** Wireless terminal entry point. Energy remains stored in the nexus. */
    @Override
    public long requestWirelessEnergy(BlockPos consumerPos, UUID networkId, UUID consumerId,
                                      String constellationId, long amount, boolean simulate) {
        if (consumerPos == null || pos.distanceSq(consumerPos)
                > StarstreamNetworkConstants.RELAY_WIRELESS_RANGE_SQUARED) return 0L;
        UUID resolved = resolveNetworkId();
        if (resolved == null || !resolved.equals(networkId)) return 0L;
        return requestWirelessEnergy(networkId, consumerId,
                world.provider.getDimension(), consumerPos, constellationId, amount,
                simulate, new HashSet<>(), 0);
    }

    @Override
    public boolean consumeWirelessEnergy(BlockPos consumerPos, UUID networkId, UUID consumerId,
                                         Map<String, Long> requirements, boolean simulate) {
        if (consumerPos == null || pos.distanceSq(consumerPos)
                > StarstreamNetworkConstants.RELAY_WIRELESS_RANGE_SQUARED) return false;
        UUID resolved = resolveNetworkId();
        if (resolved == null || !resolved.equals(networkId)) return false;
        return consumeWirelessEnergy(networkId, consumerId,
                world.provider.getDimension(), consumerPos, requirements, simulate,
                new HashSet<>(), 0);
    }

    private boolean consumeWirelessEnergy(UUID networkId, UUID consumerId,
                                          int consumerDimension, BlockPos consumerPos,
                                          Map<String, Long> requirements, boolean simulate,
                                          Set<UUID> visited, int hop) {
        refreshWirelessOutputWindow();
        long total = totalRequest(requirements);
        if (consumerId == null || total <= 0L || !visited.add(relayId)
                || hop >= StarstreamNetworkConstants.MAX_RELAY_HOPS || !hasOutput()) return false;
        if (total > StarstreamNetworkConstants.RELAY_WIRELESS_OUTPUT_PER_TICK
                - wirelessOutputThisTick) return false;
        if (world == null || world.provider.getDimension() != outputDimension) return false;
        BlockPos targetPos = BlockPos.fromLong(outputPos);
        if (pos.distanceSq(targetPos) > StarstreamNetworkConstants.DIRECT_LINK_RANGE_SQUARED
                || !world.isBlockLoaded(targetPos)) return false;

        TileEntity target = world.getTileEntity(targetPos);
        UUID expectedId = new UUID(outputIdMost, outputIdLeast);
        boolean consumed;
        if (outputType == EndpointType.NEXUS
                && target instanceof TileEntityStarstreamObeliskCore) {
            TileEntityStarstreamObeliskCore nexus = (TileEntityStarstreamObeliskCore) target;
            if (!expectedId.equals(nexus.getNetworkId()) || !networkId.equals(expectedId)) return false;
            consumed = nexus.provideWirelessEnergyBatch(
                    networkId, consumerId, consumerDimension, consumerPos,
                    requirements, simulate);
        } else if (outputType == EndpointType.RELAY
                && target instanceof TileEntityStarstreamRelay) {
            TileEntityStarstreamRelay relay = (TileEntityStarstreamRelay) target;
            if (!expectedId.equals(relay.relayId)) return false;
            consumed = relay.consumeWirelessEnergy(networkId, consumerId,
                    consumerDimension, consumerPos, requirements,
                    simulate, visited, hop + 1);
        } else {
            return false;
        }
        if (consumed && !simulate) {
            wirelessOutputThisTick += total;
            lastWirelessTransferTick = world.getTotalWorldTime();
        }
        return consumed;
    }

    private static long totalRequest(Map<String, Long> requirements) {
        if (requirements == null || requirements.isEmpty()) return -1L;
        long total = 0L;
        for (Map.Entry<String, Long> entry : requirements.entrySet()) {
            Long amount = entry.getValue();
            if (entry.getKey() == null || amount == null || amount < 0L
                    || Long.MAX_VALUE - total < amount) return -1L;
            total += amount;
        }
        return total;
    }

    private long requestWirelessEnergy(UUID networkId, UUID consumerId,
                                       int consumerDimension, BlockPos consumerPos,
                                       String constellationId, long amount, boolean simulate,
                                       Set<UUID> visited, int hop) {
        refreshWirelessOutputWindow();
        if (amount <= 0L || !visited.add(relayId)
                || hop >= StarstreamNetworkConstants.MAX_RELAY_HOPS || !hasOutput()) return 0L;
        if (world == null || world.provider.getDimension() != outputDimension) return 0L;
        BlockPos targetPos = BlockPos.fromLong(outputPos);
        if (pos.distanceSq(targetPos) > StarstreamNetworkConstants.DIRECT_LINK_RANGE_SQUARED
                || !world.isBlockLoaded(targetPos)) return 0L;

        long remaining = Math.max(0L,
                StarstreamNetworkConstants.RELAY_WIRELESS_OUTPUT_PER_TICK
                        - wirelessOutputThisTick);
        long offered = Math.min(amount, remaining);
        if (offered <= 0L) return 0L;

        TileEntity target = world.getTileEntity(targetPos);
        UUID expectedId = new UUID(outputIdMost, outputIdLeast);
        long extracted;
        if (outputType == EndpointType.NEXUS
                && target instanceof TileEntityStarstreamObeliskCore) {
            TileEntityStarstreamObeliskCore nexus = (TileEntityStarstreamObeliskCore) target;
            if (!expectedId.equals(nexus.getNetworkId()) || !networkId.equals(expectedId)) return 0L;
            extracted = nexus.provideWirelessEnergy(
                    networkId, consumerId, consumerDimension, consumerPos,
                    constellationId, offered, simulate);
        } else if (outputType == EndpointType.RELAY
                && target instanceof TileEntityStarstreamRelay) {
            TileEntityStarstreamRelay relay = (TileEntityStarstreamRelay) target;
            if (!expectedId.equals(relay.relayId)) return 0L;
            extracted = relay.requestWirelessEnergy(networkId, consumerId,
                    consumerDimension, consumerPos, constellationId,
                    offered, simulate, visited, hop + 1);
        } else {
            return 0L;
        }

        if (!simulate && extracted > 0L) {
            wirelessOutputThisTick += extracted;
            lastWirelessTransferTick = world.getTotalWorldTime();
        }
        return extracted;
    }

    public long forwardEnergy(String constellationId, long amount, boolean simulate) {
        return forwardEnergy(constellationId, amount, simulate, new HashSet<>(), 0);
    }

    private long forwardEnergy(String constellationId, long amount, boolean simulate,
                               Set<UUID> visited, int hop) {
        refreshTransferWindow();
        if (amount <= 0L) return 0L;
        if (!visited.add(relayId) || hop >= StarstreamNetworkConstants.MAX_RELAY_HOPS) {
            routeStatus = RouteStatus.LOOP_OR_TOO_DEEP;
            return 0L;
        }
        if (!hasOutput()) {
            routeStatus = RouteStatus.UNLINKED;
            return 0L;
        }
        if (world == null || world.provider.getDimension() != outputDimension) {
            routeStatus = RouteStatus.WRONG_DIMENSION;
            return 0L;
        }
        BlockPos targetPos = BlockPos.fromLong(outputPos);
        if (pos.distanceSq(targetPos) > StarstreamNetworkConstants.DIRECT_LINK_RANGE_SQUARED) {
            routeStatus = RouteStatus.OUT_OF_RANGE;
            return 0L;
        }
        if (!world.isBlockLoaded(targetPos)) {
            routeStatus = RouteStatus.ENDPOINT_UNLOADED;
            return 0L;
        }

        long remaining = Math.max(0L,
                StarstreamNetworkConstants.RELAY_TRANSFER_PER_TICK - transferredThisTick);
        long offered = Math.min(amount, remaining);
        if (offered <= 0L) {
            routeStatus = RouteStatus.THROUGHPUT_LIMIT;
            return 0L;
        }

        TileEntity target = world.getTileEntity(targetPos);
        UUID expectedId = new UUID(outputIdMost, outputIdLeast);
        long accepted;
        if (outputType == EndpointType.NEXUS && target instanceof TileEntityStarstreamObeliskCore) {
            TileEntityStarstreamObeliskCore nexus = (TileEntityStarstreamObeliskCore) target;
            if (!expectedId.equals(nexus.getNetworkId())) {
                routeStatus = RouteStatus.TARGET_REPLACED;
                return 0L;
            }
            if (!nexus.isLinkedAndFormed()) {
                routeStatus = RouteStatus.NEXUS_UNFORMED;
                return 0L;
            }
            accepted = nexus.receiveConstellationEnergy(constellationId, offered, simulate);
        } else if (outputType == EndpointType.RELAY && target instanceof TileEntityStarstreamRelay) {
            TileEntityStarstreamRelay relay = (TileEntityStarstreamRelay) target;
            if (!expectedId.equals(relay.relayId)) {
                routeStatus = RouteStatus.TARGET_REPLACED;
                return 0L;
            }
            accepted = relay.forwardEnergy(constellationId, offered, simulate, visited, hop + 1);
        } else {
            routeStatus = RouteStatus.TARGET_INVALID;
            return 0L;
        }

        if (!simulate && accepted > 0L) {
            transferredThisTick += accepted;
            lastInputTransferTick = world.getTotalWorldTime();
        }
        routeStatus = accepted > 0L ? RouteStatus.ACTIVE : RouteStatus.TARGET_FULL;
        return accepted;
    }

    public boolean onRelayRightClick(EntityPlayer player) {
        BlockPos target = getOutputPos();
        player.sendStatusMessage(new TextComponentTranslation(
                "pollution.starstream_relay.info",
                inboundEndpoints.size(),
                target == null ? "-" : target.getX() + ", " + target.getY() + ", " + target.getZ(),
                getTransferredThisTick(),
                getWirelessOutputThisTick(),
                new TextComponentTranslation(getRouteStatusTranslationKey())), false);
        return true;
    }

    @Override
    public void update() {
        if (world == null) return;
        if (world.isRemote) {
            float target = syncedWirelessActive ? 1.0F : 0.0F;
            clientWirelessActivity += (target - clientWirelessActivity)
                    * (target > clientWirelessActivity ? 0.22F : 0.10F);
            if (clientWirelessActivity < 0.002F) clientWirelessActivity = 0.0F;
            float inputTarget = syncedInputActive ? 1.0F : 0.0F;
            clientInputActivity += (inputTarget - clientInputActivity)
                    * (inputTarget > clientInputActivity ? 0.25F : 0.12F);
            if (clientInputActivity < 0.002F) clientInputActivity = 0.0F;
            return;
        }
        refreshTransferWindow();
        refreshWirelessOutputWindow();
        long tick = world.getTotalWorldTime();
        if (tick % StarstreamNetworkConstants.RELAY_HEARTBEAT_INTERVAL == 0L) {
            TileEntityStarstreamObeliskCore nexus = findNetworkCore();
            int depth = getNetworkDepth();
            if (nexus != null && depth > 0 && getOutputPos() != null) {
                nexus.heartbeatWirelessRelay(pos, relayId, getOutputPos(), depth);
            }
        }
        if (tick % 100L == 0L) pruneLoadedBrokenInputs();
        boolean active = lastWirelessTransferTick != Long.MIN_VALUE
                && tick - lastWirelessTransferTick <= 10L;
        boolean inputActive = lastInputTransferTick != Long.MIN_VALUE
                && tick - lastInputTransferTick <= 5L;
        if (active != lastSyncedWirelessActive
                || inputActive != lastSyncedInputActive
                || ((active || inputActive) && tick - lastWirelessSyncTick >= 5L)) {
            lastSyncedWirelessActive = active;
            lastSyncedInputActive = inputActive;
            syncedWirelessActive = active;
            syncedInputActive = inputActive;
            syncedWirelessOutput = Math.max(wirelessOutputThisTick,
                    lastCompletedWirelessOutput);
            syncedInputTransfer = Math.max(transferredThisTick,
                    lastCompletedInputTransfer);
            lastWirelessSyncTick = tick;
            syncToClient();
        }
    }

    private void refreshTransferWindow() {
        if (world == null) return;
        long tick = world.getTotalWorldTime();
        if (lastTransferTick != tick) {
            if (lastTransferTick != Long.MIN_VALUE) {
                lastCompletedInputTransfer = transferredThisTick;
            }
            lastTransferTick = tick;
            transferredThisTick = 0L;
        }
    }

    private void resetTransferWindow() {
        lastTransferTick = Long.MIN_VALUE;
        lastInputTransferTick = Long.MIN_VALUE;
        transferredThisTick = 0L;
        lastCompletedInputTransfer = 0L;
    }

    private void pruneLoadedBrokenInputs() {
        boolean changed = false;
        Iterator<Map.Entry<Long, EndpointType>> iterator = inboundEndpoints.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, EndpointType> entry = iterator.next();
            BlockPos sourcePos = BlockPos.fromLong(entry.getKey());
            if (!world.isBlockLoaded(sourcePos)) continue;
            TileEntity source = world.getTileEntity(sourcePos);
            boolean valid;
            if (entry.getValue() == EndpointType.TOWER) {
                valid = source instanceof TileEntityConstellationCrystal
                        && ((TileEntityConstellationCrystal) source)
                        .isLinkedTo(relayId, pos, EndpointType.RELAY);
            } else if (entry.getValue() == EndpointType.RELAY) {
                valid = source instanceof TileEntityStarstreamRelay
                        && ((TileEntityStarstreamRelay) source)
                        .isLinkedTo(relayId, pos, EndpointType.RELAY);
            } else {
                valid = false;
            }
            if (!valid) {
                iterator.remove();
                changed = true;
            }
        }
        if (changed) {
            markDirty();
            syncToClient();
        }
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
        }
    }

    private void syncToClient() {
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setLong(NBT_ID_MOST, relayId.getMostSignificantBits());
        compound.setLong(NBT_ID_LEAST, relayId.getLeastSignificantBits());
        compound.setBoolean(NBT_WIRELESS_ACTIVE, syncedWirelessActive);
        compound.setLong(NBT_WIRELESS_RATE, world != null && world.isRemote
                ? syncedWirelessOutput
                : Math.max(wirelessOutputThisTick, lastCompletedWirelessOutput));
        compound.setBoolean(NBT_INPUT_ACTIVE, syncedInputActive);
        compound.setLong(NBT_INPUT_RATE, world != null && world.isRemote
                ? syncedInputTransfer
                : Math.max(transferredThisTick, lastCompletedInputTransfer));
        if (hasOutput()) {
            compound.setInteger(NBT_OUTPUT_DIMENSION, outputDimension);
            compound.setLong(NBT_OUTPUT_POS, outputPos);
            compound.setLong(NBT_OUTPUT_ID_MOST, outputIdMost);
            compound.setLong(NBT_OUTPUT_ID_LEAST, outputIdLeast);
            compound.setByte(NBT_OUTPUT_TYPE, outputType.id);
        }
        NBTTagList inputs = new NBTTagList();
        for (Map.Entry<Long, EndpointType> entry : inboundEndpoints.entrySet()) {
            NBTTagCompound input = new NBTTagCompound();
            input.setLong(NBT_INPUT_POS, entry.getKey());
            input.setByte(NBT_INPUT_TYPE, entry.getValue().id);
            inputs.appendTag(input);
        }
        compound.setTag(NBT_INPUTS, inputs);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(NBT_ID_MOST) && compound.hasKey(NBT_ID_LEAST)) {
            relayId = new UUID(compound.getLong(NBT_ID_MOST), compound.getLong(NBT_ID_LEAST));
        } else {
            relayId = UUID.randomUUID();
        }
        if (compound.hasKey(NBT_OUTPUT_POS)) {
            outputDimension = compound.getInteger(NBT_OUTPUT_DIMENSION);
            outputPos = compound.getLong(NBT_OUTPUT_POS);
            outputIdMost = compound.getLong(NBT_OUTPUT_ID_MOST);
            outputIdLeast = compound.getLong(NBT_OUTPUT_ID_LEAST);
            outputType = EndpointType.fromId(compound.getByte(NBT_OUTPUT_TYPE));
            // Legacy wireless-output routes terminated at a constellation
            // tower. Towers are producers only, so discard that endpoint.
            if (outputType == EndpointType.TOWER) {
                outputDimension = Integer.MIN_VALUE;
                outputPos = NO_OUTPUT_POS;
                outputIdMost = 0L;
                outputIdLeast = 0L;
                outputType = EndpointType.NONE;
                routeStatus = RouteStatus.UNLINKED;
            } else {
                routeStatus = RouteStatus.IDLE;
            }
        } else {
            outputDimension = Integer.MIN_VALUE;
            outputPos = NO_OUTPUT_POS;
            outputIdMost = 0L;
            outputIdLeast = 0L;
            outputType = EndpointType.NONE;
            routeStatus = RouteStatus.UNLINKED;
        }
        syncedWirelessActive = compound.getBoolean(NBT_WIRELESS_ACTIVE);
        syncedWirelessOutput = Math.max(0L, compound.getLong(NBT_WIRELESS_RATE));
        syncedInputActive = compound.getBoolean(NBT_INPUT_ACTIVE);
        syncedInputTransfer = Math.max(0L, compound.getLong(NBT_INPUT_RATE));
        inboundEndpoints.clear();
        NBTTagList inputs = compound.getTagList(NBT_INPUTS, 10);
        for (int i = 0; i < inputs.tagCount()
                && inboundEndpoints.size() < StarstreamNetworkConstants.MAX_RELAY_INPUTS; i++) {
            NBTTagCompound input = inputs.getCompoundTagAt(i);
            EndpointType type = EndpointType.fromId(input.getByte(NBT_INPUT_TYPE));
            if (type != EndpointType.NONE) inboundEndpoints.put(input.getLong(NBT_INPUT_POS), type);
        }
        resetTransferWindow();
        lastWirelessOutputTick = Long.MIN_VALUE;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager network, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        // The active backbone beam can extend hundreds of blocks beyond the
        // relay model. A local box lets the frustum cull the TESR while the
        // beam itself is still visible, making it disappear as the view turns.
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    public enum EndpointType {
        NONE(0), TOWER(1), RELAY(2), NEXUS(3);

        private final byte id;

        EndpointType(int id) {
            this.id = (byte) id;
        }

        public byte getId() {
            return id;
        }

        public static EndpointType fromId(byte id) {
            for (EndpointType type : values()) if (type.id == id) return type;
            return NONE;
        }
    }

    private enum RouteStatus {
        UNLINKED("unlinked"),
        IDLE("idle"),
        ACTIVE("active"),
        TARGET_FULL("target_full"),
        THROUGHPUT_LIMIT("throughput_limit"),
        ENDPOINT_UNLOADED("endpoint_unloaded"),
        OUT_OF_RANGE("out_of_range"),
        WRONG_DIMENSION("wrong_dimension"),
        TARGET_INVALID("target_invalid"),
        TARGET_REPLACED("target_replaced"),
        NEXUS_UNFORMED("nexus_unformed"),
        LOOP_OR_TOO_DEEP("loop_or_too_deep");

        private final String key;

        RouteStatus(String key) {
            this.key = key;
        }
    }
}
