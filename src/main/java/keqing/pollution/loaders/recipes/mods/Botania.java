package keqing.pollution.loaders.recipes.mods;

import gregtech.api.recipes.RecipeBuilder;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.recipe.RecipeManaInfusion;

import static gregtech.api.GTValues.IV;
import static gregtech.api.GTValues.VA;
import static keqing.pollution.api.recipes.PORecipeMaps.MANA_INFUSION_RECIPES;
import static vazkii.botania.api.BotaniaAPI.manaInfusionRecipes;

public class Botania {
    public static void init() {
        for (RecipeManaInfusion recipe : manaInfusionRecipes) {
            ItemStack output = recipe.getOutput();
            int mana = recipe.getManaToConsume();


            RecipeBuilder<?> builder;
            builder = MANA_INFUSION_RECIPES.recipeBuilder()
                    .TotalMana(mana);


            if (recipe.getCatalyst() != null)
                builder.notConsumable(new ItemStack(recipe.getCatalyst().getBlock()));

            if (recipe.getInput() instanceof String recipeInput)
                builder.input(recipeInput);

            else builder.inputs((ItemStack) recipe.getInput());

            builder.outputs(output);
            builder.duration(200);
            builder.EUt(VA[IV]);
            builder.buildAndRegister();
        }
    }
}
