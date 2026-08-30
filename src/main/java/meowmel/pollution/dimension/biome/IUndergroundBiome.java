package meowmel.pollution.dimension.biome;

import meowmel.pollution.dimension.worldgen.ChunkGenerator.ChunkGeneratorUndergroundWorld;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * 地下世界群系接口（参考 Nether-API 的 INetherBiome 设计）：
 * 群系类实现本接口即被 chunk generator 自动识别，无需注册表耦合。
 * - {@link #buildSurface}：群系在区块生成阶段构建自己的表面方块
 * - {@link #populate}：群系接管装饰阶段（默认 = 原版维度装饰 populateWithVanilla）
 */
public interface IUndergroundBiome {

    /**
     * 表面构建：在给定格子 (x, z) 处替换表面方块。
     *
     * @param chunkGenerator 区块生成器（可访问世界/随机数）
     * @param chunkX        区块 X 坐标
     * @param chunkZ        区块 Z 坐标
     * @param primer        区块方块缓存
     * @param x             格子内 X（0-15）
     * @param z             格子内 Z（0-15）
     * @param terrainNoise  该格地形噪声值（可选参考）
     */
    void buildSurface(ChunkGeneratorUndergroundWorld chunkGenerator, int chunkX, int chunkZ,
                      ChunkPrimer primer, int x, int z, double terrainNoise);

    /**
     * 装饰阶段：默认实现跑维度级原版装饰（populateWithVanilla），
     * 群系可覆写为自定义装饰组。
     */
    default void populate(ChunkGeneratorUndergroundWorld chunkGenerator, int chunkX, int chunkZ) {
        chunkGenerator.populateWithVanilla(chunkX, chunkZ);
    }
}
