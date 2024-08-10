package keqing.pollution.api;

import gregtech.common.blocks.MetaBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import keqing.gtqtcore.api.blocks.IBlockTier;
import keqing.gtqtcore.api.blocks.ITired;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.api.predicate.TiredTraceabilityPredicate;
import keqing.pollution.common.block.PollutionMetaBlocks;
import net.minecraft.block.state.IBlockState;

import static keqing.pollution.common.block.PollutionMetaBlocks.WIRE_COIL;
import static keqing.pollution.common.block.metablocks.POCoilBlock.WireCoilType.*;
import static keqing.pollution.common.block.metablocks.POCoilBlock.WireCoilType.COIL_LEVEL_8;
import static keqing.pollution.common.block.metablocks.POFusionReactor.FusionBlockType.*;
import static keqing.pollution.common.block.metablocks.POFusionReactor.FusionBlockType.FRAME_V;
import static keqing.pollution.common.block.metablocks.POGlass.MagicBlockType.*;
import static keqing.pollution.common.block.metablocks.POGlass.MagicBlockType.DAMINATED_GLASS;
import static keqing.pollution.common.block.metablocks.POMBeamCore.MagicBlockType.*;
import static keqing.pollution.common.block.metablocks.POMBeamCore.MagicBlockType.FILTER_5;

public class POAPI {
    public static final Object2ObjectOpenHashMap<IBlockState, IBlockTier> MAP_COIL_CASING= new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectOpenHashMap<IBlockState, IBlockTier> MAP_CP_BEAM= new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectOpenHashMap<IBlockState, IBlockTier> MAP_CP_GLASS= new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectOpenHashMap<IBlockState, IBlockTier> MAP_CP_COMPOSE= new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectOpenHashMap<IBlockState, IBlockTier> MAP_CP_FRAME= new Object2ObjectOpenHashMap<>();
    public static void init() {

        
        MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_1),
                new WrappedIntTired(COIL_LEVEL_1, 1));
        MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_2),
                new WrappedIntTired(COIL_LEVEL_2, 2));
        MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_3),
                new WrappedIntTired(COIL_LEVEL_3, 3));
        MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_4),
                new WrappedIntTired(COIL_LEVEL_4, 4));
        MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_5),
                new WrappedIntTired(COIL_LEVEL_5, 5));
        MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_6),
                new WrappedIntTired(COIL_LEVEL_6, 6));
        MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_7),
                new WrappedIntTired(COIL_LEVEL_7, 7));
        MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_8),
                new WrappedIntTired(COIL_LEVEL_8, 8));


        MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_1),
                new WrappedIntTired(FILTER_1, 1));
        MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_2),
                new WrappedIntTired(FILTER_2, 2));
        MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_3),
                new WrappedIntTired(FILTER_3, 3));
        MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_4),
                new WrappedIntTired(FILTER_4, 4));
        MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_5),
                new WrappedIntTired(FILTER_5, 5));

        MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(LAMINATED_GLASS),
                new WrappedIntTired(LAMINATED_GLASS, 1));
        MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(AAMINATED_GLASS),
                new WrappedIntTired(AAMINATED_GLASS, 2));
        MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(BAMINATED_GLASS),
                new WrappedIntTired(BAMINATED_GLASS, 3));
        MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(CAMINATED_GLASS),
                new WrappedIntTired(CAMINATED_GLASS, 4));
        MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(DAMINATED_GLASS),
                new WrappedIntTired(DAMINATED_GLASS, 5));

        MAP_CP_COMPOSE.put(PollutionMetaBlocks.FUSION_REACTOR.getState(COMPOSE_I),
                new WrappedIntTired(COMPOSE_I, 1));
        MAP_CP_COMPOSE.put(PollutionMetaBlocks.FUSION_REACTOR.getState(COMPOSE_II),
                new WrappedIntTired(COMPOSE_II, 2));
        MAP_CP_COMPOSE.put(PollutionMetaBlocks.FUSION_REACTOR.getState(COMPOSE_III),
                new WrappedIntTired(COMPOSE_III, 3));
        MAP_CP_COMPOSE.put(PollutionMetaBlocks.FUSION_REACTOR.getState(COMPOSE_IV),
                new WrappedIntTired(COMPOSE_IV, 4));

        MAP_CP_FRAME.put(PollutionMetaBlocks.FUSION_REACTOR.getState(FRAME_II),
                new WrappedIntTired(FRAME_II, 1));
        MAP_CP_FRAME.put(PollutionMetaBlocks.FUSION_REACTOR.getState(FRAME_III),
                new WrappedIntTired(FRAME_III, 2));
        MAP_CP_FRAME.put(PollutionMetaBlocks.FUSION_REACTOR.getState(FRAME_IV),
                new WrappedIntTired(FRAME_IV, 3));
        MAP_CP_FRAME.put(PollutionMetaBlocks.FUSION_REACTOR.getState(FRAME_V),
                new WrappedIntTired(FRAME_V, 4));


    }
}
