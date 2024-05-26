package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import keqing.pollution.api.unification.Elements;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.util.GTUtility.gregtechId;

public class CompoundAspectMaterials {
    public CompoundAspectMaterials() {
    }
    private static int startId = 16200;
    private static final int END_ID = startId + 300;
    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static void register() {
        //水晶=风+地
        PollutionMaterials.infused_crystal = new Material.Builder(getMaterialsId(), gregtechId("infused_crystal"))
                .color(0x87CEFA)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_earth, 1)
                .build();
        //生命=地+水
        PollutionMaterials.infused_life = new Material.Builder(getMaterialsId(), gregtechId("infused_life"))
                .color(0xFF6A6A)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_earth, 1, PollutionMaterials.infused_water, 1)
                .build();
        //死亡=水+混沌
        PollutionMaterials.infused_death = new Material.Builder(getMaterialsId(), gregtechId("infused_death"))
                .color(0x696969)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_water, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //灵魂=生命+死亡
        PollutionMaterials.infused_soul = new Material.Builder(getMaterialsId(), gregtechId("infused_soul"))
                .color(0xCFCFCF)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_life, 1, PollutionMaterials.infused_death, 1)
                .build();
        //武器=灵魂+混沌
        PollutionMaterials.infused_weapon = new Material.Builder(getMaterialsId(), gregtechId("infused_weapon"))
                .color(0xB22222)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_soul, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //金属=地+秩序
        PollutionMaterials.infused_metal = new Material.Builder(getMaterialsId(), gregtechId("infused_metal"))
                .color(0x9FB6CD)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_earth, 1, PollutionMaterials.infused_order, 1)
                .build();
        //能量=秩序+火
        PollutionMaterials.infused_energy = new Material.Builder(getMaterialsId(), gregtechId("infused_energy"))
                .color(0xF0FFF0)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_order, 1, PollutionMaterials.infused_fire, 1)
                .build();
        //工具=金属+能量
        PollutionMaterials.infused_instrument = new Material.Builder(getMaterialsId(), gregtechId("infused_instrument"))
                .color(0x0000CD)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_metal, 1, PollutionMaterials.infused_energy, 1)
                .build();
        //交换=秩序+混沌
        PollutionMaterials.infused_exchange = new Material.Builder(getMaterialsId(), gregtechId("infused_exchange"))
                .color(0x548B54)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_order, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //魔法=风+能量
        PollutionMaterials.infused_magic = new Material.Builder(getMaterialsId(), gregtechId("infused_magic"))
                .color(0x8A2BE2)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_energy, 1)
                .build();
        //炼金=魔法+水
        PollutionMaterials.infused_alchemy = new Material.Builder(getMaterialsId(), gregtechId("infused_alchemy"))
                .color(0x8FBC8F)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_magic, 1, PollutionMaterials.infused_water, 1)
                .build();
        //寒冷=火+混沌
        PollutionMaterials.infused_cold = new Material.Builder(getMaterialsId(), gregtechId("infused_cold"))
                .color(0xF0FFFF)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_fire, 1, PollutionMaterials.infused_entropy, 1)
                .build();
        //灵气=魔法+风
        PollutionMaterials.infused_aura = new Material.Builder(getMaterialsId(), gregtechId("infused_aura"))
                .color(0xF7AEFF)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_magic, 1, PollutionMaterials.infused_air, 1)
                .build();
        //光明=火+风
        PollutionMaterials.infused_light = new Material.Builder(getMaterialsId(), gregtechId("infused_light"))
                .color(0xF3FF80)
                .ore().gem().fluid()
                .components(PollutionMaterials.infused_fire, 1, PollutionMaterials.infused_air, 1)
                .build();
    }
}
