package meowmel.pollution.api.capability.ipml;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.recipes.logic.OCParams;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import meowmel.pollution.api.amplification.AstralAmplifierSnapshot;
import meowmel.pollution.api.amplification.MagicAmplificationEngine;
import meowmel.pollution.api.amplification.MagicAmplificationResult;
import meowmel.pollution.api.amplification.MagicOutputProcessor;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gregtech.api.recipes.logic.OverclockingLogic.PERFECT_DURATION_FACTOR;
import static gregtech.api.recipes.logic.OverclockingLogic.STD_DURATION_FACTOR;

public class MagicMultiblockRecipeLogic extends MultiblockRecipeLogic {

    MagicRecipeMapMultiblockController metaTileEntity;
    private boolean explicitVisPaid;
    private MagicAmplificationResult pendingAmplification = MagicAmplificationResult.NONE;
    private MagicAmplificationResult activeAmplification = MagicAmplificationResult.NONE;
    private int chariotStacks;
    private int activeRecipeHash;
    private int lastCompletedRecipeHash;
    private long starAfterglowUntil;
    private boolean pendingNaturalSkyMatch;
    private final Map<String, Double> fractionalOutputRemainders = new HashMap<>();
    private int progressRetentionTicks;

    public MagicMultiblockRecipeLogic(MagicRecipeMapMultiblockController tileEntity) {
        super(tileEntity);
        metaTileEntity = tileEntity;
    }

    /**
     * Update the current running recipe's progress
     * <p>
     * Also handles consuming running heat by default
     * </p>
     */
    protected void updateRecipeProgress() {
        int infusedFluidPerTick = getInfusedFluidPerTick();
        long manaPerTick = getManaPerTick();
        int lifeEssencePerTick = getLifeEssencePerTick();
        int visPerCraft = getVisPerCraft();
        boolean hasMagicRequirements = previousRecipe == null || metaTileEntity.checkMagicRequirements(previousRecipe);
        boolean canPayExplicitVis = !hasExplicitVisCost() || explicitVisPaid
                || metaTileEntity.consumeVis(visPerCraft, true);

        if (canRecipeProgress && hasMagicRequirements && drawEnergy(recipeEUt, true)
                && metaTileEntity.drainInfusedFluid(infusedFluidPerTick, true)
                && metaTileEntity.consumeMana(manaPerTick, true)
                && metaTileEntity.consumeLifeEssence(lifeEssencePerTick, true)
                && canPayExplicitVis) {
            drawEnergy(recipeEUt, false);
            metaTileEntity.drainInfusedFluid(infusedFluidPerTick, false);
            metaTileEntity.consumeMana(manaPerTick, false);
            metaTileEntity.consumeLifeEssence(lifeEssencePerTick, false);
            if (hasExplicitVisCost() && !explicitVisPaid) {
                metaTileEntity.consumeVis(visPerCraft, false);
                explicitVisPaid = true;
            }
            // as recipe starts with progress on 1 this has to be > only not => to compensate for it
            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
            progressRetentionTicks = 0;
            if (this.hasNotEnoughEnergy && getEnergyInputPerSecond() > 19L * recipeEUt) {
                this.hasNotEnoughEnergy = false;
            }
        } else if (recipeEUt > 0) {
            // only set hasNotEnoughEnergy if this recipe is consuming recipe
            // generators always have enough energy
            this.hasNotEnoughEnergy = true;
            if (progressRetentionTicks < activeAmplification.getProgressRetentionTicks()) {
                progressRetentionTicks++;
            } else {
                decreaseProgress();
            }
        }
    }


    @Override
    protected double getOverclockingDurationFactor() {
        return metaTileEntity.consumeVis(getVisPerCraft(), true) ? PERFECT_DURATION_FACTOR : STD_DURATION_FACTOR;
    }

    @Override
    public boolean prepareRecipe(Recipe recipe, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
        pendingAmplification = calculateAmplification(recipe);
        boolean prepared = super.prepareRecipe(recipe, inputs, fluidInputs);
        if (prepared) {
            activeAmplification = pendingAmplification;
            activeRecipeHash = recipe.hashCode();
            if ("the_star".equals(activeAmplification.getTarot()) && pendingNaturalSkyMatch) {
                starAfterglowUntil = getWorldTime() + 200L;
            }
            if (activeAmplification.isActive()) metaTileEntity.setMagicFocusLocked(true);
        } else {
            pendingAmplification = MagicAmplificationResult.NONE;
            pendingNaturalSkyMatch = false;
        }
        return prepared;
    }

