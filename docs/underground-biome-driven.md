# 地下世界群系驱动改造预研报告 v2

> 状态：预研（未实现）
> 参考：**Nether-API 1.12.2**（`C:\Users\Meowmel\Downloads\Nether-API-1.12.2`，作者 jbredwards）
> 目标：为地下世界（基于原版下界模板开发的维度）注册多个生物群系，
> **在不破坏现有洞窟景观的前提下**实现钟乳石群、水晶簇落、蘑菇林、繁茂洞穴、原始洞穴、沙漠洞穴、岩浆洞穴。

---

## 1. Nether-API 机制解读（本方案的设计依据）

Nether-API 解决的问题与我们的完全一致："为类似下界的维度注册生物群系而不破坏景观"。
它让所有下界模组（BetterNether/NetherEx/BOP/Natura...）的生物群系在同一个维度共存。

### 1.1 核心架构（5 条原则）

| # | 机制 | 实现位置 |
|---|---|---|
| 1 | **地形骨架维度级，群系零参与**：`ChunkGeneratorNether extends ChunkGeneratorHell`，`prepareHeights` 完全保留原版下界密度噪声（地狱岩/熔岩海）。群系**不改变**地形高度/洞穴/熔岩海 | `ChunkGeneratorNether.prepareHeights` |
| 2 | **群系接管表面**：`generateChunk` 提前取 biome 数组（16×16），`buildSurfaces` 逐格查：biome 实现 `INetherBiome` → 调 `biome.buildSurface(chunkGen, chunkX, chunkZ, primer, x, z, soulSandNoise, gravelNoise, depthBuffer, terrainNoise)`（噪声数组传给群系，群系自建表面）；否则走 vanilla 标准替换 | `ChunkGeneratorNether.buildSurfaces` |
| 3 | **群系接管装饰**：`populate` 取中心 biome，实现 `INetherBiome` → `biome.populate(this, chunkX, chunkZ)`（默认实现 = `populateWithVanilla` 原版下界全套装饰）；否则原版 | `ChunkGeneratorNether.populate` |
| 4 | **原版 populate 可复用**：`populateWithVanilla(chunkX, chunkZ)` 抽出为公共方法（NETHER_LAVA/FIRE/GLOWSTONE/SHROOM/QUARTZ/MAGMA/LAVA2 全套 + biome.decorate），供群系默认装饰调用 | `populateWithVanilla` |
| 5 | **群系分布走标准 GenLayer**：`BiomeProviderNetherAPI extends BiomeProvider`，用 GenLayer 链 + BiomeCache；`getBiomes`/`getBiomesForGeneration` 从 GenLayer 取 ID → Biome；支持子群系/边缘群系（`INetherAPIBiomeProvider.getSubBiomes/getEdgeBiomes`） | `BiomeProviderNetherAPI` |

### 1.2 关键接口

```java
// 群系接口（群系类实现即可被识别）
public interface INetherBiome {
    void buildSurface(INetherAPIChunkGenerator chunkGenerator, int chunkX, int chunkZ,
                      ChunkPrimer primer, int x, int z,
                      double[] soulSandNoise, double[] gravelNoise, double[] depthBuffer, double terrainNoise);
    default void populate(INetherAPIChunkGenerator chunkGenerator, int chunkX, int chunkZ) {
        chunkGenerator.populateWithVanilla(chunkX, chunkZ);  // 默认 = 原版装饰
    }
}

// generator 接口（群系经它访问世界资源）
public interface INetherAPIChunkGenerator extends IChunkGenerator {
    World getWorld(); Random getRand(); boolean areStructuresEnabled();
    void populateWithVanilla(int chunkX, int chunkZ);
    void setBlocksInPrimer(int chunkX, int chunkZ, ChunkPrimer primer);
}
```

### 1.3 "不破坏景观"的本质

下界的景观 = **密度噪声地形 + 熔岩海 + 洞窟 + 下界堡垒**（维度级，所有群系共享）。
群系只做三件事：**改表面方块、改装饰、改客户端氛围（雾色/音乐）**。
这样无论群系怎么变，地形连续性、洞窟结构、跨群系边界都天然平滑。

---

## 2. 对照分析：之前方案的根本问题

