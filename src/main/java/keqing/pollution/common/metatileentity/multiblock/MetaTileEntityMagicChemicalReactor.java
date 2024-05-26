package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.MetaBlocks;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.metatileentity.PORecipeMapMultiblockController;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import static keqing.pollution.api.unification.PollutionMaterials.infused_alchemy;

public class MetaTileEntityMagicChemicalReactor extends PORecipeMapMultiblockController {

	public MetaTileEntityMagicChemicalReactor(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, new RecipeMap[]{RecipeMaps.CHEMICAL_RECIPES, PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES});
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityMagicChemicalReactor(this.metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("   AAAAA   ", "    B      ", "     B     ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "   AAAAA   ")
				.aisle("  AACCCAA  ", "           ", "           ", "      B    ", "           ", "           ", "    B      ", "     B     ", "           ", "           ", "           ", "           ", "  AACCCAA  ")
				.aisle(" AADCACDAA ", "    D D    ", "           ", "           ", "       B   ", "   B       ", "           ", "           ", "      B    ", "           ", "           ", "    D D    ", " AADCACDAA ")
				.aisle("AADCCACCDAA", "           ", "           ", "           ", "  B        ", "        B  ", "           ", "           ", "           ", "   B   B   ", "    B      ", "     B     ", "AADCCACCDAA")
				.aisle("ACCCCACCCCA", "  D  E  D B", "     E     ", " B   E     ", "     E     ", "     E     ", "     E   B ", "     E     ", "  B  E     ", "     E     ", "     E B   ", "  D  E  D  ", "ACCCCACCCCA")
				.aisle("ACAAAAAAACA", "    EEE    ", "B   EEE   B", "    EEE    ", "    EEE    ", "    EEE    ", "    EEE    ", " B  EEE  B ", "    EEE    ", "    EEE    ", "    EEE    ", "   BEEEB   ", "ACAAAAAAACA")
				.aisle("ACCCCACCCCA", "B D  E  D  ", "     E     ", "     E   B ", "     E     ", "     E     ", " B   E     ", "     E     ", "     E  B  ", "     E     ", "   B E     ", "  D  E  D  ", "ACCCCACCCCA")
				.aisle("AADCCACCDAA", "           ", "           ", "           ", "        B  ", "  B        ", "           ", "           ", "           ", "   B   B   ", "      B    ", "     B     ", "AADCCACCDAA")
				.aisle(" AADCACDAA ", "    D D    ", "           ", "           ", "   B       ", "       B   ", "           ", "           ", "    B      ", "           ", "           ", "    D D    ", " AADCACDAA ")
				.aisle("  AACCCAA  ", "           ", "           ", "    B      ", "           ", "           ", "      B    ", "     B     ", "           ", "           ", "           ", "           ", "  AACCCAA  ")
				.aisle("   AASAA   ", "      B    ", "     B     ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "   AAFAA   ")
				.where('S', selfPredicate())
				.where('A', states(getCasingState()).setMinGlobalLimited(35).or(autoAbilities()))
				.where('B', states(getCasingState2()))
				.where('C', states(getCasingState3()))
				.where('D', states(getCasingState4()))
				.where('E', states(getCasingState5()))
				.where(' ', any())
				.where('F', abilities(POMultiblockAbility.VIS_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
				.build();
	}

	@Override
	public Material getMaterial() {
		return infused_alchemy;
	}

	private static IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER);
	}

	private static IBlockState getCasingState2() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}

	private static IBlockState getCasingState3() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
	}

	private static IBlockState getCasingState4() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
	}

	private static IBlockState getCasingState5() {
		return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.SPELL_PRISM_WATER;
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}

	@Override
	public boolean canBeDistinct() {
		return true;
	}

}