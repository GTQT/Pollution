package meowmel.pollution.mixin.gregtech;

import gregtech.api.unification.ore.StoneType;
import gregtech.api.worldgen.generator.CachedGridEntry;
import gregtech.common.blocks.BlockLeanOre;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Selects the correct 16-stone-type block group when GTCEu scatters lean ores.
 */
@Mixin(value = CachedGridEntry.class, remap = false)
public abstract class MixinCachedGridEntry {

    @Redirect(
            method = "scatterLeanOres",
            at = @At(
                    value = "INVOKE",
                    target = "Lgregtech/common/blocks/BlockLeanOre;getOreBlock(Lgregtech/api/unification/ore/StoneType;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState pollution$selectLeanOreBlock(BlockLeanOre original, StoneType stoneType) {
        if (original.STONE_TYPE.getAllowedValues().contains(stoneType)) {
            return original.getOreBlock(stoneType);
        }

        for (BlockLeanOre candidate : MetaBlocks.LEAN_ORES) {
            if (candidate.material == original.material &&
                    candidate.STONE_TYPE.getAllowedValues().contains(stoneType)) {
                return candidate.getOreBlock(stoneType);
            }
        }

        // Keep world generation alive if another addon registers a stone type after ore blocks are created.
        return original.getDefaultState();
    }
}
