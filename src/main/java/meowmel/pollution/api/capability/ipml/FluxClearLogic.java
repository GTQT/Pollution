package meowmel.pollution.api.capability.ipml;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import meowmel.pollution.POConfig;
import meowmel.pollution.api.capability.PODataCodes;
import meowmel.pollution.common.items.behaviors.FilterBehavior;
import meowmel.pollution.common.metatileentity.single.MetaTileEntityFluxClear;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 单方块空气过滤机（{@link MetaTileEntityFluxClear}）的运行逻辑。
 * <p>
 * 写法参考 {@code gregtech.api.capability.impl.miner.MinerLogic}：
 * 机器本体只保留 UI、渲染与 Capability，具体的
 * “读取污染 -> 检查过滤器 -> 消耗能量 -> 净化污染 -> 损耗过滤器”
 * 流程以及工作状态全部由本类维护。
 */
public class FluxClearLogic {

    /** 所属机器 */
    protected final MetaTileEntityFluxClear metaTileEntity;
    /** 过滤器槽位 */
    private final ItemStackHandler containerInventory;

    /** 每 tick 消耗的能量 */
    private final long energyPerTick;
    /** 每 tick(单次) 净化的污染量，单位 Vis */
    private final double visPerTick;

    private boolean isActive;
    /** 机器是否允许工作（关闭后即使有污染也不会净化） */
    private boolean isWorkingEnabled = true;
    /** 最近一次读取到的污染值，供 UI 显示 */
    private float currentFlux;
    /** 滤芯已损耗的耐久，供 TOP 显示 */
    private int filterDamage;
    /** 滤芯的最大耐久，供 TOP 显示 */
    private int filterMaxDurability;
    /** 上一次同步给客户端的滤芯耐久 */
    private int syncedFilterDamage = -1;
    private int syncedFilterMaxDurability = -1;

    public FluxClearLogic(@NotNull MetaTileEntityFluxClear metaTileEntity,
                          @NotNull ItemStackHandler containerInventory) {
        this.metaTileEntity = metaTileEntity;
        this.containerInventory = containerInventory;
        int tier = metaTileEntity.getTier();
        this.energyPerTick = GTValues.VA[tier];
        this.visPerTick = Math.pow(2, tier - 1) * POConfig.PollutionSystemSwitch.fluxScrubberMultiplier;
    }

    /**
     * 执行一次净化，应当在机器的 update 中每 tick 调用
     */
    public void performClearing() {
        // 只在服务端工作
        if (metaTileEntity.getWorld() == null || metaTileEntity.getWorld().isRemote) return;

        updateFilterData();

        // 被关闭的机器不工作
        if (!this.isWorkingEnabled) {
            setActive(false);
            syncFilterData();
            return;
        }

        this.currentFlux = AuraHelper.getFlux(metaTileEntity.getWorld(), metaTileEntity.getPos());

        // 没有污染、没有可用的过滤器或能量不足时，机器停止工作
        if (currentFlux <= 0 || !hasValidFilter() || !drainEnergy(true)) {
            setActive(false);
            syncFilterData();
            return;
        }

        drainEnergy(false);
        AuraHelper.drainFlux(metaTileEntity.getWorld(), metaTileEntity.getPos(), (float) visPerTick, false);
        damageFilter(1);
        updateFilterData();
        setActive(true);
        syncFilterData();
    }

    /**
     * 刷新滤芯耐久数据
     */
    private void updateFilterData() {
        ItemStack stack = containerInventory.getStackInSlot(0);
        FilterBehavior behavior = FilterBehavior.getInstanceFor(stack);
        this.filterDamage = behavior == null ? 0 : AbstractMaterialPartBehavior.getPartDamage(stack);
        this.filterMaxDurability = behavior == null ? 0 : behavior.getPartMaxDurability(stack);
    }

    /**
     * 滤芯耐久变化时按秒同步给客户端（TOP 显示用），避免每 tick 发包
     */
    private void syncFilterData() {
        if (this.filterDamage == this.syncedFilterDamage &&
                this.filterMaxDurability == this.syncedFilterMaxDurability) {
            return;
        }
        if (metaTileEntity.getOffsetTimer() % 20 != 0) return;

        this.syncedFilterDamage = this.filterDamage;
        this.syncedFilterMaxDurability = this.filterMaxDurability;
        metaTileEntity.writeCustomData(PODataCodes.SYNC_FLUX_FILTER, buf -> {
            buf.writeInt(this.filterDamage);
            buf.writeInt(this.filterMaxDurability);
        });
    }

    /**
     * 消耗机器内部缓存中的能量
     *
     * @param simulate 为 true 时只做模拟，不实际消耗
     * @return 能量是否足够
     */
    protected boolean drainEnergy(boolean simulate) {
        return metaTileEntity.drainEnergy(simulate);
    }

    /**
     * 对过滤器造成损耗
     *
     * @param damage 本次造成的损耗值
     */
    protected void damageFilter(int damage) {
        FilterBehavior behavior = getFilterBehavior();
        if (behavior == null) return;

        behavior.applyDamage(containerInventory.getStackInSlot(0), damage);
    }

