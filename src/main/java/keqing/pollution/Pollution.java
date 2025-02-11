package keqing.pollution;

import keqing.pollution.Advancement.AdvancementTriggers;
import keqing.pollution.api.POAPI;
import keqing.pollution.api.utils.PollutionLog;
import keqing.pollution.common.CommonProxy;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.blocks.PollutionBlocksInit;
import keqing.pollution.common.items.PollutionMetaItems;
import keqing.pollution.common.metatileentity.PollutionMetaTileEntities;
import keqing.pollution.dimension.worldgen.PODimensionManager;
import keqing.pollution.dimension.worldgen.PODimensionType;
import keqing.pollution.dimension.worldgen.POStructureManager;
import keqing.pollution.integration.POIntegration;
import keqing.pollution.loaders.loot.GregTechLootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;


@Mod(
        modid = "pollution",
        name = "Pollution",
        acceptedMinecraftVersions = "[1.12.2,1.13)",
        version = "0.0.1-beta",
        dependencies = "required-after:gregtech@[2.8.5-beta,);" +
                "required-after:gtqtcore@[0,);" +
                "required-after:bloodmagic@[0,);" +
                "required-after:thebetweenlands@[0,);" +
                "required-after:botania@[0,);"
)
public class Pollution {
    public static final String MODID = "pollution";
    public static final String NAME = "Pollution";
    public static final String VERSION = "1.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final Random RANDOM = new Random();

    @SidedProxy(
            clientSide = "keqing.pollution.client.ClientProxy",
            serverSide = "keqing.pollution.common.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        POIntegration.init();
        proxy.init();
        GregTechLootTable.init();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PollutionLog.init(event.getModLog());
        PollutionMetaBlocks.init();
        POAPI.init();
        PollutionMetaItems.initialization();
        POStructureManager.init();
        PODimensionType.init();
        PODimensionManager.init();
        proxy.preLoad();
        MinecraftForge.EVENT_BUS.register(new PollutionBlocksInit());
        PollutionMetaTileEntities.initialization();
        AdvancementTriggers.register();
    }

}