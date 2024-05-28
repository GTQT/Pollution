package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.interpolate.Eases;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.IRenderSetup;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.shader.postprocessing.BloomEffect;
import gregtech.client.shader.postprocessing.BloomType;
import gregtech.client.utils.*;
import gregtech.common.ConfigHolder;
import keqing.gtqtcore.api.GTQTValue;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aura.AuraHelper;

import java.util.Collections;
import java.util.List;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.*;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_FUSION_REACTOR;
import static keqing.pollution.api.unification.PollutionMaterials.ErichAura;
import static keqing.pollution.api.unification.PollutionMaterials.RichAura;

public class MetaTileEntityMagicFusionReactor extends RecipeMapMultiblockController implements IBloomEffect, IFastRenderMetaTileEntity {
	int glass;//不知道
	int coil;//灵气增长倍率
	int frame;//容纳上限
	int compose = 0;//输出倍率
	int aura;//灵气

	public MetaTileEntityMagicFusionReactor(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, MAGIC_FUSION_REACTOR);
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityMagicFusionReactor(this.metaTileEntityId);
	}

	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		textList.add(new TextComponentTranslation("灵气富集存储：%s / %s Aura", aura, frame * 25000));
		textList.add(new TextComponentTranslation("存储速率: %s 转换速率: %s", coil, compose));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("aura", this.aura);
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.aura = data.getInteger("aura");

	}

	public int getCompose() {
		if (compose == 0) return 0;
		return compose;
	}

	public boolean fillTanks(FluidStack stack, boolean simulate) {
		return GTTransferUtils.addFluidsToFluidHandler(this.getOutputFluidInventory(), simulate, Collections.singletonList(stack));
	}

	FluidStack AURA_STACK = RichAura.getFluid(getCompose());
	FluidStack EAURA_STACK = ErichAura.getFluid(getCompose());

	@Override
	public void update() {
		super.update();
		IMultipleTankHandler inputTank = getInputFluidInventory();
		if (this.recipeMapWorkable.isActive() && aura < frame * 25000) {
			AuraHelper.drainFlux(getWorld(), getPos(), (float) ((coil / compose) * 0.05), false);
			aura += 5 * coil;
		} else if (aura > 0) aura--;

		if (AURA_STACK.isFluidStackIdentical(inputTank.drain(AURA_STACK, false))) {
			AURA_STACK.isFluidStackIdentical(inputTank.drain(AURA_STACK, true));
			aura -= 10 * getCompose();
			this.fillTanks(this.EAURA_STACK, false);
		}
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("           ", "           ", "           ", "     A     ", "     A     ", "   AAAAA   ", "     A     ", "     A     ", "           ", "           ", "           ")
				.aisle("           ", "     A     ", "     A     ", "   BBABB   ", "   B   B   ", "  AA A AA  ", "   B   B   ", "   BBABB   ", "     A     ", "     A     ", "           ")
				.aisle("           ", "     A     ", "           ", "           ", "    CCC    ", " A  CCC  A ", "    CCC    ", "           ", "           ", "     A     ", "           ")
				.aisle("     A     ", "   BBABB   ", "           ", " B       B ", " B  DDD  B ", "AA  DDD  AA", " B  DDD  B ", " B       B ", "           ", "   BBABB   ", "     A     ")
				.aisle("     A     ", "   B   B   ", "    CCC    ", " B  DDD  B ", "  CD   DC  ", "A CD   DC A", "  CD   DC  ", " B  DDD  B ", "    CCC    ", "   B   B   ", "     A     ")
				.aisle("   AAAAA   ", " AAA A AAA ", " A  CCC  A ", "AA  DDD  AA", "A CD   DC A", "AACD   DCAA", "A CD   DC A", "AA  DDD  AA", " A  CCC  A ", " AAA A AAA ", "   AAAAA   ")
				.aisle("     A     ", "   B   B   ", "    CCC    ", " B  DDD  B ", "  CD   DC  ", "A CD   DC A", "  CD   DC  ", " B  DDD  B ", "    CCC    ", "   B   B   ", "     A     ")
				.aisle("     A     ", "   BBABB   ", "           ", " B       B ", " B  DDD  B ", "AA  DDD  AA", " B  DDD  B ", " B       B ", "           ", "   BBABB   ", "     A     ")
				.aisle("           ", "     A     ", "           ", "           ", "    CCC    ", " A  CCC  A ", "    CCC    ", "           ", "           ", "     A     ", "           ")
				.aisle("           ", "     A     ", "     A     ", "   BBABB   ", "   B   B   ", "  AA A AA  ", "   B   B   ", "   BBABB   ", "     A     ", "     A     ", "           ")
				.aisle("           ", "           ", "           ", "     A     ", "     A     ", "   AASAA   ", "     A     ", "     A     ", "           ", "           ", "           ")
				.where('S', selfPredicate())
				.where('A', CP_FRAME.setMinGlobalLimited(105).or(autoAbilities()))
				.where('B', CP_COIL_CASING)
				.where('C', CP_COMPOSE)
				.where('D', CP_GLASS)
				.where(' ', any())
				.build();
	}

	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		Object coil = context.get("COILTiredStats");
		this.coil = POUtils.getOrDefault(() -> coil instanceof WrappedIntTired,
				() -> ((WrappedIntTired) coil).getIntTier(),
				1);
		Object glass = context.get("GLASSTiredStats");
		this.glass = POUtils.getOrDefault(() -> glass instanceof WrappedIntTired,
				() -> ((WrappedIntTired) glass).getIntTier(),
				1);
		Object frame = context.get("FRAMETiredStats");
		this.frame = POUtils.getOrDefault(() -> frame instanceof WrappedIntTired,
				() -> ((WrappedIntTired) frame).getIntTier(),
				1);
		Object compose = context.get("COMPOSETiredStats");
		this.compose = POUtils.getOrDefault(() -> compose instanceof WrappedIntTired,
				() -> ((WrappedIntTired) compose).getIntTier(),
				1);
		this.writeCustomData(GTQTValue.UPDATE_TIER, buf -> buf.writeInt(this.frame));
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		switch (this.frame) {
			case (2) -> {
				return POTextures.FRAME_II;
			}
			case (3) -> {
				return POTextures.FRAME_III;
			}
			case (4) -> {
				return POTextures.FRAME_IV;
			}
			default -> {
				return POTextures.FRAME_I;
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		tooltip.add(I18n.format("源质分解时会提高装置内的灵气富集度，需要使用灵气流质来对富集灵气进行交换"));
		tooltip.add(I18n.format("多方块在工作时会消耗少量当前区块灵气辅助源质分解，升级线圈以减少消耗"));
		tooltip.add(I18n.format("分别升级线圈以及核心提高存储和传输灵气速率，升级外部框架提高装置的灵气缓存"));
	}

	@Override
	public void writeInitialSyncData(PacketBuffer buf) {
		super.writeInitialSyncData(buf);
		buf.writeInt(this.frame);
	}

	@Override
	public void receiveInitialSyncData(PacketBuffer buf) {
		super.receiveInitialSyncData(buf);
		this.frame = buf.readInt();
	}

	@Override
	public void receiveCustomData(int dataId, PacketBuffer buf) {
		super.receiveCustomData(dataId, buf);
		if (dataId == GTQTValue.UPDATE_TIER) {
			this.frame = buf.readInt();
		}
		if (dataId == GTQTValue.REQUIRE_DATA_UPDATE) {
			this.writeCustomData(GTQTValue.UPDATE_TIER, buf1 -> buf1.writeInt(this.frame));
		}
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}

	@Override
	public boolean canBeDistinct() {
		return true;
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

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBloomEffect(BufferBuilder buffer, EffectRenderContext context) {
		int color = RenderUtil.interpolateColor(this.getFusionRingColor(), -1, Eases.QUAD_IN.getInterpolation(
				Math.abs((Math.abs(getOffsetTimer() % 100) + context.partialTicks()) - 25) / 25));


		float a = (float) (color >> 24 & 255) / 255.0F;
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		EnumFacing relativeBack = RelativeDirection.BACK.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
				isFlipped());
		EnumFacing.Axis axis = RelativeDirection.UP.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped())
				.getAxis();

		if (this.recipeMapWorkable.isActive()) {
			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 5 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset(),
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 5 + 0.5,
					1, 1, 10, 20,
					r, g, b, a, EnumFacing.Axis.Y);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 5 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset(),
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 5 + 0.5,
					1, 1, 10, 20,
					r, g, b, a, EnumFacing.Axis.X);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 5 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset(),
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 5 + 0.5,
					1, 1, 10, 20,
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
