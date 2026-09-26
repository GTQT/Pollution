package meowmel.pollution.api.capability.ipml;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import meowmel.pollution.api.capability.IEssentiaHatch;
import meowmel.pollution.common.data.EssentiaFuelData;
import meowmel.pollution.common.data.EssentiaFuelData.EssentiaCategory;
import meowmel.pollution.common.metatileentity.multiblock.generator.MetaTileEntityLargeEssentiaGenerator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Burn logic for the Large Essentia Generator.
 *
 * <p>Ported from GregicaPlusPlus' {@code EssentiaLogic}. Every tick the machine converts essentia
 * from its input hatch into EU until either the hatch runs dry, the output buffer fills, or the
 * per-tick voltage/amperage budget of the installed dynamo hatch is reached. Essentia burn value is
 * {@code fuelValue * FUEL_COEFFICIENT * catalystMultiplier * cellTierMultiplier}.</p>
 *
 * <p>Deviations from the original, each deliberate:</p>
 * <ul>
 *   <li>The ELECTRIC branch no longer computes {@code 3 ^ outputVoltage}; that expression saturates
 *       a {@code long} at every real voltage tier, so Electric essentia produced
 *       {@code Long.MAX_VALUE} EU per unit. It now scales with the dynamo's voltage tier, keeping the
 *       "more power at higher tier" intent without the overflow.</li>
 *   <li>Catalyst fluid is consumed at most once per tick, and only when that tick actually burns
 *       essentia, so an idle machine no longer drips coolant.</li>
 * </ul>
 */
public class EssentiaGeneratorLogic {

    /** Ticks per displayed progress cycle. Purely cosmetic; burning happens every tick. */
    public static final int CYCLE_TICKS = 20;

    /**
     * Cell tier multiplier, indexed by {@code cellTier - 1}. Mirrors the original
     * {@code stable = {0, 1, 2, 5, 10}} table after its {@code /25} normalisation.
     */
    private static final int[] CELL_MULTIPLIER = {1, 2, 5, 10};

    private final MetaTileEntityLargeEssentiaGenerator host;

    private int progressTime;
    private int cellTier = 1;
    private int upgradeMask = 1;
    private boolean active;

    /** EU generated during the current 20-tick cycle, for display. */
    private long lastCycleEu;
    private long cycleEu;

    /** Pollution accounting, separate from the display cycle so it stays correct when paused. */
    private int pollutionTicker;
    private long cyclePollutionEu;

    /** Aspect currently being burned, for the GUI. */
    @Nullable
    private Aspect lastBurnedAspect;

    public EssentiaGeneratorLogic(MetaTileEntityLargeEssentiaGenerator host) {
        this.host = host;
    }

    // ------------------------------------------------------------------
    // state
    // ------------------------------------------------------------------

    public void setCellTier(int cellTier) {
        this.cellTier = Math.max(1, Math.min(CELL_MULTIPLIER.length, cellTier));
    }

    public int getCellTier() {
        return cellTier;
    }

    public int getCellMultiplier() {
        return getCellMultiplierForTier(cellTier);
    }

    /** Static form so tools/JEI can display the table without an instance. */
    public static int getCellMultiplierForTier(int tier) {
        return CELL_MULTIPLIER[Math.max(0, Math.min(CELL_MULTIPLIER.length - 1, tier - 1))];
    }

    public int getUpgradeMask() {
        return upgradeMask;
    }

    /** Installs an upgrade bit; returns true when something actually changed. */
    public boolean installUpgrade(int mask) {
        if (mask == 0 || (upgradeMask & mask) != 0) return false;
        upgradeMask |= mask;
        markDirty();
        return true;
    }

    /** Replaces the whole mask; the controller derives it from the upgrade bay. */
    public void setUpgradeMask(int mask) {
        this.upgradeMask = mask;
        markDirty();
    }

    public boolean hasUpgrade(int categoryIndex) {
        return (upgradeMask & (1 << categoryIndex)) != 0;
    }

    public boolean isWorkingEnabled() {
        return true;
    }

    public boolean isActive() {
        return active;
    }

    public int getProgress() {
        return progressTime;
    }

    public int getMaxProgress() {
        return CYCLE_TICKS;
    }

    public int getProgressPercent() {
        return CYCLE_TICKS == 0 ? 0 : (int) (100.0F * progressTime / CYCLE_TICKS);
    }

    public long getLastCycleEu() {
        return lastCycleEu;
    }

    @Nullable
    public Aspect getLastBurnedAspect() {
        return lastBurnedAspect;
    }

    public void invalidate() {
        progressTime = 0;
        cycleEu = 0;
        cyclePollutionEu = 0;
        pollutionTicker = 0;
        active = false;
    }

