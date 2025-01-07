package keqing.pollution.dimension.worldgen.terraingen;

import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.common.MinecraftForge;

public abstract class TerrainGen{
    public static MapGenBase getModdedMapGen(MapGenBase original, InitMapGenEvent.EventType type)
    {
        InitMapGenEvent event = new InitMapGenEvent(type, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getNewGen();
    }
}
