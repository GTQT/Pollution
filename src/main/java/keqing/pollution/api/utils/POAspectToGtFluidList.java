package keqing.pollution.api.utils;

import gregtech.api.unification.material.Material;
import thaumcraft.api.aspects.Aspect;
import java.util.HashMap;
import static keqing.pollution.api.unification.PollutionMaterials.*;

public class POAspectToGtFluidList {
	public static final HashMap<Aspect, Material> aspectToGtFluidList = new HashMap<>(){
		{
			put(Aspect.AIR, infused_air);
			put(Aspect.AURA, infused_aura);
			put(Aspect.ALCHEMY, infused_alchemy);
			put(Aspect.BEAST, infused_animal);
			put(Aspect.COLD, infused_cold);
			put(Aspect.CRAFT, infused_craft);
			put(Aspect.CRYSTAL, infused_crystal);
			put(Aspect.DARKNESS, infused_dark);
			put(Aspect.DEATH, infused_death);
			put(Aspect.DESIRE, infused_greed);
			put(Aspect.EARTH, infused_earth);
			put(Aspect.ENERGY, infused_energy);
			put(Aspect.ENTROPY, infused_entropy);
			put(Aspect.EXCHANGE, infused_exchange);
			put(Aspect.FIRE, infused_fire);
			put(Aspect.FLIGHT, infused_fly);
			put(Aspect.FLUX, infused_taint);
			put(Aspect.LIFE, infused_life);
			put(Aspect.LIGHT, infused_light);
			put(Aspect.MAGIC, infused_magic);
			put(Aspect.MAN, infused_human);
			put(Aspect.MECHANISM, infused_mechanics);
			put(Aspect.METAL, infused_metal);
			put(Aspect.MIND, infused_thought);
			put(Aspect.MOTION, infused_motion);
			put(Aspect.ORDER, infused_order);
			put(Aspect.PLANT, infused_plant);
			put(Aspect.PROTECT, infused_armor);
			put(Aspect.SENSES, infused_sense);
			put(Aspect.SOUL, infused_soul);
			put(Aspect.TOOL, infused_instrument);
			put(Aspect.TRAP, infused_trap);
			put(Aspect.UNDEAD, infused_undead);
			put(Aspect.VOID, infused_void);
			put(Aspect.WATER, infused_water);
		}
	};
	public static Aspect getKeyByValue(Material value) {
		for (HashMap.Entry<Aspect, Material> entry : aspectToGtFluidList.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null; // 如果没有找到，返回 null
	}
}
