package meowmel.pollution.loaders.recipes;

import gregtech.api.recipes.ingredients.GTRecipeItemInput;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.item.ItemStack;

/**
 * Two non-executable JEI handbook recipes. Their input slots are deliberately
 * non-consumable, so the handbook is discoverable from the wafer and every
 * Major Arcana without pretending to be a production recipe.
 */
public final class MagicGuideRecipes {

    private MagicGuideRecipes() {
    }

    public static void init() {
        registerConstellationWaferGuide();
        registerTarotGuide();
    }

    private static void registerConstellationWaferGuide() {
        MagicRecipeProperties.guidePage(
                PORecipeMaps.CONSTELLATION_WAFER_GUIDE_RECIPES.recipeBuilder()
                        .notConsumable(PollutionMetaItems.CONSTELLATION_DATA_WAFER)
                        .duration(1)
                        .EUt(1),
                "【星座数据晶圆／星辉透镜仓】",
                "入口：晶圆必须保存星座NBT，放入星辉透镜仓；运行时不消耗。",
                "强度S：MV仓10%；LuV及以上30%；露天且对应星座活跃时额外+10%。",
                "星星塔罗：S×1.25，最终S不超过50%；无晶圆则没有任何星辉增幅。",
                "仅工序标签匹配时生效；总上限见塔罗说明页，所有效果均为正向。",
                "生息 Aevitas：生物/植物/细胞，产出+S、概率+S、时间缩短0.25S。",
                "解离 Evorsio：粉碎/分解/分离，时间缩短S、产出与概率各+0.5S。",
                "遁甲 Armara：稳定/催化/注魔，EU-0.5S、催化保护+S、保留进度。",
                "非攻 Discidia：高负载，时间缩短1.25S；S≥20%/35%时并行+1/+2。",
                "虚御 Vicio：成型/机械/运输，时间缩短S；S≥25%时并行+1。",
                "晶金 Mineralis：矿物富集，产出+S、概率+0.5S。",
                "天炉 Fornax：热加工，炉温+3000S K、时间缩短0.5S、EU-0.25S。",
                "时钟 Horologium：精密/定时，时间缩短S。",
                "圣芒 Lucerna：净化/无菌/光化，产出+0.5S、概率+S、时间缩短0.5S。",
                "南极 Octans：流体/蒸馏，时间缩短0.75S；S≥20%时并行+1。",
                "牧夫 Bootes：动物/组织/细胞，产出+S、魔法介质消耗-0.5S。",
                "唤生 Pelotrio：概率产物/召唤，概率额外重判+S。",
                "寒冰 Gelu（LuV+）：冷却/固化，时间缩短S、EU-0.5S。",
                "避役 Ulteria（LuV+）：耐久/催化，催化保护+S、保留进度。",
                "振变 Alcara（LuV+）：多魔法/共振，介质-0.75S、时间-0.25S、能效+0.5S。",
                "贪狼 Vorux（LuV+）：极限加工，时间缩短1.5S；S≥15%/30%时并行+1/+2。"
        ).buildAndRegister();
    }

    private static void registerTarotGuide() {
        MagicRecipeProperties.guidePage(
                PORecipeMaps.TAROT_GUIDE_RECIPES.recipeBuilder()
                        .notConsumable(new GTRecipeItemInput(allTarotCards()))
                        .duration(1)
                        .EUt(1),
                "【塔罗仓／大阿尔卡那增幅】",
                "入口槽轮播全部22张大阿尔卡那；放入塔罗仓后不消耗，每机仅读取一张。",
                "塔罗只提供正向效果；必须匹配配方工序标签，未匹配时不产生增幅。",
                "授权牌：愚者=实验、魔术师=魔法转化、女祭司=隐藏仪式、死神/审判=回收、世界=三系。",
                "总上限：速度70%、EU/魔力50%、产出50%、催化保护70%、额外并行+3。",
                "愚者：实验配方概率额外重判+10%。",
                "魔术师：魔法转化/多魔法配方，介质消耗-10%。",
                "女祭司：隐藏仪式配方，介质消耗-10%。",
                "皇后：生物/植物/细胞配方，产出+25%。",
                "皇帝：结构控制配方，额外并行+1、催化保护+10%。",
                "教皇：注魔配方，介质消耗-15%。",
                "恋人：双材料配方，产出+10%。",
                "战车：连续生产层数×5%时间缩短，最多计5层（25%）。",
                "力量：任意配方时间缩短10%，额外并行+1。",
                "隐者：仅单并行时，EU-20%、催化保护+25%。",
                "命运之轮：概率产物配方的概率额外重判设为100%。",
                "正义：概率产物配方的概率额外重判+50%。",
                "倒吊人：基础时长≥400tick，EU-25%、介质消耗-20%。",
                "死神：回收配方产出+25%。",
                "节制：多魔法配方介质消耗-10%。",
                "恶魔：危险工序配方产出+20%。",
                "高塔：毁灭仪式配方时间缩短10%，额外并行+2。",
                "星星：星辉晶圆最终强度×1.25，晶圆强度上限仍为50%。",
                "月亮：夜间且夜间炼金工序，产出+15%。",
                "太阳：白天且日照/净化/无菌/光化工序，时间缩短20%、产出+10%。",
                "审判：回收配方产出+25%。",
                "世界：三系或多魔法配方，时间缩短10%、介质-10%、并行+1。"
        ).buildAndRegister();
    }

    private static ItemStack[] allTarotCards() {
        return new ItemStack[]{
                PollutionMetaItems.TAROT_THE_FOOL.getStackForm(),
                PollutionMetaItems.TAROT_THE_MAGICIAN.getStackForm(),
                PollutionMetaItems.TAROT_THE_HIGH_PRIESTESS.getStackForm(),
                PollutionMetaItems.TAROT_THE_EMPRESS.getStackForm(),
                PollutionMetaItems.TAROT_THE_EMPEROR.getStackForm(),
                PollutionMetaItems.TAROT_THE_HIGHOPHANT.getStackForm(),
                PollutionMetaItems.TAROT_THE_LOVERS.getStackForm(),
                PollutionMetaItems.TAROT_THE_CHARIOT.getStackForm(),
                PollutionMetaItems.TAROT_THE_STRENGTH.getStackForm(),
                PollutionMetaItems.TAROT_THE_HERMIT.getStackForm(),
                PollutionMetaItems.TAROT_THE_WHEEL_OF_FORTUNE.getStackForm(),
                PollutionMetaItems.TAROT_JUSTICE.getStackForm(),
                PollutionMetaItems.TAROT_THE_HANGED_MAN.getStackForm(),
                PollutionMetaItems.TAROT_DEATH.getStackForm(),
                PollutionMetaItems.TAROT_TEMPERANCE.getStackForm(),
                PollutionMetaItems.TAROT_THE_DEVIL.getStackForm(),
                PollutionMetaItems.TAROT_THE_TOWER.getStackForm(),
                PollutionMetaItems.TAROT_THE_STAR.getStackForm(),
                PollutionMetaItems.TAROT_THE_MOON.getStackForm(),
                PollutionMetaItems.TAROT_THE_SUN.getStackForm(),
                PollutionMetaItems.TAROT_JUDGEMENT.getStackForm(),
                PollutionMetaItems.TAROT_THE_WORLD.getStackForm()
        };
    }
}
