package keqing.pollution.common.metatileentity.single;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IActiveOutputSide;
import gregtech.api.capability.IFilter;
import gregtech.api.capability.IFilteredFluidContainer;
import gregtech.api.capability.impl.FilteredItemHandler;
import gregtech.api.capability.impl.FluidHandlerProxy;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.GTFluidHandlerItemStack;
import gregtech.api.cover.CoverRayTracer;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.resources.IGuiTexture;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.FluidContainerSlotWidget;
import gregtech.api.gui.widgets.ImageWidget;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.PhantomTankWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.TankWidget;
import gregtech.api.gui.widgets.ToggleButtonWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.custom.QuantumStorageRenderer;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.pollution.api.utils.POAspectToGtFluidList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

import javax.annotation.Nullable;

public class MetaTileEntityQuantumTankTc extends MetaTileEntity implements ITieredMetaTileEntity, IActiveOutputSide, IFastRenderMetaTileEntity, IAspectSource, IEssentiaTransport {
    private final int tier;
    private final int maxFluidCapacity;
    protected FluidTank fluidTank;
    private boolean autoOutputFluids;
    private @Nullable EnumFacing outputFacing;
    private boolean allowInputFromOutputSide = false;
    protected IFluidHandler outputFluidInventory;
    protected @Nullable FluidStack previousFluid;
    protected boolean locked;
    protected boolean voiding;
    private @Nullable FluidStack lockedFluid;

    public MetaTileEntityQuantumTankTc(ResourceLocation metaTileEntityId, int tier, int maxFluidCapacity) {
        super(metaTileEntityId);
        this.tier = tier;
        this.maxFluidCapacity = maxFluidCapacity;
        this.initializeInventory();
    }

    public int getTier() {
        return this.tier;
    }

    protected void initializeInventory() {
        super.initializeInventory();
        this.fluidTank = new QuantumFluidTankTc(this.maxFluidCapacity);
        this.fluidInventory = this.fluidTank;
        this.importFluids = new FluidTankList(false, this.fluidTank);
        this.exportFluids = new FluidTankList(false, this.fluidTank);
        this.outputFluidInventory = new FluidHandlerProxy(new FluidTankList(false), this.exportFluids);
    }

