package keqing.pollution.api.metatileentity;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.api.util.KeyUtil;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.recipes.properties.ManaProperty;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public abstract class POManaMultiblock extends MultiMapMultiblockController {

    int tier;
    int visStorage;
    int visStorageMax;
    double timeReduce;//耗时减免
    double energyReduce;//耗能减免
    int OverclockingEnhance;//超频加强
    int ParallelEnhance;//并行加强

    public POManaMultiblock(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
        this.recipeMapWorkable = new POManaMultiblockRecipeLogic(this);
    }

    public POManaMultiblock(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new POManaMultiblockRecipeLogic(this);
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

    @Override
    public void updateFormedValid() {
        super.updateFormedValid();
        if (!isStructureFormed()) return;
        if (!this.getAbilities(POMultiblockAbility.MANA_HATCH).isEmpty()) {
            this.visStorage = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMana();
            this.energyReduce = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getEnergyReduce();
            this.timeReduce = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTimeReduce();
            this.OverclockingEnhance = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getOverclockingEnhance();
            this.ParallelEnhance = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getParallelEnhance();
        }
    }

    public boolean drainVis(int amount, boolean simulate) {
        return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).consumeMana(amount, simulate);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        // 检查 MANA_HATCH 是否存在
        List<IManaHatch> manaHatches = this.getAbilities(POMultiblockAbility.MANA_HATCH);
        if (manaHatches != null && !manaHatches.isEmpty()) {
            IManaHatch manaHatch = manaHatches.get(0);
            this.tier = manaHatch.getTier();
            this.visStorageMax = manaHatch.getMaxMana();
        }
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(this.getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(this::addHeatCapacity)
                .addCustom(this::addCustomText)
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgress(), recipeMapWorkable.getMaxProgress())
                .addRecipeOutputLine(recipeMapWorkable);
    }


    public void addHeatCapacity(KeyManager keyManager, UISyncer syncer) {

    }

    private void addCustomText(KeyManager keyManager, UISyncer uiSyncer) {
        if (isStructureFormed()) {
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "灵气源: %s / %s（总） 灵气等级: %s", uiSyncer.syncInt(visStorage), uiSyncer.syncInt(visStorageMax), uiSyncer.syncInt(tier)));
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "超频加强: " + uiSyncer.syncInt(OverclockingEnhance) + " 耗时减免: " + uiSyncer.syncDouble(timeReduce)));
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "并行加强: " + uiSyncer.syncInt(ParallelEnhance) + " 耗能减免: " + uiSyncer.syncDouble(energyReduce)));
        }
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TextFormatting.GREEN + I18n.format("-魔力支持："));
        tooltip.add(TextFormatting.GREEN +I18n.format("需要使用 §c魔力仓§r 为多方块提供魔力支持"));
        tooltip.add(TextFormatting.GREEN +I18n.format("并行不叠算魔力消耗（只消耗当前配方单次的量"));
        tooltip.add(TextFormatting.GREEN +I18n.format("升级魔力仓为多方块带来额外的并行数量"));
        tooltip.add(TextFormatting.GREEN +I18n.format("在魔力仓内填充升级部件可获得额外的耗时，耗能，超频，并行加强。"));
        tooltip.add(TextFormatting.GREEN +I18n.format("当灵气等级对应电压时大于等于配方电压等级时，获得§b无损超频§7。"));
    }

    @Override
    public boolean isBatchAllowed() {
        return false;
    }

    public class POManaMultiblockRecipeLogic extends MultiblockRecipeLogic {
        POManaMultiblock tileEntity;

        public POManaMultiblockRecipeLogic(POManaMultiblock tileEntity) {
            super(tileEntity);
            this.tileEntity = tileEntity;
        }

        @Override
        public boolean checkRecipe(Recipe recipe) {
            if (drainVis(recipe.getProperty(ManaProperty.getInstance(), 0), true))
                return drainVis(recipe.getProperty(ManaProperty.getInstance(), 0), false);
            return false;
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            super.setMaxProgress((int) (maxProgress * timeReduce));
        }

        @Override
        protected boolean drawEnergy(long EUt, boolean simulate) {
            int recipeEUt = (int) (EUt * energyReduce);
            long resultEnergy = this.getEnergyStored() - (long) recipeEUt;
            if (resultEnergy >= 0L && resultEnergy <= this.getEnergyCapacity()) {
                if (!simulate) {
                    this.getEnergyContainer().changeEnergy(-recipeEUt);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected double getOverclockingDurationFactor() {
            if (GTUtility.getTierByVoltage(this.getMaxVoltage()) <= tier + OverclockingEnhance) {
                return 0.25;
            } else {
                return 0.5;
            }
        }

        @Override
        public int getParallelLimit() {
            return tier;
        }
    }
}
