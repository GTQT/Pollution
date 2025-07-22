package keqing.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.metatileentity.PORecipeMapMultiblockController;
import keqing.pollution.api.recipes.PORecipeMaps;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POBotBlock;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POManaPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_BEAM_CORE;
import static keqing.pollution.api.unification.PollutionMaterials.infused_craft;

public class MetaTileEntityMagicAssembler extends PORecipeMapMultiblockController {

    //BEAM方块等级
    int BeamLevel;

    public MetaTileEntityMagicAssembler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.ASSEMBLER_RECIPES, PORecipeMaps.MAGIC_ASSEMBLER_RECIPES});
        this.recipeMapWorkable = new MagicAssemblerRecipeLogic(this);
        setMaterial(infused_craft);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_BASIC);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING);
    }

    private static IBlockState getCasingState3() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.hyperdimensional_silver).getBlock(PollutionMaterials.hyperdimensional_silver);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.LAMINATED_GLASS);
    }

    private static IBlockState getCasingState5() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.keqinggold).getBlock(PollutionMaterials.keqinggold);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicAssembler(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object BeamLevel = context.get("BEAMTieredStats");
        this.BeamLevel = POUtils.getOrDefault(() -> BeamLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) BeamLevel).getIntTier(),
                0);
    }

    //tooltip
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.magic_assembler.tooltip.1"));
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.FRONT, RelativeDirection.UP, RelativeDirection.RIGHT)
                .aisle(" ABABA ", "  CDC  ", "  CDC  ", "  EDE  ", "  EDE  ", "  AAA  ")
                .aisle("AAAAAAA", " D   D ", " D   D ", " D   D ", " DBBBD ", " AAAAA ")
                .aisle("AAABAAA", "C F F C", "C F F C", "E F F E", "EDB BDE", "AAAAAAA")
                .aisle("AAABAAA", "D     D", "D     D", "D     D", "DDB BDD", "AAAAAAA")
                .aisle("AAABAAA", "D F F D", "D F F D", "D F F D", "DBB BBD", "AAAAAAA")
                .aisle("SABBBAA", "D     D", "D     D", "D     D", "DB   BD", "AAAAAAA")
                .aisle("AAABAAA", "D F F D", "D F F D", "D F F D", "DBB BBD", "AAAAAAA")
                .aisle("AAABAAA", "D     D", "D     D", "D     D", "DDB BDD", "AAAAAAA")
                .aisle("AAABAAA", "C F F C", "C F F C", "E F F E", "EDB BDE", "AAAAAAA")
                .aisle("AAAAAAA", " D   D ", " D   D ", " D   D ", " DBBBD ", " AAAAA ")
                .aisle(" ABABA ", "  CDC  ", "  CDC  ", "  EDE  ", "  EDE  ", "  AAA  ")
                .where('S', selfPredicate())
                .where('A', states(getCasingState()).setMinGlobalLimited(30).or(autoAbilities()))
                .where('B', states(getCasingState2()))
                .where('C', states(getCasingState3()))
                .where('D', states(getCasingState4()))
                .where('E', states(getCasingState5()))
                .where('F', CP_BEAM_CORE.get())
                .where(' ', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.MANA_BASIC;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class MagicAssemblerRecipeLogic extends POMultiblockRecipeLogic {

        public MagicAssemblerRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        //每级-5%最大配方时长
        public void setMaxProgress(int maxProgress) {
            this.maxProgressTime = (int) (Math.round(maxProgress * (1 - 0.05 * BeamLevel)));
        }

    }
}