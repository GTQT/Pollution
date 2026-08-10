package meowmel.pollution.api.amplification;

import gregtech.api.GTValues;
import meowmel.pollution.api.capability.IAstralHatch;

/** Immutable server-side reading of an astral lens at recipe start. */
public final class AstralAmplifierSnapshot {

    private final String constellation;
    private final int hatchTier;
    private final boolean hasDataWafer;
    private final boolean skyMatched;
    private final boolean night;
    private final int opticalCrystalQuality;
    private final double opticalCrystalStrengthBonus;

    public AstralAmplifierSnapshot(String constellation, int hatchTier, boolean hasDataWafer, boolean skyMatched,
                                   boolean night) {
        this(constellation, hatchTier, hasDataWafer, skyMatched, night, 0, 0.0D);
    }

    public AstralAmplifierSnapshot(String constellation, int hatchTier, boolean hasDataWafer, boolean skyMatched,
                                   boolean night, int opticalCrystalQuality, double opticalCrystalStrengthBonus) {
        this.constellation = constellation == null ? "" : constellation;
        this.hatchTier = hatchTier;
        this.hasDataWafer = hasDataWafer;
        this.skyMatched = skyMatched;
        this.night = night;
        this.opticalCrystalQuality = Math.max(0, Math.min(100, opticalCrystalQuality));
        this.opticalCrystalStrengthBonus = Math.max(0.0D, Math.min(0.20D, opticalCrystalStrengthBonus));
    }

    public static AstralAmplifierSnapshot empty() {
        return new AstralAmplifierSnapshot("", 0, false, false, false);
    }

    public static AstralAmplifierSnapshot from(IAstralHatch hatch) {
        if (hatch == null || !hatch.hasConstellationDataWafer()) return empty();
        boolean matched = hatch.isSkyVisible() && hatch.isFocusedConstellationActive();
        return new AstralAmplifierSnapshot(hatch.getFocusedConstellation(), hatch.getTier(), true, matched,
                hatch.isNight(), hatch.getOpticalCrystalQuality(), hatch.getOpticalCrystalStrengthBonus());
    }

    public String getConstellation() {
        return constellation;
    }

    public boolean hasDataWafer() {
        return hasDataWafer;
    }

    public boolean isSkyMatched() {
        return skyMatched;
    }

    public AstralAmplifierSnapshot withSkyMatched(boolean matched) {
        return new AstralAmplifierSnapshot(constellation, hatchTier, hasDataWafer, matched, night,
                opticalCrystalQuality, opticalCrystalStrengthBonus);
    }

    public boolean isNight() { return night; }

    /** MV hatches give 10%, LuV and above give 20%; a calibrated optical insert adds up to 20 pp. */
    public double getBaseStrength() {
        if (!hasDataWafer) return 0.0D;
        return (hatchTier >= GTValues.LuV ? 0.20D : 0.10D) + opticalCrystalStrengthBonus;
    }

    public int getOpticalCrystalQuality() { return opticalCrystalQuality; }

    public double getOpticalCrystalStrengthBonus() { return opticalCrystalStrengthBonus; }

    public boolean isAdvancedHatch() {
        return hasDataWafer && hatchTier >= GTValues.LuV;
    }

    public double getSkyStrength() {
        return hasDataWafer && skyMatched ? 0.10D : 0.0D;
    }
}
