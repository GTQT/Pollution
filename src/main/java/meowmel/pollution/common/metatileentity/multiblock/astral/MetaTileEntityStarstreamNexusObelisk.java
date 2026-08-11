package meowmel.pollution.common.metatileentity.multiblock.astral;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.mui.factory.MetaTileEntityGuiFactory;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.mui.widget.ScrollableTextWidget;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import meowmel.pollution.api.capability.StarstreamNetworkConstants;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POStarstreamObelisk;
import meowmel.pollution.common.block.tile.TileEntityStarstreamObeliskCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Endgame 16-channel constellation-energy bank. The controller has no energy
 * hatches: all later transfer integrations target the physical core directly.
 */
public class MetaTileEntityStarstreamNexusObelisk extends MetaTileEntityBaseWithControl {

    public static final int STRUCTURE_SIZE = 29;
    public static final int STRUCTURE_HEIGHT = 33;
    private static final int CENTER = STRUCTURE_SIZE / 2;
    private static final int CORE_HEIGHT_FROM_CONTROLLER = 2;
    private static final int CORE_DEPTH_FROM_CONTROLLER = 14;
    private static final int[][] ANCHOR_OFFSETS = {
            {0, -11}, {4, -10}, {8, -8}, {10, -4},
            {11, 0}, {10, 4}, {8, 8}, {4, 10},
            {0, 11}, {-4, 10}, {-8, 8}, {-10, 4},
            {-11, 0}, {-10, -4}, {-8, -8}, {-4, -10}
    };

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:starstream_nexus_obelisk", MetaTileEntityStarstreamNexusObelisk::buildDefinition);
    private int networkDisplayPage;

