package keqing.pollution.common.items.behaviors;

import gregtech.api.items.metaitem.stats.IItemBehaviour;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

import java.util.ArrayList;
import java.util.List;

public class VisCheckerBehavior implements IItemBehaviour {
    public VisCheckerBehavior() {
    }
    public void addInformation(ItemStack stack, List<String> lines) {
        lines.add(I18n.format("对准任意方块右键获取当前区块的污染值与灵气值"));
    }
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!world.isRemote) {
            player.sendMessage(new TextComponentTranslation("========================"));
            player.sendMessage(new TextComponentTranslation("当前区块灵气量：%s", AuraHelper.getVis(world, pos)));
            player.sendMessage(new TextComponentTranslation("当前区块污染量：%s", AuraHelper.getFlux(world, pos)));
            player.sendMessage(new TextComponentTranslation("========================"));
        }
        return EnumActionResult.SUCCESS;
    }

}

