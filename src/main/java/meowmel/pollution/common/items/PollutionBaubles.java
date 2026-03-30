package meowmel.pollution.common.items;

import baubles.api.BaubleType;
import gregtech.api.items.metaitem.StandardMetaItem;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.common.items.bauble.ItemWaterRing;

import static meowmel.pollution.common.CommonProxy.Pollution_TAB;
import static meowmel.pollution.common.items.PollutionMetaItems.BAUBLES_WATER_RING;

public class PollutionBaubles extends StandardMetaItem {

    public PollutionBaubles() {
        this.setRegistryName("pollution_baubles");
        setCreativeTab(Pollution_TAB);
    }
    @Override
    public void registerSubItems() {
        BAUBLES_WATER_RING=this.addItem(1, "baubles.water_ring").setMaxStackSize(1).addComponents(new ItemWaterRing(1, 120000, PollutionMaterials.InfusedWater, BaubleType.RING)).setCreativeTabs(Pollution_TAB);
    }
}