    public MetaTileEntityStarstreamNexusObelisk(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected MultiblockUIFactory createUIFactory() {
        return super.createUIFactory().addScreenChildren((screen, syncManager) -> {
            ScrollableTextWidget displayText = WidgetTree.findFirstWithNameNullable(
                    screen, "display_text", ScrollableTextWidget.class);
            if (displayText != null) displayText.width(145);

            BooleanSyncValue wirelessEnabled = new BooleanSyncValue(
                    this::isWirelessOutputEnabled, this::setWirelessOutputEnabled);
            IntSyncValue outputTier = new IntSyncValue(
                    this::getWirelessOutputTier, this::setWirelessOutputTier);
            IntSyncValue displayPage = new IntSyncValue(
                    () -> networkDisplayPage, value -> networkDisplayPage = Math.max(0, Math.min(1, value)));
            BooleanSyncValue cleanup = new BooleanSyncValue(() -> false, requested -> {
                if (!requested) return;
                TileEntityStarstreamObeliskCore core = getCoreTile();
                if (core != null) core.cleanupStaleNetworkRecords();
            });
            syncManager.syncValue("pollution_wireless_enabled", wirelessEnabled);
            syncManager.syncValue("pollution_wireless_limit", outputTier);
            syncManager.syncValue("pollution_network_page", displayPage);
            syncManager.syncValue("pollution_network_cleanup", cleanup);

            ToggleButton wirelessToggle = new ToggleButton()
                    .size(36, 18)
                    .top(3)
                    .right(3)
                    .value(new BoolValue.Dynamic(wirelessEnabled::getBoolValue,
                            wirelessEnabled::setBoolValue))
                    .addTooltip(false, IKey.lang("pollution.machine.starstream_nexus.button.wireless_off"))
                    .addTooltip(true, IKey.lang("pollution.machine.starstream_nexus.button.wireless_on"));
            wirelessToggle.child(false, new TextWidget<>(
                    IKey.lang("pollution.machine.starstream_nexus.button.label.off"))
                    .textAlign(Alignment.Center).size(36, 18));
            wirelessToggle.child(true, new TextWidget<>(
                    IKey.lang("pollution.machine.starstream_nexus.button.label.on"))
                    .textAlign(Alignment.Center).size(36, 18));
            screen.child(wirelessToggle);

            CycleButtonWidget limitButton = new CycleButtonWidget()
                    .size(36, 18)
                    .top(23)
                    .right(3)
                    .value(outputTier)
                    .stateCount(4)
                    .addTooltip(0, IKey.lang("pollution.machine.starstream_nexus.button.limit_25"))
                    .addTooltip(1, IKey.lang("pollution.machine.starstream_nexus.button.limit_50"))
                    .addTooltip(2, IKey.lang("pollution.machine.starstream_nexus.button.limit_75"))
                    .addTooltip(3, IKey.lang("pollution.machine.starstream_nexus.button.limit_100"));
            limitButton.stateChild(0, new TextWidget<>("25%")
                    .textAlign(Alignment.Center).size(36, 18));
            limitButton.stateChild(1, new TextWidget<>("50%")
                    .textAlign(Alignment.Center).size(36, 18));
            limitButton.stateChild(2, new TextWidget<>("75%")
                    .textAlign(Alignment.Center).size(36, 18));
            limitButton.stateChild(3, new TextWidget<>("100%")
                    .textAlign(Alignment.Center).size(36, 18));
            screen.child(limitButton);

            CycleButtonWidget pageButton = new CycleButtonWidget()
                    .size(36, 18)
                    .top(43)
                    .right(3)
                    .value(displayPage)
                    .stateCount(2)
                    .addTooltip(0, IKey.lang("pollution.machine.starstream_nexus.button.page_network"))
                    .addTooltip(1, IKey.lang("pollution.machine.starstream_nexus.button.page_channels"));
            pageButton.stateChild(0, new TextWidget<>(
                    IKey.lang("pollution.machine.starstream_nexus.button.label.network"))
                    .textAlign(Alignment.Center).size(36, 18));
            pageButton.stateChild(1, new TextWidget<>(
                    IKey.lang("pollution.machine.starstream_nexus.button.label.channels"))
                    .textAlign(Alignment.Center).size(36, 18));
            screen.child(pageButton);

            ToggleButton cleanupButton = new ToggleButton()
                    .size(36, 18)
                    .top(63)
                    .right(3)
                    .value(new BoolValue.Dynamic(cleanup::getBoolValue, cleanup::setBoolValue))
                    .addTooltip(false, IKey.lang("pollution.machine.starstream_nexus.button.cleanup"))
                    .addTooltip(true, IKey.lang("pollution.machine.starstream_nexus.button.cleanup"));
            cleanupButton.child(false, new TextWidget<>(
                    IKey.lang("pollution.machine.starstream_nexus.button.label.cleanup"))
                    .textAlign(Alignment.Center).size(36, 18));
            cleanupButton.child(true, new TextWidget<>(
                    IKey.lang("pollution.machine.starstream_nexus.button.label.cleanup"))
                    .textAlign(Alignment.Center).size(36, 18));
            screen.child(cleanupButton);
        });
    }

    private boolean isWirelessOutputEnabled() {
        TileEntityStarstreamObeliskCore core = getCoreTile();
        return core != null && core.isWirelessOutputEnabled();
    }

    private void setWirelessOutputEnabled(boolean enabled) {
        TileEntityStarstreamObeliskCore core = getCoreTile();
        if (core != null) core.setWirelessOutputEnabled(enabled);
    }

    private int getWirelessOutputTier() {
        TileEntityStarstreamObeliskCore core = getCoreTile();
        if (core == null) return 3;
        long max = StarstreamNetworkConstants.NEXUS_WIRELESS_OUTPUT_PER_TICK;
        long limit = core.getWirelessOutputLimit();
        if (limit <= max / 4L) return 0;
        if (limit <= max / 2L) return 1;
        if (limit <= max * 3L / 4L) return 2;
        return 3;
    }

    private void setWirelessOutputTier(int tier) {
        TileEntityStarstreamObeliskCore core = getCoreTile();
        if (core == null) return;
        int clamped = Math.max(0, Math.min(3, tier));
        core.setWirelessOutputLimit(
                StarstreamNetworkConstants.NEXUS_WIRELESS_OUTPUT_PER_TICK
                        * (clamped + 1L) / 4L);
    }

    private static StructureDefinition<?> buildDefinition() {
        DeclarativePatternBuilder builder = DeclarativePatternBuilder.start();
        for (String[] aisle : buildPattern()) builder.aisle(aisle);
        return builder
                .self('S', MetaTileEntityStarstreamNexusObelisk.class)
                .block('A', obelisk(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_CASING))
                .block('R', obelisk(POStarstreamObelisk.ObeliskBlockType.STARSTREAM_RUNED_CASING))
                .block('N', obelisk(POStarstreamObelisk.ObeliskBlockType.CONSTELLATION_ANCHOR))
                .block('K', obelisk(POStarstreamObelisk.ObeliskBlockType.OBELISK_CORE))
                .block('P', marble(BlockMarble.MarbleBlockType.PILLAR))
                .air('#')
                .any(' ')
                .buildStructureDefinition();
    }

    /** Builds a four-fold-symmetric 29x33x29 monument without unreadable aisle literals. */
    private static String[][] buildPattern() {
        char[][][] pattern = new char[STRUCTURE_SIZE][STRUCTURE_HEIGHT][STRUCTURE_SIZE];
        for (char[][] aisle : pattern) {
            for (char[] row : aisle) Arrays.fill(row, ' ');
        }

        // Three-tier ground mandala: square outer ring, cardinal and diagonal spokes.
        for (int dz = -CENTER; dz <= CENTER; dz++) {
            for (int dx = -CENTER; dx <= CENTER; dx++) {
                int edge = Math.max(Math.abs(dx), Math.abs(dz));
                boolean cardinal = Math.abs(dx) <= 1 || Math.abs(dz) <= 1;
                boolean diagonal = Math.abs(Math.abs(dx) - Math.abs(dz)) <= 1;
                if (edge >= 12 || edge <= 6 || cardinal || diagonal) {
                    put(pattern, dx, 0, dz, edge >= 12 || diagonal ? 'R' : 'A');
                }
                if (edge == CENTER || edge <= 5 || cardinal || (diagonal && edge <= 12)) {
                    put(pattern, dx, 1, dz, edge == CENTER ? 'R' : 'A');
                }
                if (edge <= 4) put(pattern, dx, 2, dz, ((dx + dz) & 1) == 0 ? 'R' : 'A');
            }
        }

        // Eight buttressed towers, with taller cardinal towers framing the obelisk.
        int[][] pylons = {
                {0, -12}, {12, 0}, {0, 12}, {-12, 0},
                {9, -9}, {9, 9}, {-9, 9}, {-9, -9}
        };
        for (int i = 0; i < pylons.length; i++) {
            buildPylon(pattern, pylons[i][0], pylons[i][1], i < 4 ? 30 : 22);
        }

        // Floating star-track rings tie all eight towers into one monumental silhouette.
        buildHorizontalRing(pattern, 8, 12.0D, 'R');
        buildHorizontalRing(pattern, 14, 12.0D, 'A');
        buildHorizontalRing(pattern, 20, 9.0D, 'R');

        // Sixteen fixed anchors form a clockwise bank map for the sixteen constellations.
        for (int[] offset : ANCHOR_OFFSETS) {
            put(pattern, offset[0], 1, offset[1], 'R');
            put(pattern, offset[0], 2, offset[1], 'A');
            put(pattern, offset[0], 3, offset[1], 'N');
        }

        // The physical bank core sits at the exact rotational centre of the whole monument.
        put(pattern, 0, 3, 0, 'K');
        for (int y = 4; y <= 24; y++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dx = -2; dx <= 2; dx++) put(pattern, dx, y, dz, '#');
            }
        }

