package meowmel.pollution.client.handler;

import meowmel.pollution.Pollution;
import meowmel.pollution.api.capability.StarstreamNetworkConstants;
import meowmel.pollution.common.block.tile.TileEntityStarstreamObeliskCore;
import meowmel.pollution.common.block.tile.TileEntityStarstreamRelay;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.items.behaviors.StarstreamLinkerBehavior;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.UUID;

/** Temporary topology and wireless-coverage preview shown while holding the linker. */
@Mod.EventBusSubscriber(modid = Pollution.MODID, value = Side.CLIENT)
public final class StarstreamNetworkPreviewHandler {

    private static final int MAX_PREVIEW_NODES = 48;
    private static final double PREVIEW_DISTANCE_SQUARED = 384.0D * 384.0D;

    private StarstreamNetworkPreviewHandler() {}

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null || mc.world == null) return;
        ItemStack linker = findHeldLinker(player);
        if (linker.isEmpty() || !StarstreamLinkerBehavior.isNetworkMode(linker)) return;

        UUID selectedNetwork = StarstreamLinkerBehavior.getSelectedNetworkId(linker);
        double cameraX = mc.getRenderManager().viewerPosX;
        double cameraY = mc.getRenderManager().viewerPosY;
        double cameraZ = mc.getRenderManager().viewerPosZ;

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-cameraX, -cameraY, -cameraZ);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        int rendered = 0;
        for (TileEntity tile : mc.world.loadedTileEntityList) {
            if (rendered >= MAX_PREVIEW_NODES
                    || tile.getDistanceSq(player.posX, player.posY, player.posZ)
                    > PREVIEW_DISTANCE_SQUARED) continue;
            if (tile instanceof TileEntityStarstreamObeliskCore) {
                TileEntityStarstreamObeliskCore nexus = (TileEntityStarstreamObeliskCore) tile;
                if (!nexus.isLinkedAndFormed()) continue;
                boolean foreign = selectedNetwork != null
                        && !selectedNetwork.equals(nexus.getNetworkId());
                boolean online = nexus.isWirelessNetworkOnline();
                drawCoverageCircle(nexus.getPos(),
                        StarstreamNetworkConstants.NEXUS_WIRELESS_RANGE,
                        foreign ? 0xBB62FF : online ? 0xF2C0FF : 0xFF405A,
                        online ? 110 : 150, online && !foreign,
                        mc.world.getTotalWorldTime());
                rendered++;
            } else if (tile instanceof TileEntityStarstreamRelay) {
                TileEntityStarstreamRelay relay = (TileEntityStarstreamRelay) tile;
                TileEntityStarstreamObeliskCore networkCore = relay.findNetworkCore();
                UUID networkId = networkCore == null ? null : networkCore.getNetworkId();
                boolean online = networkCore != null && networkCore.isWirelessNetworkOnline();
                boolean foreign = selectedNetwork != null && networkId != null
                        && !selectedNetwork.equals(networkId);
                int color = foreign ? 0xBB62FF : !online ? 0xFF405A : 0x55F4FF;
                drawCoverageCircle(relay.getPos(),
                        relay.getWirelessRange(),
                        color, online ? 95 : 150, false, mc.world.getTotalWorldTime());
                BlockPos parent = relay.getOutputPos();
                if (parent != null) drawDashedTopologyLine(relay.getPos(), parent, color);
                rendered++;
            }
        }

        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private static ItemStack findHeldLinker(EntityPlayer player) {
        ItemStack expected = PollutionMetaItems.STARSTREAM_LINKER.getStackForm();
        if (player.getHeldItemMainhand().isItemEqual(expected)) return player.getHeldItemMainhand();
        if (player.getHeldItemOffhand().isItemEqual(expected)) return player.getHeldItemOffhand();
        return ItemStack.EMPTY;
    }

    private static void drawCoverageCircle(BlockPos center, double radius, int baseColor,
                                           int alpha, boolean rainbow, long time) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int segments = radius >= 100.0D ? 160 : 96;
        GL11.glLineWidth(radius >= 100.0D ? 2.5F : 2.0F);
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < segments; i++) {
            double angle = Math.PI * 2.0D * i / segments;
            int color = rainbow
                    ? Color.HSBtoRGB((float) ((i / (double) segments + time * 0.002D) % 1.0D),
                    0.68F, 1.0F) : baseColor;
            vertex(buffer, center.getX() + 0.5D + Math.cos(angle) * radius,
                    center.getY() + 0.12D,
                    center.getZ() + 0.5D + Math.sin(angle) * radius,
                    color, alpha);
        }
        tessellator.draw();
    }

    private static void drawDashedTopologyLine(BlockPos from, BlockPos to, int color) {
        double x0 = from.getX() + 0.5D;
        double y0 = from.getY() + 1.45D;
        double z0 = from.getZ() + 0.5D;
        double x1 = to.getX() + 0.5D;
        double y1 = to.getY() + 1.45D;
        double z1 = to.getZ() + 0.5D;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glLineWidth(1.5F);
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 24; i += 2) {
            double a = i / 24.0D;
            double b = (i + 1) / 24.0D;
            vertex(buffer, lerp(x0, x1, a), lerp(y0, y1, a), lerp(z0, z1, a), color, 145);
            vertex(buffer, lerp(x0, x1, b), lerp(y0, y1, b), lerp(z0, z1, b), color, 145);
        }
        tessellator.draw();
    }

    private static double lerp(double a, double b, double progress) {
        return a + (b - a) * progress;
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z,
                               int color, int alpha) {
        buffer.pos(x, y, z)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, alpha)
                .endVertex();
    }
}
