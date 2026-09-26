package meowmel.pollution.common.metatileentity.multiblockpart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * Aspect storage for the Large Essentia Generator's input hatch.
 *
 * <p>Every aspect shares one capacity pool, mirroring {@code EssentiaHatch.getEssentiaAmount(null)}
 * in GregicaPlusPlus. Serialisation uses the same {@code key}/{@code amount} tag shape Thaumcraft
 * jars use so the contents stay inspectable.</p>
 */
public class EssentiaHatchContainer {

    private final int capacity;
    private AspectList aspects = new AspectList();

    public EssentiaHatchContainer(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public AspectList getAspects() {
        return aspects;
    }

    /** Total essentia across all aspect types. */
    public int getAmount() {
        int total = 0;
        for (Aspect aspect : aspects.getAspects()) {
            total += aspects.getAmount(aspect);
        }
        return total;
    }

    public int getAmount(Aspect aspect) {
        return aspect == null ? 0 : aspects.getAmount(aspect);
    }

    public int getFreeSpace() {
        return Math.max(0, capacity - getAmount());
    }

    public boolean isFull() {
        return getAmount() >= capacity;
    }

    public boolean isEmpty() {
        return aspects.size() == 0;
    }

    /**
     * @return the amount that could not be accepted
     */
    public int add(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) return Math.max(0, amount);
        int accepted = Math.min(amount, getFreeSpace());
        if (accepted <= 0) return amount;
        aspects.add(aspect, accepted);
        return amount - accepted;
    }

    /**
     * @return the amount actually removed
     */
    public int drain(Aspect aspect, int amount, boolean simulate) {
        if (aspect == null || amount <= 0) return 0;
        int stored = aspects.getAmount(aspect);
        int removed = Math.min(stored, amount);
        if (removed <= 0 || simulate) return removed;
        if (removed == stored) {
            aspects.remove(aspect);
        } else {
            aspects.remove(aspect);
            aspects.add(aspect, stored - removed);
        }
        return removed;
    }

    public void clear() {
        aspects = new AspectList();
    }

    public @NotNull NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Capacity", capacity);
        NBTTagList list = new NBTTagList();
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null) continue;
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("key", aspect.getTag());
            entry.setInteger("amount", aspects.getAmount(aspect));
            list.appendTag(entry);
        }
        compound.setTag("Aspects", list);
        return compound;
    }

    public void deserializeNBT(NBTTagCompound compound) {
        aspects = new AspectList();
        NBTTagList list = compound.getTagList("Aspects", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            if (!entry.hasKey("key")) continue;
            Aspect aspect = Aspect.getAspect(entry.getString("key"));
            int amount = entry.getInteger("amount");
            if (aspect != null && amount > 0) {
                aspects.add(aspect, amount);
            }
        }
    }
}
