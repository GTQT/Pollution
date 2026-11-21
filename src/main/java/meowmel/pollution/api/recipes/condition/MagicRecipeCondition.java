package meowmel.pollution.api.recipes.condition;

import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.logic.IRecipeCondition;
import gregtech.api.recipes.logic.RecipeExecutionContext;
import meowmel.pollution.api.amplification.MagicAmplificationResult;
import meowmel.pollution.api.amplification.MagicOutputProcessor;
import meowmel.pollution.api.capability.ipml.MagicMultiblockRecipeLogic;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.recipes.properties.AstralCondition;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Persisted, slot-safe execution condition for magic multiblocks.
 *
 * <p>All per-recipe / per-slot magic costs, amplification parameters, retention state,
 * and authorization requirements are snapshotted into
 * {@link RecipeExecutionContext#getConditionData(String)} at start so both single-recipe
 * execution and {@code CrossRecipeParallelScheduler} slots survive chunk reloads cleanly.</p>
 */
public class MagicRecipeCondition implements IRecipeCondition {

    public static final String CONDITION_ID = "pollution:magic_recipe";

    private static final String KEY_INFUSED_FLUID_PER_TICK = "InfusedFluidPerTick";
    private static final String KEY_MANA_PER_TICK = "ManaPerTick";
    private static final String KEY_LIFE_ESSENCE_PER_TICK = "LifeEssencePerTick";
    private static final String KEY_VIS_PER_CRAFT = "VisPerCraft";
    private static final String KEY_HAS_EXPLICIT_VIS = "HasExplicitVis";
    private static final String KEY_EXPLICIT_VIS_PAID = "ExplicitVisPaid";
    private static final String KEY_RETENTION_LIMIT = "RetentionLimit";
    private static final String KEY_RETENTION_TICKS = "RetentionTicks";
    private static final String KEY_RECIPE_HASH = "RecipeHash";
    private static final String KEY_TAROT = "Tarot";
    private static final String KEY_REQUIRED_TAROT = "RequiredTarot";
    private static final String KEY_PROCESS_TAGS = "ProcessTags";
    private static final String KEY_ASTRAL_CONDITION = "AstralCondition";
    private static final String KEY_AMPLIFICATION = "Amplification";
    private static final String KEY_ASTRAL_AMPLIFICATION = "AstralAmplification";
    private static final String KEY_ACTIVE_AMPLIFICATION = "HasActiveAmplification";

    private final MagicMultiblockRecipeLogic logic;
    private final MagicRecipeMapMultiblockController controller;
    private int reservedInfusedFluid;
    private long reservedMana;
    private int reservedLifeEssence;
    private int reservedVis;

    public MagicRecipeCondition(@NotNull MagicMultiblockRecipeLogic logic,
                                @NotNull MagicRecipeMapMultiblockController controller) {
        this.logic = logic;
        this.controller = controller;
    }

    @Override
    @NotNull
    public String getConditionId() {
        return CONDITION_ID;
    }

    @Override
    public boolean canStart(@NotNull Recipe recipe,
                            @NotNull IItemHandlerModifiable importInventory,
                            @NotNull IMultipleTankHandler importFluids) {
        if (!controller.checkMagicRequirements(recipe)) {
            return false;
        }
        if (!MagicMultiblockRecipeLogic.hasRequiredCultivatedCrystal(recipe, importInventory)) {
            return false;
        }
        if (MagicMultiblockRecipeLogic.isCrystalTransformRecipe(recipe)
                && MagicMultiblockRecipeLogic.findCrystalTransformSource(recipe, importInventory).isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void onRecipeStart(@NotNull RecipeExecutionContext context) {
        Recipe recipe = context.getRecipe();
        if (recipe == null) return;

        MagicAmplificationResult amplification = logic.resolveAmplificationForStart(recipe);
        MagicAmplificationResult astralAmplification = logic.resolveAstralAmplificationForStart(recipe);
        boolean naturalSkyMatch = logic.consumePendingNaturalSkyMatch();
        ItemStack crystalSource = logic.consumePendingCrystalTransformSource();

        int perTickParallel = Math.max(1, context.getParallelCount());
        int totalOps = Math.max(1, context.getTotalOperations());

        boolean hasExplicitVis = recipe.hasProperty(MagicRecipeProperties.VIS_PER_CRAFT)
                && recipe.getProperty(MagicRecipeProperties.VIS_PER_CRAFT, 0) > 0;
        int infusedFluidPerTick = !recipe.hasProperty(MagicRecipeProperties.INFUSED_FLUID_PER_TICK)
                ? 1
                : applyMagicDiscount(scaleInt(recipe.getProperty(MagicRecipeProperties.INFUSED_FLUID_PER_TICK, 0),
                        perTickParallel), amplification);
        long manaPerTick = !recipe.hasProperty(MagicRecipeProperties.MANA_PER_TICK)
                ? 0L
                : applyMagicDiscount(scaleLong(recipe.getProperty(MagicRecipeProperties.MANA_PER_TICK, 0L),
                        perTickParallel), amplification);
        int lifeEssencePerTick = !recipe.hasProperty(MagicRecipeProperties.LIFE_ESSENCE_PER_TICK)
                ? 0
                : applyMagicDiscount(scaleInt(recipe.getProperty(MagicRecipeProperties.LIFE_ESSENCE_PER_TICK, 0),
                        perTickParallel), amplification);
        int visPerCraft = !recipe.hasProperty(MagicRecipeProperties.VIS_PER_CRAFT)
                ? 1
                : applyMagicDiscount(scaleInt(recipe.getProperty(MagicRecipeProperties.VIS_PER_CRAFT, 0),
                        totalOps), amplification);

        NBTTagCompound tag = context.getConditionData(CONDITION_ID);
        tag.setInteger(KEY_INFUSED_FLUID_PER_TICK, infusedFluidPerTick);
        tag.setLong(KEY_MANA_PER_TICK, manaPerTick);
        tag.setInteger(KEY_LIFE_ESSENCE_PER_TICK, lifeEssencePerTick);
        tag.setInteger(KEY_VIS_PER_CRAFT, visPerCraft);
        tag.setBoolean(KEY_HAS_EXPLICIT_VIS, hasExplicitVis);
        tag.setBoolean(KEY_EXPLICIT_VIS_PAID, false);
        tag.setInteger(KEY_RETENTION_LIMIT, amplification.getProgressRetentionTicks());
        tag.setInteger(KEY_RETENTION_TICKS, 0);
        int recipeHash = recipe.hashCode();
        tag.setInteger(KEY_RECIPE_HASH, recipeHash);
        tag.setString(KEY_TAROT, amplification.getTarot());
        tag.setString(KEY_REQUIRED_TAROT, recipe.getProperty(MagicRecipeProperties.TAROT, ""));
        tag.setLong(KEY_PROCESS_TAGS, controller.getMagicProcessTags(recipe));
        AstralCondition astralCondition = recipe.getProperty(MagicRecipeProperties.ASTRAL_CONDITION,
                AstralCondition.NONE);
        if (astralCondition.isConfigured()) {
            tag.setTag(KEY_ASTRAL_CONDITION, MagicRecipeProperties.ASTRAL_CONDITION.serialize(astralCondition));
        }
        tag.setTag(KEY_AMPLIFICATION, amplification.serializeSnapshot());
        tag.setTag(KEY_ASTRAL_AMPLIFICATION, astralAmplification.serializeSnapshot());
        tag.setBoolean(KEY_ACTIVE_AMPLIFICATION, amplification.isActive());

        // Adjust mutable output list for dynamic crystal transforms and amplification bonuses
        if (!crystalSource.isEmpty()) {
            logic.applyCrystalTransformOutput(recipe, crystalSource, context.getItemOutputs());
        }
        int outputParallel = context.isSchedulerSlot() ? totalOps : 1;
        List<ItemStack> bonusOutputs = MagicOutputProcessor.settle(recipe, outputParallel,
                amplification, logic.getFractionalOutputRemainders());
        if (!bonusOutputs.isEmpty()) {
            context.getItemOutputs().addAll(bonusOutputs);
        }

        logic.onMagicSlotStarted(amplification, astralAmplification, recipeHash, naturalSkyMatch);
    }

    @Override
    public void beginTick() {
        reservedInfusedFluid = 0;
        reservedMana = 0L;
        reservedLifeEssence = 0;
        reservedVis = 0;
    }

    @Override
    public boolean testTick(@NotNull RecipeExecutionContext context) {
        NBTTagCompound tag = context.getConditionData(CONDITION_ID);
        if (!checkRequirements(tag)) {
            return false;
        }
        int infusedFluidPerTick = tag.getInteger(KEY_INFUSED_FLUID_PER_TICK);
        long manaPerTick = tag.getLong(KEY_MANA_PER_TICK);
        int lifeEssencePerTick = tag.getInteger(KEY_LIFE_ESSENCE_PER_TICK);
        int visCost = getVisCostForTick(tag);

        return (infusedFluidPerTick <= 0 || controller.drainInfusedFluid(saturatingAddInt(reservedInfusedFluid, infusedFluidPerTick), true))
                && (manaPerTick <= 0L || controller.consumeMana(saturatingAddLong(reservedMana, manaPerTick), true))
                && (lifeEssencePerTick <= 0 || controller.consumeLifeEssence(saturatingAddInt(reservedLifeEssence, lifeEssencePerTick), true))
                && (visCost <= 0 || controller.consumeVis(saturatingAddInt(reservedVis, visCost), true));
    }

    @Override
    public void reserveTick(@NotNull RecipeExecutionContext context) {
        NBTTagCompound tag = context.getConditionData(CONDITION_ID);
        int infusedFluidPerTick = tag.getInteger(KEY_INFUSED_FLUID_PER_TICK);
        long manaPerTick = tag.getLong(KEY_MANA_PER_TICK);
        int lifeEssencePerTick = tag.getInteger(KEY_LIFE_ESSENCE_PER_TICK);
        int visCost = getVisCostForTick(tag);

        if (infusedFluidPerTick > 0) {
            reservedInfusedFluid = saturatingAddInt(reservedInfusedFluid, infusedFluidPerTick);
        }
        if (manaPerTick > 0L) {
            reservedMana = saturatingAddLong(reservedMana, manaPerTick);
        }
        if (lifeEssencePerTick > 0) {
            reservedLifeEssence = saturatingAddInt(reservedLifeEssence, lifeEssencePerTick);
        }
        if (visCost > 0) {
            reservedVis = saturatingAddInt(reservedVis, visCost);
        }
    }

    @Override
    public void consumeTick(@NotNull RecipeExecutionContext context) {
        NBTTagCompound tag = context.getConditionData(CONDITION_ID);
        int infusedFluidPerTick = tag.getInteger(KEY_INFUSED_FLUID_PER_TICK);
        long manaPerTick = tag.getLong(KEY_MANA_PER_TICK);
        int lifeEssencePerTick = tag.getInteger(KEY_LIFE_ESSENCE_PER_TICK);
        int visPerCraft = tag.getInteger(KEY_VIS_PER_CRAFT);
        boolean hasExplicitVis = tag.getBoolean(KEY_HAS_EXPLICIT_VIS);

        controller.drainInfusedFluid(infusedFluidPerTick, false);
        controller.consumeMana(manaPerTick, false);
        controller.consumeLifeEssence(lifeEssencePerTick, false);
        if (hasExplicitVis && !tag.getBoolean(KEY_EXPLICIT_VIS_PAID)) {
            if (controller.consumeVis(visPerCraft, false)) {
                tag.setBoolean(KEY_EXPLICIT_VIS_PAID, true);
            }
        }
        tag.setInteger(KEY_RETENTION_TICKS, 0);

        if (tag.getBoolean(KEY_ACTIVE_AMPLIFICATION)) {
            controller.setMagicFocusLocked(true);
            logic.syncActiveAmplificationIfEmpty(tag);
        }
    }

    @Override
    @NotNull
    public TickFailureAction onTickFailed(@NotNull RecipeExecutionContext context, boolean energyShortage) {
        NBTTagCompound tag = context.getConditionData(CONDITION_ID);
        int retentionLimit = tag.getInteger(KEY_RETENTION_LIMIT);
        int retentionTicks = tag.getInteger(KEY_RETENTION_TICKS);
        if (retentionTicks < retentionLimit) {
            tag.setInteger(KEY_RETENTION_TICKS, retentionTicks + 1);
            return TickFailureAction.PAUSE;
        }
        return TickFailureAction.DECREASE_PROGRESS;
    }

    @Override
    public void onRecipeComplete(@NotNull RecipeExecutionContext context) {
        NBTTagCompound tag = context.getConditionData(CONDITION_ID);
        if (!tag.getBoolean(KEY_HAS_EXPLICIT_VIS)) {
            int visPerCraft = tag.getInteger(KEY_VIS_PER_CRAFT);
            if (visPerCraft > 0) {
                controller.consumeVis(visPerCraft, false);
            }
        }
        int completedRecipeHash = tag.getInteger(KEY_RECIPE_HASH);
        String tarot = tag.getString(KEY_TAROT);
        logic.recordCompletedMagicRecipe(completedRecipeHash, tarot);
        logic.refreshActiveAmplificationAndFocusLock(context);
    }

    @Override
    public void onInvalidate(@Nullable RecipeExecutionContext context) {
        logic.refreshActiveAmplificationAndFocusLock(context);
    }

    public static boolean hasActiveAmplification(@NotNull NBTTagCompound extraData) {
        if (!extraData.hasKey(CONDITION_ID, Constants.NBT.TAG_COMPOUND)) return false;
        return extraData.getCompoundTag(CONDITION_ID).getBoolean(KEY_ACTIVE_AMPLIFICATION);
    }

    public static MagicAmplificationResult readAmplification(@NotNull NBTTagCompound conditionTag) {
        if (!conditionTag.hasKey(KEY_AMPLIFICATION, Constants.NBT.TAG_COMPOUND)) {
            return MagicAmplificationResult.NONE;
        }
        return MagicAmplificationResult.deserializeSnapshot(conditionTag.getCompoundTag(KEY_AMPLIFICATION));
    }

    public static MagicAmplificationResult readAstralAmplification(@NotNull NBTTagCompound conditionTag) {
        if (!conditionTag.hasKey(KEY_ASTRAL_AMPLIFICATION, Constants.NBT.TAG_COMPOUND)) {
            return MagicAmplificationResult.NONE;
        }
        return MagicAmplificationResult.deserializeSnapshot(conditionTag.getCompoundTag(KEY_ASTRAL_AMPLIFICATION));
    }

    private boolean checkRequirements(@NotNull NBTTagCompound tag) {
        long manaPerTick = tag.getLong(KEY_MANA_PER_TICK);
        int lifeEssencePerTick = tag.getInteger(KEY_LIFE_ESSENCE_PER_TICK);
        boolean hasExplicitVis = tag.getBoolean(KEY_HAS_EXPLICIT_VIS);
        String requiredTarot = tag.getString(KEY_REQUIRED_TAROT);
        long processTags = tag.getLong(KEY_PROCESS_TAGS);
        AstralCondition astralCondition = tag.hasKey(KEY_ASTRAL_CONDITION, Constants.NBT.TAG_COMPOUND)
                ? (AstralCondition) MagicRecipeProperties.ASTRAL_CONDITION.deserialize(
                        tag.getCompoundTag(KEY_ASTRAL_CONDITION))
                : AstralCondition.NONE;
        return controller.checkMagicRequirements(manaPerTick, lifeEssencePerTick, hasExplicitVis,
                astralCondition, requiredTarot, processTags);
    }

    private static int getVisCostForTick(@NotNull NBTTagCompound tag) {
        if (!tag.getBoolean(KEY_HAS_EXPLICIT_VIS) || tag.getBoolean(KEY_EXPLICIT_VIS_PAID)) {
            return 0;
        }
        return Math.max(0, tag.getInteger(KEY_VIS_PER_CRAFT));
    }

    private static int saturatingAddInt(int a, int b) {
        if (b <= 0) return a;
        long sum = (long) a + (long) b;
        return sum >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) sum;
    }

    private static long saturatingAddLong(long a, long b) {
        if (b <= 0L) return a;
        return a > Long.MAX_VALUE - b ? Long.MAX_VALUE : a + b;
    }

    private static int scaleInt(int amount, int multiplier) {
        if (amount <= 0) return 0;
        long result = (long) amount * Math.max(1, multiplier);
        return result > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result;
    }

    private static long scaleLong(long amount, int multiplier) {
        if (amount <= 0L) return 0L;
        int mult = Math.max(1, multiplier);
        return amount > Long.MAX_VALUE / mult ? Long.MAX_VALUE : amount * mult;
    }

    private static int applyMagicDiscount(int amount, @NotNull MagicAmplificationResult amplification) {
        if (amount <= 0) return 0;
        return Math.max(1, (int) Math.ceil(amount * (1.0D - amplification.getMagicCostReduction())));
    }

    private static long applyMagicDiscount(long amount, @NotNull MagicAmplificationResult amplification) {
        if (amount <= 0L) return 0L;
        return Math.max(1L, (long) Math.ceil(amount * (1.0D - amplification.getMagicCostReduction())));
    }
}
