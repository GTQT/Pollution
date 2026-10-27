package meowmel.pollution.common.block.tile;

import meowmel.pollution.common.entity.dragon.DragonContainmentData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import java.lang.ref.WeakReference;
import java.util.UUID;

/** No ITickable: stored life is suspended. Client packets contain only display metadata. */
public class TileEntityBloodyStarlightContainer extends TileEntity {
    private NBTTagCompound payload = new NBTTagCompound();
    private UUID contentId = UUID.randomUUID();
    private DragonContainmentData.ExtractionSnapshot extractionSnapshot;
    private boolean extractionCached;
    private boolean persistContentId;
    private boolean baseHidden;
    private WeakReference<ExtractionOwner> extractionOwner = new WeakReference<>(null);

    public interface ExtractionOwner {
        boolean ownsContainer(BlockPos pos);
    }

    public UUID getContentId() { return contentId; }

    public DragonContainmentData.ExtractionSnapshot getExtractionSnapshot() {
        // Older containers have no persisted identity. Save it before a paid batch can reference it.
        if (persistContentId && world != null && !world.isRemote) {
            persistContentId = false;
            markDirty();
        }
        if (!extractionCached) {
            extractionSnapshot = DragonContainmentData.extractionSnapshot(payload);
            extractionCached = true;
        }
        return extractionSnapshot;
    }

    public boolean tryClaim(ExtractionOwner claimant) {
        ExtractionOwner current = extractionOwner.get();
        if (current != null && current != claimant && current.ownsContainer(pos)) return false;
        extractionOwner = new WeakReference<>(claimant);
        return true;
    }

    public void releaseClaim(ExtractionOwner claimant) {
        if (extractionOwner.get() == claimant) extractionOwner.clear();
    }

    public boolean isOccupied() { return !payload.isEmpty(); }

    public NBTTagCompound getPayload() { return payload.copy(); }

    public boolean isBaseHidden() { return baseHidden; }

    public void setBaseHidden(boolean hidden) {
        if (baseHidden == hidden) return;
        baseHidden = hidden;
        // Changing the pedestal must not invalidate a paid extraction batch or its content identity.
        markDirtyAndSync();
    }

    public void applyItemData(ItemStack stack) {
        baseHidden = DragonContainmentData.isBaseHidden(stack);
        setPayload(DragonContainmentData.get(stack));
    }

    public void writeItemData(ItemStack stack) {
        DragonContainmentData.set(stack, getPayload());
        DragonContainmentData.setBaseHidden(stack, baseHidden);
    }

    public void setPayload(NBTTagCompound value) {
        payload = value.copy();
        contentId = UUID.randomUUID();
        persistContentId = false;
        extractionCached = false;
        extractionSnapshot = null;
        markDirtyAndSync();
    }

    private void markDirtyAndSync() {
        markDirty();
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            world.checkLight(pos);
        }
    }

    public NBTTagCompound takePayload() {
        NBTTagCompound result = getPayload();
        setPayload(new NBTTagCompound());
        return result;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(DragonContainmentData.HIDE_BASE, baseHidden);
        tag.setUniqueId("ExtractionContentId", contentId);
        if (isOccupied()) tag.setTag(DragonContainmentData.KEY, payload.copy());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        payload = tag.getCompoundTag(DragonContainmentData.KEY).copy();
        baseHidden = tag.getBoolean(DragonContainmentData.HIDE_BASE);
        persistContentId = !tag.hasUniqueId("ExtractionContentId");
        contentId = persistContentId ? UUID.randomUUID() : tag.getUniqueId("ExtractionContentId");
        extractionCached = false;
        extractionSnapshot = null;
        extractionOwner.clear();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.writeToNBT(new NBTTagCompound());
        tag.setBoolean(DragonContainmentData.HIDE_BASE, baseHidden);
        tag.setTag(DragonContainmentData.KEY, DragonContainmentData.displayOnly(payload));
        return tag;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        if (world != null) world.checkLight(pos);
    }

    @Override
    public double getMaxRenderDistanceSquared() { return 4096.0D; }

    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    @Override
    public boolean shouldRenderInPass(int pass) { return pass == 1; }
}
