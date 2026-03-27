package keqing.pollution.common.block.blocks;


import keqing.pollution.Pollution;
import keqing.pollution.common.block.tile.TileEntityFleshHeart;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * 血肉心脏核心方块
 * 包含TileEntity，控制整棵树的生长
 * 发出心跳粒子效果
 */
public class BlockFleshHeart extends Block implements ITileEntityProvider {

    public BlockFleshHeart() {
        super(Material.SPONGE);
        setTranslationKey(Pollution.MODID + ".flesh_heart");
        setRegistryName(Pollution.MODID, "flesh_heart");
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.SLIME);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setLightLevel(0.3F); // 微弱发光
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFleshHeart();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                     EntityPlayer playerIn, EnumHand hand,
                                     EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityFleshHeart) {
                TileEntityFleshHeart heart = (TileEntityFleshHeart) te;
                int level = heart.getLevel();
                int maxLevel = TileEntityFleshHeart.MAX_LEVEL;
                String msg = TextFormatting.DARK_RED + "§l❤ " +
                        TextFormatting.RED + "血肉之树 " +
                        TextFormatting.GRAY + "等级: " +
                        TextFormatting.GOLD + level + "/" + maxLevel;
                if (level >= maxLevel) {
                    msg += TextFormatting.DARK_PURPLE + " [已成熟]";
                }
                playerIn.sendMessage(new TextComponentString(msg));
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        // 心脏被破坏时，树不会继续生长（但已有的方块保留）
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
