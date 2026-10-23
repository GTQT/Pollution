package meowmel.pollution.common.metatileentity.multiblock.bloodMagic;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import meowmel.pollution.POConfig;
import meowmel.pollution.common.entity.dragon.DragonLifeStage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.LongPredicate;

/** Server-only paid batches. No entities are spawned, ticked, damaged or modified here. */
public final class DragonBloodExtractionLogic {
    public static final int DURATION = 100, CAPACITY = 16000;
    public enum Status { WAITING, RUNNING, PAUSED, LOW_VOLTAGE, NO_ENERGY, MAINTENANCE, OUTPUT_FULL, CHANGED }

    public static final class Source {
        public final int slot;
        public final UUID dragon, capture, content;
        public final DragonLifeStage stage;
        public final int yield;

        public Source(int slot, UUID dragon, UUID capture, UUID content, DragonLifeStage stage, int yield) {
            this.slot = slot;
            this.dragon = dragon;
            this.capture = capture;
            this.content = content;
            this.stage = stage;
            this.yield = yield;
        }

        public NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("Slot", slot);
            tag.setUniqueId("Dragon", dragon);
            tag.setUniqueId("Capture", capture);
            tag.setUniqueId("Content", content);
            tag.setString("Stage", stage.name());
            tag.setInteger("Yield", yield);
            return tag;
        }

