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
import gregtech.api.gui.widgets.ImageWidget;
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
import keqing.pollution.POConfig;
import keqing.pollution.common.items.behaviors.FilterBehavior;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntityFluxClear extends MultiblockWithDisplayBase {
    private final double VisTicks;
    private final int tier;
    private final long energyAmountPer;
    private final ItemStackHandler containerInventory;
    long TotalTick;
    long workTime;
    private IEnergyContainer energyContainer;
    private boolean isWorkingEnabled;

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

            if (!isItemValid(stack)) return;
            int aX = this.getPos().getX();
            int aY = this.getPos().getY();
            int aZ = this.getPos().getZ();
            for (int x = -tier; x <= tier; x++) {
                for (int y = -tier; y <= tier; y++) {
                    BlockPos pos = new BlockPos(aX + x * 16, aY, aZ + y * 16);
                    if (AuraHelper.getFlux(this.getWorld(), pos) > 0) {
                        if (energyContainer.getEnergyStored() >= energyAmountPer) {
                            energyContainer.removeEnergy(energyAmountPer);
                            isWorkingEnabled = true;
                            AuraHelper.drainFlux(this.getWorld(), pos, (float) VisTicks, false);
                            behavior.applyDamage(containerInventory.getStackInSlot(0), 1);
                            workTime = AbstractMaterialPartBehavior.getPartDamage(containerInventory.getStackInSlot(0));
                            TotalTick = behavior.getPartMaxDurability(containerInventory.getStackInSlot(0));
                        }
                    } else {
                        isWorkingEnabled = false;
                        workTime = 0;
                        TotalTick = 0;
                    }
                }
            }
        }
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 239);
        builder.bindPlayerInventory(entityPlayer.inventory, 156);
        builder.dynamicLabel(7, 10, () -> "Vis Clear", 0x232323);
        builder.dynamicLabel(7, 30, () -> "Tier: " + this.tier, 0x232323);
        builder.dynamicLabel(7, 50, () -> "Vis: " + AuraHelper.getFlux(getWorld(), getPos()), 0x232323);
        builder.widget(new SlotWidget(this.containerInventory, 0, 88 - 9, 70, true, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT)
                        .setChangeListener(this::markDirty)
                        .setTooltipText("请放入过滤器"))
                .widget(new ImageWidget(88 - 9, 88, 18, 6, GuiTextures.BUTTON_POWER_DETAIL));
        builder.widget((new AdvancedTextWidget(7, 96, this::addDisplayText, 2302755)).setMaxWidthLimit(181));
        return builder.build(getHolder(), entityPlayer);
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
        textList.add(new TextComponentTranslation("pollution.flux_clear.tire", tier, VisTicks));
        textList.add(new TextComponentString("已经工作: " + GTQTDateHelper.getTimeFromTicks(workTime)));
        textList.add(new TextComponentString("距离损坏: " + GTQTDateHelper.getTimeFromTicks(TotalTick - workTime)));
        if (isActive())
            textList.add(new TextComponentString("电极材料: " + getFilterBehavior().getMaterial().getLocalizedName()));
        if (isActive()) textList.add(new TextComponentString("极型等级: " + getFilterBehavior().getFilterTier()));
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
        return Textures.POWER_SUBSTATION_OVERLAY;
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
