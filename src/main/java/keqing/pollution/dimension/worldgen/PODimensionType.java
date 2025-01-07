package keqing.pollution.dimension.worldgen;

import keqing.pollution.dimension.dims.BetweenLandNether;
import keqing.pollution.dimension.dims.DimensionDemiplane;
import net.minecraft.world.DimensionType;

public class PODimensionType {
	public static DimensionType DEMIPLANE;
	public static DimensionType BTM;
	public static void init() {
		DEMIPLANE = DimensionType.register("the_demiplane", "_demiplane", 40, DimensionDemiplane.class, false);
		BTM= DimensionType.register("the_btn", "_btn", 41, BetweenLandNether.class, false);
	}

}
