package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.POConfig;
import meowmel.pollution.common.block.tile.TileEntityFleshHeart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/** Server-only loaded-core lookup; no chunk loading and no persistent entity buffs. */
public final class DragonGrowthAura {
    private static final Map<World, Snapshot> CACHE = new WeakHashMap<>();
    private DragonGrowthAura() {}

    public static int multiplier(World world, BlockPos target) {
        int radius = Math.max(0, Math.min(128, POConfig.DragonSettings.fleshHeartDragonRadius));
        int multiplier = Math.max(1, Math.min(16, POConfig.DragonSettings.fleshHeartDragonMultiplier));
        if (world.isRemote || radius == 0 || multiplier == 1) return 1;
        long now = world.getTotalWorldTime();
        Snapshot snapshot = CACHE.get(world);
        if (snapshot == null || now < snapshot.time || now - snapshot.time >= 20) {
            snapshot = new Snapshot(now);
            for (TileEntity tile : world.loadedTileEntityList) {
                if (tile instanceof TileEntityFleshHeart && !tile.isInvalid()) snapshot.hearts.add(tile.getPos().toImmutable());
            }
            CACHE.put(world, snapshot);
        }
        for (BlockPos pos : snapshot.hearts) {
            if (pos.distanceSq(target) > (double) radius * radius || !world.isBlockLoaded(pos)) continue;
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityFleshHeart && !tile.isInvalid()
                    && ((TileEntityFleshHeart) tile).getLevel() >= TileEntityFleshHeart.MAX_LEVEL) return multiplier;
        }
        return 1;
    }

    private static final class Snapshot {
        final long time;
        final List<BlockPos> hearts = new ArrayList<>();
        Snapshot(long time) { this.time = time; }
    }
}
