package keqing.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.pollution.api.metatileentity.POManaMultiblock;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.LivingRockVariant;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;

import static keqing.pollution.api.recipes.PORecipeMaps.MANA_RUNE_ALTAR_RECIPES;

public class MetaTileEntityManaRuneAltar extends POManaMultiblock {

	public MetaTileEntityManaRuneAltar(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, MANA_RUNE_ALTAR_RECIPES);
		this.recipeMapWorkable = new POManaMultiblockRecipeLogic(this);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return new keqing.pollution.common.metatileentity.multiblock.bot.MetaTileEntityManaRuneAltar(this.metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("    AABAA    ", "    ACACA    ", "     C C     ", "     C C     ", "     C C     ", "     C C     ", "     D D     ", "      D      ", "      B      ")
				.aisle("  AA  A  AA  ", "  AAEEDFFAA  ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   DG   GD   ", "   H     H   ", "             ")
				.aisle(" A    A    A ", " ACEEEDFFFCA ", "             ", "             ", "             ", "             ", "  G       G  ", "             ", "             ")
				.aisle(" A    A    A ", " AECEEDFFCFA ", " C         C ", " C         C ", " C         C ", " C         C ", " D         D ", " H         H ", "             ")
				.aisle("A     A     A", "AEEECEDFCFFFA", "             ", "             ", "             ", "             ", " G         G ", "             ", "             ")
				.aisle("A     A     A", "CEEEECDCFFFFC", "C           C", "C           C", "C           C", "C           C", "D           D", "             ", "             ")
				.aisle("BAAAAAAAAAAAB", "ADDDDDBDDDDDA", "             ", "             ", "             ", "             ", "             ", "D           D", "B           B")
				.aisle("A     A     A", "CIIIICDCJJJJC", "C           C", "C           C", "C           C", "C           C", "D           D", "             ", "             ")
				.aisle("A     A     A", "AIIICIDJCJJJA", "             ", "             ", "             ", "             ", " G         G ", "             ", "             ")
				.aisle(" A    A    A ", " AICIIDJJCJA ", " C         C ", " C         C ", " C         C ", " C         C ", " D         D ", " H         H ", "             ")
				.aisle(" A    A    A ", " ACIIIDJJJCA ", "             ", "             ", "             ", "             ", "  G       G  ", "             ", "             ")
				.aisle("  AA  A  AA  ", "  AAIIDJJAA  ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   DG   GD   ", "   H     H   ", "             ")
				.aisle("    AABAA    ", "    ACSCA    ", "     C C     ", "     C C     ", "     C C     ", "     C C     ", "     D D     ", "      D      ", "      B      ")

				.where('S', selfPredicate())
				.where(' ', any())
				.where('A', states(getCasingState()).setMinGlobalLimited(9).or(autoAbilities()))
				.where('B', states(getCasingState2()))
				.where('C', states(getCasingState3()))
				.where('D', states(getCasingState4()))
				.where('E', states(getCasingState5()))
				.where('F', states(getCasingState6()))
				.where('G', states(getCasingState7()))
				.where('H', states(getCasingState8()))
				.where('I', states(getCasingState9()))
				.where('J', states(getCasingState10()))
				.build();
	}
	@SideOnly(Side.CLIENT)
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return POTextures.Livingrock_0;
	}

	protected IBlockState getCasingState() {
		return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.BRICK);
	}
	protected IBlockState getCasingState2() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}
	protected IBlockState getCasingState3() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.mansussteel).getBlock(PollutionMaterials.mansussteel);
	}
	protected IBlockState getCasingState4() {
		return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.CHISELED_BRICK);
	}
	protected IBlockState getCasingState5() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.AAMINATED_GLASS);
	}
	protected IBlockState getCasingState6() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.BAMINATED_GLASS);
	}
	protected IBlockState getCasingState7() {
		return ModBlocks.floatingFlower.getDefaultState();
	}
	protected IBlockState getCasingState8() {
		return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
	}
	protected IBlockState getCasingState9() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
	}
	protected IBlockState getCasingState10() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.DAMINATED_GLASS);
	}

	public SoundEvent getBreakdownSound() {
		return GTSoundEvents.BREAKDOWN_ELECTRICAL;
	}

	@SideOnly(Side.CLIENT)
	protected @NotNull ICubeRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}

