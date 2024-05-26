package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipeproperties.TemperatureProperty;
import gregtech.api.unification.material.Material;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.core.sound.GTSoundEvents;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.metatileentity.PORecipeMapMultiblockController;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static keqing.pollution.api.unification.PollutionMaterials.infused_fire;

public class MetaTileEntityMagicElectricBlastFurnace extends PORecipeMapMultiblockController {


	//魔法电力高炉


	//定义两个需要用到的变量
	int CoilLevel;
	int Temp;

	public MetaTileEntityMagicElectricBlastFurnace(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, new RecipeMap[]{RecipeMaps.BLAST_RECIPES, RecipeMaps.ALLOY_SMELTER_RECIPES});
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityMagicElectricBlastFurnace(this.metaTileEntityId);
	}


	//在成型时读取机器的线圈级别 并且给炉温赋值
	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		Object CoilLevel = context.get("COILTiredStats");
		this.CoilLevel = POUtils.getOrDefault(() -> CoilLevel instanceof WrappedIntTired,
				() -> ((WrappedIntTired) CoilLevel).getIntTier(),
				0);
		Temp = 1000 + 900 * this.CoilLevel;
	}


	//集成父类的UI信息 添加自己的炉温信息
	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		textList.add(new TextComponentTranslation("Temperature: %s", Temp));
	}

	//工具提示
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
	                           boolean advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.1"));
		tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.2"));
		tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.3"));
	}

	@Override
	public SoundEvent getBreakdownSound() {
		return GTSoundEvents.BREAKDOWN_ELECTRICAL;
	}

	//配方检测 用炉温匹配高炉配方的炉温
	@Override
	public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {
		return this.Temp >= recipe.getProperty(TemperatureProperty.getInstance(), 0);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("XXX", "CCC", "CCC", "XXX")
				.aisle("XXX", "C#C", "C#C", "XMX")
				.aisle("FSX", "CCC", "CCC", "XXX")
				.where('S', selfPredicate())
				.where('X', states(getCasingState()).setMinGlobalLimited(9)
						.or(autoAbilities(true, true, true, true, true, true, false)))
				.where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
				.where('F', abilities(POMultiblockAbility.VIS_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
				.where('C', CP_COIL_CASING)
				.where('#', air())
				.build();
	}

	//这里是多方块工作需要消耗的 元素
	@Override
	public Material getMaterial() {
		return infused_fire;
	}


	//下边都是设置多方块外形材质的喵
	private static IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT);
	}

	//覆盖层材质 就是给IO渲染的材质
	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.SPELL_PRISM_HOT;
	}


	//控制器的图形 比如传统的外观 或者聚变电脑的外观等等
	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}

	@Override
	public boolean canBeDistinct() {
		return true;
	}
}