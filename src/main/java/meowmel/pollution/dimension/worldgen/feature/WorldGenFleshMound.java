package meowmel.pollution.dimension.worldgen.feature;

import meowmel.pollution.common.block.blocks.PollutionBlocksInit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 生成随高度收缩的血肉丘（血世界地表装饰）。
 */
public class WorldGenFleshMound extends WorldGenerator {

    private static final IBlockState FLESH_STATE = PollutionBlocksInit.FLESH_BLOCK.getDefaultState();

    @Override
    public boolean generate(World world, Random random, BlockPos position) {
        int radius = random.nextInt(3) + 2;
        int height = random.nextInt(4) + 2;

        for (int y = 0; y < height; y++) {
            // 高度越高半径越小，形成锥形丘体
            float shrink = (float) y / height;
            int currentRadius = MathHelper.ceil(radius * (1.0F - shrink * 0.5F));

            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    if (dx * dx + dz * dz <= currentRadius * currentRadius) {
                        world.setBlockState(position.add(dx, y, dz), FLESH_STATE, 3);
                    }
                }
            }
        }

        return true;
    }
}
