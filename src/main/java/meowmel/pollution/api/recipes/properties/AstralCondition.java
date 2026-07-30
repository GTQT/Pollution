package meowmel.pollution.api.recipes.properties;

import java.util.Locale;

/**
 * Server-side sky requirements for an Astral Sorcery powered recipe.
 * Empty values mean that the corresponding check is not required.
 */
public final class AstralCondition {

    public static final AstralCondition NONE = new AstralCondition("", "", "", false, 0.0F);

    private final String constellation;
    private final String moonPhase;
    private final String celestialEvent;
    private final boolean requireNight;
    private final float minimumDistribution;

    public AstralCondition(String constellation, String moonPhase, String celestialEvent,
                           boolean requireNight, float minimumDistribution) {
        this.constellation = normalize(constellation);
        this.moonPhase = normalize(moonPhase);
        this.celestialEvent = normalize(celestialEvent);
        this.requireNight = requireNight;
        this.minimumDistribution = Math.max(0.0F, Math.min(1.0F, minimumDistribution));
    }

    public static AstralCondition night(String constellation, float minimumDistribution) {
        return new AstralCondition(constellation, "", "", true, minimumDistribution);
    }

    public static AstralCondition fullMoon(String constellation, float minimumDistribution) {
        return new AstralCondition(constellation, "FULL", "", true, minimumDistribution);
    }

    public static AstralCondition event(String constellation, String celestialEvent, float minimumDistribution) {
        return new AstralCondition(constellation, "", celestialEvent, true, minimumDistribution);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    public String getConstellation() {
        return constellation;
    }

    public String getMoonPhase() {
        return moonPhase;
    }

    public String getCelestialEvent() {
        return celestialEvent;
    }

    public boolean isNightRequired() {
        return requireNight;
    }

    public float getMinimumDistribution() {
        return minimumDistribution;
    }

    public boolean isConfigured() {
        return requireNight || minimumDistribution > 0.0F || !constellation.isEmpty()
                || !moonPhase.isEmpty() || !celestialEvent.isEmpty();
    }
}
