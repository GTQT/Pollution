package meowmel.pollution.api.amplification;

import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Conservative fallback tags for existing magic machines.
 * Explicit recipe tags always take priority. This lets legacy recipes receive
 * only safe first-batch speed/EU/parallel effects until they are annotated.
 */
public final class MagicMachineProfileRegistry {

    private static final Map<String, Long> PROFILES = new HashMap<>();

    static {
        register("magic_macerator", MagicProcessTag.CRUSHING, MagicProcessTag.DECOMPOSITION, MagicProcessTag.ORE,
                MagicProcessTag.EXTREME_PROCESSING);
        register("magic_bender", MagicProcessTag.FORMING, MagicProcessTag.MECHANICAL);
        register("magic_cutter", MagicProcessTag.MECHANICAL, MagicProcessTag.DECOMPOSITION);
        register("magic_extruder", MagicProcessTag.FORMING, MagicProcessTag.HIGH_POWER);
        register("magic_wiremill", MagicProcessTag.FORMING, MagicProcessTag.MECHANICAL);
        register("magic_electrolyzer", MagicProcessTag.DECOMPOSITION, MagicProcessTag.FLUID,
                MagicProcessTag.HIGH_POWER);
        register("magic_mixer", MagicProcessTag.FLUID, MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC);
        register("magic_assembler", MagicProcessTag.PRECISION, MagicProcessTag.MECHANICAL);
        register("magic_centrifuge", MagicProcessTag.SEPARATION, MagicProcessTag.ORE, MagicProcessTag.FLUID,
                MagicProcessTag.CHANCE_OUTPUT);
        register("magic_chemical_bath", MagicProcessTag.DECOMPOSITION, MagicProcessTag.ORE,
                MagicProcessTag.PURIFICATION, MagicProcessTag.DANGEROUS);
        register("magic_sifter", MagicProcessTag.SEPARATION, MagicProcessTag.ORE, MagicProcessTag.CHANCE_OUTPUT);
        register("magic_solidifier", MagicProcessTag.FORMING, MagicProcessTag.FLUID, MagicProcessTag.COOLING);
        register("magic_brewery", MagicProcessTag.BIOLOGICAL, MagicProcessTag.FLUID, MagicProcessTag.TIMED,
                MagicProcessTag.CHANCE_OUTPUT, MagicProcessTag.NIGHT_ALCHEMY);
        register("magic_chemical_reactor", MagicProcessTag.FLUID, MagicProcessTag.PRECISION,
                MagicProcessTag.MULTI_MAGIC, MagicProcessTag.CATALYTIC);
        register("magic_autoclave", MagicProcessTag.BIOLOGICAL, MagicProcessTag.FLUID,
                MagicProcessTag.PRECISION, MagicProcessTag.STERILE, MagicProcessTag.CATALYTIC);
        register("magic_green_house", MagicProcessTag.BIOLOGICAL, MagicProcessTag.PLANT,
                MagicProcessTag.CELL_CULTURE, MagicProcessTag.STERILE, MagicProcessTag.CHANCE_OUTPUT,
                MagicProcessTag.DAYLIGHT);
        register("magic_distillery", MagicProcessTag.FLUID, MagicProcessTag.DISTILLATION,
                MagicProcessTag.TIMED);
        register("industrial_infusion", MagicProcessTag.PRECISION, MagicProcessTag.MULTI_MAGIC,
                MagicProcessTag.CATALYTIC, MagicProcessTag.INFUSION, MagicProcessTag.STABILITY,
                MagicProcessTag.DUAL_MATERIAL);
        register("industrial_starlight_infuser", MagicProcessTag.PRECISION, MagicProcessTag.TIMED,
                MagicProcessTag.INFUSION, MagicProcessTag.PHOTOCHEMICAL);
        register("industrial_lightwell", MagicProcessTag.BIOLOGICAL, MagicProcessTag.PURIFICATION,
                MagicProcessTag.PHOTOCHEMICAL);
        register("magic_fusion_reactor", MagicProcessTag.HIGH_POWER, MagicProcessTag.THERMAL,
                MagicProcessTag.STABILITY, MagicProcessTag.CATALYTIC, MagicProcessTag.MULTI_MAGIC,
                MagicProcessTag.EXTREME_PROCESSING, MagicProcessTag.DESTRUCTIVE_RITUAL);
        register("celestial_observation_array", MagicProcessTag.PRECISION, MagicProcessTag.TIMED,
                MagicProcessTag.EXPERIMENTAL, MagicProcessTag.HIDDEN_RITUAL);
        register("celestial_calibration_matrix", MagicProcessTag.PRECISION, MagicProcessTag.TIMED,
                MagicProcessTag.MULTI_MAGIC, MagicProcessTag.CATALYTIC, MagicProcessTag.HIDDEN_RITUAL,
                MagicProcessTag.STRUCTURAL_CONTROL);
        register("magic_electric_blast_furnace", MagicProcessTag.THERMAL, MagicProcessTag.STABILITY,
                MagicProcessTag.HIGH_POWER);
        register("magic_alloy_blast", MagicProcessTag.THERMAL, MagicProcessTag.STABILITY,
                MagicProcessTag.HIGH_POWER);
    }

    private MagicMachineProfileRegistry() {
    }

    private static void register(String path, MagicProcessTag... tags) {
        PROFILES.put(path, MagicProcessTag.maskOf(tags));
    }

    public static long getFallbackTags(ResourceLocation machineId) {
        return machineId == null ? 0L : PROFILES.getOrDefault(machineId.getPath(), 0L);
    }

    public static Map<String, Long> getProfiles() {
        return Collections.unmodifiableMap(PROFILES);
    }
}
