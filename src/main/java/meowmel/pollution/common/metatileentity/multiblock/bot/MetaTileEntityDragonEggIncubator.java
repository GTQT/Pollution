package meowmel.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import meowmel.pollution.api.capability.IStarstreamOperationCore;
import meowmel.pollution.common.block.metablocks.POStarstreamObelisk;
import meowmel.pollution.common.block.metablocks.PODragonCasing;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import java.util.List;
import java.util.Locale;

/** An open eight-petal constellation-powered flower which awakens a placed dragon egg in its central nest. */
public class MetaTileEntityDragonEggIncubator extends MetaTileEntityBaseWithControl {
    private final DragonEggAwakening awakening = new DragonEggAwakening();
    private static final int SYNC_INCUBATION_RATE = 26001;
    private int visualRate;
    private DragonEggAwakening.Status status = DragonEggAwakening.Status.WAITING;

    public MetaTileEntityDragonEggIncubator(ResourceLocation id) {
        super(id);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return StructureDefinition.getOrBuild("pollution:dragon_egg_incubator", () -> {
            DeclarativePatternBuilder builder = DeclarativePatternBuilder.start();
            for (String[] aisle : DragonIncubatorShape.createAisles()) builder.aisle(aisle);
            return builder.self('S', MetaTileEntityDragonEggIncubator.class)
                    .blocks('C', PollutionMetaBlocks.DRAGON_CASING.getState(PODragonCasing.Type.INCUBATOR_CASING),
                            PollutionMetaBlocks.STARSTREAM_OBELISK.getState(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_CASING))
                    .block('W', BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR))
                    .block('L', PollutionMetaBlocks.STARSTREAM_OBELISK.getState(POStarstreamObelisk.ObeliskBlockType.CONSTELLATION_ANCHOR))
                    .blocks('P', PollutionMetaBlocks.DRAGON_CASING.getState(PODragonCasing.Type.INCUBATOR_RUNED_CASING),
                            PollutionMetaBlocks.STARSTREAM_OBELISK.getState(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_RUNED_CASING))
                    .block('K', PollutionMetaBlocks.STARSTREAM_OBELISK.getState(POStarstreamObelisk.ObeliskBlockType.CONSTELLATION_ANCHOR))
                    .block('H', PollutionMetaBlocks.STARSTREAM_OPERATION_CORE.getDefaultState())
                    // Empty and occupied nests both form; preview/build must not consume a dragon egg.
                    .blockPredicate('E', state -> state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.DRAGON_EGG,
                            () -> new BlockInfo[] {BlockInfo.EMPTY})
                    .air('#').any(' ')
                    .buildStructureDefinition();
        });
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity holder) {
        return new MetaTileEntityDragonEggIncubator(metaTileEntityId);
    }

    public BlockPos getReceiverPos() {
        return getPos().offset(getFrontFacing().getOpposite(), 8);
    }

    private IStarstreamOperationCore getOperationCore() {
        if (getWorld() == null || !getWorld().isBlockLoaded(getReceiverPos())) return null;
        TileEntity tile = getWorld().getTileEntity(getReceiverPos());
        return tile instanceof IStarstreamOperationCore ? (IStarstreamOperationCore) tile : null;
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        awakening.reset();
        status = DragonEggAwakening.Status.WAITING;
        setActive(false);
        setVisualRate(0);
        markDirty();
    }

    public BlockPos getNestPos() {
        return getPos().add(RelativeDirection.setActualRelativeOffset(
                DragonIncubatorShape.EGG_X - DragonIncubatorShape.CONTROLLER_X,
                DragonIncubatorShape.EGG_Y - DragonIncubatorShape.CONTROLLER_Y,
                DragonIncubatorShape.EGG_Z - DragonIncubatorShape.CONTROLLER_Z,
                getFrontFacingForStructure(), getUpwardsFacing(), isFlipped(), getStructureDirForPreview()));
    }

    @Override
    protected void updateFormedValid() {
        if (getWorld().isRemote) return;
        int previousProgress = awakening.getProgress();
        // Preserve the existing compatibility rule: Dragon Mounts owns its own egg conversion.
        if (Loader.isModLoaded("dragonmounts")) {
            status = DragonEggAwakening.Status.DISABLED;
        } else {
            status = awakening.tick(getWorld(), getNestPos(), getPos(), getReceiverPos(), getOperationCore(), isWorkingEnabled());
        }
        int rate = status == DragonEggAwakening.Status.DISABLED ? 0 : awakening.getLastAdvance();
        setActive(rate > 0);
        setVisualRate(rate);
        if (previousProgress != awakening.getProgress() || rate > 0) markDirty();
    }

    private void setVisualRate(int rate) {
        if (visualRate == rate) return;
        visualRate = rate;
        if (getWorld() != null && !getWorld().isRemote) writeCustomData(SYNC_INCUBATION_RATE, buf -> buf.writeByte(rate));
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeByte(visualRate);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        visualRate = buf.readUnsignedByte();
    }

    @Override
    public void receiveCustomData(int id, PacketBuffer buf) {
        super.receiveCustomData(id, buf);
        if (id == SYNC_INCUBATION_RATE) visualRate = buf.readUnsignedByte();
    }

    @Override
    public void update() {
        super.update();
        if (getWorld().isRemote && isActive() && getOffsetTimer() % 4 == 0) {
            BlockPos nest = getNestPos();
            if (getWorld().getClosestPlayer(nest.getX(), nest.getY(), nest.getZ(), 48.0D, false) == null) return;
            double phase = getOffsetTimer() * (visualRate > 1 ? 0.12D : 0.035D);
            for (int i = 0; i < 3; i++) {
                double angle = phase + i * Math.PI * 2.0D / 3.0D;
                double x = Math.cos(angle), z = Math.sin(angle);
                getWorld().spawnParticle(EnumParticleTypes.END_ROD,
                        nest.getX() + 0.5D + x * 1.3D, nest.getY() + 0.4D,
                        nest.getZ() + 0.5D + z * 1.3D, -x * 0.01D, 0.015D, -z * 0.01D);
                getWorld().spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
                        nest.getX() + 0.5D, nest.getY() + 0.5D, nest.getZ() + 0.5D, x * 3.0D, 1.8D, z * 3.0D);
                if (visualRate > 1) {
                    getWorld().spawnParticle(EnumParticleTypes.END_ROD,
                            nest.getX() + 0.5D + x * 0.8D, nest.getY() + 0.8D + i * 0.35D,
                            nest.getZ() + 0.5D + z * 0.8D, -z * 0.025D, 0.02D, x * 0.025D);
                }
            }
        }
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.structureFormed(isStructureFormed())
                .setWorkingStatus(isWorkingEnabled(), isActive())
                .addWorkingStatusLine()
                .addCustom((keys, sync) -> {
                    String state = sync.syncString(status.name().toLowerCase(Locale.ROOT));
                    keys.add(KeyUtil.lang(TextFormatting.LIGHT_PURPLE, "pollution.machine.dragon_egg_incubator.status." + state));
                    keys.add(KeyUtil.lang(TextFormatting.GRAY, "pollution.machine.dragon_egg_incubator.progress",
                            sync.syncInt(awakening.getDisplayProgress()), sync.syncInt(awakening.getDuration())));
                    keys.add(KeyUtil.lang(TextFormatting.AQUA, "pollution.machine.dragon_egg_incubator.energy",
                            sync.syncInt(awakening.getAevitasPerTick()), sync.syncInt(awakening.getHorologiumPerTick()),
                            sync.syncInt(visualRate)));
                    IStarstreamOperationCore core = getOperationCore();
                    String network = core == null ? "pollution.starstream_terminal.status.unbound"
                            : core.getStarstreamWirelessBinding().getStatusTranslationKey();
                    keys.add(KeyUtil.lang(TextFormatting.GRAY, sync.syncString(network)));
                    keys.add(KeyUtil.lang(TextFormatting.GRAY, "pollution.machine.dragon_egg_incubator.nest"));
                });
    }

    @Override
    public int getProgress() {
        return awakening.getDisplayProgress();
    }

    @Override
    public int getMaxProgress() {
        return awakening.getDuration();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("DragonAwakening", awakening.writeToNBT());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        awakening.readFromNBT(data.getCompoundTag("DragonAwakening"));
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public boolean allowsFlip() {
        return false;
    }

    @Override
    public boolean isValidFrontFacing(EnumFacing facing) {
        return facing.getAxis().isHorizontal();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart part) {
        return POTextures.DRAGON_INCUBATOR_CASING;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        for (int i = 1; i <= 5; i++) tooltip.add(I18n.format("pollution.machine.dragon_egg_incubator.tooltip." + i));
    }
}
