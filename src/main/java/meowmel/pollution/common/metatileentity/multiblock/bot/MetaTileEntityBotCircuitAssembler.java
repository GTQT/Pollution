package meowmel.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.casing.ICasing;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
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
import meowmel.pollution.api.metatileentity.ManaMultiblockController;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.api.pattern.POTieredCasingGroups;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POManaPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.NotNull;


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
    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        ICasing frame = POTieredCasingGroups.frames().channel().getMatchedCasing(formed);
        this.frameLevel = frame == null ? 0 : frame.getTier();
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
    protected StructureDefinition<?> createStructureDefinition() {
        DeclarativePatternBuilder builder = DeclarativePatternBuilder.start()
                .aisle("A           A", "A           A", "A           A", "A           A", "A           A", "B           B")
                .aisle(" ACCCCCCCCCA ", " ADDDDDDDDDA ", " AEEEEEEEEEA ", " CCCCCCCCCCC ", "             ", "             ")
                .aisle(" CCCCCCCCCCC ", " DFXFXFXFXFD ", " EXXXXXXXXXE ", " CEEEEGEEEEC ", "             ", "             ")
                .aisle(" CCGGGGGGGCC ", " DFXFXFXFXFD ", " EXXXXXXXXXE ", " CEGGGGGGGEC ", "             ", "             ")
                .aisle(" CCGGGGGGGCC ", " DFBFBFBFBFD ", " EXXXXXXXXXE ", " CGGGGGGGGGC ", "             ", "             ")
                .aisle(" CCGGGGGGGCC ", " DFXFXFXFXFD ", " EXXXXXXXXXE ", " CEGGGGGGGEC ", "             ", "             ")
                .aisle(" CCCCCCCCCCC ", " DFXFXFXFXFD ", " EXXXXXXXXXE ", " CEEEEGEEEEC ", "             ", "             ")
                .aisle(" ACCCCCCCCCA ", " ADDDDSDDDDA ", " AEEEEEEEEEA ", " CCCCCCCCCCC ", "             ", "             ")
                .aisle("A           A", "A           A", "A           A", "A           A", "A           A", "B           B")

                .self('S', MetaTileEntityBotCircuitAssembler.class)
                .block('A', getCasingState()).block('B', getCasingState2()).block('C', getCasingState3())
                .block('D', getCasingState4()).block('E', getCasingState5()).block('F', getCasingState6())
                .tieredCasing('G', POTieredCasingGroups.frames().group()).withChannel(POTieredCasingGroups.frames().channel())
                .any(' ').air('X');
        return builder
                .where('C', Elements.choice(
                        Elements.block(getCasingState3()),
                        Elements.abilities(0, 44, POMultiblockAbility.MANA_INPUT_HATCH,
                                POMultiblockAbility.MANA_INPUT_POOL,
                                MultiblockAbility.INPUT_ENERGY, MultiblockAbility.MAINTENANCE_HATCH,
                                MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.EXPORT_ITEMS,
                                MultiblockAbility.IMPORT_FLUIDS)))
                .abilityGroup(POMultiblockAbility.MANA_INPUT_HATCH, 1, 2,
                        POMultiblockAbility.MANA_INPUT_HATCH, MultiblockAbility.INPUT_ENERGY)
                .globalAbilityLimit(POMultiblockAbility.MANA_INPUT_POOL, 0, 1)
                .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                .buildStructureDefinition();
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

    protected class BotCircuitAssemblerRecipeLogic extends MultiblockRecipeLogic {

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
