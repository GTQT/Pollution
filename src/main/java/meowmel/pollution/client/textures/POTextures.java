package meowmel.pollution.client.textures;

import codechicken.lib.texture.TextureUtils;
import gregtech.api.GTValues;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SidedCubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import meowmel.pollution.client.gui.QuantumAspectTank.AspectImage;
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

	public static SimpleOverlayRenderer Livingrock_0;
	public static SimpleOverlayRenderer ASTRAL_MARBLE;
	public static SimpleOverlayRenderer STARSTREAM_CASING;

	public static final SimpleOverlayRenderer QUANTUM_ASPECT_TANK_OVERLAY = new SimpleOverlayRenderer("overlay/machine/overlay_q_aspect_tank");
	public static final SimpleOverlayRenderer PIPE_ASPECT_OUT_OVERLAY  = new SimpleOverlayRenderer("overlay/machine/overlay_pipe_aspect_out");
	public static final SimpleOverlayRenderer ASPECT_OUTPUT_OVERLAY = new SimpleOverlayRenderer("overlay/machine/overlay_aspect_out");

	public static SimpleOverlayRenderer MANA_HATCH_INPUT_1A;
	public static SimpleOverlayRenderer MANA_HATCH_INPUT_4A;
	public static SimpleOverlayRenderer MANA_HATCH_INPUT_16A;
	public static SimpleOverlayRenderer MANA_HATCH_INPUT_64A;
	public static SimpleOverlayRenderer MANA_HATCH_OUTPUT_1A;
	public static SimpleOverlayRenderer MANA_HATCH_OUTPUT_4A;
	public static SimpleOverlayRenderer MANA_HATCH_OUTPUT_16A;
	public static SimpleOverlayRenderer MANA_HATCH_OUTPUT_64A;
	public static SimpleOverlayRenderer WIRELESS_MANA_HATCH_INPUT;
	public static SimpleOverlayRenderer WIRELESS_MANA_HATCH_OUTPUT;
	public static SimpleOverlayRenderer MANA_POOL_HATCH_INPUT;
	public static SimpleOverlayRenderer MANA_POOL_HATCH_OUTPUT;
	public static SimpleOverlayRenderer WIRELESS_MANA_POOL_HATCH_INPUT;
	public static SimpleOverlayRenderer WIRELESS_MANA_POOL_HATCH_OUTPUT;
	public static SimpleOverlayRenderer VIS_HATCH;
	public static SimpleOverlayRenderer INFUSED_FLUID_HATCH;
	public static SimpleOverlayRenderer BLOOD_MAGIC_HATCH;
	public static SimpleOverlayRenderer ASTRAL_LENS_HATCH;
	public static SimpleOverlayRenderer TAROT_HATCH;


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

		Livingrock_0 = new SimpleOverlayRenderer("botblock/livingrock0");
		ASTRAL_MARBLE = new SimpleOverlayRenderer("astralsorcery:marble_bricks");
		STARSTREAM_CASING = new SimpleOverlayRenderer("pollution:starstream/starstream_casing");

		MANA_HATCH_INPUT_1A = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_input_1a");
		MANA_HATCH_INPUT_4A = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_input_4a");
		MANA_HATCH_INPUT_16A = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_input_16a");
		MANA_HATCH_INPUT_64A = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_input_64a");
		MANA_HATCH_OUTPUT_1A = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_output_1a");
		MANA_HATCH_OUTPUT_4A = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_output_4a");
		MANA_HATCH_OUTPUT_16A = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_output_16a");
		MANA_HATCH_OUTPUT_64A = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_output_64a");
		WIRELESS_MANA_HATCH_INPUT = new SimpleOverlayRenderer("overlay/machine/magic_hatch/wireless_mana_input");
		WIRELESS_MANA_HATCH_OUTPUT = new SimpleOverlayRenderer("overlay/machine/magic_hatch/wireless_mana_output");
		MANA_POOL_HATCH_INPUT = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_pool_input");
		MANA_POOL_HATCH_OUTPUT = new SimpleOverlayRenderer("overlay/machine/magic_hatch/mana_pool_output");
		WIRELESS_MANA_POOL_HATCH_INPUT = new SimpleOverlayRenderer("overlay/machine/magic_hatch/wireless_mana_pool_input");
		WIRELESS_MANA_POOL_HATCH_OUTPUT = new SimpleOverlayRenderer("overlay/machine/magic_hatch/wireless_mana_pool_output");
		VIS_HATCH = new SimpleOverlayRenderer("overlay/machine/magic_hatch/vis_hatch");
		INFUSED_FLUID_HATCH = new SimpleOverlayRenderer("overlay/machine/magic_hatch/infused_fluid_hatch");
		BLOOD_MAGIC_HATCH = new SimpleOverlayRenderer("overlay/machine/magic_hatch/blood_magic_hatch");
		ASTRAL_LENS_HATCH = new SimpleOverlayRenderer("overlay/machine/magic_hatch/astral_lens_hatch");
		TAROT_HATCH = new SimpleOverlayRenderer("overlay/machine/magic_hatch/tarot_hatch");
	}

	public static void register(TextureMap textureMap) {

	}

	public static void preInit() {
		TextureUtils.addIconRegister(POTextures::register);
		AspectImage.create();
	}
}
