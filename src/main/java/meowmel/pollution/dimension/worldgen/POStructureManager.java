package meowmel.pollution.dimension.worldgen;

import meowmel.pollution.dimension.worldgen.mapGen.MapGenBTNBridge;
import meowmel.pollution.dimension.worldgen.structure.StructureBTNBridgePieces;

import static net.minecraft.world.gen.structure.MapGenStructureIO.registerStructure;

public class POStructureManager {

    public static void init() {
        registerStructure(MapGenBTNBridge.Start.class, "BTNFortress");
        StructureBTNBridgePieces.registerBTNFortressPieces();
    }
}
