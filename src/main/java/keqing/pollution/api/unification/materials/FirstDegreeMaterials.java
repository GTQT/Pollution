package keqing.pollution.api.unification.materials;

import gregtech.api.GTValues;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconSet;
import gregtech.api.unification.material.properties.MaterialToolProperty;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.api.unification.Elements;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.LOW;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.MID;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.api.utils.POUtils.pollutionId;


public class FirstDegreeMaterials {
    private static int startId = 100;
    private static final int END_ID = startId + 900;

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
        //水晶=风+地
        PollutionMaterials.infused_crystal = new Material.Builder(getMaterialsId(), pollutionId("infused_crystal"))
                .color(0x87CEFA)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_earth, 1)
                .build();
        //生命=地+水
        PollutionMaterials.infused_life = new Material.Builder(getMaterialsId(), pollutionId("infused_life"))
                .color(0xFF6A6A)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_earth, 1, PollutionMaterials.infused_water, 1)
                .build();
        //死亡=水+混沌
        PollutionMaterials.infused_death = new Material.Builder(getMaterialsId(), pollutionId("infused_death"))
                .color(0x696969)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_water, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //灵魂=生命+死亡
        PollutionMaterials.infused_soul = new Material.Builder(getMaterialsId(), pollutionId("infused_soul"))
                .color(0xCFCFCF)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_life, 1, PollutionMaterials.infused_death, 1)
                .build();
        //武器=灵魂+混沌
        PollutionMaterials.infused_weapon = new Material.Builder(getMaterialsId(), pollutionId("infused_weapon"))
                .color(0xB22222)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_soul, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //金属=地+秩序
        PollutionMaterials.infused_metal = new Material.Builder(getMaterialsId(), pollutionId("infused_metal"))
                .color(0x9FB6CD)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_earth, 1, PollutionMaterials.infused_order, 1)
                .build();
        //能量=秩序+火
        PollutionMaterials.infused_energy = new Material.Builder(getMaterialsId(), pollutionId("infused_energy"))
                .color(0xF0FFF0)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_order, 1, PollutionMaterials.infused_fire, 1)
                .build();
        //工具=金属+能量
        PollutionMaterials.infused_instrument = new Material.Builder(getMaterialsId(), pollutionId("infused_instrument"))
                .color(0x0000CD)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_metal, 1, PollutionMaterials.infused_energy, 1)
                .build();
        //交换=秩序+混沌
        PollutionMaterials.infused_exchange = new Material.Builder(getMaterialsId(), pollutionId("infused_exchange"))
                .color(0x548B54)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_order, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //魔法=风+能量
        PollutionMaterials.infused_magic = new Material.Builder(getMaterialsId(), pollutionId("infused_magic"))
                .color(0x8A2BE2)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_energy, 1)
                .build();
        //炼金=魔法+水
        PollutionMaterials.infused_alchemy = new Material.Builder(getMaterialsId(), pollutionId("infused_alchemy"))
                .color(0x8FBC8F)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_magic, 1, PollutionMaterials.infused_water, 1)
                .build();
        //寒冷=火+混沌
        PollutionMaterials.infused_cold = new Material.Builder(getMaterialsId(), pollutionId("infused_cold"))
                .color(0xF0FFFF)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_fire, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //灵气=魔法+风
        PollutionMaterials.infused_aura = new Material.Builder(getMaterialsId(), pollutionId("infused_aura"))
                .color(0xF7AEFF)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_magic, 1, PollutionMaterials.infused_air, 1)
                .build();
        //光明=火+风
        PollutionMaterials.infused_light = new Material.Builder(getMaterialsId(), pollutionId("infused_light"))
                .color(0xF3FF80)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_fire, 1, PollutionMaterials.infused_air, 1)
                .build();
        //合成=交换+工具
        PollutionMaterials.infused_craft = new Material.Builder(getMaterialsId(), pollutionId("infused_craft"))
                .color(0x6D26FC)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_exchange, 1, PollutionMaterials.infused_instrument, 1)
                .build();
        //虚空=风+混沌
        PollutionMaterials.infused_void = new Material.Builder(getMaterialsId(), pollutionId("infused_void"))
                .color(0xACACAC)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //运动=风+秩序
        PollutionMaterials.infused_motion = new Material.Builder(getMaterialsId(), pollutionId("infused_motion"))
                .color(0xB3B3B3)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_order, 1)
                .build();
        //腐化=混沌+魔法
        PollutionMaterials.infused_taint = new Material.Builder(getMaterialsId(), pollutionId("infused_taint"))
                .color(0x7C1280)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_magic, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //黑暗=虚空+光明
        PollutionMaterials.infused_dark = new Material.Builder(getMaterialsId(), pollutionId("infused_dark"))
                .color(0x1A1A1A)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_void, 1, PollutionMaterials.infused_light, 1)
                .build();
        //异域=虚空+黑暗
        PollutionMaterials.infused_alien = new Material.Builder(getMaterialsId(), pollutionId("infused_alien"))
                .color(0xC17EC3)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_void, 1, PollutionMaterials.infused_dark, 1)
                .build();
        //飞行=风+运动
        PollutionMaterials.infused_fly = new Material.Builder(getMaterialsId(), pollutionId("infused_fly"))
                .color(0xFFFFEC)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_motion, 1)
                .build();
        //植物=生命+地
        PollutionMaterials.infused_plant = new Material.Builder(getMaterialsId(), pollutionId("infused_plant"))
                .color(0x69FF4B)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_life, 1, PollutionMaterials.infused_earth, 1)
                .build();
        //机械=运动+工具
        PollutionMaterials.infused_mechanics = new Material.Builder(getMaterialsId(), pollutionId("infused_mechanics"))
                .color(0x727272)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_motion, 1, PollutionMaterials.infused_instrument, 1)
                .build();
        //陷阱=运动+混沌
        PollutionMaterials.infused_trap = new Material.Builder(getMaterialsId(), pollutionId("infused_trap"))
                .color(0x756060)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_motion, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //亡灵=运动+死亡
        PollutionMaterials.infused_undead = new Material.Builder(getMaterialsId(), pollutionId("infused_undead"))
                .color(0x494244)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_motion, 1, PollutionMaterials.infused_death, 1)
                .build();
        //思维=火+灵魂
        PollutionMaterials.infused_thought = new Material.Builder(getMaterialsId(), pollutionId("infused_thought"))
                .color(0xFF9999)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_fire, 1, PollutionMaterials.infused_soul, 1)
                .build();
        //感知=风+灵魂
        PollutionMaterials.infused_sense = new Material.Builder(getMaterialsId(), pollutionId("infused_sense"))
                .color(0x57D3FF)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_soul, 1)
                .build();
        //野兽=运动+生命
        PollutionMaterials.infused_animal = new Material.Builder(getMaterialsId(), pollutionId("infused_animal"))
                .color(0x994C00)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_motion, 1, PollutionMaterials.infused_life, 1)
                .build();
        //人类=灵魂+生命
        PollutionMaterials.infused_human = new Material.Builder(getMaterialsId(), pollutionId("infused_human"))
                .color(0xFFD6D1)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_soul, 1, PollutionMaterials.infused_life, 1)
                .build();
        //贪婪=灵魂+虚空
        PollutionMaterials.infused_greed = new Material.Builder(getMaterialsId(), pollutionId("infused_greed"))
                .color(0xF3F303)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_soul, 1, PollutionMaterials.infused_void, 1)
                .build();
        //装备=灵魂+地
        PollutionMaterials.infused_armor = new Material.Builder(getMaterialsId(), pollutionId("infused_armor"))
                .color(0x03CBF3)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_soul, 1, PollutionMaterials.infused_earth, 1)
                .build();

        //空间=虚空+熵
        PollutionMaterials.infused_spatio = new Material.Builder(getMaterialsId(), pollutionId("infused_spatio"))
                .color(0x4AF755)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_void, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //时间=空间+交换
        PollutionMaterials.infused_tempus = new Material.Builder(getMaterialsId(), pollutionId("infused_tempus"))
                .color(0xD6DB43)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_spatio, 1, PollutionMaterials.infused_exchange, 1)
                .build();
        //艺术=感觉+交换
        PollutionMaterials.infused_tinctura = new Material.Builder(getMaterialsId(), pollutionId("infused_tinctura"))
                .color(0xD6DB43)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.infused_sense, 1, PollutionMaterials.infused_exchange, 1)
                .build();

        startId = 200;

        PollutionMaterials.thaumium = new Material.Builder(getMaterialsId(), pollutionId("thaumium"))
                .color(0x483D8B)
                .ingot().fluid().ore(true)
                .components(Iron, 1, infused_earth, 5, infused_air, 5, infused_fire, 5, infused_order, 5)
                .fluidPipeProperties(500, 120, true)
                .toolStats(new MaterialToolProperty(5, 4, 1024, 3))
                .iconSet(BRIGHT)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .build()
                .setFormula("FeTer5(AeIgOrd)5", true);

        //赛摩铜
        PollutionMaterials.syrmorite = new Material.Builder(getMaterialsId(), pollutionId("syrmorite"))
                .color(0x2414B3)
                .ingot().fluid().ore(true)
                .components(Copper, 1, infused_earth, 10, infused_order, 5)
                .toolStats(new MaterialToolProperty(2, 4, 384, 3))
                .rotorStats(6.0F, 3.0F, 512)
                .fluidPipeProperties(1500, 50, true)
                .cableProperties(GTValues.V[2], 2, 2)
                .iconSet(METALLIC)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING, GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_FRAME, GENERATE_DOUBLE_PLATE, GENERATE_DENSE, GENERATE_FOIL)
                .build()
                .setFormula("CuTer5(TerOrd)5", true);

        //炽焰铁
        PollutionMaterials.octine = new Material.Builder(getMaterialsId(), pollutionId("octine"))
                .color(0xFFAE33)
                .ingot().fluid().ore(true)
                .components(Iron, 1, infused_fire, 10, infused_air, 5)
                .toolStats(new MaterialToolProperty(2, 4, 256, 3))
                .rotorStats(6.0F, 3.0F, 512)
                .fluidPipeProperties(1250, 100, true)
                .cableProperties(GTValues.V[2], 4, 2)
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING, GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_FRAME, GENERATE_DOUBLE_PLATE, GENERATE_DENSE, GENERATE_FOIL)
                .build()
                .setFormula("FeIg5(AeIg)5", true);

        //痂壳晶
        PollutionMaterials.scabyst = new Material.Builder(getMaterialsId(), pollutionId("scabyst"))
                .color(0x53C58D)
                .gem().fluid().ore(true)
                .components(Iron, 1, Silicon, 4, Oxygen, 8, infused_fire, 5, infused_earth, 5, infused_order, 10)
                .toolStats(new MaterialToolProperty(4, 4, 288, 4))
                .iconSet(GEM_HORIZONTAL)
                .flags(GENERATE_PLATE, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING, GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_FRAME, GENERATE_DOUBLE_PLATE, GENERATE_DENSE, GENERATE_FOIL)
                .build()
                .setFormula("((SiO2)4Fe)(IgTerOrd2)5)4", true);


        //法罗钠
        PollutionMaterials.valonite = new Material.Builder(getMaterialsId(), pollutionId("valonite"))
                .color(0xFFCCFF)
                .gem().fluid().ore(true)
                .toolStats(new MaterialToolProperty(4, 4, 1024, 4))
                .iconSet(EMERALD)
                .flags(GENERATE_PLATE, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING, GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_FRAME, GENERATE_DOUBLE_PLATE, GENERATE_DENSE, GENERATE_FOIL)
                .build()
                .setFormula("✴", true);

        PollutionMaterials.thaummix = new Material.Builder(getMaterialsId(), pollutionId("thaummix"))
                .color(0x5900B3)
                .dust()
                .iconSet(SHINY)
                .build()
                .setFormula("(FeIg5(AeIg)5)(CuTer5(TerOrd)5)", true);

        //六种神秘gcym用材料
        PollutionMaterials.aertitanium = new Material.Builder(getMaterialsId(), pollutionId("aertitanium"))
                .color(0xEED2EE)
                .ingot().fluid()
                .components(Bauxite, 2, Aluminium, 1, Manganese, 1, infused_air, 5)
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.ignissteel = new Material.Builder(getMaterialsId(), pollutionId("ignissteel"))
                .color(0x8B1A1A)
                .ingot().fluid()
                .components(Steel, 2, Magnesium, 1, Lithium, 1, infused_fire, 5)
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.aquasilver = new Material.Builder(getMaterialsId(), pollutionId("aquasilver"))
                .color(0xCAE1FF)
                .ingot().fluid()
                .components(Silver, 2, Tin, 1, Mercury, 1, infused_water, 5)
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.terracopper = new Material.Builder(getMaterialsId(), pollutionId("terracopper"))
                .color(0x8FBC8F)
                .ingot().fluid()
                .components(Copper, 2, Boron, 1, Carbon, 1, infused_earth, 5)
                .iconSet(SHINY)
                .flags(GENERATE_DENSE, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.ordolead = new Material.Builder(getMaterialsId(), pollutionId("ordolead"))
                .color(0x00008B)
                .ingot().fluid()
                .components(Lead, 2, Silicon, 1, Gold, 1, infused_order, 5)
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        PollutionMaterials.perditioaluminium = new Material.Builder(getMaterialsId(), pollutionId("perditioaluminium"))
                .color(0x9C9C9C)
                .ingot().fluid()
                .components(Aluminium, 2, Fluorine, 1, Thorium, 1, infused_entropy, 5)
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        //不纯魔力 魔力钢 世界盐 漫宿钢
        PollutionMaterials.impuremana = new Material.Builder(getMaterialsId(), pollutionId("impuremana"))
                .color(0x008B8B)
                .fluid()
                .iconSet(DULL)
                .build();

        PollutionMaterials.manasteel = new Material.Builder(getMaterialsId(), pollutionId("manasteel"))
                .color(0x1E90FF)
                .ingot().fluid().ore()
                .components(Iron, 4, GTQTMaterials.Magic, 1)
                .toolStats(new MaterialToolProperty(6, 6, 2048, 5))
                .rotorStats(8.0F, 3.0F, 1024)
                .fluidPipeProperties(2400, 160, true)
                .cableProperties(GTValues.V[3], 16, 2)
                .iconSet(METALLIC)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .build();

        PollutionMaterials.salismundus = new Material.Builder(getMaterialsId(), pollutionId("salismundus"))
                .color(0xEE82EE)
                .dust()
                .components(Redstone, 2, GTQTMaterials.Magic, 1)
                .iconSet(SHINY)
                .build();

        PollutionMaterials.mansussteel = new Material.Builder(getMaterialsId(), pollutionId("mansussteel"))
                .color(0xE6E6FA)
                .ingot().fluid()
                .components(manasteel, 3, thaumium, 2, salismundus, 1)
                .toolStats(new MaterialToolProperty(6, 6, 2048, 5))
                .rotorStats(10.0F, 3.0F, 1440)
                .fluidPipeProperties(2800, 140, true)
                .cableProperties(GTValues.V[4], 16, 4)
                .iconSet(METALLIC)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(2700, LOW)
                .build();

        //泰拉钢
        PollutionMaterials.Terrasteel = new Material.Builder(getMaterialsId(), pollutionId("terrasteel"))
                .color(0x58FF0B)
                .ingot().fluid()
                .components(Iron, 4, Carbon, 4, EnderPearl, 4, GTQTMaterials.Magic, 3)
                .toolStats(new MaterialToolProperty(8, 6, 5120, 6))
                .rotorStats(10.0F, 3.0F, 1440)
                .fluidPipeProperties(3400, 200, true)
                .cableProperties(GTValues.V[5], 16, 2)
                .iconSet(METALLIC)
                .flags(GENERATE_DENSE, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .build();

        //刻金
        PollutionMaterials.keqinggold = new Material.Builder(getMaterialsId(), pollutionId("Keqinggold"))
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
                .components(Iron, 4, Carbon, 4, EnderPearl, 4, GTQTMaterials.Magic, 3)
                .toolStats(new MaterialToolProperty(8, 6, 5120, 6))
                .rotorStats(14.0F, 3.0F, 3200)
                .fluidPipeProperties(8000, 600, true)
                .cableProperties(GTValues.V[7], 16, 2)
                .iconSet(METALLIC)
                .flags(GENERATE_DENSE, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .build();

        //注释
        //在 Elements 类下注册元素
        //在 PollutionElementMaterials 类下注册此元素的单质
        //在 FirstDegreeMaterials 类下注册元素的化合物（components使用单质而不是元素）
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
                .flags(GENERATE_DENSE, GENERATE_FRAME, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
                .cableProperties(GTValues.V[4], 8, 0, true)
                .build();

        PollutionMaterials.advanced_thaumic_superconductor = new Material.Builder(getMaterialsId(), pollutionId("advanced_thaumic_superconductor"))
                .color(0xDDFF6E)
                .ingot().fluid()
                .iconSet(BRIGHT)
                .flags(GENERATE_DENSE, GENERATE_FRAME, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
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
