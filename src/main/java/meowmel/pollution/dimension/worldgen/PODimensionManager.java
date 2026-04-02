package meowmel.pollution.dimension.worldgen;

import meowmel.pollution.dimension.dims.BloodWorld;
import meowmel.pollution.dimension.dims.UndergroundWorlds;
import meowmel.pollution.dimension.dims.DimensionDemiplane;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class PODimensionManager {
	//这个是维度管理器，拿来注册的
	public static int DEMIPLANE_DIM_ID;
    public static int UNDERGROUND_DIM_ID;
    public static int BLOOD_DIM_ID;

	public static void init() {
		// 这里注册你的维度ID和维度类型
		DEMIPLANE_DIM_ID = 40;
		DimensionManager.registerDimension(DEMIPLANE_DIM_ID, DimensionType.register("the_demiplane", "_demiplane", DEMIPLANE_DIM_ID, DimensionDemiplane.class, false));

        UNDERGROUND_DIM_ID = 41;
		DimensionManager.registerDimension(UNDERGROUND_DIM_ID, DimensionType.register("the_underground", "_underground", UNDERGROUND_DIM_ID, UndergroundWorlds.class, false));

        BLOOD_DIM_ID = 42;
        DimensionManager.registerDimension(BLOOD_DIM_ID, DimensionType.register("the_blood", "_blood", BLOOD_DIM_ID, BloodWorld.class, false));
	}
}
