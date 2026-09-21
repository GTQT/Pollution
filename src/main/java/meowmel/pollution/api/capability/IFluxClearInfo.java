package meowmel.pollution.api.capability;

/**
 * 空气过滤机对外暴露的显示信息，供 TOP 等外部显示使用。
 * 单方块机器与大型多方块控制器都实现本接口。
 */
public interface IFluxClearInfo {

    /**
     * @return 机器所在区块的当前污染值，单位 Vis（双端可读）
     */
    float getCurrentFlux();

    /**
     * @return 单次净化量，单位 Vis
     */
    double getVisPerTick();

    /**
     * @return 滤芯已损耗的耐久
     */
    int getFilterDamage();

    /**
     * @return 滤芯的最大耐久，为 0 表示当前没有滤芯
     */
    int getFilterMaxDurability();
}
