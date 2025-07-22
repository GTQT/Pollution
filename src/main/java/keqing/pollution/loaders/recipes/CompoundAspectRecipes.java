package keqing.pollution.loaders.recipes;

import com.google.common.collect.ImmutableList;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.stack.MaterialStack;

import static gregtech.api.GTValues.V;
import static gregtech.api.GTValues.VA;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_TURBINE_FUELS;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class CompoundAspectRecipes {
    public static void init() {
        aspect();
    }

    private static void aspect() {
        registerGenerator(infused_air);
        registerGenerator(infused_fire);
        registerGenerator(infused_water);
        registerGenerator(infused_earth);
        registerGenerator(infused_entropy);
        registerGenerator(infused_order);

        //这里注册所有要用到的复合要素的配方喵
        //用不到的就不负责喵
        //群友说要用搅拌机 我就用搅拌机喵
        // 晶体
        registerAspect(infused_air, infused_earth, infused_crystal);
        // 生命
        registerAspect(infused_earth, infused_water, infused_life);
        // 死亡
        registerAspect(infused_water, infused_entropy, infused_death);
        // 灵魂
        registerAspect(infused_life, infused_death, infused_soul);
        // 武器
        registerAspect(infused_soul, infused_entropy, infused_weapon);
        // 金属
        registerAspect(infused_earth, infused_order, infused_metal);
        // 能量
        registerAspect(infused_order, infused_fire, infused_energy);
        // 工具
        registerAspect(infused_metal, infused_energy, infused_instrument);
        // 交换
        registerAspect(infused_order, infused_entropy, infused_exchange);
        // 魔法
        registerAspect(infused_air, infused_energy, infused_magic);
        // 炼金
        registerAspect(infused_magic, infused_water, infused_alchemy);
        // 光明
        registerAspect(infused_fire, infused_air, infused_light);
        // 创造
        registerAspect(infused_instrument, infused_exchange, infused_craft);
        // 虚空
        registerAspect(infused_entropy, infused_air, infused_void);
        // 移动
        registerAspect(infused_order, infused_air, infused_motion);
        // 腐化
        registerAspect(infused_entropy, infused_magic, infused_taint);
        // 黑暗
        registerAspect(infused_entropy, infused_light, infused_dark);
        // 异域
        registerAspect(infused_void, infused_dark, infused_alien);
        // 飞行
        registerAspect(infused_air, infused_motion, infused_fly);
        // 植物
        registerAspect(infused_earth, infused_life, infused_plant);
        // 机械
        registerAspect(infused_motion, infused_instrument, infused_mechanics);
        // 陷阱
        registerAspect(infused_entropy, infused_motion, infused_trap);
        // 亡灵
        registerAspect(infused_earth, infused_life, infused_plant);
        // 思维
        registerAspect(infused_fire, infused_soul, infused_thought);
        // 感知
        registerAspect(infused_air, infused_soul, infused_sense);
        // 野兽
        registerAspect(infused_motion, infused_life, infused_animal);
        // 人类
        registerAspect(infused_life, infused_soul, infused_human);
        // 贪婪
        registerAspect(infused_soul, infused_void, infused_greed);
        // 装备
        registerAspect(infused_soul, infused_earth, infused_armor);
        // 寒冷
        registerAspect(infused_fire, infused_entropy, infused_cold);
        // 亡灵（再次）
        registerAspect(infused_motion, infused_death, infused_undead);
        // 灵气
        registerAspect(infused_magic, infused_air, infused_aura);
        //空间
        registerAspect(infused_void, infused_entropy, infused_spatio);
        //时间
        registerAspect(infused_spatio, infused_exchange, infused_tempus);
        //艺术
        registerAspect(infused_sense, infused_exchange, infused_tinctura);
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