    private void markDirty() {
        host.markDirty();
    }

    // ------------------------------------------------------------------
    // main tick
    // ------------------------------------------------------------------

    public void updateLogic() {
        IEnergyContainer energy = host.getEnergyContainer();
        if (energy == null) {
            active = false;
            return;
        }

        List<IEssentiaHatch> hatches = host.getEssentiaHatches();
        if (hatches.isEmpty()) {
            active = false;
            return;
        }

        // Nothing to do when the dynamo buffer is already full; this also stops essentia being
        // burned for energy that would be discarded.
        if (energy.getEnergyStored() >= energy.getEnergyCapacity()) {
            active = false;
            return;
        }

        long voltLimit = energy.getOutputVoltage();
        long ampLimit = energy.getOutputAmperage();
        if (voltLimit <= 0 || ampLimit <= 0) {
            active = false;
            return;
        }

        // Cap the burn at what the dynamo can actually accept. EnergyContainerHandler clamps to
        // capacity and silently discards the excess, so burning a full volt*amp budget into a
        // nearly-full buffer would destroy essentia for nothing.
        long headroom = energy.getEnergyCapacity() - energy.getEnergyStored();
        long budget = Math.min(voltLimit * ampLimit, headroom);
        if (budget <= 0) {
            active = false;
            return;
        }
        long generated = burnEssentia(hatches, budget);

        if (generated > 0) {
            energy.addEnergy(generated);
            cycleEu += generated;
            cyclePollutionEu += generated;
            active = true;
            if (++progressTime >= CYCLE_TICKS) {
                progressTime = 0;
                lastCycleEu = cycleEu;
                cycleEu = 0;
            }
        } else {
            active = false;
            progressTime = 0;
            lastCycleEu = 0;
            cycleEu = 0;
        }
    }

    /**
     * Walks every hatch and burns essentia until {@code budget} EU worth has been produced.
     *
     * @return the EU produced this tick, never above {@code budget}
     */
    private long burnEssentia(List<IEssentiaHatch> hatches, long budget) {
        long produced = 0;
        // Bills are tracked per category: a single tick can burn aspects from several categories,
        // and each one must be charged against its own catalyst fluid.
        java.util.EnumMap<EssentiaCategory, Long> catalystBills = new java.util.EnumMap<>(EssentiaCategory.class);
        java.util.EnumMap<EssentiaCategory, Aspect> catalystSources = new java.util.EnumMap<>(EssentiaCategory.class);
        Aspect burned = null;

        for (IEssentiaHatch hatch : hatches) {
            AspectList aspects = hatch.getEssentiaList();
            if (aspects == null) continue;

            // Copy the aspect array: draining mutates the AspectList underneath us.
            Aspect[] types = aspects.getAspects();
            for (Aspect aspect : types) {
                if (aspect == null) continue;
                if (produced >= budget) break;
                if (!isValidEssentia(aspect)) continue;

                int perUnitBase = EssentiaFuelData.getFuelValue(aspect);
                if (perUnitBase <= 0) continue;

                EssentiaCategory category = EssentiaFuelData.getCategory(aspect);
                double multiplier = catalystMultiplier(category, aspect);
                if (multiplier <= 0.0D) continue;

                long perUnit = (long) (perUnitBase * multiplier) * getCellMultiplier();
                if (perUnit <= 0L) continue;
                perUnit = Math.min(perUnit, budget);

                int affordable = (int) Math.min(Integer.MAX_VALUE, (budget - produced) / perUnit);
                if (affordable <= 0) continue;

                int drained = hatch.drainEssentia(aspect, affordable, false);
                if (drained <= 0) continue;

                produced += (long) drained * perUnit;
                if (burned == null) burned = aspect;

                // Deferred: every category's catalyst bill is charged once, after the loop.
                if (category != null) {
                    int perUnitCost = catalystCostPerUnit(category, aspect);
                    if (perUnitCost > 0) {
                        catalystBills.merge(category, (long) perUnitCost * drained, Long::sum);
                        catalystSources.putIfAbsent(category, aspect);
                    }
                }
            }
            if (produced >= budget) break;
        }

        if (produced > 0) {
            this.lastBurnedAspect = burned;
            for (java.util.Map.Entry<EssentiaCategory, Long> bill : catalystBills.entrySet()) {
                consumeCatalyst(bill.getKey(), bill.getValue(), catalystSources.get(bill.getKey()));
            }
            if (++pollutionTicker >= CYCLE_TICKS) {
                pollutionTicker = 0;
                emitCyclePollution(cyclePollutionEu);
                cyclePollutionEu = 0;
            }
        }
        return produced;
    }

