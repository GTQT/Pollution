package meowmel.pollution.dimension.biome;

/**
 * 地下世界群系地形模式：决定 chunk generator 的 prepareHeights 使用哪套地形逻辑。
 * 所有模式都必须遵守洞窟骨架约束：y ≥ 84 实心洞顶、y ≤ 8 基岩底座。
 */
public enum UndergroundTerrainMode {
    /** 标准洞窟：现有噪声插值逻辑 */
    STANDARD,
    /** 微丘：标准 + 列级噪声偏移（蘑菇森林） */
    HILLY,
    /** 柱状柱廊：钟乳石从洞顶悬挂（石林） */
    PILLAR,
    /** 低地：洞窟底部填充岩浆（熔岩盆地） */
    LOWLAND,
    /** 河谷：标准 + 河流噪声挖槽填水（地下暗河） */
    RIVER
}
