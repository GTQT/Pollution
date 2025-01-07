package keqing.pollution.dimension.worldgen.terraingen;


import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.world.gen.MapGenBase;

public class InitMapGenEvent extends Event
{
    /** Use CUSTOM to filter custom event types
     */
    public static enum EventType {BTN,BTN_CAVE}

    private final EventType type;
    private final MapGenBase originalGen;
    private MapGenBase newGen;

    InitMapGenEvent(EventType type, MapGenBase original)
    {
        this.type = type;
        this.originalGen = original;
        this.setNewGen(original);
    }
    public EventType getType() { return type; }
    public MapGenBase getOriginalGen() { return originalGen; }
    public MapGenBase getNewGen() { return newGen; }
    public void setNewGen(MapGenBase newGen) { this.newGen = newGen; }
}