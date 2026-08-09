package meowmel.pollution.api.amplification;

import java.util.ArrayList;
import java.util.List;

/**
 * Stable recipe-domain tags used by the magic amplification system.
 *
 * <p>The tag mask is deliberately stored as a recipe property instead of using
 * recipe-map identity alone: a chemical reactor can run an ordinary fluid
 * recipe and a blood-culture recipe without both receiving the same bonus.</p>
 */
public enum MagicProcessTag {
    BIOLOGICAL,
    PLANT,
    CELL_CULTURE,
    ANIMAL,
    TISSUE,
    CRUSHING,
    DECOMPOSITION,
    SEPARATION,
    ORE,
    MINERAL_ENRICHMENT,
    FORMING,
    MECHANICAL,
    TRANSPORT,
    HIGH_POWER,
    AGGRESSIVE_PROCESSING,
    STABILITY,
    THERMAL,
    SMELTING,
    FLUID,
    DISTILLATION,
    COOLING,
    SOLIDIFICATION,
    PRECISION,
    TIMED,
    MULTI_MAGIC,
    CATALYTIC,
    INFUSION,
    ENDURANCE,
    RESONANCE,
    EXTREME_PROCESSING,
    CHANCE_OUTPUT,
    SUMMONING,
    PURIFICATION,
    STERILE,
    PHOTOCHEMICAL,
    EXPERIMENTAL,
    MAGIC_CONVERSION,
    HIDDEN_RITUAL,
    STRUCTURAL_CONTROL,
    RECYCLING,
    DESTRUCTIVE_RITUAL,
    NIGHT_ALCHEMY,
    DAYLIGHT,
    DANGEROUS,
    DUAL_MATERIAL,
    THREE_MAGIC_SYSTEMS,
    CONSUMABLE_CATALYST;

    public long bit() {
        return 1L << ordinal();
    }

    public static long maskOf(MagicProcessTag... tags) {
        long mask = 0L;
        if (tags == null) return mask;
        for (MagicProcessTag tag : tags) {
            if (tag != null) mask |= tag.bit();
        }
        return mask;
    }

    public static boolean hasAny(long mask, MagicProcessTag... tags) {
        return (mask & maskOf(tags)) != 0L;
    }

    /** Compact human-readable label used by JEI and the server-side diagnostic command. */
    public static String describeMask(long mask) {
        List<String> names = new ArrayList<>();
        for (MagicProcessTag tag : values()) {
            if ((mask & tag.bit()) != 0L) names.add(tag.getDisplayName());
        }
        return names.isEmpty() ? "未标记" : String.join("、", names);
    }

    public String getDisplayName() {
        switch (this) {
            case BIOLOGICAL: return "生物";
            case PLANT: return "植物";
            case CELL_CULTURE: return "细胞培养";
            case ANIMAL: return "动物组织";
            case TISSUE: return "组织处理";
            case CRUSHING: return "粉碎";
            case DECOMPOSITION: return "分解";
            case SEPARATION: return "分离";
            case ORE: return "矿物";
            case MINERAL_ENRICHMENT: return "矿物富集";
            case FORMING: return "成型";
            case MECHANICAL: return "机械加工";
            case TRANSPORT: return "输运";
            case HIGH_POWER: return "高功率";
            case AGGRESSIVE_PROCESSING: return "强力加工";
            case STABILITY: return "稳定";
            case THERMAL: return "热处理";
            case SMELTING: return "冶炼";
            case FLUID: return "流体";
            case DISTILLATION: return "蒸馏";
            case COOLING: return "冷却";
            case SOLIDIFICATION: return "固化";
            case PRECISION: return "精密";
            case TIMED: return "时序";
            case MULTI_MAGIC: return "多魔法";
            case CATALYTIC: return "催化";
            case INFUSION: return "注魔";
            case ENDURANCE: return "耐久";
            case RESONANCE: return "共振";
            case EXTREME_PROCESSING: return "极限加工";
            case CHANCE_OUTPUT: return "概率产物";
            case SUMMONING: return "召唤";
            case PURIFICATION: return "净化";
            case STERILE: return "无菌";
            case PHOTOCHEMICAL: return "光化学";
            case EXPERIMENTAL: return "实验性";
            case MAGIC_CONVERSION: return "魔法转换";
            case HIDDEN_RITUAL: return "隐秘仪式";
            case STRUCTURAL_CONTROL: return "结构控制";
            case RECYCLING: return "回收";
            case DESTRUCTIVE_RITUAL: return "毁灭仪式";
            case NIGHT_ALCHEMY: return "夜间炼金";
            case DAYLIGHT: return "日照";
            case DANGEROUS: return "危险反应";
            case DUAL_MATERIAL: return "双材料";
            case THREE_MAGIC_SYSTEMS: return "三系魔法";
            case CONSUMABLE_CATALYST: return "可消耗催化剂";
            default: return name();
        }
    }
}
