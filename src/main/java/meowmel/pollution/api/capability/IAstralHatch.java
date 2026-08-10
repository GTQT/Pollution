package meowmel.pollution.api.capability;

import meowmel.pollution.api.recipes.properties.AstralCondition;

/** A calibrated Astral Sorcery lens that can validate live sky conditions. */
public interface IAstralHatch {

    int getTier();

    /** A valid constellation-data wafer is required for passive machine amplification. */
    boolean hasConstellationDataWafer();

    /** Quality of the optional cultivated rock-crystal optical insert, 0..100. */
    int getOpticalCrystalQuality();

    /** Extra constellation strength supplied by the optical insert, 0..10 percentage points. */
    double getOpticalCrystalStrengthBonus();

    void setFocusLocked(boolean locked);

    boolean isFocusLocked();

    String getFocusedConstellation();

    boolean matches(AstralCondition condition);

    boolean isSkyVisible();

    boolean isNight();

    boolean isFocusedConstellationActive();

    float getFocusedDistribution();

    String getMoonPhase();

    String getCelestialEvent();
}
