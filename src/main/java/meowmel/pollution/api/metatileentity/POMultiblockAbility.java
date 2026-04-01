package meowmel.pollution.api.metatileentity;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import meowmel.pollution.api.capability.IManaHatch;
import meowmel.pollution.api.capability.IVisHatch;
import net.minecraftforge.fluids.IFluidTank;

@SuppressWarnings("InstantiationOfUtilityClass")
public final class POMultiblockAbility {
    public static final MultiblockAbility<IVisHatch> VIS_HATCH = new MultiblockAbility<>("vis_hatch", IVisHatch.class);

    public static final MultiblockAbility<IFluidTank> INFUSED_FLUID_HATCH = new MultiblockAbility<>("infused_fluid_hatch", IFluidTank.class);

    // 输入魔力
    public static final MultiblockAbility<IEnergyContainer> MANA_HATCH = new MultiblockAbility<>("mana_hatch", IEnergyContainer.class);
    // 输出魔力
    public static final MultiblockAbility<IEnergyContainer> MANA_POOL_HATCH = new MultiblockAbility<>("mana_pool_hatch", IEnergyContainer.class);

    private POMultiblockAbility() {
    }

}