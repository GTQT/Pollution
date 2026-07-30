package meowmel.pollution.common.block.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * Intentionally empty display TileEntity.
 *
 * <p>Animation is derived from client render time in the TESR, so this update
 * method does not run gameplay or visual state logic.</p>
 */
public class TileEntityMineralExtractor extends TileEntity implements ITickable {

    @Override
    public void update() {
    }
}
