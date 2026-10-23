import com.mojang.authlib.GameProfile;
import meowmel.pollution.common.block.blocks.BlockBloodyStarlightContainer;
import meowmel.pollution.common.block.tile.TileEntityBloodyStarlightContainer;
import meowmel.pollution.common.entity.dragon.*;
import meowmel.pollution.common.items.ItemBlockBloodyStarlightContainer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.*;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.*;

/** Exercises the production transactions and block drops in a deterministic in-memory world. */
public final class DragonContainmentSmoke {
    private static BlockBloodyStarlightContainer block;
    private static ItemBlockBloodyStarlightContainer item;
    private static int checks;

    public static void main(String[] args) {
        try {
            Bootstrap.register();
            block = new BlockBloodyStarlightContainer();
            item = new ItemBlockBloodyStarlightContainer(block);
            net.minecraftforge.registries.GameData.getBlockItemMap().put(block, item);
            GameRegistry.registerTileEntity(TileEntityBloodyStarlightContainer.class,
                    new ResourceLocation("pollution", "containment_smoke"));
            roundTrips();
            rejectedActions();
            itemAndBlockTransfers();
            System.out.println("PASS: " + checks + " containment checks (all stages/skins, identity, health, NBT, drops, creative, rollback, collision)");
            System.exit(0);
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        }
    }

