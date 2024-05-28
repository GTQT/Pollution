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
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import static keqing.pollution.api.unification.PollutionMaterials.infused_exchange;

public class MetaTileEntityMagicSolidifier extends PORecipeMapMultiblockController {
	public MetaTileEntityMagicSolidifier(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, new RecipeMap[]{RecipeMaps.FLUID_SOLIDFICATION_RECIPES, RecipeMaps.EXTRACTOR_RECIPES, RecipeMaps.CANNER_RECIPES});
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityMagicSolidifier(this.metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("#XXX#####", "#XXX#####", "#XXX#####", "#XXX#####", "#XXX#####", "##G######", "#########")
				.aisle("XXXXXPYYY", "XXXXXPYYY", "XXXXX#YYY", "XXXXX#YYY", "XCCCX#YYY", "##G###GGG", "#######G#")
				.aisle("XXXXXPYYY", "XXXXXPYCY", "XXXXX#YCY", "XXXXX#YCY", "XCCCX#YCY", "GGGGG#GCG", "######GGG")
				.aisle("XXXXXPYYY", "XXXXXPYYY", "XXXXX#YYY", "XXXXX#YYY", "XCCCX#YYY", "##G###GGG", "#######G#")
				.aisle("#XXX#####", "#XXX#####", "#XSX#####", "#XFX#####", "#XXX#####", "##G######", "#########")
				.where('S', selfPredicate())
				.where('X', states(getCasingState()).setMinGlobalLimited(60).or(autoAbilities()))
				.where('Y', states(getCasingState2()))
				.where('C', states(getCasingState3()))
				.where('G', states(getCasingState4()))
				.where('P', states(getCasingState5()))
				.where('F', abilities(POMultiblockAbility.VIS_HATCH).setMaxGlobalLimited(1).setPreviewCount(1))
				.where('A', air())
				.where('#', any())
				.build();
	}

	@Override
	public Material getMaterial() {
		return infused_exchange;
	}

	private static IBlockState getCasingState() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_ORDER);
	}

	private static IBlockState getCasingState2() {
		return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_VOID);
	}

	private static IBlockState getCasingState3() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}

	private static IBlockState getCasingState4() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.DAMINATED_GLASS);
	}

	private static IBlockState getCasingState5() {
		return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.POLYTETRAFLUOROETHYLENE_PIPE);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.SPELL_PRISM_ORDER;
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
