package meowmel.pollution.api.utils;

import gregtech.api.unification.material.Material;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

import static meowmel.pollution.api.unification.PollutionMaterials.*;
import static meowmel.pollution.api.unification.PollutionMaterials.InfusedFly;

public class infusedFluidStack {
    public static Map<Material, FluidStack> STACK_MAP = new HashMap<>();

    //在此处添加你需要的要素 格式FluidStack xxx_STACK = infused_axx.getFluid(1);
    static final FluidStack AIR_STACK = InfusedAir.getFluid(1);
    static final FluidStack FIRE_STACK = InfusedFire.getFluid(1);
    static final FluidStack WATER_STACK = InfusedWater.getFluid(1);
    static final FluidStack EARTH_STACK = InfusedEarth.getFluid(1);
    static final FluidStack ORDER_STACK = InfusedOrder.getFluid(1);
    static final FluidStack ENTROPY_STACK = InfusedEntropy.getFluid(1);
    //复合的
    static final FluidStack CRYSTAL_STACK = InfusedCrystal.getFluid(1);
    static final FluidStack WEAPON_STACK = InfusedWeapon.getFluid(1);
    static final FluidStack INSTRUMENT_STACK = InfusedInstrument.getFluid(1);
    static final FluidStack EXCHANGE_STACK = InfusedExchange.getFluid(1);
    static final FluidStack METAL_STACK = InfusedMetal.getFluid(1);
    static final FluidStack ALCHEMY_STACK = InfusedAlchemy.getFluid(1);
    static final FluidStack LIFE_STACK = InfusedLife.getFluid(1);
    static final FluidStack DEATH_STACK = InfusedDeath.getFluid(1);
    static final FluidStack SOUL_STACK = InfusedSoul.getFluid(1);
    static final FluidStack ENERGY_STACK = InfusedEnergy.getFluid(1);
    static final FluidStack MAGIC_STACK = InfusedMagic.getFluid(1);
    static final FluidStack CRAFT_STACK = InfusedCraft.getFluid(1);
    static final FluidStack FLY_STACK = InfusedFly.getFluid(1);

    static {
        STACK_MAP.put(InfusedAir, AIR_STACK);
        STACK_MAP.put(InfusedFire, FIRE_STACK);
        STACK_MAP.put(InfusedWater, WATER_STACK);
        STACK_MAP.put(InfusedEarth, EARTH_STACK);
        STACK_MAP.put(InfusedOrder, ORDER_STACK);
        STACK_MAP.put(InfusedEntropy, ENTROPY_STACK);
        STACK_MAP.put(InfusedCrystal, CRYSTAL_STACK);
        STACK_MAP.put(InfusedWeapon, WEAPON_STACK);
        STACK_MAP.put(InfusedInstrument, INSTRUMENT_STACK);
        STACK_MAP.put(InfusedExchange, EXCHANGE_STACK);
        STACK_MAP.put(InfusedLife, LIFE_STACK);
        STACK_MAP.put(InfusedDeath, DEATH_STACK);
        STACK_MAP.put(InfusedSoul, SOUL_STACK);
        STACK_MAP.put(InfusedEnergy, ENERGY_STACK);
        STACK_MAP.put(InfusedMagic, MAGIC_STACK);
        STACK_MAP.put(InfusedAlchemy, ALCHEMY_STACK);
        STACK_MAP.put(InfusedMetal, METAL_STACK);
        STACK_MAP.put(InfusedCraft, CRAFT_STACK);
        STACK_MAP.put(InfusedFly, FLY_STACK);
    }
}
