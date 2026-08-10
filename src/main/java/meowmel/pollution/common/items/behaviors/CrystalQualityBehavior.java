package meowmel.pollution.common.items.behaviors;

import gregtech.api.items.metaitem.stats.IItemBehaviour;
import meowmel.pollution.api.astral.AstralCrystalNbtHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/** Displays the quality data preserved by the industrial crystal cultivation chain. */
public final class CrystalQualityBehavior implements IItemBehaviour {

    @Override
    public void addInformation(ItemStack stack, List<String> lines) {
        int purity = AstralCrystalNbtHelper.getPurity(stack);
        int stability = AstralCrystalNbtHelper.getStability(stack);
        if (purity <= 0 && stability <= 0) {
            lines.add(TextFormatting.DARK_GRAY + I18n.format("pollution.crystal_quality.unselected"));
            return;
        }
        lines.add(TextFormatting.AQUA + I18n.format("pollution.crystal_quality.purity", purity));
        lines.add(TextFormatting.LIGHT_PURPLE + I18n.format("pollution.crystal_quality.stability", stability));
        if (AstralCrystalNbtHelper.isCrystalEmbryo(stack)) {
            lines.add(TextFormatting.BLUE + I18n.format("pollution.crystal_quality.embryo"));
        } else if (AstralCrystalNbtHelper.isCultivatedCrystal(stack)) {
            lines.add(TextFormatting.GOLD + I18n.format("pollution.crystal_quality.cultivated"));
            lines.add(TextFormatting.LIGHT_PURPLE + I18n.format("pollution.crystal_quality.grade",
                    AstralCrystalNbtHelper.getCultivationGrade(stack),
                    AstralCrystalNbtHelper.getOpticalQuality(stack)));
        }
    }
}
