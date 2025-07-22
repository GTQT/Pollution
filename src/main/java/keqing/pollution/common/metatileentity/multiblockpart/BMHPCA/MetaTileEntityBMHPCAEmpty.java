package keqing.pollution.common.metatileentity.multiblockpart.BMHPCA;

import com.cleanroommc.modularui.drawable.UITexture;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import keqing.pollution.client.textures.POGuiTextures;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityBMHPCAEmpty extends MetaTileEntityBMHPCAComponent {
    public MetaTileEntityBMHPCAEmpty(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBMHPCAEmpty(this.metaTileEntityId);
    }

    public boolean isUltimate() {
        return false;
    }

    public SimpleOverlayRenderer getFrontOverlay() {
        return POTextures.BMHPCA_EMPTY_OVERLAY;
    }

    public UITexture getComponentIcon() {
        return POGuiTextures.BMHPCA_ICON_EMPTY_COMPONENT;
    }

    public int getUpkeepEUt() {
        return 0;
    }

    public boolean canBeDamaged() {
        return false;
    }
}
