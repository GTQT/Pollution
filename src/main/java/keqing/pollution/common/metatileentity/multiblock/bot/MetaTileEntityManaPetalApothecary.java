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
import thaumcraft.api.blocks.BlocksTC;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.LivingRockVariant;
import vazkii.botania.api.state.enums.LivingWoodVariant;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;

import static keqing.pollution.api.recipes.PORecipeMaps.MANA_PETAL_RECIPES;

public class MetaTileEntityManaPetalApothecary extends POManaMultiblock {

	public MetaTileEntityManaPetalApothecary(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, MANA_PETAL_RECIPES);
		this.recipeMapWorkable = new POManaMultiblockRecipeLogic(this);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return new MetaTileEntityManaPetalApothecary(this.metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("       A       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
				.aisle("      AAA      ", "       A       ", "       B       ", "       C       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
				.aisle("       A       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
				.aisle("       D       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "   AEEEEEEEA   ", "   AFFFFFFFA   ", "   GADDDDDAG   ")
				.aisle("     AAAAA     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "    AEEEEEA    ", "   E       E   ", "   F       F   ", "   AAEEEEEAA   ")
				.aisle("    AEEEEEA    ", "     EEEEE     ", "               ", "               ", "               ", "               ", "               ", "     AAAAA     ", "    E     E    ", "   E       E   ", "   F       F   ", "   DE     ED   ")
				.aisle(" A  AEEEEEA  A ", "     EEEEE     ", "      ADA      ", "      ADA      ", "      ADA      ", "      ADA      ", "      ADA      ", "     AEEEA     ", "    E     E    ", "   E       E   ", "   F       F   ", "   DE     ED   ")
				.aisle("AAADAEEEEEADAAA", " A   EEEEE   A ", " B    D D    A ", " C    D D    C ", "      D D      ", "      D D      ", "      D D      ", "     AEEEA     ", "    E     E    ", "   E       E   ", "   F       F   ", "   DE     ED   ")
				.aisle(" A  AEEEEEA  A ", "     EEEEE     ", "      ADA      ", "      ADA      ", "      ADA      ", "      ADA      ", "      ADA      ", "     AEEEA     ", "    E     E    ", "   E       E   ", "   F       F   ", "   DE     ED   ")
				.aisle("    AEEEEEA    ", "     EESEE     ", "               ", "               ", "               ", "               ", "               ", "     AAAAA     ", "    E     E    ", "   E       E   ", "   F       F   ", "   DE     ED   ")
				.aisle("     AAAAA     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "    AEEEEEA    ", "   E       E   ", "   F       F   ", "   AAEEEEEAA   ")
				.aisle("       D       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "   AEEEEEEEA   ", "   AFFFFFFFA   ", "   GADDDDDAG   ")
				.aisle("       A       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
				.aisle("      AAA      ", "       A       ", "       B       ", "       C       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
				.aisle("       A       ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
				.where('S', selfPredicate())
				.where(' ', any())
				.where('A', states(getCasingState()))
				.where('B', states(getCasingState2()))
				.where('C', states(getCasingState3()))
				.where('D', states(getCasingState4()))
				.where('E', states(getCasingState5()).setMinGlobalLimited(9).or(autoAbilities()))
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
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}
	protected IBlockState getCasingState3() {
		return ModBlocks.floatingFlower.getDefaultState();
	}
	protected IBlockState getCasingState4() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.mansussteel).getBlock(PollutionMaterials.mansussteel);
	}
	protected IBlockState getCasingState5() {
		return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.BRICK);
	}
	protected IBlockState getCasingState6() {
		return ModBlocks.livingwood.getDefaultState().withProperty(BotaniaStateProps.LIVINGWOOD_VARIANT, LivingWoodVariant.PLANKS);
	}
	protected IBlockState getCasingState7() {
		return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
	}

	public SoundEvent getBreakdownSound() {
		return GTSoundEvents.BREAKDOWN_ELECTRICAL;
	}

	@SideOnly(Side.CLIENT)
	protected @NotNull ICubeRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}

