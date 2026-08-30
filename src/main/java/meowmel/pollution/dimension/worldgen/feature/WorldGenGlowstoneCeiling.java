package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 洞顶萤石（原始洞穴的顶部照明）：
 * 在 y 60~82 区间寻找实心方块，在其下表面铺设 1~3 格萤石簇。
 */
public class WorldGenGlowstoneCeiling extends WorldGenerator {

    private static final int MIN_Y = 60;
    private static final int MAX_Y = 82;
    private static final int ATTEMPTS = 16;

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        boolean generated = false;
        for (int attempt = 0; attempt < ATTEMPTS; ++attempt) {
            BlockPos candidate = position.add(
                    rand.nextInt(16) - 8, MIN_Y + rand.nextInt(MAX_Y - MIN_Y), rand.nextInt(16) - 8);
            if (!worldIn.isAirBlock(candidate)) {
                continue;
            }
            // 上方 1~3 格内存在实心方块（洞顶岩层）才铺设
            for (int above = 1; above <= 3; above++) {
                if (!worldIn.isAirBlock(candidate.up(above))) {
                    // 铺 1~3 格萤石簇（向下悬挂）
                    int cluster = 1 + rand.nextInt(3);
                    for (int d = 0; d < cluster; d++) {
                        BlockPos glowPos = candidate.down(d);
                        if (worldIn.isAirBlock(glowPos)) {
                            worldIn.setBlockState(glowPos, Blocks.GLOWSTONE.getDefaultState(), 2);
                            generated = true;
                        }
                    }
                    break;
                }
            }
        }
        return generated;
    }
}
