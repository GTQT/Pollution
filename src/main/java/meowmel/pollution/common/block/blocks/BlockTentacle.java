package meowmel.pollution.common.block.blocks;


import meowmel.pollution.Pollution;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 触手方块 — 栅栏式自动连接
 *
 * 中心有一根柱体, 检测6个方向上是否有相邻触手/血肉块,
 * 有则自动伸出连接臂, 类似栅栏的自动拼接行为。
 *
 * 6个连接属性: NORTH, SOUTH, EAST, WEST, UP, DOWN
 * 1个粗细属性: THICKNESS (0=细 1=中 2=粗), 只存meta
 *
 * 碰到实体时减速+伤害
 */
public class BlockTentacle extends Block {

    public static final PropertyInteger THICKNESS = PropertyInteger.create("thickness", 0, 2);
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool EAST  = PropertyBool.create("east");
    public static final PropertyBool WEST  = PropertyBool.create("west");
    public static final PropertyBool UP    = PropertyBool.create("up");
    public static final PropertyBool DOWN  = PropertyBool.create("down");

    public BlockTentacle() {
        super(Material.SPONGE);
        setTranslationKey(Pollution.MODID + ".tentacle");
        setRegistryName(Pollution.MODID, "tentacle");
        setHardness(1.0F);
        setSoundType(SoundType.SLIME);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(blockState.getBaseState()
                .withProperty(THICKNESS, 1)
                .withProperty(NORTH, false).withProperty(SOUTH, false)
                .withProperty(EAST, false).withProperty(WEST, false)
                .withProperty(UP, false).withProperty(DOWN, false));
    }

    /**
     * 判断是否可以连接到相邻方块
     * 连接目标: 其他触手方块、血肉块、血肉树叶、心脏核心
     */
    private boolean canConnectTo(IBlockAccess world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block instanceof BlockTentacle
                || block instanceof BlockFlesh
                || block instanceof BlockFleshLeaves
                || block instanceof BlockFleshHeart;
    }

    /**
     * ★ 核心: 根据相邻方块动态计算实际渲染状态
     * MC在渲染和碰撞时调用此方法获取"真实"状态
     */
    @Override
    @Nonnull
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state
                .withProperty(NORTH, canConnectTo(worldIn, pos.north()))
                .withProperty(SOUTH, canConnectTo(worldIn, pos.south()))
                .withProperty(EAST,  canConnectTo(worldIn, pos.east()))
                .withProperty(WEST,  canConnectTo(worldIn, pos.west()))
                .withProperty(UP,    canConnectTo(worldIn, pos.up()))
                .withProperty(DOWN,  canConnectTo(worldIn, pos.down()));
    }

    // === 碰撞/交互 ===

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entityIn;
            entityIn.motionX *= 0.6;
            entityIn.motionZ *= 0.6;
            living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 1, true, false));
            if (!worldIn.isRemote && worldIn.rand.nextInt(20) == 0) {
                living.attackEntityFrom(net.minecraft.util.DamageSource.CACTUS, 1.0F);
            }
        }
    }
    // === 碰撞箱: 合并中心柱+各方向连接臂 ===

    // 中心柱 (根据粗细)
    private static final AxisAlignedBB[] CENTER = {
            new AxisAlignedBB(0.375, 0.375, 0.375, 0.625, 0.625, 0.625), // 细
            new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875), // 中
            new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75),       // 粗
    };

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = getActualState(state, source, pos);
        int t = state.getValue(THICKNESS);
        double min = CENTER[t].minX;
        double max = CENTER[t].maxX;
        double x1 = state.getValue(WEST)  ? 0.0 : min;
        double y1 = state.getValue(DOWN)  ? 0.0 : min;
        double z1 = state.getValue(NORTH) ? 0.0 : min;
        double x2 = state.getValue(EAST)  ? 1.0 : max;
        double y2 = state.getValue(UP)    ? 1.0 : max;
        double z2 = state.getValue(SOUTH) ? 1.0 : max;
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB; // 可穿过但触发减速
    }

    // === 渲染 ===

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return net.minecraft.util.BlockRenderLayer.CUTOUT;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, THICKNESS, NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(THICKNESS);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(THICKNESS, Math.min(meta & 3, 2));
    }
}