| 之前的方案（已回滚） | 问题 |
|---|---|
| 地形模式切换（PILLAR/HILLY/LOWLAND/RIVER 整列重排） | 群系直接改地形骨架 → 边界断层、"景观被破坏"，违背下界式维度设计 |
| 微丘偏移基于 biome 硬编码 | 地形高度与群系绑定，跨群系不平滑 |
| 装饰器组在 biome 类里（decorate） | 方向对，但装饰器无 generator 资源访问，被迫静态共享 |

**结论**：群系应该按 Nether-API 的模式——**表面 + 装饰 + 流体**接管，地形骨架不动。

---

## 3. 新方案设计（借鉴 Nether-API，适配地下世界）

### 3.1 总体架构

```
UndergroundWorlds (WorldProvider)
  └─ BiomeProviderUnderground        ← 新：仿 BiomeProviderNetherAPI（GenLayer 或简化）
  └─ ChunkGeneratorUndergroundWorld  ← 改（结构不动，加群系分发）
       ├─ generateChunk：提前取 biome 数组（16×16）
       ├─ prepareHeights：地形骨架维度级不变 + 仅"流体种类"逐格查 biome（水/岩浆/河道）
       ├─ buildSurfaces：逐格调群系 buildSurface（群系接口）或 vanilla 标准替换
       ├─ populate：中心 biome 是群系接口 → biome.populate（默认 populateWithVanilla）；否则原版
       └─ populateWithVanilla：抽出为公共方法（现有装饰全套 + biome.decorate）
```

### 3.2 群系接口（新）

```java
public interface IUndergroundBiome {
    void buildSurface(ChunkGeneratorUndergroundWorld chunkGenerator, int chunkX, int chunkZ,
                      ChunkPrimer primer, int x, int z,
                      double[] depthBuffer, double terrainNoise);
    default void populate(ChunkGeneratorUndergroundWorld chunkGenerator, int chunkX, int chunkZ) {
        chunkGenerator.populateWithVanilla(chunkX, chunkZ);
    }
    // 客户端氛围可选：getFogColor/getSkyColor
}
```

`POBiomeUnderground` 实现此接口（深窟基础群系保留原样 = 默认实现）。
新群系 `POBiomeUndergroundStyle implements IUndergroundBiome`（参数化：top/filler/装饰组/流体类型）。

### 3.3 generator 改造点（对照 Nether-API 的 5 条原则）

1. **generateChunk**：`Biome[] biomes = world.getBiomeProvider().getBiomes(null, x<<4, z<<4, 16, 16)` 提前到地形前（已有 biomeArray 填充，复用同一数组）
2. **prepareHeights**：地形逻辑不动（噪声/洞顶/岩石变种全维度级），**无流体分支**——
   全部群系共用默认水填充（v2 定稿：岩浆洞穴的岩浆池为装饰级，不做地形级流体差异）
3. **buildSurfaces**：逐格（16×16）：
   ```
   biome instanceof IUndergroundBiome
       → ((IUndergroundBiome)biome).buildSurface(this, chunkX, chunkZ, primer, x, z, depthBuffer, terrainNoise)
   else → vanilla 标准替换（现有 buildSurfaces 逻辑，topBlock/fillerBlock）
   ```
4. **populate**：
   ```
   centerBiome instanceof IUndergroundBiome
       → ((IUndergroundBiome)centerBiome).populate(this, chunkX, chunkZ)
   else → populateWithVanilla(chunkX, chunkZ)
   ```
5. **populateWithVanilla**：把现有 populate 的装饰全套（钟乳石/蘑菇灯/水池/岩浆池/焦油池/死水/沙砾 + SHROOM 蘑菇 + biome.decorate + 事件钩子）抽成公共方法

### 3.4 BiomeProvider

**推荐：仿 `BiomeProviderNetherAPI` 的 GenLayer 方案**（标准机制，支持 BiomeCache 与将来子/边缘群系）：
- `GenLayerUndergroundBiomes extends GenLayer`：一维噪声 → 群系 ID（区间映射，同旧方案数值）
- provider 持 `genBiomes`（生成用）+ `biomeIndexLayer`（查询用，带边缘平滑可选）+ `BiomeCache`
- `getBiomesToSpawnIn` 返回深窟基础

