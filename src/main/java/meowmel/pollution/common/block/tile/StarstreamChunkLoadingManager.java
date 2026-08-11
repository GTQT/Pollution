package meowmel.pollution.common.block.tile;

import meowmel.pollution.Pollution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Owns persistent Forge chunk tickets created by Starstream chunk anchors. */
public final class StarstreamChunkLoadingManager
        implements ForgeChunkManager.LoadingCallback {

    private static final String NBT_X = "AnchorX";
    private static final String NBT_Y = "AnchorY";
    private static final String NBT_Z = "AnchorZ";
    private static final StarstreamChunkLoadingManager INSTANCE =
            new StarstreamChunkLoadingManager();
    private final Map<String, ForgeChunkManager.Ticket> tickets = new LinkedHashMap<>();

    private StarstreamChunkLoadingManager() {}

    public static void init() {
        ForgeChunkManager.setForcedChunkLoadingCallback(Pollution.instance, INSTANCE);
    }

    public static boolean force(World world, BlockPos pos) {
        if (world == null || world.isRemote) return false;
        String key = key(world, pos);
        ForgeChunkManager.Ticket current = INSTANCE.tickets.get(key);
        if (current != null) {
            ForgeChunkManager.forceChunk(current, new ChunkPos(pos));
            return true;
        }
        ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(
                Pollution.instance, world, ForgeChunkManager.Type.NORMAL);
        if (ticket == null) return false;
        NBTTagCompound data = ticket.getModData();
        data.setInteger(NBT_X, pos.getX());
        data.setInteger(NBT_Y, pos.getY());
        data.setInteger(NBT_Z, pos.getZ());
        ForgeChunkManager.forceChunk(ticket, new ChunkPos(pos));
        INSTANCE.tickets.put(key, ticket);
        return true;
    }

    public static boolean isForced(World world, BlockPos pos) {
        return world != null && INSTANCE.tickets.containsKey(key(world, pos));
    }

    public static void release(World world, BlockPos pos) {
        if (world == null || world.isRemote) return;
        ForgeChunkManager.Ticket ticket = INSTANCE.tickets.remove(key(world, pos));
        if (ticket == null) return;
        ForgeChunkManager.unforceChunk(ticket, new ChunkPos(pos));
        ForgeChunkManager.releaseTicket(ticket);
    }

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> loaded, World world) {
        for (ForgeChunkManager.Ticket ticket : loaded) {
            NBTTagCompound data = ticket.getModData();
            if (!data.hasKey(NBT_X) || !data.hasKey(NBT_Y) || !data.hasKey(NBT_Z)) {
                ForgeChunkManager.releaseTicket(ticket);
                continue;
            }
            BlockPos pos = new BlockPos(data.getInteger(NBT_X),
                    data.getInteger(NBT_Y), data.getInteger(NBT_Z));
            ForgeChunkManager.forceChunk(ticket, new ChunkPos(pos));
            tickets.put(key(world, pos), ticket);
        }
    }

    private static String key(World world, BlockPos pos) {
        return world.provider.getDimension() + ":" + pos.toLong();
    }
}
