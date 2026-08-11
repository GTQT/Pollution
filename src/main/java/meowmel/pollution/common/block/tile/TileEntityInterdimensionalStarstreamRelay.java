package meowmel.pollution.common.block.tile;

import meowmel.pollution.api.capability.StarstreamNetworkConstants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.Map;
import java.util.UUID;

/** Wireless output gateway that reaches one loaded nexus across dimensions. */
public class TileEntityInterdimensionalStarstreamRelay extends TileEntityStarstreamRelay {

    private static final String NBT_GATEWAY_BOUND = "GatewayBound";
    private static final String NBT_GATEWAY_DIMENSION = "GatewayDimension";
    private static final String NBT_GATEWAY_POS = "GatewayPos";
    private static final String NBT_GATEWAY_NETWORK_MOST = "GatewayNetworkMost";
    private static final String NBT_GATEWAY_NETWORK_LEAST = "GatewayNetworkLeast";

    private boolean gatewayBound;
    private int nexusDimension = Integer.MIN_VALUE;
    private long nexusPos = Long.MIN_VALUE;
    private UUID gatewayNetworkId;
    private long gatewayOutputTick = Long.MIN_VALUE;
    private long gatewayOutputThisTick;

    public void bindGateway(int dimension, BlockPos corePos, UUID networkId) {
        gatewayBound = corePos != null && networkId != null;
        nexusDimension = gatewayBound ? dimension : Integer.MIN_VALUE;
        nexusPos = gatewayBound ? corePos.toLong() : Long.MIN_VALUE;
        gatewayNetworkId = gatewayBound ? networkId : null;
        markDirty();
        syncGateway();
    }

    public void clearGateway() {
        bindGateway(Integer.MIN_VALUE, null, null);
    }

    public boolean isGatewayBound() {
        return gatewayBound;
    }

    public int getNexusDimension() {
        return nexusDimension;
    }

    @Override
    public TileEntityStarstreamObeliskCore findNetworkCore() {
        if (!gatewayBound || gatewayNetworkId == null) return null;
        World targetWorld = DimensionManager.getWorld(nexusDimension);
        if (targetWorld == null) return null;
        BlockPos targetPos = BlockPos.fromLong(nexusPos);
        if (!targetWorld.isBlockLoaded(targetPos)) return null;
        if (!(targetWorld.getTileEntity(targetPos) instanceof TileEntityStarstreamObeliskCore)) return null;
        TileEntityStarstreamObeliskCore core =
                (TileEntityStarstreamObeliskCore) targetWorld.getTileEntity(targetPos);
        return gatewayNetworkId.equals(core.getNetworkId()) && core.isLinkedAndFormed()
                ? core : null;
    }

    @Override
    public int getNetworkDepth() {
        return findNetworkCore() == null ? -1 : 1;
    }

    @Override
    public int getWirelessRange() {
        return StarstreamNetworkConstants.INTERDIMENSIONAL_RELAY_RANGE;
    }

    @Override
    public long requestWirelessEnergy(BlockPos consumerPos, UUID networkId, UUID consumerId,
                                      String constellationId, long amount, boolean simulate) {
        if (world == null || consumerPos == null || pos.distanceSq(consumerPos)
                > StarstreamNetworkConstants.INTERDIMENSIONAL_RELAY_RANGE_SQUARED) return 0L;
        TileEntityStarstreamObeliskCore core = findNetworkCore();
        if (core == null || networkId == null || !networkId.equals(core.getNetworkId())) return 0L;
        refreshGatewayWindow();
        long remaining = Math.max(0L,
                StarstreamNetworkConstants.INTERDIMENSIONAL_RELAY_OUTPUT_PER_TICK
                        - gatewayOutputThisTick);
        long offered = Math.min(amount, remaining);
        long extracted = core.provideWirelessEnergy(networkId, consumerId,
                world.provider.getDimension(), consumerPos,
                constellationId, offered, simulate);
        if (!simulate && extracted > 0L) {
            gatewayOutputThisTick += extracted;
            recordWirelessOutput(extracted);
        }
        return extracted;
    }

