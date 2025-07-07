package keqing.pollution.dimension.worldgen;

import keqing.pollution.dimension.dims.UnderWorlds;
import keqing.pollution.dimension.dims.DimensionDemiplane;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class PODimensionManager {
	//这个是维度管理器，拿来注册的
	public static int DEMIPLANE_DIM_ID;
	public static int BTN_DIM_ID;
	public static void init() {
		// 这里注册你的维度ID和维度类型
		DEMIPLANE_DIM_ID = 40;
		DimensionManager.registerDimension(DEMIPLANE_DIM_ID, DimensionType.register("the_demiplane", "_demiplane", DEMIPLANE_DIM_ID, DimensionDemiplane.class, false));

		BTN_DIM_ID = 41;
		DimensionManager.registerDimension(BTN_DIM_ID, DimensionType.register("the_btn", "_btn", BTN_DIM_ID, UnderWorlds.class, false));

	}
}
