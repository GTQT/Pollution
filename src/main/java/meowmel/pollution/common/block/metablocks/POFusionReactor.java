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
public class POFusionReactor extends VariantBlock<POFusionReactor.FusionBlockType> {
	public POFusionReactor() {
		super(Material.IRON);
		this.setTranslationKey("fusion_reactor");
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("wrench", 4);
		this.setDefaultState(this.getState(FusionBlockType.FRAME_I));
	}

	public boolean canCreatureSpawn(@Nonnull IBlockState state,
	                                @Nonnull IBlockAccess world,
	                                @Nonnull BlockPos pos,
	                                @Nonnull EntityLiving.SpawnPlacementType type) {
		return false;
	}

	public enum FusionBlockType implements IStringSerializable {

		FRAME_I("frame_i"),
		FRAME_II("frame_ii"),
		FRAME_III("frame_iii"),
		FRAME_IV("frame_iv"),
		FRAME_V("frame_v"),

		COMPOSE_I("compose_i"),
		COMPOSE_II("compose_ii"),
		COMPOSE_III("compose_iii"),
		COMPOSE_IV("compose_iv");


		private final String name;

		FusionBlockType(String name) {
			this.name = name;
		}

		@Nonnull
		@Override
		public String getName() {
			return name;
		}
	}
}
