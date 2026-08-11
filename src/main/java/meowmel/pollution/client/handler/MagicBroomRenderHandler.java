package meowmel.pollution.client.handler;

import baubles.api.BaublesApi;
import meowmel.pollution.Pollution;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

/** Renders the carried magic broom below the local player's legs while flying. */
@Mod.EventBusSubscriber(modid = Pollution.MODID, value = Side.CLIENT)
public final class MagicBroomRenderHandler {

    private static final ResourceLocation BROOM_TEXTURE =
            new ResourceLocation(Pollution.MODID, "textures/items/metaitems/magic_sweep.png");
    private static final double BROOM_HALF_SIZE = 0.85D;

    private MagicBroomRenderHandler() {
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player != Minecraft.getMinecraft().player || !player.capabilities.isFlying || !hasMagicBroom(player)) {
            return;
        }

        renderBroom(player, event);
    }

    private static boolean hasMagicBroom(EntityPlayer player) {
        ItemStack broom = PollutionMetaItems.MAGIC_SWEEP.getStackForm();
        if (player.getHeldItemMainhand().isItemEqual(broom) || player.getHeldItemOffhand().isItemEqual(broom)) {
            return true;
        }

        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            if (player.inventory.getStackInSlot(slot).isItemEqual(broom)) return true;
        }

        IItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            if (baubles.getStackInSlot(slot).isItemEqual(broom)) return true;
        }
        return false;
    }

    private static void renderBroom(EntityPlayer player, RenderPlayerEvent.Post event) {
        float partialTicks = event.getPartialRenderTick();
        float yaw = player.prevRenderYawOffset
                + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(event.getX(), event.getY() + 0.03D, event.getZ());
        GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(BROOM_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-BROOM_HALF_SIZE, 0.0D, -BROOM_HALF_SIZE).tex(0.0D, 1.0D).color(255, 255, 255, 255).endVertex();
        buffer.pos(-BROOM_HALF_SIZE, 0.0D, BROOM_HALF_SIZE).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        buffer.pos(BROOM_HALF_SIZE, 0.0D, BROOM_HALF_SIZE).tex(1.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        buffer.pos(BROOM_HALF_SIZE, 0.0D, -BROOM_HALF_SIZE).tex(1.0D, 1.0D).color(255, 255, 255, 255).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
}
