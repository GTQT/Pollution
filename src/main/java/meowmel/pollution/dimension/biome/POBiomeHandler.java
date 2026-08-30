package meowmel.pollution.dimension.biome;

import meowmel.pollution.dimension.biome.AlfheimBiomes;
import meowmel.pollution.dimension.biome.biomes.POBiomeBlood;
import meowmel.pollution.dimension.biome.biomes.POBiomeDemiplane;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class POBiomeHandler {

	// 创建一个静态实例
	public static final Biome DEMIPLANE_BIOME = new POBiomeDemiplane(); // 替换为你的自定义生物群系类
	public static final Biome UNDERGROUND_BIOME = UndergroundBiomes.DEEP_CAVE;
    public static final Biome BLOOD_BIOME = new POBiomeBlood();

	@SubscribeEvent
	public static void registerBiomes(RegistryEvent.Register<Biome> event) {
		IForgeRegistry<Biome> registry = event.getRegistry();

		// 次位面
		DEMIPLANE_BIOME.setRegistryName(new ResourceLocation("Pollution", "pollution_biome.1"));
		registry.register(DEMIPLANE_BIOME);
		BiomeManager.addSpawnBiome(DEMIPLANE_BIOME);

		// 地下世界 深窟基础（兜底，现状景观）
		UNDERGROUND_BIOME.setRegistryName(new ResourceLocation("pollution", "underground_deep_cave"));
		registry.register(UNDERGROUND_BIOME);
		BiomeManager.addSpawnBiome(UNDERGROUND_BIOME);
        BiomeDictionary.addTypes(UNDERGROUND_BIOME, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);

        // 地下世界 7 个风格群系
        registerUndergroundBiome(registry, UndergroundBiomes.STALACTITE_CLUSTER, "stalactite_cluster",
                BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.SPOOKY);
        registerUndergroundBiome(registry, UndergroundBiomes.CRYSTAL_CLUSTER, "crystal_cluster",
                BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.DRY);
        registerUndergroundBiome(registry, UndergroundBiomes.MUSHROOM_FOREST, "mushroom_forest",
                BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE,
                BiomeDictionary.Type.LUSH);
        registerUndergroundBiome(registry, UndergroundBiomes.LUSH_CAVE, "lush_cave",
                BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH);
        registerUndergroundBiome(registry, UndergroundBiomes.PRIMORDIAL_CAVE, "primordial_cave",
                BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE,
                BiomeDictionary.Type.LUSH);
        registerUndergroundBiome(registry, UndergroundBiomes.DESERT_CAVE, "desert_cave",
                BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.DRY);
        registerUndergroundBiome(registry, UndergroundBiomes.MAGMA_CAVE, "magma_cave",
                BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.HOT,
                BiomeDictionary.Type.DRY);

        // 血色
        BLOOD_BIOME.setRegistryName(new ResourceLocation("Pollution", "pollution_biome.3"));
        registry.register(BLOOD_BIOME);
        BiomeManager.addSpawnBiome(BLOOD_BIOME);

        registerAlfheimBiome(registry, AlfheimBiomes.FIELD, "alfheim_field",
                BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH);
        registerAlfheimBiome(registry, AlfheimBiomes.GIANT_FLOWER_FIELD, "alfheim_giant_flower_field",
                BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH);
        registerAlfheimBiome(registry, AlfheimBiomes.BEACH, "alfheim_beach",
                BiomeDictionary.Type.SANDY, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.BEACH);
        registerAlfheimBiome(registry, AlfheimBiomes.SANDBANK, "alfheim_sandbank",
                BiomeDictionary.Type.SANDY, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.BEACH);
        registerAlfheimBiome(registry, AlfheimBiomes.RIVER, "alfheim_river",
                BiomeDictionary.Type.RIVER, BiomeDictionary.Type.WET);
        registerAlfheimBiome(registry, AlfheimBiomes.LOW_PLATEAU, "alfheim_low_plateau",
                BiomeDictionary.Type.HILLS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.PLAINS);
        registerAlfheimBiome(registry, AlfheimBiomes.MID_PLATEAU, "alfheim_mid_plateau",
                BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.PLAINS);
        registerAlfheimBiome(registry, AlfheimBiomes.HIGH_PLATEAU, "alfheim_high_plateau",
                BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.PLAINS);
        registerAlfheimBiome(registry, AlfheimBiomes.HIGH_PLATEAU_FOREST, "alfheim_high_plateau_forest",
                BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MOUNTAIN,
                BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH);
        registerAlfheimBiome(registry, AlfheimBiomes.HIGH_PLATEAU_FIELD, "alfheim_high_plateau_field",
                BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.MOUNTAIN,
                BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH);
        registerAlfheimBiome(registry, AlfheimBiomes.ISLAND_FOREST, "alfheim_island_forest",
                BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH);
        registerAlfheimBiome(registry, AlfheimBiomes.PIT_FOREST, "alfheim_pit_forest",
                BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS,
                BiomeDictionary.Type.DENSE, BiomeDictionary.Type.LUSH);
	}

    private static void registerUndergroundBiome(IForgeRegistry<Biome> registry, Biome biome, String name,
                                                 BiomeDictionary.Type... types) {
        biome.setRegistryName(new ResourceLocation("pollution", name));
        registry.register(biome);
        BiomeDictionary.addTypes(biome, types);
    }

    private static void registerAlfheimBiome(IForgeRegistry<Biome> registry, Biome biome, String name,
                                             BiomeDictionary.Type... sourceTypes) {
        biome.setRegistryName(new ResourceLocation("pollution", name));
        registry.register(biome);
        BiomeDictionary.Type[] types = new BiomeDictionary.Type[sourceTypes.length + 1];
        types[0] = BiomeDictionary.Type.MAGICAL;
        System.arraycopy(sourceTypes, 0, types, 1, sourceTypes.length);
        BiomeDictionary.addTypes(biome, types);
    }
}
