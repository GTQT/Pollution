package meowmel.pollution.api.amplification;

import meowmel.pollution.api.capability.ITarotHatch;

import java.util.Locale;

/** Pure, server-side rule table for every constellation and major arcana. */
public final class MagicAmplificationEngine {

    private MagicAmplificationEngine() {
    }

    public static MagicAmplificationResult calculate(long recipeTags, int recipeDuration,
                                                       AstralAmplifierSnapshot astral, ITarotHatch tarotHatch,
                                                       int chariotStacks, boolean singleParallel) {
        if (astral == null || !astral.hasDataWafer()) return MagicAmplificationResult.NONE;

        String tarot = tarotHatch == null ? "" : normalize(tarotHatch.getActiveTarot());
        double strength = astral.getBaseStrength() + astral.getSkyStrength();
        if ("the_star".equals(tarot)) strength *= 1.25D;
        strength = Math.min(0.50D, strength);

        double duration = 0.0D;
        double eut = 0.0D;
        double magic = 0.0D;
        double output = 0.0D;
        double chance = 0.0D;
        double catalyst = 0.0D;
        double energyEfficiency = 0.0D;
        int parallel = 0;
        int retention = 0;
        int furnaceTemperature = 0;
        String constellation = normalize(astral.getConstellation());

        if ("aevitas".equals(constellation) && has(recipeTags, MagicProcessTag.BIOLOGICAL,
                MagicProcessTag.PLANT, MagicProcessTag.CELL_CULTURE)) {
            output += strength;
            chance += strength;
            duration += 0.25D * strength;
        } else if ("evorsio".equals(constellation) && has(recipeTags, MagicProcessTag.CRUSHING,
                MagicProcessTag.DECOMPOSITION, MagicProcessTag.SEPARATION)) {
            duration += strength;
            output += 0.5D * strength;
            chance += 0.5D * strength;
        } else if ("armara".equals(constellation) && has(recipeTags, MagicProcessTag.STABILITY,
                MagicProcessTag.CATALYTIC, MagicProcessTag.INFUSION)) {
            eut += 0.5D * strength;
            catalyst += strength;
            retention = retain(strength);
        } else if ("discidia".equals(constellation) && has(recipeTags, MagicProcessTag.HIGH_POWER,
                MagicProcessTag.AGGRESSIVE_PROCESSING)) {
            duration += 1.25D * strength;
            parallel += thresholdParallel(strength, 0.20D, 0.35D);
        } else if ("vicio".equals(constellation) && has(recipeTags, MagicProcessTag.FORMING,
                MagicProcessTag.MECHANICAL, MagicProcessTag.TRANSPORT)) {
            duration += strength;
            if (strength >= 0.25D) parallel++;
        } else if ("mineralis".equals(constellation) && has(recipeTags, MagicProcessTag.ORE,
                MagicProcessTag.MINERAL_ENRICHMENT)) {
            output += strength;
            chance += 0.5D * strength;
        } else if ("fornax".equals(constellation) && has(recipeTags, MagicProcessTag.THERMAL,
                MagicProcessTag.SMELTING)) {
            furnaceTemperature = (int) Math.ceil(3000.0D * strength);
            duration += 0.5D * strength;
            eut += 0.25D * strength;
        } else if ("horologium".equals(constellation) && has(recipeTags, MagicProcessTag.PRECISION,
                MagicProcessTag.TIMED)) {
            duration += strength;
        } else if ("lucerna".equals(constellation) && has(recipeTags, MagicProcessTag.PURIFICATION,
                MagicProcessTag.STERILE, MagicProcessTag.PHOTOCHEMICAL)) {
            output += 0.5D * strength;
            chance += strength;
            duration += 0.5D * strength;
        } else if ("octans".equals(constellation) && has(recipeTags, MagicProcessTag.FLUID,
                MagicProcessTag.DISTILLATION)) {
            duration += 0.75D * strength;
            if (strength >= 0.20D) parallel++;
        } else if ("bootes".equals(constellation) && has(recipeTags, MagicProcessTag.ANIMAL,
                MagicProcessTag.TISSUE, MagicProcessTag.CELL_CULTURE)) {
            output += strength;
            magic += 0.5D * strength;
        } else if ("pelotrio".equals(constellation) && has(recipeTags, MagicProcessTag.CHANCE_OUTPUT,
                MagicProcessTag.SUMMONING)) {
            chance += strength;
        } else if ("gelu".equals(constellation) && astral.isAdvancedHatch()
                && has(recipeTags, MagicProcessTag.COOLING, MagicProcessTag.SOLIDIFICATION)) {
            duration += strength;
            eut += 0.5D * strength;
        } else if ("ulteria".equals(constellation) && astral.isAdvancedHatch()
                && has(recipeTags, MagicProcessTag.ENDURANCE, MagicProcessTag.CATALYTIC)) {
            catalyst += strength;
            retention = retain(strength);
        } else if ("alcara".equals(constellation) && astral.isAdvancedHatch()
                && has(recipeTags, MagicProcessTag.MULTI_MAGIC, MagicProcessTag.RESONANCE)) {
            magic += 0.75D * strength;
            duration += 0.25D * strength;
            energyEfficiency += 0.5D * strength;
        } else if ("vorux".equals(constellation) && astral.isAdvancedHatch()
                && has(recipeTags, MagicProcessTag.EXTREME_PROCESSING)) {
            duration += 1.5D * strength;
            parallel += thresholdParallel(strength, 0.15D, 0.30D);
        }

        if ("the_fool".equals(tarot) && has(recipeTags, MagicProcessTag.EXPERIMENTAL)) {
            chance += 0.10D;
        } else if ("the_magician".equals(tarot) && has(recipeTags, MagicProcessTag.MAGIC_CONVERSION,
                MagicProcessTag.MULTI_MAGIC)) {
            magic += 0.10D;
        } else if ("the_high_priestess".equals(tarot) && has(recipeTags, MagicProcessTag.HIDDEN_RITUAL)) {
            magic += 0.10D;
        } else if ("the_empress".equals(tarot) && has(recipeTags, MagicProcessTag.BIOLOGICAL,
                MagicProcessTag.PLANT, MagicProcessTag.CELL_CULTURE)) {
            output += 0.25D;
        } else if ("the_emperor".equals(tarot) && has(recipeTags, MagicProcessTag.STRUCTURAL_CONTROL)) {
            parallel++;
            catalyst += 0.10D;
        } else if ("the_highophant".equals(tarot) && has(recipeTags, MagicProcessTag.INFUSION)) {
            magic += 0.15D;
        } else if ("the_lovers".equals(tarot) && has(recipeTags, MagicProcessTag.DUAL_MATERIAL)) {
            output += 0.10D;
        } else if ("the_chariot".equals(tarot)) {
            duration += Math.min(5, Math.max(0, chariotStacks)) * 0.05D;
        } else if ("the_strength".equals(tarot)) {
            duration += 0.10D;
            parallel++;
        } else if ("the_hermit".equals(tarot) && singleParallel) {
            eut += 0.20D;
            catalyst += 0.25D;
        } else if ("the_wheel_of_fortune".equals(tarot) && has(recipeTags, MagicProcessTag.CHANCE_OUTPUT)) {
            chance = 1.00D;
        } else if ("the_justice".equals(tarot) && has(recipeTags, MagicProcessTag.CHANCE_OUTPUT)) {
            chance += 0.50D;
        } else if ("the_hanged_man".equals(tarot) && recipeDuration >= 400) {
            eut += 0.25D;
            magic += 0.20D;
        } else if ("death".equals(tarot) && has(recipeTags, MagicProcessTag.RECYCLING)) {
            output += 0.25D;
        } else if ("temperance".equals(tarot) && has(recipeTags, MagicProcessTag.MULTI_MAGIC)) {
            magic += 0.10D;
        } else if ("the_devil".equals(tarot) && has(recipeTags, MagicProcessTag.DANGEROUS)) {
            output += 0.20D;
        } else if ("the_tower".equals(tarot) && has(recipeTags, MagicProcessTag.DESTRUCTIVE_RITUAL)) {
            duration += 0.10D;
            parallel += 2;
        } else if ("the_moon".equals(tarot) && astral.isNight() && has(recipeTags, MagicProcessTag.NIGHT_ALCHEMY)) {
            output += 0.15D;
        } else if ("the_sun".equals(tarot) && !astral.isNight() && has(recipeTags, MagicProcessTag.DAYLIGHT,
                MagicProcessTag.PURIFICATION, MagicProcessTag.STERILE, MagicProcessTag.PHOTOCHEMICAL)) {
            duration += 0.20D;
            output += 0.10D;
        } else if ("judgement".equals(tarot) && has(recipeTags, MagicProcessTag.RECYCLING)) {
            output += 0.25D;
        } else if ("the_world".equals(tarot) && has(recipeTags, MagicProcessTag.THREE_MAGIC_SYSTEMS,
                MagicProcessTag.MULTI_MAGIC)) {
            duration += 0.10D;
            magic += 0.10D;
            parallel++;
        }

        return new MagicAmplificationResult(duration, eut, magic, parallel, strength, output, chance, catalyst,
                energyEfficiency, retention, furnaceTemperature, constellation, tarot);
    }

    private static int thresholdParallel(double strength, double first, double second) {
        return strength >= second ? 2 : strength >= first ? 1 : 0;
    }

    private static int retain(double strength) {
        return (int) Math.ceil(100.0D + 600.0D * strength);
    }

    private static boolean has(long tags, MagicProcessTag... required) {
        return MagicProcessTag.hasAny(tags, required);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
