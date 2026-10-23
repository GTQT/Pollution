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
- POConfig.DragonSettings 保存孵化开关、孵化器唤醒时间、生息座消耗、时钟座消耗与加速上限、末地开关、阶段时长和基础体型。
- ModelElemental、RenderTcSlime 和 EntityElemental 分别作为现有模型、缩放渲染和 EntityDataManager 风格的参考。
- MeowmelNetwork 保留为扭曲特效通道，不混入龙消息。

## 4. 孵化与交互

当前通过“生息龙蛋孵化器”启动孵化，详见 [机器设计与搭建说明](dragon-egg-incubator.zh.md)。把原版龙蛋放在花心，机器通过星宿运转核心接入星流方尖碑网络，默认每推进一步消耗 128 CE 生息座能量，完成 1,200 步唤醒后原地生成绑定该孵化器的蛋实体。时钟座可将唤醒与蛋阶段推进加速至默认最高 4 倍，每个额外步骤同时消耗 128 CE 生息座和 64 CE 时钟座能量。已移除右键原版龙蛋启动孵化的处理方法及 `sneakToHatch` 配置；普通龙蛋保留原版交互。`allowHatchingInEnd` 默认为 false。若 DragonMounts 已加载，孵化器暂停启动以避免两个模组争用龙蛋。

机器在服务端检查成型状态、运行开关、配置、维度、花心原版龙蛋、3×3×3 空间和星流能量余额。生成实体成功后才移除原蛋；生成失败保留原蛋，移除失败则撤销新实体。孵化蛋是 EGG 阶段的 EntityPollutionDragon，唤醒后从零开始累计最低孵化时间，达到最低时间后每个付费孵化步骤按 1/1200 随机破壳。供能不足、停机或结构失效时，绑定蛋暂停计时；破壳后恢复自然成长并停止扣费；附近满级血肉树核心会使自然孵化与幼龙阶段成长加快，默认半径 32 格、倍率 2，多个核心不叠加。旧存档中未绑定孵化器的蛋保留原有自然孵化行为，并可受该光环加速。

蛋进度达到 75% 显示第一层裂纹，达到 90% 显示更明显裂纹并摇晃；摇晃参数通过专用消息通知附近跟踪客户端。破壳后同一实体转为 HATCHLING。

潜行空手右键已成长的 Pollution 龙现在提示使用“血腥星光收容器”，不再把龙重置为原版龙蛋。手持空收容器右键龙可保存完整成长和实体数据，潜行右键地面释放；收容期间暂停成长。操作与配方见 [收容器说明](bloody-starlight-containment.zh.md)。MetaTileEntityMagicEnergyAbsorber 仍可读取未启动的原版龙蛋；启动孵化后方块被实体替代，吸收器失去该方块效果。

## 5. 成长、存档与碰撞

| 阶段 | 默认时长 | 缩放范围 |
| --- | ---: | ---: |
| EGG | 最低 24,000 tick，之后随机破壳 | 蛋模型 |
| HATCHLING | 48,000 tick | 0.04 → 0.09 |
| INFANT | 24,000 tick | 0.10 → 0.18 |
| FLEDGLING | 32,000 tick | 0.19 → 0.60 |
| JUVENILE | 60,000 tick | 0.61 → 0.99 |
| ADULT | 持续 | 1.00 |

阶段进度由服务端控制，阶段内部线性插值。阶段、阶段已用时间、阶段时长、外观、基础倍率、裂纹与摇晃状态写入 NBT；阶段、进度检查点、阶段时长、外观和基础倍率通过 EntityDataManager 同步。进度检查点每 20 tick 更新，客户端在检查点间本地推进普通成长的显示进度，但不能决定破壳或阶段切换。绑定孵化器的蛋额外保存控制器位置，并同步托管标记；客户端不自行预测其付费孵化进度。

模型使用整体缩放；实体尺寸、眼高、步高和阴影随成长更新。阶段时长在进入阶段时确定并保存，配置变化不会重置现有实体的进度。

## 6. 模型和动画

迁入 COMPAT 几何、模型部件、UV 工厂和 female/male/rare 的 body 与 glow 贴图。蛋阶段使用原版龙蛋方块模型并叠加摇晃与程序化裂纹；其他阶段使用同一末影龙模型。发光层使用对应 glow 贴图和全亮度。

