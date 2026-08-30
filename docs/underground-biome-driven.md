# 地下世界群系驱动改造预研报告

> 状态：预研（未实现）
> 目标：把地下世界从单一群系（`BiomeProviderSingle`）改为多群系噪声驱动，
> 实现钟乳石石林、蘑菇森林、水晶森林、熔岩池、地下暗河等地形分区。

---

## 1. 现状分析

### 1.1 当前生成链路

| 环节 | 现状 | 文件 |
|---|---|---|
| 群系提供器 | `BiomeProviderSingle(UNDERGROUND_BIOME)` — 全维度单一群系 | `dimension/dims/UndergroundWorlds.java:24` |
| 群系类 | `POBiomeUnderground`：`BiomeHellDecorator` + 6 种元素史莱姆生成表 | `dimension/biome/biomes/POBiomeUnderground.java` |
| 地形 | 单套噪声插值（高度噪声 + 海平面 63 水填充 + 10 层随机岩石变种） | `ChunkGeneratorUndergroundWorld.prepareHeights/buildSurfaces` |
| 装饰 | 硬编码混合：每 chunk 随机 7 选 1（草/钟乳石/树叶/水池/岩浆池/焦油池/花园）+ 蘑菇灯集群 + 沙砾 + 死水 | `ChunkGeneratorUndergroundWorld.populate` |
| 结构 | 地下堡垒 / 废弃矿井（每 chunk 二选一） | `MapGenUndergroundBridge` / `MapGenMineshaft` |

### 1.2 可复用的现成资产（重点）

- **`BiomeProviderAlfheim`**（`dimension/biome/BiomeProviderAlfheim.java`）：
  现成的"一维噪声值 → Profile 区间 → 群系"多群系 provider 模板，
  已处理 BiomeCache 的三重重载（`getBiomesForGeneration` / `getBiomes` / `getBiomes(...,cacheFlag)`），
  直接仿写即可，无需碰 GenLayer。
- **`WorldEngineNoise.perlinNoise2D(seed, x, z, profile)`**（`dimension/worldgen/WorldEngineNoise.java`）：
  群系分布噪声、暗河河道噪声、钟乳石柱噪声都能复用。
- **`POBiomeHandler.registerBiomes`**：现成的 `RegistryEvent.Register<Biome>` 注册点，加 5 个群系即可。
- **现有 8 个装饰 WorldGen 类**（`dimension/worldgen/mapGen/`）：
  `WorldGenStalactite`（钟乳石）、`WorldGenMushroom`（单蘑菇）、`WorldGenOreStone1/2`（蘑菇灯集群）、
  `WorldGenGarden`、`WorldGenSingle`（单方块）、`WorldGenFluidPool`（水池/岩浆池/焦油池）、
  `WorldGenUndergroundWater`（静水/活水）——群系装饰直接按组复用。
- **`AlfheimBiomes`** 工厂模式：参数化 `biome(name, min, max, ..., top, filler, decoration)` 静态工厂。

### 1.3 关键机制认知（决定方案可行性）

1. **`Biome.genTerrainBlocks` 只能改地表 1~4 层**，无法改变大尺度地形。
   因此"钟乳石柱状地形、暗河河谷、熔岩低地"这类**地形差异必须在 chunk generator 里按群系分支**，
   不能只靠 Biome 类实现。
2. **群系数组在 `generateChunk` 中已经取得**（`getBiomes(null, x*16, z*16, 16, 16)`），
   可在 `prepareHeights` 前获取并传入，按群系选噪声参数/地形模式。
3. `Biome.decorate`（装饰）由 `populate` 阶段调用——把装饰按群系分组是 Biomes 层就能做的事。
4. 群系分布噪声与地形噪声共用一个种子（构造器传入 world seed），保证确定性和多端一致。

---

## 2. 目标架构

