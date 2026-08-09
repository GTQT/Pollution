package meowmel.pollution.client.gui;

import com.cleanroommc.modularui.api.value.IValue;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import meowmel.pollution.api.astral.AstralNbtHelper;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Locale;

/** Compact, non-interactive constellation view embedded in the stock GT screen. */
public final class AstralConstellationMiniWidget extends Widget<AstralConstellationMiniWidget> {

    public static final int WIDTH = 82;
    public static final int HEIGHT = 109;
    private final IValue<String> state;

    public AstralConstellationMiniWidget(IValue<String> state) {
        this.state = state;
        size(WIDTH, HEIGHT);
        disableThemeBackground(true);
    }

    @Override
    public boolean canHoverThrough() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        State data = State.parse(state == null ? "" : state.getValue());
        GuiDraw.drawRect(0, 0, WIDTH, HEIGHT, 0xD9122636);
        GuiDraw.drawRect(WIDTH - 1, 0, 1, HEIGHT, 0xFF286B84);
        GlStateManager.enableTexture2D();
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        font.drawString("§b星宿图", 5, 5, 0xE6FAFF, false);

        IConstellation constellation = AstralNbtHelper.findConstellation(data.id);
        if (constellation == null) {
            font.drawString("§8未装晶圆", 9, 49, 0x96A2AD, false);
            return;
        }
        drawConstellation(constellation, data.skyMatched);
        GlStateManager.enableTexture2D();
        font.drawString("§dS " + data.baseStrength + "%" + (data.skyMatched ? "§b+10" : ""), 6, 89,
                0xEEE6FF, false);
        font.drawString(data.skyMatched ? "§a天象匹配" : "§c天象未匹配", 6, 99,
                data.skyMatched ? 0x8AFFBD : 0xFF9292, false);
    }

    @SideOnly(Side.CLIENT)
    private static void drawConstellation(IConstellation constellation, boolean skyMatched) {
        final float left = 9.0F;
        final float top = 20.0F;
        final float scale = 4.0F;
        Color color = constellation.getConstellationColor();
        int alpha = skyMatched ? 235 : 120;
        int lineColor = (alpha << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
        for (StarConnection connection : constellation.getStarConnections()) {
            drawLine(left + connection.from.x * scale, top + connection.from.y * scale,
                    left + connection.to.x * scale, top + connection.to.y * scale, lineColor);
        }
        for (StarLocation star : constellation.getStars()) {
            float x = left + star.x * scale;
            float y = top + star.y * scale;
            GuiDraw.drawCircle(x - 2.5F, y - 2.5F, 5.0F, lineColor, 0x00FFFFFF, 8);
            GuiDraw.drawCircle(x - 1.0F, y - 1.0F, 2.0F, 0xFFFFFFFF, 0xFFFFFFFF, 8);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void drawLine(float x1, float y1, float x2, float y2, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y1, 0.0D).color((color >>> 16) & 0xFF, (color >>> 8) & 0xFF,
                color & 0xFF, (color >>> 24) & 0xFF).endVertex();
        buffer.pos(x2, y2, 0.0D).color((color >>> 16) & 0xFF, (color >>> 8) & 0xFF,
                color & 0xFF, (color >>> 24) & 0xFF).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
    }

    private static final class State {
        private final String id;
        private final int baseStrength;
        private final boolean skyMatched;

        private State(String id, int baseStrength, boolean skyMatched) {
            this.id = id;
            this.baseStrength = baseStrength;
            this.skyMatched = skyMatched;
        }

        private static State parse(String encoded) {
            String[] fields = encoded == null ? new String[0] : encoded.split("\\|", -1);
            if (fields.length < 3) return new State("", 0, false);
            try {
                return new State(fields[0].toLowerCase(Locale.ROOT), Integer.parseInt(fields[1]),
                        Boolean.parseBoolean(fields[2]));
            } catch (NumberFormatException ignored) {
                return new State("", 0, false);
            }
        }
    }
}
