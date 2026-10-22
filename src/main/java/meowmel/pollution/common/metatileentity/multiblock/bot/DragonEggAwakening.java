package meowmel.pollution.common.metatileentity.multiblock.bot;

import meowmel.pollution.POConfig;
import meowmel.pollution.api.capability.IStarstreamOperationCore;
import meowmel.pollution.common.entity.dragon.DragonEggHatching;
import meowmel.pollution.common.entity.dragon.DragonGrowthAura;
import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Paid egg lifecycle: Aevitas for each step, Horologium for optional extra steps. */
public final class DragonEggAwakening {
    public enum Status {
        WAITING, PAUSED, DISABLED, BLOCKED, NO_ENERGY, CHARGING, INCUBATING, ACCELERATING, SPAWN_FAILED, COMPLETE
    }

    private int progress;
    private int duration;
    private int aevitasPerTick;
    private int horologiumPerTick;
    private int acceleration;
    private BlockPos target;
    private int lastAdvance;
    private int displayedProgress;
    private int displayedDuration;

    public Status tick(World world, BlockPos nest, BlockPos controller, BlockPos receiver,
                       IStarstreamOperationCore core, boolean workingEnabled) {
        lastAdvance = 0;
        displayedDuration = 0;
        if (world.isRemote) return Status.PAUSED;
        if (target != null && !target.equals(nest)) reset();
        if (!world.isBlockLoaded(nest)) return Status.BLOCKED;
        List<EntityPollutionDragon> eggs = world.getEntitiesWithinAABB(EntityPollutionDragon.class,
                new AxisAlignedBB(nest), egg -> egg.isEntityAlive() && egg.isBoundToIncubator(controller));
        EntityPollutionDragon egg = eggs.isEmpty() ? null : eggs.get(0);
        boolean placedEgg = world.getBlockState(nest).getBlock() == Blocks.DRAGON_EGG;
        if (egg == null && !placedEgg) {
            reset();
            return Status.WAITING;
        }
        if (egg != null) {
            displayedProgress = egg.getTicksInStage();
            displayedDuration = egg.getStageDuration();
        }
        if (!workingEnabled) return Status.PAUSED;
        if (!DragonEggHatching.isAllowed(world)) return Status.DISABLED;
        if (egg != null && !world.isAirBlock(nest)) return Status.BLOCKED;
        if (!DragonEggHatching.isNestClear(world, nest, egg)) return Status.BLOCKED;
        if (egg != null) {
            for (int step = 0; step < configuredAcceleration() && egg.isEgg(); step++) {
                if (!pay(core, world, receiver, configuredAevitas(), configuredHorologium(), step > 0)) break;
                if (egg.advanceIncubation(controller)) lastAdvance++;
            }
            displayedProgress = egg.getTicksInStage();
            if (lastAdvance == 0) return Status.NO_ENERGY;
            if (!egg.isEgg()) return Status.COMPLETE;
            return lastAdvance > 1 ? Status.ACCELERATING : Status.INCUBATING;
        }
        if (progress == 0) {
            duration = Math.max(1, POConfig.DragonSettings.incubatorAwakeningTicks);
            aevitasPerTick = configuredAevitas();
            horologiumPerTick = configuredHorologium();
            acceleration = configuredAcceleration();
            target = nest.toImmutable();
        }
        for (int step = 0; step < acceleration && progress < duration; step++) {
            if (!pay(core, world, receiver, aevitasPerTick, horologiumPerTick, step > 0)) break;
            progress = Math.min(duration, progress + DragonGrowthAura.multiplier(world, nest));
            lastAdvance++;
        }
        if (progress < duration) {
            return lastAdvance == 0 ? Status.NO_ENERGY : lastAdvance > 1 ? Status.ACCELERATING : Status.CHARGING;
        }
        // Completed awakening retries conversion for free, retaining the original block on failure.
        if (!DragonEggHatching.tryActivate(world, nest, controller)) return Status.SPAWN_FAILED;
        progress = 0;
        duration = 0;
        target = null;
        return Status.COMPLETE;
    }

    private static boolean pay(IStarstreamOperationCore core, World world, BlockPos receiver,
                               int aevitas, int horologium, boolean extra) {
        if (core == null) return false;
        Map<String, Long> cost = new LinkedHashMap<>();
        cost.put("aevitas", (long) aevitas);
        if (extra) cost.put("horologium", (long) horologium);
        // Atomic multichannel consumption: failed acceleration spends neither channel.
        return core.consumeConstellationEnergy(world, receiver, Collections.unmodifiableMap(cost), false);
    }

    public static int configuredAevitas() {
        return Math.max(1, POConfig.DragonSettings.incubatorAevitasPerTick);
    }

    public static int configuredHorologium() {
        return Math.max(1, POConfig.DragonSettings.incubatorHorologiumPerTick);
    }

    public static int configuredAcceleration() {
        return Math.max(1, Math.min(16, POConfig.DragonSettings.incubatorAcceleration));
    }

    public int getProgress() { return progress; }
    public int getDisplayProgress() { return displayedDuration > 0 ? displayedProgress : progress; }
    public int getLastAdvance() { return lastAdvance; }
    public int getDuration() {
        return displayedDuration > 0 ? displayedDuration : duration > 0 ? duration
                : Math.max(1, POConfig.DragonSettings.incubatorAwakeningTicks);
    }
    public int getAevitasPerTick() { return progress > 0 ? aevitasPerTick : configuredAevitas(); }
    public int getHorologiumPerTick() { return progress > 0 ? horologiumPerTick : configuredHorologium(); }

    public void reset() {
        progress = 0;
        duration = 0;
        target = null;
        displayedProgress = 0;
        displayedDuration = 0;
        lastAdvance = 0;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("EnergyVersion", 2);
        tag.setInteger("Progress", progress);
        tag.setInteger("Duration", duration);
        tag.setInteger("Aevitas", aevitasPerTick);
        tag.setInteger("Horologium", horologiumPerTick);
        tag.setInteger("Acceleration", acceleration);
        if (target != null) tag.setLong("Nest", target.toLong());
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        reset();
        // Legacy mana-funded awakening cannot be redeemed as constellation energy.
        if (!tag.hasKey("Nest") || tag.getInteger("EnergyVersion") != 2) return;
        duration = Math.max(1, tag.getInteger("Duration"));
        aevitasPerTick = Math.max(1, tag.getInteger("Aevitas"));
        horologiumPerTick = Math.max(1, tag.getInteger("Horologium"));
        acceleration = Math.max(1, Math.min(16, tag.getInteger("Acceleration")));
        progress = Math.max(0, Math.min(duration, tag.getInteger("Progress")));
        target = BlockPos.fromLong(tag.getLong("Nest"));
    }
}