        // Controller is on the south edge and faces outwards; core is fourteen blocks inward.
        put(pattern, 0, 1, CENTER, 'S');

        String[][] aisles = new String[STRUCTURE_SIZE][STRUCTURE_HEIGHT];
        for (int z = 0; z < STRUCTURE_SIZE; z++) {
            for (int y = 0; y < STRUCTURE_HEIGHT; y++) aisles[z][y] = new String(pattern[z][y]);
        }
        return aisles;
    }

    private static void buildPylon(char[][][] pattern, int centerX, int centerZ, int height) {
        for (int y = 1; y <= height; y++) {
            int radius = y <= 2 ? 2 : y <= 4 ? 1 : 0;
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    put(pattern, centerX + dx, y, centerZ + dz, y <= 4 ? 'A' : 'P');
                }
            }
            if (y >= 5 && (y - 5) % 4 == 0) {
                put(pattern, centerX + 1, y, centerZ, 'R');
                put(pattern, centerX - 1, y, centerZ, 'R');
                put(pattern, centerX, y, centerZ + 1, 'R');
                put(pattern, centerX, y, centerZ - 1, 'R');
            }
        }
        put(pattern, centerX, height, centerZ, 'R');
    }

    private static void buildHorizontalRing(char[][][] pattern, int y, double radius, char symbol) {
        for (int dz = -CENTER; dz <= CENTER; dz++) {
            for (int dx = -CENTER; dx <= CENTER; dx++) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (Math.abs(distance - radius) <= 0.42D) put(pattern, dx, y, dz, symbol);
            }
        }
    }

    private static void put(char[][][] pattern, int dx, int y, int dz, char value) {
        int x = CENTER + dx;
        int z = CENTER + dz;
        if (x >= 0 && x < STRUCTURE_SIZE && y >= 0 && y < STRUCTURE_HEIGHT
                && z >= 0 && z < STRUCTURE_SIZE) {
            pattern[z][y][x] = value;
        }
    }

    private static IBlockState obelisk(POStarstreamObelisk.ObeliskBlockType type) {
        return PollutionMetaBlocks.STARSTREAM_OBELISK.getState(type);
    }

    private static IBlockState marble(BlockMarble.MarbleBlockType type) {
        return BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, type);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityStarstreamNexusObelisk(metaTileEntityId);
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
        return POTextures.STARSTREAM_CASING;
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
        TileEntityStarstreamObeliskCore core = getCoreTile();
        if (core != null) core.bindController(getPos());
        setActive(core != null);
    }

    public BlockPos getCorePos() {
        return getPos().offset(getFrontFacing().getOpposite(), CORE_DEPTH_FROM_CONTROLLER)
                .up(CORE_HEIGHT_FROM_CONTROLLER);
    }

    public boolean isCoreAt(BlockPos position) {
        return getCorePos().equals(position);
    }

    public TileEntityStarstreamObeliskCore getCoreTile() {
        if (getWorld() == null) return null;
        return getWorld().getTileEntity(getCorePos()) instanceof TileEntityStarstreamObeliskCore
                ? (TileEntityStarstreamObeliskCore) getWorld().getTileEntity(getCorePos()) : null;
    }

    public boolean onCoreRightClick(EntityPlayer player) {
        if (getWorld().isRemote) return true;
        if (player instanceof EntityPlayerMP) {
            if (usesMui2()) MetaTileEntityGuiFactory.open(player, this);
            else MetaTileEntityUIFactory.INSTANCE.openUI(getHolder(), (EntityPlayerMP) player);
        }
        return true;
    }

    @Override
    public boolean onRightClick(EntityPlayer player, EnumHand hand, EnumFacing facing,
                                CuboidRayTraceResult hitResult) {
        return super.onRightClick(player, hand, facing, hitResult);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        TileEntityStarstreamObeliskCore core = getCoreTile();
        textList.add(new TextComponentTranslation(
                "pollution.machine.starstream_nexus.display.total",
                core == null ? 0L : core.getTotalConstellationEnergyStored(),
                TileEntityStarstreamObeliskCore.TOTAL_CAPACITY));
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.addCustom(this::addNexusDisplayText);
    }

    private void addNexusDisplayText(KeyManager keyManager, UISyncer syncer) {
        TileEntityStarstreamObeliskCore core = getCoreTile();
        int displayPage = syncer.syncInt(() -> networkDisplayPage);
        boolean linked = syncer.syncBoolean(core != null);
        long total = syncer.syncLong(core == null ? 0L : core.getTotalConstellationEnergyStored());
        int inboundLinks = syncer.syncInt(core == null ? 0 : core.getInboundLinkCount());
        int activeInboundLinks = syncer.syncInt(core == null ? 0 : core.getActiveInboundLinkCount());
        long networkInput = syncer.syncLong(core == null ? 0L : core.getInputThisTick());
        boolean wirelessOnline = syncer.syncBoolean(core != null && core.isWirelessNetworkOnline());
        int relayRoots = syncer.syncInt(core == null ? 0 : core.getRootRelayCount());
        int onlineRelayRoots = syncer.syncInt(core == null ? 0 : core.getOnlineRootRelayCount());
        int registeredRelays = syncer.syncInt(core == null ? 0 : core.getRegisteredRelayCount());
        int onlineRelays = syncer.syncInt(core == null ? 0 : core.getOnlineRelayCount());
        int maximumDepth = syncer.syncInt(core == null ? 0 : core.getMaximumRelayDepth());
        int activeConsumers = syncer.syncInt(core == null ? 0 : core.getActiveWirelessConsumerCount());
        int registeredTerminals = syncer.syncInt(core == null ? 0 : core.getRegisteredTerminalCount());
        int onlineTerminals = syncer.syncInt(core == null ? 0 : core.getOnlineTerminalCount());
        long networkOutput = syncer.syncLong(core == null ? 0L : core.getWirelessOutputThisTick());
        long outputLimit = syncer.syncLong(core == null ? 0L : core.getWirelessOutputLimit());
        long[] channelStored = new long[ConstellationTowerDefinition.values().length];
        for (ConstellationTowerDefinition definition : ConstellationTowerDefinition.values()) {
            channelStored[definition.ordinal()] = syncer.syncLong(core == null ? 0L
                    : core.getConstellationEnergyStored(definition.getId()));
        }
        keyManager.add(KeyUtil.lang(linked ? TextFormatting.GREEN : TextFormatting.RED,
                linked ? "pollution.machine.starstream_nexus.display.core_linked"
                        : "pollution.machine.starstream_nexus.display.core_unlinked"));
        keyManager.add(KeyUtil.lang(TextFormatting.AQUA,
                "pollution.machine.starstream_nexus.display.total",
                total, TileEntityStarstreamObeliskCore.TOTAL_CAPACITY));
        if (displayPage == 1) {
            for (ConstellationTowerDefinition definition : ConstellationTowerDefinition.values()) {
                keyManager.add(KeyUtil.lang(TextFormatting.LIGHT_PURPLE,
                        "pollution.machine.starstream_nexus.display.channel",
                        definition.getEnglishName(), channelStored[definition.ordinal()],
                        TileEntityStarstreamObeliskCore.CAPACITY_PER_CONSTELLATION));
            }
            return;
        }
        keyManager.add(KeyUtil.lang(TextFormatting.GREEN,
                "pollution.machine.starstream_nexus.display.input_network",
                activeInboundLinks, inboundLinks, networkInput));
        keyManager.add(KeyUtil.lang(wirelessOnline ? TextFormatting.GOLD : TextFormatting.RED,
                wirelessOnline
                        ? "pollution.machine.starstream_nexus.display.wireless_online"
                        : "pollution.machine.starstream_nexus.display.wireless_offline",
                activeConsumers, networkOutput, outputLimit));
        keyManager.add(KeyUtil.lang(TextFormatting.AQUA,
                "pollution.machine.starstream_nexus.display.terminal_network",
                onlineTerminals, registeredTerminals, activeConsumers,
                StarstreamNetworkConstants.DEFAULT_CONSUMER_OUTPUT_PER_TICK));
        keyManager.add(KeyUtil.lang(TextFormatting.YELLOW,
                "pollution.machine.starstream_nexus.display.relay_network",
                onlineRelays, registeredRelays, onlineRelayRoots, relayRoots, maximumDepth,
                StarstreamNetworkConstants.NEXUS_WIRELESS_RANGE,
                StarstreamNetworkConstants.RELAY_WIRELESS_RANGE));
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.starstream_nexus.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.starstream_nexus.tooltip.2",
                TileEntityStarstreamObeliskCore.CAPACITY_PER_CONSTELLATION));
        tooltip.add(I18n.format("pollution.machine.starstream_nexus.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.starstream_nexus.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.starstream_nexus.tooltip.5"));
    }
}
