package keqing.pollution.common.metatileentity.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.ItemSlot;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import gregtech.api.GTValues;
import gregtech.api.capability.IMufflerHatch;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.AbilityInstances;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMufflerHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.pollution.POConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

import java.util.ArrayList;
import java.util.List;

public class MetaTileEntityFluxMuffler extends MetaTileEntityMultiblockPart implements
        IMultiblockAbilityPart<IMufflerHatch>, ITieredMetaTileEntity, IMufflerHatch {

    private final float pollution$pollutionMultiplier = (float) ((100 - (getTier() - 1) * 12.5) / 100 * POConfig.PollutionSystemSwitch.mufflerPollutionMultiplier);

    private final int recoveryChance;
    private final GTItemStackHandler inventory;

    private boolean frontFaceFree;
    private boolean outputItem;

    public MetaTileEntityFluxMuffler(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.recoveryChance = (int) Math.ceil((tier - 1.0f) / 8 * 100);
        this.inventory = new GTItemStackHandler(this, (int) Math.pow(tier + 1, 2));
        this.frontFaceFree = false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityMufflerHatch(metaTileEntityId, getTier());
    }

    @Override
    public void update() {
        super.update();
        if (getController() == null) return;
        if (getController() instanceof MultiblockWithDisplayBase controller && controller.isActive()) {
            if (!getWorld().isRemote) {
                if (getOffsetTimer() % 10 == 0) this.frontFaceFree = checkFrontFaceFree();
                if (getOffsetTimer() % 200 == 0 && POConfig.PollutionSystemSwitch.enablePollution) {
                    AuraHelper.polluteAura(getWorld(), getPos(), (float) (pollution$pollutionMultiplier * 0.1), POConfig.PollutionSystemSwitch.mufflerPollutionShowEffects);
                }
            } else VanillaParticleEffects.mufflerEffect(this, controller.getMufflerParticle());
        }
    }

    @Override
    public void clearMachineInventory(List<ItemStack> itemBuffer) {
        clearInventory(itemBuffer, inventory);
    }

    public void recoverItemsTable(List<ItemStack> recoveryItems) {
        for (ItemStack recoveryItem : recoveryItems) {
            if (calculateChance()) {
                GTTransferUtils.insertItem(inventory, recoveryItem.copy(), false);
            }
        }
    }

    private boolean calculateChance() {
        return recoveryChance >= 100 || recoveryChance > GTValues.RNG.nextInt(100);
    }

    /**
     * @return true if front face is free and contains only air blocks in 1x1 area
     */
    public boolean isFrontFaceFree() {
        return frontFaceFree;
    }

    @Override
    public boolean outputItem() {
        return outputItem;
    }

    private boolean checkFrontFaceFree() {
        BlockPos frontPos = getPos().offset(getFrontFacing());
        IBlockState blockState = getWorld().getBlockState(frontPos);
        MultiblockWithDisplayBase controller = (MultiblockWithDisplayBase) getController();

        // break a snow layer if it exists, and if this machine is running
        if (controller != null && controller.isActive()) {
            if (GTUtility.tryBreakSnow(getWorld(), frontPos, blockState, true)) {
                return true;
            }
            return blockState.getBlock().isAir(blockState, getWorld(), frontPos);
        }
        return blockState.getBlock().isAir(blockState, getWorld(), frontPos) || GTUtility.isBlockSnow(blockState);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay())
            Textures.MUFFLER_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.machine.muffler_hatch.tooltip1"));
        tooltip.add(I18n.format("gregtech.muffler.recovery_tooltip", recoveryChance));
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip1"));
        tooltip.add(I18n.format("pollution.muffler.pollution_tooltip2", pollution$pollutionMultiplier));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
        tooltip.add(TooltipHelper.BLINKING_RED + I18n.format("gregtech.machine.muffler_hatch.tooltip2"));
    }

    @Override
    public void addToolUsages(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    @Override
    public MultiblockAbility<IMufflerHatch> getAbility() {
        return MultiblockAbility.MUFFLER_HATCH;
    }

    @Override
    public void registerAbilities(AbilityInstances abilityInstances) {
        abilityInstances.add(this);
    }

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager) {
        int rowSize = (int) Math.sqrt(this.inventory.getSlots());
        int xOffset = rowSize == 10 ? 9 : 0;

        guiSyncManager.registerSlotGroup("item_inv", rowSize);

        List<List<IWidget>> widgets = new ArrayList<>();
        for (int y = 0; y < rowSize; y++) {
            widgets.add(new ArrayList<>());
            for (int x = 0; x < rowSize; x++) {
                int index = y * rowSize + x;
                widgets.get(y).add(new ItemSlot().slot(SyncHandlers.itemSlot(this.inventory, index)
                        .slotGroup("item_inv")
                        .accessibility(false, true)));
            }
        }
        BooleanSyncValue outputStateValue = new BooleanSyncValue(() -> outputItem, val -> outputItem = val);
        guiSyncManager.syncValue("output_state", outputStateValue);

        int backgroundWidth = 176 + xOffset * 2 + 18 + 5;
        int backgroundHeight = 18 + 18 * rowSize + 94;
        // TODO: Change the position of the name when it's standardized.
        return GTGuis.createPanel(this, backgroundWidth, backgroundHeight)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5))
                .child(SlotGroupWidget.playerInventory().left(7).bottom(7))
                .child(new Grid()
                        .top(18).height(rowSize * 18)
                        .minElementMargin(0, 0)
                        .minColWidth(18).minRowHeight(18)
                        .alignX(0.5f)
                        .matrix(widgets))

                .child(Flow.column()
                        .pos(backgroundWidth - 7 - 18, backgroundHeight - 18 * 4 - 7 - 5)
                        .width(18).height(18 * 4 + 5)
                        .child(GTGuiTextures.getLogo(getUITheme()).asWidget().size(17).top(18 * 3 + 5))

                        .child(new ToggleButton()
                                .top(0)
                                .value(new BoolValue.Dynamic(outputStateValue::getBoolValue,
                                        outputStateValue::setBoolValue))
                                .overlay(GTGuiTextures.OUT_SLOT_OVERLAY)
                                .tooltipBuilder(t -> t.setAutoUpdate(true)
                                        .addLine(outputStateValue.getBoolValue() ?
                                                IKey.lang("gregtech.gui.output_item.tooltip.enabled") :
                                                IKey.lang("gregtech.gui.output_item.tooltip.disabled"))))
                );
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("RecoveryInventory", inventory.serializeNBT());
        data.setBoolean("outputItem", outputItem);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.inventory.deserializeNBT(data.getCompoundTag("RecoveryInventory"));
        outputItem = data.getBoolean("outputItem");
    }
}