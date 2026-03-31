package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.utils.PipelineUtil;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import meowmel.gtqtcore.common.metatileentities.multi.misc.ExtraEnergyContainer;
import meowmel.pollution.api.capability.IVisHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
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
        tooltip.add(I18n.format("容量："+2000*getTier()));
        tooltip.add(I18n.format("每秒消耗："+0.05+"vis灵气"));
        tooltip.add(I18n.format("每秒缓存："+getTier()+"vis灵气"));
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
        return visContainer.drainVis(amount,simulate);
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
        return Textures.HPCA_COMPUTATION_OVERLAY;
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

}
