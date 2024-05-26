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

public class QuantumWings extends Jetpack {

	public QuantumWings(int energyPerUse, long capacity, int tier) {
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

		if (!world.isRemote && canUseEnergy(item, energyPerUse)) {
			player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 10, 1, true, false));
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 10, 1, true, false));
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 10, 1, true, false));
			drainEnergy(item, energyPerUse);
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
		lines.add(I18n.format("§d急行侠II：激活后获得跳跃提升 急速增幅§r"));
		lines.add(I18n.format("§a急死了：激活后获得急迫增幅§r"));
	}

	@Override
	public double getSprintEnergyModifier() {
		return 5D;
	}

	@Override
	public double getSprintSpeedModifier() {
		return 2.0D;
	}

	@Override
	public double getVerticalHoverSpeed() {
		return 0.8D;
	}

	@Override
	public double getVerticalHoverSlowSpeed() {
		return 0.1D;
	}

	@Override
	public double getVerticalAcceleration() {
		return 0.5D;
	}

	@Override
	public double getVerticalSpeed() {
		return 1.0D;
	}

	@Override
	public double getSidewaysSpeed() {
		return 0.4D;
	}

	@Override
	public EnumParticleTypes getParticle() {
		return EnumParticleTypes.CLOUD;
	}

	@Override
	public float getFallDamageReduction() {
		return 4.0f;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "gregtech:textures/armor/quantumwing.png";
	}
}