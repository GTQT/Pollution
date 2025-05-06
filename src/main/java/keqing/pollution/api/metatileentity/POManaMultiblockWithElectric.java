package keqing.pollution.api.metatileentity;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import keqing.pollution.api.capability.IManaHatch;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class POManaMultiblockWithElectric extends RecipeMapMultiblockController{

    public IManaHatch ManaHatch;
    public int tier;
    double timeReduce;//耗时减免
    double energyReduce;//耗能减免
    int OverclockingEnhance;//超频加强
    int ParallelEnhance;//并行加强

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("tier", this.tier);
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        this.tier = data.getInteger("tier");
        super.readFromNBT(data);
    }
    public POManaMultiblockWithElectric(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId, recipeMap);
        this.recipeMapWorkable = new POManaMultiblockWithElectricRecipeLogic(this);
    }

    @Override
    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(this.getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(this.allowSameFluidFillForOutputs(), this.getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(this.getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.outputFluidInventory = new FluidTankList(this.allowSameFluidFillForOutputs(), this.getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        List<IEnergyContainer> energyContainer = new ArrayList<>(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
        energyContainer.addAll(this.getAbilities(MultiblockAbility.INPUT_LASER));
        this.energyContainer = new EnergyContainerList(energyContainer);
    }
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.ManaHatch = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0);
        tier = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTier();
    }
    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.mana_multiblock_with_electric.tooltip.1"));
        tooltip.add(I18n.format("pollution.mana_multiblock_with_electric.tooltip.2"));
        tooltip.add(I18n.format("pollution.mana_multiblock_with_electric.tooltip.3"));
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("灵气源: %s / %s（总） 灵气等级: %s", getMana(), getMaxMana(), tier));
            textList.add(new TextComponentTranslation("超频加强: " + OverclockingEnhance + " 耗时减免: " + timeReduce));
            textList.add(new TextComponentTranslation("并行加强: " + ParallelEnhance + " 耗能减免: " + energyReduce));
        }

    }

    public int getMana() {
        return ManaHatch.getMana();
    }

    public int getMaxMana() {
        return ManaHatch.getMaxMana();
    }

    @Override
    public void updateFormedValid() {
        super.updateFormedValid();
        if (!isStructureFormed()) return;
        if (!this.getAbilities(POMultiblockAbility.VIS_HATCH).isEmpty()) {
            this.energyReduce = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getEnergyReduce();
            this.timeReduce = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTimeReduce();
            this.OverclockingEnhance = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getOverclockingEnhance();
            this.ParallelEnhance = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getParallelEnhance();
        }
    }
    public boolean drainVis(int amount, boolean simulate) {
        return ManaHatch.consumeMana(amount, simulate);
    }

    public class POManaMultiblockWithElectricRecipeLogic extends MultiblockRecipeLogic {

        POManaMultiblockWithElectric tileEntity;

        public POManaMultiblockWithElectricRecipeLogic(POManaMultiblockWithElectric tileEntity) {
            super(tileEntity);
            this.tileEntity = tileEntity;
        }

        @Override
        public int getParallelLimit() {
            if (tier < 4) return 64;
            else if (tier < 8) return ParallelEnhance * 10 + 64 + (tier - 4) * 64;
            else return ParallelEnhance * 10 + 512;
        }

        @Override
        public void updateRecipeProgress() {
            if (drainVis((int) (20 * Math.pow(2, tier - 1)), true)) {
                if (this.canRecipeProgress && this.drawEnergy(this.recipeEUt, true)) {
                    this.drawEnergy(this.recipeEUt, false);
                    drainVis((int) (20 * Math.pow(2, tier - 1)), false);

                    if (++this.progressTime > this.maxProgressTime) {
                        this.completeRecipe();
                    }
                }
            }
        }

        @Override
        protected boolean drawEnergy(long EUt, boolean simulate) {
            long recipeEUt = EUt / tier;
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
        protected double getOverclockingVoltageFactor() {
            if (GTUtility.getTierByVoltage(this.getMaxVoltage()) <= tier + OverclockingEnhance) {
                return 4.0;
            } else {
                return 2.0;
            }
        }
        @Override
        public void setMaxProgress(int maxProgress) {
            super.setMaxProgress((int) (maxProgress * timeReduce));
        }
    }
}
