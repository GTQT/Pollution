package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.POConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "pollution")
public final class DragonEggInteractionHandler {
    private DragonEggInteractionHandler() {
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
        if (!dragon.isEntityAlive() || dragon.isEgg() || !player.isSneaking() || !held.isEmpty() || !POConfig.DragonSettings.enableHatching
                || !world.isAirBlock(eggPos) || !player.canPlayerEdit(eggPos, EnumFacing.UP, held)) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
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
