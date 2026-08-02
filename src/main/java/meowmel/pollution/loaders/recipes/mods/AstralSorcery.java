package meowmel.pollution.loaders.recipes.mods;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.ingredients.GTRecipeItemInput;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import meowmel.pollution.Pollution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import static meowmel.pollution.api.recipes.PORecipeMaps.INDUSTRIAL_STARLIGHT_INFUSER_RECIPES;
import static meowmel.pollution.api.recipes.PORecipeMaps.INDUSTRIAL_LIGHTWELL_RECIPES;

/** Imports Astral Sorcery's finalized Starlight Infuser recipe registry. */
public final class AstralSorcery {

    private static final int LIQUID_STARLIGHT_POSITIONS = 12;
    private static final int INFUSER_LIQUID_DISCOUNT = 6;
    private static final int LIGHTWELL_DURATION = 200;
    private static final int LIGHTWELL_OUTPUT_MULTIPLIER = 5;
    private static boolean initialized;

    private AstralSorcery() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        int[] result = new int[2];
        importRecipes(InfusionRecipeRegistry.recipes, result);
        importRecipes(InfusionRecipeRegistry.mtRecipes, result);
        Pollution.LOGGER.info("Imported {} Astral Sorcery Starlight Infuser recipes for the Industrial Starlight Infuser ({} skipped)",
                result[0], result[1]);

        int lightwellRecipes = 0;
        for (WellLiquefaction.LiquefactionEntry entry : WellLiquefaction.getRegisteredLiquefactions()) {
            if (registerLightwell(entry)) lightwellRecipes++;
        }
        for (WellLiquefaction.LiquefactionEntry entry : WellLiquefaction.mtLiquefactions.values()) {
            if (registerLightwell(entry)) lightwellRecipes++;
        }
        Pollution.LOGGER.info("Imported {} Astral Sorcery Lightwell recipes for the Industrial Lightwell",
                lightwellRecipes);
    }

    private static void importRecipes(List<AbstractInfusionRecipe> recipes, int[] result) {
        for (AbstractInfusionRecipe infusionRecipe : recipes) {
            if (register(infusionRecipe)) result[0]++;
            else result[1]++;
        }
    }

    private static boolean register(AbstractInfusionRecipe infusionRecipe) {
        if (infusionRecipe == null) return false;

        ItemStack output = infusionRecipe.getOutputForMatching();
        if (output.isEmpty()) output = infusionRecipe.getOutputForRender();
        if (output.isEmpty()) return false;

        RecipeBuilder<?> builder = INDUSTRIAL_STARLIGHT_INFUSER_RECIPES.recipeBuilder()
                .EUt(GTValues.VA[GTValues.IV])
                .duration(Math.max(1, infusionRecipe.craftingTickTime()));

        if (!addInput(builder, infusionRecipe.getInput())) return false;

        // A normal infuser independently rolls all twelve liquid-starlight positions.
        // Requiring the expected amount makes the industrial recipe deterministic.
        float expectedLiquidStarlight = LIQUID_STARLIGHT_POSITIONS * 1000.0F
                * infusionRecipe.getLiquidStarlightConsumptionChance();
        int liquidStarlight = expectedLiquidStarlight <= 0.0F ? 0
                : Math.max(1, Math.round(expectedLiquidStarlight / INFUSER_LIQUID_DISCOUNT));
        if (liquidStarlight > 0) {
            builder.fluidInputs(new FluidStack(BlocksAS.fluidLiquidStarlight, liquidStarlight));
        }

        builder.outputs(output.copy()).buildAndRegister();
        return true;
    }

    private static boolean registerLightwell(WellLiquefaction.LiquefactionEntry entry) {
        if (entry == null || entry.catalyst == null || entry.catalyst.isEmpty() || entry.producing == null) {
            return false;
        }

        int outputAmount = Math.max(1, Math.round(entry.productionMultiplier
                * LIGHTWELL_DURATION * LIGHTWELL_OUTPUT_MULTIPLIER));
        INDUSTRIAL_LIGHTWELL_RECIPES.recipeBuilder()
                .notConsumable(entry.catalyst.copy())
                .fluidOutputs(new FluidStack(entry.producing, outputAmount))
                .duration(LIGHTWELL_DURATION)
                .EUt(GTValues.VA[GTValues.IV])
                .buildAndRegister();
        return true;
    }

    private static boolean addInput(RecipeBuilder<?> builder, ItemHandle input) {
        if (input == null) return false;

        FluidStack fluidInput = input.getFluidTypeAndAmount();
        if (fluidInput != null && fluidInput.getFluid() != null && fluidInput.amount > 0) {
            builder.fluidInputs(fluidInput.copy());
            return true;
        }

        String oreName = input.getOreDictName();
        if (oreName != null && !oreName.isEmpty()) {
            builder.input(oreName);
            return true;
        }

        List<ItemStack> applicable = input.getApplicableItems();
        if (applicable == null || applicable.isEmpty()) return false;

        List<ItemStack> alternatives = new ArrayList<>(applicable.size());
        int amount = 1;
        for (ItemStack stack : applicable) {
            if (stack == null || stack.isEmpty()) continue;
            ItemStack copy = stack.copy();
            amount = Math.max(amount, copy.getCount());
            copy.setCount(1);
            alternatives.add(copy);
        }
        if (alternatives.isEmpty()) return false;

        builder.input(new GTRecipeItemInput(alternatives.toArray(new ItemStack[0]), amount));
        return true;
    }
}
