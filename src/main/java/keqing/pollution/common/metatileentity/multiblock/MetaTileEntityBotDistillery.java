package keqing.pollution.common.metatileentity.multiblock;

import gregicality.multiblocks.api.GCYMValues;
import gregicality.multiblocks.api.metatileentity.GCYMMultiblockAbility;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.RelativeDirection;
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
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiFluidHatch;
import gregtech.common.metatileentities.multi.multiblockpart.appeng.MetaTileEntityMEOutputHatch;
import keqing.gtqtcore.api.utils.GTQTUtil;
import keqing.gtqtcore.common.metatileentities.GTQTMetaTileEntities;
import keqing.gtqtcore.common.metatileentities.multi.multiblockpart.MetaTileEntityPlusMultiFluidHatch;
import keqing.pollution.api.metatileentity.POManaMultiblock;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POBotBlock;
import keqing.pollution.common.block.metablocks.POGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static gregtech.api.util.RelativeDirection.*;
import static keqing.pollution.common.block.metablocks.POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING;

public class MetaTileEntityBotDistillery extends POManaMultiblock implements IBloomEffect, IFastRenderMetaTileEntity {
    //工作配方
    public MetaTileEntityBotDistillery(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.DISTILLATION_RECIPES, RecipeMaps.DISTILLERY_RECIPES});
    }
    //方块注册
    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityBotDistillery(this.metaTileEntityId);
    }
    //多方块摆放
    @Override
    protected  BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, FRONT, UP)
                .aisle("___XXX___","___XXX___","__XXXXX__","XXXXXXXXX","XXXXXXXXX","XXXXXXXXX","__XXXXX__","___XXX___","___XXX___")
                .aisle("_________","___XSX___","__XXXXX__","_XXXXXXX_","_XXXZXXX_","_XXXXXXX_","__XXXXX__","___XXX___","_________")
                .aisle("_________","____X____","___XXX___","__XXXXX__","_XXXZXXX_","__XXXXX__","___XXX___","____X____","_________")
                .aisle("_________","_________","___M_M___","__Y___Y__","____Z____","__Y___Y__","___Y_Y___","_________","_________").setRepeatable(1, 12)
                .aisle("_________","_________","___Y_Y___","__Y___Y__","____Z____","__Y___Y__","___Y_Y___","_________","_________")
                .aisle("_________","_________","_________","_________","____Z____","_________","_________","_________","_________")

                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(28)
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))//必须设定最大最小
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setMaxGlobalLimited(1))//必须设定最大最小
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setExactLimit(1))//必须设定最大最小
                        .or(abilities(POMultiblockAbility.MANA_HATCH).setExactLimit(1))//必须要的仓 限定1及可
                        .or(abilities(GCYMMultiblockAbility.PARALLEL_HATCH).setExactLimit(1))//必须要的仓 限定1及可
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1)))//必须要的仓 限定1及可
                .where('Y', states(getCasingState2()))
                .where('M',states(getCasingState())
                        .or(metaTileEntities(MultiblockAbility.REGISTRY.get(MultiblockAbility.EXPORT_FLUIDS).stream()
                        .filter(mte -> !(mte instanceof MetaTileEntityMultiFluidHatch)
                                && !(mte instanceof MetaTileEntityMEOutputHatch)
                                && !(mte instanceof MetaTileEntityPlusMultiFluidHatch))
                        .toArray(MetaTileEntity[]::new))
                        .setMinLayerLimited(1).setMaxLayerLimited(1)))
                .where('Z', states(getCasingState3()))
                .where('_', any())
                .build();
    }
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (this.isStructureFormed()) {
            FluidStack stackInTank = this.importFluids.drain(Integer.MAX_VALUE, false);
            if (stackInTank != null && stackInTank.amount > 0) {
                TextComponentTranslation fluidName = new TextComponentTranslation(stackInTank.getFluid().getUnlocalizedName(stackInTank), new Object[0]);
                textList.add(new TextComponentTranslation("gregtech.multiblock.distillation_tower.distilling_fluid", new Object[]{fluidName}));
            }
        }

    }

    //设置外壳方块
    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
    }
    private static IBlockState getCasingState3() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
    }
    //设置主方块和功能仓室纹理
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.TERRA_WATERTIGHT_CASING;
    }
    //设置主方块机器纹理
    @Override
    protected  OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }
    //总线隔离
    @Override
    public boolean canBeDistinct() {
        return true;
    }
    //工具提示
    @Override
    public void addInformation(ItemStack stack,  World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.bot_distillery", 1));
    }

    protected static final int NO_COLOR = 0;
    private int fusionRingColor = NO_COLOR;
    private boolean registeredBloomRenderTicket;

    @Override
    protected boolean shouldShowVoidingModeButton() {
        return false;
    }

    protected int getFusionRingColor() {
        return this.fusionRingColor;
    }

    protected boolean hasFusionRingColor() {
        return true;
    }

    protected void setFusionRingColor(int fusionRingColor) {
        if (this.fusionRingColor != fusionRingColor) {
            this.fusionRingColor = fusionRingColor;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (this.hasFusionRingColor() && !this.registeredBloomRenderTicket) {
            this.registeredBloomRenderTicket = true;
            BloomEffectUtil.registerBloomRender(FusionBloomSetup.INSTANCE, getBloomType(), this, this);
        }
    }

    private static BloomType getBloomType() {
        ConfigHolder.FusionBloom fusionBloom = ConfigHolder.client.shader.fusionBloom;
        return BloomType.fromValue(fusionBloom.useShader ? fusionBloom.bloomStyle : -1);
    }

    boolean backA;
    int RadomTime;

    @Override
    public void update() {
        super.update();
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
    @SideOnly(Side.CLIENT)
    public void renderBloomEffect(BufferBuilder buffer, EffectRenderContext context) {
        int color = this.getFusionRingColor();


        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        EnumFacing relativeBack = RelativeDirection.BACK.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

            if(isStructureFormed())for(int i=0;i<5;i++) {
                RenderBufferHelper.renderRing(buffer,
                        getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 3 + 0.5,
                        getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 3+2*i,
                        getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 3 + 0.5,
                        1.6, 0.2, 10, 20,
                        r, g, b, a, EnumFacing.Axis.Y);


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