package meowmel.pollution.integration.theoneprobe;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.TextFormattingUtil;
import meowmel.pollution.common.metatileentity.storage.MetaTileEntityQuantumAspectTank;
import meowmel.pollution.common.metatileentity.storage.MetaTileEntityQuantumManaTank;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

/**
 * 量子存储罐的 TOP 信息：显示要素罐的要素种类与储量、魔力罐的魔力储量
 */
public class QuantumTankProvider implements IProbeInfoProvider {

    /** 魔力填充色，与机器渲染一致 */
    private static final int MANA_COLOR = 0x00C6FF;
    /** 要素罐没有要素时的填充色 */
    private static final int EMPTY_ASPECT_COLOR = 0xFF9A5CC6;

    @Override
    public String getID() {
        return "pollution:quantum_tank";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (!iBlockState.getBlock().hasTileEntity(iBlockState)) return;

        TileEntity te = world.getTileEntity(iProbeHitData.getPos());
        if (!(te instanceof IGregTechTileEntity igtte)) return;

        MetaTileEntity mte = igtte.getMetaTileEntity();
        if (mte instanceof MetaTileEntityQuantumAspectTank aspectTank) {
            addAspectInfo(iProbeInfo, aspectTank);
        } else if (mte instanceof MetaTileEntityQuantumManaTank manaTank) {
            addManaInfo(iProbeInfo, manaTank);
        }
    }

    private static void addAspectInfo(IProbeInfo iProbeInfo, MetaTileEntityQuantumAspectTank tank) {
        int amount = tank.getAspectAmount();
        int capacity = tank.getMaxAspectCapacity();
        Aspect aspect = tank.getAspect();
        Aspect filter = tank.getAspectFilter();
        int color = aspect != null ? aspect.getColor() : EMPTY_ASPECT_COLOR;

        iProbeInfo.text(TextStyleClass.INFO + "要素: "
                + (aspect == null ? "无" : aspect.getLocalizedDescription()));
        if (filter != null) {
            iProbeInfo.text(TextStyleClass.INFO + "已锁定: " + filter.getLocalizedDescription());
        }
        iProbeInfo.progress(amount, capacity, iProbeInfo.defaultProgressStyle()
                .suffix(" / " + TextFormattingUtil.formatNumbers(capacity))
                .filledColor(color)
                .alternateFilledColor(color)
                .borderColor(0xFF555555)
                .numberFormat(NumberFormat.COMMAS));
    }

    private static void addManaInfo(IProbeInfo iProbeInfo, MetaTileEntityQuantumManaTank tank) {
        int mana = (int) Math.min(Integer.MAX_VALUE, tank.getMana());
        int capacity = tank.getMaxManaCapacity();

        iProbeInfo.text(TextStyleClass.INFO + "魔力: "
                + TextFormattingUtil.formatNumbers(mana) + " / " + TextFormattingUtil.formatNumbers(capacity));
        iProbeInfo.progress(mana, capacity, iProbeInfo.defaultProgressStyle()
                .suffix(" / " + TextFormattingUtil.formatNumbers(capacity))
                .filledColor(MANA_COLOR)
                .alternateFilledColor(MANA_COLOR)
                .borderColor(0xFF555555)
                .numberFormat(NumberFormat.COMMAS));
    }
}
