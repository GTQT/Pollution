package keqing.pollution.common.block.blocks;

import keqing.pollution.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;

import javax.annotation.Nullable;
import java.util.Random;

import static keqing.pollution.Pollution.MODID;

public class BlockFleshFlower extends Block
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    protected BlockFleshFlower()
    {
        super(Material.PLANTS, MapColor.RED);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
        this.setCreativeTab(CommonProxy.Pollution_TAB);
        this.setTickRandomly(true);
        this.setRegistryName(MODID,"flesh_flower");
        this.setTranslationKey("fleshFlower");
        this.setHardness(0.6F);
        this.setSoundType(SoundType.WOOD);
    }


    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!this.canSurvive(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
        else
        {
            BlockPos blockpos = pos.up();

            if (worldIn.isAirBlock(blockpos) && blockpos.getY() < 256)
            {
                int i = ((Integer)state.getValue(AGE)).intValue();

                if (i < 7 &&  net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, rand.nextInt(1) == 0))
                {
                    boolean flag = false;
                    boolean flag1 = false;
                    IBlockState iblockstate = worldIn.getBlockState(pos.down());
                    Block block = iblockstate.getBlock();

                    if (block == BlocksTC.fleshBlock)
                    {
                        flag = true;
                    }
                    else if (block == PollutionBlocksInit.BLOCK_FLESH_PLANT)
                    {
                        int j = 1;

                        for (int k = 0; k < 4; ++k)
                        {
                            Block block1 = worldIn.getBlockState(pos.down(j + 1)).getBlock();

                            if (block1 != PollutionBlocksInit.BLOCK_FLESH_PLANT)
                            {
                                if (block1 == BlocksTC.fleshBlock)
                                {
                                    flag1 = true;
                                }

                                break;
                            }

                            ++j;
                        }

                        int i1 = 4;

                        if (flag1)
                        {
                            ++i1;
                        }

                        if (j < 2 || rand.nextInt(i1) >= j)
                        {
                            flag = true;
                        }
                    }
                    else if (iblockstate.getMaterial() == Material.AIR)
                    {
                        flag = true;
                    }

                    if (flag && areAllNeighborsEmpty(worldIn, blockpos, (EnumFacing)null) && worldIn.isAirBlock(pos.up(2)))
                    {
                        worldIn.setBlockState(pos, PollutionBlocksInit.BLOCK_FLESH_PLANT.getDefaultState(), 2);
                        this.placeGrownFlower(worldIn, blockpos, i);
                    }
                    else if (i < 4)
                    {
                        int l = rand.nextInt(4);
                        boolean flag2 = false;

                        if (flag1)
                        {
                            ++l;
                        }

                        for (int j1 = 0; j1 < l; ++j1)
                        {
                            EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
                            BlockPos blockpos1 = pos.offset(enumfacing);

                            if (worldIn.isAirBlock(blockpos1) && worldIn.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(worldIn, blockpos1, enumfacing.getOpposite()))
                            {
                                this.placeGrownFlower(worldIn, blockpos1, i + 1);
                                flag2 = true;
                            }
                        }

                        if (flag2)
                        {
                            worldIn.setBlockState(pos, PollutionBlocksInit.BLOCK_FLESH_PLANT.getDefaultState(), 2);
                        }
                        else
                        {
                            this.placeDeadFlower(worldIn, pos);
                        }
                    }
                    else if (i == 4)
                    {
                        this.placeDeadFlower(worldIn, pos);
                    }
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
            }
        }
    }

    private void placeGrownFlower(World worldIn, BlockPos pos, int age)
    {
        worldIn.setBlockState(pos, this.getDefaultState().withProperty(AGE, Integer.valueOf(age)), 2);
        worldIn.playEvent(1033, pos, 0);
    }

    private void placeDeadFlower(World worldIn, BlockPos pos)
    {
        worldIn.setBlockState(pos, this.getDefaultState().withProperty(AGE, Integer.valueOf(7)), 2);
        worldIn.playEvent(1034, pos, 0);
    }

    private static boolean areAllNeighborsEmpty(World worldIn, BlockPos pos, EnumFacing excludingSide)
    {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            if (enumfacing != excludingSide && !worldIn.isAirBlock(pos.offset(enumfacing)))
            {
                return false;
            }
        }

        return true;
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
        return super.canPlaceBlockAt(worldIn, pos) && this.canSurvive(worldIn, pos);
    }


    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!this.canSurvive(worldIn, pos))
        {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }

    public boolean canSurvive(World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos.down());
        Block block = iblockstate.getBlock();

        if (block != PollutionBlocksInit.BLOCK_FLESH_PLANT && block != BlocksTC.fleshBlock)
        {
            if (iblockstate.getMaterial() == Material.AIR)
            {
                int i = 0;

                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                {
                    IBlockState iblockstate1 = worldIn.getBlockState(pos.offset(enumfacing));
                    Block block1 = iblockstate1.getBlock();

                    if (block1 == PollutionBlocksInit.BLOCK_FLESH_PLANT)
                    {
                        ++i;
                    }
                    else if (iblockstate1.getMaterial() != Material.AIR)
                    {
                        return false;
                    }
                }

                return i == 1;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }


    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(this)));
    }

    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return ItemStack.EMPTY;
    }


    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }


    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }


    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
    }

    public static void generatePlant(World worldIn, BlockPos pos, Random rand, int radius)
    {
        worldIn.setBlockState(pos, PollutionBlocksInit.BLOCK_FLESH_PLANT.getDefaultState(), 2);
        growTreeRecursive(worldIn, pos, rand, pos, radius, 0);
    }

    private static void growTreeRecursive(World worldIn, BlockPos pos, Random rand, BlockPos origin, int radius, int depth)
    {
        int i = rand.nextInt(6) + 3;  //主干长度3~8格

        if (depth == 0)
        {
            i += 2;  //第一层更高
        }

        for (int j = 0; j < i; ++j)
        {
            BlockPos upPos = pos.up(j + 1);

            if (!areAllNeighborsEmpty(worldIn, upPos, null))
            {
                return;
            }

            worldIn.setBlockState(upPos, PollutionBlocksInit.BLOCK_FLESH_PLANT.getDefaultState(), 2);
        }

        boolean grewBranch = false;

        if (depth < 6)  //允许更深层分支
        {
            int branchCount = rand.nextInt(6) + 3;  //更多分支

            if (depth == 0)
            {
                branchCount += 2;
            }

            for (int k = 0; k < branchCount; ++k)
            {
                EnumFacing facing = EnumFacing.Plane.HORIZONTAL.random(rand);
                BlockPos branchPos = pos.up(i).offset(facing);

                if (Math.abs(branchPos.getX() - origin.getX()) < radius && Math.abs(branchPos.getZ() - origin.getZ()) < radius
                        && worldIn.isAirBlock(branchPos) && worldIn.isAirBlock(branchPos.down())
                        && areAllNeighborsEmpty(worldIn, branchPos, facing.getOpposite()))
                {
                    grewBranch = true;
                    worldIn.setBlockState(branchPos, PollutionBlocksInit.BLOCK_FLESH_PLANT.getDefaultState(), 2);
                    growTreeRecursive(worldIn, branchPos, rand, origin, radius, depth + 1);
                }
            }
        }

        if (!grewBranch)
        {
            worldIn.setBlockState(pos.up(i), PollutionBlocksInit.BLOCK_FLESH_FLOWER.getDefaultState().withProperty(AGE, 7), 2);
        }
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}