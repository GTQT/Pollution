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

    // 依旧属于电力系统部分，例如配方类多方块，发电机
    // 可接受魔力作为输入的能源仓，本质上是能源仓
    public static final MultiblockAbility<IEnergyContainer> MANA_INPUT_HATCH = new MultiblockAbility<>("mana_input_hatch", IEnergyContainer.class);
    // 可接受魔力作为输出的动力仓，本质上是能源仓
    public static final MultiblockAbility<IEnergyContainer> MANA_OUTPUT_HATCH = new MultiblockAbility<>("mana_output_hatch", IEnergyContainer.class);

    // 纯魔法设备用，不与电力系统挂狗
    // 输入魔力
    public static final MultiblockAbility<IManaHatch> MANA_INPUT_POOL = new MultiblockAbility<>("mana_input_pool", IManaHatch.class);
    // 输出魔力
    public static final MultiblockAbility<IManaHatch> MANA_OUTPUT_POOL = new MultiblockAbility<>("mana_output_pool", IManaHatch.class);


    private POMultiblockAbility() {
    }

}