package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import keqing.pollution.POConfig;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;

public class MetaTileEntityVisProvider extends TieredMetaTileEntity {
    private final double VisTicks;
    private final long energyAmountPer;
    int tier;

    public MetaTileEntityVisProvider(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.VisTicks = tier * POConfig.PollutionSystemSwitch.visProviderMultiplier;
        this.energyAmountPer = GTValues.VA[tier];
        this.tier = tier;
    }

    @Override
    public MetaTileEntityVisProvider createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityVisProvider(metaTileEntityId, getTier());
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote &&getOffsetTimer()%10==0&& energyContainer.getEnergyStored() >= energyAmountPer && AuraHelper.getVis(getWorld(), getPos()) < 400) {
            energyContainer.removeEnergy(energyAmountPer);
            AuraHelper.addVis(getWorld(), getPos(), (float) (VisTicks) * 10 * tier);
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.HPCA_BRIDGE_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return POTextures.MAGIC_VOLTAGE_CASINGS[getTier()];
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.vis.provider", (int) (VisTicks * 100)));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", this.energyContainer.getInputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
    }

}
