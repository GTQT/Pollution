package meowmel.pollution.dimension.worldgen.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 洞底定位包装器：把目标生成器（树/大蘑菇等需要"地面"的装饰）先定位到
 * 当前列的洞底（先空气后实心，跳过流体），再调用目标生成器。
 * 解决"装饰位置随机 y 导致树/蘑菇悬空或埋入岩体"的问题。
 */
public class WorldGenOnCaveFloor extends WorldGenerator {

    private final WorldGenerator delegate;

    public WorldGenOnCaveFloor(WorldGenerator delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        boolean seenAir = false;
        for (int y = 244; y >= 5; --y) {
            BlockPos pos = new BlockPos(position.getX(), y, position.getZ());
            if (worldIn.isAirBlock(pos)) {
                seenAir = true;
            } else if (seenAir && worldIn.getBlockState(pos).getMaterial().isSolid()) {
                return delegate.generate(worldIn, rand, pos.up());
            }
        }
        return false;
    }
}
