package meowmel.pollution.common.block.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/** Keeps exactly its own chunk loaded; it never loads an entire network. */
public class TileEntityStarstreamChunkAnchor extends TileEntity implements ITickable {

    private boolean ticketActive;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        if (!ticketActive || world.getTotalWorldTime() % 200L == 0L) {
            ticketActive = StarstreamChunkLoadingManager.force(world, pos);
        }
    }

    public boolean isTicketActive() {
        return ticketActive || StarstreamChunkLoadingManager.isForced(world, pos);
    }

    public void releaseTicket() {
        StarstreamChunkLoadingManager.release(world, pos);
        ticketActive = false;
    }
}
