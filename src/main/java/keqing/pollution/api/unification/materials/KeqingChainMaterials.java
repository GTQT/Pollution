package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.ToolProperty;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.Materials.Iron;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.BRIGHT;
import static gregtech.api.util.GTUtility.gregtechId;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.api.unification.PollutionMaterials.infused_order;

public class KeqingChainMaterials {
	public KeqingChainMaterials() {
	}

	private static int startId = 16400;
	private static final int END_ID = startId + 100;

	private static int getMaterialsId() {
		if (startId < END_ID) {
			return startId++;
		}
		throw new ArrayIndexOutOfBoundsException();
	}
	/*硫铅盐
	蕴魔硫铅盐
	一次炼金残渣
	一次升华蒸汽
	神秘锡溶液
	硫酸亚锡神秘溶液
	高魔素硫酸亚锡
	二次炼金残渣
	二次升华蒸汽
	含杂汞盐溶液
	神秘汞盐溶液
	三次炼金残渣
	三次升华蒸汽
	魔力激活氯化铁溶液
	魔力激活氯化亚铁甲醇溶液
	除杂激活氯化亚铁甲醇溶液
	四次炼金残渣
	除杂激活氯化亚铁
	四次升华蒸汽
	赛摩铜掺杂魔水溶液
	未成形胚胎魔水
	胚胎魔水
	五次升华蒸汽
	五次炼金残渣
	不稳次元银
	超次元含杂秘银
	超次元秘银
	六次升华蒸汽
	六次炼金残渣
	次元改造剂*/
	public static void register() {

		PollutionMaterials.sulfo_plumbic_salt = new Material.Builder(getMaterialsId(), gregtechId("sulfo_plumbic_salt"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.magical_sulfo_plumbic_salt = new Material.Builder(getMaterialsId(), gregtechId("magical_sulfo_plumbic_salt"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.alchemical_residue1 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue1"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor1 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor1"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue2 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue2"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor2 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor2"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue3 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue3"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor3 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor3"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue4 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue4"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor4 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor4"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue5 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue5"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor5 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor5"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue6 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue6"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor6 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor6"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.magical_tin_solution = new Material.Builder(getMaterialsId(), gregtechId("magical_tin_solution"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.magical_stannous_sulfate_solution = new Material.Builder(getMaterialsId(), gregtechId("magical_stannous_sulfate_solution"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.highmana_stannous_sulfate = new Material.Builder(getMaterialsId(), gregtechId("highmana_stannous_sulfate"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.impure_mercuric_salt_solution = new Material.Builder(getMaterialsId(), gregtechId("impure_mercuric_salt_solution"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.mercuric_salt_solution = new Material.Builder(getMaterialsId(), gregtechId("mercuric_salt_solution"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.magic_activated_iron_chloride_solution = new Material.Builder(getMaterialsId(), gregtechId("magic_activated_iron_chloride_solution"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.magic_activated_ferrous_chloride_ethanol_solution = new Material.Builder(getMaterialsId(), gregtechId("magic_activated_ferrous_chloride_ethanol_solution"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.purified_activated_ferrous_chloride_ethanol_solution = new Material.Builder(getMaterialsId(), gregtechId("purified_activated_ferrous_chloride_ethanol_solution"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.purified_activated_ferrous_chloride = new Material.Builder(getMaterialsId(), gregtechId("purified_activated_ferrous_chloride"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.syrmorite_doped_magic_water_solution = new Material.Builder(getMaterialsId(), gregtechId("syrmorite_doped_magic_water_solution"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.unformed_embryo_magic_water = new Material.Builder(getMaterialsId(), gregtechId("unformed_embryo_magic_water"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.embryo_magic_water = new Material.Builder(getMaterialsId(), gregtechId("embryo_magic_water"))
				.color(0xFEFE7D)
				.fluid()
				.build();
		PollutionMaterials.unstable_dimensional_silver = new Material.Builder(getMaterialsId(), gregtechId("unstable_dimensional_silver"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.impure_hyperdimensional_silver = new Material.Builder(getMaterialsId(), gregtechId("impure_hyperdimensional_silver"))
				.color(0xFEFE7D)
				.dust()
				.build();
		PollutionMaterials.hyperdimensional_silver = new Material.Builder(getMaterialsId(), gregtechId("hyperdimensional_silver"))
				.color(0xFEFE7D)
				.ingot().fluid()
				.iconSet(BRIGHT)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.build();
		PollutionMaterials.dimensional_transforming_agent = new Material.Builder(getMaterialsId(), gregtechId("dimensional_transforming_agent"))
				.color(0xFEFE7D)
				.fluid()
				.build();
	}
}
