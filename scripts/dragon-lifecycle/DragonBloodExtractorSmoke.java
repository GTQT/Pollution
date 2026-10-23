import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.util.RelativeDirection;
import meowmel.pollution.POConfig;
import meowmel.pollution.common.block.tile.TileEntityBloodyStarlightContainer;
import meowmel.pollution.common.entity.dragon.*;
import meowmel.pollution.common.metatileentity.multiblock.bloodMagic.*;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import java.util.*;

public final class DragonBloodExtractorSmoke {
    private static int checks;
    private static final Fluid BLOOD = new Fluid("extractor_smoke_blood", new ResourceLocation("minecraft:blocks/water_still"),
            new ResourceLocation("minecraft:blocks/water_flow"));
    private static final long UHV = GTValues.V[GTValues.UHV];

    public static void main(String[] args) {
        try {
            Bootstrap.register();
            net.minecraftforge.fluids.FluidRegistry.registerFluid(BLOOD);
            net.minecraftforge.fml.common.registry.GameRegistry.registerTileEntity(TileEntityBloodyStarlightContainer.class,
                    new ResourceLocation("pollution", "extractor_smoke_container"));
            shape();
            yieldsAndPayment();
            pausesAndChanges();
            persistenceAndOutput();
            acceleration();
            containers();
            controllerConnections();
            System.out.println("PASS: " + checks + " extractor checks: shape/orientations, UHV payment, all stages, batching, output, persistence, frozen NBT and claims");
            System.exit(0);
        } catch (Throwable failure) { failure.printStackTrace(); System.exit(1); }
    }

