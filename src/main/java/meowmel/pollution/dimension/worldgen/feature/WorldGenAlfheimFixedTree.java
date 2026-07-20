package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.common.block.ModBlocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** The shared 473-placement schema behind the source Dream Tree and Sad Oak. */
public final class WorldGenAlfheimFixedTree {

    private static final List<Placement> SCHEMA = loadSchema();

    private WorldGenAlfheimFixedTree() {
    }

    public static boolean generateDreamTree(World world, Random random, BlockPos origin) {
        return generate(world, origin, ModBlocks.dreamwood.getDefaultState(), stableOakLeaves());
    }

    public static boolean generateSadOak(World world, Random random, BlockPos origin) {
        return generate(world, origin, Blocks.LOG.getDefaultState(), stableOakLeaves());
    }

    private static boolean generate(World world, BlockPos origin, IBlockState wood, IBlockState leaves) {
        if (!locationIsValidSpawn(world, origin)) {
            return false;
        }
        for (Placement placement : SCHEMA) {
            BlockPos pos = origin.add(placement.x, placement.y, placement.z);
            IBlockState state = placement.kind == 'L' ? leaves : orient(wood, placement.kind);
            if (canBePlaced(world, pos, placement.kind == 'L')) {
                world.setBlockState(pos, state, 2);
            }
        }
        return true;
    }

    private static boolean locationIsValidSpawn(World world, BlockPos origin) {
        int distanceToAir = 0;
        while (!world.isAirBlock(origin.up(distanceToAir))) {
            distanceToAir++;
            if (distanceToAir > 1) {
                return false;
            }
        }
        BlockPos surface = origin.up(distanceToAir - 1);
        IBlockState at = world.getBlockState(surface);
        IBlockState above = world.getBlockState(surface.up());
        IBlockState below = world.getBlockState(surface.down());
        if (above.getMaterial() != Material.AIR) {
            return false;
        }
        Material material = at.getMaterial();
        return material == Material.GRASS || material == Material.GROUND
                || ((material == Material.SNOW || material == Material.PLANTS)
                && (below.getMaterial() == Material.GRASS || below.getMaterial() == Material.GROUND));
    }

    private static boolean canBePlaced(World world, BlockPos pos, boolean leaves) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            return false;
        }
        IBlockState at = world.getBlockState(pos);
        if (at.getBlockHardness(world, pos) == -1.0F) {
            return false;
        }
        Block block = at.getBlock();
        boolean replaceable = block.isAir(at, world, pos) || block.isReplaceable(world, pos)
                || block.isLeaves(at, world, pos) || at.getMaterial() == Material.PLANTS;
        return leaves ? replaceable : replaceable || at.getMaterial() == Material.WOOD
                || at.getMaterial() == Material.GROUND || at.getMaterial() == Material.GRASS;
    }

    private static IBlockState orient(IBlockState state, char kind) {
        if (!(state.getBlock() instanceof BlockLog)) {
            return state;
        }
        BlockLog.EnumAxis axis = kind == 'X' ? BlockLog.EnumAxis.X
                : kind == 'Z' ? BlockLog.EnumAxis.Z : BlockLog.EnumAxis.Y;
        return state.withProperty(BlockLog.LOG_AXIS, axis);
    }

    private static IBlockState stableOakLeaves() {
        return Blocks.LEAVES.getDefaultState()
                .withProperty(BlockLeaves.CHECK_DECAY, false)
                .withProperty(BlockLeaves.DECAYABLE, true);
    }

    private static List<Placement> loadSchema() {
        InputStream stream = WorldGenAlfheimFixedTree.class.getResourceAsStream(
                "/assets/pollution/alfheim/dream_tree.csv");
        if (stream == null) {
            throw new IllegalStateException("Missing Alfheim dream-tree schema");
        }
        List<Placement> result = new ArrayList<>(473);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                result.add(new Placement(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
                        Integer.parseInt(values[2]), values[3].charAt(0)));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read Alfheim dream-tree schema", exception);
        }
        return Collections.unmodifiableList(result);
    }

    private static final class Placement {
        private final int x;
        private final int y;
        private final int z;
        private final char kind;

        private Placement(int x, int y, int z, char kind) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.kind = kind;
        }
    }
}
