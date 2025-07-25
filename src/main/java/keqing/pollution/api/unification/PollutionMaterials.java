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
	public static Material infused_craft;
	public static Material infused_void;
	public static Material infused_motion;
	public static Material infused_taint;
	public static Material infused_dark;
	public static Material infused_alien;
	public static Material infused_fly;
	public static Material infused_plant;
	public static Material infused_mechanics;
	public static Material infused_trap;
	public static Material infused_undead;
	public static Material infused_thought;
	public static Material infused_sense;
	public static Material infused_animal;
	public static Material infused_human;
	public static Material infused_greed;
	public static Material infused_armor;
	//aspects from Planar Artifice
	public static Material infused_spatio;
	public static Material infused_tempus;
	public static Material infused_tinctura;

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
	public static Material sentient_metal;
	public static Material starrymansus;
	public static Material blood_of_avernus;
	public static Material iizunamaru_electrum;
	public static Material aetheric_dark_steel;
	//污秽线
	public static Material filth;
	public static Material filth_water;
	public static Material void_water;
	public static Material void_material;
	//超导线
	public static Material crude_lk_99;
	public static Material magical_superconductive_liquid;
	public static Material basic_thaumic_superconductor;
	public static Material advanced_thaumic_superconductor;

	public static Material TetraethylLead;
	public static Material ChlorineTrifluoride;
	public static Material hydrazine_sulfate;
	public static Material SodiumLeadAlloy;
	public static Material infernal_blaze_propellant;
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

	public static Material dragon_pulse_fuel;

	public static Material RichAura;
	public static Material ErichAura;
	public static Material sunnarium;
	public static Material whitemansus;
	public static Material blackmansus;
	public static Material Terrasteel;
	public static Material Orichalcos;
	public static Material ElvenElementium;
	public static Material elven;
	public static Material basic_substrate;
	public static Material advanced_substrate;

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
	public static Material binding_metal;
	public static Material existing_nexus;
	public static Material fading_nexus;

	//电池线
	public static Material basic_battery_hull_alloy;
	public static Material advanced_battery_hull_alloy;
	public static Material basic_battery_content;
	public static Material advanced_battery_content;
	//哈基米
	public static Material hydrazoic_acid;
	public static Material sodium_azide;
	public static Material sodium_cyclopentadienide;
	public static Material hafnocene_dichloride;
	public static Material μ_oxo_bis_hafnocene_azide;

	public static Material energy_crystal;
	public static Material starmetal_alloy;

	//其他矿物
	public static Material FlameCoal;
	public static Material DumbTin;
	public static Material MeltGold;
	public static Material AuthorityLead;
	public static Material Pyrargyrite;
	public static Material PlutoZinc;

	public PollutionMaterials() {
	}

	public static void register() {
		PollutionElementMaterials.register();
		FirstDegreeMaterials.register();
		SecondDegreeMaterials.register();
		HigherDegreeMaterials.register();
		OreMaterials.register();
		MaterialPropertyAddition.addMaterialProperties();
		MaterialFlagAddition.init();
	}
}
