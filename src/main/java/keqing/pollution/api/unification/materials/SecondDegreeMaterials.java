package keqing.pollution.api.unification.materials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.attribute.FluidAttributes;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.BlastProperty;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.LOW;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.MID;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.api.utils.POUtils.pollutionId;

public class SecondDegreeMaterials {
    private static int startId = 1000;
    private static final int END_ID = startId + 1000;

    public SecondDegreeMaterials() {
    }
    //第二类材料
    //通常为混合物品或者由不同化合物组成的符合化合物

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static void register() {
        //活性催化粗胚，作为催化剂系列的起始物品
        PollutionMaterials.roughdraft = new Material.Builder(getMaterialsId(), pollutionId("roughdraft"))
                .dust()
                .color(0xCDA5F7)
                .iconSet(DULL)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .components(Amethyst, 1, Opal, 1, CertusQuartz, 1, Sulfur, 1, Mercury, 1, Salt, 1)
                .build();

        //魔力催化场基底，作为和神秘相关设备合成的催化剂基底物质
        PollutionMaterials.substrate = new Material.Builder(getMaterialsId(), pollutionId("substrate"))
                .ingot()
                .color(0xCDA5F7)
                .iconSet(SHINY)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700)
                .components(roughdraft, 6, infused_fire, 1, infused_air, 1, infused_entropy, 1)
                .build();

        //魔法催化线材料
        //荷叶粉 硅酸乙酯 LLP@SiO2粗胚 超亲疏水LLP@SiO2 含LLP原油
        PollutionMaterials.lotus_dust = new Material.Builder(getMaterialsId(), pollutionId("lotus_dust"))
                .color(0x008B45)
                .dust()
                .iconSet(DULL)
                .build();

        PollutionMaterials.ethyl_silicate = new Material.Builder(getMaterialsId(), pollutionId("ethyl_silicate"))
                .color(0x708090)
                .fluid()
                .build()
                .setFormula("(C2H5O)4Si", true);

        PollutionMaterials.rough_llp = new Material.Builder(getMaterialsId(), pollutionId("rough_llp"))
                .color(0xB4EEB4)
                .dust()
                .iconSet(DULL)
                .build();

        PollutionMaterials.llp = new Material.Builder(getMaterialsId(), pollutionId("llp"))
                .color(0xB4EEB4)
                .dust()
                .iconSet(SHINY)
                .build();

        PollutionMaterials.oil_with_llp = new Material.Builder(getMaterialsId(), pollutionId("oil_with_llp"))
                .color(0x698B69)
                .fluid()
                .build();

        //超粘稠焦油 魔力抗爆焦化硝基苯 纯化焦油
        PollutionMaterials.super_sticky_tar = new Material.Builder(getMaterialsId(), pollutionId("super_sticky_tar"))
                .color(0x4F4F4F)
                .fluid()
                .iconSet(SHINY)
                .build();

        PollutionMaterials.magic_nitrobenzene = new Material.Builder(getMaterialsId(), pollutionId("magic_nitrobenzene"))
                .color(0x8B1A1A)
                .fluid()
                .iconSet(SHINY)
                .build();

        PollutionMaterials.pure_tar = new Material.Builder(getMaterialsId(), pollutionId("pure_tar"))
                .liquid(new FluidBuilder()
                        .block())
                .color(0x4F4F4F)
                .build();

        //通用奇术基底，高阶奇术基底，用于锻炉
        PollutionMaterials.basic_substrate = new Material.Builder(getMaterialsId(), pollutionId("basic_substrate"))
                .color(0xFFFFD8)
                .fluid().ingot()
                .build();

        PollutionMaterials.advanced_substrate = new Material.Builder(getMaterialsId(), pollutionId("advanced_substrate"))
                .color(0xD4FFF0)
                .fluid().ingot()
                .build();

        PollutionMaterials.hyper_substrate = new Material.Builder(getMaterialsId(), pollutionId("hyper_substrate"))
                .color(0x8B87FF)
                .fluid().ingot()
                .build();

        PollutionMaterials.sulfo_plumbic_salt = new Material.Builder(getMaterialsId(), pollutionId("sulfo_plumbic_salt"))
                .color(0x5A286F)
                .iconSet(METALLIC)
                .dust()
                .build();

