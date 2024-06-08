package keqing.pollution;
import net.minecraftforge.common.config.Config;
@Config(modid = Pollution.MODID)
public class POConfig {
    @Config.Comment("消声仓污染倍率")
    public static float mufflerPollutionMultiplier =0.02f;

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