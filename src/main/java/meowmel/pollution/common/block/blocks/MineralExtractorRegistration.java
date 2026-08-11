package meowmel.pollution.common.block.blocks;

import meowmel.pollution.Pollution;
import meowmel.pollution.client.tesr.ItemMineralExtractorRenderer;
import meowmel.pollution.common.block.tile.TileEntityMineralExtractor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Pollution.MODID)
public final class MineralExtractorRegistration {

    public static final BlockMineralExtractor MINERAL_EXTRACTOR = new BlockMineralExtractor();
    public static final Item MINERAL_EXTRACTOR_ITEM =
            new ItemBlock(MINERAL_EXTRACTOR).setRegistryName(MINERAL_EXTRACTOR.getRegistryName());

    private MineralExtractorRegistration() {
    }

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(MINERAL_EXTRACTOR);
        GameRegistry.registerTileEntity(
                TileEntityMineralExtractor.class,
                new ResourceLocation(Pollution.MODID, "mineral_extractor"));
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(MINERAL_EXTRACTOR_ITEM);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModel(ModelRegistryEvent event) {
        MINERAL_EXTRACTOR_ITEM.setTileEntityItemStackRenderer(ItemMineralExtractorRenderer.INSTANCE);
        ModelLoader.setCustomModelResourceLocation(
                MINERAL_EXTRACTOR_ITEM,
                0,
                new ModelResourceLocation(
                        MINERAL_EXTRACTOR.getRegistryName(),
                        "inventory"));
    }
}
