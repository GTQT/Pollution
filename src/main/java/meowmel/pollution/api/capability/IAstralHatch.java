package meowmel.pollution.api.capability;

import meowmel.pollution.api.recipes.properties.AstralCondition;

/** A calibrated Astral Sorcery lens that can validate live sky conditions. */
public interface IAstralHatch {

    String getFocusedConstellation();

    boolean matches(AstralCondition condition);
}
