package meowmel.pollution.common.metatileentity.multiblock.generator;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.casing.ICasing;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.IStructureElement;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import meowmel.pollution.api.capability.IEssentiaHatch;
import meowmel.pollution.api.capability.ipml.EssentiaGeneratorLogic;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.api.pattern.POTieredCasingGroups;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POEssentiaCell;
import meowmel.pollution.common.data.EssentiaFuelData;
import meowmel.pollution.common.items.ItemEssentiaUpgrade;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Large Essentia Generator (大型源质发电机).
 *
 * <p>Ported from GTNH' {@code MTEEssentiaGenerator}, rebuilt on this pack's live
 * multiblock API. A 9x9x3 drum of Essentia Diffusion Cells around a central controller; essentia is
 * piped into an Essentia Input Hatch and burned for EU.</p>
 *
 * <p>Installed {@link ItemEssentiaUpgrade} modules decide which aspect categories may be burned. The
 * diffusion cell tier sets a global multiplier of 1x / 2x / 5x / 10x.</p>
 */
public class MetaTileEntityLargeEssentiaGenerator extends MultiblockWithDisplayBase {

    private static final String STRUCTURE_ID = "pollution:large_essentia_generator";

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            STRUCTURE_ID, () -> {
                IStructureElement hatches = Elements.abilities(
                        MultiblockAbility.OUTPUT_ENERGY,
                        MultiblockAbility.MAINTENANCE_HATCH,
                        MultiblockAbility.IMPORT_FLUIDS,
                        POMultiblockAbility.ESSENTIA_HATCH);
                return DeclarativePatternBuilder.start()
                        .aisle("T##TXT##T", "T###C###T", "A#######A")
                        .aisle("##TCCCT##", "###CEC###", "#########")
                        .aisle("#TCCCCCT#", "##CEEEC##", "#########")
                        .aisle("TCCCCCCCT", "#CEEEEEC#", "#########")
                        .aisle("XCCCCCCCX", "CEEEEEEEC", "####S####")
                        .aisle("TCCCCCCCT", "#CEEEEEC#", "#########")
                        .aisle("#TCCCCCT#", "##CEEEC##", "#########")
                        .aisle("##TCCCT##", "###CEC###", "#########")
                        .aisle("T##TXT##T", "T###C###T", "A#######A")
                        .self('S', MetaTileEntityLargeEssentiaGenerator.class)
                        .block('A', getAmberBrick())
                        .block('T', getArcaneStoneBrick())
                        .tieredCasing('C', POTieredCasingGroups.essentiaCells().group())
                        .withChannel(POTieredCasingGroups.essentiaCells().channel())
                        .where('E', Elements.choice(
                                Elements.block(getCellState(1)),
                                Elements.block(getCellState(2)),
                                Elements.block(getCellState(3)),
                                Elements.block(getCellState(4))))
                        .where('X', Elements.choice(
                                Elements.block(getCellState(1)),
                                hatches))
                        .any('#')
                        .globalAbilityLimit(MultiblockAbility.OUTPUT_ENERGY, 1, 1)
                        .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 0, 1)
                        .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 0, 1)
                        .globalAbilityLimit(POMultiblockAbility.ESSENTIA_HATCH, 1, 1)
                        .buildStructureDefinition();
            });

    private final EssentiaGeneratorLogic logic = new EssentiaGeneratorLogic(this);
    private final EssentiaUpgradeSlots upgradeSlots = new EssentiaUpgradeSlots(this);

    private IEnergyContainer energyContainer;
    private IMultipleTankHandler inputFluidInventory;
    private final List<IEssentiaHatch> essentiaHatches = new ArrayList<>();

    public MetaTileEntityLargeEssentiaGenerator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityLargeEssentiaGenerator(this.metaTileEntityId);
    }

    // ------------------------------------------------------------------
    // structure
    // ------------------------------------------------------------------

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    private static IBlockState getCellState(int tier) {
        return PollutionMetaBlocks.ESSENTIA_CELL.getState(
                POEssentiaCell.CellType.values()[Math.max(0, Math.min(3, tier - 1))]);
    }

    private static IBlockState getArcaneStoneBrick() {
        return thaumcraft.api.blocks.BlocksTC.stoneArcaneBrick.getDefaultState();
    }

    private static IBlockState getAmberBrick() {
        return thaumcraft.api.blocks.BlocksTC.amberBrick.getDefaultState();
    }

    @Override
    protected void formStructure(@Nonnull FormedStructureView formed) {
        super.formStructure(formed);

        this.energyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.OUTPUT_ENERGY));
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));

        this.essentiaHatches.clear();
        List<IEssentiaHatch> found = getAbilities(POMultiblockAbility.ESSENTIA_HATCH);
        if (found != null) {
            this.essentiaHatches.addAll(found);
        }

        ICasing cells = POTieredCasingGroups.essentiaCells().channel().getMatchedCasing(formed);
        int tier = cells == null ? 0 : cells.getTier();
        this.logic.setCellTier(tier);

        // The upgrade bay is the source of truth; push its mask into the logic and every hatch so
        // tubes only feed burnable essentia.
        syncUpgradeMask();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.energyContainer = null;
        this.inputFluidInventory = null;
        this.essentiaHatches.clear();
        this.logic.invalidate();
    }

    // ------------------------------------------------------------------
    // tick
    // ------------------------------------------------------------------

    @Override
    protected void updateFormedValid() {
        if (getWorld() == null || getWorld().isRemote) return;
        if (this.energyContainer == null) return;
        this.logic.updateLogic();
    }

    @Override
    public boolean isActive() {
        return super.isActive() && this.logic.isActive();
    }

    // ------------------------------------------------------------------
    // accessors used by the logic
    // ------------------------------------------------------------------

    @Nullable
    public IEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    public IMultipleTankHandler getInputFluidInventory() {
        return inputFluidInventory;
    }

    public List<IEssentiaHatch> getEssentiaHatches() {
        return essentiaHatches;
    }

    public EssentiaGeneratorLogic getLogic() {
        return logic;
    }

    /** Dynamo voltage tier, used by the ELECTRIC essentia branch. */
    public int getEnergyTier() {
        if (energyContainer == null) return 0;
        return gregtech.api.util.GTUtility.getFloorTierByVoltage(energyContainer.getOutputVoltage());
    }

    /** Installs an upgrade module; called by the GUI slot and by right-click for convenience. */
    public boolean installUpgrade(ItemStack stack) {
        if (!upgradeSlots.install(stack)) return false;
        syncUpgradeMask();
        return true;
    }

    /** Rebuilds the burnable-category mask from the upgrade bay and pushes it to every hatch. */
    public void syncUpgradeMask() {
        int mask = upgradeSlots.toMask();
        logic.setUpgradeMask(mask);
        for (IEssentiaHatch hatch : essentiaHatches) {
            if (hatch instanceof meowmel.pollution.common.metatileentity.multiblockpart.MetaTileEntityEssentiaHatch part) {
                part.setAllowedCategories(mask);
            }
        }
        markDirty();
    }

    public EssentiaUpgradeSlots getUpgradeSlots() {
        return upgradeSlots;
    }

    @Override
    public boolean onRightClick(net.minecraft.entity.player.EntityPlayer playerIn,
                                net.minecraft.util.EnumHand hand,
                                net.minecraft.util.EnumFacing facing,
                                codechicken.lib.raytracer.CuboidRayTraceResult hitResult) {
        if (!getWorld().isRemote) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemEssentiaUpgrade) {
                if (installUpgrade(held)) {
                    if (!playerIn.capabilities.isCreativeMode) held.shrink(1);
                    playerIn.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                            "pollution.machine.large_essentia_generator.upgrade.installed"), true);
                } else {
                    playerIn.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                            "pollution.machine.large_essentia_generator.upgrade.duplicate"), true);
                }
                return true;
            }
        }
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }

    // ------------------------------------------------------------------
    // display
    // ------------------------------------------------------------------

    @Override
    protected MultiblockUIFactory createUIFactory() {
        return super.createUIFactory().addScreenChildren((screen, syncManager) -> {
            final int columns = 5;
            final int cell = 18;
            final int slotCount = upgradeSlots.getSlots();
            syncManager.registerSlotGroup("essentia_upgrades", columns);
            for (int slot = 0; slot < slotCount; slot++) {
                final int slotIndex = slot;
                screen.child(new com.cleanroommc.modularui.widgets.slot.ItemSlot()
                        .slot(com.cleanroommc.modularui.value.sync.SyncHandlers
                                .itemSlot(upgradeSlots, slotIndex)
                                .slotGroup("essentia_upgrades")
                                .accessibility(true, false))
                        .pos(7 + (slotIndex % columns) * cell, 74 + (slotIndex / columns) * cell));
            }
        });
    }

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(true, logic.isActive())
                .addCustom((keyManager, syncer) -> {
                    syncer.syncBoolean(logic.isActive());
                    keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                            "pollution.machine.large_essentia_generator.cell_tier", syncer.syncInt(logic.getCellTier())));
                    keyManager.add(KeyUtil.lang(TextFormatting.GOLD,
                            "pollution.machine.large_essentia_generator.multiplier", syncer.syncInt(logic.getCellMultiplier())));
                    int stored = 0;
                    int capacity = 0;
                    for (IEssentiaHatch hatch : essentiaHatches) {
                        stored += hatch.getEssentiaAmount();
                        capacity += hatch.getEssentiaCapacity();
                    }
                    keyManager.add(KeyUtil.lang(TextFormatting.AQUA,
                            "pollution.machine.large_essentia_generator.essentia",
                            syncer.syncInt(stored), syncer.syncInt(capacity)));
                    keyManager.add(KeyUtil.lang(TextFormatting.GREEN,
                            "pollution.machine.large_essentia_generator.output",
                            syncer.syncLong(logic.getLastCycleEu())));
                    Aspect burned = logic.getLastBurnedAspect();
                    // Never call net.minecraft.client.resources.I18n here: this callback also runs
                    // on the server, whose jar has no client classes. Thaumcraft's own accessor is
                    // safe on both sides.
                    keyManager.add(KeyUtil.lang(TextFormatting.LIGHT_PURPLE,
                            "pollution.machine.large_essentia_generator.burning",
                            burned == null ? "-" : burned.getLocalizedDescription()));
                })
                .addWorkingStatusLine()
                .addProgressLine(logic.getProgress(), logic.getMaxProgress());
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        super.configureWarningText(builder);
        builder.addCustom((keyManager, syncer) -> {
            if (syncer.syncBoolean(essentiaHatches.isEmpty())) {
                keyManager.add(KeyUtil.lang(TextFormatting.RED,
                        "pollution.machine.large_essentia_generator.warning.no_hatch"));
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("家用源质回收站", tooltip);
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.large_essentia_generator.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.large_essentia_generator.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.large_essentia_generator.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.large_essentia_generator.tooltip.4"));
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), logic.isActive(),
                logic.isWorkingEnabled());
    }


    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart part) {
        return POTextures.ESSENTIA_CELL;
    }

    @Override
    public List<gregtech.api.pattern.MultiblockShapeInfo> getMatchingShapes() {
        // 9x9x3 with a large tiered channel; the JEI preview is expensive and not worth the stall.
        return Collections.emptyList();
    }

    // ------------------------------------------------------------------
    // persistence
    // ------------------------------------------------------------------

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("UpgradeBay", this.upgradeSlots.serializeNBT());
        return this.logic.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("UpgradeBay")) {
            this.upgradeSlots.deserializeNBT(data.getCompoundTag("UpgradeBay"));
        }
        this.logic.readFromNBT(data);
        // The bay is authoritative: never trust a stale mask from an older save.
        this.logic.setUpgradeMask(this.upgradeSlots.toMask());
    }

    /** Used by the tooltip to enumerate what each cell tier is worth. */
    public static int getMultiplierForTier(int tier) {
        return EssentiaGeneratorLogic.getCellMultiplierForTier(tier);
    }

    /** Exposed so JEI/guidebook code can list the fuel table size without touching the map. */
    public static int getFuelEntryCount() {
        return EssentiaFuelData.size();
    }
}
