package keqing.pollution;

import keqing.gtqtcore.GTQTCoreConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = Pollution.MODID)
public class POConfig {

    public static WorldSettingSwitch WorldSettingSwitch = new WorldSettingSwitch();
    public static MachineSettingSwitch MachineSettingSwitch = new MachineSettingSwitch();
    public static PollutionSystemSwitch PollutionSystemSwitch = new PollutionSystemSwitch();
    public static OBJRenderSwitch OBJRenderSwitch = new OBJRenderSwitch();

    public static class OBJRenderSwitch {
        @Config.Comment({"节点聚变反应堆OBJ模型渲染开启"})
        @Config.RequiresMcRestart
        @Config.Name("Enable obj Model Node Fusion Reactor")
        public boolean EnableObjNodeFusionReactor = true;
    }
    public static class WorldSettingSwitch {
        @Config.RequiresMcRestart
        @Config.Comment("为交错底世界维度分配的ID号。如果与其他模组冲突，请更改。")
        public int BTNetherDimensionID = 41;
        @Config.Comment("可以始终前往交错底世界的维度，以及返回的维度。默认为交错次元。")
        public int originDimension = 20;
        @Config.Comment("允许在“交错次元”维度之外创建前往交错底世界的传送门。这可能被视为作弊。")
        public boolean allowPortalsInOtherDimensions = false;
        @Config.Comment("如果为假，则返回传送门需要激活物品。")
        public boolean shouldReturnPortalBeUsable = true;
        @Config.Comment("确定新传送门是否应预先检查安全性。如果启用，传送门将失败形成，而不是重定向到安全的替代目的地。" +
                "\n请注意，启用此功能也会降低传送门形成检查的频率。")
        public boolean checkPortalDestination = false;
    }

    public static class PollutionSystemSwitch {
        @Config.Comment("机器污染开关")
        public boolean enablePollution = true;
        @Config.Comment("机器爆炸污染")
        public boolean ExplosionPollution = true;
        @Config.Comment("玩家污染DEBUFF(包括泥土变沙子，药水效果)")
        public boolean EntityPollutionEvent = true;
        @Config.Comment("设备污染倍率(0为无污染)")
        public float mufflerPollutionMultiplier = 1.0F;
        @Config.Comment("是否开启消声仓污染特效")
        public boolean mufflerPollutionShowEffects = true;
        @Config.Comment("污染清理倍率,")
        public double fluxScrubberMultiplier = 0.002;
        @Config.Comment("灵气发电机单位灵气发电量")
        public float visGeneratorEuPerVis = 250.0f;
        @Config.Comment("灵气发电机污染倍率默认")
        public float visGeneratorPollutionMultiplier = 0.1f;
        @Config.Comment("灵气发电机污染特效")
        public boolean visGeneratorPollutionShowEffects = true;
        @Config.Comment("灵气发生器灵气生成倍率")
        public double visProviderMultiplier = 0.05;
        @Config.Comment("咒波促燃发电机单位tick发电消耗咒波")
        public float FluxPromotedGeneratorFluxPerTick = 0.005F;
    }

    public static class MachineSettingSwitch {

        @Config.Comment("气态魔力废液每mb的EU燃值")
        public double EuPerMbKqMagicRub = 256.0;
        @Config.Comment("气态离散态魔力每mb的EU燃值")
        public double EuPerMbKqMagicGas = 256.0;
        @Config.Comment("气态离散态魔力每mb的EU燃值")
        public double EuPerMbKqMagicFas = 512.0;
        @Config.Comment("气态离散态魔力每mb的EU燃值")
        public double EuPerMbKqMagicDas = 1024.0;
        @Config.Comment("气态离散态魔力每mb的EU燃值")
        public double EuPerMbKqMagicAas = 2048.0;
        @Config.Comment("自然魔力每mb的EU燃值")
        public double EuPerMbMagicKq = 8192.0;
        @Config.Comment("富集自然魔力每mb的EU燃值")
        public double EuPerMbRichMagicKq = 32768.0;
    }
}