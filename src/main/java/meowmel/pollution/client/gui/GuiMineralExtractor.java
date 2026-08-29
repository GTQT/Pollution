package meowmel.pollution.client.gui;

import meowmel.pollution.common.block.tile.ContainerMineralExtractor;
import meowmel.pollution.common.block.tile.TileEntityMineralExtractor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiMineralExtractor extends GuiContainer {

    private static final String[] MODE_NAMES = {"实体矿", "虚拟矿物", "虚拟流体"};

    // ===== 配色 =====
    private static final int C_BORDER    = 0xFF373737;
    private static final int C_BG        = 0xFFC6C6C6;
    private static final int C_TITLE     = 0xFF555555;
    private static final int C_PANEL     = 0xFFAAAAAA;
    private static final int C_LABEL     = 0xFF333333;
    private static final int C_SLOT_FILL = 0xFF8B8B8B;
    private static final int C_CHAOS     = 0xFFAA4455;
    private static final int C_MAGIC     = 0xFF6644CC;
    private static final int C_GREEN     = 0xFF55AA44;
    private static final int C_RED       = 0xFFAA3333;
    private static final int C_ORANGE    = 0xFFCC8844;
    private static final int C_BLUE      = 0xFF5588CC;

    private final ContainerMineralExtractor container;
    private final int modeBtnX, modeBtnY, powerBtnX, powerBtnY;

    public GuiMineralExtractor(InventoryPlayer playerInv, TileEntityMineralExtractor te) {
        super(new ContainerMineralExtractor(playerInv, te));
        this.container = (ContainerMineralExtractor) this.inventorySlots;
        this.xSize = 176; // 恢复标准宽度
        this.ySize = 202;

        // 按钮位置取自容器槽位，保证与点击命中一致
        Slot modeSlot = this.inventorySlots.getSlot(ContainerMineralExtractor.MODE_SLOT);
        Slot powerSlot = this.inventorySlots.getSlot(ContainerMineralExtractor.POWER_SLOT);
        this.modeBtnX = modeSlot.xPos;
        this.modeBtnY = modeSlot.yPos;
        this.powerBtnX = powerSlot.xPos;
        this.powerBtnY = powerSlot.yPos;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int x = this.guiLeft;
        int y = this.guiTop;

        // 窗口背景（深色外框 + 灰底）
        drawRect(x, y, x + xSize, y + ySize, C_BORDER);
        drawRect(x + 1, y + 1, x + xSize - 1, y + ySize - 1, C_BG);

        // 标题条
        drawRect(x + 1, y + 1, x + xSize - 1, y + 12, C_TITLE);
        drawRect(x + 1, y + 12, x + xSize - 1, y + 13, 0xFF8B8B8B);
        this.fontRenderer.drawString("矿物提取器", x + 8, y + 4, 0xFFFFFFFF);

        // 机器面板
        drawRect(x + 3, y + 15, x + xSize - 3, y + 118, C_PANEL);
        drawRect(x + 3, y + 15, x + xSize - 3, y + 16, 0xFFD6D6D6);

        // 源质仓
        drawSectionLabel(x + 8, y + 19, "源质仓", C_CHAOS);
        drawAspectBar(x + 10, y + 27, container.syncChaos, C_CHAOS, "混沌/熵");
        drawAspectBar(x + 10, y + 47, container.syncMagic, C_MAGIC, "魔法");

        // 槽位（机器槽 + 背包，按容器坐标，已回正到 x=8 对齐）
        List<Slot> slots = this.inventorySlots.inventorySlots;
        for (int i = 0; i < slots.size(); i++) {
            Slot s = slots.get(i);
            if (i == ContainerMineralExtractor.MODE_SLOT
                    || i == ContainerMineralExtractor.POWER_SLOT) continue; // 画成按钮
            drawSlot(x + s.xPos, y + s.yPos);
        }

        // 状态行
        drawStatusLine(x, y);

        // 两个按钮（背包右上方空隙，横排）
        int mode = container.syncMode % 3;
        drawButton(x + modeBtnX, y + modeBtnY, modeColor(mode));
        drawModeIcon(x + modeBtnX, y + modeBtnY);
        boolean running = container.syncEnabled == 1;
        drawButton(x + powerBtnX, y + powerBtnY, running ? C_GREEN : C_RED);
        drawPowerIcon(x + powerBtnX, y + powerBtnY, running);

        // 分区线（面板下沿 / 背包上沿）
        drawRect(x + 4, y + 120, x + xSize - 4, y + 121, 0xFF8B8B8B);
    }

    private void drawSectionLabel(int x, int y, String text, int dotColor) {
        drawRect(x, y + 3, x + 3, y + 6, dotColor);
        this.fontRenderer.drawString(text, x + 7, y, C_LABEL);
    }

    private void drawStatusLine(int x, int y) {
        int mode = container.syncMode % 3;
        if (mode == 1) {
            this.fontRenderer.drawString("虚拟矿物: 产草方块", x + 8, y + 100, C_LABEL);
        } else if (mode == 2) {
            this.fontRenderer.drawString("虚拟流体: 产石头", x + 8, y + 100, C_LABEL);
        } else if (container.syncPendingId > 0) {
            ItemStack ore = new ItemStack(Item.getItemById(container.syncPendingId), 1, container.syncPendingMeta);
            if (!ore.isEmpty()) {
                this.fontRenderer.drawString("当前检测到:", x + 8, y + 100, C_LABEL);
                drawSlot(x + 8, y + 101);
                this.mc.getRenderItem().renderItemIntoGUI(ore, x + 10, y + 103);
                this.fontRenderer.drawString(ore.getDisplayName(), x + 28, y + 106, 0xFF202020);
            } else {
                this.fontRenderer.drawString("当前检测到: 无", x + 8, y + 100, C_LABEL);
            }
        } else {
            this.fontRenderer.drawString("当前检测到: 无", x + 8, y + 100, C_LABEL);
        }
    }

    private int modeColor(int mode) {
        switch (mode) {
            case 0: return C_ORANGE;
            case 1: return C_GREEN;
            default: return C_BLUE;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (inButton(mouseX, mouseY, modeBtnX, modeBtnY)) {
            List<String> tip = new ArrayList<>();
            tip.add("模式切换");
            tip.add("当前: " + MODE_NAMES[container.syncMode % 3]);
            tip.add("点击切换 (实体矿/虚拟矿物/虚拟流体)");
            this.drawHoveringText(tip, mouseX, mouseY);
        }
        if (inButton(mouseX, mouseY, powerBtnX, powerBtnY)) {
            List<String> tip = new ArrayList<>();
            tip.add(container.syncEnabled == 1 ? "正在运行" : "已停止");
            tip.add("点击切换运行状态");
            this.drawHoveringText(tip, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (inButton(mouseX, mouseY, modeBtnX, modeBtnY)) {
            this.mc.playerController.windowClick(this.inventorySlots.windowId,
                    ContainerMineralExtractor.MODE_SLOT, 0, ClickType.PICKUP, this.mc.player);
            return;
        }
        if (inButton(mouseX, mouseY, powerBtnX, powerBtnY)) {
            this.mc.playerController.windowClick(this.inventorySlots.windowId,
                    ContainerMineralExtractor.POWER_SLOT, 0, ClickType.PICKUP, this.mc.player);
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean inButton(int mx, int my, int bx, int by) {
        int ax = guiLeft + bx, ay = guiTop + by;
        return mx >= ax && mx < ax + 18 && my >= ay && my < ay + 18;
    }

    private void drawSlot(int sx, int sy) {
        drawRect(sx, sy, sx + 18, sy + 18, C_BORDER);
        drawRect(sx + 1, sy + 1, sx + 17, sy + 17, C_SLOT_FILL);
        drawRect(sx + 1, sy + 1, sx + 17, sy + 2, 0xFFD0D0D0);
        drawRect(sx + 1, sy + 1, sx + 2, sy + 17, 0xFFB0B0B0);
        drawRect(sx + 16, sy + 16, sx + 17, sy + 17, 0xFF5A5A5A);
    }

    private void drawButton(int bx, int by, int color) {
        drawRect(bx, by, bx + 18, by + 18, 0xFF2B2B2B);
        drawRect(bx + 1, by + 1, bx + 17, by + 17, color);
        drawRect(bx + 1, by + 1, bx + 17, by + 2, 0x55FFFFFF);
        drawRect(bx + 16, by + 16, bx + 17, by + 17, 0x55000000);
    }

    private void drawModeIcon(int bx, int by) {
        int c = 0xFFFFFFFF;
        drawRect(bx + 4, by + 5, bx + 14, by + 6, c);
        drawRect(bx + 4, by + 8, bx + 14, by + 9, c);
        drawRect(bx + 4, by + 11, bx + 14, by + 12, c);
    }

    private void drawPowerIcon(int bx, int by, boolean running) {
        int c = 0xFFFFFFFF;
        if (running) {
            drawRect(bx + 6, by + 5, bx + 7, by + 13, c);
            drawRect(bx + 7, by + 6, bx + 8, by + 12, c);
            drawRect(bx + 8, by + 7, bx + 10, by + 11, c);
            drawRect(bx + 10, by + 8, bx + 12, by + 10, c);
        } else {
            drawRect(bx + 6, by + 6, bx + 12, by + 12, c);
        }
    }

    private void drawAspectBar(int ax, int ay, int amount, int color, String label) {
        int max = TileEntityMineralExtractor.getMaxStockStatic();
        int barW = 150;
        this.fontRenderer.drawString(label, ax, ay, C_LABEL);
        drawRect(ax, ay + 8, ax + barW, ay + 16, 0xFF2B2B2B);
        drawRect(ax + 1, ay + 9, ax + barW - 1, ay + 15, 0xFF000000);
        int w = (int) ((barW - 2) * Math.min(1.0F, (float) amount / Math.max(1, max)));
        if (w > 0) drawRect(ax + 1, ay + 9, ax + 1 + w, ay + 15, color);
        drawRect(ax + 1, ay + 9, ax + barW - 1, ay + 10, 0x55FFFFFF);
        String amt = amount + "/" + max;
        this.fontRenderer.drawString(amt, ax + barW - this.fontRenderer.getStringWidth(amt) - 2, ay + 8, 0xFFFFFFFF);
    }
}