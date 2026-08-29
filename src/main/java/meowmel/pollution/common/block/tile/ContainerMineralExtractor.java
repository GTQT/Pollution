package meowmel.pollution.common.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMineralExtractor extends Container {

    // 机器输出槽 9 格单行，与背包 9 列严格对齐（标准 x=8，已回正）
    public static final int MACHINE_SLOTS = 9;
    public static final int GRID_X = 8;
    public static final int MACHINE_Y = 76;

    // 按钮（假槽）：背包右上方、功能区之间的空位，横排两个
    public static final int MODE_SLOT = MACHINE_SLOTS;      // 9
    public static final int POWER_SLOT = MACHINE_SLOTS + 1; // 10
    public static final int MODE_X = 132, MODE_Y = 100;
    public static final int POWER_X = 154, POWER_Y = 100;

    public static final int PLAYER_SLOT_START = MACHINE_SLOTS + 2; // 11
    public static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36; // 47

    private static final ItemStackHandler EMPTY_HANDLER = new ItemStackHandler(1);

    private final TileEntityMineralExtractor te;

    // 供 GUI 读取的同步数据
    public int syncChaos;
    public int syncMagic;
    public int syncPendingId;
    public int syncPendingMeta;
    public int syncMode;
    public int syncEnabled;

    private int lastChaos = -1, lastMagic = -1, lastPendingId = -1, lastPendingMeta = -1;
    private int lastMode = -1, lastEnabled = -1;

    public ContainerMineralExtractor(InventoryPlayer playerInv, TileEntityMineralExtractor te) {
        this.te = te;

        // 机器输出槽（只读）——单行 9 格，x=8 与背包对齐
        for (int i = 0; i < MACHINE_SLOTS; i++) {
            this.addSlotToContainer(new SlotItemHandler(te.getOutputInventory(), i,
                    GRID_X + i * 18, MACHINE_Y) {
                @Override public boolean isItemValid(ItemStack stack) { return false; }
            });
        }

        // 模式 / 启停按钮（假槽：仅接收 windowClick）
        this.addSlotToContainer(makeDummySlot(MODE_X, MODE_Y));   // MODE_SLOT
        this.addSlotToContainer(makeDummySlot(POWER_X, POWER_Y)); // POWER_SLOT

        // 玩家背包（与机器槽同列对齐）
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlotToContainer(new Slot(playerInv, c + r * 9 + 9,
                        GRID_X + c * 18, 122 + r * 18));
            }
        }
        for (int c = 0; c < 9; c++) {
            this.addSlotToContainer(new Slot(playerInv, c, GRID_X + c * 18, 176));
        }
    }

    private static SlotItemHandler makeDummySlot(int x, int y) {
        return new SlotItemHandler(EMPTY_HANDLER, 0, x, y) {
            @Override public boolean isItemValid(ItemStack s) { return false; }
            @Override public boolean canTakeStack(EntityPlayer p) { return false; }
            @Override public boolean isEnabled() { return false; }
        };
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId == MODE_SLOT) {
            te.setMode(te.getMode() + 1); // 实体矿 → 虚拟矿物 → 虚拟流体 → 循环
            return ItemStack.EMPTY;
        }
        if (slotId == POWER_SLOT) {
            te.setEnabled(!te.isEnabled()); // 切换运行/停止
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickType, player);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        int chaos = te.getChaosAmount();
        int magic = te.getMagicAmount();
        int pid = te.getPendingOreItemId();
        int pmeta = te.getPendingOreMeta();
        int mode = te.getMode();
        int enabled = te.isEnabled() ? 1 : 0;

        if (chaos != lastChaos || magic != lastMagic || pid != lastPendingId
                || pmeta != lastPendingMeta || mode != lastMode || enabled != lastEnabled) {
            lastChaos = chaos; lastMagic = magic; lastPendingId = pid;
            lastPendingMeta = pmeta; lastMode = mode; lastEnabled = enabled;
            for (IContainerListener l : this.listeners) {
                l.sendWindowProperty(this, 0, chaos);
                l.sendWindowProperty(this, 1, magic);
                l.sendWindowProperty(this, 2, pid);
                l.sendWindowProperty(this, 3, pmeta);
                l.sendWindowProperty(this, 4, mode);
                l.sendWindowProperty(this, 5, enabled);
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0: syncChaos = data; break;
            case 1: syncMagic = data; break;
            case 2: syncPendingId = data; break;
            case 3: syncPendingMeta = data; break;
            case 4: syncMode = data; break;
            case 5: syncEnabled = data; break;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();
        if (index < MACHINE_SLOTS) {
            if (!this.mergeItemStack(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY; // 输出槽只读 / 假槽无物品
        }
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();
        return copy;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq(te.getPos().getX() + 0.5D,
                te.getPos().getY() + 0.5D, te.getPos().getZ() + 0.5D) <= 64.0D;
    }
}