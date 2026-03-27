package keqing.pollution.api.utils;

import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.blocks.PollutionBlocksInit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 血肉之树生长算法
 *
 * 设计原则:
 * - 规则整齐的对称结构（十字/圆形截面）
 * - 每级增长一圈，树干加粗、加高，树冠扩大
 * - 最大等级10，最大占地约4个区块（~60x60方块范围内）
 *
 * 树的结构概览（从下到上）:
 * - 根部: 粗壮的底座
 * - 树干: 中心柱，随等级加粗
 * - 心脏: 核心TileEntity位置（始终在树干中部）
 * - 上部树干: 向上延伸
 * - 树冠: 多层球形/椭球形树叶
 *
 * 等级参数:
 * | Level | 树干半径 | 树干高度 | 冠半径 | 冠层数 | 大致占地 |
 * |-------|---------|---------|--------|-------|---------|
 * |   1   |    0    |    4    |    2   |   2   |  5x5    |
 * |   2   |    0    |    6    |    3   |   3   |  7x7    |
 * |   3   |    1    |    8    |    5   |   4   |  11x11  |
 * |   4   |    1    |   11    |    7   |   5   |  15x15  |
 * |   5   |    2    |   14    |    9   |   6   |  19x19  |
 * |   6   |    2    |   17    |   12   |   7   |  25x25  |
 * |   7   |    3    |   21    |   15   |   8   |  31x31  |
 * |   8   |    3    |   25    |   18   |   9   |  37x37  |
 * |   9   |    4    |   29    |   22   |  10   |  45x45  |
 * |  10   |    5    |   34    |   26   |  11   |  53x53  |
 */
public class FleshTreeGrowth {