```
UndergroundWorlds (WorldProvider)
  └─ BiomeProviderUnderground          ← 新：仿 BiomeProviderAlfheim
       └─ 一维噪声 → 6 个群系 Profile 区间
  └─ ChunkGeneratorUndergroundWorld    ← 改
       ├─ prepareHeights：按 chunk 群系地形模式选噪声参数（钟乳石柱/河谷/低地/标准）
       ├─ buildSurfaces：按群系选地表/填充方块（菌丝/沙砾/石英岩…）
       ├─ populate：按群系选装饰器组（BiomeDecorator 或手写 switch）
       └─ 结构生成保持不变（堡垒/矿井全群系通用，避开暗河河道即可）
```

### 2.1 群系列表（6 个，含默认）

| 群系 | 噪声区间 | 地表/填充 | 地形模式 | 装饰组 | 生物 |
|---|---|---|---|---|---|
| **深窟基础**（保留现 `POBiomeUnderground`） | 兜底 | 石头/岩石变种 | 标准 | 现有混合（7选1） | 6 种元素史莱姆 |
| **钟乳石石林** | 区间 A | 石头 + 石灰岩填充 | **柱状**：垂直噪声控制柱高，列间挖空 | 钟乳石密集 + 石笋 + 沙砾 | 史莱姆（土/火） |
| **蘑菇森林** | 区间 B | 菌丝 + 泥土填充 | 标准/微丘 | 红/棕蘑菇集群 ×4、蘑菇灯、蘑菇树（可选） | 史莱姆（土/水）+ 蝙蝠 |
| **水晶森林** | 区间 C | 石英岩/蓝片岩填充 | 标准 | 晶簇装饰（见 §4 方块决策）+ 水池 | 史莱姆（风/火） |
| **熔岩盆地** | 区间 D | 黑石/焦油 + 岩浆池 | **低地**：高度噪声整体压低，海平面以下 | 岩浆池密集、焦油池、玄武岩柱 | 史莱姆（火） |
| **地下暗河** | 区间 E | 沙砾 + 黏土 | **河谷**：河流噪声挖槽，水填充至 8-12 格深 | 水潭、黏土、甘蔗（可选） | 史莱姆（水） |

> 注意：岩浆/水是**地形级**生成（prepareHeights 阶段按噪声填充），不是装饰级，
> 否则流体悬挂在半空。

### 2.1.x 各群系详细规格

以下参数可直接作为实现规格。装饰频率以现有 `populate` 的调用量级为基准（现有：水池×8、死水×16、沙砾×4、蘑菇灯×10+随机、洞穴装饰 7 选 1×1~10）。

---

#### ① 深窟基础（Deep Cave Basic）—— 兜底群系

| 项 | 规格 |
|---|---|
| 注册名 | `pollution:underground_deep_cave`（沿用现有 `pollution_biome.2` 亦可，建议换语义化新名） |
| Provider 区间 | 兜底（噪声值不落入任何其它区间时） |
| BiomeProperties | 沿用现有：水色 `0xADD8E6`、温度 0.5、`BiomeHellDecorator` |
| 地形模式 | **标准**（现有 `prepareHeights` 全参数不动） |
| 表面/填充 | 石头 + 10 层随机岩石变种（现有逻辑） |
| 装饰 | 现有混合组（7 选 1 ×1~10 + 蘑菇灯 + 沙砾 + 死水） |
| 生物 | 6 种元素史莱姆（权重 2，1~2 只） |
| 备注 | 完全保留现状，作为新群系的对照基准 |

---

#### ② 钟乳石石林（Stalactite Pillar）

| 项 | 规格 |
|---|---|
| 注册名 | `pollution:stalactite_pillar`；字典：`MAGICAL, HILLS, SPOOKY` |
| Provider 区间 | 噪声值落在 `(0.30, 0.55]`（一维 WorldEngineNoise，SCALE_X=2000 左右，比 Alfheim 的 8000 密，形成中等大小分区） |
| BiomeProperties | 水色 `0x3A6EA5`（岩洞蓝）、温度 0.2、湿度 0.4 |
| 地形模式 | **柱状柱廊**（新） |
| 表面/填充 | 填充 = 石灰岩（`LIMESTONE`）；表面 = 石头（柱顶） |
| 装饰 | 钟乳石 `WorldGenStalactite` ×16（现有 ×1~10 区间内偏密）、沙砾 `WorldGenMinable(GRAVEL,33)` ×6、水池 `WorldGenFluidPool(WATER)` ×4 |
| 生物 | 土/火元素史莱姆（权重 3）、蝙蝠（权重 10，洞窟氛围） |
| 特殊 | 柱间空洞区域刷怪密度翻倍（自然形成"洞穴群系"） |

