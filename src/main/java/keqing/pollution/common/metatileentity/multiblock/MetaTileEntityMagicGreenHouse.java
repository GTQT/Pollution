package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.Material;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.metatileentity.PORecipeMapMultiblockController;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import static gregtechfoodoption.recipe.GTFORecipeMaps.GREENHOUSE_RECIPES;
import static keqing.pollution.api.recipes.PORecipeMaps.MAGIC_GREENHOUSE_RECIPES;
import static keqing.pollution.api.unification.PollutionMaterials.infused_water;

public class MetaTileEntityMagicGreenHouse extends PORecipeMapMultiblockController {
	public MetaTileEntityMagicGreenHouse(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, new RecipeMap[]{GREENHOUSE_RECIPES, MAGIC_GREENHOUSE_RECIPES});
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityMagicGreenHouse(this.metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("CCCCC", "CCCCC", "GGGGG", "GGGGG", "CCCCC", "CCCCC")
				.aisle("CCCCC", "CPHPC", "G###G", "G###G", "CPHPC", "CDDDC")
				.aisle("CCCCC", "CHHHC", "G###G", "G###G", "CHHHC", "CDDDC")
				.aisle("CCCCC", "CPHPC", "G###G", "G###G", "CPHPC", "CDDDC")
				.aisle("CCCCC", "CFSCC", "GGGGG", "GGGGG", "CCCCC", "CCCCC")
				.where('S', selfPredicate())
				.where('C', states(getCasingState()).setMinGlobalLimited(40).or(autoAbilities()))
				.where('P', states(getSecondCasingState()))
				.where('D', states(getCasingState4()))
				.where('H', states(getCasingState5()))
				.where('G', states(getCasingState3()))
				.where('F', abilities(POMultiblockAbility.VIS_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
				.where('#', air())
				.build();
	}

	@Override
	public Material getMaterial() {
		return infused_water;
	}

	private static IBlockState getSecondCasingState() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_3);
	}

	private static IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER);
	}

	private static IBlockState getCasingState4() {
		return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_PIPE);
	}

	private static IBlockState getCasingState5() {
		return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TUNGSTENSTEEL_GEARBOX);
	}

	private static IBlockState getCasingState3() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
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