    /**
     * Reports pollution once per 20-tick cycle rather than once per tick, so the machine's
     * environmental footprint matches its displayed cycle instead of being 20x too high.
     */
    private void emitCyclePollution(long euThisCycle) {
        if (euThisCycle <= 0) return;
        Aspect burned = lastBurnedAspect;
        if (burned == null || EssentiaFuelData.getCategory(burned) != EssentiaCategory.TAINTED) return;

        // 32768 EU == 1 pollution point, matching the pack's other generator conversions.
        double pollution = euThisCycle / 32768.0D;
        // ticks=0 applies immediately: the mixin only fires when pollutionTicks >= ticks, so
        // ticks=1 would skip every first call and halve the effective rate.
        host.pollution(pollution, 0);

        if (host.getWorld() == null || host.getWorld().isRemote) return;
        int chance = 2000;
        if (hasCatalyst(1, "purifying_fluid")) chance = 0;
        if (greghostRng().nextInt(10000) >= chance) return;
        int tx = greghostRng().nextInt(4);
        int tz = greghostRng().nextInt(4);
        net.minecraft.util.math.BlockPos pos = host.getPos().add(tx, 0, tz);
        if (host.getWorld().isAirBlock(pos)) {
            host.getWorld().setBlockState(pos, thaumcraft.api.blocks.BlocksTC.fluxGoo.getDefaultState());
        }
    }

    private static java.util.Random greghostRng() {
        return gregtech.api.GTValues.RNG;
    }

    // ------------------------------------------------------------------
    // catalysts
    // ------------------------------------------------------------------

    /**
     * Divisor applied to an aspect's {@code consumeCeo} to get its per-unit catalyst cost. These
     * are the divisors the original machine baked into each branch.
     */
    private static int catalystDivisor(@Nullable EssentiaCategory category) {
        if (category == null) return 0;
        return switch (category) {
            case AIR -> 8;
            case THERMAL -> 2;
            case UNSTABLE -> 4;
            case VICTUS -> 18;
            case TAINTED -> 3;
            case MECHANICS -> 20;
            case SPRITE -> 2;
            case RADIATION -> 6;
            default -> 0;
        };
    }

    /** Per-unit catalyst cost for this aspect, rounded up; 0 when the category needs none. */
    private int catalystCostPerUnit(EssentiaCategory category, Aspect aspect) {
        int divisor = catalystDivisor(category);
        if (divisor == 0) return 0;
        return (int) Math.ceil(EssentiaFuelData.getConsumeCeo(aspect) * divisor);
    }

    /**
     * Multiplier for the best currently-available catalyst of this aspect's category.
     * A category that requires a catalyst returns 0 when none of its fluids are available.
     */
    private double catalystMultiplier(@Nullable EssentiaCategory category, Aspect aspect) {
        if (category == null) return 0.0D;
        int cost = catalystCostPerUnit(category, aspect);
        switch (category) {
            case NORMAL:
                return 1.0D;
            case ELECTRIC:
                // See class javadoc: originally 3^voltage, which saturated a long.
                return Math.pow(2.0D, Math.max(0, host.getEnergyTier()));
            case AIR:
                if (hasCatalyst(cost, "liquid_air")) return 1.5D;
                return hasCatalyst(cost, "air") ? 1.0D : 0.0D;
            case THERMAL:
                return pickThermal(cost);
            case UNSTABLE:
                return pickUnstable(cost);
            case VICTUS:
                if (hasCatalyst(cost, "xpjuice")) return 2.0D;
                if (hasCatalyst(cost, "lifeessence")) return 6.0D;
                return 1.0D;
            case TAINTED:
                // Liquid Death is the only Tainted catalyst that grants a multiplier. Purifying
                // Fluid is handled in the pollution step instead: it suppresses the flux-goo
                // side effect rather than boosting output.
                return hasCatalyst(cost, "liquid_death") ? 60.0D : 1.0D;
            case MECHANICS:
                return hasCatalyst(cost, "lubricant") ? 1.0D : 0.0D;
            case SPRITE:
                if (hasCatalyst(cost, "spirit")) return 10.0D;
                if (hasCatalyst(cost, "hollowtears")) return 15.0D;
                return 1.0D;
            case RADIATION:
                if (hasCatalyst(cost, "caesium")) return 2.0D;
                if (hasCatalyst(cost, "uranium_235")) return 3.0D;
                if (hasCatalyst(cost, "naquadah")) return 4.0D;
                if (hasCatalyst(cost, "atomic_separation_catalyst")) return 16.0D;
                return 1.0D;
            default:
                return 0.0D;
        }
    }


