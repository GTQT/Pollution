package meowmel.pollution.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import meowmel.pollution.api.utils.PollutionLog;
import thaumcraft.api.aspects.Aspect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Fuel table for the Large Essentia Generator.
 *
 * <p>Ported from GregicaPlusPlus' {@code LargeEssentiaEnergyData}. The original looked the
 * aspect up through {@code Aspect.getAspect(name.toLowerCase())}, which cannot resolve aspects
 * registered by addons that use a different case convention, and it also silently dropped the
 * {@code consumeCeo == 0} entries. Here the table is keyed by {@link Aspect#getTag()} instead,
 * which is the canonical lower-case identifier Thaumcraft itself uses.</p>
 *
 * <p>The JSON lives at {@code assets/pollution/data/essentia_fuel.json}.</p>
 */
public final class EssentiaFuelData {

    /** The original applies a flat 1/4 coefficient to every tabulated fuel value. */
    public static final double FUEL_COEFFICIENT = 0.25D;

    private static final String RESOURCE = "assets/pollution/data/essentia_fuel.json";

    /** Keyed by {@link Aspect#getTag()}. */
    private static final Map<String, FuelEntry> FUEL_DATA = new HashMap<>();

    private static boolean loaded = false;

    private EssentiaFuelData() {}

    /**
     * Reads the fuel table. Safe to call more than once; only the first call does work.
     * Must run before any generator ticks, i.e. from pre-init.
     */
    public static void init() {
        if (loaded) return;
        loaded = true;
        String raw = readResource();
        if (raw == null) {
            PollutionLog.logger.error("Missing resource {}, the Large Essentia Generator will produce no EU", RESOURCE);
            return;
        }
        try {
            JsonObject root = new JsonParser().parse(raw).getAsJsonObject();
            JsonArray array = root.getAsJsonArray("Essentia");
            int count = 0;
            for (JsonElement element : array) {
                JsonObject entry = element.getAsJsonObject();
                String name = entry.get("name").getAsString().toLowerCase();
                int fuelValue = entry.get("fuelValue").getAsInt();
                String category = entry.get("category").getAsString();
                float consumeCeo = entry.get("consumeCeo").getAsFloat();
                FUEL_DATA.put(name, new FuelEntry(fuelValue, category, consumeCeo));
                count++;
            }
            PollutionLog.logger.info("Loaded {} essentia fuel entries", count);
        } catch (RuntimeException e) {
            PollutionLog.logger.error("Failed to parse " + RESOURCE, e);
        }
    }

    private static String readResource() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader == null ? null : loader.getResource(RESOURCE);
        if (url == null) return null;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            PollutionLog.logger.error("Failed to read " + RESOURCE, e);
            return null;
        }
    }

    private static FuelEntry entry(Aspect aspect) {
        if (aspect == null) return null;
        String tag = aspect.getTag();
        return tag == null ? null : FUEL_DATA.get(tag.toLowerCase());
    }

    /**
     * @return the category index documented on {@link EssentiaCategory}, or {@code -1} when the
     *         aspect is not a valid generator fuel.
     */
    public static int getCategoryIndex(Aspect aspect) {
        FuelEntry entry = entry(aspect);
        if (entry == null || entry.category == null) return -1;
        return entry.category.getIndex();
    }

    /** @return the tabulated fuel value already scaled by {@link #FUEL_COEFFICIENT}, or 0. */
    public static int getFuelValue(Aspect aspect) {
        FuelEntry entry = entry(aspect);
        return entry == null ? 0 : (int) (entry.fuelValue * FUEL_COEFFICIENT);
    }

    /** @return the catalyst consumption multiplier, or 0 when the aspect is not a fuel. */
    public static float getConsumeCeo(Aspect aspect) {
        FuelEntry entry = entry(aspect);
        return entry == null ? 0.0F : entry.consumeCeo;
    }

    /** @return the structured category, or {@code null} when the aspect is not a fuel. */
    public static EssentiaCategory getCategory(Aspect aspect) {
        FuelEntry entry = entry(aspect);
        return entry == null ? null : entry.category;
    }

    /** @return true when the aspect has a fuel table entry. */
    public static boolean isFuel(Aspect aspect) {
        return entry(aspect) != null;
    }

    /** @return how many fuel entries were loaded; 0 means the JSON was missing or broken. */
    public static int size() {
        return FUEL_DATA.size();
    }

    /**
     * Fuel categories, in the order the upgrade item bitmask uses. The ordinal is the bit index
     * installed into the controller's upgrade mask, so <b>do not reorder</b> existing constants.
     */
    public enum EssentiaCategory {
        NORMAL("NORMAL", 0),
        AIR("AIR", 1),
        THERMAL("THERMAL", 2),
        UNSTABLE("UNSTABLE", 3),
        VICTUS("VICTUS", 4),
        TAINTED("TAINTED", 5),
        MECHANICS("MECHANICS", 6),
        SPRITE("SPRITE", 7),
        RADIATION("RADIATION", 8),
        ELECTRIC("ELECTRIC", 9);

        private final String jsonName;
        private final int index;

        EssentiaCategory(String jsonName, int index) {
            this.jsonName = jsonName;
            this.index = index;
        }

        public String getJsonName() {
            return jsonName;
        }

        public int getIndex() {
            return index;
        }

        public static EssentiaCategory byJsonName(String name) {
            for (EssentiaCategory category : values()) {
                if (category.jsonName.equals(name)) return category;
            }
            return null;
        }
    }

    private static final class FuelEntry {
        private final int fuelValue;
        private final EssentiaCategory category;
        private final float consumeCeo;

        private FuelEntry(int fuelValue, String categoryName, float consumeCeo) {
            this.fuelValue = fuelValue;
            this.category = EssentiaCategory.byJsonName(categoryName);
            this.consumeCeo = consumeCeo;
        }
    }
}
