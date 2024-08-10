package keqing.pollution.api.predicate;

import gregtech.api.block.VariantActiveBlock;
import gregtech.api.pattern.BlockWorldState;
import gregtech.api.pattern.PatternStringError;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.BlockInfo;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import keqing.gtqtcore.api.blocks.ITired;
import keqing.gtqtcore.api.blocks.impl.WrappedIntTired;
import keqing.gtqtcore.api.pattern.TierTraceabilityPredicate;
import keqing.pollution.common.block.PollutionMetaBlocks;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.function.Supplier;


import static keqing.pollution.api.POAPI.*;

public class TiredTraceabilityPredicate {

	public static Supplier<TierTraceabilityPredicate> CP_COIL_CASING  = () -> new TierTraceabilityPredicate(MAP_COIL_CASING,
			Comparator.comparing((s) -> ((WrappedIntTired) MAP_COIL_CASING.get(s)).getIntTier()), "COIL", null);

	public static Supplier<TierTraceabilityPredicate> CP_BEAM_CORE  = () -> new TierTraceabilityPredicate(MAP_CP_BEAM,
			Comparator.comparing((s) -> ((WrappedIntTired) MAP_CP_BEAM.get(s)).getIntTier()), "BEAM", null);

	public static Supplier<TierTraceabilityPredicate> CP_GLASS  = () -> new TierTraceabilityPredicate(MAP_CP_GLASS,
			Comparator.comparing((s) -> ((WrappedIntTired) MAP_CP_GLASS.get(s)).getIntTier()), "GLASS", null);

	public static Supplier<TierTraceabilityPredicate> CP_COMPOSE  = () -> new TierTraceabilityPredicate(MAP_CP_COMPOSE,
			Comparator.comparing((s) -> ((WrappedIntTired) MAP_CP_COMPOSE.get(s)).getIntTier()), "COMPOSE", null);

	public static Supplier<TierTraceabilityPredicate> CP_FRAME = () -> new TierTraceabilityPredicate(MAP_CP_FRAME,
			Comparator.comparing((s) -> ((WrappedIntTired) MAP_CP_FRAME.get(s)).getIntTier()), "FRAME", null);
}