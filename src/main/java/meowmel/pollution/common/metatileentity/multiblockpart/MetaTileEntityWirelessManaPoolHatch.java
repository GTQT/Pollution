package meowmel.pollution.common.metatileentity.multiblockpart;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.metatileentity.multiblockpart.wireless.WirelessManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetaTileEntityWirelessManaPoolHatch extends MetaTileEntityManaPoolHatch {

    public MetaTileEntityWirelessManaPoolHatch(ResourceLocation metaTileEntityId, PoolType poolType, boolean isExport) {
        super(metaTileEntityId, poolType, isExport);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityWirelessManaPoolHatch(this.metaTileEntityId, poolType, isExport);
    }

    @Override
    protected SimpleOverlayRenderer getOverlay() {
        return isExport
                ? POTextures.WIRELESS_MANA_POOL_HATCH_OUTPUT
                : POTextures.WIRELESS_MANA_POOL_HATCH_INPUT;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.wireless_mana_pool_hatch.tooltip"));
    }


    @Override
    public void update() {
        super.update();
        if (getWorld() == null || getWorld().isRemote) return;
        if (isExport) {
            long trans = Math.min(getMana(), getRemainingOutputTransferRate());
            long added = WirelessManager.getInstance().addManaPool(getWorld().provider.getDimension(), trans);
            manaTransferredThisTick += manaContainer.removeMana(added);
        } else {
            if (!manaContainer.isFull()) {
                long trans = Math.min(getMaxMana() - getMana(), getRemainingExternalReceiveRate());
                long requested = WirelessManager.getInstance().requestManaPool(getWorld().provider.getDimension(), trans);
                long accepted = receiveExternalMana(requested);
                if (accepted < requested) {
                    WirelessManager.getInstance().addManaPool(
                            getWorld().provider.getDimension(), requested - accepted);
                }
            }
        }
    }
}
