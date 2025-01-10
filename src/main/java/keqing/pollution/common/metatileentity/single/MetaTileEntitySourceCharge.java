package keqing.pollution.common.metatileentity.single;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.IPropertyFluidFilter;
import gregtech.api.capability.impl.FilteredFluidHandler;
import gregtech.api.capability.impl.GTFluidHandlerItemStack;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.TankWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import keqing.pollution.api.SourceMaterialItem;
import keqing.pollution.api.utils.PollutionLog;
import keqing.pollution.common.items.PollutionMetaItems;
import keqing.pollution.common.items.bauble.ItemBaubleBehavior;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static gregtech.api.unification.material.Materials.Neutronium;
import static keqing.pollution.api.utils.infusedFluidStack.STACK_MAP;
import static keqing.pollution.common.items.PollutionMetaItems.BAUBLES_WATER_RING;

public class MetaTileEntitySourceCharge extends MetaTileEntity {
    private final Material material = Neutronium;
    private final IPropertyFluidFilter fluidFilter;
    protected ItemStackHandler inventory;
    private FilteredFluidHandler fluidTank;

    public MetaTileEntitySourceCharge(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.fluidFilter = Neutronium.getProperty(PropertyKey.FLUID_PIPE);
        this.inventory = new GTItemStackHandler(this, 1);
        this.initializeInventory();

    }
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("inventory", this.inventory.serializeNBT());
        data.setTag("FluidInventory", ((FluidTank)this.fluidInventory).writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.inventory.deserializeNBT(data.getCompoundTag("inventory"));
        ((FluidTank)this.fluidInventory).readFromNBT(data.getCompoundTag("FluidInventory"));
    }
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySourceCharge(this.metaTileEntityId);
    }

    protected void initializeInventory() {
        super.initializeInventory();
        this.inventory = new GTItemStackHandler(this, 1);
        this.itemInventory = this.inventory;
        if (this.fluidFilter != null) {
            this.fluidTank = (new FilteredFluidHandler(8000)).setFilter(this.fluidFilter);
            this.fluidInventory = this.fluidTank;
        }
    }

    public ICapabilityProvider initItemStackCapabilities(ItemStack itemStack) {
        return (new GTFluidHandlerItemStack(itemStack, 4000)).setFilter(this.fluidTank.getFilter());
    }

    public void update() {
        super.update();

        if (this.getWorld().isRemote || this.inventory.getStackInSlot(0).isEmpty()) {
            return;
        }

        ItemStack stack = this.inventory.getStackInSlot(0);
        // 确保行为对象不为 null
        ItemBaubleBehavior behavior = getItemBaubleBehavior();
        if (behavior == null) {
            return;
        }
        if (isItemValid(stack)) {
            Material material = behavior.getMaterial();
            FluidStack fluidStack = STACK_MAP.get(material);
            if (this.fluidTank != null) {
                FluidStack currentFluid = this.fluidTank.getFluid();
                if (currentFluid != null && currentFluid.equals(fluidStack) && currentFluid.amount >= 1) {
                    if (behavior.addSource(1, true, stack)) {
                        behavior.addSource(1, false, stack);
                        this.fluidTank.drain(1, true);
                    }
                }
            }
        }
    }
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return ItemBaubleBehavior.getInstanceFor(stack) != null;
    }
    @Nullable
    private ItemBaubleBehavior getItemBaubleBehavior() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty()) return null;

        return ItemBaubleBehavior.getInstanceFor(stack);
    }

    public boolean showToolUsages() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
        int color = ColourRGBA.multiply(GTUtility.convertRGBtoOpaqueRGBA_CL(this.material.getMaterialRGB()), GTUtility.convertRGBtoOpaqueRGBA_CL(this.getPaintingColorForRendering()));
        color = GTUtility.convertOpaqueRGBA_CLtoRGB(color);
        return Pair.of(Textures.METAL_CRATE.getParticleTexture(), color);
    }

    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        int baseColor = ColourRGBA.multiply(GTUtility.convertRGBtoOpaqueRGBA_CL(this.material.getMaterialRGB()), GTUtility.convertRGBtoOpaqueRGBA_CL(this.getPaintingColorForRendering()));
        Textures.METAL_CRATE.render(renderState, translation, baseColor, pipeline);
    }

    public int getDefaultPaintingColor() {
        return 0xFFFFFF; // 白色
    }

    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 130).label(10, 5, this.getMetaFullName());
        builder.slot(this.inventory, 0, 72, 18, GuiTextures.SLOT);
        builder.widget((new TankWidget(fluidTank, 90, 18, 18, 18)).setContainerClicking(true, true).setBackgroundTexture(GuiTextures.FLUID_SLOT).setAlwaysShowFull(true));
        return builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 7, 48).build(this.getHolder(), entityPlayer);
    }

    public int getItemStackLimit(ItemStack stack) {
        return super.getItemStackLimit(stack);
    }

    protected boolean shouldSerializeInventories() {
        return false;
    }
}
