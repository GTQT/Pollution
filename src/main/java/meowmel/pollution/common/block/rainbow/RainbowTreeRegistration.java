package meowmel.pollution.common.block.rainbow;

import meowmel.pollution.Pollution;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.world.biomes.BiomeHandler;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Pollution.MODID)
public final class RainbowTreeRegistration {

    private static final int NATURAL_GENERATION_CHANCE = 12;
    private static final int NATURAL_GIANT_CHANCE = 3;
    private static final int PLACEMENT_ATTEMPTS = 64;

    public static final BlockRainbowLeaves RAINBOW_LEAVES = new BlockRainbowLeaves();
    public static final BlockRainbowSapling RAINBOW_SAPLING = new BlockRainbowSapling();

    private RainbowTreeRegistration() {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(RAINBOW_LEAVES, RAINBOW_SAPLING);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemBlock(RAINBOW_LEAVES).setRegistryName(RAINBOW_LEAVES.getRegistryName()),
                new ItemBlock(RAINBOW_SAPLING).setRegistryName(RAINBOW_SAPLING.getRegistryName()));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        registerModel(RAINBOW_LEAVES);
        registerModel(RAINBOW_SAPLING);
    }

    @SideOnly(Side.CLIENT)
    private static void registerModel(Block block) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(
                (state, world, pos, tintIndex) -> BlockRainbowLeaves.rainbowColor(pos),
                RAINBOW_LEAVES, RAINBOW_SAPLING);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(
                (stack, tintIndex) -> BlockRainbowLeaves.rainbowColor(null),
                Item.getItemFromBlock(RAINBOW_LEAVES), Item.getItemFromBlock(RAINBOW_SAPLING));
    }

    /** Rarely generate one magical rainbow tree in a Thaumcraft biome chunk. */
    @SubscribeEvent
    public static void decorateBiome(DecorateBiomeEvent.Post event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        Biome biome = world.getBiome(event.getPos());
        if (!isThaumcraftBiome(biome)) {
            return;
        }

        Random random = event.getRand();
        if (random.nextInt(NATURAL_GENERATION_CHANCE) != 0) {
            return;
        }

        RainbowTreeGenerator generator = new RainbowTreeGenerator();
        boolean preferGiant = random.nextInt(NATURAL_GIANT_CHANCE) == 0;
        if (preferGiant && tryGenerate(world, event.getPos(), random, generator, true)) {
            return;
        }
        tryGenerate(world, event.getPos(), random, generator, false);
    }

    private static boolean tryGenerate(World world, BlockPos chunkOrigin, Random random,
                                       RainbowTreeGenerator generator, boolean giant) {
        for (int attempt = 0; attempt < PLACEMENT_ATTEMPTS; attempt++) {
            BlockPos column = chunkOrigin.add(random.nextInt(16), 0, random.nextInt(16));
            BlockPos pos = findPlantingPosition(world, column);
            if (pos == null || !isThaumcraftBiome(world.getBiome(pos))) {
                continue;
            }

            if (giant) {
                if (generator.generateNaturalLarge(world, random, pos)) {
                    return true;
                }
            } else if (generator.generateSmall(world, random, pos)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isThaumcraftBiome(Biome biome) {
        return biome == BiomeHandler.MAGICAL_FOREST
                || biome == BiomeHandler.EERIE
                || biome == BiomeHandler.ELDRITCH;
    }

    private static BlockPos findPlantingPosition(World world, BlockPos column) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(
                column.getX(), world.getHeight(column).getY(), column.getZ());
        while (cursor.getY() > 1) {
            if (RainbowTreeGenerator.canGrowOn(world.getBlockState(cursor.down()))) {
                return cursor.toImmutable();
            }
            cursor.move(net.minecraft.util.EnumFacing.DOWN);
        }
        return null;
    }
}
