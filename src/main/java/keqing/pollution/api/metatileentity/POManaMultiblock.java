package keqing.pollution.api.metatileentity;

import gregicality.multiblocks.api.capability.IParallelMultiblock;
import gregicality.multiblocks.api.metatileentity.GCYMMultiblockAbility;
import gregicality.multiblocks.common.GCYMConfigHolder;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import keqing.pollution.api.capability.IManaMultiblock;
import keqing.pollution.api.capability.ipml.POManaMultiblockRecipeLogic;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

import static gregtech.api.GTValues.VA;

public abstract  class POManaMultiblock extends MultiMapMultiblockController implements IManaMultiblock , IParallelMultiblock {

    public POManaMultiblock(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[] { recipeMap });
        this.recipeMapWorkable = new POManaMultiblockRecipeLogic(this);
    }

    public POManaMultiblock(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new POManaMultiblockRecipeLogic(this);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed())
            textList.add(new TextComponentTranslation("等级: %s |魔力： %s / %s", this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTier(), this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMana(), this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMaxMana()));
    }

    @Override
    public void addInformation(ItemStack stack,  World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (isParallel())
            tooltip.add(I18n.format("gcym.tooltip.parallel_enabled"));
    }


    @Override
    public boolean isMana() {
        return true;
    }
    @Override
    public int getTier() {
        return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTier();
    }
    @Override
    public boolean work()
    {
        return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).consumeMana((int) Math.pow(2,getTier()));
    }

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int getMaxParallel() {
        return this.getAbilities(GCYMMultiblockAbility.PARALLEL_HATCH).isEmpty() ? 1 :
                this.getAbilities(GCYMMultiblockAbility.PARALLEL_HATCH).get(0).getCurrentParallel();
    }

}