    private static void shape() {
        String[][] shape = BloodyBloodExtractorShape.createAisles();
        int slots = 0, controllers = 0;
        check(shape.length == 7, "Seven aisles");
        for (String[] aisle : shape) {
            check(aisle.length == 13, "Thirteen levels");
            for (String row : aisle) {
                check(row.length() == 7, "Seven columns");
                for (char c : row.toCharArray()) {
                    check("#XGPDNHS".indexOf(c) >= 0, "All symbols mapped");
                    if (c == 'D') slots++;
                    if (c == 'S') controllers++;
                }
            }
        }
        check(slots == 10 && controllers == 1, "Ten slots and unique controller");
        check(shape[1][3].charAt(3) == 'S', "Controller matches coordinates");
        for (EnumFacing front : EnumFacing.HORIZONTALS) {
            Set<BlockPos> positions = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                BlockPos local = BloodyBloodExtractorShape.slot(i);
                check(shape[local.getZ() + 3][local.getY()].charAt(local.getX() + 3) == 'D', "Slot matches geometry");
                BlockPos world = RelativeDirection.setActualRelativeOffset(local.getX(), local.getY() - 3,
                        local.getZ() + 2, front, EnumFacing.NORTH, false,
                        new RelativeDirection[]{RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK});
                positions.add(world);
                check(world.getY() == local.getY() - 3, "Rotation keeps vertical levels");
            }
            check(positions.size() == 10, "Ten distinct positions in " + front);
        }
    }

    private static DragonBloodExtractionLogic.Source source(int slot, DragonLifeStage stage) {
        return new DragonBloodExtractionLogic.Source(slot, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                stage, DragonBloodExtractionLogic.yieldFor(stage));
    }
    private static List<DragonBloodExtractionLogic.Source> adults(int count) {
        List<DragonBloodExtractionLogic.Source> sources = new ArrayList<>();
        for (int i = 0; i < count; i++) sources.add(source(i, DragonLifeStage.ADULT));
        return sources;
    }
    private static void tick(DragonBloodExtractionLogic logic, List<DragonBloodExtractionLogic.Source> sources, Energy energy, int ticks) {
        for (int i = 0; i < ticks; i++) logic.tick(sources, energy, UHV, true, true);
    }
    private static void yieldsAndPayment() {
        int[] outputs = {0, 25, 50, 100, 250, 500};
        for (DragonLifeStage stage : DragonLifeStage.values()) {
            check(DragonBloodExtractionLogic.yieldFor(stage) == outputs[stage.ordinal()], "Yield " + stage);
            if (stage == DragonLifeStage.EGG) continue;
            for (int count : new int[]{1, 5, 10}) {
                DragonBloodExtractionLogic logic = new DragonBloodExtractionLogic();
                Energy energy = new Energy(UHV);
                List<DragonBloodExtractionLogic.Source> sources = new ArrayList<>();
                for (int i = 0; i < count; i++) sources.add(source(i, stage));
                tick(logic, sources, energy, 99);
                check(logic.getBuffered() == 0 && logic.getProgress() == 99, "No early payment " + stage + '/' + count);
                tick(logic, sources, energy, 1);
                check(logic.getBuffered() == outputs[stage.ordinal()] * count && logic.getProgress() == 0, "Batch yield " + stage);
                check(energy.spent == (524288L + 131072L * count) * 100, "Exact EU accounting");
            }
        }
        DragonBloodExtractionLogic mixed = new DragonBloodExtractionLogic();
        List<DragonBloodExtractionLogic.Source> sources = Arrays.asList(source(0, DragonLifeStage.ADULT),
                source(1, DragonLifeStage.JUVENILE), source(2, DragonLifeStage.JUVENILE), source(3, DragonLifeStage.FLEDGLING),
                source(4, DragonLifeStage.FLEDGLING), source(5, DragonLifeStage.FLEDGLING));
        Energy energy = new Energy(UHV);
        tick(mixed, sources, energy, 100);
        check(mixed.getBuffered() == 1300 && energy.spent == 131072000L, "Mixed stages sum");
        check(DragonBloodExtractionLogic.powerFor(10) == 1835008L, "Full load fits UHV");
    }
    private static void pausesAndChanges() {
        DragonBloodExtractionLogic logic = new DragonBloodExtractionLogic();
        List<DragonBloodExtractionLogic.Source> one = adults(1);
        Energy energy = new Energy(UHV);
        check(logic.tick(one, energy, GTValues.V[GTValues.UV], true, true) == DragonBloodExtractionLogic.Status.LOW_VOLTAGE,
                "UV cannot operate");
        EnergyContainerList manyLowVoltage = new EnergyContainerList(Arrays.asList(new Energy(GTValues.V[GTValues.UV]), new Energy(GTValues.V[GTValues.UV])));
        logic.tick(one, manyLowVoltage, GTValues.V[GTValues.UV], true, true);
        check(logic.getProgress() == 0, "Multiple UV hatches cannot bypass single voltage gate");
        tick(logic, one, energy, 45);
        long paid = energy.spent;
        logic.tick(one, energy, UHV, false, true);
        check(logic.getProgress() == 45 && energy.spent == paid, "Manual pause freezes paid progress");
        logic.tick(one, energy, UHV, true, false);
        check(logic.getProgress() == 45 && energy.spent == paid, "Maintenance pauses without power draw");
        energy.stored = 0;
        logic.tick(one, energy, UHV, true, true);
        check(logic.getProgress() == 45 && energy.spent == paid, "Power shortage freezes progress");
        energy.stored = Long.MAX_VALUE / 4;
        tick(logic, one, energy, 54);
        List<DragonBloodExtractionLogic.Source> changed = adults(1);
        check(logic.tick(changed, energy, UHV, true, true) == DragonBloodExtractionLogic.Status.CHANGED, "Swap at tick 100 cancels");
        check(logic.getProgress() == 0 && logic.getBuffered() == 0, "Swap creates no partial product");
        tick(logic, changed, energy, 99);
        logic.tick(Collections.emptyList(), energy, UHV, true, true);
        check(logic.getBuffered() == 0 && logic.getProgress() == 0, "Removal at last tick cancels");
        tick(logic, one, energy, 99);
        List<DragonBloodExtractionLogic.Source> added = new ArrayList<>(one);
        added.add(source(1, DragonLifeStage.ADULT));
        logic.tick(added, energy, UHV, true, true);
        check(logic.getProgress() == 0 && logic.getBuffered() == 0, "Adding a participant restarts batch");

        DragonBloodExtractionLogic duplicate = new DragonBloodExtractionLogic();
        DragonBloodExtractionLogic.Source first = one.get(0);
        duplicate.tick(Arrays.asList(first, new DragonBloodExtractionLogic.Source(1, first.dragon, UUID.randomUUID(),
                UUID.randomUUID(), DragonLifeStage.ADULT, 500)), energy, UHV, true, true);
        check(duplicate.getProgress() == 0, "Logic also rejects duplicate UUIDs defensively");

        Energy partial = new Energy(UHV);
        partial.partial = true;
        DragonBloodExtractionLogic refund = new DragonBloodExtractionLogic();
        long before = partial.stored;
        refund.tick(one, partial, UHV, true, true);
        check(refund.getProgress() == 0 && partial.stored == before, "Partial debit refunded without work");
    }
    private static void persistenceAndOutput() {
        List<DragonBloodExtractionLogic.Source> sources = adults(10);
        Energy energy = new Energy(UHV);
        DragonBloodExtractionLogic logic = new DragonBloodExtractionLogic();
        tick(logic, sources, energy, 63);
        NBTTagCompound saved = logic.write();
        DragonBloodExtractionLogic loaded = new DragonBloodExtractionLogic();
        loaded.read(saved);
        check(loaded.getProgress() == 63 && loaded.getBatchPower() == 1835008, "Paid batch reloads");
        tick(loaded, sources, energy, 37);
        check(loaded.getBuffered() == 5000 && energy.spent == 183500800L, "Reload neither skips nor repeats paid work");
        tick(loaded, sources, energy, 200);
        check(loaded.getBuffered() == 15000, "Three full batches buffered");
        long paid = energy.spent;
        tick(loaded, sources, energy, 250);
        check(loaded.getBuffered() == 15000 && loaded.getProgress() == 0 && energy.spent == paid, "Full buffer consumes no EU");
        FluidTank partial = new FluidTank(1000);
        loaded.export(partial, amount -> new FluidStack(BLOOD, amount));
        check(loaded.getBuffered() == 14000 && partial.getFluidAmount() == 1000, "Partial acceptance subtracts actual amount only");
        FluidTank all = new FluidTank(32000);
        loaded.export(all, amount -> new FluidStack(BLOOD, amount));
        check(loaded.getBuffered() == 0 && all.getFluidAmount() == 14000, "Remaining buffer conserved");
        loaded.export(all, amount -> new FluidStack(BLOOD, amount));
        check(all.getFluidAmount() == 14000, "No repeat export");
        tick(loaded, sources, energy, 150);
        NBTTagCompound item = loaded.writeItem();
        DragonBloodExtractionLogic placed = new DragonBloodExtractionLogic();
        placed.read(item);
        check(placed.getBuffered() == 5000 && placed.getProgress() == 0, "Tool removal preserves finished blood only");
        placed.read(loaded.write());
        placed.tick(adults(10), energy, UHV, true, true);
        check(placed.getProgress() == 0 && placed.getBuffered() == 5000, "Changed loaded participants cancel paid batch but keep buffer");
        NBTTagCompound invalid = saved.copy();
        invalid.setInteger("Progress", 100);
        placed.read(invalid);
        check(placed.getProgress() == 0, "Completed forged progress cannot mint a second batch");
        invalid = saved.copy();
        invalid.getTagList("Sources", 10).getCompoundTagAt(0).setString("Stage", "EGG");
        placed.read(invalid);
        check(placed.getProgress() == 0, "Egg payload rejected during batch load");
    }
    private static void containers() {
        NBTTagCompound payload = payload(DragonLifeStage.JUVENILE);
        TileEntityBloodyStarlightContainer tile = new TileEntityBloodyStarlightContainer();
        tile.setPayload(payload);
        UUID stamp = tile.getContentId();
        DragonContainmentData.ExtractionSnapshot snapshot = tile.getExtractionSnapshot();
        check(snapshot != null && snapshot.stage == DragonLifeStage.JUVENILE, "Server snapshot reads actual stage");
        payload.getCompoundTag("Display").setString("Stage", "ADULT");
        tile.setPayload(payload);
        check(tile.getExtractionSnapshot().stage == DragonLifeStage.JUVENILE, "Forged Display stage cannot increase production");
        NBTTagCompound frozen = tile.getPayload();
        for (int i = 0; i < 100; i++) check(tile.getExtractionSnapshot().stage == DragonLifeStage.JUVENILE, "Cached read valid");
        check(tile.getPayload().equals(frozen), "Production reads do not modify any dragon data");
        TileEntityBloodyStarlightContainer reloaded = new TileEntityBloodyStarlightContainer();
        reloaded.readFromNBT(tile.writeToNBT(new NBTTagCompound()));
        check(reloaded.getContentId().equals(tile.getContentId()), "Content identity stable across chunk reload");
        check(reloaded.getPayload().equals(frozen), "Full stored NBT survives reload");
        tile.setPayload(frozen);
        check(!tile.getContentId().equals(reloaded.getContentId()) && !tile.getContentId().equals(stamp), "Replacing same payload changes content identity");
        Owner first = new Owner(), second = new Owner();
        check(tile.tryClaim(first), "First owner claims");
        check(!tile.tryClaim(second), "Shared tile cannot have two active owners");
        first.valid = false;
        check(tile.tryClaim(second), "Stale owner does not leave permanent lock");
        tile.releaseClaim(first);
        first.valid = true;
        check(!tile.tryClaim(first), "Unrelated release cannot clear another owner");
        tile.releaseClaim(second);
        check(tile.tryClaim(first), "Owner release makes tile available");
        tile.takePayload();
        check(tile.getExtractionSnapshot() == null && !tile.isOccupied(), "Releasing dragon invalidates cached extraction data");
        tile.setPayload(payload(DragonLifeStage.EGG));
        check(tile.getExtractionSnapshot() == null, "Egg cannot be an extraction source");
    }
    private static void acceleration() {
        for (int count : new int[]{1, 10}) for (int aura : new int[]{1, 2}) for (int astral : new int[]{1, 2}) {
            List<DragonBloodExtractionLogic.Source> sources = adults(count);
            DragonBloodExtractionLogic logic = new DragonBloodExtractionLogic();
            Energy power = new Energy(UHV);
            Aevitas ce = new Aevitas(Long.MAX_VALUE / 4);
            int duration = 100 / (aura * astral);
            for (int i = 0; i < duration - 1; i++)
                logic.tick(sources, power, UHV, true, true, aura, astral, ce::pay);
            check(logic.getBuffered() == 0, "Boost cannot settle a batch early");
            logic.tick(sources, power, UHV, true, true, aura, astral, ce::pay);
            check(logic.getBuffered() == count * 500 && logic.getProgress() == 0, "Multiplicative speed " + aura + 'x' + astral);
            check(power.spent == DragonBloodExtractionLogic.powerFor(count) * duration, "EU paid once per accelerated world tick");
            check(ce.spent == (long) duration * aura * (astral - 1) * count * 128, "CE paid only for extra progress");
            check(logic.getLastAdvance() == aura * astral, "Completion keeps the actual speed metric");
        }
        List<DragonBloodExtractionLogic.Source> sources = adults(10);
        DragonBloodExtractionLogic logic = new DragonBloodExtractionLogic();
        Energy power = new Energy(UHV);
        Aevitas ce = new Aevitas(1280);
        logic.tick(sources, power, UHV, true, true, 2, 2, ce::pay);
        check(logic.getProgress() == 3 && ce.spent == 1280, "Partial CE supply buys only one complete extra step");
        logic.tick(sources, power, UHV, true, true, 2, 2, ce::pay);
        check(logic.getProgress() == 5 && ce.spent == 1280, "CE shortage falls back to heart speed");
        logic.tick(sources, power, UHV, true, true, 1, 2, ce::pay);
        check(logic.getProgress() == 6, "Losing both boosts resumes base speed without resetting batch");
        NBTTagCompound saved = logic.write();
        DragonBloodExtractionLogic loaded = new DragonBloodExtractionLogic();
        loaded.read(saved);
        check(loaded.getProgress() == 6 && loaded.getLastAdvance() == 0, "Reload keeps earned progress but not old boosts");
        tick(loaded, sources, power, 94);
        check(loaded.getBuffered() == 5000, "Unboosted reload finishes remaining work only");

        for (int progress : new int[]{97, 99}) {
            NBTTagCompound tail = saved.copy();
            tail.setInteger("Progress", progress);
            loaded.read(tail);
            Aevitas enough = new Aevitas(100000);
            loaded.tick(sources, power, UHV, true, true, 2, 2, enough::pay);
            check(loaded.getBuffered() == 5000 && loaded.getProgress() == 0, "No overflow into another batch");
            check(enough.spent == (progress == 97 ? 1280 : 0), "No CE paid for progress beyond batch boundary");
        }

        for (int reason = 0; reason < 5; reason++) {
            DragonBloodExtractionLogic paused = new DragonBloodExtractionLogic();
            Energy energy = new Energy(UHV);
            if (reason == 3) energy.stored = 0;
            if (reason == 4) {
                NBTTagCompound full = new NBTTagCompound();
                full.setInteger("Buffered", 16000);
                paused.read(full);
            }
            Aevitas enough = new Aevitas(100000);
            paused.tick(sources, energy, reason == 2 ? GTValues.V[GTValues.UV] : UHV,
                    reason != 0, reason != 1, 2, 2, enough::pay);
            check(paused.getProgress() == 0 && enough.spent == 0 && energy.spent == 0, "No CE/EU on blocked work " + reason);
        }
        DragonBloodExtractionLogic changed = new DragonBloodExtractionLogic();
        Aevitas enough = new Aevitas(1000000);
        for (int i = 0; i < 24; i++) changed.tick(sources, power, UHV, true, true, 2, 2, enough::pay);
        long beforeCE = enough.spent, beforeEU = power.spent;
        changed.tick(adults(10), power, UHV, true, true, 2, 2, enough::pay);
        check(changed.getProgress() == 0 && changed.getBuffered() == 0 && enough.spent == beforeCE && power.spent == beforeEU,
                "Changing containers cancels accelerated batch before paying EU or CE");

        MemoryWorld world = new MemoryWorld();
        BlockPos target = new BlockPos(0, 64, 0);
        meowmel.pollution.common.block.tile.TileEntityFleshHeart heart = world.heart(target.add(4, 0, 0), 9);
        check(DragonGrowthAura.multiplier(world, target) == 1, "Only max-level heart accelerates extraction");
        heart.setLevel(10);
        check(DragonGrowthAura.multiplier(world, target) == 2, "Max-level heart supplies existing 2x aura");
        world.heart(target.add(3, 0, 0), 10);
        world.time += 20;
        check(DragonGrowthAura.multiplier(world, target) == 2, "Multiple flesh hearts do not multiply");
        check(DragonGrowthAura.multiplier(world, target.add(100, 0, 0)) == 1, "Distant heart cannot boost extraction");
        world.unloaded.add(heart.getPos());
        world.unloaded.add(target.add(3, 0, 0));
        check(DragonGrowthAura.multiplier(world, target) == 1, "Unloaded cores do not force chunks or apply aura");
    }

    private static final class Aevitas {
        long remaining, spent;
        Aevitas(long remaining) { this.remaining = remaining; }
        boolean pay(long amount) {
            if (amount <= 0 || remaining < amount) return false;
            remaining -= amount;
            spent += amount;
            return true;
        }
    }
    private static NBTTagCompound payload(DragonLifeStage stage) {
        NBTTagCompound entity = new NBTTagCompound();
        entity.setString("id", DragonContainmentData.TYPE);
        entity.setUniqueId("UUID", UUID.randomUUID());
        entity.setInteger("DragonDataVersion", 1);
        entity.setString("DragonStageName", stage.name());
        entity.setInteger("DragonStage", stage.ordinal());
        entity.setInteger("DragonStageDuration", 24000);
        entity.setInteger("DragonStageTicks", 321);
        entity.setFloat("Health", 13.5F);
        entity.setFloat("DragonBaseScale", 0.8F);
        entity.setInteger("DragonAppearance", 2);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Version", 1);
        tag.setUniqueId("CaptureId", UUID.randomUUID());
        tag.setTag("Dragon", entity);
        tag.setTag("Display", new NBTTagCompound());
        return tag;
    }
    /** Uses the actual controller's ten-position scan and claim code, with real GT holders. */
    private static void controllerConnections() throws Exception {
        // The isolated harness has no FML discovery phase; describe an empty optional-mod set.
        java.lang.reflect.Field mods = net.minecraftforge.fml.common.Loader.class.getDeclaredField("namedMods");
        mods.setAccessible(true);
        mods.set(net.minecraftforge.fml.common.Loader.instance(), Collections.emptyMap());
        java.lang.reflect.Field side = net.minecraftforge.fml.common.FMLCommonHandler.class.getDeclaredField("sidedDelegate");
        side.setAccessible(true);
        side.set(net.minecraftforge.fml.common.FMLCommonHandler.instance(), java.lang.reflect.Proxy.newProxyInstance(
                DragonBloodExtractorSmoke.class.getClassLoader(), new Class<?>[]{net.minecraftforge.fml.common.IFMLSidedHandler.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getSide")) return net.minecraftforge.fml.relauncher.Side.SERVER;
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                }));
        gregtech.api.GregTechAPI.markerMaterialRegistry = gregtech.api.unification.material.registry.MarkerMaterialRegistry.getInstance();
        gregtech.api.GregTechAPI.materialManager = gregtech.core.unification.material.internal.MaterialRegistryManager.getInstance();
        gregtech.api.GregTechAPI.materialManager.createRegistry("pollution");
        gregtech.core.unification.material.internal.MaterialRegistryManager.getInstance().unfreezeRegistries();
        gregtech.api.unification.material.Materials.register();
        meowmel.pollution.api.unification.materials.MagicIntegrationMaterials.register();
        check(meowmel.pollution.api.unification.PollutionMaterials.DragonBlood.hasProperty(
                gregtech.api.unification.material.properties.PropertyKey.FLUID), "Dragon blood is a CEu fluid material");
        meowmel.pollution.api.unification.PollutionMaterials.DragonBlood.getProperty(
                gregtech.api.unification.material.properties.PropertyKey.FLUID)
                .registerFluids(meowmel.pollution.api.unification.PollutionMaterials.DragonBlood);
        check(meowmel.pollution.api.unification.PollutionMaterials.DragonBlood.getFluid(100).getFluid().getTemperature() == 310,
                "CEu dragon blood fluid registered at 310 K");
        gregtech.api.GregTechAPI.mteManager = gregtech.api.metatileentity.registry.MTEManager.getInstance();
        MemoryWorld world = new MemoryWorld();
        Machine first = new Machine();
        Holder holder = new Holder();
        holder.setWorld(world);
        holder.setPos(new BlockPos(0, 64, 0));
        holder.attach(first);
        world.tiles.put(holder.getPos(), holder);
        java.lang.reflect.Method scan = MetaTileEntityBloodyBloodExtractor.class.getDeclaredMethod("sources");
        scan.setAccessible(true);
        for (int i = 0; i < 10; i++) {
            BlockPos local = BloodyBloodExtractorShape.slot(i);
            BlockPos pos = first.shapePosition(local.getX(), local.getY(), local.getZ());
            TileEntityBloodyStarlightContainer tile = new TileEntityBloodyStarlightContainer();
            tile.setWorld(world);
            tile.setPos(pos);
            tile.setPayload(payload(i == 0 ? DragonLifeStage.JUVENILE : DragonLifeStage.ADULT));
            world.tiles.put(pos, tile);
        }
        List<?> sources = (List<?>) scan.invoke(first);
        check(sources.size() == 10, "Controller reads exactly ten physical slots");
        check(((DragonBloodExtractionLogic.Source) sources.get(0)).stage == DragonLifeStage.JUVENILE,
                "Controller uses authoritative growth stage");
        Energy power = new Energy(UHV);
        FluidTank output = new FluidTank(2000);
        java.lang.reflect.Field energyField = MetaTileEntityBloodyBloodExtractor.class.getDeclaredField("energy");
        java.lang.reflect.Field voltageField = MetaTileEntityBloodyBloodExtractor.class.getDeclaredField("highestVoltage");
        java.lang.reflect.Field outputField = MetaTileEntityBloodyBloodExtractor.class.getDeclaredField("output");
        java.lang.reflect.Field logicField = MetaTileEntityBloodyBloodExtractor.class.getDeclaredField("extraction");
        for (java.lang.reflect.Field field : Arrays.asList(energyField, voltageField, outputField, logicField)) field.setAccessible(true);
        energyField.set(first, power);
        voltageField.setLong(first, UHV);
        outputField.set(first, output);
        first.setMaintenance(gregtech.api.metatileentity.multiblock.IMaintenance.NO_PROBLEMS);
        java.lang.reflect.Method work = MetaTileEntityBloodyBloodExtractor.class.getDeclaredMethod("updateFormedValid");
        work.setAccessible(true);
        for (int i = 0; i < 100; i++) work.invoke(first);
        DragonBloodExtractionLogic actual = (DragonBloodExtractionLogic) logicField.get(first);
        check(power.spent == 183500800L && output.getFluidAmount() == 2000 && actual.getBuffered() == 2750,
                "Production controller pays UHV and conserves 4750 mB across output and internal buffer");
        NBTTagCompound dismantled = new NBTTagCompound();
        first.writeItemStackData(dismantled);
        Machine replaced = new Machine();
        replaced.initFromItemStackData(dismantled);
        check(((DragonBloodExtractionLogic) logicField.get(replaced)).getBuffered() == 2750,
                "Production controller tool removal carries completed fluid");
        BlockPos firstLocal = BloodyBloodExtractorShape.slot(0);
        BlockPos firstPos = first.shapePosition(firstLocal.getX(), firstLocal.getY(), firstLocal.getZ());
        TileEntityBloodyStarlightContainer firstTile = (TileEntityBloodyStarlightContainer) world.tiles.get(firstPos);
        check(!firstTile.tryClaim(new Owner()), "Active real controller retains its slot");
        BlockPos secondLocal = BloodyBloodExtractorShape.slot(1);
        BlockPos secondPos = first.shapePosition(secondLocal.getX(), secondLocal.getY(), secondLocal.getZ());
        TileEntityBloodyStarlightContainer secondTile = (TileEntityBloodyStarlightContainer) world.tiles.get(secondPos);
        secondTile.setPayload(firstTile.getPayload());
        check(((List<?>) scan.invoke(first)).size() == 9, "Actual scan excludes duplicate dragon UUID");
        firstTile.takePayload();
        check(((List<?>) scan.invoke(first)).size() == 9, "Next physical duplicate becomes first valid representative");
        first.onUnload();
        check(secondTile.tryClaim(new Owner()), "Controller unload releases real tile claims");
        world.tiles.remove(secondPos);
        check(((List<?>) scan.invoke(first)).size() == 8, "Physical removal updates source count");
        acceleratedController(work, energyField, voltageField, outputField, logicField);
    }

    private static void acceleratedController(java.lang.reflect.Method work, java.lang.reflect.Field energyField,
                                              java.lang.reflect.Field voltageField, java.lang.reflect.Field outputField,
                                              java.lang.reflect.Field logicField) throws Exception {
        MemoryWorld world = new MemoryWorld();
        Machine machine = new Machine();
        Holder holder = new Holder();
        holder.setWorld(world);
        holder.setPos(new BlockPos(0, 64, 0));
        holder.attach(machine);
        world.tiles.put(holder.getPos(), holder);
        NBTTagCompound frozen = null;
        TileEntityBloodyStarlightContainer witness = null;
        for (int i = 0; i < 10; i++) {
            BlockPos local = BloodyBloodExtractorShape.slot(i);
            BlockPos pos = machine.shapePosition(local.getX(), local.getY(), local.getZ());
            TileEntityBloodyStarlightContainer tile = new TileEntityBloodyStarlightContainer();
            tile.setWorld(world);
            tile.setPos(pos);
            tile.setPayload(payload(DragonLifeStage.ADULT));
            world.tiles.put(pos, tile);
            witness = tile;
            frozen = tile.getPayload();
        }
        Energy energy = new Energy(UHV);
        FluidTank output = new FluidTank(32000);
        energyField.set(machine, energy);
        voltageField.setLong(machine, UHV);
        outputField.set(machine, output);
        machine.setMaintenance(gregtech.api.metatileentity.multiblock.IMaintenance.NO_PROBLEMS);
        meowmel.pollution.common.block.tile.TileEntityFleshHeart heart = world.heart(new BlockPos(2, 64, 0), 10);
        Provider provider = new Provider();
        provider.setWorld(world);
        provider.setPos(new BlockPos(0, 64, 3));
        world.tiles.put(provider.getPos(), provider);
        world.loadedTileEntityList.add(provider);
        machine.getStarstreamWirelessBinding().bind(provider.network);
        for (int i = 0; i < 25; i++) { world.time++; work.invoke(machine); }
        DragonBloodExtractionLogic logic = (DragonBloodExtractionLogic) logicField.get(machine);
        check(output.getFluidAmount() == 5000 && energy.spent == 45875200L && provider.energy.spent == 64000,
                "Real controller stacks heart aura with bound Aevitas to 4x, with exact EU/CE output accounting");
        check(logic.getLastAdvance() == 4 && logic.getLastAevitas() == 2560, "GUI receives actual paid acceleration metrics");
        check(witness.getPayload().equals(frozen), "Boosted extraction leaves the contained dragon frozen and unchanged");
        provider.energy.remaining = 0;
        for (int i = 0; i < 25; i++) { world.time++; work.invoke(machine); }
        check(logic.getProgress() == 50 && output.getFluidAmount() == 5000 && provider.energy.spent == 64000,
                "Empty network degrades to flesh heart speed without losing progress");
        heart.setLevel(9);
        world.time++;
        work.invoke(machine);
        check(logic.getProgress() == 51 && logic.getLastAdvance() == 1, "Losing max-level heart immediately restores normal speed");
        NBTTagCompound disk = machine.writeToNBT(new NBTTagCompound());
        Machine loaded = new Machine();
        loaded.readFromNBT(disk);
        check(loaded.getStarstreamWirelessBinding().getNetworkId().equals(provider.network)
                        && ((DragonBloodExtractionLogic) logicField.get(loaded)).getProgress() == 51,
                "Controller disk save preserves network binding and earned extraction progress");
        NBTTagCompound item = new NBTTagCompound();
        machine.writeItemStackData(item);
        loaded.initFromItemStackData(item);
        check(loaded.getStarstreamWirelessBinding().getNetworkId().equals(provider.network)
                        && ((DragonBloodExtractionLogic) logicField.get(loaded)).getProgress() == 0,
                "Tool transfer preserves selected network without carrying unfinished paid work");
        long before = energy.spent;
        machine.setWorkingEnabled(false);
        provider.energy.remaining = 100000;
        heart.setLevel(10);
        world.time++;
        work.invoke(machine);
        check(logic.getProgress() == 51 && energy.spent == before && provider.energy.spent == 64000,
                "Paused actual controller spends neither EU nor constellation energy");
    }

    private static final class Provider extends net.minecraft.tileentity.TileEntity
            implements meowmel.pollution.api.capability.IStarstreamWirelessProvider {
        final UUID network = UUID.randomUUID();
        final Aevitas energy = new Aevitas(64000);
        public UUID getWirelessNetworkId() { return network; }
        public int getWirelessRange() { return 96; }
        public boolean isWirelessNetworkOnline() { return true; }
        public long requestWirelessEnergy(BlockPos pos, UUID network, UUID terminal, String channel, long amount, boolean simulate) { return 0; }
        public boolean consumeWirelessEnergy(BlockPos pos, UUID requested, UUID terminal, Map<String, Long> cost, boolean simulate) {
            check(cost.size() == 1 && cost.containsKey("aevitas"), "Extractor requests only Aevitas");
            return network.equals(requested) && (simulate ? energy.remaining >= cost.get("aevitas") : energy.pay(cost.get("aevitas")));
        }
    }

    private static final class Machine extends MetaTileEntityBloodyBloodExtractor {
        Machine() { super(new ResourceLocation("pollution", "extractor_connection_smoke")); }
        @Override public boolean isStructureFormed() { return true; }
        // Isolate NBT reload from full GT block/item registration; geometry is checked separately above.
        @Override protected gregtech.api.pattern.element.StructureDefinition<?> createStructureDefinition() {
            return gregtech.api.pattern.element.StructureDefinition.getOrBuild("pollution:extractor_smoke_nbt", () ->
                    gregtech.api.pattern.casing.DeclarativePatternBuilder.start().aisle("S")
                            .self('S', Machine.class).buildStructureDefinition());
        }
    }
    private static final class Holder extends gregtech.api.metatileentity.MetaTileEntityHolder {
        void attach(gregtech.api.metatileentity.MetaTileEntity machine) { setRawMetaTileEntity(machine); }
    }
    private static final class MemoryWorld extends net.minecraft.world.World {
        final Map<BlockPos, net.minecraft.tileentity.TileEntity> tiles = new HashMap<>();
        final Set<BlockPos> unloaded = new HashSet<>();
        long time;
        MemoryWorld() {
            super(new net.minecraft.world.storage.SaveHandlerMP(), new net.minecraft.world.storage.WorldInfo(new NBTTagCompound()),
                    new net.minecraft.world.WorldProviderSurface(), new net.minecraft.profiler.Profiler(), false);
        }
        @Override protected net.minecraft.world.chunk.IChunkProvider createChunkProvider() { return null; }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
        @Override public boolean isBlockLoaded(BlockPos pos) { return !unloaded.contains(pos); }
        @Override public long getTotalWorldTime() { return time; }
        meowmel.pollution.common.block.tile.TileEntityFleshHeart heart(BlockPos pos, int level) {
            meowmel.pollution.common.block.tile.TileEntityFleshHeart heart = new meowmel.pollution.common.block.tile.TileEntityFleshHeart();
            heart.setPos(pos);
            heart.setWorld(this);
            heart.setLevel(level);
            tiles.put(pos, heart);
            loadedTileEntityList.add(heart);
            return heart;
        }
        @Override public net.minecraft.tileentity.TileEntity getTileEntity(BlockPos pos) { return tiles.get(pos); }
        @Override public net.minecraft.block.state.IBlockState getBlockState(BlockPos pos) { return net.minecraft.init.Blocks.AIR.getDefaultState(); }
        @Override public void markChunkDirty(BlockPos pos, net.minecraft.tileentity.TileEntity tile) { }
        @Override public boolean checkLight(BlockPos pos) { return true; }
        @Override public void playSound(net.minecraft.entity.player.EntityPlayer player, double x, double y, double z,
                                        net.minecraft.util.SoundEvent sound, net.minecraft.util.SoundCategory category, float volume, float pitch) { }
    }
    private static void check(boolean condition, String name) {
        checks++;
        if (!condition) throw new AssertionError(name);
    }
    private static final class Owner implements TileEntityBloodyStarlightContainer.ExtractionOwner {
        boolean valid = true;
        public boolean ownsContainer(BlockPos pos) { return valid; }
    }
    private static final class Energy implements IEnergyContainer {
        long stored = Long.MAX_VALUE / 4, spent;
        final long voltage;
        boolean partial;
        Energy(long voltage) { this.voltage = voltage; }
        public long changeEnergy(long amount) {
            long delta = amount < 0 ? -Math.min(stored, partial ? -amount / 2 : -amount) : amount;
            stored += delta;
            spent -= delta;
            return delta;
        }
        public long getEnergyStored() { return stored; }
        public long getEnergyCapacity() { return Long.MAX_VALUE; }
        public long getInputVoltage() { return voltage; }
        public long getInputAmperage() { return 1; }
        public long acceptEnergyFromNetwork(EnumFacing side, long v, long a) { return 0; }
        public boolean inputsEnergy(EnumFacing side) { return true; }
    }
}
