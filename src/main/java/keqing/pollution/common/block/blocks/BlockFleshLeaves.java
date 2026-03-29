package keqing.pollution.common.block.blocks;


import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import keqing.pollution.Pollution;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.tile.TileEntityFleshHeart;
import keqing.pollution.common.items.PollutionItemsInit;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * 血肉树叶 - 有概率在下方生长心鸣果或掉落腐肉
 */
public class BlockFleshLeaves extends BlockLeaves {

    public TileEntityFleshHeart heart;
    public BlockFleshLeaves() {
        super();
        setTranslationKey(Pollution.MODID + ".flesh_leaves");
        setRegistryName(Pollution.MODID, "flesh_leaves");
        setHardness(0.3F);
        setSoundType(SoundType.SLIME);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setDefaultState(this.blockState.getBaseState()
                .withProperty(CHECK_DECAY, false)
                .withProperty(DECAYABLE, false));
        setTickRandomly(true);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        if (worldIn.isRemote) return;
        if(this.heart==null)
            return;
        if(worldIn.getTileEntity(this.heart.getPos())==null)
        {
            this.heart=null;
            return;
        }
        BlockPos below = pos.down();
        // 只在下方是空气时才进行操作
        if (!worldIn.isAirBlock(below)) return;
        // 1% 概率在下方生长心鸣果（未成熟）
        if (random.nextInt(100) == 0 && this.heart!=null && this.heart.getLevel()==10) {
            IBlockState fruitState = PollutionBlocksInit.HEARTFRUIT.getDefaultState()
                    .withProperty(BlockHeartFruit.AGE, 0);
            worldIn.setBlockState(below, fruitState);
            return;
        }
        // 5% 概率掉落腐肉
        if (random.nextInt(20) == 0 && this.heart!=null && this.heart.getLevel()>=5) {
            if(heart.getLinkedItemHandler()!=null)
            {
                GTTransferUtils.insertItem(heart.getLinkedItemHandler(), new ItemStack(Items.ROTTEN_FLESH),false);
            }else
            {
                EntityItem entityItem = new EntityItem(worldIn,
                        below.getX() + 0.5, below.getY() + 0.5, below.getZ() + 0.5,
                        new ItemStack(Items.ROTTEN_FLESH));
                worldIn.spawnEntity(entityItem);
            }
        }
        // 5% 概率掉落原始血肉团
        if (random.nextInt(20) == 0 && this.heart!=null && this.heart.getLevel()>=8) {
            if(heart.getLinkedItemHandler()!=null)
            {
                GTTransferUtils.insertItem(heart.getLinkedItemHandler(), PollutionMetaItems.BLOOD_PRIMITIVE_MEAT.getStackForm(),false);
            }else
            {
                EntityItem entityItem = new EntityItem(worldIn,
                        below.getX() + 0.5, below.getY() + 0.5, below.getZ() + 0.5,
                        PollutionMetaItems.BLOOD_PRIMITIVE_MEAT.getStackForm());
                worldIn.spawnEntity(entityItem);
            }
        }
    }

    @Override
    @Nonnull
    public BlockPlanks.EnumType getWoodType(int meta) {
        return BlockPlanks.EnumType.OAK; // 必须实现，但不使用
    }

    @Override
    @Nonnull
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return java.util.Collections.singletonList(new ItemStack(this));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (state.getValue(DECAYABLE)) meta |= 1;
        if (state.getValue(CHECK_DECAY)) meta |= 2;
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(DECAYABLE, (meta & 1) != 0)
                .withProperty(CHECK_DECAY, (meta & 2) != 0);
    }
}