    public void update() {
        super.update();
        EnumFacing currentOutputFacing = this.getOutputFacing();
        if (!this.getWorld().isRemote) {
            fillJar();
            this.fillContainerFromInternalTank();
            this.fillInternalTankFromFluidContainer();
            if (this.isAutoOutputFluids()) {
                this.pushFluidsIntoNearbyHandlers(currentOutputFacing);
            }

            FluidStack currentFluid = this.fluidTank.getFluid();
            if (this.previousFluid == null) {
                if (currentFluid != null) {
                    this.updatePreviousFluid(currentFluid);
                }
            } else if (currentFluid == null) {
                this.updatePreviousFluid(null);
            } else if (this.previousFluid.getFluid().equals(currentFluid.getFluid()) && this.previousFluid.amount != currentFluid.amount) {
                int currentFill = MathHelper.floor(16.0F * (float)currentFluid.amount / (float)this.fluidTank.getCapacity());
                int previousFill = MathHelper.floor(16.0F * (float)this.previousFluid.amount / (float)this.fluidTank.getCapacity());
                this.previousFluid.amount = currentFluid.amount;
                this.writeCustomData(GregtechDataCodes.UPDATE_FLUID_AMOUNT, (buf) -> {
                    buf.writeInt(currentFluid.amount);
                    buf.writeBoolean(currentFill != previousFill);
                });
            } else if (!this.previousFluid.equals(currentFluid)) {
                this.updatePreviousFluid(currentFluid);
            }
        }
    }
    void fillJar() {
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.getWorld(), this.getPos(), EnumFacing.UP);
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(EnumFacing.DOWN)) {
                return;
            }

            Aspect ta = null;
            if (this.getLockAspect() != null) {
                ta = this.getLockAspect();
            } else if (this.getAspect() != null && this.fluidTank.getFluidAmount() > 144) {
                ta = this.getAspect();
            } else if (ic.getEssentiaAmount(EnumFacing.DOWN) > 0 && ic.getSuctionAmount(EnumFacing.DOWN) < this.getSuctionAmount(EnumFacing.UP) && this.getSuctionAmount(EnumFacing.UP) >= ic.getMinimumSuction()) {
                ta = ic.getEssentiaType(EnumFacing.DOWN);
            }

            if (ta != null && ic.getSuctionAmount(EnumFacing.DOWN) < this.getSuctionAmount(EnumFacing.UP)) {
                this.addToContainer(ta, ic.takeEssentia(ta, 1, EnumFacing.DOWN));
            }
        }

    }
    protected void updatePreviousFluid(FluidStack currentFluid) {
        this.previousFluid = currentFluid == null ? null : currentFluid.copy();
        this.writeCustomData(GregtechDataCodes.UPDATE_FLUID, (buf) -> {
            buf.writeCompoundTag(currentFluid == null ? null : currentFluid.writeToNBT(new NBTTagCompound()));
        });
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("FluidInventory", this.fluidTank.writeToNBT(new NBTTagCompound()));
        data.setBoolean("AutoOutputFluids", this.autoOutputFluids);
        data.setInteger("OutputFacing", this.getOutputFacing().getIndex());
        data.setBoolean("IsVoiding", this.voiding);
        data.setBoolean("IsLocked", this.locked);
        if (this.locked && this.lockedFluid != null) {
            data.setTag("LockedFluid", this.lockedFluid.writeToNBT(new NBTTagCompound()));
        }

        data.setBoolean("AllowInputFromOutputSideF", this.allowInputFromOutputSide);
        return data;
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("ContainerInventory")) {
            legacyTankItemHandlerNBTReading(this, data.getCompoundTag("ContainerInventory"), 0, 1);
        }

        this.fluidTank.readFromNBT(data.getCompoundTag("FluidInventory"));
        this.autoOutputFluids = data.getBoolean("AutoOutputFluids");
        this.outputFacing = EnumFacing.VALUES[data.getInteger("OutputFacing")];
        this.voiding = data.getBoolean("IsVoiding") || data.getBoolean("IsPartiallyVoiding");
        this.locked = data.getBoolean("IsLocked");
        this.lockedFluid = this.locked ? FluidStack.loadFluidStackFromNBT(data.getCompoundTag("LockedFluid")) : null;
        this.allowInputFromOutputSide = data.getBoolean("AllowInputFromOutputSideF");
    }

    public static void legacyTankItemHandlerNBTReading(MetaTileEntity mte, NBTTagCompound nbt, int inputSlot, int outputSlot) {
        if (mte != null && nbt != null) {
            NBTTagList items = nbt.getTagList("Items", 10);
            if (mte.getExportItems().getSlots() >= 1 && mte.getImportItems().getSlots() >= 1 && inputSlot >= 0 && outputSlot >= 0 && inputSlot != outputSlot) {
                for(int i = 0; i < items.tagCount(); ++i) {
                    NBTTagCompound itemTags = items.getCompoundTagAt(i);
                    int slot = itemTags.getInteger("Slot");
                    if (slot == inputSlot) {
                        mte.getImportItems().setStackInSlot(0, new ItemStack(itemTags));
                    } else if (slot == outputSlot) {
                        mte.getExportItems().setStackInSlot(0, new ItemStack(itemTags));
                    }
                }

            }
        }
    }

    public void initFromItemStackData(NBTTagCompound tag) {
        super.initFromItemStackData(tag);
        if (tag.hasKey("Fluid", 10)) {
            this.fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("Fluid")));
        }

        if (tag.getBoolean("IsVoiding") || tag.getBoolean("IsPartialVoiding")) {
            this.setVoiding(true);
        }

        this.lockedFluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("LockedFluid"));
        this.locked = this.lockedFluid != null;
    }

    public void writeItemStackData(NBTTagCompound tag) {
        super.writeItemStackData(tag);
        FluidStack stack = this.fluidTank.getFluid();
        if (stack != null && stack.amount > 0) {
            tag.setTag("Fluid", stack.writeToNBT(new NBTTagCompound()));
        }

        if (this.voiding) {
            tag.setBoolean("IsVoiding", true);
        }

        if (this.locked && this.lockedFluid != null) {
            tag.setTag("LockedFluid", this.lockedFluid.writeToNBT(new NBTTagCompound()));
        }

    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new gregtech.common.metatileentities.storage.MetaTileEntityQuantumTank(this.metaTileEntityId, this.tier, this.maxFluidCapacity);
    }

    protected FluidTankList createImportFluidHandler() {
        return new FluidTankList(false, new IFluidTank[]{this.fluidTank});
    }

    protected FluidTankList createExportFluidHandler() {
        return new FluidTankList(false, new IFluidTank[]{this.fluidTank});
    }

    protected IItemHandlerModifiable createImportItemHandler() {
        return (new FilteredItemHandler(this, 1)).setFillPredicate(FilteredItemHandler.getCapabilityFilter(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY));
    }

    protected IItemHandlerModifiable createExportItemHandler() {
        return new GTItemStackHandler(this, 1);
    }

    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        Textures.QUANTUM_STORAGE_RENDERER.renderMachine(renderState, translation, (IVertexOperation[]) ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(this.getPaintingColorForRendering()))), this);
        Textures.QUANTUM_TANK_OVERLAY.renderSided(EnumFacing.UP, renderState, translation, pipeline);
        if (this.outputFacing != null) {
            Textures.PIPE_OUT_OVERLAY.renderSided(this.outputFacing, renderState, translation, pipeline);
            if (this.isAutoOutputFluids()) {
                Textures.FLUID_OUTPUT_OVERLAY.renderSided(this.outputFacing, renderState, translation, pipeline);
            }
        }

        QuantumStorageRenderer.renderTankFluid(renderState, translation, pipeline, this.fluidTank, this.getWorld(), this.getPos(), this.getFrontFacing());
    }

    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (this.fluidTank.getFluid() != null && this.fluidTank.getFluid().amount != 0) {
            QuantumStorageRenderer.renderTankAmount(x, y, z, this.getFrontFacing(), (long)this.fluidTank.getFluid().amount);
        }
    }

    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
        return Pair.of(Textures.VOLTAGE_CASINGS[this.tier].getParticleSprite(), this.getPaintingColorForRendering());
    }

    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.machine.quantum_tank.tooltip", new Object[0]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity", new Object[]{this.maxFluidCapacity}));
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            if (tag.hasKey("Fluid", 10)) {
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("Fluid"));
                if (fluidStack != null) {
                    tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_stored", new Object[]{fluidStack.getLocalizedName(), fluidStack.amount}));
                }
            }

            if (tag.getBoolean("IsVoiding") || tag.getBoolean("IsPartialVoiding")) {
                tooltip.add(I18n.format("gregtech.machine.quantum_tank.tooltip.voiding_enabled", new Object[0]));
            }
        }

    }

    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.auto_output_covers", new Object[0]));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing", new Object[0]));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    protected ModularUI createUI(EntityPlayer entityPlayer) {
        TankWidget tankWidget = (new PhantomTankWidget(this.fluidTank, 69, 43, 18, 18, () -> {
            return this.lockedFluid;
        }, (f) -> {
            if (this.fluidTank.getFluidAmount() == 0) {
                if (f == null) {
                    this.setLocked(false);
                    this.lockedFluid = null;
                } else {
                    this.setLocked(true);
                    this.lockedFluid = f.copy();
                    this.lockedFluid.amount = 1;
                }

            }
        })).setAlwaysShowFull(true).setDrawHoveringText(false);
        return ModularUI.defaultBuilder().widget(new ImageWidget(7, 16, 81, 46, GuiTextures.DISPLAY)).widget(new LabelWidget(11, 20, "gregtech.gui.fluid_amount", 16777215)).widget(tankWidget).widget(new AdvancedTextWidget(11, 30, this.getFluidAmountText(tankWidget), 16777215)).widget(new AdvancedTextWidget(11, 40, this.getFluidNameText(tankWidget), 16777215)).label(6, 6, this.getMetaFullName()).widget((new FluidContainerSlotWidget(this.importItems, 0, 90, 17, false)).setBackgroundTexture(new IGuiTexture[]{GuiTextures.SLOT, GuiTextures.IN_SLOT_OVERLAY})).widget((new SlotWidget(this.exportItems, 0, 90, 44, true, false)).setBackgroundTexture(new IGuiTexture[]{GuiTextures.SLOT, GuiTextures.OUT_SLOT_OVERLAY})).widget((new ToggleButtonWidget(7, 64, 18, 18, GuiTextures.BUTTON_FLUID_OUTPUT, this::isAutoOutputFluids, this::setAutoOutputFluids)).setTooltipText("gregtech.gui.fluid_auto_output.tooltip", new Object[0]).shouldUseBaseBackground()).widget((new ToggleButtonWidget(25, 64, 18, 18, GuiTextures.BUTTON_LOCK, this::isLocked, this::setLocked)).setTooltipText("gregtech.gui.fluid_lock.tooltip", new Object[0]).shouldUseBaseBackground()).widget((new ToggleButtonWidget(43, 64, 18, 18, GuiTextures.BUTTON_FLUID_VOID, this::isVoiding, this::setVoiding)).setTooltipText("gregtech.gui.fluid_voiding.tooltip", new Object[0]).shouldUseBaseBackground()).bindPlayerInventory(entityPlayer.inventory).build(this.getHolder(), entityPlayer);
    }

    private Consumer<List<ITextComponent>> getFluidNameText(TankWidget tankWidget) {
        return (list) -> {
            TextComponentTranslation translation = tankWidget.getFluidTextComponent();
            if (translation == null) {
                translation = GTUtility.getFluidTranslation(this.lockedFluid);
            }

            if (translation != null) {
                list.add(translation);
            }

        };
    }

    private Consumer<List<ITextComponent>> getFluidAmountText(TankWidget tankWidget) {
        return (list) -> {
            String fluidAmount = "";
            if (tankWidget.getFormattedFluidAmount().equals("0")) {
                if (this.lockedFluid != null) {
                    fluidAmount = "0";
                }
            } else {
                fluidAmount = tankWidget.getFormattedFluidAmount();
            }

            if (!fluidAmount.isEmpty()) {
                list.add(new TextComponentString(fluidAmount));
            }

        };
    }

    public EnumFacing getOutputFacing() {
        return this.outputFacing == null ? this.frontFacing.getOpposite() : this.outputFacing;
    }

    public void setFrontFacing(EnumFacing frontFacing) {
        if (frontFacing == EnumFacing.UP) {
            if (this.outputFacing != null && this.outputFacing != EnumFacing.DOWN) {
                super.setFrontFacing(this.outputFacing.getOpposite());
            } else {
                super.setFrontFacing(EnumFacing.NORTH);
            }
        } else {
            super.setFrontFacing(frontFacing);
        }

        if (this.outputFacing == null) {
            this.setOutputFacing(frontFacing.getOpposite());
        }

    }

    public boolean isAutoOutputItems() {
        return false;
    }

    public boolean isAutoOutputFluids() {
        return this.autoOutputFluids;
    }

    public boolean isAllowInputFromOutputSideItems() {
        return false;
    }

    public boolean isAllowInputFromOutputSideFluids() {
        return this.allowInputFromOutputSide;
    }

    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.UPDATE_OUTPUT_FACING) {
            this.outputFacing = EnumFacing.VALUES[buf.readByte()];
            this.scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.UPDATE_AUTO_OUTPUT_FLUIDS) {
            this.autoOutputFluids = buf.readBoolean();
            this.scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.UPDATE_FLUID) {
            try {
                this.fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(buf.readCompoundTag()));
            } catch (IOException var6) {
                GTLog.logger.warn("Failed to load fluid from NBT in a quantum tank at {} on a routine fluid update", this.getPos());
            }

            this.scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.UPDATE_FLUID_AMOUNT) {
            int amount = buf.readInt();
            boolean updateRendering = buf.readBoolean();
            FluidStack stack = this.fluidTank.getFluid();
            if (stack != null) {
                stack.amount = Math.min(amount, this.fluidTank.getCapacity());
                if (updateRendering) {
                    this.scheduleRenderUpdate();
                }
            }
        } else if (dataId == GregtechDataCodes.UPDATE_IS_VOIDING) {
            this.setVoiding(buf.readBoolean());
        }

    }

    public boolean isValidFrontFacing(EnumFacing facing) {
        return super.isValidFrontFacing(facing) && facing != this.outputFacing;
    }

    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeByte(this.getOutputFacing().getIndex());
        buf.writeBoolean(this.autoOutputFluids);
        buf.writeBoolean(this.locked);
        buf.writeCompoundTag(this.fluidTank.getFluid() == null ? null : this.fluidTank.getFluid().writeToNBT(new NBTTagCompound()));
        buf.writeBoolean(this.voiding);
    }

    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.outputFacing = EnumFacing.VALUES[buf.readByte()];
        if (this.frontFacing == EnumFacing.UP) {
            if (this.outputFacing != EnumFacing.DOWN) {
                this.frontFacing = this.outputFacing.getOpposite();
            } else {
                this.frontFacing = EnumFacing.NORTH;
            }
        }

        this.autoOutputFluids = buf.readBoolean();
        this.locked = buf.readBoolean();

        try {
            this.fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(buf.readCompoundTag()));
        } catch (IOException var3) {
            GTLog.logger.warn("Failed to load fluid from NBT in a quantum tank at " + this.getPos() + " on initial server/client sync");
        }

        this.voiding = buf.readBoolean();
    }

    public void setOutputFacing(EnumFacing outputFacing) {
        this.outputFacing = outputFacing;
        if (!this.getWorld().isRemote) {
            this.notifyBlockUpdate();
            this.writeCustomData(GregtechDataCodes.UPDATE_OUTPUT_FACING, (buf) -> {
                buf.writeByte(outputFacing.getIndex());
            });
            this.markDirty();
        }

    }

    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_ACTIVE_OUTPUT_SIDE) {
            return side == this.getOutputFacing() ? GregtechTileCapabilities.CAPABILITY_ACTIVE_OUTPUT_SIDE.cast(this) : null;
        } else if (capability != CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return super.getCapability(capability, side);
        } else {
            IFluidHandler fluidHandler = side == this.getOutputFacing() && !this.isAllowInputFromOutputSideFluids() ? this.outputFluidInventory : this.fluidInventory;
            return fluidHandler.getTankProperties().length > 0 ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler) : null;
        }
    }

    public ICapabilityProvider initItemStackCapabilities(ItemStack itemStack) {
        return new GTFluidHandlerItemStack(itemStack, this.maxFluidCapacity);
    }

    public boolean onWrenchClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (!playerIn.isSneaking()) {
            if (this.getOutputFacing() != facing && this.getFrontFacing() != facing) {
                if (!this.getWorld().isRemote) {
                    this.setOutputFacing(facing);
                }

                return true;
            } else {
                return false;
            }
        } else {
            return super.onWrenchClick(playerIn, hand, facing, hitResult);
        }
    }

    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        EnumFacing hitFacing = CoverRayTracer.determineGridSideHit(hitResult);
        if (facing == this.getOutputFacing() || hitFacing == this.getOutputFacing() && playerIn.isSneaking()) {
            if (!this.getWorld().isRemote) {
                if (this.isAllowInputFromOutputSideFluids()) {
                    this.setAllowInputFromOutputSide(false);
                    playerIn.sendStatusMessage(new TextComponentTranslation("gregtech.machine.basic.input_from_output_side.disallow"), true);
                } else {
                    this.setAllowInputFromOutputSide(true);
                    playerIn.sendStatusMessage(new TextComponentTranslation("gregtech.machine.basic.input_from_output_side.allow"), true);
                }
            }

            return true;
        } else {
            return super.onScrewdriverClick(playerIn, hand, facing, hitResult);
        }
    }

    public void setAllowInputFromOutputSide(boolean allowInputFromOutputSide) {
        if (this.allowInputFromOutputSide != allowInputFromOutputSide) {
            this.allowInputFromOutputSide = allowInputFromOutputSide;
            if (!this.getWorld().isRemote) {
                this.markDirty();
            }

        }
    }

    public void setAutoOutputFluids(boolean autoOutputFluids) {
        if (this.autoOutputFluids != autoOutputFluids) {
            this.autoOutputFluids = autoOutputFluids;
            if (!this.getWorld().isRemote) {
                this.writeCustomData(GregtechDataCodes.UPDATE_AUTO_OUTPUT_FLUIDS, (buf) -> {
                    buf.writeBoolean(autoOutputFluids);
                });
                this.markDirty();
            }

        }
    }

    protected boolean isLocked() {
        return this.locked;
    }

    protected void setLocked(boolean locked) {
        if (this.locked != locked) {
            this.locked = locked;
            if (!this.getWorld().isRemote) {
                this.markDirty();
            }

            if (locked && this.fluidTank.getFluid() != null) {
                this.lockedFluid = this.fluidTank.getFluid().copy();
                this.lockedFluid.amount = 1;
            } else {
                this.lockedFluid = null;
            }
        }
    }

    protected boolean isVoiding() {
        return this.voiding;
    }

    protected void setVoiding(boolean isPartialVoid) {
        this.voiding = isPartialVoid;
        if (!this.getWorld().isRemote) {
            this.writeCustomData(GregtechDataCodes.UPDATE_IS_VOIDING, (buf) -> {
                buf.writeBoolean(this.voiding);
            });
            this.markDirty();
        }

    }

    public ItemStack getPickItem(EntityPlayer player) {
        if (!player.isCreative()) {
            return super.getPickItem(player);
        } else {
            ItemStack baseItemStack = this.getStackForm();
            NBTTagCompound tag = new NBTTagCompound();
            this.writeItemStackData(tag);
            if (!tag.isEmpty()) {
                baseItemStack.setTagCompound(tag);
            }

            return baseItemStack;
        }
    }

    public boolean needsSneakToRotate() {
        return true;
    }

    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getPos());
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getLightOpacity() {
        return 0;
    }

    private Aspect getAspect()
    {
         Aspect as=null;
        if(this.fluidTank.getFluid()==null || this.fluidTank.getFluidAmount()==0)
            return null;
        for (var x:POAspectToGtFluidList.aspectToGtFluidList.values())
        {
            if (x.getFluid() == fluidTank.getFluid().getFluid()) {
                if (POAspectToGtFluidList.getKeyByValue(x) != null) {
                    as = POAspectToGtFluidList.getKeyByValue(x);
                }
            }
        }
        return as;
    }
    private Aspect getLockAspect()
    {
        Aspect as=null;
        if(!this.isLocked() || this.lockedFluid==null)
            return null;
        for (var x:POAspectToGtFluidList.aspectToGtFluidList.values())
        {
            if (x.getFluid() == this.lockedFluid.getFluid()) {
                if (POAspectToGtFluidList.getKeyByValue(x) != null) {
                    as = POAspectToGtFluidList.getKeyByValue(x);
                }
            }
        }
        return as;
    }
    private FluidStack getFluidStackFromAspect(AspectList aspectList)
    {
        if (aspectList != null && aspectList.size() > 0) {
            return  POAspectToGtFluidList.aspectToGtFluidList.get(aspectList.getAspectsSortedByAmount()[0]).getFluid(aspectList.getAmount(aspectList.getAspectsSortedByAmount()[0]));
        }
        return null;
    }

    public boolean isBlocked() {
        return false;
    }


    public AspectList getAspects() {
        AspectList al = new AspectList();
        Aspect as=getAspect();
        if (as != null && this.fluidTank.getFluidAmount() > 144) {
            al.add(as, this.fluidTank.getFluidAmount()/144 );
        }

        return al;
    }


    public void setAspects(AspectList aspectList) {
        if(getFluidStackFromAspect(aspectList)!=null)
            this.fluidTank.setFluid(getFluidStackFromAspect(aspectList));
    }


    public boolean doesContainerAccept(Aspect tag) {
        if(this.isLocked())
            return this.getLockAspect() != null ? tag.equals(this.getLockAspect()) : true;
        return this.getAspect() != null ? tag.equals(this.getAspect()) : true;
    }

    public int addToContainer(Aspect tt, int am) {
        am*=144;
        if (am == 0) {
            return am;
        } else {
            if (this.fluidTank.getFluidAmount() < this.maxFluidCapacity && tt == this.getAspect() || this.fluidTank.getFluidAmount() == 0 ) {
                int added = Math.min(am, this.maxFluidCapacity -this.fluidTank.getFluidAmount());
                this.fluidTank.fill(POAspectToGtFluidList.aspectToGtFluidList.get(tt).getFluid(added),true);
                am -= added;
            }
            return am/144;
        }
    }

    public boolean takeFromContainer(Aspect tt, int am) {
        am *=144;
        if (getAspect()!=null && this.fluidTank.getFluidAmount() >= am && tt == getAspect()) {
            this.fluidTank.drain(am,true);
            return true;
        } else {
            return false;
        }
    }

    public boolean takeFromContainer(AspectList aspectList) {
        return false;
    }

    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return this.fluidTank.getFluidAmount() >= amt*144 && tag == this.getAspect();
    }

    public boolean doesContainerContain(AspectList ot) {
        Aspect[] var2 = ot.getAspects();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Aspect tt = var2[var4];
            if (this.fluidTank.getFluidAmount() > 0 && tt == this.getAspect()) {
                return true;
            }
        }

        return false;
    }

    public int containerContains(Aspect tag) {
        return tag == this.getAspect() ? this.fluidTank.getFluidAmount()/144 : 0;
    }

    public boolean isConnectable(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    public boolean canInputFrom(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    public boolean canOutputTo(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    public void setSuction(Aspect aspect, int i) {

    }

    public Aspect getSuctionType(EnumFacing loc) {
        if(this.isLocked())
            return this.getLockAspect();
        return this.getAspect();
    }

    public int getSuctionAmount(EnumFacing loc) {
        if (this.fluidTank.getFluidAmount() < maxFluidCapacity) {
            return 128;
        } else {
            return 0;
        }
    }

    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0;
    }


    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canInputFrom(face) ? amount - this.addToContainer(aspect, amount) : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing enumFacing) {
        return this.getAspect();
    }

    @Override
    public int getEssentiaAmount(EnumFacing enumFacing) {
        return this.fluidTank.getFluidAmount()/144;
    }

    @Override
    public int getMinimumSuction() {
        return 128;
    }

    private class QuantumFluidTankTc extends FluidTank implements IFilteredFluidContainer, IFilter<FluidStack> {
        public QuantumFluidTankTc(int capacity) {
            super(capacity);
        }

        public int fillInternal(FluidStack resource, boolean doFill) {
            if(!canFillFluidType(resource))
                return 0;
            int accepted = super.fillInternal(resource, doFill);
            if (accepted == 0 && !resource.isFluidEqual(this.getFluid())) {
                return 0;
            } else {
                if (doFill && MetaTileEntityQuantumTankTc.this.locked && MetaTileEntityQuantumTankTc.this.lockedFluid == null) {
                    MetaTileEntityQuantumTankTc.this.lockedFluid = resource.copy();
                    MetaTileEntityQuantumTankTc.this.lockedFluid.amount = 1;
                }

                return MetaTileEntityQuantumTankTc.this.voiding ? resource.amount : accepted;
            }
        }

        public boolean canFillFluidType(FluidStack fluid) {
            boolean flag = false;
            for (var s:POAspectToGtFluidList.aspectToGtFluidList.values())
            {
                if(fluid.isFluidEqual(s.getFluid(1)))
                    flag=true;
            }
            if(!flag)
                return false;
            return this.test(fluid);
        }

        public IFilter<FluidStack> getFilter() {
            return this;
        }

        public boolean test(@NotNull FluidStack fluidStack) {
            return !MetaTileEntityQuantumTankTc.this.locked || MetaTileEntityQuantumTankTc.this.lockedFluid == null || fluidStack.isFluidEqual(MetaTileEntityQuantumTankTc.this.lockedFluid);
        }

        public int getPriority() {
            return MetaTileEntityQuantumTankTc.this.locked && MetaTileEntityQuantumTankTc.this.lockedFluid != null ? IFilter.whitelistPriority(1) : IFilter.noPriority();
        }
    }
}
