package keqing.pollution.api.utils;

import keqing.gtqtcore.GTQTCore;
import keqing.pollution.Pollution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.BitSet;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static net.minecraft.util.EnumFacing.*;

public class POUtils {

	public static ResourceLocation pollutionId(String id) {
		return new ResourceLocation(Pollution.MODID, id);
	}
	public static int intValueOfBitSet(BitSet set) {
		int result = 0;
		for (int i = 0; i < set.length(); i++) {
			result = result | (set.get(i) ? 1 : 0) << i;
		}
		return result;
	}

	public static BitSet forIntToBitSet(int i, int length) {
		return forIntToBitSet(i, length, new BitSet(length));
	}

	public static BitSet forIntToBitSet(int i, int length, BitSet result) {
		for (int j = 0; j < length; j++) {
			if (((i & (0b1 << j)) / (0b1 << j)) == 1) {
				result.set(j);
			} else {
				result.clear(j);
			}
		}
		return result;
	}

	public static <T> T getOrDefault(BooleanSupplier canGet, Supplier<T> getter, T defaultValue) {
		return canGet.getAsBoolean() ? getter.get() : defaultValue;
	}


	public static EnumFacing getFacingFromNeighbor(BlockPos pos, BlockPos neighbor) {
		BlockPos rel = neighbor.subtract(pos);
		if (rel.getX() == 1) {
			return EAST;
		}
		if (rel.getX() == -1) {
			return WEST;
		}
		if (rel.getY() == 1) {
			return UP;
		}
		if (rel.getY() == -1) {
			return DOWN;
		}
		if (rel.getZ() == 1) {
			return SOUTH;
		}
		return NORTH;
	}


	public static int getOrDefault(NBTTagCompound tag, String key, int defaultValue) {
		if (tag.hasKey(key)) {
			return tag.getInteger(key);
		}
		return defaultValue;
	}
	public static ItemStack getItemStack(String itemstr) {
		return getItemStack(itemstr, 0);
	}

	public static ItemStack getItemStack(String itemstr, long num) {
		var item = getItemStack(itemstr, 0);
		item.setCount((int) num);
		return item;
	}

	public static ItemStack getItemStack(String itemstr, int meta) {
		if (itemstr.startsWith("<") && itemstr.endsWith(">"))
			itemstr = itemstr.substring(1, itemstr.length() - 1);
		String[] str = itemstr.split(":");
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(str[0], str[1]));
		if (item != null) {
			if (str.length == 3)
				return new ItemStack(item, 1, Integer.parseInt(str[2]));
			return new ItemStack(item, 1, meta);
		} else {
			return ItemStack.EMPTY;
		}
	}

}