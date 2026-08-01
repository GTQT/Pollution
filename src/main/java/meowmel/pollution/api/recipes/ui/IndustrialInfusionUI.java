package meowmel.pollution.api.recipes.ui;

import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.RecipeProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.ui.RecipeMapUI;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

/**
 * Compact JEI layout for industrial infusion recipes.
 *
 * <p>The default layout places the eight aspect-fluid slots below a 5x5 item
 * grid. That area is also used by JEI for duration, EU/t and recipe-property
 * text, so both layers overlap. This layout keeps all inputs in the upper
 * 92 pixels and reserves the lower half for recipe information.</p>
 */
public final class IndustrialInfusionUI<R extends RecipeMap<?>> extends RecipeMapUI<R> {

    public IndustrialInfusionUI(@NotNull R recipeMap) {
        super(recipeMap, false, false, false, false);
        setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressWidget.MoveType.HORIZONTAL);
    }

    @Override
    public ModularUI.Builder createJeiUITemplate(IItemHandlerModifiable importItems,
                                                  IItemHandlerModifiable exportItems,
                                                  FluidTankList importFluids,
                                                  FluidTankList exportFluids,
                                                  int yOffset) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 176)
                .widget(new RecipeProgressWidget(200, 134, 29, 20, 20,
                        progressBarTexture(), progressBarMoveType(), recipeMap()));
        addInventorySlotGroup(builder, importItems, importFluids, false, yOffset);
        addInventorySlotGroup(builder, exportItems, exportFluids, true, yOffset);
        return builder;
    }

    @Override
    protected void addInventorySlotGroup(ModularUI.Builder builder,
                                         IItemHandlerModifiable itemHandler,
                                         FluidTankList fluidHandler,
                                         boolean isOutputs,
                                         int yOffset) {
        if (isOutputs) {
            addSlot(builder, 157, 30, 0, itemHandler, fluidHandler, false, true);
            return;
        }

        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 5; column++) {
                int index = row * 5 + column;
                addSlot(builder, 2 + column * 18, 1 + row * 18, index,
                        itemHandler, fluidHandler, false, false);
            }
        }

        for (int index = 0; index < 8; index++) {
            int column = index % 2;
            int row = index / 2;
            addSlot(builder, 94 + column * 18, 1 + row * 18, index,
                    itemHandler, fluidHandler, true, false);
        }
    }
}
