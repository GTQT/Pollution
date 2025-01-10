package keqing.pollution.common.block.blocks;

import keqing.pollution.Pollution;
import keqing.pollution.common.block.tile.TileEntityMagicCircle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Pollution.MODID)
public class PollutionBlocksInit {
	public static final BlockMagicCircle BLOCK_MAGIC_CIRCLE = new BlockMagicCircle();
	public static final Item ITEM_BLOCK_MAGIC_CIRCLE = new ItemBlock(BLOCK_MAGIC_CIRCLE).setRegistryName(BLOCK_MAGIC_CIRCLE.getRegistryName().getPath());

	public static final BlockPOPortal BLOCK_TF_PORTAL = new BlockPOPortal();

	@SubscribeEvent
	public static void registerBlock(RegistryEvent.Register<Block> event) {
		// 和物品一样，每一个方块都有唯一一个注册名，不能使用大写字母。
		event.getRegistry().register(BLOCK_MAGIC_CIRCLE);
		event.getRegistry().register(BLOCK_TF_PORTAL);

		// 注册tile entity
		GameRegistry.registerTileEntity(TileEntityMagicCircle.class, new ResourceLocation(Pollution.MODID, "magic_circle"));
	}

	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> event) {
		// 注意这个 ItemBlock 使用了和它对应的方块一样的注册名。
		// 对于所有有物品形态的方块，其物品的注册名和它自己的注册名需要保持一致。
		event.getRegistry().register(ITEM_BLOCK_MAGIC_CIRCLE);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelReg(ModelRegistryEvent event) {
		// 材质注册
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_MAGIC_CIRCLE), 0, new ModelResourceLocation(BLOCK_MAGIC_CIRCLE.getRegistryName(), "inventory"));
	}
}