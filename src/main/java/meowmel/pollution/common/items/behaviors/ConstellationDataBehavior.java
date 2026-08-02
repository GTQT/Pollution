package meowmel.pollution.common.items.behaviors;

import gregtech.api.items.metaitem.stats.IItemBehaviour;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import meowmel.pollution.api.astral.AstralNbtHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/** Shows the canonical Astral Sorcery constellation stored on a magic item. */
public final class ConstellationDataBehavior implements IItemBehaviour {

    @Override
    public void addInformation(ItemStack stack, List<String> lines) {
        IConstellation constellation = AstralNbtHelper.readConstellation(stack);
        if (constellation == null) {
            lines.add(TextFormatting.DARK_GRAY + I18n.format("pollution.astral_data.unattuned"));
            return;
        }
        String localizedName = I18n.format(constellation.getUnlocalizedName());
        lines.add(TextFormatting.AQUA + I18n.format("pollution.astral_data.constellation",
                localizedName));
        String function = stack.getTagCompound().getString(AstralNbtHelper.CELESTIAL_FUNCTION);
        if (!function.isEmpty()) {
            lines.add(TextFormatting.LIGHT_PURPLE + I18n.format("pollution.astral_data.function",
                    I18n.format("pollution.astral_data.function." + function)));
        }
        lines.add(TextFormatting.GRAY + I18n.format("pollution.astral_data.nbt_preserved"));
    }
}
