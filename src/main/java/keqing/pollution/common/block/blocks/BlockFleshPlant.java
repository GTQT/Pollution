package keqing.pollution.common.block.blocks;

import keqing.pollution.api.utils.POUtils;
import keqing.pollution.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static keqing.pollution.Pollution.MODID;

public class BlockFleshPlant extends Block
{
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    private Random RandomNum= new Random();
    protected BlockFleshPlant()
    {
        super(Material.PLANTS, MapColor.RED);
        this.setCreativeTab(CommonProxy.Pollution_TAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(UP, Boolean.valueOf(false)).withProperty(DOWN, Boolean.valueOf(false)));
        this.setRegistryName(MODID,"flesh_plant");
        this.setTranslationKey("fleshPlant");
        this.setHardness(0.6F);
        this.setSoundType(SoundType.WOOD);
    }


    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos.down()).getBlock();
        Block block1 = worldIn.getBlockState(pos.up()).getBlock();
        Block block2 = worldIn.getBlockState(pos.north()).getBlock();
        Block block3 = worldIn.getBlockState(pos.east()).getBlock();
        Block block4 = worldIn.getBlockState(pos.south()).getBlock();
        Block block5 = worldIn.getBlockState(pos.west()).getBlock();
        return state.withProperty(DOWN, Boolean.valueOf(block == this || block == PollutionBlocksInit.BLOCK_FLESH_FLOWER
                || block == BlocksTC.fleshBlock))
                .withProperty(UP, Boolean.valueOf(block1 == this || block1 == PollutionBlocksInit.BLOCK_FLESH_FLOWER))
                .withProperty(NORTH, Boolean.valueOf(block2 == this || block2 == PollutionBlocksInit.BLOCK_FLESH_FLOWER))
                .withProperty(EAST, Boolean.valueOf(block3 == this || block3 == PollutionBlocksInit.BLOCK_FLESH_FLOWER))
                .withProperty(SOUTH, Boolean.valueOf(block4 == this || block4 == PollutionBlocksInit.BLOCK_FLESH_FLOWER))
                .withProperty(WEST, Boolean.valueOf(block5 == this || block5 == PollutionBlocksInit.BLOCK_FLESH_FLOWER));
    }


    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = state.getActualState(source, pos);
        float f = 0.1875F;
        float f1 = state.getValue(WEST).booleanValue() ? 0.0F : 0.1875F;
        float f2 = state.getValue(DOWN).booleanValue() ? 0.0F : 0.1875F;
        float f3 = state.getValue(NORTH).booleanValue() ? 0.0F : 0.1875F;
        float f4 = state.getValue(EAST).booleanValue() ? 1.0F : 0.8125F;
        float f5 = state.getValue(UP).booleanValue() ? 1.0F : 0.8125F;
        float f6 = state.getValue(SOUTH).booleanValue() ? 1.0F : 0.8125F;
        return new AxisAlignedBB(f1, f2, f3, f4, f5, f6);
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        if (!isActualState)
        {
            state = state.getActualState(worldIn, pos);
        }

        float f = 0.1875F;
        float f1 = 0.8125F;
        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.8125D, 0.8125D));

        if (state.getValue(WEST).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.0D, 0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.8125D));
        }

        if (state.getValue(EAST).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.8125D, 0.1875D, 0.1875D, 1.0D, 0.8125D, 0.8125D));
        }

        if (((Boolean)state.getValue(UP)).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.1875D, 0.8125D, 0.1875D, 0.8125D, 1.0D, 0.8125D));
        }

        if (((Boolean)state.getValue(DOWN)).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.1875D, 0.8125D));
        }

        if (((Boolean)state.getValue(NORTH)).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.1875D, 0.1875D, 0.0D, 0.8125D, 0.8125D, 0.1875D));
        }

        if (((Boolean)state.getValue(SOUTH)).booleanValue())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.1875D, 0.1875D, 0.8125D, 0.8125D, 0.8125D, 1.0D));
        }
    }


    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!this.canSurviveAt(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(Items.ROTTEN_FLESH,RandomNum.nextInt(3)));
        list.add(POUtils.getItemStack("<gregtech:meta_dust:1616>",(long)RandomNum.nextInt(3)));

        for (var item : list)
        {
            worldIn.spawnEntity( new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, item));
        }

    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }


    public int quantityDropped(Random random)
    {
        return 0;
    }


    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }


    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) ? this.canSurviveAt(worldIn, pos) : false;
    }


    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.canSurviveAt(worldIn, pos))
        {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }

    public boolean canSurviveAt(World wordIn, BlockPos pos)
    {
        boolean flag = wordIn.isAirBlock(pos.up());
        boolean flag1 = wordIn.isAirBlock(pos.down());

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos.offset(enumfacing);
            Block block = wordIn.getBlockState(blockpos).getBlock();

            if (block == this)
            {
                if (!flag && !flag1)
                {
                    return false;
                }

                Block block1 = wordIn.getBlockState(blockpos.down()).getBlock();

                if (block1 == this || block1 == BlocksTC.fleshBlock)
                {
                    return true;
                }
            }
        }

        Block block2 = wordIn.getBlockState(pos.down()).getBlock();
        return block2 == this || block2 == BlocksTC.fleshBlock;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {NORTH, EAST, SOUTH, WEST, UP, DOWN});
    }


    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return false;
    }


    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }


    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        Block block = blockAccess.getBlockState(pos.offset(side)).getBlock();
        return block != this && block != PollutionBlocksInit.BLOCK_FLESH_FLOWER && (side != EnumFacing.DOWN || block != BlocksTC.fleshBlock);
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}