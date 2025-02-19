package keqing.pollution.common.metatileentity.multiblockpart.BMHPCA;

import gregtech.api.GTValues;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityBMHPCABridge extends MetaTileEntityBMHPCAComponent {

    public MetaTileEntityBMHPCABridge(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBMHPCABridge(metaTileEntityId);
    }

    @Override
    public boolean isUltimate() {
        return true;
    }

    @Override
    public boolean doesAllowBridging() {
        return true;
    }

    @Override
    public SimpleOverlayRenderer getFrontOverlay() {
        return POTextures.BMHPCA_BRIDGE_OVERLAY;
    }

    @Override
    public TextureArea getComponentIcon() {
        return POTextures.BMHPCA_ICON_BRIDGE_COMPONENT;
    }

    @Override
    public SimpleOverlayRenderer getFrontActiveOverlay() {
        return POTextures.BMHPCA_BRIDGE_ACTIVE_OVERLAY;
    }

    @Override
    public int getUpkeepEUt() {
        return GTValues.VA[GTValues.IV];
    }

    @Override
    public boolean canBeDamaged() {
        return false;
    }
}