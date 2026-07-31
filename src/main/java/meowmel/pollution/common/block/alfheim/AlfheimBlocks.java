package meowmel.pollution.common.block.alfheim;

import meowmel.pollution.Pollution;
import meowmel.pollution.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Blocks that are part of the base Alfheim world-generation port. */
@Mod.EventBusSubscriber(modid = Pollution.MODID)
public final class AlfheimBlocks {

    public static final BlockAlfheimElvenSand ELVEN_SAND = named(new BlockAlfheimElvenSand(), "alfheim_elven_sand");
    public static final BlockAlfheimDreamLeaves DREAM_LEAVES =
            named(new BlockAlfheimDreamLeaves(), "alfheim_dream_leaves");
    public static final BlockAlfheimWhiteGrape WHITE_GRAPE = named(new BlockAlfheimWhiteGrape(), "alfheim_white_grape");
    public static final BlockAlfheimRedGrape[] RED_GRAPES = {
            named(new BlockAlfheimRedGrape(0), "alfheim_red_grape_0"),
            named(new BlockAlfheimRedGrape(1), "alfheim_red_grape_1"),
            named(new BlockAlfheimRedGrape(2), "alfheim_red_grape_2")
    };

    private static final Block[] ALL = {
            ELVEN_SAND, DREAM_LEAVES, WHITE_GRAPE, RED_GRAPES[0], RED_GRAPES[1], RED_GRAPES[2]
    };

    private AlfheimBlocks() {
    }

    private static <T extends Block> T named(T block, String path) {
        block.setRegistryName(new ResourceLocation(Pollution.MODID, path));
        block.setTranslationKey(Pollution.MODID + "." + path);
        block.setCreativeTab(CommonProxy.Pollution_TAB);
        return block;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ALL);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Block block : ALL) {
            ItemBlock item = new ItemBlock(block);
            item.setRegistryName(block.getRegistryName());
            event.getRegistry().register(item);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        for (Block block : ALL) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                    new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }
    }

    /**
     * Custom blocks are not included in Minecraft's hard-coded vanilla color
     * table, even when they reuse a tinted vanilla model. Register the source
     * red-grape tint and vanilla water-lily fallback tint explicitly.
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(
                (state, world, pos, tintIndex) -> world != null && pos != null ? 0x208030 : 0x71C35C,
                WHITE_GRAPE);
        event.getBlockColors().registerBlockColorHandler(
                (state, world, pos, tintIndex) -> 0xBBBBBB,
                RED_GRAPES[0], RED_GRAPES[1], RED_GRAPES[2]);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> 0x71C35C,
                Item.getItemFromBlock(WHITE_GRAPE));
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> 0xBBBBBB,
                Item.getItemFromBlock(RED_GRAPES[0]), Item.getItemFromBlock(RED_GRAPES[1]),
                Item.getItemFromBlock(RED_GRAPES[2]));
    }
}
