package meowmel.pollution.api.metatileentity;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.capability.IVisHatch;

@SuppressWarnings("InstantiationOfUtilityClass")
public final class POMultiblockAbility {
    public static final MultiblockAbility<IVisHatch> VIS_HATCH = new MultiblockAbility<>("vis_hatch", IVisHatch.class);

    // 输入魔力
    public static final MultiblockAbility<IManaHatch> MANA_HATCH = new MultiblockAbility<>("mana_hatch", IManaHatch.class);
    // 输出魔力
    public static final MultiblockAbility<IManaHatch> MANA_POOL_HATCH = new MultiblockAbility<>("mana_pool_hatch", IManaHatch.class);

    private POMultiblockAbility() {
    }

}