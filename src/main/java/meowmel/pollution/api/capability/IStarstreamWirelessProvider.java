package meowmel.pollution.api.capability;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Common direct-access contract exposed by a formed nexus core and every
 * online relay. Consumers bind to a node; no multiblock hatch is involved.
 */
public interface IStarstreamWirelessProvider {

    /** Network identity, which may remain available while output is disabled. */
    @Nullable
    UUID getWirelessNetworkId();

    int getWirelessRange();

    boolean isWirelessNetworkOnline();

    long requestWirelessEnergy(BlockPos consumerPos, UUID networkId, UUID consumerId,
                               String constellationId, long amount, boolean simulate);

    /** Atomically validates and consumes every requested constellation channel. */
    boolean consumeWirelessEnergy(BlockPos consumerPos, UUID networkId, UUID consumerId,
                                  Map<String, Long> requirements, boolean simulate);
}
