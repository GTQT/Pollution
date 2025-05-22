package keqing.pollution.dimension.biome;

import net.minecraft.world.biome.BiomeProvider;

import static keqing.pollution.dimension.biome.POBiomeHandler.BNTNether_BIOME;

public class BiomeProviderBetweenLandNether extends BiomeProvider {


    public BiomeProviderBetweenLandNether() {
        super();
        this.getBiomesToSpawnIn().clear();
        this.getBiomesToSpawnIn().add(BNTNether_BIOME);
    }
}