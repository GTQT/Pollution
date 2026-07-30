package meowmel.pollution.common.metatileentity.multiblockpart;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.metatileentity.multiblockpart.wireless.WirelessManager;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityWirelessManaHatch extends MetaTileEntityManaHatch {

    public MetaTileEntityWirelessManaHatch(ResourceLocation metaTileEntityId, int tier, int amperage, boolean isExport) {
        super(metaTileEntityId, tier, amperage, isExport);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityWirelessManaHatch(this.metaTileEntityId, this.getTier(), amperage, isExportHatch);
    }

    @Override
    protected SimpleOverlayRenderer getOverlay() {
        return isExportHatch
                ? POTextures.WIRELESS_MANA_HATCH_OUTPUT
                : POTextures.WIRELESS_MANA_HATCH_INPUT;
    }

    @Override
    public void update() {
        super.update();
        if (isExportHatch) {
            long trans = getMana();
            long added = WirelessManager.getInstance().addEnergy(getWorld().provider.getDimension(), trans);
            energyContainer.removeEnergy(added);
        } else {
            //只要不满，就向网络请求能量
            if (!isFull()) {
                long trans = getMaxMana() - getMana();
                long requested = WirelessManager.getInstance().requestEnergy(getWorld().provider.getDimension(), trans);
                energyContainer.addEnergy(requested);
            }
        }
    }
}
