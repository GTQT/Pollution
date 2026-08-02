package meowmel.pollution.common.metatileentity.multiblock.magic;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.FluidStructureElements;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.client.renderer.CubeRendererState;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.cclop.ColourOperation;
import gregtech.client.renderer.cclop.LightMapOperation;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.utils.BloomEffectUtil;

import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static meowmel.pollution.api.unification.PollutionMaterials.InfusedWater;

public class MetaTileEntityMagicChemicalBath extends MagicRecipeMapMultiblockController {

    private static final StructureDefinition STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:magic_chemical_bath", () -> {
                DeclarativePatternBuilder builder = DeclarativePatternBuilder.start()
                        .aisle("XXXXXXX", "XXXXXXX", "XXXXXXX")
                        .aisle("XXXXXXX", "XDDCDDX", "XAAAAAX")
                        .aisle("XXXXXXX", "XDDCDDX", "XAAAAAX")
                        .aisle("XXXXXXX", "XCCCCCX", "XAAAAAX")
                        .aisle("XXXXXXX", "XDDCDDX", "XAAAAAX")
                        .aisle("XXXXXXX", "XDDCDDX", "XAAAAAX")
                        .aisle("XXXXXXX", "XXXSXXX", "XXXXXXX")
                        .self('S', MetaTileEntityMagicChemicalBath.class)
                        .block('X', getCasingState())
                        .block('C', getCasingState2())
                        .block('D', getCasingState3())
                        .where('A', FluidStructureElements.fluidElement(FluidRegistry.WATER));
                return configureMagicRecipeCasing(builder, 'X', getCasingState(),
                        new RecipeMap<?>[]{RecipeMaps.CHEMICAL_BATH_RECIPES, RecipeMaps.ORE_WASHER_RECIPES}, 31)
                        .buildStructureDefinition();
            });

    private boolean waterFilled;
    private List<BlockPos> waterPositions;

    public MetaTileEntityMagicChemicalBath(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.CHEMICAL_BATH_RECIPES, RecipeMaps.ORE_WASHER_RECIPES});
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_GEARBOX);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicChemicalBath(this.metaTileEntityId);
    }


    @Override
    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);

        this.waterPositions = formed.getAggregate(FluidStructureElements.FLUID_BLOCK_POSITIONS);
        if (this.waterPositions == null) this.waterPositions = new ArrayList<>();
        this.waterFilled = waterPositions.isEmpty();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.waterPositions = null; // Clear water fill data when the structure is invalidated
        this.waterFilled = false;
    }

    @Override
    protected void updateFormedValid() {
        super.updateFormedValid();
        if (!waterFilled && getOffsetTimer() % 5 == 0) {
            FluidStructureElements.fillFluid(this, this.waterPositions, FluidRegistry.WATER);
            if (this.waterPositions.isEmpty()) {
                this.waterFilled = true;
            }
        }
    }

    @Override
    public boolean isStructureObstructed() {
        return super.isStructureObstructed() || !waterFilled;
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
