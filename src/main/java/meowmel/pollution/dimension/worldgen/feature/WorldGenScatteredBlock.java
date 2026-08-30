package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 在区域内的石头表面随机散布方块（如蘑菇、草丛、树叶等）。
 * 每次尝试在中心点附近随机取一个位置，若为空气且下方是石头则放置目标方块。
 */
public class WorldGenScatteredBlock extends WorldGenerator {

    private final Block block;
    private final int placeAttempts;

    public WorldGenScatteredBlock(Block blockIn, int placeAttempts) {
        this.block = blockIn;
        this.placeAttempts = placeAttempts;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < this.placeAttempts; ++i) {
            BlockPos candidate = position.add(
                    rand.nextInt(8) - rand.nextInt(8),
                    rand.nextInt(4) - rand.nextInt(4),
                    rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(candidate) && worldIn.getBlockState(candidate.down()).getBlock() == Blocks.STONE) {
                worldIn.setBlockState(candidate, this.block.getDefaultState(), 2);
            }
        }

        return true;
    }
}