**简化备选**：Alfheim 式纯函数（无 GenLayer）——快但无 BiomeCache，大范围查询慢。**推荐 GenLayer**。

### 3.5 群系注册

仿 Nether-API 的 `NetherAPIRegistryEvent` 简化版或直接用 `POBiomeHandler`（现有注册点）：
- 8 个群系实例注册进 `POBiomeHandler.registerBiomes`（现有模式，加 7 个）
- 群系与 generator 的绑定通过"实现接口"自动识别（无注册表耦合）——这是 Nether-API 的精髓：**接口即注册**

---

## 4. 群系详细设计（按新哲学：表面/流体/装饰）

> 地形骨架（噪声/洞顶 84/岩石变种层）全部群系共享、不变。
> 全部 7 个风格群系 = **表面方块 + 装饰** 两个维度的差异，无任何地形形状变化。
> 深窟基础保留为兜底群系（噪声不落入其它区间时）。

| 群系 | 表面（buildSurface） | 装饰（populate） | 刷怪 |
|---|---|---|---|
| **① 深窟基础**（兜底，现 `POBiomeUnderground`） | vanilla 标准替换（现状 buildSurfaces，零变化） | 原版全套（populateWithVanilla = 现状） | 6 元素史莱姆（现状） |
| **② 钟乳石群** | 石头（不变） | **石头柱子** `WorldGenStalactite` ×24（密集石柱群）、沙砾 ×4、水池 ×2 | 土/火史莱姆 + 蝙蝠 |
| **③ 水晶簇落** | 石英岩（`QUARTZITE`） | **倾斜粗石英柱**（新生成器 `WorldGenSlantedPillar`）×8、石英矿 ×12、水池 ×2 | 风/火史莱姆 |
| **④ 蘑菇林** | 菌丝（`MYCELIUM`）+ 下 3 格泥土 | **原版大蘑菇** `WorldGenBigMushroom`（红/棕）×16、单蘑菇 ×16、蘑菇灯 ×6 | 土/水史莱姆 + 蝙蝠 |
| **⑤ 繁茂洞穴** | 草方块（`GRASS`）+ 下 3 格泥土 | **原版藤蔓** `WorldGenVines` ×10、**高密度树叶** `WorldGenScatteredBlock(LEAVES)` ×32、水池 ×4 | 土/水史莱姆 + 蝙蝠 |
| **⑥ 原始洞穴** | 草方块（`GRASS`）+ 下 3 格泥土 | **丛林树** `WorldGenTrees` ×8、藤蔓 `WorldGenVines` ×8、**洞顶萤石**（新生成器 `WorldGenGlowstoneCeiling`）×12 | 土/水史莱姆 |
| **⑦ 沙漠洞穴** | 沙子（`SAND`）+ 下 3 格砂岩（`SANDSTONE`） | **仙人掌** `WorldGenCactus` ×12、沙砾 ×6、水池 ×2 | 土/风史莱姆 |
| **⑧ 岩浆洞穴** | 地狱岩（`NETHERRACK`）+ **灵魂沙噪声点缀**（`SOUL_SAND` 按噪声低洼处成片） | **岩浆池** `WorldGenFluidPool(LAVA)` ×12、灵魂沙 `WorldGenMinable(SOUL_SAND)` ×8、焦油池 ×4 | 火史莱姆（权重 5）+ 烈焰人系 |

**流体**：全部群系保持默认水（不做流体分支——岩浆洞穴的岩浆池是装饰级，
由 `WorldGenFluidPool(LAVA)` 在低洼处生成并替换局部水，与下界熔岩海装饰同理）。

**新增 2 个装饰生成器**（`dimension/worldgen/feature/`）：
1. `WorldGenSlantedPillar`——斜石英柱：随机水平方向，每 3 格高度偏移 1 格（阶梯状斜柱），
   柱径 2×2 或 3×3（"很粗"），柱高 8~16，方块用 `QUARTZ_PILLAR` 主体 + `QUARTZ_BLOCK` 柱顶；
   生成条件：起点在洞底实心上方且柱体路径全部为空气
2. `WorldGenGlowstoneCeiling`——洞顶萤石：在 y 68~83 区间找实心方块，在其下表面铺 `GLOWSTONE`（1~3 格成簇），
   为原始洞穴提供顶部照明

