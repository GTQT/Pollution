package meowmel.pollution.common.metatileentity.multiblockpart;

import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

public class VisContainer {
    private int visStorage;
    private int visStorageMax;

    public VisContainer(int visStorageMax) {
        this.visStorageMax = visStorageMax;
    }

    public void addVis(int amount) {
        visStorage += amount;
        visStorage = Math.min(visStorageMax, visStorage);
    }

    public void removeVis(int amount) {
        visStorage -= amount;
        visStorage = Math.max(0, visStorage);
    }

    public boolean drainVis(int amount, boolean simulate) {
        if (simulate)
            return visStorage >= amount;
        else {
            visStorage = Math.max(0, visStorage - amount);
            return true;
        }
    }

    public boolean isFull() {
        return visStorage >= visStorageMax;
    }

    public int getVis() {
        return visStorage;
    }

    public int getMaxVis() {
        return visStorageMax;
    }

    public @NotNull NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Capacity", visStorageMax);
        compound.setInteger("Stored", visStorage);
        return compound;
    }

    public void deserializeNBT(NBTTagCompound compound) {
        visStorageMax = compound.getInteger("Capacity");
        visStorage = compound.getInteger("Stored");
    }
}
