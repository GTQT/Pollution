package meowmel.pollution.api.unification;

import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.ore.StoneType;
import meowmel.gtqtcore.api.unification.ore.GTQTStoneTypes;
import net.minecraft.block.SoundType;
import vazkii.botania.common.block.ModBlocks;

/**
 * Stone types supplied by Pollution for GTCEu ore generation and rendering.
 */
public final class PollutionStoneTypes {

    public static StoneType LIVINGROCK;

    private PollutionStoneTypes() {}

    public static void init() {
        if (LIVINGROCK != null) return;

        // Material events run before GTQTCore normally initializes its stone types during block registration.
        // Initialize them now so GTQTCore keeps the ids it had before Pollution claimed one, then let
        // StoneType#getStoneTypeID() hand out the next free id for livingrock.
        StoneType.init();
        GTQTStoneTypes.init();

        StoneType existing = StoneType.STONE_TYPE_REGISTRY.getObject("livingrock");
        if (existing != null) {
            LIVINGROCK = existing;
            return;
        }

        LIVINGROCK = StoneType.Builder.create("livingrock")
                .soundType(SoundType.STONE)
                .processingPrefix(OrePrefix.ore)
                .stoneMaterial(Materials.Stone)
                .stone(ModBlocks.livingrock::getDefaultState)
                .predicate(state -> state.getBlock() == ModBlocks.livingrock)
                .shouldBeDroppedAsItem(true)
                .build();
    }
}
