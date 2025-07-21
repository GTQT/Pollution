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
import vazkii.botania.api.state.enums.LivingWoodVariant;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;

import static keqing.pollution.api.recipes.PORecipeMaps.PURE_DAISY_RECIPES;

public class MetaTileEntityIndustrialPureDaisy extends POManaMultiblock {

	public MetaTileEntityIndustrialPureDaisy(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, PURE_DAISY_RECIPES);
		this.recipeMapWorkable = new POManaMultiblockRecipeLogic(this);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return new keqing.pollution.common.metatileentity.multiblock.bot.MetaTileEntityIndustrialPureDaisy(this.metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("ABA   ABA", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
				.aisle("BCCCCCCCB", " CC   CC ", " C     C ", " C     C ", " DAAAAAD ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
				.aisle("ACABBBACA", " CAEEEAC ", "  AEEEA  ", "  ABBBA  ", " AACCCAA ", "  CCDCC  ", "   CCC   ", "    C    ", "         ", "         ", "         ", "         ", "         ")
				.aisle(" CBACABC ", "  E F E  ", "  EF FE  ", "  B   B  ", " AC   CA ", "  C   C  ", "  C   C  ", "   C C   ", "    C    ", "         ", "         ", "         ", "         ")
				.aisle(" CBCGCBC ", "  EF FE  ", "  E   E  ", "  B   B  ", " AC   CA ", "  D   D  ", "  C   C  ", "  C   C  ", "   CGC   ", "    C    ", "    C    ", "    C    ", "    G    ")
				.aisle(" CBACABC ", "  E F E  ", "  EF FE  ", "  B   B  ", " AC   CA ", "  C   C  ", "  C   C  ", "   C C   ", "    C    ", "         ", "         ", "         ", "         ")
				.aisle("ACABBBACA", " CAEEEAC ", "  AEEEA  ", "  ABBBA  ", " AACCCAA ", "  CCDCC  ", "   CCC   ", "    C    ", "         ", "         ", "         ", "         ", "         ")
				.aisle("BCCCSCCCB", " CC   CC ", " C     C ", " C     C ", " DAAAAAD ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
				.aisle("ABA   ABA", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
				.where('S', selfPredicate())
				.where(' ', any())
				.where('A', states(getCasingState()))
				.where('B', states(getCasingState2()))
				.where('C', states(getCasingState3()).setMinGlobalLimited(9).or(autoAbilities()))
				.where('D', states(getCasingState4()))
				.where('E', states(getCasingState5()))
				.where('F', states(getCasingState6()))
				.where('G', states(getCasingState7()))
				.build();
	}
	@SideOnly(Side.CLIENT)
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return POTextures.Livingrock_0;
	}

	protected IBlockState getCasingState() {
		return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.CHISELED_BRICK);
	}
	protected IBlockState getCasingState2() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.mansussteel).getBlock(PollutionMaterials.mansussteel);
	}
	protected IBlockState getCasingState3() {
		return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.BRICK);
	}
	protected IBlockState getCasingState4() {
		return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
	}
	protected IBlockState getCasingState5() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.LAMINATED_GLASS);
	}
	protected IBlockState getCasingState6() {
		return ModBlocks.floatingFlower.getDefaultState();
	}
	protected IBlockState getCasingState7() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}

	public SoundEvent getBreakdownSound() {
		return GTSoundEvents.BREAKDOWN_ELECTRICAL;
	}

	@SideOnly(Side.CLIENT)
	protected @NotNull ICubeRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}