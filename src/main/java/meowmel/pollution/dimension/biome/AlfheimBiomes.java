package meowmel.pollution.dimension.biome;

import meowmel.pollution.common.block.alfheim.AlfheimBlocks;
import meowmel.pollution.dimension.biome.biomes.AlfheimBiome;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import vazkii.botania.common.block.ModBlocks;

/** Alfheim's twelve source biome profiles, ported from the original module. */
public final class AlfheimBiomes {

    // BiomeAlfheim.kt: waterColorMultiplier is 0x00FFFF outside the
    // omitted winter/Ragnarok systems.
    private static final int WATER_COLOR = 0x00FFFF;

    public static final Biome FIELD = biome("Alfheim Field", -0.55, 0.82, 1.8, 3, 250.0, 2.0, 71, 2,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.FIELD);
    public static final Biome GIANT_FLOWER_FIELD = biome("Alfheim Mutated Field", 1.0, 10.0, 1.8, 3, 250.0, 2.0, 71, 2,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.FLOWER_FIELD);
    public static final Biome BEACH = biome("Alfheim Beach", -0.5, -0.35, 1.33, 3, 250.0, 1.4, 65, 4,
            AlfheimBlocks.ELVEN_SAND.getDefaultState(), ModBlocks.livingrock.getDefaultState(), AlfheimBiome.Decoration.NONE);
    public static final Biome SANDBANK = biome("Alfheim Sandbank", -0.41, -0.38, 1.33, 3, 250.0, 0.5, 62, 2,
            AlfheimBlocks.ELVEN_SAND.getDefaultState(), ModBlocks.livingrock.getDefaultState(), AlfheimBiome.Decoration.NONE);
    public static final Biome RIVER = biome("Alfheim River", -0.48, -0.38, 1.33, 3, 250.0, 1.0, 58, 4,
            Blocks.GRAVEL.getDefaultState(), Blocks.CLAY.getDefaultState(), AlfheimBiome.Decoration.NONE);
    public static final Biome LOW_PLATEAU = biome("Alfheim Low Plateau", 0.2, 0.78, 1.8, 3, 250.0, 0.8, 96, 1,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.LOW_PLATEAU);
    public static final Biome MID_PLATEAU = biome("Alfheim Mid Plateau", 0.3, 0.75, 1.8, 3, 250.0, 1.6, 120, 1,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.MID_PLATEAU);
    public static final Biome HIGH_PLATEAU = biome("Alfheim High Plateau", 0.4, 0.7, 1.8, 3, 250.0, 2.4, 144, 1,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.HIGH_PLATEAU);
    public static final Biome HIGH_PLATEAU_FOREST = biome("Alfheim High Plateau Forest", 0.49, 0.58, 1.8, 3, 250.0, 2.4, 144, 1,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.HIGH_PLATEAU_FOREST);
    public static final Biome HIGH_PLATEAU_FIELD = biome("Alfheim High Plateau Field", 0.43, 0.65, 1.8, 3, 250.0, 2.4, 144, 1,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.HIGH_PLATEAU_FIELD);
    public static final Biome ISLAND_FOREST = biome("Alfheim Island Forest", -10.0, 0.82, 1.8, 3, 250.0, 1.0, 75, 4,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.ISLAND_FOREST);
    public static final Biome PIT_FOREST = biome("Alfheim Pit Forest", 0.82, 1.0, 1.8, 3, 250.0, 1.0, 71, 4,
            Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), AlfheimBiome.Decoration.PIT_FOREST);

    public static final Biome[] ALL = {
            FIELD, GIANT_FLOWER_FIELD, BEACH, SANDBANK, RIVER,
            LOW_PLATEAU, MID_PLATEAU, HIGH_PLATEAU, HIGH_PLATEAU_FOREST,
            HIGH_PLATEAU_FIELD, ISLAND_FOREST, PIT_FOREST
    };

    private AlfheimBiomes() {
    }

    private static Biome biome(String name, double minMapValue, double maxMapValue,
                               double persistence, int octaves, double scaleX, double scaleY,
                               int surfaceHeight, int interpolateQuality,
                               net.minecraft.block.state.IBlockState top,
                               net.minecraft.block.state.IBlockState filler,
                               AlfheimBiome.Decoration decoration) {
        return new AlfheimBiome(name, minMapValue, maxMapValue, persistence, octaves, scaleX, scaleY,
                surfaceHeight, interpolateQuality, top, filler, decoration, WATER_COLOR);
    }
}
