package meowmel.pollution.common.entity.dragon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
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
        if (!dragon.isEntityAlive() || dragon.isEgg() || !player.isSneaking() || !held.isEmpty()) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (world.isRemote) {
            return;
        }
        // Returning to a vanilla egg discarded the dragon's identity and growth progress.
        DragonContainmentService.message(player, "use_container");
    }
}
