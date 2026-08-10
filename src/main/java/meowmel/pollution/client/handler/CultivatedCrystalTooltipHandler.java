package meowmel.pollution.client.handler;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import meowmel.pollution.Pollution;
import meowmel.pollution.api.astral.AstralCrystalNbtHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Client-only identity card for native Astral Sorcery crystals cultivated by Pollution. */
@Mod.EventBusSubscriber(modid = Pollution.MODID, value = Side.CLIENT)
public final class CultivatedCrystalTooltipHandler {

    private CultivatedCrystalTooltipHandler() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!AstralCrystalNbtHelper.isCultivatedCrystal(stack)) return;

        CrystalProperties properties = AstralCrystalNbtHelper.getCultivatedProperties(stack);
        if (properties == null) return;

        event.getToolTip().add(TextFormatting.GOLD + I18n.format("pollution.crystal_quality.cultivated"));
        event.getToolTip().add(TextFormatting.GRAY + I18n.format("pollution.crystal_quality.generation", 1)
                + TextFormatting.DARK_GRAY + " " + I18n.format("pollution.crystal_quality.no_reseed"));
        event.getToolTip().add(TextFormatting.AQUA + I18n.format("pollution.crystal_quality.constellation",
                AstralCrystalNbtHelper.getCultivationConstellation(stack)));
        event.getToolTip().add(TextFormatting.WHITE + I18n.format("pollution.crystal_quality.native_stats",
                properties.getSize(), properties.getPurity(), properties.getCollectiveCapability(),
                properties.getFracturation()));
        event.getToolTip().add(TextFormatting.LIGHT_PURPLE + I18n.format("pollution.crystal_quality.grade",
                AstralCrystalNbtHelper.getCultivationGrade(stack),
                AstralCrystalNbtHelper.getOpticalQuality(stack)));
        event.getToolTip().add(TextFormatting.BLUE + I18n.format("pollution.crystal_quality.lens_slot"));
    }
}
