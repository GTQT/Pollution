package meowmel.pollution.api.recipes.ui;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.properties.RecipeProperty;
import gregtech.api.recipes.ui.RecipeMapUI;
import org.jetbrains.annotations.NotNull;

/**
 * Makes room for GT's bottom-aligned JEI recipe properties. Magic recipes
 * routinely carry media, sky, process-tag and static hint properties; the
 * stock layout only reserves this space for six-or-more inventory slots.
 */
public final class MagicPropertyRecipeUI<R extends RecipeMap<?>> extends RecipeMapUI<R> {

    public MagicPropertyRecipeUI(@NotNull R recipeMap) {
        super(recipeMap, false, false, false, false);
    }

    @Override
    public int getPropertyHeightShift() {
        int maximumLines = 0;
        for (Recipe recipe : recipeMap().getRecipeList()) {
            int propertyCount = 0;
            for (RecipeProperty<?> property : recipe.propertyStorage().values()) {
                if (!property.isHidden()) propertyCount++;
            }
            if (propertyCount > 0) {
                // Total EU, EU/t and duration are also rendered by GTRecipeWrapper.
                maximumLines = Math.max(maximumLines, propertyCount + 3);
            }
        }
        return maximumLines * 10;
    }
}
