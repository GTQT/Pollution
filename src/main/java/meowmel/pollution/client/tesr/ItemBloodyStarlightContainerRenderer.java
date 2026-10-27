package meowmel.pollution.client.tesr;

import meowmel.pollution.common.entity.dragon.DragonContainmentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class ItemBloodyStarlightContainerRenderer extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        double time = Minecraft.getMinecraft().world == null ? 0
                : Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks;
        BloodyStarlightContainerVisual.render(DragonContainmentData.get(stack), time, partialTicks, false,
                DragonContainmentData.isBaseHidden(stack));
    }
}
