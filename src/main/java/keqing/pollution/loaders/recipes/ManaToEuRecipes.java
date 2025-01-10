package keqing.pollution.loaders.recipes;

import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.POConfig;

import static keqing.pollution.api.recipes.PORecipeMaps.MANA_TO_EU;

public class ManaToEuRecipes {
    public static void init() {
        mana_to_eu();
    }
        //有点逆天，貌似这玩意发电的逻辑是每个并行把配方超频到1t到不能再超频，所以duration事实上决定了单并行的最大发电功率
    private static void mana_to_eu() {
        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicRub.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicRub/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicGas.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicGas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicFas.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicFas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicDas.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicDas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.MagicAas.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicAas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.Magic.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbMagicKq/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.Richmagic.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbRichMagicKq/8192))
                .EUt(8192)
                .buildAndRegister();
// TODO: 2024/11/19 降低基础数值，加上黑白漫宿等各种材料催化的高数值配方，到底要烧那种魔力？貌似每种魔力都有点廉价了
    }
}
