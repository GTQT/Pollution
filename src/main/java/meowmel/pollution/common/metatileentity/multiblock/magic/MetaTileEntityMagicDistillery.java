package meowmel.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.capability.IDistillationTower;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.DistillationTowerLogicHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.StructurePieceKey;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.IStructureElement;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import meowmel.pollution.api.capability.ipml.MagicMultiblockRecipeLogic;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.pollution.common.block.metablocks.POTurbine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

import static gregtech.api.util.RelativeDirection.*;
import static meowmel.pollution.api.unification.PollutionMaterials.InfusedWater;

public class MetaTileEntityMagicDistillery extends MagicRecipeMapMultiblockController implements IDistillationTower {

    private static final StructurePieceKey BODY_PIECE = StructurePieceKey.of("body");
    private static final StructureDefinition STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:magic_distillery", () -> {
                IStructureElement casing = Elements.counted(40, -1, Elements.block(getCasingState()));
                IStructureElement magicHatches = Elements.abilities(
                        POMultiblockAbility.MANA_INPUT_HATCH,
                        MultiblockAbility.INPUT_ENERGY,
                        MultiblockAbility.MAINTENANCE_HATCH,
                        POMultiblockAbility.VIS_HATCH,
                        POMultiblockAbility.INFUSED_FLUID_HATCH,
                        MultiblockAbility.IMPORT_ITEMS,
                        MultiblockAbility.EXPORT_ITEMS,
                        MultiblockAbility.IMPORT_FLUIDS);

                return DeclarativePatternBuilder.start(RIGHT, FRONT, DOWN)
                        .piece("top")
                        .aisle("#####", "#ZZZ#", "#ZCZ#", "#ZZZ#", "#####")
                        .repeatablePiece("body", 1, 12)
                        .aisle("##X##", "#XAX#", "XAPAX", "#XAX#", "##X##")
                        .piece("base")
                        .aisle("#YSY#", "YAAAY", "YATAY", "YAAAY", "#YYY#")
                        .piece("cap")
                        .aisle("#YYY#", "YYYYY", "YYYYY", "YYYYY", "#YYY#")
                        .self('S', MetaTileEntityMagicDistillery.class)
                        .where('Y', Elements.choice(casing, magicHatches))
                        .where('X', Elements.choice(casing,
                                Elements.abilitiesPerLayer(1, 1, 1, MultiblockAbility.EXPORT_FLUIDS)))
                        .where('Z', casing)
                        .block('P', getCasingState2())
                        .block('A', getCasingState3())
                        .hatch('C', MultiblockAbility.MUFFLER_HATCH)
                        .block('T', getCasingState4())
                        .any('#')
                        .abilityGroup(POMultiblockAbility.MANA_INPUT_HATCH, 1, 2,
                                POMultiblockAbility.MANA_INPUT_HATCH, MultiblockAbility.INPUT_ENERGY)
                        .globalAbilityLimit(POMultiblockAbility.VIS_HATCH, 0, 1)
                        .globalAbilityLimit(POMultiblockAbility.INFUSED_FLUID_HATCH, 1, 1)
                        .buildStructureDefinition();
            });

    protected final DistillationTowerLogicHandler handler;

    public MetaTileEntityMagicDistillery(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.DISTILLATION_RECIPES, RecipeMaps.DISTILLERY_RECIPES});
        this.recipeMapWorkable = new MagicDistilleryRecipeLogic(this);
        this.handler = new DistillationTowerLogicHandler(this);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_COLD);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TITANIUM_PIPE);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.TURBINE.getState(POTurbine.MagicBlockType.TITANIUM_GEARBOX);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicDistillery(this.metaTileEntityId);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.SPELL_PRISM_COLD;
    }

    /**
     * Used if MultiblockPart Abilities need to be sorted a certain way, like
     * Distillation Tower and Assembly Line. <br>
     * <br>
     * There will be <i>consequences</i> if this is changed. Make sure to set the logic handler to one with
     * a properly overriden {@link DistillationTowerLogicHandler#determineOrderedFluidOutputs()}
     */
    @Override
    protected Function<BlockPos, Integer> multiblockPartSorter() {
        return RelativeDirection.UP.getSorter(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
            FluidStack stackInTank = importFluids.drain(Integer.MAX_VALUE, false);
            if (stackInTank != null && stackInTank.amount > 0) {
                ITextComponent fluidName = TextComponentUtil.setColor(GTUtility.getFluidTranslation(stackInTank),
                        TextFormatting.AQUA);
                textList.add(TextComponentUtil.translationWithColor(
                        TextFormatting.GRAY,
                        "gregtech.multiblock.distillation_tower.distilling_fluid",
                        fluidName));
            }
        }
        super.addDisplayText(textList);
    }

    @Override
    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        if (!usesAdvHatchLogic()) return;
        handler.determineLayerCountFromReps(formed.getPieceRepeat(BODY_PIECE, 0));
        handler.determineOrderedFluidOutputs();
    }

    protected boolean usesAdvHatchLogic() {
        return getCurrentRecipeMap() == RecipeMaps.DISTILLATION_RECIPES;
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        if (usesAdvHatchLogic())
            this.handler.invalidate();
    }


    @Override
    public boolean allowSameFluidFillForOutputs() {
        return !usesAdvHatchLogic();
    }


    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public Material getMaterial() {
        return InfusedWater;
    }

    @Override
    public int getFluidOutputLimit() {
        if (usesAdvHatchLogic()) return this.handler.getLayerCount();
        else return super.getFluidOutputLimit();
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }

    private class MagicDistilleryRecipeLogic extends MagicMultiblockRecipeLogic {

        MagicRecipeMapMultiblockController controller;

        public MagicDistilleryRecipeLogic(MagicRecipeMapMultiblockController tileEntity) {
            super(tileEntity);
            controller = tileEntity;
        }

        @Override
        protected void outputRecipeOutputs() {
            GTTransferUtils.addItemsToItemHandler(getOutputInventory(), false, itemOutputs);
            handler.applyFluidToOutputs(fluidOutputs, true);
        }

        @Override
        protected boolean checkOutputSpaceFluids(@NotNull Recipe recipe, @NotNull IMultipleTankHandler exportFluids) {
            // We have already trimmed fluid outputs at this time
            if (!controller.canVoidRecipeFluidOutputs() &&
                    !handler.applyFluidToOutputs(recipe.getAllFluidOutputs(), false)) {
                this.isOutputsFull = true;
                return false;
            }
            return true;
        }

        @Override
        protected IMultipleTankHandler getOutputTank() {
            return handler.getFluidTanks();
        }
    }
}