        PollutionMaterials.magical_sulfo_plumbic_salt = new Material.Builder(getMaterialsId(), pollutionId("magical_sulfo_plumbic_salt"))
                .color(0x5A286F)
                .iconSet(SHINY)
                .dust()
                .build();

        PollutionMaterials.alchemical_residue_1 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_residue_1"))
                .color(0x401751)
                .iconSet(DULL)
                .dust()
                .build();

        PollutionMaterials.alchemical_vapor_1 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_vapor_1"))
                .color(0x9159A9)
                .fluid()
                .build();

        PollutionMaterials.alchemical_residue_2 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_residue_2"))
                .color(0x647080)
                .iconSet(DULL)
                .dust()
                .build();

        PollutionMaterials.alchemical_vapor_2 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_vapor_2"))
                .color(0x99CCFF)
                .fluid()
                .build();

        PollutionMaterials.alchemical_residue_3 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_residue_3"))
                .color(0x494949)
                .iconSet(DULL)
                .dust()
                .build();

        PollutionMaterials.alchemical_vapor_3 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_vapor_3"))
                .color(0xD7D7D7)
                .fluid()
                .build();

        PollutionMaterials.alchemical_residue_4 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_residue_4"))
                .color(0x759F8A)
                .iconSet(DULL)
                .dust()
                .build();

        PollutionMaterials.alchemical_vapor_4 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_vapor_4"))
                .color(0x86FCC1)
                .fluid()
                .build();

        PollutionMaterials.alchemical_residue_5 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_residue_5"))
                .color(0x1F447C)
                .iconSet(DULL)
                .dust()
                .build();

        PollutionMaterials.alchemical_vapor_5 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_vapor_5"))
                .color(0x3385FF)
                .fluid()
                .build();

        PollutionMaterials.alchemical_residue_6 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_residue_6"))
                .color(0x6B4B6F)
                .iconSet(DULL)
                .dust()
                .build();

        PollutionMaterials.alchemical_vapor_6 = new Material.Builder(getMaterialsId(), pollutionId("alchemical_vapor_6"))
                .color(0xFCDBF)
                .fluid()
                .build();

        PollutionMaterials.magical_tin_solution = new Material.Builder(getMaterialsId(), pollutionId("magical_tin_solution"))
                .color(0xB9B9B9)
                .fluid()
                .build();

        PollutionMaterials.magical_stannous_sulfate_solution = new Material.Builder(getMaterialsId(), pollutionId("magical_stannous_sulfate_solution"))
                .color(0xF1FF97)
                .fluid()
                .build();

        PollutionMaterials.highmana_stannous_sulfate = new Material.Builder(getMaterialsId(), pollutionId("highmana_stannous_sulfate"))
                .color(0xCCFF33)
                .iconSet(SHINY)
                .dust()
                .build();

        PollutionMaterials.impure_mercuric_salt_solution = new Material.Builder(getMaterialsId(), pollutionId("impure_mercuric_salt_solution"))
                .color(0x95978E)
                .fluid()
                .build();

        PollutionMaterials.mercuric_salt_solution = new Material.Builder(getMaterialsId(), pollutionId("mercuric_salt_solution"))
                .color(0xDBDCD8)
                .fluid()
                .build();

        PollutionMaterials.magic_activated_iron_chloride_solution = new Material.Builder(getMaterialsId(), pollutionId("magic_activated_iron_chloride_solution"))
                .color(0x394320)
                .fluid()
                .build();

        PollutionMaterials.magic_activated_ferrous_chloride_ethanol_solution = new Material.Builder(getMaterialsId(), pollutionId("magic_activated_ferrous_chloride_ethanol_solution"))
                .color(0x80FF66)
                .fluid()
                .build();

        PollutionMaterials.purified_activated_ferrous_chloride_ethanol_solution = new Material.Builder(getMaterialsId(), pollutionId("purified_activated_ferrous_chloride_ethanol_solution"))
                .color(0xACFF9B)
                .fluid()
                .build();

        PollutionMaterials.purified_activated_ferrous_chloride = new Material.Builder(getMaterialsId(), pollutionId("purified_activated_ferrous_chloride"))
                .color(0xDEFFD7)
                .iconSet(SHINY)
                .dust()
                .build();

