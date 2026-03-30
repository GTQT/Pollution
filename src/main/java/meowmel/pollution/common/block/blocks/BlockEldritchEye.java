package meowmel.pollution.common.block.blocks;

import meowmel.pollution.Pollution;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 深渊之眼 — 克苏鲁风格的眼球方块
 *
 * 生长在血肉之树的表面，朝向外侧。
 * 会追踪附近的玩家（仅视觉效果，通过blockstate的FACING实现）。
 * 周期性眨眼（OPEN属性切换）。
 * 靠近时给玩家施加短暂恶心效果。
 * 被破坏时发出湿润的爆裂声。
 */
public class BlockEldritchEye extends Block {

    /** 眼球朝向 */
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    /** 是否睁开 */
    public static final PropertyBool OPEN = PropertyBool.create("open");

    private static final AxisAlignedBB EYE_AABB = new AxisAlignedBB(0.2, 0.2, 0.2, 0.8, 0.8, 0.8);

    public BlockEldritchEye() {
        super(Material.SPONGE);
        setTranslationKey(Pollution.MODID + ".eldritch_eye");
        setRegistryName(Pollution.MODID, "eldritch_eye");
        setHardness(0.5F);
        setSoundType(SoundType.SLIME);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(blockState.getBaseState()
                .withProperty(FACING, EnumFacing.SOUTH)
                .withProperty(OPEN, true));
        setTickRandomly(true);
        setLightLevel(0.4F); // 眼球微微发光
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        if (worldIn.isRemote) return;

        // 20% 概率眨眼
        if (random.nextInt(5) == 0) {
            boolean currentOpen = state.getValue(OPEN);
            worldIn.setBlockState(pos, state.withProperty(OPEN, !currentOpen), 2);

            // 闭眼后短暂重新睁开
            if (currentOpen) {
                worldIn.scheduleUpdate(pos, this, 10 + random.nextInt(10));
            }
        }

        // 追踪最近的玩家 — 改变朝向
        EntityPlayer nearest = worldIn.getClosestPlayer(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16, false);
        if (nearest != null) {
            EnumFacing lookDir = EnumFacing.getFacingFromVector(
                    (float)(nearest.posX - pos.getX()),
                    (float)(nearest.posY - pos.getY()),
                    (float)(nearest.posZ - pos.getZ()));
            if (lookDir != state.getValue(FACING)) {
                worldIn.setBlockState(pos, state.withProperty(FACING, lookDir), 2);
            }
        }

        // 附近3格内的玩家施加短暂恶心
        for (EntityPlayer player : worldIn.getEntitiesWithinAABB(EntityPlayer.class,
                new AxisAlignedBB(pos).grow(3))) {
            player.addPotionEffect(new net.minecraft.potion.PotionEffect(
                    net.minecraft.init.MobEffects.NAUSEA, 60, 0, true, false));

        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        // 眨眼后重新睁开
        if (!worldIn.isRemote && !state.getValue(OPEN)) {
            worldIn.setBlockState(pos, state.withProperty(OPEN, true), 2);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                             float hitX, float hitY, float hitZ,
                                             int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, facing).withProperty(OPEN, true);
    }

    // === 渲染 ===
    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return EYE_AABB;
    }

    // === BlockState ===
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, OPEN);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(OPEN) ? 8 : 0);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.byIndex(meta & 7))
                .withProperty(OPEN, (meta & 8) != 0);
    }
}
