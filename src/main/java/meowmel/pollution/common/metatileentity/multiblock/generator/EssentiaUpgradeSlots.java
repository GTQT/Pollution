package meowmel.pollution.common.metatileentity.multiblock.generator;

import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import meowmel.pollution.common.items.ItemEssentiaUpgrade;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The Large Essentia Generator's upgrade bay.
 *
 * <p>One slot per upgrade category, indexed by the module's damage value. A slot only accepts its own
 * module and installed modules cannot be pulled back out, so the bay is a one-way "unlock this
 * category" action rather than a temporary inventory.</p>
 */
public class EssentiaUpgradeSlots extends GTItemStackHandler {

    public EssentiaUpgradeSlots(MetaTileEntity owner) {
        super(owner, ItemEssentiaUpgrade.ListUpgrade.length);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return accepts(slot, stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return accepts(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        // Installed modules are permanent; the bay is not a storage chest.
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty() || !accepts(slot, stack)) return;
        super.setStackInSlot(slot, stack);
    }

    private static boolean accepts(int slot, ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (slot < 0 || slot >= ItemEssentiaUpgrade.ListUpgrade.length) return false;
        return stack.getItem() instanceof ItemEssentiaUpgrade && stack.getMetadata() == slot;
    }

    /** Bitmask of every category whose module is currently installed. */
    public int toMask() {
        int mask = 0;
        for (int slot = 0; slot < getSlots(); slot++) {
            if (!getStackInSlot(slot).isEmpty()) mask |= 1 << slot;
        }
        return mask;
    }

    /** @return true when the module was newly installed into its (empty) slot. */
    public boolean install(ItemStack module) {
        if (module.isEmpty() || !(module.getItem() instanceof ItemEssentiaUpgrade)) return false;
        int slot = module.getMetadata();
        if (slot < 0 || slot >= getSlots()) return false;
        if (!getStackInSlot(slot).isEmpty()) return false;
        super.setStackInSlot(slot, module.copy());
        return true;
    }
}