    /**
     * @return 槽位中是否有可用的过滤器
     */
    public boolean hasValidFilter() {
        return getFilterBehavior() != null;
    }

    /**
     * @return 该物品能否作为过滤器放入机器
     */
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return FilterBehavior.getInstanceFor(stack) != null;
    }

    /**
     * @return 槽位中过滤器对应的行为，没有则返回 null
     */
    @Nullable
    public FilterBehavior getFilterBehavior() {
        ItemStack stack = containerInventory.getStackInSlot(0);
        if (stack.isEmpty()) return null;

        return FilterBehavior.getInstanceFor(stack);
    }

    /**
     * @return 机器当前是否正在净化污染
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * 更新机器的工作状态，状态变化时会同步给客户端用于渲染
     *
     * @param active 机器的新状态
     */
    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            metaTileEntity.markDirty();
            if (metaTileEntity.getWorld() != null && !metaTileEntity.getWorld().isRemote) {
                metaTileEntity.writeCustomData(GregtechDataCodes.WORKABLE_ACTIVE, buf -> buf.writeBoolean(active));
            }
        }
    }

    /**
     * 更新机器是否允许工作，状态变化时会同步给客户端用于渲染
     *
     * @param workingEnabled 机器是否允许工作
     */
    public void setWorkingEnabled(boolean workingEnabled) {
        if (this.isWorkingEnabled != workingEnabled) {
            this.isWorkingEnabled = workingEnabled;
            if (!workingEnabled) setActive(false);
            metaTileEntity.markDirty();
            if (metaTileEntity.getWorld() != null && !metaTileEntity.getWorld().isRemote) {
                metaTileEntity.writeCustomData(GregtechDataCodes.WORKING_ENABLED,
                        buf -> buf.writeBoolean(workingEnabled));
            }
        }
    }

    /**
     * @return 机器是否允许工作
     */
    public boolean isWorkingEnabled() {
        return isWorkingEnabled;
    }

    /**
     * @return 机器当前是否正在工作（处于净化状态且允许工作）
     */
    public boolean isWorking() {
        return isActive && isWorkingEnabled;
    }

    /**
     * @return 每 tick 消耗的能量
     */
    public long getEnergyPerTick() {
        return energyPerTick;
    }

    /**
     * @return 每 tick(单次) 净化的污染量
     */
    public double getVisPerTick() {
        return visPerTick;
    }

    /**
     * @return 最近一次读取到的污染值
     */
    public float getCurrentFlux() {
        return currentFlux;
    }

    /**
     * @return 滤芯已损耗的耐久
     */
    public int getFilterDamage() {
        return filterDamage;
    }

    /**
     * @return 滤芯的最大耐久，为 0 表示没有滤芯
     */
    public int getFilterMaxDurability() {
        return filterMaxDurability;
    }

    /**
     * 写入所有需要保存的数据
     * 必须在机器的 {@link MetaTileEntityFluxClear#writeToNBT(NBTTagCompound)} 中调用并返回
     */
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound data) {
        data.setBoolean("isActive", this.isActive);
        data.setBoolean("isWorkingEnabled", this.isWorkingEnabled);
        return data;
    }

    /**
     * 读取之前保存的数据
     * 必须在机器的 {@link MetaTileEntityFluxClear#readFromNBT(NBTTagCompound)} 中调用
     */
    public void readFromNBT(@NotNull NBTTagCompound data) {
        this.isActive = data.getBoolean("isActive");
        // 旧存档没有该字段，默认视为允许工作
        this.isWorkingEnabled = !data.hasKey("isWorkingEnabled") || data.getBoolean("isWorkingEnabled");
    }

    /**
     * 写入初始同步数据
     * 必须在机器的 {@link MetaTileEntityFluxClear#writeInitialSyncData(PacketBuffer)} 中调用
     */
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        updateFilterData();
        buf.writeBoolean(this.isActive);
        buf.writeBoolean(this.isWorkingEnabled);
        buf.writeInt(this.filterDamage);
        buf.writeInt(this.filterMaxDurability);
    }

    /**
     * 读取初始同步数据
     * 必须在机器的 {@link MetaTileEntityFluxClear#receiveInitialSyncData(PacketBuffer)} 中调用
     */
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        this.isActive = buf.readBoolean();
        this.isWorkingEnabled = buf.readBoolean();
        this.filterDamage = buf.readInt();
        this.filterMaxDurability = buf.readInt();
        this.syncedFilterDamage = this.filterDamage;
        this.syncedFilterMaxDurability = this.filterMaxDurability;
    }

    /**
     * 读取自定义同步数据
     * 必须在机器的 {@link MetaTileEntityFluxClear#receiveCustomData(int, PacketBuffer)} 中调用
     */
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
            this.isActive = buf.readBoolean();
            metaTileEntity.scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.isWorkingEnabled = buf.readBoolean();
            metaTileEntity.scheduleRenderUpdate();
        } else if (dataId == PODataCodes.SYNC_FLUX_FILTER) {
            this.filterDamage = buf.readInt();
            this.filterMaxDurability = buf.readInt();
        }
    }
}
