package meowmel.pollution.common.block.blocks;

import gregtech.api.items.toolitem.ToolClasses;
import meowmel.pollution.common.block.tile.TileEntityStarstreamChunkAnchor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class POStarstreamChunkAnchor extends Block {

    public POStarstreamChunkAnchor() {
        super(Material.IRON);
        setTranslationKey("starstream_chunk_anchor");
        setHardness(12.0F);
        setResistance(80.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel(ToolClasses.PICKAXE, 3);
        setLightLevel(0.75F);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityStarstreamChunkAnchor();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            boolean active = tile instanceof TileEntityStarstreamChunkAnchor
                    && ((TileEntityStarstreamChunkAnchor) tile).isTicketActive();
            player.sendStatusMessage(new TextComponentTranslation(active
                    ? "pollution.starstream_chunk_anchor.active"
                    : "pollution.starstream_chunk_anchor.failed"), true);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityStarstreamChunkAnchor) {
            ((TileEntityStarstreamChunkAnchor) tile).releaseTicket();
        }
        super.breakBlock(world, pos, state);
    }
}
