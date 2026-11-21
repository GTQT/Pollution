package meowmel.pollution.api.recipes.condition;

import gregtech.api.recipes.logic.IRecipeCondition;
import gregtech.api.recipes.logic.RecipeExecutionContext;
import meowmel.pollution.api.capability.ICleanVis;
import org.jetbrains.annotations.NotNull;

/**
 * Per-tick aura/flux cleanliness gate backed by {@link ICleanVis}.
 * Pauses recipe progress without loss while the surrounding chunk is not clean.
 */
public class CleanVisCondition implements IRecipeCondition {

    public static final String CONDITION_ID = "pollution:clean_vis";

    private final ICleanVis cleanVis;

    public CleanVisCondition(@NotNull ICleanVis cleanVis) {
        this.cleanVis = cleanVis;
    }

    @Override
    @NotNull
    public String getConditionId() {
        return CONDITION_ID;
    }

    @Override
    public boolean testTick(@NotNull RecipeExecutionContext context) {
        return cleanVis.isCleanVis();
    }

    @Override
    @NotNull
    public TickFailureAction onTickFailed(@NotNull RecipeExecutionContext context, boolean energyShortage) {
        return energyShortage ? TickFailureAction.DECREASE_PROGRESS : TickFailureAction.PAUSE;
    }
}
