package keqing.pollution.common.metatileentity.multiblockpart.BMHPCA;

import com.cleanroommc.modularui.drawable.UITexture;
import gregtech.api.GTValues;
import gregtech.api.capability.IHPCACoolantProvider;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import keqing.pollution.client.textures.POGuiTextures;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.util.ResourceLocation;

public class MetaTileEntityBMHPCACooler extends MetaTileEntityBMHPCAComponent implements IHPCACoolantProvider {
    private final boolean supers;
    private final boolean ultimate;

    public MetaTileEntityBMHPCACooler(ResourceLocation metaTileEntityId, boolean supers,boolean ultimate) {
        super(metaTileEntityId);
        this.supers = supers;
        this.ultimate = ultimate;
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBMHPCACooler(this.metaTileEntityId, this.supers, this.ultimate);
    }

    @Override
    public boolean isUltimate() {
        if (isDamaged())
            return supers;
        return ultimate;
    }

    public SimpleOverlayRenderer getFrontOverlay() {
        return this.supers||ultimate ? POTextures.BMHPCA_ACTIVE_COOLER_OVERLAY : POTextures.BMHPCA_HEAT_SINK_OVERLAY;
    }

    public UITexture getComponentIcon() {
        return this.supers||ultimate ? POGuiTextures.BMHPCA_ICON_ACTIVE_COOLER_COMPONENT : POGuiTextures.BMHPCA_ICON_HEAT_SINK_COMPONENT;
    }

    public SimpleOverlayRenderer getFrontActiveOverlay() {
        return this.supers||ultimate ? POTextures.BMHPCA_ACTIVE_COOLER_ACTIVE_OVERLAY : this.getFrontOverlay();
    }

    @Override//维持(EU/t) T/F
    public int getUpkeepEUt() {
        if (isDamaged())
            return GTValues.VA[ultimate ? 6 : supers ? 5 : 4];
        return GTValues.VA[ultimate ? 7 : supers ? 6 : 5];
    }

    @Override//冷却量 T/F
    public int getCoolingAmount() {
        if (isDamaged())
            return ultimate ? 16 : supers ? 4 : 1;
        return ultimate ? 32 : supers ? 8 : 2;
    }

    @Override
    public boolean isActiveCooler() {
        return true;
    }

    @Override//最大冷却液 T/F
    public int getMaxCoolantPerTick() {
        if (isDamaged())
            return ultimate ? 128 : supers ? 32 : 8;
        return ultimate ? 512 : supers ? 128 : 32;
    }
    @Override
    public boolean canBeDamaged() {
        return false;
    }
}

