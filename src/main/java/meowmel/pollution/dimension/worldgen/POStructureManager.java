package meowmel.pollution.dimension.worldgen;

import meowmel.pollution.dimension.worldgen.mapGen.MapGenUndergroundBridge;
import meowmel.pollution.dimension.worldgen.structure.StructureUndergroundBridgePieces;

import static net.minecraft.world.gen.structure.MapGenStructureIO.registerStructure;

public class POStructureManager {

    public static void init() {
        registerStructure(MapGenUndergroundBridge.Start.class, "UndergroundFortress");
        StructureUndergroundBridgePieces.registerUndergroundFortressPieces();
    }
}
