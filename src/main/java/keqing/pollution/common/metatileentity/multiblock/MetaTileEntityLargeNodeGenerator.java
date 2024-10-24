package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityBaseWithControl;
import keqing.pollution.Pollution;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POFusionReactor;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aura.AuraHelper;

import java.util.*;

import static java.lang.Math.max;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static net.minecraft.util.math.MathHelper.ceil;
import static net.minecraft.util.math.MathHelper.sqrt;

public class MetaTileEntityLargeNodeGenerator extends MetaTileEntityBaseWithControl {
	private static final int BASIC_CAPACITY = 8192;

	public MetaTileEntityLargeNodeGenerator(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
		return new MetaTileEntityLargeNodeGenerator(this.metaTileEntityId);
	}

	@Override
	public List<ITextComponent> getDataInfo() {
		return Collections.emptyList();
	}

	//随机数
	private final Random random = Pollution.RANDOM;
	//计数器0
	private int tickCount = 0;
	//线圈等级
	private int coilLevel;
	//发电方差（风）
	private float varience = 0.0f;
	//消耗要素速率乘数（水）
	private double essenceCostSpeedMultiplier = 1.0f;
	//总乘数
	private float overallCapacityMultiplier = 1.0f;
	//期望发电量
	private int expectedFinalCapacity = 0;
	//最终发电量
	private int finalCapacity = 0;
	//源质类型
	private final FluidStack INFUSED_ORDER = PollutionMaterials.infused_order.getFluid(1);
	private final FluidStack INFUSED_AURA = PollutionMaterials.infused_aura.getFluid(1);

	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		Object coilLevel = context.get("COILTieredStats");
		this.coilLevel = POUtils.getOrDefault(() -> coilLevel instanceof WrappedIntTired,
				() -> ((WrappedIntTired) coilLevel).getIntTier(),
				0);
		List<IEnergyContainer> energyContainer = new ArrayList(this.getAbilities(MultiblockAbility.OUTPUT_ENERGY));
		energyContainer.addAll(this.getAbilities(MultiblockAbility.OUTPUT_LASER));
		this.outEnergyContainer = new EnergyContainerList(energyContainer);
	}

	//计算总发电乘数
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
		nodeCapacityMultiplier *= amplifierType;
		//计算火、秩序、混沌
		//混沌大于20开始线性降低效率
		//火和秩序按照几何平均数计算倍率
		//TODO: NBT key validation, check whether the compound tag exists and whether it contains the key.
		nodeCapacityMultiplier *= max((1.2f - 0.005f * node.getTagCompound().getInteger("EssenceEntropy")), 0);
		nodeCapacityMultiplier *= (1 + 0.02f * sqrt(node.getTagCompound().getInteger("EssenceFire") * node.getTagCompound().getInteger("EssenceOrder")));
		//处理饕餮到64倍
		if (node.getTagCompound().getString("NodeType").equals("Voracious")
				&& INFUSED_ORDER.isFluidStackIdentical(this.inputFluidInventory.drain(INFUSED_ORDER, false))) {
			nodeCapacityMultiplier *= 4;
		}
		return nodeCapacityMultiplier;
	}

	//处理节点的各种奇怪特性
	private void doSpecialNodeBehaviors(ItemStack node, int slotNumber) {
		double basicRemovePossibility = 0.001;

		switch (Objects.requireNonNull(node.getTagCompound()).getString("NodeType")) {
			//凶险：缓慢提高区块污染，工作一段时间后不加源质有概率直接消失
			case "Ominous":
				AuraHelper.polluteAura(getWorld(), getPos(), 0.1f, true);
				if (random.nextDouble() <= basicRemovePossibility && !INFUSED_AURA.isFluidStackIdentical(this.inputFluidInventory.drain(INFUSED_AURA, false))) {
					this.inputInventory.extractItem(slotNumber, this.inputInventory.getStackInSlot(slotNumber).getCount(), false);
				}
				//纯净：缓慢降低区块污染
			case "Pure":
				AuraHelper.drainFlux(getWorld(), getPos(), 0.1f, true);
				//震荡：工作时大概率直接消失
			case "Concussive":
				if (random.nextDouble() <= basicRemovePossibility * 10) {
					this.inputInventory.extractItem(slotNumber, this.inputInventory.getStackInSlot(slotNumber).getCount(), false);
				}
				//饕餮：工作时不通源质有大概率直接消失，持续提供源质时发电提升至64x
			case "Voracious":
				if (random.nextDouble() <= basicRemovePossibility * 10 && !INFUSED_ORDER.isFluidStackIdentical(this.inputFluidInventory.drain(INFUSED_ORDER, false))) {
					this.inputInventory.extractItem(slotNumber, this.inputInventory.getStackInSlot(slotNumber).getCount(), false);
				}
				//64x在上面办好了
		}
	}

	protected void updateFormedValid(){
		if (!this.isActive()) {
			setActive(true);
		}
		expectedFinalCapacity = 0;
		//先检测每个输入总线内的节点
		for (var i = 0; i < this.getInputInventory().getSlots(); ++i) {
			if (!this.inputInventory.getStackInSlot(i).isEmpty()) {
				ItemStack stack = this.getInputInventory().getStackInSlot(i);
				if (stack.getItem() == PollutionMetaItems.PACKAGED_AURA_NODE.getMetaItem() && stack.getMetadata() == PollutionMetaItems.PACKAGED_AURA_NODE.metaValue) {
					//发电乘数加总
					overallCapacityMultiplier += getNodeCapacityMultiplier(stack) * coilLevel;
					//计算最终的发电量
					//基础发电量8192
					expectedFinalCapacity += (int) (BASIC_CAPACITY * overallCapacityMultiplier);
					//计算源质消耗
					if (stack.hasTagCompound()) {
						if (stack.getTagCompound().hasKey("EssenceWater")) {
							essenceCostSpeedMultiplier += max(0.15, 1 - (double) stack.getTagCompound().getInteger("EssenceWater") / 400);
						}
						if (stack.getTagCompound().hasKey("EssenceAir")) {
							//计算发电量方差，方差是所有风的方差值加起来除以6
							varience += (float) stack.getTagCompound().getInteger("EssenceAir") / 1000;
						}
					}
					overallCapacityMultiplier = 0.0F;
				}
			}
		}
		//处理消耗源质问题和节点特性问题，每秒一次
		tickCount++;
		if (tickCount % 20 == 0) {
			int essenceCost = ceil(200 * essenceCostSpeedMultiplier);
			if (INFUSED_AURA.isFluidStackIdentical(this.inputFluidInventory.drain(INFUSED_AURA, false))) {
				this.inputFluidInventory.drain(PollutionMaterials.infused_aura.getFluid(essenceCost), true);
			}
			if (INFUSED_ORDER.isFluidStackIdentical(this.inputFluidInventory.drain(INFUSED_ORDER, false))) {
				this.inputFluidInventory.drain(PollutionMaterials.infused_order.getFluid(essenceCost), true);
			}
			for (var i = 0; i < this.getInputInventory().getSlots(); ++i) {
				if (!this.inputInventory.getStackInSlot(i).isEmpty()
						&& this.inputInventory.getStackInSlot(i).getItem() != PollutionMetaItems.PACKAGED_AURA_NODE.getMetaItem()
						&& this.inputInventory.getStackInSlot(i).getMetadata() == PollutionMetaItems.PACKAGED_AURA_NODE.metaValue) {
					doSpecialNodeBehaviors(this.inputInventory.getStackInSlot(i), i);
				}
			}
			tickCount = 0;
		}
		//动力仓输出电，溢出式发电
		finalCapacity = (int) (expectedFinalCapacity * (1 + random.nextDouble() * varience / this.getInputInventory().getSlots()));
		if (this.isWorkingEnabled() && (this.outEnergyContainer.getEnergyCapacity() - this.outEnergyContainer.getEnergyStored()) >= finalCapacity) {
			//计算最终发电量并输出
			this.outEnergyContainer.addEnergy(finalCapacity);
		} else {
			//TODO: How to deal with energy when sufficient space in output hatch
		}
		essenceCostSpeedMultiplier = 0;
		varience = 0.0F;
	}

	public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		tooltip.add(I18n.format("pollution.machine.large_node_generator.tooltip.1"));
		tooltip.add(I18n.format("pollution.machine.large_node_generator.tooltip.2"));
		tooltip.add(I18n.format("pollution.machine.large_node_generator.tooltip.3"));
		tooltip.add(I18n.format("pollution.machine.large_node_generator.tooltip.4"));
		tooltip.add(I18n.format("pollution.machine.large_node_generator.tooltip.5"));
	}

	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		textList.add(new TextComponentTranslation("pollution.machine.large_node_generator_expectedfinalcapacity", this.expectedFinalCapacity).setStyle((new Style()).setColor(TextFormatting.RED)));
		textList.add((new TextComponentTranslation("pollution.machine.large_node_generator_finalcapacity", this.finalCapacity)).setStyle((new Style()).setColor(TextFormatting.RED)));
		//TODO: display text I18n.
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.UP, RelativeDirection.RIGHT)
				.aisle(" A ", " B ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ")
				.aisle("ABA", "BBB", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ")
				.aisle("ABA", "BBB", "HAC", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ")
				.aisle("ABA", "BBB", "HAC", "CAC", "   ", "   ", "DDD", "   ", "DDD", "   ", "   ")
				.aisle("ABA", "BBB", "HBC", "CBC", " B ", " B ", "DBD", " B ", "DBD", " E ", "   ")
				.aisle("ABA", "BBB", " A ", "C C", "C C", " A ", "DDD", "   ", "DDD", "   ", "   ")
				.aisle("ABA", "BBB", " A ", " A ", " A ", " A ", " A ", " A ", "   ", "   ", "   ")
				.aisle("ABA", "BBB", "   ", "   ", "   ", "   ", "BBB", "BBB", "BBB", "   ", "   ")
				.aisle(" B ", "   ", "   ", "   ", "   ", "   ", "   ", " F ", "   ", "   ", "   ")
				.aisle(" A ", " A ", " A ", " A ", " F ", "   ", " C ", " F ", " C ", "   ", " F ")
				.aisle(" B ", "   ", "   ", "   ", "   ", " F ", "   ", "   ", "   ", " F ", "   ")
				.aisle(" A ", " B ", "   ", "   ", " C ", "   ", "   ", " C ", "   ", "   ", " C ")
				.aisle(" A ", " G ", " B ", "   ", " C ", "   ", " C ", " S ", " C ", "   ", " C ")
				.aisle(" A ", " B ", "   ", "   ", " C ", "   ", "   ", " C ", "   ", "   ", " C ")
				.aisle(" B ", "   ", "   ", "   ", "   ", " F ", "   ", "   ", "   ", " F ", "   ")
				.aisle(" A ", " A ", " A ", " A ", " F ", "   ", " C ", " F ", " C ", "   ", " F ")
				.aisle(" B ", "   ", "   ", "   ", "   ", "   ", "   ", " F ", "   ", "   ", "   ")
				.aisle("ABA", "BBB", "   ", "   ", "   ", "   ", "BBB", "BBB", "BBB", "   ", "   ")
				.aisle("ABA", "BBB", " A ", " A ", " A ", " A ", " A ", " A ", "   ", "   ", "   ")
				.aisle("ABA", "BBB", " A ", "C C", "C C", " A ", "DDD", "   ", "DDD", "   ", "   ")
				.aisle("ABA", "BBB", "HBC", "CBC", " B ", " B ", "DBD", " B ", "DBD", " E ", "   ")
				.aisle("ABA", "BBB", "HAC", "CAC", "   ", "   ", "DDD", "   ", "DDD", "   ", "   ")
				.aisle("ABA", "BBB", "HAC", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ")
				.aisle("ABA", "BBB", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ")
				.aisle(" A ", " B ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ", "   ")
				.where('S', selfPredicate())
				.where('G', states(getCasingState2())
					.or(abilities(MultiblockAbility.OUTPUT_ENERGY).setMaxGlobalLimited(1).setPreviewCount(1))
					.or(abilities(MultiblockAbility.OUTPUT_LASER).setMaxGlobalLimited(1).setPreviewCount(0)))
				.where('A', states(getCasingState()))
				.where('B', states(getCasingState2()))
				.where('C', states(getCasingState3()).setMinGlobalLimited(25)
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setExactLimit(1).setPreviewCount(1)))
				.where('H', metaTileEntities(MetaTileEntities.ITEM_IMPORT_BUS[GTValues.ULV]))
				.where('E', states(getCasingState4()))
				.where('F', states(getCasingState5()))
				.where('D', CP_COIL_CASING.get())
				.where(' ', any())
				.build();
	}

	private static IBlockState getCasingState() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.mansussteel).getBlock(PollutionMaterials.mansussteel);
	}

	private static IBlockState getCasingState2() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.VOID_PRISM);
	}

	private static IBlockState getCasingState3() {
		return PollutionMetaBlocks.FUSION_REACTOR.getState(POFusionReactor.FusionBlockType.FRAME_II);
	}

	private static IBlockState getCasingState4() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}

	private static IBlockState getCasingState5() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.BAMINATED_GLASS);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.FRAME_I;
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}
