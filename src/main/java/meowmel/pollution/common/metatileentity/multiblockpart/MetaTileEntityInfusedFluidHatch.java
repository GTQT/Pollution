package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.RichTextWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.FilteredItemHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.mui.sync.GTFluidSyncHandler;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gregtech.common.metatileentities.storage.MetaTileEntityQuantumTank;
import gregtech.common.mui.widget.GTFluidSlot;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetaTileEntityInfusedFluidHatch extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<IFluidTank>, IFluidTank {

    public static final int INITIAL_INVENTORY_SIZE = 8000;

    public MetaTileEntityInfusedFluidHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        initializeInventory();
    }

    protected int getInventorySize() {
        return INITIAL_INVENTORY_SIZE * Math.min(Integer.MAX_VALUE, 1 << getTier());
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityInfusedFluidHatch(metaTileEntityId, getTier());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("ContainerInventory")) {
            MetaTileEntityQuantumTank.legacyTankItemHandlerNBTReading(this, data.getCompoundTag("ContainerInventory"),
                    0, 1);
        }
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            fillContainerFromInternalTank(importFluids);
            fillInternalTankFromFluidContainer();
            pullFluidsFromNearbyHandlers(getFrontFacing());
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            Textures.PIPE_IN_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
            Textures.FLUID_HATCH_INPUT_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    @Override
    protected FluidTankList createImportFluidHandler() {
        return new FluidTankList(false, new FluidTank(getInventorySize()));
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return new FilteredItemHandler(this, 1).setFillPredicate(
                FilteredItemHandler.getCapabilityFilter(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY));
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new GTItemStackHandler(this, 1);
    }

    @Override
    public MultiblockAbility<IFluidTank> getAbility() {
        return POMultiblockAbility.INFUSED_FLUID_HATCH;
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        guiSyncManager.registerSlotGroup("item_inv", 2);

        GTFluidSyncHandler tankSyncHandler = GTFluidSlot.sync(this.importFluids.getTankAt(0))
                .showAmountOnSlot(false);

        return GTGuis.createPanel(this, 176, 166)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5))
                .child(SlotGroupWidget.playerInventory(false).left(7).bottom(7))
                .child((GTGuiTextures.DISPLAY).asWidget()
                        .left(7).top(16)
                        .size(81, 55))
                .child(GTGuiTextures.TANK_ICON.asWidget()
                        .left(92).top(36)
                        .size(14, 15))
                .child(new RichTextWidget()
                        .size(75, 47)
                        .pos(10, 20)
                        .textColor(Color.WHITE.main)
                        .alignment(Alignment.TopLeft)
                        .autoUpdate(true)
                        .textBuilder(richText -> {
                            richText.addLine(IKey.lang("gregtech.gui.fluid_amount"));
                            String name = tankSyncHandler.getFluidLocalizedName();
                            if (name == null) return;

                            richText.addLine(IKey.str(name));
                            richText.addLine(IKey.str(tankSyncHandler.getFormattedFluidAmount()));
                        }))
                .child(new GTFluidSlot().syncHandler(tankSyncHandler)
                        .pos(69, 52)
                        .disableBackground())
                .child(new ItemSlot().slot(SyncHandlers.itemSlot(this.importItems, 0)
                                .slotGroup("item_inv")
                                .filter(itemStack -> FluidUtil.getFluidHandler(itemStack) != null))
                        .pos(90, 16))
                .child(new ItemSlot().slot(SyncHandlers.itemSlot(this.exportItems, 0)
                                .slotGroup("item_inv")
                                .accessibility(false, true))
                        .pos(90, 53));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity", getInventorySize()));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }


    public IMultipleTankHandler.ITankEntry getTank() {
        return this.importFluids.getFluidTanks().get(0);
    }

    @Override
    public @Nullable FluidStack getFluid() {
        return getTank().getFluid();
    }

    @Override
    public int getFluidAmount() {
        return getTank().getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return getTank().getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        return getTank().getInfo();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return getTank().fill(resource, doFill);
    }

    @Override
    public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
        return getTank().drain(maxDrain, doDrain);
    }
}
