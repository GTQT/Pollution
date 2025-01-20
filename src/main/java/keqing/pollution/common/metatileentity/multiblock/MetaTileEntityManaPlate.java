package keqing.pollution.common.metatileentity.multiblock;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.IRenderSetup;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.shader.postprocessing.BloomEffect;
import gregtech.client.shader.postprocessing.BloomType;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.client.utils.EffectRenderContext;
import gregtech.client.utils.IBloomEffect;
import gregtech.client.utils.RenderBufferHelper;
import gregtech.common.ConfigHolder;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityBaseWithControl;
import keqing.pollution.api.capability.IManaMultiblock;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POManaPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static net.minecraft.util.EnumFacing.Axis.Y;


public class MetaTileEntityManaPlate extends MetaTileEntityBaseWithControl implements IManaMultiblock, IBloomEffect, IFastRenderMetaTileEntity {
    protected static final int NO_COLOR = 0;
    int speed = 0;
    int speedMax = 1;
    int updatetime;
    boolean backA;
    int RadomTime;
    private int fusionRingColor = NO_COLOR;
    private boolean registeredBloomRenderTicket;

    public MetaTileEntityManaPlate(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    private static BloomType getBloomType() {
        ConfigHolder.FusionBloom fusionBloom = ConfigHolder.client.shader.fusionBloom;
        return BloomType.fromValue(fusionBloom.useShader ? fusionBloom.bloomStyle : -1);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaPlate(metaTileEntityId);
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        speedMax = getTier();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("speed", this.speed);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.speed = data.getInteger("speed");
    }

    @Override
    @Nonnull
    protected Widget getFlexButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::decrementThreshold)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("speed down"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::incrementThreshold)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("speed up"));
        return group;
    }

    private void incrementThreshold(Widget.ClickData clickData) {
        this.speed = MathHelper.clamp(speed + 1, 0, speedMax);
    }

    private void decrementThreshold(Widget.ClickData clickData) {
        this.speed = MathHelper.clamp(speed - 1, 0, speedMax);
    }

    public int getRapid() {
        return speed;
    }

    @Override
    public void update() {
        super.update();
        if (updatetime < 10) updatetime++;
        else updatetime = 0;

        if (!backA) if (RadomTime <= 10) RadomTime++;
        if (backA) if (RadomTime >= -10) RadomTime--;
        if (RadomTime == 10) {
            backA = true;
        }
        if (RadomTime == -10) {
            backA = false;
        }
        setFusionRingColor(0xFF000000 + RadomTime * 1250 * 50);
    }

    @Override
    protected void updateFormedValid() {
        if (speed == 0) return;
        final int xDir = this.getFrontFacing().getOpposite().getXOffset() * 5;
        final int zDir = this.getFrontFacing().getOpposite().getZOffset() * 5;
        for (int x = -5; x <= 5; ++x) {
            for (int y = -5; y <= 5; ++y) {
                BlockPos poss = this.getPos().add(xDir + x, 0, zDir + y);
                if (GTUtility.getMetaTileEntity(this.getWorld(), poss.add(0, 1, 0)) instanceof MetaTileEntity) {

                    MetaTileEntity mte = GTUtility.getMetaTileEntity(this.getWorld(), poss.add(0, 1, 0));
                    TileEntity te = this.getWorld().getTileEntity(poss.add(0, 1, 0));

                    if (mte.isActive() && !work()) return;
                    //多方块
                    if (mte instanceof MultiblockControllerBase mcb) {
                        if (mcb.isStructureFormed() && mcb.isValid()) {
                            final var inenergy = mcb.getAbilities(MultiblockAbility.INPUT_ENERGY);
                            if (inenergy.size() > 0) {
                                long[] energys = new long[inenergy.size()];
                                for (int j = 0; j < inenergy.size(); j++) {
                                    energys[j] = inenergy.get(j).getEnergyStored();
                                }
                                if (te instanceof ITickable) {
                                    for (int i = 0; i < getRapid(); i++) {
                                        ((ITickable) te).update();
                                        for (int j = 0; j < inenergy.size(); j++) {
                                            if (inenergy.get(j).getEnergyStored() < energys[j])
                                                inenergy.get(j).addEnergy(energys[j] - inenergy.get(j).getEnergyStored());
                                        }
                                    }
                                }
                            } else if (mte instanceof RecipeMapSteamMultiblockController smte) {
                                final var fin = smte.getSteamFluidTank();
                                int[] energys = new int[fin.getTanks()];
                                for (int j = 0; j < fin.getTanks(); j++) {
                                    energys[j] = fin.getTankAt(j).getFluidAmount();
                                }
                                if (te instanceof ITickable) {
                                    for (int i = 0; i < getRapid(); i++) {
                                        ((ITickable) te).update();
                                        for (int j = 0; j < fin.getTanks(); j++) {
                                            if (fin.getTankAt(j).getFluidAmount() < energys[j]) {
                                                fin.getTankAt(j).fill(Materials.Steam.getFluid(energys[j] - fin.getTankAt(j).getFluidAmount()), true);
                                            }

                                        }
                                    }
                                }
                            } else {
                                for (int i = 0; i < getRapid(); i++) {
                                    ((ITickable) te).update();
                                }
                                ((ITickable) te).update();
                            }
                        }
                    } else {
                        long cache = 0;
                        for (EnumFacing facing : EnumFacing.VALUES) {
                            if (mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing) instanceof IEnergyContainer) {
                                IEnergyContainer container = mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing);
                                cache = container.getEnergyStored();
                            }
                        }
                        if (te instanceof ITickable tickable) {
                            for (int i = 0; i < getRapid(); i++) {
                                tickable.update();
                                addEnergy(this.getWorld(), poss.add(0, 1, 0), cache);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addEnergy(World world, BlockPos pos, long cache) {
        MetaTileEntity mte = GTUtility.getMetaTileEntity(world, pos);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing) instanceof IEnergyContainer) {
                IEnergyContainer container1 = mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing);
                if (cache > container1.getEnergyStored())
                    container1.changeEnergy(cache - container1.getEnergyStored());
            }
        }

    }

    @Nonnull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCSCCCCC")
                .where('S', selfPredicate())
                .where(' ', any())
                .where('C', states(getCasingAState())
                        .or(abilities(POMultiblockAbility.MANA_HATCH).setExactLimit(1)))
                .build();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("等级: %s |魔力： %s / %s", this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTier(), this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMana(), this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMaxMana()));
            textList.add(new TextComponentTranslation("加速速率: %s |魔力消耗： %s", speed, Math.pow(2, speed - 1)));
        }
    }

    @Override
    public int getTier() {
        return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTier();
    }

    @Override
    public boolean work() {
        return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).consumeMana((int) Math.pow(2, speed - 1));
    }

    private IBlockState getCasingAState() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_BASIC);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.MANA_BASIC;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    protected int getFusionRingColor() {
        return this.fusionRingColor;
    }

    protected void setFusionRingColor(int fusionRingColor) {
        if (this.fusionRingColor != fusionRingColor) {
            this.fusionRingColor = fusionRingColor;
        }
    }

    protected boolean hasFusionRingColor() {
        return true;
    }

    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.DATA_BANK_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), true,
                true);
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
    @SideOnly(Side.CLIENT)
    public void renderBloomEffect(BufferBuilder buffer, EffectRenderContext context) {
        int color = this.getFusionRingColor();


        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        EnumFacing relativeBack = RelativeDirection.BACK.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        if (isStructureFormed()) {

            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 5 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + updatetime * 0.1,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 5 + 0.5,
                    8, 0.1, 10, 20,
                    r, g, b, a, Y);

            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 5 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + updatetime * 0.1 + 1,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 5 + 0.5,
                    8, 0.1, 10, 20,
                    r, g, b, a, Y);

            RenderBufferHelper.renderRing(buffer,
                    getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 5 + 0.5,
                    getPos().getY() - context.cameraY() + relativeBack.getYOffset() + updatetime * 0.1 + 2,
                    getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 5 + 0.5,
                    8, 0.1, 10, 20,
                    r, g, b, a, Y);

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
