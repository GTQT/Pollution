# DragonMounts2 模型与贴图来源说明

本次迁入的末影龙模型几何、UV 工厂、模型部件辅助代码和 female/male/rare 贴图源自 [DragonMounts2-Expanded](https://github.com/DragonMounts-Team/DragonMounts2-Expanded)，基准提交为 `eff2a0197facb5f7523a71edaad340e33cdb6045`（`dev` 分支）。上游仓库声明采用 GNU GPL v3.0；本项目自身继续遵循仓库根目录的 AGPL-3.0 许可。

迁入并改包名的模型代码包括 `BodyPart`、`BuiltinFactory`、`ComplexSegmentedPart`、`DragonModelContracts`、`HeadPart`、`IModelFactory`、`IModelPart`、`ISegmentedPart`、`LegPart`、`ModelMagic`、`PartSnapshot`、`ScalablePart`、`SimpleSegmentedPart`、`WingPart`，以及 `util/IUVRegistry`、`util/MathX`、`util/Segment`。`DragonModel` 与 `DragonAnimator` 保留上游来源声明并按 Pollution 实体接口重写。

复制的资源位于 `src/main/resources/assets/pollution/textures/entity/dragon/ender/`：

- `female/body.png`、`female/glow.png`
- `male/body.png`、`male/glow.png`
- `rare/body.png`、`rare/glow.png`

原模型 Java 文件中的作者声明与免责声明头已保留。成长实体、孵化事件处理、网络消息、Pollution 渲染器和集成代码为本项目新增代码。上游许可证全文随本说明保存在 `docs/licenses/DragonMounts2-GPL-3.0.txt`。
