package meowmel.pollution.api.capability;

import net.minecraft.item.ItemStack;

/**
 * 滤芯仓，为大型空气过滤机提供滤芯
 */
public interface IFilterHatch {

    /**
     * @return 槽位中的滤芯
     */
    ItemStack getFilterStack();

    /**
     * @return 该物品能否作为滤芯放入
     */
    boolean isItemValid(ItemStack stack);

    /**
     * 对滤芯造成损耗
     *
     * @param damage 本次造成的损耗值
     */
    void damageFilter(int damage);

    /**
     * @return 当前滤芯已损耗的值
     */
    int getFilterDamage();

    /**
     * @return 当前滤芯的最大耐久
     */
    int getFilterMaxDurability();
}
