package meowmel.pollution.api.capability.ipml;

import gregtech.api.GTValues;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.recipes.logic.CrossRecipeParallelScheduler;
import gregtech.api.recipes.logic.OCParams;
import gregtech.api.recipes.logic.OCResult;
import gregtech.api.recipes.logic.RecipeExecutionContext;
import gregtech.api.recipes.logic.RecipeSlot;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import gregtech.api.util.GTTransferUtils;
import meowmel.pollution.api.amplification.AstralAmplifierSnapshot;
import meowmel.pollution.api.amplification.MagicAmplificationEngine;
import meowmel.pollution.api.amplification.MagicAmplificationResult;
import meowmel.pollution.api.amplification.MagicOutputProcessor;
import meowmel.pollution.api.astral.AstralCrystalNbtHelper;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.recipes.condition.MagicRecipeCondition;
import meowmel.pollution.api.recipes.properties.AstralCondition;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static gregtech.api.recipes.logic.OverclockingLogic.PERFECT_DURATION_FACTOR;
import static gregtech.api.recipes.logic.OverclockingLogic.STD_DURATION_FACTOR;

public class MagicMultiblockRecipeLogic extends MultiblockRecipeLogic {

    MagicRecipeMapMultiblockController metaTileEntity;
    private MagicAmplificationResult pendingAmplification = MagicAmplificationResult.NONE;
    private MagicAmplificationResult pendingAstralAmplification = MagicAmplificationResult.NONE;
    private MagicAmplificationResult activeAmplification = MagicAmplificationResult.NONE;
    private MagicAmplificationResult activeAstralAmplification = MagicAmplificationResult.NONE;
    private int chariotStacks;
    private int lastCompletedRecipeHash;
    private long starAfterglowUntil;
    private boolean pendingNaturalSkyMatch;
    private final Map<String, Double> fractionalOutputRemainders = new HashMap<>();
    private ItemStack pendingCrystalTransformSource = ItemStack.EMPTY;

    @Nullable
    private transient Recipe preparingRecipe;
    @Nullable
    private transient IItemHandlerModifiable preparingInputInventory;
    private transient int preparingParallel;
    private transient boolean preparingFromOverclockPre;
    private final transient Map<RecipePropertyStorage, Recipe> candidateRecipesByStorage = new IdentityHashMap<>();

    public MagicMultiblockRecipeLogic(MagicRecipeMapMultiblockController tileEntity) {
        super(tileEntity);
        metaTileEntity = tileEntity;
        addRecipeCondition(new MagicRecipeCondition(this, tileEntity));
    }

    @Override
    protected double getOverclockingDurationFactor() {
        Recipe target = preparingRecipe != null ? preparingRecipe : previousRecipe;
        int parallel = preparingParallel > 0 ? preparingParallel : Math.max(1, parallelRecipesPerformed);
        int visPerCraft = computeVisPerCraft(target, parallel, getAmplificationBeingPrepared());
        return metaTileEntity.consumeVis(visPerCraft, true) ? PERFECT_DURATION_FACTOR : STD_DURATION_FACTOR;
    }

    @Override
    public int getEffectiveParallelLimit(@NotNull Recipe recipe,
                                         @NotNull IItemHandlerModifiable inputs,
                                         @NotNull IMultipleTankHandler fluidInputs) {
        if (isCrystalTransformRecipe(recipe)) {
            return 1;
        }
        registerCandidateRecipe(recipe);
        Recipe previousPreparing = this.preparingRecipe;
        MagicAmplificationResult previousPending = this.pendingAmplification;
        try {
            this.preparingRecipe = recipe;
            this.pendingAmplification = calculateAmplification(recipe);
            return getParallelLimit();
        } finally {
            this.preparingRecipe = previousPreparing;
            this.pendingAmplification = previousPending;
        }
    }

    @Override
    public boolean prepareRecipe(Recipe recipe, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
        beginPreparing(recipe, 1, inputs);
        try {
            return super.prepareRecipe(recipe, inputs, fluidInputs);
        } finally {
            clearPreparingState();
        }
    }

    @Override
    protected boolean prepareRecipeDistinct(Recipe recipe, IItemHandlerModifiable inputBus,
                                            IMultipleTankHandler inputFluid) {
        beginPreparing(recipe, 1, inputBus);
        try {
            return super.prepareRecipeDistinct(recipe, inputBus, inputFluid);
        } finally {
            clearPreparingState();
        }
    }

