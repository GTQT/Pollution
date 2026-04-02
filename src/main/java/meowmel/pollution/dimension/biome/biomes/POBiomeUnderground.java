package meowmel.pollution.dimension.biome.biomes;

import meowmel.pollution.common.entity.moster.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHellDecorator;

public class POBiomeUnderground extends Biome {

    public POBiomeUnderground() {
        super(new Biome.BiomeProperties("Deep Cave Basic").setWaterColor(0xADD8E6).setTemperature(0.5F));
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();

        this.addSpawnEntries();

        this.decorator = new BiomeHellDecorator();
    }

    private void addSpawnEntries() {
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySlimeAer.class, 2, 1, 2));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySlimeignis.class, 2, 1, 2));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySlimeAqua.class, 2, 1, 2));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySlimeTerra.class, 2, 1, 2));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySlimeOrdo.class, 2, 1, 2));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySlimePerditio.class, 2, 1, 2));
    }
}