        PollutionMaterials.syrmorite_doped_magic_water_solution = new Material.Builder(getMaterialsId(), pollutionId("syrmorite_doped_magic_water_solution"))
                .color(0x1D4FDA)
                .fluid()
                .build();

        PollutionMaterials.unformed_embryo_magic_water = new Material.Builder(getMaterialsId(), pollutionId("unformed_embryo_magic_water"))
                .color(0x537EF2)
                .fluid()
                .build();

        PollutionMaterials.embryo_magic_water = new Material.Builder(getMaterialsId(), pollutionId("embryo_magic_water"))
                .color(0xA5BDFF)
                .fluid()
                .build();

        PollutionMaterials.unstable_dimensional_silver = new Material.Builder(getMaterialsId(), pollutionId("unstable_dimensional_silver"))
                .color(0x7D989C)
                .dust()
                .build();

        PollutionMaterials.impure_hyperdimensional_silver = new Material.Builder(getMaterialsId(), pollutionId("impure_hyperdimensional_silver"))
                .color(0x9AD4DC)
                .fluid()
                .build();

        PollutionMaterials.hyperdimensional_silver = new Material.Builder(getMaterialsId(), pollutionId("hyperdimensional_silver"))
                .color(0xD1FAFF)
                .ingot().fluid().plasma()
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(5400, BlastProperty.GasTier.MID)
                .build()
                .setFormula("Ag50(RnMa)m(AeIgAqTerOrdPe)n", true);

        PollutionMaterials.dimensional_transforming_agent = new Material.Builder(getMaterialsId(), pollutionId("dimensional_transforming_agent"))
                .color(0xFFC7F7)
                .fluid()
                .build();

        //氯化亚铁
        PollutionMaterials.ferrous_chloride = new Material.Builder(getMaterialsId(), pollutionId("ferrous_chloride"))
                .color(0x86FF8E)
                .dust()
                .iconSet(BRIGHT)
                .components(Iron, 1, Chlorine, 2)
                .flags(DECOMPOSITION_BY_ELECTROLYZING)
                .build();

        //污秽之物化工线
        PollutionMaterials.filth = new Material.Builder(getMaterialsId(), pollutionId("filth"))
                .color(0x5C0101)
                .dust()
                .iconSet(DULL)
                .components(Netherrack, 6, Endstone, 1, Stone, 1, infused_taint, 1)
                .build();

        PollutionMaterials.filth_water = new Material.Builder(getMaterialsId(), pollutionId("filth_water"))
                .color(0x392323)
                .fluid()
                .components(filth, 9, infused_death, 1, infused_dark, 1)
                .build();

        PollutionMaterials.void_water = new Material.Builder(getMaterialsId(), pollutionId("void_water"))
                .color(0x837D7D)
                .fluid()
                .build();

        PollutionMaterials.void_material = new Material.Builder(getMaterialsId(), pollutionId("void_material"))
                .color(0xC3C3C3)
                .dust()
                .iconSet(SHINY)
                .build();

