package keqing.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps.ROCKET_RECIPES;
import static keqing.gtqtcore.api.unification.GTQTMaterials.*;
import static keqing.gtqtcore.api.unification.GTQTMaterials.OverheatedGas;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class MagicFuelRecipes {
    public static void init() {
        CombustionGenerator();
        RocketEngine();
    }

    private static void RocketEngine() {
        //焚天烈焰推进剂 烈焰之炽焰+ 肼硫酸盐 + 硝酸 + 铝粉
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(Hydrazine.getFluid(2000))         // N₂H₄（肼）
                .fluidInputs(SulfuricAcid.getFluid(1000))      // H₂SO₄（硫酸）
                .fluidOutputs(hydrazine_sulfate.getFluid(3000))
                .duration(180)
                .EUt(VA[EV])
                .buildAndRegister();

        PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
                .fluidInputs(Pyrotheum.getFluid(1000))
                .fluidInputs(hydrazine_sulfate.getFluid(1000))
                .fluidInputs(NitricAcid.getFluid(10000))
                .fluidInputs(PollutionMaterials.infused_energy.getFluid(1152))
                .input(OrePrefix.dust,Aluminium,8)
                .notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
                .fluidOutputs(PollutionMaterials.infernal_blaze_propellant.getFluid(16000))
                .duration(800)
                .EUt(VA[HV])
                .buildAndRegister();

        ROCKET_RECIPES.recipeBuilder()
                .fluidInputs(infernal_blaze_propellant.getFluid(9))
                .fluidOutputs(OverheatedGas.getFluid(4 * 1000))
                .EUt(2048)
                .duration(4 * SECOND)
                .buildAndRegister();

        //龙脉星轨燃剂 四乙基铅+偏二甲肼火箭燃料+三氟化氯+龙息
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(Chlorine.getFluid(1000))
                .fluidInputs(Fluorine.getFluid(3000))
                .fluidOutputs(ChlorineTrifluoride.getFluid(4000))
                .duration(60)
                .EUt(VA[EV])
                .buildAndRegister();

        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .input(OrePrefix.dust,Sodium)
                .input(OrePrefix.dust,LeadChloride,3)
                .fluidOutputs(SodiumLeadAlloy, 1000)
                .fluidOutputs(Chlorine.getFluid(2000))
                .duration(120)
                .EUt(VA[HV])
                .buildAndRegister();

        //NaPb+4*C₂H₅Cl=Pb(C₂H₅)₄+NaCl
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(SodiumLeadAlloy.getFluid(1000))
                .fluidInputs(Dichloroethane.getFluid(4000))
                .fluidOutputs(TetraethylLead, 1000)
                .output(OrePrefix.dust,Salt)
                .duration(120)
                .EUt(VA[HV])
                .buildAndRegister();

        PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
                .fluidInputs(TetraethylLead.getFluid(1000))
                .fluidInputs(ChlorineTrifluoride.getFluid(1000))
                .fluidInputs(Dimethylhydrazine.getFluid(10000))
                .fluidInputs(DragonBreath.getFluid(4000))
                .fluidInputs(PollutionMaterials.infused_energy.getFluid(1152))
                .notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
                .fluidOutputs(PollutionMaterials.dragon_pulse_fuel.getFluid(16000))
                .duration(800)
                .EUt(VA[IV])
                .buildAndRegister();

        ROCKET_RECIPES.recipeBuilder()
                .fluidInputs(dragon_pulse_fuel.getFluid(6))
                .fluidOutputs(OverheatedGas.getFluid(6 * 1000))
                .EUt(2048)
                .duration(8 * SECOND)
                .buildAndRegister();
    }

    public static void CombustionGenerator() {
        //魔力抗爆焦化硝基苯
        PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
                .fluidInputs(GTQTMaterials.Methylfuran.getFluid(1000))
                .fluidInputs(Materials.Ethanol.getFluid(1000))
                .fluidInputs(Materials.Nitrobenzene.getFluid(10000))
                .fluidInputs(PollutionMaterials.infused_energy.getFluid(1152))
                .notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
                .fluidOutputs(PollutionMaterials.magic_nitrobenzene.getFluid(16000))
                .duration(200)
                .EUt(480)
                .buildAndRegister();

        RecipeMaps.COMBUSTION_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(magic_nitrobenzene.getFluid(1))
                .fluidOutputs(SuperCriticalGas.getFluid(4500))
                .duration(90)
                .EUt(512)
                .buildAndRegister();
    }
}
