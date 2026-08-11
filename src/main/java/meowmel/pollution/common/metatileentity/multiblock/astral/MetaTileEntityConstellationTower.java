package meowmel.pollution.common.metatileentity.multiblock.astral;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.mui.factory.MetaTileEntityGuiFactory;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import meowmel.pollution.api.astral.AstralNbtHelper;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POConstellationCrystal;
import meowmel.pollution.common.block.tile.TileEntityConstellationCrystal;
import meowmel.pollution.common.block.tile.TileEntityStarstreamRelay;
import codechicken.lib.raytracer.CuboidRayTraceResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** A CHC-inspired open constellation tower with a permanently fixed identity. */
public class MetaTileEntityConstellationTower extends MetaTileEntityBaseWithControl {

    public static final long NORMAL_ENERGY_CAPACITY = TileEntityConstellationCrystal.BASE_ENERGY_CAPACITY;
    public static final long AMPLIFIED_ENERGY_CAPACITY = TileEntityConstellationCrystal.AMPLIFIED_ENERGY_CAPACITY;
    private static final long BASE_ENERGY_PER_TICK = 64L;
    private static final long NORMAL_CORE_TRANSFER_PER_TICK = 512L;
    private static final long AMPLIFIED_CORE_TRANSFER_PER_TICK = 2_048L;
    private static final int SAMPLE_INTERVAL = 20;
    private static final String NBT_ENERGY = "ConstellationEnergy";
    private static final String NBT_DIPPER_RITUAL_ACTIVE = "DipperResonanceRitualActive";
    private static final String NBT_LEGACY_TURBO_CHARGED = "TurboCharged";

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:constellation_tower", () -> DeclarativePatternBuilder.start()
                    // User-authored 8x13x8 tower. The horizontal axes are
                    // transposed so the side controller remains outward-facing
                    // and the top core stays four blocks directly behind it.
                    .aisle(" AAAAAAA", " B     B", " B     B", " B     B", " B C C B", " B D D B", " B  E  B", " B     B", " B     B", "   A A  ", "        ", "        ", "        ")
                    .aisle(" AAAAAAA", "  A   A ", "  A   A ", "  A C A ", "  A C A ", "  D   D ", "  EA AE ", "   A A  ", "   A A  ", "   A A  ", "        ", "        ", "        ")
                    .aisle("AAAAAAAA", "B  A A  ", "B  A A  ", "B  A A  ", "B C A A ", "B D A A ", "   A A  ", "   A A  ", "   A A  ", " AAA AAA", "   A A  ", "        ", "        ")
                    .aisle("AAAAAAAA", "        ", "        ", "        ", "C       ", "D       ", "BE     E", "B  A A  ", "B  A A  ", "        ", "        ", "        ", "    K   ")
                    .aisle("AAAAAAAA", "B  A A  ", "B  A A  ", "B  A A  ", "B C A A ", "B D A A ", "   A A  ", "   A A  ", "   A A  ", " AAA AAA", "   A A  ", "        ", "        ")
                    .aisle(" AAAAAAA", "  A   A ", "  A   A ", "  A C A ", "  A C A ", "  D   D ", "  EA AE ", "   A A  ", "   A A  ", "   A A  ", "        ", "        ", "        ")
                    .aisle(" AAAAAAA", " B     B", " B     B", " B     B", " B C C B", " B D D B", " B  E  B", " B     B", " B     B", "   A A  ", "        ", "        ", "        ")
                    .aisle("   ASA  ", "   B B  ", "   B B  ", "   B B  ", "  C B C ", "  D B D ", "        ", "        ", "        ", "        ", "        ", "        ", "        ")
                    .self('S', MetaTileEntityConstellationTower.class)
                    .block('A', marble(BlockMarble.MarbleBlockType.BRICKS))
                    .block('B', marble(BlockMarble.MarbleBlockType.PILLAR))
                    .block('C', marble(BlockMarble.MarbleBlockType.ARCH))
                    .block('D', marble(BlockMarble.MarbleBlockType.RUNED))
                    // Crystal first supplies the JEI/projector candidate; air
                    // remains a valid fallback, so ordinary formation does not
                    // require the ritual ring.
                    .where('E', Elements.choice(Elements.block(ritualCrystal()), Elements.air()))
                    .block('K', towerCore())
                    .any(' ')
                    .buildStructureDefinition());

    private final ConstellationTowerDefinition definition;
    private long legacyStoredEnergy;
    private long currentGeneration;
    private int distributionPermille;
    private boolean skyVisible;
    private boolean constellationActive;
    private boolean dipperRitualActive;

    public MetaTileEntityConstellationTower(ResourceLocation metaTileEntityId,
                                             ConstellationTowerDefinition definition) {
        super(metaTileEntityId);
        this.definition = definition;
    }

    private static IBlockState marble(BlockMarble.MarbleBlockType type) {
        return BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, type);
    }

    private static IBlockState ritualCrystal() {
        return PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                .getState(POConstellationCrystal.CrystalType.RITUAL_CRYSTAL);
    }

    private static IBlockState towerCore() {
        return PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                .getState(POConstellationCrystal.CrystalType.TOWER_CORE);
    }

    public ConstellationTowerDefinition getDefinition() {
        return definition;
    }

    public long getStoredEnergy() {
        TileEntityConstellationCrystal core = getTowerCoreTile();
        return core == null ? legacyStoredEnergy : core.getConstellationEnergyStored();
    }

    public long getCoreTransferLimit() {
        return dipperRitualActive ? AMPLIFIED_CORE_TRANSFER_PER_TICK : NORMAL_CORE_TRANSFER_PER_TICK;
    }

    public long getCoreCapacityLimit() {
        return dipperRitualActive ? AMPLIFIED_ENERGY_CAPACITY : NORMAL_ENERGY_CAPACITY;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityConstellationTower(metaTileEntityId, definition);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public EnumFacing getPreviewFrontFacing() {
        return EnumFacing.SOUTH;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return POTextures.ASTRAL_MARBLE;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    protected void updateFormedValid() {
        if (getWorld().isRemote) return;

        if (dipperRitualActive && !hasDipperRitualStructure()) {
            dipperRitualActive = false;
            markDirty();
        }

        TileEntityConstellationCrystal core = getTowerCoreTile();
        if (core == null) {
            currentGeneration = 0L;
            setActive(false);
            return;
        }
        core.bindTower(definition.getId(), getPos());
        if (legacyStoredEnergy > 0L) {
            legacyStoredEnergy -= core.receiveConstellationEnergy(legacyStoredEnergy);
            markDirty();
        }
        // Stored energy is routed even while generation is disabled. The
        // core's normal/ritual extraction budget remains the link bandwidth.
        core.transferToLinkedNexus();
        if (!isWorkingEnabled()) {
            currentGeneration = 0L;
            setActive(false);
            return;
        }
        if (getOffsetTimer() % SAMPLE_INTERVAL != 0L) return;

        IConstellation constellation = AstralNbtHelper.findConstellation(definition.getId());
        float distribution = refreshSkyState(constellation);

        long requested = 0L;
        if (skyVisible && constellation != null) {
            double multiplier = 0.5D + Math.max(0.0D, Math.min(1.0D, distribution));
            if (constellationActive) multiplier *= 4.0D;
            if (dipperRitualActive) multiplier *= 2.0D;
            requested = Math.max(1L, Math.round(BASE_ENERGY_PER_TICK * multiplier)) * SAMPLE_INTERVAL;
        }
        long accepted = core.receiveConstellationEnergy(requested);
        currentGeneration = accepted / SAMPLE_INTERVAL;
        setActive(currentGeneration > 0L);
    }

    private float refreshSkyState(IConstellation constellation) {
        BlockPos corePos = getTowerBasePos().up(12);
        skyVisible = getWorld().canSeeSky(corePos.up());
        WorldSkyHandler skyHandler = ConstellationSkyHandler.getInstance().getWorldHandler(getWorld());
        float distribution = 0.0F;
        constellationActive = constellation != null && skyHandler != null && skyHandler.isActive(constellation);
        if (constellation != null && skyHandler != null) {
            Float sampled = skyHandler.getCurrentDistribution(constellation, Function.identity());
            distribution = sampled == null ? 0.0F : sampled;
        }
        distributionPermille = Math.max(0, Math.min(1000, Math.round(distribution * 1000.0F)));
        return distribution;
    }

    private BlockPos getTowerBasePos() {
        return getPos().offset(getFrontFacing().getOpposite(), 4);
    }

    private TileEntityConstellationCrystal getTowerCoreTile() {
        if (getWorld() == null) return null;
        if (getWorld().getTileEntity(getTowerBasePos().up(12)) instanceof TileEntityConstellationCrystal core) {
            return core;
        }
        return null;
    }

    public boolean isTowerCoreAt(BlockPos corePos) {
        return getTowerBasePos().up(12).equals(corePos);
    }

    private boolean hasDipperRitualStructure() {
        BlockPos base = getTowerBasePos();
        if (!getWorld().getBlockState(base.up(12)).equals(towerCore())) return false;

        EnumFacing inward = getFrontFacing().getOpposite();
        EnumFacing right = inward.rotateY();
        int[][] ringOffsets = {
                {0, 3}, {2, 2}, {3, 0}, {2, -2},
                {0, -3}, {-2, -2}, {-3, 0}, {-2, 2}
        };
        for (int[] offset : ringOffsets) {
            BlockPos node = base.offset(right, offset[0]).offset(inward, offset[1]).up(6);
            if (!getWorld().getBlockState(node).equals(ritualCrystal())) return false;
        }
        return true;
    }

    @Override
    public boolean onRightClick(EntityPlayer player, EnumHand hand, EnumFacing facing,
                                CuboidRayTraceResult hitResult) {
        return super.onRightClick(player, hand, facing, hitResult);
    }

    /** All tower operation exposed by the physical tower core. */
    public boolean onCoreRightClick(EntityPlayer player, EnumHand hand) {
        if (getWorld().isRemote) return true;

        ItemStack held = player.getHeldItem(hand);
        if (player.isSneaking() && held.getItem() == ItemsAS.skyResonator) {
            activateDipperRitual(player);
            return true;
        }
        if (player.isSneaking() && held.isEmpty()) {
            if (!isStructureFormed()) {
                player.sendStatusMessage(new TextComponentTranslation(
                        "pollution.machine.constellation_tower.turbo.unformed"), true);
                return true;
            }
            setWorkingEnabled(!isWorkingEnabled());
            player.sendStatusMessage(new TextComponentTranslation(
                    isWorkingEnabled() ? "pollution.machine.constellation_tower.core.enabled"
                            : "pollution.machine.constellation_tower.core.disabled"), true);
            return true;
        }
        if (player instanceof EntityPlayerMP) {
            if (usesMui2()) {
                MetaTileEntityGuiFactory.open(player, this);
            } else {
                MetaTileEntityUIFactory.INSTANCE.openUI(getHolder(), (EntityPlayerMP) player);
            }
        }
        return true;
    }

    private void activateDipperRitual(EntityPlayer player) {
        refreshSkyState(AstralNbtHelper.findConstellation(definition.getId()));
        if (!isStructureFormed()) {
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.machine.constellation_tower.turbo.unformed"), true);
        } else if (!skyVisible || !constellationActive) {
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.machine.constellation_tower.turbo.sky"), true);
        } else if (!hasDipperRitualStructure()) {
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.machine.constellation_tower.turbo.structure"), true);
        } else {
            dipperRitualActive = true;
            markDirty();
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.machine.constellation_tower.turbo.success"), true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (legacyStoredEnergy > 0L) data.setLong(NBT_ENERGY, legacyStoredEnergy);
        data.setBoolean(NBT_DIPPER_RITUAL_ACTIVE, dipperRitualActive);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        legacyStoredEnergy = Math.max(0L,
                Math.min(AMPLIFIED_ENERGY_CAPACITY, data.getLong(NBT_ENERGY)));
        dipperRitualActive = data.hasKey(NBT_DIPPER_RITUAL_ACTIVE)
                ? data.getBoolean(NBT_DIPPER_RITUAL_ACTIVE)
                : data.getBoolean(NBT_LEGACY_TURBO_CHARGED);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("pollution.machine.constellation_tower.display.constellation",
                definition.getEnglishName()));
        textList.add(new TextComponentTranslation("pollution.machine.constellation_tower.display.storage",
                getStoredEnergy(), getCoreCapacityLimit()));
        textList.add(new TextComponentTranslation("pollution.machine.constellation_tower.display.generation",
                currentGeneration));
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(isWorkingEnabled(), isActive())
                .addCustom(this::addTowerDisplayText)
                .addWorkingStatusLine();
    }

    private void addTowerDisplayText(KeyManager keyManager, UISyncer syncer) {
        boolean syncedSkyVisible = syncer.syncBoolean(skyVisible);
        boolean syncedActiveConstellation = syncer.syncBoolean(constellationActive);
        int syncedDistribution = syncer.syncInt(distributionPermille);
        long syncedGeneration = syncer.syncLong(currentGeneration);
        long syncedStored = syncer.syncLong(getStoredEnergy());
        long syncedCapacity = syncer.syncLong(getCoreCapacityLimit());
        boolean syncedRitual = syncer.syncBoolean(dipperRitualActive);
        TileEntityConstellationCrystal core = getTowerCoreTile();
        boolean syncedCoreBound = syncer.syncBoolean(core != null
                && definition.getId().equals(core.getConstellationId()));
        long syncedExtracted = syncer.syncLong(core == null ? 0L : core.getExtractedThisTick());
        long syncedTransferLimit = syncer.syncLong(core == null ? 0L : core.getMaxExtractPerTick());
        boolean syncedNetworkLinked = syncer.syncBoolean(core != null && core.hasNexusLink());
        long syncedNetworkTransfer = syncer.syncLong(core == null ? 0L : core.getNetworkTransferThisTick());
        long syncedNexusPos = syncer.syncLong(core == null || core.getLinkedNexusPos() == null
                ? Long.MIN_VALUE : core.getLinkedNexusPos().toLong());
        int syncedNexusDimension = syncer.syncInt(core == null ? Integer.MIN_VALUE
                : core.getLinkedNexusDimension());
        int syncedEndpointType = syncer.syncInt(core == null
                ? TileEntityStarstreamRelay.EndpointType.NEXUS.getId()
                : core.getLinkedEndpointType().getId());
        String syncedNetworkStatus = syncer.syncString(core == null
                ? "pollution.starstream_network.status.unlinked"
                : core.getNetworkStatusTranslationKey());

        keyManager.add(KeyUtil.lang(TextFormatting.LIGHT_PURPLE,
                "pollution.machine.constellation_tower.display.constellation", definition.getEnglishName()));
        keyManager.add(KeyUtil.lang(syncedSkyVisible ? TextFormatting.GREEN : TextFormatting.RED,
                syncedSkyVisible ? "pollution.machine.constellation_tower.display.sky_open"
                        : "pollution.machine.constellation_tower.display.sky_blocked"));
        keyManager.add(KeyUtil.lang(syncedActiveConstellation ? TextFormatting.AQUA : TextFormatting.GRAY,
                syncedActiveConstellation ? "pollution.machine.constellation_tower.display.active_yes"
                        : "pollution.machine.constellation_tower.display.active_no"));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                "pollution.machine.constellation_tower.display.distribution", syncedDistribution / 10.0D));
        keyManager.add(KeyUtil.lang(TextFormatting.GREEN,
                "pollution.machine.constellation_tower.display.generation", syncedGeneration));
        keyManager.add(KeyUtil.lang(syncedRitual ? TextFormatting.GOLD : TextFormatting.GRAY,
                syncedRitual ? "pollution.machine.constellation_tower.display.turbo_on"
                        : "pollution.machine.constellation_tower.display.turbo_off"));
        keyManager.add(KeyUtil.lang(TextFormatting.AQUA,
                "pollution.machine.constellation_tower.display.storage", syncedStored, syncedCapacity));
        if (syncedStored > syncedCapacity) {
            keyManager.add(KeyUtil.lang(TextFormatting.RED,
                    "pollution.machine.constellation_tower.display.storage_overflow",
                    syncedStored - syncedCapacity));
        }
        keyManager.add(KeyUtil.lang(syncedCoreBound ? TextFormatting.GREEN : TextFormatting.RED,
                syncedCoreBound ? "pollution.machine.constellation_tower.display.core_linked"
                        : "pollution.machine.constellation_tower.display.core_unlinked"));
        keyManager.add(KeyUtil.lang(TextFormatting.GOLD,
                "pollution.machine.constellation_tower.display.core_transfer",
                syncedExtracted, syncedTransferLimit));
        keyManager.add(KeyUtil.lang(syncedNetworkLinked ? TextFormatting.GREEN : TextFormatting.GRAY,
                syncedNetworkStatus));
        if (syncedNetworkLinked && syncedNexusPos != Long.MIN_VALUE) {
            BlockPos target = BlockPos.fromLong(syncedNexusPos);
            keyManager.add(KeyUtil.lang(TextFormatting.DARK_AQUA,
                    syncedEndpointType == TileEntityStarstreamRelay.EndpointType.RELAY.getId()
                            ? "pollution.machine.constellation_tower.display.relay_target"
                            : "pollution.machine.constellation_tower.display.nexus_target",
                    syncedNexusDimension, target.getX(), target.getY(), target.getZ()));
            keyManager.add(KeyUtil.lang(TextFormatting.AQUA,
                    "pollution.machine.constellation_tower.display.network_transfer",
                    syncedNetworkTransfer));
        }
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.constellation_tower.tooltip.1", definition.getEnglishName()));
        tooltip.add(I18n.format("pollution.machine.constellation_tower.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.constellation_tower.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.constellation_tower.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.constellation_tower.tooltip.5"));
    }
}
