package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.BlockQuartz;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 倾斜粗石英柱（水晶簇落的标志景观）：
 * 阶梯状斜柱——沿随机水平方向，每 3 格高度偏移 1 格，柱径 2×2 或 3×3，柱高 8~16。
 * 生成条件：柱体路径（含偏移）全部为空气，且起点在洞底实心方块上方。
 */
public class WorldGenSlantedPillar extends WorldGenerator {

    private static final int MAX_HEIGHT = 80; // 洞顶上限，防止柱体穿顶

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        EnumFacing direction = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
        int height = 8 + rand.nextInt(9);          // 8~16
        int thickness = rand.nextBoolean() ? 2 : 3; // 柱径 2×2 或 3×3（"很粗"）

        // 起点：从上向下找洞底——先遇到空气（洞窟空间），再往下遇到实心方块（洞底）
        boolean seenAir = false;
        BlockPos base = null;
        for (int y = MAX_HEIGHT - 1; y >= 4; --y) {
            BlockPos pos = new BlockPos(position.getX(), y, position.getZ());
            if (worldIn.isAirBlock(pos)) {
                seenAir = true;
            } else if (seenAir && worldIn.getBlockState(pos).getMaterial().isSolid()) {
                base = pos.up();
                break;
            }
        }
        if (base == null || base.getY() + height >= MAX_HEIGHT) {
            return false;
        }

        // 预检：柱体路径（含阶梯偏移）全部为空气
        int offset = 0;
        for (int step = 0; step < height; ++step) {
            if (step % 3 == 0 && step > 0) {
                offset++;
            }
            int y = base.getY() + step;
            if (y >= MAX_HEIGHT) {
                return false;
            }
            for (int dx = 0; dx < thickness; dx++) {
                for (int dz = 0; dz < thickness; dz++) {
                    BlockPos pillarPos = base.add(
                            direction.getXOffset() * offset + dx, step, direction.getZOffset() * offset + dz);
                    if (!worldIn.isAirBlock(pillarPos)) {
                        return false;
                    }
                }
            }
        }

        // 生成：主体石英柱，柱顶一层石英块
        offset = 0;
        for (int step = 0; step < height; ++step) {
            if (step % 3 == 0 && step > 0) {
                offset++;
            }
            for (int dx = 0; dx < thickness; dx++) {
                for (int dz = 0; dz < thickness; dz++) {
                    BlockPos pillarPos = base.add(
                            direction.getXOffset() * offset + dx, step, direction.getZOffset() * offset + dz);
                    if (step == height - 1) {
                        worldIn.setBlockState(pillarPos, Blocks.QUARTZ_BLOCK.getDefaultState(), 2);
                    } else {
                        IBlockState pillarState = Blocks.QUARTZ_BLOCK.getDefaultState()
                                .withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y);
                        worldIn.setBlockState(pillarPos, pillarState, 2);
                    }
                }
            }
        }
        return true;
    }
}
