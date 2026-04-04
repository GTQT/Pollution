package meowmel.pollution;

import meowmel.pollution.api.POAPI;
import meowmel.pollution.api.recipes.builder.IndustrialInfusionBuilder;
import meowmel.pollution.api.utils.PollutionLog;
import meowmel.pollution.common.CommonProxy;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.blocks.PollutionBlocksInit;
import meowmel.pollution.common.entity.PoEntitiesRegistry;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import meowmel.pollution.common.metatileentity.multiblockpart.wireless.WirelessManager;
import meowmel.pollution.dimension.worldgen.PODimensionManager;
import meowmel.pollution.dimension.worldgen.PODimensionType;
import meowmel.pollution.dimension.worldgen.POStructureManager;
import meowmel.pollution.integration.POIntegration;
import meowmel.pollution.loaders.loot.GregTechLootTable;
import meowmel.pollution.loaders.recipes.MeteorsHelper;
import meowmel.pollution.loaders.recipes.mods.Botania;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static meowmel.pollution.api.utils.POTeleporter.buildPortalIngredient;


@Mod(
        modid = "pollution",
        name = "Pollution",
        acceptedMinecraftVersions = "[1.12.2,1.13)",
        version = "0.0.1-beta",
        dependencies = "required-after:gregtech@[2.8.5-beta,);" +
                "required-after:gtqtcore@[0,);" +
                "required-after:bloodmagic@[0,);" +
                "required-after:extrabotany@[0,);" +
                "required-after:botania@[0,);"
)
public class Pollution {
    public static final String MODID = "pollution";
    public static final String NAME = "Pollution";
    public static final String VERSION = "1.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final Random RANDOM = new Random();
    @Mod.Instance(MODID)
    public static Pollution instance;
    @SidedProxy(
            clientSide = "meowmel.pollution.client.ClientProxy",
            serverSide = "meowmel.pollution.common.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        POIntegration.init();
        proxy.init();
        GregTechLootTable.init();
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete();
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
        buildPortalIngredient();
        PoEntitiesRegistry.init();
        WirelessManager.getInstance().init();
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void ClientpreInit(FMLPreInitializationEvent event) {
        PoEntitiesRegistry.initRenderers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        IndustrialInfusionBuilder.init();
        Botania.init();
        MeteorsHelper.init();
    }


}