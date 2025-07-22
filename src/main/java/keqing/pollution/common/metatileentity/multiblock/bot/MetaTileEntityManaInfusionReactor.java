package keqing.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.core.sound.GTSoundEvents;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import keqing.pollution.api.metatileentity.POManaMultiblock;
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

import static keqing.pollution.api.recipes.PORecipeMaps.MANA_INFUSION_RECIPES;

public class MetaTileEntityManaInfusionReactor extends POManaMultiblock {

    public MetaTileEntityManaInfusionReactor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, MANA_INFUSION_RECIPES);
        this.recipeMapWorkable = new POManaMultiblockRecipeLogic(this);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityManaInfusionReactor(this.metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
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
                .where('S', selfPredicate())
                .where(' ', any())
                .where('A', states(getCasingState()))
                .where('B', states(getCasingState2()).setMinGlobalLimited(9).or(autoAbilities()))
                .where('C', states(getCasingState3()))
                .where('D', states(getCasingState4()))
                .where('E', states(getCasingState5()))
                .where('F', states(getCasingState6()))
                .where('G', states(getCasingState7()))
                .where('H', states(getCasingState8()))
                .build();
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
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }
}