    @Override
    public int getParallelLimit() {
        if (preparingRecipe != null && isCrystalTransformRecipe(preparingRecipe)) {
            return 1;
        }
        return Math.max(1, super.getParallelLimit() + getAmplificationBeingPrepared().getExtraParallel());
    }

    @Override
    protected void modifyOverclockPre(OCParams params, RecipePropertyStorage storage) {
        super.modifyOverclockPre(params, storage);
        if (preparingRecipe == null) {
            Recipe candidate = candidateRecipesByStorage.get(storage);
            if (candidate != null) {
                int parallel = candidate.getEUt() > 0 && shouldParallelMultiplyPower()
                        ? (int) Math.max(1L, params.eut() / candidate.getEUt())
                        : 1;
                beginPreparing(candidate, parallel, null);
            } else {
                this.pendingAmplification = calculateAmplification(storage, params.duration());
            }
            this.preparingFromOverclockPre = true;
        }

        MagicAmplificationResult result = pendingAmplification;
        if (!result.isActive()) return;
        params.setDuration(Math.max(1, (int) Math.ceil(params.duration()
                * (1.0D - result.getDurationReduction()))));
        params.setEut(Math.max(1L, (long) Math.ceil(params.eut()
                * (1.0D - result.getEutReduction()))));
    }

    @Override
    protected void modifyOverclockPost(@NotNull OCResult ocResult, @NotNull RecipePropertyStorage storage) {
        super.modifyOverclockPost(ocResult, storage);
        if (preparingFromOverclockPre) {
            clearPreparingState();
        }
    }

    @Override
    protected boolean checkOutputSpaceItems(Recipe recipe, IItemHandlerModifiable outputInventory) {
        // Crystal transforms replace JEI's static placeholder with an NBT-bearing
        // item. Validate that exact output first: otherwise a slot holding a
        // different-quality crystal can be mistaken for a valid stack target.
        if (isCrystalTransformRecipe(recipe)) {
            ItemStack source = !pendingCrystalTransformSource.isEmpty()
                    ? pendingCrystalTransformSource
                    : findCrystalTransformSource(recipe,
                            preparingInputInventory != null ? preparingInputInventory : getInputInventory());
            if (!source.isEmpty()) {
                ItemStack transformed = getCrystalTransformOutput(recipe, source);
                if (transformed.isEmpty() || !GTTransferUtils.addItemsToItemHandler(outputInventory, true,
                        Collections.singletonList(transformed))) {
                    return false;
                }
            }
        }
        if (!super.checkOutputSpaceItems(recipe, outputInventory)) return false;
        MagicAmplificationResult amp = pendingAmplification.isActive()
                ? pendingAmplification : calculateAmplification(recipe);
        List<ItemStack> forecast = MagicOutputProcessor.forecast(recipe, 1, amp);
        return forecast.isEmpty() || GTTransferUtils.addItemsToItemHandler(outputInventory, true, forecast);
    }

    @Override
    protected boolean consumeSingleRecipeInputs(@NotNull Recipe recipe,
                                                @NotNull IItemHandlerModifiable importInventory,
                                                @NotNull IMultipleTankHandler importFluids) {
        return consumeMagicInputs(recipe, importInventory, 1,
                () -> super.consumeSingleRecipeInputs(recipe, importInventory, importFluids));
    }

    @Override
    protected boolean consumeRecipeInputs(Recipe recipe, IItemHandlerModifiable inputInventory,
                                          IMultipleTankHandler inputTanks, int parallel) {
        return consumeMagicInputs(recipe, inputInventory, parallel,
                () -> super.consumeRecipeInputs(recipe, inputInventory, inputTanks, parallel));
    }

    private boolean consumeMagicInputs(Recipe recipe, IItemHandlerModifiable inputInventory,
                                       int parallel, BooleanSupplier superConsumer) {
        if (isCrystalTransformRecipe(recipe)) {
            ItemStack source = findCrystalTransformSource(recipe, inputInventory);
            if (source.isEmpty()) {
                pendingCrystalTransformSource = ItemStack.EMPTY;
                return false;
            }
            pendingCrystalTransformSource = source.copy();
            pendingCrystalTransformSource.setCount(1);
        } else {
            pendingCrystalTransformSource = ItemStack.EMPTY;
        }

        MagicAmplificationResult amp = resolveAmplificationForStart(recipe);
        List<ItemStack> protectedCatalysts = collectProtectedCatalysts(recipe, inputInventory,
                parallel, amp.getCatalystSaveChance());
        boolean consumed = superConsumer.getAsBoolean();
        if (consumed) {
            for (ItemStack catalyst : protectedCatalysts) {
                GTTransferUtils.insertItem(inputInventory, catalyst, false);
            }
        } else {
            pendingCrystalTransformSource = ItemStack.EMPTY;
        }
        return consumed;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        clearPreparingState();
        candidateRecipesByStorage.clear();
        activeAmplification = MagicAmplificationResult.NONE;
        activeAstralAmplification = MagicAmplificationResult.NONE;
        metaTileEntity.setMagicFocusLocked(false);
    }

