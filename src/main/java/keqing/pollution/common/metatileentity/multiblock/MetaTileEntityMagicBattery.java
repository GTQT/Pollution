package keqing.pollution.common.metatileentity.multiblock;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IWorkable;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.IProgressBarMultiblock;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.IRenderSetup;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.shader.postprocessing.BloomEffect;
import gregtech.client.shader.postprocessing.BloomType;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.client.utils.EffectRenderContext;
import gregtech.client.utils.IBloomEffect;
import gregtech.client.utils.RenderBufferHelper;
import gregtech.common.ConfigHolder;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static gregtech.api.GTValues.VA;
import static gregtech.api.util.RelativeDirection.*;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_BEAM_CORE;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Y;

public class MetaTileEntityMagicBattery extends MultiblockWithDisplayBase implements IWorkable, IControllable, IProgressBarMultiblock, IBloomEffect, IFastRenderMetaTileEntity {
    @Override
    public boolean usesMui2() {
        return false;
    }
    protected static final int NO_COLOR = 0;
    public int process = 0;
    public int maxProcess = 100;
    int eu = 0;
    int p;
    long inputEu = 0;
    long outputEu = 0;
    long euDelta = 0;
    long runTick = 0;
    long runDay = 0;
    long runTime = 0;
    long runMin = 0;
    long runS = 0;
    double rate;
    int RadomTime;
    int Hight;
    double RadiumA;
    int coilLevel;
    int beamCore;
    boolean back;
    boolean backA;
    private boolean isActive = true, isWorkingEnabled = true;
    private IEnergyContainer inenergyContainer;
    private IEnergyContainer outenergyContainer;
    private int fusionRingColor = NO_COLOR;
    private boolean registeredBloomRenderTicket;

