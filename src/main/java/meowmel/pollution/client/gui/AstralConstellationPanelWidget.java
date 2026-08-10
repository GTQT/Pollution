package meowmel.pollution.client.gui;

import com.cleanroommc.modularui.api.value.IValue;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import meowmel.pollution.api.amplification.MagicJeiHintResolver;
import meowmel.pollution.api.astral.AstralNbtHelper;
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

/**
 * Left-hand live status panel for every magic multiblock. The server syncs a
 * compact scalar snapshot, while the client obtains the real star layout from
 * Astral Sorcery's registered constellation definition.
 */
public final class AstralConstellationPanelWidget extends Widget<AstralConstellationPanelWidget> {

    public static final int WIDTH = 176;
    public static final int HEIGHT = 182;
    private final IValue<String> state;

    public AstralConstellationPanelWidget(IValue<String> state) {
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
        PanelState panel = PanelState.parse(state == null ? "" : state.getValue());
        GuiDraw.drawRect(0, 0, WIDTH, HEIGHT, 0xF10A1522);
        GuiDraw.drawRect(0, 0, WIDTH, 1, 0xFF4AA6C8);
        GuiDraw.drawRect(0, HEIGHT - 1, WIDTH, 1, 0xFF1D5F79);
        GuiDraw.drawRect(0, 0, 1, HEIGHT, 0xFF1D5F79);
        GuiDraw.drawRect(WIDTH - 1, 0, 1, HEIGHT, 0xFF1D5F79);

        // GuiDraw's primitive helpers leave the texture state disabled. Restore
        // it before Minecraft's font renderer draws its glyph atlas.
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        font.drawString("§b星辉透镜仓", 7, 6, 0xE8F7FF, false);
        if (!panel.hasWafer()) {
            font.drawString("§7未装入星座数据晶圆", 7, 27, 0xB5BEC8, false);
            font.drawString("§8装入后显示星宿图与实时增幅", 7, 42, 0x7E8A96, false);
            drawIdleStars();
            return;
        }

        IConstellation constellation = AstralNbtHelper.findConstellation(panel.constellation);
        if (constellation != null) {
            drawConstellation(constellation, panel.skyMatched);
        } else {
            drawIdleStars();
        }

        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        String name = MagicJeiHintResolver.constellationDisplayName(panel.constellation);
        int englishStart = name.indexOf('（');
        if (englishStart > 0) name = name.substring(0, englishStart);
        font.drawString("§b" + name, 86, 25, 0xDFFBFF, false);
        int waferBaseStrength = Math.max(0, panel.baseStrength - panel.opticalBonus);
        font.drawString("§7晶圆基础 §d" + waferBaseStrength + "%", 86, 39, 0xC9D3DB, false);
        font.drawString("§b透镜+" + panel.opticalBonus + "% §7= §d" + panel.baseStrength + "%",
                86, 51, 0xC9D3DB, false);
        font.drawString(panel.skyMatched ? "§a天象活跃 §b+10%" : "§c天象未匹配", 86, 63,
                panel.skyMatched ? 0x7CFFB0 : 0xFF8C8C, false);
        font.drawString(panel.opticalQuality > 0
                        ? "§7晶体品质 §b" + panel.opticalQuality + "%"
                        : "§8未插入培育水晶",
                86, 75, 0xC9D3DB, false);
        font.drawString(panel.tarot.isEmpty() ? "§8塔罗：未插入" : "§d塔罗："
                        + MagicJeiHintResolver.tarotDisplayName(panel.tarot)
                        + (panel.hasTarotEffect() ? " §a已生效" : " §8未匹配"),
                86, 86, panel.hasTarotEffect() ? 0x8CFFB0 : 0x9BA8B4, false);

        font.drawString(panel.idlePreview ? "§9晶圆、塔罗待机预览" : "§9晶圆、塔罗与工序合计",
                7, 99, 0xB9D6FF, false);
        font.drawString("§7基础 §d" + waferBaseStrength + "% §7/ 透镜 §b+" + panel.opticalBonus
                + "% §7/ 天象 §b" + (panel.skyMatched ? "+10%" : "+0%"), 7, 110, 0xC9D3DB, false);
        font.drawString("§f耗时 §a-" + panel.duration + "%  §fEUt §a-" + panel.eut + "%", 7, 121,
                panel.hasRecipeBonus() ? 0xE3F5E9 : 0x9BA8B4, false);
        font.drawString("§f介质 §a-" + panel.magic + "%  §f并行 §e+" + panel.parallel, 7, 132,
                panel.hasRecipeBonus() ? 0xE3F5E9 : 0x9BA8B4, false);
        font.drawString("§f产物 §e+" + panel.output + "%  §f重判 §e+" + panel.chance + "%", 7, 143,
                panel.hasRecipeBonus() ? 0xFFF0C2 : 0x9BA8B4, false);
        font.drawString("§f催化 §e" + panel.catalyst + "%  §f炉温 §6+" + panel.temperature + "K", 7, 154,
                panel.hasRecipeBonus() ? 0xFFF0C2 : 0x9BA8B4, false);
        font.drawString(panel.getTarotContributionText(), 7, 165,
                panel.hasTarotEffect() ? 0xF7A8FF : 0x8795A3, false);
    }

