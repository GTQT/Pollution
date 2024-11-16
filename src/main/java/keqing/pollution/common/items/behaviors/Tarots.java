package keqing.pollution.common.items.behaviors;

import gregtech.api.items.metaitem.stats.IItemBehaviour;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;

public class Tarots {
    public static class THE_FOOL implements IItemBehaviour{
        public THE_FOOL() {
        }

        public void addInformation(ItemStack stack, List<String> lines) {
            lines.add(I18n.format("潜行右键传送回世界出生点"));
        }

        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
            if (!world.isRemote && player.isSneaking()) {
                BlockPos spawnPoint = world.getSpawnPoint();
                player.setPositionAndUpdate(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
                player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 2.0F);


            }
            return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }


    }


}
