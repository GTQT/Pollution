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
import gregtech.api.unification.material.Material;
import gregtech.common.mui.widget.GTFluidSlot;
import meowmel.pollution.common.items.bauble.ItemBaubleBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static meowmel.pollution.api.utils.infusedFluidStack.STACK_MAP;

public class MetaTileEntitySourceCharge extends MetaTileEntity {

    public MetaTileEntitySourceCharge(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.initializeInventory();
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySourceCharge(this.metaTileEntityId);
    }

    protected void initializeInventory() {
        super.initializeInventory();
        this.itemInventory = new GTItemStackHandler(this, 1);
    }

    @Override
    protected FluidTankList createImportFluidHandler() {
        return new FluidTankList(false, new FluidTank(4000));
    }

    public void update() {
        super.update();

        if (this.getWorld().isRemote || this.itemInventory.getStackInSlot(0).isEmpty()) {
            return;
        }

        ItemStack stack = this.itemInventory.getStackInSlot(0);
        // 确保行为对象不为 null
        ItemBaubleBehavior behavior = getItemBaubleBehavior();
        if (behavior == null) {
            return;
        }
        if (isItemValid(stack)) {
            Material material = behavior.getMaterial();
            FluidStack fluidStack = STACK_MAP.get(material);
            FluidStack currentFluid = this.importFluids.getTankAt(0).getFluid();
            if (currentFluid != null && currentFluid.equals(fluidStack) && currentFluid.amount >= 1) {
                if (behavior.addSource(1, true, stack)) {
                    behavior.addSource(1, false, stack);
                    this.importFluids.getTankAt(0).drain(1, true);
                }
            }
        }
    }

    public boolean isItemValid(@Nonnull ItemStack stack) {
        return ItemBaubleBehavior.getInstanceFor(stack) != null;
    }

    @Nullable
    private ItemBaubleBehavior getItemBaubleBehavior() {
        ItemStack stack = itemInventory.getStackInSlot(0);
        if (stack.isEmpty()) return null;

        return ItemBaubleBehavior.getInstanceFor(stack);
    }

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
                                .accessibility(true, true)))

                .child(new GTFluidSlot()
                        .disableBackground()
                        .pos(80, 55)
                        .size(18)
                        .syncHandler(fluidSyncHandler))

                .bindPlayerInventory();
    }

    public int getItemStackLimit(ItemStack stack) {
        return super.getItemStackLimit(stack);
    }

    protected boolean shouldSerializeInventories() {
        return false;
    }
}
