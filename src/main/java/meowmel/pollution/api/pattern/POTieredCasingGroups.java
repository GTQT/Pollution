package meowmel.pollution.api.pattern;

import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.CasingRegistration;
import meowmel.gtqtcore.api.blocks.impl.IBlockTier;

import java.util.Map;

import static meowmel.pollution.api.POAPI.MAP_COIL_CASING;
import static meowmel.pollution.api.POAPI.MAP_CP_BEAM;
import static meowmel.pollution.api.POAPI.MAP_CP_COMPOSE;
import static meowmel.pollution.api.POAPI.MAP_CP_FRAME;
import static meowmel.pollution.api.POAPI.MAP_CP_GLASS;

/**
 * V3 structure-system registrations for Pollution's tiered multiblock casings.
 *
 * <p>The source maps are populated by {@code POAPI.init()} before metatile entity
 * registration. Each V3 casing retains its original {@link IBlockTier} payload so
 * controllers can read the same tier value from a {@code FormedStructureView}.</p>
 */
public final class POTieredCasingGroups {

    private static CasingRegistration coilCasings;
    private static CasingRegistration beamCores;
    private static CasingRegistration glasses;
    private static CasingRegistration compositionCasings;
    private static CasingRegistration frames;

    private POTieredCasingGroups() {}

    public static CasingRegistration coilCasings() {
        if (coilCasings == null) {
            coilCasings = register("pollution_coil_casing", MAP_COIL_CASING);
        }
        return coilCasings;
    }

    public static CasingRegistration beamCores() {
        if (beamCores == null) {
            beamCores = register("pollution_beam_core", MAP_CP_BEAM);
        }
        return beamCores;
    }

    public static CasingRegistration glasses() {
        if (glasses == null) {
            glasses = register("pollution_glass", MAP_CP_GLASS);
        }
        return glasses;
    }

    public static CasingRegistration compositionCasings() {
        if (compositionCasings == null) {
            compositionCasings = register("pollution_composition_casing", MAP_CP_COMPOSE);
        }
        return compositionCasings;
    }

    public static CasingRegistration frames() {
        if (frames == null) {
            frames = register("pollution_frame", MAP_CP_FRAME);
        }
        return frames;
    }

    private static CasingRegistration register(String id, Map<net.minecraft.block.state.IBlockState, IBlockTier> casings) {
        return CasingDefinition.fromMap(id, true, casings,
                tier -> ((Number) tier.getTier()).intValue(),
                tier -> tier.getName());
    }
}
