package keqing.pollution.common.metatileentity.multiblock;

import com.google.common.collect.Lists;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
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
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityBaseWithControl;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

import java.util.*;

import static gregtech.api.GTValues.VA;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static net.minecraft.util.math.MathHelper.ceil;

public class MetaTileEntityEssenceCollector extends MetaTileEntityBaseWithControl implements IBloomEffect, IFastRenderMetaTileEntity {
	//基础产速
	private float basicSpeedPerTick = 0.025f;
	//最终产速
	private int finalSpeedPerTick;
	//是否聚焦
	private boolean isFocused = false;
	//电压等级
	private int EUtTier;
	//线圈等级
	private int coilLevel;
    //最低输入功率，默认为30
    //输出单种流体的类型,只在聚焦时
	//当前区块灵气、污染
	float visThisChunk;
	float fluxThisChunk;

	private static Map<Item, Material> materialMap = new HashMap<>();

	static {
		materialMap.put(Item.getItemFromBlock(BlocksTC.crystalAir), infused_air);
		materialMap.put(Item.getItemFromBlock(BlocksTC.crystalFire), PollutionMaterials.infused_fire);
		materialMap.put(Item.getItemFromBlock(BlocksTC.crystalEarth), PollutionMaterials.infused_earth);
		materialMap.put(Item.getItemFromBlock(BlocksTC.crystalOrder), PollutionMaterials.infused_order);
		materialMap.put(Item.getItemFromBlock(BlocksTC.crystalEntropy), PollutionMaterials.infused_entropy);
		materialMap.put(Item.getItemFromBlock(BlocksTC.crystalWater), PollutionMaterials.infused_water);
	}

	public MetaTileEntityEssenceCollector(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
		return new MetaTileEntityEssenceCollector(this.metaTileEntityId);
	}

