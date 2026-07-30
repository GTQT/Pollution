package meowmel.pollution.api.recipes.properties;

import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.item.ItemStack;

import java.util.Locale;

/** Stable recipe ids for the 22 major arcana already registered by Pollution. */
public final class TarotCards {

    private static final int FIRST_META = 301;
    private static final String[] IDS = {
            "the_fool", "the_magician", "the_high_priestess", "the_empress", "the_emperor",
            "the_highophant", "the_lovers", "the_chariot", "the_strength", "the_hermit",
            "the_wheel_of_fortune", "the_justice", "the_hanged_man", "death", "temperance",
            "the_devil", "the_tower", "the_star", "the_moon", "the_sun", "judgement", "the_world"
    };

    private TarotCards() {
    }

    public static boolean isTarot(ItemStack stack) {
        return getId(stack) != null;
    }

    public static String getId(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() != PollutionMetaItems.POLLUTION_META_ITEM) return null;
        int index = stack.getMetadata() - FIRST_META;
        return index >= 0 && index < IDS.length ? IDS[index] : null;
    }

    public static boolean matches(ItemStack stack, String tarotId) {
        String active = getId(stack);
        return active != null && tarotId != null && active.equals(tarotId.trim().toLowerCase(Locale.ROOT));
    }
}
