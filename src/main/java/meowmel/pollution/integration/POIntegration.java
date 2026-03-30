package meowmel.pollution.integration;

import meowmel.pollution.integration.theoneprobe.MultiblockManaProvider;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public class POIntegration {

    public static void init() {

        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new MultiblockManaProvider());
    }


    public POIntegration() {}
}