	@Override
	public List<ITextComponent> getDataInfo() {
		return Collections.emptyList();
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("      AAA      ", "      AAA      ", "      CCC      ", "               ", "               ", "               ", "               ", "               ", "      CCC      ", "      AAA      ", "      AAA      ")
				.aisle("   AAAABAAAA   ", "   ACE   ECA   ", "     A   A     ", "     A   A     ", "     A   A     ", "     A   A     ", "     A   A     ", "     A   A     ", "     A   A     ", "   ACE   ECA   ", "   AAAABAAAA   ")
				.aisle("  AAFFABAFFAA  ", "  A         A  ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "  A         A  ", "  AAFFABAFFAA  ")
				.aisle(" AAFFEABAEFFAA ", " C           C ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", " C           C ", " AAFFEABAEFFAA ")
				.aisle(" AFFEFABAFEFFA ", " E   I   K   E ", " A   I   K   A ", " A   D   D   A ", " A   E   E   A ", " A           A ", " A   E   E   A ", " A   A   A   A ", " A   I   K   A ", " E   I   K   E ", " AFFEFABAFEFFA ")
				.aisle("AAAAAAABAAAAAAA", "A             A", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "A             A", "AAAAAAABAAAAAAA")
				.aisle("ABBBFBBEBBFBBBA", "A   G     L   A", "    G     L    ", "    D     D    ", "    E     E    ", "               ", "    E     E    ", "    A     A    ", "    G     L    ", "A   G     L   A", "ABBBFBBEBBFBBBA")
				.aisle("AAAAAAABAAAAAAA", "A             A", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "A             A", "AAAAAAABAAAAAAA")
				.aisle(" AFFEFABAFEFFA ", " E   H   J   E ", " A   H   J   A ", " A   D   D   A ", " A   E   E   A ", " A           A ", " A   E   E   A ", " A   A   A   A ", " A   H   J   A ", " E   H   J   E ", " AFFEFABAFEFFA ")
				.aisle(" AAFFEABAEFFAA ", " C           C ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", " C           C ", " AAFFEABAEFFAA ")
				.aisle("  AAFFABAFFAA  ", "  A         A  ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "  A         A  ", "  AAFFABAFFAA  ")
				.aisle("   AAAABAAAA   ", "   ACE   ECA   ", "     A   A     ", "     A   A     ", "     A   A     ", "     A   A     ", "     A   A     ", "     A   A     ", "     A   A     ", "   ACE   ECA   ", "   AAAABAAAA   ")
				.aisle("      AOA      ", "      ASA      ", "      CCC      ", "               ", "               ", "               ", "               ", "               ", "      CCC      ", "      AAA      ", "      AAA      ")

				.where('S', selfPredicate())
				.where(' ', any())
				.where('A', states(getCasingState()).setMinGlobalLimited(200)
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1)))
				.where('B', CP_COIL_CASING.get())
				.where('C', states(getCasingState2()))
				.where('E', states(getCasingState3()))
				.where('F', states(getCasingState4()))
				.where('D', abilities(MultiblockAbility.EXPORT_FLUIDS).setExactLimit(6).setPreviewCount(6))
				.where('O', abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
				.where('G', states(getCasingState5()))
				.where('H', states(getCasingState6()))
				.where('I', states(getCasingState7()))
				.where('J', states(getCasingState8()))
				.where('K', states(getCasingState9()))
				.where('L', states(getCasingState10()))
				.build();
	}

	private static IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM);
	}

	private static IBlockState getCasingState2() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.LAMINATED_GLASS);
	}

	private static IBlockState getCasingState3() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}

	private static IBlockState getCasingState4() {
		return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.STAINLESS_STEEL_GEARBOX);
	}

	private static IBlockState getCasingState5() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID);
	}

	private static IBlockState getCasingState6() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_EARTH);
	}

	private static IBlockState getCasingState7() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER);
	}

	private static IBlockState getCasingState8() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT);
	}

	private static IBlockState getCasingState9() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_AIR);
	}

	private static IBlockState getCasingState10() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_ORDER);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.SPELL_PRISM;
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}

	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		Object coilLevel = context.get("COILTieredStats");
		this.coilLevel = POUtils.getOrDefault(() -> coilLevel instanceof WrappedIntTired,
				() -> ((WrappedIntTired) coilLevel).getIntTier(),
				0);
	}

	boolean backA;

	@Override
	public void update() {
		super.update();

		// 简化逻辑，减少嵌套
		if (!backA) {
			if (randomTime < 10) {
				++randomTime;
			}
		} else {
			if (randomTime > -10) {
				--randomTime;
			}
		}

		// 处理边界条件
		if (randomTime == 10) {
			backA = true;
		}
		if (randomTime == -10) {
			backA = false;
		}

		// 避免整数溢出
		int colorValue = 0xFF000000 | (randomTime * 1250 * 50);
		setFusionRingColor(colorValue);
	}
	/**
	 * 更新设备有效性
	 * 此方法主要用于根据设备的能量状态和环境灵气状态来调整设备的运行状态
	 * 它会根据输入电压、能量存储、灵气和污染程度来决定设备是否运行，以及运行的速度和模式
	 */
	@Override
	protected void updateFormedValid() {
		// 检查并设置设备的活动状态
		if (!this.isActive()) {
			setActive(true);
		}

		// 获取设备的位置坐标
		int aX = this.getPos().getX();
		int aY = this.getPos().getY();
		int aZ = this.getPos().getZ();

		// 计算灵气和污染
		visThisChunk = AuraHelper.getVis(this.getWorld(), new BlockPos(aX, aY, aZ));
		fluxThisChunk = AuraHelper.getFlux(this.getWorld(), new BlockPos(aX, aY, aZ));

		// 处理输入电压为 0 的情况
		if (this.energyContainer.getInputVoltage() == 0) {
			// 根据缓存的能量估算能够工作的电压
			int estimatedVoltage = (int) (this.energyContainer.getEnergyStored() / 20); // 估算能够工作的电压，使能量至少能够工作 20 tick
			EUtTier = GTUtility.getTierByVoltage(estimatedVoltage);
			if (this.energyContainer.getEnergyStored() < VA[EUtTier] * 20L) {
				return; // 如果能量不足以工作 20 tick，则直接返回
			}
		} else {
			// 将能源仓的输入电压转化为 GT 的电压等级
			EUtTier = GTUtility.getTierByVoltage(this.energyContainer.getInputVoltage());
			// 检查能量是否足够
			if (this.energyContainer.getEnergyStored() < VA[EUtTier] && this.energyContainer.getInputVoltage() < VA[EUtTier]) {
				return;
			}
		}

		// 计算最终每tick处理速度
		finalSpeedPerTick = (int)calculateFinalSpeedPerTick(visThisChunk, fluxThisChunk, EUtTier);

		// 从能源仓中移除能量
		this.energyContainer.removeEnergy(VA[EUtTier]);

		// 判断输入总线是否为空
		if (!this.inputInventory.getStackInSlot(0).isEmpty()) {
			// 处理聚焦模式
			isFocused = true;
			handleFocusedMode(finalSpeedPerTick);
		} else {
			// 处理正常模式
			isFocused = false;
			handleNormalMode(finalSpeedPerTick);
		}
	}

	/**
	 * 计算最终每tick处理速度
	 * 根据当前区块的灵气和污染程度，以及设备的电压等级，计算出设备的最终处理速度
	 *
	 * @param visThisChunk 当前区块的灵气值
	 * @param fluxThisChunk 当前区块的污染值
	 * @param EUtTier 设备的电压等级
	 * @return 最终每tick处理速度
	 */
	private double calculateFinalSpeedPerTick(float visThisChunk, float fluxThisChunk, int EUtTier) {
		if (fluxThisChunk >= visThisChunk) {
			return 0;
		}
		double ratio = (double) fluxThisChunk / visThisChunk;
		double speedFactor = Math.log(visThisChunk) - Math.log(1 + ratio);
		return Math.ceil(basicSpeedPerTick * (1 + coilLevel) * Math.pow(2, EUtTier) * speedFactor);
	}

	/**
	 * 处理聚焦模式
	 * 根据输入总线中的物品类型，处理对应的污染材料
	 *
	 * @param finalSpeedPerTick 最终每tick处理速度
	 */
	private void handleFocusedMode(double finalSpeedPerTick) {
		if (this.outputFluidInventory == null || this.outputFluidInventory.getTanks() < finalSpeedPerTick) {
			return;
		}

		String translationKey = Objects.requireNonNull(this.inputInventory.getStackInSlot(0).getTranslationKey());
		switch (translationKey) {
			case "tile.crystal_aer":
				addPollutionMaterial(infused_air, finalSpeedPerTick);
				break;
			case "tile.crystal_ignis":
				addPollutionMaterial(infused_fire, finalSpeedPerTick);
				break;
			case "tile.crystal_terra":
				addPollutionMaterial(infused_earth, finalSpeedPerTick);
				break;
			case "tile.crystal_aqua":
				addPollutionMaterial(infused_water, finalSpeedPerTick);
				break;
			case "tile.crystal_ordo":
				addPollutionMaterial(infused_order, finalSpeedPerTick);
				break;
			case "tile.crystal_perditio":
				addPollutionMaterial(infused_entropy, finalSpeedPerTick);
				break;
		}
	}

	/**
	 * 处理正常模式
	 * 在没有聚焦物品的情况下，均匀处理所有类型的污染材料
	 *
	 * @param finalSpeedPerTick 最终每tick处理速度
	 */
	private void handleNormalMode(int finalSpeedPerTick) {
		if (this.outputFluidInventory == null || this.outputFluidInventory.getTanks() < finalSpeedPerTick) {
			return;
		}
		List<FluidStack> fluids = Arrays.asList(
				infused_air.getFluid(finalSpeedPerTick),
				PollutionMaterials.infused_fire.getFluid(finalSpeedPerTick),
				PollutionMaterials.infused_earth.getFluid(finalSpeedPerTick),
				PollutionMaterials.infused_water.getFluid(finalSpeedPerTick),
				PollutionMaterials.infused_order.getFluid(finalSpeedPerTick),
				PollutionMaterials.infused_entropy.getFluid(finalSpeedPerTick)
		);

		GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, fluids);
	}

	/**
	 * 添加污染材料
	 * 将指定的污染材料以流的形式添加到输出流库存中
	 *
	 * @param Material 污染材料
	 * @param amount 添加量
	 */
	private void addPollutionMaterial(Material Material, double amount) {
		FluidStack fluid = new FluidStack(Material.getFluid(), (int) (amount * 3));
		GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(fluid));
	}

	public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		tooltip.add(I18n.format("pollution.machine.essence_collector.tooltip.1"));
		tooltip.add(I18n.format("pollution.machine.essence_collector.tooltip.2"));
		tooltip.add(I18n.format("pollution.machine.essence_collector.tooltip.3"));
		tooltip.add(I18n.format("pollution.machine.essence_collector.tooltip.4"));
		tooltip.add(I18n.format("pollution.machine.essence_collector.tooltip.5"));
		tooltip.add(I18n.format("pollution.machine.essence_collector.tooltip.6"));
	}


	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		textList.add(new TextComponentTranslation("pollution.machine.essence_collector_visthischunk", this.visThisChunk).setStyle((new Style()).setColor(TextFormatting.RED)));
		textList.add((new TextComponentTranslation("pollution.machine.essence_collector_fluxthischunk", this.fluxThisChunk)).setStyle((new Style()).setColor(TextFormatting.RED)));
		textList.add((new TextComponentTranslation("pollution.machine.essence_collector_coillevel", this.coilLevel)).setStyle((new Style()).setColor(TextFormatting.RED)));
		textList.add((new TextComponentTranslation("pollution.machine.essence_collector_finalspeedpertick", this.finalSpeedPerTick)).setStyle((new Style()).setColor(TextFormatting.RED)));
		textList.add((new TextComponentTranslation("pollution.machine.essence_collector_euttier", this.EUtTier)).setStyle((new Style()).setColor(TextFormatting.RED)));
		if (isFocused) {
			textList.add((new TextComponentTranslation("pollution.machine.essence_collector_iffocused", "开启")).setStyle((new Style()).setColor(TextFormatting.RED)));
		} else {
			textList.add((new TextComponentTranslation("pollution.machine.essence_collector_iffocused", "关闭")).setStyle((new Style()).setColor(TextFormatting.RED)));
		}
	}

	int randomTime = 0;

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
				Math.abs((Math.abs(getOffsetTimer() % 50) + context.partialTicks()) - 25) / 25));


		float a = (float) (color >> 24 & 255) / 255.0F;
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		EnumFacing relativeBack = RelativeDirection.BACK.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
				isFlipped());
		EnumFacing.Axis axis = RelativeDirection.UP.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped())
				.getAxis();

		if (this.isWorkingEnabled() && this.isActive()) {
			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 6 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 4.5,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 6 + 0.5,
					3, 0.2, 10, 20,
					r, g, b, a, EnumFacing.Axis.Y);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 6 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 4.5,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 6 + 0.5,
					1, 1, 10, 20,
					r, g, b, a, axis);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 6 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 8.5,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 6 + 0.5,
					1, 0.1, 10, 20,
					r, g, b, a, axis);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 6 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 7.5,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 6 + 0.5,
					1, 0.1, 10, 20,
					r, g, b, a, axis);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 6 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 1.5,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 6 + 0.5,
					1, 0.1, 10, 20,
					r, g, b, a, axis);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 6 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 0.5,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 6 + 0.5,
					1, 0.1, 10, 20,
					r, g, b, a, axis);


			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 6 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 4.5,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 6 + 0.5,
					1, 1, 10, 20,
					r, g, b, a, EnumFacing.Axis.X);

			RenderBufferHelper.renderRing(buffer,
					getPos().getX() - context.cameraX() + relativeBack.getXOffset() * 6 + 0.5,
					getPos().getY() - context.cameraY() + relativeBack.getYOffset() + 4.5,
					getPos().getZ() - context.cameraZ() + relativeBack.getZOffset() * 6 + 0.5,
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
