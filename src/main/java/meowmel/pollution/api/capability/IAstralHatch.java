package meowmel.pollution.api.capability;

import meowmel.pollution.api.recipes.properties.AstralCondition;

/** A calibrated Astral Sorcery lens that can validate live sky conditions. */
public interface IAstralHatch {

    int getTier();

    String getFocusedConstellation();

    boolean matches(AstralCondition condition);

    boolean isSkyVisible();

    boolean isNight();

    boolean isFocusedConstellationActive();

    float getFocusedDistribution();

    String getMoonPhase();

    String getCelestialEvent();
}
