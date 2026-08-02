package meowmel.pollution.common.metatileentity.multiblock;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.casing.ICasing;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.IStructureElement;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.MetaBlocks;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.pattern.POTieredCasingGroups;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.api.utils.POUtils;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POBotBlock;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.gtqtcore.api.blocks.impl.WrappedIntTired;
import meowmel.gtqtcore.api.recipes.GTQTRecipeMaps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class MetaTileEntitySmallChemicalPlant extends MultiMapMultiblockController {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:small_chemical_plant", () -> {
                IStructureElement casing = Elements.counted(8, -1, Elements.block(getCasingState()));
                IStructureElement hatches = Elements.abilities(
                        MultiblockAbility.INPUT_ENERGY,
                        MultiblockAbility.MAINTENANCE_HATCH,
                        MultiblockAbility.MUFFLER_HATCH,
                        MultiblockAbility.IMPORT_ITEMS,
                        MultiblockAbility.EXPORT_ITEMS,
                        MultiblockAbility.IMPORT_FLUIDS,
                        MultiblockAbility.EXPORT_FLUIDS);
                return DeclarativePatternBuilder.start()
                    .aisle("GGGGG", "BAAAB", "BAAAB", "BAAAB", "GGGGG")
                    .aisle("GXXXG", "ADDDA", "ABEBA", "ADDDA", "GXXXG")
                    .aisle("GXXXG", "ADDDA", "AECEA", "ADDDA", "GXXXG")
                    .aisle("GXXXG", "ADDDA", "ABEBA", "ADDDA", "GXXXG")
                    .aisle("GGSGG", "BAAAB", "BAAAB", "BAAAB", "GGGGG")
                    .self('S', MetaTileEntitySmallChemicalPlant.class)
                    .where('G', Elements.choice(casing, hatches))
                    .block('B', getCasingState2())
                    .block('C', getCasingState3())
                    .tieredCasing('D', POTieredCasingGroups.coilCasings().group())
                    .withChannel(POTieredCasingGroups.coilCasings().channel())
                    .block('E', getCasingState4())
                    .block('X', getCasingState5())
                    .any('A')
                    .globalAbilityLimit(MultiblockAbility.INPUT_ENERGY, 1, 23)
                    .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                    .globalAbilityLimit(MultiblockAbility.MUFFLER_HATCH, 1, 1)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 1, 23)
                    .globalAbilityLimit(MultiblockAbility.EXPORT_ITEMS, 1, 23)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, 23)
                    .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, 23)
                    .buildStructureDefinition();
            });

    int CoilLevel;

    public MetaTileEntitySmallChemicalPlant(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{
                RecipeMaps.CHEMICAL_RECIPES,
                RecipeMaps.LARGE_CHEMICAL_RECIPES,
                GTQTRecipeMaps.CHEMICAL_PLANT_RECIPES,
                PORecipeMaps.MAGIC_CHEMICAL_REACTOR_RECIPES,
                //GTQTRecipeMaps.FERMENTATION_TANK_RECIPES
        });
        this.recipeMapWorkable = new SmallChemicalPlantRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_WATERTIGHT_CASING);
    }

    private static IBlockState getCasingState2() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.Mansussteel).getBlock(PollutionMaterials.Mansussteel);
    }

    private static IBlockState getCasingState3() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_4);
    }

    private static IBlockState getCasingState4() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.CAMINATED_GLASS);
    }

    private static IBlockState getCasingState5() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_1_CASING);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySmallChemicalPlant(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        ICasing coil = POTieredCasingGroups.coilCasings().channel().getMatchedCasing(formed);
        this.CoilLevel = coil == null ? 0 : coil.getTier();

        List<IEnergyContainer> energyContainer = new ArrayList(this.getAbilities(MultiblockAbility.INPUT_ENERGY));
        this.energyContainer = new EnergyContainerList(energyContainer);
    }

    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.4"));
        tooltip.add(I18n.format("pollution.machine.small.chemical.plant.tooltip.5"));
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom((textList, syncer) -> {
                    textList.add(KeyUtil.lang( "线圈等级：%s", syncer.syncInt(CoilLevel)));
                })
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgress(), recipeMapWorkable.getMaxProgress())
                .addRecipeOutputLine(recipeMapWorkable);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.TERRA_WATERTIGHT_CASING;
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    protected class SmallChemicalPlantRecipeLogic extends MultiblockRecipeLogic {

        public SmallChemicalPlantRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        public int getParallelLimit() {
            if (this.getRecipeMap() == GTQTRecipeMaps.CHEMICAL_PLANT_RECIPES) {
                return (int) Math.max(256, Math.pow(4, CoilLevel));
           // } else
           //     if (this.getRecipeMap() == GTQTcoreRecipeMaps.FERMENTATION_TANK_RECIPES) {
           //     return (int) Math.max(64, Math.pow(4, CoilLevel));
            } else {
                return Math.max(64, 4 * CoilLevel);
            }
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            this.maxProgressTime = (int) (maxProgress * (1 - 0.05 * CoilLevel));
        }

    }
}
