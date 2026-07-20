package meowmel.pollution.dimension.worldgen.feature;

import meowmel.pollution.common.block.alfheim.AlfheimBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.common.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Port of the WE_WorldTreeGen entry lists used by Alfheim's forest biomes. */
public final class WorldGenAlfheimProgramTrees {

    public enum Set {
        ISLAND_FOREST,
        FOREST,
        PLATEAU
    }

    private WorldGenAlfheimProgramTrees() {
    }

    public static void generate(World world, Random random, BlockPos chunkOrigin, Set set) {
        List<Entry> entries = new ArrayList<>();
        if (set == Set.ISLAND_FOREST) {
            entries.add(new Entry(new IBlockState[]{Blocks.LOG.getDefaultState()}, Blocks.LEAVES.getDefaultState(),
                    AlfheimBlocks.RED_GRAPES[0], 2, 1, 0, 6, true, false, 1));
            addStandardEntries(entries, 2, 1, true);
        } else if (set == Set.FOREST) {
            addStandardEntries(entries, 2, 1, false);
        } else {
            addStandardEntries(entries, 32, 3, false);
        }

        for (Entry entry : entries) {
            if (random.nextInt(entry.dispersion) != 0) {
                continue;
            }
            for (int attempt = 0; attempt < entry.treesPerChunk; attempt++) {
                int x = chunkOrigin.getX() + random.nextInt(16);
                int z = chunkOrigin.getZ() + random.nextInt(16);
                BlockPos pos = world.getHeight(new BlockPos(x, 0, z));
                IBlockState wood = entry.woods[random.nextInt(entry.woods.length)];
                boolean big = entry.chanceForBig > 0 && random.nextInt(entry.chanceForBig) == 0;
                if (big) {
                    new WorldGenWorldEngineBigTree(wood, entry.leaves, entry.trunkSize,
                            12, 4, 0.618D, 0.381D, 1.0D, 1.0D).generate(world, random, pos);
                } else {
                    new WorldGenWorldEngineTree(wood, entry.leaves, entry.vine, entry.minHeight,
                            entry.vinesGrowLeaves, entry.vinesGrowLog).generate(world, random, pos);
                }
            }
        }
    }

    private static void addStandardEntries(List<Entry> entries, int dispersion, int perChunk, boolean islandRates) {
        IBlockState livingwood = ModBlocks.livingwood.getDefaultState();
        IBlockState dreamwood = ModBlocks.dreamwood.getDefaultState();
        IBlockState oak = Blocks.LOG.getDefaultState();
        IBlockState darkOak = Blocks.LOG2.getStateFromMeta(1);
        IBlockState oakLeaves = Blocks.LEAVES.getDefaultState();
        IBlockState darkLeaves = Blocks.LEAVES2.getStateFromMeta(1);

        // The four source entries and all their WE_BigTreeGen parameters remain intact.
        // Livingwood and dreamwood are additional legal state choices inside those slots,
        // so their addition does not increase the source tree density.
        entries.add(new Entry(new IBlockState[]{oak, livingwood, dreamwood}, oakLeaves, null,
                dispersion, perChunk, 1, 4, false, false, 1));
        entries.add(new Entry(new IBlockState[]{oak, livingwood, dreamwood}, oakLeaves, null,
                dispersion, perChunk, islandRates ? 2 : 1, 4, false, false, 2));
        entries.add(new Entry(new IBlockState[]{darkOak, livingwood, dreamwood}, darkLeaves, null,
                dispersion, perChunk, 1, 4, false, false, 1));
        entries.add(new Entry(new IBlockState[]{darkOak, livingwood, dreamwood}, darkLeaves, null,
                dispersion, perChunk, islandRates ? 2 : 1, 4, false, false, 2));
    }

    private static final class Entry {
        private final IBlockState[] woods;
        private final IBlockState leaves;
        private final Block vine;
        private final int dispersion;
        private final int treesPerChunk;
        private final int chanceForBig;
        private final int minHeight;
        private final boolean vinesGrowLeaves;
        private final boolean vinesGrowLog;
        private final int trunkSize;

        private Entry(IBlockState[] woods, IBlockState leaves, Block vine, int dispersion,
                      int treesPerChunk, int chanceForBig, int minHeight,
                      boolean vinesGrowLeaves, boolean vinesGrowLog, int trunkSize) {
            this.woods = woods;
            this.leaves = leaves;
            this.vine = vine;
            this.dispersion = dispersion;
            this.treesPerChunk = treesPerChunk;
            this.chanceForBig = chanceForBig;
            this.minHeight = minHeight;
            this.vinesGrowLeaves = vinesGrowLeaves;
            this.vinesGrowLog = vinesGrowLog;
            this.trunkSize = trunkSize;
        }
    }
}
