package meowmel.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.texture.Textures;
import meowmel.pollution.api.capability.ipml.VisProviderLogic;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetaTileEntityVisProvider extends TieredMetaTileEntity {

    private final VisProviderLogic visProviderLogic;

    public MetaTileEntityVisProvider(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.visProviderLogic = new VisProviderLogic(this, tier);
    }

    @Override
    public MetaTileEntityVisProvider createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityVisProvider(metaTileEntityId, getTier());
    }

    /**
     * @return 机器的运行逻辑
     */
    public VisProviderLogic getVisProviderLogic() {
        return visProviderLogic;
    }

    /**
     * @return 机器的能量缓存，供逻辑使用
     */
    public IEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void update() {
        super.update();
        this.visProviderLogic.performCharging();
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.HPCA_BRIDGE_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("末法时代", tooltip);
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.vis.provider", visProviderLogic.getVisPerTick()));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", this.energyContainer.getInputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
    }

}
