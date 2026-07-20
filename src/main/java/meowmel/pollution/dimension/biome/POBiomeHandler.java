package meowmel.pollution.dimension.biome;

import meowmel.pollution.dimension.biome.biomes.POBiomeBlood;
import meowmel.pollution.dimension.biome.biomes.POBiomeUnderground;
import meowmel.pollution.dimension.biome.biomes.POBiomeDemiplane;
import meowmel.pollution.dimension.biome.AlfheimBiomes;
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
	public static final Biome UNDERGROUND_BIOME = new POBiomeUnderground();
    public static final Biome BLOOD_BIOME = new POBiomeBlood();

	@SubscribeEvent
	public static void registerBiomes(RegistryEvent.Register<Biome> event) {
		IForgeRegistry<Biome> registry = event.getRegistry();

		// 次位面
		DEMIPLANE_BIOME.setRegistryName(new ResourceLocation("Pollution", "pollution_biome.1"));
		registry.register(DEMIPLANE_BIOME);
		BiomeManager.addSpawnBiome(DEMIPLANE_BIOME);

		// 交错地狱 主维度
		UNDERGROUND_BIOME.setRegistryName(new ResourceLocation("Pollution", "pollution_biome.2"));
		registry.register(UNDERGROUND_BIOME);
		BiomeManager.addSpawnBiome(UNDERGROUND_BIOME);

        // 血色
        BLOOD_BIOME.setRegistryName(new ResourceLocation("Pollution", "pollution_biome.3"));
        registry.register(BLOOD_BIOME);
        BiomeManager.addSpawnBiome(BLOOD_BIOME);

        registerAlfheimBiome(registry, AlfheimBiomes.FIELD, "alfheim_field");
        registerAlfheimBiome(registry, AlfheimBiomes.GIANT_FLOWER_FIELD, "alfheim_giant_flower_field");
        registerAlfheimBiome(registry, AlfheimBiomes.BEACH, "alfheim_beach");
        registerAlfheimBiome(registry, AlfheimBiomes.SANDBANK, "alfheim_sandbank");
        registerAlfheimBiome(registry, AlfheimBiomes.RIVER, "alfheim_river");
        registerAlfheimBiome(registry, AlfheimBiomes.LOW_PLATEAU, "alfheim_low_plateau");
        registerAlfheimBiome(registry, AlfheimBiomes.MID_PLATEAU, "alfheim_mid_plateau");
        registerAlfheimBiome(registry, AlfheimBiomes.HIGH_PLATEAU, "alfheim_high_plateau");
        registerAlfheimBiome(registry, AlfheimBiomes.HIGH_PLATEAU_FOREST, "alfheim_high_plateau_forest");
        registerAlfheimBiome(registry, AlfheimBiomes.HIGH_PLATEAU_FIELD, "alfheim_high_plateau_field");
        registerAlfheimBiome(registry, AlfheimBiomes.ISLAND_FOREST, "alfheim_island_forest");
        registerAlfheimBiome(registry, AlfheimBiomes.PIT_FOREST, "alfheim_pit_forest");
	}

    private static void registerAlfheimBiome(IForgeRegistry<Biome> registry, Biome biome, String name) {
        biome.setRegistryName(new ResourceLocation("pollution", name));
        registry.register(biome);
    }
}
