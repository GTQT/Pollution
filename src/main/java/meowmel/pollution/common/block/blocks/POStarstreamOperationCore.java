package meowmel.pollution.common.block.blocks;

import gregtech.api.items.toolitem.ToolClasses;
import meowmel.pollution.common.block.tile.TileEntityStarstreamOperationCore;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/** Pedestal and moving spherical receiver for endgame constellation devices. */
@ParametersAreNonnullByDefault
public class POStarstreamOperationCore extends Block {

    private static final AxisAlignedBB SELECTION_BOX =
            new AxisAlignedBB(0.10D, 0.0D, 0.10D, 0.90D, 1.95D, 0.90D);
    private static final AxisAlignedBB COLLISION_BOX =
            new AxisAlignedBB(0.10D, 0.0D, 0.10D, 0.90D, 0.45D, 0.90D);

    public POStarstreamOperationCore() {
        super(Material.IRON);
        setTranslationKey("starstream_operation_core");
        setHardness(12.0F);
        setResistance(80.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel(ToolClasses.PICKAXE, 3);
        setLightLevel(1.0F);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityStarstreamOperationCore();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SELECTION_BOX;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return COLLISION_BOX;
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 15;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityStarstreamOperationCore
                && ((TileEntityStarstreamOperationCore) tile).onCoreRightClick(player);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world,
                               List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format("tile.starstream_operation_core.tooltip.1"));
        tooltip.add(I18n.format("tile.starstream_operation_core.tooltip.2"));
        tooltip.add(I18n.format("tile.starstream_operation_core.tooltip.3"));
    }
}
