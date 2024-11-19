package keqing.pollution.common;

import gregtech.api.unification.material.event.MaterialEvent;
import keqing.pollution.Pollution;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aura.AuraHelper;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static keqing.pollution.POConfig.EntityPollutionEvent;

@Mod.EventBusSubscriber(
        modid = "pollution"
)
public class EventLoader {

    private static final long START_TIME = System.currentTimeMillis();
    private static final Random RANDOM = ThreadLocalRandom.current();

    @SubscribeEvent(
            priority = EventPriority.HIGH
    )
    public static void registerMaterials(MaterialEvent event) {
        PollutionMaterials.register();

        //在此处注册材料
    }

    @SubscribeEvent
    static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (EntityPollutionEvent && event.getEntity() instanceof EntityPlayer player) {
            int flux = (int) AuraHelper.getFlux(player.world, player.getPosition());
            if (flux < 20) return;

            int randomNum = RANDOM.nextInt(100000);
            applyEffectIfConditionMet(player, flux, randomNum, 20, 150, 250, MobEffects.SLOWNESS, "pollution.command.slowness");
            applyEffectIfConditionMet(player, flux, randomNum, 40, 70, 150, MobEffects.WEAKNESS, "pollution.command.weakness");
            applyEffectIfConditionMet(player, flux, randomNum, 60, 30, 70, MobEffects.NAUSEA, "pollution.command.nausea");
            applyEffectIfConditionMet(player, flux, randomNum, 80, 10, 30, MobEffects.MINING_FATIGUE, "pollution.command.mining");
            applyEffectIfConditionMet(player, flux, randomNum, 100, 0, 10, MobEffects.BLINDNESS, "pollution.command.blindness");

            if (flux > 75) {
                transformBlocks(player);
            }
            if (flux > 125) {
                spawnMushrooms(player);
            }
            if (flux > 200) {
                summonWitherIfConditionMet(player);
            }
        }
    }
    private static void applyEffectIfConditionMet(EntityPlayer player, int flux, int randomNum, int fluxThreshold, int minRandom, int maxRandom, Potion effect, String messageKey) {
        if (flux >= fluxThreshold && randomNum >= minRandom && randomNum <= maxRandom) {
            // 检查玩家是否已经具有该药水效果
            if (!player.isPotionActive(effect)) {
                player.sendMessage(new TextComponentTranslation(messageKey));
                player.addPotionEffect(new PotionEffect(effect, 200, 1));
            }
        }
    }
    private static void summonWitherIfConditionMet(EntityPlayer player) {
        if (RANDOM.nextInt(5000) < 2) {
            EntityWither wither = new EntityWither(player.world);

            // 生成 X 轴的随机偏移量
            int offsetX = RANDOM.nextInt(26) + 25; // 50 到 100
            if (RANDOM.nextBoolean()) {
                offsetX = -offsetX; // -100 到 -50
            }

            // 生成 Z 轴的随机偏移量
            int offsetZ = RANDOM.nextInt(26) + 25; // 50 到 100
            if (RANDOM.nextBoolean()) {
                offsetZ = -offsetZ; // -100 到 -50
            }

            // 设置 wither 的位置
            wither.setLocationAndAngles(
                    player.posX + offsetX,
                    player.posY + 10,
                    player.posZ + offsetZ,
                    0, 0
            );

            // 召唤 wither
            player.world.spawnEntity(wither);

            player.sendMessage(new TextComponentTranslation("pollution.command.summon_wither"));
            AuraHelper.drainFlux(player.world, player.getPosition(), 10, false);

        }
    }

    private static void transformBlocks(EntityPlayer player) {
        if (RANDOM.nextInt(20) < 2) {
            // 随机选择一个方块
            int offsetX = RANDOM.nextInt(17) - 8; // -16 到 16
            int offsetY = RANDOM.nextInt(7) - 3; // -3 到 3
            int offsetZ = RANDOM.nextInt(17) - 8; // -16 到 16

            BlockPos pos = player.getPosition().add(offsetX, offsetY, offsetZ);
            IBlockState state = player.world.getBlockState(pos);

            if (state == Blocks.WATER.getDefaultState()) {

                if (RANDOM.nextBoolean()) player.world.setBlockState(pos, Blocks.LAVA.getDefaultState());
                else player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                AuraHelper.drainFlux(player.world, player.getPosition(), 1, false);

            } else if (state != Blocks.AIR.getDefaultState()) {

                if (RANDOM.nextBoolean()) player.world.setBlockState(pos, Blocks.SAND.getDefaultState());
                else player.world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
                AuraHelper.drainFlux(player.world, player.getPosition(), 1, false);

            }
        }
    }


    private static void spawnMushrooms(EntityPlayer player) {
        if (RANDOM.nextInt(20) < 2) { // 5% 的概率生成蘑菇
            int offsetX = RANDOM.nextInt(33) - 16; // -16 到 16
            int offsetY = RANDOM.nextInt(11) - 5;
            int offsetZ = RANDOM.nextInt(33) - 16; // -16 到 16

            BlockPos pos = player.getPosition().add(offsetX, offsetY, offsetZ);
            IBlockState statePos = player.world.getBlockState(pos);
            IBlockState stateAir = player.world.getBlockState(pos.add(0, 1, 0));

            if (stateAir == Blocks.AIR.getDefaultState() && statePos != Blocks.AIR.getDefaultState()) {
                if (RANDOM.nextBoolean()) {
                    player.world.setBlockState(pos.add(0, 1, 0), Blocks.RED_MUSHROOM.getDefaultState());
                    AuraHelper.drainFlux(player.world, player.getPosition(), 1, false);
                } else {
                    player.world.setBlockState(pos.add(0, 1, 0), Blocks.BROWN_MUSHROOM.getDefaultState());
                    AuraHelper.drainFlux(player.world, player.getPosition(), 1, false);
                }
            }

        }
    }
}