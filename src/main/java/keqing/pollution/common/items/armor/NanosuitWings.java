package keqing.pollution.common.items.armor;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.util.GTUtility;
import gregtech.api.util.input.KeyBind;
import gregtech.common.items.armor.Jetpack;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;


public class NanosuitWings extends Jetpack {

	public NanosuitWings(int energyPerUse, long capacity, int tier) {
		super(energyPerUse, capacity, tier);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack item) {
		IElectricItem cont = item.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		if (cont == null) {
			return;
		}
		NBTTagCompound data = GTUtility.getOrCreateNbtCompound(item);
		boolean hoverMode = data.hasKey("hover") && data.getBoolean("hover");
		byte toggleTimer = data.hasKey("toggleTimer") ? data.getByte("toggleTimer") : 0;

		if (toggleTimer == 0 && KeyBind.ARMOR_HOVER.isKeyDown(player)) {
			hoverMode = !hoverMode;
			toggleTimer = 5;
			data.setBoolean("hover", hoverMode);
			if (!world.isRemote) {
				if (hoverMode)
					player.sendStatusMessage(new TextComponentTranslation("metaarmor.jetpack.hover.enable"), true);
				else
					player.sendStatusMessage(new TextComponentTranslation("metaarmor.jetpack.hover.disable"), true);
			}
		}


		if (!world.isRemote && canUseEnergy(item, energyPerUse * 2)) {
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 10, 1, true, false));
			drainEnergy(item, energyPerUse * 2);
		}

		performFlying(player, hoverMode, item);

		if (toggleTimer > 0) toggleTimer--;

		data.setBoolean("hover", hoverMode);
		data.setByte("toggleTimer", toggleTimer);
		player.inventoryContainer.detectAndSendChanges();
	}


	@Override
	public void addInfo(ItemStack itemStack, List<String> lines) {
		super.addInfo(itemStack, lines);
		lines.add(I18n.format("§d急行侠I：激活后获得急速增幅§r"));
	}

	@Override
	public double getSprintEnergyModifier() {
		return 2.5D;
	}

	@Override
	public double getSprintSpeedModifier() {
		return 1.5D;
	}

	@Override
	public double getVerticalHoverSpeed() {
		return 0.5D;
	}

	@Override
	public double getVerticalHoverSlowSpeed() {
		return 0.05D;
	}

	@Override
	public double getVerticalAcceleration() {
		return 0.2D;
	}

	@Override
	public double getVerticalSpeed() {
		return 0.6D;
	}

	@Override
	public double getSidewaysSpeed() {
		return 0.2D;
	}

	@Override
	public EnumParticleTypes getParticle() {
		return EnumParticleTypes.CLOUD;
	}

	@Override
	public float getFallDamageReduction() {
		return 2.0f;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "gregtech:textures/armor/nanowing.png";
	}
}