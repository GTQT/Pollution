package meowmel.pollution.common.metatileentity.multiblock.astral;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.Recipe;
import gregtech.api.unification.material.Material;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Binds a celestial calibration core to the wafer, hatch and live sky constellation. */
public class MetaTileEntityCelestialCalibrationMatrix extends MagicRecipeMapMultiblockController {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:celestial_calibration_matrix", () -> DeclarativePatternBuilder.start()
                    // 9x7x9 four-pylon calibration array. The open interior is part of the
                    // structure rather than decoration: every '#' must be air, and L is the
                    // unique roof aperture for the mandatory advanced astral lens hatch.
                    .aisle("#CCCSCCC#", "##CCCCC##", "###RRR###", "####R####", "####R####", "####C####", "#########")
                    .aisle("C#######C", "#P#####P#", "##R###R##", "###P#P###", "###R#R###", "###C#C###", "#########")
                    .aisle("C#######C", "##P###P##", "#R#####R#", "##R###R##", "##R###R##", "##C###C##", "#########")
                    .aisle("C#######C", "###AAA###", "R##A#A##R", "###AAA###", "R##A#A##R", "###C#C###", "#########")
                    .aisle("C###A###C", "####P####", "R##A#A##R", "###P#P###", "R##A#A##R", "####C####", "####L####")
                    .aisle("C#######C", "###AAA###", "R##A#A##R", "###AAA###", "R##A#A##R", "###C#C###", "#########")
                    .aisle("C#######C", "##P###P##", "#R#####R#", "##R###R##", "##R###R##", "##C###C##", "#########")
                    .aisle("C#######C", "#P#####P#", "##R###R##", "###P#P###", "###R#R###", "###C#C###", "#########")
                    .aisle("#CCCCCCC#", "##CCCCC##", "###RRR###", "####R####", "####R####", "####C####", "#########")
                    .self('S', MetaTileEntityCelestialCalibrationMatrix.class)
                    .where('C', Elements.choice(Elements.block(marble(BlockMarble.MarbleBlockType.BRICKS)),
                            Elements.abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.IMPORT_ITEMS,
                                    MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS,
                                    MultiblockAbility.MAINTENANCE_HATCH,
                                    POMultiblockAbility.TAROT_HATCH)))
                    .block('R', marble(BlockMarble.MarbleBlockType.RUNED))
                    .block('P', marble(BlockMarble.MarbleBlockType.PILLAR))
                    .block('A', marble(BlockMarble.MarbleBlockType.ARCH))
                    .where('L', Elements.abilities(1, 1, POMultiblockAbility.ASTRAL_LENS_HATCH))
                    .air('#')
                    .globalAbilityLimit(MultiblockAbility.INPUT_ENERGY, 1, 2)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 1, 2)
                    .globalAbilityLimit(MultiblockAbility.EXPORT_ITEMS, 1, 2)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, 2)
                    .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                    .globalAbilityLimit(POMultiblockAbility.ASTRAL_LENS_HATCH, 1, 1)
                    .globalAbilityLimit(POMultiblockAbility.TAROT_HATCH, 0, 1)
                    .buildStructureDefinition());

    public MetaTileEntityCelestialCalibrationMatrix(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PORecipeMaps.CELESTIAL_CALIBRATION_RECIPES);
    }

    private static IBlockState marble(BlockMarble.MarbleBlockType type) {
        return BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, type);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityCelestialCalibrationMatrix(metaTileEntityId);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    /**
     * The V3 structure preview is assembled in a south-facing canonical frame.
     * State this explicitly so the controller overlay faces the observer instead
     * of inheriting the unplaced MTE's default north-facing direction.
     */
    @Override
    public EnumFacing getPreviewFrontFacing() {
        return EnumFacing.SOUTH;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return POTextures.ASTRAL_MARBLE;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public Material getMaterial() {
        return PollutionMaterials.InfusedOrder;
    }

    @Override
    public boolean checkMagicRequirements(Recipe recipe) {
        return super.checkMagicRequirements(recipe) && astralLensHatch != null
                && astralLensHatch.getTier() >= GTValues.LuV;
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        builder.addCustom((manager, syncer) -> {
            if (syncer.syncBoolean(astralLensHatch == null || astralLensHatch.getTier() < GTValues.LuV)) {
                manager.add(KeyUtil.string(TextFormatting.RED, "缺少高级星辉透镜仓"));
            } else if (syncer.syncBoolean(!astralLensHatch.isSkyVisible())) {
                manager.add(KeyUtil.string(TextFormatting.RED, "高级星辉透镜仓上方被遮挡"));
            } else if (syncer.syncBoolean(astralLensHatch.getFocusedConstellation().isEmpty())) {
                manager.add(KeyUtil.string(TextFormatting.RED, "未插入有效星座数据"));
            } else if (syncer.syncBoolean(!astralLensHatch.isFocusedConstellationActive())) {
                manager.add(KeyUtil.string(TextFormatting.YELLOW, "晶圆星座与当前天空不具备校准条件"));
            }
        });
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.celestial_calibration_matrix.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.celestial_calibration_matrix.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.celestial_calibration_matrix.tooltip.3"));
    }
}
