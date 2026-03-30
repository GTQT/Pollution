package meowmel.pollution.loaders.recipes;

import com.google.common.collect.ImmutableList;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.stack.MaterialStack;

import static gregtech.api.GTValues.V;
import static gregtech.api.GTValues.VA;
import static meowmel.pollution.api.recipes.PORecipeMaps.MAGIC_TURBINE_FUELS;
import static meowmel.pollution.api.unification.PollutionMaterials.*;

public class CompoundAspectRecipes {
    public static void init() {
        aspect();
    }

    private static void aspect() {
        registerGenerator(InfusedAir);
        registerGenerator(InfusedFire);
        registerGenerator(InfusedWater);
        registerGenerator(InfusedEarth);
        registerGenerator(InfusedEntropy);
        registerGenerator(InfusedOrder);

        //这里注册所有要用到的复合要素的配方喵
        //用不到的就不负责喵
        //群友说要用搅拌机 我就用搅拌机喵
        // 晶体
        registerAspect(InfusedAir, InfusedEarth, InfusedCrystal);
        // 生命
        registerAspect(InfusedEarth, InfusedWater, InfusedLife);
        // 死亡
        registerAspect(InfusedWater, InfusedEntropy, InfusedDeath);
        // 灵魂
        registerAspect(InfusedLife, InfusedDeath, InfusedSoul);
        // 武器
        registerAspect(InfusedSoul, InfusedEntropy, InfusedWeapon);
        // 金属
        registerAspect(InfusedEarth, InfusedOrder, InfusedMetal);
        // 能量
        registerAspect(InfusedOrder, InfusedFire, InfusedEnergy);
        // 工具
        registerAspect(InfusedMetal, InfusedEnergy, InfusedInstrument);
        // 交换
        registerAspect(InfusedOrder, InfusedEntropy, InfusedExchange);
        // 魔法
        registerAspect(InfusedAir, InfusedEnergy, InfusedMagic);
        // 炼金
        registerAspect(InfusedMagic, InfusedWater, InfusedAlchemy);
        // 光明
        registerAspect(InfusedFire, InfusedAir, InfusedLight);
        // 创造
        registerAspect(InfusedInstrument, InfusedExchange, InfusedCraft);
        // 虚空
        registerAspect(InfusedEntropy, InfusedAir, InfusedVoid);
        // 移动
        registerAspect(InfusedOrder, InfusedAir, InfusedMotion);
        // 腐化
        registerAspect(InfusedEntropy, InfusedMagic, InfusedTaint);
        // 黑暗
        registerAspect(InfusedEntropy, InfusedLight, InfusedDark);
        // 异域
        registerAspect(InfusedVoid, InfusedDark, InfusedAlien);
        // 飞行
        registerAspect(InfusedAir, InfusedMotion, InfusedFly);
        // 植物
        registerAspect(InfusedEarth, InfusedLife, InfusedPlant);
        // 机械
        registerAspect(InfusedMotion, InfusedInstrument, InfusedMechanics);
        // 陷阱
        registerAspect(InfusedEntropy, InfusedMotion, InfusedTrap);
        // 亡灵
        registerAspect(InfusedEarth, InfusedLife, InfusedPlant);
        // 思维
        registerAspect(InfusedFire, InfusedSoul, InfusedThought);
        // 感知
        registerAspect(InfusedAir, InfusedSoul, InfusedSense);
        // 野兽
        registerAspect(InfusedMotion, InfusedLife, InfusedAnimal);
        // 人类
        registerAspect(InfusedLife, InfusedSoul, InfusedHuman);
        // 贪婪
        registerAspect(InfusedSoul, InfusedVoid, InfusedGreed);
        // 装备
        registerAspect(InfusedSoul, InfusedEarth, InfusedArmor);
        // 寒冷
        registerAspect(InfusedFire, InfusedEntropy, InfusedCold);
        // 亡灵（再次）
        registerAspect(InfusedMotion, InfusedDeath, InfusedUndead);
        // 灵气
        registerAspect(InfusedMagic, InfusedAir, InfusedAura);
        //空间
        registerAspect(InfusedVoid, InfusedEntropy, InfusedSpatio);
        //时间
        registerAspect(InfusedSpatio, InfusedExchange, InfusedTempus);
        //艺术
        registerAspect(InfusedSense, InfusedExchange, InfusedTinctura);
    }

    public static void registerAspect(Material input1, Material input2, Material output) {
        int amount = countAllNestedComponents(output);

        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .fluidInputs(input1.getFluid(1000))
                .fluidInputs(input2.getFluid(1000))
                .fluidOutputs(output.getFluid(2000))
                .duration(100*amount)
                .EUt(VA[amount/4])
                .buildAndRegister();

        registerGenerator(output, 80, 40*amount,amount/3);
    }
    public static int countAllNestedComponents(Material material) {
        return countNestedComponents(material.getMaterialComponents());
    }

    private static int countNestedComponents(ImmutableList<MaterialStack> components) {
        int count = 0;
        for (MaterialStack stack : components) {
            count += 1;
            int nestedCount = countNestedComponents(stack.material.getMaterialComponents());
            count += nestedCount;
        }
        return count;
    }
    public static void registerGenerator(Material input) {
        registerGenerator(input, 80, 80, 1);
    }

    public static void registerGenerator(Material input, int amount, int duration, int tier) {
        MAGIC_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(input.getFluid(amount))
                .duration(duration)
                .EUt(V[tier])
                .buildAndRegister();
    }
}