package meowmel.pollution.dimension.worldgen;

/**
 * 地下世界的**群系分布**（纯计算，不依赖 Minecraft 类 —— 与 {@link UndergroundRivers} 同理，
 * 这样它可以被 {@code tools/biome-probe} 直接离线复现与标定）。
 *
 * <h2>🔴 为什么以前是"长条"而不是团块（根因）</h2>
 * 旧实现把**一个二维噪声的数值**切成 7 段、再取每段中间的一个窄窗口命中群系：
 * <pre>
 * value = perlinNoise2D(x/SCALE, z/SCALE) * AMPLITUDE
 * slot  = floor((value + AMPLITUDE) / SLOT_SPACING)
 * 命中：|value - 该段中心| &lt;= WINDOW / 2
 * </pre>
 * 但"一个平滑二维场的数值落在某窄区间内"解出来的，是这个场的**等值线**：沿等高线蜿蜒的**细长条带**。
 * 一维选择（= 数值落区间）永远只会得到条带；要团块就必须**二维选择**（= 位置落在哪个区域）。
 * 离线实测（{@code tools/biome-probe}，2048×2048；判据用**单个斑块的形状因子**
 * {@code A/(W²×斑块个数)}：紧凑团块 4–9，长条会到几十上百）：
 * <pre>
 *              风格群系面积   平均宽     形状因子
 * 旧实现        2.9 – 6.0%    9 – 12 格   71 – 155    ← 宽 10 格、长上千格 = 长条
 * 现在          3.7 – 8.2%    40 – 42 格  6.0 – 8.6   ← 团块
 * </pre>
 * 旧实现还有两处附带问题：① 噪声实际幅度是 **±1.4**（{@code profile(1.2, 6)} 六个八度等权叠加），
 * 代码却按 **±0.4** 切段 ⇒ 段边界与实际分布对不上；② 最高八度波长仅 {@code 4000/64 ≈ 62} 格
 * 却幅度最大 ⇒ 条带又细又碎。
 *
 * <h2>现在怎么分（抖动网格 / Voronoi 胞）</h2>
 * 世界划成 {@link #CELL} 格见方的网格，胞心在自己格内随机抖动，取**最近的胞心**决定归属 ⇒
 * 一片片团块；胞的群系由胞坐标的哈希决定：
 * <pre>
 * hash(胞) &lt;  {@link #STYLE_SHARE} → 7 个风格群系之一（等概率）
 * hash(胞) &gt;= {@link #STYLE_SHARE} → {@code DEEP_CAVE} 兜底（深窟基础）
 * </pre>
 * 相邻胞抽到同一群系时会自然连成更大的团块（想要的效果，不是 bug）；
 * {@link #WARP} 再用一层噪声把胞边界揉弯，避免"棋盘格"直边。
 *
 * <h2>⚠ 第二个坑：面积占比正确 ≠ 玩家看得见（别只调占比）</h2>
 * 改完形状后出现过"整个世界只有一个群系"，根因不在占比而在**胞太大 + 兜底群系渗流**：
 * <pre>
 *                    兜底占比   斑块宽    随机点最近的其它群系   最长"只有深窟"走廊
 * CELL=256, share=344   68.8%   146 格   中位  38 格           2537 格
 * CELL=128, share=420   55.5%    69 格   中位  16 格           1221 格
 * </pre>
 * 后者还满足"兜底占比 &lt; 渗流阈值 0.593"⇒ 深窟被切成有限团块，走不出永远深窟的走廊。
 * 真实存档核对：{@code tools/biome-probe} 的 {@code BiomeSaveCheck} 把已生成区块的群系数组
 * 与离线布局逐格对照，**一致率 100%** ⇒ 机制没问题，纯粹是尺度问题。
 *
 * <p>要调群系大小/密度只改本类常量；{@code GenLayerUndergroundBiomes} 只做"槽位 → Biome"的翻译。
 */
public final class UndergroundBiomeLayout {

    /**
     * 风格群系胞的边长（格）。
     *
     * <p>⚠ 这个值踩过一次坑：{@code 256} 时胞比玩家的活动范围还大 —— 实测真实存档里，
     * 出生点附近 4×4 区块**整块只有一个群系**，最长的"只有深窟"走廊达 **2537 格**，
     * 玩家体感就是"只生成了一个群系"。{@code 128} 时实测斑块宽约 57–69 格（仍是团块）、
     * 中位数只需走 **11–16 格**就能遇到另一个群系，最长走廊降到 ~730–1220 格。
     */
    private static final double CELL = 128.0D;

    /**
     * 风格群系占世界的比例（其余 = 深窟兜底）。
     *
     * <p>{@code 0.42} 是**结构性**选出来的，不只是"多点少点"：方形格点渗流阈值是 **0.593**，
     * 兜底占比一旦高于它，深窟会**渗流成一整片连通的"海"**，风格群系沦为海里的孤岛，
     * 玩家可以一直走在深窟里（上一版 0.344 ⇒ 深窟 66–69%，就是这个问题）。
     * 取 0.42 ⇒ 深窟 55–61% **低于阈值** ⇒ 深窟被切成有限大小的团块，走不出"永远深窟"的走廊。
     * 深窟仍是**单个占比最大的群系**（设计上"深窟兜底 + 风格群系点缀"不变）。
     */
    private static final double STYLE_SHARE = 0.42D;

    /** 胞心抖动比例：0.7 = 胞心只能落在自身格内中间 70% 的区域（防止相邻胞被挤成细条）。 */
    private static final double CELL_JITTER = 0.7D;

