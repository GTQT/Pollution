package meowmel.pollution.loaders.recipes;

import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import static meowmel.pollution.api.recipes.PORecipeMaps.DAN_DE_LIFE_ON;
import static vazkii.botania.common.item.ModItems.manaResource;

public class DandelifeonRecipe {
    public static void init() {
        dandelifeon();
    }
    private static void dandelifeon() {
        DAN_DE_LIFE_ON.recipeBuilder()
                .inputs(new ItemStack(manaResource,1,8))
                .notConsumable(ItemBlockSpecialFlower.ofType("dandelifeon"))
                .duration(20)
                .EUt(999)
                .buildAndRegister();

    }
}
