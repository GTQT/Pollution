package meowmel.pollution.common.block.tile;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import meowmel.pollution.api.capability.IConstellationEnergyCore;
import meowmel.pollution.api.capability.StarstreamNetworkConstants;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POConstellationCrystal;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityConstellationTower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.UUID;

/**
 * Animated ritual crystal and persistent, directly accessible tower core.
 * Ordinary ritual crystals keep no gameplay state; tower-core state lives here.
 */
public class TileEntityConstellationCrystal extends TileEntity implements IConstellationEnergyCore {

    public static final long BASE_ENERGY_CAPACITY = 4_194_304L;
    public static final long AMPLIFIED_ENERGY_CAPACITY = 16_777_216L;
    private static final long NO_CONTROLLER_POS = Long.MIN_VALUE;
    private static final String NBT_ENERGY = "ConstellationEnergy";
    private static final String NBT_CONSTELLATION = "ConstellationId";
    private static final String NBT_CONTROLLER_POS = "ControllerPos";
    private static final String NBT_NEXUS_DIMENSION = "NexusDimension";
    private static final String NBT_NEXUS_POS = "NexusPos";
    private static final String NBT_NEXUS_ID_MOST = "NexusIdMost";
    private static final String NBT_NEXUS_ID_LEAST = "NexusIdLeast";
    private static final String NBT_ENDPOINT_TYPE = "StarstreamEndpointType";

    private long constellationEnergy;
    private String constellationId = "";
    private long controllerPos = NO_CONTROLLER_POS;
    private long lastExtractionTick = Long.MIN_VALUE;
    private long extractedThisTick;
    private int nexusDimension = Integer.MIN_VALUE;
    private long nexusPos = NO_CONTROLLER_POS;
    private long nexusIdMost;
    private long nexusIdLeast;
    private TileEntityStarstreamRelay.EndpointType linkedEndpointType =
            TileEntityStarstreamRelay.EndpointType.NEXUS;
    private long lastNetworkTick = Long.MIN_VALUE;
    private long networkTransferThisTick;
    private NetworkStatus networkStatus = NetworkStatus.UNLINKED;

    @Override
    public String getConstellationId() {
        return constellationId.isEmpty() ? null : constellationId;
    }

    @Override
    public long getConstellationEnergyStored() {
        return constellationEnergy;
    }

    @Override
    public long getConstellationEnergyCapacity() {
        MetaTileEntityConstellationTower tower = findTower();
        return tower == null || !tower.isStructureFormed()
                ? BASE_ENERGY_CAPACITY : tower.getCoreCapacityLimit();
    }

    /** Called by the formed tower before generation or legacy-energy migration. */
    public void bindTower(String newConstellationId, BlockPos newControllerPos) {
        if (!isTowerCore() || newConstellationId == null || newConstellationId.isEmpty()) return;
        boolean identityChanged = !constellationId.isEmpty() && !constellationId.equals(newConstellationId);
        boolean bindingChanged = identityChanged
                || !constellationId.equals(newConstellationId)
                || controllerPos != newControllerPos.toLong();
        if (!bindingChanged) return;

        // A core cannot convert stored energy merely by being installed in a
        // differently attuned tower.
        if (identityChanged) constellationEnergy = 0L;
        constellationId = newConstellationId;
        controllerPos = newControllerPos.toLong();
        resetExtractionWindow();
        markDirty();
    }

    public long receiveConstellationEnergy(long amount) {
        if (amount <= 0L || !isTowerCore() || constellationId.isEmpty()) return 0L;
        long remainingCapacity = Math.max(0L, getConstellationEnergyCapacity() - constellationEnergy);
        long accepted = Math.min(amount, remainingCapacity);
        if (accepted > 0L) {
            constellationEnergy += accepted;
            markDirty();
        }
        return accepted;
    }

    public boolean isOperationalTowerCore() {
        MetaTileEntityConstellationTower tower = findTower();
        return isTowerCore() && tower != null && tower.isStructureFormed()
                && tower.isTowerCoreAt(pos)
                && tower.getDefinition().getId().equals(getConstellationId());
    }