    public void registerCandidateRecipe(@NotNull Recipe recipe) {
        if (candidateRecipesByStorage.size() > 64) {
            candidateRecipesByStorage.clear();
        }
        candidateRecipesByStorage.put(recipe.propertyStorage(), recipe);
    }

    public MagicAmplificationResult resolveAmplificationForStart(@NotNull Recipe recipe) {
        if (preparingRecipe == recipe && pendingAmplification != null) {
            return pendingAmplification;
        }
        return calculateAmplification(recipe);
    }

    public MagicAmplificationResult resolveAstralAmplificationForStart(@NotNull Recipe recipe) {
        if (preparingRecipe == recipe && pendingAstralAmplification != null) {
            return pendingAstralAmplification;
        }
        return calculateAstralAmplification(recipe);
    }

    public boolean consumePendingNaturalSkyMatch() {
        boolean matched = pendingNaturalSkyMatch;
        pendingNaturalSkyMatch = false;
        return matched;
    }

    public ItemStack consumePendingCrystalTransformSource() {
        ItemStack source = pendingCrystalTransformSource;
        pendingCrystalTransformSource = ItemStack.EMPTY;
        return source;
    }

    public Map<String, Double> getFractionalOutputRemainders() {
        return fractionalOutputRemainders;
    }

    public void onMagicSlotStarted(@NotNull MagicAmplificationResult amplification,
                                   @NotNull MagicAmplificationResult astralAmplification,
                                   int recipeHash, boolean naturalSkyMatch) {
        activeAmplification = amplification;
        activeAstralAmplification = astralAmplification;
        if ("the_star".equals(amplification.getTarot()) && naturalSkyMatch) {
            starAfterglowUntil = getWorldTime() + 200L;
        }
        if (amplification.isActive()) {
            metaTileEntity.setMagicFocusLocked(true);
        }
        clearPreparingState();
    }

    public void syncActiveAmplificationIfEmpty(@NotNull NBTTagCompound conditionTag) {
        if (!activeAmplification.isActive()) {
            activeAmplification = MagicRecipeCondition.readAmplification(conditionTag);
            activeAstralAmplification = MagicRecipeCondition.readAstralAmplification(conditionTag);
        }
    }

    public void recordCompletedMagicRecipe(int completedRecipeHash, @NotNull String tarot) {
        if ("the_chariot".equals(tarot)) {
            chariotStacks = completedRecipeHash == lastCompletedRecipeHash
                    ? Math.min(5, chariotStacks + 1) : 1;
        } else {
            chariotStacks = 0;
        }
        lastCompletedRecipeHash = completedRecipeHash;
    }

    public void refreshActiveAmplificationAndFocusLock(@Nullable RecipeExecutionContext finishedContext) {
        boolean anyAmplified = false;
        MagicAmplificationResult nextActive = MagicAmplificationResult.NONE;
        MagicAmplificationResult nextAstral = MagicAmplificationResult.NONE;

        if (crossRecipeScheduler != null && crossRecipeScheduler.hasActiveSlots()) {
            RecipeSlot finishedSlot = finishedContext != null ? finishedContext.getSlot() : null;
            for (RecipeSlot slot : crossRecipeScheduler.getActiveSlots()) {
                if (slot == finishedSlot || slot.isIdle()) continue;
                NBTTagCompound extra = slot.getExtraData();
                if (!extra.hasKey(MagicRecipeCondition.CONDITION_ID, Constants.NBT.TAG_COMPOUND)) continue;
                NBTTagCompound condTag = extra.getCompoundTag(MagicRecipeCondition.CONDITION_ID);
                MagicAmplificationResult slotAmp = MagicRecipeCondition.readAmplification(condTag);
                if (slotAmp.isActive()) {
                    anyAmplified = true;
                    if (!nextActive.isActive()) {
                        nextActive = slotAmp;
                        nextAstral = MagicRecipeCondition.readAstralAmplification(condTag);
                    }
                }
            }
        }

        activeAmplification = nextActive;
        activeAstralAmplification = nextAstral;
        metaTileEntity.setMagicFocusLocked(anyAmplified);
    }