**柱状地形实现（新 `prepareHeights` 变体，遵守洞顶原则）**：
```
对每个 (x, z) 列：
    洞顶岩层：y ≥ 84 实心石头（世界级规则，不变）
    pillar = perlin(x/64, z/64)          // 低频列噪声，决定柱子分布
    if pillar > 0.35:                     // 柱子区域
        height = 20 + perlin(x/32, z/32) * 25   // 柱体长度 20~45
        柱身：从 y = 84 - height 到 y = 84 实心石头   // 钟乳石悬挂在洞顶下
        柱基：若 perlin(x/16, z/16) > 0.5（约 1/3 概率），柱身延伸至 y=8 连地成石柱
    else:
        只保留 y<8 的基岩底座，其余为空洞（无柱区形成大厅）
```
柱子生成后再叠标准洞穴（`MapGenCavesUnderground` 照常跑，给柱子掏洞增加层次）。

---

#### ③ 蘑菇森林（Mycelium Forest）

| 项 | 规格 |
|---|---|
| 注册名 | `pollution:mushroom_forest`；字典：`MAGICAL, FOREST, DENSE, LUSH` |
| Provider 区间 | `(-0.15, 0.30]`（最大区间之一，蘑菇森林是主景观） |
| BiomeProperties | 水色 `0x6A5ACD`（紫调）、温度 0.6、湿度 0.9 |
| 地形模式 | **微丘**：标准高度噪声 + 低频起伏叠加 `±6` 格 |
| 表面/填充 | 表面 = 菌丝（`Blocks.MYCELIUM`）；填充 = 泥土 |
| 装饰 | 蘑菇灯柱 `WorldGenOreStone1/2` ×6+6（现有 10+随机，森林中加密）、单蘑菇 `WorldGenMushroom(RED/BROWN)` ×24、巨蘑菇 `WorldGenBigMushroom`（原版类）×8、水池 ×3 |
| 生物 | 土/水元素史莱姆（权重 4，森林主场）、蝙蝠（权重 8） |
| 特殊 | 菌丝替代石头出现在表层（`buildSurfaces` 按群系分支：蘑菇森林表层 1~3 格换菌丝/泥土，下层仍石头） |

---

#### ④ 水晶森林（Crystal Cavern）

| 项 | 规格 |
|---|---|
| 注册名 | `pollution:crystal_cavern`；字典：`MAGICAL, SPOOKY, DRY` |
| Provider 区间 | `(0.55, 0.80]` |
| BiomeProperties | 水色 `0x00FFCC`（荧光青）、温度 0.3、湿度 0.2 |
| 地形模式 | **标准**（二期可加"晶洞"变体：球形空洞 + 内壁晶簇） |
| 表面/填充 | 填充 = 石英岩（`QUARTZITE`）/ 蓝片岩（`BLUE_SCHIST`）交替；表面 = 石英岩 |
| 装饰 | **晶簇占位（Phase 1）**：`WorldGenMinable(QUARTZ_ORE, 8)` ×20 + `WorldGenMinable(DIAMOND_ORE, 4)` ×4 + `WorldGenMinable(LAPIS_ORE, 6)` ×8（密集点缀）；**晶簇正式（Phase 3）**：新晶簇方块替换占位；水池 ×4（晶洞湖） |
| 生物 | 风/火元素史莱姆（权重 2）、（可选）守卫者系水下 |
| 特殊 | 荧光氛围：Phase 3 晶簇方块自发光（lightValue 7~11），天然照明；顶部岩层减少（空洞感更强） |

---

#### ⑤ 熔岩盆地（Lava Basin）

