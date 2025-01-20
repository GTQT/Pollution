package keqing.pollution.integration.theoneprobe;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.TextFormattingUtil;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaHatch;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vazkii.botania.common.block.tile.mana.TilePool;

public class MultiblockManaProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return "pollution:mana";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (iBlockState.getBlock().hasTileEntity(iBlockState)) {
            TileEntity te = world.getTileEntity(iProbeHitData.getPos());
            if (te instanceof IGregTechTileEntity igtte) {
                MetaTileEntity mte = igtte.getMetaTileEntity();
                if (mte instanceof MetaTileEntityManaHatch) {
                    int Mana = ((MetaTileEntityManaHatch) mte).getMana();
                    int MaxMana = ((MetaTileEntityManaHatch) mte).getMaxMana();

                    iProbeInfo.progress(Mana, MaxMana, iProbeInfo.defaultProgressStyle()
                            .suffix(" / " + TextFormattingUtil.formatNumbers(MaxMana) + " Mana")
                            .filledColor(0xFFEEE600)
                            .alternateFilledColor(0xFFEEE600)
                            .borderColor(0xFF555555).numberFormat(mcjty.theoneprobe.api.NumberFormat.COMMAS));

                }

            }
        }
        if (!world.isRemote && world.getTileEntity(iProbeHitData.getPos()) instanceof TilePool pool) {
            iProbeInfo.progress(pool.getCurrentMana(), pool.manaCap, iProbeInfo.defaultProgressStyle()
                    .suffix(" / " + TextFormattingUtil.formatNumbers(pool.manaCap) + " Mana")
                    .filledColor(0xFFEEE600)
                    .alternateFilledColor(0xFFEEE600)
                    .borderColor(0xFF555555).numberFormat(mcjty.theoneprobe.api.NumberFormat.COMMAS));
        }
    }
}