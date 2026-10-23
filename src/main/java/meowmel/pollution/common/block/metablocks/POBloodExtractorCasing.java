package meowmel.pollution.common.block.metablocks;

import gregtech.api.block.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import static meowmel.pollution.common.CommonProxy.Pollution_TAB;

public class POBloodExtractorCasing extends VariantBlock<POBloodExtractorCasing.Type> {
    public POBloodExtractorCasing() {
        super(Material.IRON);
        setTranslationKey("blood_extractor_casing");
        setHardness(8);
        setResistance(30);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 4);
        setCreativeTab(Pollution_TAB);
    }
    private boolean glass(IBlockState state) { return getState(state) == Type.CYLINDER_GLASS; }
    @Override public boolean isOpaqueCube(IBlockState state) { return !glass(state); }
    @Override public boolean isFullCube(IBlockState state) { return !glass(state); }
    @Override public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) { return glass(state) ? 0 : 255; }
    @Override public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == (glass(state) ? BlockRenderLayer.TRANSLUCENT : BlockRenderLayer.SOLID);
    }
    @Override public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState other = world.getBlockState(pos.offset(side));
        if (glass(state) && other == state) return false;
        return super.shouldSideBeRendered(state, world, pos, side);
    }
    public enum Type implements IStringSerializable {
        SEALED_CASING("sealed_casing"), CYLINDER_GLASS("cylinder_glass");
        private final String name;
        Type(String name) { this.name = name; }
        @Override public String getName() { return name; }
    }
}
