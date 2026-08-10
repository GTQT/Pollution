package meowmel.pollution.api.astral;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Locale;

/**
 * Preserves Astral Sorcery's native rock-crystal properties through Pollution's
 * seed and embryo processing chain. The chain is deliberately one-way: a
 * cultivated crystal cannot be selected as a new industrial seed.
 */
public final class AstralCrystalNbtHelper {

    private static final String VERSION = "poCrystalVersion";
    private static final String SOURCE = "poSourceCrystal";
    private static final String PURITY = "poCrystalPurity";
    private static final String STABILITY = "poCrystalStability";
    private static final String GENERATION = "poCrystalGeneration";
    private static final String EMBRYO = "poCrystalEmbryo";
    private static final String CONSTELLATION = "poCultivatedConstellation";
    private static final String GRADE = "poCrystalGrade";
    private static final String NATIVE_CRYSTAL = "poCultivatedNativeCrystal";

    private AstralCrystalNbtHelper() {
    }

    public static boolean isEligibleRockCrystal(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() != ItemsAS.rockCrystal) return false;
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null || tag.getInteger(GENERATION) == 0;
    }

    public static boolean isCrystalSeed(ItemStack stack) {
        return sameItem(stack, PollutionMetaItems.ROCK_CRYSTAL_SEED)
                && hasCrystalData(stack) && !getTag(stack).getBoolean(EMBRYO);
    }

    public static boolean isCrystalEmbryo(ItemStack stack) {
        return sameItem(stack, PollutionMetaItems.CELESTIAL_CRYSTAL_EMBRYO)
                && hasCrystalData(stack) && getTag(stack).getBoolean(EMBRYO);
    }

    /** The dedicated Pollution output of the growth array and the only valid optical insert. */
    public static boolean isCultivatedCrystal(ItemStack stack) {
        // Slot admission must be based on the independent item itself, not on
        // its dynamic NBT. ModularUI's quick-move asks the server before it
        // transfers NBT in some paths; requiring the full tag here makes a
        // valid cultivated crystal appear client-side but be rejected server-side.
        return sameItem(stack, PollutionMetaItems.CULTIVATED_CRYSTAL);
    }

    public static ItemStack createSeed(ItemStack source) {
        if (!isEligibleRockCrystal(source)) return ItemStack.EMPTY;

        CrystalProperties properties = CrystalProperties.getCrystalProperties(source);
        if (properties == null) return ItemStack.EMPTY;

        ItemStack seed = PollutionMetaItems.ROCK_CRYSTAL_SEED.getStackForm();
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger(VERSION, 1);
        data.setTag(SOURCE, source.serializeNBT());
        data.setInteger(PURITY, clamp(properties.getPurity(), 0, 100));
        data.setInteger(STABILITY, calculateStability(properties));
        data.setInteger(GENERATION, 0);
        data.setBoolean(EMBRYO, false);
        seed.setTagCompound(data);
        return seed;
    }

    public static ItemStack createEmbryo(ItemStack seed) {
        if (!isCrystalSeed(seed)) return ItemStack.EMPTY;

        ItemStack embryo = PollutionMetaItems.CELESTIAL_CRYSTAL_EMBRYO.getStackForm();
        NBTTagCompound data = getTag(seed).copy();
        data.setBoolean(EMBRYO, true);
        embryo.setTagCompound(data);
        return embryo;
    }

    /**
     * Produces the sole cultivated generation of a rock crystal. The result
     * remains Astral Sorcery's native item, but its native CrystalProperties
     * are improved once and then locked out of seed selection forever.
     */
    public static ItemStack createCultivatedCrystal(ItemStack embryo) {
        return createCultivatedCrystal(embryo, "");
    }

    public static ItemStack createCultivatedCrystal(ItemStack embryo, String constellationId) {
        if (!isCrystalEmbryo(embryo)) return ItemStack.EMPTY;

        NBTTagCompound stored = getTag(embryo).getCompoundTag(SOURCE);
        ItemStack crystal = new ItemStack(stored);
        if (crystal.isEmpty() || crystal.getItem() != ItemsAS.rockCrystal) return ItemStack.EMPTY;

        crystal.setCount(1);
        CrystalProperties original = CrystalProperties.getCrystalProperties(crystal);
        if (original == null) return ItemStack.EMPTY;

        CultivationBonus bonus = CultivationBonus.forConstellation(constellationId);
        CrystalProperties cultivated = new CrystalProperties(
                clamp(original.getSize() + 3 + bonus.size, 0, CrystalProperties.MAX_SIZE_ROCK),
                clamp(original.getPurity() + 12 + bonus.purity, 0, 100),
                clamp(original.getCollectiveCapability() + 8 + bonus.collective, 0, 100),
                Math.max(0, original.getFracturation() - 1 - bonus.fractureRepair),
                original.getSizeOverride());
        CrystalProperties.applyCrystalProperties(crystal, cultivated);

        ItemStack cultivatedCrystal = PollutionMetaItems.CULTIVATED_CRYSTAL.getStackForm();
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger(VERSION, 1);
        data.setTag(SOURCE, stored.copy());
        data.setTag(NATIVE_CRYSTAL, crystal.serializeNBT());
        data.setInteger(PURITY, clamp(cultivated.getPurity(), 0, 100));
        data.setInteger(STABILITY, calculateStability(cultivated));
        data.setInteger(GENERATION, 1);
        data.setBoolean(EMBRYO, false);
        data.setString(CONSTELLATION, bonus.id);
        data.setString(GRADE, gradeFor(getOpticalQuality(data)));
        cultivatedCrystal.setTagCompound(data);
        return cultivatedCrystal;
    }

    public static int getPurity(ItemStack stack) {
        return hasCrystalData(stack) ? clamp(getTag(stack).getInteger(PURITY), 0, 100) : 0;
    }

    public static int getStability(ItemStack stack) {
        return hasCrystalData(stack) ? clamp(getTag(stack).getInteger(STABILITY), 0, 100) : 0;
    }

    /**
     * A quality score used only by the advanced lens optical insert. Purity is
     * weighted slightly higher than stability because fractured crystals are
     * already penalized during seed selection.
     */
    public static int getOpticalQuality(ItemStack stack) {
        if (!isCultivatedCrystal(stack)) return 0;
        return clamp((getPurity(stack) * 7 + getStability(stack) * 3) / 10, 0, 100);
    }

    /** Native Astral properties preserved inside the independent cultivated-crystal item. */
    public static CrystalProperties getCultivatedProperties(ItemStack stack) {
        if (!isCultivatedCrystal(stack)) return null;
        ItemStack nativeCrystal = new ItemStack(getTag(stack).getCompoundTag(NATIVE_CRYSTAL));
        return nativeCrystal.isEmpty() ? null : CrystalProperties.getCrystalProperties(nativeCrystal);
    }

    public static String getCultivationConstellation(ItemStack stack) {
        return isCultivatedCrystal(stack) ? getTag(stack).getString(CONSTELLATION) : "";
    }

    public static String getCultivationGrade(ItemStack stack) {
        return isCultivatedCrystal(stack) ? getTag(stack).getString(GRADE) : "";
    }

    private static boolean hasCrystalData(ItemStack stack) {
        NBTTagCompound tag = getTag(stack);
        return tag.hasKey(VERSION) && tag.hasKey(SOURCE) && tag.hasKey(PURITY) && tag.hasKey(STABILITY);
    }

    private static NBTTagCompound getTag(ItemStack stack) {
        return stack != null && stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
    }

    private static boolean sameItem(ItemStack stack, gregtech.api.items.metaitem.MetaItem<?>.MetaValueItem item) {
        return stack != null && !stack.isEmpty() && ItemStack.areItemsEqual(stack, item.getStackForm());
    }

    private static int calculateStability(CrystalProperties properties) {
        int size = clamp(properties.getSize() * 100 / CrystalProperties.MAX_SIZE_ROCK, 0, 100);
        int collective = clamp(properties.getCollectiveCapability(), 0, 100);
        int fracturePenalty = Math.min(50, Math.max(0, properties.getFracturation()) * 10);
        return clamp(25 + size / 2 + collective / 2 - fracturePenalty, 0, 100);
    }

    private static int getOpticalQuality(NBTTagCompound data) {
        int purity = clamp(data.getInteger(PURITY), 0, 100);
        int stability = clamp(data.getInteger(STABILITY), 0, 100);
        return clamp((purity * 7 + stability * 3) / 10, 0, 100);
    }

    private static String gradeFor(int quality) {
        if (quality >= 85) return "S";
        if (quality >= 70) return "A";
        if (quality >= 50) return "B";
        return "C";
    }

    private static final class CultivationBonus {
        private final String id;
        private final int size;
        private final int purity;
        private final int collective;
        private final int fractureRepair;

        private CultivationBonus(String id, int size, int purity, int collective, int fractureRepair) {
            this.id = id;
            this.size = size;
            this.purity = purity;
            this.collective = collective;
            this.fractureRepair = fractureRepair;
        }

        private static CultivationBonus forConstellation(String constellationId) {
            String id = constellationId == null ? "" : constellationId.toLowerCase(Locale.ROOT);
            switch (id) {
                case "aevitas": return new CultivationBonus(id, 6, 0, 4, 0);
                case "evorsio": return new CultivationBonus(id, 0, 6, 0, 1);
                case "armara": return new CultivationBonus(id, 0, 0, 4, 2);
                case "discidia": return new CultivationBonus(id, 8, 0, 0, 0);
                case "vicio": return new CultivationBonus(id, 0, 0, 10, 0);
                case "mineralis": return new CultivationBonus(id, 0, 10, 0, 0);
                case "fornax": return new CultivationBonus(id, 7, 3, 0, 0);
                case "horologium": return new CultivationBonus(id, 0, 4, 8, 0);
                case "lucerna": return new CultivationBonus(id, 0, 12, 0, 0);
                case "octans": return new CultivationBonus(id, 0, 0, 8, 1);
                case "bootes": return new CultivationBonus(id, 8, 0, 0, 0);
                case "pelotrio": return new CultivationBonus(id, 0, 8, 3, 0);
                case "gelu": return new CultivationBonus(id, 0, 0, 0, 2);
                case "ulteria": return new CultivationBonus(id, 0, 0, 12, 0);
                case "alcara": return new CultivationBonus(id, 4, 4, 4, 0);
                case "vorux": return new CultivationBonus(id, 10, 5, 0, 0);
                default: return new CultivationBonus(id, 0, 0, 0, 0);
            }
        }
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
