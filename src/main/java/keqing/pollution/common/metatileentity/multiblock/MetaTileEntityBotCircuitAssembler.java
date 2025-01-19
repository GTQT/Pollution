package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.gtqtcore.common.block.GTQTMetaBlocks;
import keqing.gtqtcore.common.block.blocks.GTQTStepper;
import keqing.pollution.api.capability.ipml.POManaMultiblockWithElectricRecipeLogic;
import keqing.pollution.api.metatileentity.POManaMultiblockWithElectric;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POManaPlate;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaHatch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_FRAME;

public class MetaTileEntityBotCircuitAssembler extends POManaMultiblockWithElectric {

	MetaTileEntityManaHatch manaHatch;
	private int frameLevel;

	public MetaTileEntityBotCircuitAssembler(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, RecipeMaps.CIRCUIT_ASSEMBLER_RECIPES);
		this.recipeMapWorkable = new MetaTileEntityBotCircuitAssembler.BotCircuitAssemblerRecipeLogic(this);
	}

	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		Object frameLevel = context.get("FRAMETieredStats");
		this.frameLevel = POUtils.getOrDefault(() -> frameLevel instanceof WrappedIntTired,
				() -> ((WrappedIntTired) frameLevel).getIntTier(),
				0);
		for (Map.Entry<String, Object> str : context.entrySet()) {
			if(str.getKey().startsWith("Multi")  ) {
				HashSet set = (HashSet) str.getValue();
				for (var s: set
				) {
					if(s instanceof MetaTileEntityManaHatch)
					{
						this.manaHatch = (MetaTileEntityManaHatch)s;
					}
				}
			}
		}
	}

	protected class BotCircuitAssemblerRecipeLogic extends POManaMultiblockWithElectricRecipeLogic {

		public BotCircuitAssemblerRecipeLogic(POManaMultiblockWithElectric tileEntity) {
			super(tileEntity);
		}

		@Override
		public void setMaxProgress(int maxProgress) {
			this.maxProgressTime = maxProgress * (10 - frameLevel) / 10;
		}
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityBotVacuumFreezer(this.metaTileEntityId);
	}

	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		if(isStructureFormed()) {
			textList.add(new TextComponentTranslation("魔力仓等级: %s", this.getTier()).setStyle((new Style()).setColor(TextFormatting.WHITE)));
			textList.add(new TextComponentTranslation("魔力仓缓存: %s", this.getMana()).setStyle((new Style()).setColor(TextFormatting.WHITE)));
			textList.add(new TextComponentTranslation("每tick预计消耗: %s", 20 * Math.pow(2, getTier() - 1)).setStyle((new Style()).setColor(TextFormatting.WHITE)));
		}
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("A           A", "A           A", "A           A", "A           A", "A           A", "B           B")
				.aisle(" ACCCCCCCCCA ", " ADDDDDDDDDA ", " AEEEEEEEEEA ", " CCCCCCCCCCC ", "             ", "             ")
				.aisle(" CCCCCCCCCCC ", " DFXFXFXFXFD ", " EXXXXXXXXXE ", " CEEEEGEEEEC ", "             ", "             ")
				.aisle(" CCGGGGGGGCC ", " DFXFXFXFXFD ", " EXXXXXXXXXE ", " CEGGGGGGGEC ", "             ", "             ")
				.aisle(" CCGGGGGGGCC ", " DFBFBFBFBFD ", " EXXXXXXXXXE ", " CGGGGGGGGGC ", "             ", "             ")
				.aisle(" CCGGGGGGGCC ", " DFXFXFXFXFD ", " EXXXXXXXXXE ", " CEGGGGGGGEC ", "             ", "             ")
				.aisle(" CCCCCCCCCCC ", " DFXFXFXFXFD ", " EXXXXXXXXXE ", " CEEEEGEEEEC ", "             ", "             ")
				.aisle(" ACCCCCCCCCA ", " ADDDDSDDDDA ", " AEEEEEEEEEA ", " CCCCCCCCCCC ", "             ", "             ")
				.aisle("A           A", "A           A", "A           A", "A           A", "A           A", "B           B")

				.where('S', selfPredicate())
				.where('A', states(getCasingState()))
				.where('B', states(getCasingState2()))
				.where('C', states(getCasingState3()).setMinGlobalLimited(40)
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1))
						.or(abilities(MultiblockAbility.INPUT_ENERGY).setExactLimit(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
						.or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
						.or(abilities(POMultiblockAbility.MANA_HATCH).setExactLimit(1)))
				.where('D', states(getCasingState4()))
				.where('E', states(getCasingState5()))
				.where('F', states(getCasingState6()))
				.where('G', CP_FRAME.get())
				.where(' ', any())
				.where('X', air())
				.build();
	}
	private static IBlockState getCasingState() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.blood_of_avernus).getBlock(PollutionMaterials.blood_of_avernus);
	}
	private static IBlockState getCasingState2() {
		return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
	}
	private static IBlockState getCasingState3() {
		return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_5);
	}
	private static IBlockState getCasingState4() {
		return GTQTMetaBlocks.STEPPER.getState(GTQTStepper.CasingType.CLEAN_MKV);
	}
	private static IBlockState getCasingState5() {
		return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
	}
	private static IBlockState getCasingState6() {
		return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.DAMINATED_GLASS);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		return POTextures.MANA_5;
	}

	@Override
	public boolean hasMaintenanceMechanics() {
		return true;
	}

	@Override
	public boolean hasMufflerMechanics() {
		return false;
	}

	@Override
	public void update(){
		super.update();
	}

	@Override
	public SoundEvent getBreakdownSound() {
		return GTSoundEvents.BREAKDOWN_ELECTRICAL;
	}

	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}
