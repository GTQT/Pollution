package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import vazkii.botania.common.block.ModBlocks;

import java.util.Random;

/**
 * Direct 1.12 block-state port of Alfheim's {@code AlfheimLakeGen}.
 *
 * <p>Unlike vanilla {@code WorldGenLakes}, the source only permits air,
 * grass, dirt, livingrock and still water inside the carved volume.</p>
 */
public final class WorldGenAlfheimLake extends WorldGenerator {

    private final Block lakeBlock;
    private final IBlockState lakeState;

    public WorldGenAlfheimLake(Block lakeBlock) {
        this.lakeBlock = lakeBlock;
        this.lakeState = lakeBlock.getDefaultState();
    }

    @Override
    public boolean generate(World world, Random random, BlockPos position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(x, y, z);

        while (y > 7 && world.isAirBlock(cursor.setPos(x, y, z))) {
            --y;
        }
        cursor.setPos(x, y, z);
        if (!isAllowed(world.getBlockState(cursor).getBlock()) || y <= 6) {
            return false;
        }
        y -= 6;

        boolean[] volume = new boolean[2048];
        for (int ellipsoid = 0, count = random.nextInt(4) + 4; ellipsoid < count; ellipsoid++) {
            double sizeX = random.nextDouble() * 6.0D + 3.0D;
            double sizeY = random.nextDouble() * 4.0D + 2.0D;
            double sizeZ = random.nextDouble() * 6.0D + 3.0D;
            double centerX = random.nextDouble() * (14.0D - sizeX) + 1.0D + sizeX / 2.0D;
            double centerY = random.nextDouble() * (4.0D - sizeY) + 2.0D + sizeY / 2.0D;
            double centerZ = random.nextDouble() * (14.0D - sizeZ) + 1.0D + sizeZ / 2.0D;

            for (int localX = 1; localX <= 14; localX++) {
                for (int localZ = 1; localZ <= 14; localZ++) {
                    for (int localY = 1; localY <= 6; localY++) {
                        double dx = (localX - centerX) * 2.0D / sizeX;
                        double dy = (localY - centerY) * 2.0D / sizeY;
                        double dz = (localZ - centerZ) * 2.0D / sizeZ;
                        if (dx * dx + dy * dy + dz * dz < 1.0D) {
                            volume[index(localX, localY, localZ)] = true;
                        }
                    }
                }
            }
        }

        if (!canCarve(world, x, y, z, volume, cursor)) {
            return false;
        }

        for (int localX = 0; localX <= 15; localX++) {
            for (int localZ = 0; localZ <= 15; localZ++) {
                for (int localY = 0; localY <= 7; localY++) {
                    if (volume[index(localX, localY, localZ)]) {
                        cursor.setPos(x + localX, y + localY, z + localZ);
                        world.setBlockState(cursor,
                                localY >= 4 ? Blocks.AIR.getDefaultState() : lakeState, 3);
                    }
                }
            }
        }
        return true;
    }

    private boolean canCarve(World world, int x, int y, int z, boolean[] volume,
                             BlockPos.MutableBlockPos cursor) {
        for (int localX = 0; localX <= 15; localX++) {
            for (int localZ = 0; localZ <= 15; localZ++) {
                for (int localY = 0; localY <= 7; localY++) {
                    int index = index(localX, localY, localZ);
                    boolean filled = volume[index];
                    if (!filled && bordersVolume(volume, localX, localY, localZ)) {
                        cursor.setPos(x + localX, y + localY, z + localZ);
                        IBlockState state = world.getBlockState(cursor);
                        Material material = state.getMaterial();
                        if (localY >= 4 && material.isLiquid()) {
                            return false;
                        }
                        if (localY < 4 && !material.isSolid()
                                && state.getBlock() != lakeBlock) {
                            return false;
                        }
                    }
                    if (filled) {
                        cursor.setPos(x + localX, y + localY, z + localZ);
                        if (!isAllowed(world.getBlockState(cursor).getBlock())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean bordersVolume(boolean[] volume, int x, int y, int z) {
        return x < 15 && volume[index(x + 1, y, z)]
                || x > 0 && volume[index(x - 1, y, z)]
                || z < 15 && volume[index(x, y, z + 1)]
                || z > 0 && volume[index(x, y, z - 1)]
                || y < 7 && volume[index(x, y + 1, z)]
                || y > 0 && volume[index(x, y - 1, z)];
    }

    private boolean isAllowed(Block block) {
        return block == Blocks.AIR
                || block == Blocks.GRASS
                || block == Blocks.DIRT
                || block == ModBlocks.livingrock
                || block == lakeBlock;
    }

    private static int index(int x, int y, int z) {
        return (x * 16 + z) * 8 + y;
    }
}
