package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.ImageWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import keqing.gtqtcore.api.utils.GTQTDateHelper;
import keqing.pollution.POConfig;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.items.behaviors.FilterBehavior;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntityVisClear extends TieredMetaTileEntity {
    private final double VisTicks;
    private final long energyAmountPer;
    private final ItemStackHandler containerInventory;
    int tier;
    long TotalTick;
    long workTime;
    private boolean isActive;

    public MetaTileEntityVisClear(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.tier = tier;
        this.VisTicks = tier * POConfig.PollutionSystemSwitch.fluxScrubberMultiplier;
        this.energyAmountPer = GTValues.VA[tier];
        this.containerInventory = new GTItemStackHandler(this, 1);
        initializeInventory();
    }

    @Override
    public MetaTileEntityVisClear createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityVisClear(metaTileEntityId, getTier());
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
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 229);
        builder .bindPlayerInventory(entityPlayer.inventory, 146);
        builder.dynamicLabel(7, 10, () -> "Vis Clear", 0x232323);
        builder.dynamicLabel(7, 30, () -> "Tier: " + this.getTier(), 0x232323);
        builder.dynamicLabel(7, 50, () -> "Vis: " + AuraHelper.getFlux(getWorld(), getPos()), 0x232323);
        builder.widget(new SlotWidget(this.containerInventory, 0, 88 - 9, 70, true, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT)
                        .setChangeListener(this::markDirty)
                        .setTooltipText("请放入过滤器"))
                .widget(new ImageWidget(88 - 9, 88, 18, 6, GuiTextures.BUTTON_POWER_DETAIL));
        builder.widget((new AdvancedTextWidget(7, 96, this::addDisplayText, 2302755)).setMaxWidthLimit(181));
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

        if (AuraHelper.getFlux(getWorld(), getPos()) > 0 && isItemValid(stack)) {
            if (energyContainer.getEnergyStored() >= energyAmountPer) {
                isActive = true;
                energyContainer.removeEnergy(energyAmountPer);
                AuraHelper.drainFlux(getWorld(), getPos(), (float) VisTicks, false);
                behavior.applyDamage(containerInventory.getStackInSlot(0), 1);
                workTime = AbstractMaterialPartBehavior.getPartDamage(containerInventory.getStackInSlot(0));
                TotalTick = behavior.getPartMaxDurability(containerInventory.getStackInSlot(0));
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
        textList.add(new TextComponentString("已经工作: " + GTQTDateHelper.getTimeFromTicks(workTime)));
        textList.add(new TextComponentString("距离损坏: " + GTQTDateHelper.getTimeFromTicks(TotalTick - workTime)));
        if (isActive())
            textList.add(new TextComponentString("电极材料: " + getFilterBehavior().getMaterial().getLocalizedName()));
        if (isActive()) textList.add(new TextComponentString("极型等级: " + getFilterBehavior().getFilterTier()));
    }

    public boolean isActive() {
        return super.isActive() && this.isActive;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.POWER_SUBSTATION_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
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
