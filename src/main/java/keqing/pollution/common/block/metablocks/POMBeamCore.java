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
public class POMBeamCore extends VariantBlock<POMBeamCore.MagicBlockType> {
	public POMBeamCore() {
		super(Material.IRON);
		this.setTranslationKey("beam_core");
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("wrench", 4);
		this.setDefaultState(this.getState(MagicBlockType.BEAM_CORE_0));
	}

	public boolean canCreatureSpawn(@Nonnull IBlockState state,
	                                @Nonnull IBlockAccess world,
	                                @Nonnull BlockPos pos,
	                                @Nonnull EntityLiving.SpawnPlacementType type) {
		return false;
	}

	public enum MagicBlockType implements IStringSerializable {

		BEAM_CORE_0("beam_core_0"),
		BEAM_CORE_1("beam_core_1"),
		BEAM_CORE_2("beam_core_2"),
		BEAM_CORE_3("beam_core_3"),
		BEAM_CORE_4("beam_core_4"),

		FILTER_1("filter_1"),
		FILTER_2("filter_2"),
		FILTER_3("filter_3"),
		FILTER_4("filter_4"),
		FILTER_5("filter_5");


		private final String name;

		MagicBlockType(String name) {
			this.name = name;
		}

		@Nonnull
		@Override
		public String getName() {
			return name;
		}
	}
}
