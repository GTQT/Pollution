package keqing.pollution.api.unification.materials;

import gregtech.api.GTValues;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.ToolProperty;
import keqing.pollution.api.unification.Elements;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.*;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.*;
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
				.fluid().ingot().plasma()
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

		//魔法超导线路
		//LK-99粗胚粉、灌魔超导液、初阶神秘超导体、高阶神秘超导体
		PollutionMaterials.crude_lk_99 = new Material.Builder(getMaterialsId(), gregtechId("crude_lk_99"))
				.color(0x808080)
				.ingot().fluid()
				.components(Lead, 6, Copper, 4, Phosphate, 6, Oxygen, 1)
				.flags(DECOMPOSITION_BY_CENTRIFUGING)
				.iconSet(BRIGHT)
				.blast(2700)
				.build();
		PollutionMaterials.magical_superconductive_liquid = new Material.Builder(getMaterialsId(), gregtechId("magical_superconductive_liquid"))
				.color(0x9C039C)
				.fluid()
				.build();
		PollutionMaterials.basic_thaumic_superconductor = new Material.Builder(getMaterialsId(), gregtechId("basic_thaumic_superconductor"))
				.color(0xC6B3C6)
				.ingot().fluid()
				.iconSet(BRIGHT)
				.flags(GENERATE_DENSE,GENERATE_FRAME,GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
				.cableProperties(GTValues.V[4], 8, 0, true)
				.build();
		PollutionMaterials.advanced_thaumic_superconductor = new Material.Builder(getMaterialsId(), gregtechId("advanced_thaumic_superconductor"))
				.color(0xDDFF6E)
				.ingot().fluid()
				.iconSet(BRIGHT)
				.flags(GENERATE_DENSE,GENERATE_FRAME,GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
				.cableProperties(GTValues.V[8], 8, 0, true)
				.build();
		//电池相关
		PollutionMaterials.basic_battery_hull_alloy = new Material.Builder(getMaterialsId(), gregtechId("basic_battery_hull_alloy"))
				.color(0x877886)
				.ingot().fluid()
				.iconSet(METALLIC)
				.flags(DECOMPOSITION_BY_ELECTROLYZING)
				.components(mansussteel, 4, ordolead, 1)
				.flags(GENERATE_DENSE,GENERATE_FRAME,GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
				.blast(2700, LOW)
				.build();

		PollutionMaterials.advanced_battery_hull_alloy = new Material.Builder(getMaterialsId(), gregtechId("advanced_battery_hull_alloy"))
				.color(0xA4D4CD)
				.ingot().fluid()
				.iconSet(METALLIC)
				.flags(DECOMPOSITION_BY_ELECTROLYZING)
				.components(hyperdimensional_silver, 4, valonite, 1)
				.flags(GENERATE_DENSE,GENERATE_FRAME,GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND)
				.blast(5400, MID)
				.build();

		PollutionMaterials.basic_battery_content = new Material.Builder(getMaterialsId(), gregtechId("basic_battery_content"))
				.color(0x687D9F)
				.dust()
				.iconSet(DULL)
				.flags(DECOMPOSITION_BY_ELECTROLYZING)
				.components(Lithium, 6, thaumium, 2, infused_energy, 1, infused_motion, 1)
				.build();

		PollutionMaterials.advanced_battery_content = new Material.Builder(getMaterialsId(), gregtechId("advanced_battery_content"))
				.color(0xFFFFE2)
				.dust()
				.iconSet(BRIGHT)
				.flags(DECOMPOSITION_BY_ELECTROLYZING)
				.components(Caesium, 6, keqinggold, 2, infused_energy, 1, infused_motion, 1)
				.build();

		//高级合金
		//太虚玄钢、阿弗纳斯之血、光风霁月琥珀金
		PollutionMaterials.aetheric_dark_steel = new Material.Builder(getMaterialsId(), gregtechId("aetheric_dark_steel"))
	            .color(0x041B4E)
				.ingot().fluid()
				.iconSet(SHINY)
				.flags(GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(7200, HIGH)
				.build()
				.setFormula("䷜", true);
		PollutionMaterials.blood_of_avernus = new Material.Builder(getMaterialsId(), gregtechId("blood_of_avernus"))
				.color(0x5E0000)
				.ingot().fluid()
				.iconSet(BRIGHT)
				.flags(GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(7200, HIGH)
				.build()
				.setFormula("♆", true);
		PollutionMaterials.iizunamaru_electrum = new Material.Builder(getMaterialsId(), gregtechId("iizunamaru_electrum"))
				.color(0xF0FFB2)
				.ingot().fluid()
				.iconSet(SHINY)
				.flags(GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
				.blast(7200, HIGH)
				.build()
				.setFormula("✦✧", true);

		//交错基础材料：
		//淤泥 Mud
		Mud = new Material.Builder(getMaterialsId(), gregtechId("mud"))
				.liquid()
				.color(0x211b14)
				.build();

		BetweenStone= new Material.Builder(getMaterialsId(), gregtechId("betweenstone"))
				.dust()
				.color(0x336600)
				.components(SiliconDioxide, 13, Calcite, 7, AluminiumSulfite, 4, Water, 3, Pyrite, 2, infused_earth, 1)
				.flags(DECOMPOSITION_BY_CENTRIFUGING)
				.build();

		PitStone= new Material.Builder(getMaterialsId(), gregtechId("pitstone"))
				.dust()
				.color(0x4F540A)
				.components(Alunite, 10, Quartzite, 8, Biotite, 6, Water, 3, infused_earth, 2, infused_water, 1)
				.flags(DECOMPOSITION_BY_CENTRIFUGING)
				.build();

		CragRock= new Material.Builder(getMaterialsId(), gregtechId("cragrock"))
				.dust()
				.color(0x4F540A)
				.components(SiliconDioxide, 10, Quartzite, 8, Biotite, 6, Water, 3, infused_earth, 2, infused_water, 1)
				.flags(DECOMPOSITION_BY_CENTRIFUGING)
				.build();

		//污秽之物化工线
		PollutionMaterials.filth = new Material.Builder(getMaterialsId(), gregtechId("filth"))
				.color(0x5C0101)
				.dust()
				.iconSet(DULL)
				.components(Netherrack, 6, Endstone, 1, BetweenStone, 1, infused_taint, 1)
				.build();
		PollutionMaterials.filth_water = new Material.Builder(getMaterialsId(), gregtechId("filth_water"))
				.color(0x392323)
				.fluid()
				.components(filth, 9, infused_death, 1, infused_dark, 1)
				.build();
		PollutionMaterials.void_water = new Material.Builder(getMaterialsId(), gregtechId("void_water"))
				.color(0x837D7D)
				.fluid()
				.build();
		PollutionMaterials.void_material = new Material.Builder(getMaterialsId(), gregtechId("void_material"))
				.color(0xC3C3C3)
				.dust()
				.iconSet(SHINY)
				.build();
	}
}
