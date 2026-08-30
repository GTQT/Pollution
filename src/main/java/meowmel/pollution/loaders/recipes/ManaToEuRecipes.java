package meowmel.pollution.loaders.recipes;

import meowmel.gtqtcore.api.unification.material.GTQTMaterials;
import meowmel.pollution.POConfig;
import meowmel.pollution.api.unification.PollutionMaterials;

import static meowmel.pollution.api.recipes.PORecipeMaps.MANA_TO_EU;

public class ManaToEuRecipes {
    public static void init() {
        mana_to_eu();
    }
        //有点逆天，貌似这玩意发电的逻辑是每个并行把配方超频到1t到不能再超频，所以duration事实上决定了单并行的最大发电功率
    private static void mana_to_eu() {
        MANA_TO_EU.recipeBuilder()
                .fluidInputs(PollutionMaterials.Impuremana.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicRub/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(PollutionMaterials.WhiteMansus.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicGas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(PollutionMaterials.BlackMansus.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicFas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(PollutionMaterials.Starrymansus.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicDas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(PollutionMaterials.RichAura.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicAas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.Mana.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbMagicKq/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(PollutionMaterials.ErichAura.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbRichMagicKq/8192))
                .EUt(8192)
                .buildAndRegister();

    }
}
