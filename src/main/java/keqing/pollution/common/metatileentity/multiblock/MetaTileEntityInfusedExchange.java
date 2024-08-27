package keqing.pollution.common.metatileentity.multiblock;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.GTTransferUtils;
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
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityBaseWithControl;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MetaTileEntityInfusedExchange extends MetaTileEntityBaseWithControl implements IBloomEffect, IFastRenderMetaTileEntity {
	public Aspect al;
	public String name;
	public int storage = 0;
	public int number = 0;
	FluidStack AIR_STACK = PollutionMaterials.infused_air.getFluid(1);
	FluidStack FIRE_STACK = PollutionMaterials.infused_fire.getFluid(1);
	FluidStack WATER_STACK = PollutionMaterials.infused_water.getFluid(1);
	FluidStack ERATH_STACK = PollutionMaterials.infused_earth.getFluid(1);
	FluidStack ORDER_STACK = PollutionMaterials.infused_order.getFluid(1);
	FluidStack ENTROPY_STACK = PollutionMaterials.infused_entropy.getFluid(1);
	boolean work;
	int randomTime = 0;

	public MetaTileEntityInfusedExchange(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
	}

	@Override
	public boolean hasMaintenanceMechanics() {
		return false;
	}

	public boolean hasMufflerMechanics() {
		return false;
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
		return new MetaTileEntityInfusedExchange(this.metaTileEntityId);
	}

	@Override
	public List<ITextComponent> getDataInfo() {
		return Collections.emptyList();
	}

	public void fillTanks(FluidStack stack) {
		GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(stack));
	}

	boolean backA;

	public void update() {
		super.update();
		if (!backA) if (randomTime <= 10) randomTime++;
		if (backA) if (randomTime >= -10) randomTime--;
		if (randomTime == 10) {
			backA = true;
		}
		if (randomTime == -10) {
			backA = false;
		}
		setFusionRingColor(0xFF000000 + randomTime * 1250 * 50);
	}

	public void updateFormedValid() {
		if (this.getWorld().getTileEntity(this.getPos().add(0, 2, 0)) instanceof TileJarFillable) {
			TileJarFillable s = (TileJarFillable) this.getWorld().getTileEntity(this.getPos().add(0, 2, 0));
			this.al = s.getEssentiaType(s.getFacing());
			this.storage = s.getEssentiaAmount(s.getFacing());
			if (this.al != null) {
				this.name = this.al.getName();
			}
		}
		if (this.number == 0) {
			this.number = this.storage;
			this.clearInfused();
			this.name = null;
		}

		if (this.number > 0 && this.outputFluidInventory != null) this.doFillTank();
	}

	private void doFillTank() {
		if (Objects.equals(this.name, "Aer")) {
			this.fillTanks(this.AIR_STACK);
			--this.number;
		}

		if (Objects.equals(this.name, "Terra")) {
			this.fillTanks(this.ERATH_STACK);
			--this.number;
		}

		if (Objects.equals(this.name, "Aqua")) {
			this.fillTanks(this.WATER_STACK);
			--this.number;
		}

		if (Objects.equals(this.name, "Ignis")) {
			this.fillTanks(this.FIRE_STACK);
			--this.number;
		}

		if (Objects.equals(this.name, "Ordo")) {
			this.fillTanks(this.ORDER_STACK);
			--this.number;
		}

		if (Objects.equals(this.name, "Perdition")) {
			this.fillTanks(this.ENTROPY_STACK);
			--this.number;
		}

	}

	public void clearInfused() {
		if (this.getWorld().getTileEntity(this.getPos().add(0, 2, 0)) instanceof TileJarFillable) {
			TileJarFillable s = (TileJarFillable) this.getWorld().getTileEntity(this.getPos().add(0, 2, 0));
			s.amount = 0;
		}

	}

	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		if (this.isStructureFormed()) {
			textList.add(new TextComponentTranslation("源质罐内缓存:Infused: %s  Amount: %s", new Object[]{this.name, this.storage}));
			textList.add(new TextComponentTranslation("多方块内缓存：Infused: %s  Amount: %s", new Object[]{this.name, this.number}));
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data.setInteger("number", this.number);
		return super.writeToNBT(data);
	}

	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.number = data.getInteger("number");
	}

	public void writeInitialSyncData(PacketBuffer buf) {
		super.writeInitialSyncData(buf);
		buf.writeInt(this.number);
	}

	public void receiveInitialSyncData(PacketBuffer buf) {
		super.receiveInitialSyncData(buf);
		this.number = buf.readInt();
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle(" AAAAAAA ", "         ", "         ", "         ", "         ")
				.aisle("AAAAAAAAA", "    A    ", "         ", "         ", "         ")
				.aisle("AAAAAAAAA", "  AAAAA  ", "  AA AA  ", "         ", "         ")
				.aisle("AAAAAAAAA", "  AAAAA  ", "  AAAAA  ", "   A A   ", "         ")
				.aisle("AAAAAAAAA", " AAAAAAA ", "   AAA   ", "    S    ", "    F    ")
				.aisle("AAAAAAAAA", "  AAAAA  ", "  AAAAA  ", "   A A   ", "         ")
				.aisle("AAAAAAAAA", "  AAAAA  ", "  AA AA  ", "         ", "         ")
				.aisle("AAAAAAAAA", "    A    ", "         ", "         ", "         ")
				.aisle(" AAAAAAA ", "         ", "         ", "         ", "         ")

				.where('S', selfPredicate())
				.where('A', states(getCasingState())
						.or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1)))
				.where(' ', any())
				.where('F', abilities(POMultiblockAbility.TANK_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
				.build();
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		super.renderMetaTileEntity(renderState, translation, pipeline);
		this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), true, true);
	}

	private static IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.VOID_PRISM);
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.VOID_PRISM;
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
			//writeCustomData(GregtechDataCodes.UPDATE_COLOR, buf -> buf.writeVarInt(fusionRingColor));
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
	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		tooltip.add(I18n.format("在源质仓正上方放置源质罐子即可输入对应的源质"));
		tooltip.add(I18n.format("仅限：Aer，Terra，Aqua，Ignis，Ordo，Perdition 源质"));
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
		EnumFacing.Axis axis = RelativeDirection.UP.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped())
				.getAxis();

		if (isStructureFormed()) {
			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 12,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() + 0.5,
					3, 1, 10, 20,
					r, g, b, a, axis);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 12,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() + 0.5,
					3, 1, 10, 20,
					r, g, b, a, EnumFacing.Axis.X);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 12,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() + 0.5,
					3, 1, 10, 20,
					r, g, b, a, EnumFacing.Axis.Z);


			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 12,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() + 0.5,
					randomTime, 0.2, 10, 20,
					r, g, b, a, EnumFacing.Axis.Y);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 12,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() + 0.5,
					randomTime * 2, 0.2, 10, 20,
					r, g, b, a, EnumFacing.Axis.Y);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 12,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() + 0.5,
					randomTime * 3, 0.2, 10, 20,
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