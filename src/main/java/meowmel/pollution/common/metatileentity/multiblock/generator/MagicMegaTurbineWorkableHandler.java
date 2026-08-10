package meowmel.pollution.common.metatileentity.multiblock.generator;

import meowmel.gtqtcore.common.metatileentities.multi.electric.generator.MegaTurbineWorkableHandler;

/** Applies the bounded astral/tarot generation bonus after GTQT rotor production is calculated. */
final class MagicMegaTurbineWorkableHandler extends MegaTurbineWorkableHandler {

    private final MetaTileEntityMagicMegaTurbine controller;

    MagicMegaTurbineWorkableHandler(MetaTileEntityMagicMegaTurbine controller, int tier) {
        super(controller, tier);
        this.controller = controller;
    }

    @Override
    protected long boostProduction(long maxVoltage) {
        long production = super.boostProduction(maxVoltage);
        if (production <= 0L) return production;
        return (long) Math.min(Long.MAX_VALUE, Math.floor(production
                * (1.0D + controller.getEnergyAmplification().getGenerationBonus())));
    }
}