    private void beginPreparing(@NotNull Recipe recipe, int parallel,
                                @Nullable IItemHandlerModifiable inputInventory) {
        this.preparingRecipe = recipe;
        this.preparingParallel = Math.max(1, parallel);
        this.preparingInputInventory = inputInventory;
        this.pendingAmplification = calculateAmplification(recipe);
        this.pendingAstralAmplification = calculateAstralAmplification(recipe);
    }

    private void clearPreparingState() {
        this.preparingRecipe = null;
        this.preparingInputInventory = null;
        this.preparingParallel = 0;
        this.preparingFromOverclockPre = false;
        this.pendingAmplification = MagicAmplificationResult.NONE;
        this.pendingAstralAmplification = MagicAmplificationResult.NONE;
        this.pendingNaturalSkyMatch = false;
        this.pendingCrystalTransformSource = ItemStack.EMPTY;
    }

    private int computeVisPerCraft(@Nullable Recipe recipe, int parallel, @NotNull MagicAmplificationResult amp) {
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.VIS_PER_CRAFT)) return 1;
        int raw = recipe.getProperty(MagicRecipeProperties.VIS_PER_CRAFT, 0);
        if (raw <= 0) return 0;
        long scaledLong = (long) raw * Math.max(1, parallel);
        int scaled = scaledLong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) scaledLong;
        return Math.max(1, (int) Math.ceil(scaled * (1.0D - amp.getMagicCostReduction())));
    }

    private MagicAmplificationResult calculateAmplification(@NotNull Recipe recipe) {
        AstralAmplifierSnapshot snapshot = getEffectiveAstralSnapshot();
        int stacks = recipe.hashCode() == lastCompletedRecipeHash ? chariotStacks : 0;
        return MagicAmplificationEngine.calculate(metaTileEntity.getMagicProcessTags(recipe), recipe.getDuration(),
                snapshot, metaTileEntity.getTarotHatch(), stacks, super.getParallelLimit() == 1);
    }

    private MagicAmplificationResult calculateAmplification(@NotNull RecipePropertyStorage storage, int duration) {
        AstralAmplifierSnapshot snapshot = getEffectiveAstralSnapshot();
        long tags = storage.contains(MagicRecipeProperties.PROCESS_TAG_MASK)
                ? storage.get(MagicRecipeProperties.PROCESS_TAG_MASK, 0L)
                : metaTileEntity.getMagicProcessTags(null);
        return MagicAmplificationEngine.calculate(tags, duration,
                snapshot, metaTileEntity.getTarotHatch(), 0, super.getParallelLimit() == 1);
    }

    /** Same recipe snapshot with no tarot, used only to expose the card's actual delta in the UI. */
    private MagicAmplificationResult calculateAstralAmplification(@NotNull Recipe recipe) {
        AstralAmplifierSnapshot snapshot = getEffectiveAstralSnapshot();
        int stacks = recipe.hashCode() == lastCompletedRecipeHash ? chariotStacks : 0;
        return MagicAmplificationEngine.calculate(metaTileEntity.getMagicProcessTags(recipe), recipe.getDuration(),
                snapshot, null, stacks, super.getParallelLimit() == 1);
    }

    private MagicAmplificationResult calculatePreviewAmplification() {
        AstralAmplifierSnapshot snapshot = metaTileEntity.getAstralAmplifierSnapshot();
        if (!snapshot.hasDataWafer() && metaTileEntity.getTarotHatch() == null) {
            return MagicAmplificationResult.NONE;
        }
        return MagicAmplificationEngine.calculate(metaTileEntity.getMagicProcessTags(null), 0,
                snapshot, metaTileEntity.getTarotHatch(), 0, super.getParallelLimit() == 1);
    }

    private AstralAmplifierSnapshot getEffectiveAstralSnapshot() {
        AstralAmplifierSnapshot snapshot = metaTileEntity.getAstralAmplifierSnapshot();
        pendingNaturalSkyMatch = snapshot.isSkyMatched();
        String tarot = metaTileEntity.getTarotHatch() == null ? "" : metaTileEntity.getTarotHatch().getActiveTarot();
        if ("the_star".equalsIgnoreCase(tarot) && !snapshot.isSkyMatched()
                && getWorldTime() <= starAfterglowUntil) {
            snapshot = snapshot.withSkyMatched(true);
        }
        return snapshot;
    }

    public static boolean isCrystalTransformRecipe(@Nullable Recipe recipe) {
        return recipe != null && (recipe.hasProperty(MagicRecipeProperties.CRYSTAL_SEED_SELECTION)
                || recipe.hasProperty(MagicRecipeProperties.CRYSTAL_EMBRYO_CULTIVATION)
                || recipe.hasProperty(MagicRecipeProperties.CRYSTAL_CELESTIAL_GROWTH));
    }

    /**
     * GT's generic NBT matcher can expose the catalyst in JEI, but cannot compare
     * Astral Sorcery crystal attributes. Keep that comparison here so quality
     * gates remain authoritative for every magic multiblock recipe map.
     */
    public static boolean hasRequiredCultivatedCrystal(@Nullable Recipe recipe,
                                                       @NotNull IItemHandlerModifiable inventory) {
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.MIN_CULTIVATED_CRYSTAL_QUALITY)) {
            return true;
        }
        int minimumQuality = recipe.getProperty(MagicRecipeProperties.MIN_CULTIVATED_CRYSTAL_QUALITY, 0);
        if (minimumQuality <= 0) return true;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (AstralCrystalNbtHelper.isCultivatedCrystal(stack)
                    && AstralCrystalNbtHelper.getOpticalQuality(stack) >= minimumQuality) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public static ItemStack findCrystalTransformSource(@NotNull Recipe recipe,
                                                       @NotNull IItemHandlerModifiable inventory) {
        boolean seedSelection = recipe.hasProperty(MagicRecipeProperties.CRYSTAL_SEED_SELECTION);
        boolean embryoCultivation = recipe.hasProperty(MagicRecipeProperties.CRYSTAL_EMBRYO_CULTIVATION);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (seedSelection ? AstralCrystalNbtHelper.isEligibleRockCrystal(stack)
                    : embryoCultivation ? AstralCrystalNbtHelper.isCrystalSeed(stack)
                    : AstralCrystalNbtHelper.isCrystalEmbryo(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public void applyCrystalTransformOutput(@NotNull Recipe recipe,
                                            @NotNull ItemStack source,
                                            @NotNull List<ItemStack> targetOutputs) {
        if (!isCrystalTransformRecipe(recipe) || source.isEmpty()) return;
        ItemStack transformed = getCrystalTransformOutput(recipe, source);
        if (transformed.isEmpty()) return;

        ItemStack placeholder;
        if (recipe.hasProperty(MagicRecipeProperties.CRYSTAL_SEED_SELECTION)) {
            placeholder = PollutionMetaItems.ROCK_CRYSTAL_SEED.getStackForm();
        } else if (recipe.hasProperty(MagicRecipeProperties.CRYSTAL_EMBRYO_CULTIVATION)) {
            placeholder = PollutionMetaItems.CELESTIAL_CRYSTAL_EMBRYO.getStackForm();
        } else {
            placeholder = PollutionMetaItems.CULTIVATED_CRYSTAL.getStackForm();
        }
        for (int index = 0; index < targetOutputs.size(); index++) {
            if (ItemStack.areItemsEqual(targetOutputs.get(index), placeholder)) {
                targetOutputs.set(index, transformed);
                return;
            }
        }
        targetOutputs.add(transformed);
    }

    private static ItemStack getCrystalTransformOutput(@Nullable Recipe recipe, @NotNull ItemStack source) {
        if (recipe == null || source.isEmpty()) return ItemStack.EMPTY;
        if (recipe.hasProperty(MagicRecipeProperties.CRYSTAL_SEED_SELECTION)) {
            return AstralCrystalNbtHelper.createSeed(source);
        }
        if (recipe.hasProperty(MagicRecipeProperties.CRYSTAL_EMBRYO_CULTIVATION)) {
            return AstralCrystalNbtHelper.createEmbryo(source);
        }
        AstralCondition condition = recipe.getProperty(MagicRecipeProperties.ASTRAL_CONDITION, AstralCondition.NONE);
        return AstralCrystalNbtHelper.createCultivatedCrystal(source, condition.getConstellation());
    }

    private List<ItemStack> collectProtectedCatalysts(Recipe recipe,
                                                      IItemHandlerModifiable inventory,
                                                      int parallel, double protectionChance) {
        List<ItemStack> protectedStacks = new ArrayList<>();
        if (recipe == null || protectionChance <= 0.0D
                || !recipe.hasProperty(MagicRecipeProperties.CONSUMABLE_CATALYST_INPUTS)) {
            return protectedStacks;
        }
        String configured = recipe.getProperty(MagicRecipeProperties.CONSUMABLE_CATALYST_INPUTS, "");
        for (String token : configured.split(",")) {
            try {
                int index = Integer.parseInt(token.trim());
                if (index < 0 || index >= recipe.getInputs().size()) continue;
                GTRecipeInput input = recipe.getInputs().get(index);
                if (input.isNonConsumable() || GTValues.RNG.nextDouble() >= protectionChance) continue;
                ItemStack source = findMatchingStack(inventory, input);
                if (source == null || source.isEmpty() || isForbiddenCatalyst(source)) continue;
                ItemStack restored = source.copy();
                restored.setCount(Math.max(1, input.getAmount() * Math.max(1, parallel)));
                protectedStacks.add(restored);
            } catch (NumberFormatException ignored) {
                // Invalid pack data must not block an otherwise valid recipe.
            }
        }
        return protectedStacks;
    }

    private static ItemStack findMatchingStack(IItemHandlerModifiable inventory, GTRecipeInput input) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && input.acceptsStack(stack)) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isForbiddenCatalyst(ItemStack stack) {
        return ItemStack.areItemsEqual(stack,
                PollutionMetaItems.CONSTELLATION_DATA_WAFER.getStackForm())
                || ItemStack.areItemsEqual(stack,
                PollutionMetaItems.CELESTIAL_CALIBRATION_CORE.getStackForm())
                || ItemStack.areItemsEqual(stack,
                PollutionMetaItems.ATTUNED_CRYSTAL_WAFER.getStackForm());
    }

    public MagicAmplificationResult getActiveAmplification() {
        return activeAmplification;
    }

    /** The portion of the active total that was contributed by the inserted tarot card. */
    public MagicAmplificationResult getActiveTarotAmplification() {
        return activeAmplification.subtract(activeAstralAmplification);
    }

    protected MagicAmplificationResult getAmplificationBeingPrepared() {
        if (pendingAmplification.isActive()) return pendingAmplification;
        if (activeAmplification.isActive()) return activeAmplification;
        return calculatePreviewAmplification();
    }

    private long getWorldTime() {
        return metaTileEntity.getWorld() == null ? 0L : metaTileEntity.getWorld().getTotalWorldTime();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound data = super.serializeNBT();
        data.setInteger("ChariotStacks", chariotStacks);
        data.setInteger("LastCompletedMagicRecipeHash", lastCompletedRecipeHash);
        data.setLong("StarAfterglowUntil", starAfterglowUntil);
        data.setTag("ActiveAmplificationSnapshot", activeAmplification.serializeSnapshot());
        data.setTag("ActiveAstralOnlyAmplification", activeAstralAmplification.serializeSnapshot());
        NBTTagCompound remainders = new NBTTagCompound();
        for (Map.Entry<String, Double> entry : fractionalOutputRemainders.entrySet()) {
            remainders.setDouble(entry.getKey(), entry.getValue());
        }
        data.setTag("MagicOutputRemainders", remainders);
        return data;
    }

    @Override
    public void deserializeNBT(NBTTagCompound data) {
        super.deserializeNBT(data);
        chariotStacks = Math.max(0, Math.min(5, data.getInteger("ChariotStacks")));
        lastCompletedRecipeHash = data.getInteger("LastCompletedMagicRecipeHash");
        starAfterglowUntil = data.getLong("StarAfterglowUntil");
        activeAmplification = MagicAmplificationResult.deserializeSnapshot(
                data.getCompoundTag("ActiveAmplificationSnapshot"));
        activeAstralAmplification = MagicAmplificationResult.deserializeSnapshot(
                data.getCompoundTag("ActiveAstralOnlyAmplification"));
        if (!activeAmplification.isActive()) {
            refreshActiveAmplificationAndFocusLock(null);
        }
        fractionalOutputRemainders.clear();
        NBTTagCompound remainders = data.getCompoundTag("MagicOutputRemainders");
        for (String key : remainders.getKeySet()) {
            fractionalOutputRemainders.put(key, remainders.getDouble(key));
        }
    }
}
