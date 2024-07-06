package keqing.pollution.api.capability.ipml;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;

import gregicality.multiblocks.api.capability.IParallelMultiblock;
import gregicality.multiblocks.api.metatileentity.GCYMMultiblockAbility;
import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.common.GCYMConfigHolder;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.capability.IManaMultiblock;
import keqing.pollution.api.metatileentity.POManaMultiblock;

import java.util.List;

public class POManaMultiblockRecipeLogic extends MultiblockRecipeLogic {

    POManaMultiblock tileEntity;

    public POManaMultiblockRecipeLogic(POManaMultiblock tileEntity) {
        super(tileEntity);
        this.tileEntity=tileEntity;
    }

    public int getParallelLimit() {
        return ((IParallelMultiblock)this.metaTileEntity).isParallel() ? ((IParallelMultiblock)this.metaTileEntity).getMaxParallel() : 1;
    }
    @Override
    protected void updateRecipeProgress() {
        if (this.canRecipeProgress && this.drawEnergy((int) (this.recipeEUt*0.75), true)) {
            this.drawEnergy((int) (this.recipeEUt*0.75), false);

            if(tileEntity.work()&&++this.progressTime > this.maxProgressTime) {
                this.completeRecipe();
            }

            if (this.hasNotEnoughEnergy && this.getEnergyInputPerSecond() > 19L * (long)this.recipeEUt*0.75) {
                this.hasNotEnoughEnergy = false;
            }
        } else if (this.recipeEUt > 0) {
            this.hasNotEnoughEnergy = true;
            this.decreaseProgress();
        }

    }

    @Override
    public void setMaxProgress(int maxProgress)
    {
        if(((IParallelMultiblock)this.metaTileEntity).getMaxParallel()<=16) {
            this.maxProgressTime = maxProgress / ((IParallelMultiblock) this.metaTileEntity).getMaxParallel();
            return;
        }
        if(((IParallelMultiblock)this.metaTileEntity).getMaxParallel()<=64){
            this.maxProgressTime = maxProgress*8/((IParallelMultiblock)this.metaTileEntity).getMaxParallel();
            return;
        }
        if(((IParallelMultiblock)this.metaTileEntity).getMaxParallel()<=256){
            this.maxProgressTime = maxProgress*64/((IParallelMultiblock)this.metaTileEntity).getMaxParallel();
            return;
        }
        if(((IParallelMultiblock)this.metaTileEntity).getMaxParallel()<=1024){
            this.maxProgressTime = maxProgress*384/((IParallelMultiblock)this.metaTileEntity).getMaxParallel();
        }
        else this.maxProgressTime = maxProgress;
    }
    @Override
    public  RecipeMapMultiblockController getMetaTileEntity() {
        return (RecipeMapMultiblockController) super.getMetaTileEntity();
    }
}