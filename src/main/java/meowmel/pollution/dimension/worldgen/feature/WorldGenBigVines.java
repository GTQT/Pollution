package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 超长藤蔓（繁茂洞穴的标志景观）：
 * 从洞顶垂下的超长藤蔓柱——柱芯树叶（LEAVES，绿色藤蔓本体）+ 四面藤蔓包裹，
 * 高 20~45 格，从洞顶一路垂到接近洞底。
 * 生成条件：洞顶下表面定位（先空气后实心），柱体路径全部为空气。
 */
public class WorldGenBigVines extends WorldGenerator {

    private static final IBlockState LEAVES = Blocks.LEAVES.getDefaultState()
            .withProperty(BlockLeaves.DECAYABLE, false);

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        // 定位洞顶下表面：从上向下，先空气（洞窟空间）后实心（洞顶岩层）
        boolean seenAir = false;
        BlockPos ceiling = null;
        for (int y = 244; y >= 12; --y) {
            BlockPos pos = new BlockPos(position.getX(), y, position.getZ());
            if (worldIn.isAirBlock(pos)) {
                seenAir = true;
            } else if (seenAir && worldIn.getBlockState(pos).getMaterial().isSolid()) {
                ceiling = new BlockPos(position.getX(), y - 1, position.getZ());
                break;
            }
        }
        if (ceiling == null) {
            return false;
        }

        int height = 20 + rand.nextInt(26);          // 20~45 格（超长）
        int thickness = rand.nextBoolean() ? 1 : 2;  // 柱芯 1×1 或 2×2

        // 预检：柱体路径（含藤蔓外皮）全部为空气
        for (int d = 0; d < height; ++d) {
            for (int dx = 0; dx < thickness; dx++) {
                for (int dz = 0; dz < thickness; dz++) {
                    if (!worldIn.isAirBlock(ceiling.add(dx, -d, dz))) {
                        return false;
                    }
                }
            }
        }

        // 生成：柱芯树叶（不衰减）+ 外表面四面藤蔓
        for (int d = 0; d < height; ++d) {
            for (int dx = 0; dx < thickness; dx++) {
                for (int dz = 0; dz < thickness; dz++) {
                    BlockPos core = ceiling.add(dx, -d, dz);
                    worldIn.setBlockState(core, LEAVES, 2);
                    if (dx == 0) setVine(worldIn, core.west(), EnumFacing.EAST);
                    if (dx == thickness - 1) setVine(worldIn, core.east(), EnumFacing.WEST);
                    if (dz == 0) setVine(worldIn, core.north(), EnumFacing.SOUTH);
                    if (dz == thickness - 1) setVine(worldIn, core.south(), EnumFacing.NORTH);
                }
            }
        }
        return true;
    }

    /** 放置藤蔓：依附于柱子方向（attachedTo 为藤蔓看向柱子的方向） */
    private static void setVine(World worldIn, BlockPos pos, EnumFacing attachedTo) {
        if (!worldIn.isAirBlock(pos)) {
            return;
        }
        IBlockState state = Blocks.VINE.getDefaultState()
                .withProperty(BlockVine.NORTH, attachedTo == EnumFacing.NORTH)
                .withProperty(BlockVine.SOUTH, attachedTo == EnumFacing.SOUTH)
                .withProperty(BlockVine.EAST, attachedTo == EnumFacing.EAST)
                .withProperty(BlockVine.WEST, attachedTo == EnumFacing.WEST);
        worldIn.setBlockState(pos, state, 2);
    }
}
