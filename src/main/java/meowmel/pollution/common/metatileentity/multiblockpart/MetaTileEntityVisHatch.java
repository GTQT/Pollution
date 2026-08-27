package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.utils.PipelineUtil;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import meowmel.pollution.api.capability.IVisHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;

public class MetaTileEntityVisHatch extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<IVisHatch>, IVisHatch {

    private VisContainer visContainer;

    public MetaTileEntityVisHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        visContainer = new VisContainer(getTier() * 2000);

    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("store", visContainer.serializeNBT());
        return super.writeToNBT(data);
    }


    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.visContainer = new VisContainer(0);
        this.visContainer.deserializeNBT(data.getCompoundTag("store"));
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityVisHatch(this.metaTileEntityId, this.getTier());
    }

    @Override
    public void update() {
        super.update();
        if (getOffsetTimer() % 20 == 0) {
            if (AuraHelper.getVis(getWorld(), getPos()) >= 0.05) {
                if (!visContainer.isFull()) {
                    AuraHelper.drainVis(getWorld(), this.getPos(), 0.05F, false);
                    visContainer.addVis(getTier());
                }
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.vis_hatch.tooltip.capacity", getMaxVisStore()));
        tooltip.add(I18n.format("pollution.machine.vis_hatch.tooltip.drain"));
        tooltip.add(I18n.format("pollution.machine.vis_hatch.tooltip.buffer", getTier()));
    }

    @Override
    public int getVisStore() {
        return visContainer.getVis();
    }

    @Override
    public int getMaxVisStore() {
        return visContainer.getMaxVis();
    }

    @Override
    public boolean drainVis(int amount, boolean simulate) {
        return visContainer.drainVis(amount, simulate);
    }

    @Override
    public MultiblockAbility<IVisHatch> getAbility() {
        return POMultiblockAbility.VIS_HATCH;
    }

    @Override
    public void registerAbilities(AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            getOverlay().renderSided(getFrontFacing(), renderState, translation,
                    PipelineUtil.color(pipeline, GTValues.VC[getTier()]));
        }
    }

    @NotNull
    private SimpleOverlayRenderer getOverlay() {
        return POTextures.VIS_HATCH;
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

}
