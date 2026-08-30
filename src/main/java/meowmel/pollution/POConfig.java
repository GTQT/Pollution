package meowmel.pollution;

import net.minecraftforge.common.config.Config;

@Config(modid = Pollution.MODID)
public class POConfig {

    public static WorldSettingSwitch WorldSettingSwitch = new WorldSettingSwitch();
    public static MachineSettingSwitch MachineSettingSwitch = new MachineSettingSwitch();
    public static PollutionSystemSwitch PollutionSystemSwitch = new PollutionSystemSwitch();
    public static OBJRenderSwitch OBJRenderSwitch = new OBJRenderSwitch();
    public static WarpEventSwitch WarpEventSwitch = new WarpEventSwitch();

    public static class OBJRenderSwitch {
        @Config.Comment({"节点聚变反应堆OBJ模型渲染开启"})
        @Config.RequiresMcRestart
        @Config.Name("Enable obj Model Node Fusion Reactor")
        public boolean EnableObjNodeFusionReactor = true;
    }
    public static class WorldSettingSwitch {
        @Config.RequiresMcRestart
        @Config.Comment("为地下世界维度分配的ID号。如果与其他模组冲突，请更改。")
        public int UndergroundDimensionID = 41;
        @Config.RequiresMcRestart
        @Config.Comment("Dimension ID used by the terrain-only Alfheim port.")
        public int AlfheimDimensionID = 43;
        @Config.Comment("Allow players to respawn in the terrain-only Alfheim dimension.")
        public boolean enableAlfheimRespawn = true;
        @Config.Comment("可以始终前往地下世界的维度，以及返回的维度。默认为交错次元。")
        public int originDimension = 0;
        @Config.Comment("允许在“主世界”维度之外创建前往地下世界的传送门。这可能被视为作弊。")
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
        public boolean enableExplosionPollution = true;
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

    /**
     * 扭曲事件系统（污染→扭曲→事件联动）。
     * 玩家身处高咒波(flux)区块时持续累积临时扭曲，扭曲值达到阈值后按概率触发扭曲事件。
     */
    public static class WarpEventSwitch {
        @Config.Comment("扭曲事件总开关")
        public boolean enableWarpEvents = true;
        @Config.Comment("佛系模式：开启后扭曲事件永不触发")
        public boolean wussMode = false;
        @Config.Comment("污染累积扭曲的区块咒波阈值（低于此值不累积）")
        public float fluxThreshold = 10.0F;
        @Config.Comment("污染累积速率：每秒每超过阈值1点咒波获得的临时扭曲量")
        public double fluxWarpRate = 0.05;
        @Config.Comment("事件触发判定间隔（tick，默认2000=100秒）")
        public int checkInterval = 2000;
        @Config.Comment("事件出队执行间隔（tick，默认20=1秒）")
        public int dequeueInterval = 20;
        @Config.Comment({"中毒事件：启用开关", "最低扭曲值（-1 使用默认值 65）"})
        public boolean poisonEnabled = true;
        public int poisonMinWarp = -1;
        @Config.Comment({"反胃事件：启用开关", "最低扭曲值（-1 使用默认值 45）"})
        public boolean nauseaEnabled = true;
        public int nauseaMinWarp = -1;
        @Config.Comment({"失明事件：启用开关", "最低扭曲值（-1 使用默认值 60）"})
        public boolean blindEnabled = true;
        public int blindMinWarp = -1;
        @Config.Comment({"凋零事件：启用开关", "最低扭曲值（-1 使用默认值 100）"})
        public boolean witherEnabled = true;
        public int witherMinWarp = -1;
        @Config.Comment({"跳跃失控事件：启用开关", "最低扭曲值（-1 使用默认值 55）"})
        public boolean jumpEnabled = true;
        public int jumpMinWarp = -1;
        @Config.Comment({"落雷事件：启用开关", "最低扭曲值（-1 使用默认值 80）"})
        public boolean lightningEnabled = true;
        public int lightningMinWarp = -1;
        @Config.Comment({"红色咒波雨事件：启用开关", "最低扭曲值（-1 使用默认值 90）"})
        public boolean rainEnabled = true;
        public int rainMinWarp = -1;
        @Config.Comment({"咒波狂风事件：启用开关", "最低扭曲值（-1 使用默认值 70）"})
        public boolean windEnabled = true;
        public int windMinWarp = -1;
        @Config.Comment({"蘑菇滋生事件：启用开关", "最低扭曲值（-1 使用默认值 40）"})
        public boolean mushroomsEnabled = true;
        public int mushroomsMinWarp = -1;
        @Config.Comment({"沼泽腐化事件：启用开关", "最低扭曲值（-1 使用默认值 85）"})
        public boolean swampEnabled = true;
        public int swampMinWarp = -1;
        @Config.Comment({"蓝色假雨事件：启用开关", "最低扭曲值（-1 使用默认值 50）"})
        public boolean fakerainEnabled = true;
        public int fakerainMinWarp = -1;
        @Config.Comment({"咒波闪现事件：启用开关", "最低扭曲值（-1 使用默认值 95）"})
        public boolean blinkEnabled = true;
        public int blinkMinWarp = -1;
        @Config.Comment({"血腥滴落事件：启用开关", "最低扭曲值（-1 使用默认值 75）"})
        public boolean bloodEnabled = true;
        public int bloodMinWarp = -1;
        @Config.Comment({"黑曜石围困事件：启用开关", "最低扭曲值（-1 使用默认值 110）"})
        public boolean obsidianEnabled = true;
        public int obsidianMinWarp = -1;
        @Config.Comment({"世界破洞事件：启用开关", "最低扭曲值（-1 使用默认值 100）"})
        public boolean fallEnabled = true;
        public int fallMinWarp = -1;
        @Config.Comment({"假爆炸事件：启用开关", "最低扭曲值（-1 使用默认值 60）"})
        public boolean fakeexplosionEnabled = true;
        public int fakeexplosionMinWarp = -1;
        @Config.Comment({"僵尸围城事件：启用开关", "最低扭曲值（-1 使用默认值 120）"})
        public boolean siegeEnabled = true;
        public int siegeMinWarp = -1;
        @Config.Comment({"倒计时炸弹事件：启用开关", "最低扭曲值（-1 使用默认值 90）"})
        public boolean countdownbombEnabled = true;
        public int countdownbombMinWarp = -1;
        @Config.Comment({"咒波呕物事件：启用开关", "最低扭曲值（-1 使用默认值 30）"})
        public boolean junkEnabled = true;
        public int junkMinWarp = -1;
        @Config.Comment({"背包乱序事件：启用开关", "最低扭曲值（-1 使用默认值 105）"})
        public boolean inventoryscrambleEnabled = true;
        public int inventoryscrambleMinWarp = -1;
    }
}
