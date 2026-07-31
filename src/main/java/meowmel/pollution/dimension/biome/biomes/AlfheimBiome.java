package meowmel.pollution.dimension.biome.biomes;

import meowmel.pollution.common.block.alfheim.AlfheimBlocks;
import meowmel.pollution.dimension.biome.AlfheimBiomes;
import meowmel.pollution.dimension.worldgen.WorldEngineNoise;
import meowmel.pollution.dimension.worldgen.feature.WorldGenAlfheimFixedTree;
import meowmel.pollution.dimension.worldgen.feature.WorldGenAlfheimGiantFlower;
import meowmel.pollution.dimension.worldgen.feature.WorldGenAlfheimProgramTrees;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import vazkii.botania.common.block.BlockModFlower;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.AltGrassVariant;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.handler.ConfigHandler;

import java.util.Random;

/**
 * Java/1.12 representation of Alfheim's WorldEngine biome definitions.
 *
 * <p>The original Alfheim biome classes clear vanilla spawns and use custom
 * trees, flowers and blocks. Alfheim-only assets are replaced with vanilla
 * equivalents, while flower and mushroom decoration uses Botania's registered
 * blocks, which are available in this project.</p>
 */
public class AlfheimBiome extends Biome {

    private static final int ALFHEIM_GRASS_COLOR = 0x08F500;
    private static final int ALFHEIM_SKY_COLOR = 0x266EFF;
    private static final WorldGenTrees IRIDESCENCE_TREE = new WorldGenTrees(false);
    private static final AltGrassVariant[] IRIDESCENCE_GRASS = AltGrassVariant.values();

    public enum Decoration {
        NONE,
        FIELD,
        FLOWER_FIELD,
        ISLAND_FOREST,
        PIT_FOREST,
        LOW_PLATEAU,
        MID_PLATEAU,
        HIGH_PLATEAU,
        HIGH_PLATEAU_FOREST,
        HIGH_PLATEAU_FIELD
    }

    private final Decoration decoration;
    private final int waterColor;
    private final int surfaceHeight;
    private final double terrainPersistence;
    private final int terrainOctaves;
    private final double terrainScaleX;
    private final double terrainScaleY;
    private final int interpolateQuality;
    private final WorldEngineNoise.NoiseProfile terrainNoiseProfile;

    public AlfheimBiome(String name, double minMapValue, double maxMapValue,
                        double persistence, int octaves, double scaleX, double scaleY,
                        int surfaceHeight, int interpolateQuality,
                        IBlockState topBlock, IBlockState fillerBlock,
                        Decoration decoration, int waterColor) {
        super(new BiomeProperties(name)
                .setBaseHeight((surfaceHeight - 64.0F) / 64.0F)
                .setHeightVariation((float) Math.max(0.1D, scaleY / 2.0D))
                .setTemperature(0.5F)
                .setRainfall(0.8F)
                .setWaterColor(waterColor));
        this.topBlock = topBlock;
        this.fillerBlock = fillerBlock;
        this.decoration = decoration;
        this.waterColor = waterColor;
        this.surfaceHeight = surfaceHeight;
        this.terrainPersistence = persistence;
        this.terrainOctaves = octaves;
        this.terrainScaleX = scaleX;
        this.terrainScaleY = scaleY;
        this.interpolateQuality = interpolateQuality;
        this.terrainNoiseProfile = WorldEngineNoise.profile(persistence, octaves);

        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
    }

    @Override
    public int getWaterColorMultiplier() {
        return waterColor;
    }

