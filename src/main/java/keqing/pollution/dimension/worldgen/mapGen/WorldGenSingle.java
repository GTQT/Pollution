package keqing.pollution.dimension.worldgen.mapGen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.WorldGenCave;

import java.util.Random;

public class WorldGenSingle extends WorldGenCave {


    private final Block block;

    public WorldGenSingle(Block blockIn) {
        super(true);
        this.block = blockIn;
    }


    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 48; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == BlockRegistry.BETWEENSTONE) {
                worldIn.setBlockState(blockpos, block.getDefaultState(), 2);
            }
        }

        return true;
    }
}
