package meowmel.pollution.api.capability;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

/**
 * Shared constellation-energy receiver used by endgame machines and rituals.
 * Implementations pull energy from a bound Starstream network only when the
 * owning device actually starts work.
 */
public interface IStarstreamOperationCore extends IStarstreamWirelessTerminal {

    long requestConstellationEnergy(World world, BlockPos consumerPos,
                                    String constellationId, long amount,
                                    boolean simulate);

    /** Atomically consumes all requested channels or consumes nothing. */
    boolean consumeConstellationEnergy(World world, BlockPos consumerPos,
                                       Map<String, Long> requirements,
                                       boolean simulate);
}
