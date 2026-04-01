package meowmel.pollution.api.metatileentity;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.capability.ipml.ManaMultiblockRecipeLogic;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class ManaMultiblockController extends MultiMapMultiblockController {

    protected List<IManaHatch> manaHatchList;

    public ManaMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
        this.recipeMapWorkable = new ManaMultiblockRecipeLogic(this);
    }

    public ManaMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new ManaMultiblockRecipeLogic(this);
    }

    @Override
    public TraceabilityPredicate autoAbilities() {
        return this.autoAbilities(true, true, true, true, true, true, true);
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn, boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut, boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkMaintenance, checkMuffler);
        if (checkEnergyIn) {
            predicate = predicate.or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2).setPreviewCount(1));
        }

        if (checkItemIn && this.recipeMap.getMaxInputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(1));
        }

        if (checkItemOut && this.recipeMap.getMaxOutputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.EXPORT_ITEMS).setPreviewCount(1));
        }

        if (checkFluidOut && this.recipeMap.getMaxFluidInputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(1));
        }
        if (checkFluidOut && this.recipeMap.getMaxFluidOutputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(1));
        }

        predicate = predicate.or(abilities(POMultiblockAbility.MANA_HATCH).setMaxGlobalLimited(1));
        return predicate;
    }


    public boolean drainVis(int amount, boolean simulate) {
        return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).consumeMana(amount, simulate);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        manaHatchList = this.getAbilities(POMultiblockAbility.MANA_HATCH);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        manaHatchList = new ArrayList<>();
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(this.getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(this::addCustomCapacity)
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgress(), recipeMapWorkable.getMaxProgress())
                .addRecipeOutputLine(recipeMapWorkable);
    }

    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {

    }

    public long getManaCapacity() {
        return manaHatchList.stream().mapToLong(IManaHatch::getMaxMana).sum();
    }

    public long getManaStore() {
        return manaHatchList.stream().mapToLong(IManaHatch::getMana).sum();
    }

    public int getMaxManaHatchTier() {
        return manaHatchList.stream().mapToInt(IManaHatch::getTier).max().orElse(0);
    }

    public long getMaximumOverclockMana() {
        long maxWorkMana = 0;
        for (IManaHatch manaHatch : manaHatchList) {
            maxWorkMana += GTValues.V[manaHatch.getTier()] * manaHatch.getAmp();
        }
        return maxWorkMana;
    }

    public boolean consumeMana(long mana, boolean simulate) {
        if (simulate) {
            return getManaStore() >= mana;
        }
        long manaConsumed = 0;
        for (IManaHatch manaHatch : manaHatchList) {
            long manaToConsume = Math.min(manaHatch.getMaxMana() - manaHatch.getMana(), mana - manaConsumed);
            manaHatch.consumeMana(manaToConsume, true);
            manaConsumed += manaToConsume;
            if (manaConsumed >= mana) {
                return true;
            }
        }
        return false;
    }
}
