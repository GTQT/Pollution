package keqing.pollution.common.block.tile;


import gregtech.api.unification.material.Materials;
import gregtechfoodoption.GTFOMaterialHandler;
import keqing.pollution.api.utils.FleshTreeGrowth;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * 血肉之树心脏核心 TileEntity
 *
 * 功能:
 * - 记录当前等级(1-10)，初始为1
 * - 定期触发生长（每次+1级），最多长10次到等级10
 * - 最大占地4个区块（64x64方块范围）
 * - 发出心跳声
 * - 控制整棵树的结构生长
 */
public class TileEntityFleshHeart extends TileEntity implements ITickable {

    public static final int MAX_LEVEL = 10;

    /** 当前等级 1~10 */
    private int level = 1;

    /** 树根部位置（最下方的树干） */
    private BlockPos originPos = BlockPos.ORIGIN;

    /** 生长冷却计时器（tick数） */
    private int growthTimer = 0;

    /** 每次生长间隔（tick）= 约5分钟 */
    private static final int GROWTH_INTERVAL = 6000; // 5分钟 = 6000 ticks

    /** 心跳声计时器 */
    private int heartbeatTimer = 0;

    /** 心跳间隔随等级变化：等级越高心跳越快 */
    private int getHeartbeatInterval() {
        return Math.max(20, 80 - level * 5); // 等级1: 75tick, 等级10: 30tick
    }

    @Override
    public void update() {
        // === 心跳声效 === 客户端
        if(getWorld().isRemote){
            heartbeatTimer++;
            if (heartbeatTimer >= getHeartbeatInterval()) {
                heartbeatTimer = 0;
                playHeartbeat();
            }
        }//服务端
        if (world == null || world.isRemote) return;
        pushFluidToNeighbors();
        scanForInventory();
        // === 生长逻辑 ===
        if (level >= MAX_LEVEL) return; // 已满级不再生长
        growthTimer++;
        if (growthTimer >= GROWTH_INTERVAL * (this.level*1.25)) {
            growthTimer = 0;
            growOnce();
        }
    }
    /** 流体输出间隔 (tick), 等级越高越快 */
    private int getFluidInterval() {
        return Math.max(10, 60 - level * 5); // 等级1: 55tick, 等级10: 10tick
    }

    /** 每次输出量 (mB), 等级越高越多 */
    private int getFluidAmount() {
        return 50 + (level-1) * 50; // 等级1: 80mB, 等级10: 350mB
    }

    /** 流体输出计时器 */
    private int fluidTimer = 0;

    /**
     * 在 update() 方法中调用此方法
     * 即在 update() 的末尾加一行: pushFluidToNeighbors();
     */
    private void pushFluidToNeighbors() {
        if (world == null || world.isRemote) return;
        fluidTimer++;
        if (fluidTimer < getFluidInterval()) return;
        fluidTimer = 0;
        // 要输出的流体: 水 (可改为自定义流体如 "血液")
        FluidStack toFill = GTFOMaterialHandler.Blood.getFluid(getFluidAmount());
        // 扫描6个方向
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos neighborPos = pos.offset(facing);
            TileEntity neighborTE = world.getTileEntity(neighborPos);
            if (neighborTE == null) continue;
            // 检查邻居是否有流体容器能力 (从心脏这边看过去的反方向)
            if (!neighborTE.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
                continue;
            }
            IFluidHandler handler = neighborTE.getCapability(
                    CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
            if (handler == null) continue;
            // 先模拟填充, 看能装多少
            int accepted = handler.fill(toFill, false);
            if (accepted > 0) {
                // 实际填充
                handler.fill(GTFOMaterialHandler.Blood.getFluid(accepted), true);
            }
        }
    }
    /** 记录的容器位置, null = 未找到 */
    private BlockPos linkedInventoryPos = null;

    /** 记录的容器朝向 (心脏看过去的方向) */
    private EnumFacing linkedInventoryFacing = null;

    /** 扫描间隔计时器 */
    private int scanTimer = 0;

