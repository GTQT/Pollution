package meowmel.pollution.common.metatileentity.single;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.common.mui.widget.GTFluidSlot;
import meowmel.pollution.common.items.bauble.ItemBaubleBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nonnull;

import static meowmel.pollution.api.utils.infusedFluidStack.STACK_MAP;

/**
 * 源质充能器：消耗与饰品材质匹配的源质流体，为饰品补充源质储量
 */
public class MetaTileEntitySourceCharge extends MetaTileEntity {

    private static final String ITEM_INVENTORY_TAG = "BaubleInventory";

    /** 饰品槽位，基类字段是 IItemHandler，这里保留具体类型以便存档 */
    private GTItemStackHandler baubleInventory;

    public MetaTileEntitySourceCharge(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySourceCharge(this.metaTileEntityId);
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        this.baubleInventory = new GTItemStackHandler(this, 1);
        this.itemInventory = this.baubleInventory;
    }

    @Override
    protected FluidTankList createImportFluidHandler() {
        return new FluidTankList(false, new FluidTank(4000));
    }

    @Override
    public void update() {
        super.update();

        if (this.getWorld().isRemote) return;

        ItemStack stack = this.itemInventory.getStackInSlot(0);
        ItemBaubleBehavior behavior = ItemBaubleBehavior.getInstanceFor(stack);
        if (behavior == null) return;

        // 只有与饰品材质匹配的源质流体才能充能
        FluidStack requiredFluid = STACK_MAP.get(behavior.getMaterial());
        IFluidTank tank = this.importFluids.getTankAt(0);
        FluidStack currentFluid = tank.getFluid();
        if (requiredFluid == null || currentFluid == null || !currentFluid.isFluidEqual(requiredFluid)) return;
        if (currentFluid.amount < 1 || !behavior.addSource(1, true, stack)) return;

        behavior.addSource(1, false, stack);
        tank.drain(1, true);
    }

    public boolean isItemValid(@Nonnull ItemStack stack) {
        return ItemBaubleBehavior.getInstanceFor(stack) != null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag(ITEM_INVENTORY_TAG, this.baubleInventory.serializeNBT());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.baubleInventory.deserializeNBT(data.getCompoundTag(ITEM_INVENTORY_TAG));
    }

    @Override
    public boolean showToolUsages() {
        return false;
    }

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        var fluidSyncHandler = GTFluidSlot.sync(importFluids.getTankAt(0))
                .showAmountOnSlot(false)
                .accessibility(true, true);

        int panelWidth = 176;
        int panelHeight = 166;

        return GTGuis.createPanel(this, panelWidth, panelHeight)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(6, 6))

                .child(new ItemSlot()
                        .pos(80, 25)
                        .background(GTGuiTextures.SLOT)
                        .slot(new ModularSlot(itemInventory, 0)
                                .filter(this::isItemValid)
                                .accessibility(true, true)))

                .child(new GTFluidSlot()
                        .disableBackground()
                        .pos(80, 55)
                        .size(18)
                        .syncHandler(fluidSyncHandler))

                .bindPlayerInventory();
    }
}
