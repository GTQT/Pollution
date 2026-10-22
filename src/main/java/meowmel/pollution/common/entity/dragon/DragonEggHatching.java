package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.POConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Server-side block-to-entity conversion shared by the incubator and its regression checks. */
public final class DragonEggHatching {
    private DragonEggHatching() {
    }

    public static boolean isAllowed(World world) {
        return !world.isRemote && POConfig.DragonSettings.enableHatching
                && (world.provider.getDimension() != 1 || POConfig.DragonSettings.allowHatchingInEnd);
    }

    public static boolean isNestClear(World world, BlockPos eggPos) {
        return isNestClear(world, eggPos, null);
    }

    public static boolean isNestClear(World world, BlockPos eggPos, EntityPollutionDragon managedEgg) {
        for (BlockPos pos : BlockPos.getAllInBox(eggPos.add(-1, 0, -1), eggPos.add(1, 2, 1))) {
            if (!world.isBlockLoaded(pos) || (!pos.equals(eggPos) && !world.isAirBlock(pos))) {
                return false;
            }
        }
        AxisAlignedBB nest = new AxisAlignedBB(eggPos.add(-1, 0, -1), eggPos.add(2, 3, 2));
        return world.getEntitiesWithinAABB(EntityLivingBase.class, nest,
                entity -> entity != managedEgg && entity.isEntityAlive()).isEmpty();
    }

    public static boolean tryActivate(World world, BlockPos eggPos) {
        return tryActivate(world, eggPos, null);
    }

    public static boolean tryActivate(World world, BlockPos eggPos, BlockPos controller) {
        if (!isAllowed(world) || world.getBlockState(eggPos).getBlock() != Blocks.DRAGON_EGG
                || !isNestClear(world, eggPos)) {
            return false;
        }
        EntityPollutionDragon dragon = new EntityPollutionDragon(world);
        if (controller != null) dragon.bindIncubator(controller);
        dragon.setLocationAndAngles(eggPos.getX() + 0.5D, eggPos.getY() + 0.05D,
                eggPos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
        // Keep the block until the spawn event succeeds. No ticks occur between these operations.
        if (!world.spawnEntity(dragon)) {
            return false;
        }
        if (!dragon.isEntityAlive() || world.getBlockState(eggPos).getBlock() != Blocks.DRAGON_EGG
                || !world.setBlockToAir(eggPos)) {
            dragon.setDead();
            return false;
        }
        world.playSound(null, eggPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.9F, 0.7F);
        return true;
    }
}
