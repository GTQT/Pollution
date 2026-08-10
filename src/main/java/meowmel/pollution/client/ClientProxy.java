package meowmel.pollution.client;

import gregtech.api.GregTechAPI;
import gregtech.client.renderer.handler.MetaTileEntityRenderer;
import meowmel.pollution.Pollution;
import meowmel.pollution.client.tesr.TesrMagicCircle;
import meowmel.pollution.client.tesr.TesrMineralExtractor;
import meowmel.pollution.client.tesr.TesrConstellationCrystal;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.CommonProxy;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.tile.TileEntityMagicCircle;
import meowmel.pollution.common.block.tile.TileEntityMineralExtractor;
import meowmel.pollution.common.block.tile.TileEntityConstellationCrystal;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber({Side.CLIENT})
public class ClientProxy extends CommonProxy {
	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		PollutionMetaBlocks.registerItemModels();
		ModelLoader.setCustomMeshDefinition(
				Item.getItemFromBlock(GregTechAPI.mteManager.getRegistry(Pollution.MODID).getBlock()),
				stack -> MetaTileEntityRenderer.MODEL_LOCATION);
	}

	public void preLoad() {
		super.preLoad();
		OBJLoader.INSTANCE.addDomain(Pollution.MODID);
		POTextures.init();
		POTextures.preInit();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagicCircle.class, new TesrMagicCircle());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMineralExtractor.class, new TesrMineralExtractor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConstellationCrystal.class,
				new TesrConstellationCrystal());
	}

	@Override
	public void init() {
		super.init();
	}
}