    @Override
    public boolean consumeWirelessEnergy(BlockPos consumerPos, UUID networkId, UUID consumerId,
                                         Map<String, Long> requirements, boolean simulate) {
        if (world == null || consumerPos == null || pos.distanceSq(consumerPos)
                > StarstreamNetworkConstants.INTERDIMENSIONAL_RELAY_RANGE_SQUARED) return false;
        TileEntityStarstreamObeliskCore core = findNetworkCore();
        if (core == null || networkId == null || !networkId.equals(core.getNetworkId())) return false;
        long total = total(requirements);
        refreshGatewayWindow();
        if (total <= 0L || total > StarstreamNetworkConstants.INTERDIMENSIONAL_RELAY_OUTPUT_PER_TICK
                - gatewayOutputThisTick) return false;
        boolean consumed = core.provideWirelessEnergyBatch(networkId, consumerId,
                world.provider.getDimension(), consumerPos, requirements, simulate);
        if (consumed && !simulate) {
            gatewayOutputThisTick += total;
            recordWirelessOutput(total);
        }
        return consumed;
    }

    @Override
    public void update() {
        super.update();
        if (world == null || world.isRemote || !gatewayBound) return;
        long tick = world.getTotalWorldTime();
        if (tick % StarstreamNetworkConstants.RELAY_HEARTBEAT_INTERVAL != 0L) return;
        TileEntityStarstreamObeliskCore core = findNetworkCore();
        if (core != null) {
            core.heartbeatWirelessRelay(world.provider.getDimension(), pos, getRelayId(),
                    nexusDimension, BlockPos.fromLong(nexusPos), 1);
        }
    }

    @Override
    public boolean onRelayRightClick(EntityPlayer player) {
        player.sendStatusMessage(new TextComponentTranslation(
                gatewayBound
                        ? "pollution.starstream_interdimensional_relay.info.bound"
                        : "pollution.starstream_interdimensional_relay.info.unbound",
                nexusDimension,
                gatewayBound ? BlockPos.fromLong(nexusPos).toString() : "-",
                getWirelessOutputThisTick()), false);
        return true;
    }

    private void refreshGatewayWindow() {
        if (world == null) return;
        long tick = world.getTotalWorldTime();
        if (gatewayOutputTick != tick) {
            gatewayOutputTick = tick;
            gatewayOutputThisTick = 0L;
        }
    }

    private static long total(Map<String, Long> requirements) {
        if (requirements == null || requirements.isEmpty()) return -1L;
        long total = 0L;
        for (Long amount : requirements.values()) {
            if (amount == null || amount < 0L || Long.MAX_VALUE - total < amount) return -1L;
            total += amount;
        }
        return total;
    }

    private void syncGateway() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean(NBT_GATEWAY_BOUND, gatewayBound);
        if (gatewayBound && gatewayNetworkId != null) {
            compound.setInteger(NBT_GATEWAY_DIMENSION, nexusDimension);
            compound.setLong(NBT_GATEWAY_POS, nexusPos);
            compound.setLong(NBT_GATEWAY_NETWORK_MOST, gatewayNetworkId.getMostSignificantBits());
            compound.setLong(NBT_GATEWAY_NETWORK_LEAST, gatewayNetworkId.getLeastSignificantBits());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        gatewayBound = compound.getBoolean(NBT_GATEWAY_BOUND);
        nexusDimension = compound.getInteger(NBT_GATEWAY_DIMENSION);
        nexusPos = compound.hasKey(NBT_GATEWAY_POS)
                ? compound.getLong(NBT_GATEWAY_POS) : Long.MIN_VALUE;
        gatewayNetworkId = gatewayBound
                ? new UUID(compound.getLong(NBT_GATEWAY_NETWORK_MOST),
                compound.getLong(NBT_GATEWAY_NETWORK_LEAST)) : null;
    }
}
