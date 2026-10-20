# 末影龙孵化、模型与成长移植方案

## 1. 目标与范围

把 DragonMounts2-Expanded 的原版龙蛋孵化、末影龙模型和成长过程接入 Pollution。首轮包含龙蛋方块转换、蛋阶段摇晃与破壳、六阶段成长、三种末影龙外观、发光层、基本行走和观察动画、实体同步与存档。

首轮不实现飞行控制、骑乘、吐息战斗、繁殖或环境转化；“初飞期”只是成长阶段名称。

## 2. 上游依据与许可

- 上游项目：[DragonMounts2-Expanded](https://github.com/DragonMounts-Team/DragonMounts2-Expanded)
- 阅读分支：dev；基准提交：eff2a0197facb5f7523a71edaad340e33cdb6045。
- 上游代码和资源采用 GPL-3.0。保留迁入模型源文件的作者声明，并记录迁入文件、贴图和提交号。
- 上游设计以同一个服务端实体承载蛋阶段和成龙阶段，破壳只改变阶段；所有成长阶段共用末影龙模型，整体缩放，不切换独立幼龙模型。
- Ender 外观使用 female、male、rare 三组贴图和 BuiltinFactory.COMPAT 几何；模型有 7 节颈、12 节尾，每侧 4 根翼指。

迁入项与许可证记录见 [DragonMounts2 模型与贴图来源说明](dragonmounts2-model-attribution.zh.md)。

## 3. Pollution 接入点

- PoEntitiesRegistry 集中注册实体和客户端渲染器；已有实体数字 ID 到 12，末影龙使用 13。
- Pollution.preInit 注册独立的 pollution:dragon 网络通道；ClientpreInit 注册实体渲染器。
- POConfig.DragonSettings 保存孵化开关、交互要求、末地开关、阶段时长和基础体型。
- ModelElemental、RenderTcSlime 和 EntityElemental 分别作为现有模型、缩放渲染和 EntityDataManager 风格的参考。
- MeowmelNetwork 保留为扭曲特效通道，不混入龙消息。

## 4. 孵化与交互

默认在末地之外空手潜行右键原版龙蛋启动孵化。sneakToHatch 可配置为普通右键；allowHatchingInEnd 默认为 false。若 DragonMounts 已加载，Pollution 不接管原版龙蛋交互。

服务端先检查配置、维度、目标方块、玩家编辑权限、手和持有物，再取消原版龙蛋交互以阻止瞬移。方块移除与实体生成失败回滚。孵化蛋是 EGG 阶段的 EntityPollutionDragon，达到最低时间后每 tick 按 1/1200 随机破壳。

蛋进度达到 75% 显示第一层裂纹，达到 90% 显示更明显裂纹并摇晃；摇晃参数通过专用消息通知附近跟踪客户端。破壳后同一实体转为 HATCHLING。

潜行空手右键已成长的 Pollution 龙，会在可放置位置恢复原版龙蛋并移除实体。该方块重新孵化时从零计时。MetaTileEntityMagicEnergyAbsorber 仍可读取未启动的原版龙蛋；玩家启动孵化后方块被实体替代，吸收器失去该方块效果。

## 5. 成长、存档与碰撞

| 阶段 | 默认时长 | 缩放范围 |
| --- | ---: | ---: |
| EGG | 最低 24,000 tick，之后随机破壳 | 蛋模型 |
| HATCHLING | 48,000 tick | 0.04 → 0.09 |
| INFANT | 24,000 tick | 0.10 → 0.18 |
| FLEDGLING | 32,000 tick | 0.19 → 0.60 |
| JUVENILE | 60,000 tick | 0.61 → 0.99 |
| ADULT | 持续 | 1.00 |

阶段进度由服务端控制，阶段内部线性插值。阶段、阶段已用时间、阶段时长、外观、基础倍率、裂纹与摇晃状态写入 NBT；阶段、进度检查点、阶段时长、外观和基础倍率通过 EntityDataManager 同步。进度检查点每 20 tick 更新，客户端在检查点间本地推进显示进度，但不能决定破壳或阶段切换。

模型使用整体缩放；实体尺寸、眼高、步高和阴影随成长更新。阶段时长在进入阶段时确定并保存，配置变化不会重置现有实体的进度。

## 6. 模型和动画

迁入 COMPAT 几何、模型部件、UV 工厂和 female/male/rare 的 body 与 glow 贴图。蛋阶段使用原版龙蛋方块模型并叠加摇晃与程序化裂纹；其他阶段使用同一末影龙模型。发光层使用对应 glow 贴图和全亮度。

动画每帧根据实体 tick、行走输入和观察角重算，结果写入 DragonModel 持有的可复用临时动画器；每次完整覆盖姿态字段，不保留实体引用或累积上一次姿态。这样既避免每次绘制分配分段对象，也让多只龙共用渲染器时不会继承彼此动画。首轮支持头颈、尾部、翅膀、腿部的待机、观察和行走动作。

## 7. 本次实现文件

- 公共实体与阶段：src/main/java/meowmel/pollution/common/entity/dragon/EntityPollutionDragon.java、DragonLifeStage.java。
- 孵化和回收：src/main/java/meowmel/pollution/common/entity/dragon/DragonEggInteractionHandler.java。
- 摇晃网络：src/main/java/meowmel/pollution/common/entity/dragon/net/。
- 客户端渲染：src/main/java/meowmel/pollution/client/entity/dragon/。
- 外观资源：src/main/resources/assets/pollution/textures/entity/dragon/ender/{female,male,rare}/{body,glow}.png。
- 集中注册：PoEntitiesRegistry、Pollution、POConfig 以及 assets/pollution/lang 下的中英文语言文件。

## 8. 实施状态与偏差

首轮代码已落地：实体和注册、六阶段计时与缩放、蛋转换与回收、静止和水下呼吸、裂纹/摇晃/破壳、外观同步、COMPAT 模型、发光层、配置、阶段名称和来源说明均已加入。阶段名称随成长状态显示；玩家设置的自定义名称优先保留。

实体使用 EntityCreature，而非 EntityTameable，避免首轮意外带入驯服、坐下和繁殖语义。动画直接由实体状态计算，没有独立的客户端 tick 缓存。当前仅实现基本游走、观察和待机，飞行、战斗、骑乘和繁殖仍不在首轮范围。

本机忽略目录 `libs/` 已换成可与当前源码 API 对齐的本地构建依赖；正式 `compileJava` 和 `build` 均通过，构建包含 Java 8 字节码、资源处理、重混淆 jar 和 sources jar。GregTech、GTQTCore 及 GFO 的依赖包在同级工程/临时源码副本中重建，没有把这些依赖源码改动写入 Pollution。本地依赖包不纳入版本控制。

运行时检查发现并修复一个既有包名大小写错误：世界生成器源码原来声明 `mapgen`，但仓库目录与打包路径是 `mapGen`，导致 `POStructureManager` 在 Pollution 预初始化阶段找不到内部类。现在包名和引用统一为 `mapGen`，全量编译与正式打包通过。

运行时验收仍未完成。客户端通过了 Pollution 的注册及资源/模型初始化阶段，但这次没有确认进入主菜单或世界。专用服务器在 GTQT Core 注册机器阶段退出，错误为 `NoSuchFieldError: SUPPLY_OVERLAYS`；这是临时重建的 GTQTCore/GregTech 测试包之间的二进制接口不匹配，测试结束后已恢复临时包和源码。此前也确认本机原始 GTQTCore 的配方代码仍引用 `GTSMetaitems`，而本机 GTSteam jar 又引用当前 GregTech 已移除的 `gregtech.api.pattern.TraceabilityPredicate`。

Pollution 源码没有引用 GTSteam 类，故已按要求从 `build.gradle` 移除其直接依赖；但本机 GTQTCore 的残留引用仍会阻止这套依赖组合完整启动。最终 `build` 可以通过，游戏内孵化、模型动画、存档重载和网络同步仍需在兼容的 GTQTCore/GregTech 运行环境中验证。

阶段时长、阶段顺序、缩放端点、成年终态和非法阶段 ID 回退的烟测通过；正式构建也已重新执行成功。这些检查不能代替尚未完成的客户端与服务器运行验收。

## 9. 验收清单

- [x] 使用当前对齐的本地依赖执行 compileJava / build 成功。
- [ ] 客户端及专用服务器可完整加载；当前客户端没有确认进入主菜单，专用服务器因本地 GTQTCore/GregTech 测试包 ABI 不匹配而退出。
- [ ] 专用服务端可加载实体，不触发客户端类加载错误。
- [ ] 潜行/普通右键配置、主副手、维度和编辑权限行为正确；生成失败不丢蛋也不复制。
- [ ] 蛋摇晃、裂纹、破壳和六阶段顺序及缩放正确，客户端不会提前破壳。
- [ ] 多只龙同时渲染、不同外观、跟踪范围进出、保存重载时状态正确。
- [ ] 模型、发光层、碰撞箱、眼高、步高和阴影随体型一致。
- [ ] 吸收器与未启动原版龙蛋、DragonMounts 同装的兼容行为正确。
- [ ] 中英文名称、交互和阶段名称显示正确。
