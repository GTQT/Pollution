package meowmel.pollution.api.amplification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static JEI-facing explanation of the dynamic amplification rules.
 * Values deliberately describe applicability rather than a final percentage:
 * the latter depends on the installed wafer, hatch tier, live sky and active card.
 */
public final class MagicJeiHintResolver {

    // The first line also carries a "星座（晶圆）" or "塔罗（塔罗仓）" prefix.
    // Keep the payload below 18 CJK characters so it fits a 176 px JEI page.
    private static final int MAX_LINE_LENGTH = 18;

    private MagicJeiHintResolver() {
    }

    /**
     * The display text for the constellation stored in a data wafer. Keeping
     * this in the JEI-facing resolver means recipe pages do not depend on
     * Astral Sorcery's internal/unlocalized name format.
     */
    public static String constellationDisplayName(String constellation) {
        String id = normalize(constellation);
        if ("aevitas".equals(id)) return "生息（Aevitas）";
        if ("evorsio".equals(id)) return "解离（Evorsio）";
        if ("armara".equals(id)) return "遁甲（Armara）";
        if ("discidia".equals(id)) return "非攻（Discidia）";
        if ("vicio".equals(id)) return "虚御（Vicio）";
        if ("mineralis".equals(id)) return "晶金（Mineralis）";
        if ("fornax".equals(id)) return "天炉（Fornax）";
        if ("horologium".equals(id)) return "时钟（Horologium）";
        if ("lucerna".equals(id)) return "圣芒（Lucerna）";
        if ("octans".equals(id)) return "南极（Octans）";
        if ("bootes".equals(id)) return "牧夫（Bootes）";
        if ("pelotrio".equals(id)) return "唤生（Pelotrio）";
        if ("gelu".equals(id)) return "寒冰（Gelu）";
        if ("ulteria".equals(id)) return "避役（Ulteria）";
        if ("alcara".equals(id)) return "振变（Alcara）";
        if ("vorux".equals(id)) return "贪狼（Vorux）";
        return constellation == null || constellation.trim().isEmpty() ? "任意" : constellation;
    }

    /** A compact, per-wafer description for celestial-machine JEI pages. */
    public static String constellationEffect(String constellation) {
        String id = normalize(constellation);
        if ("aevitas".equals(id)) return "生物产出、概率与速度";
        if ("evorsio".equals(id)) return "分解速度与副产概率";
        if ("armara".equals(id)) return "注魔节能与催化保护";
        if ("discidia".equals(id)) return "高负载速度与并行";
        if ("vicio".equals(id)) return "机械速度与并行";
        if ("mineralis".equals(id)) return "矿物产出与概率";
        if ("fornax".equals(id)) return "炉温、热加工速度与节能";
        if ("horologium".equals(id)) return "精密定时工序速度";
        if ("lucerna".equals(id)) return "净化产出、概率与速度";
        if ("octans".equals(id)) return "流体速度与并行";
        if ("bootes".equals(id)) return "组织产出与魔力介质";
        if ("pelotrio".equals(id)) return "概率产物额外重判";
        if ("gelu".equals(id)) return "冷却速度与节能（LuV+）";
        if ("ulteria".equals(id)) return "催化保护与进度保留（LuV+）";
        if ("alcara".equals(id)) return "多魔法介质、速度与能效（LuV+）";
        if ("vorux".equals(id)) return "极限加工速度与并行（LuV+）";
        return "对应工序的星辉增幅";
    }

