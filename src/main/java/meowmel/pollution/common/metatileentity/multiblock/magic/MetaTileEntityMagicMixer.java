package meowmel.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;

import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import static meowmel.pollution.api.unification.PollutionMaterials.InfusedEntropy;

public class MetaTileEntityMagicMixer extends MagicRecipeMapMultiblockController {
    public MetaTileEntityMagicMixer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.MIXER_RECIPES});
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_3);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.POLYTETRAFLUOROETHYLENE_PIPE);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.DAMINATED_GLASS);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicMixer(this.metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#XXX#", "#XXX#", "#XXX#", "#XXX#", "#XXX#", "##G##")
                .aisle("XXXXX", "XACAX", "XAAAX", "XACAX", "XAAAX", "##G##")
                .aisle("XXXXX", "XCPCX", "XAPAX", "XCPCX", "XAPAX", "GGGGG")
                .aisle("XXXXX", "XACAX", "XAAAX", "XACAX", "XAAAX", "##G##")
                .aisle("#XXX#", "#XSX#", "#XXX#", "#XXX#", "#XXX#", "##G##")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(40).or(autoAbilities()))
                .where('P', states(getCasingState2()))
                .where('C', states(getCasingState3()))
                .where('G', states(getCasingState4()))
                .where('A', air())
                .where('#', any())
                .build();
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
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public Material getMaterial() {
        return InfusedEntropy;
    }
}
