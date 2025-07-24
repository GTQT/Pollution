package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_GLASS;

public class MetaTileEntityIndustrialInfusion extends RecipeMapMultiblockController {
    int glass;
    int coil;

    public MetaTileEntityIndustrialInfusion(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PORecipeMaps.INDUSTRIAL_INFUSION_RECIPES);
        this.recipeMapWorkable = new IndustrialInfusionRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID);
    }

    private static IBlockState getCasingState1() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TITANIUM_PIPE);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TITANIUM_GEARBOX);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.MAGIC_BATTERY);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityIndustrialInfusion(this.metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("             ABA             ", "          BBBABABBB          ", "             ABA             ")
                .aisle("           BBABABB           ", "        BBB C   C BBB        ", "           BBABABB           ")
                .aisle("         BB  ABA  BB         ", "      BBB CBBABABBC BBB      ", "         BB  ABA  BB         ")
                .aisle("       BB           BB       ", "    BBB CBBB D D BBBC BBB    ", "       BB           BB       ")
                .aisle("     BB               BB     ", "   BB CBBB   D D   BBBC BB   ", "     BB               BB     ")
                .aisle("    B  E             E  B    ", "   B BBB     D D     BBB B   ", "    B  E             E  B    ")
                .aisle("    B  E             E  B    ", "  BBCB       D D       BCBB  ", "    B  E             E  B    ")
                .aisle("   B EEE      D      EEE B   ", "  B BB ED    DED    DE BB B  ", "   B EEE      D      EEE B   ")
                .aisle("   B    D     D     D    B   ", " BBCB  DED         DED  BCBB ", "   B    D     D     D    B   ")
                .aisle("  B      D    D    D      B  ", " B BB   DED       DED   BB B ", "  B      D    D    D      B  ")
                .aisle("  B       A   D   A       B  ", "BBCB     DEA  F  AED     BCBB", "  B       A   D   A       B  ")
                .aisle(" B         A     A         B ", "B BB      AEFFFFFEA      BB B", " B         A     A         B ")
                .aisle(" B                         B ", "BCB        FF G FF        BCB", " B                         B ")
                .aisle("AAA          GGG          AAA", "A ADDDDD   F GGG F   DDDDDA A", "AAA          GGG          AAA")
                .aisle("BBB    DDDD  GGG  DDDD    BBB", "B B    E  FFGGGGGFF  E    B B", "BBB    DDDD  GGG  DDDD    BBB")
                .aisle("AAA          GGG          AAA", "A ADDDDD   F GGG F   DDDDDA A", "AAA          GGG          AAA")
                .aisle(" B                         B ", "BCB        FF G FF        BCB", " B                         B ")
                .aisle(" B         A     A         B ", "B BB      AEFFFFFEA      BB B", " B         A     A         B ")
                .aisle("  B       A   D   A       B  ", "BBCB     DEA  F  AED     BCBB", "  B       A   D   A       B  ")
                .aisle("  B      D    D    D      B  ", " B BB   DED       DED   BB B ", "  B      D    D    D      B  ")
                .aisle("   B    D     D     D    B   ", " BBCB  DED         DED  BCBB ", "   B    D     D     D    B   ")
                .aisle("   B EEE      D      EEE B   ", "  B BB ED    DED    DE BB B  ", "   B EEE      D      EEE B   ")
                .aisle("    B  E             E  B    ", "  BBCB       D D       BCBB  ", "    B  E             E  B    ")
                .aisle("    B  E             E  B    ", "   B BBB     D D     BBB B   ", "    B  E             E  B    ")
                .aisle("     BB               BB     ", "   BB CBBB   D D   BBBC BB   ", "     BB               BB     ")
                .aisle("       BB           BB       ", "    BBB CBBB D D BBBC BBB    ", "       BB           BB       ")
                .aisle("         BB  ABA  BB         ", "      BBB CBBABABBC BBB      ", "         BB  ABA  BB         ")
                .aisle("           BBABABB           ", "        BBB C   C BBB        ", "           BBABABB           ")
                .aisle("             ABA             ", "          BBBASABBB          ", "             ABA             ")
                .where('S', selfPredicate())
                .where('A', CP_COIL_CASING.get())
                .where('B', states(getCasingState())
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1)
                                .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(0).setMaxGlobalLimited(2).setPreviewCount(1))))
                .where('C', states(getCasingState1()))
                .where('D', states(getCasingState2()))
                .where('E', states(getCasingState3()))
                .where('F', states(getCasingState4()))
                .where('G', CP_GLASS.get())
                .where(' ', any())
                .build();
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.SPELL_PRISM_VOID;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object coil = context.get("COILTieredStats");
        this.coil = POUtils.getOrDefault(() -> coil instanceof WrappedIntTired,
                () -> ((WrappedIntTired) coil).getIntTier(),
                0);
        Object glass = context.get("GLASSTieredStats");
        this.glass = POUtils.getOrDefault(() -> glass instanceof WrappedIntTired,
                () -> ((WrappedIntTired) glass).getIntTier(),
                0);
    }

    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.industrial_infusion.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.industrial_infusion.tooltip.2"));
    }

    protected class IndustrialInfusionRecipeLogic extends MultiblockRecipeLogic {
        public IndustrialInfusionRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        public int getParallelLimit() {
            return (int) Math.pow(2, coil);
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            super.setMaxProgress(maxProgress * (100 - 5 * glass) / 100);
        }
    }
}

