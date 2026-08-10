package meowmel.pollution.api.amplification;

import net.minecraft.nbt.NBTTagCompound;

/** First-batch, recipe-start modifiers. All values are non-negative ratios. */
public final class MagicAmplificationResult {

    public static final MagicAmplificationResult NONE = new MagicAmplificationResult(0.0D, 0.0D, 0.0D, 0, 0.0D,
            0.0D, 0.0D, 0.0D, 0.0D, 0, 0, "", "");

    private final double durationReduction;
    private final double eutReduction;
    private final double magicCostReduction;
    private final int extraParallel;
    private final double strength;
    private final double outputBonus;
    private final double chanceExtraRoll;
    private final double catalystSaveChance;
    private final double magicEnergyEfficiencyBonus;
    private final int progressRetentionTicks;
    private final int furnaceTemperatureBonus;
    private final String constellation;
    private final String tarot;

    public MagicAmplificationResult(double durationReduction, double eutReduction, double magicCostReduction,
                                    int extraParallel, double strength, double outputBonus, double chanceExtraRoll,
                                    double catalystSaveChance, double magicEnergyEfficiencyBonus,
                                    int progressRetentionTicks, int furnaceTemperatureBonus,
                                    String constellation, String tarot) {
        this.durationReduction = cap(durationReduction, 0.70D);
        this.eutReduction = cap(eutReduction, 0.50D);
        this.magicCostReduction = cap(magicCostReduction, 0.50D);
        this.extraParallel = Math.max(0, Math.min(3, extraParallel));
        this.strength = Math.max(0.0D, Math.min(0.50D, strength));
        this.outputBonus = cap(outputBonus, 0.50D);
        this.chanceExtraRoll = cap(chanceExtraRoll, 1.00D);
        this.catalystSaveChance = cap(catalystSaveChance, 0.70D);
        this.magicEnergyEfficiencyBonus = cap(magicEnergyEfficiencyBonus, 0.20D);
        this.progressRetentionTicks = Math.max(0, Math.min(340, progressRetentionTicks));
        this.furnaceTemperatureBonus = Math.max(0, Math.min(1200, furnaceTemperatureBonus));
        this.constellation = constellation == null ? "" : constellation;
        this.tarot = tarot == null ? "" : tarot;
    }

    private static double cap(double value, double maximum) {
        return Math.max(0.0D, Math.min(maximum, value));
    }

    /**
     * Returns only the contribution added on top of a baseline snapshot. This
     * lets the UI report a tarot card separately while the recipe logic still
     * consumes one authoritative, already-combined result.
     */
    public MagicAmplificationResult subtract(MagicAmplificationResult baseline) {
        MagicAmplificationResult base = baseline == null ? NONE : baseline;
        return new MagicAmplificationResult(
                durationReduction - base.durationReduction,
                eutReduction - base.eutReduction,
                magicCostReduction - base.magicCostReduction,
                extraParallel - base.extraParallel,
                strength - base.strength,
                outputBonus - base.outputBonus,
                chanceExtraRoll - base.chanceExtraRoll,
                catalystSaveChance - base.catalystSaveChance,
                magicEnergyEfficiencyBonus - base.magicEnergyEfficiencyBonus,
                progressRetentionTicks - base.progressRetentionTicks,
                furnaceTemperatureBonus - base.furnaceTemperatureBonus,
                constellation, tarot);
    }

    /** Compact persistence used for the baseline snapshot of a running recipe. */
    public NBTTagCompound serializeSnapshot() {
        NBTTagCompound data = new NBTTagCompound();
        data.setDouble("duration", durationReduction);
        data.setDouble("eut", eutReduction);
        data.setDouble("magic", magicCostReduction);
        data.setInteger("parallel", extraParallel);
        data.setDouble("strength", strength);
        data.setDouble("output", outputBonus);
        data.setDouble("chance", chanceExtraRoll);
        data.setDouble("catalyst", catalystSaveChance);
        data.setDouble("efficiency", magicEnergyEfficiencyBonus);
        data.setInteger("retention", progressRetentionTicks);
        data.setInteger("temperature", furnaceTemperatureBonus);
        data.setString("constellation", constellation);
        data.setString("tarot", tarot);
        return data;
    }

    public static MagicAmplificationResult deserializeSnapshot(NBTTagCompound data) {
        if (data == null || data.isEmpty()) return NONE;
        return new MagicAmplificationResult(
                data.getDouble("duration"), data.getDouble("eut"), data.getDouble("magic"),
                data.getInteger("parallel"), data.getDouble("strength"), data.getDouble("output"),
                data.getDouble("chance"), data.getDouble("catalyst"), data.getDouble("efficiency"),
                data.getInteger("retention"), data.getInteger("temperature"),
                data.getString("constellation"), data.getString("tarot"));
    }

    public double getDurationReduction() { return durationReduction; }
    public double getEutReduction() { return eutReduction; }
    public double getMagicCostReduction() { return magicCostReduction; }
    public int getExtraParallel() { return extraParallel; }
    public double getStrength() { return strength; }
    public double getOutputBonus() { return outputBonus; }
    public double getChanceExtraRoll() { return chanceExtraRoll; }
    public double getCatalystSaveChance() { return catalystSaveChance; }
    public double getMagicEnergyEfficiencyBonus() { return magicEnergyEfficiencyBonus; }
    public int getProgressRetentionTicks() { return progressRetentionTicks; }
    public int getFurnaceTemperatureBonus() { return furnaceTemperatureBonus; }
    public String getConstellation() { return constellation; }
    public String getTarot() { return tarot; }
    public boolean isActive() {
        return durationReduction > 0.0D || eutReduction > 0.0D || magicCostReduction > 0.0D || extraParallel > 0
                || outputBonus > 0.0D || chanceExtraRoll > 0.0D || catalystSaveChance > 0.0D
                || progressRetentionTicks > 0 || furnaceTemperatureBonus > 0;
    }
}
