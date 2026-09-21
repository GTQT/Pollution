package meowmel.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.texture.Textures;
import meowmel.pollution.api.capability.ipml.VisGeneratorLogic;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetaTileEntityAuraGenerator extends TieredMetaTileEntity {

    private final VisGeneratorLogic visGeneratorLogic;

    public MetaTileEntityAuraGenerator(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.visGeneratorLogic = new VisGeneratorLogic(this, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityAuraGenerator(metaTileEntityId, getTier());
    }

    /**
     * @return 机器的运行逻辑
     */
    public VisGeneratorLogic getVisGeneratorLogic() {
        return visGeneratorLogic;
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
        this.visGeneratorLogic.performGeneration();
    }

    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("会产生很多蘑菇", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.universal.tooltip.consume", visGeneratorLogic.getVisPerTick(), visGeneratorLogic.getEuPerVis()));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", energyContainer.getOutputVoltage(),
                GTValues.VNF[getTier()]));
        tooltip.add(
                I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.HPCA_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    protected boolean isEnergyEmitter() {
        return true;
    }

}
