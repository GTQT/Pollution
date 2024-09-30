package keqing.pollution.common.block.metablocks;

import gregtech.api.block.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

import static keqing.pollution.common.block.metablocks.POManaPlate.ManaBlockType.MANA_BASIC;

public class POManaPlate extends VariantBlock<POManaPlate.ManaBlockType> {
    public POManaPlate() {
        super(Material.IRON);
        this.setTranslationKey("mana_plate");
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("wrench", 4);
        this.setDefaultState(this.getState(MANA_BASIC));
    }

    public boolean canCreatureSpawn(@Nonnull IBlockState state,
                                    @Nonnull IBlockAccess world,
                                    @Nonnull BlockPos pos,
                                    @Nonnull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum ManaBlockType implements IStringSerializable {

        MANA_BASIC("mana_basic"),
        MANA_1("mana_1"),
        MANA_2("mana_2"),
        MANA_3("mana_3"),
        MANA_4("mana_4"),
        MANA_5("mana_5");


        private final String name;

        ManaBlockType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }
    }
}
