package keqing.pollution.dimension.worldgen;

import keqing.pollution.dimension.worldgen.mapGen.MapGenBTNBridge;
import keqing.pollution.dimension.worldgen.structure.StructureBTNBridgePieces;

import static net.minecraft.world.gen.structure.MapGenStructureIO.registerStructure;

public class POStructureManager {

    public static void init() {
        registerStructure(MapGenBTNBridge.Start.class, "BTNFortress");
        StructureBTNBridgePieces.registerBTNFortressPieces();
    }
}
