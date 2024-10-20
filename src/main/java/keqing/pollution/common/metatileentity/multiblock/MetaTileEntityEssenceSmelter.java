package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityBaseWithControl;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.log;
import static net.minecraft.util.math.MathHelper.ceil;

public class MetaTileEntityEssenceSmelter extends MetaTileEntityBaseWithControl{
	public MetaTileEntityEssenceSmelter(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
		return new MetaTileEntityEssenceSmelter(this.metaTileEntityId);
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
	boolean isWorking = false;
	AspectList tempAspectList = new AspectList();

	@Override
	public List<ITextComponent> getDataInfo() {
		return Collections.emptyList();
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("XXX", "XXX", "XXX")
				.aisle("XXX", "X#X", "XXX")
				.aisle("XXX", "XSX", "XXX")
				.where('S', selfPredicate())
				.where('X', states(getCasingState())
						.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1))
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
				)
				.where('#', air())
				.build();
	}

	protected IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.MAGIC_BATTERY);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.MAGIC_BATTERY;
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

		for (int i = 0; i < this.inputInventory.getSlots(); i++) {
			if (!this.inputInventory.getStackInSlot(i).isEmpty() && !isWorking) {
				for (int j = 0; j < this.inputInventory.getStackInSlot(i).getCount(); j++) {
					tempAspectList.add(AspectHelper.getObjectAspects(this.inputInventory.getStackInSlot(i)));
				}
				for (Integer aspectAmount : tempAspectList.aspects.values()) {
					smeltingDuration += 20 * 2 * aspectAmount;
				}
				smeltingDuration /= (int) Math.pow(2, EUtTier - 1);
				this.inputInventory.extractItem(i, this.inputInventory.getStackInSlot(i).getCount(), false);
				isWorking = true;
			}
		}
		if (isWorking && fluidInputInventory.get(0).getFluidAmount() >= infusedCost && INFUSED_FIRE.isFluidStackIdentical(this.inputFluidInventory.drain(INFUSED_FIRE, false))) {
			this.inputFluidInventory.drain(INFUSED_FIRE, true);
		}

		if (isWorking) {
			if (this.energyContainer.getEnergyStored() >= this.energyContainer.getInputVoltage()) {
				this.energyContainer.removeEnergy(this.energyContainer.getInputVoltage());
				timer++;
			}
		} else {timer = 1;}
		if (timer == smeltingDuration) {
			transportEssenceToContainers(tempAspectList);
			isWorking = false;
			timer = 0;
			smeltingDuration = 0;
			tempAspectList = new AspectList();
		}
			//每tick检测是否有物品进入输入总线
			//如果有，检测能源仓等级，以决定流体消耗，并且判断流体消耗是否支持一次冶炼
			//消耗能源仓能源，流体，检测熔炼掉的物品，并且为主方块的对应源质的一个列表加上若干项
		/*
		for (int i = 0; i < this.inputInventory.getSlots(); i++) {
			if (!this.inputInventory.getStackInSlot(i).isEmpty() && INFUSED_FIRE.isFluidStackIdentical(this.fluidInventory.drain(INFUSED_FIRE, false))) {
				//计算源质消耗
				infusedCost = (int) (log(1000) / log(EUtTier));
				//读取槽位物品的aspectlist
				//如果有，要素列表暂存，然后烧掉物品，并且开始消耗流体
				AspectList tempAspectList = new AspectList(this.inputInventory.getStackInSlot(i));
				boolean isWorking = false;
				if (timer == 0) {
					this.inputInventory.extractItem(i, this.inputInventory.getStackInSlot(i).getCount(), false);
					//消耗流体
					if (fluidInputInventory.get(0).getFluidAmount() >= infusedCost) {
						this.fluidInventory.drain(INFUSED_FIRE, true);
						//判定进入工作状态，可以计时
						isWorking = true;
					}
				}

				if (this.energyContainer.getEnergyStored() >= this.energyContainer.getInputVoltage()) {
					this.energyContainer.removeEnergy(this.energyContainer.getInputVoltage());
				} else {
					timer = 1;
				} //让计时器在能量不足的时候跳回1
				//启动计时器
				if (isWorking) {
					timer++;
				}
				//计算时间消耗
				//基本冶炼时间：每1点源质2s，加总后除以2^（电压等级-1）
				if (!tempAspectList.aspects.isEmpty()) {
					for (Integer aspectAmount : tempAspectList.aspects.values()) {
						smeltingDuration += 20 * 2 * aspectAmount;
					}
					smeltingDuration /= (int) Math.pow(2, EUtTier - 1);
				}
				//冶炼时间完毕：输出源质
				//把源质自动传出到附近的罐子里面，尽可能传出
				if (timer == smeltingDuration) {
					transportEssenceToContainers(tempAspectList);
				}
				//传出以后，整理这个列表
				for (int j = 0; j < tempAspectList.size(); j++) {
					if (tempAspectList.getAmount(tempAspectList.getAspects()[j]) == 0) {
						tempAspectList.remove(tempAspectList.getAspects()[j]);
					}
				}
			}
		}
		*/
		}

		private void transportEssenceToContainers (AspectList list){
			//获取设备的位置坐标
			//在指定的罐子位找罐子
			BlockPos centerPos = this.getPos();
			int radius = 3;
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						BlockPos currentPos = centerPos.add(x, y, z);
						TileEntity te = this.getWorld().getTileEntity(currentPos);
						if (te instanceof IAspectSource) {
							tryAddAspect(te, list);
						}
					}
				}
			}
		}

		//向罐子加入要素
		//每次输入成功，都修改一下自己当前的列表
		private void tryAddAspect (TileEntity te, AspectList list){
			for (int i = 0; i < list.size(); i++) {
				if (te instanceof IAspectSource sourceTe) {
					Aspect as = list.getAspects()[i];
					sourceTe.addToContainer(as, list.getAmount(as));
					list.remove(as);
				}
			}
		}

		@Override
		protected void addDisplayText (List < ITextComponent > textList) {
			super.addDisplayText(textList);
			textList.add(new TextComponentTranslation("pollution.machine.essence_smelter_infusedcost", this.infusedCost).setStyle((new Style()).setColor(TextFormatting.RED)));
			textList.add((new TextComponentTranslation("pollution.machine.essence_smelter_smeltingduration", this.smeltingDuration)).setStyle((new Style()).setColor(TextFormatting.RED)));
			textList.add((new TextComponentTranslation("pollution.machine.essence_smelter_timer", this.timer)).setStyle((new Style()).setColor(TextFormatting.RED)));
			textList.add((new TextComponentTranslation("pollution.machine.essence_smelter_euttier", this.EUtTier)).setStyle((new Style()).setColor(TextFormatting.RED)));
			textList.add((new TextComponentTranslation("pollution.machine.essence_smelter_aspectslist", this.tempAspectList)).setStyle((new Style()).setColor(TextFormatting.RED)));
	}

	}
