package keqing.pollution.common.items;

import gregtech.api.GregTechAPI;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.common.items.behaviors.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

import static keqing.pollution.common.CommonProxy.Pollution_TAB;
import static keqing.pollution.common.CommonProxy.Pollution_TAROT;
import static keqing.pollution.common.items.PollutionMetaItems.DEVAY_PILL_EMPTY;
import static keqing.pollution.common.items.PollutionMetaItems.PESTICIDE_EMPTY;

public class PollutionMetaItem1 extends StandardMetaItem {

	public PollutionMetaItem1() {
		this.setRegistryName("pollution_meta_item_1");
		setCreativeTab(Pollution_TAB);
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

		//魔法元件 理式核心=马达
		//太一燃素瓶=泵
		//自动反诘装置=活塞
		//四因阐释器=机械臂
		//意志数据链=传送带
		//密契询唤针=发射器
		//我思除颤仪=传感器
		//自在球=力场发生器
		PollutionMetaItems.CORE_OF_IDEA = this.addItem(101, "core_of_idea").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BOTTLE_OF_PHLOGISTONIC_ONENESS = this.addItem(102, "bottle_of_phlogistonic_oneness").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.AUTO_ELENCHUS_DEVICE = this.addItem(103, "auto_elenchus_device").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.ELUCIDATOR_OF_FOUR_CAUSES = this.addItem(104, "elucidator_of_four_causes").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.SYMPTOMATIC_VIS_DATA_LINK = this.addItem(105, "symptomatic_vis_data_link").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.NEEDLE_OF_MYSTIC_INTERPELLATION = this.addItem(106, "needle_of_mystic_interpellation").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.COGITO_AED = this.addItem(107, "cogito_AED").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BALL_IN_ITSELF = this.addItem(108, "ball_in_itself").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);

		//贤者之石
		PollutionMetaItems.STONE_OF_PHILOSOPHER_1 = this.addItem(150, "stone_of_philosopher_1").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STONE_OF_PHILOSOPHER_2 = this.addItem(151, "stone_of_philosopher_2").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STONE_OF_PHILOSOPHER_3 = this.addItem(152, "stone_of_philosopher_3").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STONE_OF_PHILOSOPHER_4 = this.addItem(153, "stone_of_philosopher_4").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STONE_OF_PHILOSOPHER_FINAL= this.addItem(154, "stone_of_philosopher_final").setMaxStackSize(1).setRarity(EnumRarity.EPIC).setCreativeTabs(Pollution_TAB);
        //新符文
		PollutionMetaItems.WHITE_RUNE = this.addItem(155, "white_rune").setMaxStackSize(64).setRarity(EnumRarity.UNCOMMON).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLACK_RUNE = this.addItem(156, "black_rune").setMaxStackSize(64).setRarity(EnumRarity.UNCOMMON).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.STARRY_RUNE = this.addItem(157, "starry_rune").setMaxStackSize(64).setRarity(EnumRarity.UNCOMMON).setCreativeTabs(Pollution_TAB);

		//实用物品
		PollutionMetaItems.VIS_CHECKER = this.addItem(200, "vis_checker").setMaxStackSize(1).addComponents(new VisCheckerBehavior()).setCreativeTabs(Pollution_TAB);
		//杀虫剂
		PollutionMetaItems.PESTICIDE_EMPTY = addItem(201, "pesticide.empty").setMaxStackSize(1).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.PESTICIDE= this.addItem(202, "pesticide.full").setMaxStackSize(1).setCreativeTabs(Pollution_TAB);
		//理智回复药
		PollutionMetaItems.DEVAY_PILL_EMPTY = addItem(203, "devay_pill.empty").setMaxStackSize(1).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.DEVAY_PILL_1= this.addItem(204, "devay_pill.1").setMaxStackSize(1).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.DEVAY_PILL_5= this.addItem(205, "devay_pill.5").setMaxStackSize(1).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.DEVAY_PILL_10= this.addItem(206, "devay_pill.10").setMaxStackSize(1).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.DEVAY_PILL_20= this.addItem(207, "devay_pill.20").setMaxStackSize(1).setCreativeTabs(Pollution_TAB);

