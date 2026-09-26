package meowmel.pollution.common.block.metablocks;

import gregtech.api.block.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

/**
 * Essentia Diffusion Cell ("源质扩散单元").
 *
 * <p>Ported from GregicaPlusPlus' {@code GCMetaCells}. These blocks form the shell of the Large
 * Essentia Generator and are the machine's single upgrade axis: the whole shell must be built from
 * one tier, and the tier multiplies every unit of essentia the generator burns.</p>
 *
 * <p>Tier values deliberately start at 1 so that {@code tier - 1} is a safe array index and
 * "no cell" can be represented as 0.</p>
 */
public class POEssentiaCell extends VariantBlock<POEssentiaCell.CellType> {

    public POEssentiaCell() {
        super(Material.IRON);
        setTranslationKey("essentia_cell");
        setHardness(4.0F);
        setResistance(12.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 3);
        setDefaultState(getState(CellType.ESSENTIA_CELL_T1));
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull IBlockState state,
                                   @Nonnull IBlockAccess world,
                                   @Nonnull BlockPos pos,
                                   @Nonnull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum CellType implements IStringSerializable {

        ESSENTIA_CELL_T1("essentia_cell_1"),
        ESSENTIA_CELL_T2("essentia_cell_2"),
        ESSENTIA_CELL_T3("essentia_cell_3"),
        ESSENTIA_CELL_T4("essentia_cell_4");

        private final String name;

        CellType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }

        /** Tier as used by the structure-definition channel, matching the original 1..4 scale. */
        public int getTier() {
            return ordinal() + 1;
        }
    }
}
