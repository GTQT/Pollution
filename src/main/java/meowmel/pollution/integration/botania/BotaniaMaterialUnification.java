package meowmel.pollution.integration.botania;

import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.ore.OrePrefix;
import meowmel.pollution.api.unification.PollutionMaterials;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.ModItems;

/**
 * Makes Pollution's GTCEu material forms interchangeable with Botania's elven materials.
 */
public final class BotaniaMaterialUnification {

    private static final int PIXIE_DUST_META = 8;
    private static final int DRAGONSTONE_META = 9;
    private static final int ELVEN_QUARTZ_META = 5;

    private BotaniaMaterialUnification() {}

    public static void init() {
        register(new ItemStack(ModItems.manaResource, 1, DRAGONSTONE_META),
                OrePrefix.gem, PollutionMaterials.Dragonstone, "elvenDragonstone");
        register(new ItemStack(ModItems.quartz, 1, ELVEN_QUARTZ_META),
                OrePrefix.gem, PollutionMaterials.ElvenQuartz, "quartzElven");
        register(new ItemStack(ModItems.manaResource, 1, PIXIE_DUST_META),
                OrePrefix.dust, PollutionMaterials.PixieDust, "elvenPixieDust");
    }

    private static void register(ItemStack botaniaStack, OrePrefix prefix, Material material,
                                 String botaniaOreDictionaryName) {
        // Capture Pollution's generated GTCEu form before Botania's item joins the same unification entry.
        ItemStack pollutionStack = OreDictUnifier.get(prefix, material);

        // GTCEu recipes and machines can consume the original Botania item.
        OreDictUnifier.registerOre(botaniaStack, prefix, material);

        // Botania recipes using its legacy ore-dictionary names can consume Pollution's generated form.
        OreDictUnifier.registerOre(pollutionStack, botaniaOreDictionaryName);
    }
}
