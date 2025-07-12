package keqing.pollution;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import keqing.pollution.api.POAPI;
import keqing.pollution.api.recipes.builder.IndustrialInfusionBuilder;
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
import keqing.pollution.loaders.recipes.mods.Botania;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static keqing.pollution.POConfig.AspectsCache;


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

    @SidedProxy(
            clientSide = "keqing.pollution.client.ClientProxy",
            serverSide = "keqing.pollution.common.CommonProxy"
    )
    public static CommonProxy proxy;
    public static AspectEventProxy PROXY_INSTANCE = new AspectEventProxy();
    public static volatile boolean persistentAspectsCache = true;
    public static Thread aspectsThread;
    public static HashMap<Integer, AspectList> lateObjectTags;
    private static long nextSave = -1L;

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return;
        long now = System.currentTimeMillis();
        if (nextSave > 0 && nextSave < now) {
            nextSave = now + (AspectsCache.saveInterval * 1000L);
            if (lateObjectTags.isEmpty()) {
                LOGGER.info(Pollution.NAME + " found 0 new item aspects to save.");
                return;
            }
            LOGGER.info(Pollution.NAME + " found " + lateObjectTags.size() + " new item aspects to save.");
            File aspectsCache = new File((File) Launch.blackboard.get("CachesFolderFile"), "pollution/aspects_cache.bin");
            if (aspectsCache.isFile() && aspectsCache.exists() && aspectsCache.length() > 0L) {
                new Thread(() -> {
                    try {
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        FileOutputStream fileOutputStream = new FileOutputStream(aspectsCache);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                        Int2ObjectMap<Object2IntMap<String>> objectTags = new Int2ObjectOpenHashMap<>();
                        lateObjectTags.forEach((k, v) -> objectTags.put(k, v.aspects.entrySet().stream().collect(Object2IntArrayMap::new, (m, av) -> m.put(av.getKey().getTag(), av.getValue()), Map::putAll)));
                        objectOutputStream.writeObject(objectTags);
                        fileOutputStream.close();
                        objectOutputStream.close();
                        Pollution.LOGGER.info("Aspects serialization complete! Taken {}.", stopwatch.stop());
                        Pollution.lateObjectTags.clear();
                    } catch (IOException e) {
                        Pollution.LOGGER.error("Aspects serialization failed!");
                        e.printStackTrace();
                    }
                }, "pollution/AspectThread").start();
            }
        }
    }

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
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        IndustrialInfusionBuilder.init();
        Botania.init();
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        if (POConfig.AspectsCache.saveInterval > 0) {
            nextSave = System.currentTimeMillis() + (AspectsCache.saveInterval * 1000L);
        }
    }

}