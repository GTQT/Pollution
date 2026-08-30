package meowmel.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.logic.OCResult;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import gregtech.api.unification.material.Material;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;

import meowmel.pollution.api.capability.ipml.MagicMultiblockRecipeLogic;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.api.pattern.POTieredCasingGroups;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POBotBlock;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POManaPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static meowmel.pollution.api.unification.PollutionMaterials.InfusedCraft;

public class MetaTileEntityMagicAssembler extends MagicRecipeMapMultiblockController {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:magic_assembler", () -> configureMagicRecipeCasing(
                    DeclarativePatternBuilder.start(RelativeDirection.FRONT, RelativeDirection.UP, RelativeDirection.RIGHT)
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
                            .self('S', MetaTileEntityMagicAssembler.class),
                    'A', getCasingState(),
                    new RecipeMap<?>[]{RecipeMaps.ASSEMBLER_RECIPES,
                            PORecipeMaps.MAGIC_ASSEMBLER_RECIPES}, 94)
                    .block('B', getCasingState2())
                    .block('C', getCasingState3())
                    .block('D', getCasingState4())
                    .block('E', getCasingState5())
                    .tieredCasing('F', POTieredCasingGroups.beamCores().group())
                    .withChannel(POTieredCasingGroups.beamCores().channel())
                    .any(' ')
                    .buildStructureDefinition());

    //BEAM方块等级
    int BeamLevel;

    public MetaTileEntityMagicAssembler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.ASSEMBLER_RECIPES, PORecipeMaps.MAGIC_ASSEMBLER_RECIPES});
        this.recipeMapWorkable = new MagicAssemblerRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_BASIC);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING);
    }

    private static IBlockState getCasingState3() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.HyperdimensionalSilver).getBlock(PollutionMaterials.HyperdimensionalSilver);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.LAMINATED_GLASS);
    }

    private static IBlockState getCasingState5() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.KQGold).getBlock(PollutionMaterials.KQGold);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicAssembler(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        var casing = POTieredCasingGroups.beamCores().channel().getMatchedCasing(formed);
        this.BeamLevel = casing == null ? 0 : casing.getTier();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.BeamLevel = 0;
    }

    @Override
    public Material getMaterial() {
        return InfusedCraft;
    }

    //tooltip
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.magic_assembler.tooltip.1"));
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.MANA_BASIC;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class MagicAssemblerRecipeLogic extends MagicMultiblockRecipeLogic {

        public MagicAssemblerRecipeLogic(MagicRecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }


        @Override
        protected void modifyOverclockPost(@NotNull OCResult ocResult, @NotNull RecipePropertyStorage storage) {
            super.modifyOverclockPost(ocResult, storage);

            ocResult.setDuration((int) Math.round(ocResult.duration() * (1 - 0.05 * BeamLevel)));
        }
    }
}
