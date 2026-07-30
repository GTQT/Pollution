package meowmel.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import meowmel.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_BOLT_SCREW;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_FINE_WIRE;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_FOIL;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_FRAME;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_GEAR;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_LONG_ROD;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_PLATE;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_RING;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_ROD;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_ROUND;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_SMALL_GEAR;
import static gregtech.api.unification.material.info.MaterialIconSet.DULL;
import static gregtech.api.unification.material.info.MaterialIconSet.GEM_HORIZONTAL;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.HIGH;
import static meowmel.pollution.api.utils.POUtils.pollutionId;

/**
 * Materials shared by the Astral Sorcery, Blood Magic, Botania, Thaumcraft and
 * GregTech integration layer. This class only declares material forms and does
 * not add processing recipes.
 */
public final class MagicIntegrationMaterials {

    private static int startId = 3000;
    private static final int END_ID = 3100;

    private MagicIntegrationMaterials() {}

    private static int getMaterialId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException("Magic integration material ID range exhausted");
    }

    public static void register() {
        PollutionMaterials.OpticalGradeAquamarine =
                new Material.Builder(getMaterialId(), pollutionId("optical_grade_aquamarine"))
                        .color(0x6DE8F2)
                        .gem()
                        .iconSet(GEM_HORIZONTAL)
                        .build();

        PollutionMaterials.StarlightPollen =
                new Material.Builder(getMaterialId(), pollutionId("starlight_pollen"))
                        .color(0xBCEBFF)
                        .dust()
                        .fluid()
                        .iconSet(SHINY)
                        .build();

        PollutionMaterials.MoonlightResin =
                new Material.Builder(getMaterialId(), pollutionId("moonlight_resin"))
                        .color(0xA9B5F7)
                        .fluid()
                        .iconSet(DULL)
                        .build();

        PollutionMaterials.AstralBloodPlasma =
                new Material.Builder(getMaterialId(), pollutionId("astral_blood_plasma"))
                        .color(0x6D123D)
                        .fluid()
                        .iconSet(SHINY)
                        .build();

        PollutionMaterials.CelestialBiologicalMedium =
                new Material.Builder(getMaterialId(), pollutionId("celestial_biological_medium"))
                        .color(0x694C9E)
                        .fluid()
                        .iconSet(DULL)
                        .build();

        PollutionMaterials.ArcaneInk =
                new Material.Builder(getMaterialId(), pollutionId("arcane_ink"))
                        .color(0x241035)
                        .fluid()
                        .iconSet(DULL)
                        .build();

        PollutionMaterials.StarryArcaneAlloy =
                new Material.Builder(getMaterialId(), pollutionId("starry_arcane_alloy"))
                        .color(0x8E76D8)
                        .ingot()
                        .fluid()
                        .iconSet(SHINY)
                        .flags(
                                GENERATE_PLATE,
                                GENERATE_ROD,
                                GENERATE_LONG_ROD,
                                GENERATE_FRAME,
                                GENERATE_GEAR,
                                GENERATE_SMALL_GEAR,
                                GENERATE_ROUND,
                                GENERATE_FINE_WIRE,
                                GENERATE_FOIL,
                                GENERATE_RING,
                                GENERATE_BOLT_SCREW)
                        .blast(7200, HIGH)
                        .build();
    }
}