    /** BiomeAlfheim.kt sets grassColor to 0x08F500 for every Alfheim biome. */
    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        return getModdedBiomeGrassColor(ALFHEIM_GRASS_COLOR);
    }

    /**
     * The source does not override foliage color, so replacement vanilla
     * leaves continue to use the normal overworld climate calculation.
     */
    @Override
    public int getFoliageColorAtPos(BlockPos pos) {
        return super.getFoliageColorAtPos(pos);
    }

    /** BiomeAlfheim.kt returns 0x266EFF when seasonal logic is disabled. */
    @Override
    public int getSkyColorByTemp(float currentTemperature) {
        return ALFHEIM_SKY_COLOR;
    }

    /** Original WorldEngine surface height, before Alfheim's source offset of -7. */
    public int getSurfaceHeight() {
        return surfaceHeight - 7;
    }

    public double getTerrainPersistence() {
        return terrainPersistence;
    }

    public int getTerrainOctaves() {
        return terrainOctaves;
    }

    public double getTerrainScaleX() {
        return terrainScaleX;
    }

    public double getTerrainScaleY() {
        return terrainScaleY;
    }

    public int getInterpolateQuality() {
        return interpolateQuality;
    }

    public WorldEngineNoise.NoiseProfile getTerrainNoiseProfile() {
        return terrainNoiseProfile;
    }

    /**
     * Equivalent to the random end offsets in each source WE_BiomeLayer.
     * The returned value is the number of blocks below the surface block.
     */
    public int getFillerDepth(Random rand) {
        switch (decoration) {
            case LOW_PLATEAU:
            case MID_PLATEAU:
            case HIGH_PLATEAU:
            case HIGH_PLATEAU_FOREST:
            case HIGH_PLATEAU_FIELD:
                return rand.nextInt(3);
            default:
                return 4 + rand.nextInt(3);
        }
    }

    /**
     * BiomeRiver's second layer replaces its top clay block with gravel only
     * when WE_BiomeLayer's {@code randomEnd(1)} returns zero.
     */
    public IBlockState getGeneratedTopBlock(Random rand) {
        return this == AlfheimBiomes.RIVER && rand.nextInt(2) != 0 ? fillerBlock : topBlock;
    }

    public boolean canGenerateTopUnderwater() {
        return this == AlfheimBiomes.RIVER || topBlock == fillerBlock;
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        MinecraftForge.TERRAIN_GEN_BUS.post(new DecorateBiomeEvent.Pre(worldIn, rand, chunkPos));
        try {
            switch (decoration) {
                case FIELD:
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.GRASS)) {
                        generateSourceGrass(worldIn, rand, pos, true, true, true, true, 1.0D);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.REED)) {
                        generateReeds(worldIn, rand, pos, 32);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.CUSTOM)) {
                        generateWhiteGrapes(worldIn, rand, pos, 4);
                        generateIridescenceFallback(worldIn, rand, pos);
                    }
                    break;
                case FLOWER_FIELD:
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.GRASS)) {
                        generateSourceGrass(worldIn, rand, pos, true, true, false, false, 2.0D);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.CUSTOM)) {
                        generateMutatedFlower(worldIn, rand, pos);
                    }
                    break;
                case ISLAND_FOREST:
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.TREE)) {
                        WorldGenAlfheimProgramTrees.generate(worldIn, rand, pos,
                                WorldGenAlfheimProgramTrees.Set.ISLAND_FOREST);
                        generateFixedTrees(worldIn, rand, pos, 1, 2);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.GRASS)) {
                        generateSourceGrass(worldIn, rand, pos, true, false, false, false, 2.5D);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.CUSTOM)) {
                        generateWhiteGrapes(worldIn, rand, pos, 2);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.PUMPKIN)) {
                        generateMelonsAndPumpkins(worldIn, rand, pos);
                    }
                    break;
                case PIT_FOREST:
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.TREE)) {
                        WorldGenAlfheimProgramTrees.generate(worldIn, rand, pos,
                                WorldGenAlfheimProgramTrees.Set.FOREST);
                        generateFixedTrees(worldIn, rand, pos, 1, 2);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.GRASS)) {
                        generateSourceGrass(worldIn, rand, pos, true, false, false, false, 2.5D);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.CUSTOM)) {
                        generateWhiteGrapes(worldIn, rand, pos, 6);
                    }
                    break;
                case LOW_PLATEAU:
                case MID_PLATEAU:
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.TREE)) {
                        WorldGenAlfheimProgramTrees.generate(worldIn, rand, pos,
                                WorldGenAlfheimProgramTrees.Set.PLATEAU);
                        generateFixedTrees(worldIn, rand, pos, 12, 20);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.GRASS)) {
                        generateSourceGrass(worldIn, rand, pos, true, true, true, true, 1.2D);
                    }
                    break;
                case HIGH_PLATEAU:
                case HIGH_PLATEAU_FIELD:
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.GRASS)) {
                        generateSourceGrass(worldIn, rand, pos, true, true, true, true, 1.2D);
                    }
                    break;
                case HIGH_PLATEAU_FOREST:
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.TREE)) {
                        WorldGenAlfheimProgramTrees.generate(worldIn, rand, pos,
                                WorldGenAlfheimProgramTrees.Set.FOREST);
                        generateFixedTrees(worldIn, rand, pos, 1, 2);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.GRASS)) {
                        generateSourceGrass(worldIn, rand, pos, true, true, true, true, 1.2D);
                    }
                    if (canDecorate(worldIn, rand, chunkPos, pos,
                            DecorateBiomeEvent.Decorate.EventType.LILYPAD)) {
                        generateWaterLily(worldIn, rand, pos, 4);
                    }
                    break;
                case NONE:
                default:
                    break;
            }
        } finally {
            MinecraftForge.TERRAIN_GEN_BUS.post(new DecorateBiomeEvent.Post(worldIn, rand, chunkPos));
        }
    }

    private static boolean canDecorate(World world, Random rand, ChunkPos chunkPos,
                                       BlockPos placement,
                                       DecorateBiomeEvent.Decorate.EventType type) {
        return TerrainGen.decorate(world, rand, chunkPos, placement, type);
    }

    /**
     * Direct port of WorldGenGrass's control flow.  The Alfheim-only rainbow
     * variants have no target block, so color 16 falls back to one of the
     * sixteen Botania colors; all standard Botania configuration values are
     * read from the target project's Botania runtime.
     */
    private static void generateSourceGrass(World world, Random rand, BlockPos origin,
                                            boolean grass, boolean flowers, boolean doubleFlowers,
                                            boolean botanicalFlowers, double modifier) {
        if (botanicalFlowers) {
            int patchRadius = Math.min(8, Math.max(1, ConfigHandler.flowerPatchSize));
            int patchChance = Math.max(1, (int) Math.round(ConfigHandler.flowerPatchChance / modifier));
            int placements = Math.max(0, ConfigHandler.flowerDensity * ConfigHandler.flowerPatchChance);

            for (int patch = 0; patch < ConfigHandler.flowerQuantity; patch++) {
                if (rand.nextInt(patchChance) != 0) {
                    continue;
                }
                BlockPos center = surface(world, origin, rand);
                int color = botaniaColor(rand);
                for (int entry = 0; entry < placements; entry++) {
                    BlockPos target = center.add(rand.nextInt(patchRadius * 2) - patchRadius, 0,
                            rand.nextInt(patchRadius * 2) - patchRadius);
                    placeBotaniaFlower(world, rand, target, color);
                }
            }
        }

        for (int mushroom = 0; mushroom < ConfigHandler.mushroomQuantity; mushroom++) {
            int x = rand.nextInt(16) + 8;
            int z = rand.nextInt(16) + 8;
            int y = 4 + rand.nextInt(28);
            BlockPos target = origin.add(x, y, z);
            int color = botaniaColor(rand);
            if (world.isAirBlock(target) && ModBlocks.mushroom.canPlaceBlockAt(world, target)) {
                world.setBlockState(target, ModBlocks.mushroom.getStateFromMeta(color), 2);
            }
        }

        int remaining = (int) Math.round(64.0D * modifier);
        for (int attempts = 256; remaining > 0 && attempts > 0; attempts--) {
            BlockPos target = surface(world, origin, rand);
            if (!isGrassSurface(world, target)) {
                continue;
            }

            int type = rand.nextInt(20);
            if (type > 12) {
                continue;
            }
            if ((type < 5 && !flowers)
                    || (type >= 5 && type <= 10 && !grass)
                    || (type > 10 && !doubleFlowers)) {
                // The source's labeled select block consumes one requested
                // placement even when that vegetation category is disabled.
                remaining--;
                continue;
            }

            if (placeVanillaVegetation(world, rand, target, type)) {
                remaining--;
            }
        }
    }

    private static boolean placeVanillaVegetation(World world, Random rand, BlockPos target, int type) {
        switch (type) {
            case 0:
            case 1:
                world.setBlockState(target, Blocks.YELLOW_FLOWER.getDefaultState(), 2);
                return true;
            case 2:
            case 3:
                world.setBlockState(target, Blocks.RED_FLOWER.getDefaultState(), 2);
                return true;
            case 4:
                world.setBlockState(target, Blocks.RED_FLOWER.getStateFromMeta(rand.nextInt(8) + 1), 2);
                return true;
            case 5:
                world.setBlockState(target, Blocks.TALLGRASS.getStateFromMeta(0), 2);
                return true;
            case 6:
                world.setBlockState(target, Blocks.TALLGRASS.getStateFromMeta(2), 2);
                return true;
            case 7:
            case 8:
            case 9:
            case 10:
                world.setBlockState(target, Blocks.TALLGRASS.getStateFromMeta(1), 2);
                return true;
            case 11:
                Blocks.DOUBLE_PLANT.placeAt(world, target, BlockDoublePlant.EnumPlantType.GRASS, 2);
                return true;
            case 12:
                int meta = rand.nextInt(6);
                if (meta == 2) {
                    // Meta 2 is the same double grass already represented by
                    // type 11; WorldGenGrass rejects it without decrementing.
                    return false;
                }
                Blocks.DOUBLE_PLANT.placeAt(world, target,
                        BlockDoublePlant.EnumPlantType.byMetadata(meta), 2);
                return true;
            default:
                return false;
        }
    }

    private static void placeBotaniaFlower(World world, Random rand, BlockPos target) {
        placeBotaniaFlower(world, rand, target, botaniaColor(rand));
    }

    private static void placeBotaniaFlower(World world, Random rand, BlockPos target, int color) {
        if (!isGrassSurface(world, target)) {
            return;
        }
        world.setBlockState(target, ModBlocks.flower.getStateFromMeta(color), 2);
        if (rand.nextDouble() < ConfigHandler.flowerTallChance) {
            BlockModFlower.placeDoubleFlower(world, target, EnumDyeColor.byMetadata(color), 2);
        }
    }

    private static int botaniaColor(Random rand) {
        // Source color 16 selects Alfheim rainbow blocks.  Those blocks are
        // absent, and the requested fallback is an available Botania color.
        int sourceColor = rand.nextInt(17);
        return sourceColor == 16 ? rand.nextInt(16) : sourceColor;
    }

    private static void generateReeds(World world, Random rand, BlockPos origin, int count) {
        for (int i = 0; i < count; i++) {
            BlockPos target = decoratedSurface(world, origin, rand);
            if (!Blocks.REEDS.canPlaceBlockAt(world, target)) {
                continue;
            }
            for (int height = 0, max = 2 + rand.nextInt(4); height < max && world.isAirBlock(target.up(height)); height++) {
                world.setBlockState(target.up(height), Blocks.REEDS.getDefaultState(), 2);
            }
        }
    }

    private static void generateWaterLily(World world, Random rand, BlockPos origin, int count) {
        for (int i = 0; i < count; i++) {
            int x = origin.getX() + rand.nextInt(16) + 8;
            int z = origin.getZ() + rand.nextInt(16) + 8;
            if (world.getBiome(new BlockPos(x, 0, z)) == AlfheimBiomes.RIVER) {
                continue;
            }
            BlockPos target = topLiquid(world, x, z);
            if (target != null && world.isAirBlock(target)
                    && Blocks.WATERLILY.canPlaceBlockAt(world, target)) {
                world.setBlockState(target, Blocks.WATERLILY.getDefaultState(), 2);
            }
        }
    }

    /** Direct port of WorldGenGrapesWhiteAlfheim, including its +8 chunk offset. */
    private static void generateWhiteGrapes(World world, Random rand, BlockPos origin, int count) {
        for (int i = 0; i < count; i++) {
            int x = origin.getX() + rand.nextInt(16) + 8;
            int z = origin.getZ() + rand.nextInt(16) + 8;
            if (world.getBiome(new BlockPos(x, 0, z)) == AlfheimBiomes.RIVER) {
                continue;
            }
            BlockPos target = topLiquid(world, x, z);
            if (target != null && world.isAirBlock(target)
                    && AlfheimBlocks.WHITE_GRAPE.canBlockStay(world, target,
                    AlfheimBlocks.WHITE_GRAPE.getDefaultState())) {
                world.setBlockState(target, AlfheimBlocks.WHITE_GRAPE.getDefaultState(), 3);
            }
        }
    }

    private static BlockPos topLiquid(World world, int x, int z) {
        int top = world.getHeight(new BlockPos(x, 0, z)).getY();
        for (int y = top; y > 0; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (world.getBlockState(pos).getMaterial().isLiquid()) {
                return pos.up();
            }
        }
        return null;
    }

    /** Port of WE_StructureGen's independent Sad Oak and Dream Tree rolls. */
    private static void generateFixedTrees(World world, Random rand, BlockPos origin,
                                           int sadOakRarity, int dreamTreeRarity) {
        if (rand.nextInt(sadOakRarity) == 0) {
            int x = origin.getX() + rand.nextInt(16) + 8;
            int z = origin.getZ() + rand.nextInt(16) + 8;
            BlockPos target = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            WorldGenAlfheimFixedTree.generateSadOak(world, rand, target);
        }
        if (rand.nextInt(dreamTreeRarity) == 0) {
            int x = origin.getX() + rand.nextInt(16) + 8;
            int z = origin.getZ() + rand.nextInt(16) + 8;
            BlockPos target = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            WorldGenAlfheimFixedTree.generateDreamTree(world, rand, target);
        }
    }

    /** Direct port of WorldGenMelonPumpkins with the omitted snow-grass check removed. */
    private static void generateMelonsAndPumpkins(World world, Random rand, BlockPos origin) {
        if (rand.nextInt(10) != 0) {
            return;
        }
        BlockPos center = origin.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8);
        net.minecraft.block.Block block = rand.nextBoolean() ? Blocks.MELON_BLOCK : Blocks.PUMPKIN;
        int remaining = rand.nextInt(8) + 4;
        for (int retries = 63; retries > 0 && remaining > 0; retries--) {
            BlockPos target = surfaceAt(world, center.add(rand.nextInt(8) - 4, 0, rand.nextInt(8) - 4));
            if (block.canPlaceBlockAt(world, target) && world.getBlockState(target.down()).getBlock() == Blocks.GRASS) {
                world.setBlockState(target, block.getDefaultState(), 2);
                remaining--;
            }
        }
    }

    /**
     * Port of WorldGenIridescence. Missing iris dirt and colored trees use
     * Botania alt-grass and a vanilla tree, while the source's enchanted-soil
     * branch can use its original Botania block directly.
     */
    private static void generateIridescenceFallback(World world, Random rand, BlockPos origin) {
        if (rand.nextInt(64) != 0) {
            return;
        }
        BlockPos target = surface(world, origin, rand);
        BlockPos ground = target.down();

        // ItemColorSeeds.addBlockSwapper(..., 1000) resolves directly to
        // Botania enchanted soil and returns before the tree roll.
        if (rand.nextInt(20) == 0) {
            world.setBlockState(ground, ModBlocks.enchantedSoil.getDefaultState(), 3);
            return;
        }

        IBlockState groundState = world.getBlockState(ground);
        if (!isPlainGrassOrDirt(groundState)) {
            return;
        }

        AltGrassVariant variant;
        if (rand.nextInt(3) == 0) {
            // The source chooses Botania grass-seed metadata 3..8, which maps
            // in 1.12 to the six AltGrassVariant values in declaration order.
            variant = IRIDESCENCE_GRASS[rand.nextInt(IRIDESCENCE_GRASS.length)];
        } else {
            // Alfheim iris dirt is absent; Botania alt grass is the requested
            // available botanical replacement. Preserve the source's 0..17
            // color roll, folding those missing colors over the six variants.
            variant = IRIDESCENCE_GRASS[rand.nextInt(18) % IRIDESCENCE_GRASS.length];
        }
        world.setBlockState(ground, ModBlocks.altGrass.getDefaultState()
                .withProperty(BotaniaStateProps.ALTGRASS_VARIANT, variant), 3);

        if (rand.nextInt(4) == 0 && world.isAirBlock(target)) {
            IRIDESCENCE_TREE.generate(world, rand, target);
        }
    }

    private static boolean isPlainGrassOrDirt(IBlockState state) {
        return state.getBlock() == Blocks.GRASS
                || state.getBlock() == Blocks.DIRT
                && state.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT;
    }

    private static void generateMutatedFlower(World world, Random rand, BlockPos origin) {
        if (rand.nextInt(2) != 0) {
            return;
        }
        int startX = rand.nextInt(16);
        int startZ = rand.nextInt(16);

        // The source makes one random attempt and rejects edge positions via
        // its four ±24 biome checks. Search the same chunk from that random
        // starting point so a valid interior position is not lost merely
        // because the first point landed on the biome boundary.
        for (int dz = 0; dz < 16; dz++) {
            for (int dx = 0; dx < 16; dx++) {
                int x = origin.getX() + ((startX + dx) & 15) + 8;
                int z = origin.getZ() + ((startZ + dz) & 15) + 8;
                BlockPos horizontal = new BlockPos(x, 0, z);
                if (!WorldGenAlfheimGiantFlower.isValidCenter(world, horizontal)) {
                    continue;
                }
                BlockPos target = world.getTopSolidOrLiquidBlock(horizontal);
                WorldGenAlfheimGiantFlower.generateAtValidCenter(world, rand, target);
                return;
            }
        }
    }

    private static BlockPos surface(World world, BlockPos origin, Random rand) {
        int x = origin.getX() + rand.nextInt(16) + 8;
        int z = origin.getZ() + rand.nextInt(16) + 8;
        return new BlockPos(x, world.getHeight(x, z), z);
    }

    private static BlockPos decoratedSurface(World world, BlockPos origin, Random rand) {
        return surface(world, origin, rand);
    }

    private static BlockPos surfaceAt(World world, BlockPos position) {
        int x = position.getX();
        int z = position.getZ();
        return new BlockPos(x, world.getHeight(x, z), z);
    }

    private static boolean isGrassSurface(World world, BlockPos target) {
        return world.isAirBlock(target) && world.getBlockState(target.down()).getBlock() == Blocks.GRASS;
    }
}
