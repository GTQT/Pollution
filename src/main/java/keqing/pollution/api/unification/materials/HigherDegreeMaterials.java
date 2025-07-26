package keqing.pollution.api.unification.materials;

import gregtech.api.unification.material.Material;
import keqing.pollution.api.unification.PollutionMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.BRIGHT;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static gregtech.api.unification.material.properties.BlastProperty.GasTier.HIGH;
import static keqing.pollution.api.utils.POUtils.pollutionId;

public class HigherDegreeMaterials {
    private static int startId = 2000;
    private static final int END_ID = startId + 1000;

    public HigherDegreeMaterials() {
    }
    //第三类材料
    //通常为需求前两类化合物组成的复合化合物或者混合物

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    public static void register() {
        //牢大 想你了
        PollutionMaterials.kobemetal = new Material.Builder(getMaterialsId(), pollutionId("kobemetal"))
                .color(0xFFD700)
                .ingot().fluid()
                .components(Helium, 1, Lithium, 1, Cobalt, 1, Platinum, 1, Erbium, 1)
                .iconSet(SHINY)
                .flags(GENERATE_PLATE, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROD, DECOMPOSITION_BY_CENTRIFUGING)
                .build()
                .setFormula("HeLiCoPtEr", true);

        //高级合金
        //太虚玄钢、阿弗纳斯之血、光风霁月琥珀金
        PollutionMaterials.aetheric_dark_steel = new Material.Builder(getMaterialsId(), pollutionId("aetheric_dark_steel"))
                .color(0x041B4E)
                .ingot().fluid()
                .iconSet(SHINY)
                .flags(GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(7200, HIGH)
                .build()
                .setFormula("䷜", true);

        PollutionMaterials.blood_of_avernus = new Material.Builder(getMaterialsId(), pollutionId("blood_of_avernus"))
                .color(0x5E0000)
                .ingot().fluid()
                .iconSet(BRIGHT)
                .flags(GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(7200, HIGH)
                .build()
                .setFormula("♆", true);

        PollutionMaterials.iizunamaru_electrum = new Material.Builder(getMaterialsId(), pollutionId("iizunamaru_electrum"))
                .color(0xF2FF2C)
                .ingot().fluid()
                .iconSet(SHINY)
                .flags(GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_PLATE, GENERATE_ROTOR, GENERATE_ROD, GENERATE_LONG_ROD, GENERATE_FRAME, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROUND, DECOMPOSITION_BY_CENTRIFUGING)
                .blast(7200, HIGH)
                .build()
                .setFormula("✦✧", true);
    }
}
