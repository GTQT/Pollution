package keqing.pollution.common.metatileentity.multiblock;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import keqing.gtqtcore.api.utils.GTQTDateHelper;
import keqing.gtqtcore.client.textures.GTQTTextures;
import keqing.pollution.POConfig;
import keqing.pollution.common.items.behaviors.FilterBehavior;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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

public class MetaTileEntityFluxClear extends MultiblockWithDisplayBase {
    @Override
    public boolean usesMui2() {
        return false;
    }
    private final double VisTicks;
    private final int tier;
    private final long energyAmountPer;
    private final ItemStackHandler containerInventory;
    long TotalTick;
    long workTime;
    private IEnergyContainer energyContainer;
    private boolean isWorkingEnabled;
    int flux;

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
        super(metaTileEntityId);
        this.tier = tier;
        this.VisTicks = tier * 5 * POConfig.PollutionSystemSwitch.fluxScrubberMultiplier;
        this.energyAmountPer = GTValues.V[tier];
        this.containerInventory = new GTItemStackHandler(this, 1);
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
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.energyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
    }

    @Override
    protected void updateFormedValid() {
        if (!this.getWorld().isRemote) {
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

            int aX = this.getPos().getX();
            int aY = this.getPos().getY();
            int aZ = this.getPos().getZ();
            for (int x = -tier; x <= tier; x++) {
                for (int y = -tier; y <= tier; y++) {
                    BlockPos pos = new BlockPos(aX + x * 16, aY, aZ + y * 16);
                    if (AuraHelper.getFlux(this.getWorld(), pos) > 0) {
                        flux= (int) AuraHelper.getFlux(this.getWorld(), pos);
                        if (energyContainer.getEnergyStored() >= energyAmountPer) {
                            energyContainer.removeEnergy(energyAmountPer);
                            isWorkingEnabled = true;
                            AuraHelper.drainFlux(this.getWorld(), pos, (float) VisTicks, false);
                            behavior.applyDamage(containerInventory.getStackInSlot(0), 1);
                        }
                    } else {
                        isWorkingEnabled = false;
                    }
                }
            }
        }
    }

    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 180, 240);
        builder.dynamicLabel(28, 12, () -> "大型污染清理机 等级："+tier, 0xFFFFFF);
        builder.widget(new SlotWidget(containerInventory, 0, 8, 8, true, true)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setTooltipText("输入槽位"));
        builder.image(4, 28, 172, 128, GuiTextures.DISPLAY);
        builder.widget((new AdvancedTextWidget(8, 32, this::addDisplayText, 16777215)).setMaxWidthLimit(180).setClickHandler(this::handleDisplayClick));
        builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 8, 160);
        return builder;
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
        textList.add(new TextComponentString("清理半径: " + tier));
        textList.add(new TextComponentString("当前污染: " + flux));
        textList.add(new TextComponentString("清理速率: " + VisTicks));
        textList.add(new TextComponentString("已经工作: " + GTQTDateHelper.getTimeFromTicks(workTime)));
        textList.add(new TextComponentString("距离损坏: " + GTQTDateHelper.getTimeFromTicks(TotalTick - workTime)));
        if(!isItemValid(containerInventory.getStackInSlot(0)))return;
        if (isActive())
            textList.add(new TextComponentString("过滤器材料: " + getFilterBehavior().getMaterial().getLocalizedName()));
        if (isActive()) textList.add(new TextComponentString("过滤等级: " + getFilterBehavior().getFilterTier()));
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.flux_clear.tire", tier, VisTicks));
        tooltip.add(I18n.format("pollution.flux_clear.tooltip"));
    }

    @Nonnull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "XXX", "XXX", "AAA", "AAA")
                .aisle("XXX", "XXX", "XXX", "AXA", "AAA")
                .aisle("XXX", "XSX", "XXX", "AAA", "AAA")
                .where('S', selfPredicate())
                .where('A', states(getIntakeState()).addTooltips("gregtech.multiblock.pattern.clear_amount_1"))
                .where('X', states(getCasingAState()).setMinGlobalLimited(15)
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setExactLimit(1)))
                .where(' ', any())
                .build();
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return GTQTTextures.LARGE_ROCKET_ENGINE_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), this.isActive(),
                this.isWorkingEnabled());
    }

    private boolean isWorkingEnabled() {
        return isWorkingEnabled;
    }

    public IBlockState getIntakeState() {
        return tier == GTValues.IV ? MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING) :
                MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.ENGINE_INTAKE_CASING);
    }

    private IBlockState getCasingAState() {
        if (tier == GTValues.EV)
            return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TITANIUM_STABLE);

        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        if (tier == GTValues.EV) return Textures.STABLE_TITANIUM_CASING;
        return Textures.ROBUST_TUNGSTENSTEEL_CASING;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityFluxClear(metaTileEntityId, tier);
    }
}
