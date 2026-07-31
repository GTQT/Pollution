package meowmel.pollution.dimension.worldgen.feature;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import meowmel.pollution.dimension.biome.AlfheimBiomes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.common.block.ModBlocks;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Port of WorldGenMutatedFlowers backed by the source's 38 schema files. */
public final class WorldGenAlfheimGiantFlower {

    private static final int BIOME_CLEARANCE = 24;
    private static final int[] VARIANT_COUNTS = {2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 3, 3, 2, 2, 2, 3};
    private static final Schema[][] SCHEMAS = loadSchemas();

    private WorldGenAlfheimGiantFlower() {
    }

    public static boolean generate(World world, Random random, BlockPos origin) {
        if (!isValidCenter(world, origin)) {
            return false;
        }
        return generateAtValidCenter(world, random, origin);
    }

    /**
     * Generates after the caller has already performed {@link #isValidCenter}.
     */
    public static boolean generateAtValidCenter(World world, Random random, BlockPos origin) {
        int color = random.nextInt(17);
        int variant = random.nextInt(VARIANT_COUNTS[color]);
        IBlockState groundFlower = ModBlocks.flower.getStateFromMeta(color < 16 ? color : random.nextInt(16));
        int upper = 8 + random.nextInt(9);
        for (int i = 0; i <= upper; i++) {
            int flowerX = origin.getX() + random.nextInt(9) - 4;
            int flowerZ = origin.getZ() + random.nextInt(9) - 4;
            BlockPos target = world.getTopSolidOrLiquidBlock(new BlockPos(flowerX, 0, flowerZ));
            world.setBlockState(target, groundFlower, 3);
        }

        return placeSchema(world, origin, SCHEMAS[color][variant]);
    }

    /**
     * WorldGenMutatedFlowers requires all four points 24 blocks diagonally
     * from the candidate to remain inside the mutated-flower biome.
     */
    public static boolean isValidCenter(World world, BlockPos origin) {
        int x = origin.getX();
        int z = origin.getZ();
        return isGiantFlowerBiome(world, x - BIOME_CLEARANCE, z - BIOME_CLEARANCE)
                && isGiantFlowerBiome(world, x + BIOME_CLEARANCE, z + BIOME_CLEARANCE)
                && isGiantFlowerBiome(world, x - BIOME_CLEARANCE, z + BIOME_CLEARANCE)
                && isGiantFlowerBiome(world, x + BIOME_CLEARANCE, z - BIOME_CLEARANCE);
    }

    private static boolean isGiantFlowerBiome(World world, int x, int z) {
        return world.getBiome(new BlockPos(x, 0, z)) == AlfheimBiomes.GIANT_FLOWER_FIELD;
    }

    private static boolean placeSchema(World world, BlockPos origin, Schema schema) {
        for (Placement placement : schema.placements) {
            BlockPos target = origin.add(placement.x, placement.y, placement.z);
            world.setBlockState(target, placement.state, 2);
            if (placement.tileNbt != null) {
                applyTileNbt(world, target, placement.tileNbt);
            }
        }
        return true;
    }

    private static int get(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsInt() : 0;
    }

    private static void applyTileNbt(World world, BlockPos pos, NBTTagCompound sourceNbt) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            return;
        }
        NBTTagCompound nbt = sourceNbt.copy();
        nbt.setInteger("x", pos.getX());
        nbt.setInteger("y", pos.getY());
        nbt.setInteger("z", pos.getZ());
        tile.readFromNBT(nbt);
        tile.markDirty();
    }

    private static Schema[][] loadSchemas() {
        Schema[][] schemas = new Schema[VARIANT_COUNTS.length][];
        for (int color = 0; color < VARIANT_COUNTS.length; color++) {
            schemas[color] = new Schema[VARIANT_COUNTS[color]];
            for (int variant = 0; variant < VARIANT_COUNTS[color]; variant++) {
                schemas[color][variant] = loadSchema(color, variant);
            }
        }
        return schemas;
    }

    private static Schema loadSchema(int color, int variant) {
        String path = "/assets/pollution/alfheim/flowers/" + color + "-" + variant + ".json";
        try (InputStream stream = WorldGenAlfheimGiantFlower.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Missing Alfheim giant-flower schema " + path);
            }

            JsonArray entries = new JsonParser().parse(
                    new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonArray();
            List<Placement> placements = new ArrayList<>();
            for (JsonElement entryElement : entries) {
                JsonObject entry = entryElement.getAsJsonObject();
                boolean cellBlock = entry.get("block").getAsString()
                        .equalsIgnoreCase("Botania:cellBlock");
                for (JsonElement locationElement : entry.getAsJsonArray("location")) {
                    JsonObject location = locationElement.getAsJsonObject();
                    int meta = get(location, "meta");
                    IBlockState state = cellBlock
                            ? ModBlocks.cellBlock.getStateFromMeta(meta)
                            : ModBlocks.petalBlock.getStateFromMeta(meta & 15);
                    NBTTagCompound tileNbt = location.has("nbt")
                            ? JsonToNBT.getTagFromJson(location.get("nbt").getAsString())
                            : null;
                    placements.add(new Placement(
                            get(location, "x"), get(location, "y"), get(location, "z"),
                            state, tileNbt));
                }
            }
            return new Schema(Collections.unmodifiableList(placements));
        } catch (NBTException exception) {
            throw new IllegalStateException(
                    "Invalid NBT in Alfheim giant-flower schema " + path, exception);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to load Alfheim giant-flower schema " + path, exception);
        }
    }

    private static final class Schema {
        private final List<Placement> placements;

        private Schema(List<Placement> placements) {
            this.placements = placements;
        }
    }

    private static final class Placement {
        private final int x;
        private final int y;
        private final int z;
        private final IBlockState state;
        private final NBTTagCompound tileNbt;

        private Placement(int x, int y, int z, IBlockState state, NBTTagCompound tileNbt) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
            this.tileNbt = tileNbt;
        }
    }
}
