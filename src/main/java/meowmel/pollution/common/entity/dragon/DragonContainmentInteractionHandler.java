package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.common.items.ItemBlockBloodyStarlightContainer;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "pollution")
public final class DragonContainmentInteractionHandler {
    private DragonContainmentInteractionHandler() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getItemStack().getItem() instanceof ItemBlockBloodyStarlightContainer)
                || !(event.getTarget() instanceof EntityPollutionDragon)) return;
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (!event.getWorld().isRemote) {
            DragonContainmentService.capture(event.getEntityPlayer(), event.getHand(),
                    (EntityPollutionDragon) event.getTarget());
        }
    }
}
