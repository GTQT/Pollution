package meowmel.pollution.common.block.rainbow;

import meowmel.pollution.Pollution;
import meowmel.pollution.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockRainbowSapling extends Block implements IGrowable {

    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
    private static final AxisAlignedBB SAPLING_AABB =
            new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);

    public BlockRainbowSapling() {
        super(Material.PLANTS);
        setRegistryName(Pollution.MODID, "rainbow_sapling");
        setTranslationKey(Pollution.MODID + ".rainbow_sapling");
        setCreativeTab(CommonProxy.Pollution_TAB);
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote && world.getLightFromNeighbors(pos.up()) >= 9 && random.nextInt(7) == 0) {
            grow(world, random, pos, state);
        }
    }

    @Override
    public void grow(World world, Random random, BlockPos pos, IBlockState state) {
        if (state.getValue(STAGE) == 0) {
            world.setBlockState(pos, state.withProperty(STAGE, 1), 4);
        } else {
            growTree(world, pos, random);
        }
    }

    public void growTree(World world, BlockPos pos, Random random) {
        BlockPos largeOrigin = findTwoByTwo(world, pos);
        if (largeOrigin != null) {
            RainbowTreeGenerator generator = new RainbowTreeGenerator();
            if (!generator.canGenerateLarge(world, largeOrigin)) {
                return;
            }
            IBlockState[] oldStates = new IBlockState[4];
            int index = 0;
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos saplingPos = largeOrigin.add(dx, 0, dz);
                    oldStates[index++] = world.getBlockState(saplingPos);
                    world.setBlockToAir(saplingPos);
                }
            }
            if (!generator.generateLarge(world, random, largeOrigin)) {
                index = 0;
                for (int dx = 0; dx < 2; dx++) {
                    for (int dz = 0; dz < 2; dz++) {
                        world.setBlockState(largeOrigin.add(dx, 0, dz), oldStates[index++], 4);
                    }
                }
            }
            return;
        }

        RainbowTreeGenerator generator = new RainbowTreeGenerator();
        if (!generator.canGenerateSmall(world, pos)) {
            return;
        }
        IBlockState oldState = world.getBlockState(pos);
        world.setBlockToAir(pos);
        if (!generator.generateSmall(world, random, pos)) {
            world.setBlockState(pos, oldState, 4);
        }
    }

    @Nullable
    private BlockPos findTwoByTwo(World world, BlockPos pos) {
        for (int dx = -1; dx <= 0; dx++) {
            for (int dz = -1; dz <= 0; dz++) {
                BlockPos origin = pos.add(dx, 0, dz);
                if (isSapling(world, origin)
                        && isSapling(world, origin.east())
                        && isSapling(world, origin.south())
                        && isSapling(world, origin.east().south())) {
                    return origin;
                }
            }
        }
        return null;
    }

    private boolean isSapling(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == this;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return RainbowTreeGenerator.canGrowOn(world.getBlockState(pos.down()));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
                                Block blockIn, BlockPos fromPos) {
        // Only loss of suitable soil should uproot the sapling. Calling the
        // generic placement check here also tests whether the already occupied
        // position is replaceable, which can incorrectly pop adjacent saplings.
        if (!RainbowTreeGenerator.canGrowOn(world.getBlockState(pos.down()))) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SAPLING_AABB;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STAGE);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STAGE, meta & 1);
    }
}
