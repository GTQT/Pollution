package keqing.pollution.api.unification.materials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.Elements;
import gregtech.api.unification.material.Material;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.Materials.EXT2_METAL;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static gregtech.api.unification.material.info.MaterialIconSet.LIGNITE;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static gregtech.api.util.GTUtility.gregtechId;
import static keqing.gtqtcore.api.GTQTValue.gtqtcoreId;
import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.api.utils.POUtils.pollutionId;

public class OreMaterials {
    private static int startId = 3000;
    private static final int END_ID = startId + 1000;

    public OreMaterials() {
    }

    //矿物材料
    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static void register() {
        //固焰煤 红色
        PollutionMaterials.FlameCoal = new Material.Builder(getMaterialsId(), pollutionId("flame_coal"))
                .gem(1, 2400).ore(2, 1,true) // default coal burn time in vanilla
                .color(0xFF6347).iconSet(LIGNITE)
                .flags(FLAMMABLE, NO_SMELTING, NO_SMASHING, MORTAR_GRINDABLE, EXCLUDE_BLOCK_CRAFTING_BY_HAND_RECIPES,
                        DISABLE_DECOMPOSITION)
                .components(Carbon, 1, infused_fire, 7, infused_earth, 3)
                .build()
                .setTooltips("似乎比煤炭蕴含更多能量");

        //哑泽锡
        PollutionMaterials.DumbTin = new Material.Builder(getMaterialsId(), pollutionId("dumb_tin"))
                .ingot(1)
                .liquid(new FluidBuilder().temperature(505))
                .plasma()
                .ore(true)
                .color(0xDCDCDC)
                .flags(EXT2_METAL, MORTAR_GRINDABLE, GENERATE_ROTOR, GENERATE_SPRING, GENERATE_SPRING_SMALL,
                        GENERATE_FINE_WIRE, GENERATE_DOUBLE_PLATE)
                .components(Tin, 1, infused_water, 5, infused_air, 2)
                .cableProperties(V[LV], 4, 1)
                .itemPipeProperties(4096, 0.8f)
                .build()
                .setTooltips("斑驳的外表下略显光泽");

        //铄世金
        PollutionMaterials.MeltGold = new Material.Builder(getMaterialsId(), pollutionId("melt_gold"))
                .ingot()
                .liquid(new FluidBuilder().temperature(1337))
                .ore(true)
                .color(0xFFE650).iconSet(SHINY)
                .flags(EXT2_METAL, GENERATE_RING, MORTAR_GRINDABLE, EXCLUDE_BLOCK_CRAFTING_BY_HAND_RECIPES,
                        GENERATE_SPRING, GENERATE_SPRING_SMALL, GENERATE_FINE_WIRE, GENERATE_FOIL,
                        GENERATE_DOUBLE_PLATE)
                .components(Gold, 1, infused_fire, 5, infused_air, 7)
                .cableProperties(V[HV], 6, 2)
                .fluidPipeProperties(1671, 50, true, true, false, false)
                .build()
                .setTooltips("隐约在振动");

        //镇渊铅 AuthorityLead
        PollutionMaterials.AuthorityLead = new Material.Builder(getMaterialsId(), pollutionId("authority_lead"))
                .ingot(1)
                .liquid(new FluidBuilder().temperature(600))
                .ore(true)
                .color(0x8C648C)
                .flags(EXT2_METAL, MORTAR_GRINDABLE, GENERATE_ROTOR, GENERATE_SPRING, GENERATE_SPRING_SMALL,
                        GENERATE_FINE_WIRE, GENERATE_DOUBLE_PLATE)
                .components(Lead, 1, infused_fire, 5, infused_order, 7)
                .cableProperties(V[LV], 16, 2)
                .fluidPipeProperties(1600, 64, true)
                .build()
                .setTooltips("不可视质量");

        // 深红银矿
        PollutionMaterials.Pyrargyrite = new Material.Builder(getMaterialsId(), pollutionId("pyrargyrite"))
                .ore(true)
                .dust().fluid()
                .color(0x8B4726)
                .components(Silver, 3, Antimony, 1, Sulfur, 3, infused_fire, 5, infused_air, 7)
                .build()
                .setTooltips("它在看我？");

        //冥晶锌
        PollutionMaterials.PlutoZinc = new Material.Builder(getMaterialsId(), pollutionId("pluto_zinc"))
                .ore(true)
                .dust().fluid()
                .color(0x8B2252)
                .components(Zinc, 2, Antimony, 1, Oxygen, 1, infused_water, 5, infused_earth, 7)
                .build()
                .setTooltips("你无法移开视线");


    }
}
