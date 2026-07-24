package meowmel.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.core.sound.GTSoundEvents;
import meowmel.pollution.api.metatileentity.ManaMultiblockController;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
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

import static meowmel.pollution.api.recipes.PORecipeMaps.MANA_INFUSION_RECIPES;

public class MetaTileEntityManaInfusionReactor extends ManaMultiblockController {

    private final StructureDefinition STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:mana_infusion_reactor", () -> {
                DeclarativePatternBuilder builder = DeclarativePatternBuilder.start()
                        .aisle(" ABBBBBBBA ", "    C C    ", "    D D    ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("ACEEEEEEECA", " C       C ", " C       C ", " C       C ", " C       C ", " C       C ", " D       D ", " F       F ", " G       G ")
                        .aisle("BEHHHHHHHEB", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("BEHAAAAAHEB", "     F     ", "     G     ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("BEHAAAAAHEB", "C         C", "D         D", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("BEHAAAAAHEB", "   F A F   ", "   G A G   ", "     A     ", "     F     ", "     G     ", "           ", "           ", "           ")
                        .aisle("BEHAAAAAHEB", "C         C", "D         D", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("BEHAAAAAHEB", "     F     ", "     G     ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("BEHHHHHHHEB", "           ", "           ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .aisle("ACEEEEEEECA", " C       C ", " C       C ", " C       C ", " C       C ", " C       C ", " D       D ", " F       F ", " G       G ")
                        .aisle(" ABBBSBBBA ", "    C C    ", "    D D    ", "           ", "           ", "           ", "           ", "           ", "           ")
                        .self('S', MetaTileEntityManaInfusionReactor.class)
                        .block('A', getCasingState())
                        .block('B', getCasingState2())
                        .block('C', getCasingState3())
                        .block('D', getCasingState4())
                        .block('E', getCasingState5())
                        .block('F', getCasingState6())
                        .block('G', getCasingState7())
                        .block('H', getCasingState8())
                        .any(' ');
                DeclarativePatternBuilder.CasingSlot casing = builder.casing('B', getCasingState2());
                return configureManaRecipeCasing(casing, MANA_INFUSION_RECIPES, 18).buildStructureDefinition();
            });

    public MetaTileEntityManaInfusionReactor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, MANA_INFUSION_RECIPES);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityManaInfusionReactor(this.metaTileEntityId);
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
        return BlocksTC.stoneArcaneBrick.getDefaultState();
    }
    protected IBlockState getCasingState2() {
        return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.BRICK);
    }
    protected IBlockState getCasingState3() {
        return ModBlocks.livingrock.getDefaultState().withProperty(BotaniaStateProps.LIVINGROCK_VARIANT, LivingRockVariant.CHISELED_BRICK);
    }
    protected IBlockState getCasingState4() {
        return BlocksTC.infusionMatrix.getDefaultState();
    }
    protected IBlockState getCasingState5() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.LAMINATED_GLASS);
    }
    protected IBlockState getCasingState6() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }
    protected IBlockState getCasingState7() {
        return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
    }
    protected IBlockState getCasingState8() {
        return ModBlocks.livingwood.getDefaultState().withProperty(BotaniaStateProps.LIVINGWOOD_VARIANT, LivingWoodVariant.PLANKS);
    }

    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @SideOnly(Side.CLIENT)
    protected  ICubeRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }
}
