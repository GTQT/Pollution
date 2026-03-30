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
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.RichTextWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import gregtech.api.GTValues;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.util.GTTransferUtils;
import gregtech.client.renderer.texture.Textures;
import lombok.Getter;
import meowmel.pollution.POConfig;
import meowmel.pollution.common.items.behaviors.FilterBehavior;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntityFluxClear extends TieredMetaTileEntity {

    private final double VisTicks;
    private final long energyAmountPer;
    private final ItemStackHandler containerInventory;

    @Getter
    private boolean isActive;

    public MetaTileEntityFluxClear(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.VisTicks = Math.pow(2, tier - 1) * POConfig.PollutionSystemSwitch.fluxScrubberMultiplier;
        this.energyAmountPer = GTValues.VA[tier];
        this.containerInventory = new GTItemStackHandler(this, 1);
    }

    @Override
    public MetaTileEntityFluxClear createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityFluxClear(metaTileEntityId, getTier());
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

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("ContainerInventory", this.containerInventory.serializeNBT());
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.containerInventory.deserializeNBT(data.getCompoundTag("ContainerInventory"));
    }

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
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

                .child(GTGuiTextures.DISPLAY.asWidget()
                        .pos(4, 28)
                        .size(172, 128))

                .child(new RichTextWidget()
                        .pos(8, 32)
                        .size(172, 128)
                        .textColor(Color.WHITE.main)
                        .autoUpdate(true)
                        .textBuilder(richText -> {
                            BooleanSyncValue isActive = new BooleanSyncValue(this::isActive);
                            richText.addLine(IKey.lang("当前状态: " + (isActive.getValue() ? "运行中" : "停止")));

                            FloatSyncValue pollution = new FloatSyncValue(() -> AuraHelper.getFlux(getWorld(), getPos()));
                            richText.addLine(IKey.lang("当前污染: " + pollution.getValue()));

                            DoubleSyncValue visTicks = new DoubleSyncValue(() -> VisTicks);
                            richText.addLine(IKey.lang("清理速率: " + visTicks.getValue()));
                        }))


                .bindPlayerInventory();
    }


    @Override
    public void update() {
        super.update();
        if (getWorld().isRemote) return;
        // 确保槽位存在且不为空
        ItemStack stack = containerInventory.getStackInSlot(0);
        if (stack.isEmpty()) {
            return;
        }

        FilterBehavior behavior = getFilterBehavior();
        if (behavior == null) {
            return;
        }

        if (!isItemValid(stack)) {
            return;
        }

        if (AuraHelper.getFlux(getWorld(), getPos()) > 0) {
            if (energyContainer.getEnergyStored() >= energyAmountPer) {
                isActive = true;
                energyContainer.removeEnergy(energyAmountPer);
                AuraHelper.drainFlux(getWorld(), getPos(), (float) VisTicks, false);
                behavior.applyDamage(containerInventory.getStackInSlot(0), 1);
            } else {
                isActive = false;
            }
        }
    }


    public boolean isItemValid(@Nonnull ItemStack stack) {
        return FilterBehavior.getInstanceFor(stack) != null;
    }

    @Nullable
    private FilterBehavior getFilterBehavior() {
        ItemStack stack = containerInventory.getStackInSlot(0);
        if (stack.isEmpty()) return null;

        return FilterBehavior.getInstanceFor(stack);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.GAS_COLLECTOR_OVERLAY.renderOrientedState(renderState, translation, pipeline, getFrontFacing(), this.isActive(),
                energyContainer.getEnergyStored() > 0);
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.flux_clear.tire", getTier(), VisTicks));
        tooltip.add(I18n.format("pollution.flux_clear.tooltip"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", this.energyContainer.getInputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
    }
}
