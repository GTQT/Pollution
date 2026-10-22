import com.google.common.base.Predicate;
import gregtech.api.util.RelativeDirection;
import meowmel.pollution.POConfig;
import meowmel.pollution.api.capability.IStarstreamOperationCore;
import meowmel.pollution.api.capability.StarstreamWirelessBinding;
import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import meowmel.pollution.common.metatileentity.multiblock.bot.DragonEggAwakening;
import meowmel.pollution.common.metatileentity.multiblock.bot.DragonIncubatorShape;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DragonIncubatorSmoke {
    private static final BlockPos NEST = new BlockPos(5, 68, 5);
    private static final BlockPos CONTROLLER = new BlockPos(5, 64, 9);
    private static final BlockPos RECEIVER = new BlockPos(5, 64, 1);

    public static void main(String[] args) {
        try {
            Bootstrap.register();
            POConfig.DragonSettings.enableHatching = true;
            POConfig.DragonSettings.incubatorAwakeningTicks = 3;
            POConfig.DragonSettings.incubatorAevitasPerTick = 128;
            POConfig.DragonSettings.incubatorHorologiumPerTick = 64;
            POConfig.DragonSettings.incubatorAcceleration = 4;
            shape();
            chargeAndReload();
            rollback();
            blockedAndDisabled();
            accelerationAndManagedEgg();
            System.out.println("PASS: flower geometry, orientation, constellation accounting, save/reload, conversion, rollback, blocked/disabled nests");
            System.exit(0);
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        }
    }

    private static void shape() {
        String[][] aisles = DragonIncubatorShape.createAisles();
        check(aisles.length == 11, "11 aisles");
        Map<Character, Integer> counts = new HashMap<>();
        for (String[] aisle : aisles) {
            check(aisle.length == 7, "7 rows");
            for (String row : aisle) {
                check(row.length() == 11, "11 columns");
                for (char c : row.toCharArray()) {
                    check(" SHCWLPKE#".indexOf(c) >= 0, "Mapped character " + c);
                    counts.put(c, counts.getOrDefault(c, 0) + 1);
                }
            }
        }
        check(counts.get('S') == 1 && counts.get('H') == 1 && counts.get('E') == 1, "Unique controller/hatch/nest");
        check(aisles[DragonIncubatorShape.CONTROLLER_Z][DragonIncubatorShape.CONTROLLER_Y]
                .charAt(DragonIncubatorShape.CONTROLLER_X) == 'S', "Controller marker matches offsets");
        check(aisles[DragonIncubatorShape.EGG_Z][DragonIncubatorShape.EGG_Y]
                .charAt(DragonIncubatorShape.EGG_X) == 'E', "Egg marker matches offsets");
        check(aisles[5][3].charAt(5) == 'K', "Solid core supports the egg");
        for (int z = 4; z <= 6; z++) for (int y = 4; y <= 6; y++) for (int x = 4; x <= 6; x++) {
            check(aisles[z][y].charAt(x) == (x == 5 && z == 5 && y == 4 ? 'E' : '#'), "Clear 3x3x3 nest");
        }
        check(counts.get('P') == 32, "Eight petals, four tiers each");
        for (EnumFacing front : EnumFacing.HORIZONTALS) {
            BlockPos offset = RelativeDirection.setActualRelativeOffset(0, 4, -4, front.getOpposite(), EnumFacing.NORTH,
                    false, new RelativeDirection[] {RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK});
            check(offset.equals(BlockPos.ORIGIN.offset(front.getOpposite(), 4).up(4)), "Nest orientation " + front);
        }
        System.out.println("Structure material counts: " + counts);
    }

    private static DragonEggAwakening.Status tick(DragonEggAwakening logic, TestWorld world, Core core, boolean enabled) {
        return logic.tick(world, NEST, CONTROLLER, RECEIVER, core, enabled);
    }

    private static void chargeAndReload() {
        TestWorld world = new TestWorld(false);
        Core core = new Core(128, 0);
        DragonEggAwakening logic = new DragonEggAwakening();
        check(tick(logic, world, core, true) == DragonEggAwakening.Status.CHARGING, "Base speed without Horologium");
        check(logic.getProgress() == 1 && core.aevitas == 0, "One paid step");
        check(tick(logic, world, core, true) == DragonEggAwakening.Status.NO_ENERGY && logic.getProgress() == 1, "No Aevitas pauses");
        core.aevitas = 256;
        check(tick(logic, world, core, false) == DragonEggAwakening.Status.PAUSED && core.aevitas == 256, "Machine disabled");
        NBTTagCompound tag = logic.writeToNBT();
        logic = new DragonEggAwakening();
        logic.readFromNBT(tag);
        POConfig.DragonSettings.incubatorAwakeningTicks = 99;
        POConfig.DragonSettings.incubatorAevitasPerTick = 999;
        check(logic.getProgress() == 1 && logic.getDuration() == 3 && logic.getAevitasPerTick() == 128, "Saved paid costs");
        tick(logic, world, core, true);
        check(tick(logic, world, core, true) == DragonEggAwakening.Status.COMPLETE, "Awakening completes");
        check(core.aevitas == 0 && world.isAirBlock(NEST) && world.liveCount() == 1, "384 CE, one entity, no original block");
        EntityPollutionDragon egg = (EntityPollutionDragon) world.entities.get(0);
        check(egg.isBoundToIncubator(CONTROLLER) && egg.getTicksInStage() == 0, "Egg remains machine-managed");
        check(tick(logic, world, core, true) == DragonEggAwakening.Status.NO_ENERGY, "Incubation also needs power");
        POConfig.DragonSettings.incubatorAwakeningTicks = 3;
        POConfig.DragonSettings.incubatorAevitasPerTick = 128;
        NBTTagCompound legacy = new NBTTagCompound();
        legacy.setLong("Nest", NEST.toLong());
        legacy.setInteger("Progress", 999);
        logic.readFromNBT(legacy);
        check(logic.getProgress() == 0, "Legacy mana charge cannot be redeemed");
    }

    private static void rollback() {
        for (boolean rejectSpawn : new boolean[] {true, false}) {
            TestWorld world = new TestWorld(false);
            world.allowSpawn = !rejectSpawn;
            world.allowRemoval = rejectSpawn;
            Core core = new Core(384, 0);
            DragonEggAwakening logic = new DragonEggAwakening();
            tick(logic, world, core, true);
            tick(logic, world, core, true);
            check(tick(logic, world, core, true) == DragonEggAwakening.Status.SPAWN_FAILED, "Failure reported");
            check(world.getBlockState(NEST).getBlock() == Blocks.DRAGON_EGG && world.liveCount() == 0, "Original egg retained");
            world.allowSpawn = true;
            world.allowRemoval = true;
            check(tick(logic, world, core, true) == DragonEggAwakening.Status.COMPLETE && core.aevitas == 0, "Retry does not pay again");
        }
    }

    private static void blockedAndDisabled() {
        TestWorld world = new TestWorld(false);
        Core core = new Core(1024, 1024);
        DragonEggAwakening logic = new DragonEggAwakening();
        world.blocks.put(NEST.up(), Blocks.STONE.getDefaultState());
        check(tick(logic, world, core, true) == DragonEggAwakening.Status.BLOCKED && core.aevitas == 1024, "Blocked chamber");
        world.blocks.remove(NEST.up());
        POConfig.DragonSettings.enableHatching = false;
        check(tick(logic, world, core, true) == DragonEggAwakening.Status.DISABLED, "Global disable");
        POConfig.DragonSettings.enableHatching = true;
        world.provider.setDimension(1);
        check(tick(logic, world, core, true) == DragonEggAwakening.Status.DISABLED, "End restriction");
        world.provider.setDimension(0);
        tick(logic, world, new Core(128, 0), true);
        world.blocks.remove(NEST);
        check(tick(logic, world, core, true) == DragonEggAwakening.Status.WAITING && logic.getProgress() == 0, "Removed egg resets");
        TestWorld client = new TestWorld(true);
        check(tick(logic, client, core, true) == DragonEggAwakening.Status.PAUSED && client.entities.isEmpty(), "No client side spending");
    }

    private static void accelerationAndManagedEgg() {
        try {
            TestWorld world = new TestWorld(false);
            DragonEggAwakening logic = new DragonEggAwakening();
            Core core = new Core(384, 128);
            check(tick(logic, world, core, true) == DragonEggAwakening.Status.COMPLETE, "Clock accelerates awakening, capped at remaining work");
            check(core.aevitas == 0 && core.clock == 0, "Three Aevitas steps, only two extra clock steps");
            EntityPollutionDragon egg = (EntityPollutionDragon) world.entities.get(0);
            java.lang.reflect.Method naturalTick = EntityPollutionDragon.class.getDeclaredMethod("tickGrowth");
            naturalTick.setAccessible(true);
            naturalTick.invoke(egg);
            check(egg.getTicksInStage() == 0, "Managed egg does not also tick naturally");
            EntityPollutionDragon clientEgg = new EntityPollutionDragon(new TestWorld(true));
            clientEgg.getDataManager().setEntryValues(egg.getDataManager().getAll());
            naturalTick.invoke(clientEgg);
            check(clientEgg.getTicksInStage() == 0, "Client does not predict unpaid incubation");
            check(!egg.advanceIncubation(CONTROLLER.up()), "Other controller cannot advance egg");
            core = new Core(512, 192);
            check(tick(logic, world, core, true) == DragonEggAwakening.Status.ACCELERATING, "Paid 4x incubation");
            check(egg.getTicksInStage() == 4 && core.aevitas == 0 && core.clock == 0, "Exactly four steps paid");
            core = new Core(256, 0);
            check(tick(logic, world, core, true) == DragonEggAwakening.Status.INCUBATING, "Clock loss falls back to 1x");
            check(egg.getTicksInStage() == 5 && core.aevitas == 128, "Failed extra step consumes no Aevitas");
            core = new Core(0, 999);
            check(tick(logic, world, core, true) == DragonEggAwakening.Status.NO_ENERGY && core.clock == 999, "Clock alone cannot incubate");
            NBTTagCompound saved = new NBTTagCompound();
            egg.writeEntityToNBT(saved);
            EntityPollutionDragon restored = new EntityPollutionDragon(world);
            restored.readEntityFromNBT(saved);
            naturalTick.invoke(restored);
            check(restored.isBoundToIncubator(CONTROLLER) && restored.getTicksInStage() == 5, "Managed ownership/progress survive reload");
            // Force a hatch at the next paid step, with no random wobble/network dependency.
            saved.setInteger("DragonStageDuration", 6);
            saved.setInteger("DragonCrackLevel", 2);
            egg.readEntityFromNBT(saved);
            setRandom(egg, true);
            core = new Core(512, 192);
            check(tick(logic, world, core, true) == DragonEggAwakening.Status.COMPLETE, "Paid tick hatches egg");
            check(!egg.isEgg() && core.aevitas == 384 && core.clock == 192, "No extra debit after hatch");
            check(tick(logic, world, core, true) == DragonEggAwakening.Status.WAITING
                    && core.aevitas == 384, "Next machine tick never charges or accelerates a hatchling");
            naturalTick.invoke(egg);
            check(egg.getTicksInStage() == 1, "Juvenile growth resumes at natural speed");
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError(failure);
        }
    }

    private static void setRandom(EntityPollutionDragon egg, boolean hatch) throws ReflectiveOperationException {
        java.lang.reflect.Field random = EntityPollutionDragon.class.getDeclaredField("dragonRandom");
        random.setAccessible(true);
        random.set(egg, new java.util.Random() {
            @Override public int nextInt(int bound) { return hatch && bound == 1200 ? 0 : bound - 1; }
        });
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static final class Core implements IStarstreamOperationCore {
        long aevitas;
        long clock;
        final StarstreamWirelessBinding binding = new StarstreamWirelessBinding();
        Core(long aevitas, long clock) { this.aevitas = aevitas; this.clock = clock; }
        public StarstreamWirelessBinding getStarstreamWirelessBinding() { return binding; }
        public long requestConstellationEnergy(World world, BlockPos pos, String channel, long amount, boolean simulate) {
            throw new AssertionError("Use the atomic multichannel API");
        }
        public boolean consumeConstellationEnergy(World world, BlockPos pos, Map<String, Long> costs, boolean simulate) {
            check(pos.equals(RECEIVER), "Network range is measured from receiver");
            long life = costs.getOrDefault("aevitas", 0L), time = costs.getOrDefault("horologium", 0L);
            check(costs.size() == (time == 0 ? 1 : 2), "Only intended channels requested");
            if (aevitas < life || clock < time) return false;
            if (!simulate) { aevitas -= life; clock -= time; }
            return true;
        }
    }

    private static final class TestWorld extends World {
        final Map<BlockPos, IBlockState> blocks = new HashMap<>();
        final List<Entity> entities = new ArrayList<>();
        boolean allowSpawn = true;
        boolean allowRemoval = true;
        TestWorld(boolean client) {
            super(new SaveHandlerMP(), new WorldInfo(new NBTTagCompound()), new WorldProviderSurface(), new Profiler(), client);
            blocks.put(NEST, Blocks.DRAGON_EGG.getDefaultState());
        }
        int liveCount() { return (int) entities.stream().filter(e -> !e.isDead).count(); }
        @Override protected IChunkProvider createChunkProvider() { return null; }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
        @Override public boolean isAirBlock(BlockPos pos) { return getBlockState(pos).getBlock() == Blocks.AIR; }
        @Override public IBlockState getBlockState(BlockPos pos) { return blocks.getOrDefault(pos, Blocks.AIR.getDefaultState()); }
        @Override public boolean setBlockToAir(BlockPos pos) {
            if (!allowRemoval) return false;
            blocks.remove(pos);
            return true;
        }
        @Override public boolean spawnEntity(Entity entity) {
            if (!allowSpawn) return false;
            if (entity instanceof EntityPollutionDragon) {
                try { setRandom((EntityPollutionDragon) entity, false); }
                catch (ReflectiveOperationException failure) { throw new AssertionError(failure); }
            }
            entities.add(entity);
            return true;
        }
        @Override public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> type, AxisAlignedBB box, Predicate<? super T> predicate) {
            List<T> found = new ArrayList<>();
            for (Entity entity : entities) {
                if (type.isInstance(entity) && box.intersects(entity.getEntityBoundingBox())) {
                    T candidate = type.cast(entity);
                    if (predicate == null || predicate.apply(candidate)) found.add(candidate);
                }
            }
            return found;
        }
        @Override public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) { }
    }
}
