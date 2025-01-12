package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.pow;
import static net.minecraft.util.math.MathHelper.sqrt;

public class MetaTileEntitySmallNodeGenerator extends TieredMetaTileEntity {

    public boolean isActive = false;
    public float overallCapacityMultiplier = 0;
    public float expectedFinalCapacity = 0;
    public float BASIC_CAPACITY = 8192;
    private ItemStackHandler inventory;

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
            int slotCount = inventory.getSlots();


            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == PollutionMetaItems.PACKAGED_AURA_NODE.getMetaItem() && stack.getMetadata() == PollutionMetaItems.PACKAGED_AURA_NODE.metaValue) {
                    // Calculate the capacity multiplier for each node
                    overallCapacityMultiplier += getNodeCapacityMultiplier(stack) * getTier();
                }
            }
            expectedFinalCapacity = BASIC_CAPACITY * overallCapacityMultiplier;
            this.energyContainer.addEnergy((long) expectedFinalCapacity);
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
    public boolean isActive() {
        return this.isActive() && isActive;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public boolean getIsWeatherOrTerrainResistant() {
        return true;
    }

    public long getMaxInputOutputAmperage() {
        return 1L;
    }

    public boolean isEnergyEmitter() {
        return true;
    }

    public long getEnergyCapacity() {
        return (long) (8192 * pow(4, getTier()));
    }

    protected void reinitializeEnergyContainer() {
        super.reinitializeEnergyContainer();
        long tierVoltage = GTValues.V[this.getTier()];
        super.energyContainer = EnergyContainerHandler.emitterContainer(this, getEnergyCapacity(), tierVoltage, this.getMaxInputOutputAmperage());
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        String key = this.metaTileEntityId.getPath().split("\\.")[0];
        String mainKey = String.format("gregtech.machine.%s.tooltip", key);
        if (I18n.hasKey(mainKey)) {
            tooltip.add(1, I18n.format(mainKey, new Object[0]));
        }

        tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", new Object[]{this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()]}));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", new Object[]{this.energyContainer.getEnergyCapacity()}));
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
        return isActive ? Textures.MAGIC_ENERGY_ABSORBER_ACTIVE : Textures.MAGIC_ENERGY_ABSORBER;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.defaultBuilder();
        builder.label(6, 6, this.getMetaFullName(), 0x232323);
        builder.slot(inventory, 0, 78, 35, true, true, gregtech.api.gui.GuiTextures.SLOT);
        builder.bindPlayerInventory(entityPlayer.inventory, 84);
        return builder.build(getHolder(), entityPlayer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return POTextures.MAGIC_VOLTAGE_CASINGS[getTier()];
    }
}