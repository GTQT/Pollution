package meowmel.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.RecipeMap;
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
import org.jetbrains.annotations.NotNull;

import static meowmel.pollution.api.recipes.PORecipeMaps.MAGIC_GREENHOUSE_RECIPES;
import static meowmel.pollution.api.unification.PollutionMaterials.InfusedWater;

public class MetaTileEntityMagicGreenHouse extends MagicRecipeMapMultiblockController {
    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:magic_greenhouse", () -> configureMagicRecipeCasing(
                    DeclarativePatternBuilder.start()
                            .aisle("CCCCC", "CCCCC", "GGGGG", "GGGGG", "CCCCC", "CCCCC")
                            .aisle("CCCCC", "CPHPC", "G###G", "G###G", "CPHPC", "CDDDC")
                            .aisle("CCCCC", "CHHHC", "G###G", "G###G", "CHHHC", "CDDDC")
                            .aisle("CCCCC", "CPHPC", "G###G", "G###G", "CPHPC", "CDDDC")
                            .aisle("CCCCC", "CCSCC", "GGGGG", "GGGGG", "CCCCC", "CCCCC")
                            .self('S', MetaTileEntityMagicGreenHouse.class),
                    'C', getCasingState(),
                    MAGIC_GREENHOUSE_RECIPES, 32)
                    .block('P', getSecondCasingState())
                    .block('D', getCasingState4())
                    .block('H', getCasingState5())
                    .block('G', getCasingState3())
                    .air('#')
                    .buildStructureDefinition());
    public MetaTileEntityMagicGreenHouse(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]
                {
                        //GREENHOUSE_RECIPES,
                        MAGIC_GREENHOUSE_RECIPES});

    }

    private static IBlockState getSecondCasingState() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_3);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_PIPE);
    }

    private static IBlockState getCasingState5() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_GEARBOX);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicGreenHouse(this.metaTileEntityId);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.SPELL_PRISM_WATER;
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
