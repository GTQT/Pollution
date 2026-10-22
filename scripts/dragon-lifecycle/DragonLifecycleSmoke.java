import com.mojang.authlib.GameProfile;
import meowmel.pollution.POConfig;
import meowmel.pollution.common.entity.dragon.DragonEggInteractionHandler;
import meowmel.pollution.common.entity.dragon.DragonLifeStage;
import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Runs the real entity lifecycle against an in-memory world; no saves or game client needed. */
public final class DragonLifecycleSmoke {
    private static final Method TICK = method("tickGrowth");
    private static final Method RESIZE = method("applyStageSize");

    public static void main(String[] args) {
        try {
            Bootstrap.register();
            savedProgress();
            stageBoundaries();
            incubation();
            clientCheckpoints();
            centeredGrowth();
            stoppedLifecycle();
            returnToEgg();
            debugGrowth();
            System.out.println("PASS: persistence, stage boundaries, incubation, metadata, size, stopped lifecycle, return-to-egg");
            System.exit(0);
        } catch (Throwable failure) {
            failure.printStackTrace();
            // Minecraft bootstrap may leave background threads running after a failure.
            System.exit(1);
        }
    }

    private static void debugGrowth() throws Exception {
        TestDragon dragon = loaded(false, DragonLifeStage.EGG, 19, 24000);
        BlockPos controller = new BlockPos(0, 64, 0);
        dragon.bindIncubator(controller);
        check(dragon.debugAdvanceGrowth(false), "Debug egg hatches immediately");
        check(dragon.getLifeStage() == DragonLifeStage.HATCHLING && !dragon.isBoundToIncubator(controller),
                "Debug hatch clears machine ownership");
        check(dragon.getTicksInStage() == 0 && !dragon.isAIDisabled(), "Debug hatch resets ticks and enables AI");
        dragon.debugAdvanceGrowth(false);
        check(dragon.getLifeStage() == DragonLifeStage.INFANT, "Debug next stage");
        dragon.debugAdvanceGrowth(true);
        check(dragon.getLifeStage() == DragonLifeStage.ADULT, "Debug instant adult");
        check(!dragon.debugAdvanceGrowth(false), "Adult remains terminal");
        TestDragon client = loaded(true, DragonLifeStage.EGG, 0, 24000);
        check(!client.debugAdvanceGrowth(true) && client.isEgg(), "Client cannot debug-grow");
        TestDragon dead = loaded(false, DragonLifeStage.EGG, 0, 24000);
        dead.setDead();
        check(!dead.debugAdvanceGrowth(true), "Dead dragon cannot debug-grow");
    }

    private static void savedProgress() throws Exception {
        for (DragonLifeStage stage : DragonLifeStage.values()) {
            TestDragon original = loaded(false, stage, 12345, 23456);
            TestDragon restored = new TestDragon(new TestWorld(false));
            NBTTagCompound tag = new NBTTagCompound();
            original.writeEntityToNBT(tag);
            restored.readEntityFromNBT(tag);
            check(restored.getLifeStage() == stage, "Saved stage " + stage);
            check(restored.getTicksInStage() == 12345, "Saved ticks " + stage);
            check(restored.getStageDuration() == 23456, "Saved duration " + stage);
            check(restored.isAIDisabled() == (stage == DragonLifeStage.EGG), "Saved AI " + stage);
            check(restored.getAppearance() == 2 && restored.getBaseScale() == 0.8F, "Saved appearance/scale");
            check(restored.getCrackLevel() == 1, "Saved cracks");
            check(restored.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue() == 6,
                    "Attack attribute registered");
            TICK.invoke(restored);
            check(restored.getTicksInStage() == (stage == DragonLifeStage.ADULT ? 12345 : 12346),
                    "Continues after reload " + stage);
        }
    }

    private static void stageBoundaries() throws Exception {
        for (DragonLifeStage stage : DragonLifeStage.values()) {
            if (stage == DragonLifeStage.EGG || stage == DragonLifeStage.ADULT) continue;
            TestDragon dragon = loaded(false, stage, 98, 100);
            TICK.invoke(dragon);
            check(dragon.getLifeStage() == stage && dragon.getTicksInStage() == 99, "No early stage advance");
            TICK.invoke(dragon);
            check(dragon.getLifeStage() == stage.next() && dragon.getTicksInStage() == 0, "Stage advance " + stage);
            check(dragon.getStageDuration() == stage.next().getDurationTicks(), "Next stage configuration");
            check(!dragon.isAIDisabled(), "Hatched dragon AI");
        }
    }

