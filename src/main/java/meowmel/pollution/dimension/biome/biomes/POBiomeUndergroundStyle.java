package meowmel.pollution.dimension.biome.biomes;

import meowmel.pollution.dimension.biome.UndergroundTerrainMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHellDecorator;

/**
 * 地下世界风格化群系参数基类：地表/填充方块、水色、温度湿度、生物生成表、地形模式。
 * 具体群系由 {@link meowmel.pollution.dimension.biome.UndergroundBiomes} 工厂静态构建。
 * 装饰不在此类中（由 chunk generator 的 populate 阶段按群系 switch 分发）。
 */
public class POBiomeUndergroundStyle extends Biome {

    private final UndergroundTerrainMode terrainMode;

    public POBiomeUndergroundStyle(String name, int waterColor, float temperature, float rainfall,
                                   IBlockState topBlock, IBlockState fillerBlock,
                                   UndergroundTerrainMode terrainMode,
                                   Biome.SpawnListEntry... monsterEntries) {
        super(new Biome.BiomeProperties(name)
                .setWaterColor(waterColor)
                .setTemperature(temperature)
                .setRainfall(rainfall));
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        for (Biome.SpawnListEntry entry : monsterEntries) {
            this.spawnableMonsterList.add(entry);
        }
        this.terrainMode = terrainMode;
        this.decorator = new BiomeHellDecorator();
    }

    /** chunk generator 据此选择地形生成逻辑 */
    public UndergroundTerrainMode getTerrainMode() {
        return terrainMode;
    }
}
