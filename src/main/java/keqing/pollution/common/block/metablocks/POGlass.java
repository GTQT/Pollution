package keqing.pollution.common.block.metablocks;

import gregtech.api.block.VariantActiveBlock;
import gregtech.api.items.toolitem.ToolClasses;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class POGlass extends VariantActiveBlock<POGlass.MagicBlockType> {

	public POGlass() {
		super(Material.GLASS);
		setTranslationKey("glass");
		setHardness(5.0F);
		setResistance(5.0F);
		setSoundType(SoundType.GLASS);
		setHarvestLevel(ToolClasses.PICKAXE, 1);
		this.setDefaultState(this.getState(MagicBlockType.LAMINATED_GLASS));
	}

	public boolean canCreatureSpawn(@Nonnull IBlockState state,
	                                @Nonnull IBlockAccess world,
	                                @Nonnull BlockPos pos,
	                                @Nonnull EntityLiving.SpawnPlacementType type) {
		return false;
	}


	@Override
	@Nonnull
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
		return super.canRenderInLayer(state, layer);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(@Nonnull IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(@Nonnull IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public boolean shouldSideBeRendered(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos,
	                                    @Nonnull EnumFacing side) {
		IBlockState sideState = world.getBlockState(pos.offset(side));

		return sideState.getBlock() == this ?
				getState(sideState) != getState(state) :
				super.shouldSideBeRendered(state, world, pos, side);
	}

	public enum MagicBlockType implements IStringSerializable {

		LAMINATED_GLASS("laminated_glass"),
		AAMINATED_GLASS("aaminated_glass"),
		BAMINATED_GLASS("baminated_glass"),
		CAMINATED_GLASS("caminated_glass"),
		DAMINATED_GLASS("daminated_glass");

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