    public static List<String> constellationHints(long tags) {
        List<String> hints = new ArrayList<>();
        if (has(tags, MagicProcessTag.BIOLOGICAL, MagicProcessTag.PLANT, MagicProcessTag.CELL_CULTURE))
            hints.add("生息：生物产出/速度");
        if (has(tags, MagicProcessTag.CRUSHING, MagicProcessTag.DECOMPOSITION, MagicProcessTag.SEPARATION))
            hints.add("解离：分解速度/副产物");
        if (has(tags, MagicProcessTag.STABILITY, MagicProcessTag.CATALYTIC, MagicProcessTag.INFUSION))
            hints.add("遁甲：EU/催化保护");
        if (has(tags, MagicProcessTag.HIGH_POWER, MagicProcessTag.AGGRESSIVE_PROCESSING))
            hints.add("非攻：高负载速度/并行");
        if (has(tags, MagicProcessTag.FORMING, MagicProcessTag.MECHANICAL, MagicProcessTag.TRANSPORT))
            hints.add("虚御：机械速度/并行");
        if (has(tags, MagicProcessTag.ORE, MagicProcessTag.MINERAL_ENRICHMENT))
            hints.add("晶金：矿物产出/概率");
        if (has(tags, MagicProcessTag.THERMAL, MagicProcessTag.SMELTING))
            hints.add("天炉：炉温/节能");
        if (has(tags, MagicProcessTag.PRECISION, MagicProcessTag.TIMED))
            hints.add("时钟：时序速度");
        if (has(tags, MagicProcessTag.PURIFICATION, MagicProcessTag.STERILE, MagicProcessTag.PHOTOCHEMICAL))
            hints.add("圣芒：净化产出/速度");
        if (has(tags, MagicProcessTag.FLUID, MagicProcessTag.DISTILLATION))
            hints.add("南极：流体速度/并行");
        if (has(tags, MagicProcessTag.ANIMAL, MagicProcessTag.TISSUE, MagicProcessTag.CELL_CULTURE))
            hints.add("牧夫：组织产出/介质");
        if (has(tags, MagicProcessTag.CHANCE_OUTPUT, MagicProcessTag.SUMMONING))
            hints.add("唤生：概率产物重判");
        if (has(tags, MagicProcessTag.COOLING, MagicProcessTag.SOLIDIFICATION))
            hints.add("寒冰(高级)：冷却节能");
        if (has(tags, MagicProcessTag.ENDURANCE, MagicProcessTag.CATALYTIC))
            hints.add("避役(高级)：保催化/进度");
        if (has(tags, MagicProcessTag.MULTI_MAGIC, MagicProcessTag.RESONANCE))
            hints.add("振变(高级)：介质节省");
        if (has(tags, MagicProcessTag.EXTREME_PROCESSING))
            hints.add("贪狼(高级)：极限并行");
        return pack(hints);
    }

    public static List<String> tarotHints(long tags) {
        List<String> hints = new ArrayList<>();
        if (has(tags, MagicProcessTag.EXPERIMENTAL)) hints.add("愚者：实验概率");
        if (has(tags, MagicProcessTag.MAGIC_CONVERSION, MagicProcessTag.MULTI_MAGIC)) hints.add("魔术师：介质节省");
        if (has(tags, MagicProcessTag.HIDDEN_RITUAL)) hints.add("女祭司：仪式介质");
        if (has(tags, MagicProcessTag.BIOLOGICAL, MagicProcessTag.PLANT, MagicProcessTag.CELL_CULTURE)) hints.add("皇后：生物产出");
        if (has(tags, MagicProcessTag.STRUCTURAL_CONTROL)) hints.add("皇帝：并行/催化");
        if (has(tags, MagicProcessTag.INFUSION)) hints.add("教皇：注魔介质");
        if (has(tags, MagicProcessTag.DUAL_MATERIAL)) hints.add("恋人：双材料产出");
        hints.add("战车：连续生产速度");
        hints.add("力量：速度/并行");
        hints.add("隐者：单并行节能");
        if (has(tags, MagicProcessTag.CHANCE_OUTPUT)) {
            hints.add("命运之轮：概率重判");
            hints.add("正义：概率强化");
        }
        hints.add("倒吊人：长配方节能");
        if (has(tags, MagicProcessTag.RECYCLING)) {
            hints.add("死神：回收产出");
            hints.add("审判：回收产出");
        }
        if (has(tags, MagicProcessTag.MULTI_MAGIC)) hints.add("节制：多魔法介质");
        if (has(tags, MagicProcessTag.DANGEROUS)) hints.add("恶魔：危险产出");
        if (has(tags, MagicProcessTag.DESTRUCTIVE_RITUAL)) hints.add("高塔：毁灭并行");
        hints.add("星星：晶圆强度");
        if (has(tags, MagicProcessTag.NIGHT_ALCHEMY)) hints.add("月亮：夜间产出");
        if (has(tags, MagicProcessTag.DAYLIGHT, MagicProcessTag.PURIFICATION,
                MagicProcessTag.STERILE, MagicProcessTag.PHOTOCHEMICAL)) hints.add("太阳：日照净化");
        if (has(tags, MagicProcessTag.THREE_MAGIC_SYSTEMS, MagicProcessTag.MULTI_MAGIC)) hints.add("世界：三系并行");
        return pack(hints);
    }

    private static boolean has(long tags, MagicProcessTag... required) {
        return MagicProcessTag.hasAny(tags, required);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static List<String> pack(List<String> entries) {
        if (entries.isEmpty()) return Collections.emptyList();
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String entry : entries) {
            int addedLength = entry.length() + (line.length() == 0 ? 0 : 1);
            if (line.length() > 0 && line.length() + addedLength > MAX_LINE_LENGTH) {
                lines.add(line.toString());
                line.setLength(0);
            }
            if (line.length() > 0) line.append('；');
            line.append(entry);
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }
}