    /** 扫描间隔 (tick), 不用太频繁 */
    private static final int SCAN_INTERVAL = 60;

    /**
     * 在 update() 中调用: scanForInventory();
     * 每隔一段时间扫描一次, 找到第一个有物品容器能力的邻居就记住
     */
    private void scanForInventory() {
        if (world == null || world.isRemote) return;

        scanTimer++;
        if (scanTimer < SCAN_INTERVAL) return;
        scanTimer = 0;

        // 先检查已记录的容器是否还有效
        if (linkedInventoryPos != null) {
            if (isValidInventory(linkedInventoryPos, linkedInventoryFacing)) {
                return; // 仍然有效, 不用重新扫描
            }
            // 失效了, 清除
            linkedInventoryPos = null;
            linkedInventoryFacing = null;
            markDirty();
        }

        // 扫描6个方向, 找第一个有效容器
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos neighborPos = pos.offset(facing);
            if (isValidInventory(neighborPos, facing)) {
                linkedInventoryPos = neighborPos;
                linkedInventoryFacing = facing;
                markDirty();
                return;
            }
        }
    }

    /**
     * 检查某个位置是否是有效的物品容器
     */
    private boolean isValidInventory(BlockPos checkPos, EnumFacing facing) {
        if (world == null) return false;
        TileEntity te = world.getTileEntity(checkPos);
        if (te == null) return false;
        return te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
    }


    /**
     * 获取已绑定的容器位置, null = 没有
     * 其他地方用: heart.getLinkedInventoryPos()
     */
    public BlockPos getLinkedInventoryPos() {
        return linkedInventoryPos;
    }

    /**
     * 获取已绑定的容器朝向
     */
    public EnumFacing getLinkedInventoryFacing() {
        return linkedInventoryFacing;
    }

    /**
     * 直接拿到已绑定容器的 IItemHandler, null = 无效
     * 其他地方用: heart.getLinkedItemHandler()
     */
    public IItemHandler getLinkedItemHandler() {
        if (world == null || linkedInventoryPos == null || linkedInventoryFacing == null) return null;
        TileEntity te = world.getTileEntity(linkedInventoryPos);
        if (te == null) return null;
        if (!te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, linkedInventoryFacing.getOpposite())) {
            return null;
        }
        return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, linkedInventoryFacing.getOpposite());
    }
    /**
     * 执行一次生长
     */
    private void growOnce() {
        if (level >= MAX_LEVEL) return;

        int newLevel = level + 1;

        // 委托给生长工具类执行实际的方块放置
        boolean success = FleshTreeGrowth.growToLevel(world, originPos, pos, newLevel);

        if (success) {
            level = newLevel;
            markDirty();
            // 同步到客户端
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    /**
     * 播放心跳声
     */
    private void playHeartbeat() {
        float volume = 0.2F + level * 0.08F; // 等级越高越响
        // 第一声 "咚"
        world.playSound(null, pos,
                net.minecraft.init.SoundEvents.BLOCK_NOTE_BASEDRUM,
                SoundCategory.BLOCKS, volume, 0.5F);
    }

    // === 等级和位置访问 ===

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.min(Math.max(level, 1), MAX_LEVEL);
        markDirty();
    }

    public BlockPos getOrigin() {
        return originPos;
    }

    public void setOrigin(BlockPos origin) {
        this.originPos = origin;
        markDirty();
    }

    // === NBT 序列化 ===

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Level", level);
        compound.setInteger("GrowthTimer", growthTimer);
        compound.setInteger("OriginX", originPos.getX());
        compound.setInteger("OriginY", originPos.getY());
        compound.setInteger("OriginZ", originPos.getZ());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        level = compound.getInteger("Level");
        if (level < 1) level = 1;
        if (level > MAX_LEVEL) level = MAX_LEVEL;
        growthTimer = compound.getInteger("GrowthTimer");
        originPos = new BlockPos(
                compound.getInteger("OriginX"),
                compound.getInteger("OriginY"),
                compound.getInteger("OriginZ")
        );
    }

    // === 网络同步 ===

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
