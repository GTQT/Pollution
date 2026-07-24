package meowmel.pollution.common.metatileentity.multiblock.generator;

import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.metatileentities.multi.electric.generator.ILargeTurbineType;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.NotNull;

final class MagicTurbineType implements ILargeTurbineType {

    private final String name;
    private final RecipeMap<?> recipeMap;
    private final int tier;
    private final IBlockState casing;
    private final IBlockState gearbox;
    private final ICubeRenderer renderer;
    private final boolean muffler;
    private final ICubeRenderer overlay;

    MagicTurbineType(String name, RecipeMap<?> recipeMap, int tier, IBlockState casing, IBlockState gearbox,
                     ICubeRenderer renderer, boolean muffler, ICubeRenderer overlay) {
        this.name = name;
        this.recipeMap = recipeMap;
        this.tier = tier;
        this.casing = casing;
        this.gearbox = gearbox;
        this.renderer = renderer;
        this.muffler = muffler;
        this.overlay = overlay;
    }

    @Override public @NotNull String getName() { return name; }
    @Override public @NotNull RecipeMap<?> getRecipeMap() { return recipeMap; }
    @Override public int getTier() { return tier; }
    @Override public @NotNull IBlockState getCasingState() { return casing; }
    @Override public @NotNull IBlockState getGearboxState() { return gearbox; }
    @Override public @NotNull ICubeRenderer getCasingRenderer() { return renderer; }
    @Override public boolean hasMufflerHatch() { return muffler; }
    @Override public @NotNull ICubeRenderer getFrontOverlay() { return overlay; }
}