        @Override public boolean equals(Object obj) {
            if (!(obj instanceof Source)) return false;
            Source other = (Source) obj;
            return slot == other.slot && dragon.equals(other.dragon) && capture.equals(other.capture)
                    && content.equals(other.content) && stage == other.stage && yield == other.yield;
        }
        @Override public int hashCode() { return content.hashCode() * 31 + slot; }
    }

    private List<Source> batch = Collections.emptyList();
    private int progress, buffered, batchYield;
    private long batchPower;
    private Status status = Status.WAITING;
    private int lastAdvance;
    private long lastAevitas;

    public static int yieldFor(DragonLifeStage stage) {
        POConfig.DragonBloodExtractorSettings config = POConfig.DragonBloodExtractorSettings;
        int value;
        switch (stage) {
            case HATCHLING: value = config.hatchlingYield; break;
            case INFANT: value = config.infantYield; break;
            case FLEDGLING: value = config.fledglingYield; break;
            case JUVENILE: value = config.juvenileYield; break;
            case ADULT: value = config.adultYield; break;
            default: return 0;
        }
        return Math.max(1, Math.min(CAPACITY / 10, value));
    }

    public static long powerFor(int count) {
        if (count <= 0) return 0;
        return Math.max(1L, (long) POConfig.DragonBloodExtractorSettings.baseEUt)
                + Math.max(1L, (long) POConfig.DragonBloodExtractorSettings.perDragonEUt) * Math.min(10, count);
    }

    public Status tick(List<Source> sources, IEnergyContainer energy, long highestVoltage,
                       boolean enabled, boolean maintained) {
        return tick(sources, energy, highestVoltage, enabled, maintained, 1, 1, cost -> false);
    }

    public static int configuredAevitasMultiplier() {
        return Math.max(1, Math.min(4, POConfig.DragonBloodExtractorSettings.aevitasMultiplier));
    }

    public static long aevitasCost(int count) {
        return Math.max(1L, Math.min(1000000L, POConfig.DragonBloodExtractorSettings.aevitasPerExtraProgress))
                * Math.max(0, Math.min(10, count));
    }

    /** EU is paid once per working world tick; optional CE pays only for useful extra progress. */
    public Status tick(List<Source> sources, IEnergyContainer energy, long highestVoltage,
                       boolean enabled, boolean maintained, int fleshMultiplier, int aevitasMultiplier,
                       LongPredicate consumeAevitas) {
        lastAdvance = 0;
        lastAevitas = 0;
        if (!batch.isEmpty() && !batch.equals(sources)) {
            cancelBatch();
            return status = Status.CHANGED;
        }
        if (sources.isEmpty()) return status = Status.WAITING;
        if (!enabled) return status = Status.PAUSED;
        if (!maintained) return status = Status.MAINTENANCE;
        if (highestVoltage < GTValues.V[GTValues.UHV]) return status = Status.LOW_VOLTAGE;
        int output = batch.isEmpty() ? sum(sources) : batchYield;
        if (output <= 0 || output > CAPACITY - buffered) return status = Status.OUTPUT_FULL;
        if (batch.isEmpty()) {
            batch = Collections.unmodifiableList(new ArrayList<>(sources));
            batchYield = output;
            batchPower = powerFor(batch.size());
        }
        if (energy.getEnergyStored() < batchPower) return status = Status.NO_ENERGY;
        // IEnergyContainer returns a signed energy delta. Refund partial debit without advancing.
        long removed = energy.removeEnergy(batchPower);
        if (removed != -batchPower) {
            if (removed < 0) energy.addEnergy(-removed);
            return status = Status.NO_ENERGY;
        }
        int aura = Math.max(1, Math.min(16, fleshMultiplier));
        int celestial = Math.max(1, Math.min(4, aevitasMultiplier));
        int remaining = DURATION - progress;
        int advance = Math.min(aura, remaining);
        int extraLimit = Math.min(aura * (celestial - 1), remaining - advance);
        long cost = aevitasCost(batch.size()), spentCE = 0;
        for (int extra = 0; extra < extraLimit; extra++) {
            if (!consumeAevitas.test(cost)) break;
            advance++;
            spentCE += cost;
        }
        progress += advance;
        if (progress == DURATION) {
            buffered += batchYield;
            cancelBatch();
        }
        lastAdvance = advance;
        lastAevitas = spentCE;
        return status = Status.RUNNING;
    }

    private static int sum(List<Source> sources) {
        if (sources.size() > 10) return 0;
        Set<UUID> ids = new HashSet<>();
        Set<Integer> slots = new HashSet<>();
        int result = 0;
        for (Source source : sources) {
            if (source.slot < 0 || source.slot >= 10 || source.stage == DragonLifeStage.EGG
                    || source.yield <= 0 || source.yield > CAPACITY / 10
                    || !ids.add(source.dragon) || !slots.add(source.slot)) return 0;
            result += source.yield;
        }
        return result;
    }

    public void export(IFluidHandler output, IntFunction<FluidStack> fluid) {
        if (buffered <= 0) return;
        int accepted = output.fill(fluid.apply(buffered), true);
        buffered -= Math.max(0, Math.min(buffered, accepted));
    }

    public void cancelBatch() {
        lastAdvance = 0;
        lastAevitas = 0;
        progress = batchYield = 0;
        batchPower = 0;
        batch = Collections.emptyList();
    }

    public int getProgress() { return progress; }
    public int getBuffered() { return buffered; }
    public Status getStatus() { return status; }
    public int getBatchYield() { return batchYield; }
    public long getBatchPower() { return batchPower; }
    public int getLastAdvance() { return lastAdvance; }
    public long getLastAevitas() { return lastAevitas; }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Version", 1);
        tag.setInteger("Buffered", buffered);
        tag.setInteger("Progress", progress);
        tag.setLong("Power", batchPower);
        tag.setInteger("Yield", batchYield);
        NBTTagList participants = new NBTTagList();
        for (Source source : batch) participants.appendTag(source.write());
        tag.setTag("Sources", participants);
        return tag;
    }

    public void read(NBTTagCompound tag) {
        cancelBatch();
        status = Status.WAITING;
        buffered = Math.max(0, Math.min(CAPACITY, tag.getInteger("Buffered")));
        if (tag.getInteger("Version") != 1) return;
        NBTTagList participants = tag.getTagList("Sources", 10);
        if (participants.tagCount() == 0 || participants.tagCount() > 10) return;
        List<Source> restored = new ArrayList<>();
        try {
            for (int i = 0; i < participants.tagCount(); i++) {
                NBTTagCompound p = participants.getCompoundTagAt(i);
                if (!p.hasUniqueId("Dragon") || !p.hasUniqueId("Capture") || !p.hasUniqueId("Content")) return;
                restored.add(new Source(p.getInteger("Slot"), p.getUniqueId("Dragon"), p.getUniqueId("Capture"),
                        p.getUniqueId("Content"), DragonLifeStage.valueOf(p.getString("Stage")), p.getInteger("Yield")));
            }
        } catch (IllegalArgumentException invalid) { return; }
        int total = sum(restored), ticks = tag.getInteger("Progress");
        long power = tag.getLong("Power");
        if (total <= 0 || total != tag.getInteger("Yield") || ticks < 0 || ticks >= DURATION
                || power <= 0 || power > 11L * Integer.MAX_VALUE) return;
        batch = Collections.unmodifiableList(restored);
        batchYield = total;
        batchPower = power;
        progress = ticks;
    }

    /** Breaking/replacing the controller carries only finished fluid, never partly paid batches. */
    public NBTTagCompound writeItem() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Buffered", buffered);
        return tag;
    }
}
