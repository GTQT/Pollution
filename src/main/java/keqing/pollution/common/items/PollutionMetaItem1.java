package keqing.pollution.common.items;

import gregtech.api.GregTechAPI;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.CommonProxy;
import keqing.pollution.common.items.behaviors.VisCheckerBehavior;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

import static keqing.gtqtcore.common.items.GTQTMetaItems.BATTERY_NIMH_EMPTY;
import static keqing.pollution.common.CommonProxy.Pollution_TAB;

public class PollutionMetaItem1 extends StandardMetaItem {

	public PollutionMetaItem1() {
		this.setRegistryName("pollution_meta_item_1");
		setCreativeTab(GregTechAPI.TAB_GREGTECH);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(stack, worldIn, tooltip, tooltipFlag);
		if (stack.getTagCompound() != null && stack.getMetadata() == 100) { // 检查物品是否有NBT数据，以及物品是不是灵气节点
			tooltip.add(TextFormatting.GRAY + "节点信息:"); // 添加一个标题
			NBTTagCompound compound = stack.getTagCompound();
			if (compound != null) {
				compound.getKeySet().forEach(key -> { // 遍历NBT数据的键
					if (Objects.equals(key, "NodeTire")) {
						String value = compound.getString(key); // 获取键对应的值的字符串表示
						tooltip.add(TextFormatting.GRAY + key + ": " + value);
					} else if (Objects.equals(key, "NodeType")) {
						String value = compound.getString(key); // 获取键对应的值的字符串表示
						tooltip.add(TextFormatting.GRAY + key + ": " + value);
					} else {
						int value = compound.getInteger(key);
						tooltip.add(TextFormatting.GRAY + key + ": " + value);
					}
				});
			}
		}
	}

	public void registerSubItems() {
		PollutionMetaItems.TEST = this.addItem(1, "test").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);

		//空白催化核心，需要注魔合成
		PollutionMetaItems.BLANKCORE = this.addItem(2, "blank_catalyst_core").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		//燃能催化核心，用于高温催化
		PollutionMetaItems.HOTCORE = this.addItem(3, "hot_catalyst_core").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		//冷寂催化核心，用于低温催化
		PollutionMetaItems.COLDCORE = this.addItem(4, "cold_catalyst_core").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		//凝聚催化核心，用于物质的聚合催化
		PollutionMetaItems.INTEGRATECORE = this.addItem(5, "integration_catalyst_core").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		//分离催化核心，用于物质的分解催化
		PollutionMetaItems.SEGREGATECORE = this.addItem(6, "segregation_catalyst_core").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		//焦化催化核心，用于史莱姆线
		PollutionMetaItems.COKINGCORE = this.addItem(7, "coking_catalyst_core").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		//嬗变催化核心，用于塑料
		PollutionMetaItems.EVOLUTIONCORE = this.addItem(8, "evolution_catalyst_core").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);



		//史莱姆
		//焦油史莱姆
		PollutionMetaItems.TARSLIME = this.addItem(20, "tar_slime").setMaxStackSize(1).setRarity(EnumRarity.RARE).setCreativeTabs(Pollution_TAB);
		//糖果史莱姆
		PollutionMetaItems.SUGARSLIME = this.addItem(21, "sugar_slime").setMaxStackSize(1).setRarity(EnumRarity.RARE).setCreativeTabs(Pollution_TAB);
		//粘胶史莱姆
		PollutionMetaItems.GLUESLIME = this.addItem(22, "glue_slime").setMaxStackSize(1).setRarity(EnumRarity.RARE).setCreativeTabs(Pollution_TAB);
		//甘油史莱姆
		PollutionMetaItems.GLYCEROLSLIME = this.addItem(23, "glycerol_slime").setMaxStackSize(1).setRarity(EnumRarity.RARE).setCreativeTabs(Pollution_TAB);
		//橡胶史莱姆
		PollutionMetaItems.RUBBERSLIME = this.addItem(24, "rubber_slime").setMaxStackSize(1).setRarity(EnumRarity.RARE).setCreativeTabs(Pollution_TAB);

		//魔法系列电路板
		PollutionMetaItems.MAGIC_CIRCUIT_ULV = this.addItem(50, "magic_circuit.ulv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.ULV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_LV = this.addItem(51, "magic_circuit.lv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.LV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_MV = this.addItem(52, "magic_circuit.mv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.MV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_HV = this.addItem(53, "magic_circuit.hv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.HV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_EV = this.addItem(54, "magic_circuit.ev").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.EV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_IV = this.addItem(55, "magic_circuit.iv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.IV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_LuV = this.addItem(56, "magic_circuit.luv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.LuV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_ZPM = this.addItem(57, "magic_circuit.zpm").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.ZPM).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_UV = this.addItem(58, "magic_circuit.uv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_UHV = this.addItem(59, "magic_circuit.uhv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UHV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_UEV = this.addItem(60, "magic_circuit.uev").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UEV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_UIV = this.addItem(61, "magic_circuit.uiv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UIV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_UXV = this.addItem(62, "magic_circuit.uxv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UXV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_OpV = this.addItem(63, "magic_circuit.opv").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.OpV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.MAGIC_CIRCUIT_MAX = this.addItem(64, "magic_circuit.max").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.MAX).setCreativeTabs(Pollution_TAB);

		//封装灵气节点
		PollutionMetaItems.PACKAGED_AURA_NODE = this.addItem(100, "packaged_aura_node").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);

		//贤者之石
		PollutionMetaItems.STONE_OF_PHILOSOPHER_1 = this.addItem(150, "stone_of_philosopher_1").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STONE_OF_PHILOSOPHER_2 = this.addItem(151, "stone_of_philosopher_2").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STONE_OF_PHILOSOPHER_3 = this.addItem(152, "stone_of_philosopher_3").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STONE_OF_PHILOSOPHER_4 = this.addItem(153, "stone_of_philosopher_4").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STONE_OF_PHILOSOPHER_FINAL= this.addItem(154, "stone_of_philosopher_final").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);

		//实用物品
		PollutionMetaItems.VIS_CHECKER = this.addItem(200, "vis_checker").setMaxStackSize(1).addComponents(new VisCheckerBehavior()).setCreativeTabs(Pollution_TAB);
		//200 电池 外壳注册
		PollutionMetaItems.BATTERY_HULL_LV = addItem(250, "magic_battery.hull.lv").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BATTERY_HULL_MV = addItem(251, "magic_battery.hull.mv").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BATTERY_HULL_HV = addItem(252, "magic_battery.hull.hv").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BATTERY_HULL_EV = addItem(253, "magic_battery.hull.ev").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BATTERY_HULL_IV = addItem(254, "magic_battery.hull.iv").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BATTERY_HULL_LuV = addItem(255, "magic_battery.hull.luv").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BATTERY_HULL_ZPM = addItem(256, "magic_battery.hull.zpm").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BATTERY_HULL_UV = addItem(257, "magic_battery.hull.uv").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);


	}

}
