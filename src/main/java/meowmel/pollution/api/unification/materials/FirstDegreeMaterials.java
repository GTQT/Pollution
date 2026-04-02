package meowmel.pollution.api.unification.materials;

import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.BlastProperty;
import gregtech.api.unification.material.properties.MaterialToolProperty;
import meowmel.pollution.api.unification.Elements;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.gtqtcore.api.unification.material.GTQTMaterials;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.LOW;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.MID;
import static gregtech.api.util.GTUtility.gregtechId;
import static meowmel.pollution.api.unification.PollutionMaterials.*;
import static meowmel.pollution.api.utils.POUtils.pollutionId;


public class FirstDegreeMaterials {

    private static int startId = 200;
    private static final int END_ID = startId + 800;

    public FirstDegreeMaterials() {
    }

    //第一类材料
    //通常为基本元素组成的化合物，其组分为已注册的单质
    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static void register() {
        //交错铜铁混合物
        PollutionMaterials.Thaummix = new Material.Builder(getMaterialsId(), pollutionId("thaummix"))
                .color(0x5900B3)
                .dust()
                .iconSet(SHINY)
                .build()
                .setFormula("(FeIg5(AeIg)5)(CuTer5(TerOrd)5)", true);

        //六种神秘gcym用材料
        PollutionMaterials.Aertitanium = new Material.Builder(getMaterialsId(), pollutionId("aertitanium"))
                .color(0xEED2EE)
                .ingot().fluid()
                .components(Bauxite, 2, Aluminium, 1, Manganese, 1, InfusedAir, 5)
                .iconSet(SHINY)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.IgnisSteel = new Material.Builder(getMaterialsId(), pollutionId("ignissteel"))
                .color(0x8B1A1A)
                .ingot().fluid()
                .components(Steel, 2, Magnesium, 1, Lithium, 1, InfusedFire, 5)
                .iconSet(SHINY)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.Aquasilver = new Material.Builder(getMaterialsId(), pollutionId("aquasilver"))
                .color(0xCAE1FF)
                .ingot().fluid()
                .components(Silver, 2, Tin, 1, Mercury, 1, InfusedWater, 5)
                .iconSet(SHINY)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.Terracopper = new Material.Builder(getMaterialsId(), pollutionId("terracopper"))
                .color(0x8FBC8F)
                .ingot().fluid()
                .components(Copper, 2, Boron, 1, Carbon, 1, InfusedEarth, 5)
                .iconSet(SHINY)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.Ordolead = new Material.Builder(getMaterialsId(), pollutionId("ordolead"))
                .color(0x00008B)
                .ingot().fluid()
                .components(Lead, 2, Silicon, 1, Gold, 1, InfusedOrder, 5)
                .iconSet(SHINY)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.Perditioaluminium = new Material.Builder(getMaterialsId(), pollutionId("perditioaluminium"))
                .color(0x9C9C9C)
                .ingot().fluid()
                .components(Aluminium, 2, Fluorine, 1, Thorium, 1, InfusedEntropy, 5)
                .iconSet(SHINY)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        //不纯魔力 魔力钢 世界盐 漫宿钢
        PollutionMaterials.Impuremana = new Material.Builder(getMaterialsId(), pollutionId("impuremana"))
                .color(0x008B8B)
                .fluid()
                .iconSet(DULL)
                .build();

        PollutionMaterials.Manasteel = new Material.Builder(getMaterialsId(), pollutionId("manasteel"))
                .color(0x1E90FF)
                .ingot().fluid().ore()
                .components(Iron, 4, GTQTMaterials.Mana, 1)
                .toolStats(new MaterialToolProperty(6, 6, 2048, 5))
                .rotorStats(8.0F, 3.0F, 1024)
                .fluidPipeProperties(2400, 160, true)
                .cableProperties(GTValues.V[3], 16, 2)
                .iconSet(METALLIC)
                .build();

        PollutionMaterials.Salismundus = new Material.Builder(getMaterialsId(), pollutionId("salismundus"))
                .color(0xEE82EE)
                .dust()
                .components(Redstone, 2, GTQTMaterials.Mana, 1)
                .iconSet(SHINY)
                .build();

        PollutionMaterials.Mansussteel = new Material.Builder(getMaterialsId(), pollutionId("mansussteel"))
                .color(0xE6E6FA)
                .ingot().fluid()
                .components(Manasteel, 3, GTQTMaterials.Thaumium, 2, Salismundus, 1)
                .toolStats(new MaterialToolProperty(6, 6, 2048, 5))
                .rotorStats(10.0F, 3.0F, 1440)
                .fluidPipeProperties(2800, 140, true)
                .cableProperties(GTValues.V[4], 16, 4)
                .iconSet(METALLIC)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .blast(b -> b
                        .temp(2700, BlastProperty.GasTier.LOW)
                        .blastStats(VA[HV], 1300)
                        .vacuumStats(VA[MV]))
                .build();

        //泰拉钢
        PollutionMaterials.Terrasteel = new Material.Builder(getMaterialsId(), pollutionId("terrasteel"))
                .color(0x58FF0B)
                .ingot().fluid()
                .components(Iron, 4, Carbon, 4, EnderPearl, 4, GTQTMaterials.Mana, 3)
                .toolStats(new MaterialToolProperty(8, 6, 5120, 6))
                .rotorStats(10.0F, 3.0F, 1440)
                .fluidPipeProperties(3400, 200, true)
                .cableProperties(GTValues.V[5], 16, 2)
                .iconSet(METALLIC)
                .flags(GENERATE_DENSE, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .build();

        //刻金
        PollutionMaterials.KQGold = new Material.Builder(getMaterialsId(), pollutionId("Keqinggold"))
                .color(0xFCF770)
                .fluid().ingot().plasma()
                .iconSet(SHINY)
                .toolStats(new MaterialToolProperty(8, 10, 14400, 8))
                .rotorStats(12.0F, 3.0F, 2400)
                .fluidPipeProperties(6000, 400, true)
                .cableProperties(GTValues.V[6], 16, 2)
                .element(Elements.Kqt)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
                .blast(3600, MID)
                .build();

        //奥利哈
        PollutionMaterials.Orichalcos = new Material.Builder(getMaterialsId(), pollutionId("orichalcos"))
                .color(0xFF00FF)
                .ingot().fluid()
                .components(Iron, 4, Carbon, 4, EnderPearl, 4, GTQTMaterials.Mana, 3)
                .toolStats(new MaterialToolProperty(8, 6, 5120, 6))
                .rotorStats(14.0F, 3.0F, 3200)
                .fluidPipeProperties(8000, 600, true)
                .cableProperties(GTValues.V[7], 16, 2)
                .iconSet(METALLIC)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .build();

        PollutionMaterials.RichAura = new Material.Builder(getMaterialsId(), pollutionId("rich_aura"))
                .color(0xCD6600)
                .fluid()
                .iconSet(SHINY)
                .build();

        PollutionMaterials.ErichAura = new Material.Builder(getMaterialsId(), pollutionId("erich_aura"))
                .color(0xCD0000)
                .fluid()
                .iconSet(SHINY)
                .build();

        PollutionMaterials.ElvenElementium = new Material.Builder(getMaterialsId(), pollutionId("elven_elementium"))
                .color(0xEE6AA7)
                .ingot().fluid().ore()
                .components(Iron, 4, elven, 1)
                .iconSet(SHINY)
                .flags(GENERATE_DENSE, GENERATE_FRAME, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .build();

        //魔法超导线路
        //LK-99粗胚粉、灌魔超导液、初阶神秘超导体、高阶神秘超导体
        PollutionMaterials.crude_lk_99 = new Material.Builder(getMaterialsId(), pollutionId("crude_lk_99"))
                .color(0x808080)
                .ingot().fluid()
                .components(Lead, 6, Copper, 4, Phosphate, 6, Oxygen, 1)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .iconSet(BRIGHT)
                .blast(2700)
                .build();

        PollutionMaterials.magical_superconductive_liquid = new Material.Builder(getMaterialsId(), pollutionId("magical_superconductive_liquid"))
                .color(0x9C039C)
                .fluid()
                .build();

        PollutionMaterials.basic_thaumic_superconductor = new Material.Builder(getMaterialsId(), pollutionId("basic_thaumic_superconductor"))
                .color(0xC6B3C6)
                .ingot().fluid()
                .iconSet(BRIGHT)
                .cableProperties(GTValues.V[4], 8, 0, true)
                .build();

        PollutionMaterials.advanced_thaumic_superconductor = new Material.Builder(getMaterialsId(), pollutionId("advanced_thaumic_superconductor"))
                .color(0xDDFF6E)
                .ingot().fluid()
                .iconSet(BRIGHT)
                .cableProperties(GTValues.V[8], 8, 0, true)
                .build();

        //四乙基铅
        PollutionMaterials.TetraethylLead = new Material.Builder(getMaterialsId(), pollutionId("tetraethyl_lead"))
                .color(0xDDFF6E)
                .fluid()
                .build()
                .setFormula("Pb(C₂H₅)₄", true);

        //三氟化氯 Chlorine trifluoride
        PollutionMaterials.ChlorineTrifluoride = new Material.Builder(getMaterialsId(), pollutionId("chlorine_trifluoride"))
                .color(0xDDFF6E)
                .fluid()
                .build()
                .setFormula("ClF₃", true);

        // 肼硫酸盐 - Hydrazine Sulfate
        PollutionMaterials.hydrazine_sulfate = new Material.Builder(getMaterialsId(), pollutionId("hydrazine_sulfate"))
                .fluid()
                .color(0xFFA500)
                .iconSet(FLUID)
                .build()
                .setFormula("(N₂H₅)₂SO₄", true);

        // 钠铅合金 NaPb
        PollutionMaterials.SodiumLeadAlloy = new Material.Builder(getMaterialsId(), pollutionId("sodium_lead_alloy"))
                .fluid()
                .color(0x9E7B59)
                .components(Sodium, 1, Lead, 1)
                .build();

        //血魔法 血线
        //除杂血
        PollutionMaterials.purified_blood = new Material.Builder(getMaterialsId(), pollutionId("purified_blood"))
                .fluid()
                .color(0xFF6B6B)
                .build();

        //注魔除杂血
        PollutionMaterials.infused_purified_blood = new Material.Builder(getMaterialsId(), pollutionId("infused_purified_blood"))
                .fluid()
                .color(0xFF6B6B)
                .build();

        //奇术凛冰液
        PollutionMaterials.arcane_gelid_fluid = new Material.Builder(getMaterialsId(), pollutionId("arcane_gelid_fluid"))
                .fluid()
                .color(0x66FFFF)
                .build();

        //极寒人造血
        PollutionMaterials.cryogenic_synthetic_blood = new Material.Builder(getMaterialsId(), pollutionId("cryogenic_synthetic_blood"))
                .fluid()
                .color(0x003366)
                .build();

        //秘学运算液基底
        PollutionMaterials.arcane_computational_substrate = new Material.Builder(getMaterialsId(), pollutionId("arcane_computational_substrate"))
                .fluid()
                .color(0x4B0082)
                .build();

        //运算人造血
        PollutionMaterials.synthetic_computational_blood = new Material.Builder(getMaterialsId(), pollutionId("synthetic_computational_blood"))
                .fluid()
                .color(0x8A2BE2)
                .build();
    }
}
