package keqing.pollution.loaders.recipes;

import keqing.gtqtcore.api.unification.GTQTMaterials;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockFloatingSpecialFlower;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import static gregtech.api.unification.material.Materials.HydrochloricAcid;
import static keqing.pollution.api.recipes.PORecipeMaps.DAN_DE_LIFE_ON;
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
