package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.RichTextWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import meowmel.pollution.api.capability.IEssentiaHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.data.EssentiaFuelData;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.IEssentiaTransport;

import java.util.List;

/**
 * Essentia input hatch for the Large Essentia Generator.
 *
 * <p>Ported from GTNH' {@code EssentiaHatch}, but rebuilt as a proper GTCEu multiblock
 * part so it registers through {@link POMultiblockAbility#ESSENTIA_HATCH} instead of being a bare
 * {@code TileEntity} discovered by a bespoke predicate.</p>
 *
 * <p>The hatch only ever inputs: {@link #canOutputTo} is false and it advertises a high suction so
 * Thaumcraft tubes push essentia into it. Which aspect categories it is willing to accept is driven
 * by {@link #setAllowedCategories(int)}, which the controller refreshes from its installed
 * upgrades.</p>
 */
public class MetaTileEntityEssentiaHatch extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<IEssentiaHatch>, IEssentiaHatch,
        IEssentiaTransport, IAspectContainer {

    /** Base capacity; scales with the hatch tier. */
    public static final int BASE_CAPACITY = 1000;

    /** Advertised suction; matches the original machine so tubes actually feed it. */
    private static final int SUCTION = 256;

    private EssentiaHatchContainer container;

    /**
     * Bitmask of allowed aspect categories; 0 means "nothing accepted". Seeded to 1 so that the
     * NORMAL category works before the controller ever forms.
     */
    private int allowedCategories = 1;

    /** Optional single-aspect lock, set by right-clicking with a filled phial or jar. */
    @Nullable
    private Aspect lockedAspect;

    public MetaTileEntityEssentiaHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.container = new EssentiaHatchContainer(getCapacityForTier(tier));
    }

    public static int getCapacityForTier(int tier) {
        // LV..MAX; keep the growth gentle so a hatch never trivialises the burn rate.
        return BASE_CAPACITY * Math.max(1, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityEssentiaHatch(metaTileEntityId, getTier());
    }

    // ------------------------------------------------------------------
    // state
    // ------------------------------------------------------------------

    public void setAllowedCategories(int mask) {
        if (this.allowedCategories != mask) {
            this.allowedCategories = mask;
            markDirty();
        }
    }

    public int getAllowedCategories() {
        return allowedCategories;
    }

    @Nullable
    public Aspect getLockedAspect() {
        return lockedAspect;
    }

    private boolean acceptsCategory(Aspect aspect) {
        int index = EssentiaFuelData.getCategoryIndex(aspect);
        return index != -1 && (allowedCategories & (1 << index)) != 0;
    }

    private boolean accepts(Aspect aspect) {
        if (aspect == null) return false;
        if (lockedAspect != null && !lockedAspect.equals(aspect)) return false;
        return acceptsCategory(aspect);
    }

    // ------------------------------------------------------------------
    // update
    // ------------------------------------------------------------------

    @Override
    public void update() {
        super.update();
        if (getWorld().isRemote) return;
        if (container.isFull()) return;
        pullFromNeighbouringTubes();
    }

    /**
     * Mirrors {@code EssentiaHatch.fillfrompipe()}: pull one unit at a time from any adjacent
     * Thaumcraft tube that is willing to output to us.
     *
     * <p>Thaumcraft's contract is that the face passed to a neighbour names the side of <em>that</em>
     * neighbour through which the access happens, i.e. the opposite of the direction we reached it
     * by. {@code TileTube.equalizeWithNeighbours} does the same, and
     * {@code TileEntityMineralExtractor.pullAspect} in this repo follows it.</p>
     */
    private void pullFromNeighbouringTubes() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (container.isFull()) return;
            if (!(ThaumcraftApiHelper.getConnectableTile(getWorld(), getPos(), facing) instanceof IEssentiaTransport pipe)) {
                continue;
            }
            EnumFacing side = facing.getOpposite();
            if (!pipe.canOutputTo(side)) continue;
            if (pipe.getSuctionAmount(side) >= getSuctionAmount(facing)) continue;
            Aspect offered = pipe.getEssentiaType(side);
            if (!accepts(offered)) continue;
            int taken = pipe.takeEssentia(offered, 1, side);
            if (taken > 0) {
                container.add(offered, taken);
                markDirty();
            }
        }
    }

    // ------------------------------------------------------------------
    // IEssentiaHatch
    // ------------------------------------------------------------------

    @Override
    public int getEssentiaAmount() {
        return container.getAmount();
    }

    @Override
    public int getEssentiaCapacity() {
        return container.getCapacity();
    }

    @Override
    public int getEssentiaAmount(Aspect aspect) {
        return container.getAmount(aspect);
    }

    @Override
    public AspectList getEssentiaList() {
        return container.getAspects();
    }

    @Override
    public boolean isFull() {
        return container.isFull();
    }

    @Override
    public int drainEssentia(@Nullable Aspect aspect, int amount, boolean simulate) {
        int drained = container.drain(aspect, amount, simulate);
        if (drained > 0 && !simulate) markDirty();
        return drained;
    }

    // ------------------------------------------------------------------
    // IAspectContainer
    // ------------------------------------------------------------------

    @Override
    public AspectList getAspects() {
        return container.getAspects();
    }

    @Override
    public void setAspects(AspectList aspectList) {
        container.clear();
        if (aspectList != null) {
            for (Aspect aspect : aspectList.getAspects()) {
                container.add(aspect, aspectList.getAmount(aspect));
            }
        }
        markDirty();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return accepts(aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (!accepts(aspect)) return amount;
        int leftover = container.add(aspect, amount);
        if (leftover != amount) markDirty();
        return leftover;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        // Check before draining: drain removes min(stored, amount), so a failed take would still
        // destroy essentia.
        if (container.getAmount(aspect) < amount) return false;
        return container.drain(aspect, amount, false) >= amount;
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        if (aspectList == null) return false;
        for (Aspect aspect : aspectList.getAspects()) {
            if (container.getAmount(aspect) < aspectList.getAmount(aspect)) return false;
        }
        for (Aspect aspect : aspectList.getAspects()) {
            container.drain(aspect, aspectList.getAmount(aspect), false);
        }
        markDirty();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return container.getAmount(aspect) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        if (aspectList == null) return false;
        for (Aspect aspect : aspectList.getAspects()) {
            if (container.getAmount(aspect) <= 0) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return container.getAmount(aspect);
    }

    // ------------------------------------------------------------------
    // IEssentiaTransport -- input only
    // ------------------------------------------------------------------

    @Override
    public boolean isConnectable(EnumFacing facing) {
        return true;
    }

    @Override
    public boolean canInputFrom(EnumFacing facing) {
        return true;
    }

    @Override
    public boolean canOutputTo(EnumFacing facing) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        // suction is derived from the lock, not settable by tubes
    }

    @Override
    public Aspect getSuctionType(EnumFacing facing) {
        return lockedAspect;
    }

    @Override
    public int getSuctionAmount(EnumFacing facing) {
        return container.isFull() ? 0 : SUCTION;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing facing) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing facing) {
        return amount - addToContainer(aspect, amount);
    }

    @Override
    public Aspect getEssentiaType(EnumFacing facing) {
        AspectList list = container.getAspects();
        Aspect[] aspects = list.getAspects();
        return aspects.length == 0 ? null : aspects[0];
    }

    @Override
    public int getEssentiaAmount(EnumFacing facing) {
        return container.getAmount();
    }

    /** Deliberately huge: we always want tubes to prefer pushing into the generator. */
    @Override
    public int getMinimumSuction() {
        return Integer.MAX_VALUE;
    }

    // ------------------------------------------------------------------
    // interaction
    // ------------------------------------------------------------------

    /**
     * Screwdriver toggles the single-aspect lock. Right-clicking with a filled phial or jar locks
     * onto that aspect; with an empty hand it clears the lock, matching the original hatch.
     */
    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn,
                                      EnumHand hand,
                                      EnumFacing wrenchSide,
                                      CuboidRayTraceResult hitResult) {
        if (getWorld().isRemote) return true;
        ItemStack held = playerIn.getHeldItem(hand);
        Aspect target = null;
        if (!held.isEmpty() && held.getItem() instanceof IEssentiaContainerItem containerItem) {
            AspectList heldAspects = containerItem.getAspects(held);
            if (heldAspects != null && heldAspects.size() > 0) {
                target = heldAspects.getAspects()[0];
            }
        }
        this.lockedAspect = target == null || target.equals(lockedAspect) ? null : target;
        markDirty();
        playerIn.sendStatusMessage(new TextComponentTranslation(
                lockedAspect == null ? "pollution.machine.essentia_hatch.lock.cleared"
                        : "pollution.machine.essentia_hatch.lock.set",
                lockedAspect == null ? "" : lockedAspect.getLocalizedDescription()), true);
        return true;
    }

    // ------------------------------------------------------------------
    // part plumbing
    // ------------------------------------------------------------------

    @Override
    public MultiblockAbility<IEssentiaHatch> getAbility() {
        return POMultiblockAbility.ESSENTIA_HATCH;
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            POTextures.ESSENTIA_INPUT_HATCH.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    // ------------------------------------------------------------------
    // persistence
    // ------------------------------------------------------------------

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("EssentiaContainer", container.serializeNBT());
        data.setInteger("AllowedCategories", allowedCategories);
        data.setString("LockedAspect", lockedAspect == null ? "" : lockedAspect.getTag());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.container = new EssentiaHatchContainer(getCapacityForTier(getTier()));
        if (data.hasKey("EssentiaContainer")) {
            container.deserializeNBT(data.getCompoundTag("EssentiaContainer"));
        }
        this.allowedCategories = data.hasKey("AllowedCategories") ? data.getInteger("AllowedCategories") : 1;
        String locked = data.getString("LockedAspect");
        this.lockedAspect = locked.isEmpty() ? null : Aspect.getAspect(locked);
    }

    // ------------------------------------------------------------------
    // UI / tooltips
    // ------------------------------------------------------------------

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager syncManager, UISettings settings) {
        // GTCEu only pushes MTE state through writeCustomData/initial sync, so reading the live
        // container from the client would show a frozen chunk-load snapshot. Publish it as a synced
        // value instead; the getter runs on the server and the text refreshes while the GUI is open.
        com.cleanroommc.modularui.value.sync.GenericSyncValue<String> contents =
                syncManager.getOrCreateSyncHandler("essentia_hatch_contents", 0,
                        com.cleanroommc.modularui.value.sync.GenericSyncValue.class,
                        () -> com.cleanroommc.modularui.value.sync.GenericSyncValue.builder(String.class)
                                .getter(this::describeContents)
                                .serializer((buffer, value) -> buffer.writeString(value))
                                .deserializer(buffer -> buffer.readString(512))
                                .copyImmutable()
                                .build());

        return GTGuis.createPanel(this, 176, 166)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5))
                .child(GTGuiTextures.DISPLAY.asWidget().left(7).top(16).size(162, 55))
                .child(new RichTextWidget()
                        .size(155, 50)
                        .pos(10, 20)
                        .textColor(Color.WHITE.main)
                        .alignment(Alignment.TopLeft)
                        .autoUpdate(true)
                        .textBuilder(richText -> {
                            for (String line : contents.getValue().split("\n")) {
                                if (!line.isEmpty()) richText.addLine(IKey.str(line));
                            }
                        }))
                .child(com.cleanroommc.modularui.widgets.SlotGroupWidget.playerInventory(false).left(7).bottom(7));
    }

    /**
     * Server-side snapshot of the hatch contents, one aspect per line.
     *
     * <p>Deliberately free of {@code net.minecraft.client.resources.I18n}: this runs on the server,
     * whose jar has no client classes. Aspect names come from Thaumcraft's own
     * {@code getLocalizedDescription()}, which uses the common translation helper.</p>
     */
    private String describeContents() {
        StringBuilder builder = new StringBuilder();
        builder.append(getEssentiaAmount()).append(" / ").append(getEssentiaCapacity());
        AspectList list = container.getAspects();
        Aspect[] aspects = list.getAspects();
        if (aspects.length == 0) {
            builder.append('\n').append("--");
            return builder.toString();
        }
        int shown = 0;
        for (Aspect aspect : aspects) {
            if (aspect == null) continue;
            builder.append('\n').append(aspect.getLocalizedDescription())
                    .append(" x").append(list.getAmount(aspect));
            if (++shown >= 4) break;
        }
        return builder.toString();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.essentia_hatch.tooltip.capacity", getCapacityForTier(getTier())));
        tooltip.add(I18n.format("pollution.machine.essentia_hatch.tooltip.input"));
    }
}
