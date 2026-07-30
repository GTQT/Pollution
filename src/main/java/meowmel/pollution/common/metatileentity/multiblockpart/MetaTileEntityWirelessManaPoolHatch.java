package meowmel.pollution.common.metatileentity.multiblockpart;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.metatileentity.multiblockpart.wireless.WirelessManager;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityWirelessManaPoolHatch extends MetaTileEntityManaPoolHatch {

    public MetaTileEntityWirelessManaPoolHatch(ResourceLocation metaTileEntityId, int tier, boolean isExport) {
        super(metaTileEntityId, tier, isExport);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityWirelessManaPoolHatch(this.metaTileEntityId, this.getTier(), isExport);
    }

    @Override
    protected SimpleOverlayRenderer getOverlay() {
        return isExport
                ? POTextures.WIRELESS_MANA_POOL_HATCH_OUTPUT
                : POTextures.WIRELESS_MANA_POOL_HATCH_INPUT;
    }


    @Override
    public void update() {
        super.update();
        if (isExport) {
            long trans = getMana();
            long added = WirelessManager.getInstance().addEnergy(getWorld().provider.getDimension(), trans);
            manaContainer.removeMana(added);
        } else {
            //只要不满，就向网络请求能量
            if (!isFull()) {
                long trans = getMaxMana() - getMana();
                long requested = WirelessManager.getInstance().requestEnergy(getWorld().provider.getDimension(), trans);
                manaContainer.addMana(requested);
            }
        }
    }
}
