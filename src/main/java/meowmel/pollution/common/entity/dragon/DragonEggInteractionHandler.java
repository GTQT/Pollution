package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.POConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "pollution")
public final class DragonEggInteractionHandler {
    private DragonEggInteractionHandler() {
    }

    @SubscribeEvent
    public static void onRightClickDragonEgg(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();
        if (!POConfig.DragonSettings.enableHatching || Loader.isModLoaded("dragonmounts")) {
            return;
        }
        if (world.provider.getDimension() == 1 && !POConfig.DragonSettings.allowHatchingInEnd) {
            return;
        }
        IBlockState state = world.getBlockState(event.getPos());
        if (state.getBlock() != Blocks.DRAGON_EGG || !player.canPlayerEdit(event.getPos(), event.getFace(), player.getHeldItem(event.getHand()))) {
            return;
        }
        if (POConfig.DragonSettings.sneakToHatch && !player.isSneaking()) {
            return;
        }
        if (!player.getHeldItem(event.getHand()).isEmpty()) {
            return;
        }

        event.setCanceled(true);
        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        if (world.isRemote) {
            return;
        }
        BlockPos eggPos = event.getPos();
        EntityPollutionDragon dragon = new EntityPollutionDragon(world);
        dragon.setLocationAndAngles(eggPos.getX() + 0.5D, eggPos.getY() + 0.05D,
                eggPos.getZ() + 0.5D, player.rotationYaw, 0.0F);
        dragon.setStage(DragonLifeStage.EGG);
        dragon.setAppearance(world.rand.nextInt(3));
        if (world.setBlockToAir(eggPos)) {
            if (!world.spawnEntity(dragon)) {
                world.setBlockState(eggPos, Blocks.DRAGON_EGG.getDefaultState(), 3);
            } else {
                world.playSound(null, eggPos, net.minecraft.init.SoundEvents.BLOCK_STONE_HIT,
                        net.minecraft.util.SoundCategory.BLOCKS, 0.9F, 0.7F);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickDragon(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof EntityPollutionDragon)) {
            return;
        }
        EntityPollutionDragon dragon = (EntityPollutionDragon) event.getTarget();
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        ItemStack held = player.getHeldItem(event.getHand());
        BlockPos eggPos = dragon.getPosition();
        if (!world.isAirBlock(eggPos)) {
            eggPos = eggPos.up();
        }
        if (dragon.isEgg() || !player.isSneaking() || !held.isEmpty() || !POConfig.DragonSettings.enableHatching
                || !world.isAirBlock(eggPos) || !player.canPlayerEdit(eggPos, EnumFacing.UP, held)) {
            return;
        }
        event.setCanceled(true);
        if (world.isRemote) {
            return;
        }
        if (world.isAirBlock(eggPos) && world.setBlockState(eggPos, Blocks.DRAGON_EGG.getDefaultState(), 3)) {
            dragon.setDead();
            world.playSound(null, eggPos, net.minecraft.init.SoundEvents.BLOCK_STONE_PLACE,
                    net.minecraft.util.SoundCategory.BLOCKS, 0.9F, 0.9F);
        }
    }
}
