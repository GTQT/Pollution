package meowmel.pollution.common.block.blocks;


import meowmel.pollution.Pollution;
import meowmel.pollution.common.items.PollutionItemsInit;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * 心鸣果 - 悬挂在血肉树叶下方
 * 有4个成长阶段 (0-3)
 * 阶段3为完全成熟：深红色，发出微弱心跳声
 * 右键采摘获得心鸣果物品
 */
public class BlockHeartFruit extends Block {

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

    // 不同阶段的碰撞箱大小（逐渐变大）
    private static final AxisAlignedBB[] AABB_BY_AGE = new AxisAlignedBB[]{
            new AxisAlignedBB(0.375, 0.625, 0.375, 0.625, 1.0, 0.625),  // 阶段0: 小
            new AxisAlignedBB(0.3125, 0.5, 0.3125, 0.6875, 1.0, 0.6875), // 阶段1
            new AxisAlignedBB(0.25, 0.375, 0.25, 0.75, 1.0, 0.75),       // 阶段2
            new AxisAlignedBB(0.1875, 0.25, 0.1875, 0.8125, 1.0, 0.8125) // 阶段3: 成熟
    };

    public BlockHeartFruit() {
        super(Material.PLANTS);
        setTranslationKey(Pollution.MODID + ".heart_fruit");
        setRegistryName(Pollution.MODID, "heart_fruit");
        setHardness(0.2F);
        setSoundType(SoundType.SLIME);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(blockState.getBaseState().withProperty(AGE, 0));
        setTickRandomly(true);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        if (worldIn.isRemote) return;

        int age = state.getValue(AGE);

        // 检查上方是否还是血肉树叶
        IBlockState above = worldIn.getBlockState(pos.up());
        if (!(above.getBlock() instanceof BlockFleshLeaves)) {
            // 失去支撑，掉落
            worldIn.destroyBlock(pos, true);
            return;
        }

        // 未成熟时有20%概率生长
        if (age < 3 && random.nextInt(5) == 0) {
            worldIn.setBlockState(pos, state.withProperty(AGE, age + 1));
        }

    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        if (worldIn.isRemote && state.getValue(AGE) == 3) {
            worldIn.playSound(null, pos,
                    net.minecraft.init.SoundEvents.BLOCK_NOTE_BASEDRUM,
                    SoundCategory.BLOCKS,
                    0.12F,
                    0.6F); // 第二声稍微高一点
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                     EntityPlayer playerIn, EnumHand hand,
                                     EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getValue(AGE) == 3) {
            // 成熟后右键采摘
            if (!worldIn.isRemote) {
                spawnAsEntity(worldIn, pos, new ItemStack(PollutionItemsInit.HEART_FRUIT, 1));
                worldIn.setBlockToAir(pos);
            }
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_BY_AGE[state.getValue(AGE)];
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB; // 无碰撞
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
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock() instanceof BlockFleshLeaves;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockFleshLeaves)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AGE, Math.min(meta, 3));
    }

    @Override
    public java.util.List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        java.util.List<ItemStack> drops = new java.util.ArrayList<>();
        if (state.getValue(AGE) == 3) {
            drops.add(new ItemStack(PollutionItemsInit.HEART_FRUIT));
        }
        return drops;
    }
}
