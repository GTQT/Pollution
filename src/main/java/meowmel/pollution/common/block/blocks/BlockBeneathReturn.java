package meowmel.pollution.common.block.blocks;

import meowmel.pollution.Pollution;
import meowmel.pollution.api.utils.POBeneathTeleporter;
import meowmel.pollution.common.block.tile.TileEntityBeneathReturnPad;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * 地下世界落点平台中央的返程传送器方块。
 *
 * <p>由 {@link POBeneathTeleporter} 在玩家进入地下世界时生成在平台上，读取
 * {@link TileEntityBeneathReturnPad} 记录的返程坐标，右键把玩家送回去。</p>
 */
public class BlockBeneathReturn extends Block implements ITileEntityProvider {

    /** 四分之一格高的板子，玩家可以直接踩过去。 */
    private static final AxisAlignedBB PAD_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);

    public BlockBeneathReturn() {
        super(Material.ROCK);
        setRegistryName(Pollution.MODID, "beneath_return_pad");
        setTranslationKey(Pollution.MODID + ".beneath_return_pad");
        // 挖不掉：这是地下世界唯一的回程手段，被破坏（尤其是被爆炸掀掉）就再也回不来了
        setBlockUnbreakable();
        setResistance(Float.MAX_VALUE);
        setLightLevel(0.6F);
    }

    @Override
    @Deprecated
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;

        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileEntityBeneathReturnPad returnPad) || !returnPad.hasReturnTarget()) {
            playerIn.sendStatusMessage(new TextComponentTranslation("pollution.beneath_trans.return_unset"), true);
            return true;
        }

        POBeneathTeleporter.sendBack(playerIn, returnPad.getReturnDimension(), returnPad.getReturnPos());
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBeneathReturnPad();
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return PAD_BOX;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}