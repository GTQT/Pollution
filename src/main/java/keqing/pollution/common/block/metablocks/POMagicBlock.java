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
public class POMagicBlock extends VariantBlock<POMagicBlock.MagicBlockType> {
	public POMagicBlock() {
		super(Material.IRON);
		this.setTranslationKey("magic_block");
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("wrench", 4);
		this.setDefaultState(this.getState(MagicBlockType.VOID_PRISM));
	}

	public boolean canCreatureSpawn(@Nonnull IBlockState state,
	                                @Nonnull IBlockAccess world,
	                                @Nonnull BlockPos pos,
	                                @Nonnull EntityLiving.SpawnPlacementType type) {
		return false;
	}

	public enum MagicBlockType implements IStringSerializable {

		VOID_PRISM("void_prism"),
		SPELL_PRISM("spell_prism"),
		SPELL_PRISM_COLD("spell_prism_cold"),
		SPELL_PRISM_HOT("spell_prism_hot"),
		SPELL_PRISM_WATER("spell_prism_water"),
		SPELL_PRISM_ORDER("spell_prism_order"),
		SPELL_PRISM_AIR("spell_prism_air"),
		SPELL_PRISM_EARTH("spell_prism_earth"),
		SPELL_PRISM_VOID("spell_prism_void"),
		ALLOY_BLAST_CASING("alloy_blast_casing"),
		MAGIC_BATTERY("magic_battery");


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
