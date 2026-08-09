package meowmel.pollution.api.recipes.ui;

import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.ui.RecipeMapUI;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

/**
 * Static JEI guide layout. The one non-consumable item slot is an entry point
 * only; the enlarged lower area is reserved for the guide recipe property.
 */
public final class MagicGuideUI<R extends RecipeMap<?>> extends RecipeMapUI<R> {

    public MagicGuideUI(@NotNull R recipeMap) {
        super(recipeMap, false, false, false, false);
    }

    @Override
    public ModularUI.Builder createJeiUITemplate(IItemHandlerModifiable importItems,
                                                  IItemHandlerModifiable exportItems,
                                                  FluidTankList importFluids,
                                                  FluidTankList exportFluids,
                                                  int yOffset) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 112);
        addSlot(builder, 79, 18, 0, importItems, importFluids, false, false);
        return builder;
    }

    @Override
    public int getPropertyHeightShift() {
        int maximumLineCount = 0;
        for (Recipe recipe : recipeMap().getRecipeList()) {
            maximumLineCount = Math.max(maximumLineCount, recipe.propertyStorage().size());
        }
        return maximumLineCount * 10;
    }
}
