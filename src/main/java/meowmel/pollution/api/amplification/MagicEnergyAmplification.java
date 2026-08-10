package meowmel.pollution.api.amplification;

import meowmel.pollution.api.capability.IAstralHatch;
import meowmel.pollution.api.capability.ITarotHatch;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Read-only amplification context for non-recipe magic energy machines.
 *
 * <p>Energy machines do not consume a magic recipe, therefore they must not reuse
 * {@link MagicAmplificationEngine}'s recipe cost/output rules. This class only
 * exposes bounded capacity, transfer and generation bonuses. A constellation wafer
 * is still mandatory, and its live-sky bonus is inherited from
 * {@link AstralAmplifierSnapshot}.</p>
 */
public final class MagicEnergyAmplification {

    public enum MachineKind {
        BATTERY,
        LARGE_TURBINE,
        MEGA_TURBINE
    }

    private static final double MAX_BONUS = 0.20D;

    private final AstralAmplifierSnapshot astral;
    private final String tarot;
    private final double strength;
    private final double capacityBonus;
    private final double transferBonus;
    private final double generationBonus;

    private MagicEnergyAmplification(AstralAmplifierSnapshot astral, String tarot, double strength,
                                     double capacityBonus, double transferBonus, double generationBonus) {
        this.astral = astral;
        this.tarot = tarot;
        this.strength = strength;
        this.capacityBonus = cap(capacityBonus);
        this.transferBonus = cap(transferBonus);
        this.generationBonus = cap(generationBonus);
    }

    public static MagicEnergyAmplification empty() {
        return new MagicEnergyAmplification(AstralAmplifierSnapshot.empty(), "", 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public static MagicEnergyAmplification read(List<IAstralHatch> astralHatches,
                                                 List<ITarotHatch> tarotHatches,
                                                 MachineKind kind) {
        IAstralHatch astralHatch = firstAstral(astralHatches);
        AstralAmplifierSnapshot astral = AstralAmplifierSnapshot.from(astralHatch);
        if (!astral.hasDataWafer()) return empty();

        String tarot = normalize(firstTarot(tarotHatches));
        double strength = astral.getBaseStrength() + astral.getSkyStrength();
        if ("the_star".equals(tarot)) strength *= 1.25D;
        strength = Math.min(0.50D, strength);

        String constellation = normalize(astral.getConstellation());
        double capacity = 0.0D;
        double transfer = 0.0D;
        double generation = 0.0D;

        switch (kind) {
            case BATTERY:
                if ("armara".equals(constellation)) {
                    capacity += 0.30D * strength;
                } else if ("vicio".equals(constellation)) {
                    transfer += 0.30D * strength;
                } else if ("alcara".equals(constellation) && astral.isAdvancedHatch()) {
                    transfer += 0.30D * strength;
                } else if ("ulteria".equals(constellation) && astral.isAdvancedHatch()) {
                    capacity += 0.30D * strength;
                }
                if ("the_emperor".equals(tarot)) capacity += 0.10D * strength;
                if ("the_world".equals(tarot)) transfer += 0.10D * strength;
                break;
            case LARGE_TURBINE:
                if ("vicio".equals(constellation) || "discidia".equals(constellation)
                        || "ulteria".equals(constellation) && astral.isAdvancedHatch()) {
                    generation += 0.30D * strength;
                }
                if ("the_chariot".equals(tarot) || "the_strength".equals(tarot)
                        || "the_world".equals(tarot)) generation += 0.10D * strength;
                break;
            case MEGA_TURBINE:
                if ("vicio".equals(constellation) || "discidia".equals(constellation)
                        || "ulteria".equals(constellation) && astral.isAdvancedHatch()
                        || "vorux".equals(constellation) && astral.isAdvancedHatch()) {
                    generation += 0.30D * strength;
                }
                if ("the_chariot".equals(tarot) || "the_strength".equals(tarot)
                        || "the_tower".equals(tarot) || "the_world".equals(tarot)) {
                    generation += 0.10D * strength;
                }
                break;
            default:
                break;
        }
        return new MagicEnergyAmplification(astral, tarot, strength, capacity, transfer, generation);
    }

    private static IAstralHatch firstAstral(List<IAstralHatch> hatches) {
        for (IAstralHatch hatch : hatches == null ? Collections.<IAstralHatch>emptyList() : hatches) {
            if (hatch != null && hatch.hasConstellationDataWafer()) return hatch;
        }
        return null;
    }

    private static String firstTarot(List<ITarotHatch> hatches) {
        for (ITarotHatch hatch : hatches == null ? Collections.<ITarotHatch>emptyList() : hatches) {
            if (hatch != null && hatch.getActiveTarot() != null && !hatch.getActiveTarot().isEmpty()) {
                return hatch.getActiveTarot();
            }
        }
        return "";
    }

    private static double cap(double value) {
        return Math.max(0.0D, Math.min(MAX_BONUS, value));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    public AstralAmplifierSnapshot getAstral() { return astral; }
    public String getTarot() { return tarot; }
    public double getStrength() { return strength; }
    public double getCapacityBonus() { return capacityBonus; }
    public double getTransferBonus() { return transferBonus; }
    public double getGenerationBonus() { return generationBonus; }
    public boolean isActive() { return capacityBonus > 0.0D || transferBonus > 0.0D || generationBonus > 0.0D; }
}
