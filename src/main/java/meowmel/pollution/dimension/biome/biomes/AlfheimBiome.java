package meowmel.pollution.dimension.biome.biomes;

import meowmel.pollution.common.block.alfheim.AlfheimBlocks;
import meowmel.pollution.dimension.biome.AlfheimBiomes;
import meowmel.pollution.dimension.worldgen.feature.WorldGenAlfheimFixedTree;
import meowmel.pollution.dimension.worldgen.feature.WorldGenAlfheimGiantFlower;
import meowmel.pollution.dimension.worldgen.feature.WorldGenAlfheimProgramTrees;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenTrees;
import vazkii.botania.common.block.BlockModFlower;
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

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);

        switch (decoration) {
            case FIELD:
                generateSourceGrass(worldIn, rand, pos, true, true, true, true, 1.0D);
                generateReeds(worldIn, rand, pos, 32);
                generateWhiteGrapes(worldIn, rand, pos, 4);
                generateIridescenceFallback(worldIn, rand, pos);
                break;
            case FLOWER_FIELD:
                generateSourceGrass(worldIn, rand, pos, true, true, false, false, 2.0D);
                generateMutatedFlower(worldIn, rand, pos);
                break;
            case ISLAND_FOREST:
                WorldGenAlfheimProgramTrees.generate(worldIn, rand, pos,
                        WorldGenAlfheimProgramTrees.Set.ISLAND_FOREST);
                generateFixedTrees(worldIn, rand, pos, 1, 2);
                generateSourceGrass(worldIn, rand, pos, true, false, false, false, 2.5D);
                generateWhiteGrapes(worldIn, rand, pos, 2);
                generateMelonsAndPumpkins(worldIn, rand, pos);
                break;
            case PIT_FOREST:
                WorldGenAlfheimProgramTrees.generate(worldIn, rand, pos,
                        WorldGenAlfheimProgramTrees.Set.FOREST);
                generateFixedTrees(worldIn, rand, pos, 1, 2);
                generateSourceGrass(worldIn, rand, pos, true, false, false, false, 2.5D);
                generateWhiteGrapes(worldIn, rand, pos, 6);
                break;
            case LOW_PLATEAU:
            case MID_PLATEAU:
                WorldGenAlfheimProgramTrees.generate(worldIn, rand, pos,
                        WorldGenAlfheimProgramTrees.Set.PLATEAU);
                generateFixedTrees(worldIn, rand, pos, 12, 20);
                generateSourceGrass(worldIn, rand, pos, true, true, true, true, 1.2D);
                break;
            case HIGH_PLATEAU:
            case HIGH_PLATEAU_FIELD:
                generateSourceGrass(worldIn, rand, pos, true, true, true, true, 1.2D);
                break;
            case HIGH_PLATEAU_FOREST:
                WorldGenAlfheimProgramTrees.generate(worldIn, rand, pos,
                        WorldGenAlfheimProgramTrees.Set.FOREST);
                generateFixedTrees(worldIn, rand, pos, 1, 2);
                generateSourceGrass(worldIn, rand, pos, true, true, true, true, 1.2D);
                generateWaterLily(worldIn, rand, pos, 4);
                break;
            case NONE:
            default:
                break;
        }
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
                for (int entry = 0; entry < placements; entry++) {
                    BlockPos target = center.add(rand.nextInt(patchRadius * 2) - patchRadius, 0,
                            rand.nextInt(patchRadius * 2) - patchRadius);
                    placeBotaniaFlower(world, rand, target);
                }
            }
        }

        for (int mushroom = 0; mushroom < ConfigHandler.mushroomQuantity; mushroom++) {
            BlockPos target = origin.add(rand.nextInt(16), 4 + rand.nextInt(28), rand.nextInt(16));
            if (world.isAirBlock(target) && ModBlocks.mushroom.canPlaceBlockAt(world, target)) {
                world.setBlockState(target, ModBlocks.mushroom.getStateFromMeta(botaniaColor(rand)), 2);
            }
        }

        int remaining = (int) Math.round(64.0D * modifier);
        for (int attempts = 256; remaining > 0 && attempts > 0; attempts--) {
            BlockPos target = surface(world, origin, rand);
            if (!isGrassSurface(world, target)) {
                continue;
            }

            int type = rand.nextInt(20);
            if (type > 12 || (type < 5 && !flowers)
                    || (type >= 5 && type <= 10 && !grass)
                    || (type > 10 && !doubleFlowers)) {
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
                BlockDoublePlant.EnumPlantType[] types = {
                        BlockDoublePlant.EnumPlantType.SUNFLOWER,
                        BlockDoublePlant.EnumPlantType.SYRINGA,
                        BlockDoublePlant.EnumPlantType.FERN,
                        BlockDoublePlant.EnumPlantType.ROSE,
                        BlockDoublePlant.EnumPlantType.PAEONIA
                };
                Blocks.DOUBLE_PLANT.placeAt(world, target, types[rand.nextInt(types.length)], 2);
                return true;
            default:
                return false;
        }
    }

    private static void placeBotaniaFlower(World world, Random rand, BlockPos target) {
        if (!isGrassSurface(world, target)) {
            return;
        }
        int color = botaniaColor(rand);
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
            BlockPos target = decoratedSurface(world, origin, rand);
            if (Blocks.WATERLILY.canPlaceBlockAt(world, target)) {
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
            int x = origin.getX() + rand.nextInt(16);
            int z = origin.getZ() + rand.nextInt(16);
            BlockPos target = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            WorldGenAlfheimFixedTree.generateSadOak(world, rand, target);
        }
        if (rand.nextInt(dreamTreeRarity) == 0) {
            int x = origin.getX() + rand.nextInt(16);
            int z = origin.getZ() + rand.nextInt(16);
            BlockPos target = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            WorldGenAlfheimFixedTree.generateDreamTree(world, rand, target);
        }
    }

    /** Direct port of WorldGenMelonPumpkins with the omitted snow-grass check removed. */
    private static void generateMelonsAndPumpkins(World world, Random rand, BlockPos origin) {
        if (rand.nextInt(10) != 0) {
            return;
        }
        BlockPos center = origin.add(rand.nextInt(16), 0, rand.nextInt(16));
        net.minecraft.block.Block block = rand.nextBoolean() ? Blocks.MELON_BLOCK : Blocks.PUMPKIN;
        int remaining = rand.nextInt(8) + 4;
        for (int retries = 64; retries > 0 && remaining > 0; retries--) {
            BlockPos target = surfaceAt(world, center.add(rand.nextInt(8) - 4, 0, rand.nextInt(8) - 4));
            if (block.canPlaceBlockAt(world, target) && world.getBlockState(target.down()).getBlock() == Blocks.GRASS) {
                world.setBlockState(target, block.getDefaultState(), 2);
                remaining--;
            }
        }
    }

    /** Fallback for the custom iridescent seeds and iris tree. */
    private static void generateIridescenceFallback(World world, Random rand, BlockPos origin) {
        if (rand.nextInt(64) != 0) {
            return;
        }
        BlockPos target = surface(world, origin, rand);
        if (!isGrassSurface(world, target)) {
            return;
        }
        if (rand.nextInt(4) == 0) {
            new WorldGenTrees(false).generate(world, rand, target);
        } else {
            placeBotaniaFlower(world, rand, target);
        }
    }

    private static void generateMutatedFlower(World world, Random rand, BlockPos origin) {
        if (rand.nextInt(2) != 0) {
            return;
        }
        int x = origin.getX() + rand.nextInt(16);
        int z = origin.getZ() + rand.nextInt(16);
        BlockPos target = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        WorldGenAlfheimGiantFlower.generate(world, rand, target);
    }

    private static BlockPos surface(World world, BlockPos origin, Random rand) {
        return surfaceAt(world, origin.add(rand.nextInt(16), 0, rand.nextInt(16)));
    }

    private static BlockPos decoratedSurface(World world, BlockPos origin, Random rand) {
        return surfaceAt(world, origin.add(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8));
    }

    private static BlockPos surfaceAt(World world, BlockPos position) {
        return world.getHeight(new BlockPos(position.getX(), 0, position.getZ()));
    }

    private static boolean isGrassSurface(World world, BlockPos target) {
        return world.isAirBlock(target) && world.getBlockState(target.down()).getBlock() == Blocks.GRASS;
    }
}
