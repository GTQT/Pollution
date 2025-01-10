package keqing.pollution.common.items;

import baubles.api.BaubleType;
import gregtech.api.GregTechAPI;
import gregtech.api.items.metaitem.StandardMetaItem;
import keqing.pollution.Pollution;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.items.bauble.ItemWaterRing;

import static keqing.pollution.common.CommonProxy.Pollution_TAB;
import static keqing.pollution.common.items.PollutionMetaItems.BAUBLES_WATER_RING;

public class PollutionBaubles extends StandardMetaItem {

    public PollutionBaubles() {
        this.setRegistryName("pollution_baubles");
        setCreativeTab(GregTechAPI.TAB_GREGTECH);
    }
    @Override
    public void registerSubItems() {
        BAUBLES_WATER_RING=this.addItem(1, "baubles.water_ring").setMaxStackSize(1).addComponents(new ItemWaterRing(1, 120000, PollutionMaterials.infused_water, BaubleType.RING)).setCreativeTabs(Pollution_TAB);
    }
}
