package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

/** 1.12 state-based port of WorldEngine's WE_TreeGen. */
public final class WorldGenWorldEngineTree extends WorldGenerator {

    private final IBlockState wood;
    private final IBlockState leaves;
    private final Block vine;
    private final int minTreeHeight;
    private final boolean vinesGrowLeaves;
    private final boolean vinesGrowLog;

    public WorldGenWorldEngineTree(IBlockState wood, IBlockState leaves, Block vine,
                                   int minTreeHeight, boolean vinesGrowLeaves, boolean vinesGrowLog) {
        super(false);
        this.wood = wood;
        this.leaves = leaves;
        this.vine = vine;
        this.minTreeHeight = minTreeHeight;
        this.vinesGrowLeaves = vinesGrowLeaves;
        this.vinesGrowLog = vinesGrowLog;
    }

    @Override
    public boolean generate(World world, Random random, BlockPos origin) {
        int height = random.nextInt(3) + minTreeHeight;
        if (origin.getY() < 1 || origin.getY() + height + 1 > world.getHeight()) {
            return false;
        }

        for (int y = origin.getY(); y <= origin.getY() + height + 1; y++) {
            int radius = y == origin.getY() ? 0 : (y >= origin.getY() + height - 1 ? 2 : 1);
            for (int x = origin.getX() - radius; x <= origin.getX() + radius; x++) {
                for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; z++) {
                    BlockPos check = new BlockPos(x, y, z);
                    if (y < 0 || y >= world.getHeight() || !isReplaceable(world, check)) {
                        return false;
                    }
                }
            }
        }

        BlockPos soilPos = origin.down();
        IBlockState soil = world.getBlockState(soilPos);
        if (!soil.getBlock().canSustainPlant(soil, world, soilPos, EnumFacing.UP, (IPlantable) Blocks.SAPLING)
                || origin.getY() >= world.getHeight() - height - 1) {
            return false;
        }
        soil.getBlock().onPlantGrow(soil, world, soilPos, origin);

        for (int y = origin.getY() + height - 3; y <= origin.getY() + height; y++) {
            int fromTop = y - origin.getY() - height;
            int radius = 1 - fromTop / 2;
            for (int x = origin.getX() - radius; x <= origin.getX() + radius; x++) {
                int dx = x - origin.getX();
                for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; z++) {
                    int dz = z - origin.getZ();
                    if (Math.abs(dx) != radius || Math.abs(dz) != radius
                            || random.nextInt(2) == 0 || fromTop == 0) {
                        BlockPos leafPos = new BlockPos(x, y, z);
                        IBlockState state = world.getBlockState(leafPos);
                        if (state.getBlock().isAir(state, world, leafPos)
                                || state.getBlock().isLeaves(state, world, leafPos)) {
                            setBlockAndNotifyAdequately(world, leafPos, leaves);
                        }
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            BlockPos trunkPos = origin.up(y);
            IBlockState state = world.getBlockState(trunkPos);
            if (state.getBlock().isAir(state, world, trunkPos)
                    || state.getBlock().isLeaves(state, world, trunkPos)) {
                setBlockAndNotifyAdequately(world, trunkPos, verticalWood(wood));
                if (vinesGrowLog && y > 0 && vine != null) {
                    placeTrunkVine(world, random, trunkPos.west(), 8);
                    placeTrunkVine(world, random, trunkPos.east(), 2);
                    placeTrunkVine(world, random, trunkPos.north(), 1);
                    placeTrunkVine(world, random, trunkPos.south(), 4);
                }
            }
        }

        if (vinesGrowLeaves && vine != null) {
            for (int y = origin.getY() + height - 3; y <= origin.getY() + height; y++) {
                int radius = 2 - (y - origin.getY() - height) / 2;
                for (int x = origin.getX() - radius; x <= origin.getX() + radius; x++) {
                    for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; z++) {
                        BlockPos leafPos = new BlockPos(x, y, z);
                        IBlockState state = world.getBlockState(leafPos);
                        if (!state.getBlock().isLeaves(state, world, leafPos)) {
                            continue;
                        }
                        maybeGrowLeafVine(world, random, leafPos.west(), 8);
                        maybeGrowLeafVine(world, random, leafPos.east(), 2);
                        maybeGrowLeafVine(world, random, leafPos.north(), 1);
                        maybeGrowLeafVine(world, random, leafPos.south(), 4);
                    }
                }
            }
        }
        return true;
    }

    private void placeTrunkVine(World world, Random random, BlockPos pos, int meta) {
        if (random.nextInt(3) > 0 && world.isAirBlock(pos)) {
            setBlockAndNotifyAdequately(world, pos, vine.getStateFromMeta(meta));
        }
    }

    private void maybeGrowLeafVine(World world, Random random, BlockPos pos, int meta) {
        if (random.nextInt(4) == 0 && world.isAirBlock(pos)) {
            growVines(world, pos, meta);
        }
    }

    private void growVines(World world, BlockPos pos, int meta) {
        IBlockState vineState = vine.getStateFromMeta(meta);
        setBlockAndNotifyAdequately(world, pos, vineState);
        BlockPos cursor = pos;
        for (int remaining = 4; remaining > 0; remaining--) {
            cursor = cursor.down();
            if (!world.isAirBlock(cursor)) {
                break;
            }
            setBlockAndNotifyAdequately(world, cursor, vineState);
        }
    }

    private static boolean isReplaceable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block.isAir(state, world, pos) || block.isLeaves(state, world, pos)
                || block.isWood(world, pos) || block == Blocks.GRASS || block == Blocks.DIRT
                || block == Blocks.LOG || block == Blocks.LOG2 || block == Blocks.SAPLING
                || block instanceof BlockVine || material == Material.AIR || material == Material.LEAVES;
    }

    private static IBlockState verticalWood(IBlockState state) {
        if (state.getBlock() instanceof BlockLog) {
            return state.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y);
        }
        return state;
    }
}
