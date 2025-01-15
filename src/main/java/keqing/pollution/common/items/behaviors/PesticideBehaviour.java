package keqing.pollution.common.items.behaviors;

import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import gregtech.common.items.behaviors.AbstractUsableBehaviour;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;

import java.util.List;

public class PesticideBehaviour extends AbstractUsableBehaviour implements IItemDurabilityManager {
    private final ItemStack empty;

    public PesticideBehaviour(ItemStack empty, int totalUses) {
        super(totalUses);
        this.empty = empty;
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            IRotSmellCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ROT_SMELL, null);
            if (cap != null) {
                if (cap.isSmellingBad()) {
                    cap.setNotSmellingBad();
                    cap.setImmune(Math.max(cap.getRemainingImmunityTicks(), 600));
                    if (!player.capabilities.isCreativeMode)
                        this.useItemDurability(player, hand, stack, this.empty.copy());
                    if (player instanceof EntityPlayerMP)
                        AdvancementCriterionRegistry.USED_FUMIGANT.trigger((EntityPlayerMP) player);
                }
            }
        }
        if (world.isRemote)
            for (int count = 0; count <= 5; ++count)
                BLParticles.FANCY_BUBBLE.spawn(world, player.posX + (world.rand.nextDouble() - 0.5D), player.posY + 1D + world.rand.nextDouble(), player.posZ + (world.rand.nextDouble() - 0.5D));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }


    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        return (double) this.getUsesLeft(itemStack) / (double) this.totalUses;
    }

    public void addInformation(ItemStack itemStack, List<String> lines) {
        int remainingUses = this.getUsesLeft(itemStack);

        lines.add(I18n.format("§5§l全 无 敌§r"));


        lines.add(I18n.format("behaviour.paintspray.uses", remainingUses));
        lines.add(I18n.format("右键后清除玩家的 “恶臭” 效果（例如：大量类似于苍蝇的小飞虫环绕在玩家身边）"));
    }
}
