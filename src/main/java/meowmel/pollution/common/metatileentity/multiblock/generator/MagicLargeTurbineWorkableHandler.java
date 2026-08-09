package meowmel.pollution.common.metatileentity.multiblock.generator;

import gregtech.common.metatileentities.multi.electric.generator.LargeTurbineWorkableHandler;

/** Applies the bounded astral/tarot generation bonus after GT rotor production is calculated. */
final class MagicLargeTurbineWorkableHandler extends LargeTurbineWorkableHandler {

    private final MetaTileEntityMagicLargeTurbine controller;

    MagicLargeTurbineWorkableHandler(MetaTileEntityMagicLargeTurbine controller, int tier) {
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
