package meowmel.pollution.api.capability;

/**
 * 本模组自定义的 MTE 同步数据包 id，避开 {@code GregtechDataCodes} 已占用的值（0-121）
 */
public final class PODataCodes {

    /** 空气过滤机滤芯耐久：已损耗 + 最大耐久 */
    public static final int SYNC_FLUX_FILTER = 200;

    /** 量子要素罐：当前要素种类 + 数量 */
    public static final int QUANTUM_ASPECT_TANK_CONTENT = 201;
    /** 量子要素罐：要素过滤（锁定） */
    public static final int QUANTUM_ASPECT_TANK_FILTER = 202;

    /** 量子魔力罐：当前魔力储量 */
    public static final int MANA_TANK_AMOUNT = 203;

    private PODataCodes() {}
}
