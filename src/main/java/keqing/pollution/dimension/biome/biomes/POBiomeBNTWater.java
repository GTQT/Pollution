package keqing.pollution.dimension.biome.biomes;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.biome.spawning.spawners.*;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorDeepWaters;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CragSpiresFeature;
import thebetweenlands.common.world.gen.biome.feature.DeepWatersFeature;

import java.util.List;

public class POBiomeBNTWater extends BiomeBetweenlands {
    public POBiomeBNTWater() {
        super(new ResourceLocation("pollution", "pollution_biome.3"),
                (new Biome.BiomeProperties("Deep Cave Waters"))
                        .setBaseHeight(108.0F)
                        .setHeightVariation(5.0F)
                        .setWaterColor(0xADD8E6)
                        .setTemperature(0.8F)
                        .setRainfall(0F));
        this.setWeight(12);
        this.getBiomeGenerator()
                .setDecorator(new BiomeDecoratorDeepWaters(this))
                .addFeature(new DeepWatersFeature())
                .addFeature(new AlgaeFeature())
                .addFeature(new CragSpiresFeature());
        this.setFoliageColors(15071045, 15071045);
    }

    public void addTypes() {
        BiomeDictionary.addTypes(this, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.WATER);
    }

    protected void addSpawnEntries(List<ICustomSpawnEntry> entries) {
        super.addSpawnEntries(entries);
        entries.add((new SurfaceSpawnEntry(0, EntityFirefly.class, EntityFirefly::new, (short) 10)).setSpawnCheckRadius(32.0));
        entries.add((new SporelingSpawnEntry(1, EntitySporeling.class, EntitySporeling::new, (short) 80)).setGroupSize(2, 5).setSpawnCheckRadius(32.0));
        entries.add((new CaveSpawnEntry(2, EntityOlm.class, EntityOlm::new, (short) 30)).setCanSpawnInWater(true).setGroupSize(2, 4).setSpawnCheckRadius(32.0));
        entries.add((new SurfaceSpawnEntry(3, EntityLurker.class, EntityLurker::new, (short) 35)).setCanSpawnInWater(true).setHostile(true).setSpawnCheckRadius(16.0));
        entries.add((new SurfaceSpawnEntry(4, EntityAngler.class, EntityAngler::new, (short) 45)).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
        entries.add((new CaveSpawnEntry(5, EntityAngler.class, EntityAngler::new, (short) 35)).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 3));
        entries.add((new SwampHagCaveSpawnEntry(6, (short) 120)).setHostile(true).setSpawnCheckRadius(24.0).setGroupSize(1, 3));
        entries.add((new CaveSpawnEntry(7, EntityWight.class, EntityWight::new, (short) 18)).setHostile(true).setSpawnCheckRadius(64.0));
        entries.add((new CaveSpawnEntry(8, EntityChiromaw.class, EntityChiromaw::new, (short) 60)).setHostile(true).setSpawnCheckRadius(20.0).setGroupSize(1, 3));
        entries.add((new BetweenstoneCaveSpawnEntry(9, EntityBoulderSprite.class, EntityBoulderSprite::new, (short) 60)).setHostile(true).setSpawnCheckRadius(16.0).setSpawnCheckRangeY(8.0));
        entries.add((new SkySpawnEntry(10, EntityChiromawGreeblingRider.class, EntityChiromawGreeblingRider::new, (short) 20)).setSpawnCheckRadius(64.0).setGroupSize(1, 3).setSpawningInterval(600).setHostile(true));
        entries.add((new PitstoneCaveSpawnEntry(11, EntityStalker.class, EntityStalker::new, (short) 12)).setConstantWeight(true).setHostile(true).setSpawnCheckRadius(64.0).setSpawnCheckRangeY(16.0).setSpawningInterval(6000));
        entries.add((new CaveSpawnEntry(12, EntitySwarm.class, EntitySwarm::new, (short) 60)).setConstantWeight(true).setHostile(true).setSpawnCheckRadius(32.0));
        entries.add((new SurfaceSpawnEntry(13, EntityAnadia.class, EntityAnadia::new, (short) 60)).setCanSpawnInWater(true).setHostile(false).setGroupSize(1, 5));
        entries.add((new SurfaceSpawnEntry(14, EntityFreshwaterUrchin.class, EntityFreshwaterUrchin::new, (short) 30)).setSurfacePredicate(SurfaceType.DIRT).setCanSpawnInWater(true).setHostile(false).setGroupSize(1, 3));
        entries.add((new SurfaceSpawnEntry(15, EntityJellyfish.class, EntityJellyfish::new, (short) 30)).setCanSpawnInWater(true).setHostile(false).setGroupSize(1, 3));
        entries.add((new CaveSpawnEntry(16, EntityJellyfishCave.class, EntityJellyfishCave::new, (short) 25)).setCanSpawnInWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0));
        entries.add((new SurfaceSpawnEntry(17, EntityRockSnot.class, EntityRockSnot::new, (short) 20)).setSurfacePredicate(SurfaceType.MIXED_GROUND).setCanSpawnInWater(true).setHostile(true).setGroupSize(1, 1));
        entries.add((new CaveSpawnEntry(18, EntityCaveFish.class, EntityCaveFish::new, (short) 30)).setCanSpawnInWater(true).setGroupSize(1, 3).setSpawnCheckRadius(32.0));
        entries.add((new SurfaceSpawnEntry(19, EntityGreeblingCoracle.class, EntityGreeblingCoracle::new, (short) 5)).setCanSpawnOnWater(true).setHostile(false).setGroupSize(1, 1).setSpawningInterval(2000));
    }
}
