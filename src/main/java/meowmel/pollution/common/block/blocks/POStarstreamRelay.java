package meowmel.pollution.common.block.blocks;

import gregtech.api.items.toolitem.ToolClasses;
import meowmel.pollution.common.block.tile.TileEntityStarstreamRelay;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

/** A placeable, chainable node for constellation-energy routes. */
@ParametersAreNonnullByDefault
public class POStarstreamRelay extends Block {

    public POStarstreamRelay() {
        super(Material.IRON);
        setTranslationKey("starstream_relay");
        setHardness(8.0F);
        setResistance(40.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel(ToolClasses.PICKAXE, 3);
        setLightLevel(0.65F);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityStarstreamRelay();
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 10;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityStarstreamRelay
                && ((TileEntityStarstreamRelay) tile).onRelayRightClick(player);
    }
}
