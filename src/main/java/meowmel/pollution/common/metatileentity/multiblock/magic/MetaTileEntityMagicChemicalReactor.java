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
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.MetaBlocks;

import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static meowmel.pollution.api.unification.PollutionMaterials.InfusedAlchemy;

public class MetaTileEntityMagicChemicalReactor extends MagicRecipeMapMultiblockController {

    private static final StructureDefinition STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:magic_chemical_reactor", () -> {
                DeclarativePatternBuilder builder = DeclarativePatternBuilder.start()
                        .aisle("   AAAAA   ", "    B      ", "     B     ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "   AAAAA   ")
                        .aisle("  AACCCAA  ", "           ", "           ", "      B    ", "           ", "           ", "    B      ", "     B     ", "           ", "           ", "           ", "           ", "  AACCCAA  ")
                        .aisle(" AADCACDAA ", "    D D    ", "           ", "           ", "       B   ", "   B       ", "           ", "           ", "      B    ", "           ", "           ", "    D D    ", " AADCACDAA ")
                        .aisle("AADCCACCDAA", "           ", "           ", "           ", "  B        ", "        B  ", "           ", "           ", "           ", "   B   B   ", "    B      ", "     B     ", "AADCCACCDAA")
                        .aisle("ACCCCACCCCA", "  D  E  D B", "     E     ", " B   E     ", "     E     ", "     E     ", "     E   B ", "     E     ", "  B  E     ", "     E     ", "     E B   ", "  D  E  D  ", "ACCCCACCCCA")
                        .aisle("ACAAAAAAACA", "    EEE    ", "B   EEE   B", "    EEE    ", "    EEE    ", "    EEE    ", "    EEE    ", " B  EEE  B ", "    EEE    ", "    EEE    ", "    EEE    ", "   BEEEB   ", "ACAAAAAAACA")
                        .aisle("ACCCCACCCCA", "B D  E  D  ", "     E     ", "     E   B ", "     E     ", "     E     ", " B   E     ", "     E     ", "     E  B  ", "     E     ", "   B E     ", "  D  E  D  ", "ACCCCACCCCA")
                        .aisle("AADCCACCDAA", "           ", "           ", "           ", "        B  ", "  B        ", "           ", "           ", "           ", "   B   B   ", "      B    ", "     B     ", "AADCCACCDAA")
                        .aisle(" AADCACDAA ", "    D D    ", "           ", "           ", "   B       ", "       B   ", "           ", "           ", "    B      ", "           ", "           ", "    D D    ", " AADCACDAA ")
                        .aisle("  AACCCAA  ", "           ", "           ", "    B      ", "           ", "           ", "      B    ", "     B     ", "           ", "           ", "           ", "           ", "  AACCCAA  ")
                        .aisle("   AASAA   ", "      B    ", "     B     ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "   AAAAA   ")
                        .self('S', MetaTileEntityMagicChemicalReactor.class)
                        .block('A', getCasingState())
                        .block('B', getCasingState2())
                        .block('C', getCasingState3())
                        .block('D', getCasingState4())
                        .block('E', getCasingState5())
                        .any(' ');
                return configureMagicRecipeCasing(builder, 'A', getCasingState(),
                        new RecipeMap<?>[]{RecipeMaps.CHEMICAL_RECIPES,
                                PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES}, 70)
                        .buildStructureDefinition();
            });

    public MetaTileEntityMagicChemicalReactor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.CHEMICAL_RECIPES, PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES});
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
    }

    private static IBlockState getCasingState5() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicChemicalReactor(this.metaTileEntityId);
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
        return InfusedAlchemy;
    }
}
