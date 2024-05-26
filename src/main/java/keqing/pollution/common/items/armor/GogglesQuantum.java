package keqing.pollution.common.items.armor;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.armor.ArmorLogicSuite;
import gregtech.api.util.GTUtility;
import gregtech.api.util.input.KeyBind;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;

import java.util.List;

public class GogglesQuantum extends ArmorLogicSuite implements IVisDiscountGear, IRevealer, IGoggles {

	public GogglesQuantum(int energyPerUse, long capacity, int voltageTier, EntityEquipmentSlot slot) {
		super(energyPerUse, capacity, voltageTier, slot);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		IElectricItem item = itemStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		if (item == null) {
			return;
		}


		FoodStats foodStats = player.getFoodStats();
		if (foodStats.needFood()) {
			if (item.getCharge() >= energyPerUse) {
				item.discharge((energyPerUse), this.tier, true, false, false);
				foodStats.addStats(1, 0.2f);
			}
		}

		NBTTagCompound nbtData = GTUtility.getOrCreateNbtCompound(itemStack);
		byte toggleTimer = nbtData.getByte("toggleTimer");
		if (!player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isItemEqual(itemStack)) {
			disableNightVision(world, player, false);
		}
		if (SLOT == EntityEquipmentSlot.HEAD) {
			boolean nightvision = nbtData.getBoolean("Nightvision");
			if (toggleTimer == 0 && KeyBind.ARMOR_MODE_SWITCH.isKeyDown(player)) {
				toggleTimer = 5;
				if (!nightvision && item.getCharge() >= energyPerUse) {
					nightvision = true;
					if (!world.isRemote)
						player.sendStatusMessage(new TextComponentTranslation("metaarmor.message.nightvision.enabled"),
								true);
				} else if (nightvision) {
					nightvision = false;
					disableNightVision(world, player, true);
				} else {
					if (!world.isRemote) {
						player.sendStatusMessage(new TextComponentTranslation("metaarmor.message.nightvision.error"),
								true);
					}
				}

				if (!world.isRemote) {
					nbtData.setBoolean("Nightvision", nightvision);
				}
			}

			if (nightvision && !world.isRemote && item.getCharge() >= energyPerUse) {
				player.removePotionEffect(MobEffects.BLINDNESS);
				player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 999999, 0, true, false));
				player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 999999, 0, true, false));
				item.discharge((energyPerUse), this.tier, true, false, false);
			}
			if (!nightvision && !world.isRemote && world.isDaytime()) {
				item.charge(energyPerUse * 2L, this.tier, true, false);
			}
			if (toggleTimer > 0) --toggleTimer;

			nbtData.setByte("toggleTimer", toggleTimer);
		}
		player.inventoryContainer.detectAndSendChanges();
	}

	public static void disableNightVision(World world, EntityPlayer player, boolean sendMsg) {
		if (!world.isRemote) {
			player.removePotionEffect(MobEffects.NIGHT_VISION);
			player.removePotionEffect(MobEffects.WATER_BREATHING);
			if (sendMsg)
				player.sendStatusMessage(new TextComponentTranslation("metaarmor.message.nightvision.disabled"), true);
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "gregtech:textures/armor/quantum_goggles.png";
	}

	@Override
	public void addInfo(ItemStack itemStack, List<String> lines) {
		super.addInfo(itemStack, lines);
		if (SLOT == EntityEquipmentSlot.HEAD) {
			NBTTagCompound nbtData = GTUtility.getOrCreateNbtCompound(itemStack);
			lines.add(I18n.format("§2饕餮：消耗电力自动恢复饱食度§r"));
			lines.add(I18n.format("§b水生：开启后获得水下速掘§r"));
			lines.add(I18n.format("§1太阳能：不工作时在阳光下自然恢复电力§r"));
			lines.add(I18n.format("§a魔力减免：5§r"));
			boolean nv = nbtData.getBoolean("Nightvision");
			if (nv) {
				lines.add(I18n.format("metaarmor.message.nightvision.enabled"));
			} else {
				lines.add(I18n.format("metaarmor.message.nightvision.disabled"));
			}
		}
	}

	public int getVisDiscount(ItemStack stack, EntityPlayer player) {
		return 5;
	}

	public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}
}