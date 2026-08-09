package meowmel.pollution.api.astral;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Locale;

/** Canonical NBT helpers shared by celestial items, recipes and hatches. */
public final class AstralNbtHelper {

    public static final String POLLUTION_CONSTELLATION = "pollutionConstellation";
    public static final String CELESTIAL_FUNCTION = "pollutionCelestialFunction";

    private AstralNbtHelper() {}

    public static ItemStack createDataWafer(IConstellation constellation) {
        return writeConstellation(PollutionMetaItems.CONSTELLATION_DATA_WAFER.getStackForm(), constellation);
    }

    public static ItemStack createCalibratedCore(IConstellation constellation) {
        return writeConstellation(PollutionMetaItems.CELESTIAL_CALIBRATION_CORE.getStackForm(), constellation);
    }

    public static ItemStack writeConstellation(ItemStack stack, IConstellation constellation) {
        ItemStack result = stack.copy();
        NBTTagCompound tag = result.hasTagCompound() ? result.getTagCompound().copy() : new NBTTagCompound();
        constellation.writeToNBT(tag);
        tag.setString(POLLUTION_CONSTELLATION,
                constellation.getSimpleName().toLowerCase(Locale.ROOT));
        tag.setString(CELESTIAL_FUNCTION, getFunctionKey(constellation));
        result.setTagCompound(tag);
        return result;
    }

    public static String getFunctionKey(IConstellation constellation) {
        String name = constellation.getSimpleName().toLowerCase(Locale.ROOT);
        if ("aevitas".equals(name)) return "life";
        if ("evorsio".equals(name)) return "processing";
        if ("armara".equals(name)) return "stability";
        if ("discidia".equals(name)) return "energy";
        if ("horologium".equals(name)) return "time";
        return "resonance";
    }

    @Nullable
    public static IConstellation readConstellation(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) return null;
        NBTTagCompound tag = stack.getTagCompound();
        IConstellation constellation = IConstellation.readFromNBT(tag);
        if (constellation != null) return constellation;

        // Early data wafers and copied recipe outputs may only retain the
        // Pollution identifier.  It is an authoritative fallback, not a
        // client-side guess, and keeps those existing wafers usable.
        String id = tag.getString(POLLUTION_CONSTELLATION);
        return findConstellation(id);
    }

    /** Resolves both Astral Sorcery's unlocalized ID and this mod's simple ID. */
    @Nullable
    public static IConstellation findConstellation(String id) {
        if (id == null || id.isEmpty()) return null;
        IConstellation constellation = ConstellationRegistry.getConstellationByName(id);
        if (constellation != null) return constellation;
        for (IConstellation candidate : ConstellationRegistry.getAllConstellations()) {
            if (candidate != null && candidate.getSimpleName().equalsIgnoreCase(id)) {
                return candidate;
            }
        }
        return null;
    }
}
