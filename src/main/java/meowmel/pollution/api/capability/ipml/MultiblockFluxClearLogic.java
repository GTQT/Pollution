package meowmel.pollution.api.capability.ipml;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import meowmel.pollution.POConfig;
import meowmel.pollution.api.capability.IFilterHatch;
import meowmel.pollution.api.capability.PODataCodes;
import meowmel.pollution.common.items.behaviors.FilterBehavior;
import meowmel.pollution.common.metatileentity.multiblock.IFluxClearType;
import meowmel.pollution.common.metatileentity.multiblock.MetaTileEntityLargeFluxClear;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aura.AuraHelper;

/**
 * 大型空气过滤机（{@link MetaTileEntityLargeFluxClear}）的运行逻辑。
 * <p>
 * 把“遍历周围区块 -> 读取污染 -> 消耗能量 -> 净化污染 -> 损耗滤芯”的机制
 * 从多方块控制器中抽离出来，控制器只负责结构与 UI。
 * 滤芯不再放在控制器里，而是由结构中的滤芯仓（{@link IFilterHatch}）提供。
 */
public class MultiblockFluxClearLogic {

    /** 所属机器 */
    protected final MetaTileEntityLargeFluxClear metaTileEntity;

    /** 清理半径，单位区块 */
    private final int radius;
    /** 每个区块每次净化消耗的能量 */
    private final long energyPerOperation;
    /** 每次净化的污染量，单位 Vis */
    private final double visPerTick;

    /** 最近一次读取到的污染值，供 UI 显示 */
    private float flux;
    /** 当前滤芯已工作的时间 */
    private long workTime;
    /** 当前滤芯的最大工作时间 */
    private long maxWorkTime;
    /** 上一次同步给客户端的滤芯耐久 */
    private long syncedWorkTime = -1;
    private long syncedMaxWorkTime = -1;

    public MultiblockFluxClearLogic(MetaTileEntityLargeFluxClear metaTileEntity, IFluxClearType type) {
        this.metaTileEntity = metaTileEntity;
        this.radius = type.getRadius();
        this.energyPerOperation = GTValues.V[type.getTier()];
        this.visPerTick = Math.pow(4, type.getTier() - 1) * POConfig.PollutionSystemSwitch.fluxScrubberMultiplier;
    }

    /**
     * 执行一次净化，应当在控制器的 updateFormedValid 中每 tick 调用
     */
    public void performClearing() {
        World world = metaTileEntity.getWorld();
        if (world == null || world.isRemote) return;

        IFilterHatch hatch = metaTileEntity.getFilterHatch();
        ItemStack filter = hatch == null ? ItemStack.EMPTY : hatch.getFilterStack();
        FilterBehavior behavior = FilterBehavior.getInstanceFor(filter);

        // 被关闭（软锤）或没有可用滤芯时停止工作
        if (!metaTileEntity.isWorkingEnabled() || behavior == null) {
            this.workTime = 0;
            this.maxWorkTime = 0;
            metaTileEntity.setActive(false);
            syncFilterData();
            return;
        }

        this.workTime = AbstractMaterialPartBehavior.getPartDamage(filter);
        this.maxWorkTime = behavior.getPartMaxDurability(filter);

        // 遍历以机器为中心的区块并清理污染，任意一个区块被清理即视为工作中
        boolean worked = false;
        IEnergyContainer energy = metaTileEntity.getEnergyContainer();
        BlockPos origin = metaTileEntity.getPos();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos pos = new BlockPos(origin.getX() + x * 16, origin.getY(), origin.getZ() + z * 16);
                float fluxThisChunk = AuraHelper.getFlux(world, pos);
                if (fluxThisChunk <= 0) continue;

                this.flux = fluxThisChunk;
                if (energy != null && energy.getEnergyStored() >= energyPerOperation) {
                    energy.removeEnergy(energyPerOperation);
                    AuraHelper.drainFlux(world, pos, (float) visPerTick, false);
                    hatch.damageFilter(1);
                    worked = true;
                }
            }
        }
        metaTileEntity.setActive(worked);
        syncFilterData();
    }

    /**
     * 滤芯耐久变化时按秒同步给客户端（TOP 显示用），避免每 tick 发包
     */
    private void syncFilterData() {
        if (this.workTime == this.syncedWorkTime && this.maxWorkTime == this.syncedMaxWorkTime) return;
        if (metaTileEntity.getOffsetTimer() % 20 != 0) return;

        this.syncedWorkTime = this.workTime;
        this.syncedMaxWorkTime = this.maxWorkTime;
        metaTileEntity.writeCustomData(PODataCodes.SYNC_FLUX_FILTER, buf -> {
            buf.writeLong(this.workTime);
            buf.writeLong(this.maxWorkTime);
        });
    }

    /**
     * @return 滤芯仓中是否有可用的滤芯
     */
    public boolean hasValidFilter() {
        IFilterHatch hatch = metaTileEntity.getFilterHatch();
        return hatch != null && FilterBehavior.getInstanceFor(hatch.getFilterStack()) != null;
    }

    /**
     * @return 清理半径（区块）
     */
    public int getRadius() {
        return radius;
    }

    /**
     * @return 每个区块每次净化消耗的能量
     */
    public long getEnergyPerOperation() {
        return energyPerOperation;
    }

    /**
     * @return 每次净化的污染量
     */
    public double getVisPerTick() {
        return visPerTick;
    }

    /**
     * @return 最近一次读取到的污染值
     */
    public float getFlux() {
        return flux;
    }

    /**
     * @return 当前滤芯已工作的时间
     */
    public long getWorkTime() {
        return workTime;
    }

    /**
     * @return 当前滤芯的最大工作时间
     */
    public long getMaxWorkTime() {
        return maxWorkTime;
    }

    /**
     * @return 滤芯已损耗的耐久
     */
    public int getFilterDamage() {
        return (int) workTime;
    }

    /**
     * @return 滤芯的最大耐久，为 0 表示没有滤芯
     */
    public int getFilterMaxDurability() {
        return (int) maxWorkTime;
    }

    /**
     * 写入初始同步数据
     * 必须在控制器的 {@link MetaTileEntityLargeFluxClear#writeInitialSyncData(PacketBuffer)} 中调用
     */
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        buf.writeLong(this.workTime);
        buf.writeLong(this.maxWorkTime);
    }

    /**
     * 读取初始同步数据
     * 必须在控制器的 {@link MetaTileEntityLargeFluxClear#receiveInitialSyncData(PacketBuffer)} 中调用
     */
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        this.workTime = buf.readLong();
        this.maxWorkTime = buf.readLong();
        this.syncedWorkTime = this.workTime;
        this.syncedMaxWorkTime = this.maxWorkTime;
    }

    /**
     * 读取自定义同步数据
     * 必须在控制器的 {@link MetaTileEntityLargeFluxClear#receiveCustomData(int, PacketBuffer)} 中调用
     */
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        if (dataId == PODataCodes.SYNC_FLUX_FILTER) {
            this.workTime = buf.readLong();
            this.maxWorkTime = buf.readLong();
        }
    }
}
