package meowmel.pollution.common.entity.dragon;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/** The same payload travels between the item and tile; never retain a live dragon. */
public final class DragonContainmentData {
    public static final String KEY = "DragonContainment";
    public static final String TYPE = "pollution:ender_dragon";

    private DragonContainmentData() {}

    public static int progress(NBTTagCompound display) {
        if ("ADULT".equals(display.getString("Stage"))) return 100;
        return (int) Math.max(0, Math.min(100, 100.0D * display.getInteger("Ticks")
                / Math.max(1, display.getInteger("Duration"))));
    }

    public static boolean isOccupied(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(KEY);
    }

    public static NBTTagCompound get(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag(KEY) : new NBTTagCompound();
    }

    public static void set(ItemStack stack, NBTTagCompound payload) {
        NBTTagCompound root = stack.getTagCompound() != null ? stack.getTagCompound().copy() : new NBTTagCompound();
        if (payload.isEmpty()) root.removeTag(KEY);
        else root.setTag(KEY, payload.copy());
        stack.setTagCompound(root.isEmpty() ? null : root);
    }

    public static NBTTagCompound capture(EntityPollutionDragon dragon) {
        NBTTagCompound entity = dragon.writeToNBT(new NBTTagCompound());
        entity.setString("id", TYPE);
        NBTTagCompound payload = new NBTTagCompound();
        payload.setInteger("Version", 1);
        payload.setUniqueId("CaptureId", UUID.randomUUID());
        payload.setTag("Dragon", entity);
        NBTTagCompound display = new NBTTagCompound();
        display.setString("Stage", dragon.getLifeStage().name());
        display.setString("Name", dragon.hasCustomName() ? dragon.getCustomNameTag() : "");
        display.setInteger("Appearance", dragon.getAppearance());
        display.setInteger("Ticks", dragon.getTicksInStage());
        display.setInteger("Duration", dragon.getStageDuration());
        display.setFloat("Health", dragon.getHealth());
        display.setFloat("MaxHealth", dragon.getMaxHealth());
        display.setFloat("BaseScale", dragon.getBaseScale());
        payload.setTag("Display", display);
        return payload;
    }

    public static boolean isValid(NBTTagCompound payload) {
        if (payload.getInteger("Version") != 1 || !payload.hasUniqueId("CaptureId")
                || !payload.hasKey("Dragon", 10)) return false;
        NBTTagCompound tag = payload.getCompoundTag("Dragon");
        if (!TYPE.equals(tag.getString("id")) || !tag.hasUniqueId("UUID")) return false;
        try {
            DragonLifeStage stage = DragonLifeStage.valueOf(tag.getString("DragonStageName"));
            float health = tag.getFloat("Health");
            float scale = tag.getFloat("DragonBaseScale");
            return stage != DragonLifeStage.EGG && tag.getInteger("DragonDataVersion") == 1
                    && tag.getInteger("DragonStage") == stage.ordinal()
                    && tag.getInteger("DragonStageTicks") >= 0
                    && (stage == DragonLifeStage.ADULT || tag.getInteger("DragonStageDuration") > 0)
                    && tag.getInteger("DragonAppearance") >= 0 && tag.getInteger("DragonAppearance") <= 2
                    && Float.isFinite(health) && health > 0 && Float.isFinite(scale) && scale >= 0.1F && scale <= 2.0F
                    && !tag.hasKey("Passengers") && !tag.getBoolean("Leashed");
        } catch (IllegalArgumentException invalid) {
            return false;
        }
    }

    /** Derived from authoritative full server data, never from the client Display tag. */
    public static ExtractionSnapshot extractionSnapshot(NBTTagCompound payload) {
        if (!isValid(payload)) return null;
        NBTTagCompound dragon = payload.getCompoundTag("Dragon");
        return new ExtractionSnapshot(dragon.getUniqueId("UUID"), payload.getUniqueId("CaptureId"),
                DragonLifeStage.valueOf(dragon.getString("DragonStageName")));
    }

    public static final class ExtractionSnapshot {
        public final UUID dragonId, captureId;
        public final DragonLifeStage stage;
        private ExtractionSnapshot(UUID dragonId, UUID captureId, DragonLifeStage stage) {
            this.dragonId = dragonId;
            this.captureId = captureId;
            this.stage = stage;
        }
    }

    /** Only render metadata is sent in tile update packets, never executable entity NBT. */
    public static NBTTagCompound displayOnly(NBTTagCompound payload) {
        if (payload.isEmpty()) return new NBTTagCompound();
        NBTTagCompound display = new NBTTagCompound();
        display.setBoolean("Occupied", true);
        display.setTag("Display", payload.getCompoundTag("Display").copy());
        if (payload.hasUniqueId("CaptureId")) display.setUniqueId("CaptureId", payload.getUniqueId("CaptureId"));
        return display;
    }
}
