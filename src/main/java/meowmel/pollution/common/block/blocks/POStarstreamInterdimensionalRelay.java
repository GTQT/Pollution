package meowmel.pollution.common.block.blocks;

import meowmel.pollution.common.block.tile.TileEntityInterdimensionalStarstreamRelay;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class POStarstreamInterdimensionalRelay extends POStarstreamRelay {

    public POStarstreamInterdimensionalRelay() {
        setTranslationKey("starstream_interdimensional_relay");
        setLightLevel(0.9F);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityInterdimensionalStarstreamRelay();
    }
}