**buildSurface 标准实现**（仿 `NetherGenerationUtils.buildSurfaceAndSoulSandGravel`）：
```
从世界高度向下扫，遇到"填充方块"（石头）时：
  顶部第 1 格 → topBlock
  下面 3 格（深度 buffer/3+3 随机）→ fillerBlock
  遇到水/岩浆 → 跳过（流体层不替换）
```

**洞窟骨架约束保留**（所有群系）：y ≥ 84 实心洞顶、y ≤ 8 基岩底座——这是维度级规则，与群系无关。

**buildSurface 标准实现**（仿 `NetherGenerationUtils.buildSurfaceAndSoulSandGravel`）：
```
从世界高度向下扫，遇到"填充方块"（石头）时：
  顶部第 1 格 → topBlock
  下面 3 格（深度 buffer/3+3 随机）→ fillerBlock
  遇到水/岩浆 → 跳过（流体层不替换）
```

---

## 5. 实现阶段

| 阶段 | 内容 | 验收 |
|---|---|---|
| **Phase 1** | `IUndergroundBiome` 接口 + `BiomeProviderUnderground`（GenLayer）+ 5 个新群系类 + 注册；generator 加 buildSurface 逐格分发 + populate 群系接管 + populateWithVanilla 抽取；流体分支（岩浆/河道） | 地下世界环游：5 种风格分区、地形连续无断层、洞窟景观与现状一致 |
| **Phase 2** | 群系氛围（雾色/音乐）、钟乳石群柱高变化、岩浆洞穴灵魂沙边缘渐变 | 氛围完整 |
| **Phase 3** | 水晶晶簇方块（自发光装饰方块替换矿石占位）、群系子/边缘群系（INetherAPIBiomeProvider 式） | 完整群系体验 |

## 6. 涉及文件

**新增**：
- `dimension/biome/IUndergroundBiome.java`（群系接口）
- `dimension/biome/BiomeProviderUnderground.java`（GenLayer 群系提供器）
- `dimension/biome/gen/GenLayerUndergroundBiomes.java`（群系分布层）
- `dimension/biome/biomes/POBiomeUndergroundStyle.java`（参数化群系，implements IUndergroundBiome）
- `dimension/biome/UndergroundBiomes.java`（6 群系工厂：表面/流体类型/装饰组/刷怪表）

**修改**：
- `dimension/biome/POBiomeHandler.java`（注册 5 个新群系）
- `dimension/dims/UndergroundWorlds.java`（BiomeProviderSingle → BiomeProviderUnderground）
- `dimension/worldgen/ChunkGenerator/ChunkGeneratorUndergroundWorld.java`
  （biome 数组提前取 + buildSurfaces 逐格分发 + populate 群系接管 + populateWithVanilla 抽取 + 流体分支）
- `dimension/biome/biomes/POBiomeUnderground.java`（实现 IUndergroundBiome，默认行为 = 现状）
- lang 文件（6 群系名）

**复用不动**：`worldgen/feature/*` 全部装饰类、`MapGenUndergroundBridge`/`MapGenMineshaft`、`WorldEngineNoise`、洞顶/基底骨架逻辑。

## 7. 风险与注意点

1. **无地形级群系参与**（v2 定稿）：地形形状、高度、流体全部维度级统一，群系只有表面与装饰——景观零破坏
2. **装饰生成器注意**：`WorldGenSlantedPillar`（斜柱）与 `WorldGenGlowstoneCeiling`（萤石）是唯二新增生成器，
   生成路径必须全部为空气（斜柱阶梯逐格检查），否则会嵌进地形
3. **GenLayer 与 BiomeCache**：`getBiomes` 三个重载都要处理（Nether-API 的处理方式可直接参考）
4. **旧存档**：群系分布变化导致新区块与旧区块边界突变——新维度影响小
5. **接口即注册**：generator 只认接口，新群系无需改 generator——这是 Nether-API 的核心设计，务必保持
6. **岩浆洞穴的岩浆池**：装饰级 `WorldGenFluidPool(LAVA)` 会替换低洼处的水——视觉上"岩浆池"，不需要流体分支
