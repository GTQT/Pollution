package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.DECOMPOSITION_BY_CENTRIFUGING;
import static gregtech.api.unification.material.info.MaterialIconSet.DULL;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static gregtech.api.util.GTUtility.gregtechId;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class CatalystMaterials {

	public CatalystMaterials() {
	}

	private static int startId = 16400;
	private static final int END_ID = startId + 300;

	private static int getMaterialsId() {
		if (startId < END_ID) {
			return startId++;
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	public static void register() {

		//活性催化粗胚，作为催化剂系列的起始物品
		PollutionMaterials.roughdraft = new Material.Builder(getMaterialsId(), gregtechId("roughdraft"))
				.dust()
				.color(0xCDA5F7)
				.iconSet(DULL)
				.flags(DECOMPOSITION_BY_CENTRIFUGING)
				.components(Amethyst, 1, Opal, 1, CertusQuartz, 1, Sulfur, 1, Mercury, 1, Salt, 1)
				.build();

		//魔力催化场基底，作为和神秘相关设备合成的催化剂基底物质
		PollutionMaterials.substrate = new Material.Builder(getMaterialsId(), gregtechId("substrate"))
				.ingot()
				.color(0xCDA5F7)
				.iconSet(SHINY)
				.flags(DECOMPOSITION_BY_CENTRIFUGING)
				.blast(2700)
				.components(roughdraft, 6, infused_fire, 1, infused_air, 1, infused_entropy, 1)
				.build();

		//魔法催化线材料
		//荷叶粉 硅酸乙酯 LLP@SiO2粗胚 超亲疏水LLP@SiO2 含LLP原油
		PollutionMaterials.lotus_dust = new Material.Builder(getMaterialsId(), gregtechId("lotus_dust"))
				.color(0x008B45)
				.dust()
				.iconSet(DULL)
				.build();
		PollutionMaterials.ethyl_silicate = new Material.Builder(getMaterialsId(), gregtechId("ethyl_silicate"))
				.color(0x708090)
				.fluid()
				.build()
				.setFormula("(C2H5O)4Si", true);
		PollutionMaterials.rough_llp = new Material.Builder(getMaterialsId(), gregtechId("rough_llp"))
				.color(0xB4EEB4)
				.dust()
				.iconSet(DULL)
				.build();
		PollutionMaterials.llp = new Material.Builder(getMaterialsId(), gregtechId("llp"))
				.color(0xB4EEB4)
				.dust()
				.iconSet(SHINY)
				.build();
		PollutionMaterials.oil_with_llp = new Material.Builder(getMaterialsId(), gregtechId("oil_with_llp"))
				.color(0x698B69)
				.fluid()
				.build();
		//超粘稠焦油 魔力抗爆焦化硝基苯 纯化焦油
		PollutionMaterials.super_sticky_tar = new Material.Builder(getMaterialsId(), gregtechId("super_sticky_tar"))
				.color(0x4F4F4F)
				.fluid()
				.iconSet(SHINY)
				.build();
		PollutionMaterials.magic_nitrobenzene = new Material.Builder(getMaterialsId(), gregtechId("magic_nitrobenzene"))
				.color(0x8B1A1A)
				.fluid()
				.iconSet(SHINY)
				.build();
		PollutionMaterials.pure_tar = new Material.Builder(getMaterialsId(), gregtechId("pure_tar"))
				.color(0x4F4F4F)
				.fluid()
				.build();
	}
}
