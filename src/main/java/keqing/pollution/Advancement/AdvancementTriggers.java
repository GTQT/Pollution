package keqing.pollution.Advancement;

import gregtech.api.GregTechAPI;
import gregtech.api.advancement.IAdvancementTrigger;
import gregtech.core.advancement.criterion.BasicCriterion;
import gregtech.core.advancement.criterion.DeathCriterion;

public class AdvancementTriggers {
    public static IAdvancementTrigger<?> FIRST_TO_BTN;

    public AdvancementTriggers() {
    }

    public static void register() {
        FIRST_TO_BTN = GregTechAPI.advancementManager.registerTrigger("first_to_btn", new DeathCriterion());
    }
}
