package meowmel.pollution.api.recipes.properties;

import gregtech.api.GregTechAPI;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.properties.RecipeProperty;
import meowmel.pollution.api.amplification.MagicProcessTag;
import meowmel.pollution.api.amplification.MagicJeiHintResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

/**
 * Recipe properties understood by {@code MagicMultiblockRecipeLogic}.
 *
 * <p>Only recipes that explicitly carry one of these properties opt in to
 * the new behaviour. Recipes without them continue to use the legacy magic
 * machine costs, which keeps existing packs and scripts valid.</p>
 */
public final class MagicRecipeProperties {

    public static final IntProperty INFUSED_FLUID_PER_TICK = new IntProperty(
            "pollution.magic.infused_fluid_per_tick", "要素液/t");
    public static final LongProperty MANA_PER_TICK = new LongProperty(
            "pollution.magic.mana_per_tick", "纯魔力/t");
    public static final IntProperty LIFE_ESSENCE_PER_TICK = new IntProperty(
            "pollution.magic.life_essence_per_tick", "生命源质/t");
    public static final IntProperty VIS_PER_CRAFT = new IntProperty(
            "pollution.magic.vis_per_craft", "Vis/次");
    public static final AstralProperty ASTRAL_CONDITION = new AstralProperty();
    public static final StringProperty TAROT = new StringProperty(
            "pollution.magic.tarot", "塔罗牌");
    public static final StringProperty THAUMCRAFT_RESEARCH = new StringProperty(
            "pollution.magic.thaumcraft_research", "所需神秘研究");
    private static final ConstellationTargetProperty CELESTIAL_TARGET = new ConstellationTargetProperty();
    private static final ConstellationEffectProperty CELESTIAL_EFFECT = new ConstellationEffectProperty();

    /** Explicit recipe domain. A zero value uses the machine's legacy fallback profile. */
    public static final ProcessTagProperty PROCESS_TAG_MASK = new ProcessTagProperty();
    /** Comma-separated recipe item-input indices eligible for catalyst protection. */
    public static final CatalystInputsProperty CONSUMABLE_CATALYST_INPUTS = new CatalystInputsProperty();
    /**
     * Static JEI handbook text. Each visible line is a separate property because
     * GT's JEI wrapper reserves vertical space by property count, not by a
     * property's reported multi-line height.
     */
    private static final GuideLineProperty[] JEI_GUIDE_LINES = createGuideLines();
    private static final HintProperty[] CONSTELLATION_HINTS = {
            new HintProperty("pollution.magic.constellation_hint_1", "可触发星座"),
            new HintProperty("pollution.magic.constellation_hint_2", "星座续项"),
            new HintProperty("pollution.magic.constellation_hint_3", "星座续项")
    };
    private static final HintProperty[] TAROT_HINTS = {
            new HintProperty("pollution.magic.tarot_hint_1", "可触发塔罗"),
            new HintProperty("pollution.magic.tarot_hint_2", "塔罗续项"),
            new HintProperty("pollution.magic.tarot_hint_3", "塔罗续项"),
            new HintProperty("pollution.magic.tarot_hint_4", "塔罗续项")
    };

    private static boolean initialized;

    private MagicRecipeProperties() {
    }

