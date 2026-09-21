package meowmel.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.FloatSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.RichTextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import gregtech.api.GTValues;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.tooltips.InformationHandler;
import meowmel.pollution.api.capability.IFluxClearInfo;
import meowmel.pollution.api.capability.ipml.FluxClearLogic;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;

public class MetaTileEntityFluxClear extends TieredMetaTileEntity implements IFluxClearInfo {

    private final ItemStackHandler containerInventory;
    private final FluxClearLogic fluxClearLogic;

    public MetaTileEntityFluxClear(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.containerInventory = new GTItemStackHandler(this, 1);
        this.fluxClearLogic = new FluxClearLogic(this, this.containerInventory);
    }

    @Override
    public MetaTileEntityFluxClear createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityFluxClear(metaTileEntityId, getTier());
    }

    /**
     * @return 机器的运行逻辑
     */
    public FluxClearLogic getFluxClearLogic() {
        return fluxClearLogic;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.containerInventory) :
                super.getCapability(capability, side);
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        GTTransferUtils.dropInventoryItems(getWorld(), getPos(), this.containerInventory);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("ContainerInventory", this.containerInventory.serializeNBT());
        return this.fluxClearLogic.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.containerInventory.deserializeNBT(data.getCompoundTag("ContainerInventory"));
        this.fluxClearLogic.readFromNBT(data);
    }

    @Override
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        this.fluxClearLogic.writeInitialSyncData(buf);
    }

    @Override
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.fluxClearLogic.receiveInitialSyncData(buf);
    }

    @Override
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        this.fluxClearLogic.receiveCustomData(dataId, buf);
    }

    @Override
    public void update() {
        super.update();
        this.fluxClearLogic.performClearing();
    }

    /**
     * 消耗机器内部缓存中的能量，由逻辑调用
     *
     * @param simulate 为 true 时只做模拟，不实际消耗
     * @return 能量是否足够
     */
    public boolean drainEnergy(boolean simulate) {
        long energyPerTick = this.fluxClearLogic.getEnergyPerTick();
        if (energyContainer.getEnergyStored() < energyPerTick) return false;

        if (!simulate) {
            energyContainer.removeEnergy(energyPerTick);
        }
        return true;
    }

    @Override
    public boolean isActive() {
        return this.fluxClearLogic.isActive();
    }

    /**
     * @return 机器是否允许工作
     */
    public boolean isWorkingEnabled() {
        return this.fluxClearLogic.isWorkingEnabled();
    }

    /**
     * 设置机器是否允许工作
     *
     * @param workingEnabled 机器是否允许工作
     */
    public void setWorkingEnabled(boolean workingEnabled) {
        this.fluxClearLogic.setWorkingEnabled(workingEnabled);
    }

    @Override
    public float getCurrentFlux() {
        World world = getWorld();
        return world == null ? 0.0f : AuraHelper.getFlux(world, getPos());
    }

    @Override
    public double getVisPerTick() {
        return this.fluxClearLogic.getVisPerTick();
    }

    @Override
    public int getFilterDamage() {
        return this.fluxClearLogic.getFilterDamage();
    }

    @Override
    public int getFilterMaxDurability() {
        return this.fluxClearLogic.getFilterMaxDurability();
    }

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager panelSyncManager, UISettings settings) {
        BooleanSyncValue isActiveSync = new BooleanSyncValue(fluxClearLogic::isActive);
        BooleanSyncValue isWorkingEnabledSync = new BooleanSyncValue(
                fluxClearLogic::isWorkingEnabled,
                val -> {
                    if (!getWorld().isRemote) {
                        fluxClearLogic.setWorkingEnabled(val);
                    }
                });
        FloatSyncValue fluxSync = new FloatSyncValue(fluxClearLogic::getCurrentFlux);
        DoubleSyncValue visTicksSync = new DoubleSyncValue(fluxClearLogic::getVisPerTick);
        IntSyncValue filterDamageSync = new IntSyncValue(fluxClearLogic::getFilterDamage);
        IntSyncValue filterMaxSync = new IntSyncValue(fluxClearLogic::getFilterMaxDurability);
        panelSyncManager.syncValue("active", isActiveSync);
        panelSyncManager.syncValue("workingEnabled", isWorkingEnabledSync);
        panelSyncManager.syncValue("flux", fluxSync);
        panelSyncManager.syncValue("visTicks", visTicksSync);
        panelSyncManager.syncValue("filterDamage", filterDamageSync);
        panelSyncManager.syncValue("filterMax", filterMaxSync);

        return GTGuis.createPanel(this, 180, 240)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(28, 12))

                .child(new ItemSlot()
                        .pos(8, 8)
                        .background(GTGuiTextures.SLOT)
                        .slot(new ModularSlot(containerInventory, 0)
                                .accessibility(true, true))
                        .tooltip(tooltip -> tooltip
                                .addLine(IKey.lang("输入槽位")))
                )

                .child(new ToggleButton()
                        .pos(28, 8)
                        .overlay(true, GTGuiTextures.BUTTON_POWER[1])
                        .overlay(false, GTGuiTextures.BUTTON_POWER[0])
                        .value(isWorkingEnabledSync)
                        .addTooltipLine("设置电源开关")
                )

                .child(GTGuiTextures.DISPLAY.asWidget()
                        .pos(4, 28)
                        .size(172, 128))

                .child(new RichTextWidget()
                        .pos(8, 32)
                        .size(172, 128)
                        .textColor(Color.WHITE.main)
                        .autoUpdate(true)
                        .textBuilder(richText -> {
                            richText.addLine(IKey.lang("当前状态: " + (isActiveSync.getValue() ? "运行中" :
                                    (isWorkingEnabledSync.getValue() ? "停止" : "已暂停"))));
                            richText.addLine(IKey.lang("当前污染: " + fluxSync.getValue()));
                            richText.addLine(IKey.lang("清理速率: " + visTicksSync.getValue()));
                            int filterMax = filterMaxSync.getIntValue();
                            richText.addLine(IKey.lang(filterMax <= 0
                                    ? "滤芯耐久: 无滤芯"
                                    : "滤芯耐久: " + (filterMax - filterDamageSync.getIntValue()) + " / " + filterMax));
                        }))


                .bindPlayerInventory();
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        POTextures.FLUX_CLEAR_OVERLAY.renderOrientedState(renderState, translation, pipeline, getFrontFacing(), fluxClearLogic.isActive(),
                fluxClearLogic.isWorkingEnabled());
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("清理污染！", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.flux_clear.tire", getTier(), fluxClearLogic.getVisPerTick()));
        tooltip.add(I18n.format("pollution.flux_clear.tooltip"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", energyContainer.getInputVoltage(),
                GTValues.VNF[getTier()]));
        tooltip.add(
                I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
    }
}
