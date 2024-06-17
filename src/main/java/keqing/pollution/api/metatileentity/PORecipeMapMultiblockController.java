package keqing.pollution.api.metatileentity;

import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.unification.material.Material;
import keqing.pollution.api.capability.IVisMultiblock;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aura.AuraHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static keqing.pollution.api.unification.PollutionMaterials.*;

public abstract class
PORecipeMapMultiblockController extends MultiMapMultiblockController implements IVisMultiblock {

	int visStorage;
	int tier;
	int visStorageMax;
	int time;
	boolean checkVis = false;
	Material material;
	//这里提供需要要素的接口

	//只需要将我覆写即可，其他的不要动喵
	public Material getMaterial() {
		return null;
	}
	//我是多方块是否需要要素的开关，默认开喵

	//下边就不要动了喵
	//定义储罐

	private static Map<Material, FluidStack> STACK_MAP = new HashMap<>();


	//在此处添加你需要的要素 格式FluidStack xxx_STACK = infused_axx.getFluid(1);
	static final FluidStack AIR_STACK = infused_air.getFluid(1);
	static final FluidStack FIRE_STACK = infused_fire.getFluid(1);
	static final FluidStack WATER_STACK = infused_water.getFluid(1);
	static final FluidStack EARTH_STACK = infused_earth.getFluid(1);
	static final FluidStack ORDER_STACK = infused_order.getFluid(1);
	static final FluidStack ENTROPY_STACK = infused_entropy.getFluid(1);
	//复合的
	static final FluidStack CRYSTAL_STACK = infused_crystal.getFluid(1);
	static final FluidStack WEAPON_STACK = infused_weapon.getFluid(1);
	static final FluidStack INSTRUMENT_STACK = infused_instrument.getFluid(1);
	static final FluidStack EXCHANGE_STACK = infused_exchange.getFluid(1);
	static final FluidStack METAL_STACK = infused_metal.getFluid(1);
	static final FluidStack ALCHEMY_STACK = infused_alchemy.getFluid(1);
	static final FluidStack LIFE_STACK = infused_life.getFluid(1);
	static final FluidStack DEATH_STACK = infused_death.getFluid(1);
	static final FluidStack SOUL_STACK = infused_soul.getFluid(1);
	static final FluidStack ENERGY_STACK = infused_energy.getFluid(1);
	static final FluidStack MAGIC_STACK = infused_magic.getFluid(1);

	static {
		STACK_MAP.put(infused_air, AIR_STACK);
		STACK_MAP.put(infused_fire, FIRE_STACK);
		STACK_MAP.put(infused_water, WATER_STACK);
		STACK_MAP.put(infused_earth, EARTH_STACK);
		STACK_MAP.put(infused_order, ORDER_STACK);
		STACK_MAP.put(infused_entropy, ENTROPY_STACK);
		STACK_MAP.put(infused_crystal, CRYSTAL_STACK);
		STACK_MAP.put(infused_weapon, WEAPON_STACK);
		STACK_MAP.put(infused_instrument, INSTRUMENT_STACK);
		STACK_MAP.put(infused_exchange, EXCHANGE_STACK);
		STACK_MAP.put(infused_life, LIFE_STACK);
		STACK_MAP.put(infused_death, DEATH_STACK);
		STACK_MAP.put(infused_soul, SOUL_STACK);
		STACK_MAP.put(infused_energy, ENERGY_STACK);
		STACK_MAP.put(infused_magic, MAGIC_STACK);
		STACK_MAP.put(infused_alchemy, ALCHEMY_STACK);
		STACK_MAP.put(infused_metal, METAL_STACK);
	}

	//检查有没有+消耗  由consume控制

	//if (material == infused_xxx){
	//            if (xxx_STACK.isFluidStackIdentical(inputTank.drain(xxx_STACK, consume))) {
	//                return true;
	//            }}
	public boolean isCheckVis(Material material, Boolean consume) {
		IMultipleTankHandler inputTank = getInputFluidInventory();

		if (material == null) return true;

		FluidStack stack = STACK_MAP.get(material);
		if (stack.isFluidStackIdentical(inputTank.drain(stack, consume))) {
			return true;
		}

		return false;
	}


	public PORecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
		this(metaTileEntityId, new RecipeMap<?>[]{recipeMap});
	}

	public PORecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
		super(metaTileEntityId, recipeMaps);
		this.recipeMapWorkable = new POMultiblockRecipeLogic(this);
	}

	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if (isVis())
			tooltip.add(I18n.format("需要使用§c灵气仓§r获取当前区块灵气"));
	}

	int aX = 0;
	int aY = 0;
	int aZ = 0;

	@Override
	public void update() {
		super.update();
		time++;
		if(time==20) if (AuraHelper.drainVis(getWorld(), getPos(), (float) (tier * tier), true) > 0) {
			time=0;
			if (visStorage < visStorageMax) {
				AuraHelper.drainVis(getWorld(), new BlockPos(aX, aY, aZ), (float) (tier * tier * 0.01), false);
				visStorage += tier * tier;
			}
		}
		if (isActive()) if (visStorage > 10) visStorage -= tier;
	}

	@Override
	public boolean isActive() {
		return enough() && this.isStructureFormed() && this.recipeMapWorkable.isActive() && this.recipeMapWorkable.isWorkingEnabled();
	}

	@Override
	protected void addErrorText(List<ITextComponent> textList) {
		super.addErrorText(textList);
		if (!isCheckVis(material, false)) {
			textList.add(new TextComponentTranslation("缺少源质输入!!!"));
		}
	}

	public boolean enough() {
		return visStorage > 5;
	}

	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		material = getMaterial();
		aX = this.getPos().getX();
		aY = this.getPos().getY();
		aZ = this.getPos().getZ();
		tier = this.getAbilities(POMultiblockAbility.VIS_HATCH).get(0).getTier();
		visStorageMax = 1000 * tier;
	}

	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		if (isStructureFormed())
			textList.add(new TextComponentTranslation("区块灵气: %s | %s 灵气等级: %s", visStorage, visStorageMax, tier));
		if (material != null)
			textList.add(new TextComponentTranslation("要素需求 : %s | 工作状态: %s", material.getLocalizedName(), isCheckVis(material, false)));
	}

	@Override
	public boolean isVis() {
		return true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data.setInteger("visStorage", this.visStorage);
		return super.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.visStorage = data.getInteger("visStorage");
	}

	@Override
	public void writeInitialSyncData(PacketBuffer buf) {
		super.writeInitialSyncData(buf);
		buf.writeInt(this.visStorage);
	}

	@Override
	public void receiveInitialSyncData(PacketBuffer buf) {
		super.receiveInitialSyncData(buf);
		this.visStorage = buf.readInt();
	}

	public class POMultiblockRecipeLogic extends MultiblockRecipeLogic {
		public int getn() {
			if (visStorage <= 100) return 1;
			return visStorage / 100;
		}

		@Override
		protected void modifyOverclockPost(int[] resultOverclock, IRecipePropertyStorage storage) {
			super.modifyOverclockPost(resultOverclock, storage);

			resultOverclock[0] *= 1.0f - visStorage*0.00005; // each coil above cupronickel (coilTier = 0) uses 10% less
			// energy
			resultOverclock[0] = Math.max(1, resultOverclock[0]);
		}

		@Override
		public int getParallelLimit() {
			return getn();
		}

		public POMultiblockRecipeLogic(RecipeMapMultiblockController tileEntity) {
			super(tileEntity);
		}

		protected void updateRecipeProgress() {
			if (this.canRecipeProgress && this.drawEnergy(this.recipeEUt, true)) {
				this.drawEnergy(this.recipeEUt, false);
				if (!enough()) this.maxProgressTime++;

				if (enough() && isCheckVis(material, true)) {
					if (++this.progressTime > this.maxProgressTime) {
						this.completeRecipe();
					}
				}
				if (this.hasNotEnoughEnergy && this.getEnergyInputPerSecond() > 19L * (long) this.recipeEUt) {
					this.hasNotEnoughEnergy = false;
				}
			} else if (this.recipeEUt > 0) {
				this.hasNotEnoughEnergy = true;
				this.decreaseProgress();
			}

		}
	}
}