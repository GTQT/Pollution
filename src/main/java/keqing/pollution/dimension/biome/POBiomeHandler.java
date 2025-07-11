package keqing.pollution.dimension.biome;

import keqing.pollution.dimension.biome.biomes.POBiomeBNTNether;
import keqing.pollution.dimension.biome.biomes.POBiomeDemiplane;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class POBiomeHandler {

	// 创建一个静态实例
	public static final Biome DEMIPLANE_BIOME = new POBiomeDemiplane(); // 替换为你的自定义生物群系类
	public static final Biome UnderWorld_BIOME = new POBiomeBNTNether();
	@SubscribeEvent
	public static void registerBiomes(RegistryEvent.Register<Biome> event) {
		IForgeRegistry<Biome> registry = event.getRegistry();

		// 次位面
		DEMIPLANE_BIOME.setRegistryName(new ResourceLocation("Pollution", "pollution_biome.1"));
		registry.register(DEMIPLANE_BIOME);
		BiomeManager.addSpawnBiome(DEMIPLANE_BIOME);

		// 交错地狱 主维度
		UnderWorld_BIOME.setRegistryName(new ResourceLocation("Pollution", "pollution_biome.2"));
		registry.register(UnderWorld_BIOME);
		BiomeManager.addSpawnBiome(UnderWorld_BIOME);
	}
}