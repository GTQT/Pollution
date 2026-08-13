package meowmel.pollution.common.block.tile;

import meowmel.pollution.api.capability.IStarstreamOperationCore;
import meowmel.pollution.api.capability.StarstreamWirelessBinding;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/** Persistent wireless receiver placed inside endgame machines and rituals. */
public class TileEntityStarstreamOperationCore extends TileEntity
        implements IStarstreamOperationCore {

    private static final String NBT_BINDING = "StarstreamWireless";
    private final StarstreamWirelessBinding binding = new StarstreamWirelessBinding();

    @Override
    public StarstreamWirelessBinding getStarstreamWirelessBinding() {
        return binding;
    }

    @Override
    public void onStarstreamNetworkChanged(@Nullable UUID networkId) {
        markDirtyAndSync();
    }

    @Override
    public long requestConstellationEnergy(World requestWorld, BlockPos consumerPos,
                                           String constellationId, long amount,
                                           boolean simulate) {
        return binding.requestEnergy(requestWorld, consumerPos,
                constellationId, amount, simulate);
    }

    public long requestConstellationEnergy(String constellationId, long amount,
                                           boolean simulate) {
        return requestConstellationEnergy(world, pos, constellationId, amount, simulate);
    }

    @Override
    public boolean consumeConstellationEnergy(World requestWorld, BlockPos consumerPos,
                                              Map<String, Long> requirements,
                                              boolean simulate) {
        return binding.consumeEnergy(requestWorld, consumerPos, requirements, simulate);
    }

    public boolean consumeConstellationEnergy(Map<String, Long> requirements,
                                              boolean simulate) {
        return consumeConstellationEnergy(world, pos, requirements, simulate);
    }

    public boolean onCoreRightClick(EntityPlayer player) {
        BlockPos provider = binding.getProviderPos();
        if (binding.isBound()) {
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.starstream_operation_core.info.bound",
                    new TextComponentTranslation(binding.getStatusTranslationKey()),
                    binding.getTransferredThisTick(world),
                    provider == null ? "-" : provider.getX() + ", "
                            + provider.getY() + ", " + provider.getZ()), false);
        } else {
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.starstream_operation_core.info.unbound"), false);
        }
        return true;
    }

    public boolean isNetworkBound() {
        return binding.isBound();
    }

    private void markDirtyAndSync() {
        markDirty();
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag(NBT_BINDING, binding.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        binding.deserializeNBT(compound.getCompoundTag(NBT_BINDING));
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
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos).grow(2.5D);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}