    @Override
    public int getParallelLimit() {
        MagicAmplificationResult result = pendingAmplification.isActive()
                ? pendingAmplification : activeAmplification;
        return Math.max(1, super.getParallelLimit() + result.getExtraParallel());
    }

    @Override
    protected void modifyOverclockPre(OCParams params, RecipePropertyStorage storage) {
        super.modifyOverclockPre(params, storage);
        MagicAmplificationResult result = pendingAmplification;
        if (!result.isActive()) return;
        params.setDuration(Math.max(1, (int) Math.ceil(params.duration()
                * (1.0D - result.getDurationReduction()))));
        params.setEut(Math.max(1L, (long) Math.ceil(params.eut()
                * (1.0D - result.getEutReduction()))));
    }

    @Override
    protected boolean checkOutputSpaceItems(Recipe recipe, IItemHandlerModifiable outputInventory) {
        if (!super.checkOutputSpaceItems(recipe, outputInventory)) return false;
        List<net.minecraft.item.ItemStack> forecast = MagicOutputProcessor.forecast(recipe, getParallelLimit(),
                pendingAmplification);
        return forecast.isEmpty() || GTTransferUtils.addItemsToItemHandler(outputInventory, true, forecast);
    }

    @Override
    protected void setupRecipe(Recipe recipe) {
        super.setupRecipe(recipe);
        List<net.minecraft.item.ItemStack> bonusOutputs = MagicOutputProcessor.settle(recipe, parallelRecipesPerformed,
                pendingAmplification, fractionalOutputRemainders);
        if (!bonusOutputs.isEmpty()) itemOutputs.addAll(bonusOutputs);
    }

    @Override
    protected boolean consumeRecipeInputs(Recipe recipe, IItemHandlerModifiable inputInventory,
                                          IMultipleTankHandler inputTanks, int parallel) {
        List<net.minecraft.item.ItemStack> protectedCatalysts = collectProtectedCatalysts(recipe, inputInventory,
                parallel, pendingAmplification.getCatalystSaveChance());
        boolean consumed = super.consumeRecipeInputs(recipe, inputInventory, inputTanks, parallel);
        if (consumed) {
            for (net.minecraft.item.ItemStack catalyst : protectedCatalysts) {
                GTTransferUtils.insertItem(inputInventory, catalyst, false);
            }
        }
        return consumed;
    }

    @Override
    protected void completeRecipe() {
        int visPerCraft = getVisPerCraft();
        boolean shouldChargeLegacyVis = !hasExplicitVisCost();
        int completedRecipeHash = activeRecipeHash;
        super.completeRecipe();
        if (shouldChargeLegacyVis) {
            metaTileEntity.consumeVis(visPerCraft, false);
        }
        if ("the_chariot".equals(activeAmplification.getTarot())) {
            chariotStacks = completedRecipeHash == lastCompletedRecipeHash
                    ? Math.min(5, chariotStacks + 1) : 1;
        } else {
            chariotStacks = 0;
        }
        lastCompletedRecipeHash = completedRecipeHash;
        explicitVisPaid = false;
        activeAmplification = MagicAmplificationResult.NONE;
        pendingAmplification = MagicAmplificationResult.NONE;
        pendingNaturalSkyMatch = false;
        progressRetentionTicks = 0;
        metaTileEntity.setMagicFocusLocked(false);
    }

    @Override
    public boolean checkRecipe(Recipe recipe) {
        return super.checkRecipe(recipe) && metaTileEntity.checkMagicRequirements(recipe);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        explicitVisPaid = false;
        pendingAmplification = MagicAmplificationResult.NONE;
        activeAmplification = MagicAmplificationResult.NONE;
        pendingNaturalSkyMatch = false;
        progressRetentionTicks = 0;
        metaTileEntity.setMagicFocusLocked(false);
    }

