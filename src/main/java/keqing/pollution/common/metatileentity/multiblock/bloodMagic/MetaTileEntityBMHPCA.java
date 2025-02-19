package keqing.pollution.common.metatileentity.multiblock.bloodMagic;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IHPCAComponentHatch;
import gregtech.api.capability.IHPCAComputationProvider;
import gregtech.api.capability.IHPCACoolantProvider;
import gregtech.api.capability.IOpticalComputationProvider;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.resources.IGuiTexture;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.SuppliedImageWidget;
import gregtech.api.gui.widgets.ProgressWidget.MoveType;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.IProgressBarMultiblock;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.ConfigHolder;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.core.sound.GTSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static keqing.pollution.common.block.metablocks.POComputerCasing.CasingType.*;

public class MetaTileEntityBMHPCA extends MultiblockWithDisplayBase implements IOpticalComputationProvider, IControllable, IProgressBarMultiblock {
    private IEnergyContainer energyContainer = new EnergyContainerList(new ArrayList<>());
    private IFluidHandler coolantHandler;
    private final HPCAGridHandler hpcaHandler = new HPCAGridHandler(this);
    private boolean isActive;
    private boolean isWorkingEnabled = true;
    private boolean hasNotEnoughEnergy;
    private double temperature = 200.0;
    private final ProgressWidget.TimedProgressSupplier progressSupplier = new ProgressWidget.TimedProgressSupplier(200, 47, false);

