package keqing.pollution.api.metatileentity;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.capability.ITankHatch;
import keqing.pollution.api.capability.IVisHatch;
import vazkii.botania.api.mana.IManaPool;

@SuppressWarnings("InstantiationOfUtilityClass")
public final class POMultiblockAbility {
    public static final MultiblockAbility<IVisHatch> VIS_HATCH = new MultiblockAbility<>("vis_hatch");
    public static final MultiblockAbility<ITankHatch> TANK_HATCH = new MultiblockAbility<>("tank_hatch");
    public static final MultiblockAbility<IManaHatch> MANA_HATCH = new MultiblockAbility<>("mana_hatch");
    public static final MultiblockAbility<IManaHatch> MANA_POOL_HATCH = new MultiblockAbility<>("mana_pool_hatch");
    private POMultiblockAbility() {}

}