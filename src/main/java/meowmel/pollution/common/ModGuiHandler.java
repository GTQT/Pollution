package meowmel.pollution.common;

import meowmel.pollution.client.gui.GuiMineralExtractor;
import meowmel.pollution.common.block.tile.ContainerMineralExtractor;
import meowmel.pollution.common.block.tile.TileEntityMineralExtractor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {

    public static final int GUI_MINERAL_EXTRACTOR = 0;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == GUI_MINERAL_EXTRACTOR) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityMineralExtractor) {
                return new ContainerMineralExtractor(player.inventory, (TileEntityMineralExtractor) te);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == GUI_MINERAL_EXTRACTOR) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityMineralExtractor) {
                return new GuiMineralExtractor(player.inventory, (TileEntityMineralExtractor) te);
            }
        }
        return null;
    }
}
