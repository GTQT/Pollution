package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.common.items.ItemBlockBloodyStarlightContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Server-thread transactions. Failed release leaves the source payload untouched. */
public final class DragonContainmentService {
    private static final Set<UUID> TRANSFERRING = new HashSet<>();

    private DragonContainmentService() {}

    public static boolean capture(EntityPlayer player, EnumHand hand, EntityPollutionDragon dragon) {
        World world = player.world;
        if (world.isRemote) return false;
        ItemStack held = player.getHeldItem(hand);
        if (!(held.getItem() instanceof ItemBlockBloodyStarlightContainer) || held.getCount() != 1) return false;
        if (DragonContainmentData.isOccupied(held)) return fail(player, "full");
        if (!dragon.isEntityAlive() || dragon.isEgg()) return fail(player, "target");
        if (dragon.isBeingRidden() || dragon.isRiding() || dragon.getLeashed()) return fail(player, "attached");
        if (dragon.world != world || player.getDistanceSq(dragon) > 64.0D
                || !canEdit(player, dragon.getPosition(), held)) return fail(player, "permission");
        UUID id = dragon.getUniqueID();
        if (!TRANSFERRING.add(id)) return false;
        try {
            NBTTagCompound payload = DragonContainmentData.capture(dragon);
            if (!DragonContainmentData.isValid(payload)) return fail(player, "invalid");
            ItemStack filled = held.copy();
            DragonContainmentData.set(filled, payload);
            player.setHeldItem(hand, filled);
            // No death callback, loot or experience. A second click sees a dead target/full item.
            dragon.setDead();
            player.inventory.markDirty();
            effect(world, dragon.posX, dragon.posY + dragon.height * 0.5D, dragon.posZ, true);
            message(player, "captured");
            return true;
        } catch (RuntimeException invalid) {
            // Serialization failed before commit: the living dragon remains available.
            if (!dragon.isDead) player.setHeldItem(hand, held);
            return fail(player, "invalid");
        } finally {
            TRANSFERRING.remove(id);
        }
    }

    public static boolean release(EntityPlayer player, NBTTagCompound payload, BlockPos origin) {
        World world = player.world;
        if (world.isRemote) return false;
        if (!DragonContainmentData.isValid(payload)) return fail(player, "invalid");
        UUID id = payload.getCompoundTag("Dragon").getUniqueId("UUID");
        if (!TRANSFERRING.add(id)) return false;
        EntityPollutionDragon dragon = null;
        try {
            for (WorldServer loaded : DimensionManager.getWorlds()) {
                Entity existing = loaded.getEntityFromUuid(id);
                if (existing != null && !existing.isDead) return fail(player, "duplicate");
            }
            for (Entity existing : world.loadedEntityList) {
                if (!existing.isDead && id.equals(existing.getUniqueID())) return fail(player, "duplicate");
            }
            NBTTagCompound tag = payload.getCompoundTag("Dragon").copy();
            tag.removeTag("DragonIncubatorPos");
            tag.removeTag("Dimension");
            dragon = new EntityPollutionDragon(world);
            dragon.readFromNBT(tag);
            if (!dragon.isEntityAlive() || dragon.isEgg()) return fail(player, "invalid");
            dragon.dimension = world.provider.getDimension();
            dragon.motionX = dragon.motionY = dragon.motionZ = 0;
            dragon.fallDistance = 0;
            dragon.getNavigator().clearPath();
            if (!findPosition(player, dragon, origin)) return fail(player, "space");
            if (!world.spawnEntity(dragon) || !dragon.isEntityAlive()) {
                dragon.setDead();
                world.removeEntity(dragon);
                return fail(player, "spawn_failed");
            }
            effect(world, dragon.posX, dragon.posY + dragon.height * 0.5D, dragon.posZ, false);
            message(player, "released");
            return true;
        } catch (RuntimeException invalid) {
            // Bad addon capability NBT must not destroy the only copy of the dragon.
            if (dragon != null) {
                dragon.setDead();
                world.removeEntity(dragon);
            }
            return fail(player, "invalid");
        } finally {
            TRANSFERRING.remove(id);
        }
    }

    private static boolean findPosition(EntityPlayer player, EntityPollutionDragon dragon, BlockPos origin) {
        for (int radius = 0; radius <= 4; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                    for (int dy : new int[]{0, 1, -1}) {
                        BlockPos pos = origin.add(dx, dy, dz);
                        dragon.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                                player.rotationYaw, 0);
                        if (safePosition(player, dragon)) return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean safePosition(EntityPlayer player, EntityPollutionDragon dragon) {
        World world = player.world;
        AxisAlignedBB box = dragon.getEntityBoundingBox();
        BlockPos min = new BlockPos(box.minX, box.minY - 1, box.minZ);
        BlockPos max = new BlockPos(box.maxX, box.maxY, box.maxZ);
        if (box.minY < 1 || box.maxY >= world.getHeight()
                || !world.isAreaLoaded(min, max) || !world.getWorldBorder().contains(min)
                || !world.getWorldBorder().contains(max)) return false;
        for (BlockPos pos : BlockPos.getAllInBoxMutable(min, max)) {
            if (!canEdit(player, pos, ItemStack.EMPTY)) return false;
            net.minecraft.block.Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.FIRE || block == Blocks.CACTUS || block == Blocks.MAGMA) return false;
        }
        if (!world.getCollisionBoxes(dragon, box).isEmpty() || !world.checkNoEntityCollision(box, dragon)
                || world.containsAnyLiquid(box.grow(0, 0.05D, 0))) return false;
        double inset = Math.min(0.05D, dragon.width * 0.1D);
        for (double x : new double[]{box.minX + inset, box.maxX - inset}) {
            for (double z : new double[]{box.minZ + inset, box.maxZ - inset}) {
                BlockPos support = new BlockPos(x, box.minY - 0.01D, z);
                if (!world.getBlockState(support).isSideSolid(world, support, EnumFacing.UP)
                        || world.getBlockState(support).getMaterial().isLiquid()) return false;
            }
        }
        return true;
    }

    public static boolean canEdit(EntityPlayer player, BlockPos pos, ItemStack stack) {
        return !player.isSpectator() && player.world.isBlockModifiable(player, pos)
                && player.canPlayerEdit(pos, EnumFacing.UP, stack);
    }

    private static void effect(World world, double x, double y, double z, boolean capture) {
        world.playSound(null, x, y, z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS,
                0.6F, capture ? 1.4F : 0.8F);
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.PORTAL, x, y, z, 36, 0.5, 0.4, 0.5, 0.15);
            ((WorldServer) world).spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 18, 0.4, 0.35, 0.4, 0);
        }
    }

    public static void message(EntityPlayer player, String key) {
        player.sendStatusMessage(new TextComponentTranslation("pollution.containment." + key), true);
    }

    private static boolean fail(EntityPlayer player, String key) {
        message(player, key);
        return false;
    }
}
