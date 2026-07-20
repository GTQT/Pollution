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
import java.util.Random;

/** Port of WorldGenMutatedFlowers backed by the source's 38 schema files. */
public final class WorldGenAlfheimGiantFlower {

    private static final int[] VARIANT_COUNTS = {2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 3, 3, 2, 2, 2, 3};

    private WorldGenAlfheimGiantFlower() {
    }

    public static boolean generate(World world, Random random, BlockPos origin) {
        int x = origin.getX();
        int z = origin.getZ();
        int[][] diagonals = {{-24, -24}, {24, 24}, {-24, 24}, {24, -24}};
        for (int[] offset : diagonals) {
            if (world.getBiome(new BlockPos(x + offset[0], 0, z + offset[1])) != AlfheimBiomes.GIANT_FLOWER_FIELD) {
                return false;
            }
        }

        int color = random.nextInt(17);
        int variant = random.nextInt(VARIANT_COUNTS[color]);
        IBlockState groundFlower = ModBlocks.flower.getStateFromMeta(color < 16 ? color : random.nextInt(16));
        int upper = 8 + random.nextInt(9);
        for (int i = 0; i <= upper; i++) {
            int flowerX = x + random.nextInt(9) - 4;
            int flowerZ = z + random.nextInt(9) - 4;
            BlockPos target = world.getTopSolidOrLiquidBlock(new BlockPos(flowerX, 0, flowerZ));
            world.setBlockState(target, groundFlower, 3);
        }

        return placeSchema(world, origin, color, variant);
    }

    private static boolean placeSchema(World world, BlockPos origin, int color, int variant) {
        String path = "/assets/pollution/alfheim/flowers/" + color + "-" + variant + ".json";
        try (InputStream stream = WorldGenAlfheimGiantFlower.class.getResourceAsStream(path)) {
            if (stream == null) {
                return false;
            }
            JsonArray entries = new JsonParser().parse(
                    new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonArray();
            for (JsonElement entryElement : entries) {
                JsonObject entry = entryElement.getAsJsonObject();
                String sourceBlock = entry.get("block").getAsString();
                for (JsonElement locationElement : entry.getAsJsonArray("location")) {
                    JsonObject location = locationElement.getAsJsonObject();
                    int x = get(location, "x");
                    int y = get(location, "y");
                    int z = get(location, "z");
                    int meta = get(location, "meta");
                    BlockPos target = origin.add(x, y, z);
                    IBlockState state = sourceBlock.equalsIgnoreCase("Botania:cellBlock")
                            ? ModBlocks.cellBlock.getStateFromMeta(meta)
                            : ModBlocks.petalBlock.getStateFromMeta(meta & 15);
                    world.setBlockState(target, state, 2);
                    if (location.has("nbt")) {
                        applyTileNbt(world, target, location.get("nbt").getAsString());
                    }
                }
            }
            return true;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to generate Alfheim giant flower " + color + '-' + variant, exception);
        }
    }

    private static int get(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsInt() : 0;
    }

    private static void applyTileNbt(World world, BlockPos pos, String sourceNbt) throws NBTException {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            return;
        }
        NBTTagCompound nbt = JsonToNBT.getTagFromJson(sourceNbt);
        nbt.setInteger("x", pos.getX());
        nbt.setInteger("y", pos.getY());
        nbt.setInteger("z", pos.getZ());
        tile.readFromNBT(nbt);
        tile.markDirty();
    }
}