| 项 | 规格 |
|---|---|
| 注册名 | `pollution:lava_basin`；字典：`MAGICAL, NETHER, HOT, DRY` |
| Provider 区间 | `(-0.45, -0.15]` |
| BiomeProperties | 水色（岩浆色 `0xFF4500`）、温度 2.0（炎热）、湿度 0.0、`setRainDisabled` |
| 地形模式 | **低地**（新）：**洞顶保持不变**（遵守洞顶原则），洞窟底部区域（y≈8~25）填**岩浆**（代替水），地面噪声仅局部下移形成洼地 |
| 表面/填充 | 表面 = 黑曜石/石头随机；填充 = 石头 + 焦油（`PURE_TAR` 液体方块） |
| 装饰 | 岩浆池 `WorldGenFluidPool(LAVA)` ×12（盆地主场，现有 ×~3 密度翻 4 倍）、焦油池 ×6、沙砾 ×2 |
| 生物 | 火元素史莱姆（权重 5，主场）、烈焰人系（可选）、苦力怕（黑暗火区） |
| 特殊 | 岩浆**在地形阶段**填充（避免悬挂）；地表岩浆湖是地形一部分（噪声值 < 阈值 → 直接填岩浆到 y=8） |

---

#### ⑥ 地下暗河（Underground River）

| 项 | 规格 |
|---|---|
| 注册名 | `pollution:underground_river`；字典：`MAGICAL, RIVER, WET` |
| Provider 区间 | 河流噪声专用：河流噪声绝对值 `小于 0.06`（低频 perlin，频率 0.0015，周期约 700 格）——**与其它群系的分区噪声独立**，河流噪声叠加在所有群系之上 |
| BiomeProperties | 水色 `0x0033FF`（深水蓝）、温度 0.5、湿度 0.9 |
| 地形模式 | **河谷**（新）：标准地形生成后，河道处挖至 y=10 并填水至 y=8 |
| 表面/填充 | 河床 = 沙砾（`GRAVEL`）+ 黏土（`CLAY`）各半；两岸 = 原群系地表 |
| 装饰 | 水潭 `WorldGenUndergroundWater(insideRock=true)` ×8、黏土 `WorldGenMinable(CLAY, 16)` ×6、（可选）甘蔗沿河岸 ×4 |
| 生物 | 水元素史莱姆（权重 5，主场）、河岸蝙蝠 |
| 特殊 | 河道宽度 7~11 格，边缘渐变（`riverNoise` 的平方作深度系数）；暗河是**跨越多个群系的线状特征**——建议作为"叠加地形"实现（先群系分区，再切河道），而非独立群系，这样蘑菇森林/石林里也能有暗河流过 |

---

#### Provider 区间总表（一维噪声，SCALE_X≈2000）

```
      噪声值        -0.45    -0.15    0.30    0.55    0.80
                   ┌─────────┬────────┬───────┬───────┬─────────┐
     熔岩盆地      │  ⑤      │        │       │       │         │
     蘑菇森林      │         │  ③     │       │       │         │
     钟乳石石林    │         │        │  ②    │       │         │
     水晶森林      │         │        │       │   ④  │         │
     深窟基础      │         │        │       │       │   兜底   │
     地下暗河      └─────────┴────────┴───────┴───────┴─────────┘
                   （河流噪声 |v|<0.06 时叠加覆盖河道）
```

### 2.2 群系类设计（推荐：一个参数化基类 + 静态工厂）

参照 `AlfheimBiome` 模式，新增 `POBiomeUndergroundX` 基类，字段：
`topBlock / fillerBlock / terrainMode (enum) / decorator 列表`，
6 个静态实例用工厂方法构建，避免 6 个几乎相同的类文件。
注册进 `POBiomeHandler`（registryName 沿用 `pollution_biome.4~8` 风格）。

### 2.3 BiomeProviderUnderground

仿 `BiomeProviderAlfheim` 抄写即可（约 130 行）：
- `PROFILES` 数组：6 个群系 × (min, max) 区间，一维噪声值落入哪个区间即哪个群系
- `getBiomesToSpawnIn` 返回深窟基础群系（禁止出生在熔岩/暗河里）
- `areBiomesViable` / `findBiomePosition` 抄 Alfheim 版
- 需要处理群系缓存：默认 BiomeProvider 内部有 `BiomeCache`，但 Alfheim 版绕开了——
  地下世界建议直接用 `BiomeCache`（`new BiomeCache(this)`）或照抄 Alfheim 的纯函数方案（简单、一致）。

