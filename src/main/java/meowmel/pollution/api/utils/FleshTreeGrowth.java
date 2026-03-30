package meowmel.pollution.api.utils;


import meowmel.pollution.common.block.blocks.BlockEldritchEye;
import meowmel.pollution.common.block.blocks.BlockFleshLeaves;
import meowmel.pollution.common.block.blocks.BlockTentacle;
import meowmel.pollution.common.block.blocks.PollutionBlocksInit;
import meowmel.pollution.common.block.tile.TileEntityFleshHeart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * ================================================================
 * 血肉之树生长算法 — 克苏鲁风格
 * ================================================================
 *
 * 整体形态: 一颗扭曲的、充满眼球和触手的异界心脏
 *
 *          触手 ~~~\     /~~~ 触手
 *                  \   /
 *            👁  ┌──┴─┴──┐  👁
 *               ┌┤ V切迹 ├┐
 *          👁  ┌┤│       │├┐  👁
 *         ~~~ ┤│ 心室体  │├ ~~~
 *          👁 └┤│ ♥核心  │├┘  👁
 *              └┤       ├┘
 *               └───┬───┘
 *            触手 ~~~│~~~ 触手
 *                   │
 *              ┌────┼────┐ 扭曲根系
 *              └─────────┘
 *
 * 克苏鲁特色:
 *   - 触手: 从心室体表面沿多个方向蜿蜒伸出，用种子化随机
 *           实现"有机但可重现"的弯曲路径，末梢渐细
 *   - 深渊之眼: 嵌入心室外壳表面，朝向外侧，等级越高越多
 *   - 扭曲不对称: 心室膨胀体加入周期性扰动，轮廓不再完美椭圆
 *   - 脊柱/骨刺: 高等级时心室表面长出向外的短刺
 *   - 根系蠕动: 根从地面扭曲延伸，部分下扎
 *
 * 随机性原则:
 *   所有随机元素使用 originPos.hashCode() 为种子，
 *   确保同一棵树每次加载生长结果一致（确定性随机）。
 */
public class FleshTreeGrowth {

