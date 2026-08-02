package meowmel.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import meowmel.pollution.api.metatileentity.ManaMultiblockController;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.LivingRockVariant;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;

import static meowmel.pollution.api.recipes.PORecipeMaps.MANA_RUNE_ALTAR_RECIPES;

public class MetaTileEntityManaRuneAltar extends ManaMultiblockController {

    private final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:mana_rune_altar", () -> {
                DeclarativePatternBuilder builder = DeclarativePatternBuilder.start()
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
                        .self('S', MetaTileEntityManaRuneAltar.class)
                        .block('A', getCasingState())
                        .block('B', getCasingState2()).block('C', getCasingState3()).block('D', getCasingState4())
                        .block('E', getCasingState5()).block('F', getCasingState6()).block('G', getCasingState7())
                        .block('H', getCasingState8()).block('I', getCasingState9()).block('J', getCasingState10())
                        .any(' ');
                return configureManaRecipeCasing(builder, 'A', getCasingState(), MANA_RUNE_ALTAR_RECIPES, 71)
                        .buildStructureDefinition();
            });

	public MetaTileEntityManaRuneAltar(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, MANA_RUNE_ALTAR_RECIPES);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return new MetaTileEntityManaRuneAltar(this.metaTileEntityId);
	}

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
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
		return MetaBlocks.FRAMES.get(PollutionMaterials.Mansussteel).getBlock(PollutionMaterials.Mansussteel);
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
	protected  ICubeRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}