---

## 2.x 洞窟骨架保持原则（关键约束）

群系化**不改变地下世界的整体洞窟形态**，风格差异只在洞窟骨架内部做。

**共享骨架（所有群系一致，任何地形模式不得突破）**：

```
洞顶岩层：y ≥ 84 统一实心石头        ← 世界级规则，保证永远是"洞窟"而不是露天
洞窟空间：y = 8 ~ 84（群系风格区）    ← 柱子/岩浆/河道/标准孔洞都在此层内变化
基底岩层：y ≤ 8（基岩/底座）         ← 现有
```

**每个群系只是对洞窟空间的"重新装修"**：

| 群系 | 洞窟骨架 | 风格化操作 |
|---|---|---|
| 深窟基础 | 现有逻辑 | 无 |
| 蘑菇森林 | 现有逻辑 | 表层换菌丝/泥土 + 装饰 |
| 水晶森林 | 现有逻辑 | 填充换石英岩 + 晶簇装饰 |
| 熔岩盆地 | 现有逻辑 | 洞窟底部流体=岩浆（不是压低到露天） |
| 钟乳石石林 | 孔洞区重排 | 柱子从洞底长到洞顶下方（20~45 格悬垂），洞顶保留 |
| 地下暗河 | 现有逻辑 | 洞窟底部挖河谷（y=10 水） |

**约束检查清单（实现时逐项验证）**：
1. 任何模式下 y ≥ 84 必须是实心岩层（杜绝"露天"）
2. 任何模式下 y ≤ 8 必须是基岩底座（杜绝"挖穿"）
3. 石林柱体只占据洞窟空间，柱顶 ≤ 84 且不穿透洞顶
4. 熔岩盆地不压低洞顶——只改变洞窟底部的填充物
5. 暗河河道深度 ≤ 洞窟底部，不与基底岩层冲突

---

## 3. 地形驱动方案（核心）

### 方案 A（推荐）：ChunkGenerator 内按群系分支

`generateChunk` 中先取 biome 数组 → 按**该 chunk 占多数的群系**选地形模式：

| 地形模式 | 实现 |
|---|---|
| 标准 | 现有 `prepareHeights` 逻辑（不动） |
| 柱状（钟乳石石林） | 替换高度噪声：`height = f(perlin(x,z))` 生成实心柱体，柱间为空洞；柱子位置由列噪声决定，柱高 10~40 格 |
| 河谷（暗河） | 高度噪声整体生成后，用河流噪声（低频 perlin，周期约 500~800 格）在河道处把地面挖到 y≈8~12 并填水；河道边缘渐变（河道噪声的平方平滑） |
| 低地（熔岩盆地） | 洞顶不变，洞窟底部（y≈8~25）填岩浆（代替水），地面局部下移成洼地 |
| 微丘（蘑菇森林） | 标准模式 + 高度噪声叠加 ±6 格低频起伏 |

关键点：
- 每种模式是独立的 `prepareHeights` 变体（或提取公共插值骨架，模式只提供噪声参数表）
- 岩浆/水在**地形阶段**填充，避免悬挂流体
- chunk 边界处两种模式的交界会产生台阶——用群系噪声做 1~2 格高度混合（可选，二期优化）

### 方案 B（最低成本，先跑通）：纯装饰驱动

地形完全不变，只在 `populate` 阶段按 biome 分发装饰器组。
优点：改动最小（Biome + Provider + populate 分支），一周内可见效果；
缺点：暗河/熔岩盆地没有地形配合，只能做成"水池密集区"，观感打折。
**建议先做 B 验证群系分布与装饰分组，再做 A 的地形分支。**

### 地形模式与结构生成的兼容

- 地下堡垒/废弃矿井在柱状/河谷地形中会悬空——生成结构前检查地形模式，
  河谷与柱状群系中跳过堡垒（或只保留矿井），熔岩盆地中堡垒照常生成（岩浆填充其底部是自然景观）。
