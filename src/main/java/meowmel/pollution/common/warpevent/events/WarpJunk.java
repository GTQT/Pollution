package meowmel.pollution.common.warpevent.events;

import meowmel.pollution.common.warpevent.IEventWarp;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** 扭曲事件：咒波呕出垃圾，周围掉出杂物 */
public class WarpJunk extends IEventWarp {
    private static final ItemStack[] JUNK = {
            new ItemStack(Items.ROTTEN_FLESH),
            new ItemStack(Items.BONE),
            new ItemStack(Items.STICK, 2),
            new ItemStack(Items.SPIDER_EYE),
            new ItemStack(Items.POISONOUS_POTATO),
            new ItemStack(Blocks.DIRT, 4),
            new ItemStack(Blocks.COBBLESTONE, 3),
            new ItemStack(Blocks.GRAVEL, 2),
    };

    public WarpJunk(int minWarp) {
        super("junk", minWarp);
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        sendChat(player);
        int count = 4 + world.rand.nextInt(4);
        for (int i = 0; i < count; i++) {
            EntityItem item = new EntityItem(world,
                    player.posX + world.rand.nextGaussian() * 3.0,
                    player.posY + 1.0,
                    player.posZ + world.rand.nextGaussian() * 3.0,
                    JUNK[world.rand.nextInt(JUNK.length)].copy());
            item.setDefaultPickupDelay();
            world.spawnEntity(item);
        }
        return true;
    }
}
