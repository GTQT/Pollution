package meowmel.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconSet;
import meowmel.pollution.api.unification.PollutionMaterials;

import static meowmel.pollution.api.utils.POUtils.pollutionId;

public class InfusedMaterials {
    private static int startId = 100;
    private static final int END_ID = startId + 50;

    public InfusedMaterials() {
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
        PollutionMaterials.InfusedCrystal = new Material.Builder(getMaterialsId(), pollutionId("infused_crystal"))
                .color(0x87CEFA)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedAir, 1, PollutionMaterials.InfusedEarth, 1)
                .build()
                .setTooltips("Vitreus");

        //生命=地+水
        PollutionMaterials.InfusedLife = new Material.Builder(getMaterialsId(), pollutionId("infused_life"))
                .color(0xFF6A6A)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedEarth, 1, PollutionMaterials.InfusedWater, 1)
                .build()
                .setTooltips("Victus");

        //死亡=水+混沌
        PollutionMaterials.InfusedDeath = new Material.Builder(getMaterialsId(), pollutionId("infused_death"))
                .color(0x696969)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedWater, 1, PollutionMaterials.InfusedEntropy, 1)
                .build()
                .setTooltips("Mortuus");

        //灵魂=生命+死亡
        PollutionMaterials.InfusedSoul = new Material.Builder(getMaterialsId(), pollutionId("infused_soul"))
                .color(0xCFCFCF)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedLife, 1, PollutionMaterials.InfusedDeath, 1)
                .build()
                .setTooltips("Spiritus");

        //武器=灵魂+混沌
        PollutionMaterials.InfusedWeapon = new Material.Builder(getMaterialsId(), pollutionId("infused_weapon"))
                .color(0xB22222)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedSoul, 1, PollutionMaterials.InfusedEntropy, 1)
                .build()
                .setTooltips("Aversio");

        //金属=地+秩序
        PollutionMaterials.InfusedMetal = new Material.Builder(getMaterialsId(), pollutionId("infused_metal"))
                .color(0x9FB6CD)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedEarth, 1, PollutionMaterials.InfusedOrder, 1)
                .build()
                .setTooltips("Metallum");

        //能量=秩序+火
        PollutionMaterials.InfusedEnergy = new Material.Builder(getMaterialsId(), pollutionId("infused_energy"))
                .color(0xF0FFF0)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedOrder, 1, PollutionMaterials.InfusedFire, 1)
                .build()
                .setTooltips("Potentia");

        //工具=金属+能量
        PollutionMaterials.InfusedInstrument = new Material.Builder(getMaterialsId(), pollutionId("infused_instrument"))
                .color(0x0000CD)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedMetal, 1, PollutionMaterials.InfusedEnergy, 1)
                .build()
                .setTooltips("Instrumentum");

        //交换=秩序+混沌
        PollutionMaterials.InfusedExchange = new Material.Builder(getMaterialsId(), pollutionId("infused_exchange"))
                .color(0x548B54)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedOrder, 1, PollutionMaterials.InfusedEntropy, 1)
                .build()
                .setTooltips("Permutatio");

        //魔法=风+能量
        PollutionMaterials.InfusedMagic = new Material.Builder(getMaterialsId(), pollutionId("infused_magic"))
                .color(0x8A2BE2)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedAir, 1, PollutionMaterials.InfusedEnergy, 1)
                .build()
                .setTooltips("Magia");

        //炼金=魔法+水
        PollutionMaterials.InfusedAlchemy = new Material.Builder(getMaterialsId(), pollutionId("infused_alchemy"))
                .color(0x8FBC8F)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedMagic, 1, PollutionMaterials.InfusedWater, 1)
                .build()
                .setTooltips("Alchemia");

        //寒冷=火+混沌
        PollutionMaterials.InfusedCold = new Material.Builder(getMaterialsId(), pollutionId("infused_cold"))
                .color(0xF0FFFF)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedFire, 1, PollutionMaterials.InfusedEntropy, 1)
                .build()
                .setTooltips("Gelum");

        //灵气=魔法+风
        PollutionMaterials.InfusedAura = new Material.Builder(getMaterialsId(), pollutionId("infused_aura"))
                .color(0xF7AEFF)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedMagic, 1, PollutionMaterials.InfusedAir, 1)
                .build()
                .setTooltips("Aura");

        //光明=火+风
        PollutionMaterials.InfusedLight = new Material.Builder(getMaterialsId(), pollutionId("infused_light"))
                .color(0xF3FF80)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedFire, 1, PollutionMaterials.InfusedAir, 1)
                .build()
                .setTooltips("Lux");

        //合成=交换+工具
        PollutionMaterials.InfusedCraft = new Material.Builder(getMaterialsId(), pollutionId("infused_craft"))
                .color(0x6D26FC)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedExchange, 1, PollutionMaterials.InfusedInstrument, 1)
                .build()
                .setTooltips("Ars");

        //虚空=风+混沌
        PollutionMaterials.InfusedVoid = new Material.Builder(getMaterialsId(), pollutionId("infused_void"))
                .color(0xACACAC)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedAir, 1, PollutionMaterials.InfusedEntropy, 1)
                .build()
                .setTooltips("Inanis");

        //运动=风+秩序
        PollutionMaterials.InfusedMotion = new Material.Builder(getMaterialsId(), pollutionId("infused_motion"))
                .color(0xB3B3B3)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedAir, 1, PollutionMaterials.InfusedOrder, 1)
                .build()
                .setTooltips("Motus");

        //腐化=混沌+魔法
        PollutionMaterials.InfusedTaint = new Material.Builder(getMaterialsId(), pollutionId("infused_taint"))
                .color(0x7C1280)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedMagic, 1, PollutionMaterials.InfusedEntropy, 1)
                .build()
                .setTooltips("Corruptio");

        //黑暗=虚空+光明
        PollutionMaterials.InfusedDark = new Material.Builder(getMaterialsId(), pollutionId("infused_dark"))
                .color(0x1A1A1A)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedVoid, 1, PollutionMaterials.InfusedLight, 1)
                .build()
                .setTooltips("Tenebrae");

        //异域=虚空+黑暗
        PollutionMaterials.InfusedAlien = new Material.Builder(getMaterialsId(), pollutionId("infused_alien"))
                .color(0xC17EC3)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedVoid, 1, PollutionMaterials.InfusedDark, 1)
                .build()
                .setTooltips("Alienus");

        //飞行=风+运动
        PollutionMaterials.InfusedFly = new Material.Builder(getMaterialsId(), pollutionId("infused_fly"))
                .color(0xFFFFEC)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedAir, 1, PollutionMaterials.InfusedMotion, 1)
                .build()
                .setTooltips("Volatus");

        //植物=生命+地
        PollutionMaterials.InfusedPlant = new Material.Builder(getMaterialsId(), pollutionId("infused_plant"))
                .color(0x69FF4B)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedLife, 1, PollutionMaterials.InfusedEarth, 1)
                .build()
                .setTooltips("Herba");

        //机械=运动+工具
        PollutionMaterials.InfusedMechanics = new Material.Builder(getMaterialsId(), pollutionId("infused_mechanics"))
                .color(0x727272)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedMotion, 1, PollutionMaterials.InfusedInstrument, 1)
                .build()
                .setTooltips("Machina");

        //陷阱=运动+混沌
        PollutionMaterials.InfusedTrap = new Material.Builder(getMaterialsId(), pollutionId("infused_trap"))
                .color(0x756060)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedMotion, 1, PollutionMaterials.InfusedEntropy, 1)
                .build()
                .setTooltips("Insidiae");

        //亡灵=运动+死亡
        PollutionMaterials.InfusedUndead = new Material.Builder(getMaterialsId(), pollutionId("infused_undead"))
                .color(0x494244)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedMotion, 1, PollutionMaterials.InfusedDeath, 1)
                .build()
                .setTooltips("Mortui");

        //思维=火+灵魂
        PollutionMaterials.InfusedThought = new Material.Builder(getMaterialsId(), pollutionId("infused_thought"))
                .color(0xFF9999)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedFire, 1, PollutionMaterials.InfusedSoul, 1)
                .build()
                .setTooltips("Cogitatio");

        //感知=风+灵魂
        PollutionMaterials.InfusedSense = new Material.Builder(getMaterialsId(), pollutionId("infused_sense"))
                .color(0x57D3FF)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedAir, 1, PollutionMaterials.InfusedSoul, 1)
                .build()
                .setTooltips("Sensus");

        //野兽=运动+生命
        PollutionMaterials.InfusedAnimal = new Material.Builder(getMaterialsId(), pollutionId("infused_animal"))
                .color(0x994C00)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedMotion, 1, PollutionMaterials.InfusedLife, 1)
                .build()
                .setTooltips("Bestia");

        //人类=灵魂+生命
        PollutionMaterials.InfusedHuman = new Material.Builder(getMaterialsId(), pollutionId("infused_human"))
                .color(0xFFD6D1)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedSoul, 1, PollutionMaterials.InfusedLife, 1)
                .build()
                .setTooltips("Humanus");

        //贪婪=灵魂+虚空
        PollutionMaterials.InfusedGreed = new Material.Builder(getMaterialsId(), pollutionId("infused_greed"))
                .color(0xF3F303)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedSoul, 1, PollutionMaterials.InfusedVoid, 1)
                .build()
                .setTooltips("Avaritia");

        //装备=灵魂+地
        PollutionMaterials.InfusedArmor = new Material.Builder(getMaterialsId(), pollutionId("infused_armor"))
                .color(0x03CBF3)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedSoul, 1, PollutionMaterials.InfusedEarth, 1)
                .build()
                .setTooltips("Armatura");

        //空间=虚空+熵
        PollutionMaterials.InfusedSpatio = new Material.Builder(getMaterialsId(), pollutionId("infused_spatio"))
                .color(0x4AF755)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedVoid, 1, PollutionMaterials.InfusedEntropy, 1)
                .build()
                .setTooltips("Spatium");

        //时间=空间+交换
        PollutionMaterials.InfusedTempus = new Material.Builder(getMaterialsId(), pollutionId("infused_tempus"))
                .color(0xD6DB43)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedSpatio, 1, PollutionMaterials.InfusedExchange, 1)
                .build()
                .setTooltips("Tempus");

        //艺术=感觉+交换
        PollutionMaterials.InfusedTinctura = new Material.Builder(getMaterialsId(), pollutionId("infused_tinctura"))
                .color(0xD6DB43)
                .ore().gem().fluid()
                .iconSet(MaterialIconSet.SHINY)
                .components(PollutionMaterials.InfusedSense, 1, PollutionMaterials.InfusedExchange, 1)
                .build()
                .setTooltips("Tinctura");
    }
}
