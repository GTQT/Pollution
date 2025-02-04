package keqing.pollution.api.metatileentity;

import gregtech.api.GTValues;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.Material;
import gregtech.api.util.TextFormattingUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static gregtech.api.unification.material.Materials.Lubricant;
import static keqing.pollution.api.utils.infusedFluidStack.STACK_MAP;

public abstract class PORecipeMapMultiblockController extends MultiMapMultiblockController {

    protected Material material;
    int tier;
    int visStorage;
    int visStorageMax;

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
        tooltip.add(I18n.format("多方块工作每tick需要消耗1mb的对应流体要素与5mb灵气源"));
        tooltip.add(I18n.format("需要使用 §c灵气仓§r 为多方块提供灵气支持"));
        tooltip.add(I18n.format("每100灵气源为多方块带来额外的一并行数量"));
        tooltip.add(I18n.format("当灵气等级对应电压时大于等于配方电压等级时，获得§b无损超频§7。"));
    }

    @Override
    public void updateFormedValid() {
        super.updateFormedValid();
        if (!isStructureFormed()) return;
        this.visStorage = this.getAbilities(POMultiblockAbility.VIS_HATCH).get(0).getVisStore();
    }

    @Override
    public boolean isActive() {
        return  this.isStructureFormed()
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
        return this.getAbilities(POMultiblockAbility.VIS_HATCH).get(0).drainVis(amount, simulate);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.tier = this.getAbilities(POMultiblockAbility.VIS_HATCH).get(0).getTier();
        this.visStorageMax = this.getAbilities(POMultiblockAbility.VIS_HATCH).get(0).getMaxVisStore();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed())
            textList.add(new TextComponentTranslation("灵气源: %s / %s（总） 灵气等级: %s", visStorage, visStorageMax, tier));
        if (material != null) {
            textList.add(new TextComponentTranslation("要素需求 : %s | 工作状态: %s", material.getLocalizedName(), drainMaterial(material, false)));
            if (getInputFluidInventory() != null) {
                FluidStack fluidStack = getInputFluidInventory().drain(material.getFluid(Integer.MAX_VALUE), false);
                int i = fluidStack == null ? 0 : fluidStack.amount;
                textList.add(new TextComponentTranslation("要素储量 : %s", TextFormattingUtil.formatNumbers((i))));
            }
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.visStorage);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.visStorage = buf.readInt();
    }


    public class POMultiblockRecipeLogic extends MultiblockRecipeLogic {
        public POMultiblockRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        public int getUpdateByVis() {
            if (visStorage <= 100) return 1;
            return visStorage / 100;
        }

        @Override
        protected double getOverclockingDurationDivisor() {
            if (this.getMaxVoltage() <= GTValues.V[tier]) {
                return 4.0;
            } else {
                return 2.0;
            }
        }

        @Override
        public int getParallelLimit() {
            return getUpdateByVis();
        }

        protected void updateRecipeProgress() {
            if (this.canRecipeProgress && this.drawEnergy(this.recipeEUt, true)) {
                if (drainVis(5,false) && drainMaterial(material, false)) {
                    this.drawEnergy(this.recipeEUt, false);
                    drainMaterial(material, true);
                    drainVis(5,true);
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
    }
}