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
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class POCoilBlock extends VariantBlock<POCoilBlock.WireCoilType> {
	public POCoilBlock() {
		super(Material.IRON);
		this.setTranslationKey("wire_coil");
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("wrench", 4);
		this.setDefaultState(this.getState(WireCoilType.COIL_LEVEL_1));
	}

	public boolean canCreatureSpawn(@Nonnull IBlockState state,
	                                @Nonnull IBlockAccess world,
	                                @Nonnull BlockPos pos,
	                                @Nonnull EntityLiving.SpawnPlacementType type) {
		return false;
	}

	public enum WireCoilType implements IStringSerializable {

		COIL_LEVEL_1("machine_coil_cupronickel"),
		COIL_LEVEL_2("machine_coil_kanthal"),
		COIL_LEVEL_3("machine_coil_nichrome"),
		COIL_LEVEL_4("machine_coil_rtm_alloy"),
		COIL_LEVEL_5("machine_coil_hssg"),
		COIL_LEVEL_6("machine_coil_naquadah"),
		COIL_LEVEL_7("machine_coil_trinium"),
		COIL_LEVEL_8("machine_coil_tritanium");

		private final String name;

		WireCoilType(String name) {
			this.name = name;
		}

		@Nonnull
		@Override
		public String getName() {
			return name;
		}
	}
}
