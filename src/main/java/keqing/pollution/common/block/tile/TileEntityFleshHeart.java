package keqing.pollution.common.block.tile;


import keqing.pollution.api.utils.FleshTreeGrowth;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

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
        if (world == null || world.isRemote) return;

        // === 心跳声效 ===
        heartbeatTimer++;
        if (heartbeatTimer >= getHeartbeatInterval()) {
            heartbeatTimer = 0;
            playHeartbeat();
        }

        // === 生长逻辑 ===
        if (level >= MAX_LEVEL) return; // 已满级不再生长

        growthTimer++;
        if (growthTimer >= GROWTH_INTERVAL) {
            growthTimer = 0;
            growOnce();
        }
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

            // 生长时发出更响亮的声音
            world.playSound(null, pos,
                    net.minecraft.init.SoundEvents.BLOCK_SLIME_PLACE,
                    SoundCategory.BLOCKS, 1.0F, 0.5F);

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

        // 安排第二声（约0.3秒后）
        // 由于TileEntity不能直接schedule，用内部标记处理
        // 简化：只播放一声，在randomTick中偶尔播放双击
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
