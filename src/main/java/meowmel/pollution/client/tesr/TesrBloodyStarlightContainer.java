package meowmel.pollution.client.tesr;

import meowmel.pollution.common.block.tile.TileEntityBloodyStarlightContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TesrBloodyStarlightContainer extends TileEntitySpecialRenderer<TileEntityBloodyStarlightContainer> {
    @Override
    public void render(TileEntityBloodyStarlightContainer tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (!tile.hasWorld()) return;
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y, z);
            boolean detailed = Minecraft.getMinecraft().player != null
                    && tile.getDistanceSq(Minecraft.getMinecraft().player.posX,
                    Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ) < 256;
            BloodyStarlightContainerVisual.render(tile.getPayload(),
                    tile.getWorld().getTotalWorldTime() + partialTicks, partialTicks, detailed, tile.isBaseHidden());
        } finally {
            GlStateManager.popMatrix();
        }
    }
}
