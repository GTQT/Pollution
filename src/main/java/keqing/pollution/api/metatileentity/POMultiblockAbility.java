package keqing.pollution.api.metatileentity;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.capability.IVisHatch;

@SuppressWarnings("InstantiationOfUtilityClass")
public final class POMultiblockAbility {
    public static final MultiblockAbility<IVisHatch> VIS_HATCH = new MultiblockAbility<>("vis_hatch", IVisHatch.class);
    public static final MultiblockAbility<IManaHatch> MANA_HATCH = new MultiblockAbility<>("mana_hatch", IManaHatch.class);
    public static final MultiblockAbility<IManaHatch> MANA_POOL_HATCH = new MultiblockAbility<>("mana_pool_hatch", IManaHatch.class);

    private POMultiblockAbility() {
    }

}