        //电池相关
        PollutionMaterials.basic_battery_hull_alloy = new Material.Builder(getMaterialsId(), pollutionId("basic_battery_hull_alloy"))
                .color(0x877886)
                .ingot().fluid()
                .iconSet(METALLIC)
                .flags(DECOMPOSITION_BY_ELECTROLYZING)
                .components(mansussteel, 4, ordolead, 1)
                .flags(GENERATE_DENSE, GENERATE_FRAME, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.advanced_battery_hull_alloy = new Material.Builder(getMaterialsId(), pollutionId("advanced_battery_hull_alloy"))
                .color(0xA4D4CD)
                .ingot().fluid()
                .iconSet(METALLIC)
                .flags(DECOMPOSITION_BY_ELECTROLYZING)
                .components(hyperdimensional_silver, 4, valonite, 1)
                .flags(GENERATE_DENSE, GENERATE_FRAME, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
                .blast(5400, MID)
                .build();

        PollutionMaterials.basic_battery_content = new Material.Builder(getMaterialsId(), pollutionId("basic_battery_content"))
                .color(0x687D9F)
                .dust()
                .iconSet(DULL)
                .flags(DECOMPOSITION_BY_ELECTROLYZING)
                .components(Lithium, 6, thaumium, 2, infused_energy, 1, infused_motion, 1)
                .build();

        PollutionMaterials.advanced_battery_content = new Material.Builder(getMaterialsId(), pollutionId("advanced_battery_content"))
                .color(0xFFFFE2)
                .dust()
                .iconSet(BRIGHT)
                .flags(DECOMPOSITION_BY_ELECTROLYZING)
                .components(Caesium, 6, keqinggold, 2, infused_energy, 1, infused_motion, 1)
                .build();

        //龙脉星轨燃剂
        PollutionMaterials.dragon_pulse_fuel = new Material.Builder(getMaterialsId(), pollutionId("dragon_pulse_fuel"))
                .color(0xFFFFE2)
                .fluid()
                .iconSet(BRIGHT)
                .flags(DECOMPOSITION_BY_ELECTROLYZING)
                .build()
                .setFormula("DrAg50(RnMa)m(AeIgAqTerOrdPe)n");

        //焚天烈焰推进剂 烈焰之炽焰+ 肼硫酸盐 + 硝酸 + 铝粉
        PollutionMaterials.infernal_blaze_propellant = new Material.Builder(getMaterialsId(), pollutionId("infernal_blaze_propellant"))
                .fluid()
                .color(0xFF4500) // 橙红色，象征高温与爆炸性
                .iconSet(FLUID)
                .build()
                .setFormula("(BlazingFlame)(N₂H₅)₂SO₄·HNO₃·Al", true);

        //耄耋哈基米线
        //叠氮酸
        PollutionMaterials.hydrazoic_acid = new Material.Builder(getMaterialsId(), pollutionId("hydrazoic_acid"))
                .color(0xFAF0AF)
                .fluid()
                .iconSet(DULL)
                .components(Hydrogen, 1, Nitrogen, 3)
                .build();

        //叠氮化钠
        PollutionMaterials.sodium_azide = new Material.Builder(getMaterialsId(), pollutionId("sodium_azide"))
                .color(0xFAF0AF)
                .dust()
                .iconSet(DULL)
                .components(Sodium, 1, Nitrogen, 3)
                .build();
        //环戊二烯基钠
        PollutionMaterials.sodium_cyclopentadienide = new Material.Builder(getMaterialsId(), pollutionId("sodium_cyclopentadienide"))
                .color(0xF0BB74)
                .dust()
                .iconSet(DULL)
                .components(Carbon, 5, Hydrogen, 5, Sodium, 1)
                .build()
                .setFormula("(C5H5)Na");
        //二氯二茂铪
        PollutionMaterials.hafnocene_dichloride = new Material.Builder(getMaterialsId(), pollutionId("hafnocene_dichloride"))
                .color(0xF0A274)
                .dust()
                .iconSet(DULL)
                .components(Carbon, 10, Hydrogen, 10, Hafnium, 1, Chlorine, 2)
                .build()
                .setFormula("(C5H5)2HfCl2");
        //茂叠铪基醚
        PollutionMaterials.μ_oxo_bis_hafnocene_azide = new Material.Builder(getMaterialsId(), pollutionId("μ_oxo_bis_hafnocene_azide"))
                .color(0xFDDD5A)
                .dust()
                .iconSet(DULL)
                .components(Carbon, 20, Hydrogen, 20, Hafnium, 2, Nitrogen, 6, Oxygen, 1)
                .build()
                .setFormula("[(Cp)2Hf(N3)2]2(μ-O)")
                .setTooltips("能合成这个的家里要请铪基锆了");

        //能量水晶
        PollutionMaterials.energy_crystal = new Material.Builder(getMaterialsId(), pollutionId("energy_crystal"))
                .color(0xF0A274)
                .dust()
                .fluid()
                .iconSet(DULL)
                .components(infused_air, 1, infused_fire, 1, infused_water, 1, infused_earth, 1)
                .build()
                .setFormula("(Ae)(Fi)(Wa)(Ea)", true);

        //StarmetalAlloy
        PollutionMaterials.starmetal_alloy = new Material.Builder(getMaterialsId(), pollutionId("starmetal_alloy"))
                .color(0xF0A274)
                .ingot().fluid()
                .iconSet(METALLIC)
                .blast(5400, MID)
                .build();
    }
}