动画每帧根据实体 tick、行走输入和观察角重算，结果写入 DragonModel 持有的可复用临时动画器；每次完整覆盖姿态字段，不保留实体引用或累积上一次姿态。这样既避免每次绘制分配分段对象，也让多只龙共用渲染器时不会继承彼此动画。躯干保留原有长度并收窄、压低截面，背脊沿躯干连续排列，以减轻等粗方箱感。动画支持头颈、尾部、翅膀、腿部的待机、观察和行走动作；飞行控制仍不在首轮范围。

## 7. 本次实现文件

- 公共实体与阶段：src/main/java/meowmel/pollution/common/entity/dragon/EntityPollutionDragon.java、DragonLifeStage.java。
- 孵化转换：src/main/java/meowmel/pollution/common/entity/dragon/DragonEggHatching.java；已成长龙的回收：DragonEggInteractionHandler.java。
- 花形孵化器：src/main/java/meowmel/pollution/common/metatileentity/multiblock/bot/ 下的 MetaTileEntityDragonEggIncubator、DragonEggAwakening、DragonIncubatorShape。
- 摇晃网络：src/main/java/meowmel/pollution/common/entity/dragon/net/。
- 客户端渲染：src/main/java/meowmel/pollution/client/entity/dragon/。
- 外观资源：src/main/resources/assets/pollution/textures/entity/dragon/ender/{female,male,rare}/{body,glow}.png。
- 集中注册：PoEntitiesRegistry、Pollution、POConfig 以及 assets/pollution/lang 下的中英文语言文件。

## 8. 实施状态与偏差

首轮代码已落地：实体和注册、六阶段计时与缩放、蛋转换与回收、静止和水下呼吸、裂纹/摇晃/破壳、外观同步、COMPAT 模型、发光层、配置、阶段名称和来源说明均已加入。阶段名称随成长状态显示；玩家设置的自定义名称优先保留。

实体使用 EntityCreature，而非 EntityTameable，避免首轮意外带入驯服、坐下和繁殖语义。动画直接由实体状态计算，没有独立的客户端 tick 缓存。当前仅实现基本游走、观察和待机，飞行、战斗、骑乘和繁殖仍不在首轮范围。

本机忽略目录 `libs/` 已换成可与当前源码 API 对齐的本地构建依赖；正式 `compileJava` 和 `build` 均通过，构建包含 Java 8 字节码、资源处理、重混淆 jar 和 sources jar。GregTech、GTQTCore 及 GFO 的依赖包在同级工程/临时源码副本中重建，没有把这些依赖源码改动写入 Pollution。本地依赖包不纳入版本控制。

运行时检查发现并修复一个既有包名大小写错误：世界生成器源码原来声明 `mapgen`，但仓库目录与打包路径是 `mapGen`，导致 `POStructureManager` 在 Pollution 预初始化阶段找不到内部类。现在包名和引用统一为 `mapGen`，全量编译与正式打包通过。

2026-10-21 用户已修复运行依赖问题，当前日志确认客户端能够进入世界。随后右键孵蛋触发 `EntityPollutionDragon.applyEntityAttributes` 空指针：`EntityCreature` 默认不注册 `ATTACK_DAMAGE`，原实现直接读取并赋值，导致蛋实体构造失败，原版龙蛋方块仍留在世界。现已在赋值前显式注册该属性；实际属性初始化方法的独立烟测通过，正式 `build` 通过。完整右键转换和后续破壳仍待重启客户端后复测。

Pollution 源码没有直接引用 GTSteam 类；当前本机配置已恢复其 Gradle 依赖，以满足 GTQTCore 对 GTSteam 类的运行时引用。本次孵蛋修复沿用用户已能启动的依赖配置。游戏内孵化、模型动画、存档重载和网络同步仍需继续验证。

阶段时长、阶段顺序、缩放端点、成年终态和非法阶段 ID 回退的烟测通过；正式构建也已重新执行成功。这些检查不能代替尚未完成的客户端与服务器运行验收。

## 9. 验收清单

### 2026-10-22 孵化与成长代码复查

本轮确认并修复以下问题：

