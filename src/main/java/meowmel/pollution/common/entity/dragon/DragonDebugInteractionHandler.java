package meowmel.pollution.common.entity.dragon;

import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Creative-only diagnostics; never invoked by normal incubation or growth. */
@Mod.EventBusSubscriber(modid = "pollution")
public final class DragonDebugInteractionHandler {
    private DragonDebugInteractionHandler() {}

    private static boolean claim(PlayerInteractEvent event) {
        ItemStack held = event.getItemStack();
        if (PollutionMetaItems.DRAGON_DEBUGGER == null || held.isEmpty()
                || !held.isItemEqual(PollutionMetaItems.DRAGON_DEBUGGER.getStackForm())) return false;
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (event.getWorld().isRemote || event.getHand() != EnumHand.MAIN_HAND) return false;
        if (!event.getEntityPlayer().isCreative()) {
            event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("pollution.dragon_debug.creative"), true);
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void onDragon(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof EntityPollutionDragon) || !claim(event)) return;
        EntityPollutionDragon dragon = (EntityPollutionDragon) event.getTarget();
        if (!dragon.isEntityAlive()) return;
        dragon.debugAdvanceGrowth(event.getEntityPlayer().isSneaking());
        report(event, dragon);
    }

    @SubscribeEvent
    public static void onEgg(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().getBlockState(event.getPos()).getBlock() != Blocks.DRAGON_EGG || !claim(event)) return;
        if (!event.getEntityPlayer().canPlayerEdit(event.getPos(), event.getFace(), event.getItemStack())
                || !DragonEggHatching.tryActivate(event.getWorld(), event.getPos())) {
            event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("pollution.dragon_debug.blocked"), true);
            return;
        }
        // Conversion checks an empty chamber, so this is the newly spawned egg only.
        for (EntityPollutionDragon dragon : event.getWorld().getEntitiesWithinAABB(EntityPollutionDragon.class,
                new AxisAlignedBB(event.getPos()), entity -> entity.isEntityAlive() && entity.isEgg())) {
            dragon.debugAdvanceGrowth(event.getEntityPlayer().isSneaking());
            report(event, dragon);
            break;
        }
    }

    private static void report(PlayerInteractEvent event, EntityPollutionDragon dragon) {
        event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("pollution.dragon_debug.stage",
                new TextComponentTranslation(dragon.getLifeStage().getTranslationKey())), true);
    }
}
