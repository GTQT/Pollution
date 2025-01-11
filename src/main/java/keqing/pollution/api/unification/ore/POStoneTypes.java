package keqing.pollution.api.unification.ore;

import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.ore.StoneType;
import keqing.gtqtcore.api.unification.GTQTMaterials;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import thebetweenlands.common.registries.BlockRegistry;

public class POStoneTypes {
    public static StoneType BETWEENSTONE;
    public static StoneType PITSTONE;
    public static StoneType BTLIMESTONE;
    public POStoneTypes(){

    }
    public static void init(){
        BETWEENSTONE = new StoneType(21, "betweenstone", SoundType.STONE, OrePrefix.ore, PollutionMaterials.BetweenStone,
                BlockRegistry.BETWEENSTONE::getDefaultState,
                (state) -> state.getBlock() == BlockRegistry.BETWEENSTONE, false);

        PITSTONE = new StoneType(22, "pitstone", SoundType.STONE, OrePrefix.ore, PollutionMaterials.PitStone,
                BlockRegistry.PITSTONE::getDefaultState,
                (state) -> state.getBlock() == BlockRegistry.PITSTONE, false);

        BTLIMESTONE = new StoneType(23, "btlimestone", SoundType.STONE, OrePrefix.ore, GTQTMaterials.Limestone,
                BlockRegistry.LIMESTONE::getDefaultState,
                (state) -> state.getBlock() == BlockRegistry.LIMESTONE, false);
    }
}
