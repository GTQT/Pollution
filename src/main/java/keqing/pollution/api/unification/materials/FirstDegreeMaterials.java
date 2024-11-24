package keqing.pollution.api.unification.materials;

import gregtech.api.GTValues;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.ToolProperty;
import keqing.pollution.api.unification.Elements;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.LOW;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.MID;
import static gregtech.api.util.GTUtility.gregtechId;
import static keqing.pollution.api.unification.PollutionMaterials.*;


public class FirstDegreeMaterials {
	public FirstDegreeMaterials() {
	}

	private static int startId = 16100;
	private static final int END_ID = startId + 300;

	private static int getMaterialsId() {
		if (startId < END_ID) {
			return startId++;
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	public static void register() {
		PollutionMaterials.thaumium = new Material.Builder(getMaterialsId(), gregtechId("thaumium"))
				.color(0x483D8B)
				.ingot().fluid()
				.components(Iron, 1, infused_earth, 5, infused_air, 5, infused_fire, 5, infused_order, 5)
				.fluidPipeProperties(500, 120, true)
				.toolStats(new ToolProperty(5, 4, 1024, 3))
				.iconSet(BRIGHT)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.build()
				.setFormula("FeTer5(AeIgOrd)5", true);

		PollutionMaterials.syrmorite = new Material.Builder(getMaterialsId(), gregtechId("syrmorite"))
				.color(0x2414B3)
				.ingot().fluid().ore()
				.components(Copper, 1, infused_earth, 10, infused_order, 5)
				.toolStats(new ToolProperty(2, 4, 384, 3))
				.rotorStats(6.0F, 3.0F, 512)
				.fluidPipeProperties(1500, 50, true)
				.cableProperties(GTValues.V[2], 2, 2)
				.iconSet(METALLIC)
				.flags(GENERATE_PLATE, GENERATE_ROTOR,GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING,GENERATE_BOLT_SCREW,GENERATE_RING,GENERATE_FRAME,GENERATE_DOUBLE_PLATE,GENERATE_DENSE,GENERATE_FOIL)
				.build()
				.setFormula("CuTer5(TerOrd)5", true);

		PollutionMaterials.octine = new Material.Builder(getMaterialsId(), gregtechId("octine"))
				.color(0xFFAE33)
				.ingot().fluid().ore()
				.components(Iron, 1, infused_fire, 10, infused_air, 5)
				.toolStats(new ToolProperty(2, 4, 256, 3))
				.rotorStats(6.0F, 3.0F, 512)
				.fluidPipeProperties(1250, 100, true)
				.cableProperties(GTValues.V[2], 4, 2)
				.iconSet(SHINY)
				.flags(GENERATE_PLATE,GENERATE_ROTOR, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING,GENERATE_BOLT_SCREW,GENERATE_RING,GENERATE_FRAME,GENERATE_DOUBLE_PLATE,GENERATE_DENSE,GENERATE_FOIL)
				.build()
				.setFormula("FeIg5(AeIg)5", true);

		PollutionMaterials.scabyst = new Material.Builder(getMaterialsId(), gregtechId("scabyst"))
				.color(0x53C58D)
				.gem().fluid().ore()
				.components(Iron, 1, Silicon, 4, Oxygen, 8, infused_fire, 5, infused_earth, 5, infused_order, 10)
				.toolStats(new ToolProperty(4, 4, 288, 4))
				.iconSet(GEM_HORIZONTAL)
				.flags(GENERATE_PLATE, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING,GENERATE_BOLT_SCREW,GENERATE_RING,GENERATE_FRAME,GENERATE_DOUBLE_PLATE,GENERATE_DENSE,GENERATE_FOIL)
				.build()
				.setFormula("((SiO2)4Fe)(IgTerOrd2)5)4", true);

		PollutionMaterials.valonite = new Material.Builder(getMaterialsId(), gregtechId("valonite"))
				.color(0xFFCCFF)
				.gem().fluid().ore()
				.toolStats(new ToolProperty(4, 4, 1024, 4))
				.iconSet(EMERALD)
				.flags(GENERATE_PLATE, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING,GENERATE_BOLT_SCREW,GENERATE_RING,GENERATE_FRAME,GENERATE_DOUBLE_PLATE,GENERATE_DENSE,GENERATE_FOIL)
				.build()
				.setFormula("✴", true);

		PollutionMaterials.thaummix = new Material.Builder(getMaterialsId(), gregtechId("thaummix"))
				.color(0x5900B3)
				.dust()
				.iconSet(SHINY)
				.build()
				.setFormula("(FeIg5(AeIg)5)(CuTer5(TerOrd)5)", true);

		//六种神秘gcym用材料
		PollutionMaterials.aertitanium = new Material.Builder(getMaterialsId(), gregtechId("aertitanium"))
				.color(0xEED2EE)
				.ingot().fluid()
				.components(Bauxite, 2, Aluminium, 1, Manganese, 1, infused_air, 5)
				.iconSet(SHINY)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(2700, LOW)
				.build();

		PollutionMaterials.ignissteel = new Material.Builder(getMaterialsId(), gregtechId("ignissteel"))
				.color(0x8B1A1A)
				.ingot().fluid()
				.components(Steel, 2, Magnesium, 1, Lithium, 1, infused_fire, 5)
				.iconSet(SHINY)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(2700, LOW)
				.build();

		PollutionMaterials.aquasilver = new Material.Builder(getMaterialsId(), gregtechId("aquasilver"))
				.color(0xCAE1FF)
				.ingot().fluid()
				.components(Silver, 2, Tin, 1, Mercury, 1, infused_water, 5)
				.iconSet(SHINY)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(2700, LOW)
				.build();

		PollutionMaterials.terracopper = new Material.Builder(getMaterialsId(), gregtechId("terracopper"))
				.color(0x8FBC8F)
				.ingot().fluid()
				.components(Copper, 2, Boron, 1, Carbon, 1, infused_earth, 5)
				.iconSet(SHINY)
				.flags(GENERATE_DENSE,GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(2700, LOW)
				.build();

		PollutionMaterials.ordolead = new Material.Builder(getMaterialsId(), gregtechId("ordolead"))
				.color(0x00008B)
				.ingot().fluid()
				.components(Lead, 2, Silicon, 1, Gold, 1, infused_order, 5)
				.iconSet(SHINY)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(2700, LOW)
				.build();

		PollutionMaterials.perditioaluminium = new Material.Builder(getMaterialsId(), gregtechId("perditioaluminium"))
				.color(0x9C9C9C)
				.ingot().fluid()
				.components(Aluminium, 2, Fluorine, 1, Thorium, 1, infused_entropy, 5)
				.iconSet(SHINY)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(2700, LOW)
				.build();

		//不纯魔力 魔力钢 世界盐 漫宿钢
		PollutionMaterials.impuremana = new Material.Builder(getMaterialsId(), gregtechId("impuremana"))
				.color(0x008B8B)
				.fluid()
				.iconSet(DULL)
				.build();

		PollutionMaterials.manasteel = new Material.Builder(getMaterialsId(), gregtechId("manasteel"))
				.color(0x1E90FF)
				.ingot().fluid().ore()
				.components(Iron, 4, mana, 1)
				.toolStats(new ToolProperty(6, 6, 2048, 5))
				.rotorStats(8.0F, 3.0F, 1024)
				.fluidPipeProperties(2400, 160, true)
				.cableProperties(GTValues.V[3], 16, 2)
				.iconSet(METALLIC)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.build();

		PollutionMaterials.salismundus = new Material.Builder(getMaterialsId(), gregtechId("salismundus"))
				.color(0xEE82EE)
				.dust()
				.components(Redstone, 2, mana, 1)
				.iconSet(SHINY)
				.build();

		PollutionMaterials.mansussteel = new Material.Builder(getMaterialsId(), gregtechId("mansussteel"))
				.color(0xE6E6FA)
				.ingot().fluid()
				.components(manasteel, 3, thaumium, 2, salismundus, 1)
				.toolStats(new ToolProperty(6, 6, 2048, 5))
				.rotorStats(10.0F, 3.0F, 1440)
				.fluidPipeProperties(2800, 140, true)
				.cableProperties(GTValues.V[4], 16, 4)
				.iconSet(METALLIC)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(2700, LOW)
				.build();

		//泰拉钢
		PollutionMaterials.Terrasteel = new Material.Builder(getMaterialsId(), gregtechId("terrasteel"))
				.color(0x58FF0B)
				.ingot().fluid()
				.components(Iron, 4,Carbon,4,EnderPearl,4, mana, 3)
				.toolStats(new ToolProperty(8, 6, 5120, 6))
				.rotorStats(10.0F, 3.0F, 1440)
				.fluidPipeProperties(3400, 200, true)
				.cableProperties(GTValues.V[5], 16, 2)
				.iconSet(METALLIC)
				.flags(GENERATE_DENSE,GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.build();

		//刻金
		PollutionMaterials.keqinggold = new Material.Builder(getMaterialsId(), gregtechId("Keqinggold"))
				.color(0xFCF770)
				.fluid().ingot()
				.iconSet(SHINY)
				.toolStats(new ToolProperty(8, 10, 14400, 8))
				.rotorStats(12.0F, 3.0F, 2400)
				.fluidPipeProperties(6000, 400, true)
				.cableProperties(GTValues.V[6], 16, 2)
				.element(Elements.Kqt)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
				.blast(3600, MID)
				.build();

		//牢大 想你了
		PollutionMaterials.kobemetal = new Material.Builder(getMaterialsId(), gregtechId("kobemetal"))
				.color(0xFFD700)
				.ingot().fluid()
				.components(Helium, 1, Lithium, 1, Cobalt, 1, Platinum, 1, Erbium, 1)
				.iconSet(SHINY)
				.flags(GENERATE_PLATE, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING)
				.build()
				.setFormula("HeLiCoPtEr", true);

		//注释
		//在 Elements 类下注册元素
		//在 PollutionElementMaterials 类下注册此元素的单质
		//在 FirstDegreeMaterials 类下注册元素的化合物（components使用单质而不是元素）
		PollutionMaterials.RichAura = new Material.Builder(getMaterialsId(), gregtechId("rich_aura"))
				.color(0xCD6600)
				.fluid()
				.iconSet(SHINY)
				.build();

		PollutionMaterials.ErichAura = new Material.Builder(getMaterialsId(), gregtechId("erich_aura"))
				.color(0xCD0000)
				.fluid()
				.iconSet(SHINY)
				.build();

		PollutionMaterials.ElvenElementium = new Material.Builder(getMaterialsId(), gregtechId("elven_elementium"))
				.color(0xEE6AA7)
				.ingot().fluid().ore()
				.components(Iron, 4, elven, 1)
				.iconSet(SHINY)
				.flags(GENERATE_DENSE,GENERATE_FRAME,GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.build();

		//高级合金
		//星辰物质
		//阿弗纳斯之血、光风霁月琥珀金
		PollutionMaterials.blood_of_avernus = new Material.Builder(getMaterialsId(), gregtechId("blood_of_avernus"))
				.color(0x5E0000)
				.ingot().fluid()
				.iconSet(BRIGHT)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.build()
				.setFormula("♆", true);
		PollutionMaterials.iizunamaru_electrum = new Material.Builder(getMaterialsId(), gregtechId("iizunamaru_electrum"))
				.color(0xF0FFB2)
				.ingot().fluid()
				.iconSet(SHINY)
				.flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.build()
				.setFormula("((Ag50(RnMa)m(AeIgAqTerOrdPe)n)Kqt)Sen4", true);

		//交错基础材料：
		//淤泥 Mud
		Mud = new Material.Builder(getMaterialsId(), gregtechId("mud"))
				.liquid()
				.color(0x211b14)
				.build();

	}
}