    public MetaTileEntityBMHPCA(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBMHPCA(this.metaTileEntityId);
    }

    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.energyContainer = new EnergyContainerList(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
        this.coolantHandler = new FluidTankList(false, this.getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.hpcaHandler.onStructureForm(this.getAbilities(MultiblockAbility.HPCA_COMPONENT));
    }

    public void invalidateStructure() {
        super.invalidateStructure();
        this.energyContainer = new EnergyContainerList(new ArrayList());
        this.hpcaHandler.onStructureInvalidate();
    }

    public int requestCWUt(int cwut, boolean simulate, Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return this.isActive() && this.isWorkingEnabled() && !this.hasNotEnoughEnergy ? this.hpcaHandler.allocateCWUt(cwut, simulate) : 0;
    }

    public int getMaxCWUt( Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return this.isActive() && this.isWorkingEnabled() ? this.hpcaHandler.getMaxCWUt() : 0;
    }

    public boolean canBridge( Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return !this.isStructureFormed() || this.hpcaHandler.hasHPCABridge();
    }

    public void update() {
        super.update();
        if (this.getWorld().isRemote) {
            if (this.isStructureFormed()) {
                this.hpcaHandler.tryGatherClientComponents(this.getWorld(), this.getPos(), this.getFrontFacing(), this.getUpwardsFacing(), this.isFlipped());
            } else {
                this.hpcaHandler.clearClientComponents();
            }
        }

    }

    protected void updateFormedValid() {
        if (this.isWorkingEnabled()) {
            this.consumeEnergy();
        }

        if (this.isActive()) {
            double midpoint = 400.0;
            double temperatureChange = this.hpcaHandler.calculateTemperatureChange(this.coolantHandler, this.temperature >= midpoint) / 2.0;
            if (this.temperature + temperatureChange <= 200.0) {
                this.temperature = 200.0;
            } else {
                this.temperature += temperatureChange;
            }

            if (this.temperature >= 1000.0) {
                this.hpcaHandler.attemptDamageHPCA();
            }

            this.hpcaHandler.tick();
        } else {
            this.hpcaHandler.clearComputationCache();
            this.temperature = Math.max(200.0, this.temperature - 0.25);
        }

    }

    private void consumeEnergy() {
        long energyToConsume = this.hpcaHandler.getCurrentEUt();
        boolean hasMaintenance = ConfigHolder.machines.enableMaintenance && this.hasMaintenanceMechanics();
        if (hasMaintenance) {
            energyToConsume += (long)this.getNumMaintenanceProblems() * energyToConsume / 10L;
        }

        if (this.hasNotEnoughEnergy && this.energyContainer.getInputPerSec() > 19L * energyToConsume) {
            this.hasNotEnoughEnergy = false;
        }

        if (this.energyContainer.getEnergyStored() >= energyToConsume) {
            if (!this.hasNotEnoughEnergy) {
                long consumed = this.energyContainer.removeEnergy(energyToConsume);
                if (consumed == -energyToConsume) {
                    this.setActive(true);
                } else {
                    this.hasNotEnoughEnergy = true;
                    this.setActive(false);
                }
            }
        } else {
            this.hasNotEnoughEnergy = true;
            this.setActive(false);
        }

    }

    protected  BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("AA", "CC", "CC", "CC", "AA")
                .aisle("VA", "XV", "XV", "XV", "VA")
                .aisle("VA", "XV", "XV", "XV", "VA")
                .aisle("VA", "XV", "XV", "XV", "VA")
                .aisle("SA", "CC", "CC", "CC", "AA")
                .where('S', this.selfPredicate())
                .where('A', states(getAdvancedState()))
                .where('V', states(getVentState()))
                .where('X', abilities(MultiblockAbility.HPCA_COMPONENT))
                .where('C', states(getCasingState()).setMinGlobalLimited(5)
                        .or(this.maintenancePredicate())
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1))
                        .or(abilities(MultiblockAbility.COMPUTATION_DATA_TRANSMISSION).setExactLimit(1)))
                .build();
    }

    private static  IBlockState getCasingState() {
        return PollutionMetaBlocks.COMPUTER_CASING.getState(COMPUTER_CASING);
    }

    private static  IBlockState getAdvancedState() {
        return PollutionMetaBlocks.COMPUTER_CASING.getState(ADVANCED_COMPUTER_CASING);
    }

    private static  IBlockState getVentState() {
        return PollutionMetaBlocks.COMPUTER_CASING.getState(COMPUTER_HEAT_VENT);
    }

    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder()
                .aisle("AA", "EC", "MC", "HC", "AA")
                .aisle("VA", "6V", "3V", "0V", "VA")
                .aisle("VA", "7V", "4V", "1V", "VA")
                .aisle("VA", "8V", "5V", "2V", "VA")
                .aisle("SA", "CC", "CC", "OC", "AA")
                .where('S', PollutionMetaTileEntities.BMHPCA, EnumFacing.SOUTH)
                .where('A', getAdvancedState())
                .where('V', getVentState())
                .where('C', getCasingState())
                .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[6], EnumFacing.NORTH)
                .where('H', MetaTileEntities.FLUID_IMPORT_HATCH[1], EnumFacing.NORTH)
                .where('O', MetaTileEntities.COMPUTATION_HATCH_TRANSMITTER, EnumFacing.SOUTH)
                .where('M', () -> ConfigHolder.machines.enableMaintenance ? MetaTileEntities.MAINTENANCE_HATCH : getCasingState(), EnumFacing.NORTH);
        shapeInfo.add(builder.shallowCopy().where('0', PollutionMetaTileEntities.BMHPCA_EMPTY_COMPONENT, EnumFacing.WEST).where('1', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('2', PollutionMetaTileEntities.BMHPCA_EMPTY_COMPONENT, EnumFacing.WEST).where('3', PollutionMetaTileEntities.BMHPCA_EMPTY_COMPONENT, EnumFacing.WEST).where('4', PollutionMetaTileEntities.BMHPCA_COMPUTATION_COMPONENT, EnumFacing.WEST).where('5', PollutionMetaTileEntities.BMHPCA_EMPTY_COMPONENT, EnumFacing.WEST).where('6', PollutionMetaTileEntities.BMHPCA_EMPTY_COMPONENT, EnumFacing.WEST).where('7', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('8', PollutionMetaTileEntities.BMHPCA_EMPTY_COMPONENT, EnumFacing.WEST).build());
        shapeInfo.add(builder.shallowCopy().where('0', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('1', PollutionMetaTileEntities.BMHPCA_COMPUTATION_COMPONENT, EnumFacing.WEST).where('2', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('3', PollutionMetaTileEntities.BMHPCA_SUPER_COOLER_COMPONENT, EnumFacing.WEST).where('4', PollutionMetaTileEntities.BMHPCA_COMPUTATION_COMPONENT, EnumFacing.WEST).where('5', PollutionMetaTileEntities.BMHPCA_BRIDGE_COMPONENT, EnumFacing.WEST).where('6', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('7', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('8', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).build());
        shapeInfo.add(builder.shallowCopy().where('0', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('1', PollutionMetaTileEntities.BMHPCA_COMPUTATION_COMPONENT, EnumFacing.WEST).where('2', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('3', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('4', PollutionMetaTileEntities.BMHPCA_COMPUTATION_COMPONENT, EnumFacing.WEST).where('5', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('6', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('7', PollutionMetaTileEntities.BMHPCA_BRIDGE_COMPONENT, EnumFacing.WEST).where('8', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).build());
        shapeInfo.add(builder.shallowCopy().where('0', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('1', PollutionMetaTileEntities.BMHPCA_ADVANCED_COMPUTATION_COMPONENT, EnumFacing.WEST).where('2', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('3', PollutionMetaTileEntities.BMHPCA_SUPER_COOLER_COMPONENT, EnumFacing.WEST).where('4', PollutionMetaTileEntities.BMHPCA_BRIDGE_COMPONENT, EnumFacing.WEST).where('5', PollutionMetaTileEntities.BMHPCA_SUPER_COOLER_COMPONENT, EnumFacing.WEST).where('6', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).where('7', PollutionMetaTileEntities.BMHPCA_ULTIMATE_COOLER_COMPONENT, EnumFacing.WEST).where('8', PollutionMetaTileEntities.BMHPCA_ADVANCED_COOLER_COMPONENT, EnumFacing.WEST).build());
        return shapeInfo;
    }

    @SideOnly(Side.CLIENT)
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return sourcePart == null ? POTextures.BMADVANCED_COMPUTER_CASING : POTextures.BMCOMPUTER_CASING;
    }

    @SideOnly(Side.CLIENT)
    protected ICubeRenderer getFrontOverlay() {
        return POTextures.BMHPCA_OVERLAY;
    }

    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), this.isActive(), this.isWorkingEnabled());
    }

    public boolean isActive() {
        return super.isActive() && this.isActive;
    }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            this.markDirty();
            if (this.getWorld() != null && !this.getWorld().isRemote) {
                this.writeCustomData(GregtechDataCodes.WORKABLE_ACTIVE, (buf) -> buf.writeBoolean(active));
            }
        }

    }

    public boolean isWorkingEnabled() {
        return this.isWorkingEnabled;
    }

    public void setWorkingEnabled(boolean isWorkingAllowed) {
        if (this.isWorkingEnabled != isWorkingAllowed) {
            this.isWorkingEnabled = isWorkingAllowed;
            this.markDirty();
            if (this.getWorld() != null && !this.getWorld().isRemote) {
                this.writeCustomData(GregtechDataCodes.WORKING_ENABLED, (buf) -> {
                    buf.writeBoolean(this.isWorkingEnabled);
                });
            }
        }

    }

    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = super.createUITemplate(entityPlayer);
        ProgressWidget var10001 = (new ProgressWidget(() -> this.hpcaHandler.getAllocatedCWUt() > 0 ? this.progressSupplier.getAsDouble() : 0.0, 74, 57, 47, 47, POTextures.BMHPCA_COMPONENT_OUTLINE, MoveType.HORIZONTAL)).setIgnoreColor(true);
        HPCAGridHandler var10002 = this.hpcaHandler;
        Objects.requireNonNull(var10002);
        builder.widget(var10001.setHoverTextConsumer(var10002::addInfo));
        int startX = 76;
        int startY = 59;

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                int index = i * 3 + j;
                Supplier<IGuiTexture> textureSupplier = () -> this.hpcaHandler.getComponentTexture(index);
                builder.widget((new SuppliedImageWidget(startX + 15 * j, startY + 15 * i, 13, 13, textureSupplier)).setIgnoreColor(true));
            }
        }

        return builder;
    }

    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, this.isStructureFormed()).setWorkingStatus(true, this.hpcaHandler.getAllocatedCWUt() > 0).setWorkingStatusKeys("gregtech.multiblock.idling", "gregtech.multiblock.idling", "gregtech.multiblock.data_bank.providing").addCustom((tl) -> {
            if (this.isStructureFormed()) {
                ITextComponent voltageName = new TextComponentString(GTValues.VNF[GTUtility.getTierByVoltage(this.hpcaHandler.getMaxEUt())]);
                tl.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.energy", new Object[]{TextFormattingUtil.formatNumbers(this.hpcaHandler.cachedEUt), TextFormattingUtil.formatNumbers(this.hpcaHandler.getMaxEUt()), voltageName}));
                ITextComponent cwutInfo = TextComponentUtil.stringWithColor(TextFormatting.AQUA, this.hpcaHandler.cachedCWUt + " / " + this.hpcaHandler.getMaxCWUt() + " CWU/t");
                tl.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.computation", new Object[]{cwutInfo}));
            }

        }).addWorkingStatusLine();
    }

    private TextFormatting getDisplayTemperatureColor() {
        if (this.temperature < 500.0) {
            return TextFormatting.GREEN;
        } else {
            return this.temperature < 750.0 ? TextFormatting.YELLOW : TextFormatting.RED;
        }
    }

    protected void addWarningText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, this.isStructureFormed(), false).addLowPowerLine(this.hasNotEnoughEnergy).addCustom((tl) -> {
            if (this.isStructureFormed()) {
                if (this.temperature > 500.0) {
                    tl.add(TextComponentUtil.translationWithColor(TextFormatting.YELLOW, "gregtech.multiblock.hpca.warning_temperature", new Object[0]));
                    tl.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.warning_temperature_active_cool", new Object[0]));
                }

                this.hpcaHandler.addWarnings(tl);
            }

        }).addMaintenanceProblemLines(this.getMaintenanceProblems());
    }

    protected void addErrorText(List<ITextComponent> textList) {
        super.addErrorText(textList);
        if (this.isStructureFormed()) {
            if (this.temperature > 1000.0) {
                textList.add(TextComponentUtil.translationWithColor(TextFormatting.RED, "gregtech.multiblock.hpca.error_temperature", new Object[0]));
            }

            this.hpcaHandler.addErrors(textList);
        }

    }

    public void addInformation(ItemStack stack,  World world,  List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.machine.high_performance_computing_array.tooltip.1"));
        tooltip.add(I18n.format("gregtech.machine.high_performance_computing_array.tooltip.2"));
        tooltip.add(I18n.format("gregtech.machine.high_performance_computing_array.tooltip.3"));
    }

    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public SoundEvent getSound() {
        return GTSoundEvents.COMPUTATION;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("isActive", this.isActive);
        data.setBoolean("isWorkingEnabled", this.isWorkingEnabled);
        data.setDouble("temperature", this.temperature);
        return data;
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.isActive = data.getBoolean("isActive");
        this.isWorkingEnabled = data.getBoolean("isWorkingEnabled");
        this.temperature = data.getDouble("temperature");
    }

    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(this.isActive);
        buf.writeBoolean(this.isWorkingEnabled);
    }

    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.isActive = buf.readBoolean();
        this.isWorkingEnabled = buf.readBoolean();
    }

    public void receiveCustomData(int dataId,  PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
            this.isActive = buf.readBoolean();
            this.scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.isWorkingEnabled = buf.readBoolean();
            this.scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.CACHED_CWU) {
            this.hpcaHandler.cachedCWUt = buf.readInt();
        }

    }

    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        return capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE ? GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this) : super.getCapability(capability, side);
    }

    public int getNumProgressBars() {
        return 2;
    }

    public double getFillPercentage(int index) {
        return index == 0 ? 1.0 * (double)this.hpcaHandler.cachedCWUt / (double)this.hpcaHandler.getMaxCWUt() : Math.min(1.0, this.temperature / 1000.0);
    }

    public TextureArea getProgressBarTexture(int index) {
        return index == 0 ? GuiTextures.PROGRESS_BAR_HPCA_COMPUTATION : GuiTextures.PROGRESS_BAR_FUSION_HEAT;
    }

    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        TextComponentString cwutInfo;
        if (index == 0) {
            cwutInfo = TextComponentUtil.stringWithColor(TextFormatting.AQUA, this.hpcaHandler.cachedCWUt + " / " + this.hpcaHandler.getMaxCWUt() + " CWU/t");
            hoverList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.computation", new Object[]{cwutInfo}));
        } else {
            cwutInfo = TextComponentUtil.stringWithColor(this.getDisplayTemperatureColor(), Math.round(this.temperature / 10.0) + "°C");
            hoverList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.temperature", new Object[]{cwutInfo}));
        }

    }

    public static class HPCAGridHandler {
        private final  MetaTileEntityBMHPCA controller;
        private final List<IHPCAComponentHatch> components = new ObjectArrayList();
        private final Set<IHPCACoolantProvider> coolantProviders = new ObjectOpenHashSet();
        private final Set<IHPCAComputationProvider> computationProviders = new ObjectOpenHashSet();
        private int numBridges;
        private int allocatedCWUt;
        private long cachedEUt;
        private int cachedCWUt;

        public HPCAGridHandler( MetaTileEntityBMHPCA controller) {
            this.controller = controller;
        }

        public void onStructureForm(Collection<IHPCAComponentHatch> components) {
            this.reset();
            Iterator var2 = components.iterator();

            while(var2.hasNext()) {
                IHPCAComponentHatch component = (IHPCAComponentHatch)var2.next();
                this.components.add(component);
                if (component instanceof IHPCACoolantProvider) {
                    IHPCACoolantProvider coolantProvider = (IHPCACoolantProvider)component;
                    this.coolantProviders.add(coolantProvider);
                }

                if (component instanceof IHPCAComputationProvider) {
                    IHPCAComputationProvider computationProvider = (IHPCAComputationProvider)component;
                    this.computationProviders.add(computationProvider);
                }

                if (component.isBridge()) {
                    ++this.numBridges;
                }
            }

        }

        private void onStructureInvalidate() {
            this.reset();
        }

        private void reset() {
            this.clearComputationCache();
            this.components.clear();
            this.coolantProviders.clear();
            this.computationProviders.clear();
            this.numBridges = 0;
        }

        private void clearComputationCache() {
            this.allocatedCWUt = 0;
        }

        public void tick() {
            if (this.cachedCWUt != this.allocatedCWUt) {
                this.cachedCWUt = this.allocatedCWUt;
                if (this.controller != null) {
                    this.controller.writeCustomData(GregtechDataCodes.CACHED_CWU, (buf) -> {
                        buf.writeInt(this.cachedCWUt);
                    });
                }
            }

            this.cachedEUt = this.getCurrentEUt();
            if (this.allocatedCWUt != 0) {
                this.allocatedCWUt = 0;
            }

        }

        public double calculateTemperatureChange(IFluidHandler coolantTank, boolean forceCoolWithActive) {
            int maxCWUt = Math.max(1, this.getMaxCWUt());
            int maxCoolingDemand = this.getMaxCoolingDemand();
            int temperatureIncrease = (int)Math.round(1.0 * (double)maxCoolingDemand * (double)this.allocatedCWUt / (double)maxCWUt);
            int maxPassiveCooling = 0;
            int maxActiveCooling = 0;
            int maxCoolantDrain = 0;
            Iterator var9 = this.coolantProviders.iterator();

            while(var9.hasNext()) {
                IHPCACoolantProvider coolantProvider = (IHPCACoolantProvider)var9.next();
                if (coolantProvider.isActiveCooler()) {
                    maxActiveCooling += coolantProvider.getCoolingAmount();
                    maxCoolantDrain += coolantProvider.getMaxCoolantPerTick();
                } else {
                    maxPassiveCooling += coolantProvider.getCoolingAmount();
                }
            }

            double temperatureChange = (double)(temperatureIncrease - maxPassiveCooling);
            if (maxActiveCooling == 0 && maxCoolantDrain == 0) {
                return temperatureChange;
            } else {
                if (!forceCoolWithActive && !((double)maxActiveCooling <= temperatureChange)) {
                    if (temperatureChange > 0.0) {
                        double temperatureToDecrease = Math.min(temperatureChange, (double)maxActiveCooling);
                        int coolantToDrain = Math.max(1, (int)((double)maxCoolantDrain * (temperatureToDecrease / (double)maxActiveCooling)));
                        FluidStack coolantStack = coolantTank.drain(this.getCoolantStack(coolantToDrain), true);
                        if (coolantStack != null) {
                            int coolantDrained = coolantStack.amount;
                            if (coolantDrained == coolantToDrain) {
                                return 0.0;
                            }

                            temperatureChange -= temperatureToDecrease * (1.0 * (double)coolantDrained / (double)coolantToDrain);
                        }
                    }
                } else {
                    FluidStack coolantStack = coolantTank.drain(this.getCoolantStack(maxCoolantDrain), true);
                    if (coolantStack != null) {
                        int coolantDrained = coolantStack.amount;
                        if (coolantDrained == maxCoolantDrain) {
                            temperatureChange -= (double)maxActiveCooling;
                        } else {
                            temperatureChange -= (double)maxActiveCooling * (1.0 * (double)coolantDrained / (double)maxCoolantDrain);
                        }
                    }
                }

                return temperatureChange;
            }
        }

        public FluidStack getCoolantStack(int amount) {
            return new FluidStack(this.getCoolant(), amount);
        }

        private Fluid getCoolant() {
            return Materials.PCBCoolant.getFluid();
        }

        public void attemptDamageHPCA() {
            if (GTValues.RNG.nextInt(200) == 0) {
                List<IHPCAComponentHatch> candidates = new ArrayList();
                Iterator var2 = this.components.iterator();

                while(var2.hasNext()) {
                    IHPCAComponentHatch component = (IHPCAComponentHatch)var2.next();
                    if (component.canBeDamaged()) {
                        candidates.add(component);
                    }
                }

                if (!candidates.isEmpty()) {
                    ((IHPCAComponentHatch)candidates.get(GTValues.RNG.nextInt(candidates.size()))).setDamaged(true);
                }
            }

        }

        public int allocateCWUt(int cwut, boolean simulate) {
            int maxCWUt = this.getMaxCWUt();
            int availableCWUt = maxCWUt - this.allocatedCWUt;
            int toAllocate = Math.min(cwut, availableCWUt);
            if (!simulate) {
                this.allocatedCWUt += toAllocate;
            }

            return toAllocate;
        }

        public int getAllocatedCWUt() {
            return this.allocatedCWUt;
        }

        public int getMaxCWUt() {
            int maxCWUt = 0;

            IHPCAComputationProvider computationProvider;
            for(Iterator var2 = this.computationProviders.iterator(); var2.hasNext(); maxCWUt += computationProvider.getCWUPerTick()) {
                computationProvider = (IHPCAComputationProvider)var2.next();
            }

            return maxCWUt;
        }

        public long getCurrentEUt() {
            int maximumCWUt = Math.max(1, this.getMaxCWUt());
            long maximumEUt = this.getMaxEUt();
            long upkeepEUt = this.getUpkeepEUt();
            return maximumEUt == upkeepEUt ? maximumEUt : upkeepEUt + (maximumEUt - upkeepEUt) * (long)this.allocatedCWUt / (long)maximumCWUt;
        }

        public long getUpkeepEUt() {
            long upkeepEUt = 0L;

            IHPCAComponentHatch component;
            for(Iterator var3 = this.components.iterator(); var3.hasNext(); upkeepEUt += (long)component.getUpkeepEUt()) {
                component = (IHPCAComponentHatch)var3.next();
            }

            return upkeepEUt;
        }

        public long getMaxEUt() {
            long maximumEUt = 0L;

            IHPCAComponentHatch component;
            for(Iterator var3 = this.components.iterator(); var3.hasNext(); maximumEUt += (long)component.getMaxEUt()) {
                component = (IHPCAComponentHatch)var3.next();
            }

            return maximumEUt;
        }

        public boolean hasHPCABridge() {
            return this.numBridges > 0;
        }

        public boolean hasActiveCoolers() {
            Iterator var1 = this.coolantProviders.iterator();

            IHPCACoolantProvider coolantProvider;
            do {
                if (!var1.hasNext()) {
                    return false;
                }

                coolantProvider = (IHPCACoolantProvider)var1.next();
            } while(!coolantProvider.isActiveCooler());

            return true;
        }

        public int getMaxCoolingAmount() {
            int maxCooling = 0;

            IHPCACoolantProvider coolantProvider;
            for(Iterator var2 = this.coolantProviders.iterator(); var2.hasNext(); maxCooling += coolantProvider.getCoolingAmount()) {
                coolantProvider = (IHPCACoolantProvider)var2.next();
            }

            return maxCooling;
        }

        public int getMaxCoolingDemand() {
            int maxCooling = 0;

            IHPCAComputationProvider computationProvider;
            for(Iterator var2 = this.computationProviders.iterator(); var2.hasNext(); maxCooling += computationProvider.getCoolingPerTick()) {
                computationProvider = (IHPCAComputationProvider)var2.next();
            }

            return maxCooling;
        }

        public int getMaxCoolantDemand() {
            int maxCoolant = 0;

            IHPCACoolantProvider coolantProvider;
            for(Iterator var2 = this.coolantProviders.iterator(); var2.hasNext(); maxCoolant += coolantProvider.getMaxCoolantPerTick()) {
                coolantProvider = (IHPCACoolantProvider)var2.next();
            }

            return maxCoolant;
        }

        public void addInfo(List<ITextComponent> textList) {
            ITextComponent data = TextComponentUtil.stringWithColor(TextFormatting.AQUA, Integer.toString(this.getMaxCWUt()));
            textList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.info_max_computation", new Object[]{data}));
            TextFormatting coolingColor = this.getMaxCoolingAmount() < this.getMaxCoolingDemand() ? TextFormatting.RED : TextFormatting.GREEN;
            data = TextComponentUtil.stringWithColor(coolingColor, Integer.toString(this.getMaxCoolingDemand()));
            textList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.info_max_cooling_demand", new Object[]{data}));
            data = TextComponentUtil.stringWithColor(coolingColor, Integer.toString(this.getMaxCoolingAmount()));
            textList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.info_max_cooling_available", new Object[]{data}));
            if (this.getMaxCoolantDemand() > 0) {
                data = TextComponentUtil.stringWithColor(TextFormatting.YELLOW, this.getMaxCoolantDemand() + "L ");
                ITextComponent coolantName = TextComponentUtil.translationWithColor(TextFormatting.YELLOW, "gregtech.multiblock.hpca.info_coolant_name", new Object[0]);
                data.appendSibling(coolantName);
            } else {
                data = TextComponentUtil.stringWithColor(TextFormatting.GREEN, "0");
            }

            textList.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.info_max_coolant_required", new Object[]{data}));
            if (this.numBridges > 0) {
                textList.add(TextComponentUtil.translationWithColor(TextFormatting.GREEN, "gregtech.multiblock.hpca.info_bridging_enabled", new Object[0]));
            } else {
                textList.add(TextComponentUtil.translationWithColor(TextFormatting.RED, "gregtech.multiblock.hpca.info_bridging_disabled", new Object[0]));
            }

        }

        public void addWarnings(List<ITextComponent> textList) {
            List<ITextComponent> warnings = new ArrayList();
            if (this.numBridges > 1) {
                warnings.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.warning_multiple_bridges", new Object[0]));
            }

            if (this.computationProviders.isEmpty()) {
                warnings.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.warning_no_computation", new Object[0]));
            }

            if (this.getMaxCoolingDemand() > this.getMaxCoolingAmount()) {
                warnings.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gregtech.multiblock.hpca.warning_low_cooling", new Object[0]));
            }

            if (!warnings.isEmpty()) {
                textList.add(TextComponentUtil.translationWithColor(TextFormatting.YELLOW, "gregtech.multiblock.hpca.warning_structure_header", new Object[0]));
                textList.addAll(warnings);
            }

        }

        public void addErrors(List<ITextComponent> textList) {
            if (this.components.stream().anyMatch(IHPCAComponentHatch::isDamaged)) {
                textList.add(TextComponentUtil.translationWithColor(TextFormatting.RED, "gregtech.multiblock.hpca.error_damaged", new Object[0]));
            }

        }

        public TextureArea getComponentTexture(int index) {
            return this.components.size() <= index ? GuiTextures.BLANK_TRANSPARENT : this.components.get(index).getComponentIcon();
        }

        public void tryGatherClientComponents(World world, BlockPos pos, EnumFacing frontFacing, EnumFacing upwardsFacing, boolean flip) {
            EnumFacing relativeUp = RelativeDirection.UP.getRelativeFacing(frontFacing, upwardsFacing, flip);
            if (this.components.isEmpty()) {
                BlockPos testPos = pos.offset(frontFacing.getOpposite(), 3).offset(relativeUp, 3);

                for(int i = 0; i < 3; ++i) {
                    for(int j = 0; j < 3; ++j) {
                        BlockPos tempPos = testPos.offset(frontFacing, j).offset(relativeUp.getOpposite(), i);
                        TileEntity te = world.getTileEntity(tempPos);
                        if (te instanceof IHPCAComponentHatch) {
                            IHPCAComponentHatch hatch = (IHPCAComponentHatch)te;
                            this.components.add(hatch);
                        } else if (te instanceof IGregTechTileEntity) {
                            IGregTechTileEntity igtte = (IGregTechTileEntity)te;
                            MetaTileEntity mte = igtte.getMetaTileEntity();
                            if (mte instanceof IHPCAComponentHatch) {
                                IHPCAComponentHatch hatch = (IHPCAComponentHatch)mte;
                                this.components.add(hatch);
                            }
                        }
                    }
                }
            }

        }

        public void clearClientComponents() {
            this.components.clear();
        }
    }
}
