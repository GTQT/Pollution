package meowmel.pollution.common.metatileentity.multiblock.astral;

import java.util.Locale;

/**
 * Fixed identities of the industrial constellation towers. A tower never
 * reads its identity from an inserted item: its controller recipe permanently
 * binds it to exactly one Astral Sorcery constellation.
 */
public enum ConstellationTowerDefinition {

    AEVITAS("aevitas", "Aevitas", "生息座"),
    EVORSIO("evorsio", "Evorsio", "解离座"),
    ARMARA("armara", "Armara", "遁甲座"),
    DISCIDIA("discidia", "Discidia", "非攻座"),
    VICIO("vicio", "Vicio", "虚尽座"),
    MINERALIS("mineralis", "Mineralis", "鎏金座"),
    FORNAX("fornax", "Fornax", "天炉座"),
    HOROLOGIUM("horologium", "Horologium", "时钟座"),
    LUCERNA("lucerna", "Lucerna", "圣芒座"),
    OCTANS("octans", "Octans", "南极座"),
    BOOTES("bootes", "Bootes", "牧夫座"),
    PELOTRIO("pelotrio", "Pelotrio", "唤生座"),
    GELU("gelu", "Gelu", "寒冰座"),
    ULTERIA("ulteria", "Ulteria", "避焦座"),
    ALCARA("alcara", "Alcara", "振变座"),
    VORUX("vorux", "Vorux", "贪狼座");

    private final String id;
    private final String englishName;
    private final String chineseName;

    ConstellationTowerDefinition(String id, String englishName, String chineseName) {
        this.id = id;
        this.englishName = englishName;
        this.chineseName = chineseName;
    }

    public String getId() {
        return id;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getControllerPath() {
        return "constellation_tower." + id;
    }

    public String getTranslationKey() {
        return "pollution.machine.constellation_tower." + id + ".name";
    }

    public static ConstellationTowerDefinition fromId(String id) {
        if (id == null) return null;
        for (ConstellationTowerDefinition definition : values()) {
            if (definition.id.equalsIgnoreCase(id)) return definition;
        }
        return null;
    }

    @Override
    public String toString() {
        return id.toLowerCase(Locale.ROOT);
    }
}