    /**
     * Legacy magic-machine recipes did not contain property data. Preserve their
     * single 1 mB/t element cost, while new properties scale with the number of
     * parallel operations actually being performed.
     */
    private int getInfusedFluidPerTick() {
        Recipe recipe = previousRecipe;
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.INFUSED_FLUID_PER_TICK)) return 1;
        return applyMagicDiscount(scaleInt(recipe.getProperty(MagicRecipeProperties.INFUSED_FLUID_PER_TICK, 0)));
    }

    private long getManaPerTick() {
        Recipe recipe = previousRecipe;
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.MANA_PER_TICK)) return 0L;
        return applyMagicDiscount(scaleLong(recipe.getProperty(MagicRecipeProperties.MANA_PER_TICK, 0L)));
    }

    private int getLifeEssencePerTick() {
        Recipe recipe = previousRecipe;
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.LIFE_ESSENCE_PER_TICK)) return 0;
        return applyMagicDiscount(scaleInt(recipe.getProperty(MagicRecipeProperties.LIFE_ESSENCE_PER_TICK, 0)));
    }

    private int getVisPerCraft() {
        Recipe recipe = previousRecipe;
        if (recipe == null || !recipe.hasProperty(MagicRecipeProperties.VIS_PER_CRAFT)) return 1;
        return applyMagicDiscount(scaleInt(recipe.getProperty(MagicRecipeProperties.VIS_PER_CRAFT, 0)));
    }

    private boolean hasExplicitVisCost() {
        return previousRecipe != null && previousRecipe.hasProperty(MagicRecipeProperties.VIS_PER_CRAFT)
                && previousRecipe.getProperty(MagicRecipeProperties.VIS_PER_CRAFT, 0) > 0;
    }

    private int getParallelCostMultiplier() {
        return Math.max(1, parallelRecipesPerformed);
    }

    private int scaleInt(int amount) {
        if (amount <= 0) return 0;
        long result = (long) amount * getParallelCostMultiplier();
        return result > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result;
    }

    private long scaleLong(long amount) {
        if (amount <= 0) return 0L;
        int multiplier = getParallelCostMultiplier();
        return amount > Long.MAX_VALUE / multiplier ? Long.MAX_VALUE : amount * multiplier;
    }

    private int applyMagicDiscount(int amount) {
        if (amount <= 0) return 0;
        return Math.max(1, (int) Math.ceil(amount * (1.0D - activeAmplification.getMagicCostReduction())));
    }

    private long applyMagicDiscount(long amount) {
        if (amount <= 0L) return 0L;
        return Math.max(1L, (long) Math.ceil(amount * (1.0D - activeAmplification.getMagicCostReduction())));
    }

    private MagicAmplificationResult calculateAmplification(Recipe recipe) {
        AstralAmplifierSnapshot snapshot = metaTileEntity.getAstralAmplifierSnapshot();
        pendingNaturalSkyMatch = snapshot.isSkyMatched();
        String tarot = metaTileEntity.getTarotHatch() == null ? "" : metaTileEntity.getTarotHatch().getActiveTarot();
        if ("the_star".equalsIgnoreCase(tarot) && !snapshot.isSkyMatched()
                && getWorldTime() <= starAfterglowUntil) {
            snapshot = snapshot.withSkyMatched(true);
        }
        int stacks = recipe != null && recipe.hashCode() == lastCompletedRecipeHash ? chariotStacks : 0;
        return MagicAmplificationEngine.calculate(metaTileEntity.getMagicProcessTags(recipe), recipe.getDuration(),
                snapshot, metaTileEntity.getTarotHatch(), stacks, super.getParallelLimit() == 1);
    }

    private List<net.minecraft.item.ItemStack> collectProtectedCatalysts(Recipe recipe,
                                                                          IItemHandlerModifiable inventory,
                                                                          int parallel, double protectionChance) {
        List<net.minecraft.item.ItemStack> protectedStacks = new ArrayList<>();
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
                if (input.isNonConsumable() || gregtech.api.GTValues.RNG.nextDouble() >= protectionChance) continue;
                net.minecraft.item.ItemStack source = findMatchingStack(inventory, input);
                if (source == null || source.isEmpty() || isForbiddenCatalyst(source)) continue;
                net.minecraft.item.ItemStack restored = source.copy();
                restored.setCount(Math.max(1, input.getAmount() * Math.max(1, parallel)));
                protectedStacks.add(restored);
            } catch (NumberFormatException ignored) {
                // Invalid pack data must not block an otherwise valid recipe.
            }
        }
        return protectedStacks;
    }

    private static net.minecraft.item.ItemStack findMatchingStack(IItemHandlerModifiable inventory,
                                                                    GTRecipeInput input) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            net.minecraft.item.ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && input.acceptsStack(stack)) return stack;
        }
        return net.minecraft.item.ItemStack.EMPTY;
    }

    private static boolean isForbiddenCatalyst(net.minecraft.item.ItemStack stack) {
        return net.minecraft.item.ItemStack.areItemsEqual(stack,
                PollutionMetaItems.CONSTELLATION_DATA_WAFER.getStackForm())
                || net.minecraft.item.ItemStack.areItemsEqual(stack,
                PollutionMetaItems.CELESTIAL_CALIBRATION_CORE.getStackForm())
                || net.minecraft.item.ItemStack.areItemsEqual(stack,
                PollutionMetaItems.ATTUNED_CRYSTAL_WAFER.getStackForm());
    }

    public MagicAmplificationResult getActiveAmplification() {
        return activeAmplification;
    }

    protected MagicAmplificationResult getAmplificationBeingPrepared() {
        return pendingAmplification.isActive() ? pendingAmplification : activeAmplification;
    }

    private long getWorldTime() {
        return metaTileEntity.getWorld() == null ? 0L : metaTileEntity.getWorld().getTotalWorldTime();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound data = super.serializeNBT();
        data.setInteger("ChariotStacks", chariotStacks);
        data.setInteger("ActiveMagicRecipeHash", activeRecipeHash);
        data.setInteger("LastCompletedMagicRecipeHash", lastCompletedRecipeHash);
        data.setLong("StarAfterglowUntil", starAfterglowUntil);
        data.setDouble("ActiveMagicDurationReduction", activeAmplification.getDurationReduction());
        data.setDouble("ActiveMagicEUtReduction", activeAmplification.getEutReduction());
        data.setDouble("ActiveMagicCostReduction", activeAmplification.getMagicCostReduction());
        data.setInteger("ActiveMagicParallel", activeAmplification.getExtraParallel());
        data.setDouble("ActiveMagicStrength", activeAmplification.getStrength());
        data.setDouble("ActiveMagicOutputBonus", activeAmplification.getOutputBonus());
        data.setDouble("ActiveMagicChanceExtraRoll", activeAmplification.getChanceExtraRoll());
        data.setDouble("ActiveMagicCatalystSave", activeAmplification.getCatalystSaveChance());
        data.setDouble("ActiveMagicEnergyEfficiency", activeAmplification.getMagicEnergyEfficiencyBonus());
        data.setInteger("ActiveMagicRetention", activeAmplification.getProgressRetentionTicks());
        data.setInteger("ActiveMagicFurnaceTemperature", activeAmplification.getFurnaceTemperatureBonus());
        data.setInteger("MagicProgressRetentionTicks", progressRetentionTicks);
        data.setString("ActiveMagicConstellation", activeAmplification.getConstellation());
        data.setString("ActiveMagicTarot", activeAmplification.getTarot());
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
        activeRecipeHash = data.getInteger("ActiveMagicRecipeHash");
        lastCompletedRecipeHash = data.getInteger("LastCompletedMagicRecipeHash");
        starAfterglowUntil = data.getLong("StarAfterglowUntil");
        activeAmplification = new MagicAmplificationResult(
                data.getDouble("ActiveMagicDurationReduction"), data.getDouble("ActiveMagicEUtReduction"),
                data.getDouble("ActiveMagicCostReduction"), data.getInteger("ActiveMagicParallel"),
                data.getDouble("ActiveMagicStrength"), data.getDouble("ActiveMagicOutputBonus"),
                data.getDouble("ActiveMagicChanceExtraRoll"), data.getDouble("ActiveMagicCatalystSave"),
                data.getDouble("ActiveMagicEnergyEfficiency"), data.getInteger("ActiveMagicRetention"),
                data.getInteger("ActiveMagicFurnaceTemperature"), data.getString("ActiveMagicConstellation"),
                data.getString("ActiveMagicTarot"));
        progressRetentionTicks = Math.max(0, data.getInteger("MagicProgressRetentionTicks"));
        fractionalOutputRemainders.clear();
        NBTTagCompound remainders = data.getCompoundTag("MagicOutputRemainders");
        for (String key : remainders.getKeySet()) {
            fractionalOutputRemainders.put(key, remainders.getDouble(key));
        }
    }
}