    public boolean hasNexusLink() {
        return nexusDimension != Integer.MIN_VALUE && nexusPos != NO_CONTROLLER_POS;
    }

    public void bindNexus(int dimension, BlockPos targetPos, UUID networkId) {
        bindEndpoint(dimension, targetPos, networkId, TileEntityStarstreamRelay.EndpointType.NEXUS);
    }

    public void bindRelay(int dimension, BlockPos targetPos, UUID relayId) {
        bindEndpoint(dimension, targetPos, relayId, TileEntityStarstreamRelay.EndpointType.RELAY);
    }

    private void bindEndpoint(int dimension, BlockPos targetPos, UUID endpointId,
                              TileEntityStarstreamRelay.EndpointType endpointType) {
        if (hasNexusLink() && isLinkedTo(endpointId, targetPos, endpointType)) return;
        unregisterFromOldEndpoint();
        nexusDimension = dimension;
        nexusPos = targetPos.toLong();
        nexusIdMost = endpointId.getMostSignificantBits();
        nexusIdLeast = endpointId.getLeastSignificantBits();
        linkedEndpointType = endpointType;
        networkStatus = NetworkStatus.NO_ENERGY;
        resetNetworkWindow();
        markDirty();
        syncToClient();
    }

    public void clearNexusLink() {
        unregisterFromOldEndpoint();
        nexusDimension = Integer.MIN_VALUE;
        nexusPos = NO_CONTROLLER_POS;
        nexusIdMost = 0L;
        nexusIdLeast = 0L;
        linkedEndpointType = TileEntityStarstreamRelay.EndpointType.NEXUS;
        networkStatus = NetworkStatus.UNLINKED;
        resetNetworkWindow();
        markDirty();
        syncToClient();
    }

    private void unregisterFromOldEndpoint() {
        if (world == null || !hasNexusLink()
                || world.provider.getDimension() != nexusDimension) return;
        BlockPos oldPos = BlockPos.fromLong(nexusPos);
        if (!world.isBlockLoaded(oldPos)) return;
        TileEntity oldTarget = world.getTileEntity(oldPos);
        if (linkedEndpointType == TileEntityStarstreamRelay.EndpointType.NEXUS
                && oldTarget instanceof TileEntityStarstreamObeliskCore) {
            ((TileEntityStarstreamObeliskCore) oldTarget).unregisterInboundTower(pos);
        } else if (linkedEndpointType == TileEntityStarstreamRelay.EndpointType.RELAY
                && oldTarget instanceof TileEntityStarstreamRelay) {
            ((TileEntityStarstreamRelay) oldTarget).unregisterInbound(pos);
        }
    }

    public boolean isLinkedTo(UUID networkId, BlockPos targetPos) {
        return isLinkedTo(networkId, targetPos, TileEntityStarstreamRelay.EndpointType.NEXUS);
    }

    public boolean isLinkedTo(UUID endpointId, BlockPos targetPos,
                              TileEntityStarstreamRelay.EndpointType endpointType) {
        return hasNexusLink() && linkedEndpointType == endpointType && nexusPos == targetPos.toLong()
                && nexusIdMost == endpointId.getMostSignificantBits()
                && nexusIdLeast == endpointId.getLeastSignificantBits();
    }

    public TileEntityStarstreamRelay.EndpointType getLinkedEndpointType() {
        return linkedEndpointType;
    }

    public BlockPos getLinkedNexusPos() {
        return hasNexusLink() ? BlockPos.fromLong(nexusPos) : null;
    }

    public int getLinkedNexusDimension() {
        return nexusDimension;
    }

    public long getNetworkTransferThisTick() {
        refreshNetworkWindow();
        return networkTransferThisTick;
    }

    public String getNetworkStatusTranslationKey() {
        return "pollution.starstream_network.status." + networkStatus.key;
    }

