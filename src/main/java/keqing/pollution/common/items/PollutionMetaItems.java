package keqing.pollution.common.items;

import gregtech.api.items.metaitem.MetaItem;
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

	public static void initialization() {
		PollutionMetaItem1 item1 = new PollutionMetaItem1();
		MetaArmor armor = new MetaArmor();
		armor.setRegistryName("pollution_armor");
	}

	public static void initSubItems() {
		PollutionMetaItem1.registerItems();
	}

}
