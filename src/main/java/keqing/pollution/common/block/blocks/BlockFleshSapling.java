package keqing.pollution.common.block.blocks;

import keqing.pollution.Pollution;
import keqing.pollution.common.block.tile.TileEntityFleshHeart;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * 血肉树苗 - 种下后会生长成血肉之树
 * 经过一段时间或骨粉催熟后，生成初始树结构和心脏核心TileEntity
 */
public class BlockFleshSapling extends Block implements IGrowable {

    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);

    private static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(
            0.1, 0.0, 0.1, 0.9, 0.8, 0.9);

    public BlockFleshSapling() {
        super(Material.PLANTS);
        setTranslationKey(Pollution.MODID + ".flesh_sapling");
        setRegistryName(Pollution.MODID, "flesh_sapling");
        setHardness(0.0F);
        setSoundType(SoundType.SLIME);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
        setTickRandomly(true);
    }
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        if (worldIn.isRemote) return;

        int stage = state.getValue(STAGE);
        if (stage == 0) {
            // 第一阶段：等待成长
            worldIn.setBlockState(pos, state.withProperty(STAGE, 1));
        } else {
            // 第二阶段：尝试生成树
            growTree(worldIn, pos, random);
        }
    }

    /**
     * 生成初始的血肉之树结构（等级1的小树）
     * 包含心脏核心TileEntity
     */
    public void growTree(World world, BlockPos saplingPos, Random random) {
        // 检查空间是否足够（至少需要3x3x5的空间）
        for (int y = 1; y <= 4; y++) {
            if (!world.isAirBlock(saplingPos.up(y))) return;
        }

        // 移除树苗
        world.setBlockToAir(saplingPos);

        // === 生成等级1的初始树 ===
        BlockPos heartPos = saplingPos.up(2); // 心脏在第3格高度

        // 树干: 3格高的血肉块
        world.setBlockState(saplingPos, PollutionBlocksInit.FLESH_BLOCK.getDefaultState());
        world.setBlockState(saplingPos.up(1), PollutionBlocksInit.FLESH_BLOCK.getDefaultState());
        // heartPos (saplingPos+2) 放心脏核心
        world.setBlockState(heartPos, PollutionBlocksInit.FLESH_HEART.getDefaultState());
        world.setBlockState(saplingPos.up(3), PollutionBlocksInit.FLESH_BLOCK.getDefaultState());

        // 树冠: 等级1的简单树叶（3x3，顶部十字形）
        IBlockState leaves = PollutionBlocksInit.FLESH_LEAVES.getDefaultState();
        BlockPos topTrunk = saplingPos.up(3);

        // 顶部树干周围一圈树叶
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                world.setBlockState(topTrunk.add(dx, 0, dz), leaves);
            }
        }

        // 最顶层十字形
        BlockPos top = saplingPos.up(4);
        world.setBlockState(top, leaves);
        world.setBlockState(top.north(), leaves);
        world.setBlockState(top.south(), leaves);
        world.setBlockState(top.east(), leaves);
        world.setBlockState(top.west(), leaves);

        // 设置心脏核心TileEntity
        TileEntityFleshHeart heart = (TileEntityFleshHeart) world.getTileEntity(heartPos);
        if (heart != null) {
            heart.setLevel(1);
            heart.setOrigin(saplingPos); // 记录树的根部位置
        }
    }

    // === IGrowable接口 - 骨粉催熟 ===
    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        if (state.getValue(STAGE) == 0) {
            worldIn.setBlockState(pos, state.withProperty(STAGE, 1));
        } else {
            growTree(worldIn, pos, rand);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SAPLING_AABB;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STAGE);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STAGE, meta & 1);
    }
}
