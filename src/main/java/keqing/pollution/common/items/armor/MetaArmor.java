package keqing.pollution.common.items.armor;

import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.common.ConfigHolder;
import keqing.pollution.common.items.PollutionMetaItems;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;

public class MetaArmor extends ArmorMetaItem<ArmorMetaItem<?>.ArmorMetaValueItem> {

	@Override
	public void registerSubItems() {
		//200
		PollutionMetaItems.WING_NANO = addItem(200, "wing_nano").setArmorLogic(
				new NanosuitWings(30, 6_400_000L * (long) Math.max(1, Math.pow(4, ConfigHolder.tools.voltageTierImpeller - 2)),
						ConfigHolder.tools.voltageTierImpeller)).setModelAmount(8).setRarity(EnumRarity.UNCOMMON);

		PollutionMetaItems.WING_QUANTUM = addItem(201, "wing_quantum")
				.setArmorLogic(
						new QuantumWings(120, 25_600_000L * (long) Math.max(1, Math.pow(4, ConfigHolder.tools.voltageTierAdvImpeller - 4)),
								ConfigHolder.tools.voltageTierAdvImpeller))
				.setRarity(EnumRarity.RARE);

		PollutionMetaItems.NANO_GOGGLES = addItem(205, "nano_goggles").setArmorLogic(
				new GogglesNano(2, 160_000L * (long) Math.max(1, Math.pow(1, ConfigHolder.tools.voltageTierNightVision - 1)),
						ConfigHolder.tools.voltageTierNightVision, EntityEquipmentSlot.HEAD));

		PollutionMetaItems.QUANTUM_GOGGLES = addItem(206, "quantum_goggles").setArmorLogic(
				new GogglesQuantum(2, 1_280_000L * (long) Math.max(1, Math.pow(1, ConfigHolder.tools.voltageTierNightVision - 1)),
						ConfigHolder.tools.voltageTierNightVision, EntityEquipmentSlot.HEAD));

	}
}