    @SideOnly(Side.CLIENT)
    private static void drawIdleStars() {
        int[][] stars = {{104, 26}, {126, 40}, {143, 27}, {119, 56}, {151, 61}, {132, 76}};
        for (int[] star : stars) {
            GuiDraw.drawCircle(star[0] - 1, star[1] - 1, 3, 0x557DA9C8, 0x007DA9C8, 8);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void drawConstellation(IConstellation constellation, boolean skyMatched) {
        if (constellation.getStars().isEmpty()) return;
        final float left = 8.0F;
        final float top = 24.0F;
        final float chartWidth = 68.0F;
        final float chartHeight = 58.0F;
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (StarLocation star : constellation.getStars()) {
            minX = Math.min(minX, star.x);
            minY = Math.min(minY, star.y);
            maxX = Math.max(maxX, star.x);
            maxY = Math.max(maxY, star.y);
        }
        float scale = Math.min(chartWidth / Math.max(1.0F, maxX - minX),
                chartHeight / Math.max(1.0F, maxY - minY));
        float offsetX = left + (chartWidth - (maxX - minX) * scale) * 0.5F - minX * scale;
        float offsetY = top + (chartHeight - (maxY - minY) * scale) * 0.5F - minY * scale;
        Color color = constellation.getConstellationColor();
        int alpha = skyMatched ? 230 : 125;
        int lineColor = ((alpha & 0xFF) << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();

        for (StarConnection connection : constellation.getStarConnections()) {
            drawLine(offsetX + connection.from.x * scale, offsetY + connection.from.y * scale,
                    offsetX + connection.to.x * scale, offsetY + connection.to.y * scale, lineColor);
        }
        for (StarLocation star : constellation.getStars()) {
            float x = offsetX + star.x * scale;
            float y = offsetY + star.y * scale;
            GuiDraw.drawCircle(x - 3.0F, y - 3.0F, 6.0F, lineColor, 0x00FFFFFF, 10);
            GuiDraw.drawCircle(x - 1.0F, y - 1.0F, 2.0F, 0xFFFFFFFF, 0xFFFFFFFF, 8);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void drawLine(float x1, float y1, float x2, float y2, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length <= 0.0F) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x1, y1, 0.0F);
        GlStateManager.rotate((float) Math.toDegrees(Math.atan2(dy, dx)), 0.0F, 0.0F, 1.0F);
        GuiDraw.drawRect(0.0F, -0.5F, length, 1.0F, color);
        GlStateManager.popMatrix();
    }

    private static final class PanelState {
        private final String constellation;
        private final int baseStrength;
        private final boolean skyMatched;
        private final int distribution;
        private final int duration;
        private final int eut;
        private final int magic;
        private final int parallel;
        private final int output;
        private final int chance;
        private final int catalyst;
        private final int temperature;
        private final int opticalQuality;
        private final int opticalBonus;
        private final String tarot;
        private final int tarotDuration;
        private final int tarotEut;
        private final int tarotMagic;
        private final int tarotParallel;
        private final int tarotOutput;
        private final int tarotChance;
        private final int tarotCatalyst;
        private final int tarotTemperature;
        private final boolean idlePreview;

        private PanelState(String constellation, int baseStrength, boolean skyMatched, int distribution,
                           int duration, int eut, int magic, int parallel, int output, int chance,
                           int catalyst, int temperature, int opticalQuality, int opticalBonus, String tarot,
                           int tarotDuration, int tarotEut, int tarotMagic, int tarotParallel, int tarotOutput,
                           int tarotChance, int tarotCatalyst, int tarotTemperature, boolean idlePreview) {
            this.constellation = constellation;
            this.baseStrength = baseStrength;
            this.skyMatched = skyMatched;
            this.distribution = distribution;
            this.duration = duration;
            this.eut = eut;
            this.magic = magic;
            this.parallel = parallel;
            this.output = output;
            this.chance = chance;
            this.catalyst = catalyst;
            this.temperature = temperature;
            this.opticalQuality = opticalQuality;
            this.opticalBonus = opticalBonus;
            this.tarot = tarot == null ? "" : tarot;
            this.tarotDuration = tarotDuration;
            this.tarotEut = tarotEut;
            this.tarotMagic = tarotMagic;
            this.tarotParallel = tarotParallel;
            this.tarotOutput = tarotOutput;
            this.tarotChance = tarotChance;
            this.tarotCatalyst = tarotCatalyst;
            this.tarotTemperature = tarotTemperature;
            this.idlePreview = idlePreview;
        }

        private static PanelState parse(String encoded) {
            if (encoded == null || encoded.isEmpty()) return new PanelState("", 0, false, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 0, 0, 0, 0, 0, 0, 0, true);
            String[] fields = encoded.split("\\|", -1);
            if (fields.length != 24) return new PanelState("", 0, false, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 0, 0, 0, 0, 0, 0, 0, true);
            try {
                return new PanelState(fields[0].toLowerCase(Locale.ROOT), Integer.parseInt(fields[1]),
                        Boolean.parseBoolean(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]),
                        Integer.parseInt(fields[5]), Integer.parseInt(fields[6]), Integer.parseInt(fields[7]),
                        Integer.parseInt(fields[8]), Integer.parseInt(fields[9]), Integer.parseInt(fields[10]),
                        Integer.parseInt(fields[11]), Integer.parseInt(fields[12]), Integer.parseInt(fields[13]),
                        fields[14].toLowerCase(Locale.ROOT), Integer.parseInt(fields[15]),
                        Integer.parseInt(fields[16]), Integer.parseInt(fields[17]), Integer.parseInt(fields[18]),
                        Integer.parseInt(fields[19]), Integer.parseInt(fields[20]), Integer.parseInt(fields[21]),
                        Integer.parseInt(fields[22]), Boolean.parseBoolean(fields[23]));
            } catch (NumberFormatException ignored) {
                return new PanelState("", 0, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        "", 0, 0, 0, 0, 0, 0, 0, 0, true);
            }
        }

        private boolean hasWafer() {
            return !constellation.isEmpty();
        }

        private boolean hasRecipeBonus() {
            return duration > 0 || eut > 0 || magic > 0 || parallel > 0 || output > 0
                    || chance > 0 || catalyst > 0 || temperature > 0;
        }

        private boolean hasTarotEffect() {
            return tarotDuration > 0 || tarotEut > 0 || tarotMagic > 0 || tarotParallel > 0
                    || tarotOutput > 0 || tarotChance > 0 || tarotCatalyst > 0 || tarotTemperature > 0;
        }

        private String getTarotContributionText() {
            if (tarot.isEmpty()) return "§8塔罗贡献：未插入塔罗牌";
            if (!hasTarotEffect()) return "§8塔罗贡献：已插入，但未匹配";
            StringBuilder line = new StringBuilder("§d塔罗贡献：");
            append(line, "耗时 -", tarotDuration, "%");
            append(line, "EUt -", tarotEut, "%");
            append(line, "介质 -", tarotMagic, "%");
            append(line, "并行 +", tarotParallel, "");
            append(line, "产物 +", tarotOutput, "%");
            append(line, "重判 +", tarotChance, "%");
            append(line, "催化 ", tarotCatalyst, "%");
            append(line, "炉温 +", tarotTemperature, "K");
            return line.toString();
        }

        private static void append(StringBuilder line, String label, int value, String suffix) {
            if (value <= 0) return;
            // The floating panel is intentionally compact. The normal machine
            // UI contains the complete contribution list; keep this one line
            // within the 176 px panel instead of allowing it to overlap it.
            if (line.length() > 20) return;
            if (line.length() > 7) line.append(' ');
            line.append(label).append(value).append(suffix);
        }

    }
}
