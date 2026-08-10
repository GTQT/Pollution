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
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An open marble cradle that finishes a celestial crystal embryo under a
 * live constellation. The exact lens-hatch position is deliberately exposed
 * to the sky, preventing enclosed factory builds from bypassing astronomy.
 */
public class MetaTileEntityCelestialCrystalGrowthArray extends MagicRecipeMapMultiblockController {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:celestial_crystal_growth_array", () -> DeclarativePatternBuilder.start()
                    // Vertical 7x7x7 Astral capsule. Its rounded horizontal
                    // section is formed by progressively wider marble rings;
                    // runes form the stabilising band, glass is limited to
                    // small viewing ports, and a translucent core suspends
                    // the crystal beneath the roof-mounted lens hatch.
                    .aisle("##CCC##", "##CRC##", "##C#C##", "##C#C##", "##C#C##", "##CGC##", "##CCC##")
                    .aisle("#CCCCC#", "#C###C#", "#C#G#C#", "#C###C#", "#C#G#C#", "#C###C#", "#CCCCC#")
                    .aisle("CCCCCCC", "C#####C", "C#R#R#C", "C##T##C", "C#R#R#C", "C#####C", "CCCCCCC")
                    .aisle("CCCCCCC", "C#R#R#C", "C##T##C", "C##T##C", "C##T##C", "C#R#R#C", "CCCLCCC")
                    .aisle("CCCCCCC", "C#####C", "C#R#R#C", "C##T##C", "C#R#R#C", "C#####C", "CCCCCCC")
                    .aisle("#CCCCC#", "#C###C#", "#C#G#C#", "#C###C#", "#C#G#C#", "#C###C#", "#CCCCC#")
                    .aisle("##CCC##", "##CRC##", "##C#C##", "##CSC##", "##C#C##", "##CGC##", "##CCC##")
                    .self('S', MetaTileEntityCelestialCrystalGrowthArray.class)
                    .where('C', Elements.choice(Elements.block(marble(BlockMarble.MarbleBlockType.BRICKS)),
                            Elements.abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.IMPORT_ITEMS,
                                    MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS,
                                    MultiblockAbility.MAINTENANCE_HATCH, POMultiblockAbility.VIS_HATCH,
                                    POMultiblockAbility.INFUSED_FLUID_HATCH, POMultiblockAbility.MANA_INPUT_POOL,
                                    POMultiblockAbility.TAROT_HATCH)))
                    .block('R', marble(BlockMarble.MarbleBlockType.RUNED))
                    .block('G', PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.LAMINATED_GLASS))
                    .block('T', BlocksAS.translucentBlock.getDefaultState())
                    .where('L', Elements.abilities(1, 1, POMultiblockAbility.ASTRAL_LENS_HATCH))
                    .air('#')
                    .globalAbilityLimit(MultiblockAbility.INPUT_ENERGY, 1, 2)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 1, 2)
                    .globalAbilityLimit(MultiblockAbility.EXPORT_ITEMS, 1, 2)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, 2)
                    .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                    .globalAbilityLimit(POMultiblockAbility.VIS_HATCH, 1, 1)
                    .globalAbilityLimit(POMultiblockAbility.INFUSED_FLUID_HATCH, 1, 1)
                    .globalAbilityLimit(POMultiblockAbility.MANA_INPUT_POOL, 1, 1)
                    .globalAbilityLimit(POMultiblockAbility.ASTRAL_LENS_HATCH, 1, 1)
                    .globalAbilityLimit(POMultiblockAbility.TAROT_HATCH, 0, 1)
                    .buildStructureDefinition());

    public MetaTileEntityCelestialCrystalGrowthArray(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PORecipeMaps.CELESTIAL_CRYSTAL_GROWTH_RECIPES);
    }

    private static IBlockState marble(BlockMarble.MarbleBlockType type) {
        return BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, type);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityCelestialCrystalGrowthArray(metaTileEntityId);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

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
        return PollutionMaterials.InfusedSense;
    }

    @Override
    public boolean checkMagicRequirements(Recipe recipe) {
        return super.checkMagicRequirements(recipe) && astralLensHatch != null
                && astralLensHatch.getTier() >= GTValues.LuV
                && astralLensHatch.hasConstellationDataWafer()
                && astralLensHatch.isSkyVisible()
                && astralLensHatch.isNight()
                && astralLensHatch.isFocusedConstellationActive();
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        super.configureWarningText(builder);
        builder.addCustom((manager, syncer) -> {
            if (syncer.syncBoolean(astralLensHatch == null || astralLensHatch.getTier() < GTValues.LuV)) {
                manager.add(KeyUtil.string(TextFormatting.RED, "缺少 LuV 级高级星辉透镜仓"));
            } else if (syncer.syncBoolean(!astralLensHatch.hasConstellationDataWafer())) {
                manager.add(KeyUtil.string(TextFormatting.RED, "高级星辉透镜仓必须装入星座数据晶圆"));
            } else if (syncer.syncBoolean(!astralLensHatch.isSkyVisible())) {
                manager.add(KeyUtil.string(TextFormatting.RED, "星辉透镜仓上方被遮挡"));
            } else if (syncer.syncBoolean(!astralLensHatch.isNight())) {
                manager.add(KeyUtil.string(TextFormatting.YELLOW, "天体晶体只能在夜间生长"));
            } else if (syncer.syncBoolean(!astralLensHatch.isFocusedConstellationActive())) {
                manager.add(KeyUtil.string(TextFormatting.YELLOW, "晶圆对应星座当前未活跃"));
            }
        });
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.celestial_crystal_growth_array.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.celestial_crystal_growth_array.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.celestial_crystal_growth_array.tooltip.3"));
    }
}
