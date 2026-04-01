package meowmel.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.logic.OCResult;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockCleanroomCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import meowmel.pollution.api.capability.ipml.ManaMultiblockRecipeLogic;
import meowmel.pollution.api.metatileentity.ManaMultiblockController;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.api.utils.POUtils;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POManaPlate;
import meowmel.gtqtcore.api.blocks.impl.WrappedIntTired;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static meowmel.pollution.api.predicate.TiredTraceabilityPredicate.CP_FRAME;

public class MetaTileEntityBotCircuitAssembler extends ManaMultiblockController {

    private int frameLevel;

    public MetaTileEntityBotCircuitAssembler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.CIRCUIT_ASSEMBLER_RECIPES);
        this.recipeMapWorkable = new BotCircuitAssemblerRecipeLogic(this);
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
        return MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING);
    }

    private static IBlockState getCasingState5() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
    }

    private static IBlockState getCasingState6() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.DAMINATED_GLASS);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object frameLevel = context.get("FRAMETieredStats");
        this.frameLevel = POUtils.getOrDefault(() -> frameLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) frameLevel).getIntTier(),
                0);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("frameLevel", this.frameLevel);
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        this.frameLevel = data.getInteger("frameLevel");
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityBotCircuitAssembler(this.metaTileEntityId);
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
                        .or(autoAbilities()))
                .where('D', states(getCasingState4()))
                .where('E', states(getCasingState5()))
                .where('F', states(getCasingState6()))
                .where('G', CP_FRAME.get())
                .where(' ', any())
                .where('X', air())
                .build();
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
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    protected class BotCircuitAssemblerRecipeLogic extends ManaMultiblockRecipeLogic {

        public BotCircuitAssemblerRecipeLogic(ManaMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        protected void modifyOverclockPost(@NotNull OCResult ocResult, @NotNull RecipePropertyStorage storage) {
            super.modifyOverclockPost(ocResult, storage);

            ocResult.setDuration(Math.round((float) (ocResult.duration() * (10.0 - frameLevel) / 10)));
        }
    }
}
