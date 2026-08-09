package meowmel.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import gregtech.api.GTValues;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gregtech.api.mui.GTGuis;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

/** Shared one-slot UI, persistence and item capability for magic authorization hatches. */
public abstract class MetaTileEntityMagicItemHatch extends MetaTileEntityMultiblockPart {

    protected final GTItemStackHandler inventory;
    private boolean focusLocked;

    protected MetaTileEntityMagicItemHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.inventory = new GTItemStackHandler(this, 1) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (focusLocked) return stack;
                return isAcceptedStack(stack) ? super.insertItem(slot, stack, simulate) : stack;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return focusLocked ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                if (!focusLocked) super.setStackInSlot(slot, stack);
            }
        };
    }

    protected abstract boolean isAcceptedStack(ItemStack stack);

    protected abstract SimpleOverlayRenderer getOverlay();

    protected ItemStack getFocusStack() {
        return inventory.getStackInSlot(0);
    }

    public void setFocusLocked(boolean locked) {
        if (focusLocked != locked) {
            focusLocked = locked;
            markDirty();
        }
    }

    public boolean isFocusLocked() {
        return focusLocked;
    }

    @Override
    public abstract MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity);

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        guiSyncManager.registerSlotGroup("magic_focus", 1);
        return GTGuis.createPanel(this, 176, 166)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5))
                .child(SlotGroupWidget.playerInventory(false).left(7).bottom(7))
                .child(new ItemSlot().slot(SyncHandlers.itemSlot(inventory, 0)
                                .slotGroup("magic_focus")
                                .filter(this::isAcceptedStack))
                        .pos(79, 42));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("MagicFocus", inventory.serializeNBT());
        data.setBoolean("MagicFocusLocked", focusLocked);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        focusLocked = false;
        inventory.deserializeNBT(data.getCompoundTag("MagicFocus"));
        focusLocked = data.getBoolean("MagicFocusLocked");
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            getOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    @Override
    public boolean canPartShare() {
        return false;
    }
}
