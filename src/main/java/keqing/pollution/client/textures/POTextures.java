package keqing.pollution.client.textures;

import codechicken.lib.texture.TextureUtils;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraft.client.renderer.texture.TextureMap;

public class POTextures {
	public static OrientedOverlayRenderer SOLAR_PLATE_I = new OrientedOverlayRenderer("machines/solar_i");
	public static OrientedOverlayRenderer SOLAR_PLATE_II = new OrientedOverlayRenderer("machines/solar_ii");
	public static OrientedOverlayRenderer SOLAR_PLATE_III = new OrientedOverlayRenderer("machines/solar_iii");

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


	public static void init() {
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
		TERRA_WATERTIGHT_CASING = new SimpleOverlayRenderer("botblock/terra_watertight_casing");
	}

	public static void register(TextureMap textureMap) {

	}

	public static void preInit() {
		TextureUtils.addIconRegister(POTextures::register);
	}
}