package meowmel.pollution.common.metatileentity.multiblock.astral;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetaTileEntityIndustrialStarlightInfuser extends RecipeMapMultiblockController {

    private static final StructureDefinition<?> STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:industrial_starlight_infuser", () -> DeclarativePatternBuilder.start()
                    .aisle("  CCCCC  ", "  CCCCC  ", "         ", "         ", "         ", "         ")
                    .aisle(" CCCCCCC ", " CARRRAC ", " P     P ", " P     P ", " AAAAAAA ", "         ")
                    .aisle("CCCCCCCCC", "CRLLLLLRC", "         ", "         ", " A     A ", " AAAAAAA ")
                    .aisle("CCCCCCCCC", "CRLLLLLRC", "         ", "         ", " A     A ", " A     A ")
                    .aisle("CCCCCCCCC", "CRLLILLRC", "         ", "         ", " A     A ", " A     A ")
                    .aisle("CCCCCCCCC", "CRLLLLLRC", "         ", "         ", " A     A ", " A     A ")
                    .aisle("CCCCCCCCC", "CRLLLLLRC", "         ", "         ", " A     A ", " AAAAAAA ")
                    .aisle(" CCCCCCC ", " CARRRAC ", " P     P ", " P     P ", " AAAAAAA ", "         ")
                    .aisle("  CCSCC  ", "  CCCCC  ", "         ", "         ", "         ", "         ")
                    .self('S', MetaTileEntityIndustrialStarlightInfuser.class)
                    .where('C', Elements.choice(Elements.block(marble(BlockMarble.MarbleBlockType.BRICKS)),
                            Elements.abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.IMPORT_ITEMS,
                                    MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS,
                                    MultiblockAbility.MAINTENANCE_HATCH)))
                    .block('R', marble(BlockMarble.MarbleBlockType.RUNED))
                    .block('P', marble(BlockMarble.MarbleBlockType.PILLAR))
                    .block('A', marble(BlockMarble.MarbleBlockType.ARCH))
                     .block('L', BlocksAS.blockLiquidStarlight.getDefaultState())
                     .block('I', BlocksAS.starlightInfuser.getDefaultState())
                     .any(' ')
                     .globalAbilityLimit(MultiblockAbility.INPUT_ENERGY, 1, -1)
                     .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 1, -1)
                     .globalAbilityLimit(MultiblockAbility.EXPORT_ITEMS, 1, -1)
                     .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, -1)
                     .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                     .buildStructureDefinition());

    public MetaTileEntityIndustrialStarlightInfuser(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PORecipeMaps.INDUSTRIAL_STARLIGHT_INFUSER_RECIPES);
    }

    private static IBlockState marble(BlockMarble.MarbleBlockType type) {
        return BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, type);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityIndustrialStarlightInfuser(metaTileEntityId);
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
        BlockPos center = getPos().offset(inward, 4).up(2);
        double angle = world.rand.nextDouble() * Math.PI * 2.0;
        double radius = 0.8 + world.rand.nextDouble() * 2.4;
        double x = center.getX() + 0.5 + Math.cos(angle) * radius;
        double y = center.getY() + world.rand.nextDouble() * 3.0;
        double z = center.getZ() + 0.5 + Math.sin(angle) * radius;
        world.spawnParticle(EnumParticleTypes.END_ROD, x, y, z, 0.0, 0.025, 0.0);
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.industrial_starlight_infuser.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.industrial_starlight_infuser.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.industrial_starlight_infuser.tooltip.3"));
    }
}
