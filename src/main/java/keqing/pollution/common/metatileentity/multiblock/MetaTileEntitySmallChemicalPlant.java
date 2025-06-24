package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.gtqtcore.api.recipes.GTQTcoreRecipeMaps;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POBotBlock;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;

public class MetaTileEntitySmallChemicalPlant extends MultiMapMultiblockController {
    @Override
    public boolean usesMui2() {
        return false;
    }
    int CoilLevel;

    public MetaTileEntitySmallChemicalPlant(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{
                RecipeMaps.CHEMICAL_RECIPES,
                RecipeMaps.LARGE_CHEMICAL_RECIPES,
                GTQTcoreRecipeMaps.CHEMICAL_PLANT,
                PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES,
                GTQTcoreRecipeMaps.FERMENTATION_TANK_RECIPES});
        this.recipeMapWorkable = new SmallChemicalPlantRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING);
    }

    private static IBlockState getCasingState2() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.mansussteel).getBlock(PollutionMaterials.mansussteel);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
    }

    private static IBlockState getCasingState5() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_1_CASING);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySmallChemicalPlant(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object CoilLevel = context.get("COILTieredStats");
        this.CoilLevel = POUtils.getOrDefault(() -> CoilLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) CoilLevel).getIntTier(),
                0);

        List<IEnergyContainer> energyContainer = new ArrayList(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
        this.energyContainer = new EnergyContainerList(energyContainer);
    }

    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.5"));
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("线圈等级: %s", CoilLevel));
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("GGGGG", "BAAAB", "BAAAB", "BAAAB", "GGGGG")
                .aisle("GXXXG", "ADDDA", "ABEBA", "ADDDA", "GXXXG")
                .aisle("GXXXG", "ADDDA", "AECEA", "ADDDA", "GXXXG")
                .aisle("GXXXG", "ADDDA", "ABEBA", "ADDDA", "GXXXG")
                .aisle("GGSGG", "BAAAB", "BAAAB", "BAAAB", "GGGGG")
                .where('S', selfPredicate())
                .where('G', states(getCasingState()).setMinGlobalLimited(8).or(autoAbilities()))
                .where('B', states(getCasingState2()))
                .where('C', states(getCasingState3()))
                .where('D', CP_COIL_CASING.get())
                .where('E', states(getCasingState4()))
                .where('X', states(getCasingState5()))
                .where('A', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.TERRA_WATERTIGHT_CASING;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    protected class SmallChemicalPlantRecipeLogic extends MultiblockRecipeLogic {

        public SmallChemicalPlantRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        public int getParallelLimit() {
            if (this.getRecipeMap() == GTQTcoreRecipeMaps.CHEMICAL_PLANT) {
                return (int) Math.max(256, Math.pow(4, CoilLevel));
            } else if (this.getRecipeMap() == GTQTcoreRecipeMaps.FERMENTATION_TANK_RECIPES) {
                return (int) Math.max(64, Math.pow(4, CoilLevel));
            } else {
                return Math.max(64, 4 * CoilLevel);
            }
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            this.maxProgressTime = (int) (maxProgress * (1 - 0.05 * CoilLevel));
        }

    }
}