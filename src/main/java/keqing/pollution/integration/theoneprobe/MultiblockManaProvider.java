package keqing.pollution.integration.theoneprobe;

import gregicality.multiblocks.common.metatileentities.multiblock.standard.MetaTileEntityAlloyBlastSmelter;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.TextFormattingUtil;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityElectricBlastFurnace;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityBlazingBlastFurnace;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityElectricArcFurnace;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntitySepticTank;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityVacuumDryingFurnace;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.gcys.MetaTileEntityIndustrialRoaster;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.huge.MetaTileEntityHugeBlastFurnace;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaHatch;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MultiblockManaProvider implements  IProbeInfoProvider {

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
                    int MaxMana= ((MetaTileEntityManaHatch) mte).getMaxMana();

                    iProbeInfo.progress(Mana, MaxMana, iProbeInfo.defaultProgressStyle()
                            .suffix(" / " + TextFormattingUtil.formatNumbers(MaxMana) + " Mana")
                            .filledColor(0xFFEEE600)
                            .alternateFilledColor(0xFFEEE600)
                            .borderColor(0xFF555555).numberFormat(mcjty.theoneprobe.api.NumberFormat.COMMAS));

                }

            }
        }
    }
}