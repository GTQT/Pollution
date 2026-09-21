package meowmel.pollution.common.items.bauble;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.items.metaitem.stats.IItemContainerItemProvider;
import gregtech.api.unification.material.Material;
import gregtech.integration.baubles.BaubleBehavior;
import meowmel.pollution.api.SourceMaterialItem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.api.item.IPhantomInkable;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.PlayerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "pollution")
public class ItemBaubleBehavior extends BaubleBehavior
        implements IItemContainerItemProvider, IItemBehaviour, SourceMaterialItem, ICosmeticAttachable, IPhantomInkable {

    /** 饰品内存储源质的 NBT 键 */
    private static final String SOURCE_KEY = "source";

    private final int maxSource;
    private final Material material;

    public ItemBaubleBehavior(int maxSource, Material material, BaubleType type) {
        super(type);
        this.maxSource = maxSource;
        this.material = material;
    }

    @Nullable
    public static ItemBaubleBehavior getInstanceFor(@Nonnull ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof MetaItem)) return null;

        MetaItem<?>.MetaValueItem valueItem = ((MetaItem<?>) itemStack.getItem()).getItem(itemStack);
        if (valueItem == null) return null;

        IItemContainerItemProvider provider = valueItem.getContainerItemProvider();
        if (!(provider instanceof ItemBaubleBehavior behavior)) return null;

        return behavior;
    }

    /**
     * 死亡时清理饰品带来的效果（例如水之戒给予的夜视）
     */
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent evt) {
        EntityLivingBase entity = evt.getEntityLiving();
        if (entity.world.isRemote || !(entity instanceof EntityPlayer player)) return;
        if (entity.world.getGameRules().getBoolean("keepInventory") || player.isSpectator()) return;

        IItemHandler inv = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < inv.getSlots(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            ItemBaubleBehavior behavior = getInstanceFor(stack);
            if (behavior != null) {
                behavior.onUnequipped(stack, player);
            }
        }
    }

    public static UUID getBaubleUUID(ItemStack stack) {
        long most = ItemNBTHelper.getLong(stack, "baubleUUIDMost", 0L);
        if (most == 0L) {
            UUID uuid = UUID.randomUUID();
            ItemNBTHelper.setLong(stack, "baubleUUIDMost", uuid.getMostSignificantBits());
            ItemNBTHelper.setLong(stack, "baubleUUIDLeast", uuid.getLeastSignificantBits());
            return getBaubleUUID(stack);
        } else {
            long least = ItemNBTHelper.getLong(stack, "baubleUUIDLeast", 0L);
            return new UUID(most, least);
        }
    }

    public static int getLastPlayerHashcode(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "playerHashcode", 0);
    }

    public static void setLastPlayerHashcode(ItemStack stack, int hash) {
        ItemNBTHelper.setInt(stack, "playerHashcode", hash);
    }

    @Override
    public int getMaxSourceStore() {
        return maxSource;
    }

    @Override
    public int getSourceStore(ItemStack item) {
        return ItemNBTHelper.getInt(item, SOURCE_KEY, 0);
    }

    @Override
    public boolean addSource(int amount, boolean simulate, ItemStack item) {
        int currentSource = getSourceStore(item);
        if (amount <= 0 || currentSource >= maxSource) return false;
        if (simulate) return currentSource + amount <= maxSource;

        setSourceStore(currentSource + amount, item);
        return true;
    }

    @Override
    public boolean consumeSource(int amount, boolean simulate, ItemStack item) {
        int currentSource = getSourceStore(item);
        if (amount < 0 || currentSource < amount) return false;

        if (!simulate) {
            setSourceStore(currentSource - amount, item);
        }
        return true;
    }

    @Override
    public void setSourceStore(int source, ItemStack item) {
        ItemNBTHelper.setInt(item, SOURCE_KEY, Math.max(0, Math.min(source, maxSource)));
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public void addInformation(ItemStack stack, List<String> lines) {
        lines.add(I18n.format("储量") + ": " + getSourceStore(stack) + "/" + getMaxSourceStore());
        lines.add(I18n.format("源质") + ": " + getMaterial().getLocalizedName());
        if (GuiScreen.isShiftKeyDown()) {
            addHiddenTooltip(stack, lines);
        } else {
            addStringToTooltip(I18n.format("botaniamisc.shiftinfo"), lines);
        }
    }

    @SideOnly(Side.CLIENT)
    public void addHiddenTooltip(ItemStack par1ItemStack, List<String> stacks) {
        String key = RenderHelper.getKeyDisplayString("Baubles Inventory");
        if (key != null) {
            addStringToTooltip(I18n.format("botania.baubletooltip", key), stacks);
        }

        ItemStack cosmetic = getCosmeticItem(par1ItemStack);
        if (!cosmetic.isEmpty()) {
            addStringToTooltip(I18n.format("botaniamisc.hasCosmetic", cosmetic.getDisplayName()), stacks);
        }

        if (hasPhantomInk(par1ItemStack)) {
            addStringToTooltip(I18n.format("botaniamisc.hasPhantomInk"), stacks);
        }
    }

    private void addStringToTooltip(String s, List<String> tooltip) {
        tooltip.add(s.replaceAll("&", "§"));
    }

    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (getLastPlayerHashcode(stack) != player.hashCode()) {
            onEquippedOrLoadedIntoWorld(stack, player);
            setLastPlayerHashcode(stack, player.hashCode());
        }
    }

    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player == null) return;

        if (!player.world.isRemote) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.equipBauble,
                    SoundCategory.PLAYERS, 0.1F, 1.3F);
            if (player instanceof EntityPlayerMP serverPlayer) {
                PlayerHelper.grantCriterion(serverPlayer,
                        new ResourceLocation("pollution", "main/bauble_wear"), "code_triggered");
            }
        }

        onEquippedOrLoadedIntoWorld(stack, player);
        setLastPlayerHashcode(stack, player.hashCode());
    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
    }

    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
    }

    @Override
    public ItemStack getCosmeticItem(ItemStack stack) {
        NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "cosmeticItem", true);
        return cmp == null ? ItemStack.EMPTY : new ItemStack(cmp);
    }

    @Override
    public void setCosmeticItem(ItemStack stack, ItemStack cosmetic) {
        NBTTagCompound cmp = new NBTTagCompound();
        if (!cosmetic.isEmpty()) {
            cmp = cosmetic.writeToNBT(cmp);
        }

        ItemNBTHelper.setCompound(stack, "cosmeticItem", cmp);
    }

    public boolean hasContainerItem(ItemStack stack) {
        return !getContainerItem(stack).isEmpty();
    }

    @Override
    public @Nonnull ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
        return getCosmeticItem(itemStack);
    }

    @Override
    public boolean hasPhantomInk(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "phantomInk", false);
    }

    @Override
    public void setPhantomInk(ItemStack stack, boolean ink) {
        ItemNBTHelper.setBoolean(stack, "phantomInk", ink);
    }
}
