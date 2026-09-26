package meowmel.pollution.common.items;

import meowmel.pollution.Pollution;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.NotNull;

/**
 * Upgrade module for the Large Essentia Generator.
 *
 * <p>Ported from GregicaPlusPlus' {@code itemUpgrades}. Each damage value unlocks one aspect
 * category; the controller stores the installed set as a bitmask where bit {@code n} is
 * {@code ListUpgrade[n]}.</p>
 *
 * <p>Index 0 ("null") is the NORMAL category and is granted by default, so the machine can burn
 * basic essentia before any module is installed.</p>
 */
public class ItemEssentiaUpgrade extends Item {

    /**
     * Model/damage names, in bitmask order. <b>Appending is safe; reordering is not</b>, because
     * installed upgrade masks are persisted in NBT.
     */
    public static final String[] ListUpgrade = new String[]{
            "null",
            "air",
            "thermal",
            "unstable",
            "victus",
            "tainted",
            "mechanics",
            "spirit",
            "radiation",
            "electric"
    };

    /** Bitmask granted to a freshly formed generator (the default NORMAL category). */
    public static final int DEFAULT_MASK = 1;

    public ItemEssentiaUpgrade() {
        super();
        setRegistryName(Pollution.MODID, "essentia_upgrade");
        setTranslationKey(Pollution.MODID + ".essentia_upgrade");
        setHasSubtypes(true);
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) return;
        for (int i = 0; i < ListUpgrade.length; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public @NotNull String getTranslationKey(@NotNull ItemStack stack) {
        int damage = stack.getMetadata();
        if (damage < 0 || damage >= ListUpgrade.length) damage = 0;
        return "item." + Pollution.MODID + ".essentia_upgrade." + ListUpgrade[damage];
    }

    /** @return the bitmask bit this module installs, or 0 when the damage value is unknown. */
    public static int maskFor(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemEssentiaUpgrade)) return 0;
        int damage = stack.getMetadata();
        if (damage < 0 || damage >= ListUpgrade.length) return 0;
        return 1 << damage;
    }
}
