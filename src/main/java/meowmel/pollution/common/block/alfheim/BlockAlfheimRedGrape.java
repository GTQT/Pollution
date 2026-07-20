package meowmel.pollution.common.block.alfheim;

import net.minecraft.block.BlockVine;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/** One of the three source red-grape vine growth stages. */
public final class BlockAlfheimRedGrape extends BlockVine implements IGrowable {

    private final int stage;

    public BlockAlfheimRedGrape(int stage) {
        this.stage = stage;
        setHardness(0.2F);
        setLightOpacity(0);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (stage == 0) {
            super.updateTick(world, pos, state, random);
            state = world.getBlockState(pos);
            if (state.getBlock() != this) {
                return;
            }
        }
        if (stage < 2 && random.nextInt(stage == 0 ? 50 : 10) == 0) {
            advance(world, pos, state);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && stage == 2 && player.getHeldItem(hand).isEmpty()) {
            spawnAsEntity(world, pos, new ItemStack(AlfheimBlocks.RED_GRAPES[0], world.rand.nextInt(2) + 1));
            world.setBlockState(pos, AlfheimBlocks.RED_GRAPES[0].getStateFromMeta(getMetaFromState(state)), 3);
            return true;
        }
        return false;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return stage < 2;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return rand.nextInt(3) == 0;
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        if (stage < 2) {
            advance(world, pos, state);
        }
    }

    private void advance(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, AlfheimBlocks.RED_GRAPES[stage + 1].getStateFromMeta(getMetaFromState(state)), 3);
    }
}
