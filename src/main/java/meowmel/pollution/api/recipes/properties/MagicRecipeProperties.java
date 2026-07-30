package meowmel.pollution.api.recipes.properties;

import gregtech.api.GregTechAPI;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.properties.RecipeProperty;
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

    public static final class LongProperty extends RecipeProperty<Long> {
        private final String label;

        private LongProperty(String key, String label) {
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

    public static final class StringProperty extends RecipeProperty<String> {
        private final String label;

        private StringProperty(String key, String label) {
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
            String constellation = condition.getConstellation().isEmpty() ? "任意" : condition.getConstellation();
            minecraft.fontRenderer.drawString(TextFormatting.GRAY + "星象: " + constellation, x, y, color);
        }
    }
}
