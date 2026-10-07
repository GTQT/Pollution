package meowmel.pollution.common.block.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * 返程传送器的数据载体：记录玩家从哪来、要回到哪去。
 *
 * <p>坐标在平台上生成传送器时由 {@link meowmel.pollution.api.utils.POBeneathTeleporter} 写入，
 * 随区块一起持久化，因此玩家下线、区块卸载之后回来仍然能用。</p>
 */
public class TileEntityBeneathReturnPad extends TileEntity {

    private static final String NBT_DIMENSION = "ReturnDimension";
    private static final String NBT_POS = "ReturnPos";

    /** 没有记录返程点时的哨兵值。 */
    private static final int NO_DIMENSION = Integer.MIN_VALUE;

    private int returnDimension = NO_DIMENSION;

    @Nullable
    private BlockPos returnPos;

    /** 记录返程坐标；同一座平台被重复使用时会被刷新。 */
    public void setReturnTarget(int dimension, BlockPos pos) {
        this.returnDimension = dimension;
        this.returnPos = pos.toImmutable();
        markDirty();
    }

    public int getReturnDimension() {
        return returnDimension;
    }

    @Nullable
    public BlockPos getReturnPos() {
        return returnPos;
    }

    public boolean hasReturnTarget() {
        return returnPos != null && returnDimension != NO_DIMENSION;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.returnDimension = compound.hasKey(NBT_DIMENSION)
                ? compound.getInteger(NBT_DIMENSION) : NO_DIMENSION;
        this.returnPos = compound.hasKey(NBT_POS)
                ? BlockPos.fromLong(compound.getLong(NBT_POS)) : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(NBT_DIMENSION, returnDimension);
        if (returnPos != null) {
            compound.setLong(NBT_POS, returnPos.toLong());
        }
        return compound;
    }
}