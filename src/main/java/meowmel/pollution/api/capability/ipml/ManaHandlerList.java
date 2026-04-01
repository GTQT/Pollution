package meowmel.pollution.api.capability.ipml;

import meowmel.pollution.api.capability.IManaHatch;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ManaHandlerList {

    List<IManaHatch> handlers;

    public ManaHandlerList(@NotNull List<IManaHatch> handlers) {
        this.handlers = handlers;
    }

    public long getMana() {
        return handlers.stream().mapToLong(IManaHatch::getMana).sum();
    }

    public long getMaxMana() {
        return handlers.stream().mapToLong(IManaHatch::getMaxMana).sum();
    }

    public boolean isFull() {
        return getMana() >= getMaxMana();
    }

    public long addMana(long amount) {
        long mana = amount;
        for (IManaHatch handler : handlers) {
            if (mana <= 0) break;
            long toAdd = handler.getMaxMana() - handler.getMana();
            toAdd = Math.min(toAdd, mana);
            handler.receiveMana(toAdd);
            mana -= toAdd;
        }
        return mana;
    }

    public long removeMana(long amount) {
        long mana = amount;
        for (IManaHatch handler : handlers) {
            if (mana <= 0) break;
            long toRemove = handler.getMana();
            toRemove = Math.min(toRemove, mana);
            handler.consumeMana(toRemove, false);
            mana -= toRemove;
        }
        return mana;
    }

    public boolean consumeMana(long amount, boolean simulate) {
        if (simulate)
            return getMana() >= amount;
        if (amount <= getMana()) {
            removeMana(amount);
            return true;
        }
        return false;
    }

    public int getTier() {
        //返回最小的
        return handlers.stream().mapToInt(IManaHatch::getTier).min().orElse(1);
    }
}