    /** 边界扭曲幅度（格）；0 = 关闭（边界是直的多边形）。{@link #WARP_NOISE} 实测幅度 ≈±0.8 ⇒ 实际扭曲 ≈±32 格。 */
    private static final double WARP = 40.0D;

    /** 扭曲噪声的尺度（格）：越大边界越平缓。 */
    private static final double WARP_SCALE = 700.0D;

    /**
     * 扭曲用的噪声。⚠ 必须用**低 persistence**：{@code profile(1.0, 3)} 三个八度等权叠加，
     * 实测幅度约 ±3 ⇒ {@code *WARP} 会放大成 ±120 格（比胞还大！把团块揉烂）。
     * {@code profile(0.5, 3)} 实测幅度 ≈±0.8，{@code *WARP(40)} ≈ ±32 格，正合"把边界揉弯"的用量。
     */
    private static final WorldEngineNoise.NoiseProfile WARP_NOISE = WorldEngineNoise.profile(0.5D, 3);

    /** 风格群系个数（对应 {@code UndergroundBiomes.ALL} 的 0..6）。 */
    public static final int STYLE_COUNT = 7;

    /** 兜底群系（深窟基础）在 {@code UndergroundBiomes.ALL} 里的下标。 */
    public static final int DEEP_CAVE_SLOT = 7;

    // 各类哈希的盐：保证"抖动量 / 是否风格群系 / 是哪一个风格"互相独立
    private static final long SALT_OFFSET_X = 0x51ED270B1L;
    private static final long SALT_OFFSET_Z = 0x2F1B3C5D7L;
    private static final long SALT_IS_STYLE = 0x1D8E4E27C6B3A509L;
    private static final long SALT_STYLE_ID = 0x7A5B3C9D1E2F4A6BL;

    private UndergroundBiomeLayout() {}

    /**
     * 该位置属于哪个群系槽位：{@code 0..6} = 7 个风格群系（等概率），{@code 7} = 深窟兜底。
     * 下标即 {@code UndergroundBiomes.ALL} 的下标。
     *
     * <p>纯函数：只依赖世界坐标与种子，跨区块天然连续；任何调用方（含离线工具、调试命令）
     * 都能独立复现同一份分布，不需要各算一遍。
     */
    public static int slotAt(final long seed, final int worldX, final int worldZ) {
        return slotAt(seed, worldX, worldZ, CELL, STYLE_SHARE);
    }

    /**
     * 同上，但可指定胞边长与风格群系占比 —— **给离线标定/调参用**
     * （{@code tools/biome-probe} 扫参数就靠它，这样扫的还是同一份算法，不会出现"工具里的副本"漂移）。
     */
    public static int slotAt(final long seed, final int worldX, final int worldZ,
                             final double cell, final double styleShare) {

        // 1) 边界扭曲：揉采样坐标，胞边界就不是直边
        final double sampleX = worldX + WorldEngineNoise.perlinNoise2D(
                seed ^ SALT_OFFSET_X, worldX / WARP_SCALE, worldZ / WARP_SCALE, WARP_NOISE) * WARP;
        final double sampleZ = worldZ + WorldEngineNoise.perlinNoise2D(
                seed ^ SALT_OFFSET_Z, worldX / WARP_SCALE, worldZ / WARP_SCALE, WARP_NOISE) * WARP;

        // 2) 最近抖动胞心（胞心只在自身格内，故 3×3 邻域一定包含最近的胞）
        final long cellX = (long) Math.floor(sampleX / cell);
        final long cellZ = (long) Math.floor(sampleZ / cell);
        double bestDistance = Double.MAX_VALUE;
        long winnerX = cellX;
        long winnerZ = cellZ;
        for (long dz = -1L; dz <= 1L; dz++) {
            for (long dx = -1L; dx <= 1L; dx++) {
                final long cx = cellX + dx;
                final long cz = cellZ + dz;
                final double centerX = cx * cell + jitter(cx, cz, seed, SALT_OFFSET_X) * cell;
                final double centerZ = cz * cell + jitter(cx, cz, seed, SALT_OFFSET_Z) * cell;
                final double ddx = sampleX - centerX;
                final double ddz = sampleZ - centerZ;
                final double distance = ddx * ddx + ddz * ddz;
                if (distance < bestDistance) {
                    bestDistance = distance;
                    winnerX = cx;
                    winnerZ = cz;
                }
            }
        }

        // 3) 先决定"是不是风格群系"，再决定是哪一个
        if (hash01(winnerX, winnerZ, seed, SALT_IS_STYLE) >= styleShare) {
            return DEEP_CAVE_SLOT;
        }
        return (int) Math.min(STYLE_COUNT - 1,
                Math.floor(hash01(winnerX, winnerZ, seed, SALT_STYLE_ID) * STYLE_COUNT));
    }

    /** 胞心在自身格内的偏移比例，落在 {@code [(1-JITTER)/2, (1+JITTER)/2]}。 */
    private static double jitter(final long cellX, final long cellZ, final long seed, final long salt) {
        final double low = (1.0D - CELL_JITTER) * 0.5D;
        return low + CELL_JITTER * hash01(cellX, cellZ, seed, salt);
    }

    /** 由（胞坐标, 种子, 盐）得到 {@code [0,1)} 的确定性哈希（splitmix64 收尾）。 */
    private static double hash01(final long cellX, final long cellZ, final long seed, final long salt) {
        long h = cellX * 0x9E3779B97F4A7C15L ^ cellZ * 0xC2B2AE3D27D4EB4FL ^ (seed + salt);
        h ^= h >>> 29;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 32;
        h *= 0x94D049BB133111EBL;
        h ^= h >>> 31;
        return (h >>> 11) * 0x1.0p-53;
    }
}
