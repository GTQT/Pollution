package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipeproperties.TemperatureProperty;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static org.spongepowered.asm.util.perf.Profiler.setActive;

public class MetaTileEntityNodeBlastFurnace extends MultiMapMultiblockController {

	int CoilLevel;
	int Temp;

	public  MetaTileEntityNodeBlastFurnace(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, new RecipeMap[]{RecipeMaps.BLAST_RECIPES, PORecipeMaps.FORGE_ALCHEMY_RECIPES});
		this.recipeMapWorkable = new MetaTileEntityNodeBlastFurnace.NodeBlastFurnaceRecipeLogic(this);
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityNodeBlastFurnace (this.metaTileEntityId);
	}

	//在成型时读取机器的线圈级别 并且给炉温赋值 	//还有激光仓支持
	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		Object CoilLevel = context.get("COILTieredStats");
		this.CoilLevel = POUtils.getOrDefault(() -> CoilLevel instanceof WrappedIntTired,
				() -> ((WrappedIntTired) CoilLevel).getIntTier(),
				0);
		Temp = 1000 + 900 * this.CoilLevel;

		List<IEnergyContainer> energyContainer = new ArrayList(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
		energyContainer.addAll(this.getAbilities(MultiblockAbility.INPUT_LASER));
		this.energyContainer=new EnergyContainerList(energyContainer);
	}

	//tooltip
	public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		tooltip.add(I18n.format("pollution.machine.node_blast_furnace.tooltip.1"));
		tooltip.add(I18n.format("pollution.machine.node_blast_furnace.tooltip.2"));
		tooltip.add(I18n.format("pollution.machine.node_blast_furnace.tooltip.3"));
		tooltip.add(I18n.format("pollution.machine.node_blast_furnace.tooltip.4"));
		tooltip.add(I18n.format("pollution.machine.node_blast_furnace.tooltip.5"));
		tooltip.add(I18n.format("pollution.machine.node_blast_furnace.tooltip.6"));
		tooltip.add(I18n.format("pollution.machine.node_blast_furnace.tooltip.7"));
		tooltip.add(I18n.format("pollution.machine.node_blast_furnace.tooltip.8"));

	}

	//集成父类的UI信息 添加自己的炉温信息
	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		textList.add(new TextComponentTranslation("Temperature: %s", Temp));
		textList.add(new TextComponentTranslation("pollution.machine.node_blast_furnace_coillevel", this.CoilLevel).setStyle((new Style()).setColor(TextFormatting.WHITE)));
	}

	@Override
	public SoundEvent getBreakdownSound() {
		return GTSoundEvents.BREAKDOWN_ELECTRICAL;
	}

	@Override
	public boolean hasMufflerMechanics() {
		return true;
	}

	@Override
	public boolean canBeDistinct() {
		return true;
	}

	//配方检测 用炉温匹配高炉配方的炉温
	@Override
	public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {
		return this.Temp >= recipe.getProperty(TemperatureProperty.getInstance(), 0);
	}

	//锻炉逻辑，高炉模式256x并行，炼金模式16x并行
	protected class NodeBlastFurnaceRecipeLogic extends MultiblockRecipeLogic {

		public NodeBlastFurnaceRecipeLogic(RecipeMapMultiblockController tileEntity) {
			super(tileEntity);
		}

		@Override
		public int getParallelLimit() {
			if(this.getRecipeMap() == RecipeMaps.BLAST_RECIPES){
				return 256;
			}
			else if (this.getRecipeMap() == PORecipeMaps.FORGE_ALCHEMY_RECIPES){
				return 16;
			}
			else return 1;
		}

		@Override
		public void setMaxProgress(int maxProgress) {
			this.maxProgressTime = (int)(maxProgress * (1 - 0.05 * CoilLevel));
		}
	}

	//节点锻炉核心逻辑
	//锻炉先检测是否是锻炉炼金配方，如果是才进行后续，否则一般处理
	//在锻炉炼金配方，每30s检测输入总线是否有封装灵气节点，如果有则消耗掉，并且在接下来30s内允许配方运行
	//每当一个锻炉炼金配方运行成功，根据当前30s内消耗掉的封装灵气节点的属性，向输出仓输出不同量的两种漫宿物质
	//同时保持一个计时器计算连续允许配方运行的时长，进行越来越高的耗能减免，封顶25%
	/*
	protected void updateFormedValid(){
		if (!this.isActive()) {
			setActive(true);
		}
	}
	*/

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("  ABBBBBBBA  ", "  CBBBBBBBC  ", "   CBBBBBC   ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
				.aisle(" AADDDBDDDAA ", " CBDDDDDDDBC ", " EC       CE ", "             ", "             ", "  DDDDDDDDD  ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
				.aisle("AADDDDBDDDDAA", "CBCDDDDDDDCBC", " CC       CC ", "  C       C  ", "  C       C  ", " DC       CD ", "  C       C  ", "  E       E  ", "             ", "             ", "             ", "   DDDDDDD   ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
				.aisle("BDDDDDBDDDDDB", "BDDCFFFFFCDDB", "C  CGGGGGC  C", "   CGGGGGC   ", "   CGGGGGC   ", " D CHHHHHC D ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "  DC     CD  ", "   C     C   ", "   E     E   ", "             ", "             ", "    DDDDD    ", "             ", "             ")
				.aisle("BDDDBBBBBDDDB", "BDDFFFFFFFDDB", "B  GIIIIIG  B", "   GIIIIIG   ", "   GIIIIIG   ", " D HFFFFFH D ", "    GGGGG    ", "    GGGGG    ", "    HHHHH    ", "    GGGGG    ", "    GGGGG    ", "  D HHHHH D  ", "    C   C    ", "    C   C    ", "    C   C    ", "    CHHHC    ", "   DCCCCCD   ", "    E   E    ", "             ")
				.aisle("BDDDBDDDBDDDB", "BDDFF   FFDDB", "B  GI   IG  B", "   GI   IG   ", "   GI   IG   ", " D HF   FH D ", "    GIIIG    ", "    GIIIG    ", "    HIIIH    ", "    GIIIG    ", "    GIIIG    ", "  D HFFFH D  ", "     GGG     ", "     GGG     ", "     GGG     ", "    HGGGH    ", "   DCFFFCD   ", "     CCC     ", "     XXX     ")
				.aisle("BBBBBDDDBBBBB", "BDDFF   FFDDB", "B  GI   IG  B", "   GI   IG   ", "   GI   IG   ", " D HF   FH D ", "    GI IG    ", "    GI IG    ", "    HI IH    ", "    GI IG    ", "    GI IG    ", "  D HF FH D  ", "     GIG     ", "     GIG     ", "     GIG     ", "    HGIGH    ", "   DCF FCD   ", "     C C     ", "     XYX     ")
				.aisle("BDDDBDDDBDDDB", "BDDFF   FFDDB", "B  GI   IG  B", "   GI   IG   ", "   GI   IG   ", " D HF   FH D ", "    GIIIG    ", "    GIIIG    ", "    HIIIH    ", "    GIIIG    ", "    GIIIG    ", "  D HFFFH D  ", "     GGG     ", "     GGG     ", "     GGG     ", "    HGGGH    ", "   DCFFFCD   ", "     CCC     ", "     XXX     ")
				.aisle("BDDDBBBBBDDDB", "BDDFFFFFFFDDB", "B  GIIIIIG  B", "   GIIIIIG   ", "   GIIIIIG   ", " D HFFFFFH D ", "    GGGGG    ", "    GGGGG    ", "    HHHHH    ", "    GGGGG    ", "    GGGGG    ", "  D HHHHH D  ", "    C   C    ", "    C   C    ", "    C   C    ", "    CHHHC    ", "   DCCCCCD   ", "    E   E    ", "             ")
				.aisle("BDDDDDBDDDDDB", "BDDCFFFFFCDDB", "C  CGGGGGC  C", "   CGGGGGC   ", "   CGGGGGC   ", " D CHHHHHC D ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "  DC     CD  ", "   C     C   ", "   E     E   ", "             ", "             ", "    DDDDD    ", "             ", "             ")
				.aisle("AADDDDBDDDDAA", "CBCDDDDDDDCBC", " CC       CC ", "  C       C  ", "  C       C  ", " DC       CD ", "  C       C  ", "  E       E  ", "             ", "             ", "             ", "   DDDDDDD   ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
				.aisle(" AADDDBDDDAA ", " CBDDDDDDDBC ", " EC       CE ", "             ", "             ", "  DDDDDDDDD  ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
				.aisle("  ABBBBBBBA  ", "  CBBBSBBBC  ", "   CBBBBBC   ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
				.where('S', selfPredicate())
				.where(' ', any())
				.where('A', states(getCasingState()))
				.where('B', states(getCasingState2()).setMinGlobalLimited(20)
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1))
						.or(abilities(MultiblockAbility.INPUT_LASER).setMaxGlobalLimited(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(3).setPreviewCount(1))
						.or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(3).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(3).setPreviewCount(1))
						.or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(3).setPreviewCount(1)))
				.where('C', states(getCasingState3()))
				.where('D', states(getCasingState4()))
				.where('E', states(getCasingState5()))
				.where('F', states(getCasingState6()))
				.where('G', states(getCasingState7()))
				.where('H', states(getCasingState8()))
				.where('I', CP_COIL_CASING.get())
				.where('X', states(getCasingState2()))
				.where('Y', abilities(MultiblockAbility.MUFFLER_HATCH).setExactLimit(1).setPreviewCount(1))
				.build();
	}

	private static IBlockState getCasingState() {
		return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_GEARBOX);
	}

	private static IBlockState getCasingState2() {
		return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_BASIC);
	}

	private static IBlockState getCasingState3() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.mansussteel).getBlock(PollutionMaterials.mansussteel);
	}

	private static IBlockState getCasingState4() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.VOID_PRISM);
	}

	private static IBlockState getCasingState5() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}

	private static IBlockState getCasingState6() {
		return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_PIPE);
	}

	private static IBlockState getCasingState7() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.AAMINATED_GLASS);
	}

	private static IBlockState getCasingState8() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_0);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.MANA_BASIC;
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}

