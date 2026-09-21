package meowmel.pollution.api.capability.ipml;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import meowmel.pollution.POConfig;
import meowmel.pollution.common.metatileentity.single.MetaTileEntityAuraGenerator;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

/**
 * 灵气发电机（{@link MetaTileEntityAuraGenerator}）的运行逻辑。
 * <p>
 * 把“按缓存空余直接发电，并按发电量产生咒波”的机制从机器里抽出来，
 * 机器本体只保留 Capability、贴图与提示。
 */
public class VisGeneratorLogic {

    /** 所属机器 */
    protected final MetaTileEntityAuraGenerator metaTileEntity;

    /** 单位灵气发电量 */
    private final float euPerVis;
    /** 发电产生的污染倍率 */
    private final float pollutionMultiplier;
    /** 是否播放污染特效 */
    private final boolean pollutionShowEffects;
    /** 该等级满速时每秒消耗的灵气，供提示显示 */
    private final double visPerTick;

    /** 上一次发电量与产生的污染，供 UI/TOP 显示 */
    private long generatedEU;
    private float generatedPollution;

    public VisGeneratorLogic(MetaTileEntityAuraGenerator metaTileEntity, int tier) {
        this.metaTileEntity = metaTileEntity;
        this.euPerVis = POConfig.PollutionSystemSwitch.visGeneratorEuPerVis;
        this.pollutionMultiplier = POConfig.PollutionSystemSwitch.visGeneratorPollutionMultiplier;
        this.pollutionShowEffects = POConfig.PollutionSystemSwitch.visGeneratorPollutionShowEffects;
        this.visPerTick = GTValues.VA[tier] / euPerVis;
    }

    /**
     * 执行一次发电，应当在机器的 update 中每 tick 调用
     */
    public void performGeneration() {
        World world = metaTileEntity.getWorld();
        if (world == null || world.isRemote) return;

        IEnergyContainer energy = metaTileEntity.getEnergyContainer();
        long missingEnergy = energy.getEnergyCapacity() - energy.getEnergyStored();
        if (missingEnergy <= 0) {
            this.generatedEU = 0L;
            this.generatedPollution = 0.0f;
            return;
        }

        // 把缓存补满，再按补入的电量折算成咒波
        float drainedVis = missingEnergy / euPerVis;
        this.generatedEU = missingEnergy;
        this.generatedPollution = drainedVis * pollutionMultiplier;

        energy.changeEnergy(missingEnergy);
        AuraHelper.polluteAura(world, metaTileEntity.getPos(), this.generatedPollution, pollutionShowEffects);
    }

    /**
     * @return 单位灵气发电量
     */
    public float getEuPerVis() {
        return euPerVis;
    }

    /**
     * @return 该等级满速时每秒消耗的灵气
     */
    public double getVisPerTick() {
        return visPerTick;
    }

    /**
     * @return 上一次发电量
     */
    public long getGeneratedEU() {
        return generatedEU;
    }

    /**
     * @return 上一次产生的污染
     */
    public float getGeneratedPollution() {
        return generatedPollution;
    }
}