    private static void incubation() throws Exception {
        TestDragon dragon = loaded(false, DragonLifeStage.EGG, 73, 100);
        TICK.invoke(dragon);
        check(dragon.getCrackLevel() == 1, "Existing cracks retained");
        dragon.setCrackLevel(0);
        TICK.invoke(dragon);
        check(dragon.getCrackLevel() == 1, "Cracks at 75 percent");
        for (int i = 0; i < 15; i++) TICK.invoke(dragon);
        check(dragon.getCrackLevel() == 2 && dragon.wobbles == 1, "Cracks and wobble at 90 percent");
        for (int i = 0; i < 50; i++) TICK.invoke(dragon);
        check(dragon.isEgg() && dragon.getTicksInStage() == 140, "Minimum is not a guaranteed hatch time");

        TestDragon lucky = loaded(false, DragonLifeStage.EGG, 98, 100);
        random(lucky, true);
        TICK.invoke(lucky);
        check(lucky.isEgg(), "Cannot hatch before minimum even with successful random draw");
        TICK.invoke(lucky);
        check(lucky.getLifeStage() == DragonLifeStage.HATCHLING && lucky.getTicksInStage() == 0, "Hatches at minimum");
        check(lucky.getCrackLevel() == 3 && !lucky.isAIDisabled(), "Hatch state");
    }

    private static void clientCheckpoints() throws Exception {
        TestDragon server = loaded(false, DragonLifeStage.FLEDGLING, 12340, 32000);
        for (boolean reversed : new boolean[] {false, true}) {
            TestDragon client = new TestDragon(new TestWorld(true));
            sync(server, client, reversed);
            check(client.getTicksInStage() == 12340, "Initial checkpoint order=" + reversed);
            TICK.invoke(client);
            check(client.getTicksInStage() == 12341, "Interpolates from checkpoint");
            for (int i = 0; i < 40; i++) TICK.invoke(client);
            check(client.getTicksInStage() == 12360, "Bounded client extrapolation");
            sync(server, client, reversed);
            check(client.getTicksInStage() == 12340, "Server corrects client prediction");
            server.setStage(DragonLifeStage.JUVENILE);
            sync(server, client, reversed);
            check(client.getLifeStage() == DragonLifeStage.JUVENILE && client.getTicksInStage() == 0,
                    "Stage transition checkpoint order=" + reversed);
            server = loaded(false, DragonLifeStage.FLEDGLING, 12340, 32000);
        }
        TestDragon clientEgg = loaded(true, DragonLifeStage.EGG, 99, 100);
        random(clientEgg, true);
        for (int i = 0; i < 50; i++) TICK.invoke(clientEgg);
        check(clientEgg.isEgg() && clientEgg.getTicksInStage() == 100, "Client cannot hatch");
        TestDragon clientYoung = loaded(true, DragonLifeStage.JUVENILE, 99, 100);
        for (int i = 0; i < 50; i++) TICK.invoke(clientYoung);
        check(clientYoung.getLifeStage() == DragonLifeStage.JUVENILE, "Client cannot advance stage");
    }

    private static void centeredGrowth() throws Exception {
        for (boolean client : new boolean[] {false, true}) {
            TestDragon dragon = loaded(client, DragonLifeStage.HATCHLING, 0, 100);
            dragon.setPosition(12.5D, 64.0D, -8.5D);
            for (DragonLifeStage stage : DragonLifeStage.values()) {
                dragon.setStage(stage);
                for (float scale : new float[] {0.1F, 1.0F, 2.0F}) {
                    dragon.setBaseScale(scale);
                    RESIZE.invoke(dragon);
                    AxisAlignedBB box = dragon.getEntityBoundingBox();
                    check(dragon.posX == 12.5D && dragon.posY == 64 && dragon.posZ == -8.5D, "Growth does not move entity");
                    check(Math.abs((box.minX + box.maxX) / 2 - dragon.posX) < 0.000001D, "Box X center");
                    check(Math.abs((box.minZ + box.maxZ) / 2 - dragon.posZ) < 0.000001D, "Box Z center");
                    check(Math.abs(box.maxY - box.minY - dragon.height) < 0.000001D, "Box height");
                }
            }
        }
    }

    private static void stoppedLifecycle() throws Exception {
        boolean allowEnd = POConfig.DragonSettings.allowHatchingInEnd;
        try {
            POConfig.DragonSettings.allowHatchingInEnd = false;
            for (boolean client : new boolean[] {false, true}) {
                TestDragon egg = loaded(client, DragonLifeStage.EGG, 99, 100);
                egg.world.provider.setDimension(1);
                TICK.invoke(egg);
                check(egg.getTicksInStage() == 99, "End pauses egg on both sides");
                TestDragon dead = loaded(client, DragonLifeStage.JUVENILE, 99, 100);
                dead.setHealth(0);
                TICK.invoke(dead);
                check(dead.getLifeStage() == DragonLifeStage.JUVENILE && dead.getTicksInStage() == 99, "Dead dragon cannot grow");
            }
        } finally {
            POConfig.DragonSettings.allowHatchingInEnd = allowEnd;
        }
    }

