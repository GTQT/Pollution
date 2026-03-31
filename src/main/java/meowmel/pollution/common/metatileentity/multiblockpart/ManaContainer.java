package meowmel.pollution.common.metatileentity.multiblockpart;

import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

public class ManaContainer {
    private long manaStorage;
    private long manaStorageMax;
    private int amp;

    public ManaContainer(long manaStorageMax,int amp) {
        this.manaStorageMax = manaStorageMax;
        this.amp = amp;
    }

    public long addMana(long amount) {
        long add = Math.min(amount, manaStorageMax - manaStorage);
        manaStorage += add;
        return add;
    }

    public long removeMana(long amount) {
        long remove = Math.min(amount, manaStorage);
        manaStorage -= remove;
        return remove;
    }

    public boolean drainMana(long amount, boolean simulate) {
        if (simulate)
            return manaStorage >= amount;
        else {
            removeMana(amount);
            return true;
        }
    }

    public boolean isFull() {
        return manaStorage >= manaStorageMax;
    }

    public long getMana() {
        return manaStorage;
    }

    public long getMaxMana() {
        return manaStorageMax;
    }

    public @NotNull NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setLong("Capacity", manaStorageMax);
        compound.setLong("Stored", manaStorage);
        compound.setInteger("Amp", amp);
        return compound;
    }

    public void deserializeNBT(NBTTagCompound compound) {
        manaStorageMax = compound.getLong("Capacity");
        manaStorage = compound.getLong("Stored");
        amp = compound.getInteger("Amp");
    }
}
