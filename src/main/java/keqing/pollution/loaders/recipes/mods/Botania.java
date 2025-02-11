package keqing.pollution.loaders.recipes.mods;

import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.util.GTUtility;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.recipe.RecipeRuneAltar;

import java.util.Iterator;

import static gregtech.api.GTValues.VA;
import static keqing.pollution.api.recipes.PORecipeMaps.*;
import static vazkii.botania.api.BotaniaAPI.*;

public class Botania {
    public static void init() {
        for (RecipePetals recipe : petalRecipes) {
            ItemStack output = recipe.getOutput();
            RecipeBuilder<?> builder;
            builder = MANA_PETAL_RECIPES.recipeBuilder()
                    .TotalMana(100);
            for (Object input : recipe.getInputs()) {
                if (input instanceof ItemStack s) {
                    builder.inputs(s);
                }
                if (input instanceof String s) {
                    builder.input(s);
                }
            }
            builder.outputs(output);
            builder.duration(200);
            builder.EUt(VA[3]);
            builder.buildAndRegister();

        }
        for (RecipeRuneAltar recipe : runeAltarRecipes) {
            ItemStack output = recipe.getOutput();
            int mana = recipe.getManaUsage();

            RecipeBuilder<?> builder;
            builder = MANA_RUNE_ALTAR_RECIPES.recipeBuilder()
                    .TotalMana(mana);

            for (Object input : recipe.getInputs()) {
                if (input instanceof ItemStack s) {
                    builder.inputs(s);
                }
                if (input instanceof String s) {
                    builder.input(s);
                }
            }
            builder.outputs(output);
            builder.duration(200 * GTUtility.getTierByVoltage(mana));
            builder.EUt(VA[GTUtility.getTierByVoltage(mana) + 3]);
            builder.buildAndRegister();
        }
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
            builder.duration(200 * GTUtility.getTierByVoltage(mana));
            builder.EUt(VA[GTUtility.getTierByVoltage(mana) + 3]);
            builder.buildAndRegister();
        }
    }
}
