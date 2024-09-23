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
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.metatileentity.PORecipeMapMultiblockController;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;

public class MetaTileEntityNodeBlastFurnace extends PORecipeMapMultiblockController {

	int CoilLevel;
	int Temp;

	public  MetaTileEntityNodeBlastFurnace(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, new RecipeMap[]{RecipeMaps.BLAST_RECIPES, RecipeMaps.ALLOY_SMELTER_RECIPES});
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityNodeBlastFurnace (this.metaTileEntityId);
	}

	//在成型时读取机器的线圈级别 并且给炉温赋值
	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		Object CoilLevel = context.get("COILTieredStats");
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
				.aisle("  ABBBBBBBA  ", "  CBBBSBBBC  ", "   CBBZBBC   ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
				.where('S', selfPredicate())
				.where(' ', any())
				.where('A', states(getCasingState()))
				.where('B', states(getCasingState2()).setMinGlobalLimited(20)
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1))
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
				.where('Z', abilities(POMultiblockAbility.VIS_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
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

