package meowmel.pollution.common.metatileentity.multiblock.bloodMagic;

import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.TemplateBarBuilder;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.GTUtility;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.api.capability.IStarstreamWirelessTerminal;
import meowmel.pollution.api.capability.StarstreamWirelessBinding;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.blocks.PollutionBlocksInit;
import meowmel.pollution.common.block.metablocks.POBloodExtractorCasing;
import meowmel.pollution.common.block.tile.TileEntityBloodyStarlightContainer;
import meowmel.pollution.common.entity.dragon.DragonContainmentData;
import meowmel.pollution.common.entity.dragon.DragonGrowthAura;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.function.UnaryOperator;

public class MetaTileEntityBloodyBloodExtractor extends MultiblockWithDisplayBase
        implements IControllable, ProgressBarMultiblock, IFastRenderMetaTileEntity, IStarstreamWirelessTerminal,
        TileEntityBloodyStarlightContainer.ExtractionOwner {
    private static final int SYNC_VISUAL = 26110;
    private final DragonBloodExtractionLogic extraction = new DragonBloodExtractionLogic();
    private final StarstreamWirelessBinding starstream = new StarstreamWirelessBinding();
    private IEnergyContainer energy = new EnergyContainerList(Collections.emptyList());
    private IFluidHandler output = new FluidTankList(false);
    private final TileEntityBloodyStarlightContainer[] claimed = new TileEntityBloodyStarlightContainer[10];
    private final String[] slotStates = new String[10];
    private boolean enabled = true, active;
    private long highestVoltage;
    private int count, estimatedYield, mask;
    private int auraMultiplier = 1;
    private int visualProgress, visualBuffered, visualMask;

    public MetaTileEntityBloodyBloodExtractor(ResourceLocation id) {
        super(id);
        Arrays.fill(slotStates, "absent");
    }

    public static IBlockState casing() {
        return PollutionMetaBlocks.BLOOD_EXTRACTOR_CASING.getState(POBloodExtractorCasing.Type.SEALED_CASING);
    }
    public static IBlockState glass() {
        return PollutionMetaBlocks.BLOOD_EXTRACTOR_CASING.getState(POBloodExtractorCasing.Type.CYLINDER_GLASS);
    }

    @Override protected StructureDefinition<?> createStructureDefinition() {
        return StructureDefinition.getOrBuild("pollution:bloody_blood_extractor", () -> {
            DeclarativePatternBuilder builder = DeclarativePatternBuilder.start();
            for (String[] aisle : BloodyBloodExtractorShape.createAisles()) builder.aisle(aisle);
            return builder.self('S', MetaTileEntityBloodyBloodExtractor.class)
                    .air('#').block('G', glass())
                    .block('N', MetaBlocks.FRAMES.get(Materials.StainlessSteel).getDefaultState())
                    .block('P', MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN))
                    .block('H', casing())
                    .blockPredicate('D', state -> state.getBlock() == Blocks.AIR
                                    || state.getBlock() == PollutionBlocksInit.BLOODY_STARLIGHT_CONTAINER,
                            () -> new BlockInfo[]{BlockInfo.EMPTY})
                    .casing('X', casing())
                    .hatch(MultiblockAbility.MAINTENANCE_HATCH, 1, 1, () -> MetaTileEntities.MAINTENANCE_HATCH)
                    .hatch(MultiblockAbility.INPUT_ENERGY, 1, 2, () -> MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.UHV])
                    .fluidOutput(1, 2)
                    .buildStructureDefinition();
        });
    }

    @Override public MetaTileEntity createMetaTileEntity(IGregTechTileEntity holder) {
        return new MetaTileEntityBloodyBloodExtractor(metaTileEntityId);
    }

    public BlockPos shapePosition(int x, int y, int z) {
        return getPos().add(RelativeDirection.setActualRelativeOffset(x, y - 3, z + 2,
                getFrontFacingForStructure(), getUpwardsFacing(), isFlipped(), getStructureDirForPreview()));
    }

    @Override protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        List<IEnergyContainer> inputs = getAbilities(MultiblockAbility.INPUT_ENERGY);
        energy = new EnergyContainerList(inputs);
        highestVoltage = 0;
        for (IEnergyContainer input : inputs) highestVoltage = Math.max(highestVoltage, input.getInputVoltage());
        output = new FluidTankList(false, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    private List<DragonBloodExtractionLogic.Source> sources() {
        List<DragonBloodExtractionLogic.Source> result = new ArrayList<>();
        Set<UUID> identities = new HashSet<>();
        count = estimatedYield = mask = 0;
        for (int i = 0; i < 10; i++) {
            BlockPos local = BloodyBloodExtractorShape.slot(i);
            BlockPos pos = shapePosition(local.getX(), local.getY(), local.getZ());
            TileEntity tile = getWorld().isBlockLoaded(pos) ? getWorld().getTileEntity(pos) : null;
            TileEntityBloodyStarlightContainer container = tile instanceof TileEntityBloodyStarlightContainer
                    ? (TileEntityBloodyStarlightContainer) tile : null;
            if (claimed[i] != container) {
                if (claimed[i] != null) claimed[i].releaseClaim(this);
                claimed[i] = container;
            }
            slotStates[i] = container == null ? "absent" : container.isOccupied() ? "invalid" : "empty";
            if (container == null) continue;
            DragonContainmentData.ExtractionSnapshot snapshot = container.getExtractionSnapshot();
            if (snapshot == null) continue;
            if (!container.tryClaim(this)) { slotStates[i] = "conflict"; continue; }
            if (!identities.add(snapshot.dragonId) || existsInWorld(snapshot.dragonId)) {
                slotStates[i] = "duplicate";
                continue;
            }
            int yield = DragonBloodExtractionLogic.yieldFor(snapshot.stage);
            result.add(new DragonBloodExtractionLogic.Source(i, snapshot.dragonId, snapshot.captureId,
                    container.getContentId(), snapshot.stage, yield));
            slotStates[i] = snapshot.stage.name().toLowerCase(Locale.ROOT);
            count++;
            estimatedYield += yield;
            mask |= 1 << i;
        }
        return result;
    }

    private boolean existsInWorld(UUID id) {
        for (WorldServer world : DimensionManager.getWorlds()) {
            Entity existing = world.getEntityFromUuid(id);
            if (existing != null && !existing.isDead) return true;
        }
        return false;
    }

    @Override public boolean ownsContainer(BlockPos pos) {
        if (!isValid() || !isStructureFormed() || getWorld() == null
                || !getWorld().isBlockLoaded(getPos()) || GTUtility.getMetaTileEntity(getWorld(), getPos()) != this) return false;
        for (TileEntityBloodyStarlightContainer tile : claimed) {
            if (tile != null && !tile.isInvalid() && tile.getPos().equals(pos)
                    && getWorld().isBlockLoaded(pos) && getWorld().getTileEntity(pos) == tile) return true;
        }
        return false;
    }

    private void releaseClaims() {
        for (TileEntityBloodyStarlightContainer tile : claimed) if (tile != null) tile.releaseClaim(this);
        Arrays.fill(claimed, null);
    }

    @Override protected void updateFormedValid() {
        if (getWorld().isRemote) return;
        int beforeProgress = extraction.getProgress(), beforeBuffer = extraction.getBuffered();
        long beforePower = extraction.getBatchPower();
        DragonBloodExtractionLogic.Status previous = extraction.getStatus();
        extraction.export(output, PollutionMaterials.DragonBlood::getFluid);
        auraMultiplier = DragonGrowthAura.multiplier(getWorld(), getPos());
        DragonBloodExtractionLogic.Status status = extraction.tick(sources(), energy, highestVoltage,
                enabled, !hasMaintenanceProblems(), auraMultiplier, DragonBloodExtractionLogic.configuredAevitasMultiplier(),
                cost -> starstream.consumeEnergy(getWorld(), getPos(), Collections.singletonMap("aevitas", cost), false));
        active = status == DragonBloodExtractionLogic.Status.RUNNING;
        if (active && extraction.getProgress() == 0) {
            BlockPos hub = shapePosition(0, 2, 0);
            getWorld().playSound(null, hub, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.3F, 0.65F);
        }
        extraction.export(output, PollutionMaterials.DragonBlood::getFluid);
        if (beforeProgress != extraction.getProgress() || beforeBuffer != extraction.getBuffered()
                || beforePower != extraction.getBatchPower()) markDirty();
        if (previous != status || active && extraction.getProgress() == 0 || getOffsetTimer() % 5 == 0) syncVisual();
    }

    @Override public void invalidateStructure() {
        releaseClaims();
        extraction.cancelBatch();
        active = false;
        count = estimatedYield = mask = 0;
        Arrays.fill(slotStates, "absent");
        energy = new EnergyContainerList(Collections.emptyList());
        output = new FluidTankList(false);
        highestVoltage = 0;
        auraMultiplier = 1;
        super.invalidateStructure();
        markDirty();
        if (getWorld() != null && !getWorld().isRemote) syncVisual();
    }

    @Override public void onUnload() { releaseClaims(); super.onUnload(); }
    @Override public void onRemoval() { releaseClaims(); super.onRemoval(); }
    @Override public boolean isActive() { return isStructureFormed() && active; }
    @Override public boolean isWorkingEnabled() { return enabled; }
    @Override public void setWorkingEnabled(boolean value) {
        enabled = value;
        if (!value) active = false;
        markDirty();
        if (getWorld() != null && !getWorld().isRemote) syncVisual();
    }
    @Override public boolean shouldShowVoidingModeButton() { return false; }
    @Override public StarstreamWirelessBinding getStarstreamWirelessBinding() { return starstream; }
    @Override public void onStarstreamNetworkChanged(UUID networkId) { markDirty(); }
    @Override public boolean allowsExtendedFacing() { return false; }
    @Override public boolean allowsFlip() { return false; }
    @Override public boolean isValidFrontFacing(EnumFacing facing) { return facing.getAxis().isHorizontal(); }

    @Override protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.structureFormed(isStructureFormed()).setWorkingStatus(enabled, isActive()).addWorkingStatusLine()
                .addCustom((keys, sync) -> {
                    keys.add(KeyUtil.lang(TextFormatting.RED, "pollution.extractor.status."
                            + sync.syncString(extraction.getStatus().name().toLowerCase(Locale.ROOT))));
                    keys.add(KeyUtil.lang(TextFormatting.WHITE, "pollution.extractor.summary",
                            sync.syncInt(count), sync.syncInt(estimatedYield)));
                    keys.add(KeyUtil.lang(TextFormatting.GOLD, "pollution.extractor.energy",
                            sync.syncLong(extraction.getBatchPower() > 0 ? extraction.getBatchPower()
                                    : DragonBloodExtractionLogic.powerFor(count))));
                    keys.add(KeyUtil.lang(TextFormatting.GRAY, "pollution.extractor.progress",
                            sync.syncInt(extraction.getProgress())));
                    keys.add(KeyUtil.lang(TextFormatting.LIGHT_PURPLE, "pollution.extractor.acceleration",
                            sync.syncInt(auraMultiplier), sync.syncInt(extraction.getLastAdvance()),
                            sync.syncLong(extraction.getLastAevitas())));
                    keys.add(KeyUtil.lang(TextFormatting.GRAY, "pollution.extractor.network",
                            KeyUtil.lang(sync.syncString(starstream.getStatusTranslationKey()))));
                    keys.add(KeyUtil.lang(TextFormatting.RED, "pollution.extractor.buffer",
                            sync.syncInt(extraction.getBuffered())));
                    for (int row = 0; row < 5; row++) {
                        keys.add(KeyUtil.lang(TextFormatting.GRAY, "pollution.extractor.slots", row + 1,
                                KeyUtil.lang("pollution.extractor.slot." + sync.syncString(slotStates[row])),
                                row + 6, KeyUtil.lang("pollution.extractor.slot." + sync.syncString(slotStates[row + 5]))));
                    }
                });
    }

    @Override public int getProgressBarCount() { return 2; }
    @Override public void registerBars(List<UnaryOperator<TemplateBarBuilder>> bars, PanelSyncManager sync) {
        IntSyncValue progress = new IntSyncValue(extraction::getProgress);
        IntSyncValue fluid = new IntSyncValue(extraction::getBuffered);
        sync.syncValue("blood_progress", progress);
        sync.syncValue("blood_buffer", fluid);
        bars.add(bar -> bar.progress(() -> progress.getIntValue() / 100.0).texture(GTGuiTextures.PROGRESS_BAR_EXTRACT));
        bars.add(bar -> bar.progress(() -> fluid.getIntValue() / 16000.0).texture(GTGuiTextures.PROGRESS_BAR_ARROW));
    }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("BloodExtraction", extraction.write());
        tag.setBoolean("ExtractionEnabled", enabled);
        tag.setTag("ExtractionStarstream", starstream.serializeNBT());
        return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        extraction.read(tag.getCompoundTag("BloodExtraction"));
        enabled = !tag.hasKey("ExtractionEnabled") || tag.getBoolean("ExtractionEnabled");
        starstream.deserializeNBT(tag.getCompoundTag("ExtractionStarstream"));
    }
    @Override public void writeItemStackData(NBTTagCompound tag) {
        super.writeItemStackData(tag);
        tag.setTag("BloodExtraction", extraction.writeItem());
        tag.setTag("ExtractionStarstream", starstream.serializeNBT());
    }
    @Override public void initFromItemStackData(NBTTagCompound tag) {
        super.initFromItemStackData(tag);
        extraction.read(tag.getCompoundTag("BloodExtraction"));
        starstream.deserializeNBT(tag.getCompoundTag("ExtractionStarstream"));
    }

    private void visualData(PacketBuffer buf) {
        buf.writeBoolean(active);
        buf.writeBoolean(enabled);
        buf.writeByte(extraction.getProgress());
        buf.writeInt(extraction.getBuffered());
        buf.writeShort(mask);
    }
    private void readVisual(PacketBuffer buf) {
        active = buf.readBoolean();
        enabled = buf.readBoolean();
        visualProgress = buf.readUnsignedByte();
        visualBuffered = buf.readInt();
        visualMask = buf.readUnsignedShort();
    }
    private void syncVisual() { writeCustomData(SYNC_VISUAL, this::visualData); }
    @Override public void writeInitialSyncData(PacketBuffer buf) { super.writeInitialSyncData(buf); visualData(buf); }
    @Override public void receiveInitialSyncData(PacketBuffer buf) { super.receiveInitialSyncData(buf); readVisual(buf); }
    @Override public void receiveCustomData(int id, PacketBuffer buf) {
        if (id == SYNC_VISUAL) readVisual(buf); else super.receiveCustomData(id, buf);
    }
    @Override public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE)
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        return super.getCapability(capability, side);
    }

    @Override public AxisAlignedBB getRenderBoundingBox() { return new AxisAlignedBB(getPos()).grow(13); }
    @Override public boolean shouldRenderInPass(int pass) { return pass == 1; }
    @SideOnly(Side.CLIENT)
    @Override public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (isStructureFormed()) meowmel.pollution.client.tesr.BloodyBloodExtractorVisual.render(this,
                x, y, z, visualProgress, visualBuffered, visualMask, isActive(), partialTicks);
    }
    @SideOnly(Side.CLIENT)
    @Override public ICubeRenderer getBaseTexture(IMultiblockPart part) { return POTextures.BLOOD_EXTRACTOR_CASING; }
    @SideOnly(Side.CLIENT)
    @Override protected OrientedOverlayRenderer getFrontOverlay() { return Textures.EXTRACTOR_OVERLAY; }
    @Override public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        for (int i = 1; i <= 6; i++) tooltip.add(I18n.format("pollution.extractor.tooltip." + i));
        int stored = stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag("BloodExtraction").getInteger("Buffered") : 0;
        if (stored > 0) tooltip.add(I18n.format("pollution.extractor.buffer", stored));
    }
}
