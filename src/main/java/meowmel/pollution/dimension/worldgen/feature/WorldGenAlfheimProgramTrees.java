package meowmel.pollution.dimension.worldgen.feature;

import meowmel.pollution.common.block.alfheim.AlfheimBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.common.block.ModBlocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Port of the WE_WorldTreeGen entry lists used by Alfheim's forest biomes. */
public final class WorldGenAlfheimProgramTrees {

    private static final List<Entry> ISLAND_FOREST_ENTRIES = createEntries(Set.ISLAND_FOREST);
    private static final List<Entry> FOREST_ENTRIES = createEntries(Set.FOREST);
    private static final List<Entry> PLATEAU_ENTRIES = createEntries(Set.PLATEAU);

    public enum Set {
        ISLAND_FOREST,
        FOREST,
        PLATEAU
    }

    private WorldGenAlfheimProgramTrees() {
    }

    public static void generate(World world, Random random, BlockPos chunkOrigin, Set set) {
        for (Entry entry : entriesFor(set)) {
            if (random.nextInt(entry.dispersion) != 0) {
                continue;
            }
            for (int attempt = 0; attempt < entry.treesPerChunk; attempt++) {
                // Forge 1.12 populates against the prepared east/south 2x2
                // chunk area. The +8 adaptation keeps canopies out of
                // unprepared west/north chunks without changing tree counts.
                int x = chunkOrigin.getX() + random.nextInt(16) + 8;
                int z = chunkOrigin.getZ() + random.nextInt(16) + 8;
                BlockPos pos = new BlockPos(x, world.getHeight(x, z), z);
                int woodIndex = random.nextInt(entry.smallTrees.length);
                boolean big = entry.chanceForBig > 0 && random.nextInt(entry.chanceForBig) == 0;
                if (big) {
                    entry.bigTrees[woodIndex].generate(world, random, pos);
                } else {
                    entry.smallTrees[woodIndex].generate(world, random, pos);
                }
            }
        }
    }

    private static List<Entry> createEntries(Set set) {
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
        return Collections.unmodifiableList(entries);
    }

    private static List<Entry> entriesFor(Set set) {
        switch (set) {
            case ISLAND_FOREST:
                return ISLAND_FOREST_ENTRIES;
            case FOREST:
                return FOREST_ENTRIES;
            case PLATEAU:
            default:
                return PLATEAU_ENTRIES;
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
        private final WorldGenWorldEngineTree[] smallTrees;
        private final WorldGenWorldEngineBigTree[] bigTrees;
        private final int dispersion;
        private final int treesPerChunk;
        private final int chanceForBig;

        private Entry(IBlockState[] woods, IBlockState leaves, Block vine, int dispersion,
                      int treesPerChunk, int chanceForBig, int minHeight,
                      boolean vinesGrowLeaves, boolean vinesGrowLog, int trunkSize) {
            this.smallTrees = new WorldGenWorldEngineTree[woods.length];
            this.bigTrees = new WorldGenWorldEngineBigTree[woods.length];
            for (int i = 0; i < woods.length; i++) {
                this.smallTrees[i] = new WorldGenWorldEngineTree(
                        woods[i], leaves, vine, minHeight, vinesGrowLeaves, vinesGrowLog);
                this.bigTrees[i] = new WorldGenWorldEngineBigTree(
                        woods[i], leaves, trunkSize,
                        12, 4, 0.618D, 0.381D, 1.0D, 1.0D);
            }
            this.dispersion = dispersion;
            this.treesPerChunk = treesPerChunk;
            this.chanceForBig = chanceForBig;
        }
    }
}
