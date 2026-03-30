package meowmel.pollution.api.utils;

import gregtech.api.unification.material.Material;
import thaumcraft.api.aspects.Aspect;
import java.util.HashMap;
import static meowmel.pollution.api.unification.PollutionMaterials.*;

public class POAspectToGtFluidList {
	public static final HashMap<Aspect, Material> aspectToGtFluidList = new HashMap<>(){
		{
			put(Aspect.AIR, InfusedAir);
			put(Aspect.AURA, InfusedAura);
			put(Aspect.ALCHEMY, InfusedAlchemy);
			put(Aspect.BEAST, InfusedAnimal);
			put(Aspect.COLD, InfusedCold);
			put(Aspect.CRAFT, InfusedCraft);
			put(Aspect.CRYSTAL, InfusedCrystal);
			put(Aspect.DARKNESS, InfusedDark);
			put(Aspect.DEATH, InfusedDeath);
			put(Aspect.DESIRE, InfusedGreed);
			put(Aspect.EARTH, InfusedEarth);
			put(Aspect.ENERGY, InfusedEnergy);
			put(Aspect.ENTROPY, InfusedEntropy);
			put(Aspect.EXCHANGE, InfusedExchange);
			put(Aspect.FIRE, InfusedFire);
			put(Aspect.FLIGHT, InfusedFly);
			put(Aspect.FLUX, InfusedTaint);
			put(Aspect.LIFE, InfusedLife);
			put(Aspect.LIGHT, InfusedLight);
			put(Aspect.MAGIC, InfusedMagic);
			put(Aspect.MAN, InfusedHuman);
			put(Aspect.MECHANISM, InfusedMechanics);
			put(Aspect.METAL, InfusedMetal);
			put(Aspect.MIND, InfusedThought);
			put(Aspect.MOTION, InfusedMotion);
			put(Aspect.ORDER, InfusedOrder);
			put(Aspect.PLANT, InfusedPlant);
			put(Aspect.PROTECT, InfusedArmor);
			put(Aspect.SENSES, InfusedSense);
			put(Aspect.SOUL, InfusedSoul);
			put(Aspect.TOOL, InfusedInstrument);
			put(Aspect.TRAP, InfusedTrap);
			put(Aspect.UNDEAD, InfusedUndead);
			put(Aspect.VOID, InfusedVoid);
			put(Aspect.WATER, InfusedWater);
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
