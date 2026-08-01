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

    private static final int FIRST_ADDON_STONE_TYPE_ID = 12;
    private static final int MAX_STONE_TYPE_ID = 128;

    public static StoneType LIVINGROCK;

    private PollutionStoneTypes() {}

    public static void init() {
        if (LIVINGROCK != null) return;

        // Material events run before GTQTCore normally initializes its stone types during block registration.
        // Initialize both registries now so livingrock cannot claim one of GTQTCore's reserved IDs (12-19).
        StoneType.init();
        GTQTStoneTypes.init();

        StoneType existing = StoneType.STONE_TYPE_REGISTRY.getObject("livingrock");
        if (existing != null) {
            LIVINGROCK = existing;
            return;
        }

        LIVINGROCK = StoneType.Builder.create(findFreeId(), "livingrock")
                .soundType(SoundType.STONE)
                .processingPrefix(OrePrefix.ore)
                .stoneMaterial(Materials.Stone)
                .stone(ModBlocks.livingrock::getDefaultState)
                .predicate(state -> state.getBlock() == ModBlocks.livingrock)
                .shouldBeDroppedAsItem(true)
                .build();
    }

    private static int findFreeId() {
        for (int id = FIRST_ADDON_STONE_TYPE_ID; id < MAX_STONE_TYPE_ID; id++) {
            if (StoneType.STONE_TYPE_REGISTRY.getObjectById(id) == null) return id;
        }
        throw new IllegalStateException("No free GTCEu stone type ID is available for Botania livingrock");
    }
}
