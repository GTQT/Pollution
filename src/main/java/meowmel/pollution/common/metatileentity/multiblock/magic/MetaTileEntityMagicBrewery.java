package meowmel.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
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
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static meowmel.pollution.api.unification.PollutionMaterials.InfusedWater;

public class MetaTileEntityMagicBrewery extends MagicRecipeMapMultiblockController {

    public MetaTileEntityMagicBrewery(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.BREWING_RECIPES, RecipeMaps.FERMENTING_RECIPES,
                RecipeMaps.FLUID_HEATER_RECIPES});
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_COLD);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_PIPE);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_GEARBOX);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicBrewery(this.metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#XXX#", "#XXX#", "#XXX#", "#XXX#", "#####")
                .aisle("XXXXX", "XCCCX", "XAAAX", "XXAXX", "##X##")
                .aisle("XXXXX", "XCPCX", "XAPAX", "XAPAX", "#XMX#")
                .aisle("XXXXX", "XCCCX", "XAAAX", "XXAXX", "##X##")
                .aisle("#XXX#", "#XSX#", "#XXX#", "#XXX#", "#####")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(40).or(autoAbilities()))
                .where('C', states(getCasingState2()))
                .where('P', states(getCasingState3()))
                .where('A', states(getCasingState4()))
                .where('#', any())
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.SPELL_PRISM_COLD;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public Material getMaterial() {
        return InfusedWater;
    }
}