    private double pickThermal(int ceo) {
        if (hasCatalyst(ceo, "super_coolant")) return 9.0D;
        if (hasCatalyst(ceo, "cryotheum")) return 5.0D;
        if (hasCatalyst(ceo, "coolant")) return 1.5D;
        if (hasCatalyst(ceo, "ice")) return 1.2D;
        if (hasCatalyst(ceo, "distilled_water")) return 1.0D;
        if (hasCatalyst(ceo, "water")) return 0.5D;
        return 0.0D;
    }

    private double pickUnstable(int ceo) {
        if (hasCatalyst(ceo, "xenon")) return 4.0D;
        if (hasCatalyst(ceo, "krypton")) return 3.0D;
        if (hasCatalyst(ceo, "argon")) return 2.5D;
        if (hasCatalyst(ceo, "neon")) return 2.2D;
        if (hasCatalyst(ceo, "helium")) return 2.0D;
        if (hasCatalyst(ceo, "nitrogen")) return 1.0D;
        return 0.0D;
    }

    /** @return true when at least one of the named fluids has at least {@code amount} available. */
    private boolean hasCatalyst(int amount, String... fluidNames) {
        if (amount <= 0) return true;
        for (String name : fluidNames) {
            if (countFluid(amount, name) >= amount) return true;
        }
        return false;
    }

    private int countFluid(int amount, String fluidName) {
        net.minecraftforge.fluids.Fluid fluid = net.minecraftforge.fluids.FluidRegistry.getFluid(fluidName);
        if (fluid == null) return 0;
        IMultipleTankHandler tanks = host.getInputFluidInventory();
        if (tanks == null) return 0;
        FluidStack probe = new FluidStack(fluid, amount);
        FluidStack drained = tanks.drain(probe, false);
        return drained == null ? 0 : drained.amount;
    }

    /**
     * Charges the catalyst bill for this tick against the specific catalyst that granted the
     * multiplier, so e.g. Tainted essentia burned with Liquid Death is not billed for Purifying
     * Fluid.
     */
    private void consumeCatalyst(EssentiaCategory category, long amount, @Nullable Aspect burned) {
        if (amount <= 0) return;
        int clamped = (int) Math.min(Integer.MAX_VALUE, amount);
        switch (category) {
            case AIR -> drainFirst(clamped, "liquid_air", "air");
            case THERMAL -> drainFirst(clamped, "super_coolant", "cryotheum", "coolant", "ice",
                    "distilled_water", "water");
            case UNSTABLE -> drainFirst(clamped, "xenon", "krypton", "argon", "neon", "helium", "nitrogen");
            case VICTUS -> drainFirst(clamped, "xpjuice", "lifeessence");
            case TAINTED -> drainFirst(clamped, "liquid_death", "purifying_fluid");
            case MECHANICS -> drainFirst(clamped, "lubricant");
            case SPRITE -> drainFirst(clamped, "spirit", "hollowtears");
            case RADIATION -> drainFirst(clamped, "caesium", "uranium_235", "naquadah",
                    "atomic_separation_catalyst");
            default -> {
            }
        }
    }

    private void drainFirst(int amount, String... fluidNames) {
        IMultipleTankHandler tanks = host.getInputFluidInventory();
        if (tanks == null) return;
        for (String name : fluidNames) {
            net.minecraftforge.fluids.Fluid fluid = net.minecraftforge.fluids.FluidRegistry.getFluid(name);
            if (fluid == null) continue;
            FluidStack probe = new FluidStack(fluid, amount);
            FluidStack drained = tanks.drain(probe, true);
            if (drained != null && drained.amount > 0) return;
        }
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    public boolean isValidEssentia(Aspect aspect) {
        int index = EssentiaFuelData.getCategoryIndex(aspect);
        return index != -1 && hasUpgrade(index) && EssentiaFuelData.getFuelValue(aspect) > 0;
    }

    // ------------------------------------------------------------------
    // persistence
    // ------------------------------------------------------------------

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("ProgressTime", progressTime);
        data.setInteger("CellTier", cellTier);
        data.setInteger("UpgradeMask", upgradeMask);
        data.setLong("LastCycleEu", lastCycleEu);
        return data;
    }

    public void readFromNBT(NBTTagCompound data) {
        this.progressTime = data.getInteger("ProgressTime");
        setCellTier(data.hasKey("CellTier") ? data.getInteger("CellTier") : 1);
        this.upgradeMask = data.hasKey("UpgradeMask") ? data.getInteger("UpgradeMask") : 1;
        this.lastCycleEu = data.getLong("LastCycleEu");
    }
}
