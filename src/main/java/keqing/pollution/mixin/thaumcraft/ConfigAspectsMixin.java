package keqing.pollution.mixin.thaumcraft;

import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import keqing.pollution.Pollution;
import keqing.pollution.api.utils.DummyAspectEventProxy;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.config.ConfigAspects;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static keqing.pollution.Pollution.aspectsThread;
import static keqing.pollution.Pollution.persistentAspectsCache;

@Mixin(value = ConfigAspects.class, remap = false)
public abstract class ConfigAspectsMixin {

    @Shadow
    private static void registerItemAspects() {
        throw new AssertionError();
    }

    @Shadow
    private static void registerEntityAspects() {
        throw new AssertionError();
    }

    /**
     * @author MeowmelMuku
     * @reason Offload to an async thread if ConsistentLoad is false
     */
    @Overwrite
    public static void postInit() {
        File pollutionFolder = new File((File) Launch.blackboard.get("CachesFolderFile"), "pollution");
        pollutionFolder.mkdirs();
        File aspectsCache = new File(pollutionFolder, "aspects_cache.bin");

        // 添加详细的缓存状态日志
        Pollution.LOGGER.info("检查缓存文件: {}", aspectsCache.getAbsolutePath());
        Pollution.LOGGER.info("缓存存在: {}, 大小: {} bytes",
                aspectsCache.exists(), aspectsCache.length());

        boolean cacheValid = false;
        Thread loadThread = null;

        // 尝试读取缓存
        if (aspectsCache.isFile() && aspectsCache.exists() && aspectsCache.length() > 0L) {
            Pollution.LOGGER.info("找到缓存，尝试读取...");

            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicBoolean loadSuccess = new AtomicBoolean(false);

            loadThread = new Thread(() -> {
                try {
                    Pollution.LOGGER.info("开始反序列化缓存...");
                    Stopwatch stopwatch = Stopwatch.createStarted();

                    try (FileInputStream fileStream = new FileInputStream(aspectsCache);
                         ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {

                        Int2ObjectMap<Object2IntMap<String>> objectTags =
                                (Int2ObjectMap<Object2IntMap<String>>) objectStream.readObject();

                        objectTags.forEach((i, m) -> {
                            AspectList aspectList = new AspectList();
                            m.forEach((aspect, value) ->
                                    aspectList.aspects.put(Aspect.getAspect(aspect), value));
                            CommonInternals.objectTags.put(i, aspectList);
                        });

                        Pollution.LOGGER.info("缓存反序列化成功! 耗时: {}", stopwatch.stop());
                        loadSuccess.set(true);
                    }
                } catch (InvalidClassException e) {
                    Pollution.LOGGER.error("缓存版本不兼容: {}", e.getMessage());
                    Pollution.LOGGER.error("正在生成新缓存替代...");
                } catch (ClassNotFoundException | IOException e) {
                    Pollution.LOGGER.error("缓存读取失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            }, "pollution/AspectLoader");

            loadThread.start();

            // 等待缓存加载完成（最多10秒）
            try {
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    Pollution.LOGGER.error("缓存加载超时!");
                }
            } catch (InterruptedException e) {
                Pollution.LOGGER.error("缓存加载被中断");
            }

            cacheValid = loadSuccess.get();
        }

        // 如果缓存无效或不存在，则重建
        if (!cacheValid) {
            Pollution.LOGGER.info("开始注册aspects...");
            Pollution.LOGGER.info("缓存无效，创建新缓存");

            Stopwatch stopwatch = Stopwatch.createStarted();
            CommonInternals.objectTags.clear();

            registerItemAspects();
            registerEntityAspects();

            AspectRegistryEvent are = new AspectRegistryEvent();
            are.register = Pollution.PROXY_INSTANCE;
            MinecraftForge.EVENT_BUS.post(are);

            Pollution.LOGGER.info("Aspects注册完成! 耗时: {}", stopwatch.stop());

            // 写入新缓存
            try {
                stopwatch.reset().start();

                // 使用临时文件确保原子写入
                File tempFile = File.createTempFile("pollution-cache", ".tmp", pollutionFolder);
                tempFile.deleteOnExit();

                try (FileOutputStream fos = new FileOutputStream(tempFile);
                     ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                    Int2ObjectMap<Object2IntMap<String>> objectTags = new Int2ObjectOpenHashMap<>();
                    CommonInternals.objectTags.forEach((k, v) ->
                            objectTags.put(k, v.aspects.entrySet().stream()
                                    .collect(Object2IntArrayMap::new,
                                            (m, av) -> m.put(av.getKey().getTag(), av.getValue()),
                                            Map::putAll))
                    );

                    oos.writeObject(objectTags);
                }

                // 原子替换旧缓存
                Files.move(tempFile, aspectsCache);

                Pollution.LOGGER.info("缓存写入成功! 耗时: {}", stopwatch.stop());
            } catch (IOException e) {
                Pollution.LOGGER.error("缓存写入失败!", e);
            }
        }

        // 补充实体注册（无论是否使用缓存都需要）
        if (cacheValid) {
            Pollution.LOGGER.info("补充注册实体aspects...");
            registerEntityAspects();

            // 发送虚拟注册事件
            AspectRegistryEvent are = new AspectRegistryEvent();
            Pollution.PROXY_INSTANCE = new DummyAspectEventProxy();
            are.register = Pollution.PROXY_INSTANCE;
            MinecraftForge.EVENT_BUS.post(are);
        }

        // 清理线程资源
        if (loadThread != null && loadThread.isAlive()) {
            try {
                loadThread.join(5000);
                if (loadThread.isAlive()) {
                    Pollution.LOGGER.warn("缓存加载线程未正常结束，强制中断");
                    loadThread.interrupt();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}