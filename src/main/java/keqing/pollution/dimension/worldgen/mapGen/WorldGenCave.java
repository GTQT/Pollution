package keqing.pollution.dimension.worldgen.mapGen;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public abstract class WorldGenCave extends WorldGenerator {
    protected final EnumFacing[] directions = { EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST };

    public WorldGenCave(boolean doBlockNotify) {
        super(doBlockNotify);
    }




    protected class PlantLocation {
        private BlockPos pos;

        private int height;

        public PlantLocation(World world, BlockPos pos) {
            this.setPos(pos);
            setHeight(1);
            while (world.isAirBlock(pos.add(0, -getHeight(), 0)) && (pos.getY() - getHeight()) > 0) {
                setHeight(getHeight() + 1);
            }
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public BlockPos getPos() {
            return pos;
        }

        public void setPos(BlockPos pos) {
            this.pos = pos;
        }
    }
}