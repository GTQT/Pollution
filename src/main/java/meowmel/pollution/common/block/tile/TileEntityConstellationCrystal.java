package meowmel.pollution.common.block.tile;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POConstellationCrystal;
import meowmel.pollution.common.metatileentity.multiblock.astral.MetaTileEntityConstellationTower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Render-only tile for animated constellation crystals.
 * Animation time is derived client-side and does not tick gameplay logic.
 */
public class TileEntityConstellationCrystal extends TileEntity {

    public static final long ENERGY_CAPACITY = 4_194_304L;
    private static final String NBT_ENERGY = "ConstellationEnergy";

    private long constellationEnergy;

    public long getConstellationEnergyStored() {
        return constellationEnergy;
    }

    public long receiveConstellationEnergy(long amount) {
        if (amount <= 0L || !isTowerCore()) return 0L;
        long accepted = Math.min(amount, ENERGY_CAPACITY - constellationEnergy);
        if (accepted > 0L) {
            constellationEnergy += accepted;
            markDirty();
        }
        return accepted;
    }

    public long extractConstellationEnergy(long amount, boolean simulate) {
        if (amount <= 0L || !isTowerCore()) return 0L;
        long extracted = Math.min(amount, constellationEnergy);
        if (!simulate && extracted > 0L) {
            constellationEnergy -= extracted;
            markDirty();
        }
        return extracted;
    }

    public boolean onCoreRightClick(EntityPlayer player, EnumHand hand) {
        MetaTileEntityConstellationTower tower = findTower();
        if (tower == null) {
            player.sendStatusMessage(new TextComponentTranslation(
                    "pollution.machine.constellation_tower.core.unlinked"), true);
            return true;
        }
        return tower.onCoreRightClick(player, hand);
    }

    private MetaTileEntityConstellationTower findTower() {
        if (world == null || !isTowerCore()) return null;
        BlockPos base = pos.down(12);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            TileEntity tile = world.getTileEntity(base.offset(facing, 4));
            if (!(tile instanceof IGregTechTileEntity)) continue;
            MetaTileEntity metaTileEntity = ((IGregTechTileEntity) tile).getMetaTileEntity();
            if (metaTileEntity instanceof MetaTileEntityConstellationTower) {
                MetaTileEntityConstellationTower tower = (MetaTileEntityConstellationTower) metaTileEntity;
                if (tower.isTowerCoreAt(pos)) return tower;
            }
        }
        return null;
    }

    private boolean isTowerCore() {
        if (world == null) return false;
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                && PollutionMetaBlocks.CONSTELLATION_CRYSTAL.getState(state)
                == POConstellationCrystal.CrystalType.TOWER_CORE;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setLong(NBT_ENERGY, constellationEnergy);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        constellationEnergy = Math.max(0L, Math.min(ENERGY_CAPACITY, compound.getLong(NBT_ENERGY)));
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (world != null) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == PollutionMetaBlocks.CONSTELLATION_CRYSTAL
                    && PollutionMetaBlocks.CONSTELLATION_CRYSTAL.getState(state)
                    == POConstellationCrystal.CrystalType.TOWER_CORE) {
                return new AxisAlignedBB(pos).grow(4.0D, 1.5D, 4.0D).expand(0.0D, -7.0D, 0.0D);
            }
        }
        return new AxisAlignedBB(pos).grow(0.35D);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 16384.0D;
    }
}
