package meowmel.pollution.common.block.metablocks;

import gregtech.api.block.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import static meowmel.pollution.common.CommonProxy.Pollution_TAB;

/** Dedicated dragon-machine casings. Append variants to preserve saved metadata. */
public class PODragonCasing extends VariantBlock<PODragonCasing.Type> {
    public PODragonCasing() {
        super(Material.IRON);
        setTranslationKey("dragon_casing");
        setHardness(10.0F);
        setResistance(80.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 4);
        setCreativeTab(Pollution_TAB);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getState(state) == Type.INCUBATOR_RUNED_CASING ? 5 : 0;
    }

    public enum Type implements IStringSerializable {
        BLOOD_EXTRACTION_CASING("blood_extraction_casing"),
        INCUBATOR_CASING("incubator_casing"),
        INCUBATOR_RUNED_CASING("incubator_runed_casing");

        private final String name;
        Type(String name) { this.name = name; }
        @Override public String getName() { return name; }
    }
}
