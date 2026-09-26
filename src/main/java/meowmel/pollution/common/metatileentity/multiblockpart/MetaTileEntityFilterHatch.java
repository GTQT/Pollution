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
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.mui.GTGuis;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import meowmel.pollution.api.capability.IFilterHatch;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.common.items.behaviors.FilterBehavior;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 滤芯仓：为大型空气过滤机提供滤芯，控制器本身不再持有滤芯槽位
 */
public class MetaTileEntityFilterHatch extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<IFilterHatch>, IFilterHatch {

    private final GTItemStackHandler inventory;

    public MetaTileEntityFilterHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.inventory = new GTItemStackHandler(this, 1) {

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return MetaTileEntityFilterHatch.this.isItemValid(stack) ? super.insertItem(slot, stack, simulate) : stack;
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                if (stack.isEmpty() || MetaTileEntityFilterHatch.this.isItemValid(stack)) {
                    super.setStackInSlot(slot, stack);
                }
            }
        };
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityFilterHatch(metaTileEntityId, getTier());
    }

    @Override
    public MultiblockAbility<IFilterHatch> getAbility() {
        return POMultiblockAbility.FILTER_HATCH;
    }

    @Override
    public void registerAbilities(@NotNull AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public ItemStack getFilterStack() {
        return inventory.getStackInSlot(0);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && FilterBehavior.getInstanceFor(stack) != null;
    }

    @Override
    public void damageFilter(int damage) {
        ItemStack filter = getFilterStack();
        FilterBehavior behavior = FilterBehavior.getInstanceFor(filter);
        if (behavior != null) {
            behavior.applyDamage(filter, damage);
        }
    }

    @Override
    public int getFilterDamage() {
        return AbstractMaterialPartBehavior.getPartDamage(getFilterStack());
    }

    @Override
    public int getFilterMaxDurability() {
        FilterBehavior behavior = FilterBehavior.getInstanceFor(getFilterStack());
        return behavior == null ? 0 : behavior.getPartMaxDurability(getFilterStack());
    }

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        guiSyncManager.registerSlotGroup("filter", 1);
        return GTGuis.createPanel(this, 176, 166)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5))
                .child(new ItemSlot()
                        .slot(SyncHandlers.itemSlot(inventory, 0)
                                .slotGroup("filter")
                                .filter(this::isItemValid))
                        .pos(80, 35))
                .child(SlotGroupWidget.playerInventory(false).left(7).bottom(7));
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            Textures.FILTER_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.filter_hatch.tooltip.1"));
    }

    @Override
    public void clearMachineInventory(@NotNull List<@NotNull ItemStack> itemBuffer) {
        super.clearMachineInventory(itemBuffer);
        clearInventory(itemBuffer, inventory);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("FilterInventory", inventory.serializeNBT());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inventory.deserializeNBT(data.getCompoundTag("FilterInventory"));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean canPartShare() {
        return false;
    }
}
