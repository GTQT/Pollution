package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
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
		//合成=交换+工具
		PollutionMaterials.infused_craft = new Material.Builder(getMaterialsId(), gregtechId("infused_craft"))
				.color(0x6D26FC)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_exchange, 1, PollutionMaterials.infused_instrument, 1)
				.build();
		//虚空=风+混沌
		PollutionMaterials.infused_void = new Material.Builder(getMaterialsId(), gregtechId("infused_void"))
				.color(0xACACAC)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_entropy, 1)
				.build();
		//运动=风+秩序
		PollutionMaterials.infused_motion = new Material.Builder(getMaterialsId(), gregtechId("infused_motion"))
				.color(0xB3B3B3)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_order, 1)
				.build();
		//腐化=混沌+魔法
		PollutionMaterials.infused_taint = new Material.Builder(getMaterialsId(), gregtechId("infused_taint"))
				.color(0x7C1280)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_magic, 1, PollutionMaterials.infused_entropy, 1)
				.build();
		//黑暗=虚空+光明
		PollutionMaterials.infused_dark = new Material.Builder(getMaterialsId(), gregtechId("infused_dark"))
				.color(0x1A1A1A)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_void, 1, PollutionMaterials.infused_light, 1)
				.build();
		//异域=虚空+黑暗
		PollutionMaterials.infused_alien = new Material.Builder(getMaterialsId(), gregtechId("infused_alien"))
				.color(0xC17EC3)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_void, 1, PollutionMaterials.infused_dark, 1)
				.build();
		//飞行=风+运动
		PollutionMaterials.infused_fly = new Material.Builder(getMaterialsId(), gregtechId("infused_fly"))
				.color(0xFFFFEC)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_motion, 1)
				.build();
		//植物=生命+地
		PollutionMaterials.infused_plant = new Material.Builder(getMaterialsId(), gregtechId("infused_plant"))
				.color(0x69FF4B)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_life, 1, PollutionMaterials.infused_earth, 1)
				.build();
		//机械=运动+工具
		PollutionMaterials.infused_mechanics = new Material.Builder(getMaterialsId(), gregtechId("infused_mechanics"))
				.color(0x727272)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_motion, 1, PollutionMaterials.infused_instrument, 1)
				.build();
		//陷阱=运动+混沌
		PollutionMaterials.infused_trap = new Material.Builder(getMaterialsId(), gregtechId("infused_trap"))
				.color(0x756060)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_motion, 1, PollutionMaterials.infused_entropy, 1)
				.build();
		//亡灵=运动+死亡
		PollutionMaterials.infused_undead = new Material.Builder(getMaterialsId(), gregtechId("infused_undead"))
				.color(0x494244)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_motion, 1, PollutionMaterials.infused_death, 1)
				.build();
		//思维=火+灵魂
		PollutionMaterials.infused_thought = new Material.Builder(getMaterialsId(), gregtechId("infused_thought"))
				.color(0xFF9999)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_fire, 1, PollutionMaterials.infused_soul, 1)
				.build();
		//感知=风+灵魂
		PollutionMaterials.infused_sense = new Material.Builder(getMaterialsId(), gregtechId("infused_sense"))
				.color(0x57D3FF)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_air, 1, PollutionMaterials.infused_soul, 1)
				.build();
		//野兽=运动+生命
		PollutionMaterials.infused_animal = new Material.Builder(getMaterialsId(), gregtechId("infused_animal"))
				.color(0x994C00)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_motion, 1, PollutionMaterials.infused_life, 1)
				.build();
		//人类=灵魂+生命
		PollutionMaterials.infused_human = new Material.Builder(getMaterialsId(), gregtechId("infused_human"))
				.color(0xFFD6D1)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_soul, 1, PollutionMaterials.infused_life, 1)
				.build();
		//贪婪=灵魂+虚空
		PollutionMaterials.infused_greed = new Material.Builder(getMaterialsId(), gregtechId("infused_greed"))
				.color(0xF3F303)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_soul, 1, PollutionMaterials.infused_void, 1)
				.build();
		//装备=灵魂+地
		PollutionMaterials.infused_armor = new Material.Builder(getMaterialsId(), gregtechId("infused_armor"))
				.color(0x03CBF3)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_soul, 1, PollutionMaterials.infused_earth, 1)
				.build();

		//空间=虚空+熵
		PollutionMaterials.infused_spatio = new Material.Builder(getMaterialsId(), gregtechId("infused_spatio"))
				.color(0x4AF755)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_void, 1, PollutionMaterials.infused_entropy, 1)
				.build();
		//时间=空间+交换
		PollutionMaterials.infused_tempus = new Material.Builder(getMaterialsId(), gregtechId("infused_tempus"))
				.color(0xD6DB43)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_spatio, 1, PollutionMaterials.infused_exchange, 1)
				.build();
		//艺术=感觉+交换
		PollutionMaterials.infused_tinctura = new Material.Builder(getMaterialsId(), gregtechId("infused_tinctura"))
				.color(0xD6DB43)
				.ore().gem().fluid()
				.components(PollutionMaterials.infused_sense, 1, PollutionMaterials.infused_exchange, 1)
				.build();
	}
}
