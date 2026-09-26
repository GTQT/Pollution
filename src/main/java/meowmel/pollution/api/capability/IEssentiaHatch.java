package meowmel.pollution.api.capability;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * Capability exposed by the Large Essentia Generator's input hatch.
 *
 * <p>Unlike {@link IVisHatch} this stores several aspect types at once, so the controller's burn
 * logic can walk the whole contents the way the original GoodGenerator machine did.</p>
 */
public interface IEssentiaHatch {

    /** Tier of the hatch part; storage scales with it. */
    int getTier();

    /** Total essentia stored across every aspect. */
    int getEssentiaAmount();

    int getEssentiaCapacity();

    /** Amount stored of one specific aspect, or 0. */
    int getEssentiaAmount(Aspect aspect);

    /** Live view of the hatch contents. Callers may read but must not restructure it. */
    AspectList getEssentiaList();

    boolean isFull();

    /**
     * Removes up to {@code amount} of {@code aspect}.
     *
     * @param simulate when true, reports what could be removed without changing anything
     * @return the amount actually removed (or removable, when simulating)
     */
    int drainEssentia(Aspect aspect, int amount, boolean simulate);
}
