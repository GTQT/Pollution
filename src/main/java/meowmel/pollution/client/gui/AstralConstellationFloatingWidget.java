package meowmel.pollution.client.gui;

import com.cleanroommc.modularui.api.value.IBoolValue;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.widget.DraggableWidget;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import meowmel.pollution.api.astral.AstralNbtHelper;

import java.awt.Color;
import java.util.Locale;
import java.util.function.Supplier;

/** Draggable, client-local constellation window. It never changes machine controls. */
public final class AstralConstellationFloatingWidget extends DraggableWidget<AstralConstellationFloatingWidget>
        implements Interactable {

    public static final int WIDTH = 158;
    public static final int HEIGHT = 101;
    private final Supplier<String> stateSupplier;
    private final IBoolValue open;

    public AstralConstellationFloatingWidget(Supplier<String> stateSupplier, IBoolValue open) {
        this.stateSupplier = stateSupplier;
        this.open = open;
        size(WIDTH, HEIGHT);
        disableThemeBackground(true);
    }

    @Override
    public boolean canBeSeen(com.cleanroommc.modularui.api.layout.IViewportStack stack) {
        return open.getBoolValue() && super.canBeSeen(stack);
    }

    @Override
    public boolean canHover() {
        return open.getBoolValue();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Result onMousePressed(int mouseButton) {
        if (!open.getBoolValue() || mouseButton != 0) return Result.ACCEPT;
        int realX = getContext().transformX(0, 0) - getParentArea().x;
        int realY = getContext().transformY(0, 0) - getParentArea().y;
        int localX = getContext().getAbsMouseX() - realX;
        int localY = getContext().getAbsMouseY() - realY;
        if (localX >= WIDTH - 16 && localY >= 0 && localY < 16) {
            open.setBoolValue(false);
            Interactable.playButtonClickSound();
            return Result.SUCCESS;
        }
        return Result.ACCEPT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean onDragStart(int mouseButton) {
        if (!open.getBoolValue()) return false;
        int realX = getContext().transformX(0, 0) - getParentArea().x;
        int realY = getContext().transformY(0, 0) - getParentArea().y;
        int localX = getContext().getAbsMouseX() - realX;
        int localY = getContext().getAbsMouseY() - realY;
        return mouseButton == 0 && localY >= 0 && localY < 16 && localX < WIDTH - 16
                && super.onDragStart(mouseButton);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        if (!open.getBoolValue()) return;
        State data = State.parse(stateSupplier.get());
        GuiDraw.drawRect(0, 0, WIDTH, HEIGHT, 0xF00B1826);
        GuiDraw.drawRect(0, 0, WIDTH, 16, 0xFF123A55);
        GuiDraw.drawRect(0, 0, WIDTH, 1, 0xFF55D5F2);
        GuiDraw.drawRect(WIDTH - 1, 0, 1, HEIGHT, 0xFF287F9F);
        GuiDraw.drawRect(0, HEIGHT - 1, WIDTH, 1, 0xFF287F9F);
        GlStateManager.enableTexture2D();
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        font.drawString(TextFormatting.AQUA + "星宿图（拖动标题栏）", 5, 5, 0xE9FBFF, false);
        font.drawString(TextFormatting.RED + "×", WIDTH - 12, 4, 0xFF7070, false);

        IConstellation constellation = AstralNbtHelper.findConstellation(data.id);
        if (constellation == null) {
            font.drawString(TextFormatting.GRAY + "未装入星座数据晶圆", 11, 45, 0xBAC4CD, false);
            font.drawString(TextFormatting.DARK_GRAY + "点击右上角按钮可再次打开", 11, 59, 0x7B8791, false);
            return;
        }
        drawConstellation(constellation, data.skyMatched);
        GlStateManager.enableTexture2D();
        font.drawString(TextFormatting.LIGHT_PURPLE + "S " + data.baseStrength + "%"
                + (data.skyMatched ? TextFormatting.AQUA + " + 天象10%" : ""), 91, 30, 0xF2E8FF, false);
        font.drawString(data.skyMatched ? TextFormatting.GREEN + "露天且星座活跃"
                : TextFormatting.RED + "天象未匹配", 91, 44, data.skyMatched ? 0x8AFFBB : 0xFF8F8F, false);
        font.drawString(TextFormatting.GRAY + "活跃度 " + TextFormatting.WHITE + data.distribution + "%", 91, 58,
                0xD6DFE7, false);
        font.drawString(TextFormatting.DARK_AQUA + "完整增幅见右侧信息", 91, 82, 0x99D5E8, false);
    }

    @SideOnly(Side.CLIENT)
    private static void drawConstellation(IConstellation constellation, boolean skyMatched) {
        final float left = 12.0F;
        final float top = 25.0F;
        final float scale = 4.25F;
        Color color = constellation.getConstellationColor();
        int alpha = skyMatched ? 235 : 115;
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
        private final int distribution;

        private State(String id, int baseStrength, boolean skyMatched, int distribution) {
            this.id = id;
            this.baseStrength = baseStrength;
            this.skyMatched = skyMatched;
            this.distribution = distribution;
        }

        private static State parse(String encoded) {
            String[] fields = encoded == null ? new String[0] : encoded.split("\\|", -1);
            if (fields.length < 4) return new State("", 0, false, 0);
            try {
                return new State(fields[0].toLowerCase(Locale.ROOT), Integer.parseInt(fields[1]),
                        Boolean.parseBoolean(fields[2]), Integer.parseInt(fields[3]));
            } catch (NumberFormatException ignored) {
                return new State("", 0, false, 0);
            }
        }
    }
}
