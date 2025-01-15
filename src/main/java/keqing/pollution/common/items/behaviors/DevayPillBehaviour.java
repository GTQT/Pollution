package keqing.pollution.common.items.behaviors;

import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import gregtech.common.items.behaviors.AbstractUsableBehaviour;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

import java.util.List;

public class DevayPillBehaviour extends AbstractUsableBehaviour implements IItemDurabilityManager {
    private static int decay;
    private final ItemStack empty;


    public DevayPillBehaviour(int decay, ItemStack empty, int totalUses) {
        super(totalUses);
        this.empty = empty;
        DevayPillBehaviour.decay = decay;
    }



    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
            if (cap != null) {
                if (!player.capabilities.isCreativeMode) cap.getDecayStats().addStats(-decay, 0);
                this.useItemDurability(player, hand, stack, this.empty.copy());
            }

        } else
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }


    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        return (double) this.getUsesLeft(itemStack) / (double) this.totalUses;
    }

    public void addInformation(ItemStack itemStack, List<String> lines) {
        int remainingUses = this.getUsesLeft(itemStack);

        lines.add(I18n.format("§5§l万 能 理 智 药§r"));


        lines.add(I18n.format("behaviour.paintspray.uses", remainingUses));
        lines.add(I18n.format("tooltip.bl.decay_food", itemStack.getDisplayName()));
        lines.add(I18n.format("右键后可修复玩家 %s 腐蚀量",decay));
    }
}
