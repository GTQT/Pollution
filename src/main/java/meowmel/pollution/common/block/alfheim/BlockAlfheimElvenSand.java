package meowmel.pollution.common.block.alfheim;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

/** Java port of Alfheim's falling elven sand block. */
public final class BlockAlfheimElvenSand extends BlockFalling {

    public BlockAlfheimElvenSand() {
        super(Material.SAND);
        setHardness(0.5F);
        setSoundType(SoundType.SAND);
        setHarvestLevel("shovel", 0);
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos,
                                   EnumFacing direction, IPlantable plantable) {
        EnumPlantType type = plantable.getPlantType(world, pos.up());
        if (type == EnumPlantType.Desert) {
            return true;
        }
        if (type == EnumPlantType.Beach) {
            return isWater(world, pos.west()) || isWater(world, pos.east())
                    || isWater(world, pos.north()) || isWater(world, pos.south());
        }
        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    private static boolean isWater(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER;
    }
}
