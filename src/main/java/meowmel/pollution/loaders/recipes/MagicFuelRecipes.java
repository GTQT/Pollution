package meowmel.pollution.loaders.recipes;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import meowmel.gtqtcore.api.unification.material.GTQTMaterials;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static meowmel.gtqtcore.api.recipes.GTQTRecipeMaps.ROCKET_ENGINE_RECIPES;
import static meowmel.gtqtcore.api.unification.material.GTQTMaterials.*;
import static meowmel.pollution.api.unification.PollutionMaterials.*;

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
                .fluidInputs(BlazingPyrotheum.getFluid(1000))
                .fluidInputs(hydrazine_sulfate.getFluid(1000))
                .fluidInputs(NitricAcid.getFluid(10000))
                .fluidInputs(PollutionMaterials.InfusedEnergy.getFluid(1152))
                .input(OrePrefix.dust,Aluminium,8)
                .notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
                .fluidOutputs(PollutionMaterials.InfernalBlazePropellant.getFluid(16000))
                .duration(800)
                .EUt(VA[HV])
                .buildAndRegister();

        ROCKET_ENGINE_RECIPES.recipeBuilder()
                .fluidInputs(InfernalBlazePropellant.getFluid(9))
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
                .fluidOutputs(SodiumLeadAlloy.getFluid(1000))
                .fluidOutputs(Chlorine.getFluid(2000))
                .duration(120)
                .EUt(VA[HV])
                .buildAndRegister();

        //NaPb+4*C₂H₅Cl=Pb(C₂H₅)₄+NaCl
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(SodiumLeadAlloy.getFluid(1000))
                .fluidInputs(Ethylene.getFluid(4000))
                .fluidOutputs(TetraethylLead.getFluid(1000))
                .output(OrePrefix.dust,Salt)
                .duration(120)
                .EUt(VA[HV])
                .buildAndRegister();

        PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
                .fluidInputs(TetraethylLead.getFluid(1000))
                .fluidInputs(ChlorineTrifluoride.getFluid(1000))
                .fluidInputs(Dimethylhydrazine.getFluid(10000))
                .input(Items.DRAGON_BREATH, 4)
                .fluidInputs(PollutionMaterials.InfusedEnergy.getFluid(1152))
                .notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
                .fluidOutputs(PollutionMaterials.DragonPulseFuel.getFluid(16000))
                .duration(800)
                .EUt(VA[IV])
                .buildAndRegister();

        ROCKET_ENGINE_RECIPES.recipeBuilder()
                .fluidInputs(DragonPulseFuel.getFluid(6))
                .EUt(2048)
                .duration(8 * SECOND)
                .buildAndRegister();
    }

    public static void CombustionGenerator() {
        //魔力抗爆焦化硝基苯
        PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES.recipeBuilder()
                .fluidInputs(GTQTMaterials.MethylFormate.getFluid(1000))
                .fluidInputs(Materials.Ethanol.getFluid(1000))
                .fluidInputs(Materials.Nitrobenzene.getFluid(10000))
                .fluidInputs(PollutionMaterials.InfusedEnergy.getFluid(1152))
                .notConsumable(new ItemStack(PollutionMetaItems.COKINGCORE.getMetaItem(), 1, 7))
                .fluidOutputs(PollutionMaterials.MagicNitrobenzene.getFluid(16000))
                .duration(200)
                .EUt(480)
                .buildAndRegister();

        RecipeMaps.COMBUSTION_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(MagicNitrobenzene.getFluid(1))
                .duration(90)
                .EUt(512)
                .buildAndRegister();
    }

}
