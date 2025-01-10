package keqing.pollution.api.utils;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class POWorld {

    public static boolean isProgressionEnforced(World world) {
        return false;
    }

    public static boolean isBiomeSafeFor(Biome biome, Entity entity) {
        return true;
    }

}