package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import keqing.pollution.api.unification.Elements;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static gregtech.api.util.GTUtility.gregtechId;

public class PollutionElementMaterials {
    private static int startId = 16000;
    private static final int END_ID = startId + 100;
    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    public static void register() {
        PollutionMaterials.infused_air= new Material.Builder(getMaterialsId(), gregtechId("infused_air"))
                .color(0xFEFE7D)
                .ore().gem().fluid()
                .element(Elements.Ae)
                .build();
        PollutionMaterials.infused_fire= new Material.Builder(getMaterialsId(), gregtechId("infused_fire"))
                .color(0xFE3C01)
                .ore().gem().fluid()
                .element(Elements.Ig)
                .build();
        PollutionMaterials.infused_water= new Material.Builder(getMaterialsId(), gregtechId("infused_water"))
                .color(0x0090FF)
                .ore().gem().fluid()
                .element(Elements.Aq)
                .build();
        PollutionMaterials.infused_earth= new Material.Builder(getMaterialsId(), gregtechId("infused_earth"))
                .color(0x00A000)
                .ore().gem().fluid()
                .element(Elements.Ter)
                .build();
        PollutionMaterials.infused_entropy= new Material.Builder(getMaterialsId(), gregtechId("infused_entropy"))
                .color(0x43435E)
                .ore().gem().fluid()
                .element(Elements.Pe)
                .build();
        PollutionMaterials.infused_order= new Material.Builder(getMaterialsId(), gregtechId("infused_order"))
                .color(0xEECCFF)
                .ore().gem().fluid()
                .element(Elements.Ord)
                .build();
        PollutionMaterials.mana= new Material.Builder(getMaterialsId(), gregtechId("mana"))
                .color(0x87CEEB)
                .fluid()
                .element(Elements.Ma)
                .build();
        PollutionMaterials.Sunnarium = new Material.Builder(getMaterialsId(), gregtechId("sunnarium"))
                .color(0xFAF20E)
                .dust().fluid()
                .iconSet(SHINY)
                .element(Elements.Su)
                .build();
        PollutionMaterials.Whitemansus = new Material.Builder(getMaterialsId(), gregtechId("whitemansus"))
                .color(0xEFF0FF)
                .dust().fluid()
                .iconSet(SHINY)
                .element(Elements.Wma)
                .build();
        PollutionMaterials.Blackmansus = new Material.Builder(getMaterialsId(), gregtechId("Blackmansus"))
                .color(0xFAF20E)
                .dust().fluid()
                .iconSet(SHINY)
                .element(Elements.Bma)
                .build();
    }
}
