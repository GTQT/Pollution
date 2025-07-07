package keqing.pollution.dimension.worldgen;

import keqing.pollution.dimension.dims.UnderWorlds;
import keqing.pollution.dimension.dims.DimensionDemiplane;
import net.minecraft.world.DimensionType;

public class PODimensionType {
	public static DimensionType DEMIPLANE;
	public static DimensionType UNDER_WORLD;
	public static void init() {
		DEMIPLANE = DimensionType.register("the_demiplane", "_demiplane", 40, DimensionDemiplane.class, false);
		UNDER_WORLD = DimensionType.register("the_btn", "_btn", 41, UnderWorlds.class, false);
	}

}
