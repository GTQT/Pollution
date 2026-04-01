package meowmel.pollution.common.metatileentity.multiblock.magic;

import gregtech.api.capability.IHeatingCoil;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
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
import meowmel.pollution.api.utils.POUtils;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POMagicBlock;
import meowmel.gtqtcore.api.blocks.impl.WrappedIntTired;
import net.minecraft.block.state.IBlockState;
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

import static meowmel.pollution.api.predicate.TiredTraceabilityPredicate.CP_COIL_CASING;
import static meowmel.pollution.api.unification.PollutionMaterials.InfusedFire;

public class MetaTileEntityMagicElectricBlastFurnace extends MagicRecipeMapMultiblockController implements IHeatingCoil {

    private int blastFurnaceTemperature;

    public MetaTileEntityMagicElectricBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.BLAST_RECIPES});
        this.recipeMapWorkable = new MagicHeatingCoilRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.SPELL_PRISM_HOT);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityMagicElectricBlastFurnace(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object CoilLevel = context.get("COILTieredStats");
        int coilTier = POUtils.getOrDefault(() -> CoilLevel instanceof WrappedIntTired,
                () -> ((WrappedIntTired) CoilLevel).getIntTier(),
                1);

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
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "CCC", "CCC", "XXX")
                .aisle("XXX", "C#C", "C#C", "XMX")
                .aisle("XSX", "CCC", "CCC", "XXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(6)
                        .or(autoAbilities(true, true, true, true, true, true, false)))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('C', CP_COIL_CASING.get())
                .where('#', air())
                .build();
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