package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.BlastProperty;
import gregtech.api.unification.material.properties.ToolProperty;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.Materials.Chlorine;
import static gregtech.api.unification.material.Materials.Iron;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static gregtech.api.util.GTUtility.gregtechId;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.api.unification.PollutionMaterials.infused_order;

public class KeqingChainMaterials {
	public KeqingChainMaterials() {
	}

	private static int startId = 16700;
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
				.color(0x5A286F)
				.iconSet(METALLIC)
				.dust()
				.build();
		PollutionMaterials.magical_sulfo_plumbic_salt = new Material.Builder(getMaterialsId(), gregtechId("magical_sulfo_plumbic_salt"))
				.color(0x5A286F)
				.iconSet(SHINY)
				.dust()
				.build();
		PollutionMaterials.alchemical_residue_1 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue_1"))
				.color(0x401751)
				.iconSet(DULL)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor_1 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor_1"))
				.color(0x9159A9)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue_2 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue_2"))
				.color(0x647080)
				.iconSet(DULL)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor_2 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor_2"))
				.color(0x99CCFF)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue_3 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue_3"))
				.color(0x494949)
				.iconSet(DULL)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor_3 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor_3"))
				.color(0xD7D7D7)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue_4 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue_4"))
				.color(0x759F8A)
				.iconSet(DULL)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor_4 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor_4"))
				.color(0x86FCC1)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue_5 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue_5"))
				.color(0x1F447C)
				.iconSet(DULL)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor_5 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor_5"))
				.color(0x3385FF)
				.fluid()
				.build();
		PollutionMaterials.alchemical_residue_6 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_residue_6"))
				.color(0x6B4B6F)
				.iconSet(DULL)
				.dust()
				.build();
		PollutionMaterials.alchemical_vapor_6 = new Material.Builder(getMaterialsId(), gregtechId("alchemical_vapor_6"))
				.color(0xFCDBF)
				.fluid()
				.build();
		PollutionMaterials.magical_tin_solution = new Material.Builder(getMaterialsId(), gregtechId("magical_tin_solution"))
				.color(0xB9B9B9)
				.fluid()
				.build();
		PollutionMaterials.magical_stannous_sulfate_solution = new Material.Builder(getMaterialsId(), gregtechId("magical_stannous_sulfate_solution"))
				.color(0xF1FF97)
				.fluid()
				.build();
		PollutionMaterials.highmana_stannous_sulfate = new Material.Builder(getMaterialsId(), gregtechId("highmana_stannous_sulfate"))
				.color(0xCCFF33)
				.iconSet(SHINY)
				.dust()
				.build();
		PollutionMaterials.impure_mercuric_salt_solution = new Material.Builder(getMaterialsId(), gregtechId("impure_mercuric_salt_solution"))
				.color(0x95978E)
				.fluid()
				.build();
		PollutionMaterials.mercuric_salt_solution = new Material.Builder(getMaterialsId(), gregtechId("mercuric_salt_solution"))
				.color(0xDBDCD8)
				.fluid()
				.build();
		PollutionMaterials.magic_activated_iron_chloride_solution = new Material.Builder(getMaterialsId(), gregtechId("magic_activated_iron_chloride_solution"))
				.color(0x394320)
				.fluid()
				.build();
		PollutionMaterials.magic_activated_ferrous_chloride_ethanol_solution = new Material.Builder(getMaterialsId(), gregtechId("magic_activated_ferrous_chloride_ethanol_solution"))
				.color(0x80FF66)
				.fluid()
				.build();
		PollutionMaterials.purified_activated_ferrous_chloride_ethanol_solution = new Material.Builder(getMaterialsId(), gregtechId("purified_activated_ferrous_chloride_ethanol_solution"))
				.color(0xACFF9B)
				.fluid()
				.build();
		PollutionMaterials.purified_activated_ferrous_chloride = new Material.Builder(getMaterialsId(), gregtechId("purified_activated_ferrous_chloride"))
				.color(0xDEFFD7)
				.iconSet(SHINY)
				.dust()
				.build();
		PollutionMaterials.syrmorite_doped_magic_water_solution = new Material.Builder(getMaterialsId(), gregtechId("syrmorite_doped_magic_water_solution"))
				.color(0x1D4FDA)
				.fluid()
				.build();
		PollutionMaterials.unformed_embryo_magic_water = new Material.Builder(getMaterialsId(), gregtechId("unformed_embryo_magic_water"))
				.color(0x537EF2)
				.fluid()
				.build();
		PollutionMaterials.embryo_magic_water = new Material.Builder(getMaterialsId(), gregtechId("embryo_magic_water"))
				.color(0xA5BDFF)
				.fluid()
				.build();
		PollutionMaterials.unstable_dimensional_silver = new Material.Builder(getMaterialsId(), gregtechId("unstable_dimensional_silver"))
				.color(0x7D989C)
				.dust()
				.build();
		PollutionMaterials.impure_hyperdimensional_silver = new Material.Builder(getMaterialsId(), gregtechId("impure_hyperdimensional_silver"))
				.color(0x9AD4DC)
				.fluid()
				.build();
		PollutionMaterials.hyperdimensional_silver = new Material.Builder(getMaterialsId(), gregtechId("hyperdimensional_silver"))
				.color(0xD1FAFF)
				.ingot().fluid()
				.iconSet(SHINY)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(5400, BlastProperty.GasTier.MID)
				.build()
				.setFormula("Ag50(RnMa)m(AeIgAqTerOrdPe)n", true);
		PollutionMaterials.dimensional_transforming_agent = new Material.Builder(getMaterialsId(), gregtechId("dimensional_transforming_agent"))
				.color(0xFFC7F7)
				.fluid()
				.build();

		//氯化亚铁
		PollutionMaterials.ferrous_chloride = new Material.Builder(getMaterialsId(), gregtechId("ferrous_chloride"))
				.color(0x86FF8E)
				.dust()
				.iconSet(BRIGHT)
				.components(Iron, 1, Chlorine, 2)
				.flags(DECOMPOSITION_BY_ELECTROLYZING)
				.build();
	}
}