    private static void returnToEgg() throws Exception {
        boolean enabled = POConfig.DragonSettings.enableHatching;
        try {
            POConfig.DragonSettings.enableHatching = true;
            TestWorld world = new TestWorld(false);
            TestPlayer player = new TestPlayer(world);
            player.setSneaking(true);
            TestDragon dragon = new TestDragon(world);
            dragon.setStage(DragonLifeStage.ADULT);
            dragon.setPosition(0.5D, 64.0D, 0.5D);
            PlayerInteractEvent.EntityInteract event = interaction(player, EnumHand.MAIN_HAND, dragon);
            DragonEggInteractionHandler.onRightClickDragon(event);
            check(event.isCanceled() && event.getCancellationResult() == EnumActionResult.SUCCESS, "Return consumes interaction");
            check(dragon.isDead && world.blocks.size() == 1, "Return creates exactly one egg");
            PlayerInteractEvent.EntityInteract duplicate = interaction(player, EnumHand.OFF_HAND, dragon);
            DragonEggInteractionHandler.onRightClickDragon(duplicate);
            check(!duplicate.isCanceled() && world.blocks.size() == 1, "Repeated request cannot duplicate egg");

            TestDragon blocked = new TestDragon(world);
            blocked.setStage(DragonLifeStage.INFANT);
            blocked.setPosition(5.5D, 64.0D, 5.5D);
            world.allowPlacement = false;
            DragonEggInteractionHandler.onRightClickDragon(interaction(player, EnumHand.MAIN_HAND, blocked));
            check(!blocked.isDead && world.blocks.size() == 1, "Failed placement keeps dragon");
        } finally {
            POConfig.DragonSettings.enableHatching = enabled;
        }
    }

    private static PlayerInteractEvent.EntityInteract interaction(TestPlayer player, EnumHand hand, TestDragon dragon) {
        return new PlayerInteractEvent.EntityInteract(player, hand, dragon) {
            // Forge normally generates this method from @Cancelable in its launch transformer.
            @Override public boolean isCancelable() { return true; }
        };
    }

    private static TestDragon loaded(boolean client, DragonLifeStage stage, int ticks, int duration) throws Exception {
        TestDragon dragon = new TestDragon(new TestWorld(client));
        NBTTagCompound tag = new NBTTagCompound();
        dragon.writeEntityToNBT(tag);
        tag.setString("DragonStageName", stage.name());
        tag.setInteger("DragonStageTicks", ticks);
        tag.setInteger("DragonStageDuration", duration);
        tag.setInteger("DragonAppearance", 2);
        tag.setFloat("DragonBaseScale", 0.8F);
        tag.setInteger("DragonCrackLevel", 1);
        dragon.readEntityFromNBT(tag);
        return dragon;
    }

    private static void sync(TestDragon server, TestDragon client, boolean reversed) {
        List<EntityDataManager.DataEntry<?>> entries = server.getDataManager().getAll();
        if (reversed) Collections.reverse(entries);
        client.getDataManager().setEntryValues(entries);
    }

    private static void random(TestDragon dragon, final boolean lucky) throws Exception {
        Field field = EntityPollutionDragon.class.getDeclaredField("dragonRandom");
        field.setAccessible(true);
        field.set(dragon, new Random() {
            @Override public int nextInt(int bound) { return lucky ? 0 : bound - 1; }
        });
    }

    private static Method method(String name) {
        try {
            Method method = EntityPollutionDragon.class.getDeclaredMethod(name);
            method.setAccessible(true);
            return method;
        } catch (ReflectiveOperationException failure) {
            throw new ExceptionInInitializerError(failure);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static final class TestDragon extends EntityPollutionDragon {
        int wobbles;
        TestDragon(World world) throws Exception {
            super(world);
            random(this, false);
            firstUpdate = false;
        }
        @Override public void startWobble(int ticks, int axis) { wobbles++; }
    }

    private static final class TestWorld extends World {
        final Map<BlockPos, IBlockState> blocks = new HashMap<>();
        boolean allowPlacement = true;
        TestWorld(boolean client) {
            super(new SaveHandlerMP(), new WorldInfo(new NBTTagCompound()), new WorldProviderSurface(), new Profiler(), client);
        }
        @Override protected IChunkProvider createChunkProvider() { return null; }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return false; }
        @Override public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) { }
        @Override public BlockPos getSpawnPoint() { return new BlockPos(0, 64, 0); }
        @Override public boolean isAirBlock(BlockPos pos) { return getBlockState(pos).getBlock() == Blocks.AIR; }
        @Override public IBlockState getBlockState(BlockPos pos) { return blocks.getOrDefault(pos, Blocks.AIR.getDefaultState()); }
        @Override public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            if (!allowPlacement) return false;
            blocks.put(pos, state);
            return true;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        TestPlayer(World world) { super(world, new GameProfile(new UUID(0, 1), "DragonSmoke")); }
        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
        @Override public boolean canPlayerEdit(BlockPos pos, EnumFacing facing, ItemStack stack) { return true; }
    }
}