    public long transferToLinkedNexus() {
        refreshNetworkWindow();
        if (!hasNexusLink()) {
            networkStatus = NetworkStatus.UNLINKED;
            return 0L;
        }
        TileEntity target = findLinkedEndpoint(true);
        if (target == null) return 0L;
        if (!isOperationalTowerCore()) {
            networkStatus = NetworkStatus.SOURCE_UNFORMED;
            return 0L;
        }
        String id = getConstellationId();
        long available = extractConstellationEnergy(Long.MAX_VALUE, true);
        if (available <= 0L) {
            networkStatus = NetworkStatus.NO_ENERGY;
            return 0L;
        }
        long accepted = receiveAtEndpoint(target, id, available, true);
        if (accepted <= 0L) {
            networkStatus = NetworkStatus.CHANNEL_FULL;
            return 0L;
        }

        long extracted = extractConstellationEnergy(accepted, false);
        long deposited = receiveAtEndpoint(target, id, extracted, false);
        if (deposited < extracted) receiveConstellationEnergy(extracted - deposited);
        networkTransferThisTick += deposited;
        networkStatus = deposited > 0L ? NetworkStatus.ACTIVE : NetworkStatus.CHANNEL_FULL;
        return deposited;
    }

    private long receiveAtEndpoint(TileEntity target, String id, long amount, boolean simulate) {
        if (target instanceof TileEntityStarstreamObeliskCore) {
            return ((TileEntityStarstreamObeliskCore) target)
                    .receiveConstellationEnergy(id, amount, simulate);
        }
        if (target instanceof TileEntityStarstreamRelay) {
            return ((TileEntityStarstreamRelay) target).forwardEnergy(id, amount, simulate);
        }
        return 0L;
    }

    private TileEntity findLinkedEndpoint(boolean requireFormed) {
        if (world == null || !hasNexusLink()) return null;
        if (world.provider.getDimension() != nexusDimension) {
            networkStatus = NetworkStatus.WRONG_DIMENSION;
            return null;
        }
        BlockPos targetPos = BlockPos.fromLong(nexusPos);
        if (pos.distanceSq(targetPos) > StarstreamNetworkConstants.DIRECT_LINK_RANGE_SQUARED) {
            networkStatus = NetworkStatus.OUT_OF_RANGE;
            return null;
        }
        if (!world.isBlockLoaded(targetPos)) {
            networkStatus = NetworkStatus.ENDPOINT_UNLOADED;
            return null;
        }
        UUID expected = new UUID(nexusIdMost, nexusIdLeast);
        TileEntity target = world.getTileEntity(targetPos);
        if (linkedEndpointType == TileEntityStarstreamRelay.EndpointType.NEXUS
                && target instanceof TileEntityStarstreamObeliskCore) {
            TileEntityStarstreamObeliskCore nexus = (TileEntityStarstreamObeliskCore) target;
            if (!expected.equals(nexus.getNetworkId())) {
                networkStatus = NetworkStatus.TARGET_REPLACED;
                return null;
            }
            if (requireFormed && !nexus.isLinkedAndFormed()) {
                networkStatus = NetworkStatus.TARGET_UNFORMED;
                return null;
            }
            return nexus;
        }
        if (linkedEndpointType == TileEntityStarstreamRelay.EndpointType.RELAY
                && target instanceof TileEntityStarstreamRelay) {
            TileEntityStarstreamRelay relay = (TileEntityStarstreamRelay) target;
            if (!expected.equals(relay.getRelayId())) {
                networkStatus = NetworkStatus.TARGET_REPLACED;
                return null;
            }
            return relay;
        }
        networkStatus = NetworkStatus.TARGET_INVALID;
        return null;
    }

    private void refreshNetworkWindow() {
        if (world == null) return;
        long tick = world.getTotalWorldTime();
        if (lastNetworkTick != tick) {
            lastNetworkTick = tick;
            networkTransferThisTick = 0L;
        }
    }

    private void resetNetworkWindow() {
        lastNetworkTick = Long.MIN_VALUE;
        networkTransferThisTick = 0L;
    }