    public static synchronized void init() {
        if (initialized) return;
        initialized = true;
        GregTechAPI.RECIPE_PROPERTIES.register(INFUSED_FLUID_PER_TICK.getKey(), INFUSED_FLUID_PER_TICK);
        GregTechAPI.RECIPE_PROPERTIES.register(MANA_PER_TICK.getKey(), MANA_PER_TICK);
        GregTechAPI.RECIPE_PROPERTIES.register(LIFE_ESSENCE_PER_TICK.getKey(), LIFE_ESSENCE_PER_TICK);
        GregTechAPI.RECIPE_PROPERTIES.register(VIS_PER_CRAFT.getKey(), VIS_PER_CRAFT);
        GregTechAPI.RECIPE_PROPERTIES.register(ASTRAL_CONDITION.getKey(), ASTRAL_CONDITION);
        GregTechAPI.RECIPE_PROPERTIES.register(TAROT.getKey(), TAROT);
        GregTechAPI.RECIPE_PROPERTIES.register(THAUMCRAFT_RESEARCH.getKey(), THAUMCRAFT_RESEARCH);
        GregTechAPI.RECIPE_PROPERTIES.register(CELESTIAL_TARGET.getKey(), CELESTIAL_TARGET);
        GregTechAPI.RECIPE_PROPERTIES.register(CELESTIAL_EFFECT.getKey(), CELESTIAL_EFFECT);
        GregTechAPI.RECIPE_PROPERTIES.register(PROCESS_TAG_MASK.getKey(), PROCESS_TAG_MASK);
        GregTechAPI.RECIPE_PROPERTIES.register(CONSUMABLE_CATALYST_INPUTS.getKey(), CONSUMABLE_CATALYST_INPUTS);
        for (GuideLineProperty property : JEI_GUIDE_LINES) {
            GregTechAPI.RECIPE_PROPERTIES.register(property.getKey(), property);
        }
        for (HintProperty property : CONSTELLATION_HINTS) {
            GregTechAPI.RECIPE_PROPERTIES.register(property.getKey(), property);
        }
        for (HintProperty property : TAROT_HINTS) {
            GregTechAPI.RECIPE_PROPERTIES.register(property.getKey(), property);
        }
    }

    public static <R extends RecipeBuilder<R>> R infusedFluidPerTick(R builder, int amount) {
        builder.applyProperty(INFUSED_FLUID_PER_TICK, Math.max(0, amount));
        return builder;
    }

    public static <R extends RecipeBuilder<R>> R manaPerTick(R builder, long amount) {
        builder.applyProperty(MANA_PER_TICK, Math.max(0L, amount));
        return builder;
    }

    public static <R extends RecipeBuilder<R>> R lifeEssencePerTick(R builder, int amount) {
        builder.applyProperty(LIFE_ESSENCE_PER_TICK, Math.max(0, amount));
        return builder;
    }

    public static <R extends RecipeBuilder<R>> R visPerCraft(R builder, int amount) {
        builder.applyProperty(VIS_PER_CRAFT, Math.max(0, amount));
        return builder;
    }

    public static <R extends RecipeBuilder<R>> R astralCondition(R builder, AstralCondition condition) {
        builder.applyProperty(ASTRAL_CONDITION, condition == null ? AstralCondition.NONE : condition);
        return builder;
    }

    public static <R extends RecipeBuilder<R>> R tarot(R builder, String tarotId) {
        builder.applyProperty(TAROT, tarotId == null ? "" : tarotId.trim());
        return builder;
    }

    public static <R extends RecipeBuilder<R>> R thaumcraftResearch(R builder, String researchKey) {
        String normalized = researchKey == null ? "" : researchKey.trim();
        if (!normalized.isEmpty()) {
            builder.applyProperty(THAUMCRAFT_RESEARCH, normalized);
        }
        return builder;
    }

    /**
     * Identifies the constellation NBT written by the current celestial
     * observation/calibration page. Generic process hints are shared by these
     * recipes; these two properties make each target page distinct in JEI.
     */
    public static <R extends RecipeBuilder<R>> R celestialTarget(R builder, String constellationId) {
        String normalized = constellationId == null ? "" : constellationId.trim();
        builder.applyProperty(CELESTIAL_TARGET, normalized);
        builder.applyProperty(CELESTIAL_EFFECT, normalized);
        return builder;
    }

    public static <R extends RecipeBuilder<R>> R processTags(R builder, MagicProcessTag... tags) {
        long mask = MagicProcessTag.maskOf(tags);
        builder.applyProperty(PROCESS_TAG_MASK, mask);
        applyHints(builder, CONSTELLATION_HINTS, MagicJeiHintResolver.constellationHints(mask));
        applyHints(builder, TAROT_HINTS, MagicJeiHintResolver.tarotHints(mask));
        return builder;
    }

