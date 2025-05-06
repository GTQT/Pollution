package keqing.pollution.api.metatileentity;

import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.Material;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextFormattingUtil;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.capability.IVisHatch;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static keqing.pollution.api.utils.infusedFluidStack.STACK_MAP;

public abstract class PORecipeMapMultiblockController extends MultiMapMultiblockController {

    protected Material material;
    int tier;
    int visStorage;
    int visStorageMax;
    boolean isVisModule = false;
    boolean isManaModule = false;
    int maxParallel;

    double timeReduce;//耗时减免
    double energyReduce;//耗能减免
    int OverclockingEnhance;//超频加强
    int ParallelEnhance;//并行加强

    public PORecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
    }

    public PORecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new POMultiblockRecipeLogic(this);
    }

    protected Material getMaterial() {
        return material;
    }

    protected void setMaterial(Material material) {
        this.material = material;
    }

    public boolean drainMaterial(Material material, Boolean consume) {
        IMultipleTankHandler inputTank = getInputFluidInventory();

        if (material == null) return true;

        FluidStack stack = STACK_MAP.get(material);
        return stack.isFluidStackIdentical(inputTank.drain(stack, consume));
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("多方块工作需要源质：%s，请将液态源质输入设备的输入仓内", material.getLocalizedName()));
        tooltip.add(I18n.format("========灵气模式========"));
        tooltip.add(I18n.format("需要使用 §c灵气仓§r 为多方块提供灵气支持"));
        tooltip.add(I18n.format("多方块工作每tick需要消耗1mb的对应流体要素与4*仓室等级 mb的灵气源"));
        tooltip.add(I18n.format("每128灵气源为多方块带来额外的一并行数量"));
        tooltip.add(I18n.format("当灵气等级对应电压时大于等于配方电压等级时，获得§b无损超频§7。"));
        tooltip.add(I18n.format("========魔力模式========"));
        tooltip.add(I18n.format("需要使用 §c魔力仓§r 为多方块提供魔力支持"));
        tooltip.add(I18n.format("多方块工作每tick需要消耗1mb的对应流体要素与Math.pow(2,仓室等级) mb的灵气源"));
        tooltip.add(I18n.format("每1024灵气源为多方块带来额外的一并行数量"));
        tooltip.add(I18n.format("在魔力仓内填充升级部件可获得额外的耗时，耗能，超频，并行加强。"));
        tooltip.add(I18n.format("当灵气等级对应电压时大于等于配方电压等级时，获得§b无损超频§7。"));
        tooltip.add(I18n.format("注意：如果你同时安装灵气仓与魔力仓，那么机器会优先自动选取二者中较低的数值"));
    }

    @Override
    public void updateFormedValid() {
        super.updateFormedValid();
        if (!isStructureFormed()) return;
        isVisModule=!this.getAbilities(POMultiblockAbility.VIS_HATCH).isEmpty();
        isManaModule=!this.getAbilities(POMultiblockAbility.MANA_HATCH).isEmpty();
        if (isVisModule) {
            this.visStorage = this.getAbilities(POMultiblockAbility.VIS_HATCH).get(0).getVisStore();
            energyReduce=1;
            timeReduce=1;
            OverclockingEnhance=0;
            ParallelEnhance=0;
        }
        if (isManaModule) {
            this.visStorage = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMana();
            this.energyReduce = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getEnergyReduce();
            this.timeReduce = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTimeReduce();
            this.OverclockingEnhance = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getOverclockingEnhance();
            this.ParallelEnhance = this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getParallelEnhance();
        }
    }

    @Override
    public boolean isActive() {
        return this.isStructureFormed()
                && this.recipeMapWorkable.isActive()
                && this.recipeMapWorkable.isWorkingEnabled();
    }

    @Override
    protected void addErrorText(List<ITextComponent> textList) {
        super.addErrorText(textList);
        if (!drainMaterial(material, false)) {
            textList.add(new TextComponentTranslation("缺少源质输入!!!"));
        }
    }

    public boolean drainVis(int amount, boolean simulate) {
        if (isVisModule) return this.getAbilities(POMultiblockAbility.VIS_HATCH).get(0).drainVis(amount * 4, simulate);
        if (isManaModule)
            return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).consumeMana((int) Math.pow(2, amount), simulate);
        return false;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        // 检查 VIS_HATCH 是否存在
        List<IVisHatch> visHatches = this.getAbilities(POMultiblockAbility.VIS_HATCH);
        if (visHatches != null && !visHatches.isEmpty()) {
            IVisHatch visHatch = visHatches.get(0);
            this.tier = visHatch.getTier();
            maxParallel = tier * 8;
            this.visStorageMax = visHatch.getMaxVisStore();
            isVisModule = true;
        }

        // 检查 MANA_HATCH 是否存在
        List<IManaHatch> manaHatches = this.getAbilities(POMultiblockAbility.MANA_HATCH);
        if (manaHatches != null && !manaHatches.isEmpty()) {
            IManaHatch manaHatch = manaHatches.get(0);
            this.tier = manaHatch.getTier();
            maxParallel = (int) Math.pow(2, tier);
            this.visStorageMax = manaHatch.getMaxMana();
            isManaModule = true;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("tier", this.tier);
        data.setInteger("visStorage", this.visStorage);
        data.setInteger("visStorageMax", this.visStorageMax);
        data.setBoolean("isVisModule", this.isVisModule);
        data.setBoolean("isManaModule", this.isManaModule);
        data.setInteger("maxParallel", this.maxParallel);
        data.setDouble("timeReduce", this.timeReduce);
        data.setDouble("energyReduce", this.energyReduce);
        data.setInteger("ParallelEnhance", this.ParallelEnhance);
        data.setInteger("OverclockingEnhance", this.OverclockingEnhance);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.tier = data.getInteger("tier");
        this.visStorage = data.getInteger("visStorage");
        this.visStorageMax = data.getInteger("visStorageMax");
        this.isVisModule = data.getBoolean("isVisModule");
        this.isManaModule = data.getBoolean("isManaModule");
        this.maxParallel = data.getInteger("maxParallel");
        this.timeReduce=data.getDouble("timeReduce");
        this.energyReduce=data.getDouble("energyReduce");
        this.ParallelEnhance=data.getInteger("ParallelEnhance");
        this.OverclockingEnhance=data.getInteger("OverclockingEnhance");
    }
    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed())
            textList.add(new TextComponentTranslation("灵气源: %s / %s（总） 灵气等级: %s", visStorage, visStorageMax, tier));
        if (material != null) {
            FluidStack fluidStack = getInputFluidInventory().drain(material.getFluid(Integer.MAX_VALUE), false);
            int i = fluidStack == null ? 0 : fluidStack.amount;
            if (i == 0)
                textList.add(new TextComponentTranslation("%s要素未填充", material.getLocalizedName()));
            if (i != 0)
                textList.add(new TextComponentTranslation("%s要素储量: %s", material.getLocalizedName(), TextFormattingUtil.formatNumbers((i))));
        }
        if (isManaModule) {
            textList.add(new TextComponentTranslation("超频加强: " + OverclockingEnhance + " 耗时减免: " + timeReduce));
            textList.add(new TextComponentTranslation("并行加强: " + ParallelEnhance + " 耗能减免: " + energyReduce));
        }
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


        predicate = predicate.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(1));

        if (checkFluidOut && this.recipeMap.getMaxFluidOutputs() > 0) {
            predicate = predicate.or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(1));
        }
        predicate = predicate
                .or(abilities(POMultiblockAbility.VIS_HATCH).setMaxGlobalLimited(1))
                .or(abilities(POMultiblockAbility.MANA_HATCH).setMaxGlobalLimited(1));

        return predicate;
    }

    public class POMultiblockRecipeLogic extends MultiblockRecipeLogic {
        public POMultiblockRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        public int getUpdateByVis() {
            if (isVisModule) {
                if (visStorage <= 128) return 1;
                return ParallelEnhance * 10 + visStorage / 128;
            }
            if (isManaModule) {
                if (visStorage <= 1024) return 1;
                return ParallelEnhance * 10 + visStorage / 1024;
            }
            return ParallelEnhance * 10;
        }

        @Override
        public int getParallelLimit() {
            return Math.min(getUpdateByVis(), maxParallel);
        }

        @Override
        protected void updateRecipeProgress() {
            if (this.canRecipeProgress && this.drawEnergy((int) (this.recipeEUt* energyReduce), true)) {
                if (drainVis(tier, true) && drainMaterial(material, false)) {

                    this.drawEnergy((int) (this.recipeEUt* energyReduce), false);

                    drainMaterial(material, true);
                    drainVis(tier, false);
                    if (++this.progressTime > this.maxProgressTime) {
                        this.completeRecipe();
                    }
                }
                if (this.hasNotEnoughEnergy && this.getEnergyInputPerSecond() > 19L * (long) this.recipeEUt) {
                    this.hasNotEnoughEnergy = false;
                }
            } else if (this.recipeEUt > 0) {
                this.hasNotEnoughEnergy = true;
                this.decreaseProgress();
            }
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            super.setMaxProgress((int) (maxProgress * timeReduce));
        }

        @Override
        protected double getOverclockingDurationFactor() {
            if (GTUtility.getTierByVoltage(this.getMaxVoltage()) <= tier + OverclockingEnhance) {
                return 0.33;
            } else {
                return 0.5;
            }
        }

        @Override
        protected double getOverclockingVoltageFactor() {
            if (GTUtility.getTierByVoltage(this.getMaxVoltage()) <= tier + OverclockingEnhance) {
                return 3.0;
            } else {
                return 2.0;
            }
        }
    }
}