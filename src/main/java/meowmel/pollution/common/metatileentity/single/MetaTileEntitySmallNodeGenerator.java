package meowmel.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.RichTextWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregtech.api.capability.GregtechDataCodes.IS_WORKING;
import static java.lang.Math.max;
import static net.minecraft.util.math.MathHelper.sqrt;

public class MetaTileEntitySmallNodeGenerator extends TieredMetaTileEntity {

    private final ItemStackHandler inventory;
    private float overallCapacityMultiplier = 0;
    private float BASIC_CAPACITY = 8192;
    private boolean isActive = false;

    public MetaTileEntitySmallNodeGenerator(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.inventory = new ItemStackHandler(1); // Ensure the inventory has 1 slot
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity GregTechTileEntity) {
        return new MetaTileEntitySmallNodeGenerator(metaTileEntityId, getTier());
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {

            ItemStack stack = inventory.getStackInSlot(0);
            if (!stack.isEmpty() && stack.getItem() == PollutionMetaItems.PACKAGED_AURA_NODE.getMetaItem() && stack.getMetadata() == PollutionMetaItems.PACKAGED_AURA_NODE.metaValue) {
                // Calculate the capacity multiplier for each node
                overallCapacityMultiplier = getNodeCapacityMultiplier(stack) * getTier();
                setActive(true);
            } else setActive(false);

            this.energyContainer.addEnergy((long) (BASIC_CAPACITY * overallCapacityMultiplier));
        }
    }

    private float getNodeCapacityMultiplier(ItemStack node) {
        float nodeCapacityMultiplier = 1.0f;
        float amplifierTire = 1.0F;
        if (node.hasTagCompound() && node.getTagCompound().hasKey("NodeTier")) {
            amplifierTire = switch (node.getTagCompound().getString("NodeTire")) {
                case "Withering" -> 0.25F;
                case "Pale" -> 0.5F;
                case "Bright" -> 4.0F;
                default -> 1.0F;
            };
        }
        nodeCapacityMultiplier *= amplifierTire;
        float amplifierType = 1.0F;
        if (node.hasTagCompound() && node.getTagCompound().hasKey("NodeType")) {
            amplifierType = switch (node.getTagCompound().getString("NodeType")) {
                case "Ominous", "Pure" -> 4.0F;
                case "Concussive" -> 8.0F;
                case "Voracious" -> 16.0F;
                default -> 1.0F;
            };
        }

        //计算火、秩序、混沌
        //混沌大于20开始线性降低效率
        //火和秩序按照几何平均数计算倍率
        //TODO: NBT key validation, check whether the compound tag exists and whether it contains the key.
        nodeCapacityMultiplier *= max((1.2f - 0.005f * node.getTagCompound().getInteger("EssenceEntropy")), 0);
        nodeCapacityMultiplier *= (1 + 0.02f * sqrt(node.getTagCompound().getInteger("EssenceFire") * node.getTagCompound().getInteger("EssenceOrder")));
        nodeCapacityMultiplier *= amplifierType;
        return nodeCapacityMultiplier;
    }

    //保存机器数据
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", inventory.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    public boolean isEnergyEmitter() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", this.energyContainer.getEnergyCapacity()));
        tooltip.add(I18n.format("pollution.machine.pollution_small_node_generator.tooltip"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline,
                new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
        getRenderer().render(renderState, translation, colouredPipeline);
    }

    @SideOnly(Side.CLIENT)
    private ICubeRenderer getRenderer() {
        return isActive() ? Textures.MAGIC_ENERGY_ABSORBER_ACTIVE : Textures.MAGIC_ENERGY_ABSORBER;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    private void setActive(boolean isActive) {
        if (this.isActive != isActive) {
            this.isActive = isActive;
            if (!getWorld().isRemote) {
                writeCustomData(IS_WORKING, w -> w.writeBoolean(isActive));
            }
        }
    }

    @Override
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == IS_WORKING) {
            this.isActive = buf.readBoolean();
        }
    }

    @Override
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(isActive);
    }

    @Override
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.isActive = buf.readBoolean();
    }

    @Override
    public boolean usesMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        return GTGuis.createPanel(this, 180, 240)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(28, 12))

                .child(new ItemSlot()
                        .pos(8, 8)
                        .background(GTGuiTextures.SLOT)
                        .slot(new ModularSlot(inventory, 0)
                                .accessibility(true, true))
                        .tooltip(tooltip -> tooltip
                                .addLine(IKey.lang("输入槽位")))
                )

                .child(GTGuiTextures.DISPLAY.asWidget()
                        .pos(4, 28)
                        .size(172, 128))

                .child(new RichTextWidget()
                        .pos(8, 32)
                        .size(172, 128)
                        .textColor(Color.WHITE.main)
                        .autoUpdate(true)
                        .textBuilder(richText -> {
                            BooleanSyncValue isActive = new BooleanSyncValue(this::isActive);
                            richText.addLine(IKey.lang("当前状态: " + (isActive.getValue() ? "运行中" : "停止")));
                        }))


                .bindPlayerInventory();
    }
}