    private static EntityPollutionDragon dragon(TestWorld world, DragonLifeStage stage, int appearance) {
        EntityPollutionDragon dragon = new EntityPollutionDragon(world);
        dragon.setStage(stage);
        dragon.setAppearance(appearance);
        dragon.setBaseScale(0.8F);
        dragon.setCustomNameTag("Containment Dragon " + appearance);
        dragon.setHealth(13.5F);
        dragon.setPosition(2.5, 64, 0.5);
        dragon.getEntityData().setString("AddonPayload", "preserved");
        dragon.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 150, 1));
        NBTTagCompound tag = dragon.writeToNBT(new NBTTagCompound());
        tag.setInteger("DragonStageTicks", stage == DragonLifeStage.ADULT ? 0 : 123);
        dragon.readFromNBT(tag);
        world.loadedEntityList.add(dragon);
        return dragon;
    }

    private static void roundTrips() {
        for (DragonLifeStage stage : DragonLifeStage.values()) {
            if (stage == DragonLifeStage.EGG) continue;
            for (int appearance = 0; appearance < 3; appearance++) {
                TestWorld world = new TestWorld();
                TestPlayer player = new TestPlayer(world);
                player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(item));
                EntityPollutionDragon source = dragon(world, stage, appearance);
                UUID uuid = source.getUniqueID();
                int ticks = source.getTicksInStage();
                check(DragonContainmentService.capture(player, EnumHand.MAIN_HAND, source), "Capture " + stage);
                check(source.isDead, "Original removed");
                NBTTagCompound data = DragonContainmentData.get(player.getHeldItemMainhand());
                check(DragonContainmentData.isValid(data), "Valid payload");
                NBTTagCompound frozen = data.copy();
                check(!DragonContainmentService.capture(player, EnumHand.MAIN_HAND, source), "Duplicate click rejected");
                check(data.equals(frozen), "Duplicate leaves snapshot unchanged");

                // Tile save, reload, and client sync all use real production serialization.
                TileEntityBloodyStarlightContainer tile = new TileEntityBloodyStarlightContainer();
                tile.setPayload(data);
                TileEntityBloodyStarlightContainer loaded = new TileEntityBloodyStarlightContainer();
                loaded.readFromNBT(tile.writeToNBT(new NBTTagCompound()));
                check(loaded.getPayload().equals(data), "Tile round trip preserves complete data");
                check(!loaded.getUpdateTag().getCompoundTag(DragonContainmentData.KEY).hasKey("Dragon"), "No full entity in tile packet");

                check(DragonContainmentService.release(player, loaded.getPayload(), new BlockPos(8, 64, 0)), "Release " + stage);
                EntityPollutionDragon restored = world.lastDragon;
                check(uuid.equals(restored.getUniqueID()), "UUID preserved");
                check(restored.getLifeStage() == stage && restored.getTicksInStage() == ticks, "Growth preserved");
                check(restored.getAppearance() == appearance && restored.getBaseScale() == 0.8F, "Appearance and scale preserved");
                check(restored.getHealth() == 13.5F && restored.getCustomNameTag().equals(source.getCustomNameTag()), "Health and name preserved");
                check(restored.getEntityData().getString("AddonPayload").equals("preserved"), "ForgeData preserved");
                check(restored.getActivePotionEffect(MobEffects.REGENERATION).getDuration() == 150, "Potion clock frozen");
                check(!DragonContainmentService.release(player, data, new BlockPos(16, 64, 0)), "Loaded UUID cannot be duplicated");
            }
        }
    }

    private static void rejectedActions() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(item));
        EntityPollutionDragon egg = dragon(world, DragonLifeStage.EGG, 0);
        check(!DragonContainmentService.capture(player, EnumHand.MAIN_HAND, egg) && !egg.isDead, "Egg rejected without deletion");
        EntityPollutionDragon source = dragon(world, DragonLifeStage.ADULT, 1);
        source.startRiding(egg, true);
        check(!DragonContainmentService.capture(player, EnumHand.MAIN_HAND, source), "Riding dragon rejected");
        source.dismountRidingEntity();
        player.allowEdit = false;
        check(!DragonContainmentService.capture(player, EnumHand.MAIN_HAND, source) && !source.isDead, "Permission protects target");
        player.allowEdit = true;
        check(DragonContainmentService.capture(player, EnumHand.MAIN_HAND, source), "Capture fixture");
        player.setSneaking(true);
        NBTTagCompound data = DragonContainmentData.get(player.getHeldItemMainhand()).copy();
        world.allowSpawn = false;
        item.onItemUseFirst(player, world, new BlockPos(8, 63, 0), EnumFacing.UP, 0.5F, 1, 0.5F, EnumHand.MAIN_HAND);
        check(DragonContainmentData.get(player.getHeldItemMainhand()).equals(data), "Cancelled spawn preserves filled item");
        check(world.lastDragon == null, "No spawned dragon after rejection");
        world.allowSpawn = true;
        world.blocked = true;
        check(!DragonContainmentService.release(player, data, new BlockPos(8, 64, 0)), "Blocked volume rejected");
        world.blocked = false;
        world.liquid = true;
        check(!DragonContainmentService.release(player, data, new BlockPos(8, 64, 0)), "Liquid rejected");
        world.liquid = false;
        world.loaded = false;
        check(!DragonContainmentService.release(player, data, new BlockPos(8, 64, 0)), "Unloaded chunks rejected");
        world.loaded = true;
        world.support = false;
        check(!DragonContainmentService.release(player, data, new BlockPos(8, 64, 0)), "Unsupported drop rejected");
        world.support = true;
        NBTTagCompound invalid = data.copy();
        invalid.getCompoundTag("Dragon").setString("DragonStageName", "unknown_stage");
        check(!DragonContainmentService.release(player, invalid, new BlockPos(8, 64, 0)), "Unknown stage cannot turn into egg");
        item.onItemUseFirst(player, world, new BlockPos(8, 63, 0), EnumFacing.UP, 0.5F, 1, 0.5F, EnumHand.MAIN_HAND);
        check(!DragonContainmentData.isOccupied(player.getHeldItemMainhand()) && world.lastDragon != null, "Successful item release empties contents");
    }

    private static void itemAndBlockTransfers() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        EntityPollutionDragon source = dragon(world, DragonLifeStage.JUVENILE, 2);
        NBTTagCompound payload = DragonContainmentData.capture(source);
        source.setDead();
        BlockPos pos = new BlockPos(8, 64, 0);
        ItemStack held = new ItemStack(item);
        DragonContainmentData.set(held, payload);
        player.setHeldItem(EnumHand.MAIN_HAND, held);
        player.capabilities.isCreativeMode = true;
        item.onItemUse(player, world, pos.down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 1, 0.5F);
        // The interaction manager restores the count after invoking ItemBlock in creative.
        held.setCount(1);
        check(!DragonContainmentData.isOccupied(held), "Creative placement transfers rather than clones");
        TileEntityBloodyStarlightContainer tile = (TileEntityBloodyStarlightContainer) world.getTileEntity(pos);
        check(tile != null && tile.getPayload().equals(payload), "Placed tile contains snapshot");
        block.onBlockExploded(world, pos, null);
        check(world.getTileEntity(pos) == tile && tile.isOccupied(), "Explosion preserves occupied block");
        world.destroyBlock(pos, true);
        check(world.drops.size() == 1, "Destroy with drops produces exactly one item");
        check(DragonContainmentData.get(world.drops.get(0)).equals(payload), "Dropped item retains contents");

        world.setBlockState(pos, block.getDefaultState(), 3);
        ((TileEntityBloodyStarlightContainer) world.getTileEntity(pos)).setPayload(payload);
        world.setBlockToAir(pos);
        check(world.drops.size() == 2, "Forced replacement drops one preserved item");
        world.setBlockState(pos, block.getDefaultState(), 3);
        ((TileEntityBloodyStarlightContainer) world.getTileEntity(pos)).setPayload(payload);
        world.restoringBlockSnapshots = true;
        world.setBlockToAir(pos);
        world.restoringBlockSnapshots = false;
        check(world.drops.size() == 2, "Placement rollback does not duplicate contents");

        world.setBlockState(pos, block.getDefaultState(), 3);
        tile = (TileEntityBloodyStarlightContainer) world.getTileEntity(pos);
        tile.setPayload(payload);
        player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        player.setSneaking(true);
        block.onBlockActivated(world, pos, block.getDefaultState(), player, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
        check(!tile.isOccupied() && world.lastDragon != null, "Placed container releases and remains empty");
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static final class TestPlayer extends EntityPlayer {
        boolean allowEdit = true;
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "ContainmentSmoke"));
            setPosition(0.5, 64, 0.5);
        }
        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return capabilities.isCreativeMode; }
        @Override public boolean canPlayerEdit(BlockPos pos, EnumFacing side, ItemStack stack) { return allowEdit; }
        @Override public void sendStatusMessage(ITextComponent message, boolean actionBar) { }
    }

    private static final class TestWorld extends World {
        final Map<BlockPos, IBlockState> blocks = new HashMap<>();
        final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        final List<ItemStack> drops = new ArrayList<>();
        boolean allowSpawn = true, loaded = true, support = true, blocked, liquid;
        EntityPollutionDragon lastDragon;
        TestWorld() { super(new SaveHandlerMP(), new WorldInfo(new NBTTagCompound()), new WorldProviderSurface(), new Profiler(), false); }
        @Override protected IChunkProvider createChunkProvider() { return null; }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return loaded; }
        @Override public boolean isBlockLoaded(BlockPos pos) { return loaded; }
        @Override public BlockPos getSpawnPoint() { return new BlockPos(0, 64, 0); }
        @Override public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) { return true; }
        @Override public IBlockState getBlockState(BlockPos pos) { return blocks.getOrDefault(pos, support && pos.getY() == 63 ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState()); }
        @Override public boolean mayPlace(Block block, BlockPos pos, boolean ignore, EnumFacing side, Entity placer) { return isAirBlock(pos); }
        @Override public boolean isAirBlock(BlockPos pos) { return getBlockState(pos).getBlock() == Blocks.AIR; }
        @Override public TileEntity getTileEntity(BlockPos pos) { return tiles.get(pos); }
        @Override public void removeTileEntity(BlockPos pos) { tiles.remove(pos); }
        @Override public void markChunkDirty(BlockPos pos, TileEntity tile) { }
        @Override public boolean checkLight(BlockPos pos) { return true; }
        @Override public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            IBlockState previous = getBlockState(pos);
            if (previous.getBlock() != state.getBlock()) previous.getBlock().breakBlock(this, pos, previous);
            blocks.put(pos.toImmutable(), state);
            if (state.getBlock().hasTileEntity(state) && !tiles.containsKey(pos)) {
                TileEntity tile = state.getBlock().createTileEntity(this, state);
                tile.setWorld(this);
                tile.setPos(pos);
                tiles.put(pos.toImmutable(), tile);
            }
            return true;
        }
        @Override public List<AxisAlignedBB> getCollisionBoxes(Entity entity, AxisAlignedBB box) {
            if (blocked) return Collections.singletonList(box);
            List<AxisAlignedBB> result = new ArrayList<>();
            for (Map.Entry<BlockPos, IBlockState> entry : blocks.entrySet()) {
                if (entry.getValue().getBlock() != Blocks.AIR) {
                    AxisAlignedBB collision = new AxisAlignedBB(entry.getKey());
                    if (collision.intersects(box)) result.add(collision);
                }
            }
            return result;
        }
        @Override public boolean checkNoEntityCollision(AxisAlignedBB box, Entity ignored) {
            for (Entity entity : loadedEntityList) if (entity != ignored && !entity.isDead && entity.getEntityBoundingBox().intersects(box)) return false;
            return true;
        }
        @Override public boolean containsAnyLiquid(AxisAlignedBB box) { return liquid; }
        @Override public boolean spawnEntity(Entity entity) {
            if (!allowSpawn) return false;
            loadedEntityList.add(entity);
            if (entity instanceof EntityPollutionDragon) lastDragon = (EntityPollutionDragon) entity;
            if (entity instanceof EntityItem) drops.add(((EntityItem) entity).getItem());
            return true;
        }
        @Override public void removeEntity(Entity entity) { entity.setDead(); loadedEntityList.remove(entity); }
        @Override public void updateEntityWithOptionalForce(Entity entity, boolean force) { }
        @Override public void playSound(EntityPlayer player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) { }
    }
}