    private void syncToClient() {
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public long getMaxExtractPerTick() {
        refreshExtractionWindow();
        MetaTileEntityConstellationTower tower = findTower();
        return tower == null || !tower.isStructureFormed() ? 0L : tower.getCoreTransferLimit();
    }

    @Override
    public long getExtractedThisTick() {
        refreshExtractionWindow();
        return extractedThisTick;
    }

    @Override
    public long extractConstellationEnergy(long amount, boolean simulate) {
        if (amount <= 0L || !isTowerCore() || constellationId.isEmpty()) return 0L;
        refreshExtractionWindow();
        long remainingBudget = Math.max(0L, getMaxExtractPerTick() - extractedThisTick);
        long extracted = Math.min(amount, Math.min(constellationEnergy, remainingBudget));
        if (!simulate && extracted > 0L) {
            constellationEnergy -= extracted;
            extractedThisTick += extracted;
            markDirty();
        }
        return extracted;
    }

    private void refreshExtractionWindow() {
        if (world == null) return;
        long tick = world.getTotalWorldTime();
        if (lastExtractionTick != tick) {
            lastExtractionTick = tick;
            extractedThisTick = 0L;
        }
    }

    private void resetExtractionWindow() {
        lastExtractionTick = Long.MIN_VALUE;
        extractedThisTick = 0L;
    }

    public boolean onCoreRightClick(EntityPlayer player, EnumHand hand) {
        MetaTileEntityConstellationTower tower = findTower();
        if (tower == null) {
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.machine.constellation_tower.core.unlinked"), true);
            return true;
        }
        return tower.onCoreRightClick(player, hand);
    }

    public MetaTileEntityConstellationTower findTower() {
        if (world == null || !isTowerCore()) return null;

        if (controllerPos != NO_CONTROLLER_POS) {
            MetaTileEntityConstellationTower boundTower = findTowerAt(BlockPos.fromLong(controllerPos));
            if (isMatchingTower(boundTower)) return boundTower;
        }

        BlockPos base = pos.down(12);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            MetaTileEntityConstellationTower tower = findTowerAt(base.offset(facing, 4));
            if (isMatchingTower(tower)) {
                if (!world.isRemote) bindTower(tower.getDefinition().getId(), tower.getPos());
                return tower;
            }
        }
        return null;
    }

    private MetaTileEntityConstellationTower findTowerAt(BlockPos candidatePos) {
        TileEntity tile = world.getTileEntity(candidatePos);
        if (!(tile instanceof IGregTechTileEntity)) return null;
        MetaTileEntity metaTileEntity = ((IGregTechTileEntity) tile).getMetaTileEntity();
        return metaTileEntity instanceof MetaTileEntityConstellationTower
                ? (MetaTileEntityConstellationTower) metaTileEntity : null;
    }

    private boolean isMatchingTower(MetaTileEntityConstellationTower tower) {
        return tower != null
                && tower.isTowerCoreAt(pos)
                && (constellationId.isEmpty() || constellationId.equals(tower.getDefinition().getId()));
    }