    /**
     * 将树生长到指定等级
     * 这是增量生长——只添加新的方块，不移除旧的
     *
     * @param world     世界
     * @param origin    树根位置（最底部）
     * @param heartPos  心脏核心位置
     * @param newLevel  目标等级
     * @return 是否成功生长
     */
    public static boolean growToLevel(World world, BlockPos origin, BlockPos heartPos, int newLevel) {
        if (newLevel < 1 || newLevel > 10) return false;

        IBlockState flesh = PollutionBlocksInit.FLESH_BLOCK.getDefaultState();
        IBlockState leaves = PollutionBlocksInit.FLESH_LEAVES.getDefaultState();

        // 计算当前等级的参数
        int trunkRadius = getTrunkRadius(newLevel);
        int trunkHeight = getTrunkHeight(newLevel);
        int crownRadius = getCrownRadius(newLevel);
        int crownLayers = getCrownLayers(newLevel);

        // === 1. 生成树干 ===
        // 树干从origin开始向上延伸
        for (int y = 0; y < trunkHeight; y++) {
            BlockPos center = origin.up(y);
            // 使用圆形截面
            for (int dx = -trunkRadius; dx <= trunkRadius; dx++) {
                for (int dz = -trunkRadius; dz <= trunkRadius; dz++) {
                    // 圆形判定（使用欧几里得距离）
                    if (dx * dx + dz * dz <= trunkRadius * trunkRadius + trunkRadius) {
                        BlockPos p = center.add(dx, 0, dz);
                        // 不要覆盖心脏核心
                        if (!p.equals(heartPos)) {
                            setBlockIfNeeded(world, p, flesh);
                        }
                    }
                }
            }
        }

        // === 2. 生成根部底座（随等级扩大的十字形根） ===
        if (newLevel >= 3) {
            int rootSpread = trunkRadius + 2;
            for (int i = 1; i <= rootSpread; i++) {
                // 四个方向的根
                setBlockIfNeeded(world, origin.add(i, 0, 0), flesh);
                setBlockIfNeeded(world, origin.add(-i, 0, 0), flesh);
                setBlockIfNeeded(world, origin.add(0, 0, i), flesh);
                setBlockIfNeeded(world, origin.add(0, 0, -i), flesh);
                // 对角线根（等级5以上）
                if (newLevel >= 5 && i <= rootSpread - 1) {
                    setBlockIfNeeded(world, origin.add(i, 0, i), flesh);
                    setBlockIfNeeded(world, origin.add(-i, 0, i), flesh);
                    setBlockIfNeeded(world, origin.add(i, 0, -i), flesh);
                    setBlockIfNeeded(world, origin.add(-i, 0, -i), flesh);
                }
            }
        }

        // === 3. 生成树冠 ===
        // 树冠起始位置：树干顶部往下一点
        int crownStartY = trunkHeight - 2;
        int crownCenterY = crownStartY + crownLayers / 2;

        for (int layer = 0; layer < crownLayers; layer++) {
            int y = crownStartY + layer;
            BlockPos layerCenter = origin.up(y);

            // 计算该层的半径（椭球形，中间最大，上下渐小）
            float normalizedY = (float)(layer - crownLayers / 2.0f) / (crownLayers / 2.0f);
            float radiusMultiplier = 1.0f - normalizedY * normalizedY; // 抛物线
            int layerRadius = Math.max(1, (int)(crownRadius * radiusMultiplier));

            // 填充该层的圆形树叶
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    float dist = (float) Math.sqrt(dx * dx + dz * dz);
                    if (dist <= layerRadius + 0.5f) {
                        BlockPos p = layerCenter.add(dx, 0, dz);
                        // 不覆盖树干和心脏
                        if (isInsideTrunk(dx, dz, trunkRadius) && y < trunkHeight) {
                            continue; // 树干区域跳过
                        }
                        if (p.equals(heartPos)) continue;

                        setBlockIfNeeded(world, p, leaves);
                    }
                }
            }
        }

        // === 4. 顶部尖顶 ===
        BlockPos tip = origin.up(crownStartY + crownLayers);
        setBlockIfNeeded(world, tip, leaves);
        if (newLevel >= 3) {
            setBlockIfNeeded(world, tip.north(), leaves);
            setBlockIfNeeded(world, tip.south(), leaves);
            setBlockIfNeeded(world, tip.east(), leaves);
            setBlockIfNeeded(world, tip.west(), leaves);
        }
        if (newLevel >= 5) {
            setBlockIfNeeded(world, tip.up(), leaves);
        }

        // === 5. 生成主枝干（等级4以上） ===
        if (newLevel >= 4) {
            generateBranches(world, origin, heartPos, trunkHeight, trunkRadius, newLevel, flesh, leaves);
        }

        return true;
    }

    /**
     * 生成对称的主枝干（四个方向）
     */
    private static void generateBranches(World world, BlockPos origin, BlockPos heartPos,
                                          int trunkHeight, int trunkRadius, int level,
                                          IBlockState flesh, IBlockState leaves) {
        // 枝干从树干中上部伸出
        int branchCount = (level - 3); // 等级4:1层枝干, 等级10:7层枝干
        branchCount = Math.min(branchCount, 5);

        for (int b = 0; b < branchCount; b++) {
            // 枝干高度均匀分布在树干中部到上部
            int branchY = trunkHeight / 3 + (b * (trunkHeight / 2)) / Math.max(branchCount, 1);
            int branchLength = trunkRadius + 2 + b;
            branchLength = Math.min(branchLength, getCrownRadius(level) - 2);

            // 四个正方向各一根枝干
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int[] dir : directions) {
                for (int i = trunkRadius + 1; i <= branchLength; i++) {
                    BlockPos branchPos = origin.add(dir[0] * i, branchY, dir[1] * i);
                    if (!branchPos.equals(heartPos)) {
                        setBlockIfNeeded(world, branchPos, flesh);
                    }
                    // 枝干末端上翘
                    if (i == branchLength && level >= 6) {
                        setBlockIfNeeded(world, branchPos.up(), flesh);
                    }
                }
            }

            // 等级7以上增加对角线枝干
            if (level >= 7) {
                int diagLength = branchLength - 2;
                int[][] diagDirs = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
                for (int[] dir : diagDirs) {
                    for (int i = trunkRadius + 1; i <= diagLength; i++) {
                        BlockPos branchPos = origin.add(dir[0] * i, branchY, dir[1] * i);
                        if (!branchPos.equals(heartPos)) {
                            setBlockIfNeeded(world, branchPos, flesh);
                        }
                    }
                }
            }
        }
    }

    // === 参数查表 ===

    private static int getTrunkRadius(int level) {
        int[] radii = {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 5};
        return radii[level];
    }

    private static int getTrunkHeight(int level) {
        int[] heights = {0, 4, 6, 8, 11, 14, 17, 21, 25, 29, 34};
        return heights[level];
    }

    private static int getCrownRadius(int level) {
        int[] radii = {0, 2, 3, 5, 7, 9, 12, 15, 18, 22, 26};
        return radii[level];
    }

    private static int getCrownLayers(int level) {
        int[] layers = {0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        return layers[level];
    }

    /**
     * 判断坐标是否在树干圆形截面内
     */
    private static boolean isInsideTrunk(int dx, int dz, int trunkRadius) {
        return dx * dx + dz * dz <= trunkRadius * trunkRadius + trunkRadius;
    }

    /**
     * 仅在目标位置为空气时才放置方块（避免覆盖已有结构）
     */
    private static void setBlockIfNeeded(World world, BlockPos pos, IBlockState state) {
        if (world.isAirBlock(pos)) {
            world.setBlockState(pos, state, 2); // flag 2 = 不触发方块更新给客户端（批量操作后统一更新）
        }
    }
}
