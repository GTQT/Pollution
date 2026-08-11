package meowmel.pollution.api.capability;

import javax.annotation.Nullable;

/**
 * Direct access contract for a constellation tower core.
 * Consumers link to the core block itself; no multiblock hatch is involved.
 */
public interface IConstellationEnergyCore {

    /** Fixed Astral Sorcery constellation id, or {@code null} while unbound. */
    @Nullable
    String getConstellationId();

    long getConstellationEnergyStored();

    long getConstellationEnergyCapacity();

    /** Shared extraction budget for all consumers during the current server tick. */
    long getMaxExtractPerTick();

    long getExtractedThisTick();

    /**
     * Extracts typed constellation energy from this core.
     * The result is clamped by both stored energy and the shared per-tick budget.
     */
    long extractConstellationEnergy(long amount, boolean simulate);
}
