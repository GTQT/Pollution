package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconSet;
import keqing.gtqtcore.api.unification.material.info.GTQTMaterialFlags;
import keqing.pollution.api.unification.Elements;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.BRIGHT;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static keqing.pollution.api.utils.POUtils.pollutionId;

public class PollutionElementMaterials {
    private static int startId = 0;
    private static final int END_ID = startId + 100;

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static void register() {
        PollutionMaterials.infused_air = new Material.Builder(getMaterialsId(), pollutionId("infused_air"))
                .color(0xFEFE7D)
                .ore().dust().fluid().gem()
                .flags(CRYSTALLIZABLE,GTQTMaterialFlags.GENERATE_BOULE)
                .iconSet(MaterialIconSet.SHINY)
                .element(Elements.Ae)
                .build();
        PollutionMaterials.infused_fire = new Material.Builder(getMaterialsId(), pollutionId("infused_fire"))
                .color(0xFE3C01)
                .ore().dust().fluid().gem()
                .flags(CRYSTALLIZABLE,GTQTMaterialFlags.GENERATE_BOULE)
                .iconSet(MaterialIconSet.SHINY)
                .element(Elements.Ig)
                .build();
        PollutionMaterials.infused_water = new Material.Builder(getMaterialsId(), pollutionId("infused_water"))
                .color(0x0090FF)
                .ore().dust().fluid().gem()
                .flags(CRYSTALLIZABLE,GTQTMaterialFlags.GENERATE_BOULE)
                .iconSet(MaterialIconSet.SHINY)
                .element(Elements.Aq)
                .build();
        PollutionMaterials.infused_earth = new Material.Builder(getMaterialsId(), pollutionId("infused_earth"))
                .color(0x00A000)
                .ore().dust().fluid().gem()
                .flags(CRYSTALLIZABLE,GTQTMaterialFlags.GENERATE_BOULE)
                .iconSet(MaterialIconSet.SHINY)
                .element(Elements.Ter)
                .build();
        PollutionMaterials.infused_entropy = new Material.Builder(getMaterialsId(), pollutionId("infused_entropy"))
                .color(0x43435E)
                .ore().dust().fluid().gem()
                .flags(CRYSTALLIZABLE,GTQTMaterialFlags.GENERATE_BOULE)
                .iconSet(MaterialIconSet.SHINY)
                .element(Elements.Pe)
                .build();
        PollutionMaterials.infused_order = new Material.Builder(getMaterialsId(), pollutionId("infused_order"))
                .color(0xEECCFF)
                .ore().dust().fluid().gem()
                .flags(CRYSTALLIZABLE,GTQTMaterialFlags.GENERATE_BOULE)
                .iconSet(MaterialIconSet.SHINY)
                .element(Elements.Ord)
                .build();

        startId++;

        PollutionMaterials.sunnarium = new Material.Builder(getMaterialsId(), pollutionId("sunnarium"))
                .color(0xFAF20E)
                .dust().fluid()
                .iconSet(SHINY)
                .element(Elements.Su)
                .build();
        PollutionMaterials.whitemansus = new Material.Builder(getMaterialsId(), pollutionId("whitemansus"))
                .color(0xEFF0FF)
                .fluid()
                .iconSet(SHINY)
                .element(Elements.Wma)
                .build();
        PollutionMaterials.blackmansus = new Material.Builder(getMaterialsId(), pollutionId("Blackmansus"))
                .color(0x606060)
                .fluid()
                .iconSet(SHINY)
                .element(Elements.Bma)
                .build();
        PollutionMaterials.elven = new Material.Builder(getMaterialsId(), pollutionId("Elven"))
                .color(0xEE30A7)
                .fluid()
                .iconSet(SHINY)
                .element(Elements.El)
                .build();
        PollutionMaterials.starrymansus = new Material.Builder(getMaterialsId(), pollutionId("starrymansus"))
                .color(0xFFF6FF)
                .fluid()
                .iconSet(BRIGHT)
                .element(Elements.St)
                .build();
        PollutionMaterials.sentient_metal = new Material.Builder(getMaterialsId(), pollutionId("sentient_metal"))
                .color(0x55FFFA)
                .ingot().fluid()
                .iconSet(BRIGHT)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .element(Elements.Sen)
                .build();
        PollutionMaterials.binding_metal = new Material.Builder(getMaterialsId(), pollutionId("binding_metal"))
                .color(0xDA1D0F)
                .ingot().fluid()
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .element(Elements.Bin)
                .build();
        PollutionMaterials.existing_nexus = new Material.Builder(getMaterialsId(), pollutionId("existing_nexus"))
                .color(0xC0C0C0)
                .ingot().fluid()
                .iconSet(BRIGHT)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .element(Elements.Exn)
                .build();
        PollutionMaterials.fading_nexus = new Material.Builder(getMaterialsId(), pollutionId("fading_nexus"))
                .color(0x404040)
                .ingot().fluid()
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .element(Elements.Fan)
                .build();

    }
}
