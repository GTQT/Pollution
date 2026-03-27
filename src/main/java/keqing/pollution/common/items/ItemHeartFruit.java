package keqing.pollution.common.items;

import keqing.pollution.Pollution;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * 心鸣果物品
 * 食用后恢复大量饥饿值，并获得再生效果
 * 带有轻微的恶心副作用
 */
public class ItemHeartFruit extends ItemFood {

    public ItemHeartFruit() {
        super(6, 1.2F, false); // 6点饥饿值, 1.2饱和度
        setTranslationKey(Pollution.MODID + ".heart_fruit");
        setRegistryName(Pollution.MODID, "heart_fruit");
        setCreativeTab(CreativeTabs.FOOD);
        setAlwaysEdible(); // 随时可食用
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        if (!worldIn.isRemote) {
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 600, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 60, 0));
        }
    }
}
