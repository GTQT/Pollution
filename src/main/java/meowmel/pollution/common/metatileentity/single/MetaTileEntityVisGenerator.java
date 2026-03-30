package meowmel.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import meowmel.pollution.POConfig;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;

import java.util.List;

import static gregtech.api.GTValues.V;
import static gregtech.api.GTValues.VA;

public class MetaTileEntityVisGenerator extends TieredMetaTileEntity {

    public MetaTileEntityVisGenerator(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityVisGenerator(metaTileEntityId, getTier());
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            long euRest = energyContainer.getEnergyCapacity() - energyContainer.getEnergyStored();
            if(euRest>0)
            {
                float drainedVis = euRest / POConfig.PollutionSystemSwitch.visGeneratorEuPerVis;
                energyContainer.changeEnergy(euRest);
                AuraHelper.polluteAura(getWorld(), getPos(),
                        drainedVis * POConfig.PollutionSystemSwitch.visGeneratorPollutionMultiplier,
                        POConfig.PollutionSystemSwitch.visGeneratorPollutionShowEffects);
            }
        }
    }

    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.universal.tooltip.consume",
                VA[getTier()] / POConfig.PollutionSystemSwitch.visGeneratorEuPerVis, POConfig.PollutionSystemSwitch.visGeneratorEuPerVis));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", this.energyContainer.getEnergyCapacity()));
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