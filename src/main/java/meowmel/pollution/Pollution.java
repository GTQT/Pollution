package meowmel.pollution;

import meowmel.pollution.api.POAPI;
import meowmel.pollution.api.recipes.builder.IndustrialInfusionBuilder;
import meowmel.pollution.api.recipes.properties.MagicRecipeProperties;
import meowmel.pollution.api.utils.PollutionLog;
import meowmel.pollution.common.CommonProxy;
import meowmel.pollution.common.command.CommandMagicAmplification;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.blocks.PollutionBlocksInit;
import meowmel.pollution.common.entity.PoEntitiesRegistry;
import meowmel.pollution.common.items.PollutionMetaItems;
import meowmel.pollution.common.metatileentity.PollutionMetaTileEntities;
import meowmel.pollution.common.metatileentity.multiblockpart.wireless.WirelessManager;
import meowmel.pollution.dimension.worldgen.PODimensionManager;
import meowmel.pollution.dimension.worldgen.PODimensionType;
import meowmel.pollution.dimension.worldgen.POStructureManager;
import meowmel.pollution.dimension.worldgen.PollutionOreVeins;
import meowmel.pollution.integration.POIntegration;
import meowmel.pollution.integration.botania.BotaniaMaterialUnification;
import meowmel.pollution.loaders.loot.GregTechLootTable;
import meowmel.pollution.loaders.recipes.MeteorsHelper;
import meowmel.pollution.loaders.recipes.mods.AstralSorcery;
import meowmel.pollution.loaders.recipes.mods.Botania;
import meowmel.pollution.common.ModGuiHandler;
import meowmel.pollution.common.warpevent.WarpEventHandler;
import meowmel.pollution.common.warpevent.net.MeowmelNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
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
        dependencies = "required-after:gregtech@[0,);" +
                "required-after:gregtechfoodoption@[0,);" +
                "required-after:gtqtcore@[0,);" +
                "required-after:bloodmagic@[0,);" +
                "required-after:extrabotany@[0,);" +
                "required-after:botania@[0,);" +
                "required-after:astralsorcery@[0,);"
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
        BotaniaMaterialUnification.init();
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
        // 注册 GUI 处理器（右键矿物提取器打开 GUI 的前提）
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());
        PollutionLog.init(event.getModLog());
        MagicRecipeProperties.init();
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
        WarpEventHandler.init();
        MeowmelNetwork.init();
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void ClientpreInit(FMLPreInitializationEvent event) {
        PoEntitiesRegistry.initRenderers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Dynamic Thaumcraft infusion imports are disabled until the GT/HEI input expansion is fixed upstream.开启的话会至少占用额外约 0.87 GiB
        IndustrialInfusionBuilder.init();
        Botania.init();
        AstralSorcery.init();
        MeteorsHelper.init();
        try {
            PollutionOreVeins.registerVeins();
            PollutionOreVeins.registerOrbs();
        } catch (Exception exception) {
            LOGGER.error("Failed to register GTCEu ore veins", exception);
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMagicAmplification());
    }


}