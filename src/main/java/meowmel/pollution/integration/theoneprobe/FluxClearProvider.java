package meowmel.pollution.integration.theoneprobe;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import meowmel.pollution.api.capability.IFluxClearInfo;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * 空气过滤机的 TOP 信息：当前污染、清理速率与滤芯耐久。
 * 单方块机器与大型多方块控制器都实现 {@link IFluxClearInfo}，因此共用这一个 provider。
 */
public class FluxClearProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return "pollution:flux_clear";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (!iBlockState.getBlock().hasTileEntity(iBlockState)) return;

        TileEntity te = world.getTileEntity(iProbeHitData.getPos());
        if (!(te instanceof IGregTechTileEntity igtte)) return;

        MetaTileEntity mte = igtte.getMetaTileEntity();
        if (!(mte instanceof IFluxClearInfo info)) return;

        int maxDurability = info.getFilterMaxDurability();

        iProbeInfo.text(TextStyleClass.INFO + "当前污染: " + info.getCurrentFlux() + " Vis");
        iProbeInfo.text(TextStyleClass.INFO + "清理速率: " + info.getVisPerTick() + " Vis/t");
        iProbeInfo.text(TextStyleClass.INFO + (maxDurability <= 0
                ? "滤芯耐久: 无滤芯"
                : "滤芯耐久: " + (maxDurability - info.getFilterDamage()) + " / " + maxDurability));
    }
}