    private static <R extends RecipeBuilder<R>> void applyHints(R builder, HintProperty[] properties,
                                                                  java.util.List<String> hints) {
        int count = Math.min(properties.length, hints.size());
        for (int i = 0; i < count; i++) {
            String value = hints.get(i);
            if (i == count - 1 && hints.size() > properties.length) {
                value += "（另" + (hints.size() - properties.length) + "项）";
            }
            builder.applyProperty(properties[i], value);
        }
    }

    public static <R extends RecipeBuilder<R>> R consumableCatalystInputs(R builder, int... inputIndices) {
        if (inputIndices == null || inputIndices.length == 0) return builder;
        StringBuilder serialized = new StringBuilder();
        for (int index : inputIndices) {
            if (index < 0) continue;
            if (serialized.length() > 0) serialized.append(',');
            serialized.append(index);
        }
        if (serialized.length() > 0) builder.applyProperty(CONSUMABLE_CATALYST_INPUTS, serialized.toString());
        return builder;
    }

    public static <R extends RecipeBuilder<R>> R guidePage(R builder, String title, String... lines) {
        builder.applyProperty(JEI_GUIDE_LINES[0], title == null ? "" : title);
        if (lines == null) return builder;
        int lineIndex = 1;
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            if (lineIndex >= JEI_GUIDE_LINES.length) break;
            builder.applyProperty(JEI_GUIDE_LINES[lineIndex++], line);
        }
        return builder;
    }

    private static GuideLineProperty[] createGuideLines() {
        GuideLineProperty[] properties = new GuideLineProperty[32];
        for (int i = 0; i < properties.length; i++) {
            properties[i] = new GuideLineProperty(String.format("pollution.magic.jei_guide_line_%02d", i + 1), i == 0);
        }
        return properties;
    }

    public static <R extends RecipeBuilder<R>> R magic(R builder, int infusedPerTick, long manaPerTick,
                                                        int lifeEssencePerTick, int visPerCraft,
                                                        AstralCondition astralCondition, String tarotId) {
        infusedFluidPerTick(builder, infusedPerTick);
        manaPerTick(builder, manaPerTick);
        lifeEssencePerTick(builder, lifeEssencePerTick);
        visPerCraft(builder, visPerCraft);
        if (astralCondition != null && astralCondition.isConfigured()) {
            astralCondition(builder, astralCondition);
        }
        if (tarotId != null && !tarotId.trim().isEmpty()) {
            tarot(builder, tarotId);
        }
        return builder;
    }

    public static final class IntProperty extends RecipeProperty<Integer> {
        private final String label;

        private IntProperty(String key, String label) {
            super(key, Integer.class);
            this.label = label;
        }

        @Override
        public NBTBase serialize(Object value) {
            return new NBTTagInt(Math.max(0, castValue(value)));
        }

        @Override
        public Object deserialize(NBTBase tag) {
            return tag instanceof NBTPrimitive ? Math.max(0, ((NBTPrimitive) tag).getInt()) : 0;
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString(TextFormatting.GRAY + label + ": " + castValue(value), x, y, color);
        }
    }

    public static class LongProperty extends RecipeProperty<Long> {
        private final String label;

        protected LongProperty(String key, String label) {
            super(key, Long.class);
            this.label = label;
        }

        @Override
        public NBTBase serialize(Object value) {
            return new NBTTagLong(Math.max(0L, castValue(value)));
        }

        @Override
        public Object deserialize(NBTBase tag) {
            return tag instanceof NBTPrimitive ? Math.max(0L, ((NBTPrimitive) tag).getLong()) : 0L;
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString(TextFormatting.GRAY + label + ": " + castValue(value), x, y, color);
        }
    }

    public static class StringProperty extends RecipeProperty<String> {
        private final String label;

        protected StringProperty(String key, String label) {
            super(key, String.class);
            this.label = label;
        }

        @Override
        public NBTBase serialize(Object value) {
            return new NBTTagString(castValue(value));
        }

        @Override
        public Object deserialize(NBTBase tag) {
            return tag instanceof NBTTagString ? ((NBTTagString) tag).getString() : "";
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString(TextFormatting.GRAY + label + ": " + castValue(value), x, y, color);
        }
    }

    /** Shows tags as named domains instead of an opaque bit mask in JEI. */
    public static final class ProcessTagProperty extends LongProperty {
        private ProcessTagProperty() {
            super("pollution.magic.process_tag_mask", "魔导工序");
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString(TextFormatting.GRAY + "魔导工序: "
                    + MagicProcessTag.describeMask(castValue(value)), x, y, color);
        }
    }

    /** Identifies the distinct constellation NBT written by this recipe page. */
    private static final class ConstellationTargetProperty extends StringProperty {
        private ConstellationTargetProperty() {
            super("pollution.magic.celestial_target", "目标星座");
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString(TextFormatting.AQUA + "写入星座："
                    + MagicJeiHintResolver.constellationDisplayName(castValue(value)), x, y, color);
        }
    }

    /** States the gameplay purpose of the constellation written by this page. */
    private static final class ConstellationEffectProperty extends StringProperty {
        private ConstellationEffectProperty() {
            super("pollution.magic.celestial_effect", "星座晶圆增幅");
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString(TextFormatting.LIGHT_PURPLE + "后续增幅："
                    + MagicJeiHintResolver.constellationEffect(castValue(value)), x, y, color);
        }
    }

    /** Identifies the item-input indices eligible for the catalyst-save rule. */
    public static final class CatalystInputsProperty extends StringProperty {
        private CatalystInputsProperty() {
            super("pollution.magic.consumable_catalyst_inputs", "可保护催化剂输入");
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString(TextFormatting.GRAY + "可保护催化剂槽: "
                    + castValue(value), x, y, color);
        }
    }

    /** One JEI line belonging to either the constellation or tarot static-help layer. */
    public static final class HintProperty extends StringProperty {
        private HintProperty(String key, String label) {
            super(key, label);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString(TextFormatting.LIGHT_PURPLE + label() + ": "
                    + castValue(value), x, y, color);
        }

        private String label() {
            return getKey().endsWith("hint_1")
                    ? (getKey().contains("constellation") ? "星座（晶圆）" : "塔罗（塔罗仓）")
                    : "　";
        }
    }

    /** One physical JEI line in a static handbook recipe. */
    private static final class GuideLineProperty extends StringProperty {
        private final boolean title;

        private GuideLineProperty(String key, boolean title) {
            super(key, "");
            this.title = title;
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            minecraft.fontRenderer.drawString((title ? TextFormatting.GOLD : TextFormatting.LIGHT_PURPLE)
                    + castValue(value), x, y, color);
        }

        @Override
        public boolean hideTotalEU() {
            return true;
        }

        @Override
        public boolean hideEUt() {
            return true;
        }

        @Override
        public boolean hideDuration() {
            return true;
        }
    }

    public static final class AstralProperty extends RecipeProperty<AstralCondition> {
        private AstralProperty() {
            super("pollution.magic.astral_condition", AstralCondition.class);
        }

        @Override
        public NBTBase serialize(Object value) {
            AstralCondition condition = castValue(value);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("Constellation", condition.getConstellation());
            tag.setString("MoonPhase", condition.getMoonPhase());
            tag.setString("CelestialEvent", condition.getCelestialEvent());
            tag.setBoolean("RequireNight", condition.isNightRequired());
            tag.setFloat("MinimumDistribution", condition.getMinimumDistribution());
            return tag;
        }

        @Override
        public Object deserialize(NBTBase tag) {
            if (!(tag instanceof NBTTagCompound)) return AstralCondition.NONE;
            NBTTagCompound compound = (NBTTagCompound) tag;
            return new AstralCondition(
                    compound.getString("Constellation"),
                    compound.getString("MoonPhase"),
                    compound.getString("CelestialEvent"),
                    compound.getBoolean("RequireNight"),
                    compound.getFloat("MinimumDistribution"));
        }

        @Override
        public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
            AstralCondition condition = castValue(value);
            String constellation = MagicJeiHintResolver.constellationDisplayName(condition.getConstellation());
            minecraft.fontRenderer.drawString(TextFormatting.GRAY + "星象: " + constellation, x, y, color);
        }
    }
}