    private boolean isTowerCore() {
        if (world == null) return false;
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                && PollutionMetaBlocks.CONSTELLATION_CRYSTAL.getState(state)
                == POConstellationCrystal.CrystalType.TOWER_CORE;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setLong(NBT_ENERGY, constellationEnergy);
        if (!constellationId.isEmpty()) compound.setString(NBT_CONSTELLATION, constellationId);
        if (controllerPos != NO_CONTROLLER_POS) compound.setLong(NBT_CONTROLLER_POS, controllerPos);
        if (hasNexusLink()) {
            compound.setInteger(NBT_NEXUS_DIMENSION, nexusDimension);
            compound.setLong(NBT_NEXUS_POS, nexusPos);
            compound.setLong(NBT_NEXUS_ID_MOST, nexusIdMost);
            compound.setLong(NBT_NEXUS_ID_LEAST, nexusIdLeast);
            compound.setByte(NBT_ENDPOINT_TYPE, linkedEndpointType.getId());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        constellationEnergy = Math.max(0L,
                Math.min(AMPLIFIED_ENERGY_CAPACITY, compound.getLong(NBT_ENERGY)));
        constellationId = compound.getString(NBT_CONSTELLATION);
        controllerPos = compound.hasKey(NBT_CONTROLLER_POS)
                ? compound.getLong(NBT_CONTROLLER_POS) : NO_CONTROLLER_POS;
        if (compound.hasKey(NBT_NEXUS_POS) && compound.hasKey(NBT_NEXUS_ID_MOST)
                && compound.hasKey(NBT_NEXUS_ID_LEAST)) {
            nexusDimension = compound.getInteger(NBT_NEXUS_DIMENSION);
            nexusPos = compound.getLong(NBT_NEXUS_POS);
            nexusIdMost = compound.getLong(NBT_NEXUS_ID_MOST);
            nexusIdLeast = compound.getLong(NBT_NEXUS_ID_LEAST);
            linkedEndpointType = compound.hasKey(NBT_ENDPOINT_TYPE)
                    ? TileEntityStarstreamRelay.EndpointType.fromId(compound.getByte(NBT_ENDPOINT_TYPE))
                    : TileEntityStarstreamRelay.EndpointType.NEXUS;
            if (linkedEndpointType != TileEntityStarstreamRelay.EndpointType.RELAY
                    && linkedEndpointType != TileEntityStarstreamRelay.EndpointType.NEXUS) {
                linkedEndpointType = TileEntityStarstreamRelay.EndpointType.NEXUS;
            }
            networkStatus = NetworkStatus.ENDPOINT_UNLOADED;
        } else {
            nexusDimension = Integer.MIN_VALUE;
            nexusPos = NO_CONTROLLER_POS;
            nexusIdMost = 0L;
            nexusIdLeast = 0L;
            linkedEndpointType = TileEntityStarstreamRelay.EndpointType.NEXUS;
            networkStatus = NetworkStatus.UNLINKED;
        }
        resetExtractionWindow();
        resetNetworkWindow();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                    && PollutionMetaBlocks.CONSTELLATION_CRYSTAL.getState(state)
                    == POConstellationCrystal.CrystalType.TOWER_CORE) {
                AxisAlignedBB bounds = new AxisAlignedBB(pos).grow(4.0D, 1.5D, 4.0D)
                        .expand(0.0D, -7.0D, 0.0D);
                BlockPos target = getLinkedNexusPos();
                if (target == null) return bounds;

                double dx = target.getX() - pos.getX();
                double dz = target.getZ() - pos.getZ();
                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
                double targetCenterY = target.getY()
                        + (linkedEndpointType == TileEntityStarstreamRelay.EndpointType.RELAY
                        ? 1.45D : 8.65D);
                double arcHeight = Math.min(14.0D, 2.5D + horizontalDistance * 0.08D);
                double apexY = (pos.getY() + 0.5D + targetCenterY) * 0.5D + arcHeight;
                double minX = Math.min(pos.getX() + 0.5D, target.getX() + 0.5D) - 1.0D;
                double maxX = Math.max(pos.getX() + 0.5D, target.getX() + 0.5D) + 1.0D;
                double minY = Math.min(pos.getY() - 7.0D, target.getY()) - 1.0D;
                double maxY = Math.max(Math.max(pos.getY() + 2.0D, targetCenterY), apexY) + 1.0D;
                double minZ = Math.min(pos.getZ() + 0.5D, target.getZ() + 0.5D) - 1.0D;
                double maxZ = Math.max(pos.getZ() + 0.5D, target.getZ() + 0.5D) + 1.0D;
                return bounds.union(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
            }
        }
        return new AxisAlignedBB(pos).grow(0.35D);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 262144.0D;
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

    private enum NetworkStatus {
        UNLINKED("unlinked"),
        ACTIVE("active"),
        NO_ENERGY("no_energy"),
        CHANNEL_FULL("channel_full"),
        SOURCE_UNFORMED("source_unformed"),
        TARGET_UNFORMED("target_unformed"),
        ENDPOINT_UNLOADED("endpoint_unloaded"),
        OUT_OF_RANGE("out_of_range"),
        WRONG_DIMENSION("wrong_dimension"),
        TARGET_INVALID("target_invalid"),
        TARGET_REPLACED("target_replaced");

        private final String key;

        NetworkStatus(String key) {
            this.key = key;
        }
    }
}