- `recreateStructures` / `getNearestStructurePos` 逻辑不变。

---

## 4. 水晶森林方块决策（需拍板）

水晶森林的地表与晶簇方块来源有 4 个选项：

| 选项 | 实现 | 成本 | 观感 |
|---|---|---|---|
| **A. 复用原版矿石块**（石英矿/钻石矿/青金石矿密集生成） | 零新资产，`WorldGenMinable` 即用 | 低 | 一般，与原版雷同 |
| **B. 新增简单晶簇装饰方块**（非 TileEntity，像石英晶簇） | 仿 `WorldGenSingle` 模式 + 一个新 Block + meta 注册 | 中 | 好，可自发光/彩色 |
| **C. 复用星座水晶方块**（`POConstellationCrystal`） | 零新方块，但它是 TileEntity + 多功能方块，每格生成性能差 | 低 | 好但昂贵 |
| **D. 复用 Botania 魔力水晶/GT 水晶物品** | 查 Botania `ModBlocks`/GT 水晶 | 中 | 取决于现有资产 |

**预研推荐**：一期用 **A（原版矿石块占位）** 快速见效，二期做 **B**（新晶簇方块，顺带发光效果，契合"地下水晶洞"）。

---

## 5. 实现阶段规划

| 阶段 | 内容 | 验收 |
|---|---|---|
| **Phase 1** | `BiomeProviderUnderground` + 6 群系类 + 注册；`populate` 按群系分发装饰（方案 B） | `/tp` 地下世界环游可见 5 种风格的装饰分区，群系边界平滑 |
| **Phase 2** | 地形分支（方案 A）：钟乳石柱状地形、暗河河谷+水、熔岩低地+岩浆 | 各群系地形差异明显、无悬挂流体、chunk 边界台阶可接受 |
| **Phase 3** | 水晶方块资产（选项 B）、生物分布细化、群系字典/显示名、平衡调整 | 完整群系体验，可随 mod 发布 |

## 6. 风险与注意点

1. **旧存档**：群系分布改变后旧区块与新区块边界会突变——建议大版本号变化时测试新档。
2. **性能**：biome 数组每 chunk 读取 1 次（已有）；地形分支只增加 1~2 次列噪声计算；
   钟乳石柱状地形注意不要对每格做列噪声（每列 1 次即可）。
3. **Cascading chunk 警告**：暗河/熔岩跨 chunk 填充需检查相邻 chunk 状态（沿用 `fixVanillaCascading` 逻辑）。
4. **群系噪声与地形噪声解耦**：群系分布噪声种子用 `seed * 84` 之类的派生种子（照抄 Alfheim），
   与地形噪声互不干扰，避免"群系分布 = 地形高度"的周期性重叠。
5. **水下/岩浆中的刷怪**：暗河/熔岩盆地调整 spawnable 列表（如熔岩盆地放烈焰人系）。

## 7. 涉及文件清单

**新增**：
- `dimension/biome/BiomeProviderUnderground.java`（仿 BiomeProviderAlfheim）
- `dimension/biome/biomes/POBiomeUndergroundX.java`（参数化群系基类 + 5~6 个静态实例）
- （Phase 2）地形模式枚举 `UndergroundTerrainMode.java` 或并入 chunk generator
- （Phase 3）水晶晶簇方块（`common/block/` + 注册）

**修改**：
- `dimension/biome/POBiomeHandler.java`（注册 5 个新群系）
- `dimension/dims/UndergroundWorlds.java`（`BiomeProviderSingle` → `BiomeProviderUnderground`）
- `dimension/worldgen/ChunkGenerator/ChunkGeneratorUndergroundWorld.java`
  （`prepareHeights` 地形模式分支、`populate` 按群系装饰分发）
- `dimension/worldgen/mapGen/`（装饰类复用，可能微调参数）
- lang 文件（群系显示名）

**复用不动**：`WorldEngineNoise`、`BiomeProviderAlfheim`（作模板）、全部现有 mapGen 装饰类、
`MapGenUndergroundBridge` / `MapGenMineshaft` 结构生成。
