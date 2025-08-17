package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import keqing.gtqtcore.api.utils.GTQTDateHelper;
import keqing.gtqtcore.client.textures.GTQTTextures;
import keqing.pollution.POConfig;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.items.behaviors.FilterBehavior;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntityFluxClear extends TieredMetaTileEntity {
    private final double VisTicks;
    private final long energyAmountPer;
    private final ItemStackHandler containerInventory;
    int tier;
    long TotalTick;
    long workTime;
    private boolean isActive;

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.containerInventory) :
                super.getCapability(capability, side);
    }
    @Override
    public void onRemoval() {
        super.onRemoval();
        for (int i = 0; i < containerInventory.getSlots(); i++) {
            var pos = getPos();
            if(!containerInventory.getStackInSlot(i).isEmpty())
            {
                getWorld().spawnEntity(new EntityItem(getWorld(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,containerInventory.getStackInSlot(i)));
                containerInventory.extractItem(i,1,false);
            }

        }
    }
    public MetaTileEntityFluxClear(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.tier = tier;
        this.VisTicks =  Math.pow(2,tier-1) * POConfig.PollutionSystemSwitch.fluxScrubberMultiplier;
        this.energyAmountPer = GTValues.VA[tier];
        this.containerInventory = new GTItemStackHandler(this, 1);
        initializeInventory();
    }

    @Override
    public MetaTileEntityFluxClear createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityFluxClear(metaTileEntityId, getTier());
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
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 180, 240);
        builder.dynamicLabel(28, 12, () -> "污染清洁机 等级：" + tier, 0xFFFFFF);
        builder.widget(new SlotWidget(containerInventory, 0, 8, 8, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("输入槽位"));

        builder.image(4, 28, 172, 128, GuiTextures.DISPLAY);
        builder.widget((new AdvancedTextWidget(8, 32, this::addDisplayText, 16777215)).setMaxWidthLimit(180));
        builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 8, 160);
        return builder.build(getHolder(), entityPlayer);
    }


    @Override
    public void update() {
        super.update();
        if (getWorld().isRemote) return;
        // 确保槽位存在且不为空
        ItemStack stack = containerInventory.getStackInSlot(0);
        if (stack.isEmpty()) {
            workTime = 0;
            TotalTick = 0;
            return;
        }

        // 确保行为对象不为 null
        FilterBehavior behavior = getFilterBehavior();
        if (behavior == null) {
            workTime = 0;
            TotalTick = 0;
            return;
        }

        if (!isItemValid(stack))
        {
            workTime = 0;
            TotalTick = 0;
            return;
        }

        workTime = AbstractMaterialPartBehavior.getPartDamage(containerInventory.getStackInSlot(0));
        TotalTick = behavior.getPartMaxDurability(containerInventory.getStackInSlot(0));

        if (AuraHelper.getFlux(getWorld(), getPos()) > 0 && isItemValid(stack)) {
            if (energyContainer.getEnergyStored() >= energyAmountPer) {
                isActive = true;
                energyContainer.removeEnergy(energyAmountPer);
                AuraHelper.drainFlux(getWorld(), getPos(), (float) VisTicks, false);
                behavior.applyDamage(containerInventory.getStackInSlot(0), 1);
            } else {
                isActive = false;
                workTime = 0;
                TotalTick = 0;
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

    protected void addDisplayText(List<ITextComponent> textList) {
        textList.add(new TextComponentString("当前状态: " + (isActive() ? "运行中" : "停止")));
        textList.add(new TextComponentString("当前污染: " + AuraHelper.getFlux(getWorld(), getPos())));
        textList.add(new TextComponentString("清理速率: " + VisTicks));
        textList.add(new TextComponentString("已经工作: " + GTQTDateHelper.getTimeFromTicks(workTime)));
        textList.add(new TextComponentString("距离损坏: " + GTQTDateHelper.getTimeFromTicks(TotalTick - workTime)));
        if(!isItemValid(containerInventory.getStackInSlot(0)))return;
        if (isActive())
            textList.add(new TextComponentString("过滤器材料: " + getFilterBehavior().getMaterial().getLocalizedName()));
        if (isActive()) textList.add(new TextComponentString("过滤等级: " + getFilterBehavior().getFilterTier()));
    }

    public boolean isActive() {
        return super.isActive() && this.isActive;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        GTQTTextures.LARGE_ROCKET_ENGINE_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return POTextures.MAGIC_VOLTAGE_CASINGS[getTier()];
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.flux_clear.tire", tier, VisTicks));
        tooltip.add(I18n.format("pollution.flux_clear.tooltip"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", this.energyContainer.getInputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
    }
}
