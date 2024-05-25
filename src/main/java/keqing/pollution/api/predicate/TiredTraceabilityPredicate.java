package keqing.pollution.api.predicate;

import gregtech.api.block.VariantActiveBlock;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.PatternStringError;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.BlockInfo;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import keqing.gtqtcore.api.blocks.ITired;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.pollution.common.block.PollutionMetaBlocks;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static keqing.pollution.common.block.metablocks.POCoilBlock.WireCoilType.*;
import static keqing.pollution.common.block.metablocks.POFusionReactor.FusionBlockType.*;
import static keqing.pollution.common.block.metablocks.POGlass.MagicBlockType.*;
import static keqing.pollution.common.block.metablocks.POMBeamCore.MagicBlockType.*;
import static keqing.pollution.common.block.PollutionMetaBlocks.WIRE_COIL;

public class TiredTraceabilityPredicate extends TraceabilityPredicate {


    static {

        MAP_COIL_CASING = new Object2ObjectOpenHashMap<>();
        MAP_CP_BEAM = new Object2ObjectOpenHashMap<>();
        MAP_CP_GLASS = new Object2ObjectOpenHashMap<>();
        MAP_CP_FRAME = new Object2ObjectOpenHashMap<>();
        MAP_CP_COMPOSE = new Object2ObjectOpenHashMap<>();

        TiredTraceabilityPredicate.MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_1),
                new WrappedIntTired(COIL_LEVEL_1, 1));
        TiredTraceabilityPredicate.MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_2),
                new WrappedIntTired(COIL_LEVEL_2, 2));
        TiredTraceabilityPredicate.MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_3),
                new WrappedIntTired(COIL_LEVEL_3, 3));
        TiredTraceabilityPredicate.MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_4),
                new WrappedIntTired(COIL_LEVEL_4, 4));
        TiredTraceabilityPredicate.MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_5),
                new WrappedIntTired(COIL_LEVEL_5, 5));
        TiredTraceabilityPredicate.MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_6),
                new WrappedIntTired(COIL_LEVEL_6, 6));
        TiredTraceabilityPredicate.MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_7),
                new WrappedIntTired(COIL_LEVEL_7, 7));
        TiredTraceabilityPredicate.MAP_COIL_CASING.put(WIRE_COIL.getState(COIL_LEVEL_8),
                new WrappedIntTired(COIL_LEVEL_8, 8));


        TiredTraceabilityPredicate.MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_1),
                new WrappedIntTired(FILTER_1,1));
        TiredTraceabilityPredicate.MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_2),
                new WrappedIntTired(FILTER_2,2));
        TiredTraceabilityPredicate.MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_3),
                new WrappedIntTired(FILTER_3,3));
        TiredTraceabilityPredicate.MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_4),
                new WrappedIntTired(FILTER_4,4));
        TiredTraceabilityPredicate.MAP_CP_BEAM.put(PollutionMetaBlocks.BEAM_CORE.getState(FILTER_5),
                new WrappedIntTired(FILTER_5,5));

        TiredTraceabilityPredicate.MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(LAMINATED_GLASS),
                new WrappedIntTired(LAMINATED_GLASS,1));
        TiredTraceabilityPredicate.MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(AAMINATED_GLASS),
                new WrappedIntTired(AAMINATED_GLASS,2));
        TiredTraceabilityPredicate.MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(BAMINATED_GLASS),
                new WrappedIntTired(BAMINATED_GLASS,3));
        TiredTraceabilityPredicate.MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(CAMINATED_GLASS),
                new WrappedIntTired(CAMINATED_GLASS,4));
        TiredTraceabilityPredicate.MAP_CP_GLASS.put(PollutionMetaBlocks.GLASS.getState(DAMINATED_GLASS),
                new WrappedIntTired(DAMINATED_GLASS,5));

        TiredTraceabilityPredicate.MAP_CP_COMPOSE.put(PollutionMetaBlocks.FUSION_REACTOR.getState(COMPOSE_I),
                new WrappedIntTired(COMPOSE_I,1));
        TiredTraceabilityPredicate.MAP_CP_COMPOSE.put(PollutionMetaBlocks.FUSION_REACTOR.getState(COMPOSE_II),
                new WrappedIntTired(COMPOSE_II,2));
        TiredTraceabilityPredicate.MAP_CP_COMPOSE.put(PollutionMetaBlocks.FUSION_REACTOR.getState(COMPOSE_III),
                new WrappedIntTired(COMPOSE_III,3));
        TiredTraceabilityPredicate.MAP_CP_COMPOSE.put(PollutionMetaBlocks.FUSION_REACTOR.getState(COMPOSE_IV),
                new WrappedIntTired(COMPOSE_IV,4));

        TiredTraceabilityPredicate.MAP_CP_FRAME.put(PollutionMetaBlocks.FUSION_REACTOR.getState(FRAME_II),
                new WrappedIntTired(FRAME_II,1));
        TiredTraceabilityPredicate.MAP_CP_FRAME.put(PollutionMetaBlocks.FUSION_REACTOR.getState(FRAME_III),
                new WrappedIntTired(FRAME_III,2));
        TiredTraceabilityPredicate.MAP_CP_FRAME.put(PollutionMetaBlocks.FUSION_REACTOR.getState(FRAME_IV),
                new WrappedIntTired(FRAME_IV,3));
        TiredTraceabilityPredicate.MAP_CP_FRAME.put(PollutionMetaBlocks.FUSION_REACTOR.getState(FRAME_V),
                new WrappedIntTired(FRAME_V,4));


    }

    public static final Object2ObjectOpenHashMap<IBlockState,ITired> MAP_COIL_CASING;
    public static final Object2ObjectOpenHashMap<IBlockState,ITired> MAP_CP_BEAM;
    public static final Object2ObjectOpenHashMap<IBlockState,ITired> MAP_CP_GLASS;
    public static final Object2ObjectOpenHashMap<IBlockState,ITired> MAP_CP_COMPOSE;
    public static final Object2ObjectOpenHashMap<IBlockState,ITired> MAP_CP_FRAME;

    public static TraceabilityPredicate CP_COIL_CASING = new TiredTraceabilityPredicate(MAP_COIL_CASING,
            Comparator.comparing((s) -> ((WrappedIntTired)MAP_COIL_CASING.get(s)).getIntTier()),"COIL",null);


    public static TraceabilityPredicate CP_BEAM_CORE = new TiredTraceabilityPredicate(MAP_CP_BEAM,
            Comparator.comparing((s) -> ((WrappedIntTired)MAP_CP_BEAM.get(s)).getIntTier()),"BEAM",null);

    public static TraceabilityPredicate CP_GLASS = new TiredTraceabilityPredicate(MAP_CP_GLASS,
            Comparator.comparing((s) -> ((WrappedIntTired)MAP_CP_GLASS.get(s)).getIntTier()),"GLASS",null);

    public static TraceabilityPredicate CP_COMPOSE = new TiredTraceabilityPredicate(MAP_CP_COMPOSE,
            Comparator.comparing((s) -> ((WrappedIntTired)MAP_CP_COMPOSE.get(s)).getIntTier()),"COMPOSE",null);

    public static TraceabilityPredicate CP_FRAME = new TiredTraceabilityPredicate(MAP_CP_FRAME,
            Comparator.comparing((s) -> ((WrappedIntTired)MAP_CP_FRAME.get(s)).getIntTier()),"FRAME",null);


    private final Object2ObjectOpenHashMap<IBlockState, ITired> map;
    private final String name;

    private final String errorKey;

    private Supplier<BlockInfo[]> candidatesCache;

    private final Comparator<IBlockState> comparator;


    public TiredTraceabilityPredicate(Object2ObjectOpenHashMap<IBlockState, ITired> map, String name, @Nullable String errorKey){
        this(map,Comparator.comparing((s) -> map.get(s).getName()),name,errorKey);
    }

    public TiredTraceabilityPredicate(Object2ObjectOpenHashMap<IBlockState, ITired> map, Comparator<IBlockState> comparator, String name, @Nullable String errorKey){
        super();
        this.map = map;
        this.name = name;
        if(errorKey == null){
            this.errorKey = "gregtech.multiblock.pattern.error.casing";
        }
        else{
            this.errorKey = errorKey;
        }
        this.common.add(new SimplePredicate(predicate(), candidates()));
        this.comparator = comparator;

    }

    private Predicate<BlockWorldState> predicate(){
        return  (blockWorldState) -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (map.containsKey(blockState)) {
                ITired stats = map.get(blockState);
                Object tier = stats.getTire();
                Object current = blockWorldState.getMatchContext().getOrPut(name, tier);
                if (!current.equals(tier)) {
                    blockWorldState.setError(new PatternStringError(errorKey));
                    return false;
                } else {
                    blockWorldState.getMatchContext().getOrPut(name+"TiredStats",stats);
                    if(blockState.getBlock() instanceof VariantActiveBlock){
                        (blockWorldState.getMatchContext().getOrPut("VABlock", new LinkedList<>())).add(blockWorldState.getPos());
                    }
                    return true;
                }
            } else {
                return false;
            }
        };
    }

    private Supplier<BlockInfo[]> candidates(){
        if(candidatesCache == null) {
            candidatesCache = () -> map.keySet().stream()
                    .sorted(comparator)
                    .map(type -> new BlockInfo(type, null,map.get(type).getInfo()))
                    .toArray(BlockInfo[]::new);
        }
        return candidatesCache;
    }
}