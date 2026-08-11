package meowmel.pollution.common.items.behaviors;

import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import meowmel.pollution.api.capability.IStarstreamWirelessTerminal;
import meowmel.pollution.api.capability.StarstreamNetworkConstants;
import meowmel.pollution.api.capability.StarstreamWirelessBinding;
import meowmel.pollution.common.block.tile.TileEntityConstellationCrystal;
import meowmel.pollution.common.block.tile.TileEntityInterdimensionalStarstreamRelay;
import meowmel.pollution.common.block.tile.TileEntityStarstreamObeliskCore;
import meowmel.pollution.common.block.tile.TileEntityStarstreamRelay;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/** Configures tower input routes and the relay-backed wireless network. */
public final class StarstreamLinkerBehavior implements IItemBehaviour {

    private static final String NBT_LINK = "StarstreamPendingLink";
    private static final String NBT_DIMENSION = "Dimension";
    private static final String NBT_POSITION = "Position";
    private static final String NBT_CONSTELLATION = "Constellation";
    private static final String NBT_SOURCE_TYPE = "SourceType";
    private static final String NBT_SOURCE_ID_MOST = "SourceIdMost";
    private static final String NBT_SOURCE_ID_LEAST = "SourceIdLeast";
    private static final String NBT_NETWORK_CORE_DIMENSION = "NetworkCoreDimension";
    private static final String NBT_NETWORK_CORE_POSITION = "NetworkCorePosition";
    private static final String NBT_MODE = "StarstreamLinkMode";
    private static final String MODE_INPUT = "INPUT";
    private static final String MODE_NETWORK = "NETWORK";
    private static final String LEGACY_MODE_OUTPUT = "OUTPUT";

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack linker = player.getHeldItem(hand);
        if (!player.isSneaking()) return ActionResult.newResult(EnumActionResult.PASS, linker);
        RayTraceResult hit = player.rayTrace(5.0D, 1.0F);
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            return ActionResult.newResult(EnumActionResult.PASS, linker);
        }
        if (!world.isRemote) {
            NBTTagCompound root = getOrCreateRoot(linker);
            String next = MODE_NETWORK.equals(getMode(linker)) ? MODE_INPUT : MODE_NETWORK;
            root.setString(NBT_MODE, next);
            clearPending(linker);
            message(player, MODE_NETWORK.equals(next)
                    ? "pollution.starstream_linker.mode.network"
                    : "pollution.starstream_linker.mode.input");
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, linker);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos,
                                           EnumFacing side, float hitX, float hitY, float hitZ,
                                           EnumHand hand) {
        if (!player.isSneaking()) return EnumActionResult.PASS;
        TileEntity tile = world.getTileEntity(pos);
        ItemStack linker = player.getHeldItem(hand);
        boolean networkMode = MODE_NETWORK.equals(getMode(linker));
        IStarstreamWirelessTerminal terminal = findWirelessTerminal(tile);
        if (!(tile instanceof TileEntityConstellationCrystal)
                && !(tile instanceof TileEntityStarstreamRelay)
                && !(tile instanceof TileEntityStarstreamObeliskCore)
                && !(networkMode && terminal != null)) {
            return EnumActionResult.PASS;
        }
        if (world.isRemote) return EnumActionResult.SUCCESS;

        if (networkMode) {
            if (tile instanceof TileEntityConstellationCrystal) {
                message(player, "pollution.starstream_linker.error.tower_producer_only");
                return EnumActionResult.SUCCESS;
            }
            if (tile instanceof TileEntityStarstreamObeliskCore) {
                return selectNetworkNexus(player, linker, (TileEntityStarstreamObeliskCore) tile);
            }
            if (tile instanceof TileEntityInterdimensionalStarstreamRelay) {
                return useInterdimensionalRelay(player, linker,
                        (TileEntityInterdimensionalStarstreamRelay) tile);
            }
            if (tile instanceof TileEntityStarstreamRelay) {
                return useNetworkRelay(player, linker, (TileEntityStarstreamRelay) tile);
            }
            return useWirelessTerminal(player, linker, tile, terminal);
        }

        if (tile instanceof TileEntityConstellationCrystal) {
            return selectTowerSource(player, linker, (TileEntityConstellationCrystal) tile);
        }
        if (tile instanceof TileEntityStarstreamRelay) {
            return useInputRelay(player, linker, (TileEntityStarstreamRelay) tile);
        }
        return completeNexusInput(player, linker, (TileEntityStarstreamObeliskCore) tile);
    }

    private static EnumActionResult useInterdimensionalRelay(
            EntityPlayer player, ItemStack linker,
            TileEntityInterdimensionalStarstreamRelay target) {
        NBTTagCompound pending = getPending(linker);
        if (pending == null) {
            if (target.isGatewayBound()) {
                target.clearGateway();
                message(player, "pollution.starstream_linker.interdimensional_unbound");
            } else {
                message(player, "pollution.starstream_linker.error.no_network_source");
            }
            return EnumActionResult.SUCCESS;
        }
        TileEntityStarstreamRelay.EndpointType sourceType = getPendingType(pending);
        if (sourceType != TileEntityStarstreamRelay.EndpointType.NEXUS
                && sourceType != TileEntityStarstreamRelay.EndpointType.RELAY) {
            message(player, "pollution.starstream_linker.error.no_network_source");
            return EnumActionResult.SUCCESS;
        }
        int sourceDimension = pending.hasKey(NBT_NETWORK_CORE_DIMENSION)
                ? pending.getInteger(NBT_NETWORK_CORE_DIMENSION)
                : pending.getInteger(NBT_DIMENSION);
        World sourceWorld = DimensionManager.getWorld(sourceDimension);
        if (sourceWorld == null) {
            message(player, "pollution.starstream_linker.error.interdimensional_source_unloaded");
            return EnumActionResult.SUCCESS;
        }
        BlockPos sourcePos = BlockPos.fromLong(pending.hasKey(NBT_NETWORK_CORE_POSITION)
                ? pending.getLong(NBT_NETWORK_CORE_POSITION)
                : pending.getLong(NBT_POSITION));
        if (!sourceWorld.isBlockLoaded(sourcePos)) {
            message(player, "pollution.starstream_linker.error.interdimensional_source_unloaded");
            return EnumActionResult.SUCCESS;
        }
        TileEntity sourceTile = sourceWorld.getTileEntity(sourcePos);
        TileEntityStarstreamObeliskCore core;
        if (pending.hasKey(NBT_NETWORK_CORE_POSITION)
                && sourceTile instanceof TileEntityStarstreamObeliskCore) {
            core = (TileEntityStarstreamObeliskCore) sourceTile;
        } else if (sourceType == TileEntityStarstreamRelay.EndpointType.NEXUS
                && sourceTile instanceof TileEntityStarstreamObeliskCore) {
            core = (TileEntityStarstreamObeliskCore) sourceTile;
        } else if (sourceType == TileEntityStarstreamRelay.EndpointType.RELAY
                && sourceTile instanceof TileEntityStarstreamRelay) {
            core = ((TileEntityStarstreamRelay) sourceTile).findNetworkCore();
        } else {
            core = null;
        }
        UUID selectedNetwork = pendingRelayId(pending);
        if (core == null || !core.isLinkedAndFormed()
                || !selectedNetwork.equals(core.getNetworkId())) {
            message(player, "pollution.starstream_linker.error.interdimensional_source_unloaded");
            return EnumActionResult.SUCCESS;
        }
        target.bindGateway(core.getWorld().provider.getDimension(), core.getPos(), selectedNetwork);
        core.heartbeatWirelessRelay(target.getWorld().provider.getDimension(), target.getPos(),
                target.getRelayId(), core.getWorld().provider.getDimension(), core.getPos(), 1);
        message(player, "pollution.starstream_linker.interdimensional_bound",
                target.getWorld().provider.getDimension(),
                core.getWorld().provider.getDimension(),
                selectedNetwork.toString().substring(0, 8));
        return EnumActionResult.SUCCESS;
    }

    private static EnumActionResult useWirelessTerminal(EntityPlayer player, ItemStack linker,
                                                         TileEntity tile,
                                                         IStarstreamWirelessTerminal terminal) {
        StarstreamWirelessBinding binding = terminal.getStarstreamWirelessBinding();
        if (binding == null) {
            message(player, "pollution.starstream_linker.error.invalid_terminal");
            return EnumActionResult.SUCCESS;
        }
        NBTTagCompound pending = getPending(linker);
        if (pending == null) {
            if (!binding.isBound()) {
                message(player, "pollution.starstream_linker.error.no_network_source");
                return EnumActionResult.SUCCESS;
            }
            binding.clear();
            terminal.onStarstreamNetworkChanged(null);
            markTerminalChanged(tile);
            message(player, "pollution.starstream_linker.terminal_unbound",
                    tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
            return EnumActionResult.SUCCESS;
        }

        TileEntityStarstreamRelay.EndpointType sourceType = getPendingType(pending);
        if (sourceType != TileEntityStarstreamRelay.EndpointType.NEXUS
                && sourceType != TileEntityStarstreamRelay.EndpointType.RELAY) {
            message(player, "pollution.starstream_linker.error.no_network_source");
            return EnumActionResult.SUCCESS;
        }
        UUID selectedNetwork = pendingRelayId(pending);
        if (selectedNetwork.getMostSignificantBits() == 0L
                && selectedNetwork.getLeastSignificantBits() == 0L) {
            message(player, "pollution.starstream_linker.error.no_network_source");
            return EnumActionResult.SUCCESS;
        }
        if (!terminal.canBindStarstreamNetwork(player, selectedNetwork)) {
            message(player, "pollution.starstream_linker.error.terminal_denied");
            return EnumActionResult.SUCCESS;
        }

        binding.bind(selectedNetwork);
        terminal.onStarstreamNetworkChanged(selectedNetwork);
        markTerminalChanged(tile);
        message(player, "pollution.starstream_linker.terminal_bound",
                selectedNetwork.toString().substring(0, 8),
                tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
        // Keep the selected network in the linker so several terminals can be
        // configured in one pass without returning to the nexus each time.
        return EnumActionResult.SUCCESS;
    }

    @Nullable
    private static IStarstreamWirelessTerminal findWirelessTerminal(TileEntity tile) {
        if (tile instanceof IStarstreamWirelessTerminal) {
            return (IStarstreamWirelessTerminal) tile;
        }
        if (tile instanceof IGregTechTileEntity) {
            MetaTileEntity metaTileEntity = ((IGregTechTileEntity) tile).getMetaTileEntity();
            if (metaTileEntity instanceof IStarstreamWirelessTerminal) {
                return (IStarstreamWirelessTerminal) metaTileEntity;
            }
        }
        return null;
    }

    private static void markTerminalChanged(TileEntity tile) {
        tile.markDirty();
        World world = tile.getWorld();
        if (world != null) {
            world.notifyBlockUpdate(tile.getPos(), world.getBlockState(tile.getPos()),
                    world.getBlockState(tile.getPos()), 3);
        }
    }

    private static EnumActionResult selectTowerSource(EntityPlayer player, ItemStack linker,
                                                       TileEntityConstellationCrystal source) {
        if (!source.isOperationalTowerCore()) {
            message(player, "pollution.starstream_linker.error.invalid_tower");
            return EnumActionResult.SUCCESS;
        }
        NBTTagCompound pending = getPending(linker);
        if (isSamePending(pending, source.getWorld(), source.getPos(),
                TileEntityStarstreamRelay.EndpointType.TOWER)) {
            clearPending(linker);
            if (source.hasNexusLink()) {
                source.clearNexusLink();
                message(player, "pollution.starstream_linker.unlinked");
            } else {
                message(player, "pollution.starstream_linker.cancelled");
            }
            return EnumActionResult.SUCCESS;
        }

        setPendingTower(linker, source);
        message(player, source.hasNexusLink()
                        ? "pollution.starstream_linker.source_relink"
                        : "pollution.starstream_linker.source_selected",
                source.getConstellationId(), source.getPos().getX(),
                source.getPos().getY(), source.getPos().getZ());
        return EnumActionResult.SUCCESS;
    }

    private static EnumActionResult useInputRelay(EntityPlayer player, ItemStack linker,
                                                   TileEntityStarstreamRelay target) {
        NBTTagCompound pending = getPending(linker);
        if (pending == null) {
            setPendingRelay(linker, target);
            message(player, "pollution.starstream_linker.relay_selected",
                    target.getPos().getX(), target.getPos().getY(), target.getPos().getZ());
            return EnumActionResult.SUCCESS;
        }
        if (isSamePending(pending, target.getWorld(), target.getPos(),
                TileEntityStarstreamRelay.EndpointType.RELAY)) {
            clearPending(linker);
            if (target.hasOutput()) {
                target.clearOutput();
                message(player, "pollution.starstream_linker.relay_unlinked");
            } else {
                message(player, "pollution.starstream_linker.cancelled");
            }
            return EnumActionResult.SUCCESS;
        }
        if (!validateDimensionAndRange(player, pending, target.getWorld(), target.getPos())) {
            return EnumActionResult.SUCCESS;
        }

        World world = target.getWorld();
        BlockPos sourcePos = BlockPos.fromLong(pending.getLong(NBT_POSITION));
        if (!world.isBlockLoaded(sourcePos)) {
            message(player, "pollution.starstream_linker.error.source_unavailable");
            return EnumActionResult.SUCCESS;
        }
        TileEntityStarstreamRelay.EndpointType sourceType = getPendingType(pending);
        if (sourceType == TileEntityStarstreamRelay.EndpointType.TOWER) {
            TileEntity sourceTile = world.getTileEntity(sourcePos);
            if (!(sourceTile instanceof TileEntityConstellationCrystal)) {
                message(player, "pollution.starstream_linker.error.source_changed");
                return EnumActionResult.SUCCESS;
            }
            TileEntityConstellationCrystal tower = (TileEntityConstellationCrystal) sourceTile;
            if (!tower.isOperationalTowerCore()
                    || !pending.getString(NBT_CONSTELLATION).equals(tower.getConstellationId())) {
                message(player, "pollution.starstream_linker.error.source_changed");
                return EnumActionResult.SUCCESS;
            }
            if (!target.registerInbound(sourcePos, TileEntityStarstreamRelay.EndpointType.TOWER)) {
                message(player, "pollution.starstream_linker.error.relay_input_limit",
                        StarstreamNetworkConstants.MAX_RELAY_INPUTS);
                return EnumActionResult.SUCCESS;
            }
            tower.bindRelay(world.provider.getDimension(), target.getPos(), target.getRelayId());
        } else if (sourceType == TileEntityStarstreamRelay.EndpointType.RELAY) {
            TileEntity sourceTile = world.getTileEntity(sourcePos);
            if (!(sourceTile instanceof TileEntityStarstreamRelay)
                    || !pendingRelayId(pending)
                    .equals(((TileEntityStarstreamRelay) sourceTile).getRelayId())) {
                message(player, "pollution.starstream_linker.error.relay_changed");
                return EnumActionResult.SUCCESS;
            }
            TileEntityStarstreamRelay source = (TileEntityStarstreamRelay) sourceTile;
            if (target.routeContains(source.getRelayId())) {
                message(player, "pollution.starstream_linker.error.relay_loop");
                return EnumActionResult.SUCCESS;
            }
            if (!target.registerInbound(sourcePos, TileEntityStarstreamRelay.EndpointType.RELAY)) {
                message(player, "pollution.starstream_linker.error.relay_input_limit",
                        StarstreamNetworkConstants.MAX_RELAY_INPUTS);
                return EnumActionResult.SUCCESS;
            }
            source.bindOutput(world.provider.getDimension(), target.getPos(), target.getRelayId(),
                    TileEntityStarstreamRelay.EndpointType.RELAY);
        } else {
            message(player, "pollution.starstream_linker.error.no_source");
            return EnumActionResult.SUCCESS;
        }

        long distance = Math.round(Math.sqrt(sourcePos.distanceSq(target.getPos())));
        setPendingRelay(linker, target);
        message(player, "pollution.starstream_linker.relay_segment_success", distance,
                target.getPos().getX(), target.getPos().getY(), target.getPos().getZ());
        return EnumActionResult.SUCCESS;
    }

    private static EnumActionResult completeNexusInput(EntityPlayer player, ItemStack linker,
                                                        TileEntityStarstreamObeliskCore target) {
        NBTTagCompound pending = getPending(linker);
        if (pending == null) {
            message(player, "pollution.starstream_linker.error.no_source");
            return EnumActionResult.SUCCESS;
        }
        if (!validateDimensionAndRange(player, pending, target.getWorld(), target.getPos())) {
            return EnumActionResult.SUCCESS;
        }
        if (!target.isLinkedAndFormed()) {
            message(player, "pollution.starstream_linker.error.nexus_unformed");
            return EnumActionResult.SUCCESS;
        }

        World world = target.getWorld();
        BlockPos sourcePos = BlockPos.fromLong(pending.getLong(NBT_POSITION));
        if (!world.isBlockLoaded(sourcePos)) {
            message(player, "pollution.starstream_linker.error.source_unavailable");
            return EnumActionResult.SUCCESS;
        }
        TileEntityStarstreamRelay.EndpointType sourceType = getPendingType(pending);
        UUID networkId = target.getNetworkId();
        if (sourceType == TileEntityStarstreamRelay.EndpointType.TOWER) {
            TileEntity sourceTile = world.getTileEntity(sourcePos);
            if (!(sourceTile instanceof TileEntityConstellationCrystal)) {
                message(player, "pollution.starstream_linker.error.source_changed");
                return EnumActionResult.SUCCESS;
            }
            TileEntityConstellationCrystal tower = (TileEntityConstellationCrystal) sourceTile;
            String constellation = pending.getString(NBT_CONSTELLATION);
            if (!tower.isOperationalTowerCore() || !constellation.equals(tower.getConstellationId())) {
                message(player, "pollution.starstream_linker.error.source_changed");
                return EnumActionResult.SUCCESS;
            }
            if (!target.registerInboundTower(sourcePos, constellation)) {
                message(player, "pollution.starstream_linker.error.input_limit",
                        StarstreamNetworkConstants.MAX_DIRECT_INPUT_LINKS);
                return EnumActionResult.SUCCESS;
            }
            tower.bindNexus(world.provider.getDimension(), target.getPos(), networkId);
            clearPending(linker);
            message(player, "pollution.starstream_linker.success", constellation,
                    Math.round(Math.sqrt(sourcePos.distanceSq(target.getPos()))));
            return EnumActionResult.SUCCESS;
        }
        if (sourceType == TileEntityStarstreamRelay.EndpointType.RELAY) {
            TileEntity sourceTile = world.getTileEntity(sourcePos);
            if (!(sourceTile instanceof TileEntityStarstreamRelay)
                    || !pendingRelayId(pending)
                    .equals(((TileEntityStarstreamRelay) sourceTile).getRelayId())) {
                message(player, "pollution.starstream_linker.error.relay_changed");
                return EnumActionResult.SUCCESS;
            }
            if (!target.registerInboundRelay(sourcePos)) {
                message(player, "pollution.starstream_linker.error.input_limit",
                        StarstreamNetworkConstants.MAX_DIRECT_INPUT_LINKS);
                return EnumActionResult.SUCCESS;
            }
            ((TileEntityStarstreamRelay) sourceTile).bindOutput(
                    world.provider.getDimension(), target.getPos(), networkId,
                    TileEntityStarstreamRelay.EndpointType.NEXUS);
            clearPending(linker);
            message(player, "pollution.starstream_linker.relay_route_success",
                    Math.round(Math.sqrt(sourcePos.distanceSq(target.getPos()))));
            return EnumActionResult.SUCCESS;
        }
        message(player, "pollution.starstream_linker.error.no_source");
        return EnumActionResult.SUCCESS;
    }

    private static EnumActionResult selectNetworkNexus(EntityPlayer player, ItemStack linker,
                                                        TileEntityStarstreamObeliskCore nexus) {
        if (!nexus.isLinkedAndFormed()) {
            message(player, "pollution.starstream_linker.error.nexus_unformed");
            return EnumActionResult.SUCCESS;
        }
        NBTTagCompound pending = getPending(linker);
        if (isSamePending(pending, nexus.getWorld(), nexus.getPos(),
                TileEntityStarstreamRelay.EndpointType.NEXUS)) {
            clearPending(linker);
            message(player, "pollution.starstream_linker.cancelled");
            return EnumActionResult.SUCCESS;
        }
        setPendingNexus(linker, nexus);
        message(player, "pollution.starstream_linker.network_source_nexus",
                nexus.getPos().getX(), nexus.getPos().getY(), nexus.getPos().getZ());
        return EnumActionResult.SUCCESS;
    }

    private static EnumActionResult useNetworkRelay(EntityPlayer player, ItemStack linker,
                                                     TileEntityStarstreamRelay target) {
        NBTTagCompound pending = getPending(linker);
        if (pending == null) {
            TileEntityStarstreamObeliskCore networkCore = target.findNetworkCore();
            if (networkCore == null) {
                message(player, "pollution.starstream_linker.error.relay_not_online");
                return EnumActionResult.SUCCESS;
            }
            UUID networkId = networkCore.getNetworkId();
            setPendingNetworkRelay(linker, target, networkId);
            message(player, "pollution.starstream_linker.network_source_relay",
                    target.getPos().getX(), target.getPos().getY(), target.getPos().getZ());
            return EnumActionResult.SUCCESS;
        }
        if (isSamePending(pending, target.getWorld(), target.getPos(),
                TileEntityStarstreamRelay.EndpointType.RELAY)) {
            clearPending(linker);
            message(player, "pollution.starstream_linker.cancelled");
            return EnumActionResult.SUCCESS;
        }
        TileEntityStarstreamObeliskCore targetNetworkCore = target.findNetworkCore();
        if (targetNetworkCore != null) {
            UUID networkId = targetNetworkCore.getNetworkId();
            setPendingNetworkRelay(linker, target, networkId);
            message(player, "pollution.starstream_linker.network_source_relay",
                    target.getPos().getX(), target.getPos().getY(), target.getPos().getZ());
            return EnumActionResult.SUCCESS;
        }
        if (!validateDimensionAndRange(player, pending, target.getWorld(), target.getPos())) {
            return EnumActionResult.SUCCESS;
        }

        World world = target.getWorld();
        BlockPos parentPos = BlockPos.fromLong(pending.getLong(NBT_POSITION));
        if (!world.isBlockLoaded(parentPos)) {
            message(player, "pollution.starstream_linker.error.source_unavailable");
            return EnumActionResult.SUCCESS;
        }
        UUID networkId = pendingRelayId(pending);
        TileEntityStarstreamRelay.EndpointType parentType = getPendingType(pending);
        if (parentType == TileEntityStarstreamRelay.EndpointType.NEXUS) {
            TileEntity tile = world.getTileEntity(parentPos);
            if (!(tile instanceof TileEntityStarstreamObeliskCore)) {
                message(player, "pollution.starstream_linker.error.nexus_unformed");
                return EnumActionResult.SUCCESS;
            }
            TileEntityStarstreamObeliskCore nexus = (TileEntityStarstreamObeliskCore) tile;
            if (!nexus.isLinkedAndFormed() || !networkId.equals(nexus.getNetworkId())) {
                message(player, "pollution.starstream_linker.error.nexus_unformed");
                return EnumActionResult.SUCCESS;
            }
            if (!nexus.registerInboundRelay(target.getPos())) {
                message(player, "pollution.starstream_linker.error.input_limit",
                        StarstreamNetworkConstants.MAX_DIRECT_INPUT_LINKS);
                return EnumActionResult.SUCCESS;
            }
            target.bindOutput(world.provider.getDimension(), parentPos, networkId,
                    TileEntityStarstreamRelay.EndpointType.NEXUS);
        } else if (parentType == TileEntityStarstreamRelay.EndpointType.RELAY) {
            TileEntity tile = world.getTileEntity(parentPos);
            if (!(tile instanceof TileEntityStarstreamRelay)) {
                message(player, "pollution.starstream_linker.error.relay_changed");
                return EnumActionResult.SUCCESS;
            }
            TileEntityStarstreamRelay parent = (TileEntityStarstreamRelay) tile;
            TileEntityStarstreamObeliskCore networkCore = parent.findNetworkCore();
            if (networkCore == null || !networkId.equals(networkCore.getNetworkId())) {
                message(player, "pollution.starstream_linker.error.relay_not_online");
                return EnumActionResult.SUCCESS;
            }
            if (parent.getNetworkDepth() >= StarstreamNetworkConstants.MAX_RELAY_HOPS) {
                message(player, "pollution.starstream_linker.error.relay_depth_limit",
                        StarstreamNetworkConstants.MAX_RELAY_HOPS);
                return EnumActionResult.SUCCESS;
            }
            if (target.routeContains(parent.getRelayId())) {
                message(player, "pollution.starstream_linker.error.relay_loop");
                return EnumActionResult.SUCCESS;
            }
            if (!parent.registerInbound(target.getPos(), TileEntityStarstreamRelay.EndpointType.RELAY)) {
                message(player, "pollution.starstream_linker.error.relay_input_limit",
                        StarstreamNetworkConstants.MAX_RELAY_INPUTS);
                return EnumActionResult.SUCCESS;
            }
            target.bindOutput(world.provider.getDimension(), parentPos, parent.getRelayId(),
                    TileEntityStarstreamRelay.EndpointType.RELAY);
        } else {
            message(player, "pollution.starstream_linker.error.no_network_source");
            return EnumActionResult.SUCCESS;
        }

        long distance = Math.round(Math.sqrt(parentPos.distanceSq(target.getPos())));
        setPendingNetworkRelay(linker, target, networkId);
        message(player, "pollution.starstream_linker.network_extended", distance,
                target.getPos().getX(), target.getPos().getY(), target.getPos().getZ(),
                StarstreamNetworkConstants.RELAY_WIRELESS_RANGE);
        return EnumActionResult.SUCCESS;
    }

    private static boolean validateDimensionAndRange(EntityPlayer player, NBTTagCompound pending,
                                                      World targetWorld, BlockPos targetPos) {
        if (pending.getInteger(NBT_DIMENSION) != targetWorld.provider.getDimension()) {
            message(player, "pollution.starstream_linker.error.dimension");
            return false;
        }
        BlockPos sourcePos = BlockPos.fromLong(pending.getLong(NBT_POSITION));
        double distanceSq = sourcePos.distanceSq(targetPos);
        if (distanceSq > StarstreamNetworkConstants.DIRECT_LINK_RANGE_SQUARED) {
            message(player, "pollution.starstream_linker.error.range",
                    Math.round(Math.sqrt(distanceSq)), StarstreamNetworkConstants.DIRECT_LINK_RANGE);
            return false;
        }
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, List<String> lines) {
        boolean networkMode = MODE_NETWORK.equals(getMode(stack));
        lines.add((networkMode ? TextFormatting.GOLD : TextFormatting.GREEN)
                + I18n.format(networkMode
                ? "metaitem.starstream_linker.mode.network"
                : "metaitem.starstream_linker.mode.input"));
        lines.add(TextFormatting.GRAY + I18n.format("metaitem.starstream_linker.tooltip.1",
                StarstreamNetworkConstants.DIRECT_LINK_RANGE));
        lines.add(TextFormatting.DARK_AQUA + I18n.format("metaitem.starstream_linker.tooltip.2"));
        if (networkMode) {
            lines.add(TextFormatting.LIGHT_PURPLE
                    + I18n.format("metaitem.starstream_linker.tooltip.3"));
            lines.add(TextFormatting.AQUA
                    + I18n.format("metaitem.starstream_linker.tooltip.4"));
            lines.add(TextFormatting.DARK_PURPLE
                    + I18n.format("metaitem.starstream_linker.tooltip.5"));
        }
        NBTTagCompound pending = getPending(stack);
        if (pending != null) {
            BlockPos sourcePos = BlockPos.fromLong(pending.getLong(NBT_POSITION));
            TileEntityStarstreamRelay.EndpointType type = getPendingType(pending);
            String source = type == TileEntityStarstreamRelay.EndpointType.NEXUS
                    ? I18n.format("tile.starstream_obelisk.obelisk_core.name")
                    : type == TileEntityStarstreamRelay.EndpointType.RELAY
                    ? I18n.format("tile.starstream_relay.name")
                    : pending.getString(NBT_CONSTELLATION)
                    + I18n.format("metaitem.starstream_linker.pending.tower_suffix");
            lines.add(TextFormatting.AQUA + I18n.format(
                    networkMode ? "metaitem.starstream_linker.pending.network"
                            : "metaitem.starstream_linker.pending",
                    source, sourcePos.getX(), sourcePos.getY(), sourcePos.getZ()));
        }
    }

    private static void setPendingTower(ItemStack linker, TileEntityConstellationCrystal source) {
        NBTTagCompound pending = newPending(source.getWorld(), source.getPos(),
                TileEntityStarstreamRelay.EndpointType.TOWER);
        pending.setString(NBT_CONSTELLATION, source.getConstellationId());
        getOrCreateRoot(linker).setTag(NBT_LINK, pending);
    }

    private static void setPendingRelay(ItemStack linker, TileEntityStarstreamRelay source) {
        NBTTagCompound pending = newPending(source.getWorld(), source.getPos(),
                TileEntityStarstreamRelay.EndpointType.RELAY);
        setPendingId(pending, source.getRelayId());
        getOrCreateRoot(linker).setTag(NBT_LINK, pending);
    }

    private static void setPendingNexus(ItemStack linker, TileEntityStarstreamObeliskCore nexus) {
        NBTTagCompound pending = newPending(nexus.getWorld(), nexus.getPos(),
                TileEntityStarstreamRelay.EndpointType.NEXUS);
        setPendingId(pending, nexus.getNetworkId());
        pending.setInteger(NBT_NETWORK_CORE_DIMENSION,
                nexus.getWorld().provider.getDimension());
        pending.setLong(NBT_NETWORK_CORE_POSITION, nexus.getPos().toLong());
        getOrCreateRoot(linker).setTag(NBT_LINK, pending);
    }

    private static void setPendingNetworkRelay(ItemStack linker, TileEntityStarstreamRelay relay,
                                               UUID networkId) {
        NBTTagCompound pending = newPending(relay.getWorld(), relay.getPos(),
                TileEntityStarstreamRelay.EndpointType.RELAY);
        setPendingId(pending, networkId);
        TileEntityStarstreamObeliskCore core = relay.findNetworkCore();
        if (core != null && core.getWorld() != null) {
            pending.setInteger(NBT_NETWORK_CORE_DIMENSION,
                    core.getWorld().provider.getDimension());
            pending.setLong(NBT_NETWORK_CORE_POSITION, core.getPos().toLong());
        }
        getOrCreateRoot(linker).setTag(NBT_LINK, pending);
    }

    private static void setPendingId(NBTTagCompound pending, UUID id) {
        pending.setLong(NBT_SOURCE_ID_MOST, id.getMostSignificantBits());
        pending.setLong(NBT_SOURCE_ID_LEAST, id.getLeastSignificantBits());
    }

    private static NBTTagCompound newPending(World world, BlockPos pos,
                                             TileEntityStarstreamRelay.EndpointType type) {
        NBTTagCompound pending = new NBTTagCompound();
        pending.setInteger(NBT_DIMENSION, world.provider.getDimension());
        pending.setLong(NBT_POSITION, pos.toLong());
        pending.setByte(NBT_SOURCE_TYPE, type.getId());
        return pending;
    }

    private static TileEntityStarstreamRelay.EndpointType getPendingType(NBTTagCompound pending) {
        if (!pending.hasKey(NBT_SOURCE_TYPE)) return TileEntityStarstreamRelay.EndpointType.TOWER;
        return TileEntityStarstreamRelay.EndpointType.fromId(pending.getByte(NBT_SOURCE_TYPE));
    }

    private static UUID pendingRelayId(NBTTagCompound pending) {
        return new UUID(pending.getLong(NBT_SOURCE_ID_MOST), pending.getLong(NBT_SOURCE_ID_LEAST));
    }

    private static boolean isSamePending(NBTTagCompound pending, World world, BlockPos pos,
                                         TileEntityStarstreamRelay.EndpointType type) {
        return pending != null && getPendingType(pending) == type
                && pending.getInteger(NBT_DIMENSION) == world.provider.getDimension()
                && pending.getLong(NBT_POSITION) == pos.toLong();
    }

    private static void message(EntityPlayer player, String key, Object... args) {
        player.sendStatusMessage(new TextComponentTranslation(key, args), true);
    }

    private static NBTTagCompound getPending(ItemStack stack) {
        NBTTagCompound root = stack.getTagCompound();
        return root != null && root.hasKey(NBT_LINK, 10) ? root.getCompoundTag(NBT_LINK) : null;
    }

    private static NBTTagCompound getOrCreateRoot(ItemStack stack) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            stack.setTagCompound(root);
        }
        return root;
    }

    private static String getMode(ItemStack stack) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) return MODE_INPUT;
        String mode = root.getString(NBT_MODE);
        return MODE_NETWORK.equals(mode) || LEGACY_MODE_OUTPUT.equals(mode)
                ? MODE_NETWORK : MODE_INPUT;
    }

    public static boolean isNetworkMode(ItemStack stack) {
        return MODE_NETWORK.equals(getMode(stack));
    }

    @Nullable
    public static UUID getSelectedNetworkId(ItemStack stack) {
        if (!isNetworkMode(stack)) return null;
        NBTTagCompound pending = getPending(stack);
        if (pending == null
                || (getPendingType(pending) != TileEntityStarstreamRelay.EndpointType.NEXUS
                && getPendingType(pending) != TileEntityStarstreamRelay.EndpointType.RELAY)) return null;
        UUID id = pendingRelayId(pending);
        return id.getMostSignificantBits() == 0L && id.getLeastSignificantBits() == 0L
                ? null : id;
    }

    private static void clearPending(ItemStack stack) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) return;
        root.removeTag(NBT_LINK);
        if (root.isEmpty()) stack.setTagCompound(null);
    }
}
