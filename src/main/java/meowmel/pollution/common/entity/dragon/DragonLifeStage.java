package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.POConfig;

/** Growth stages used by the Pollution ender dragon. The entity itself remains the same entity at every stage. */
public enum DragonLifeStage {
    EGG(0.04F, 0.04F),
    HATCHLING(0.04F, 0.09F),
    INFANT(0.10F, 0.18F),
    FLEDGLING(0.19F, 0.60F),
    JUVENILE(0.61F, 0.99F),
    ADULT(1.0F, 1.0F);

    private final float startScale;
    private final float finalScale;

    DragonLifeStage(float startScale, float finalScale) {
        this.startScale = startScale;
        this.finalScale = finalScale;
    }

    public float getScale() {
        return finalScale;
    }

    public float getScale(float progress) {
        float clamped = Math.max(0.0F, Math.min(1.0F, progress));
        return startScale + (finalScale - startScale) * clamped;
    }

    public int getDurationTicks() {
        switch (this) {
            case EGG:
                return Math.max(1, POConfig.DragonSettings.minimumIncubationTicks);
            case HATCHLING:
                return Math.max(1, POConfig.DragonSettings.hatchlingDurationTicks);
            case INFANT:
                return Math.max(1, POConfig.DragonSettings.infantDurationTicks);
            case FLEDGLING:
                return Math.max(1, POConfig.DragonSettings.fledglingDurationTicks);
            case JUVENILE:
                return Math.max(1, POConfig.DragonSettings.juvenileDurationTicks);
            default:
                return 0;
        }
    }

    public DragonLifeStage next() {
        int next = ordinal() + 1;
        return next < values().length ? values()[next] : this;
    }

    public String getTranslationKey() {
        return "entity.pollution.ender_dragon.stage." + name().toLowerCase(java.util.Locale.ROOT);
    }

    public static DragonLifeStage fromId(int id) {
        DragonLifeStage[] stages = values();
        return id >= 0 && id < stages.length ? stages[id] : EGG;
    }
}
