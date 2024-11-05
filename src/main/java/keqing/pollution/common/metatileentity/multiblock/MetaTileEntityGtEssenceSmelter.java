package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityBaseWithControl;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POAspectToGtFluidList;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.log;
import static net.minecraft.util.math.MathHelper.ceil;

public class MetaTileEntityGtEssenceSmelter extends MetaTileEntityBaseWithControl {
	public MetaTileEntityGtEssenceSmelter(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
		return new MetaTileEntityGtEssenceSmelter(this.metaTileEntityId);
	}

	//变量
	//计时器
	int timer = 0;
	//单次熔炼时长
	int smeltingDuration;
	//电压等级
	int EUtTier;
	//源质消耗
	int infusedCost;
	//工作状态
	boolean isWorking = false;
	//机器缓存源质列表
	AspectList tempAspectList = new AspectList();

	@Override
	public List<ITextComponent> getDataInfo() {
		return Collections.emptyList();
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.UP, RelativeDirection.RIGHT)
				.aisle(" ABBBBBA", " AACCCAA", " A AAA A", " D     D", "        ", "        ")
				.aisle("ABEEEEEB", "ABCCCCCA", " BBBBBB ", "  F   F ", "  FFFFF ", "  G   G ")
				.aisle("BBEEEEEB", "CCCHHHCC", " CBGGGBA", "   CCC  ", "  F   F ", "        ")
				.aisle("SBEEEEEB", "CCCHHHCC", " CBGGGBA", "   CCC  ", "  F   F ", "        ")
				.aisle("BBEEEEEB", "CCCHHHCC", " CBGGGBA", "   CCC  ", "  F   F ", "        ")
				.aisle("ABEEEEEB", "ABCCCCCA", " BBBBBB ", "  F   F ", "  FFFFF ", "  G   G ")
				.aisle(" ABBBBBA", " AACCCAA", " A AAA A", " D     D", "        ", "        ")
				.where('S', selfPredicate())
				.where('B', states(getCasingState()).setMinGlobalLimited(15)
						.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setExactLimit(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(6).setPreviewCount(6))
						.or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1))
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
				)
				.where('E', states(getCasingState2()))
				.where('A', states(getCasingState3()))
				.where('F', states(getCasingState4()))
				.where('D', states(getCasingState5()))
				.where('G', states(getCasingState6()))
				.where('C', states(getCasingState7()))
				.where('H', states(getCasingState8()))
				.where(' ', any())
				.build();
	}

	protected IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID);
	}

	protected IBlockState getCasingState2() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.MAGIC_BATTERY);
	}

	protected IBlockState getCasingState3() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.thaumium).getBlock(PollutionMaterials.thaumium);
	}

	protected IBlockState getCasingState4() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.mansussteel).getBlock(PollutionMaterials.mansussteel);
	}

	protected IBlockState getCasingState5() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
	}

	protected IBlockState getCasingState6() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}

	protected IBlockState getCasingState7() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
	}

	protected IBlockState getCasingState8() {
		return BlocksTC.metalAlchemical.getDefaultState();
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.SPELL_PRISM_VOID;
	}
	@Override
	protected void updateFormedValid() {
		if (!this.isActive()) {
			setActive(true);
		}
		EUtTier = ceil(log((double) this.energyContainer.getInputVoltage() / 32) / log(4) + 1);
		FluidStack INFUSED_FIRE = PollutionMaterials.infused_fire.getFluid(infusedCost);
		List<IFluidTank> fluidInputInventory = getAbilities(MultiblockAbility.IMPORT_FLUIDS);
		infusedCost = (int) (log(1024) / log(EUtTier));
		//每tick检测是否有物品进入输入总线
		//如果有，根据电压决定冶炼时间
		//消耗物品并调整工作状态到可以工作
		for (int i = 0; i < this.inputInventory.getSlots(); i++) {
			ItemStack smeltedItem = this.inputInventory.getStackInSlot(i);
			if (!smeltedItem.isEmpty() && !isWorking) {
				for (int j = 0; j < smeltedItem.getCount(); j++) {
					tempAspectList.add(AspectHelper.getObjectAspects(smeltedItem));
				}
				for (Integer aspectAmount : tempAspectList.aspects.values()) {
					smeltingDuration += 10 * aspectAmount;
				}
				smeltingDuration /= (int) Math.pow(4, EUtTier - 1);
				//处理物品没有要素的情况，如果没有要素duration就是0了，应该什么都不做
				if (!canSmelt(smeltedItem)) {
					smeltingDuration = 20;
				} else {
					this.inputInventory.extractItem(i, smeltedItem.getCount(), false);
					isWorking = true;
				}
			}
		}
		//消耗流体
		if (isWorking && fluidInputInventory.get(0) != null && INFUSED_FIRE.isFluidStackIdentical(this.inputFluidInventory.drain(INFUSED_FIRE, false))) {
			if (fluidInputInventory.get(0).getFluidAmount() >= infusedCost) {
				this.inputFluidInventory.drain(INFUSED_FIRE, true);
			}
		}
		//如果在工作，不断消耗能量，让计时器在能量不足的时候跳回1
		if (isWorking) {
			if (this.energyContainer.getEnergyStored() >= this.energyContainer.getInputVoltage()) {
				this.energyContainer.removeEnergy(this.energyContainer.getInputVoltage());
				timer++;
			}
		} else {timer = 1;}
		//冶炼时间完毕：输出源质
		//把源质传出到输出仓
		if (timer == smeltingDuration) {
			transportGtEssenceToInventory(tempAspectList);
			isWorking = false;
			timer = 0;
			smeltingDuration = 0;
		}
	}

	private void transportGtEssenceToInventory(AspectList aspectList){
		POAspectToGtFluidList temp = new POAspectToGtFluidList();
		for (Aspect aspect: aspectList.getAspects()){
			if (temp.aspectToGtFluidList.get(aspect) != null){
				FluidStack fluid = new FluidStack(temp.aspectToGtFluidList.get(aspect).getFluid(), 144 * aspectList.getAmount(aspect));
				GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(fluid));
			}
		}
	}

	//判断物品能不能融掉
	private boolean canSmelt(ItemStack Item) {
		AspectList al = AspectHelper.getObjectAspects(Item);
		return al != null && al.size() != 0;
	}

	//tooltip
	public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		tooltip.add(I18n.format("pollution.machine.gt_essence_smelter.tooltip.1"));
		tooltip.add(I18n.format("pollution.machine.gt_essence_smelter.tooltip.2"));
		tooltip.add(I18n.format("pollution.machine.gt_essence_smelter.tooltip.3"));
		tooltip.add(I18n.format("pollution.machine.gt_essence_smelter.tooltip.4"));
	}

	@Override
	protected void addDisplayText (List < ITextComponent > textList) {
		super.addDisplayText(textList);
		textList.add(new TextComponentTranslation("pollution.machine.gt_essence_smelter_infusedcost", this.infusedCost).setStyle((new Style()).setColor(TextFormatting.RED)));
		textList.add((new TextComponentTranslation("pollution.machine.gt_essence_smelter_smeltingduration", this.smeltingDuration)).setStyle((new Style()).setColor(TextFormatting.RED)));
		textList.add((new TextComponentTranslation("pollution.machine.gt_essence_smelter_timer", this.timer)).setStyle((new Style()).setColor(TextFormatting.RED)));
		textList.add((new TextComponentTranslation("pollution.machine.gt_essence_smelter_euttier", this.EUtTier)).setStyle((new Style()).setColor(TextFormatting.RED)));
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}
