package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.tile.TileEntityMineralExtractor;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

/** Reuses the placed mineral extractor TESR for every item render context. */
public final class ItemMineralExtractorRenderer extends TileEntityItemStackRenderer {

    public static final ItemMineralExtractorRenderer INSTANCE = new ItemMineralExtractorRenderer();

    private static final TileEntityMineralExtractor DISPLAY_TILE = new TileEntityMineralExtractor();
    private static final TesrMineralExtractor DISPLAY_RENDERER = new TesrMineralExtractor();

    private ItemMineralExtractorRenderer() {
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        DISPLAY_RENDERER.render(DISPLAY_TILE, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
    }
}
