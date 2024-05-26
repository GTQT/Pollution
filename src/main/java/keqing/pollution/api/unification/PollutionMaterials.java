package keqing.pollution.api.unification;

import gregtech.api.unification.material.Material;
import keqing.pollution.api.unification.materials.CompoundAspectMaterials;
import keqing.pollution.api.unification.materials.FirstDegreeMaterials;
import keqing.pollution.api.unification.materials.PollutionElementMaterials;
import keqing.pollution.api.unification.materials.CatalystMaterials;

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

    public PollutionMaterials() {}

    public static void register() {
        PollutionElementMaterials.register();
        FirstDegreeMaterials.register();
        CatalystMaterials.register();
        CompoundAspectMaterials.register();
    }
}
