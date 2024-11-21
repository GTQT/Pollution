package keqing.pollution.loaders.recipes;

import keqing.gtqtcore.api.unification.GTQTMaterials;

import static keqing.pollution.api.recipes.PORecipeMaps.MANA_TO_EU;
import static keqing.pollution.api.unification.PollutionMaterials.mana;

public class ManaToEuRecipes {
    public static void init() {
        mana_to_eu();
    }
        //有点逆天，貌似这玩意发电的逻辑是每个并行把配方超频到1t到不能再超频，所以duration事实上决定了单并行的最大发电功率
    private static void mana_to_eu() {
        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicGas.getFluid(1))
                .duration(1)
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicFas.getFluid(1))
                .duration(2)
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicDas.getFluid(1))
                .duration(4)
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicAas.getFluid(1))
                .duration(8)
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicRub.getFluid(1))
                .duration(1)
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.Magic.getFluid(1))
                .duration(32)
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.Richmagic.getFluid(1))
                .duration(128)
                .EUt(8192)
                .buildAndRegister();
// TODO: 2024/11/19 降低基础数值，加上黑白漫宿等各种材料催化的高数值配方，到底要烧那种魔力？貌似每种魔力都有点廉价了
    }
}
