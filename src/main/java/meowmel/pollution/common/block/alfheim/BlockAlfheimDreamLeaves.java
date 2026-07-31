package meowmel.pollution.common.block.alfheim;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Dreamwood-colored leaves used by Alfheim's fixed Dream Tree structure.
 *
 * <p>The source tree uses Alfheim's Dreamwood leaf variant (altLeaves meta 7).
 * This dedicated 1.12 block keeps the generated canopy stable and uses the
 * source texture without biome foliage tinting.</p>
 */
public final class BlockAlfheimDreamLeaves extends BlockLeaves {

    public BlockAlfheimDreamLeaves() {
        setHardness(0.2F);
        setLightOpacity(0);
        setSoundType(SoundType.PLANT);
        setDefaultState(this.blockState.getBaseState()
                .withProperty(CHECK_DECAY, false)
                .withProperty(DECAYABLE, false));
    }

    @Override
    @Nonnull
    public BlockPlanks.EnumType getWoodType(int meta) {
        return BlockPlanks.EnumType.OAK;
    }

    @Override
    @Nonnull
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return world.getBlockState(pos.offset(side)).getBlock() != this
                && super.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(DECAYABLE)) {
            meta |= 1;
        }
        if (state.getValue(CHECK_DECAY)) {
            meta |= 2;
        }
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(DECAYABLE, (meta & 1) != 0)
                .withProperty(CHECK_DECAY, (meta & 2) != 0);
    }
}
