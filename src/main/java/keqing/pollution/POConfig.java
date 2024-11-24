package keqing.pollution;
import net.minecraftforge.common.config.Config;
@Config(modid = Pollution.MODID)
public class POConfig {
    @Config.Comment("气态魔力废液每mb的EU燃值")
    public static double EuPerMbKqMagicRub = 256.0;
    @Config.Comment("气态离散态魔力每mb的EU燃值")
    public static double EuPerMbKqMagicGas = 256.0;
    @Config.Comment("气态离散态魔力每mb的EU燃值")
    public static double EuPerMbKqMagicFas = 512.0;
    @Config.Comment("气态离散态魔力每mb的EU燃值")
    public static double EuPerMbKqMagicDas = 1024.0;
    @Config.Comment("气态离散态魔力每mb的EU燃值")
    public static double EuPerMbKqMagicAas = 2048.0;
    @Config.Comment("自然魔力每mb的EU燃值")
    public static double EuPerMbMagicKq = 8192.0;
    @Config.Comment("富集自然魔力每mb的EU燃值")
    public static double EuPerMbRichMagicKq = 32768.0;
    @Config.Comment("咒波促燃发电机单位tick发电消耗咒波")
    public static float FluxPromotedGeneratorFluxPerTick = 0.005F;
    @Config.Comment("机器污染开关")
    public static boolean enablePollution =true;

    @Config.Comment("机器爆炸污染")
    public static boolean ExplosionPollution =true;

    @Config.Comment("玩家污染DEBUFF(包括泥土变沙子，药水效果)")
    public static boolean EntityPollutionEvent =true;

    @Config.Comment("消声仓污染倍率(0为无污染)")
    public static double mufflerPollutionMultiplier =1.0;

    @Config.Comment("是否开启消声仓污染特效")
    public static boolean mufflerPollutionShowEffects=true;

    @Config.Comment("污染清理倍率")
    public static double fluxScrubberMultiplier=0.004;

    @Config.Comment("灵气发电机单位灵气发电量")
    public static float visGeneratorEuPerVis=250.0f;

    @Config.Comment("灵气发电机污染倍率默认")
    public static float visGeneratorPollutionMultiplier=0.1f;

    @Config.Comment("灵气发电机污染倍率最小值")
    public static float visGeneratorMinPollution=0.0f;

    @Config.Comment("灵气发电机污染倍率最大值")
    public static float visGeneratorMaxPollution=1.0f;

    @Config.Comment("灵气发电机污染特效")
    public static boolean visGeneratorPollutionShowEffects =true;

    @Config.Comment("灵气发生器灵气生成倍率")
    public static double visProviderMultiplier=0.05;
}