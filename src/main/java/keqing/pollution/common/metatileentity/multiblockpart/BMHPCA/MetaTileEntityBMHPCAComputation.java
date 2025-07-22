package keqing.pollution.common.metatileentity.multiblockpart.BMHPCA;

import com.cleanroommc.modularui.drawable.UITexture;
import gregtech.api.GTValues;
import gregtech.api.capability.IHPCAComputationProvider;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;

import keqing.pollution.client.textures.POGuiTextures;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityBMHPCAComputation extends MetaTileEntityBMHPCAComponent implements IHPCAComputationProvider {

    private final boolean ultimate;

    public MetaTileEntityBMHPCAComputation(ResourceLocation metaTileEntityId, boolean ultimate) {
        super(metaTileEntityId);
        this.ultimate = ultimate;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBMHPCAComputation(metaTileEntityId, ultimate);
    }

    public boolean isUltimate() {
        return this.ultimate;
    }

    public SimpleOverlayRenderer getFrontOverlay() {
        if (this.isDamaged()) {
            return this.ultimate ? POTextures.BMHPCA_ADVANCED_DAMAGED_OVERLAY : POTextures.BMHPCA_DAMAGED_OVERLAY;
        } else {
            return this.ultimate ? POTextures.BMHPCA_ADVANCED_COMPUTATION_OVERLAY : POTextures.BMHPCA_COMPUTATION_OVERLAY;
        }
    }

    public UITexture getComponentIcon() {
        if (this.isDamaged()) {
            return this.ultimate ? POGuiTextures.BMHPCA_ICON_DAMAGED_ADVANCED_COMPUTATION_COMPONENT : POGuiTextures.BMHPCA_ICON_DAMAGED_COMPUTATION_COMPONENT;
        } else {
            return this.ultimate ? POGuiTextures.BMHPCA_ICON_ADVANCED_COMPUTATION_COMPONENT : POGuiTextures.BMHPCA_ICON_COMPUTATION_COMPONENT;
        }
    }

    public SimpleOverlayRenderer getFrontActiveOverlay() {
        if (this.isDamaged()) {
            return this.ultimate ? POTextures.BMHPCA_ADVANCED_DAMAGED_ACTIVE_OVERLAY : POTextures.BMHPCA_DAMAGED_ACTIVE_OVERLAY;
        } else {
            return this.ultimate ? POTextures.BMHPCA_ADVANCED_COMPUTATION_ACTIVE_OVERLAY : POTextures.BMHPCA_COMPUTATION_ACTIVE_OVERLAY;
        }
    }

    public int getUpkeepEUt() {
        return GTValues.VA[this.ultimate ? 5 : 4];
    }

    public int getMaxEUt() {
        return GTValues.VA[this.ultimate ? 7 : 6];
    }

    public int getCWUPerTick() {
        if (this.isDamaged()) {
            return 0;
        } else {
            return this.ultimate ? 64 : 16;
        }
    }

    public int getCoolingPerTick() {
        return this.ultimate ? 16 : 8;
    }

    public boolean canBeDamaged() {
        return true;
    }
}
