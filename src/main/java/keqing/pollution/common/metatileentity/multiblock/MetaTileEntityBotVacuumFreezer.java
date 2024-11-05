package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
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
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.gtqtcore.common.block.GTQTMetaBlocks;
import keqing.gtqtcore.common.block.blocks.BlockCoolingCoil;
import keqing.gtqtcore.common.block.blocks.GTQTBlockWireCoil;
import keqing.gtqtcore.common.block.blocks.GTQTTurbineCasing1;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityCryogenicFreezer;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.capability.IManaMultiblock;
import keqing.pollution.api.capability.ipml.POManaMultiblockWithElectricRecipeLogic;
import keqing.pollution.api.metatileentity.POManaMultiblock;
import keqing.pollution.api.metatileentity.POManaMultiblockWithElectric;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.*;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaHatch;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaPoolHatch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityCryogenicFreezer.coolingCoils;

public class MetaTileEntityBotVacuumFreezer extends POManaMultiblockWithElectric {

	int temperature;

	public MetaTileEntityBotVacuumFreezer(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, RecipeMaps.VACUUM_RECIPES);
		this.recipeMapWorkable = new BotVacuumFreezerRecipeLogic(this);
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityBotVacuumFreezer(this.metaTileEntityId);
	}

	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		Object type = context.get("CoolingCoilType");
		if (type instanceof BlockCoolingCoil.CoolingCoilType) {
			this.temperature = ((BlockCoolingCoil.CoolingCoilType)type).coilTemperature;
		} else {
			this.temperature = BlockCoolingCoil.CoolingCoilType.MANGANESE_IRON_ARSENIC_PHOSPHIDE.coilTemperature;
		}
	}
	public int getCoilTier()
	{
		if(temperature==160)return 1;
		if(temperature==50)return 2;
		if(temperature==1)return 3;
		return 0;
	}
	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		if(isStructureFormed()) {
			textList.add(new TextComponentTranslation("磁致冷线圈温度: %s K", this.temperature).setStyle((new Style()).setColor(TextFormatting.WHITE)));
			textList.add(new TextComponentTranslation("磁致冷线圈等级: %s K", this.getCoilTier()).setStyle((new Style()).setColor(TextFormatting.WHITE)));
			textList.add(new TextComponentTranslation("魔力仓等级: %s", this.getTier()).setStyle((new Style()).setColor(TextFormatting.WHITE)));
			textList.add(new TextComponentTranslation("魔力仓缓存: %s", this.getMana()).setStyle((new Style()).setColor(TextFormatting.WHITE)));
			textList.add(new TextComponentTranslation("每tick预计消耗: %s", 20 * Math.pow(2, getTier() - 1)).setStyle((new Style()).setColor(TextFormatting.WHITE)));
		}
	}

	@Override
	public SoundEvent getBreakdownSound() {
		return GTSoundEvents.BREAKDOWN_ELECTRICAL;
	}

	@Override
	public boolean canBeDistinct() {
		return true;
	}

	protected class BotVacuumFreezerRecipeLogic extends POManaMultiblockWithElectricRecipeLogic {

		public BotVacuumFreezerRecipeLogic(POManaMultiblockWithElectric tileEntity) {
			super(tileEntity);
		}

		@Override
		public void setMaxProgress(int maxProgress) {
			this.maxProgressTime = (int) (maxProgress*(10-2.0*getCoilTier())/10);
		}
	}

	@Override
	public void update(){
		super.update();
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("      AXXXA      ", "       XXX       ", "        X        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
				.aisle("     BABBBAB     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
				.aisle("    BBBAAABBB    ", "    CCC   CCC    ", "    DED   DED    ", "     D     D     ", "                 ", "     F     F     ", "                 ", "                 ", "                 ")
				.aisle("     B AAA B     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
				.aisle("       GAG       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
				.aisle("      GBBBG      ", "       DHD       ", "       DHD       ", "       DHD       ", "       DHD       ", "       GGG       ", "       ADA       ", "       GGG       ", "                 ")
				.aisle("     GBBCBBG     ", "      DE ED      ", "      DE ED      ", "      DE ED      ", "      DEBED      ", "      GGBGG      ", "      A   A      ", "      GGCGG      ", "       GGG       ")
				.aisle(" BAAGBBCCCBBGAAB ", " C   DE I ED   C ", " D   DE J ED   D ", "     DE   ED     ", "     DE   ED     ", "     GGBBBGG     ", "     A K K A     ", "     GGCCCGG     ", "      GGHGG      ")
				.aisle("BBBAABCCCCCBAABBB", "CCC  H IEI H  CCC", "DED  H JEJ H  DED", " D   H  E  H   D ", "     HB E BH     ", " F   GBBBBBG   F ", "     D  K  D     ", "     GCCCCCG     ", "      GHHHG      ")
				.aisle(" BAAGBBCCCBBGAAB ", " C   DE I ED   C ", " D   DE J ED   D ", "     DE   ED     ", "     DE   ED     ", "     GGBBBGG     ", "     A K K A     ", "     GGCCCGG     ", "      GGHGG      ")
				.aisle("     GBBCBBG     ", "      DE ED      ", "      DE ED      ", "      DE ED      ", "      DEBED      ", "      GGBGG      ", "      A   A      ", "      GGCGG      ", "       GGG       ")
				.aisle("      GBBBG      ", "       DHD       ", "       DHD       ", "       DHD       ", "       DHD       ", "       GGG       ", "       ADA       ", "       GGG       ", "                 ")
				.aisle("       GAG       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
				.aisle("     B AAA B     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
				.aisle("    BBBAAABBB    ", "    CCC   CCC    ", "    DED   DED    ", "     D     D     ", "                 ", "     F     F     ", "                 ", "                 ", "                 ")
				.aisle("     BABBBAB     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
				.aisle("      AXXXA      ", "       XSX       ", "        X        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")

				.where('S', selfPredicate())
				.where(' ', any())
				.where('A', states(getCasingState()))
				.where('X', states(getCasingState2())
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1))
						.or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2).setPreviewCount(1))
						.or(abilities(MultiblockAbility.INPUT_LASER).setMaxGlobalLimited(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
						.or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
						.or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
						.or(abilities(POMultiblockAbility.MANA_HATCH).setExactLimit(1)))
				.where('B', states(getCasingState2()))
				.where('C', states(getCasingState3()))
				.where('D', states(getCasingState4()))
				.where('E', states(getCasingState5()))
				.where('G', states(getCasingState6()))
				.where('F', states(getCasingState7()))
				.where('H', coolingCoils())
				.where('I', states(getCasingState8()))
				.where('J', states(getCasingState9()))
				.where('K', states(getCasingState10()))
				.build();
	}
	@Override
	public boolean hasMaintenanceMechanics() {
		return true;
	}
	@Override
	public boolean hasMufflerMechanics() {
		return false;
	}
	private static IBlockState getCasingState() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.hyperdimensional_silver).getBlock(PollutionMaterials.hyperdimensional_silver);
	}

	private static IBlockState getCasingState2() {
		return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_4);
	}

	private static IBlockState getCasingState3() {
		return GTQTMetaBlocks.TURBINE_CASING1.getState(GTQTTurbineCasing1.TurbineCasingType.ADVANCED_COLD_CASING);
	}

	private static IBlockState getCasingState4() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
	}

	private static IBlockState getCasingState5() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
	}

	private static IBlockState getCasingState6() {
		return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_1_CASING);
	}

	private static IBlockState getCasingState7() {
		return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
	}

	protected IBlockState getCasingState8() {
		assert Blocks.DIRT != null;
		return Blocks.DIRT.getDefaultState();
	}

	protected IBlockState getCasingState9() {
		return ModBlocks.floatingFlower.getDefaultState();
	}

	protected IBlockState getCasingState10() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.MANA_4;
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}
