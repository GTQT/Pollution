package meowmel.pollution.api.capability.ipml;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import meowmel.pollution.POConfig;
import meowmel.pollution.common.metatileentity.single.MetaTileEntityVisProvider;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

/**
 * 灵气发生器（{@link MetaTileEntityVisProvider}）的运行逻辑。
 * <p>
 * 把“耗电向所在区块补充灵气，达到上限后停机”的机制从机器里抽出来，
 * 机器本体只保留 Capability、贴图与提示。
 */
public class VisProviderLogic {

    /** 区块灵气上限，达到后不再补充 */
    public static final float VIS_CAP = 400.0f;

    /** 所属机器 */
    protected final MetaTileEntityVisProvider metaTileEntity;

    /** 每 tick 消耗的能量 */
    private final long energyPerTick;
    /** 单位能量产生的灵气 */
    private final double visMultiplier;
    /** 该等级满速时每秒生成的灵气，供提示显示 */
    private final double visPerTick;

    public VisProviderLogic(MetaTileEntityVisProvider metaTileEntity, int tier) {
        this.metaTileEntity = metaTileEntity;
        this.energyPerTick = GTValues.V[tier];
        this.visMultiplier = POConfig.PollutionSystemSwitch.visProviderMultiplier;
        this.visPerTick = energyPerTick * visMultiplier;
    }

    /**
     * 执行一次灵气补充，应当在机器的 update 中每 tick 调用
     */
    public void performCharging() {
        World world = metaTileEntity.getWorld();
        if (world == null || world.isRemote) return;

        float currentVis = AuraHelper.getVis(world, metaTileEntity.getPos());

        float generatedVis;
        if (currentVis >= VIS_CAP) {
            return;
        }

        IEnergyContainer energy = metaTileEntity.getEnergyContainer();
        if (energy.getEnergyStored() < energyPerTick) {
            return;
        }

        energy.removeEnergy(energyPerTick);
        generatedVis = (float) (energyPerTick * visMultiplier);
        AuraHelper.addVis(world, metaTileEntity.getPos(), generatedVis);
    }

    /**
     * @return 该等级满速时每秒生成的灵气
     */
    public double getVisPerTick() {
        return visPerTick;
    }
}
