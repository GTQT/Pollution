package meowmel.pollution.api.amplification;

import gregtech.api.GTValues;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.chance.output.impl.ChancedItemOutput;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.ore.OrePrefix;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Safe, deterministic item-output settlement for constellation and tarot bonuses. */
public final class MagicOutputProcessor {

    private static final int CHANCE_SCALE = 10000;

    private MagicOutputProcessor() {
    }

    public static List<ItemStack> forecast(Recipe recipe, int parallel, MagicAmplificationResult result) {
        List<ItemStack> resultStacks = new ArrayList<>();
        if (recipe == null || result == null || result.getOutputBonus() <= 0.0D) return resultStacks;
        for (ItemStack stack : recipe.getOutputs()) {
            if (!isAmplifiable(stack)) continue;
            int amount = safeCount(Math.ceil(stack.getCount() * Math.max(1, parallel) * result.getOutputBonus()));
            addSplit(resultStacks, stack, amount);
        }
        if (result.getChanceExtraRoll() > 0.0D) {
            for (int copy = 0; copy < Math.max(1, parallel); copy++) {
                for (ChancedItemOutput output : recipe.getChancedOutputs().getChancedEntries()) {
                    if (isAmplifiable(output.getIngredient())) addSplit(resultStacks, output.getIngredient(),
                            Math.max(1, output.getIngredient().getCount()));
                }
            }
        }
        return resultStacks;
    }

    /**
     * Advances only the safe deterministic accumulators. It is called exactly
     * once after GT has accepted and consumed the recipe inputs.
     */
    public static List<ItemStack> settle(Recipe recipe, int parallel, MagicAmplificationResult result,
                                          Map<String, Double> fractionalOutputs) {
        List<ItemStack> extras = new ArrayList<>();
        if (recipe == null || result == null) return extras;

        if (result.getOutputBonus() > 0.0D) {
            List<ItemStack> outputs = recipe.getOutputs();
            for (int index = 0; index < outputs.size(); index++) {
                ItemStack stack = outputs.get(index);
                if (!isAmplifiable(stack)) continue;
                String key = fractionKey(recipe, index);
                double stored = fractionalOutputs.containsKey(key) ? fractionalOutputs.get(key) : 0.0D;
                double value = stored + stack.getCount() * Math.max(1, parallel) * result.getOutputBonus();
                int extra = safeCount(Math.floor(value));
                fractionalOutputs.put(key, value - extra);
                addSplit(extras, stack, extra);
            }
        }

        if (result.getChanceExtraRoll() > 0.0D) {
            for (int copy = 0; copy < Math.max(1, parallel); copy++) {
                for (ChancedItemOutput output : recipe.getChancedOutputs().getChancedEntries()) {
                    ItemStack stack = output.getIngredient();
                    if (!isAmplifiable(stack)) continue;
                    int chance = (int) Math.min(CHANCE_SCALE,
                            Math.ceil(output.getChance() * result.getChanceExtraRoll()));
                    if (chance > 0 && GTValues.RNG.nextInt(CHANCE_SCALE) < chance) {
                        addSplit(extras, stack, stack.getCount());
                    }
                }
            }
        }
        return extras;
    }

    private static boolean isAmplifiable(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (stack.getItem() instanceof ItemFood || stack.getItem() instanceof ItemSeeds) return true;
        OrePrefix prefix = OreDictUnifier.getPrefix(stack);
        return prefix == OrePrefix.ore || prefix == OrePrefix.oreLean || prefix == OrePrefix.rawOre
                || prefix == OrePrefix.crushed || prefix == OrePrefix.crushedPurified
                || prefix == OrePrefix.crushedCentrifuged || prefix == OrePrefix.dust
                || prefix == OrePrefix.dustImpure || prefix == OrePrefix.dustPure || prefix == OrePrefix.dustSmall
                || prefix == OrePrefix.dustTiny || prefix == OrePrefix.gem || prefix == OrePrefix.gemChipped
                || prefix == OrePrefix.gemFlawed || prefix == OrePrefix.gemFlawless || prefix == OrePrefix.ingot
                || prefix == OrePrefix.ingotHot || prefix == OrePrefix.nugget;
    }

    private static String fractionKey(Recipe recipe, int index) {
        return "r" + Integer.toHexString(recipe.hashCode()) + "o" + index;
    }

    private static int safeCount(double amount) {
        return amount <= 0.0D ? 0 : amount >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
    }

    private static void addSplit(List<ItemStack> target, ItemStack source, int count) {
        while (count > 0) {
            int amount = Math.min(count, source.getMaxStackSize());
            ItemStack copy = source.copy();
            copy.setCount(amount);
            target.add(copy);
            count -= amount;
        }
    }
}
