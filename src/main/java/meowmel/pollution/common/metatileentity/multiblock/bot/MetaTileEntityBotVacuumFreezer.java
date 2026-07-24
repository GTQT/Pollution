package meowmel.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.logic.OCResult;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import meowmel.gtqtcore.api.blocks.impl.IBlockTier;
import meowmel.gtqtcore.api.blocks.impl.WrappedTired;
import meowmel.gtqtcore.api.predicate.TiredTraceabilityPredicate;
import meowmel.gtqtcore.common.blocks.BlockCoolingCoil;
import meowmel.gtqtcore.common.blocks.BlockMachineCasing;
import meowmel.gtqtcore.common.blocks.GTQTMetaBlocks;
import meowmel.pollution.api.metatileentity.ManaMultiblockController;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POBotBlock;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.block.metablocks.POManaPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;

public class MetaTileEntityBotVacuumFreezer extends ManaMultiblockController {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:bot_vacuum_freezer", () -> {
                DeclarativePatternBuilder builder = DeclarativePatternBuilder.start()
                        .aisle("      AXXXA      ", "       XXX       ", "        X        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                        .aisle("     BABBBAB     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                        .aisle("    BBBAAABBB    ", "    CCC   CCC    ", "    DED   DED    ", "     D     D     ", "                 ", "     F     F     ", "                 ", "                 ", "                 ")
                        .aisle("     B AAA B     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                        .aisle("       GAG       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                        .aisle("      GBBBG      ", "       DHD       ", "       DHD       ", "       DHD       ", "       DHD       ", "       GGG       ", "       ADA       ", "       GGG       ", "                 ")
                        .aisle("     GBBCBBG     ", "      DE ED      ", "      DE ED      ", "      DE ED      ", "      DEBED      ", "      GGBGG      ", "      A   A      ", "      GGCGG      ", "       GGG       ")
                        .aisle(" BAAGBBCCCBBGAAB ", " C   DE I ED   C ", " D   DE J ED   D ", "     DE   ED     ", "     DE   ED     ", "     GGBBBGG     ", "     A K K A     ", "     GGCCCGG     ", "      GGHGG      ")
                        .aisle("BBBAABCCCCCBAABBB", "CCC  H IEI H  CCC", "DED  H JEJ H  DED", " D   H  E  H   D ", "     HB E BH     ", " F   GBBBBBG   F ", "     D  K  D     ", "     GCCCCCG     ", "      GHHHG      ")
                        .aisle(" BAAGBBCCCBBGAAB ", " C   DE I ED   C ", " D   DE J ED   D ", "     DE   ED     ", "     DE   ED     ", "     GGBBBGG     ", "     A K K A     ", "     GGCCCGG     ", "      GGHGG      ")
                        .aisle("     GBBCBBG     ", "      DE ED      ", "      DE ED      ", "      DE ED      ", "      DEBED      ", "      GGBGG      ", "      A   A      ", "      GGCGG      ", "       GGG       ")
                        .aisle("      GBBBG      ", "       DHD       ", "       DHD       ", "       DHD       ", "       DHD       ", "       GGG       ", "       ADA       ", "       GGG       ", "                 ")
                        .aisle("       GAG       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                        .aisle("     B AAA B     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                        .aisle("    BBBAAABBB    ", "    CCC   CCC    ", "    DED   DED    ", "     D     D     ", "                 ", "     F     F     ", "                 ", "                 ", "                 ")
                        .aisle("     BABBBAB     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                        .aisle("      AXXXA      ", "       XSX       ", "        X        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                        .self('S', MetaTileEntityBotVacuumFreezer.class)
                        .block('A', getCasingState())
                        .block('B', getCasingState2())
                        .block('C', getCasingState3())
                        .block('D', getCasingState4())
                        .block('E', getCasingState5())
                        .block('F', getCasingState7())
                        .block('G', getCasingState6())
                        .where('H', TiredTraceabilityPredicate.element(TiredTraceabilityPredicate.coolingCoils()))
                        .block('I', Blocks.DIRT.getDefaultState())
                        .block('J', ModBlocks.floatingFlower.getDefaultState())
                        .block('K', PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4))
                        .any(' ');
                return builder
                        .casing('X', getCasingState2())
                        .custom(Elements.abilities(0, 13,
                                meowmel.pollution.api.metatileentity.POMultiblockAbility.MANA_INPUT_HATCH,
                                MultiblockAbility.INPUT_ENERGY, MultiblockAbility.MAINTENANCE_HATCH,
                                MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.EXPORT_ITEMS,
                                MultiblockAbility.IMPORT_FLUIDS, MultiblockAbility.EXPORT_FLUIDS), 13)
                        .done()
                        .abilityGroup(meowmel.pollution.api.metatileentity.POMultiblockAbility.MANA_INPUT_HATCH, 1, 2,
                                meowmel.pollution.api.metatileentity.POMultiblockAbility.MANA_INPUT_HATCH,
                                MultiblockAbility.INPUT_ENERGY)
                        .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                        .buildStructureDefinition();
            });

    int temperature;

    public MetaTileEntityBotVacuumFreezer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.VACUUM_RECIPES);
        this.recipeMapWorkable = new BotVacuumFreezerRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.hyperdimensional_silver).getBlock(PollutionMaterials.hyperdimensional_silver);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_4);
    }

    private static IBlockState getCasingState3() {
        return GTQTMetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.CRYOGENIC_CASING);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
    }

    private static IBlockState getCasingState5() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_2);
    }

    private static IBlockState getCasingState6() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_1_CASING);
    }

    private static IBlockState getCasingState7() {
        return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityBotVacuumFreezer(this.metaTileEntityId);
    }

    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        IBlockTier type = TiredTraceabilityPredicate.getTier(formed, TiredTraceabilityPredicate.coolingCoils());
        if (type instanceof WrappedTired &&
                ((WrappedTired) type).getInner() instanceof BlockCoolingCoil.CoolingCoilType) {
            this.temperature = ((BlockCoolingCoil.CoolingCoilType) ((WrappedTired) type).getInner())
                    .getCoilTemperature();
        } else {
            this.temperature = BlockCoolingCoil.CoolingCoilType.MANGANESE_IRON_ARSENIC_PHOSPHIDE.getCoilTemperature();
        }
    }

    public int getCoilTier() {
        if (temperature == 160) return 1;
        if (temperature == 50) return 2;
        if (temperature == 1) return 3;
        return 0;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("temperature", this.temperature);
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        this.temperature = data.getInteger("temperature");
    }

    @Override
    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {
        if (isStructureFormed()) {
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "磁致冷线圈温度: %s K", syncer.syncInt(temperature)));
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "磁致冷线圈等级: %s K", syncer.syncInt(getCoilTier())));
        }

    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
        /*
        return FactoryBlockPattern.start()
                .aisle("      AXXXA      ", "       XXX       ", "        X        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                .aisle("     BABBBAB     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                .aisle("    BBBAAABBB    ", "    CCC   CCC    ", "    DED   DED    ", "     D     D     ", "                 ", "     F     F     ", "                 ", "                 ", "                 ")
                .aisle("     B AAA B     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                .aisle("       GAG       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                .aisle("      GBBBG      ", "       DHD       ", "       DHD       ", "       DHD       ", "       DHD       ", "       GGG       ", "       ADA       ", "       GGG       ", "                 ")
                .aisle("     GBBCBBG     ", "      DE ED      ", "      DE ED      ", "      DE ED      ", "      DEBED      ", "      GGBGG      ", "      A   A      ", "      GGCGG      ", "       GGG       ")
                .aisle(" BAAGBBCCCBBGAAB ", " C   DE I ED   C ", " D   DE J ED   D ", "     DE   ED     ", "     DE   ED     ", "     GGBBBGG     ", "     A K K A     ", "     GGCCCGG     ", "      GGHGG      ")
                .aisle("BBBAABCCCCCBAABBB", "CCC  H IEI H  CCC", "DED  H JEJ H  DED", " D   H  E  H   D ", "     HB E BH     ", " F   GBBBBBG   F ", "     D  K  D     ", "     GCCCCCG     ", "      GHHHG      ")
                .aisle(" BAAGBBCCCBBGAAB ", " C   DE I ED   C ", " D   DE J ED   D ", "     DE   ED     ", "     DE   ED     ", "     GGBBBGG     ", "     A K K A     ", "     GGCCCGG     ", "      GGHGG      ")
                .aisle("     GBBCBBG     ", "      DE ED      ", "      DE ED      ", "      DE ED      ", "      DEBED      ", "      GGBGG      ", "      A   A      ", "      GGCGG      ", "       GGG       ")
                .aisle("      GBBBG      ", "       DHD       ", "       DHD       ", "       DHD       ", "       DHD       ", "       GGG       ", "       ADA       ", "       GGG       ", "                 ")
                .aisle("       GAG       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                .aisle("     B AAA B     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                .aisle("    BBBAAABBB    ", "    CCC   CCC    ", "    DED   DED    ", "     D     D     ", "                 ", "     F     F     ", "                 ", "                 ", "                 ")
                .aisle("     BABBBAB     ", "     C     C     ", "     D     D     ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                .aisle("      AXXXA      ", "       XSX       ", "        X        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")

                .where('S', selfPredicate())
                .where(' ', any())
                .where('A', states(getCasingState()))
                .where('X', states(getCasingState2())
                        .or(autoAbilities()))
                .where('B', states(getCasingState2()))
                .where('C', states(getCasingState3()))
                .where('D', states(getCasingState4()))
                .where('E', states(getCasingState5()))
                .where('G', states(getCasingState6()))
                .where('F', states(getCasingState7()))
                .where('H', TiredTraceabilityPredicate.CP_COOLING_COIL.get())
                .where('I', states(getCasingState8()))
                .where('J', states(getCasingState9()))
                .where('K', states(getCasingState10()))
                .build();*/
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return true;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    protected IBlockState getCasingState8() {
        assert Blocks.DIRT != null;
        return Blocks.DIRT.getDefaultState();
    }

    protected IBlockState getCasingState9() {
        return ModBlocks.floatingFlower.getDefaultState();
    }

    protected IBlockState getCasingState10() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.MANA_4;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    protected class BotVacuumFreezerRecipeLogic extends MultiblockRecipeLogic {

        public BotVacuumFreezerRecipeLogic(ManaMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        protected void modifyOverclockPost(@NotNull OCResult ocResult, @NotNull RecipePropertyStorage storage) {
            super.modifyOverclockPost(ocResult, storage);

            ocResult.setDuration(Math.round((float) (ocResult.duration() * (10 - 2.0 * getCoilTier()) / 10)));
        }
    }
}
