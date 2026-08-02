package meowmel.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.recipes.GCYMRecipeMaps;
import gregtech.api.capability.IHeatingCoil;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.properties.impl.TemperatureProperty;
import gregtech.api.unification.material.Material;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.core.sound.GTSoundEvents;
import meowmel.pollution.api.capability.ipml.MagicHeatingCoilRecipeLogic;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.api.pattern.POTieredCasingGroups;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static meowmel.pollution.api.unification.PollutionMaterials.InfusedFire;

public class MetaTileEntityMagicAlloyBlastSmelter extends MagicRecipeMapMultiblockController implements IHeatingCoil {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:magic_alloy_blast_smelter", () -> configureMagicRecipeCasing(
                    DeclarativePatternBuilder.start()
                            .aisle("#XXX#", "#CCC#", "#GGG#", "#CCC#", "#XXX#")
                            .aisle("XXXXX", "CAAAC", "GAAAG", "CAAAC", "XXXXX")
                            .aisle("XXXXX", "CAAAC", "GAAAG", "CAAAC", "XXMXX")
                            .aisle("XXXXX", "CAAAC", "GAAAG", "CAAAC", "XXXXX")
                            .aisle("#XSX#", "#CCC#", "#GGG#", "#CCC#", "#XXX#")
                            .self('S', MetaTileEntityMagicAlloyBlastSmelter.class),
                    'X', getCasingState1(),
                    new RecipeMap<?>[]{PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES,
                            GCYMRecipeMaps.ALLOY_BLAST_RECIPES}, 31)
                    .hatch('M', MultiblockAbility.MUFFLER_HATCH)
                    .tieredCasing('C', POTieredCasingGroups.coilCasings().group())
                    .withChannel(POTieredCasingGroups.coilCasings().channel())
                    .block('G', getCasingState2())
                    .any('#')
                    .air('A')
                    .buildStructureDefinition());

    private int blastFurnaceTemperature;

    public MetaTileEntityMagicAlloyBlastSmelter(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{PORecipeMaps.MAGIC_ALLOY_BLAST_RECIPES, GCYMRecipeMaps.ALLOY_BLAST_RECIPES});
        this.recipeMapWorkable = new MagicHeatingCoilRecipeLogic(this);
    }

    private static IBlockState getCasingState1() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT);
    }

    private static IBlockState getCasingState2() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.ALLOY_BLAST_CASING);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicAlloyBlastSmelter(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(FormedStructureView formed) {
        super.formStructure(formed);
        var casing = POTieredCasingGroups.coilCasings().channel().getMatchedCasing(formed);
        int coilTier = casing == null ? 1 : casing.getTier();

        blastFurnaceTemperature = 0;
        switch (coilTier) {
            case 1, 2, 3, 4, 5:
                blastFurnaceTemperature += 900 + 900 * coilTier;
                break;
            case 6, 7, 8:
                blastFurnaceTemperature += 5400 + 1800 * (coilTier - 5);
                break;
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.blastFurnaceTemperature = 0;
    }

    @Override
    public Material getMaterial() {
        return InfusedFire;
    }

    @Override
    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {
        super.addCustomCapacity(keyManager, syncer);
        if (isStructureFormed()) {
            var heatString = KeyUtil.number(TextFormatting.RED,
                    syncer.syncInt(blastFurnaceTemperature), "K");

            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,"gregtech.multiblock.blast_furnace.max_temperature", heatString));
        }

    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        TooltipBuilder.create().addBlast().build(this, tooltip);
    }
    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe, boolean consumeIfSuccess) {
        int recipeTemp = recipe.getProperty(TemperatureProperty.getInstance(), 0);
        if(this.blastFurnaceTemperature >= recipeTemp)
            return true;
        recipeMapWorkable.setWhyFailed("线圈温度过低，配方需求至少 "+ recipeTemp + " K温度");
        return false;
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.SPELL_PRISM_HOT;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public int getCurrentTemperature() {
        return blastFurnaceTemperature;
    }
}
