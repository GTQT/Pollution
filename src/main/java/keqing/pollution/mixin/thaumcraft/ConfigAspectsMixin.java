package keqing.pollution.mixin.thaumcraft;

import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import keqing.pollution.Pollution;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.common.config.ConfigAspects;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

        // 添加目录权限检查
        if (!pollutionFolder.canWrite()) {
            Pollution.LOGGER.error("缓存目录不可写: {}", pollutionFolder.getAbsolutePath());
            throw new RuntimeException("无法写入缓存目录");
        }

        File aspectsCache = new File(pollutionFolder, "aspects_cache.bin");
        CommonInternals.objectTags.clear();

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
                    // 记录完整异常堆栈
                    Pollution.LOGGER.error("缓存版本不兼容", e);
                    Pollution.LOGGER.error("正在生成新缓存替代...");
                } catch (ClassNotFoundException | IOException e) {
                    // 记录完整异常堆栈
                    Pollution.LOGGER.error("缓存读取失败", e);
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
                Thread.currentThread().interrupt();
            }

            cacheValid = loadSuccess.get();
        }

        // 如果缓存无效或不存在，则重建
        if (!cacheValid) {
            Pollution.LOGGER.info("开始注册aspects...");
            Pollution.LOGGER.info("缓存无效，创建新缓存");

            Stopwatch stopwatch = Stopwatch.createStarted();

            registerItemAspects();
            // 写入新缓存
            File tempFile;
            try {
                stopwatch.reset().start();

                // 使用临时文件确保原子写入
                tempFile = File.createTempFile("pollution-cache", ".tmp", pollutionFolder);

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

        Pollution.LOGGER.info("注册实体aspects...");
        registerEntityAspects();
        // 发送虚拟注册事件
        AspectRegistryEvent are = new AspectRegistryEvent();
        are.register = new AspectEventProxy();
        MinecraftForge.EVENT_BUS.post(are);

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