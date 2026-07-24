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

import static meowmel.pollution.api.recipes.PORecipeMaps.PURE_DAISY_RECIPES;

public class MetaTileEntityIndustrialPureDaisy extends ManaMultiblockController {

    private final StructureDefinition STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:industrial_pure_daisy", () -> {
                DeclarativePatternBuilder builder = DeclarativePatternBuilder.start()
                        .aisle("ABA   ABA", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
                        .aisle("BCCCCCCCB", " CC   CC ", " C     C ", " C     C ", " DAAAAAD ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
                        .aisle("ACABBBACA", " CAEEEAC ", "  AEEEA  ", "  ABBBA  ", " AACCCAA ", "  CCDCC  ", "   CCC   ", "    C    ", "         ", "         ", "         ", "         ", "         ")
                        .aisle(" CBACABC ", "  E F E  ", "  EF FE  ", "  B   B  ", " AC   CA ", "  C   C  ", "  C   C  ", "   C C   ", "    C    ", "         ", "         ", "         ", "         ")
                        .aisle(" CBCGCBC ", "  EF FE  ", "  E   E  ", "  B   B  ", " AC   CA ", "  D   D  ", "  C   C  ", "  C   C  ", "   CGC   ", "    C    ", "    C    ", "    C    ", "    G    ")
                        .aisle(" CBACABC ", "  E F E  ", "  EF FE  ", "  B   B  ", " AC   CA ", "  C   C  ", "  C   C  ", "   C C   ", "    C    ", "         ", "         ", "         ", "         ")
                        .aisle("ACABBBACA", " CAEEEAC ", "  AEEEA  ", "  ABBBA  ", " AACCCAA ", "  CCDCC  ", "   CCC   ", "    C    ", "         ", "         ", "         ", "         ", "         ")
                        .aisle("BCCCSCCCB", " CC   CC ", " C     C ", " C     C ", " DAAAAAD ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
                        .aisle("ABA   ABA", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
                        .self('S', MetaTileEntityIndustrialPureDaisy.class)
                        .block('C', getCasingState3())
                        .block('A', getCasingState())
                        .block('B', getCasingState2())
                        .block('D', getCasingState4())
                        .block('E', getCasingState5())
                        .block('F', getCasingState6())
                        .block('G', getCasingState7())
                        .any(' ');
                DeclarativePatternBuilder.CasingSlot casing = builder.casing('C', getCasingState3());
                return configureManaRecipeCasing(casing, PURE_DAISY_RECIPES, 89).buildStructureDefinition();
            });

	public MetaTileEntityIndustrialPureDaisy(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, PURE_DAISY_RECIPES);
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
		return new MetaTileEntityIndustrialPureDaisy(this.metaTileEntityId);
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
		return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.CHISELED_BRICK);
	}
	protected IBlockState getCasingState2() {
		return MetaBlocks.FRAMES.get(PollutionMaterials.Mansussteel).getBlock(PollutionMaterials.Mansussteel);
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
	protected  ICubeRenderer getFrontOverlay() {
		return Textures.HPCA_OVERLAY;
	}
}
