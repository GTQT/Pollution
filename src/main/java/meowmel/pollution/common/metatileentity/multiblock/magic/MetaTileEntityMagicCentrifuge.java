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
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import static meowmel.pollution.api.unification.PollutionMaterials.InfusedAir;

public class MetaTileEntityMagicCentrifuge extends MagicRecipeMapMultiblockController {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:magic_centrifuge", () -> configureMagicRecipeCasing(
                    DeclarativePatternBuilder.start()
                            .aisle("BXXXB", "XXXXX", "BXXXB")
                            .aisle("XXXXX", "XHGHX", "XXXXX")
                            .aisle("XXXXX", "XGHGX", "XXXXX")
                            .aisle("XXXXX", "XHGHX", "XXXXX")
                            .aisle("BXXXB", "XXSXX", "BXXXB")
                            .self('S', MetaTileEntityMagicCentrifuge.class),
                    'X', getCasingState(),
                    new RecipeMap<?>[]{RecipeMaps.CENTRIFUGE_RECIPES,
                            RecipeMaps.THERMAL_CENTRIFUGE_RECIPES}, 17)
                    .any('B')
                    .air('A')
                    .block('G', getCasingState2())
                    .block('H', getCasingState3())
                    .buildStructureDefinition());

    public MetaTileEntityMagicCentrifuge(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.CENTRIFUGE_RECIPES, RecipeMaps.THERMAL_CENTRIFUGE_RECIPES});
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_AIR);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.STEEL_PIPE);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.STEEL_GEARBOX);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicCentrifuge(this.metaTileEntityId);
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
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public Material getMaterial() {
        return InfusedAir;
    }
}
