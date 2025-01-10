package keqing.pollution.api.utils;

import gregtech.api.unification.material.Material;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

import static keqing.pollution.api.unification.PollutionMaterials.*;
import static keqing.pollution.api.unification.PollutionMaterials.infused_fly;

public class infusedFluidStack {
    public static Map<Material, FluidStack> STACK_MAP = new HashMap<>();

    //在此处添加你需要的要素 格式FluidStack xxx_STACK = infused_axx.getFluid(1);
    static final FluidStack AIR_STACK = infused_air.getFluid(1);
    static final FluidStack FIRE_STACK = infused_fire.getFluid(1);
    static final FluidStack WATER_STACK = infused_water.getFluid(1);
    static final FluidStack EARTH_STACK = infused_earth.getFluid(1);
    static final FluidStack ORDER_STACK = infused_order.getFluid(1);
    static final FluidStack ENTROPY_STACK = infused_entropy.getFluid(1);
    //复合的
    static final FluidStack CRYSTAL_STACK = infused_crystal.getFluid(1);
    static final FluidStack WEAPON_STACK = infused_weapon.getFluid(1);
    static final FluidStack INSTRUMENT_STACK = infused_instrument.getFluid(1);
    static final FluidStack EXCHANGE_STACK = infused_exchange.getFluid(1);
    static final FluidStack METAL_STACK = infused_metal.getFluid(1);
    static final FluidStack ALCHEMY_STACK = infused_alchemy.getFluid(1);
    static final FluidStack LIFE_STACK = infused_life.getFluid(1);
    static final FluidStack DEATH_STACK = infused_death.getFluid(1);
    static final FluidStack SOUL_STACK = infused_soul.getFluid(1);
    static final FluidStack ENERGY_STACK = infused_energy.getFluid(1);
    static final FluidStack MAGIC_STACK = infused_magic.getFluid(1);
    static final FluidStack CRAFT_STACK = infused_craft.getFluid(1);
    static final FluidStack FLY_STACK = infused_fly.getFluid(1);

    static {
        STACK_MAP.put(infused_air, AIR_STACK);
        STACK_MAP.put(infused_fire, FIRE_STACK);
        STACK_MAP.put(infused_water, WATER_STACK);
        STACK_MAP.put(infused_earth, EARTH_STACK);
        STACK_MAP.put(infused_order, ORDER_STACK);
        STACK_MAP.put(infused_entropy, ENTROPY_STACK);
        STACK_MAP.put(infused_crystal, CRYSTAL_STACK);
        STACK_MAP.put(infused_weapon, WEAPON_STACK);
        STACK_MAP.put(infused_instrument, INSTRUMENT_STACK);
        STACK_MAP.put(infused_exchange, EXCHANGE_STACK);
        STACK_MAP.put(infused_life, LIFE_STACK);
        STACK_MAP.put(infused_death, DEATH_STACK);
        STACK_MAP.put(infused_soul, SOUL_STACK);
        STACK_MAP.put(infused_energy, ENERGY_STACK);
        STACK_MAP.put(infused_magic, MAGIC_STACK);
        STACK_MAP.put(infused_alchemy, ALCHEMY_STACK);
        STACK_MAP.put(infused_metal, METAL_STACK);
        STACK_MAP.put(infused_craft, CRAFT_STACK);
        STACK_MAP.put(infused_fly, FLY_STACK);
    }
}
