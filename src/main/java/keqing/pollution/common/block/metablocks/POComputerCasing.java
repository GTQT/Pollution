package keqing.pollution.common.block.metablocks;

import gregtech.api.block.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;
import static keqing.pollution.common.block.metablocks.POComputerCasing.CasingType.COMPUTER_CASING;

public class POComputerCasing extends VariantBlock<POComputerCasing.CasingType> {
    public POComputerCasing() {
        super(Material.IRON);
        this.setTranslationKey("bm_computer_casing");
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("wrench", 2);
        this.setDefaultState(this.getState(COMPUTER_CASING));
    }

    public static enum CasingType implements IStringSerializable {
        COMPUTER_CASING("computer_casing"),
        COMPUTER_HEAT_VENT("computer_heat_vent"),
        HIGH_POWER_CASING("high_power_casing"),
        ADVANCED_COMPUTER_CASING("advanced_computer_casing");

        private final String name;

        private CasingType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.getName();
        }
    }
}
