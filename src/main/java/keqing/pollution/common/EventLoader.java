package keqing.pollution.common;

import gregtech.api.unification.material.event.MaterialEvent;
import keqing.pollution.Advancement.AdvancementTriggers;
import keqing.pollution.POConfig;
import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static keqing.pollution.api.utils.POTeleporter.buildPortalIngredient;
import static keqing.pollution.api.utils.POTeleporter.portalIngredient;
import static keqing.pollution.common.block.blocks.PollutionBlocksInit.BLOCK_TF_PORTAL;

@Mod.EventBusSubscriber(modid = "pollution")
public class EventLoader {

    private static final Random RANDOM = ThreadLocalRandom.current();
    private static long clientTick = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterials(MaterialEvent event) {
        PollutionMaterials.register();
        buildPortalIngredient();
        //在此处注册材料
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = player.world;

        if(player.isCreative())return;
        // check for portal creation, at least if it's not disabled
        if (!world.isRemote && event.phase == TickEvent.Phase.END && player.ticksExisted % (POConfig.WorldSettingSwitch.checkPortalDestination ? 100 : 20) == 0) {
            checkForPortalCreation(player, world, 32.0F);
        }

        if (POConfig.PollutionSystemSwitch.EntityPollutionEvent) {
            clientTick++;
            if (clientTick < 200) return;
            clientTick = 0;

            int flux = (int) AuraHelper.getFlux(player.world, player.getPosition());
            if (flux < 40) return;

            int randomNum = RANDOM.nextInt(250);
            applyEffectIfConditionMet(player, flux, randomNum, 40, 150, 250, MobEffects.SLOWNESS, "pollution.command.slowness");
            applyEffectIfConditionMet(player, flux, randomNum, 60, 70, 150, MobEffects.WEAKNESS, "pollution.command.weakness");
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

    private static void checkForPortalCreation(EntityPlayer player, World world, float rangeToCheck) {
        if (world.provider.getDimension() == POConfig.WorldSettingSwitch.originDimension
                || world.provider.getDimension() == POConfig.WorldSettingSwitch.BTNetherDimensionID
                || POConfig.WorldSettingSwitch.allowPortalsInOtherDimensions) {

            List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, player.getEntityBoundingBox().grow(rangeToCheck));

            for (EntityItem entityItem : itemList) {
                if (portalIngredient.apply(entityItem.getItem())) {
                    BlockPos pos = entityItem.getPosition();
                    IBlockState state = world.getBlockState(pos);
                    if (BLOCK_TF_PORTAL.canFormPortal(state)) {
                        Random rand = new Random();
                        for (int i = 0; i < 2; i++) {
                            double vx = rand.nextGaussian() * 0.02D;
                            double vy = rand.nextGaussian() * 0.02D;
                            double vz = rand.nextGaussian() * 0.02D;

                            world.spawnParticle(EnumParticleTypes.SPELL, entityItem.posX, entityItem.posY + 0.2, entityItem.posZ, vx, vy, vz);
                        }


                        if (BLOCK_TF_PORTAL.tryToCreatePortal(world, pos, entityItem, player)) {
                            AdvancementTriggers.FIRST_TO_BTN.trigger((EntityPlayerMP) player);
                            return;
                        }
                    }
                }
            }
        }
    }


    private static void applyEffectIfConditionMet(EntityPlayer player, int flux, int randomNum, int fluxThreshold, int minRandom, int maxRandom, Potion effect, String messageKey) {
        if (flux >= fluxThreshold && randomNum >= minRandom && randomNum <= maxRandom) {
            // 检查玩家是否已经具有该药水效果
            if (!player.isPotionActive(effect)) {
                player.sendMessage(new TextComponentTranslation(messageKey));
                player.addPotionEffect(new PotionEffect(effect, 200, 1));

                AuraHelper.drainFlux(player.world, player.getPosition(), 10, false);
            }
        }
    }

    private static void summonWitherIfConditionMet(EntityPlayer player) {
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
        wither.setLocationAndAngles(player.posX + offsetX, player.posY + 10, player.posZ + offsetZ, 0, 0);

        // 召唤 wither
        player.world.spawnEntity(wither);
        player.sendMessage(new TextComponentTranslation("pollution.command.summon_wither"));
        AuraHelper.drainFlux(player.world, player.getPosition(), 100, false);

    }

    private static void transformBlocks(EntityPlayer player) {
        // 随机选择一个方块
        int offsetX = RANDOM.nextInt(17) - 8; // -16 到 16
        int offsetY = RANDOM.nextInt(7) - 3; // -3 到 3
        int offsetZ = RANDOM.nextInt(17) - 8; // -16 到 16

        BlockPos pos = player.getPosition().add(offsetX, offsetY, offsetZ);
        IBlockState state = player.world.getBlockState(pos);

        if (state == Blocks.WATER.getDefaultState()) {

            if (RANDOM.nextBoolean()) player.world.setBlockState(pos, Blocks.LAVA.getDefaultState());
            else player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            AuraHelper.drainFlux(player.world, player.getPosition(), 25, false);

        } else if (state != Blocks.AIR.getDefaultState()) {

            if (RANDOM.nextBoolean()) player.world.setBlockState(pos, Blocks.SAND.getDefaultState());
            else player.world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
            AuraHelper.drainFlux(player.world, player.getPosition(), 25, false);
        }
    }


    private static void spawnMushrooms(EntityPlayer player) {
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