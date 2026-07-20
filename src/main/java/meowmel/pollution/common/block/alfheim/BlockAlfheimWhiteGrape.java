package meowmel.pollution.common.block.alfheim;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/** Three-stage white grape pad, following the source BlockGrapeWhite growth rules. */
public final class BlockAlfheimWhiteGrape extends BlockBush implements IGrowable {

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 2);
    private static final AxisAlignedBB GRAPE_AABB = new AxisAlignedBB(0, 0, 0, 1, 1.0D / 16.0D, 1);

    public BlockAlfheimWhiteGrape() {
        setHardness(0.2F);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(AGE, 0));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return GRAPE_AABB;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getMaterial() == Material.WATER;
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        IBlockState below = world.getBlockState(pos.down());
        return below.getMaterial() == Material.WATER && below.getBlock().getMetaFromState(below) == 0;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        super.updateTick(world, pos, state, random);
        if (!canBlockStay(world, pos, state)) {
            return;
        }

        int age = state.getValue(AGE);
        int chance = age == 0 ? 50 : 10;
        if (age < 2 && random.nextInt(chance) == 0) {
            world.setBlockState(pos, state.withProperty(AGE, age + 1), 3);
            return;
        }

        if (random.nextInt(100) == 0) {
            EnumFacing[] horizontal = {EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST};
            for (int i = horizontal.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                EnumFacing swap = horizontal[i];
                horizontal[i] = horizontal[j];
                horizontal[j] = swap;
            }
            for (EnumFacing facing : horizontal) {
                BlockPos target = pos.offset(facing);
                if (world.getBlockState(target).getBlock() == Blocks.WATERLILY) {
                    world.setBlockState(target, getDefaultState(), 3);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && state.getValue(AGE) >= 2 && player.getHeldItem(hand).isEmpty()) {
            spawnAsEntity(world, pos, new ItemStack(this, world.rand.nextInt(2) + 1));
            world.setBlockState(pos, state.withProperty(AGE, 0), 3);
            return true;
        }
        return false;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(AGE) < 2;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return rand.nextInt(3) == 0;
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        int age = state.getValue(AGE);
        world.setBlockState(pos, state.withProperty(AGE, Math.min(2, age + 1)), 3);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AGE, Math.max(0, Math.min(2, meta)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE);
    }
}
