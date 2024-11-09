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
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.metatileentity.PORecipeMapMultiblockController;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import static keqing.pollution.api.unification.PollutionMaterials.infused_fly;

public class MetaTileEntityMagicAutoclave extends PORecipeMapMultiblockController {

	public MetaTileEntityMagicAutoclave(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, new RecipeMap[]{RecipeMaps.AUTOCLAVE_RECIPES});
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityMagicAutoclave(this.metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("YYY", "YYY", "YYY")
				.aisle("XXX", "XCX", "XXX")
				.aisle("XXX", "XCX", "XXX")
				.aisle("XXX", "XCX", "XXX")
				.aisle("XXX", "XSX", "XFX")
				.where('S', selfPredicate())
				.where('X', states(getCasingState()).setMinGlobalLimited(15).or(autoAbilities()))
				.where('C', states(getCasingState2()))
				.where('Y', states(getCasingState3()))
				.where('A', air())
				.where('#', any())
				.where('F', abilities(POMultiblockAbility.VIS_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
				.build();
	}

	@Override
	public Material getMaterial() {
		return infused_fly;
	}

	private static IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_AIR);
	}

	private static IBlockState getCasingState2() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
	}

	private static IBlockState getCasingState3() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_WATER);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.SPELL_PRISM_AIR;
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