| 问题 | 原因与处理 |
| --- | --- |
| 非蛋阶段读档后成长进度归零 | 服务端修改阶段数据时触发同步回调，覆盖刚读取的计时。现在服务端计时独立保存；回归程序曾复现 `INFANT` 的 12,345 tick 读回为 0，修复后所有阶段均能恢复并继续计时。 |
| 客户端重新跟踪后体型每秒跳变 | 原实现只取本地计时与服务端检查点的最大值，没有从检查点继续推进。现在收到阶段或检查点时校准本地计时，最多预测 20 tick，并兼容同步条目的不同顺序。阶段切换仍由服务端决定。 |
| 成长时坐标偏移、碰撞箱偏心 | 原版 `Entity.setSize` 在增大尺寸时带有移动逻辑。改为保留坐标、按当前中心更新碰撞箱。 |
| 破壳粒子未广播给玩家 | 改用 `WorldServer.spawnParticle` 的广播重载。 |
| 已处理的右键仍可能继续其他交互 | 收容交互显式返回 `SUCCESS`，拒绝死亡目标和已满物品；空手回蛋已改成提示，避免误删成长数据。 |
| 死亡过程中继续成长；旧摇晃被重新播放 | 死亡实体停止生命周期计时，服务端清除到期摇晃数据；末地禁止孵化时客户端也停止预测蛋进度。 |

新增可重复执行的回归入口，无需新增测试框架：

```powershell
.\gradlew.bat -I scripts/dragon-lifecycle/smoke.init.gradle dragonLifecycleSmoke build --console=plain
```

本轮该命令通过。检查调用真实实体的存档、生命周期与数据同步方法，覆盖六阶段存档恢复、阶段时长保留、最早破壳边界、随机等待、75%/90% 裂纹、各成长阶段切换、成年终态、客户端禁止自行切换、双端碰撞箱居中、末地暂停、死亡停止，以及恢复龙蛋时的重复请求和放置失败保护。

回归使用内存世界，替代声音、摇晃发包与方块存储，并模拟 Forge 对可取消交互事件的启动转换。它未启动完整游戏，不能替代真实方块右键、实体跟踪网络、破壳粒子和模型画面的联机验收。下列游戏内检查项保持未勾选。

上述复查记录对应原右键方案；随后入口已按用户要求迁移至花形孵化器，并改用星流网络供能。唤醒后的蛋至少需要推进 24,000 个付费孵化步骤：正常 20 TPS、持续供能时，1 倍速最低 20 分钟，默认最高 4 倍速最低 5 分钟。之后每步以 1/1200 概率破壳，没有固定最晚时刻。供能中断、停机、区块卸载或服务器停止时不会累计托管蛋进度。破壳后到成年需 164,000 个有效成长 tick；正常速度约 2 小时 16 分 40 秒，持续处于满级血肉树范围内约 1 小时 8 分 20 秒。时钟座只加速孵化器付费步骤；满级血肉树核心可独立加速自然蛋孵化和破壳后的幼龙成长。

最新验证命令为 `.\gradlew.bat -I scripts/dragon-lifecycle/smoke.init.gradle dragonIncubatorSmoke dragonLifecycleSmoke build --console=plain`，两项回归和完整构建均通过。新增检查覆盖星座原子扣费、缺能暂停、时钟座 4 倍与满级血肉树 2 倍叠加、能量仍按付费步骤结算、范围和满级判定、自然龙成长加速、孵化器绑定存档、客户端停止免费推进及破壳后停止机器扣费；真实网络联动、成型与光效仍待游戏内验收。

- [x] 使用当前对齐的本地依赖执行 compileJava / build 成功。
- [x] 客户端可完整加载并进入世界；用户已修复本机运行依赖问题。
- [ ] 专用服务器可完整加载；此前测试使用的本地依赖包 ABI 不匹配，需使用当前兼容配置重测。
- [ ] 专用服务端可加载实体，不触发客户端类加载错误。
- [ ] 孵化器真实成型、花心放蛋、生息座供给与时钟座/血肉树加速叠加、维度限制和生成失败保护；原版龙蛋右键不会启动 Pollution 孵化。
- [ ] 蛋摇晃、裂纹、破壳和六阶段顺序及缩放正确，客户端不会提前破壳。
- [ ] 多只龙同时渲染、不同外观、跟踪范围进出、保存重载时状态正确。
- [ ] 模型、发光层、碰撞箱、眼高、步高和阴影随体型一致。
- [ ] 吸收器与未启动原版龙蛋、DragonMounts 同装的兼容行为正确。
- [ ] 中英文名称、交互和阶段名称显示正确。
