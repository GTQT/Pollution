package meowmel.pollution.common.block.rainbow;

import gregtechfoodoption.worldgen.trees.GTFOTrees;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Procedural rainbow tree generator. The giant form intentionally uses a new
 * branch-and-canopy algorithm rather than ChromatiCraft's hard-coded blueprint.
 */
public class RainbowTreeGenerator {

    private static final int[][] BRANCH_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {-1, 1}, {1, -1}, {-1, -1}
    };

    public boolean canGenerateSmall(World world, BlockPos base) {
        return canGrowOn(world.getBlockState(base.down()))
                && hasSpace(world, base, 3, 12);
    }

    public boolean canGenerateLarge(World world, BlockPos origin) {
        if (!hasLargeGround(world, origin)) {
            return false;
        }
        return hasSpace(world, origin, 7, 30);
    }

    /**
     * Natural giant trees only require a valid 2x2 foundation and an open
     * trunk. Their canopy may merge harmlessly with the surrounding forest.
     */
    public boolean canGenerateNaturalLarge(World world, BlockPos origin) {
        if (!hasLargeGround(world, origin)
                || origin.getY() < 1 || origin.getY() + 30 >= world.getHeight()) {
            return false;
        }
        for (int y = 0; y < 30; y++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos trunkPos = origin.add(dx, y, dz);
                    if (!world.isBlockLoaded(trunkPos) || !canReplace(world, trunkPos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean hasLargeGround(World world, BlockPos origin) {
        for (int dx = 0; dx < 2; dx++) {
            for (int dz = 0; dz < 2; dz++) {
                if (!canGrowOn(world.getBlockState(origin.add(dx, -1, dz)))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean generateSmall(World world, Random random, BlockPos base) {
        if (!canGenerateSmall(world, base)) {
            return false;
        }
        int height = 7 + random.nextInt(5);
        for (int y = 0; y < height; y++) {
            placeLog(world, base.up(y), BlockLog.EnumAxis.Y);
        }

        int crownStart = height - 4;
        for (int y = crownStart; y <= height + 1; y++) {
            int distanceFromTop = Math.abs(height - y);
            int radius = distanceFromTop == 0 ? 2 : (distanceFromTop <= 2 ? 3 : 2);
            if (y == height + 1) {
                radius = 1;
            }
            placeCanopyLayer(world, random, base.up(y), radius, true);
        }
        return true;
    }

    public boolean generateLarge(World world, Random random, BlockPos origin) {
        if (!canGenerateLarge(world, origin)) {
            return false;
        }
        return buildLarge(world, random, origin);
    }

    public boolean generateNaturalLarge(World world, Random random, BlockPos origin) {
        if (!canGenerateNaturalLarge(world, origin)) {
            return false;
        }
        return buildLarge(world, random, origin);
    }

    private boolean buildLarge(World world, Random random, BlockPos origin) {
        int height = 23 + random.nextInt(5);

        for (int y = 0; y < height; y++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    placeLog(world, origin.add(dx, y, dz), BlockLog.EnumAxis.Y);
                }
            }
        }

        // Four low root flares visually anchor the 2x2 trunk.
        placeLog(world, origin.west(), BlockLog.EnumAxis.X);
        placeLog(world, origin.add(2, 0, 1), BlockLog.EnumAxis.X);
        placeLog(world, origin.north(), BlockLog.EnumAxis.Z);
        placeLog(world, origin.add(1, 0, 2), BlockLog.EnumAxis.Z);

        BlockPos center = origin.add(0, 0, 0);
        for (int i = 0; i < BRANCH_DIRECTIONS.length; i++) {
            int[] direction = BRANCH_DIRECTIONS[i];
            int startY = height - 11 + (i % 4) * 2;
            int length = 3 + random.nextInt(2);
            BlockPos endpoint = growBranch(world, center.add(0, startY, 0),
                    direction[0], direction[1], length, i % 2);
            placeCanopyBlob(world, random, endpoint, 2 + random.nextInt(2), false);
        }

        for (int y = height - 5; y <= height + 3; y++) {
            int radius = y < height ? 5 : Math.max(1, 4 - (y - height));
            placeCanopyLayer(world, random, center.add(0, y, 0), radius, false);
        }
        placeLog(world, center.up(height), BlockLog.EnumAxis.Y);
        placeLog(world, center.up(height + 1), BlockLog.EnumAxis.Y);
        return true;
    }

    private BlockPos growBranch(World world, BlockPos start, int dx, int dz,
                                int length, int verticalOffset) {
        BlockPos previous = start;
        for (int step = 1; step <= length; step++) {
            int y = (step + verticalOffset) / 2;
            BlockPos next = start.add(dx * step, y, dz * step);
            BlockLog.EnumAxis axis = dx != 0 ? BlockLog.EnumAxis.X : BlockLog.EnumAxis.Z;
            if (dx != 0 && dz != 0) {
                axis = step % 2 == 0 ? BlockLog.EnumAxis.X : BlockLog.EnumAxis.Z;
            }
            placeLine(world, previous, next, axis);
            previous = next;
        }
        return previous;
    }

    private void placeLine(World world, BlockPos from, BlockPos to, BlockLog.EnumAxis axis) {
        int length = Math.max(Math.abs(to.getX() - from.getX()),
                Math.max(Math.abs(to.getY() - from.getY()), Math.abs(to.getZ() - from.getZ())));
        for (int i = 1; i <= Math.max(1, length); i++) {
            double fraction = i / (double) Math.max(1, length);
            BlockPos pos = new BlockPos(
                    Math.round(from.getX() + (to.getX() - from.getX()) * fraction),
                    Math.round(from.getY() + (to.getY() - from.getY()) * fraction),
                    Math.round(from.getZ() + (to.getZ() - from.getZ()) * fraction));
            placeLog(world, pos, axis);
        }
    }

    private void placeCanopyBlob(World world, Random random, BlockPos center,
                                 int radius, boolean small) {
        for (int y = -2; y <= 2; y++) {
            int layerRadius = Math.max(1, radius - Math.abs(y));
            placeCanopyLayer(world, random, center.up(y), layerRadius, small);
        }
    }

    private void placeCanopyLayer(World world, Random random, BlockPos center,
                                  int radius, boolean small) {
        int radiusSquared = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int distance = dx * dx + dz * dz;
                if (distance <= radiusSquared + random.nextInt(3) - 1) {
                    placeLeaf(world, center.add(dx, 0, dz), small);
                }
            }
        }
    }

    private boolean hasSpace(World world, BlockPos base, int radius, int height) {
        if (base.getY() < 1 || base.getY() + height + 3 >= world.getHeight()) {
            return false;
        }
        for (int y = 0; y <= height + 2; y++) {
            // The old 5x5 check at ground level rejected nearly every forest
            // slope because neighbouring terrain occupied the sapling's Y
            // level. Only the trunk needs clearance near the ground.
            int scanRadius = y == 0 ? 0 : (y < 3 ? 1 : radius);
            for (int dx = -scanRadius; dx <= scanRadius; dx++) {
                for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                    BlockPos pos = base.add(dx, y, dz);
                    if (!world.isBlockLoaded(pos) || !canReplace(world, pos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean canGrowOn(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS || block == Blocks.DIRT
                || block == Blocks.FARMLAND || block == Blocks.MYCELIUM
                || state.getMaterial() == Material.GRASS
                || state.getMaterial() == Material.GROUND;
    }

    private boolean canReplace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return block.isAir(state, world, pos)
                || block.isLeaves(state, world, pos)
                || block == Blocks.VINE
                || block == RainbowTreeRegistration.RAINBOW_SAPLING
                || block.isReplaceable(world, pos);
    }

    private void placeLog(World world, BlockPos pos, BlockLog.EnumAxis axis) {
        if (canReplace(world, pos)
                || world.getBlockState(pos).getBlock() == RainbowTreeRegistration.RAINBOW_LEAVES) {
            world.setBlockState(pos,
                    GTFOTrees.RAINBOWWOOD_TREE.logState.withProperty(BlockLog.LOG_AXIS, axis), 2);
        }
    }

    private void placeLeaf(World world, BlockPos pos, boolean small) {
        if (canReplace(world, pos)) {
            world.setBlockState(pos, RainbowTreeRegistration.RAINBOW_LEAVES.generatedState(small), 2);
        }
    }
}
