package keqing.pollution.dimension.biome.biomes;

import keqing.pollution.common.entity.moster.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHellDecorator;

public class POBiomeBNTNether extends Biome {
    public POBiomeBNTNether() {
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
