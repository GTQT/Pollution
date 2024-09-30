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
public class POBotBlock extends VariantBlock<POBotBlock.BotBlockType> {
	public POBotBlock() {
		super(Material.IRON);
		this.setTranslationKey("bot_block");
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("wrench", 4);
		this.setDefaultState(this.getState(BotBlockType.TERRA_WATERTIGHT_CASING));
	}


	public boolean canCreatureSpawn(@Nonnull IBlockState state,
									@Nonnull IBlockAccess world,
									@Nonnull BlockPos pos,
									@Nonnull EntityLiving.SpawnPlacementType type) {
		return false;
	}

	public enum BotBlockType implements IStringSerializable {

		TERRA_WATERTIGHT_CASING("terra_watertight_casing"),
		TERRA_1_CASING("terra_1_casing"),
		TERRA_2_CASING("terra_2_casing"),
		TERRA_3_CASING("terra_3_casing"),
		TERRA_4_CASING("terra_4_casing"),
		TERRA_5_CASING("terra_5_casing"),
		TERRA_6_CASING("terra_6_casing");


		private final String name;

		BotBlockType(String name) {
			this.name = name;
		}

		@Nonnull
		@Override
		public String getName() {
			return name;
		}
		}
}