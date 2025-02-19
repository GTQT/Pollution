package keqing.pollution.client.textures;

import codechicken.lib.texture.TextureUtils;
import gregtech.api.GTValues;
import gregtech.api.gui.resources.TextureArea;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SidedCubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import net.minecraft.client.renderer.texture.TextureMap;

public class POTextures {
	public static final ICubeRenderer BMCOMPUTER_CASING;
	public static final ICubeRenderer BMADVANCED_COMPUTER_CASING;
	public static final SimpleOverlayRenderer BMHPCA_ACTIVE_COOLER_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_ACTIVE_COOLER_ACTIVE_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_BRIDGE_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_BRIDGE_ACTIVE_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_COMPUTATION_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_COMPUTATION_ACTIVE_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_ADVANCED_COMPUTATION_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_ADVANCED_COMPUTATION_ACTIVE_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_DAMAGED_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_DAMAGED_ACTIVE_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_ADVANCED_DAMAGED_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_ADVANCED_DAMAGED_ACTIVE_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_EMPTY_OVERLAY;
	public static final SimpleOverlayRenderer BMHPCA_HEAT_SINK_OVERLAY;
	public static final OrientedOverlayRenderer BMHPCA_OVERLAY;

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

	public static final TextureArea BMHPCA_COMPONENT_OUTLINE = TextureArea.fullImage("textures/gui/widget/bm_hpca/component_outline.png");
	public static final TextureArea BMHPCA_ICON_EMPTY_COMPONENT = TextureArea.fullImage("textures/gui/widget/bm_hpca/empty_component.png");
	public static final TextureArea BMHPCA_ICON_ADVANCED_COMPUTATION_COMPONENT = TextureArea.fullImage("textures/gui/widget/bm_hpca/advanced_computation_component.png");
	public static final TextureArea BMHPCA_ICON_BRIDGE_COMPONENT = TextureArea.fullImage("textures/gui/widget/bm_hpca/bridge_component.png");
	public static final TextureArea BMHPCA_ICON_COMPUTATION_COMPONENT = TextureArea.fullImage("textures/gui/widget/bm_hpca/computation_component.png");
	public static final TextureArea BMHPCA_ICON_ACTIVE_COOLER_COMPONENT = TextureArea.fullImage("textures/gui/widget/bm_hpca/active_cooler_component.png");
	public static final TextureArea BMHPCA_ICON_HEAT_SINK_COMPONENT = TextureArea.fullImage("textures/gui/widget/bm_hpca/heat_sink_component.png");
	public static final TextureArea BMHPCA_ICON_DAMAGED_ADVANCED_COMPUTATION_COMPONENT = TextureArea.fullImage("textures/gui/widget/bm_hpca/damaged_advanced_computation_component.png");
	public static final TextureArea BMHPCA_ICON_DAMAGED_COMPUTATION_COMPONENT = TextureArea.fullImage("textures/gui/widget/bm_hpca/damaged_computation_component.png");


	static {
		BMCOMPUTER_CASING = new SidedCubeRenderer("casings/bm_computer/computer_casing");
		BMADVANCED_COMPUTER_CASING = new SidedCubeRenderer("casings/bm_computer/advanced_computer_casing");
		BMHPCA_ACTIVE_COOLER_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/active_cooler");
		BMHPCA_ACTIVE_COOLER_ACTIVE_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/active_cooler_active");
		BMHPCA_BRIDGE_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/bridge");
		BMHPCA_BRIDGE_ACTIVE_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/bridge_active");
		BMHPCA_COMPUTATION_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/computation");
		BMHPCA_COMPUTATION_ACTIVE_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/computation_active");
		BMHPCA_ADVANCED_COMPUTATION_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/computation_advanced");
		BMHPCA_ADVANCED_COMPUTATION_ACTIVE_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/computation_advanced_active");
		BMHPCA_DAMAGED_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/damaged");
		BMHPCA_DAMAGED_ACTIVE_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/damaged_active");
		BMHPCA_ADVANCED_DAMAGED_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/damaged_advanced");
		BMHPCA_ADVANCED_DAMAGED_ACTIVE_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/damaged_advanced_active");
		BMHPCA_EMPTY_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/empty");
		BMHPCA_HEAT_SINK_OVERLAY = new SimpleOverlayRenderer("overlay/machine/bm_hpca/heat_sink");
		BMHPCA_OVERLAY = new OrientedOverlayRenderer("multiblock/bm_hpca");
	}
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