		//过滤器注册（5个）
		PollutionMetaItems.FILTER_MKI = this.addItem(210, "filter.i").setMaxStackSize(8).addComponents(new FilterBehavior(240000, 1, PollutionMaterials.infused_earth)).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.FILTER_MKII = this.addItem(211, "filter.ii").setMaxStackSize(8).addComponents(new FilterBehavior(360000, 2, PollutionMaterials.infused_water)).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.FILTER_MKIII = this.addItem(212, "filter.iii").setMaxStackSize(8).addComponents(new FilterBehavior(480000, 3, PollutionMaterials.syrmorite)).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.FILTER_MKIV = this.addItem(213, "filter.iv").setMaxStackSize(8).addComponents(new FilterBehavior(640000, 4, PollutionMaterials.thaumium)).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.FILTER_MKV = this.addItem(214, "filter.v").setMaxStackSize(8).addComponents(new FilterBehavior(720000, 5, PollutionMaterials.octine)).setCreativeTabs(Pollution_TAB);
		//模块升级4个
		PollutionMetaItems.ENERGY_REDUCE = this.addItem(220, "energy_reduce").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.TIME_REDUCE = this.addItem(221, "time_increase").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.OVERCLOCKING_ENHANCE = this.addItem(222, "overclocking_enhance").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.PARALLEL_ENHANCE = this.addItem(223, "parallel_enhance").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);

		//血魔法电路
		PollutionMetaItems.BLOOD_CIRCUIT = this.addItem(250, "blood_circuit").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_ADVANCED = this.addItem(251, "blood_circuit_advanced").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_ULTIMATE = this.addItem(252, "blood_circuit_ultimate").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_SUPREME = this.addItem(253, "blood_circuit_supreme").setMaxStackSize(64).setCreativeTabs(Pollution_TAB);

		//塔罗牌
		PollutionMetaItems.TEST_ITEM = this.addItem(300, "test_item").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_FOOL = this.addItem(301, "the_fool").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT).addComponents(new Tarots.THE_FOOL());
		PollutionMetaItems.TAROT_THE_MAGICIAN = this.addItem(302, "the_magician").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_HIGH_PRIESTESS = this.addItem(303, "the_high_priestess").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_EMPRESS = this.addItem(304, "the_empress").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_EMPEROR = this.addItem(305, "the_emperor").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_HIGHOPHANT = this.addItem(306, "the_highophant").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_LOVERS = this.addItem(307, "the_lovers").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_CHARIOT = this.addItem(308, "the_chariot").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_STRENGTH = this.addItem(309, "the_strength").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_HERMIT = this.addItem(310, "the_hermit").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_WHEEL_OF_FORTUNE = this.addItem(311, "the_wheel_of_fortune").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_JUSTICE = this.addItem(312, "the_justice").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_HANGED_MAN = this.addItem(313, "the_hanged_man").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_DEATH = this.addItem(314, "the_death").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_TEMPERANCE = this.addItem(315, "the_temperance").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_DEVIL = this.addItem(316, "the_devil").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_TOWER = this.addItem(317, "the_tower").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_STAR = this.addItem(318, "the_star").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_MOON = this.addItem(319, "the_moon").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_SUN = this.addItem(320, "the_sun").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_JUDGEMENT = this.addItem(321, "the_judgement").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);
		PollutionMetaItems.TAROT_THE_WORLD = this.addItem(322, "the_world").setMaxStackSize(64).setCreativeTabs(Pollution_TAROT);

		//血魔法系列电路板
		PollutionMetaItems.BLOOD_CIRCUIT_MV  = this.addItem(402, "blood_circuit.0") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.MV) .setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_HV  = this.addItem(403, "blood_circuit.1") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.HV) .setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_EV  = this.addItem(404, "blood_circuit.2") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.EV) .setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_IV  = this.addItem(405, "blood_circuit.3") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.IV) .setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_LuV = this.addItem(406, "blood_circuit.4") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.LuV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_ZPM = this.addItem(407, "blood_circuit.5") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.ZPM).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_UV  = this.addItem(408, "blood_circuit.6") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UV) .setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_UHV = this.addItem(409, "blood_circuit.7") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UHV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_UEV = this.addItem(410, "blood_circuit.8") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UEV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_UIV = this.addItem(411, "blood_circuit.9") .setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UIV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_UXV = this.addItem(412, "blood_circuit.10").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.UXV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_OpV = this.addItem(413, "blood_circuit.11").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.OpV).setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_CIRCUIT_MAX = this.addItem(414, "blood_circuit.12").setUnificationData(OrePrefix.circuit, MarkerMaterials.Tier.MAX).setCreativeTabs(Pollution_TAB);


		PollutionMetaItems.BLOOD_PRIMITIVE_MEAT       = this.addItem(430, "primitive_meat").setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_RATS_BRAIN           = this.addItem(431, "rat_brain").setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_MITOCHONDRION_POWER  = this.addItem(432, "mitochondrion_power").setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_ENDORPHINS_STABILIZER= this.addItem(433, "endorphins_stabilizer").setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_FREEZE_COOLER        = this.addItem(434, "freeze_cooler").setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_LYSOSOME_STABILIZER  = this.addItem(435, "lysosome_stabilizer").setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_IPS_HUMAN_BRAIN      = this.addItem(436, "ips_human_brain").setCreativeTabs(Pollution_TAB);
		PollutionMetaItems.BLOOD_PORT                 = this.addItem(437, "blood_port").setCreativeTabs(Pollution_TAB);

	}

}
