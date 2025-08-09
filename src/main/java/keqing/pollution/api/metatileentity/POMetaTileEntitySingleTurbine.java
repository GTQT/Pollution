package keqing.pollution.api.metatileentity;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import keqing.gtqtcore.client.textures.GTQTTextures;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Function;

public class POMetaTileEntitySingleTurbine extends SimpleGeneratorMetaTileEntity {
    public POMetaTileEntitySingleTurbine(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, ICubeRenderer renderer, int tier, Function<Integer, Integer> tankScalingFunction) {
        super(metaTileEntityId, recipeMap, renderer, tier, tankScalingFunction);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new POMetaTileEntitySingleTurbine(this.metaTileEntityId, this.recipeMap, this.renderer, this.getTier(), this.getTankScalingFunction());
    }

    public boolean isValidFrontFacing(EnumFacing facing) {
        return true;
    }

    protected void renderOverlays(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        if (this.getFrontFacing().getAxis() == EnumFacing.Axis.Y) {
            this.renderer.renderOrientedState(renderState, translation, pipeline, EnumFacing.NORTH, this.workable.isActive(), this.workable.isWorkingEnabled());
            this.renderer.renderOrientedState(renderState, translation, pipeline, EnumFacing.WEST, this.workable.isActive(), this.workable.isWorkingEnabled());
        } else {
            super.renderOverlays(renderState, translation, pipeline);
        }

    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        GTQTTextures.ROCKET_ENGINE_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return POTextures.MAGIC_VOLTAGE_CASINGS[getTier()];
    }
}
