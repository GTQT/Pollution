package meowmel.pollution.common.metatileentity.multiblock.astral;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import meowmel.pollution.api.recipes.PORecipeMaps;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetaTileEntityIndustrialLightwell extends RecipeMapMultiblockController {

    private static final int LIGHTWELL_RECIPE_TICKS = 200;

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:industrial_lightwell", () -> DeclarativePatternBuilder.start()
                    .aisle(" CCCCC ", " CCCCC ", "       ", "       ", "       ", "       ", "       ")
                    .aisle("CCCCCCC", "CARRRAC", "P     P", "P     P", "P     P", "AAAAAAA", "       ")
                    .aisle("CCCCCCC", "CRRRRRC", "       ", "       ", "       ", "A     A", "AAAAAAA")
                    .aisle("CCCCCCC", "CRRRRRC", "R  W  R", "       ", "       ", "A     A", "A     A")
                    .aisle("CCCCCCC", "CRRRRRC", "       ", "       ", "       ", "A     A", "AAAAAAA")
                    .aisle("CCCCCCC", "CARRRAC", "P     P", "P     P", "P     P", "AAAAAAA", "       ")
                    .aisle(" CCSCC ", " CCCCC ", "       ", "       ", "       ", "       ", "       ")
                    .self('S', MetaTileEntityIndustrialLightwell.class)
                    .where('C', Elements.choice(Elements.block(marble(BlockMarble.MarbleBlockType.BRICKS)),
                            Elements.abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.IMPORT_ITEMS,
                                    MultiblockAbility.EXPORT_FLUIDS, MultiblockAbility.MAINTENANCE_HATCH)))
                    .block('R', marble(BlockMarble.MarbleBlockType.RUNED))
                    .block('P', marble(BlockMarble.MarbleBlockType.PILLAR))
                     .block('A', marble(BlockMarble.MarbleBlockType.ARCH))
                     .block('W', BlocksAS.blockWell.getDefaultState())
                     .any(' ')
                     .globalAbilityLimit(MultiblockAbility.INPUT_ENERGY, 1, -1)
                     .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 1, -1)
                     .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, -1)
                     .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                     .buildStructureDefinition());

    public MetaTileEntityIndustrialLightwell(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PORecipeMaps.INDUSTRIAL_LIGHTWELL_RECIPES);
        this.recipeMapWorkable = new IndustrialLightwellRecipeLogic(this);
    }

    private static IBlockState marble(BlockMarble.MarbleBlockType type) {
        return BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, type);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityIndustrialLightwell(metaTileEntityId);
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
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
    public void update() {
        super.update();
        World world = getWorld();
        if (world == null || !world.isRemote || !recipeMapWorkable.isActive() || getOffsetTimer() % 3L != 0L) {
            return;
        }

        EnumFacing inward = getFrontFacing().getOpposite();
        BlockPos center = getPos().offset(inward, 3).up(2);
        double angle = world.rand.nextDouble() * Math.PI * 2.0;
        double radius = 0.4 + world.rand.nextDouble() * 1.8;
        world.spawnParticle(EnumParticleTypes.END_ROD,
                center.getX() + 0.5 + Math.cos(angle) * radius,
                center.getY() + 0.4 + world.rand.nextDouble() * 3.2,
                center.getZ() + 0.5 + Math.sin(angle) * radius,
                0.0, 0.035, 0.0);
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.industrial_lightwell.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.industrial_lightwell.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.industrial_lightwell.tooltip.3"));
    }

    private void tryShatterCatalyst(Recipe recipe, IItemHandlerModifiable inputInventory) {
        if (recipe == null || inputInventory == null || recipe.getInputs().isEmpty()) return;

        GTRecipeInput catalystInput = recipe.getInputs().get(0);
        for (int slot = 0; slot < inputInventory.getSlots(); slot++) {
            ItemStack stack = inputInventory.getStackInSlot(slot);
            if (stack.isEmpty() || !catalystInput.acceptsStack(stack)) continue;

            WellLiquefaction.LiquefactionEntry entry = WellLiquefaction.getLiquefactionEntry(stack);
            if (entry == null) return;

            double denominator = 1.0 + 1000.0 * Math.max(0.0F, entry.shatterMultiplier);
            double perTickShatterChance = 1.0 / denominator;
            double shatterChance = 1.0 - Math.pow(1.0 - perTickShatterChance, LIGHTWELL_RECIPE_TICKS);
            if (getWorld().rand.nextDouble() < shatterChance) {
                ItemStack remaining = stack.copy();
                remaining.shrink(1);
                inputInventory.setStackInSlot(slot, remaining);
            }
            return;
        }
    }

    private class IndustrialLightwellRecipeLogic extends MultiblockRecipeLogic {

        private IndustrialLightwellRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        protected void completeRecipe() {
            tryShatterCatalyst(previousRecipe, getInputInventory());
            super.completeRecipe();
        }
    }
}
