package meowmel.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;

import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static meowmel.pollution.api.unification.PollutionMaterials.InfusedFly;

public class MetaTileEntityMagicAutoclave extends MagicRecipeMapMultiblockController {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:magic_autoclave", () -> configureMagicRecipeCasing(
                    DeclarativePatternBuilder.start()
                            .aisle("YYY", "YYY", "YYY")
                            .aisle("XXX", "XCX", "XXX")
                            .aisle("XXX", "XCX", "XXX")
                            .aisle("XXX", "XCX", "XXX")
                            .aisle("XXX", "XSX", "XXX")
                            .self('S', MetaTileEntityMagicAutoclave.class),
                    'X', getCasingState(),
                    new RecipeMap<?>[]{RecipeMaps.AUTOCLAVE_RECIPES, PORecipeMaps.CRYSTAL_CULTIVATION_RECIPES}, 17)
                    .block('C', getCasingState2())
                    .block('Y', getCasingState3())
                    .air('A')
                    .any('#')
                    .buildStructureDefinition());

    public MetaTileEntityMagicAutoclave(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.AUTOCLAVE_RECIPES,
                PORecipeMaps.CRYSTAL_CULTIVATION_RECIPES});
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_AIR);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicAutoclave(this.metaTileEntityId);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.SPELL_PRISM_AIR;
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
        return InfusedFly;
    }
}
