package meowmel.pollution.dimension.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * 地下世界的**地下河网络**：在地下水面上切出一条连续、宽阔、可一路划船的水道。
 *
 * <h2>它解决什么问题</h2>
 * 本维度的地形是三维噪声 + "低于海平面的空洞自动灌水"（见
 * {@code ChunkGeneratorUndergroundWorld.prepareHeights}），于是 y&lt;63 的洞窟**各自灌满水但彼此不通**：
 * 玩家能看见一片片水，却没法从一个洞窟把船划到另一个洞窟。
 * 本类沿着一条噪声河道，把挡在水面以下的岩体**凿穿并灌水**，同时把水面以上凿出一条"峡谷"，
 * 让水道连成**一张网** ⇒ 地下河。
 *
 * <h2>为什么用噪声等值线（比 RTG 那套简单）</h2>
 * RTG/RWG 用的是 Voronoi 单元边界 + 扭曲场，还要算"隧道强度/交汇强度/山地链门控"。
 * 这里不需要那么讲究：<b>二维噪声的零等值线本来就是一条条蜿蜒的长曲线</b>，
 * 取 {@code |n| < WIDTH} 的邻域就是沿曲线铺开的一条带 —— 河道。一行判定，无状态、无跨区块依赖、
 * 天然连续（噪声按世界坐标采样，区块边界不会断）。
 *
 * <h2>参数是**离线实测**选出来的，不是拍的</h2>
 * 用 {@code WorldEngineNoise} 本身在窗口上量（含 chamfer 距离变换算真实**垂直河宽**、
 * 并数水面连通分量）：{@code tools/river-probe/.../RiverFieldProbe.java`}（复跑方法见该文件注释）。
 *
 * <pre>
 * profile(persistence, octaves)  SCALE   WIDTH   覆盖率   垂直河宽 p10/p50/p90    连通分量
 * (1.0, 3)                       2500    0.07     2.30%    10.0/ 46.7/ 84.0      1（一整张网）
 * (1.0, 3)                       2500    0.05     1.62%     7.3/ 33.3/ 58.7      1
 * (1.0, 3)                       2000    0.07     4.46%    10.7/ 52.0/120.0      1
 * (1.0, 3)                       3000    0.07     0.22%     4.7/ 22.0/ 46.7      1（太稀）
 * (0.5, 3)                       2500    0.15     8.00%    18.7/109.3/286.7     3（糊成一片，不是河）
 * </pre>
 *
 * 两个结论：① <b>persistence 必须给足</b>（0.5 时最低频八度占绝对主导，噪声很少过零 ⇒
 * 等值线退化成一整片糊块，河宽中位数上百格且碎成好几块，不是河道）；
 * ② 选 <b>(1.0, 3) + SCALE 2500 + WIDTH 0.07</b>：覆盖率 2.30%（平均每 ~1700 格横穿一条河），
 * 垂直河宽中位数 <b>≈47 格</b>（窄处 ~10 格、宽处 80+ 格，正是"水面比较宽广"），
 * 河道深度 p10/p50/p90 ≈ <b>7 / 10.9 / 15</b> 格，而且窗口内水面**只有一个连通分量**
 * —— 真的是一条能一路划下去的水道，而不是一摊摊孤立的湖。
 *
 * <h2>为什么水面恒在 {@code waterTop}（= 海平面 − 1）</h2>
 * 周围低于海平面的洞窟已经被灌到同一水位，河道只要**用同一个水面**，就与它们天然同高、无缝连通；
 * 不需要任何"联通口"逻辑。同理，水面以上不填水（见下）。
 *
 * <h2>断面（由河心向两侧渐隐）</h2>
 * <pre>
 * strength = 1 - |n| / WIDTH        // 河心 1、河道边缘 0
 * 河床      = waterTop - (BED_MIN + strength * (BED_MAX - BED_MIN))     // 6 → 16 格深
 * 峡谷顶    = waterTop + strength * ROOF_MAX                            // 0 → 12 格高
 * </pre>
 * 河床越靠中心越深、峡谷越靠中心越高 ⇒ 边缘自然收口，不会有"直上直下的水墙"。
 * 水面之上凿成空气，是为了让这条河穿过岩体时看得见（否则它只是石缝里的一条水线）。
 *
 * <h2>为什么不会漏水</h2>
 * 液体横向流动要求"侧面那一格是空气"。本类**从不在水面及以下留空气**（y≤waterTop 一律填水），
 * 而在本维度 y=62 这一层本来就非水即石（海平面以下的空洞在 {@code prepareHeights} 里已被全部灌水，
 * 且洞穴生成器 {@code MapGenCavesUnderground} 一旦在开凿范围内扫到液体就放弃该段）
 * ⇒ 水面那一层的四周是水或石头，不会"水漫上岸"。所有方块都用 flag 2（不触发邻块更新）写入，
 * 与其余世界生成一致。
 *
 * <h2>调用位置（有讲究）</h2>
 * <pre>
 * prepareHeights → buildSurfaces → <b>UndergroundRivers.carve</b> → replaceStoneNearWater → caveGen
 * </pre>
 * 放在 {@code replaceStoneNearWater} 之前，河道床与两岸才会被它刷上沙砾（那里的"湖床"逻辑：
 * 从水面往下穿过水体后遇到的第一层石头 ⇒ 铺 2–4 层沙砾），河道自然有了一条砾石河床；
 * 放在 {@code caveGen} 之前，洞穴才能与河道打通（洞穴生成器自己会避开液体，不会抽干河道）。
 *
 * <p>要调河道密度/宽度/深度，<b>只改本类的常量</b>；生成器那边只有一行调用。
 */
public final class UndergroundRivers {

    /** 噪声种子盐，避免和本维度其它 {@code WorldEngineNoise} 用途撞相位。 */
    private static final long SEED_SALT = 0x9E3779B97F4A7C15L;

    /** 噪声尺度（格）：越大河道越直、弯越缓。实测 2500 时一个 2048 格窗口内只有一张连通河网。 */
    private static final double SCALE = 2500.0D;

    /** 河道判定的噪声阈值（|n| &lt; WIDTH 即为河道），同时是河宽标尺：实测 0.07 ⇒ 河宽中位数 ≈40 格。 */
    private static final double WIDTH = 0.07D;

    /**
     * 河道噪声的频谱。{@code persistence = 1.0}（各八度等幅）是实测选出来的：
     * 幅值不足时噪声很少过零，等值线退化成一整片糊块（见类注释的实测表）。
     */
    private static final WorldEngineNoise.NoiseProfile RIVER_NOISE = WorldEngineNoise.profile(1.0D, 3);

    /** 河道边缘（strength=0）处的河床深度（格，水面以下）。 */
    private static final int BED_MIN = 6;
    /** 河心（strength=1）处的河床深度（格，水面以下）。 */
    private static final int BED_MAX = 16;
    /** 河心处水面以上凿开的峡谷高度（格）；向河道边缘渐隐到 0。 */
    private static final int ROOF_MAX = 12;

    private static final IBlockState WATER = Blocks.WATER.getDefaultState();
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();

    private UndergroundRivers() {}

    /**
     * 在区块内开凿地下河。
     *
     * @param worldSeed 世界种子（河道按世界坐标采样，故跨区块连续）
     * @param waterTop  水面高度（= 该维度的海平面 − 1，与本维度地下水面的高度一致）
     * @param primer    正在生成的区块
     * @param chunkX    区块 X
     * @param chunkZ    区块 Z
     */
    public static void carve(final long worldSeed, final int waterTop,
                             final ChunkPrimer primer, final int chunkX, final int chunkZ) {

        final long seed = worldSeed ^ SEED_SALT;
        final int baseX = chunkX * 16;
        final int baseZ = chunkZ * 16;

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {

                final double strength = fieldStrength(seed, baseX + localX, baseZ + localZ);
                if (strength <= 0.0D) {
                    continue;                       // 不在河道内：本列一格都不碰
                }

                final int floor = waterTop - (BED_MIN + (int) Math.round(strength * (BED_MAX - BED_MIN)));
                final int roof = waterTop + (int) Math.round(strength * ROOF_MAX);

                for (int y = Math.max(2, floor); y <= roof; y++) {
                    if (!isCarvable(primer.getBlockState(localX, y, localZ))) {
                        continue;                   // 基岩/水/岩浆/空气一律不动
                    }
                    primer.setBlockState(localX, y, localZ, y <= waterTop ? WATER : AIR);
                }
            }
        }
    }

    /**
     * 河道强度：{@code 1} = 河心，{@code (0,1)} = 河道内（边缘趋 0），{@code <= 0} = 不在河道内。
     *
     * <p>公开出来是为了让其它系统（装饰、调试命令、未来的渡口/桥）**复用同一条河道**，
     * 而不是各自再算一遍噪声 —— 河宽/位置只有一个真相，就在本类。
     */
    public static double strengthAt(final long worldSeed, final int worldX, final int worldZ) {
        return fieldStrength(worldSeed ^ SEED_SALT, (double) worldX, (double) worldZ);
    }

    /** 内部实现：入参 seed 必须是**已加盐**的（避免与公开重载混淆导致二次加盐）。 */
    private static double fieldStrength(final long saltedSeed, final double worldX, final double worldZ) {
        final double noise = WorldEngineNoise.perlinNoise2D(
                saltedSeed, worldX / SCALE, worldZ / SCALE, RIVER_NOISE);
        final double strength = 1.0D - Math.abs(noise) / WIDTH;
        return strength > 0.0D ? strength : 0.0D;
    }

    /** 可开凿的方块：基岩、水、岩浆、空气都不动（其余一律凿掉，含群系表面方块）。 */
    private static boolean isCarvable(final IBlockState state) {
        final Block block = state.getBlock();
        return block != Blocks.AIR && block != Blocks.WATER && block != Blocks.LAVA
                && block != Blocks.BEDROCK;
    }
}
