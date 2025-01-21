package keqing.pollution.client.textures;

import codechicken.lib.texture.TextureUtils;
import gregtech.api.GTValues;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import net.minecraft.client.renderer.texture.TextureMap;

public class POTextures {
	public static OrientedOverlayRenderer SOLAR_PLATE_I = new OrientedOverlayRenderer("machines/solar_i");
	public static OrientedOverlayRenderer SOLAR_PLATE_II = new OrientedOverlayRenderer("machines/solar_ii");
	public static OrientedOverlayRenderer SOLAR_PLATE_III = new OrientedOverlayRenderer("machines/solar_iii");
	public static SimpleSidedCubeRenderer[] MAGIC_VOLTAGE_CASINGS;
	public static SimpleOverlayRenderer AIR;
	public static SimpleOverlayRenderer DARK;
	public static SimpleOverlayRenderer EARTH;
	public static SimpleOverlayRenderer FIRE;
	public static SimpleOverlayRenderer ORDER;
	public static SimpleOverlayRenderer WATER;

	public static SimpleOverlayRenderer FRAME_I;
	public static SimpleOverlayRenderer FRAME_II;
	public static SimpleOverlayRenderer FRAME_III;
	public static SimpleOverlayRenderer FRAME_IV;

	public static SimpleOverlayRenderer SPELL_PRISM;
	public static SimpleOverlayRenderer SPELL_PRISM_COLD;
	public static SimpleOverlayRenderer SPELL_PRISM_HOT;
	public static SimpleOverlayRenderer SPELL_PRISM_WATER;
	public static SimpleOverlayRenderer SPELL_PRISM_AIR;
	public static SimpleOverlayRenderer SPELL_PRISM_VOID;
	public static SimpleOverlayRenderer SPELL_PRISM_ORDER;
	public static SimpleOverlayRenderer SPELL_PRISM_EARTH;
	public static SimpleOverlayRenderer VOID_PRISM;
	public static SimpleOverlayRenderer MAGIC_BATTERY;
	public static SimpleOverlayRenderer TERRA_WATERTIGHT_CASING;
	public static SimpleOverlayRenderer TERRA_1_CASING;
	public static SimpleOverlayRenderer TERRA_2_CASING;
	public static SimpleOverlayRenderer TERRA_3_CASING;
	public static SimpleOverlayRenderer TERRA_4_CASING;
	public static SimpleOverlayRenderer TERRA_5_CASING;
	public static SimpleOverlayRenderer TERRA_6_CASING;

	public static SimpleOverlayRenderer MANA_BASIC;
	public static SimpleOverlayRenderer MANA_1;
	public static SimpleOverlayRenderer MANA_2;
	public static SimpleOverlayRenderer MANA_3;
	public static SimpleOverlayRenderer MANA_4;
	public static SimpleOverlayRenderer MANA_5;

	public static SimpleOverlayRenderer HYPER_1;
	public static SimpleOverlayRenderer HYPER_2;
	public static SimpleOverlayRenderer HYPER_3;
	public static SimpleOverlayRenderer HYPER_4;
	public static SimpleOverlayRenderer HYPER_5;

	public static void init() {
		MAGIC_VOLTAGE_CASINGS = new SimpleSidedCubeRenderer[GTValues.V.length];

		for(int i = 0; i < MAGIC_VOLTAGE_CASINGS.length; ++i) {
			String voltageName = GTValues.VN[i].toLowerCase();
			MAGIC_VOLTAGE_CASINGS[i] = new SimpleSidedCubeRenderer("casings/magic_voltage/" + voltageName);
		}

		AIR = new SimpleOverlayRenderer("machines/solars/airside");
		DARK = new SimpleOverlayRenderer("machines/solars/darkside");
		EARTH = new SimpleOverlayRenderer("machines/solars/earthside");
		FIRE = new SimpleOverlayRenderer("machines/solars/fireside");
		ORDER = new SimpleOverlayRenderer("machines/solars/orderside");
		WATER = new SimpleOverlayRenderer("machines/solars/waterside");

		FRAME_I = new SimpleOverlayRenderer("fusion_reactor/frame_ii");
		FRAME_II = new SimpleOverlayRenderer("fusion_reactor/frame_iii");
		FRAME_III = new SimpleOverlayRenderer("fusion_reactor/frame_iv");
		FRAME_IV = new SimpleOverlayRenderer("fusion_reactor/frame_v");

		SPELL_PRISM = new SimpleOverlayRenderer("magicblock/spell_prism");
		SPELL_PRISM_COLD = new SimpleOverlayRenderer("magicblock/spell_prism_cold");
		SPELL_PRISM_HOT = new SimpleOverlayRenderer("magicblock/spell_prism_hot");
		SPELL_PRISM_AIR = new SimpleOverlayRenderer("magicblock/spell_prism_air");
		SPELL_PRISM_VOID = new SimpleOverlayRenderer("magicblock/spell_prism_void");
		SPELL_PRISM_WATER = new SimpleOverlayRenderer("magicblock/spell_prism_water");
		SPELL_PRISM_ORDER = new SimpleOverlayRenderer("magicblock/spell_prism_order");
		SPELL_PRISM_EARTH = new SimpleOverlayRenderer("magicblock/spell_prism_earth");
		VOID_PRISM = new SimpleOverlayRenderer("magicblock/void_prism");
		MAGIC_BATTERY = new SimpleOverlayRenderer("magicblock/magic_battery");
		MANA_BASIC = new SimpleOverlayRenderer("magicblock/mana_basic");
		MANA_1 = new SimpleOverlayRenderer("magicblock/mana_1");
		MANA_2 = new SimpleOverlayRenderer("magicblock/mana_2");
		MANA_3 = new SimpleOverlayRenderer("magicblock/mana_3");
		MANA_4 = new SimpleOverlayRenderer("magicblock/mana_4");
		MANA_5 = new SimpleOverlayRenderer("magicblock/mana_5");
		TERRA_WATERTIGHT_CASING = new SimpleOverlayRenderer("botblock/terra_watertight_casing");
		TERRA_1_CASING = new SimpleOverlayRenderer("botblock/terra_1_casing");
		TERRA_2_CASING = new SimpleOverlayRenderer("botblock/terra_2_casing");
		TERRA_3_CASING = new SimpleOverlayRenderer("botblock/terra_3_casing");
		TERRA_4_CASING = new SimpleOverlayRenderer("botblock/terra_4_casing");
		TERRA_5_CASING = new SimpleOverlayRenderer("botblock/terra_5_casing");
		TERRA_6_CASING = new SimpleOverlayRenderer("botblock/terra_6_casing");
		HYPER_1 = new SimpleOverlayRenderer("hyper/hyper_1");
		HYPER_2 = new SimpleOverlayRenderer("hyper/hyper_2");
		HYPER_3 = new SimpleOverlayRenderer("hyper/hyper_3");
		HYPER_4 = new SimpleOverlayRenderer("hyper/hyper_4");
		HYPER_5 = new SimpleOverlayRenderer("hyper/hyper_5");
	}

	public static void register(TextureMap textureMap) {

	}

	public static void preInit() {
		TextureUtils.addIconRegister(POTextures::register);
	}
}