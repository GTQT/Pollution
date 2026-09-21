package meowmel.pollution.integration;

import meowmel.pollution.integration.theoneprobe.FluxClearProvider;
import meowmel.pollution.integration.theoneprobe.MultiblockManaProvider;
import meowmel.pollution.integration.theoneprobe.QuantumTankProvider;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public class POIntegration {

    public static void init() {

        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new MultiblockManaProvider());
        oneProbe.registerProvider(new FluxClearProvider());
        oneProbe.registerProvider(new QuantumTankProvider());
    }


    public POIntegration() {}
}

