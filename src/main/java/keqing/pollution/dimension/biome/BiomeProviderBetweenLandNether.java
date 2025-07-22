package keqing.pollution.dimension.biome;

import net.minecraft.world.biome.BiomeProvider;

import static keqing.pollution.dimension.biome.POBiomeHandler.UnderWorld_BIOME;

public class BiomeProviderBetweenLandNether extends BiomeProvider {


    public BiomeProviderBetweenLandNether() {
        super();
        this.getBiomesToSpawnIn().clear();
        this.getBiomesToSpawnIn().add(UnderWorld_BIOME);
    }
}