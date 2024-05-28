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
public class POTurbine extends VariantBlock<POTurbine.MagicBlockType> {
	public POTurbine() {
		super(Material.IRON);
		this.setTranslationKey("turbine");
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("wrench", 4);
		this.setDefaultState(this.getState(MagicBlockType.BRONZE_GEARBOX));
	}

	public boolean canCreatureSpawn(@Nonnull IBlockState state,
	                                @Nonnull IBlockAccess world,
	                                @Nonnull BlockPos pos,
	                                @Nonnull EntityLiving.SpawnPlacementType type) {
		return false;
	}

	public enum MagicBlockType implements IStringSerializable {

		BRONZE_GEARBOX("bronze_gearbox"),
		STEEL_GEARBOX("steel_gearbox"),
		STAINLESS_STEEL_GEARBOX("stainless_steel_gearbox"),
		TITANIUM_GEARBOX("titanium_gearbox"),
		TUNGSTENSTEEL_GEARBOX("tungstensteel_gearbox"),

		BRONZE_PIPE("bronze_pipe"),
		STEEL_PIPE("steel_pipe"),
		TITANIUM_PIPE("titanium_pipe"),
		TUNGSTENSTEEL_PIPE("tungstensteel_pipe"),
		POLYTETRAFLUOROETHYLENE_PIPE("polytetrafluoroethylene_pipe");


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
