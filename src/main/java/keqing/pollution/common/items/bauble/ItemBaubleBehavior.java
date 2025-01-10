package keqing.pollution.common.items.bauble;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.items.metaitem.stats.IItemContainerItemProvider;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import gregtech.api.unification.material.Material;
import gregtech.integration.baubles.BaubleBehavior;
import keqing.pollution.api.SourceMaterialItem;
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

public class ItemBaubleBehavior extends BaubleBehavior implements IItemContainerItemProvider, IItemBehaviour, SourceMaterialItem, ICosmeticAttachable, IPhantomInkable {
    int MaxSource;
    Material material;

    @Nullable
    public static ItemBaubleBehavior getInstanceFor(@Nonnull ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof MetaItem)) return null;

        MetaItem<?>.MetaValueItem valueItem = ((MetaItem<?>) itemStack.getItem()).getItem(itemStack);
        if (valueItem == null) return null;

        IItemContainerItemProvider durabilityManager = valueItem.getContainerItemProvider();
        if (!(durabilityManager instanceof ItemBaubleBehavior)) return null;

        return (ItemBaubleBehavior) durabilityManager;
    }

    public ItemBaubleBehavior(int MaxSource, Material material, BaubleType type) {
        super(type);
        this.MaxSource = MaxSource;
        this.material = material;
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent evt) {
        if (!evt.getEntityLiving().world.isRemote && evt.getEntityLiving() instanceof EntityPlayer && !evt.getEntityLiving().world.getGameRules().getBoolean("keepInventory") && !((EntityPlayer) evt.getEntityLiving()).isSpectator()) {
            IItemHandler inv = BaublesApi.getBaublesHandler((EntityPlayer) evt.getEntityLiving());

            for (int i = 0; i < inv.getSlots(); ++i) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem().getRegistryName().getNamespace().equals("pollution")) {
                    ((vazkii.botania.common.item.equipment.bauble.ItemBauble) stack.getItem()).onUnequipped(stack, evt.getEntityLiving());
                }
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
        return MaxSource;
    }

    @Override
    public int getSourceStore(ItemStack item) {
        NBTTagCompound tag = item.getTagCompound();

        if (tag != null) return tag.getInteger("source");
        return 0;
    }

    @Override
    public boolean addSource(int n, boolean simulate, ItemStack item) {
        NBTTagCompound tag = item.getTagCompound();
        if (tag != null) {
            int amount = tag.getInteger("source");

            if(simulate)
            {
                 return (amount + n )< getMaxSourceStore();

            }
            else {
                if (amount + n < getMaxSourceStore()) {
                    tag.setInteger("source", Math.min(amount + n, getMaxSourceStore()));
                } else {
                    tag.setInteger("source", getMaxSourceStore());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean consumeSource(int amount, boolean simulate, ItemStack item) {
        int currentSource = getSourceStore(item);
        if (currentSource < 0 || amount < 0) return false;

        // 检查当前源存储量是否足够
        if (currentSource < amount) {
            return false; // 不够消耗
        }
        if (!simulate) {
            // 实际消耗资源
            setSourceStore(currentSource - amount, item);
        }
        return true; // 可以消耗
    }

    @Override
    public void setSourceStore(int source, ItemStack item) {
        NBTTagCompound tag = item.getTagCompound();
        if (tag != null) {
            tag.setInteger("source", source);
        }
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
            this.addHiddenTooltip(stack, lines);
        } else {
            this.addStringToTooltip(I18n.format("botaniamisc.shiftinfo"), lines);
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @SideOnly(Side.CLIENT)
    public void addHiddenTooltip(ItemStack par1ItemStack, List<String> stacks) {
        String key = RenderHelper.getKeyDisplayString("Baubles Inventory");
        if (key != null) {
            this.addStringToTooltip(I18n.format("botania.baubletooltip", key), stacks);
        }

        ItemStack cosmetic = this.getCosmeticItem(par1ItemStack);
        if (!cosmetic.isEmpty()) {
            this.addStringToTooltip(I18n.format("botaniamisc.hasCosmetic", cosmetic.getDisplayName()), stacks);
        }

        if (this.hasPhantomInk(par1ItemStack)) {
            this.addStringToTooltip(I18n.format("botaniamisc.hasPhantomInk"), stacks);
        }

    }

    void addStringToTooltip(String s, List<String> tooltip) {
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
            this.onEquippedOrLoadedIntoWorld(stack, player);
            setLastPlayerHashcode(stack, player.hashCode());
        }

    }

    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player != null) {
            if (!player.world.isRemote) {
                player.world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.equipBauble, SoundCategory.PLAYERS, 0.1F, 1.3F);
                PlayerHelper.grantCriterion((EntityPlayerMP) player, new ResourceLocation("pollution", "main/bauble_wear"), "code_triggered");
            }

            this.onEquippedOrLoadedIntoWorld(stack, player);
            setLastPlayerHashcode(stack, player.hashCode());
        }

    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
    }

    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
    }

    public ItemStack getCosmeticItem(ItemStack stack) {
        NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "cosmeticItem", true);
        return cmp == null ? ItemStack.EMPTY : new ItemStack(cmp);
    }

    public void setCosmeticItem(ItemStack stack, ItemStack cosmetic) {
        NBTTagCompound cmp = new NBTTagCompound();
        if (!cosmetic.isEmpty()) {
            cmp = cosmetic.writeToNBT(cmp);
        }

        ItemNBTHelper.setCompound(stack, "cosmeticItem", cmp);
    }

    public boolean hasContainerItem(ItemStack stack) {
        return !this.getContainerItem(stack).isEmpty();
    }

    @Nonnull
    public ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
        return this.getCosmeticItem(itemStack);
    }

    public boolean hasPhantomInk(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "phantomInk", false);
    }

    public void setPhantomInk(ItemStack stack, boolean ink) {
        ItemNBTHelper.setBoolean(stack, "phantomInk", ink);
    }
}
