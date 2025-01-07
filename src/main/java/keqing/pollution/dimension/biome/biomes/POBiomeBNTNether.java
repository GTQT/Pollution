package keqing.pollution.dimension.biome.biomes;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHellDecorator;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntityShambler;
import thebetweenlands.common.world.biome.BiomeBetweenlands;

public class POBiomeBNTNether extends BiomeBetweenlands {
    public POBiomeBNTNether() {
        super(new ResourceLocation("pollution", "pollution_biome.2"),
                (new Biome.BiomeProperties("Deep Cave Basic"))
                        .setBaseHeight(108.0F)
                        .setHeightVariation(5.0F)
                        .setWaterColor(0xADD8E6)
                        .setTemperature(0.8F)
                        .setRainfall(0F));
        this.setWeight(12);
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();

        this.addSpawnEntries();

        this.decorator = new BiomeHellDecorator();
    }

    private void addSpawnEntries() {
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityLurker.class, 4, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityPeatMummy.class, 2, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityShambler.class, 2, 1, 1));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityChiromaw.class, 1, 1, 1));
    }
}
