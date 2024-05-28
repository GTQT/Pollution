package keqing.pollution.api.metatileentity;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import keqing.pollution.api.capability.ITankHatch;
import keqing.pollution.api.capability.IVisHatch;

@SuppressWarnings("InstantiationOfUtilityClass")
public final class POMultiblockAbility {
    public static final MultiblockAbility<IVisHatch> VIS_HATCH = new MultiblockAbility<>("vis_hatch");
    public static final MultiblockAbility<ITankHatch> TANK_HATCH = new MultiblockAbility<>("tank_hatch");
    private POMultiblockAbility() {}

}