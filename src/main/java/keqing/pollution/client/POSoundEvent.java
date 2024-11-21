package keqing.pollution.client;

import gregtech.api.GregTechAPI;
import gregtech.core.sound.GTSoundEvents;
import net.minecraft.util.SoundEvent;

public class POSoundEvent extends GTSoundEvents {
    public static SoundEvent MANA_PLUSE;

    public static void register() {
        MANA_PLUSE = GregTechAPI.soundManager.registerSound("botania.subtitle.dash");
    }
}
