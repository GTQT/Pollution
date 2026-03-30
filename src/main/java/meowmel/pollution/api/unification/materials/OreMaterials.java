package meowmel.pollution.api.unification.materials;

import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.MaterialToolProperty;
import meowmel.pollution.api.unification.PollutionMaterials;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static meowmel.pollution.api.unification.PollutionMaterials.*;
import static meowmel.pollution.api.utils.POUtils.pollutionId;

public class OreMaterials {
    private static int startId = 150;
    private static final int END_ID = startId + 50;

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
        //赛摩铜
        PollutionMaterials.Syrmorite = new Material.Builder(getMaterialsId(), pollutionId("syrmorite"))
                .color(0x2414B3)
                .ingot().fluid().ore(true)
                .components(Copper, 1, InfusedEarth, 10, InfusedOrder, 5)
                .toolStats(new MaterialToolProperty(2, 4, 384, 3))
                .rotorStats(6.0F, 3.0F, 512)
                .fluidPipeProperties(1500, 50, true)
                .cableProperties(GTValues.V[2], 2, 2)
                .iconSet(METALLIC)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING, GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_FRAME, GENERATE_DOUBLE_PLATE, GENERATE_DENSE, GENERATE_FOIL)
                .build()
                .setFormula("CuTer5(TerOrd)5", true);

        //炽焰铁
        PollutionMaterials.Octine = new Material.Builder(getMaterialsId(), pollutionId("octine"))
                .color(0xFFAE33)
                .ingot().fluid().ore(true)
                .components(Iron, 1, InfusedFire, 10, InfusedAir, 5)
                .toolStats(new MaterialToolProperty(2, 4, 256, 3))
                .rotorStats(6.0F, 3.0F, 512)
                .fluidPipeProperties(1250, 100, true)
                .cableProperties(GTValues.V[2], 4, 2)
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING, GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_FRAME, GENERATE_DOUBLE_PLATE, GENERATE_DENSE, GENERATE_FOIL)
                .build()
                .setFormula("FeIg5(AeIg)5", true);

        //痂壳晶
        PollutionMaterials.Scabyst = new Material.Builder(getMaterialsId(), pollutionId("scabyst"))
                .color(0x53C58D)
                .gem().fluid().ore(true)
                .components(Iron, 1, Silicon, 4, Oxygen, 8, InfusedFire, 5, InfusedEarth, 5, InfusedOrder, 10)
                .toolStats(new MaterialToolProperty(4, 4, 288, 4))
                .iconSet(GEM_HORIZONTAL)
                .flags(GENERATE_PLATE, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING, GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_FRAME, GENERATE_DOUBLE_PLATE, GENERATE_DENSE, GENERATE_FOIL)
                .build()
                .setFormula("((SiO2)4Fe)(IgTerOrd2)5)4", true);

        //法罗钠
        PollutionMaterials.Valonite = new Material.Builder(getMaterialsId(), pollutionId("valonite"))
                .color(0xFFCCFF)
                .gem().fluid().ore(true)
                .toolStats(new MaterialToolProperty(4, 4, 1024, 4))
                .iconSet(EMERALD)
                .flags(GENERATE_PLATE, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING, GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_FRAME, GENERATE_DOUBLE_PLATE, GENERATE_DENSE, GENERATE_FOIL)
                .build()
                .setFormula("✴", true);

        //固焰煤 红色
        PollutionMaterials.FlameCoal = new Material.Builder(getMaterialsId(), pollutionId("flame_coal"))
                .gem(1, 2400).ore(2, 1, true) // default coal burn time in vanilla
                .color(0xFF6347).iconSet(LIGNITE)
                .flags(FLAMMABLE, NO_SMELTING, NO_SMASHING, MORTAR_GRINDABLE, EXCLUDE_BLOCK_CRAFTING_BY_HAND_RECIPES,
                        DISABLE_DECOMPOSITION)
                .components(Carbon, 1, InfusedFire, 7, InfusedEarth, 3)
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
                .components(Tin, 1, InfusedWater, 5, InfusedAir, 2)
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
                .components(Gold, 1, InfusedFire, 5, InfusedAir, 7)
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
                .components(Lead, 1, InfusedFire, 5, InfusedOrder, 7)
                .cableProperties(V[LV], 16, 2)
                .fluidPipeProperties(1600, 64, true)
                .build()
                .setTooltips("不可视质量");

        // 深红银矿
        PollutionMaterials.Pyrargyrite = new Material.Builder(getMaterialsId(), pollutionId("pyrargyrite"))
                .ore(true)
                .dust().fluid()
                .color(0x8B4726)
                .components(Silver, 3, Antimony, 1, Sulfur, 3, InfusedFire, 5, InfusedAir, 7)
                .build()
                .setTooltips("它在看我？");

        //冥晶锌
        PollutionMaterials.PlutoZinc = new Material.Builder(getMaterialsId(), pollutionId("pluto_zinc"))
                .ore(true)
                .dust().fluid()
                .color(0x8B2252)
                .components(Zinc, 2, Antimony, 1, Oxygen, 1, InfusedWater, 5, InfusedEarth, 7)
                .build()
                .setTooltips("你无法移开视线");
    }
}
