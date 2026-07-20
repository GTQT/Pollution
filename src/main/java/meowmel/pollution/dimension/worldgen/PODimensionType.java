package meowmel.pollution.dimension.worldgen;

import meowmel.pollution.dimension.dims.BloodWorld;
import meowmel.pollution.dimension.dims.UndergroundWorlds;
import meowmel.pollution.dimension.dims.DimensionDemiplane;
import meowmel.pollution.dimension.dims.AlfheimWorld;
import meowmel.pollution.POConfig;
import net.minecraft.world.DimensionType;

public class PODimensionType {
	public static DimensionType DEMIPLANE;
	public static DimensionType UNDER_WORLD;
    public static DimensionType BLOOD_WORLD;
    public static DimensionType ALFHEIM_WORLD;
	public static void init() {
        DEMIPLANE = DimensionType.register("the_demiplane", "_demiplane", 40, DimensionDemiplane.class, false);
        UNDER_WORLD = DimensionType.register("the_underground", "_underground", 41, UndergroundWorlds.class, false);
        BLOOD_WORLD = DimensionType.register("the_blood", "_blood", 42, BloodWorld.class, false);
        ALFHEIM_WORLD = DimensionType.register("alfheim", "_alfheim", POConfig.WorldSettingSwitch.AlfheimDimensionID, AlfheimWorld.class, false);
    }
}