    // ============================
    // 参数表 (索引0占位, 1~10有效)
    // ============================
    private static final int[] TRUNK_RADIUS     = {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 5};
    private static final int[] TRUNK_HEIGHT      = {0, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static final int[] VENTRICLE_HALF_W  = {0, 2, 3, 5, 6, 8, 10, 12, 14, 17, 20};
    private static final int[] VENTRICLE_HEIGHT  = {0, 3, 4, 6, 7, 9, 11, 13, 15, 18, 20};
    private static final int[] NOTCH_DEPTH       = {0, 1, 1, 2, 2, 3, 4, 4, 5, 6, 7};
    private static final int[] AORTA_HEIGHT      = {0, 2, 3, 4, 5, 6, 7, 9, 10, 12, 14};
    private static final int[] TENTACLE_COUNT    = {0, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12};
    private static final int[] TENTACLE_LENGTH   = {0, 4, 5, 7, 9, 11, 13, 15, 18, 20, 22};
    private static final int[] EYE_COUNT         = {0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static final int[] SPINE_COUNT       = {0, 0, 0, 2, 3, 4, 6, 8, 10, 12, 16};

    /**
     * 将树生长到指定等级
     */
    public static boolean growToLevel(World world, BlockPos origin, BlockPos heartPos, int newLevel) {
        if (newLevel < 1 || newLevel > 10) return false;

        // 确定性随机: 同一棵树同一等级总是生成相同的随机元素
        Random rand = new Random(origin.hashCode() * 31L + newLevel);

        IBlockState flesh  = PollutionBlocksInit.FLESH_BLOCK.getDefaultState();
        IBlockState leaves = PollutionBlocksInit.FLESH_LEAVES.getDefaultState();
        ((BlockFleshLeaves)leaves.getBlock()).heart=(TileEntityFleshHeart) world.getTileEntity(heartPos);

        int tR     = TRUNK_RADIUS[newLevel];
        int tH     = TRUNK_HEIGHT[newLevel];
        int vHalfW = VENTRICLE_HALF_W[newLevel];
        int vH     = VENTRICLE_HEIGHT[newLevel];
        int nD     = NOTCH_DEPTH[newLevel];
        int aH     = AORTA_HEIGHT[newLevel];

        int vBaseY = tH;

        // 1. 扭曲根系
        buildTwistedRoots(world, origin, heartPos, tR, newLevel, rand, flesh);
        // 2. 树干
        buildTrunk(world, origin, heartPos, tR, tH, flesh);
        // 3. 扭曲心室体 + 骨刺
        buildDistortedVentricles(world, origin, heartPos, tR, vBaseY, vH, vHalfW, nD, newLevel, rand, flesh, leaves);
        // 4. 主动脉
        buildAorta(world, origin, heartPos, tR, vBaseY + vH, aH, newLevel, rand, flesh, leaves);
        // 5. 触手
        buildTentacles(world, origin, heartPos, vBaseY, vH, vHalfW, newLevel, rand);
        // 6. 深渊之眼
        placeEyes(world, origin, heartPos, vBaseY, vH, vHalfW, newLevel, rand);

        return true;
    }

    // ============================
    // 1. 扭曲根系
    // ============================
    private static void buildTwistedRoots(World world, BlockPos origin, BlockPos heartPos,
                                          int trunkR, int level, Random rand, IBlockState flesh) {
        int rootCount = 4 + level / 2; // 4~9根
        for (int i = 0; i < rootCount; i++) {
            // 随机起始角度
            double angle = (i * Math.PI * 2.0 / rootCount) + (rand.nextDouble() - 0.5) * 0.6;
            int length = trunkR + 2 + rand.nextInt(level + 2);

            // 蜿蜒路径
            double cx = 0, cz = 0;
            int cy = 0;
            for (int step = 0; step < length; step++) {
                cx += Math.cos(angle) * 1.0;
                cz += Math.sin(angle) * 1.0;
                // 下扎
                if (step > trunkR + 1 && rand.nextInt(3) == 0) cy--;
                // 路径扰动
                angle += (rand.nextDouble() - 0.5) * 0.4;

                place(world, origin.add(Math.round(cx), cy, Math.round(cz)), heartPos, flesh);
            }
        }
    }

    // ============================
    // 2. 树干
    // ============================
    private static void buildTrunk(World world, BlockPos origin, BlockPos heartPos,
                                   int trunkR, int trunkH, IBlockState flesh) {
        for (int y = 0; y < trunkH; y++) {
            fillCircleXZ(world, origin.up(y), heartPos, trunkR, flesh);
        }
    }

    // ============================
    // 3. 扭曲心室体 + 骨刺
    // ============================
    private static void buildDistortedVentricles(World world, BlockPos origin, BlockPos heartPos,
                                                 int trunkR, int vBaseY, int vH, int vHalfW,
                                                 int notchDepth, int level, Random rand,
                                                 IBlockState flesh, IBlockState leaves) {
        int spineCount = SPINE_COUNT[level];
        // 预生成骨刺位置
        double[] spineAngles = new double[spineCount];
        int[] spineHeights = new int[spineCount];
        int[] spineLengths = new int[spineCount];
        for (int i = 0; i < spineCount; i++) {
            spineAngles[i] = rand.nextDouble() * Math.PI * 2;
            spineHeights[i] = vBaseY + 2 + rand.nextInt(Math.max(vH - 4, 1));
            spineLengths[i] = 1 + rand.nextInt(3);
        }

        for (int dy = 0; dy < vH; dy++) {
            int y = vBaseY + dy;
            float progress = (float) dy / vH;

            // 基础半径计算 (同心脏版)
            int baseHalfW = calcVentricleRadius(progress, trunkR, vHalfW);
            int notchW = 0;
            if (progress > 0.75f) {
                float t = (progress - 0.75f) / 0.25f;
                notchW = (int) Math.ceil(notchDepth * smoothStep(t));
            }

            // ★ 克苏鲁扭曲: 对轮廓加入周期性扰动
            // 每层的半径在不同角度有不同偏移, 产生不规则凹凸
            int layerHalfZ = Math.max((int) (baseHalfW * 0.85), trunkR + 1);

            for (int dx = -baseHalfW - 2; dx <= baseHalfW + 2; dx++) {
                for (int dz = -layerHalfZ - 2; dz <= layerHalfZ + 2; dz++) {
                    if (dx == 0 && dz == 0 && baseHalfW > 1) {
                        // 中心始终填充
                    }

                    // 极坐标角度
                    double angle = Math.atan2(dz, dx);
                    float ex = (float) dx / Math.max(baseHalfW, 1);
                    float ez = (float) dz / Math.max(layerHalfZ, 1);
                    float baseDist = ex * ex + ez * ez;

                    // ★ 扭曲: 3层不同频率的正弦扰动叠加
                    float distortion = 0;
                    distortion += 0.12f * (float) Math.sin(angle * 3 + dy * 0.5);  // 低频大波
                    distortion += 0.06f * (float) Math.sin(angle * 7 + dy * 1.2);  // 中频
                    distortion += 0.03f * (float) Math.sin(angle * 13 + dy * 2.1); // 高频细节

                    float distortedDist = baseDist - distortion;

                    if (distortedDist > 1.1f) continue;

                    // V切迹
                    if (notchW > 0 && Math.abs(dx) <= notchW && Math.abs(dz) <= (notchW / 2 + 1)) {
                        continue;
                    }

                    BlockPos pos = origin.add(dx, y, dz);
                    if (pos.equals(heartPos)) continue;

                    boolean isShell = distortedDist > 0.68f
                            || dy <= 1 || dy >= vH - 2
                            || (notchW > 0 && Math.abs(dx) <= notchW + 2 && Math.abs(dz) <= notchW / 2 + 2);

                    place(world, pos, heartPos, isShell ? leaves : flesh);
                }
            }
        }

        // 骨刺: 从心室表面向外伸出的短尖刺
        for (int i = 0; i < spineCount; i++) {
            double sa = spineAngles[i];
            int sy = spineHeights[i];
            int sLen = spineLengths[i];
            float progress = (float) (sy - vBaseY) / vH;
            int surfR = calcVentricleRadius(progress, trunkR, vHalfW);

            for (int s = 1; s <= sLen; s++) {
                int sx = (int) Math.round(Math.cos(sa) * (surfR + s));
                int sz = (int) Math.round(Math.sin(sa) * (surfR + s));
                place(world, origin.add(sx, sy, sz), heartPos, flesh);
            }
        }
    }

    /** 计算心室某一层的基础半径 */
    private static int calcVentricleRadius(float progress, int trunkR, int vHalfW) {
        int halfW;
        if (progress < 0.15f) {
            float t = progress / 0.15f;
            int startW = trunkR + 1;
            int targetW = trunkR + (int) Math.ceil((vHalfW - trunkR) * 0.3);
            halfW = startW + (int) ((targetW - startW) * smoothStep(t));
        } else if (progress < 0.45f) {
            float t = (progress - 0.15f) / 0.3f;
            int startW = trunkR + (int) Math.ceil((vHalfW - trunkR) * 0.3);
            halfW = startW + (int) ((vHalfW - startW) * smoothStep(t));
        } else if (progress < 0.75f) {
            float t = (progress - 0.45f) / 0.3f;
            halfW = vHalfW - (int) (vHalfW * 0.08 * t);
        } else {
            float t = (progress - 0.75f) / 0.25f;
            halfW = (int) (vHalfW * 0.92 * (1.0 - t * 0.25));
        }
        return Math.max(halfW, trunkR + 1);
    }

    // ============================
    // 4. 主动脉 (略微扭曲)
    // ============================
    private static void buildAorta(World world, BlockPos origin, BlockPos heartPos,
                                   int trunkR, int aortaBaseY, int aortaH,
                                   int level, Random rand,
                                   IBlockState flesh, IBlockState leaves) {
        if (aortaH <= 0) return;

        int aortaR = Math.min(trunkR, 2);
        // 主动脉路径有轻微偏移 (克苏鲁风: 不是笔直的)
        double driftX = 0, driftZ = 0;
        for (int dy = 0; dy <= aortaH; dy++) {
            driftX += (rand.nextDouble() - 0.5) * 0.4;
            driftZ += (rand.nextDouble() - 0.5) * 0.4;
            int offX = (int) Math.round(driftX);
            int offZ = (int) Math.round(driftZ);
            BlockPos center = origin.add(offX, aortaBaseY + dy, offZ);
            fillCircleXZ(world, center, heartPos, aortaR, flesh);
        }

        // 顶端: 不规则树叶冠
        int tipY = aortaBaseY + aortaH + 1;
        int offX = (int) Math.round(driftX);
        int offZ = (int) Math.round(driftZ);
        int crownR = aortaR + 2 + rand.nextInt(2);
        for (int dx = -crownR; dx <= crownR; dx++) {
            for (int dz = -crownR; dz <= crownR; dz++) {
                float d = dx * dx + dz * dz;
                // 不规则边缘: 概率跳过
                if (d <= crownR * crownR && (d < crownR * crownR * 0.6 || rand.nextInt(3) > 0)) {
                    place(world, origin.add(offX + dx, tipY, offZ + dz), heartPos, leaves);
                    place(world, origin.add(offX + dx, tipY + 1, offZ + dz), heartPos, leaves);
                }
            }
        }
    }

    // ============================
    // 5. ★ 触手
    // ============================

    /**
     * 从心室表面生成蜿蜒触手
     *
     * 每根触手:
     *   - 起点: 心室表面某个角度的位置
     *   - 路径: 用柏林噪声风格的随机漫步, 整体趋势向外+下垂
     *   - 粗细: 根部粗(2) → 中段中(1) → 末梢细(0)
     *   - 末端: 可能分叉为2根更细的子触手
     */
    private static void buildTentacles(World world, BlockPos origin, BlockPos heartPos,
                                       int vBaseY, int vH, int vHalfW,
                                       int level, Random rand) {
        int count = TENTACLE_COUNT[level];
        int maxLen = TENTACLE_LENGTH[level];
        IBlockState tentThick = PollutionBlocksInit.TENTACLE.getDefaultState().withProperty(BlockTentacle.THICKNESS, 2);
        IBlockState tentMid   = PollutionBlocksInit.TENTACLE.getDefaultState().withProperty(BlockTentacle.THICKNESS, 1);
        IBlockState tentThin  = PollutionBlocksInit.TENTACLE.getDefaultState().withProperty(BlockTentacle.THICKNESS, 0);

        for (int t = 0; t < count; t++) {
            // 触手起始角度（均匀分布 + 随机偏移）
            double angle = (t * Math.PI * 2.0 / count) + (rand.nextDouble() - 0.5) * 0.5;
            // 起始高度: 心室中部某处
            int startY = vBaseY + 2 + rand.nextInt(Math.max(vH - 4, 1));
            // 起始位置: 心室表面
            float progress = (float)(startY - vBaseY) / vH;
            int surfR = calcVentricleRadius(progress, TRUNK_RADIUS[level], vHalfW);

            double cx = Math.cos(angle) * (surfR + 1);
            double cy = startY;
            double cz = Math.sin(angle) * (surfR + 1);

            // 路径参数
            double dirX = Math.cos(angle); // 主要向外
            double dirZ = Math.sin(angle);
            double droop = -0.15; // 轻微下垂

            int length = maxLen - 2 + rand.nextInt(5);
            for (int step = 0; step < length; step++) {
                float stepProgress = (float) step / length;

                // 选择粗细
                IBlockState tentState;
                if (stepProgress < 0.3f) tentState = tentThick;
                else if (stepProgress < 0.7f) tentState = tentMid;
                else tentState = tentThin;

                BlockPos tentPos = origin.add(
                        (int) Math.round(cx),
                        (int) Math.round(cy - vBaseY), // 转换为相对origin的y
                        (int) Math.round(cz));
                // 修正: cy已经是绝对Y层级
                tentPos = new BlockPos(
                        origin.getX() + (int) Math.round(cx),
                        (int) Math.round(cy),
                        origin.getZ() + (int) Math.round(cz));

                place(world, tentPos, heartPos, tentState);

                // 路径演化: 蜿蜒+下垂
                cx += dirX * (0.8 + rand.nextDouble() * 0.4);
                cz += dirZ * (0.8 + rand.nextDouble() * 0.4);
                cy += droop + (rand.nextDouble() - 0.5) * 0.8;

                // 方向扰动 (越远扰动越大)
                double wobble = 0.3 + stepProgress * 0.4;
                dirX += (rand.nextDouble() - 0.5) * wobble;
                dirZ += (rand.nextDouble() - 0.5) * wobble;
                // 归一化方向 (保持速度)
                double mag = Math.sqrt(dirX * dirX + dirZ * dirZ);
                if (mag > 0.1) { dirX /= mag; dirZ /= mag; }

                droop -= 0.02; // 越往末端越下垂
            }

            // ★ 末端分叉 (等级5+, 50%概率)
            if (level >= 5 && rand.nextBoolean()) {
                for (int fork = 0; fork < 2; fork++) {
                    double fAngle = angle + (fork == 0 ? 0.5 : -0.5);
                    double fx = cx, fz = cz, fy = cy;
                    double fdx = Math.cos(fAngle), fdz = Math.sin(fAngle);
                    int forkLen = 3 + rand.nextInt(4);
                    for (int fs = 0; fs < forkLen; fs++) {
                        BlockPos fPos = new BlockPos(
                                origin.getX() + (int) Math.round(fx),
                                (int) Math.round(fy),
                                origin.getZ() + (int) Math.round(fz));
                        place(world, fPos, heartPos, tentThin);
                        fx += fdx * 0.9;
                        fz += fdz * 0.9;
                        fy += droop + (rand.nextDouble() - 0.5) * 0.5;
                        fdx += (rand.nextDouble() - 0.5) * 0.5;
                        fdz += (rand.nextDouble() - 0.5) * 0.5;
                    }
                }
            }
        }
    }

    // ============================
    // 6. ★ 深渊之眼
    // ============================

    /**
     * 在心室表面嵌入眼球
     * 位置均匀分布在心室外壳上, 朝向外侧
     */
    private static void placeEyes(World world, BlockPos origin, BlockPos heartPos,
                                  int vBaseY, int vH, int vHalfW,
                                  int level, Random rand) {
        int count = EYE_COUNT[level];

        for (int i = 0; i < count; i++) {
            // 极坐标分布
            double angle = (i * Math.PI * 2.0 / count) + (rand.nextDouble() - 0.5) * 0.8;
            int eyeY = vBaseY + 2 + rand.nextInt(Math.max(vH - 4, 1));

            float progress = (float)(eyeY - vBaseY) / vH;
            int surfR = calcVentricleRadius(progress, TRUNK_RADIUS[level], vHalfW);
            // 放在表面 (半径-1, 嵌入外壳)
            int ex = (int) Math.round(Math.cos(angle) * (surfR - 1));
            int ez = (int) Math.round(Math.sin(angle) * (surfR - 1));

            BlockPos eyePos = origin.add(ex, eyeY, ez);
            if (eyePos.equals(heartPos)) continue;

            // 计算朝向 (面朝外)
            EnumFacing facing = EnumFacing.getFacingFromVector(
                    (float) Math.cos(angle), 0, (float) Math.sin(angle));

            IBlockState eyeState = PollutionBlocksInit.ELDRITCH_EYE.getDefaultState()
                    .withProperty(BlockEldritchEye.FACING, facing)
                    .withProperty(BlockEldritchEye.OPEN, true);

            // 强制放置 (替换树叶/血肉)
            if (!eyePos.equals(heartPos)) {
                world.setBlockState(eyePos, eyeState, 2);
            }
        }
    }

    // ============================
    // 工具方法
    // ============================

    private static void fillCircleXZ(World world, BlockPos center, BlockPos heartPos,
                                     int radius, IBlockState state) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius + radius) {
                    place(world, center.add(dx, 0, dz), heartPos, state);
                }
            }
        }
    }

    private static void place(World world, BlockPos pos, BlockPos heartPos, IBlockState state) {
        if (pos.equals(heartPos)) return;
        if (world.isAirBlock(pos)) {
            world.setBlockState(pos, state, 2);
        }
    }

    private static float smoothStep(float t) {
        t = Math.max(0, Math.min(1, t));
        return t * t * (3 - 2 * t);
    }
}
