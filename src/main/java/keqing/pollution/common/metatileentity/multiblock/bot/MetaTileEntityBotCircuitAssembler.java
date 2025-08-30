package keqing.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
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
import keqing.pollution.api.metatileentity.POManaMultiblockWithElectric;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.api.utils.POUtils;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.block.metablocks.POManaPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import static keqing.gtqtcore.common.block.GTQTMetaBlocks.blockStepperCasing;
import static keqing.gtqtcore.common.block.blocks.BlockStepperCasing.CasingType.CLEAN_MKV;
import static keqing.pollution.api.predicate.TiredTraceabilityPredicate.CP_FRAME;

public class MetaTileEntityBotCircuitAssembler extends POManaMultiblockWithElectric {

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
        return blockStepperCasing.getState(CLEAN_MKV);
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
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    protected class BotCircuitAssemblerRecipeLogic extends POManaMultiblockWithElectricRecipeLogic {

        public BotCircuitAssemblerRecipeLogic(POManaMultiblockWithElectric tileEntity) {
            super(tileEntity);
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            super.setMaxProgress((int) (maxProgress * (10.0 - frameLevel) / 10));
        }
    }
}
