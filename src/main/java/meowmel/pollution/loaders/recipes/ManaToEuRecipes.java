package meowmel.pollution.loaders.recipes;

public class ManaToEuRecipes {
    public static void init() {
        mana_to_eu();
    }
        //有点逆天，貌似这玩意发电的逻辑是每个并行把配方超频到1t到不能再超频，所以duration事实上决定了单并行的最大发电功率
    private static void mana_to_eu() {
        /*
        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.ManaRub.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicRub/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.ManaGas.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicGas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.ManaFas.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicFas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.ManaDas.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicDas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.ManaAas.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbKqMagicAas/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.Mana.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbMagicKq/8192))
                .EUt(8192)
                .buildAndRegister();

        MANA_TO_EU.recipeBuilder()
                .fluidInputs(GTQTMaterials.Richmagic.getFluid(100))
                .duration((int) (100* POConfig.MachineSettingSwitch.EuPerMbRichMagicKq/8192))
                .EUt(8192)
                .buildAndRegister();

         */
    }
}
