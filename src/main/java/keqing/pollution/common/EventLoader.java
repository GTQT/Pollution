package keqing.pollution.common;

import gregtech.api.unification.material.event.MaterialEvent;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aura.AuraHelper;

import java.util.Random;

@Mod.EventBusSubscriber(
		modid = "pollution"
)
public class EventLoader {

	@SubscribeEvent(
			priority = EventPriority.HIGH
	)
	public static void registerMaterials(MaterialEvent event) {
		PollutionMaterials.register();

		//在此处注册材料
	}

	@SubscribeEvent
	static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			Random rand = new Random();
			int randomNum = rand.nextInt(50000);


			if (AuraHelper.getFlux(player.world, player.getPosition()) > 20 && randomNum >= 100 && randomNum <= 110) {
				player.sendMessage(new TextComponentTranslation("pollution.command.slowness"));
				player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 1));
			}
			if (AuraHelper.getFlux(player.world, player.getPosition()) > 40 && randomNum >= 140 && randomNum <= 150) {
				player.sendMessage(new TextComponentTranslation("pollution.command.weakness"));
				player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 1));
			}
			if (AuraHelper.getFlux(player.world, player.getPosition()) > 60 && randomNum >= 80 && randomNum <= 90) {
				player.sendMessage(new TextComponentTranslation("pollution.command.nausea"));
				player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 1));
			}
			if (AuraHelper.getFlux(player.world, player.getPosition()) > 80 && randomNum >= 60 && randomNum <= 70) {
				player.sendMessage(new TextComponentTranslation("pollution.command.mining"));
				player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 1));
			}
			if (AuraHelper.getFlux(player.world, player.getPosition()) > 100 && randomNum >= 10 && randomNum <= 15) {
				player.sendMessage(new TextComponentTranslation("pollution.command.blindness"));
				player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200, 1));
			}

			if (AuraHelper.getFlux(player.world, player.getPosition()) > 100) {
				for (int h = -3; h < 3; h++)
					for (int i = -16; i <= 16; i++)
						for (int j = -16; j <= 16; j++) {
							BlockPos sand = player.getPosition().add(i, h, j);
							if (player.world.getBlockState(sand) == Blocks.GRASS.getDefaultState())
								player.world.setBlockState(sand, Blocks.SAND.getDefaultState());

							if (player.world.getBlockState(sand) == Blocks.DIRT.getDefaultState())
								player.world.setBlockState(sand, Blocks.SAND.getDefaultState());

							if (player.world.getBlockState(sand) == Blocks.WATER.getDefaultState())
								player.world.setBlockState(sand, Blocks.LAVA.getDefaultState());
						}

			}
		}
	}
}