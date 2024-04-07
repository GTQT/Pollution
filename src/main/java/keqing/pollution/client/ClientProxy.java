package keqing.pollution.client;

import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.CommonProxy;
import keqing.pollution.common.block.PollutionMetaBlocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber({Side.CLIENT})
public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        PollutionMetaBlocks.registerItemModels();
    }
    public void preLoad()
    {
        super.preLoad();
        POTextures.init();
        POTextures.preInit();
    }

}