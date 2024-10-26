package keqing.pollution.common.items;

import gregtech.api.GTValues;
import gregtech.api.items.metaitem.ElectricStats;
import gregtech.api.items.metaitem.MetaItem;
import keqing.gtqtcore.common.items.GTQTBattery;
import keqing.gtqtcore.common.items.GTQTMetaItem1;
import keqing.pollution.common.items.armor.MetaArmor;

public class PollutionMetaItems {
	public static MetaItem<?>.MetaValueItem VIS_CHECKER;
    public static MetaItem<?>.MetaValueItem NANO_GOGGLES;
	public static MetaItem<?>.MetaValueItem WING_QUANTUM;
	public static MetaItem<?>.MetaValueItem WING_NANO;
	public static MetaItem<?>.MetaValueItem TEST;
	public static MetaItem<?>.MetaValueItem BLANKCORE;
	public static MetaItem<?>.MetaValueItem HOTCORE;
	public static MetaItem<?>.MetaValueItem COLDCORE;
	public static MetaItem<?>.MetaValueItem INTEGRATECORE;
	public static MetaItem<?>.MetaValueItem SEGREGATECORE;
	public static MetaItem<?>.MetaValueItem COKINGCORE;
	public static MetaItem<?>.MetaValueItem EVOLUTIONCORE;
	public static MetaItem<?>.MetaValueItem TARSLIME;
	public static MetaItem<?>.MetaValueItem SUGARSLIME;
	public static MetaItem<?>.MetaValueItem GLUESLIME;
	public static MetaItem<?>.MetaValueItem GLYCEROLSLIME;
	public static MetaItem<?>.MetaValueItem RUBBERSLIME;

	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_ULV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_LV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_MV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_HV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_EV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_IV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_LuV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_ZPM;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_UV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_UHV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_UEV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_UIV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_UXV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_OpV;
	public static MetaItem<?>.MetaValueItem MAGIC_CIRCUIT_MAX;

	public static MetaItem<?>.MetaValueItem PACKAGED_AURA_NODE;
	public static MetaItem<?>.MetaValueItem STONE_OF_PHILOSOPHER_1;
	public static MetaItem<?>.MetaValueItem STONE_OF_PHILOSOPHER_2;


	public static MetaItem<?>.MetaValueItem BATTERY_HULL_LV ;
	public static MetaItem<?>.MetaValueItem BATTERY_HULL_MV ;
	public static MetaItem<?>.MetaValueItem BATTERY_HULL_HV ;
	public static MetaItem<?>.MetaValueItem BATTERY_HULL_EV ;
	public static MetaItem<?>.MetaValueItem BATTERY_HULL_IV ;
	public static MetaItem<?>.MetaValueItem BATTERY_HULL_LuV ;
	public static MetaItem<?>.MetaValueItem BATTERY_HULL_ZPM ;
	public static MetaItem<?>.MetaValueItem BATTERY_HULL_UV ;

	public static MetaItem<?>.MetaValueItem MAGIC_BATTERY_LV ;
	public static MetaItem<?>.MetaValueItem MAGIC_BATTERY_MV ;
	public static MetaItem<?>.MetaValueItem MAGIC_BATTERY_HV ;
	public static MetaItem<?>.MetaValueItem MAGIC_BATTERY_EV ;
	public static MetaItem<?>.MetaValueItem MAGIC_BATTERY_IV ;
	public static MetaItem<?>.MetaValueItem MAGIC_BATTERY_LuV ;
	public static MetaItem<?>.MetaValueItem MAGIC_BATTERY_ZPM ;
	public static MetaItem<?>.MetaValueItem MAGIC_BATTERY_UV ;

	public static PollutionMetaItem1 POLLUTION_META_ITEM;
	public static POBattery POLLUTION_META_BATTERY;
	public static void initialization() {
		POLLUTION_META_ITEM = new PollutionMetaItem1();
		POLLUTION_META_BATTERY = new POBattery();
		MetaArmor armor = new MetaArmor();
		armor.setRegistryName("pollution_armor");
	}

	public static void initSubItems() {
		PollutionMetaItem1.registerItems();
		POBattery.registerItems();
	}

}
