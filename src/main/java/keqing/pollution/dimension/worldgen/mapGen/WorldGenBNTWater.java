package keqing.pollution.dimension.worldgen.mapGen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class WorldGenBNTWater extends WorldGenerator
{
    private final Block block;
    private final boolean insideRock;

    public WorldGenBNTWater(Block blockIn, boolean insideRockIn)
    {
        this.block = blockIn;
        this.insideRock = insideRockIn;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (worldIn.getBlockState(position.up()).getBlock() != BlockRegistry.BETWEENSTONE)
        {
            return false;
        }
        else if (!worldIn.isAirBlock(position) && worldIn.getBlockState(position).getBlock() != BlockRegistry.BETWEENSTONE)
        {
            return false;
        }
        else
        {
            int i = 0;

            if (worldIn.getBlockState(position.west()).getBlock() == BlockRegistry.BETWEENSTONE)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.east()).getBlock() == BlockRegistry.BETWEENSTONE)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.north()).getBlock() == BlockRegistry.BETWEENSTONE)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.south()).getBlock() == BlockRegistry.BETWEENSTONE)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.down()).getBlock() == BlockRegistry.BETWEENSTONE)
            {
                ++i;
            }

            int j = 0;

            if (worldIn.isAirBlock(position.west()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.east()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.north()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.south()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.down()))
            {
                ++j;
            }

            if (!this.insideRock && i == 4 && j == 1 || i == 5)
            {
                IBlockState iblockstate = this.block.getDefaultState();
                worldIn.setBlockState(position, iblockstate, 2);
                worldIn.immediateBlockTick(position, iblockstate, rand);
            }

            return true;
        }
    }
}