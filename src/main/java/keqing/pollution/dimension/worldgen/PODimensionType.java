package keqing.pollution.dimension.worldgen;

import keqing.pollution.dimension.dims.DimensionDemiplane;
import net.minecraft.world.DimensionType;

public class PODimensionType {
	public static DimensionType DEMIPLANE;

	public static void init() {
		DEMIPLANE = DimensionType.register("the_demiplane", "_demiplane", 527, DimensionDemiplane.class, false);
	}

}
