package meowmel.pollution.dimension.worldgen.feature;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.List;
import java.util.Random;

/**
 * 在地下生成椭圆形流体池（水/岩浆/焦油等）。
 * 池体由多个椭球并集构成，上半部分为空气空腔，下半部分填充流体。
 */
public class WorldGenFluidPool extends WorldGenerator {

    private static final int POOL_WIDTH = 16;
    private static final int POOL_HEIGHT = 8;

    private final Block block;
    private final double size;
    // 区域内出现这些方块时放弃生成（可通过子类/构造扩展）
    private final List<Block> blackListedBlocks = ImmutableList.of();
    private int minY = 0;

    public WorldGenFluidPool(Block blockIn, double size) {
        super(true);
        this.block = blockIn;
        this.size = size;
    }

    public WorldGenFluidPool(Block blockIn) {
        this(blockIn, 1.0D);
    }

    public WorldGenFluidPool setMinY(int minY) {
        this.minY = minY;
        return this;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        // 先下落到实体地面
        position = position.add(-8, 0, -8);
        while (position.getY() > 5 && worldIn.isAirBlock(position)) {
            position = position.down();
        }

        if (position.getY() <= 4 + this.minY) {
            return false;
        }
        position = position.down(4);

        // 黑名单方块检查：区域内存在黑名单方块则放弃
        for (int xx = 0; xx < POOL_WIDTH; ++xx) {
            for (int zz = 0; zz < POOL_WIDTH; ++zz) {
                for (int yy = 0; yy < POOL_HEIGHT; ++yy) {
                    if (this.blackListedBlocks.contains(worldIn.getBlockState(position.add(xx, yy, zz)).getBlock())) {
                        return false;
                    }
                }
            }
        }

        // 用多个随机椭球的并集标记池体内部
        boolean[] isInPool = new boolean[POOL_WIDTH * POOL_WIDTH * POOL_HEIGHT];
        int blobs = rand.nextInt(4) + 4;

        for (int blob = 0; blob < blobs; ++blob) {
            double sx = (rand.nextDouble() * 6.0D + 3.0D) * this.size;
            double sy = (rand.nextDouble() * 4.0D + 2.0D) * this.size;
            double sz = (rand.nextDouble() * 6.0D + 3.0D) * this.size;
            double bx = rand.nextDouble() * (POOL_WIDTH - sx - 2.0D) + 1.0D + sx / 2.0D;
            double by = rand.nextDouble() * (POOL_HEIGHT - sy - 4.0D) + 2.0D + sy / 2.0D;
            double bz = rand.nextDouble() * (POOL_WIDTH - sz - 2.0D) + 1.0D + sz / 2.0D;

            for (int ox = 1; ox < POOL_WIDTH - 1; ++ox) {
                for (int oz = 1; oz < POOL_WIDTH - 1; ++oz) {
                    for (int oy = 1; oy < POOL_HEIGHT - 1; ++oy) {
                        double dx = ((double) ox - bx) / (sx / 2.0D);
                        double dy = ((double) oy - by) / (sy / 2.0D);
                        double dz = ((double) oz - bz) / (sz / 2.0D);
                        double dst = dx * dx + dy * dy + dz * dz;

                        if (dst < 1.0D) {
                            isInPool[(ox * POOL_WIDTH + oz) * POOL_HEIGHT + oy] = true;
                        }
                    }
                }
            }
        }

        // 校验池体外壁：外壁处不能出现液体（上半）或非固体（下半），否则放弃
        for (int ox = 0; ox < POOL_WIDTH; ++ox) {
            for (int oz = 0; oz < POOL_WIDTH; ++oz) {
                for (int oy = 0; oy < POOL_HEIGHT; ++oy) {
                    boolean isOuterBlock = !isInPool[(ox * POOL_WIDTH + oz) * POOL_HEIGHT + oy]
                            && (ox < POOL_WIDTH - 1 && isInPool[((ox + 1) * POOL_WIDTH + oz) * POOL_HEIGHT + oy]
                            || ox > 0 && isInPool[((ox - 1) * POOL_WIDTH + oz) * POOL_HEIGHT + oy]
                            || oz < POOL_WIDTH - 1 && isInPool[(ox * POOL_WIDTH + oz + 1) * POOL_HEIGHT + oy]
                            || oz > 0 && isInPool[(ox * POOL_WIDTH + (oz - 1)) * POOL_HEIGHT + oy]
                            || oy < POOL_HEIGHT - 1 && isInPool[(ox * POOL_WIDTH + oz) * POOL_HEIGHT + oy + 1]
                            || oy > 0 && isInPool[(ox * POOL_WIDTH + oz) * POOL_HEIGHT + (oy - 1)]);

                    if (isOuterBlock) {
                        Material material = worldIn.getBlockState(position.add(ox, oy, oz)).getMaterial();

                        if (oy >= 4 && material.isLiquid()) {
                            return false;
                        }

                        if (oy < 4 && !material.isSolid() && worldIn.getBlockState(position.add(ox, oy, oz)).getBlock() != this.block) {
                            return false;
                        }
                    }
                }
            }
        }

        // 放置池体：上半为空气空腔，下半为流体
        for (int ox = 0; ox < POOL_WIDTH; ++ox) {
            for (int oz = 0; oz < POOL_WIDTH; ++oz) {
                for (int oy = 0; oy < POOL_HEIGHT; ++oy) {
                    if (isInPool[(ox * POOL_WIDTH + oz) * POOL_HEIGHT + oy]) {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(ox, oy, oz),
                                oy >= 4 ? Blocks.AIR.getDefaultState() : this.block.getDefaultState());
                    }
                }
            }
        }

        return true;
    }
}
