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
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class POHyper extends VariantBlock<POHyper.HyperType> {
	public POHyper() {
		super(Material.IRON);
		this.setTranslationKey("hyper");
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("wrench", 4);
		this.setDefaultState(this.getState(HyperType.HYPER_1_CASING));
	}


	public boolean canCreatureSpawn(@Nonnull IBlockState state,
	                                @Nonnull IBlockAccess world,
	                                @Nonnull BlockPos pos,
	                                @Nonnull EntityLiving.SpawnPlacementType type) {
		return false;
	}

	public enum HyperType implements IStringSerializable {

		HYPER_1_CASING("hyper_1_casing"),
		HYPER_2_CASING("hyper_2_casing"),
		HYPER_3_CASING("hyper_3_casing"),
		HYPER_4_CASING("hyper_4_casing"),
		HYPER_5_CASING("hyper_5_casing");

		private final String name;

		HyperType(String name) {
			this.name = name;
		}

		@Nonnull
		@Override
		public String getName() {
			return name;
		}
	}
}