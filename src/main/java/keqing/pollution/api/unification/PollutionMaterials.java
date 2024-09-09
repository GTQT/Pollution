package keqing.pollution.api.unification;

import gregtech.api.unification.material.Material;
import keqing.pollution.api.unification.materials.*;

public class PollutionMaterials {
	//基础六要素
	public static Material infused_air;
	public static Material infused_fire;
	public static Material infused_water;
	public static Material infused_earth;
	public static Material infused_entropy;
	public static Material infused_order;
	//复合要素
	public static Material infused_crystal;
	public static Material infused_life;
	public static Material infused_death;
	public static Material infused_soul;
	public static Material infused_weapon;
	public static Material infused_metal;
	public static Material infused_energy;
	public static Material infused_instrument;
	public static Material infused_exchange;
	public static Material infused_magic;
	public static Material infused_alchemy;
	public static Material infused_cold;
	public static Material infused_aura;
	public static Material infused_light;
	//mana酱
	public static Material mana;
	//材料
	public static Material thaumium;
	public static Material syrmorite;
	public static Material octine;
	public static Material scabyst;
	public static Material valonite;
	public static Material thaummix;
	public static Material aertitanium;
	public static Material ignissteel;
	public static Material aquasilver;
	public static Material terracopper;
	public static Material ordolead;
	public static Material perditioaluminium;
	public static Material impuremana;
	public static Material manasteel;
	public static Material salismundus;
	public static Material mansussteel;
	public static Material keqinggold;
	//man what can I say
	public static Material kobemetal;
	//催化剂
	public static Material roughdraft;
	public static Material substrate;
	//魔法催化线路材料
	public static Material lotus_dust;
	public static Material ethyl_silicate;
	public static Material rough_llp;
	public static Material llp;
	public static Material oil_with_llp;
	public static Material super_sticky_tar;
	public static Material magic_nitrobenzene;
	public static Material pure_tar;

	public static Material RichAura;
	public static Material ErichAura;
	public static Material sunnarium;
	public static Material whitemansus;
	public static Material blackmansus;
	public static Material Terrasteel;
	public static Material ElvenElementium;
	public static Material elven;

	//刻金线
	public static Material sulfo_plumbic_salt;
	public static Material magical_sulfo_plumbic_salt;
	public static Material alchemical_residue_1;
	public static Material alchemical_vapor_1;
	public static Material alchemical_residue_2;
	public static Material alchemical_vapor_2;
	public static Material alchemical_residue_3;
	public static Material alchemical_vapor_3;
	public static Material alchemical_residue_4;
	public static Material alchemical_vapor_4;
	public static Material alchemical_residue_5;
	public static Material alchemical_vapor_5;
	public static Material alchemical_residue_6;
	public static Material alchemical_vapor_6;
	public static Material magical_tin_solution;
	public static Material magical_stannous_sulfate_solution;
	public static Material highmana_stannous_sulfate;
	public static Material impure_mercuric_salt_solution;
	public static Material mercuric_salt_solution;
	public static Material magic_activated_iron_chloride_solution;
	public static Material magic_activated_ferrous_chloride_ethanol_solution;
	public static Material purified_activated_ferrous_chloride_ethanol_solution;
	public static Material purified_activated_ferrous_chloride;
	public static Material syrmorite_doped_magic_water_solution;
	public static Material unformed_embryo_magic_water;
	public static Material embryo_magic_water;
	public static Material unstable_dimensional_silver;
	public static Material impure_hyperdimensional_silver;
	public static Material hyperdimensional_silver;
	public static Material dimensional_transforming_agent;
	public static Material ferrous_chloride;

	public PollutionMaterials() {
	}

	public static void register() {
		PollutionElementMaterials.register();
		FirstDegreeMaterials.register();
		CatalystMaterials.register();
		CompoundAspectMaterials.register();
		KeqingChainMaterials.register();
	}
}