    public MetaTileEntityMagicBattery(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.MAGIC_BATTERY);
    }

    private static BloomType getBloomType() {
        ConfigHolder.FusionBloom fusionBloom = ConfigHolder.client.shader.fusionBloom;
        return BloomType.fromValue(fusionBloom.useShader ? fusionBloom.bloomStyle : -1);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityMagicBattery(this.metaTileEntityId);
    }

    public long euMax() {
        return (long) coilLevel * beamCore * 250000;
    }

    public int powerMax() {
        return VA[beamCore * 2];
    }

    @Override
    protected void updateFormedValid() {
        int store = 0;
        inputEu = 0;
        outputEu = 0;
        euDelta = 0;
        runTick = 0;
        runDay = 0;
        runTime = 0;
        runMin = 0;
        runS = 0;
        if (!this.getWorld().isRemote && this.isWorkingEnabled) {
            if (this.inenergyContainer != null && this.inenergyContainer.getEnergyStored() > 0 && euMax() >= eu && eu >= 0) {
                store = (int) Math.min(Math.min(this.inenergyContainer.getEnergyStored(), euMax() - eu), powerMax());
                inputEu = store;
                this.inenergyContainer.changeEnergy(-store);
                eu = eu + store;
            }

            store = 0;
            if (this.outenergyContainer != null && eu > 0) {
                store = (int) Math.min(Math.min(this.outenergyContainer.getEnergyCapacity() - this.outenergyContainer.getEnergyStored(), eu), powerMax());
                outputEu = store;
                this.outenergyContainer.changeEnergy(store);
                eu = eu - store;
            }
            euDelta = inputEu - outputEu;
            if (euDelta > 0) {
                runTick = (euMax() - eu) / euDelta / 20;
            }
            if (euDelta < 0) {
                runTick = -eu / euDelta / 20;
            }
            if (euDelta == 0) {
                runTick = eu / powerMax() / 20;
            }
            if (runTick != 0) {
                runDay = runTick / 86400;
                runTime = runTick % 86400 / 3600;
                runMin = runTick % 86400 % 3600 / 60;
                runS = runTick % 86400 % 3600 % 60;
            }
        }
        euDelta = inputEu - outputEu;
        rate = (eu * 1.0) / euMax();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("======================="));
        textList.add(new TextComponentTranslation("pollution.eke.count", beamCore, coilLevel));
        textList.add(new TextComponentTranslation("pollution.eke.eu1", inputEu, outputEu));
        textList.add(new TextComponentTranslation("pollution.eke.eu2", eu, euMax(), rate));
        if (this.isWorkingEnabled) {
            if (euDelta > 0) {
                textList.add(new TextComponentTranslation("pollution.eke.output1", euDelta, powerMax()));
                textList.add(new TextComponentTranslation("pollution.eke.outputA", runDay, runTime, runMin, runS));
            }
            if (euDelta < 0) {
                textList.add(new TextComponentTranslation("pollution.eke.output2", -euDelta, powerMax()));
                textList.add(new TextComponentTranslation("pollution.eke.outputB", runDay, runTime, runMin, runS));
            }
            if (euDelta == 0) {
                textList.add(new TextComponentTranslation("pollution.eke.output3", powerMax()));
                textList.add(new TextComponentTranslation("pollution.eke.outputC", runDay, runTime, runMin, runS));
            }
        } else {
            textList.add(new TextComponentTranslation("pollution.eke.output4", powerMax()));
        }
        textList.add(new TextComponentTranslation("======================="));
    }

    //进度条
    @Override
    public int getNumProgressBars() {
        return 1;
    }

    @Override
    public double getFillPercentage(int index) {
        return (eu * 1.0) / euMax();
    }

    @Override
    public TextureArea getProgressBarTexture(int index) {
        return GuiTextures.PROGRESS_BAR_HPCA_COMPUTATION;
    }

    @Override
    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        if (index == 0) {
            ITextComponent cwutInfo = TextComponentUtil.stringWithColor(
                    TextFormatting.AQUA,
                    (eu * 1.0) + " / " + euMax() + " EU");
            hoverList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.GRAY,
                    "gregtech.multiblock.battery.EU",
                    cwutInfo));
        }
    }

    //初始化能源仓室
    private void initializeAbilities() {
        this.inenergyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
        this.outenergyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.OUTPUT_ENERGY));

    }

    private void resetTileAbilities() {
        this.inenergyContainer = new EnergyContainerList(new ArrayList());
        this.outenergyContainer = new EnergyContainerList(new ArrayList());
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();

        resetTileAbilities();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        Object CoilLevel = context.get("COILTieredStats");
        Object beamCore = context.get("BEAMTieredStats");
        this.coilLevel = POUtils.getOrDefault(() -> CoilLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) CoilLevel).getIntTier(),
                0);
        this.beamCore = POUtils.getOrDefault(() -> beamCore instanceof WrappedIntTired,
                () -> ((WrappedIntTired) beamCore).getIntTier(),
                0);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        FactoryBlockPattern pattern = FactoryBlockPattern.start(RIGHT, UP, FRONT)
                .aisle("               ", "               ", "               ", "               ", "               ", "       A       ", "      AAA      ", "     AASAA     ", "      AAA      ", "       A       ", "               ", "               ", "               ", "               ", "               ")
                .aisle("               ", "               ", "               ", "       A       ", "     BBABB     ", "    B  A  B    ", "    B CCC B    ", "   AAACBCAAA   ", "    B CCC B    ", "    B  A  B    ", "     BBABB     ", "       A       ", "               ", "               ", "               ")
                .aisle("               ", "               ", "       A       ", "       A       ", "               ", "               ", "               ", "  AA       AA  ", "               ", "               ", "               ", "       A       ", "       A       ", "               ", "               ")
                .aisle("               ", "       A       ", "       A       ", "               ", "               ", "               ", "               ", " AA         AA ", "               ", "               ", "               ", "               ", "       A       ", "       A       ", "               ")
                .aisle("               ", "     BBABB     ", "               ", "               ", "               ", " B           B ", " B           B ", " A           A ", " B           B ", " B           B ", "               ", "               ", "               ", "     BBABB     ", "               ")
                .aisle("       A       ", "    B  A  B    ", "               ", "               ", " B           B ", "               ", "               ", "AA           AA", "               ", "               ", " B           B ", "               ", "               ", "    B  A  B    ", "       A       ")
                .aisle("      AAA      ", "    B CCC B    ", "               ", "               ", " B           B ", "               ", "AC           CA", "AC           CA", "AC           CA", "               ", " B           B ", "               ", "               ", "    B CCC B    ", "      AAA      ")
                .aisle("     AAAAA     ", "   AAACBCAAA   ", "  AA       AA  ", " AA         AA ", " A           A ", "AA           AA", "AC           CA", "AB           BA", "AC           CA", "AA           AA", " A           A ", " AA         AA ", "  AA       AA  ", "   AAACBCAAA   ", "     AAAAA     ")
                .aisle("      AAA      ", "    B CCC B    ", "               ", "               ", " B           B ", "               ", "AC           CA", "AC           CA", "AC           CA", "               ", " B           B ", "               ", "               ", "    B CCC B    ", "      AAA      ")
                .aisle("       A       ", "    B  A  B    ", "               ", "               ", " B           B ", "               ", "               ", "AA           AA", "               ", "               ", " B           B ", "               ", "               ", "    B  A  B    ", "       A       ")
                .aisle("               ", "     BBABB     ", "               ", "               ", "               ", " B           B ", " B           B ", " A           A ", " B           B ", " B           B ", "               ", "               ", "               ", "     BBABB     ", "               ")
                .aisle("               ", "       A       ", "       A       ", "               ", "               ", "               ", "               ", " AA         AA ", "               ", "               ", "               ", "               ", "       A       ", "       A       ", "               ")
                .aisle("               ", "               ", "       A       ", "       A       ", "               ", "               ", "               ", "  AA       AA  ", "               ", "               ", "               ", "       A       ", "       A       ", "               ", "               ")
                .aisle("               ", "               ", "               ", "       A       ", "     BBABB     ", "    B  A  B    ", "    B CCC B    ", "   AAACBCAAA   ", "    B CCC B    ", "    B  A  B    ", "     BBABB     ", "       A       ", "               ", "               ", "               ")
                .aisle("               ", "               ", "               ", "               ", "               ", "       A       ", "      AAA      ", "     AAAAA     ", "      AAA      ", "       A       ", "               ", "               ", "               ", "               ", "               ")

                .where('S', selfPredicate())
                .where('A', states(getCasingState())
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setMinGlobalLimited(1).setMaxGlobalLimited(1).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(16).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.OUTPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(16).setPreviewCount(1))
                )
                .where('B', CP_BEAM_CORE.get())
                .where('C', CP_COIL_CASING.get())
                .where(' ', any());

        return pattern.build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.MAGIC_BATTERY;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.RESEARCH_STATION_OVERLAY;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setBoolean("isActive", isActive);
        data.setBoolean("isWorkingEnabled", isWorkingEnabled);
        data.setInteger("Eu", eu);
        return super.writeToNBT(data);
    }

    //NBT读写这块需要搬过来  还有
    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        isActive = data.getBoolean("isActive");
        isWorkingEnabled = data.getBoolean("isWorkingEnabled");
        eu = data.getInteger("Eu");

    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(isActive);
        buf.writeBoolean(isWorkingEnabled);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        isActive = buf.readBoolean();
        isWorkingEnabled = buf.readBoolean();
    }

    @Override
    public int getProgress() {
        return this.process;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProcess;
    }

    public boolean isWorkingEnabled() {
        return this.isWorkingEnabled;
    }

    //先跑起来看看
    public void setWorkingEnabled(boolean b) {
        this.isWorkingEnabled = b;
        markDirty();
        World world = getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(isWorkingEnabled));
        }
    }

    @Override
    public boolean isActive() {
        return super.isActive() && this.isActive;
    }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            markDirty();
            World world = getWorld();
            if (world != null && !world.isRemote) {
                writeCustomData(GregtechDataCodes.WORKABLE_ACTIVE, buf -> buf.writeBoolean(active));
            }
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), this.isActive(),
                this.isWorkingEnabled());
    }

    @Override
    public boolean shouldShowVoidingModeButton() {
        return false;
    }

    protected int getFusionRingColor() {
        return this.fusionRingColor;
    }

    protected void setFusionRingColor(int fusionRingColor) {
        if (this.fusionRingColor != fusionRingColor) {
            this.fusionRingColor = fusionRingColor;
            //writeCustomData(GregtechDataCodes.UPDATE_COLOR, buf -> buf.writeVarInt(fusionRingColor));
        }
    }

    protected boolean hasFusionRingColor() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (this.hasFusionRingColor() && !this.registeredBloomRenderTicket) {
            this.registeredBloomRenderTicket = true;
            BloomEffectUtil.registerBloomRender(FusionBloomSetup.INSTANCE, getBloomType(), this, this);
        }
    }

    @Override
    public void update() {
        super.update();
        if (!backA) if (RadomTime <= 500) RadomTime++;
        if (backA) if (RadomTime >= -500) RadomTime--;
        if (RadomTime == 500) {
            backA = true;
        }
        if (RadomTime == -500) {
            backA = false;
        }
        setFusionRingColor(0xFF000000 + RadomTime * 1250);

        if (!back) if (Hight <= 30) Hight++;
        if (back) if (Hight >= -30) Hight--;
        if (Hight == 30) {
            back = true;
        }
        if (Hight == -30) {
            back = false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderBloomEffect(BufferBuilder buffer, EffectRenderContext context) {
        int color = this.getFusionRingColor();
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        EnumFacing relativeBack = RelativeDirection.BACK.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        if (this.eu > 0) {
            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 + 0.5,
                    1, 2, 10, 20,
                    r, g, b, a, Y);

            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 + 0.5,
                    1, 2, 10, 20,
                    r, g, b, a, X);

            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 + 0.5,
                    1, 2, 10, 20,
                    r, g, b, a, EnumFacing.Axis.Z);

            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 + (double) Hight / 10 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 + 0.5,
                    4, 0.2, 10, 20,
                    r, g, b, a, X);
            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + (double) Hight / 10 + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 + 0.5,
                    4, 0.2, 10, 20,
                    r, g, b, a, Y);
            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 + (double) Hight / 10 + 0.5,
                    4, 0.2, 10, 20,
                    r, g, b, a, EnumFacing.Axis.Z);

            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 - (double) Hight / 10 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 + 0.5,
                    4, 0.2, 10, 20,
                    r, g, b, a, X);
            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() - (double) Hight / 10 + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 + 0.5,
                    4, 0.2, 10, 20,
                    r, g, b, a, Y);
            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 7 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 0.5,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 7 - (double) Hight / 10 + 0.5,
                    4, 0.2, 10, 20,
                    r, g, b, a, EnumFacing.Axis.Z);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRenderBloomEffect(EffectRenderContext context) {
        return this.hasFusionRingColor();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        EnumFacing relativeRight = RelativeDirection.RIGHT.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());
        EnumFacing relativeBack = RelativeDirection.BACK.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        return new AxisAlignedBB(
                this.getPos().offset(relativeBack).offset(relativeRight, 6),
                this.getPos().offset(relativeBack, 13).offset(relativeRight.getOpposite(), 6));
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
    }

    @Override
    public boolean isGlobalRenderer() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    private static final class FusionBloomSetup implements IRenderSetup {

        private static final FusionBloomSetup INSTANCE = new FusionBloomSetup();

        float lastBrightnessX;
        float lastBrightnessY;

        @Override
        public void preDraw(BufferBuilder buffer) {
            BloomEffect.strength = (float) ConfigHolder.client.shader.fusionBloom.strength;
            BloomEffect.baseBrightness = (float) ConfigHolder.client.shader.fusionBloom.baseBrightness;
            BloomEffect.highBrightnessThreshold = (float) ConfigHolder.client.shader.fusionBloom.highBrightnessThreshold;
            BloomEffect.lowBrightnessThreshold = (float) ConfigHolder.client.shader.fusionBloom.lowBrightnessThreshold;
            BloomEffect.step = 1;

            lastBrightnessX = OpenGlHelper.lastBrightnessX;
            lastBrightnessY = OpenGlHelper.lastBrightnessY;
            GlStateManager.color(1, 1, 1, 1);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GlStateManager.disableTexture2D();

            buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        }

        @Override
        public void postDraw(BufferBuilder buffer) {
            Tessellator.getInstance().draw();

            GlStateManager.enableTexture2D();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
    }
}
