package meowmel.pollution.common.metatileentity.multiblock;

import gregtech.api.GTValues;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import meowmel.gtqtcore.client.textures.GTQTTextures;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 大型空气过滤机的三个等级，写法参考 {@code LargeTurbineType}
 */
public enum FluxClearType implements IFluxClearType {

    EV("ev", GTValues.EV, 4,
            MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TITANIUM_STABLE),
            MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.ENGINE_INTAKE_CASING),
            Textures.STABLE_TITANIUM_CASING,
            GTQTTextures.ROCKET_ENGINE_OVERLAY),

    IV("iv", GTValues.IV, 5,
            MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST),
            MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING),
            Textures.ROBUST_TUNGSTENSTEEL_CASING,
            GTQTTextures.ROCKET_ENGINE_OVERLAY),

    LuV("luv", GTValues.LuV, 6,
            MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.HSSE_STURDY),
            MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING),
            Textures.STURDY_HSSE_CASING,
            GTQTTextures.ROCKET_ENGINE_OVERLAY);

    private final String name;
    private final int tier;
    private final int radius;
    private final IBlockState casingState;
    private final IBlockState intakeState;
    private final ICubeRenderer casingRenderer;
    private final ICubeRenderer frontOverlay;

    FluxClearType(String name, int tier, int radius, IBlockState casingState, IBlockState intakeState,
                  ICubeRenderer casingRenderer, ICubeRenderer frontOverlay) {
        this.name = name;
        this.tier = tier;
        this.radius = radius;
        this.casingState = casingState;
        this.intakeState = intakeState;
        this.casingRenderer = casingRenderer;
        this.frontOverlay = frontOverlay;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public @NotNull IBlockState getCasingState() {
        return casingState;
    }

    @Override
    public @NotNull IBlockState getIntakeState() {
        return intakeState;
    }

    @Override
    public @NotNull ICubeRenderer getCasingRenderer() {
        return casingRenderer;
    }

    @Override
    public @NotNull ICubeRenderer getFrontOverlay() {
        return frontOverlay;
    }
}
